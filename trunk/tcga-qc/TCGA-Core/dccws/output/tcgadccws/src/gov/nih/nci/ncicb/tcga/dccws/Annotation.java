package gov.nih.nci.ncicb.tcga.dccws;


import java.io.Serializable;
/**
	* 
	**/

public class Annotation  implements Serializable
{
	/**
	* An attribute to allow serialization of the domain objects
	*/
	private static final long serialVersionUID = 1234567890L;

	
	/**
	* 
	**/
	
	private String columnName;
	/**
	* Retrieves the value of the columnName attribute
	* @return columnName
	**/

	public String getColumnName(){
		return columnName;
	}

	/**
	* Sets the value of columnName attribute
	**/

	public void setColumnName(String columnName){
		this.columnName = columnName;
	}
	
	/**
	* 
	**/
	
	private java.util.Date enteredDate;
	/**
	* Retrieves the value of the enteredDate attribute
	* @return enteredDate
	**/

	public java.util.Date getEnteredDate(){
		return enteredDate;
	}

	/**
	* Sets the value of enteredDate attribute
	**/

	public void setEnteredDate(java.util.Date enteredDate){
		this.enteredDate = enteredDate;
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
	
	private java.util.Date modifiedDate;
	/**
	* Retrieves the value of the modifiedDate attribute
	* @return modifiedDate
	**/

	public java.util.Date getModifiedDate(){
		return modifiedDate;
	}

	/**
	* Sets the value of modifiedDate attribute
	**/

	public void setModifiedDate(java.util.Date modifiedDate){
		this.modifiedDate = modifiedDate;
	}
	
	/**
	* 
	**/
	
	private String note;
	/**
	* Retrieves the value of the note attribute
	* @return note
	**/

	public String getNote(){
		return note;
	}

	/**
	* Sets the value of note attribute
	**/

	public void setNote(String note){
		this.note = note;
	}
	
	/**
	* 
	**/
	
	private String noteProvider;
	/**
	* Retrieves the value of the noteProvider attribute
	* @return noteProvider
	**/

	public String getNoteProvider(){
		return noteProvider;
	}

	/**
	* Sets the value of noteProvider attribute
	**/

	public void setNoteProvider(String noteProvider){
		this.noteProvider = noteProvider;
	}
	
	/**
	* 
	**/
	
	private Integer recordId;
	/**
	* Retrieves the value of the recordId attribute
	* @return recordId
	**/

	public Integer getRecordId(){
		return recordId;
	}

	/**
	* Sets the value of recordId attribute
	**/

	public void setRecordId(Integer recordId){
		this.recordId = recordId;
	}
	
	/**
	* 
	**/
	
	private String subject;
	/**
	* Retrieves the value of the subject attribute
	* @return subject
	**/

	public String getSubject(){
		return subject;
	}

	/**
	* Sets the value of subject attribute
	**/

	public void setSubject(String subject){
		this.subject = subject;
	}
	
	/**
	* 
	**/
	
	private String tableName;
	/**
	* Retrieves the value of the tableName attribute
	* @return tableName
	**/

	public String getTableName(){
		return tableName;
	}

	/**
	* Sets the value of tableName attribute
	**/

	public void setTableName(String tableName){
		this.tableName = tableName;
	}
	
	/**
	* Compares <code>obj</code> to it self and returns true if they both are same
	*
	* @param obj
	**/
	public boolean equals(Object obj)
	{
		if(obj instanceof Annotation) 
		{
			Annotation c =(Annotation)obj; 			 
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