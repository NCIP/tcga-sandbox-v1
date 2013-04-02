package gov.nih.nci.ncicb.tcga.dcc.common.bean.validation.validators;

import gov.nih.nci.ncicb.tcga.dcc.common.bean.validation.BeanPropertyValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author bertondl
 *         Last updated by: $Author$
 * @version $Rev$
 */

@Component
public class DispatcherValidator {

    @Autowired
    private AnalyteTypeValidator analyteTypeValidator;

    @Autowired
    private BCRValidator bcrValidator;

    @Autowired
    private CenterValidator centerValidator;

    @Autowired
    private SampleTypeValidator sampleTypeValidator;

    public BeanPropertyValidator getInjectedValidator(final BeanPropertyValidator validator) {

        if (validator instanceof AnalyteTypeValidator) {
            return analyteTypeValidator;
        }
        if (validator instanceof BCRValidator) {
            return bcrValidator;
        }
        if (validator instanceof CenterValidator) {
            return centerValidator;
        }
        if (validator instanceof SampleTypeValidator) {
            return sampleTypeValidator;
        }
        return validator;
    }
}
