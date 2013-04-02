package gov.nih.nci.ncicb.tcga.dcc.bamloader;

import gov.nih.nci.ncicb.tcga.dcc.common.bean.Tumor;

import java.util.List;

/**
 * general class for look ups for the BAM loader
 *
 * @author Dominique Berton
 *         Last updated by: $Author$
 * @version $Rev$
 */
public interface LookupForBAM {
    List<Tumor> getDiseases();

    List<CenterShort> getCenters();

    List<BAMDatatype> getDatatypeBAMs();

    List<AliquotShort> getAliquots();
}
