package gov.nih.nci.ncicb.tcga.dccws;

import java.util.Collection;

import java.io.Serializable;
/**
	* 
	**/

public class Archive  implements Serializable
{
	/**
	* An attribute to allow serialization of the domain objects
	*/
	private static final long serialVersionUID = 1234567890L;

	
	/**
	* 
	**/
	
	private java.util.Date addedDate;
	/**
	* Retrieves the value of the addedDate attribute
	* @return addedDate
	**/

	public java.util.Date getAddedDate(){
		return addedDate;
	}

	/**
	* Sets the value of addedDate attribute
	**/

	public void setAddedDate(java.util.Date addedDate){
		this.addedDate = addedDate;
	}
	
	/**
	* 
	**/
	
	private String baseName;
	/**
	* Retrieves the value of the baseName attribute
	* @return baseName
	**/

	public String getBaseName(){
		return baseName;
	}

	/**
	* Sets the value of baseName attribute
	**/

	public void setBaseName(String baseName){
		this.baseName = baseName;
	}
	
	/**
	* 
	**/
	
	private String deployLocation;
	/**
	* Retrieves the value of the deployLocation attribute
	* @return deployLocation
	**/

	public String getDeployLocation(){
		return deployLocation;
	}

	/**
	* Sets the value of deployLocation attribute
	**/

	public void setDeployLocation(String deployLocation){
		this.deployLocation = deployLocation;
	}
	
	/**
	* 
	**/
	
	private String deployStatus;
	/**
	* Retrieves the value of the deployStatus attribute
	* @return deployStatus
	**/

	public String getDeployStatus(){
		return deployStatus;
	}

	/**
	* Sets the value of deployStatus attribute
	**/

	public void setDeployStatus(String deployStatus){
		this.deployStatus = deployStatus;
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
	
	private Integer isLatest;
	/**
	* Retrieves the value of the isLatest attribute
	* @return isLatest
	**/

	public Integer getIsLatest(){
		return isLatest;
	}

	/**
	* Sets the value of isLatest attribute
	**/

	public void setIsLatest(Integer isLatest){
		this.isLatest = isLatest;
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
	
	private Integer revision;
	/**
	* Retrieves the value of the revision attribute
	* @return revision
	**/

	public Integer getRevision(){
		return revision;
	}

	/**
	* Sets the value of revision attribute
	**/

	public void setRevision(Integer revision){
		this.revision = revision;
	}
	
	/**
	* 
	**/
	
	private Integer serialIndex;
	/**
	* Retrieves the value of the serialIndex attribute
	* @return serialIndex
	**/

	public Integer getSerialIndex(){
		return serialIndex;
	}

	/**
	* Sets the value of serialIndex attribute
	**/

	public void setSerialIndex(Integer serialIndex){
		this.serialIndex = serialIndex;
	}
	
	/**
	* An associated gov.nih.nci.ncicb.tcga.dccws.Disease object
	**/
			
	private Disease disease;
	/**
	* Retrieves the value of the disease attribute
	* @return disease
	**/
	
	public Disease getDisease(){
		return disease;
	}
	/**
	* Sets the value of disease attribute
	**/

	public void setDisease(Disease disease){
		this.disease = disease;
	}
			
	/**
	* An associated gov.nih.nci.ncicb.tcga.dccws.Platform object
	**/
			
	private Platform platform;
	/**
	* Retrieves the value of the platform attribute
	* @return platform
	**/
	
	public Platform getPlatform(){
		return platform;
	}
	/**
	* Sets the value of platform attribute
	**/

	public void setPlatform(Platform platform){
		this.platform = platform;
	}
			
	/**
	* An associated gov.nih.nci.ncicb.tcga.dccws.Center object
	**/
			
	private Center center;
	/**
	* Retrieves the value of the center attribute
	* @return center
	**/
	
	public Center getCenter(){
		return center;
	}
	/**
	* Sets the value of center attribute
	**/

	public void setCenter(Center center){
		this.center = center;
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
	* An associated gov.nih.nci.ncicb.tcga.dccws.ArchiveType object
	**/
			
	private ArchiveType archiveType;
	/**
	* Retrieves the value of the archiveType attribute
	* @return archiveType
	**/
	
	public ArchiveType getArchiveType(){
		return archiveType;
	}
	/**
	* Sets the value of archiveType attribute
	**/

	public void setArchiveType(ArchiveType archiveType){
		this.archiveType = archiveType;
	}
			
	/**
	* An associated gov.nih.nci.ncicb.tcga.dccws.BiospecimenBarcode object's collection 
	**/
			
	private Collection<BiospecimenBarcode> bcrBiospecimenBarcodeCollection;
	/**
	* Retrieves the value of the bcrBiospecimenBarcodeCollection attribute
	* @return bcrBiospecimenBarcodeCollection
	**/

	public Collection<BiospecimenBarcode> getBcrBiospecimenBarcodeCollection(){
		return bcrBiospecimenBarcodeCollection;
	}

	/**
	* Sets the value of bcrBiospecimenBarcodeCollection attribute
	**/

	public void setBcrBiospecimenBarcodeCollection(Collection<BiospecimenBarcode> bcrBiospecimenBarcodeCollection){
		this.bcrBiospecimenBarcodeCollection = bcrBiospecimenBarcodeCollection;
	}
		
	/**
	* Compares <code>obj</code> to it self and returns true if they both are same
	*
	* @param obj
	**/
	public boolean equals(Object obj)
	{
		if(obj instanceof Archive) 
		{
			Archive c =(Archive)obj; 			 
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