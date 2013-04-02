package gov.nih.nci.ncicb.tcga.dccws;

import java.util.Collection;

import java.io.Serializable;
/**
	* 
	**/

public class FileInfo  implements Serializable
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
	
	private String md5sum;
	/**
	* Retrieves the value of the md5sum attribute
	* @return md5sum
	**/

	public String getMd5sum(){
		return md5sum;
	}

	/**
	* Sets the value of md5sum attribute
	**/

	public void setMd5sum(String md5sum){
		this.md5sum = md5sum;
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
	
	private Long size;
	/**
	* Retrieves the value of the size attribute
	* @return size
	**/

	public Long getSize(){
		return size;
	}

	/**
	* Sets the value of size attribute
	**/

	public void setSize(Long size){
		this.size = size;
	}
	
	/**
	* An associated gov.nih.nci.ncicb.tcga.dccws.ArchiveFile object's collection 
	**/
			
	private Collection<ArchiveFile> archiveFileCollection;
	/**
	* Retrieves the value of the archiveFileCollection attribute
	* @return archiveFileCollection
	**/

	public Collection<ArchiveFile> getArchiveFileCollection(){
		return archiveFileCollection;
	}

	/**
	* Sets the value of archiveFileCollection attribute
	**/

	public void setArchiveFileCollection(Collection<ArchiveFile> archiveFileCollection){
		this.archiveFileCollection = archiveFileCollection;
	}
		
	/**
	* An associated gov.nih.nci.ncicb.tcga.dccws.BiospecimenBarcode object's collection 
	**/
			
	private Collection<BiospecimenBarcode> biospecimenBarcodeCollection;
	/**
	* Retrieves the value of the biospecimenBarcodeCollection attribute
	* @return biospecimenBarcodeCollection
	**/

	public Collection<BiospecimenBarcode> getBiospecimenBarcodeCollection(){
		return biospecimenBarcodeCollection;
	}

	/**
	* Sets the value of biospecimenBarcodeCollection attribute
	**/

	public void setBiospecimenBarcodeCollection(Collection<BiospecimenBarcode> biospecimenBarcodeCollection){
		this.biospecimenBarcodeCollection = biospecimenBarcodeCollection;
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
		if(obj instanceof FileInfo) 
		{
			FileInfo c =(FileInfo)obj; 			 
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