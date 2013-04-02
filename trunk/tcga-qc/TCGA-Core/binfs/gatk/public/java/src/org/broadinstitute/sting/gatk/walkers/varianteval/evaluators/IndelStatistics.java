package org.broadinstitute.sting.gatk.walkers.varianteval.evaluators;

import org.broadinstitute.sting.utils.variantcontext.Genotype;
import org.broadinstitute.sting.utils.variantcontext.VariantContext;
import org.broadinstitute.sting.gatk.contexts.AlignmentContext;
import org.broadinstitute.sting.gatk.contexts.ReferenceContext;
import org.broadinstitute.sting.gatk.refdata.RefMetaDataTracker;
import org.broadinstitute.sting.gatk.walkers.varianteval.VariantEvalWalker;
import org.broadinstitute.sting.gatk.walkers.varianteval.tags.Analysis;
import org.broadinstitute.sting.gatk.walkers.varianteval.tags.DataPoint;
import org.broadinstitute.sting.gatk.walkers.varianteval.util.TableType;
import org.broadinstitute.sting.utils.IndelUtils;

import java.util.ArrayList;
import java.util.HashMap;

/*
 * Copyright (c) 2010 The Broad Institute
 *
 * Permission is hereby granted, free of charge, to any person
 * obtaining a copy of this software and associated documentation
 * files (the "Software"), to deal in the Software without
 * restriction, including without limitation the rights to use,
 * copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the
 * Software is furnished to do so, subject to the following
 * conditions:
 *
 * The above copyright notice and this permission notice shall be
 * included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES
 * OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT
 * HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY,
 * WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING
 * FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR
 * OTHER DEALINGS IN THE SOFTWARE.
 */

@Analysis(name = "IndelStatistics", description = "Shows various indel metrics and statistics")
public class IndelStatistics extends VariantEvaluator {
    @DataPoint(description = "Indel Statistics")
    IndelStats indelStats = null;

    @DataPoint(description = "Indel Classification")
    IndelClasses indelClasses = null;

    int numSamples = 0;

    public void initialize(VariantEvalWalker walker) {
        numSamples = walker.getNumSamples();
    }

    private static final int INDEL_SIZE_LIMIT = 100;
    private static final int IND_HET = 0;
    private static final int IND_INS = 1;
    private static final int IND_DEL = 2;
    private static final int IND_AT_CG_RATIO = 3;
    private static final int IND_HET_INS = 4;
    private static final int IND_HOM_INS = 5;
    private static final int IND_HET_DEL = 6;
    private static final int IND_HOM_DEL = 7;
    private static final int IND_HOM_REF = 8;
    private static final int IND_COMPLEX = 9;
    private static final int IND_LONG = 10;
    private static final int IND_AT_EXP = 11;
    private static final int IND_CG_EXP = 12;
    private static final int IND_FRAMESHIFT = 13;
    private static final int NUM_SCALAR_COLUMNS = 14;

    static int len2Index(int ind) {
        return ind+INDEL_SIZE_LIMIT+NUM_SCALAR_COLUMNS;
    }

    static int index2len(int ind) {
        return ind-INDEL_SIZE_LIMIT-NUM_SCALAR_COLUMNS;
    }

    static class IndelStats implements TableType {
        protected final static String ALL_SAMPLES_KEY = "allSamples";
        protected final static String[] COLUMN_KEYS;

         static {
            COLUMN_KEYS= new String[NUM_SCALAR_COLUMNS+2*INDEL_SIZE_LIMIT+1];
            COLUMN_KEYS[0] = "heterozygosity";
            COLUMN_KEYS[1] = "insertions";
            COLUMN_KEYS[2] = "deletions";
            COLUMN_KEYS[3] = "AT_CG_expansion_ratio";
            COLUMN_KEYS[4] = "het_insertions";
            COLUMN_KEYS[5] = "homozygous_insertions";
            COLUMN_KEYS[6] = "het_deletions";
            COLUMN_KEYS[7] = "homozygous_deletions";
            COLUMN_KEYS[8] = "homozygous_reference_sites";
            COLUMN_KEYS[9] = "complex_events";
            COLUMN_KEYS[10] = "long_indels";
            COLUMN_KEYS[11] = "AT_expansions";
            COLUMN_KEYS[12] = "CG_expansions";
            COLUMN_KEYS[13] = "frameshift_indels";

            for (int k=NUM_SCALAR_COLUMNS; k < NUM_SCALAR_COLUMNS+ 2*INDEL_SIZE_LIMIT+1; k++)
                COLUMN_KEYS[k] = "indel_size_len"+Integer.valueOf(index2len(k));
        }

