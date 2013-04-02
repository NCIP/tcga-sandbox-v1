package gov.nih.nci.ncicb.tcga.dccws;

import java.util.Collection;

import java.io.Serializable;
/**
	* 
	**/

public class ArchiveType  implements Serializable
{
	/**
	* An attribute to allow serialization of the domain objects
	*/
	private static final long serialVersionUID = 1234567890L;

	
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
	
	private String type;
	/**
	* Retrieves the value of the type attribute
	* @return type
	**/

	public String getType(){
		return type;
	}

	/**
	* Sets the value of type attribute
	**/

	public void setType(String type){
		this.type = type;
	}
	
	/**
	* An associated gov.nih.nci.ncicb.tcga.dccws.DataLevel object
	**/
			
	private DataLevel dataLevel;
	/**
	* Retrieves the value of the dataLevel attribute
	* @return dataLevel
	**/
	
	public DataLevel getDataLevel(){
		return dataLevel;
	}
	/**
	* Sets the value of dataLevel attribute
	**/

	public void setDataLevel(DataLevel dataLevel){
		this.dataLevel = dataLevel;
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
	* Compares <code>obj</code> to it self and returns true if they both are same
	*
	* @param obj
	**/
	public boolean equals(Object obj)
	{
		if(obj instanceof ArchiveType) 
		{
			ArchiveType c =(ArchiveType)obj; 			 
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