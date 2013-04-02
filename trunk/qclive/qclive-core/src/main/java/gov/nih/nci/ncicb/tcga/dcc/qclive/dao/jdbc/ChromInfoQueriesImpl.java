package gov.nih.nci.ncicb.tcga.dcc.qclive.dao.jdbc;

import gov.nih.nci.ncicb.tcga.dcc.common.dao.BaseQueriesProcessor;
import gov.nih.nci.ncicb.tcga.dcc.qclive.bean.ChromInfo;
import gov.nih.nci.ncicb.tcga.dcc.qclive.dao.ChromInfoQueries;
import org.springframework.jdbc.core.RowCallbackHandler;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;

/**
 * Chromosome info DAO
 *
 * @author Tarek Hassan
 *         Last updated by: $Author$
 * @version $Rev$
 */
public class ChromInfoQueriesImpl  extends BaseQueriesProcessor implements ChromInfoQueries {

    /**
     * Return list of all chromosomes
     */
    public List<ChromInfo> getAllChromInfo() {
        final String query = "select chromosome_name, length, build from chromosome";
        final List<ChromInfo> chromList = new LinkedList<ChromInfo>();
        getJdbcTemplate().query( query, new RowCallbackHandler() {
            public void processRow( final ResultSet resultSet ) throws SQLException {
                final ChromInfo chrom = new ChromInfo();
                chrom.setChromName( resultSet.getString( "chromosome_name" ) );
                chrom.setChromSize( resultSet.getInt( "length" ) );
                chrom.setBuild( resultSet.getString( "build" ) );
                chromList.add( chrom );
            }
        } );
        return chromList;
    }
}
