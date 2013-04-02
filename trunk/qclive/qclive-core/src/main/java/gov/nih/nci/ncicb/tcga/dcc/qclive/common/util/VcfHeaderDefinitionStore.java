package gov.nih.nci.ncicb.tcga.dcc.qclive.common.util;

import gov.nih.nci.ncicb.tcga.dcc.qclive.bean.VcfFileHeader;

/**
 * Interface VcfHeaderDefinitionStore.  Represents interface for classes that fetch definitions for VCF headers.
 *
 * @author waltonj
 *         Last updated by: $Author$
 * @version $Rev$
 */
public interface VcfHeaderDefinitionStore {
    /**
     * Gets the VcfFileHeader representing the set definition of this header.  Will return null if no definition.
     *
     * @param headerType the header type (such as INFO or FORMAT)
     * @param headerId the ID value for the header (such as VLS or DP)
     * @return the VcfFileHeader containing the expected definition, or null if no definition is found for that header
     */
    public VcfFileHeader getHeaderDefinition(String headerType, String headerId);
}
