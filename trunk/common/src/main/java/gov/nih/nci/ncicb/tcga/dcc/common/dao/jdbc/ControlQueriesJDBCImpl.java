package gov.nih.nci.ncicb.tcga.dcc.common.dao.jdbc;

import gov.nih.nci.ncicb.tcga.dcc.common.dao.ControlQueries;
import gov.nih.nci.ncicb.tcga.dcc.common.dao.TumorQueries;
import gov.nih.nci.ncicb.tcga.dcc.common.jaxb.generated.Control;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.simple.SimpleJdbcDaoSupport;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * Jdbc implementation of the control queries interface
 *
 * @author bertondl
 *         Last updated by: $Author$
 * @version $Rev$
 */

@Repository
@Transactional
public class ControlQueriesJDBCImpl extends SimpleJdbcDaoSupport implements ControlQueries {

    protected final Log logger = LogFactory.getLog(ControlQueriesJDBCImpl.class);

    @Autowired
    private TumorQueries tumorQueries;

    private static final String GET_CONTROL_TYPE_ID = "select control_type_id from control_type where xml_name = ?";

    private static final String GET_CONTROL_ID = "select shipped_biospecimen_id from shipped_biospecimen where uuid = lower(?)";

    private static final String UPDATE_IS_CONTROL = "update shipped_biospecimen set is_control=1 where uuid = lower(?)";

    private static final String MERGE_CONTROL = "merge into control using dual on (control_id=? and control_type_id=?) " +
            "when not matched then insert (control_id, control_type_id) values (?,?)";

    private static final String MERGE_CONTROL_TO_DISEASE = "merge into control_to_disease using dual on (control_id=? and disease_id=?) " +
            "when not matched then insert (control_id, disease_id) values (?,?)";

    @Override
    public void persistControl(final Control control) {
        String controlTypeXmlName = null;
        String uuid = null;
        List<Integer> diseaseList = null;
        if (control.getControlElement() != null) {
            controlTypeXmlName = control.getControlElement().value();
        }
        if (control.getAliquotsToDiseases() != null &&
                control.getAliquotsToDiseases().getBcrAliquotUuid() != null) {
            uuid = control.getAliquotsToDiseases().getBcrAliquotUuid().getValue();
            diseaseList = getDiseaseIdList(control.getAliquotsToDiseases().getDiseaseCodeList());
        }
        final Long controlTypeId = getControlTypeId(controlTypeXmlName);
        final Long controlId = getControlId(uuid);
        getSimpleJdbcTemplate().update(MERGE_CONTROL, new Object[]{controlId, controlTypeId, controlId, controlTypeId});
        addControlToDisease(controlId, diseaseList);
    }

    @Override
    public void updateControlForShippedBiospecimen(final Control control) {
        String uuid = null;
        if (control.getAliquotsToDiseases() != null &&
                control.getAliquotsToDiseases().getBcrAliquotUuid() != null) {
            uuid = control.getAliquotsToDiseases().getBcrAliquotUuid().getValue();
        }
        getSimpleJdbcTemplate().update(UPDATE_IS_CONTROL, new Object[]{uuid});
    }

    @Override
    public List<Integer> getDiseaseIdList(final List<String> diseaseCodeTypeList) {
        List<Integer> resList = new LinkedList<Integer>();
        if (diseaseCodeTypeList != null) {
            for (final String diseaseCodeType : diseaseCodeTypeList) {
                if (diseaseCodeType != null) {
                    resList.add(tumorQueries.getTumorIdByName(diseaseCodeType));
                }
            }
        }
        return resList;
    }

    @Override
    public void addControlToDisease(final Long controlId, final List<Integer> diseaseList) {
        final List<Object[]> batchParams = new ArrayList<Object[]>();
        for (final Integer diseaseId : diseaseList) {
            batchParams.add(new Object[]{controlId, diseaseId, controlId, diseaseId});
        }
        getSimpleJdbcTemplate().batchUpdate(MERGE_CONTROL_TO_DISEASE, batchParams);
    }

    @Override
    public Long getControlId(final String uuid) {
        try {
            return getSimpleJdbcTemplate().queryForLong(GET_CONTROL_ID, new Object[]{uuid == null ? "" : uuid});
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    @Override
    public Long getControlTypeId(final String xmlName) {
        try {
            return getSimpleJdbcTemplate().queryForLong(GET_CONTROL_TYPE_ID, new Object[]{xmlName == null ? "" : xmlName});
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    public void setTumorQueries(TumorQueries tumorQueries) {
        this.tumorQueries = tumorQueries;
    }
}//End of Class
