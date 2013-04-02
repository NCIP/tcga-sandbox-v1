package gov.nih.nci.ncicb.tcga.dccws;

import java.util.Collection;

import java.io.Serializable;
/**
	* 
	**/

public class Center  implements Serializable
{
	/**
	* An attribute to allow serialization of the domain objects
	*/
	private static final long serialVersionUID = 1234567890L;

	
	/**
	* 
	**/
	
	private String displayName;
	/**
	* Retrieves the value of the displayName attribute
	* @return displayName
	**/

	public String getDisplayName(){
		return displayName;
	}

	/**
	* Sets the value of displayName attribute
	**/

	public void setDisplayName(String displayName){
		this.displayName = displayName;
	}
	
	/**
	* 
	**/
	
	private Integer id;
	/**
	* Retrieves the value of the id attribute
	* @return id
	**/

	public Integer getId(){
		return id;
	}

	/**
	* Sets the value of id attribute
	**/

	public void setId(Integer id){
		this.id = id;
	}
	
	/**
	* 
	**/
	
	private String name;
	/**
	* Retrieves the value of the name attribute
	* @return name
	**/

	public String getName(){
		return name;
	}

	/**
	* Sets the value of name attribute
	**/

	public void setName(String name){
		this.name = name;
	}
	
	/**
	* An associated gov.nih.nci.ncicb.tcga.dccws.Archive object's collection 
	**/
			
	private Collection<Archive> archiveCollection;
	/**
	* Retrieves the value of the archiveCollection attribute
	* @return archiveCollection
	**/

	public Collection<Archive> getArchiveCollection(){
		return archiveCollection;
	}

	/**
	* Sets the value of archiveCollection attribute
	**/

	public void setArchiveCollection(Collection<Archive> archiveCollection){
		this.archiveCollection = archiveCollection;
	}
		
	/**
	* An associated gov.nih.nci.ncicb.tcga.dccws.BcrCenter object's collection 
	**/
			
	private Collection<BcrCenter> bcrCenterCollection;
	/**
	* Retrieves the value of the bcrCenterCollection attribute
	* @return bcrCenterCollection
	**/

	public Collection<BcrCenter> getBcrCenterCollection(){
		return bcrCenterCollection;
	}

	/**
	* Sets the value of bcrCenterCollection attribute
	**/

	public void setBcrCenterCollection(Collection<BcrCenter> bcrCenterCollection){
		this.bcrCenterCollection = bcrCenterCollection;
	}
		
	/**
	* An associated gov.nih.nci.ncicb.tcga.dccws.TissueSourceSite object's collection 
	**/
			
	private Collection<TissueSourceSite> bcrTssCollection;
	/**
	* Retrieves the value of the bcrTssCollection attribute
	* @return bcrTssCollection
	**/

	public Collection<TissueSourceSite> getBcrTssCollection(){
		return bcrTssCollection;
	}

	/**
	* Sets the value of bcrTssCollection attribute
	**/

	public void setBcrTssCollection(Collection<TissueSourceSite> bcrTssCollection){
		this.bcrTssCollection = bcrTssCollection;
	}
		
	/**
	* An associated gov.nih.nci.ncicb.tcga.dccws.CenterType object
	**/
			
	private CenterType centerType;
	/**
	* Retrieves the value of the centerType attribute
	* @return centerType
	**/
	
	public CenterType getCenterType(){
		return centerType;
	}
	/**
	* Sets the value of centerType attribute
	**/

	public void setCenterType(CenterType centerType){
		this.centerType = centerType;
	}
			
	/**
	* Compares <code>obj</code> to it self and returns true if they both are same
	*
	* @param obj
	**/
	public boolean equals(Object obj)
	{
		if(obj instanceof Center) 
		{
			Center c =(Center)obj; 			 
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