/*
 * Copyright (c) 2011 The Broad Institute
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
 * FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR
 * THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package org.broadinstitute.sting.gatk.walkers.variantrecalibration;

import org.broadinstitute.sting.utils.variantcontext.VariantContext;
import org.broadinstitute.sting.commandline.Argument;
import org.broadinstitute.sting.commandline.ArgumentCollection;
import org.broadinstitute.sting.commandline.Hidden;
import org.broadinstitute.sting.commandline.Output;
import org.broadinstitute.sting.gatk.contexts.AlignmentContext;
import org.broadinstitute.sting.gatk.contexts.ReferenceContext;
import org.broadinstitute.sting.utils.variantcontext.VariantContextUtils;
import org.broadinstitute.sting.gatk.datasources.rmd.ReferenceOrderedDataSource;
import org.broadinstitute.sting.gatk.refdata.RefMetaDataTracker;
import org.broadinstitute.sting.gatk.walkers.RodWalker;
import org.broadinstitute.sting.gatk.walkers.TreeReducible;
import org.broadinstitute.sting.utils.MathUtils;
import org.broadinstitute.sting.utils.QualityUtils;
import org.broadinstitute.sting.utils.Utils;
import org.broadinstitute.sting.utils.collections.ExpandingArrayList;
import org.broadinstitute.sting.utils.exceptions.UserException;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.util.*;

/**
 * Takes variant calls as .vcf files, learns a Gaussian mixture model over the variant annotations and evaluates the variant -- assigning an informative lod score
 *
 * User: rpoplin
 * Date: 3/12/11
 *
 * @help.summary Takes variant calls as .vcf files, learns a Gaussian mixture model over the variant annotations and evaluates the variant -- assigning an informative lod score
 */

public class VariantRecalibrator extends RodWalker<ExpandingArrayList<VariantDatum>, ExpandingArrayList<VariantDatum>> implements TreeReducible<ExpandingArrayList<VariantDatum>> {

    public static final String VQS_LOD_KEY = "VQSLOD";

    @ArgumentCollection private VariantRecalibratorArgumentCollection VRAC = new VariantRecalibratorArgumentCollection();

    /////////////////////////////
    // Outputs
    /////////////////////////////
    @Output(fullName="recal_file", shortName="recalFile", doc="The output recal file used by ApplyRecalibration", required=true)
    private PrintStream RECAL_FILE;
    @Output(fullName="tranches_file", shortName="tranchesFile", doc="The output tranches file used by ApplyRecalibration", required=true)
    private File TRANCHES_FILE;

    /////////////////////////////
    // Additional Command Line Arguments
    /////////////////////////////
    @Argument(fullName="target_titv", shortName="titv", doc="The expected novel Ti/Tv ratio to use when calculating FDR tranches and for display on optimization curve output figures. (approx 2.15 for whole genome experiments). ONLY USED FOR PLOTTING PURPOSES!", required=false)
    private double TARGET_TITV = 2.15;
    @Argument(fullName="use_annotation", shortName="an", doc="The names of the annotations which should used for calculations", required=true)
    private String[] USE_ANNOTATIONS = null;
    @Argument(fullName="TStranche", shortName="tranche", doc="The levels of novel false discovery rate (FDR, implied by ti/tv) at which to slice the data. (in percent, that is 1.0 for 1 percent)", required=false)
    private double[] TS_TRANCHES = new double[] {100.0, 99.9, 99.0, 90.0};
    @Argument(fullName="ignore_filter", shortName="ignoreFilter", doc="If specified the optimizer will use variants even if the specified filter name is marked in the input VCF file", required=false)
    private String[] IGNORE_INPUT_FILTERS = null;
    @Argument(fullName="path_to_Rscript", shortName = "Rscript", doc = "The path to your implementation of Rscript. For Broad users this is maybe /broad/tools/apps/R-2.6.0/bin/Rscript", required=false)
    private String PATH_TO_RSCRIPT = "Rscript";
    @Argument(fullName="rscript_file", shortName="rscriptFile", doc="The output rscript file generated by the VQSR to aid in visualization of the input data and learned model", required=false)
    private String RSCRIPT_FILE = null;
    @Argument(fullName = "path_to_resources", shortName = "resources", doc = "Path to resources folder holding the Sting R scripts.", required=false)
    private String PATH_TO_RESOURCES = "R/";
    @Argument(fullName="ts_filter_level", shortName="ts_filter_level", doc="The truth sensitivity level at which to start filtering, used here to indicate filtered variants in plots", required=false)
    private double TS_FILTER_LEVEL = 99.0;

