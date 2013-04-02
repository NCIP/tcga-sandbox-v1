package gov.nih.nci.ncicb.tcga.dccws;

import java.util.Collection;

import java.io.Serializable;
/**
	* 
	**/

public class BcrCenter  implements Serializable
{
	/**
	* An attribute to allow serialization of the domain objects
	*/
	private static final long serialVersionUID = 1234567890L;

	
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
			
	private Center center;
	/**
	* Retrieves the value of the center attribute
	* @return center
	**/
	
	public Center getCenter(){
		return center;
	}
	/**
	* Sets the value of center attribute
	**/

	public void setCenter(Center center){
		this.center = center;
	}
			
	/**
	* Compares <code>obj</code> to it self and returns true if they both are same
	*
	* @param obj
	**/
	public boolean equals(Object obj)
	{
		if(obj instanceof BcrCenter) 
		{
			BcrCenter c =(BcrCenter)obj; 			 
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