#!/usr/bin/perl
use lib '../lib';
use Pod::Usage;
use TCGA::BCR::ReadXlsDashboard;
use TCGA::BCR::DashboardConfig qw(%dtfs);
use TCGA::BCR::JSONVal::Configure;
use DateTime::Format::Strptime;
use JSON;
use Scalar::Util qw(looks_like_number);
use List::MoreUtils qw(pairwise);
use strict;
use warnings;

my $dashbd;
my ($xlsf, $jsonf, $one_table) = @ARGV;
my @tables = qw( case_summary_by_disease case_summary_by_tss case_by_shipment );

pod2usage(2) unless $xlsf && $jsonf;

$dashbd = TCGA::BCR::ReadXlsDashboard->new($xlsf) or die $dashbd->error;

open my $jfh, $jsonf or die $!;
undef $/;
my $json_text = <$jfh>;
my $json = decode_json $json_text or die $!;
@tables = ($one_table) if $one_table;
foreach my $table (@tables) {
    print "$table : \n";
    my $data = $dashbd->get_table($table) or die "Can't get table $table from Excel file";
    for my $hdr ($dashbd->get_json_headers($table)) {
	print "$hdr\n";
	my $i = 0;
	my $fail;
	my @json_row = map { $_->{$hdr} } @{$json->{$table}};
	my $expected_type = $type_map{$hdr};
	pairwise { $i++;
		   my $msg = '%s:%s, row %d - excel %s json %s'."\n";
		   # anything in $a?
		   !defined $a && do {
		       unless (!$b) {
			   printf($msg, $table, $hdr, $i, 'null', $b) ;
			   $fail =1;
		       }
		       return;
		   };
		   $a =~ s/^\s+//;
		   $a =~ s/\s+$//;
		   $a eq '' && do {
		       unless (!$b) {
			   printf($msg, $table, $hdr, $i, 'empty', $b);
			   $fail=1;
		       }
		       return;
		   };
		   # something in $a:
		   if ($expected_type =~ /^int|float$/) {
		       looks_like_number($a) && do {
			   unless (defined $b ? $a == $b : !$a) {
			       printf("%s:%s, row %d - excel %s json %s\n",
				      $table, $hdr, $i, $a, $b||'null');
			       $fail =1 ;
			   }
			   return;
		       };
		       ($a =~ s/%$//) && do {
			   unless (abs($a/100-$b) < EPS()) {
			       printf("%s:%s, row %d - excel %s json %s\n",
				      $table, $hdr, $i, $a/100, $b);
			       $fail=1;
			   }
			   return;
		       };
		   }
		   elsif ($expected_type =~ /^date$/) {
		       # simple eq
		       my ($dta, $dtb);
		       foreach my $dtf (values %dtfs) {
			   $dta = $dtf->parse_datetime($a);
			   last if defined $dta;
		       }
		       foreach my $dtf (values %dtfs) {
			   $dtb = $dtf->parse_datetime($b);
			   last if defined $dtb;
		       }
		       unless ($a && $b) {
			   printf("%s:%s, row %d - excel : date expected but got %s\n", $table, $hdr, $i, $a) unless $dta;
			   printf("%s:%s, row %d - json : date expected but got %s\n", $table, $hdr, $i, $b) unless $dtb;
		       }
		       unless ($dta == $dtb) {
			   printf("%s:%s, row %d - excel %s json %s\n",
				  $table, $hdr,$i, $a, $b);
			   $fail=1;
		       }
		   }
		   else {
		       # default
		       unless ($a eq $b) {
			   printf("%s:%s, row %d - excel %s json %s\n",
				  $table, $hdr,$i, $a, $b);
			   $fail=1;
		       }
		   }
	} @{$data->{$hdr}}, @json_row;
	print $fail ? "" : "- PASS\n";
    }
}

sub EPS() {0.005}
1;

=head1  NAME

chkdash.pl - compare a table in a BCR Dashboard with a JSON upload

=head1 SYNOPSIS

perl chkdash.pl dashboard.xls dashboard.json table_name

=head1 DESCRIPTION

The script compares the given table in the given XML and json
representations and carps when values are not equivalent.

=head1 AUTHOR

Mark A. Jensen (mark -dot- jensen -at- nih -dot- gov)

=head1 COPYRIGHT

(c) 2012 SRA International, Inc.
Distributed under the terms of the caBIG v1.0 license.

=cut

