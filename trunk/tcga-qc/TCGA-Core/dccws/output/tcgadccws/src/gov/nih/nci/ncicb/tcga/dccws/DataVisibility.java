package gov.nih.nci.ncicb.tcga.dccws;


import java.io.Serializable;
/**
	* 
	**/

public class DataVisibility  implements Serializable
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
	* An associated gov.nih.nci.ncicb.tcga.dccws.Visibility object
	**/
			
	private Visibility visibility;
	/**
	* Retrieves the value of the visibility attribute
	* @return visibility
	**/
	
	public Visibility getVisibility(){
		return visibility;
	}
	/**
	* Sets the value of visibility attribute
	**/

	public void setVisibility(Visibility visibility){
		this.visibility = visibility;
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
	* An associated gov.nih.nci.ncicb.tcga.dccws.Datatype object
	**/
			
	private Datatype dataType;
	/**
	* Retrieves the value of the dataType attribute
	* @return dataType
	**/
	
	public Datatype getDataType(){
		return dataType;
	}
	/**
	* Sets the value of dataType attribute
	**/

	public void setDataType(Datatype dataType){
		this.dataType = dataType;
	}
			
	/**
	* Compares <code>obj</code> to it self and returns true if they both are same
	*
	* @param obj
	**/
	public boolean equals(Object obj)
	{
		if(obj instanceof DataVisibility) 
		{
			DataVisibility c =(DataVisibility)obj; 			 
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