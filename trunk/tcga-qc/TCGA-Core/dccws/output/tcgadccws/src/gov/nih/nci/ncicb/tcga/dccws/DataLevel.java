package gov.nih.nci.ncicb.tcga.dccws;

import java.util.Collection;

import java.io.Serializable;
/**
	* 
	**/

public class DataLevel  implements Serializable
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
	* An associated gov.nih.nci.ncicb.tcga.dccws.ArchiveType object's collection 
	**/
			
	private Collection<ArchiveType> archiveTypeCollection;
	/**
	* Retrieves the value of the archiveTypeCollection attribute
	* @return archiveTypeCollection
	**/

	public Collection<ArchiveType> getArchiveTypeCollection(){
		return archiveTypeCollection;
	}

	/**
	* Sets the value of archiveTypeCollection attribute
	**/

	public void setArchiveTypeCollection(Collection<ArchiveType> archiveTypeCollection){
		this.archiveTypeCollection = archiveTypeCollection;
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
		if(obj instanceof DataLevel) 
		{
			DataLevel c =(DataLevel)obj; 			 
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