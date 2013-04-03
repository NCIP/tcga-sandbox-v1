/*
 * Software License, Version 1.0 Copyright 2011 SRA International, Inc.
 * Copyright Notice.  The software subject to this notice and license includes both human
 * readable source code form and machine readable, binary, object code form (the "caBIG
 * Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */

package gov.nih.nci.ncicb.tcga.dcc.uuid.constants;

/**
 * Class containing constants to be used by the UUID Browser
 *
 * @author Dominique Berton
 *         Last updated by: $Author$
 * @version $Rev$
 */
public class UUIDBrowserConstants {

    public static final String QUERY_UUID_BROWSER = "select disease_abbreviation, uuid, parent_uuid, is_redacted, platforms, " +
            "item_type_id, tss_code, center_id_bcr, batch_number, barcode, participant_number, sample_type_code, " +
            "sample_sequence, portion_sequence, portion_analyte_code, plate_id, receiving_center_id, slide, slide_layer, " +
            "create_date, update_date, is_shipped, shipped_date, center_code from uuid_hierarchy " +
            "order by disease_abbreviation";

    public static final String QUERY_UUID_BROWSER_BY_UUID = "select disease_abbreviation, uuid, parent_uuid, is_redacted, platforms, " +
            "item_type_id, tss_code, center_id_bcr, batch_number, barcode, participant_number, sample_type_code, " +
            "sample_sequence, portion_sequence, portion_analyte_code, plate_id, receiving_center_id, slide, slide_layer, " +
            "create_date, update_date, is_shipped, shipped_date, center_code from uuid_hierarchy where uuid = ? " +
            "order by disease_abbreviation";

    public static final String QUERY_UUID_BROWSER_BY_BARCODE = "select disease_abbreviation, uuid, parent_uuid, is_redacted, platforms, " +
            "item_type_id, tss_code, center_id_bcr, batch_number, barcode, participant_number, sample_type_code, " +
            "sample_sequence, portion_sequence, portion_analyte_code, plate_id, receiving_center_id, slide, slide_layer, " +
            "create_date, update_date, is_shipped, shipped_date, center_code from uuid_hierarchy where barcode = ? " +
            "order by disease_abbreviation";

    public static final String REPLACE_UUID = "REPLACE_UUID";
    public static final String QUERY_UUID_BROWSER_BY_UUIDS = "select disease_abbreviation, uuid, parent_uuid, is_redacted, platforms, " +
            "item_type_id, tss_code, center_id_bcr, batch_number, barcode, participant_number, sample_type_code, " +
            "sample_sequence, portion_sequence, portion_analyte_code, plate_id, receiving_center_id, slide, slide_layer, " +
            "create_date, update_date, is_shipped, shipped_date, center_code from uuid_hierarchy where uuid in (" + REPLACE_UUID + ") " +
            "order by disease_abbreviation";

    public static final String REPLACE_BARCODE = "REPLACE_BARCODE";
    public static final String QUERY_UUID_BROWSER_BY_BARCODES = "select disease_abbreviation, uuid, parent_uuid, is_redacted, platforms, " +
            "item_type_id, tss_code, center_id_bcr, batch_number, barcode, participant_number, sample_type_code, " +
            "sample_sequence, portion_sequence, portion_analyte_code, plate_id, receiving_center_id, slide, slide_layer, " +
            "create_date, update_date, is_shipped, shipped_date, center_code from uuid_hierarchy where barcode in (" + REPLACE_BARCODE + ") " +
            "order by disease_abbreviation";

    public static final String QUERY_EXISTING_BARCODES = "select barcode from barcode_history where barcode in(" + REPLACE_BARCODE + ")";
}
