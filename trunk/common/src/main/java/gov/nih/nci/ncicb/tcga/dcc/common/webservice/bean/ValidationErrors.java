package gov.nih.nci.ncicb.tcga.dcc.common.webservice.bean;

import javax.validation.ConstraintViolation;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Bean to store validation error details
 *
 * @author Rohini Raman
 *         Last updated by: $Author$
 * @version $Rev$
 */
@XmlRootElement(name="validationErrors")
@XmlAccessorType(XmlAccessType.FIELD)
public class ValidationErrors<T> {

	List<ValidationError> validationError;

	public ValidationErrors(){}

	public ValidationErrors(List<ValidationError> validationErrors) {
		this.validationError = validationErrors;
	}

	public ValidationErrors(Set<ConstraintViolation<T>> constraintViolations) {
		if(constraintViolations != null && constraintViolations.size() > 0) {
			validationError = new ArrayList<ValidationError>();
			for(ConstraintViolation<T> constraintViolation : constraintViolations) {
				validationError.add(new ValidationError(constraintViolation));
			}
		}
	}

	public ValidationErrors(Map<String, String> constraintViolations) {
		if(constraintViolations != null && constraintViolations.size() > 0) {
			validationError = new ArrayList<ValidationError>();
			for(String valueKey : constraintViolations.keySet()) {
				validationError.add(new ValidationError(valueKey, constraintViolations.get(valueKey)));
			}
		}
	}

	/**
	 * @return the validationErrors
	 */
	public List<ValidationError> getValidationError() {
		return validationError;
	}

	/**
	 * @param validationErrors the validationErrors to set
	 */
	public void setValidationErrors(List<ValidationError> validationErrors) {
		this.validationError = validationErrors;
	}

	@XmlRootElement(name="validationError")
	@XmlAccessorType(XmlAccessType.FIELD)
	static public class ValidationError {

		String invalidValue;
		String errorMessage;

		public ValidationError(){}

		public ValidationError(String invalidValue, String errorMessage) {
			this.invalidValue = invalidValue;
			this.errorMessage = errorMessage;
		}

		public ValidationError(ConstraintViolation<?> constraintViolation) {
			Object value = constraintViolation.getInvalidValue();
			this.invalidValue = value == null ? "" : value.toString();
			this.errorMessage = constraintViolation.getMessage();
		}

		/**
		 * @return the invalidValue
		 */
		public String getInvalidValue() {
			return invalidValue;
		}

		/**
		 * @param invalidValue the invalidValue to set
		 */
		public void setInvalidValue(String invalidValue) {
			this.invalidValue = invalidValue;
		}

		/**
		 * @return the errorMessage
		 */
		public String getErrorMessage() {
			return errorMessage;
		}

		/**
		 * @param errorMessage the errorMessage to set
		 */
		public void setErrorMessage(String errorMessage) {
			this.errorMessage = errorMessage;
		}
	}
}


