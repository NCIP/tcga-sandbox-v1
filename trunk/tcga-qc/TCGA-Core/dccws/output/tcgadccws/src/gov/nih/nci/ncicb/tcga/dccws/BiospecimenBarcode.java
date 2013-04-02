package gov.nih.nci.ncicb.tcga.dccws;

import java.util.Collection;

import java.io.Serializable;
/**
	* 
	**/

public class BiospecimenBarcode  implements Serializable
{
	/**
	* An attribute to allow serialization of the domain objects
	*/
	private static final long serialVersionUID = 1234567890L;

	
	/**
	* 
	**/
	
	private String barcode;
	/**
	* Retrieves the value of the barcode attribute
	* @return barcode
	**/

	public String getBarcode(){
		return barcode;
	}

	/**
	* Sets the value of barcode attribute
	**/

	public void setBarcode(String barcode){
		this.barcode = barcode;
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
	
	private Boolean isValid;
	/**
	* Retrieves the value of the isValid attribute
	* @return isValid
	**/

	public Boolean getIsValid(){
		return isValid;
	}

	/**
	* Sets the value of isValid attribute
	**/

	public void setIsValid(Boolean isValid){
		this.isValid = isValid;
	}
	
	/**
	* 
	**/
	
	private String patient;
	/**
	* Retrieves the value of the patient attribute
	* @return patient
	**/

	public String getPatient(){
		return patient;
	}

	/**
	* Sets the value of patient attribute
	**/

	public void setPatient(String patient){
		this.patient = patient;
	}
	
	/**
	* 
	**/
	
	private String plateId;
	/**
	* Retrieves the value of the plateId attribute
	* @return plateId
	**/

	public String getPlateId(){
		return plateId;
	}

	/**
	* Sets the value of plateId attribute
	**/

	public void setPlateId(String plateId){
		this.plateId = plateId;
	}
	
	/**
	* 
	**/
	
	private String portionSequence;
	/**
	* Retrieves the value of the portionSequence attribute
	* @return portionSequence
	**/

	public String getPortionSequence(){
		return portionSequence;
	}

	/**
	* Sets the value of portionSequence attribute
	**/

	public void setPortionSequence(String portionSequence){
		this.portionSequence = portionSequence;
	}
	
	/**
	* 
	**/
	
	private String sampleSequence;
	/**
	* Retrieves the value of the sampleSequence attribute
	* @return sampleSequence
	**/

	public String getSampleSequence(){
		return sampleSequence;
	}

	/**
	* Sets the value of sampleSequence attribute
	**/

	public void setSampleSequence(String sampleSequence){
		this.sampleSequence = sampleSequence;
	}
	
	/**
	* 
	**/
	
	private java.util.Date shipDate;
	/**
	* Retrieves the value of the shipDate attribute
	* @return shipDate
	**/

	public java.util.Date getShipDate(){
		return shipDate;
	}

	/**
	* Sets the value of shipDate attribute
	**/

	public void setShipDate(java.util.Date shipDate){
		this.shipDate = shipDate;
	}
	
	/**
	* An associated gov.nih.nci.ncicb.tcga.dccws.BcrCenter object
	**/
			
	private BcrCenter bcrCenter;
	/**
	* Retrieves the value of the bcrCenter attribute
	* @return bcrCenter
	**/
	
	public BcrCenter getBcrCenter(){
		return bcrCenter;
	}
	/**
	* Sets the value of bcrCenter attribute
	**/

	public void setBcrCenter(BcrCenter bcrCenter){
		this.bcrCenter = bcrCenter;
	}
			
	/**
	* An associated gov.nih.nci.ncicb.tcga.dccws.TissueSourceSite object
	**/
			
	private TissueSourceSite tissueSourceSite;
	/**
	* Retrieves the value of the tissueSourceSite attribute
	* @return tissueSourceSite
	**/
	
	public TissueSourceSite getTissueSourceSite(){
		return tissueSourceSite;
	}
	/**
	* Sets the value of tissueSourceSite attribute
	**/

	public void setTissueSourceSite(TissueSourceSite tissueSourceSite){
		this.tissueSourceSite = tissueSourceSite;
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
	* An associated gov.nih.nci.ncicb.tcga.dccws.Archive object's collection 
	**/
			
	private Collection<Archive> bcrArchiveCollection;
	/**
	* Retrieves the value of the bcrArchiveCollection attribute
	* @return bcrArchiveCollection
	**/

	public Collection<Archive> getBcrArchiveCollection(){
		return bcrArchiveCollection;
	}

	/**
	* Sets the value of bcrArchiveCollection attribute
	**/

	public void setBcrArchiveCollection(Collection<Archive> bcrArchiveCollection){
		this.bcrArchiveCollection = bcrArchiveCollection;
	}
		
	/**
	* An associated gov.nih.nci.ncicb.tcga.dccws.Project object
	**/
			
	private Project project;
	/**
	* Retrieves the value of the project attribute
	* @return project
	**/
	
	public Project getProject(){
		return project;
	}
	/**
	* Sets the value of project attribute
	**/

	public void setProject(Project project){
		this.project = project;
	}
			
	/**
	* An associated gov.nih.nci.ncicb.tcga.dccws.SampleType object
	**/
			
	private SampleType sampleType;
	/**
	* Retrieves the value of the sampleType attribute
	* @return sampleType
	**/
	
	public SampleType getSampleType(){
		return sampleType;
	}
	/**
	* Sets the value of sampleType attribute
	**/

	public void setSampleType(SampleType sampleType){
		this.sampleType = sampleType;
	}
			
	/**
	* An associated gov.nih.nci.ncicb.tcga.dccws.PortionAnalyte object
	**/
			
	private PortionAnalyte portionAnalyte;
	/**
	* Retrieves the value of the portionAnalyte attribute
	* @return portionAnalyte
	**/
	
	public PortionAnalyte getPortionAnalyte(){
		return portionAnalyte;
	}
	/**
	* Sets the value of portionAnalyte attribute
	**/

	public void setPortionAnalyte(PortionAnalyte portionAnalyte){
		this.portionAnalyte = portionAnalyte;
	}
			
	/**
	* Compares <code>obj</code> to it self and returns true if they both are same
	*
	* @param obj
	**/
	public boolean equals(Object obj)
	{
		if(obj instanceof BiospecimenBarcode) 
		{
			BiospecimenBarcode c =(BiospecimenBarcode)obj; 			 
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