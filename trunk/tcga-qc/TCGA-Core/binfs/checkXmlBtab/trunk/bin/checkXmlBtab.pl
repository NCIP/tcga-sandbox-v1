#!/usr/bin/perl
#$Id: checkXmlBtab.pl 10217 2011-03-06 02:02:42Z jensenma $
use strict;
use warnings;

=head1 NAME

checkXmlBtab.pl - check concordance between BCR XML and resultant biotab files

=head1 SYNOPSIS

perl checkXmlBtab.pl [options] biotabfile xmlfile [xmlfile] ...

Options:

--strict              : check that values in xml are equal to values in biotab
--nostrict (default)  : check only that values present or absent in xml are present or 
                        absent respectively in biotab
--verbose (default)   : send informational warnings to stderr
--noverbose           : run quietly, exiting with 0 (UNIX true) for success,
                        1 (UNIX false) for failure
--diff                : print differences to stdout
--exclude [field]     : exclude biotab header [field] from validation
                        use multiple --exclude options to exclude multiple fields

Examples:

# exclude certain fields 
./checkXmlBtab.pl --exclude drugs --exclude radiations mybiotab.txt TCGA-00-0000.xml

# silent mode
if [ $(./checkXmlBtab.pl --noverbose mybiotab.txt TCGA-00-0000.xml) ]; then echo good ; else echo bad ; fi;

# do a "diff" -- xml content against biotab content
./checkXmlBtab.pl --noverbose --diff mybiotab.txt TCGA-00-0000.xml TCGA-00-0001.xml

=head1 DESCRIPTION

The script checks that data found in a biotab file matches that found
in a corresponding XML. In particular, if null values are present in
the biotab, the script will flag those values if the corresponding XML
element is non-empty.

For the fields provided in a biotab file, the script also checks the
XML for missing content that should be present based on the 
'procurement_status' attribute. If 'procurement_status' == 'Completed',
but the tag is empty, this indicates that data was lost on post-processing.
(This is important to check especially for days_to_* fields.)

=head1 AUTHOR

Mark A. Jensen (mark.jensen@nih.gov)
TCGA DCC

=head1 LICENSE

See LICENSE file in installation directory.

=cut

use lib '../lib';
use checkXmlBtab::Config;
use XML::Twig;
use Pod::Usage;
use Getopt::Long;
use File::Spec;

our $VERSION;
our @biotab_hdrs;
my %biotab_tbl;

my $twig = XML::Twig->new( twig_handlers => { _all_ => \&find_barcodes} );
my ($XMLFIELDS,$BARCODES, $ptr);
my %MEMO;
my $FAIL = 0;
my $XML_MATCHER = qr/^<\?xml/;
my $help;
my $strict = 0;
my $verbose = 1;
my $diff = 0;
my @exclude = ();

GetOptions('help|?' => \$help,
	   'verbose!' => \$verbose,
	   'diff' => \$diff,
	   'strict!' => \$strict,
	   'exclude=s' => \@exclude);

$help = 1 unless @ARGV;
$help && pod2usage(2);

my ($biotab, @xml) = @ARGV;

open my $btf, $biotab or die "File '$biotab': $!";

$_ = <$btf>; chomp;

if (/$XML_MATCHER/) {
  die "Biotab file should be first on the commandline; '$btf' looks like XML";
}


my @file_hdrs = split /\t/;
@biotab_hdrs = @file_hdrs;
if (@exclude) {
  @exclude = grep !/^.*barcode$/, @exclude; # can't exclude the barcode
  @biotab_hdrs = map { my $e = $_ ; grep(/^$e$/, @exclude) ? () : $e } @biotab_hdrs;
}

my @barcode_hdrs = grep /^.*_barcode$/, @file_hdrs;

# Load biotab hash
my $line = 1;
while (<$btf>) {
  $line++;
  chomp;
  my @values = split /\t/;
  next unless @values;
  my %data;
  @data{@file_hdrs} = @values;
  my (@barcodes, $key);
  push @barcodes, delete $data{$_} for @barcode_hdrs;
  unless (@barcodes && ($barcodes[0] =~ /^TCGA/)) {
    print STDERR "Barcode not present in biotab file line $line; skipping...\n" if $verbose;
    next;
  }
  $key = join(':', sort { length $a <=> length $b } @barcodes);
  $biotab_tbl{$key} = \%data;
}

