package gov.nih.nci.ncicb.tcga.dcc.common.bean;


import javax.persistence.Cacheable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;


/**
 * Entity bean that maps to records in the <tt>PROTECTED_CLINICAL_ELEMENT</tt> table, and can be used for
 * CRUD operations.
 * 
 * @author nichollsmc
 */
@Entity
@Table(name = "PROTECTED_CLINICAL_ELEMENT")
@Cacheable
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class ProtectedClinicalElement {
	
	@Id
	@Column(name = "CLINICAL_ELEMENT_ID")
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "protected_clinical_element_seq")
	@SequenceGenerator(name = "protected_clinical_element_seq", sequenceName = "PROTECTED_CLINICAL_ELEMENT_SEQ")
    private Integer clinicalElementId;
	
	@Column(name = "ELEMENT_NAME")
	private String elementName;

	public Integer getClinicalElementId() {
		return clinicalElementId;
	}

	public void setClinicalElementId(final Integer clinicalElementId) {
		this.clinicalElementId = clinicalElementId;
	}

	public String getElementName() {
		return elementName;
	}

	public void setElementName(final String elementName) {
		this.elementName = elementName;
	}
	
}
