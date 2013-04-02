#!/usr/bin/perl
use lib '../lib';
use Pod::Usage;
use TCGA::BCR::ReadXlsDashboard;
use TCGA::BCR::DashboardConfig qw(%dtfs $laml_kludge);
use TCGA::BCR::JSONVal::Configure qw(%type_map);
use Tie::IxHash;
use JSON;
use File::Basename;
use DateTime;
use DateTime::Format::Strptime;
use Scalar::Util qw(looks_like_number);
use strict;
use warnings;

my $xlsf = shift;

my @specced_tables = @ARGV;

pod2usage(2) unless $xlsf;

my $dash = TCGA::BCR::ReadXlsDashboard->new($xlsf);
die $dash->error if $dash->error;

my @required_tables = @specced_tables;
    @required_tables = $dash->get_valid_table_names unless @required_tables;
my $json = {};
tie %$json, 'Tie::IxHash';

my $strp = DateTime::Format::Strptime->new( pattern => '%D' );
my $dt = DateTime->now;

$json->{version} = basename($xlsf, '.xls','.xlsx','.XLS','.XLSX')."/DCC";
$json->{timestamp} = $strp->format_datetime($dt);

foreach my $table (@required_tables) {
  my $data = $dash->get_table($table);
  if (!$data) {
      print STDERR "Problem for table '$table' : ".$dash->error;
      exit(1);
  }
  my @json_tags = $dash->get_json_headers($table);
  my @idx = @{$data->{$json_tags[0]}};
  foreach my $i (0..$#idx) {
    my $hash = {};
    tie %$hash, 'Tie::IxHash';
    for my $tag (@json_tags) {
	my $value = $data->{$tag}[$i];
	# Pass Pending Genotype kludge:
	# per Martin Ferguson guidance, email of 20 Nov 2012

	if ( $table eq 'case_summary_by_disease' ) {
	     if ($tag eq 'pass_pending_geno' ) {
		 next;
	     }
	     if ($tag eq 'pending_mol_qc') {
		 $value += ( $data->{pass_pending_geno} ?
			     $data->{pass_pending_geno}[$i] :
			     0 )
	     }
	}

	# LAML kludge:
	# Removing the addition of pipeline b samples per 
	# guidance at 10/17/12 client mtg. 
# 	if ( $table eq 'case_summary_by_disease' &&
# 	    $data->{tumor_abbrev}[$i] eq 'LAML' &&
# 	     $laml_kludge->{case_summary_by_disease}->[0]->{$tag}
# 	    ) {
# 	    $value += $laml_kludge->{case_summary_by_disease}->[0]->{$tag} if
# 		looks_like_number($value);
# 	}
	# Held samples kludge (client meeting 9/5/12)
	# Removing the addition of held samples with the resolution of 
	# APPS-6578 (per guidance at 10/17/12 client mtg)
#  	if ( $table eq 'case_summary_by_disease' ) {
#  	    $hash->{note} ||= 'some values inflated by qualified_hold in order to display per specification of 9/5/12';
#  	    my $held_samples = $data->{qualified_hold}[$i] || 0;
# 	    grep (/^$tag$/, qw(submitted_to_bcr qual_path qual_mol)) && do {
#  		$value += $held_samples;
#  	    };
#  	}
	# typecheck/fix percent
	if (defined $value && !looks_like_number($value)) {
	    if ($type_map{$tag} =~ /^int|float$/) {
		$value /= 100 if ($value =~ s/%$//);
		print STDERR "$type_map{$tag} expected, but value is '$value' for $tag\n" if !looks_like_number($value);
	    }
	    if ($type_map{$tag} =~ /^date$/) {
		# convert
	      my $dt;
	      foreach my $dtf (values %dtfs) {
		$dt = $dtf->parse_datetime($value);
		last if defined $dt;
	      }
	      if (defined $dt)  {
		$value = $dtfs{json}->format_datetime($dt);
	      }
	      else {
		print STDERR "date '$value' cannot be parsed\n";
	      }
	    }
	}
	$hash->{$tag} = looks_like_number $value ? $value+0 : $value; # numify
    }
    push @{$json->{$table}}, $hash;
  }
  1;
}

print JSON->new()->pretty(1)->encode($json);


1;

=head1 NAME

bcrx2j.pl - Create BCR Pipeline JSON from a dashboard spreadsheet

=head1 SYNOPSIS

$ bcrx2j.pl dashboard.xls > dashboard.json.txt

Type 'perldoc bcrx2j.pl' for more information.

=head1 DESCRIPTION

bcrx2j.pl extracts data from the BCR Dashboard Excel spreadsheets and
creates L<json-formatted|https://wiki.nci.nih.gov/x/jqn9AQ> text
files, according to the format required for the DCC L<BCR Pipeline
Report|https://tcga-data.nci.nih.gov/datareports/BCRPipelineReport.htm>.

=head1 AUTHOR

Mark A. Jensen ( mark -dot- jensen -at- nih -dot- gov) 

=head1 COPYRIGHT

(c) 2012 SRA International, Inc.

Distributed under the terms of the caBIG v1.0 license.

=cut
