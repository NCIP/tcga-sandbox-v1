package org.broadinstitute.sting.gatk.refdata;

import org.broad.tribble.Feature;
import org.broad.tribble.dbsnp.DbSNPFeature;
import org.broad.tribble.gelitext.GeliTextFeature;
import org.broadinstitute.sting.utils.codecs.hapmap.HapMapFeature;
import org.broadinstitute.sting.utils.variantcontext.Allele;
import org.broadinstitute.sting.utils.variantcontext.Genotype;
import org.broadinstitute.sting.utils.variantcontext.MutableGenotype;
import org.broadinstitute.sting.utils.variantcontext.VariantContext;
import org.broadinstitute.sting.utils.codecs.vcf.*;
import org.broadinstitute.sting.gatk.contexts.ReferenceContext;
import org.broadinstitute.sting.utils.variantcontext.VariantContextUtils;
import org.broadinstitute.sting.gatk.refdata.utils.helpers.DbSNPHelper;
import org.broadinstitute.sting.utils.classloader.PluginManager;
import org.broadinstitute.sting.utils.exceptions.ReviewedStingException;

import java.util.*;

/**
 * A terrible but temporary approach to converting objects to VariantContexts.  If you want to add a converter,
 * you need to create a adaptor object here and register a converter from your class to this object.  When tribble arrives,
 * we'll use a better approach.
 *
 * To add a new converter:
 *
 *   create a subclass of VCAdaptor, overloading the convert operator
 *   add it to the static map from input type -> converter where the input type is the object.class you want to convert
 *
 * That's it 
 *
 * @author depristo@broadinstitute.org
 */
public class VariantContextAdaptors {
    // --------------------------------------------------------------------------------------------------------------
    //
    // Generic support routines.  Do not modify
    //
    // --------------------------------------------------------------------------------------------------------------

    private static Map<Class<? extends Feature>,VCAdaptor> adaptors = new HashMap<Class<? extends Feature>,VCAdaptor>();

    static {
        PluginManager<VCAdaptor> vcAdaptorManager = new PluginManager<VCAdaptor>(VCAdaptor.class);
        List<VCAdaptor> adaptorInstances = vcAdaptorManager.createAllTypes();
        for(VCAdaptor adaptor: adaptorInstances)
            adaptors.put(adaptor.getAdaptableFeatureType(),adaptor);
    }

    public static boolean canBeConvertedToVariantContext(Object variantContainingObject) {
        return adaptors.containsKey(variantContainingObject.getClass());
    }

    /** generic superclass */
    public interface VCAdaptor {
        /**
         * Gets the type of feature that this adaptor can 'adapt' into a VariantContext.
         * @return Type of adaptable feature.  Must be a Tribble feature class.
         */
        Class<? extends Feature> getAdaptableFeatureType();
        VariantContext convert(String name, Object input, ReferenceContext ref);
    }

    public static VariantContext toVariantContext(String name, Object variantContainingObject, ReferenceContext ref) {
        if ( ! adaptors.containsKey(variantContainingObject.getClass()) )
            return null;
        else {
            return adaptors.get(variantContainingObject.getClass()).convert(name, variantContainingObject, ref);
        }
    }

    // --------------------------------------------------------------------------------------------------------------
    //
    // From here below you can add adaptor classes for new rods (or other types) to convert to VC
    //
    // --------------------------------------------------------------------------------------------------------------
    private static class VariantContextAdaptor implements VCAdaptor {
        /**
         * 'Null' adaptor; adapts variant contexts to variant contexts.
         * @return VariantContext.
         */
        @Override
        public Class<? extends Feature> getAdaptableFeatureType() { return VariantContext.class; }

        // already a VC, just cast and return it
        @Override        
        public VariantContext convert(String name, Object input, ReferenceContext ref) {
            return (VariantContext)input;
        }
    }

    // --------------------------------------------------------------------------------------------------------------
    //
    // dbSNP to VariantContext
    //
    // --------------------------------------------------------------------------------------------------------------

    private static class DBSnpAdaptor implements VCAdaptor {
        /**
         * Converts non-VCF formatted dbSNP records to VariantContext. 
         * @return DbSNPFeature.
         */
        @Override
        public Class<? extends Feature> getAdaptableFeatureType() { return DbSNPFeature.class; }