    /////////////////////////////
    // Debug Arguments
    /////////////////////////////
    @Hidden
    @Argument(fullName = "trustAllPolymorphic", shortName = "allPoly", doc = "Trust that all the input training sets' unfiltered records contain only polymorphic sites to drastically speed up the computation.", required = false)
    protected Boolean TRUST_ALL_POLYMORPHIC = false;
    @Hidden
    @Argument(fullName = "projectConsensus", shortName = "projectConsensus", doc = "Perform 1000G project consensus. This implies an extra prior factor based on the individual participant callsets passed in with consensus=true rod binding tags.", required = false)
    protected Boolean PERFORM_PROJECT_CONSENSUS = false;

    /////////////////////////////
    // Private Member Variables
    /////////////////////////////
    private VariantDataManager dataManager;
    private PrintStream tranchesStream;
    private final Set<String> ignoreInputFilterSet = new TreeSet<String>();
    private final Set<String> inputNames = new HashSet<String>();
    private final VariantRecalibratorEngine engine = new VariantRecalibratorEngine( VRAC );

    //---------------------------------------------------------------------------------------------------------------
    //
    // initialize
    //
    //---------------------------------------------------------------------------------------------------------------

    public void initialize() {
        dataManager = new VariantDataManager( new ArrayList<String>(Arrays.asList(USE_ANNOTATIONS)), VRAC );

        if( IGNORE_INPUT_FILTERS != null ) {
            ignoreInputFilterSet.addAll( Arrays.asList(IGNORE_INPUT_FILTERS) );
        }

        for( ReferenceOrderedDataSource d : this.getToolkit().getRodDataSources() ) {
            if( d.getName().toLowerCase().startsWith("input") ) {
                inputNames.add(d.getName());
                logger.info( "Found input variant track with name " + d.getName() );
            } else {
                dataManager.addTrainingSet( new TrainingSet(d.getName(), d.getTags()) );
            }
        }

        if( !dataManager.checkHasTrainingSet() ) {
            throw new UserException.CommandLineException( "No training set found! Please provide sets of known polymorphic loci marked with the training=true ROD binding tag. For example, -B:hapmap,VCF,known=false,training=true,truth=true,prior=12.0 hapmapFile.vcf" );
        }
        if( !dataManager.checkHasTruthSet() ) {
            throw new UserException.CommandLineException( "No truth set found! Please provide sets of known polymorphic loci marked with the truth=true ROD binding tag. For example, -B:hapmap,VCF,known=false,training=true,truth=true,prior=12.0 hapmapFile.vcf" );
        }

        if( inputNames.size() == 0 ) {
            throw new UserException.BadInput( "No input variant tracks found. Input variant binding names must begin with 'input'." );
        }

        try {
            tranchesStream = new PrintStream(TRANCHES_FILE);
        } catch (FileNotFoundException e) {
            throw new UserException.CouldNotCreateOutputFile(TRANCHES_FILE, e);
        }
    }

    //---------------------------------------------------------------------------------------------------------------
    //
    // map
    //
    //---------------------------------------------------------------------------------------------------------------

