package gov.nih.nci.ncicb.tcgaportal.level4.web.request;


import gov.nih.nci.ncicb.tcgaportal.level4.gwtEnabled.domainobjects.FilterSpecifier;

/**
 * Description :   Request object used by ExternalRequest Controller
 *
 * @author Namrata Rane
 *         Last updated by: $Author$
 * @version $Rev$
 */
public class ExternalRequest {

    private String mode;
    private String disease;

    public ExternalRequest(){
        
    }
    
    public ExternalRequest(String mode, String disease){
        this.mode = mode;
        this.disease = disease;        
    }
    
    public FilterSpecifier.ListBy getListBy(String listByParam) {
        for (FilterSpecifier.ListBy listBy : FilterSpecifier.ListBy.values()) {
            if ((listBy.getStringValue()).equalsIgnoreCase(listByParam)){
                return listBy;                
            }
        }
        return null;
    }

    public String getMode() {
        return mode;
    }

    public void setMode(String mode) {
        this.mode = mode;
    }

    public String getDisease() {
        return disease;
    }

    public void setDisease(String disease) {
        this.disease = disease;
    }

    public void validate() throws IllegalArgumentException {
        if (mode == null || disease == null) {
            throw new IllegalArgumentException("Mode Or Disease was not specified. " +
                    "Please refer documentation for details.");
        }

        // check if the mode value is correct
        if (getListBy(mode) == null){
            throw new IllegalArgumentException("Mode parameter value " + mode + " is not correct. " +
                    "Valid parameter values are : genes, patients, pathways");
        }

    }

}
