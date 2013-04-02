package sif2json;
use strict;

our $VERSION = 0.01;
1;

#################### main pod documentation begin ###################


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
 Copyright Notice.  The software subject to this notice and license includes both human readable source code form and machine readable, binary, object code form (the "caBIG Software"). Please refer to the complete License text for full details at the root of the project.
=cut

#################### main pod documentation end ###################


1;


