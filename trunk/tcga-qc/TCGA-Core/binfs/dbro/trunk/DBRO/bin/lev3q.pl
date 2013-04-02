#!/usr/bin/perl
use lib '.';
use Pod::Usage;
use TCGA::DBH;
use TCGA::Level4::Config;
($dtype, $dis) = @ARGV;

pod2usage(2) unless ($dtype && $dis);
unless (grep /^$dtype$/,@DTYPE) {
    print STDERR "'$dtype' data type unknown";
    pod2usage(2);
}
unless (grep /^$dis$/, @DISEASE) {
    print STDERR  "'$dis' unknown";
    pod2usage(2);
}

$dbh = TCGA::DBH->new();
$dbh->{RaiseError} = 1;

$stmt = sprintf($queries{$dtype}, ($dis)x7);
$sth = $dbh->prepare($stmt);
$sth->execute();
$i =0;

print join("\t", @{$headers{$dis}}),"\n";
while (@a = $sth->fetchrow_array) {
    $i++;
    unless ($i % 500000) {
	print STDERR "$i lines\n";
    }
    print join("\t",@a), "\n";
}

=head1 NAME

lev3q.pl - Get TCGA Level 3 data for Level 4 (dbro) processing

=head1 SYNOPSIS

$ lev3q.pl [datatype] [disease_abbrev] > level3data.txt

[datatype] : one of methylation|cna|expgene

[disease_abbrev] : a valid TCGA disease abbreviation (e.g., OV, SKCM)

=head1 DESCRIPTION

This script generates output from specific level 3 data queries that are 
contained in L<TCGA::Level4::Config>, for Data Browser datatypes and all
diseases. The module L<TCGA::DBH> is used to connect to the DCC Oracle
database. It is included in the distribution.

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

Mark
