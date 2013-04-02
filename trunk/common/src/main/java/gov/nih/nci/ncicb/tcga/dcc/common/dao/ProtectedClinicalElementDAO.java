package gov.nih.nci.ncicb.tcga.dcc.common.dao;

import java.util.List;

import gov.nih.nci.ncicb.tcga.dcc.common.bean.ProtectedClinicalElement;

/**
 * Data access object (DAO) interface for performing CRUD operations on protected clinical element
 * data sources.
 * 
 * @author nichollsmc
 */
public interface ProtectedClinicalElementDAO {
	
	/**
	 * Adds a single protected clinical element to a protected clinical element data source.
	 * 
	 * @param protectedClinicalElement - the {@link ProtectedClinicalElement} to add
	 * @return an integer representing the Id of the new element if it was added successfully, null otherwise
	 */
	public Integer addElement(ProtectedClinicalElement protectedClinicalElement);
	
	/**
	 * Retrieves a single protected clinical element from a protected clinical element data source using the
	 * provided element name.
	 * 
	 * @param elementName - a string representing the element name to retrieve
	 * @return an instance of {@link ProtectedClinicalElement} that corresponds to the provided element name, null if
	 * no record was found
	 */
	public ProtectedClinicalElement getElementByName(String elementName);
	
	/**
	 * Returns a boolean indicating whether or not the provided element name is protected or not for specific
	 * protected clinical element data source.
	 * 
	 * @param elementName - a string representing the element name to check
	 * @return true if the element is protected, false otherwise
	 */
	public boolean isProtected(String elementName);
	
	/**
	 * Retrieves all protected clinical element records from a protected clinical element data source.
	 * 
	 * @return a list of {@link ProtectedClinicalElement}s
	 */
    public List<ProtectedClinicalElement> getElements();
    
    /**
     * Removes a single protected clinical element from from a protected clinical element data source.
     * 
     * @param elementName - a string representing the element name to remove
     */
    public void removeElementByName(String elementName);
}
