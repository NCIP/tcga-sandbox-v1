package gov.nih.nci.ncicb.tcga.dccws;

import java.util.Collection;

import java.io.Serializable;
/**
	* 
	**/

public class TissueSourceSite  implements Serializable
{
	/**
	* An attribute to allow serialization of the domain objects
	*/
	private static final long serialVersionUID = 1234567890L;

	
	/**
	* 
	**/
	
	private String definition;
	/**
	* Retrieves the value of the definition attribute
	* @return definition
	**/

	public String getDefinition(){
		return definition;
	}

	/**
	* Sets the value of definition attribute
	**/

	public void setDefinition(String definition){
		this.definition = definition;
	}
	
	/**
	* 
	**/
	
	private String id;
	/**
	* Retrieves the value of the id attribute
	* @return id
	**/

	public String getId(){
		return id;
	}

	/**
	* Sets the value of id attribute
	**/

	public void setId(String id){
		this.id = id;
	}
	
	/**
	* An associated gov.nih.nci.ncicb.tcga.dccws.BiospecimenBarcode object's collection 
	**/
			
	private Collection<BiospecimenBarcode> biospecimenBarcodeCollection;
	/**
	* Retrieves the value of the biospecimenBarcodeCollection attribute
	* @return biospecimenBarcodeCollection
	**/

	public Collection<BiospecimenBarcode> getBiospecimenBarcodeCollection(){
		return biospecimenBarcodeCollection;
	}

	/**
	* Sets the value of biospecimenBarcodeCollection attribute
	**/

	public void setBiospecimenBarcodeCollection(Collection<BiospecimenBarcode> biospecimenBarcodeCollection){
		this.biospecimenBarcodeCollection = biospecimenBarcodeCollection;
	}
		
	/**
	* An associated gov.nih.nci.ncicb.tcga.dccws.Center object
	**/
			
	private Center receivingCenter;
	/**
	* Retrieves the value of the receivingCenter attribute
	* @return receivingCenter
	**/
	
	public Center getReceivingCenter(){
		return receivingCenter;
	}
	/**
	* Sets the value of receivingCenter attribute
	**/

	public void setReceivingCenter(Center receivingCenter){
		this.receivingCenter = receivingCenter;
	}
			
	/**
	* An associated gov.nih.nci.ncicb.tcga.dccws.Disease object's collection 
	**/
			
	private Collection<Disease> diseaseCollection;
	/**
	* Retrieves the value of the diseaseCollection attribute
	* @return diseaseCollection
	**/

	public Collection<Disease> getDiseaseCollection(){
		return diseaseCollection;
	}

	/**
	* Sets the value of diseaseCollection attribute
	**/

	public void setDiseaseCollection(Collection<Disease> diseaseCollection){
		this.diseaseCollection = diseaseCollection;
	}
		
	/**
	* Compares <code>obj</code> to it self and returns true if they both are same
	*
	* @param obj
	**/
	public boolean equals(Object obj)
	{
		if(obj instanceof TissueSourceSite) 
		{
			TissueSourceSite c =(TissueSourceSite)obj; 			 
			if(getId() != null && getId().equals(c.getId()))
				return true;
		}
		return false;
	}
		
	/**
	* Returns hash code for the primary key of the object
	**/
	public int hashCode()
	{
		if(getId() != null)
			return getId().hashCode();
		return 0;
	}
	
}