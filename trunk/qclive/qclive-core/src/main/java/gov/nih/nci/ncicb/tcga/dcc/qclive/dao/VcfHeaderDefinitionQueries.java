package gov.nih.nci.ncicb.tcga.dcc.qclive.dao;

import gov.nih.nci.ncicb.tcga.dcc.qclive.bean.VcfFileHeader;

/**
 * Interface for queries for vcf_header_definition table.
 *
 * @author Your Name
 *         Last updated by: $Author$
 * @version $Rev$
 */
public interface VcfHeaderDefinitionQueries {
    /**
     * Gets the VcfFileHeader object represented by the given header type and id.
     *
     * @param headerType the header type
     * @param headerId the header id
     * @return the VcfFileHeader representing the definition, or null if not found
     */
    public VcfFileHeader getHeaderDefinition(String headerType, String headerId);
}
