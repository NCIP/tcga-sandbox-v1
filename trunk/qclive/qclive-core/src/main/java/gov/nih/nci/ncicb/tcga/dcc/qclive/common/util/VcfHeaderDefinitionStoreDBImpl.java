package gov.nih.nci.ncicb.tcga.dcc.qclive.common.util;

import gov.nih.nci.ncicb.tcga.dcc.qclive.bean.VcfFileHeader;
import gov.nih.nci.ncicb.tcga.dcc.qclive.dao.VcfHeaderDefinitionQueries;

/**
 * VCF header definition store that uses a DAO to fetch definitions.
 *
 * @author waltonj
 *         Last updated by: $Author$
 * @version $Rev$
 */
public class VcfHeaderDefinitionStoreDBImpl implements VcfHeaderDefinitionStore {

    private VcfHeaderDefinitionQueries vcfHeaderDefinitionQueries;
    /**
     * Gets the VcfFileHeader representing the set definition of this header.  Will return null if no definition.
     *
     * @param headerType the header type (such as INFO or FORMAT)
     * @param headerId   the ID value for the header (such as VLS or DP)
     * @return the VcfFileHeader containing the expected definition, or null if no definition is found for that header
     */
    @Override
    public VcfFileHeader getHeaderDefinition(final String headerType, final String headerId) {
        return vcfHeaderDefinitionQueries.getHeaderDefinition(headerType, headerId);
    }

    public void setVcfHeaderDefinitionQueries(final VcfHeaderDefinitionQueries vcfHeaderDefinitionQueries) {
        this.vcfHeaderDefinitionQueries = vcfHeaderDefinitionQueries;
    }
}
