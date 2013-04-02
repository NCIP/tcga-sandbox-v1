#!/usr/bin/perl
#$Id: ddbuild.pl 9734 2011-02-10 01:32:58Z jensenma $
use strict;
use warnings;
use Getopt::Long;
use Pod::Usage;
use XML::Twig;

use lib '../lib'; # may need to change
use DataDict::caDSR;
use DataDict::XSDParser;
use DataDict::Dictionary;
use DataDict::Dictionary::Entry;
use DataDict::OCDump;

my @SCHEMA_ATTRIBUTES = qw(topic disease xml_elt_ns xsd_current_ver);
my @ELEMENT_ATTRIBUTES = qw(public_id xml_elt_name xml_tier_level xsd_intro_ver);
my @CRF_ELEMENTS = qw(CRFquestionText CRFdataEltLabel CRFcaBIGdefinition
                      CRFadditionalInstructions CRFentryAlternatives);
my @CADSR_ELEMENTS = qw(caDSRlongName caDSRshortName caDSRdefinition
                        caDSRalternateDefinition caDSRlatestVersion
			caDSRvalueDomainHref CDEBrowserLink);

my $VERSION = "0.2";
my $version_matcher = qr/(?:[0-9]+\.?)+/;

Getopt::Long::Configure( qw(auto_help auto_version) );

# options
my $cadsr = 0;
my $pretty;
my ($help, $dictionary, $output, $verbose);
my ($dict_ver, $schema_ver);

my $opt_status = GetOptions( 
			    "help|?" => \$help,
			    "cadsr!" => \$cadsr,
			    "verbose!" => \$verbose,
			    "dictionary=s" => \$dictionary,
			    "output=s" => \$output,
			    "pretty=s" => \$pretty,
			    "schema-version=s" => \$schema_ver,
			    "dict-version=s" => \$dict_ver
			   );

$opt_status || pod2usage(1);
pod2usage(1) if $help;

$DataDict::Dictionary::VERBOSE = $DataDict::Dictionary::Entry::VERBOSE =
  $DataDict::OCDump::VERBOSE = $DataDict::caDSR::VERBOSE = 
  $DataDict::XSDParser::VERBOSE = $verbose;

die "Schema version format invalid" unless !defined($schema_ver) || ($schema_ver =~ /$version_matcher/);
die "Dictionary version format invalid" unless !defined($dict_ver) || ($dict_ver =~ /$version_matcher/);

my $dict = DataDict::Dictionary->new();

$dict->twig->set_pretty_print($pretty) if $pretty;

$dict->parse($dictionary) if ($dictionary);

# set versions
$dict->twig->root->set_att('version', $dict_ver) if defined $dict_ver;
$dict->twig->root->set_att('schemaVersion', $schema_ver) if defined $schema_ver;

 my @input_files = @ARGV;

my @xsds = grep /\.xsd$/i, @input_files;
my @oc_files = grep /\.txt$/i, @input_files;

foreach my $file (@xsds) {
  update_from_schema($dict, $file);
};

foreach my $file (@oc_files) {
  update_from_ocdump($dict, $file);
};

if ($cadsr) {
  update_from_cadsr($dict);
}

my $fh;
if ($output) {
  open $fh, ">$output" or die "Output file '$output': $!";
}
else {
  $fh = *STDOUT;
}
$dict->print($fh);

sub update_from_schema {
  my ($dict, $file) = @_;
  $DB::single=1;
  my $xsd = DataDict::XSDParser->new();
  $xsd->parse($file);
  while( my $elt = $xsd->next_elt ) {
    if (!defined $elt->cde) {
      warn "Schema elt with name '".$elt->xml_elt_name."' has no cde public id. Skipping..."
	if $verbose;
      next;
    }
    my $entry = $dict->get_entry_by_cde($elt->cde) || DataDict::Dictionary::Entry->new();
    no strict qw(subs);
    $entry->cde( $elt->cde );
    # add to the TCGAxmlElts list for the entry...
    my %xml_info;
    foreach ( @SCHEMA_ATTRIBUTES ) {
      $xml_info{$_} = $xsd->$_;
    }
    foreach ( @ELEMENT_ATTRIBUTES ) {
      $xml_info{$_} = $elt->$_;
    }
    $entry->TCGAxmlElts(\%xml_info);
    $entry->tags( $xsd->topic ) if $xsd->topic;
    $entry->studies( $xsd->disease ) if $xsd->disease;
    $dict->add_entry($entry);
  }
  
  return;
}

