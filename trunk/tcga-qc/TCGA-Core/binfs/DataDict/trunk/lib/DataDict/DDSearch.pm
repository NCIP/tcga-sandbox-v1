package DataDict::DDSearch;
use strict;
use warnings;

use XML::Twig;
use Scalar::Util qw(looks_like_number);

sub new {
    my $class = shift;
    bless { _twig => XML::Twig->new() }, $class;
}

sub load_dict {
    my $self = shift;
    my $dict_file = shift;
    return unless -e $dict_file;
    $self->_twig->parsefile($dict_file);
    return 1;
}

# get_best_definition: 
# args -- array of element names and/or cde ids
# use same algorithm as dd.xsl: if TCGAadditionalExplanation is
# present, return this definition. If not, then use the
# shortest of caDSRdefinition, caDSRalternateDefinition and 
# CRFcaBIGdefinition

sub get_best_definition {
    my $self = shift;
    my @queries = @_;
    my $t = $self->_twig;
    my $defn;
    my %ret;
    return unless @queries;
    foreach my $q (@queries) {
	my $elt = (looks_like_number($q) ? $self->_get_entry($q) :
		   $self->_get_entry_by_elt_name($q));
	unless ($elt) {
	    $ret{$q} = undef;
	    next;
	}
	my ($TCGAadditionalExplanation, 
	    $caDSRdefinition,
	    $caDSRalternateDefinition,
	    $CRFcaBIGdefinition) = 
		( $elt->first_descendant('TCGAadditionalExplanation'),
		  $elt->first_descendant('caDSRdefinition'),
		  $elt->first_descendant('caDSRalternateDefinition'),
		  $elt->first_descendant('CRFcaBIGdefinition') );
	$TCGAadditionalExplanation &&= $TCGAadditionalExplanation->text;
	$caDSRdefinition &&= $caDSRdefinition->text;
	$caDSRalternateDefinition &&= $caDSRalternateDefinition->text;
	$CRFcaBIGdefinition &&= $CRFcaBIGdefinition->text;
	
	if (length($TCGAadditionalExplanation || '')) {
	    $ret{$q} = $TCGAadditionalExplanation;
	}
	else {
	    $ret{$q} = shortest_string($caDSRdefinition,$caDSRalternateDefinition,$CRFcaBIGdefinition);
	}
    }
    
    return (@queries == 1 ? $ret{shift(@queries)} : \%ret);
    
}

sub get_cde_by_elt_name {
    my $self = shift;
    my $elt_name = shift;
    return unless $elt_name;
    my $cde = $self->{_elt_name_to_cde_memo}->{$elt_name};
    return $cde if $cde;
    my $entry = $self->_get_entry_by_elt_name($elt_name);
    return unless $entry;
    return $self->{_elt_name_to_cde_memo}->{$elt_name} = $entry->att('cde');
}

# find a single entry twig based on  cde id
sub _get_entry {
    my $self = shift;
    my $cde = shift;
    return unless (defined $cde && looks_like_number($cde));
    my @ret = $self->_twig->get_xpath("//dictEntry[\@cde=$cde]");
    return $ret[0];
}

# find a single entry twig using XML element tag name
sub _get_entry_by_elt_name {
    my $self = shift;
    my $elt_name = shift;
    return unless $elt_name;
    if (!$self->{_xml_elt_info_array}) {
	$self->{_xml_elt_info_array} =
	    [$self->_twig->get_xpath("//XMLeltInfo")];
    }
    # brute force search
    my $ret;
    for (@{$self->{_xml_elt_info_array}}) {
	$ret = $_ if $elt_name eq $_->att('xml_elt_name');
	last if $ret;
    }
    return unless $ret;
    return $ret->parent('dictEntry');
}

sub _twig {
    my $self = shift;
    $self->{_twig} = shift if @_;
    return $self->{_twig}
}

# return the shortest string out of all DEFINED strings in an array
sub shortest_string {
    my @strings = grep {defined} @_;
    return unless @strings;
    return (sort {length($a) <=> length($b)} @strings)[0];
}

=head1 NAME

DDSearch - useful search functions against the DCC Data Dictionary

=head1 SYNOPSIS

 $dict = DDSearch->new()
 $dict->load_dict('mydd.xml');

 $defn = $dict->get_best_definition('time_between_excision_and_freezing');
 $defn = $dict->get_best_definition(2783887);
 $hash = $dict->get_best_definition(2783887,'time_between_excision_and_freezing');
 printf "Definition (2783887) : %s\n", $hash->{2783887};

=head1 DESCRIPTION

=head1 AUTHOR

Mark A. Jensen, mark.jensen@nih.gov

=head COPYRIGHT

(c)2011 SRA International, Inc.

=cut

    1;