        // map of sample to statistics
        protected final HashMap<String, int[]> indelSummary = new HashMap<String, int[]>();

        public IndelStats(final VariantContext vc) {
            indelSummary.put(ALL_SAMPLES_KEY, new int[COLUMN_KEYS.length]);
            for( final String sample : vc.getGenotypes().keySet() ) {
                indelSummary.put(sample, new int[COLUMN_KEYS.length]);
            }
        }

        /**
         *
         * @return one row per sample
         */
        public Object[] getRowKeys() {
            return indelSummary.keySet().toArray(new String[indelSummary.size()]);
        }
        public Object getCell(int x, int y) {
            final Object[] rowKeys = getRowKeys();
            if (y == IND_AT_CG_RATIO) {

                int at = indelSummary.get(rowKeys[x])[IND_AT_EXP];
                int cg = indelSummary.get(rowKeys[x])[IND_CG_EXP];
                return String.format("%4.2f",((double)at) / (Math.max(cg, 1)));
            }
            else
                return String.format("%d",indelSummary.get(rowKeys[x])[y]);

        }

        /**
         * get the column keys
         * @return a list of objects, in this case strings, that are the column names
         */
        public Object[] getColumnKeys() {
            return COLUMN_KEYS;
        }

        public String getName() {
            return "IndelStats";
        }

        public int getComparisonOrder() {
            return 1;   // we only need to see each eval track
        }

         public String toString() {
            return getName();
        }

        /*
         * increment the specified value
         */
        public void incrValue(VariantContext vc, ReferenceContext ref) {
            int eventLength = 0;
            boolean isInsertion = false, isDeletion = false;

            if ( vc.isInsertion() ) {
                eventLength = vc.getAlternateAllele(0).length();
                indelSummary.get(ALL_SAMPLES_KEY)[IND_INS]++;
                isInsertion = true;
            } else if ( vc.isDeletion() ) {
                indelSummary.get(ALL_SAMPLES_KEY)[IND_DEL]++;
                eventLength = -vc.getReference().length();
                isDeletion = true;
            }
            else {
                indelSummary.get(ALL_SAMPLES_KEY)[IND_COMPLEX]++;
            }
            if (IndelUtils.isATExpansion(vc,ref))
                indelSummary.get(ALL_SAMPLES_KEY)[IND_AT_EXP]++;
            if (IndelUtils.isCGExpansion(vc,ref))
                 indelSummary.get(ALL_SAMPLES_KEY)[IND_CG_EXP]++;

            // make sure event doesn't overstep array boundaries
            if (Math.abs(eventLength) < INDEL_SIZE_LIMIT) {
                indelSummary.get(ALL_SAMPLES_KEY)[len2Index(eventLength)]++;
                if (eventLength % 3 != 0)
                    indelSummary.get(ALL_SAMPLES_KEY)[IND_FRAMESHIFT]++;
            }
            else
                indelSummary.get(ALL_SAMPLES_KEY)[IND_LONG]++;


            for( final String sample : vc.getGenotypes().keySet() ) {
                if ( indelSummary.containsKey(sample) ) {
                    Genotype g = vc.getGenotype(sample);
                    boolean isVariant = (g.isCalled() && !g.isHomRef());
                    if (isVariant) {
                        // update ins/del count
                        if (isInsertion) {
                            indelSummary.get(sample)[IND_INS]++;
                        }
                        else if (isDeletion)
                            indelSummary.get(sample)[IND_DEL]++;
                        else
                            indelSummary.get(sample)[IND_COMPLEX]++;

                        // update histogram
                        if (Math.abs(eventLength) < INDEL_SIZE_LIMIT) {
                            indelSummary.get(sample)[len2Index(eventLength)]++;
                            if (eventLength % 3 != 0)
                                indelSummary.get(sample)[IND_FRAMESHIFT]++;    
                        }
                        else
                            indelSummary.get(sample)[IND_LONG]++;

                        if (g.isHet())
                            if (isInsertion)
                                indelSummary.get(sample)[IND_HET_INS]++;
                            else if (isDeletion)
                                indelSummary.get(sample)[IND_HET_DEL]++;
                        else
                            if (isInsertion)
                                indelSummary.get(sample)[IND_HOM_INS]++;
                            else if (isDeletion)
                                indelSummary.get(sample)[IND_HOM_DEL]++;

                        if (IndelUtils.isATExpansion(vc,ref))
                            indelSummary.get(sample)[IND_AT_EXP]++;
                        if (IndelUtils.isCGExpansion(vc,ref))
                             indelSummary.get(sample)[IND_CG_EXP]++;


                    }
                    else
                        indelSummary.get(sample)[IND_HOM_REF]++;
                }
            }


        }
    }