    public ExpandingArrayList<VariantDatum> map( final RefMetaDataTracker tracker, final ReferenceContext ref, final AlignmentContext context ) {
        final ExpandingArrayList<VariantDatum> mapList = new ExpandingArrayList<VariantDatum>();

        if( tracker == null ) { // For some reason RodWalkers get map calls with null trackers
            return mapList;
        }

        for( final VariantContext vc : tracker.getVariantContexts(ref, inputNames, null, context.getLocation(), true, false) ) {
            if( vc != null && ( vc.isNotFiltered() || ignoreInputFilterSet.containsAll(vc.getFilters()) ) ) {
                if( checkRecalibrationMode( vc, VRAC.MODE ) ) {
                    final VariantDatum datum = new VariantDatum();
                    dataManager.decodeAnnotations( datum, vc, true ); //BUGBUG: when run with HierarchicalMicroScheduler this is non-deterministic because order of calls depends on load of machine
                    datum.contig = vc.getChr();
                    datum.start = vc.getStart();
                    datum.stop = vc.getEnd();
                    datum.originalQual = vc.getPhredScaledQual();
                    datum.isSNP = vc.isSNP() && vc.isBiallelic();
                    datum.isTransition = datum.isSNP && VariantContextUtils.isTransition(vc);
                    datum.usedForTraining = 0;

                    // Loop through the training data sets and if they overlap this loci then update the prior and training status appropriately
                    dataManager.parseTrainingSets( tracker, ref, context, vc, datum, TRUST_ALL_POLYMORPHIC );
                    double priorFactor = QualityUtils.qualToProb( datum.prior );
                    if( PERFORM_PROJECT_CONSENSUS ) {
                        final double consensusPrior = QualityUtils.qualToProb( 1.0 + 5.0 * datum.consensusCount );
                        priorFactor = 1.0 - ((1.0 - priorFactor) * (1.0 - consensusPrior));
                    }
                    datum.prior = Math.log10( priorFactor ) - Math.log10( 1.0 - priorFactor );

                    mapList.add( datum );
                }
            }
        }

        return mapList;
    }

    public static boolean checkRecalibrationMode( final VariantContext vc, final VariantRecalibratorArgumentCollection.Mode mode ) {
        return mode == VariantRecalibratorArgumentCollection.Mode.BOTH ||
                (mode == VariantRecalibratorArgumentCollection.Mode.SNP && vc.isSNP()) ||
	            (mode == VariantRecalibratorArgumentCollection.Mode.INDEL && (vc.isIndel() || vc.isMixed()));
    }

    //---------------------------------------------------------------------------------------------------------------
    //
    // reduce
    //
    //---------------------------------------------------------------------------------------------------------------

    public ExpandingArrayList<VariantDatum> reduceInit() {
        return new ExpandingArrayList<VariantDatum>();
    }

    public ExpandingArrayList<VariantDatum> reduce( final ExpandingArrayList<VariantDatum> mapValue, final ExpandingArrayList<VariantDatum> reduceSum ) {
        reduceSum.addAll( mapValue );
        return reduceSum;
    }

    public ExpandingArrayList<VariantDatum> treeReduce( final ExpandingArrayList<VariantDatum> lhs, final ExpandingArrayList<VariantDatum> rhs ) {
        rhs.addAll( lhs );
        return rhs;
    }

    //---------------------------------------------------------------------------------------------------------------
    //
    // on traversal done
    //
    //---------------------------------------------------------------------------------------------------------------

