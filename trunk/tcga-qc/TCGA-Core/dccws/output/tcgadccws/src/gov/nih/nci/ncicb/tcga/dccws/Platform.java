package gov.nih.nci.ncicb.tcga.dccws;

import java.util.Collection;

import java.io.Serializable;
/**
	* 
	**/

public class Platform  implements Serializable
{
	/**
	* An attribute to allow serialization of the domain objects
	*/
	private static final long serialVersionUID = 1234567890L;

	
	/**
	* 
	**/
	
	private String alias;
	/**
	* Retrieves the value of the alias attribute
	* @return alias
	**/

	public String getAlias(){
		return alias;
	}

	/**
	* Sets the value of alias attribute
	**/

	public void setAlias(String alias){
		this.alias = alias;
	}
	
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
	* An associated gov.nih.nci.ncicb.tcga.dccws.Datatype object
	**/
			
	private Datatype baseDataType;
	/**
	* Retrieves the value of the baseDataType attribute
	* @return baseDataType
	**/
	
	public Datatype getBaseDataType(){
		return baseDataType;
	}
	/**
	* Sets the value of baseDataType attribute
	**/

	public void setBaseDataType(Datatype baseDataType){
		this.baseDataType = baseDataType;
	}
			
	/**
	* An associated gov.nih.nci.ncicb.tcga.dccws.Datatype object's collection 
	**/
			
	private Collection<Datatype> datatypeCollection;
	/**
	* Retrieves the value of the datatypeCollection attribute
	* @return datatypeCollection
	**/

	public Collection<Datatype> getDatatypeCollection(){
		return datatypeCollection;
	}

	/**
	* Sets the value of datatypeCollection attribute
	**/

	public void setDatatypeCollection(Collection<Datatype> datatypeCollection){
		this.datatypeCollection = datatypeCollection;
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
		if(obj instanceof Platform) 
		{
			Platform c =(Platform)obj; 			 
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