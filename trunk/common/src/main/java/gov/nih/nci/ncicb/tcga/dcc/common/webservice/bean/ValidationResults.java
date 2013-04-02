package gov.nih.nci.ncicb.tcga.dcc.common.webservice.bean;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.List;

/**
 * Bean to store validation results
 *
 * @author Rohini Raman
 *         Last updated by: $Author$
 * @version $Rev$
 */
@XmlRootElement(name = "validationResults")
@XmlAccessorType(XmlAccessType.FIELD)
public class ValidationResults {
    private List<ValidationResult> validationResult;


    public List<ValidationResult> getValidationResult() {
        return validationResult;
    }

    public void setValidationResult(List<ValidationResult> validationResult) {
        this.validationResult = validationResult;
    }

    public void addValidationResult(List<ValidationResult> validationResult) {

        if(this.validationResult == null) {
            this.validationResult = validationResult;
        } else {
            if(validationResult != null) {
                this.validationResult.addAll(validationResult);
            }
        }
    }
}