    static class IndelClasses implements TableType {
        protected final static String ALL_SAMPLES_KEY = "allSamples";
        protected final static String[] columnNames = IndelUtils.getIndelClassificationNames();


        // map of sample to statistics
        protected final HashMap<String, int[]> indelClassSummary = new HashMap<String, int[]>();

        public IndelClasses(final VariantContext vc) {
            indelClassSummary.put(ALL_SAMPLES_KEY, new int[columnNames.length]);
            for( final String sample : vc.getGenotypes().keySet() ) {
                indelClassSummary.put(sample, new int[columnNames.length]);
            }
        }

        /**
         *
         * @return one row per sample
         */
        public Object[] getRowKeys() {
            return indelClassSummary.keySet().toArray(new String[indelClassSummary.size()]);
        }
        public Object getCell(int x, int y) {
            final Object[] rowKeys = getRowKeys();
            return String.format("%d",indelClassSummary.get(rowKeys[x])[y]);
        }

        /**
         * get the column keys
         * @return a list of objects, in this case strings, that are the column names
         */
        public Object[] getColumnKeys() {
            return columnNames;
        }

        public String getName() {
            return "IndelClasses";
        }

        public int getComparisonOrder() {
            return 1;   // we only need to see each eval track
        }

         public String toString() {
            return getName();
        }

        private void incrementSampleStat(VariantContext vc, int index) {
            indelClassSummary.get(ALL_SAMPLES_KEY)[index]++;
            for( final String sample : vc.getGenotypes().keySet() ) {
                 if ( indelClassSummary.containsKey(sample) ) {
                     Genotype g = vc.getGenotype(sample);
                     boolean isVariant = (g.isCalled() && !g.isHomRef());
                     if (isVariant)
                         // update  count
                         indelClassSummary.get(sample)[index]++;

                 }
             }

        }
        /*
         * increment the specified value
         */
         public void incrValue(VariantContext vc, ReferenceContext ref) {


            ArrayList<Integer> indices = IndelUtils.findEventClassificationIndex(vc,ref);
             //System.out.format("pos:%d \nREF: %s, ALT: %s\n",vc.getStart(), vc.getReference().getDisplayString(),
             //  vc.getAlternateAllele(0).getDisplayString());

             byte[] refBases = ref.getBases();
             //System.out.format("ref bef:%s\n",new String(Arrays.copyOfRange(refBases,0,refBases.length/2+1) ));
             //System.out.format("ref aft:%s\n",new String(Arrays.copyOfRange(refBases,refBases.length/2+1,refBases.length) ));
            for (int index: indices)    {
                incrementSampleStat(vc, index);
               // System.out.println(IndelUtils.getIndelClassificationName(index));
            }
        }

    }

    //public IndelStatistics(VariantEvalWalker parent) {
        //super(parent);
        // don't do anything
    //}

    public String getName() {
        return "IndelStatistics";
    }

    public int getComparisonOrder() {
        return 1;   // we only need to see each eval track
    }

    public boolean enabled() {
        return true;
    }

    public String toString() {
        return getName();
    }

    public String update1(VariantContext eval, RefMetaDataTracker tracker, ReferenceContext ref, AlignmentContext context) {

        if (eval != null ) {
            if ( indelStats == null ) {
                int nSamples = numSamples;

                if ( nSamples != -1 )
                    indelStats = new IndelStats(eval);
            }
            if ( indelClasses == null ) {
                indelClasses = new IndelClasses(eval);
            }

            if ( eval.isIndel() && eval.isBiallelic() ) {
                if (indelStats != null )
                    indelStats.incrValue(eval, ref);

                if (indelClasses != null)
                    indelClasses.incrValue(eval, ref);
            }
        }

        return null; // This module doesn't capture any interesting sites, so return null
    }

    public void finalizeEvaluation() {
        int k=0;
    }

}