    public void onTraversalDone( final ExpandingArrayList<VariantDatum> reduceSum ) {
        dataManager.setData( reduceSum );
        dataManager.normalizeData(); // Each data point is now (x - mean) / standard deviation
        final GaussianMixtureModel goodModel = engine.generateModel( dataManager.getTrainingData() );
        engine.evaluateData( dataManager.getData(), goodModel, false );
        final GaussianMixtureModel badModel = engine.generateModel( dataManager.selectWorstVariants( VRAC.PERCENT_BAD_VARIANTS, VRAC.MIN_NUM_BAD_VARIANTS ) );
        engine.evaluateData( dataManager.getData(), badModel, true );

        final ExpandingArrayList<VariantDatum> randomData = dataManager.getRandomDataForPlotting( 6000 );

        final int nCallsAtTruth = TrancheManager.countCallsAtTruth( dataManager.getData(), Double.NEGATIVE_INFINITY );
        final TrancheManager.SelectionMetric metric = new TrancheManager.TruthSensitivityMetric( nCallsAtTruth );
        final List<Tranche> tranches = TrancheManager.findTranches( dataManager.getData(), TS_TRANCHES, metric );
        tranchesStream.print(Tranche.tranchesString( tranches ));

        double lodCutoff = 0.0;
        for( final Tranche tranche : tranches ) {
            if( MathUtils.compareDoubles(tranche.ts, TS_FILTER_LEVEL, 0.0001)==0 ) {
                lodCutoff = tranche.minVQSLod;
            }
        }

        logger.info( "Writing out recalibration table..." );
        dataManager.writeOutRecalibrationTable( RECAL_FILE );
        if( RSCRIPT_FILE != null ) {
            logger.info( "Writing out visualization Rscript file...");
            createVisualizationScript( randomData, goodModel, badModel, lodCutoff );
        }

        // Execute Rscript command to create the tranche plot
        // Print out the command line to make it clear to the user what is being executed and how one might modify it
        final String rScriptTranchesCommandLine = PATH_TO_RSCRIPT + " " + PATH_TO_RESOURCES + "plot_Tranches.R" + " " + TRANCHES_FILE.getAbsolutePath() + " " + TARGET_TITV;
        logger.info( "Executing: " + rScriptTranchesCommandLine );

        // Execute the RScript command to plot the table of truth values
        try {
            Process p;
            p = Runtime.getRuntime().exec( rScriptTranchesCommandLine );
            p.waitFor();
        } catch ( Exception e ) {
            Utils.warnUser("Unable to execute the RScript command.  While not critical to the calculations themselves, the script outputs a report that is extremely useful for confirming that the recalibration proceded as expected.  We highly recommend trying to rerun the script manually if possible.");
        }

    }

