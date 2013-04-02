#!/usr/bin/perl
use Spreadsheet::ParseExcel;
use JSON;
use Tie::IxHash;
use DateTime::Format::Strptime;
use Getopt::Long;
use Pod::Usage;
use Carp qw(carp croak);
use strict;
use warnings;

=head1 NAME

 sif2json.pl - Create JSON from BCR Excel SIF

=head1 SYNOPSIS

  $ perl sif2json.pl igc_sif.xls IGC > igc.json
  $ perl sif2json.pl nch_sif.xls NCH > nch.json

  The Excel files must be in .xls (2003) and not .xlsx (2007) format.

=head1 DESCRIPTION

This script extracts requisite information from an IGC or NCH
Excel-formatted shipping information file (SIF) and creates a json
message compliant with the specification at
https://wiki.nci.nih.gov/x/F5eTAw. The json text can then be submitted
to the DCC through the webservice at
L<https://tcga-data.nci.nih.gov/datareports/resources/pendinguuid/json>.

=head1 AUTHOR

    Mark A. Jensen
    CPAN ID: MAJENSEN
    TCGA DCC
    mark.jensen@nih.gov
    http://tcga-data.nci.nih.gov

=head1 COPYRIGHT

 Software License, Version 1.0 Copyright 2012 SRA International, Inc.
 Copyright Notice.  The software subject to this notice and license
 includes both human readable source code form and machine readable,
 binary, object code form (the "caBIG Software"). Please refer to the
 complete License text for full details at the root of the project.

=cut

my $p = Spreadsheet::ParseExcel->new();
die $p->error unless defined $p;
my $infile = shift;
my $bcr = shift;

pod2usage(2) unless ( 
    defined $infile &&
    ($infile =~ /\.xls$/) && 
    defined $bcr && 
    ($bcr =~ /^IGC|NCH$/) ) ;

my $wkbk = $p->parse($infile);
die $p->error unless defined $wkbk;
my ($wks) = $wkbk->worksheets;
my ($row_min, $row_max) = (0, 99);
my @hdrs;
my $data = {};
tie %$data, 'Tie::IxHash';

$data->{bcr} = $bcr;
my @date_parsers = (
		    DateTime::Format::Strptime->new( pattern => '%d-%b-%Y' ),
		    DateTime::Format::Strptime->new( pattern => '%m/%d/%Y' )
		   );

for my $row ($row_min..$row_max) {
  my $first_cell = $wks->get_cell( $row, 0 );
  for ($first_cell->value) {
    /Institution/ && do {
      my $ctr_info = $wks->get_cell( $row, 2 );
      my ($ctr_code) = $ctr_info->value =~ /([0-9]+)$/;
      $data->{center} = $ctr_code;
      last;
    };
    /Date/  && do {
      my $date_info = $wks->get_cell( $row, 2 );
      my $dt;
      foreach my $date_parser (@date_parsers) {
	$dt = $date_parser->parse_datetime($date_info->value);
	last if $dt;
      }
      croak "I don't understand the date '".$date_info->value."'" unless $dt;
      $data->{ship_date} = $dt->strftime('%m-%d-%Y');
      last;
    };
    /Plate/ && do {
      my $plate_info = $wks->get_cell( $row, 2 );
      $data->{plate_id} = $plate_info->value;
      last; 
    };
    /Row/i && do {
      my $i = 2;
      my $hdr_info;
      for ($hdr_info = $wks->get_cell($row,$i); $hdr_info;
	   $hdr_info = $wks->get_cell($row,++$i)) {
	my $hdr = lc $hdr_info->value;
	$hdr =~ s/\s+/_/g;
	push @hdrs, $hdr;
      }
      last;
    };
    /^[A-H]/ && do {
      my $second_cell = $wks->get_cell($row,1);
      my $coord = $first_cell->value.$second_cell->value;
      my $plate_row = $data->{$coord} = {};
      my $i = 2;
      my (@plate_row_data,%plate_row_data);
      my $plate_row_info;
      for ($plate_row_info = $wks->get_cell($row,$i);
	   $plate_row_info;
	   $plate_row_info = $wks->get_cell($row,++$i)) {
	push @plate_row_data, $plate_row_info->value;
      }
      @plate_row_data{@hdrs} = @plate_row_data;
      
      if ($plate_row_data{uuid} !~ /^\s*$/) {
	$plate_row->{bcr_aliquot_uuid} = $plate_row_data{uuid};
	$plate_row->{batch_number} = $plate_row_data{'batch_#'};
	$plate_row->{bcr_aliquot_barcode} = $plate_row_data{'biospecimen_barcode_side'};
	(@$plate_row{qw(portion_number vial_number sample_type analyte_type)},my $ctr) =
		       $plate_row->{bcr_aliquot_barcode} =~ /TCGA-..-....-(..)(.)-(..)(.)-....-(..)/;
	!$data->{center} && ($data->{center} = $ctr);

      }
      else {
	$data->{$coord} = 'null';
      }

      last;
    };
    # default
    carp "Unrecognized row : $row";
  }

}

my $json = JSON->new;
print $json->pretty->encode($data);
1;

