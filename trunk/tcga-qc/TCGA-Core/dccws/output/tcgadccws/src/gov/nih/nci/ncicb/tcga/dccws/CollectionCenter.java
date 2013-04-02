package gov.nih.nci.ncicb.tcga.dccws;

import java.util.Collection;

import java.io.Serializable;
/**
	* 
	**/

public class CollectionCenter  implements Serializable
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
	* An associated gov.nih.nci.ncicb.tcga.dccws.Tissue object
	**/
			
	private Tissue tissue;
	/**
	* Retrieves the value of the tissue attribute
	* @return tissue
	**/
	
	public Tissue getTissue(){
		return tissue;
	}
	/**
	* Sets the value of tissue attribute
	**/

	public void setTissue(Tissue tissue){
		this.tissue = tissue;
	}
			
	/**
	* Compares <code>obj</code> to it self and returns true if they both are same
	*
	* @param obj
	**/
	public boolean equals(Object obj)
	{
		if(obj instanceof CollectionCenter) 
		{
			CollectionCenter c =(CollectionCenter)obj; 			 
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