    private void createVisualizationScript( final ExpandingArrayList<VariantDatum> randomData, final GaussianMixtureModel goodModel, final GaussianMixtureModel badModel, final double lodCutoff ) {
        PrintStream stream;
        try {
            stream = new PrintStream(RSCRIPT_FILE);
        } catch( FileNotFoundException e ) {
            throw new UserException.CouldNotCreateOutputFile(RSCRIPT_FILE, "", e);
        }
        stream.println("library(ggplot2)");

        createArrangeFunction( stream );

        stream.println("pdf(\"" + RSCRIPT_FILE + ".pdf\")"); // Unfortunately this is a huge pdf file, BUGBUG: need to work on reducing the file size

        for(int iii = 0; iii < USE_ANNOTATIONS.length; iii++) {
            for( int jjj = iii + 1; jjj < USE_ANNOTATIONS.length; jjj++) {
                logger.info( "Building " + USE_ANNOTATIONS[iii] + " x " + USE_ANNOTATIONS[jjj] + " plot...");

                final ExpandingArrayList<VariantDatum> fakeData = new ExpandingArrayList<VariantDatum>();
                double minAnn1 = 100.0, maxAnn1 = -100.0, minAnn2 = 100.0, maxAnn2 = -100.0;
                for( final VariantDatum datum : randomData ) {
                    minAnn1 = Math.min(minAnn1, datum.annotations[iii]);
                    maxAnn1 = Math.max(maxAnn1, datum.annotations[iii]);
                    minAnn2 = Math.min(minAnn2, datum.annotations[jjj]);
                    maxAnn2 = Math.max(maxAnn2, datum.annotations[jjj]);
                }
                // Create a fake set of data which spans the full extent of these two annotation dimensions in order to calculate the model PDF projected to 2D
                for(double ann1 = minAnn1; ann1 <= maxAnn1; ann1+=0.1) {
                    for(double ann2 = minAnn2; ann2 <= maxAnn2; ann2+=0.1) {
                        final VariantDatum datum = new VariantDatum();
                        datum.prior = 0.0;
                        datum.annotations = new double[randomData.get(0).annotations.length];
                        datum.isNull = new boolean[randomData.get(0).annotations.length];
                        for(int ann=0; ann< datum.annotations.length; ann++) {
                            datum.annotations[ann] = 0.0;
                            datum.isNull[ann] = true;
                        }
                        datum.annotations[iii] = ann1;
                        datum.annotations[jjj] = ann2;
                        datum.isNull[iii] = false;
                        datum.isNull[jjj] = false;
                        fakeData.add(datum);
                    }
                }

                engine.evaluateData( fakeData, goodModel, false );
                engine.evaluateData( fakeData, badModel, true );

                stream.print("surface <- c(");
                for( final VariantDatum datum : fakeData ) {
                    stream.print(String.format("%.3f, %.3f, %.3f, ", datum.annotations[iii], datum.annotations[jjj], Math.min(4.0, Math.max(-4.0, datum.lod))));
                }
                stream.println("NA,NA,NA)");
                stream.println("s <- matrix(surface,ncol=3,byrow=T)");

                stream.print("data <- c(");
                for( final VariantDatum datum : randomData ) {
                    stream.print(String.format("%.3f, %.3f, %.3f, %d, %d,", datum.annotations[iii], datum.annotations[jjj], (datum.lod < lodCutoff ? -1.0 : 1.0), datum.usedForTraining, (datum.isKnown ? 1 : -1)));
                }
                stream.println("NA,NA,NA,NA,1)");
                stream.println("d <- matrix(data,ncol=5,byrow=T)");

                final String surfaceFrame = "sf." + USE_ANNOTATIONS[iii] + "." + USE_ANNOTATIONS[jjj];
                final String dataFrame = "df." + USE_ANNOTATIONS[iii] + "." + USE_ANNOTATIONS[jjj];

                stream.println(surfaceFrame + " <- data.frame(x=s[,1], y=s[,2], lod=s[,3])");
                stream.println(dataFrame + " <- data.frame(x=d[,1], y=d[,2], retained=d[,3], training=d[,4], novelty=d[,5])");
                stream.println("dummyData <- " + dataFrame + "[1,]");
                stream.println("dummyData$x <- NaN");
                stream.println("dummyData$y <- NaN");
                stream.println("p <- ggplot(data=" + surfaceFrame + ", aes(x=x, y=y)) + opts(panel.background = theme_rect(colour = NA), panel.grid.minor = theme_line(colour = NA), panel.grid.major = theme_line(colour = NA))");
                stream.println("p1 = p + opts(title=\"model PDF\") + labs(x=\""+ USE_ANNOTATIONS[iii] +"\", y=\""+ USE_ANNOTATIONS[jjj] +"\") + geom_tile(aes(fill = lod)) + scale_fill_gradient(high=\"green\", low=\"red\")");
                stream.println("p <- qplot(x,y,data=" + dataFrame + ", color=retained, alpha=I(1/7),legend=FALSE) + opts(panel.background = theme_rect(colour = NA), panel.grid.minor = theme_line(colour = NA), panel.grid.major = theme_line(colour = NA))");
                stream.println("q <- geom_point(aes(x=x,y=y,color=retained),data=dummyData, alpha=1.0, na.rm=TRUE)");
                stream.println("p2 = p + q + labs(x=\""+ USE_ANNOTATIONS[iii] +"\", y=\""+ USE_ANNOTATIONS[jjj] +"\") + scale_colour_gradient(name=\"outcome\", high=\"black\", low=\"red\",breaks=c(-1,1),labels=c(\"filtered\",\"retained\"))");
                stream.println("p <- qplot(x,y,data="+ dataFrame + "["+dataFrame+"$training != 0,], color=training, alpha=I(1/7)) + opts(panel.background = theme_rect(colour = NA), panel.grid.minor = theme_line(colour = NA), panel.grid.major = theme_line(colour = NA))");
                stream.println("q <- geom_point(aes(x=x,y=y,color=training),data=dummyData, alpha=1.0, na.rm=TRUE)");
                stream.println("p3 = p + q + labs(x=\""+ USE_ANNOTATIONS[iii] +"\", y=\""+ USE_ANNOTATIONS[jjj] +"\") + scale_colour_gradient(high=\"green\", low=\"purple\",breaks=c(-1,1), labels=c(\"neg\", \"pos\"))");
                stream.println("p <- qplot(x,y,data=" + dataFrame + ", color=novelty, alpha=I(1/7)) + opts(panel.background = theme_rect(colour = NA), panel.grid.minor = theme_line(colour = NA), panel.grid.major = theme_line(colour = NA))");
                stream.println("q <- geom_point(aes(x=x,y=y,color=novelty),data=dummyData, alpha=1.0, na.rm=TRUE)");
                stream.println("p4 = p + q + labs(x=\""+ USE_ANNOTATIONS[iii] +"\", y=\""+ USE_ANNOTATIONS[jjj] +"\") + scale_colour_gradient(name=\"novelty\", high=\"blue\", low=\"red\",breaks=c(-1,1), labels=c(\"novel\",\"known\"))");
                stream.println("arrange(p1, p2, p3, p4, ncol=2)");
            }
        }
        stream.println("dev.off()");

        stream.close();

        // Execute Rscript command to generate the clustering plots
        final String rScriptTranchesCommandLine = PATH_TO_RSCRIPT + " " +  RSCRIPT_FILE;
        logger.info( "Executing: " + rScriptTranchesCommandLine );
        try {
            Process p;
            p = Runtime.getRuntime().exec( rScriptTranchesCommandLine );
            p.waitFor();
        } catch ( Exception e ) {
            Utils.warnUser("Unable to execute the RScript command.  While not critical to the calculations themselves, the script outputs a report that is extremely useful for visualizing the recalibration results.  We highly recommend trying to rerun the script manually if possible.");
        }
     }