        @Override        
        public VariantContext convert(String name, Object input, ReferenceContext ref) {
            DbSNPFeature dbsnp = (DbSNPFeature)input;
            if ( ! Allele.acceptableAlleleBases(DbSNPHelper.getReference(dbsnp)) )
                return null;
            Allele refAllele = Allele.create(DbSNPHelper.getReference(dbsnp), true);

            if ( DbSNPHelper.isSNP(dbsnp) || DbSNPHelper.isIndel(dbsnp) || DbSNPHelper.isMNP(dbsnp) || dbsnp.getVariantType().contains("mixed") ) {
                // add the reference allele
                List<Allele> alleles = new ArrayList<Allele>();
                alleles.add(refAllele);

                // add all of the alt alleles
                for ( String alt : DbSNPHelper.getAlternateAlleleList(dbsnp) ) {
                    if ( ! Allele.acceptableAlleleBases(alt) ) {
                        //System.out.printf("Excluding dbsnp record %s%n", dbsnp);
                        return null;
                    }
                    alleles.add(Allele.create(alt, false));
                }

                Map<String, Object> attributes = new HashMap<String, Object>();
                attributes.put(VariantContext.ID_KEY, dbsnp.getRsID());
                if ( DbSNPHelper.isDeletion(dbsnp) ) {
                    int index = ref.getLocus().getStart() - ref.getWindow().getStart() - 1;
                    if ( index < 0 )
                        throw new ReviewedStingException("DbSNP conversion requested using a reference context with no window; we will fail to convert deletions");
                    attributes.put(VariantContext.REFERENCE_BASE_FOR_INDEL_KEY, new Byte(ref.getBases()[index]));
                }
                Collection<Genotype> genotypes = null;
                VariantContext vc = new VariantContext(name, dbsnp.getChr(),dbsnp.getStart() - (DbSNPHelper.isDeletion(dbsnp) ? 1 : 0),dbsnp.getEnd(), alleles, genotypes, VariantContext.NO_NEG_LOG_10PERROR, null, attributes);
                return vc;
            } else
                return null; // can't handle anything else
        }
    }

    public static VCFHeader createVCFHeader(Set<VCFHeaderLine> hInfo, VariantContext vc) {
        HashSet<String> names = new LinkedHashSet<String>();
        for ( Genotype g : vc.getGenotypesSortedByName() ) {
            names.add(g.getSampleName());
        }

        return new VCFHeader(hInfo == null ? new HashSet<VCFHeaderLine>() : hInfo, names);
    }

    // --------------------------------------------------------------------------------------------------------------
    //
    // GELI to VariantContext
    //
    // --------------------------------------------------------------------------------------------------------------

    private static class GeliTextAdaptor implements VCAdaptor {
        /**
         * Converts Geli text records to VariantContext. 
         * @return GeliTextFeature.
         */
        @Override
        public Class<? extends Feature> getAdaptableFeatureType() { return GeliTextFeature.class; }

          /**
         * convert to a Variant Context, given:
         * @param name the name of the ROD
         * @param input the Rod object, in this case a RodGeliText
         * @return a VariantContext object
         */
//        VariantContext convert(String name, Object input) {
//            return convert(name, input, null);
//        }

        /**
         * convert to a Variant Context, given:
         * @param name  the name of the ROD
         * @param input the Rod object, in this case a RodGeliText
         * @param ref   the reference context
         * @return a VariantContext object
         */
        @Override
        public VariantContext convert(String name, Object input, ReferenceContext ref) {
            GeliTextFeature geli = (GeliTextFeature)input;
            if ( ! Allele.acceptableAlleleBases(String.valueOf(geli.getRefBase())) )
                return null;
            Allele refAllele = Allele.create(String.valueOf(geli.getRefBase()), true);

            // make sure we can convert it
            if ( geli.getGenotype().isHet() || !geli.getGenotype().containsBase(geli.getRefBase())) {
                // add the reference allele
                List<Allele> alleles = new ArrayList<Allele>();
                List<Allele> genotypeAlleles = new ArrayList<Allele>();
                // add all of the alt alleles
                for ( char alt : geli.getGenotype().toString().toCharArray() ) {
                    if ( ! Allele.acceptableAlleleBases(String.valueOf(alt)) ) {
                        return null;
                    }
                    Allele allele = Allele.create(String.valueOf(alt), false);
                    if (!alleles.contains(allele) && !refAllele.basesMatch(allele.getBases())) alleles.add(allele);

                    // add the allele, first checking if it's reference or not
                    if (!refAllele.basesMatch(allele.getBases())) genotypeAlleles.add(allele);
                    else genotypeAlleles.add(refAllele);
                }

                Map<String, String> attributes = new HashMap<String, String>();
                Collection<Genotype> genotypes = new ArrayList<Genotype>();
                MutableGenotype call = new MutableGenotype(name, genotypeAlleles);

                // set the likelihoods, depth, and RMS mapping quality values
                //call.putAttribute(CalledGenotype.POSTERIORS_ATTRIBUTE_KEY,geli.getLikelihoods());
                //call.putAttribute(GeliTextWriter.MAXIMUM_MAPPING_QUALITY_ATTRIBUTE_KEY,geli.getMaximumMappingQual());
                //call.putAttribute(GeliTextWriter.READ_COUNT_ATTRIBUTE_KEY,geli.getDepthOfCoverage());

                // add the call to the genotype list, and then use this list to create a VariantContext
                genotypes.add(call);
                alleles.add(refAllele);
                VariantContext vc = VariantContextUtils.toVC(name, ref.getGenomeLocParser().createGenomeLoc(geli.getChr(),geli.getStart()), alleles, genotypes, geli.getLODBestToReference(), null, attributes);
                return vc;
            } else
                return null; // can't handle anything else
        }
    }