foreach my $xml (@xml) {
  my $xml_filename = (File::Spec->splitpath($xml))[-1];
  unless ( -e $xml && -f $xml ) {
    print STDERR "File '$xml' not found; skipping...\n" if $verbose;
    $FAIL = 1;
    next;
  }

  $BARCODES = {};
  $ptr = [];
  eval {
    $twig->parsefile($xml);
  };
  if ($@) {
    my ($val_msg) = $@ =~ /\n(.*byte [0-9]+)/;
    print STDERR "Problems with XML file '$xml' ($val_msg), skipping...\n";
    next;
  }

  foreach my $key (keys %$BARCODES) {
      # parse
      $XMLFIELDS = {};
      $BARCODES->{$key}->descendants( \&biotab_elt );
      my $biotab_row = $biotab_tbl{$key};

      # validation logic:
      unless ($biotab_row) {
	  print STDERR "No corresponding biotab row for barcode '$key', XML file '$xml'\n" if $verbose;
	  $FAIL = 1;
	  next;
      }
      
      foreach my $field (grep !/barcode/, @biotab_hdrs) {
	  my $biotab_val = $biotab_row->{$field};
	  my $xml_val = $XMLFIELDS->{$field}->{content};
	  my $xml_data_missing = $XMLFIELDS->{$field}->{missing};
	  if ($xml_data_missing) {
	      print STDERR "$xml_filename:$field ($key) - XML tag is empty, but procurement_status is 'Completed'\n";
	      $FAIL = 1;
	  }
	  if ( !defined $xml_val || ($xml_val eq '') ) { # xml tag is not present or empty
	      if ( defined $biotab_val && ($biotab_val ne 'null')) {
		  print STDERR "$xml_filename:$field ($key) - XML tag is empty, but biotab field is not null\n" if $verbose;
		  print "$xml_filename:$field\t$key\t$xml_val\t$biotab_val\n" if $diff;
		  $FAIL = 1;
		  next;
	      }
	  }
	  else { # xml tag nonempty
	      if ( !defined $biotab_val || ($biotab_val eq 'null') ) {
		  print STDERR "$xml_filename:$field ($key) - XML tag is non-empty, but biotab field is null\n" if $verbose;
		  print "$xml_filename:$field ($key)\t$xml_val\t".(defined $biotab_val ? $biotab_val : '(undef)')."\n" if $diff;
		  $FAIL = 1;
		  next;
	      }
	      else {
		  if ( !defined $biotab_val || ($biotab_val ne $xml_val) ) {
		      print "$xml_filename:$field ($key)\t$xml_val\t". (defined $biotab_val ? $biotab_val : '(undef)') ."\n" if $diff;
		      print STDERR "$xml_filename:$field ($key) - XML value is not equal to biotab field (strict validation)\n" if $verbose && $strict;
		      $FAIL = 1 if $strict;
		  }
		  
		  # else, something is there in both files, this is valid for nonstrict test
	      }
	  }
      }
  }
  1;
  print "$xml_filename OK\n" if $verbose && !$FAIL;
  exit($FAIL);
}

sub biotab_elt {
  my $ln = $_[0]->local_name;
  return unless grep /^$ln$/, @biotab_hdrs;
  my $content = $_[0]->text;
  my $missing = 0;
  if ($_[0]->att('procurement_status')) {
    $missing = ( ($_[0]->att('procurement_status') eq 'Completed') && (!defined $content || ($content eq '')) );
  }
  $XMLFIELDS->{$ln} = { content => $content, missing => $missing };
  return;
}

sub find_barcodes {
    my $ln = $_->local_name;
    return unless grep(/^$ln$/, @barcode_hdrs);
    $ptr = [] if ( $ln eq $barcode_hdrs[0] );
    push @$ptr, $_->text;
    if ( $ln eq $barcode_hdrs[-1] ) {
	$BARCODES->{join(':',@$ptr)} = $_->parent;
	pop @$ptr;
    }
}

sub parent_containing_barcode {
  my ($twig, $barcode) = @_;
  return $MEMO{$barcode} if defined $MEMO{$barcode};
  my ($ret, @rest) = $twig->root->descendants( sub { $_->text eq $barcode } );
  warn "More than one element contains barcode '$barcode'" if @rest;
  warn "No parent elt containing barcode '$barcode' found" unless $ret;
  return $ret && $ret->parent;
}
