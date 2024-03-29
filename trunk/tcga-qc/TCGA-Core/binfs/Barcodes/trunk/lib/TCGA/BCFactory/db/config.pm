#$Id: config.pm 18378 2013-03-12 15:46:29Z jensenma $
package TCGA::BCFactory::db::config;
use strict;
use warnings;
use base 'Exporter';
BEGIN {
  our @EXPORT = qw($CONNECT_STRING $USERN %STMT %RESULT_FIELDS);
}

our $HOST="ncidb-tcgas-p.nci.nih.gov";
our $SID="TCGAPRD";
our $PORT=1652;
our $CONNECT_STRING = "dbi:Oracle:host=$HOST;sid=$SID;port=$PORT";
our $USERN = "commonread";

our %STMT = 
  (
    metadata_by_aliquot => "select  bb.built_barcode, bb.uuid, bb.tss_code, bb.participant_code, bb.bcr_center_id, ss.batch_number,d.disease_abbreviation from dcccommon.shipped_biospecimen bb, dcccommon.disease d, dcccommon.tss_to_disease t, dcccommon.samples_sent_by_bcr ss where (bb.built_barcode = ? or bb.uuid = ?) and bb.tss_code = t.tss_code and t.disease_id = d .disease_id and bb.shipped_biospecimen_id = ss.biospecimen_id",
   metadata  => "select uh.barcode, uh.uuid, uh.tss_code, uh.participant_number, uh.receiving_center_id, uh.batch_number, uh.disease_abbreviation from dcccommon.uuid_hierarchy uh where uh.barcode = ? or uh.uuid = ?",
   disease_from_id => "select bh.barcode, u.uuid, d.disease_abbreviation from dcccommon.barcode_history bh, dcccommon.uuid u, dcccommon.disease d where (bh.barcode = ? or u.uuid = ?) and bh.barcode_id = u.latest_barcode_id and bh.disease_id = d.disease_id",
   patient_barcode => 'select patient_barcode, uuid from tcga%s.patient where (patient_barcode = ? or uuid = ?)',
   sample_barcode  => 'select sample_barcode, uuid from tcga%s.sample where (sample_barcode = ? or uuid = ?)',
   portion_barcode => 'select portion_barcode, uuid from tcga%s.portion where (portion_barcode = ? or uuid = ?)',
   analyte_barcode => 'select analyte_barcode, uuid from tcga%s.analyte where (analyte_barcode = ? or uuid = ?)',
   aliquot_barcode => 'select aliquot_barcode, uuid from tcga%s.aliquot where (aliquot_barcode = ? or uuid = ?)'
  );

our %RESULT_FIELDS =
(
 metadata_by_aliquot => [qw( barcode uuid tss case center batch disease)],
 metadata => [qw( barcode uuid tss case center batch disease)],
 disease_from_id => [qw( barcode uuid disease )],
 patient_barcode => [qw(barcode uuid)],
 sample_barcode => [qw(barcode uuid)],
 portion_barcode => [qw(barcode uuid)],
 analyte_barcode => [qw(barcode uuid)],
 aliquot_barcode => [qw(barcode uuid)]
);

=head1 NAME

TCGA::BCFactory::db::config - database connection configuration

=head1 DESCRIPTION

=head1 NOTES

Identifying barcodes/uuids for items other than aliquots will be easy
when the DCCCOMMON.UUID_HIERARCHY table is available on
production. Until then, the hierarchy can be traversed, but only in
the disease-specific clinical tables. Use the DCCCOMMON.BARCODE_HISTORY table
to get the disease_id and disease. For a uuid, use DCCCOMMON.UUID for a link
into BARCODE_HISTORY to get disease. Use the disease abbreviation to determine
which disease-specific schema to use to find the item.

=head1 AUTHOR

Mark A. Jensen -  mark.jensen -at- nih -dot- gov

=cut

1;
