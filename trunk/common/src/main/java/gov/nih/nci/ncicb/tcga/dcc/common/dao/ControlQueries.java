package gov.nih.nci.ncicb.tcga.dcc.common.dao;

import gov.nih.nci.ncicb.tcga.dcc.common.jaxb.generated.Control;

import java.util.List;

/**
 * Interface defining persistence of control metadata domain objects to the database
 *
 * @author bertondl
 *         Last updated by: $Author$
 * @version $Rev$
 */
public interface ControlQueries {

    /**
     * Persist control object to database
     *
     * @param control data structure with values to persist
     */
    public void persistControl(final Control control);


    /**
     * Update shippedBiospecimen control flag
     *
     * @param control data structure
     */
    public void updateControlForShippedBiospecimen(final Control control);

    public List<Integer> getDiseaseIdList(final List<String> diseaseCodeTypeList);

    public Long getControlId(final String uuid);

    public Long getControlTypeId(final String xmlName);

    public void addControlToDisease(final Long controlId, final List<Integer> diseaseList);

}
