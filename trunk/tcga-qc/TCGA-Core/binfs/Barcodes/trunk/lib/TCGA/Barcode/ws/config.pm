#$Id$
package TCGA::Barcode::ws::config;
use strict;
use warnings;
use base 'Exporter';
BEGIN {
  our @EXPORT = qw($BCURL $UURL %JSON_FIELDS @RESULT_FIELDS);
}

our $BCURL='https://tcga-data.nci.nih.gov/uuid/uuidws/metadata/json/barcode';
our $UURL='https://tcga-data.nci.nih.gov/uuid/uuidws/metadata/json/uuid';

# map result fields to the multi-index of the converted json
our %JSON_FIELDS = (
		    item => [qw(tcgaElement elementType)],
		    barcode => [qw(tcgaElement barcodes barcode)],
		    batch => [qw(tcgaElement batch)],
		    uuid => [qw(tcgaElement uuid)],
		    disease => [qw(tcgaElement disease abbreviation)],
		    center => [qw(tcgaElement aliquot receivingCenter abbreviation)],
		    bcr => [qw(tcgaElement bcr abbreviation)],
		    case => [qw(tcgaElement participant id)],
		    case_url => [qw(tcgaElement participant @href)],
		    sample_url => [qw(tcgaElement sample  @href)],
		    portion_url => [qw(tcgaElement portion @href)],
		    analyte_url => [qw(tcgaElement analyte @href)],
		    tss => [qw(tcgaElement tss id)],
		    analyte_type => [qw(tcgaElement analyte analyteType)],
		    tissue_type => [qw(tcgaElement sample sampleType)],
		    vial => [qw(tcgaElement sample vial)],
		    plate => [qw(tcgaElement aliquot plateID)]
);

# these are the fields that will actually be updated in the Barcode object
# (a subset of the %JSON_FIELDS keys)
our @RESULT_FIELDS = qw( item barcode uuid tss case center batch disease analyte_type tissue_type bcr sample 
                         portion analyte plate vial);

=head1 NAME

TCGA::BCFactory::ws::config - webservice configuration

=head1 DESCRIPTION

This module defined the mappings between the L<TCGA::Barcode> accessors and the 
Biospecimen Metadata Web Service json structure (as converted to a Perl reference
by L<JSON>).

=head1 NOTES

See the Biospecimen Metadata Web Service User Guide at L<https://wiki.nci.nih.gov/x/xQpyAg>.

=head1 AUTHOR

Mark A. Jensen -  mark.jensen -at- nih -dot- gov

=cut

1;
