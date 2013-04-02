package gov.nih.nci.ncicb.tcga.dccws;

import java.util.Collection;

import java.io.Serializable;
/**
	* 
	**/

public class File  implements Serializable
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
	* 
	**/
	
	private String url;
	/**
	* Retrieves the value of the url attribute
	* @return url
	**/

	public String getUrl(){
		return url;
	}

	/**
	* Sets the value of url attribute
	**/

	public void setUrl(String url){
		this.url = url;
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
	* An associated gov.nih.nci.ncicb.tcga.dccws.Archive object
	**/
			
	private Archive archive;
	/**
	* Retrieves the value of the archive attribute
	* @return archive
	**/
	
	public Archive getArchive(){
		return archive;
	}
	/**
	* Sets the value of archive attribute
	**/

	public void setArchive(Archive archive){
		this.archive = archive;
	}
			
	/**
	* An associated gov.nih.nci.ncicb.tcga.dccws.FileType object
	**/
			
	private FileType fileType;
	/**
	* Retrieves the value of the fileType attribute
	* @return fileType
	**/
	
	public FileType getFileType(){
		return fileType;
	}
	/**
	* Sets the value of fileType attribute
	**/

	public void setFileType(FileType fileType){
		this.fileType = fileType;
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
	* Compares <code>obj</code> to it self and returns true if they both are same
	*
	* @param obj
	**/
	public boolean equals(Object obj)
	{
		if(obj instanceof File) 
		{
			File c =(File)obj; 			 
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