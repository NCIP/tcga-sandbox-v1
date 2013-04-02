package gov.nih.nci.ncicb.tcga.dcc.common.webservice.bean;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Bean to store validation result
 *
 * @author Rohini Raman
 *         Last updated by: $Author$
 * @version $Rev$
 */
@XmlRootElement(name="validationObject")
@XmlAccessorType(XmlAccessType.FIELD)
public class ValidationResult {

    private String validationObject;

    private String createdOn;

    private Boolean existsInDB;

    private ValidationErrors  validationErrors;

    public ValidationResult(){
        existsInDB = false;
    }

    public String getCreatedOn() {
        return createdOn;
    }

    public void setCreatedOn(String createdOn) {
        this.createdOn = createdOn;
    }

    public Boolean getExistsInDB() {
        return existsInDB;
    }

    public Boolean existsInDB() {
        return existsInDB;
    }

    public void setExistsInDB(Boolean existsInDB) {
        this.existsInDB = existsInDB;
    }


    public String getValidationObject() {
        return validationObject;
    }

    public void setValidationObject(String validationObject) {
        this.validationObject = validationObject;
    }

    public ValidationErrors getValidationError() {
        return validationErrors;
    }

    public void setValidationError(ValidationErrors validationErrors) {
        this.validationErrors = validationErrors;
    }
}

