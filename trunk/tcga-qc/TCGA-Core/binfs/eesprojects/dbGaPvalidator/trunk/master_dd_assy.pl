#! /bin/perl -w
use DDSearch;
use dbGaP::Config;
use Storable;
use Getopt::Long;
use strict;
use warnings;


my ($elt_type_hash_f, $dd_file, $outfile);
GetOptions ( "typehash=s" => \$elt_type_hash_f,
	     "dd=s" => \$dd_file,
	     "output:s" => \$outfile);

my $elt_type_hash = retrieve $elt_type_hash_f or die "Problem with element-type serialized hash: $!";

my $dd = DDSearch->new();
$dd->load_dict($dd_file) or die "Problem with DCC data dictionary instance: $!";

my $outfh = \*STDOUT;
open $outfh, ">$outfile" or die "Problem with outfile: $!" if $outfile;

print $outfh join("\t",'schematag',@DBGAP_DD_HEADERS), "\n";
my @schemata = keys %$elt_type_hash;

foreach my $tag (@schemata) {
    my @recs = @{$elt_type_hash->{$tag}};
    foreach my $rec (@recs) {
	my $desc = $dd->get_best_definition($$rec{name});
	my @values = $$rec{value} && @{$$rec{value}};
	# clean up description
	if ($desc) {
	    $desc =~ s/\t/ /g;
	    $desc =~ s/^\"(.*)\"$/$1/;
	    $desc =~ s/^\'(.*)\'$/$1/;
	    $desc .= '(public CDE id '.$dd->get_cde_by_elt_name($$rec{name}).')';
	}
	# may need to clean out quote chars here too
	print $outfh join("\t", $tag, $$rec{name}, $desc || 'null', $$rec{type}), (  $$rec{value}  ? ("\t",join("\t",@values)) : ''), "\n";
    }

}


=head1 NAME

master_dd_assy.pl - Assemble a master dbGaP data dictionary

=head1 SYNOPSIS

$ perl master_dd_assy.pl --typehash elt_type_hash_2 --dd dd2.3.11.xml > newdd.txt
$ perl master_dd_assy.pl --typehash elt_type_hash_2 --dd dd2.3.11.xml --outfile newdd.txt


=head1 DESCRIPTION

This script outputs a dbGaP data dictionary for all data elements
represented in the serialized element-type hash provided as input.
The schema tag associated with the element is output first, followed
by the columns represented in @dbGaP::Config::DBGAP_DD_HEADERS.  The
schema tag should be used to split the master dd into the component
dds as described at
L<https://wiki.nci.nih.gov/display/TCGAproject/DCC+to+dbGaP+Metadata+Submission+Protocol+%28Proposed%29>.

=over

* The assumption on the elt_type_hash is that all type values are
  compliant with dbGaP accepted types. See L<elt_type.pl>.

* For elements for which a definition (i.e. VARDESC) cannot be found
  in the DCC Data Dictionary, the value C<null> is substituted.

* For elements for which a definition can be found in the DCC Data
  Dictionary, the public id is appended to the definition as "(public
  CDE id NNNNNN)".

=back

=head1 AUTHOR

Mark A. Jensen (mark.jensen@nih.gov)

=head1 COPYRIGHT

(c) 2011 SRA International, Inc.

=cut
