package org.broadinstitute.sting.utils;

import org.broadinstitute.sting.utils.variantcontext.Genotype;
import org.broadinstitute.sting.utils.variantcontext.VariantContext;
import org.broadinstitute.sting.gatk.GenomeAnalysisEngine;
import org.broadinstitute.sting.gatk.datasources.sample.Sample;
import org.broadinstitute.sting.utils.exceptions.UserException;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * User: carneiro
 * Date: 3/9/11
 * Time: 12:38 PM
 */
public class MendelianViolation {



    String sampleMom;
    String sampleDad;
    String sampleChild;

    List allelesMom;
    List allelesDad;
    List allelesChild;

    double minGenotypeQuality;

    private static Pattern FAMILY_PATTERN = Pattern.compile("(.*)\\+(.*)=(.*)");


    public String getSampleMom() {
        return sampleMom;
    }

    public String getSampleDad() {
        return sampleDad;
    }

    public String getSampleChild() {
        return sampleChild;
    }

    public double getMinGenotypeQuality() {
        return minGenotypeQuality;
    }

    /**
     *
     * @param sampleMomP - sample name of mom
     * @param sampleDadP - sample name of dad
     * @param sampleChildP - sample name of child
     */
    public MendelianViolation (String sampleMomP, String sampleDadP, String sampleChildP) {
        sampleMom = sampleMomP;
        sampleDad = sampleDadP;
        sampleChild = sampleChildP;
    }

    /**
     *
     * @param family - the sample names string "mom+dad=child"
     * @param minGenotypeQualityP - the minimum phred scaled genotype quality score necessary to asses mendelian violation
     */
    public MendelianViolation(String family, double minGenotypeQualityP) {
        minGenotypeQuality = minGenotypeQualityP;

        Matcher m = FAMILY_PATTERN.matcher(family);
        if (m.matches()) {
            sampleMom = m.group(1);
            sampleDad = m.group(2);
            sampleChild = m.group(3);
        }
        else
            throw new IllegalArgumentException("Malformatted family structure string: " + family + " required format is mom+dad=child");
    }

    /**
     * An alternative to the more general constructor if you want to get the Sample information from the engine yourself.
     * @param sample - the sample object extracted from the sample metadata YAML file given to the engine.
     * @param minGenotypeQualityP - the minimum phred scaled genotype quality score necessary to asses mendelian violation
     */
    public MendelianViolation(Sample sample, double minGenotypeQualityP) {
        sampleMom = sample.getMother().getId();
        sampleDad = sample.getFather().getId();
        sampleChild = sample.getId();
        minGenotypeQuality = minGenotypeQualityP;
    }


    /**
     * The most common constructor to be used when give a YAML file with the relationships to the engine with the -SM option.
     * @param engine - The GATK engine, use getToolkit(). That's where the sample information is stored.
     * @param minGenotypeQualityP - the minimum phred scaled genotype quality score necessary to asses mendelian violation
     */
    public MendelianViolation(GenomeAnalysisEngine engine, double minGenotypeQualityP) {
        boolean gotSampleInformation = false;
        Collection<Sample> samples = engine.getSamples();
        // Iterate through all samples in the sample_metadata file but we really can only take one.
        for (Sample sample : samples) {
            if (sample.getMother() != null && sample.getFather() != null) {
                sampleMom = sample.getMother().getId();
                sampleDad = sample.getFather().getId();
                sampleChild = sample.getId();
                minGenotypeQuality = minGenotypeQualityP;
                gotSampleInformation = true;
                break; // we can only deal with one trio information
            }
        }
        if (!gotSampleInformation)
            throw new UserException("YAML file has no sample with relationship information (mother/father)");
    }


    /**
     * This method prepares the object to evaluate for violation. Typically you won't call it directly, a call to
     * isViolation(vc) will take care of this. But if you want to know whether your site was a valid comparison site
     * before evaluating it for mendelian violation, you can call setAlleles and then isViolation().
     * @param vc - the variant context to extract the genotypes and alleles for mom, dad and child.
     * @return false if couldn't find the genotypes or context has empty alleles. True otherwise.
     */
    public boolean setAlleles (VariantContext vc)
    {
        Genotype gMom = vc.getGenotypes(sampleMom).get(sampleMom);
        Genotype gDad = vc.getGenotypes(sampleDad).get(sampleDad);
        Genotype gChild = vc.getGenotypes(sampleChild).get(sampleChild);

        if (gMom == null || gDad == null || gChild == null)
            throw new IllegalArgumentException(String.format("Variant %s:%d didn't contain genotypes for all family members: mom=%s dad=%s child=%s", vc.getChr(), vc.getStart(), sampleMom, sampleDad, sampleChild));

        if (gMom.isNoCall() || gDad.isNoCall() || gChild.isNoCall() ||
            gMom.getPhredScaledQual()   < minGenotypeQuality ||
            gDad.getPhredScaledQual()   < minGenotypeQuality ||
            gChild.getPhredScaledQual() < minGenotypeQuality ) {

            return false;
        }

        allelesMom = gMom.getAlleles();
        allelesDad = gDad.getAlleles();
        allelesChild = gChild.getAlleles();
        return !allelesMom.isEmpty() && !allelesDad.isEmpty() && !allelesChild.isEmpty();
    }


    /**
     *
     * @param vc the variant context to extract the genotypes and alleles for mom, dad and child.
     * @return False if we can't determine (lack of information), or it's not a violation. True if it is a violation.
     *
     */
    public boolean isViolation (VariantContext vc)
    {
        return setAlleles(vc) && isViolation();
    }

    /**
     * @return whether or not there is a mendelian violation at the site.
     */
    public boolean isViolation() {
        if (allelesMom.contains(allelesChild.get(0)) && allelesDad.contains(allelesChild.get(1)) ||
            allelesMom.contains(allelesChild.get(1)) && allelesDad.contains(allelesChild.get(0)))
            return false;
        return true;
    }

}
