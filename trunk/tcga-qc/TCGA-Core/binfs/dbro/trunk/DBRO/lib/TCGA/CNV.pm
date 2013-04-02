package TCGA::CNV;
use strict;

our $VERSION     = '0.01';

=head1 NAME

TCGA::CNV - Slice/dice SNP6 CNV data for data browser

=head1 SYNOPSIS

  use TCGA::CNV;

=head1 DESCRIPTION

This set of modules is based on Carl Schaefer's scripts bequeathed to
the DCC upon his retirement. Their main job is to turn SNP6 segment
data into gene copy number differences betwen tumor and normal
samples. To do this, genes are identified as sites of CNV using a dump
of the Database of Genomic Variation, and segments are mapped to genes
using the UCSC RefFlat tables.

This set of modules attempts to bring Carl's pipeline into a single
maintainable location. It also uses input data generated directly from
DCC level 3 database queries, rather than from publically distributed
data archives, as Carl originally did.

=head1 AUTHOR

    Mark A. Jensen
    CPAN ID: MAJENSEN
    TCGA DCC
    mark.jensen@nih.gov
    http://tcga-data.nci.nih.gov

=head1 COPYRIGHT

This program is licensed under the caBIG Software License.
The full text of the license can be found in the LICENSE file
included with this module.

=cut

1;


