package gov.nih.nci.ncicb.tcga.dccws;

import java.util.Collection;

import java.io.Serializable;
/**
	* 
	**/

public class Visibility  implements Serializable
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
	
	private Boolean isProtected;
	/**
	* Retrieves the value of the isProtected attribute
	* @return isProtected
	**/

	public Boolean getIsProtected(){
		return isProtected;
	}

	/**
	* Sets the value of isProtected attribute
	**/

	public void setIsProtected(Boolean isProtected){
		this.isProtected = isProtected;
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
	* Compares <code>obj</code> to it self and returns true if they both are same
	*
	* @param obj
	**/
	public boolean equals(Object obj)
	{
		if(obj instanceof Visibility) 
		{
			Visibility c =(Visibility)obj; 			 
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