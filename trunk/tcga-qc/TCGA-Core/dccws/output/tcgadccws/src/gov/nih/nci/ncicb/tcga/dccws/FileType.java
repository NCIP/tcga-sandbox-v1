package gov.nih.nci.ncicb.tcga.dccws;

import java.util.Collection;

import java.io.Serializable;
/**
	* 
	**/

public class FileType  implements Serializable
{
	/**
	* An attribute to allow serialization of the domain objects
	*/
	private static final long serialVersionUID = 1234567890L;

	
	/**
	* 
	**/
	
	private String description;
	/**
	* Retrieves the value of the description attribute
	* @return description
	**/

	public String getDescription(){
		return description;
	}

	/**
	* Sets the value of description attribute
	**/

	public void setDescription(String description){
		this.description = description;
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
	* 
	**/
	
	private String suffix;
	/**
	* Retrieves the value of the suffix attribute
	* @return suffix
	**/

	public String getSuffix(){
		return suffix;
	}

	/**
	* Sets the value of suffix attribute
	**/

	public void setSuffix(String suffix){
		this.suffix = suffix;
	}
	
	/**
	* An associated gov.nih.nci.ncicb.tcga.dccws.File object's collection 
	**/
			
	private Collection<File> fileCollection;
	/**
	* Retrieves the value of the fileCollection attribute
	* @return fileCollection
	**/

	public Collection<File> getFileCollection(){
		return fileCollection;
	}

	/**
	* Sets the value of fileCollection attribute
	**/

	public void setFileCollection(Collection<File> fileCollection){
		this.fileCollection = fileCollection;
	}
		
	/**
	* Compares <code>obj</code> to it self and returns true if they both are same
	*
	* @param obj
	**/
	public boolean equals(Object obj)
	{
		if(obj instanceof FileType) 
		{
			FileType c =(FileType)obj; 			 
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