    // --------------------------------------------------------------------------------------------------------------
    //
    // HapMap to VariantContext
    //
    // --------------------------------------------------------------------------------------------------------------

    private static class HapMapAdaptor implements VCAdaptor {
        /**
         * Converts HapMap records to VariantContext. 
         * @return HapMapFeature.
         */
        @Override
        public Class<? extends Feature> getAdaptableFeatureType() { return HapMapFeature.class; }

          /**
         * convert to a Variant Context, given:
         * @param name the name of the ROD
         * @param input the Rod object, in this case a RodGeliText
         * @return a VariantContext object
         */
//        VariantContext convert(String name, Object input) {
//            return convert(name, input, null);
//        }

        /**
         * convert to a Variant Context, given:
         * @param name  the name of the ROD
         * @param input the Rod object, in this case a RodGeliText
         * @param ref   the reference context
         * @return a VariantContext object
         */
        @Override        
        public VariantContext convert(String name, Object input, ReferenceContext ref) {
            if ( ref == null )
                throw new UnsupportedOperationException("Conversion from HapMap to VariantContext requires a reference context");

            HapMapFeature hapmap = (HapMapFeature)input;

            HashSet<Allele> alleles = new HashSet<Allele>();
            Allele refSNPAllele = Allele.create(ref.getBase(), true);
            int deletionLength = -1;

            Map<String, Allele> alleleMap = hapmap.getActualAlleles();
            // use the actual alleles, if available
            if ( alleleMap != null ) {
                alleles.addAll(alleleMap.values());
                Allele deletionAllele = alleleMap.get(HapMapFeature.INSERTION);  // yes, use insertion here (since we want the reference bases)
                if ( deletionAllele != null && deletionAllele.isReference() )
                    deletionLength = deletionAllele.length();
            } else {
                // add the reference allele for SNPs
                alleles.add(refSNPAllele);
            }

            // make a mapping from sample to genotype
            String[] samples = hapmap.getSampleIDs();
            String[] genotypeStrings = hapmap.getGenotypes();

            Map<String, Genotype> genotypes = new HashMap<String, Genotype>(samples.length);
            for ( int i = 0; i < samples.length; i++ ) {
                // ignore bad genotypes
                if ( genotypeStrings[i].contains("N") )
                    continue;

                String a1 = genotypeStrings[i].substring(0,1);
                String a2 = genotypeStrings[i].substring(1);
                ArrayList<Allele> myAlleles = new ArrayList<Allele>(2);

                // use the mapping to actual alleles, if available
                if ( alleleMap != null ) {
                    myAlleles.add(alleleMap.get(a1));
                    myAlleles.add(alleleMap.get(a2));
                } else {
                    // ignore indels (which we can't handle without knowing the alleles)
                    if ( genotypeStrings[i].contains("I") || genotypeStrings[i].contains("D") )
                        continue;

                    Allele allele1 = Allele.create(a1, refSNPAllele.basesMatch(a1));
                    Allele allele2 = Allele.create(a2, refSNPAllele.basesMatch(a2));

                    myAlleles.add(allele1);
                    myAlleles.add(allele2);
                    alleles.add(allele1);
                    alleles.add(allele2);
                }

                Genotype g = new Genotype(samples[i], myAlleles);
                genotypes.put(samples[i], g);
            }

            HashMap<String, Object> attrs = new HashMap<String, Object>(1);
            attrs.put(VariantContext.ID_KEY, hapmap.getName());

            long end = hapmap.getEnd();
            if ( deletionLength > 0 )
                end += deletionLength;
            VariantContext vc = new VariantContext(name, hapmap.getChr(), hapmap.getStart(), end, alleles, genotypes, VariantContext.NO_NEG_LOG_10PERROR, null, attrs);
            return vc;
       }
    }
}
