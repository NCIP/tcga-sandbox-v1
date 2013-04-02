package org.broadinstitute.sting.gatk.walkers.variantrecalibration;

import org.apache.log4j.Logger;
import org.broadinstitute.sting.utils.exceptions.UserException;

import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: rpoplin
 * Date: Mar 4, 2011
 */

public class VariantRecalibratorEngine {

    /////////////////////////////
    // Private Member Variables
    /////////////////////////////

    protected final static Logger logger = Logger.getLogger(VariantRecalibratorEngine.class);

    // the unified argument collection
    final private VariantRecalibratorArgumentCollection VRAC;

    private final static double MIN_PROB_CONVERGENCE = 2E-2;

    /////////////////////////////
    // Public Methods to interface with the Engine
    /////////////////////////////

    public VariantRecalibratorEngine( final VariantRecalibratorArgumentCollection VRAC ) {
        this.VRAC = VRAC;
    }

    public GaussianMixtureModel generateModel( final List<VariantDatum> data ) {
        final GaussianMixtureModel model = new GaussianMixtureModel( VRAC.MAX_GAUSSIANS, data.get(0).annotations.length, VRAC.SHRINKAGE, VRAC.DIRICHLET_PARAMETER, VRAC.PRIOR_COUNTS );
        variationalBayesExpectationMaximization( model, data );
        return model;
    }

    public void evaluateData( final List<VariantDatum> data, final GaussianMixtureModel model, final boolean evaluateContrastively ) {
        if( !model.isModelReadyForEvaluation ) {
            model.precomputeDenominatorForEvaluation();
        }
        
        logger.info("Evaluating full set of " + data.size() + " variants...");
        for( final VariantDatum datum : data ) {
            final double thisLod = evaluateDatum( datum, model );
            if( Double.isNaN(thisLod) ) {
                if( evaluateContrastively ) {
                    throw new UserException("NaN LOD value assigned. Clustering with this few variants and these annotations is unsafe. Please consider raising the number of variants used to train the negative model (via --percentBadVariants 0.05, for example) or lowering the maximum number of Gaussians to use in the model (via --maxGaussians 4, for example)");
                } else {
                    throw new UserException("NaN LOD value assigned. Clustering with this few variants and these annotations is unsafe.");
                }
            }
            datum.lod = ( evaluateContrastively ? (datum.prior + datum.lod - thisLod) : thisLod );
        }
    }

    /////////////////////////////
    // Private Methods used for generating a GaussianMixtureModel
    /////////////////////////////

    private void variationalBayesExpectationMaximization( final GaussianMixtureModel model, final List<VariantDatum> data ) {

        model.initializeRandomModel( data, VRAC.NUM_KMEANS_ITERATIONS );

        // The VBEM loop
        model.normalizePMixtureLog10();
        model.expectationStep( data );
        double currentChangeInMixtureCoefficients;
        int iteration = 0;
        logger.info("Finished iteration " + iteration + ".");
        while( iteration < VRAC.MAX_ITERATIONS ) {
            iteration++;
            model.maximizationStep( data );
            currentChangeInMixtureCoefficients = model.normalizePMixtureLog10();
            model.expectationStep(data);
            if( iteration % 5 == 0 ) { // cut down on the number of output lines so that users can read the warning messages
                logger.info("Finished iteration " + iteration + ". \tCurrent change in mixture coefficients = " + String.format("%.5f", currentChangeInMixtureCoefficients));
            }
            if( iteration > 2 && currentChangeInMixtureCoefficients < MIN_PROB_CONVERGENCE ) {
                logger.info("Convergence after " + iteration + " iterations!");
                break;
            }
        }

        model.evaluateFinalModelParameters( data );
    }

    /////////////////////////////
    // Private Methods used for evaluating data given a GaussianMixtureModel
    /////////////////////////////

    private double evaluateDatum( final VariantDatum datum, final GaussianMixtureModel model ) {
        return model.evaluateDatum( datum );
    }
}
