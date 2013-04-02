#! /bin/perl -w


use strict;
use warnings;
use XML::Twig;
use Storable;

my @DATA;
my $SCHEMA_TAG;
my $TABLE = {};

my $twig = XML::Twig->new( twig_handlers => {
    'xs:schema' => sub { ($SCHEMA_TAG) = $_->att('targetNamespace') =~ m|http.*xml/([^0-9]*)(?:/2\.3.*\|$)| },
    'xs:element' => \&elt_process});

foreach my $f (@ARGV) {
    $twig->parsefile($f);
    $TABLE->{ $SCHEMA_TAG }  = [@DATA];
    undef @DATA;
    undef $SCHEMA_TAG;
    1;
}

store $TABLE, 'elt_type_hash';


sub elt_process {
    my $elt = $_;
    my %data;
    return unless ($data{name} = $_->att('name')); # skip if a ref
    my @exten = $_->get_xpath('.//xs:extension');
    my @restr = $_->get_xpath('.//xs:restriction');
    return if (!@exten && !@restr); # no type info
    if (@exten) {
	$data{type} = $exten[0]->att('base');
    }
    elsif (@restr) {
	$data{type} = $restr[0]->att('base');
	$data{value} = [];
	foreach my $r ($restr[0]->children('xs:enumeration')) {
	    push @{$data{value}}, $r->att('value');
	}
    }
    else {
	die "Shouldn't be here."
    }
    push @DATA, \%data;

    1;
}

=head1 NAME

elt_type.pl

=head1 SYNOPSIS

$ perl elt_type.pl *.xsd

...later...

perl -MStorable -e '$mytbl = retrieve("elt_type_hash")'

=head1 DESCRIPTION

Script to parse BCR XML Schemas in batch, creating a table of

=over

* clinical data element tags

* data types

* accepted values (if enumerated)

=back

to support creation of the dbGaP submission data dictionary.

The script parses the set of xsds provided on the command line, creating a
hash (ref) with the following structure:

{ <schema id tag> => [ { name => <elt name>,

		       type => <data type>,

		       value => <array of enum. string values> } ] }


=over

* <schema id tag> is the final two tokens (omitting the version number) of the
schema targetNamespace: e.g., 'clinical/brca' or 'clinical/shared'. This
is to aid parsing the hash into disease-specific dbGaP data dictionaries.

* <name> is the xml element name.

* <type> is the base type of the element, taken either from an xs:extension or
xs:restriction element

* <value> is an array of (string) values, taken from the value attributes in a
sequence of xs:enumeration elements (for xs:restrictions only).

=back

The hash is serialized to the file "elt_type_hash" using Storable.

=head1 AUTHOR

Mark A. Jensen, mark.jensen@nih.gov

=head1 COPYRIGHT

(c) 2011 SRA International, Inc.

=cut
