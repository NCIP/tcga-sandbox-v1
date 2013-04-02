package gov.nih.nci.ncicb.tcga.dcc.common.dao.jdbc;

import gov.nih.nci.ncicb.tcga.dcc.common.bean.BatchNumberAssignment;
import gov.nih.nci.ncicb.tcga.dcc.common.dao.BaseQueriesProcessor;
import gov.nih.nci.ncicb.tcga.dcc.common.dao.BatchNumberQueries;
import org.springframework.jdbc.core.RowCallbackHandler;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * JDBC Implementation for BatchNumber queries
 *
 * @author Rohini Raman
 *         Last updated by: $Author$
 * @version $Rev$
 */
public class BatchNumberQueriesJDBCImpl extends BaseQueriesProcessor implements BatchNumberQueries {
    public static final String GET_BATCH_NUMBER_ASSIGNMENT_QUERY = " select batch_number_assignment.batch_id,disease.disease_abbreviation, center.domain_name  " +
            " from " +
            " batch_number_assignment, disease,center " +
            " where " +
            " batch_number_assignment.center_id = center.center_id " +
            " and batch_number_assignment.disease_id = disease.disease_id " +
            " and batch_number_assignment.batch_id= ? order by center.domain_name";

    public static final String IS_VALID_BATCH_ASSIGNMENT_QUERY = "select count(*) from batch_number_assignment bna, center c, disease d " +
            "where c.center_id= bna.center_id and d.disease_id= bna.disease_id " +
            "and bna.batch_id=? and c.domain_name=? and d.disease_abbreviation=?";


    public List<BatchNumberAssignment> getBatchNumberAssignment(final Integer batchId) {
        final List<BatchNumberAssignment> batchNumberAssignments = new ArrayList<BatchNumberAssignment>();
        getJdbcTemplate().query(GET_BATCH_NUMBER_ASSIGNMENT_QUERY, new Object[]{batchId},
                new RowCallbackHandler() {
                    @Override
                    public void processRow(final ResultSet resultSet) throws SQLException {
                    final BatchNumberAssignment batchNumberAssignment = new BatchNumberAssignment();
                    batchNumberAssignment.setBatchId(resultSet.getInt("batch_id"));
                    batchNumberAssignment.setDisease(resultSet.getString("disease_abbreviation"));
                    batchNumberAssignment.setCenterDomainName(resultSet.getString("domain_name"));
                        batchNumberAssignments.add(batchNumberAssignment);
                }
            });

        return batchNumberAssignments.size() > 0 ? batchNumberAssignments : null;
        }

    @Override
    public boolean isValidBatchNumberAssignment(final Integer batchNumber, final String diseaseAbbreviation, final String centerDomain) {
        int count = getJdbcTemplate().queryForInt(IS_VALID_BATCH_ASSIGNMENT_QUERY, batchNumber, centerDomain, diseaseAbbreviation);
        return count > 0;
    }
}
