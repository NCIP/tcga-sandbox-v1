package gov.nih.nci.ncicb.tcga.dccws;

import java.util.Collection;

import java.io.Serializable;
/**
	* 
	**/

public class CenterType  implements Serializable
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
	* An associated gov.nih.nci.ncicb.tcga.dccws.Center object's collection 
	**/
			
	private Collection<Center> centerCollection;
	/**
	* Retrieves the value of the centerCollection attribute
	* @return centerCollection
	**/

	public Collection<Center> getCenterCollection(){
		return centerCollection;
	}

	/**
	* Sets the value of centerCollection attribute
	**/

	public void setCenterCollection(Collection<Center> centerCollection){
		this.centerCollection = centerCollection;
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
	* Compares <code>obj</code> to it self and returns true if they both are same
	*
	* @param obj
	**/
	public boolean equals(Object obj)
	{
		if(obj instanceof CenterType) 
		{
			CenterType c =(CenterType)obj; 			 
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