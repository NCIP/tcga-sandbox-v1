package gov.nih.nci.ncicb.tcga.dccws;

import java.util.Collection;

import java.io.Serializable;
/**
	* 
	**/

public class Disease  implements Serializable
{
	/**
	* An attribute to allow serialization of the domain objects
	*/
	private static final long serialVersionUID = 1234567890L;

	
	/**
	* 
	**/
	
	private String abbreviation;
	/**
	* Retrieves the value of the abbreviation attribute
	* @return abbreviation
	**/

	public String getAbbreviation(){
		return abbreviation;
	}

	/**
	* Sets the value of abbreviation attribute
	**/

	public void setAbbreviation(String abbreviation){
		this.abbreviation = abbreviation;
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
	* An associated gov.nih.nci.ncicb.tcga.dccws.Tissue object's collection 
	**/
			
	private Collection<Tissue> tissueCollection;
	/**
	* Retrieves the value of the tissueCollection attribute
	* @return tissueCollection
	**/

	public Collection<Tissue> getTissueCollection(){
		return tissueCollection;
	}

	/**
	* Sets the value of tissueCollection attribute
	**/

	public void setTissueCollection(Collection<Tissue> tissueCollection){
		this.tissueCollection = tissueCollection;
	}
		
	/**
	* An associated gov.nih.nci.ncicb.tcga.dccws.TissueSourceSite object's collection 
	**/
			
	private Collection<TissueSourceSite> tssCollection;
	/**
	* Retrieves the value of the tssCollection attribute
	* @return tssCollection
	**/

	public Collection<TissueSourceSite> getTssCollection(){
		return tssCollection;
	}

	/**
	* Sets the value of tssCollection attribute
	**/

	public void setTssCollection(Collection<TissueSourceSite> tssCollection){
		this.tssCollection = tssCollection;
	}
		
	/**
	* Compares <code>obj</code> to it self and returns true if they both are same
	*
	* @param obj
	**/
	public boolean equals(Object obj)
	{
		if(obj instanceof Disease) 
		{
			Disease c =(Disease)obj; 			 
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