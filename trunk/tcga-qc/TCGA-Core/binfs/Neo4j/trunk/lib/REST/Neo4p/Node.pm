#$Id: Node.pm 17684 2012-09-23 01:12:42Z jensenma $
package REST::Neo4p::Node;
use REST::Neo4p::Relationship;
use REST::Neo4p::Exceptions;
use JSON;
use Carp qw(croak carp);
use base 'REST::Neo4p::Entity';
use strict;
use warnings;
BEGIN {
  $REST::Neo4p::Node::VERSION = '0.1';
}

# creation, deletion and property manipulation are delegated
# to the parent Entity.

# $node1->relate_to($node2, $relationship_type, \%reln_props);
# direction is from $node1 -> $node2
# return the Relationship object
sub relate_to {
  my $self = shift;
  my ($target_node, $rel_type, $rel_props) = @_;
  my $agent = $REST::Neo4p::AGENT;
  my $suffix = $self->_get_url_suffix('create_relationship')
    || 'relationships'; # weak workaround
  my $content = {
		 'to' => $target_node->_self_url,
		 'type' => $rel_type
		};
  if ($rel_props) {
    $content->{data} = $rel_props;
  }
  my $decoded_resp;
  eval {
    $decoded_resp = $agent->post_node([$$self,$suffix],
				      $content);
  };
  my $e;
  if ($e = Exception::Class->caught('REST::Neo4p::Exception')) {
    # TODO : handle different classes
    $e->rethrow;
  }
  elsif ($@) {
    ref $@ ? $@->rethrow : die $@;
  }
  return ref($decoded_resp) ? 
    REST::Neo4p::Relationship->new_from_json_response($decoded_resp) :
	REST::Neo4p::Relationship->new_from_batch_response($decoded_resp);
}

sub get_relationships {
  my $self = shift;
  my ($direction) = @_;
  $direction ||= 'all';
  my $agent = $REST::Neo4p::AGENT;
  my $action;
  for ($direction) {
    /^all$/ && do {
      $action = 'all_relationships';
      last;
    };
    /^in$/ && do {
      $action = 'incoming_relationships';
      last;
    };
    /^out$/ && do {
      $action = 'outgoing_relationships';
      last;
    };
    do { # huh?
      REST::Neo4p::LocalException->throw("Got '$direction' for relationship direction; expected [in|out|all]");
    };
  }
  my $decoded_resp;
  eval { 
    $decoded_resp = $agent->get_node($$self,$self->_get_url_suffix($action) );
  };
  my $e;
  if ($e = Exception::Class->caught('REST::Neo4p::Exception')) {
    # TODO : handle different classes
    $e->rethrow;
  }
  elsif ($@) {
    ref $@ ? $@->rethrow : die $@;
  }
  my @ret;
  if (ref $decoded_resp eq 'HASH') {
    $decoded_resp = [$decoded_resp];
  }
  for (@$decoded_resp) {
    push @ret, ref($_) ? 
      REST::Neo4p::Relationship->new_from_json_response($_) :
	  REST::Neo4p::Relationship->new_from_batch_response($_);
  }
  return @ret;
}

sub get_incoming_relationships { shift->get_relationships('in',@_) }
sub get_outgoing_relationships { shift->get_relationships('out',@_) }
sub get_all_relationships { shift->get_relationships('all',@_) }

sub get_typed_relationships {
  my $self = shift;
  REST::Neo4p::NotImplException->throw( 'not implemented yet' );
}

=head1 NAME

REST::Neo4p::Node - Neo4j node object

=head1 SYNOPSIS

 $n1 = REST::Neo4p::Node->new( {name => 'Ferb'} )
 $n2 = REST::Neo4p::Node->new( {name => 'Phineas'} );
 $n3 = REST::Neo4p::Node->new( {name => 'Perry'} );
 $n1->relate_to($n2, 'brother');
 $n3->relate_to($n1, 'pet');
 $n3->set_property({ species => 'Ornithorhynchus anatinus' });

=head1 DESCRIPTION

C<REST::Neo4p::Node> objects represent Neo4j nodes.

=head1 METHODS

=over

=item new()

 $node = REST::Neo4p::Node->new();
 $node_with_properties = Rest::Neo4p::Node( \%props );

Instantiates a new Node object and creates corresponding node in the database.

=item remove()

 $node->remove()

Removes a node from the database and destroys the object.

=item get_property()

 $name = $node->get_property('name');
 @vitals = $node->get_property( qw( height weight bp temp ) );

Get the values of properties on nodes and relationships.

=item set_property()

 $name = $node->set_property( {name => "Sun Tzu", occupation => "General"} );
 $node1->relate_to($node2,"is_pal_of")->set_property( {duration => 'old pal'} );

Sets values of properties on nodes and relationships.

=item get_properties()

 $props = $relationship->get_properties;
 print "'Sup, Al." if ($props->{name} eq 'Al');

Get all the properties of a node or relationship as a hashref.

=item relate_to()

 $relationship = $node1->relate_to($node2, 'manager');

Set a relationship between two nodes and returns the
L<REST::Neo4p::Relationship|REST::Neo4p::Relationship> thus
created. Call on the "from" node, first argument is the "to" node,
second argument is the relationship type.

=item get_relationships()

 @all_relationships = $node1->get_relationships()

Get all incoming and outgoing relationships of a node. Returns array
of L<REST::Neo4p::Relationship|REST::Neo4p::Relationship> objects;

=item get_incoming_relationships()

 @incoming_relationships = $node1->get_incoming_relationships();

=item get_outgoing_relationships()

 @outgoing_relationships = $node1->get_outgoing_relationships();

=item property auto-accessors

See L<REST::Neo4p/Property Auto-accessors>.

=back

=head1 SEE ALSO

L<REST::Neo4p>, L<REST::Neo4p::Relationship>, L<REST::Neo4p::Index>.

=head1 AUTHOR

    Mark A. Jensen
    CPAN ID: MAJENSEN
    TCGA DCC
    mark -dot- jensen -at- nih -dot- gov
    http://tcga-data.nci.nih.gov

=head1 LICENSE

Copyright (c) 2012 Mark A. Jensen. This program is free software; you
can redistribute it and/or modify it under the same terms as Perl
itself.

=cut


1;
