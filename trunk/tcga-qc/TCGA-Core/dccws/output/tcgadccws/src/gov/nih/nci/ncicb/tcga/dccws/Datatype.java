package gov.nih.nci.ncicb.tcga.dccws;

import java.util.Collection;

import java.io.Serializable;
/**
	* 
	**/

public class Datatype  implements Serializable
{
	/**
	* An attribute to allow serialization of the domain objects
	*/
	private static final long serialVersionUID = 1234567890L;

	
	/**
	* 
	**/
	
	private String centerType;
	/**
	* Retrieves the value of the centerType attribute
	* @return centerType
	**/

	public String getCenterType(){
		return centerType;
	}

	/**
	* Sets the value of centerType attribute
	**/

	public void setCenterType(String centerType){
		this.centerType = centerType;
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
	* An associated gov.nih.nci.ncicb.tcga.dccws.Platform object's collection 
	**/
			
	private Collection<Platform> basePlatformCollection;
	/**
	* Retrieves the value of the basePlatformCollection attribute
	* @return basePlatformCollection
	**/

	public Collection<Platform> getBasePlatformCollection(){
		return basePlatformCollection;
	}

	/**
	* Sets the value of basePlatformCollection attribute
	**/

	public void setBasePlatformCollection(Collection<Platform> basePlatformCollection){
		this.basePlatformCollection = basePlatformCollection;
	}
		
	/**
	* An associated gov.nih.nci.ncicb.tcga.dccws.DataVisibility object's collection 
	**/
			
	private Collection<DataVisibility> dataVisibilityCollection;
	/**
	* Retrieves the value of the dataVisibilityCollection attribute
	* @return dataVisibilityCollection
	**/

	public Collection<DataVisibility> getDataVisibilityCollection(){
		return dataVisibilityCollection;
	}

	/**
	* Sets the value of dataVisibilityCollection attribute
	**/

	public void setDataVisibilityCollection(Collection<DataVisibility> dataVisibilityCollection){
		this.dataVisibilityCollection = dataVisibilityCollection;
	}
		
	/**
	* An associated gov.nih.nci.ncicb.tcga.dccws.Platform object's collection 
	**/
			
	private Collection<Platform> platformCollection;
	/**
	* Retrieves the value of the platformCollection attribute
	* @return platformCollection
	**/

	public Collection<Platform> getPlatformCollection(){
		return platformCollection;
	}

	/**
	* Sets the value of platformCollection attribute
	**/

	public void setPlatformCollection(Collection<Platform> platformCollection){
		this.platformCollection = platformCollection;
	}
		
	/**
	* An associated gov.nih.nci.ncicb.tcga.dccws.FileInfo object's collection 
	**/
			
	private Collection<FileInfo> fileCollection;
	/**
	* Retrieves the value of the fileCollection attribute
	* @return fileCollection
	**/

	public Collection<FileInfo> getFileCollection(){
		return fileCollection;
	}

	/**
	* Sets the value of fileCollection attribute
	**/

	public void setFileCollection(Collection<FileInfo> fileCollection){
		this.fileCollection = fileCollection;
	}
		
	/**
	* Compares <code>obj</code> to it self and returns true if they both are same
	*
	* @param obj
	**/
	public boolean equals(Object obj)
	{
		if(obj instanceof Datatype) 
		{
			Datatype c =(Datatype)obj; 			 
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