sub update_from_ocdump {
  my ($dict, $file) = @_;
  my $oc = DataDict::OCDump->new();
  $oc->parse($file);
  while (my $rec = $oc->next_record) {
    my $entry = $dict->get_entry_by_cde($rec->cde);
    if (!defined $entry) {
      warn "OC dump cde '".$rec->cde."' not present in dictionary. Skipping..." if $verbose;
      next;
    }
    no strict qw(subs);
    for ( @CRF_ELEMENTS ) {
      $entry->$_( $rec->$_ );
    }
  }
}

sub update_from_cadsr {
  my $dict = shift;
  my $cadsr = DataDict::caDSR->new();
  for my $cde ($dict->cdes) {
    $DB::single=1 if !length($cde);
    my $entry = $dict->get_entry_by_cde($cde);
    $cadsr->query_by_cde($cde);
    no strict qw(subs);
    for (@CADSR_ELEMENTS) {
      $entry->$_( $cadsr->$_ );
    }
  }
}


=head1 NAME

ddbuild.pl - Data dictionary builder/updater

=head1 SYNOPSIS

perl ddbuild.pl [--cadsr --verbose] [--pretty <token>] [--dictionary old_dict.xml] [--output new_dict.xml] input.xsd input2.xsd ... input_OC.txt input_OC2.txt ...

Options:

 --dictionary [file]   dictionary xml to be updated (if any)
 --output [file]       file to contain created/updated dictionary
                       (default, stdout)
 --pretty [token]      pretty print output ('indented' for [token] is good)
 --cadsr               add/update caDSR elements (uses caDSR webservice)
 --verbose             emit warnings
 --schema-version [dotted version]
                       specify the schemaVersion attribute (default 0.1)
 --dict-version [dotted version]
                       specify the version attribute

if old_dict.xml is specified with --dictionary, then the input files
should be used to update old_dict.xml; otherwise, create a new
dictionary instance

if new_dict.xml is specified with --output, direct the output to this
file; otherwise, send output to stdout

=head1 DETAILS

Features: should be able to 
* accept a (list of) xsds and create or update dict entries
* accept a tab-delimited table of OC information and update dict entries

The dictionary is based on BCR xsd files. Schema files on the command
line are processed first and create new dictionary entry elements.
OpenClinica dump files are then parsed to update the entries already
created or existing. Finally, the caDSR webservice is called (if
enabled) to update caDSR-related elements.

The CDE id (public id) must be present in an item to be included in
the dictionary. Schema or OC items without a CDE are skipped (with a
warning, if --verbose is active).

OC items read from a dump file are skipped if the corresponding CDE is
not represented in the dictionary (with a warning, if --verbose is
active).

Guts
* map between OC data and xsds using the CDE public id; handle missing CDE ids how?

DD data elements and where they come from: 

dictionary 
  @id - local unique id
  @name - xsd : xs:element value of 'name' attr. with _ -> ' ' and capitalize all tokens
  @topic - xsd : short token from the xsd namespace
  @disease - xsd : short token from the xsd namespace (if disease specific)
  caDSRInfo
    @public_id - xsd : xs:attribute element with name="cde", value of 'default' attr.
    @xlink:href - call out caDSR svc (CDEBrowserLink)
    @xlink:type = "simple"
    caDSRlongName - call out to caDSR svc
    caDSRshortName -  call out to caDSR svc
    caDSRdefinition -  call out to caDSR svc
    caDSRlatestVersion -  call out to caDSR svc
    caDSRvalueDomainHref
      @xlink:href  - call out to caDSR svc
      @xlink:type = "simple"
  TCGAInfo 
    @xml_elt_name - xsd : xs:element element 'name' attribute
    @xml_elt_ns - xsd : xs:schema element 'targetNamespace' attribute
    @xml_tier_level - xsd : xs:attribute element with name="tier", value of 'default' attr.
    @xsd_intro_ver - xsd : xs:attribute element with name="xsd_ver", value of 'default' attr.
    @xsd_current_ver - xsd : 'version' attr. of the xs:schema element
    CRFquestionText - OC data
    CRFdataEltLabel - OC data
    CRFadditionalInstructions - OC data
    TCGAadditionalExplanation - handwritten
    CRFentryAlternatives - OC data

=cut