    // from http://gettinggeneticsdone.blogspot.com/2010/03/arrange-multiple-ggplot2-plots-in-same.html
    private void createArrangeFunction( final PrintStream stream ) {
        stream.println("vp.layout <- function(x, y) viewport(layout.pos.row=x, layout.pos.col=y)");
        stream.println("arrange <- function(..., nrow=NULL, ncol=NULL, as.table=FALSE) {");
        stream.println("dots <- list(...)");
        stream.println("n <- length(dots)");
        stream.println("if(is.null(nrow) & is.null(ncol)) { nrow = floor(n/2) ; ncol = ceiling(n/nrow)}");
        stream.println("if(is.null(nrow)) { nrow = ceiling(n/ncol)}");
        stream.println("if(is.null(ncol)) { ncol = ceiling(n/nrow)}");
        stream.println("grid.newpage()");
        stream.println("pushViewport(viewport(layout=grid.layout(nrow,ncol) ) )");
        stream.println("ii.p <- 1");
        stream.println("for(ii.row in seq(1, nrow)){");
        stream.println("ii.table.row <- ii.row ");
        stream.println("if(as.table) {ii.table.row <- nrow - ii.table.row + 1}");
        stream.println("for(ii.col in seq(1, ncol)){");
        stream.println("ii.table <- ii.p");
        stream.println("if(ii.p > n) break");
        stream.println("print(dots[[ii.table]], vp=vp.layout(ii.table.row, ii.col))");
        stream.println("ii.p <- ii.p + 1");
        stream.println("}");
        stream.println("}");
        stream.println("}");
    }

 }
