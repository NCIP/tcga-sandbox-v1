package TCGA::DBRO;
use strict;

our $VERSION     = '0.01';

=head1 NAME

TCGA::DBRO - Slice/dice Level 3 data for data browser

=head1 SYNOPSIS

# various

=head1 DESCRIPTION

This set of modules is based in part on Carl Schaefer's scripts
bequeathed to the DCC upon his retirement. The final product is
ultimately tab-delimited files that can be directly imported into the
L4 tables in the DCC database (using SQLImport or whatever it 
happens to be).

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


