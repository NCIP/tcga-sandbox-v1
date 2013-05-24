/*
 * Software License, Version 1.0 Copyright 2013 SRA International, Inc.
 * Copyright Notice.  The software subject to this notice and license includes both human
 * readable source code form and machine readable, binary, object code form (the "caBIG
 * Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */

package gov.nih.nci.ncicb.tcga.dcc.bamloader;

import gov.nih.nci.ncicb.tcga.dcc.common.bean.Tumor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.ParameterizedRowMapper;
import org.springframework.stereotype.Repository;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.sql.DataSource;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;

/**
 * Bean class for creating the lookup dataset for loading BAM files and easy test wiith actual dev lookup data instead of empty
 *
 * @author Dominique Berton
 *         Last updated by: $Author$
 * @version $Rev$
 */

@Repository
public class LookupForBAMImpl implements LookupForBAM {

    private JdbcTemplate jdbcTemplate;
    private List<Tumor> diseases = new LinkedList<Tumor>();
    private List<CenterShort> centers = new LinkedList<CenterShort>();
    private List<BAMDatatype> datatypeBAMs = new LinkedList<BAMDatatype>();
    private List<AliquotShort> aliquots = new LinkedList<AliquotShort>();

    @Override
    public List<Tumor> getDiseases() {
        return diseases;
    }

    @Override
    public List<CenterShort> getCenters() {
        return centers;
    }

    @Override
    public List<BAMDatatype> getDatatypeBAMs() {
        return datatypeBAMs;
    }

    @Override
    public List<AliquotShort> getAliquots() {
        return aliquots;
    }

    @Resource(name = "lookUpBAMDataSource")
    public void setDataSource(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    @PostConstruct
    protected void setupQueries() {
        centers = jdbcTemplate.query(BAMLoaderConstants.CENTER_QUERY, new ParameterizedRowMapper<CenterShort>() {
            public CenterShort mapRow(final ResultSet resultSet, final int i) throws SQLException {
                final CenterShort c = new CenterShort();
                c.setCenterId(resultSet.getInt(1));
                c.setShortName(resultSet.getString(2));
                c.setCenterType(resultSet.getString(3));
                return c;
            }
        });
        diseases = jdbcTemplate.query(BAMLoaderConstants.DISEASE_QUERY, new ParameterizedRowMapper<Tumor>() {
            public Tumor mapRow(final ResultSet resultSet, final int i) throws SQLException {
                final Tumor tumor = new Tumor();
                tumor.setTumorId(resultSet.getInt(1));
                tumor.setTumorName(resultSet.getString(2));
                tumor.setTumorDescription(resultSet.getString(3));
                return tumor;
            }
        });
        datatypeBAMs = jdbcTemplate.query(BAMLoaderConstants.DATATYPE_BAM_QUERY, new ParameterizedRowMapper<BAMDatatype>() {
            public BAMDatatype mapRow(final ResultSet rs, final int i) throws SQLException {
                final BAMDatatype dt = new BAMDatatype();
                dt.setDatatypeBAMId(rs.getInt(1));
                dt.setDatatypeBAM(rs.getString(2));
                dt.setGeneralDatatype(rs.getString(3));
                return dt;
            }
        });
        aliquots = jdbcTemplate.query(BAMLoaderConstants.ALIQUOT_QUERY, new ParameterizedRowMapper<AliquotShort>() {
            public AliquotShort mapRow(final ResultSet rs, final int i) throws SQLException {
                final AliquotShort a = new AliquotShort();
                a.setAliquotId(rs.getLong(1));
                a.setBarcode(rs.getString(2));
                return a;
            }
        });
    }

}//End of Class
