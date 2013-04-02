#$Id: Agent.pm 17684 2012-09-23 01:12:42Z jensenma $
package REST::Neo4p::Agent;
use base LWP::UserAgent;
use REST::Neo4p::Exceptions;
use File::Temp qw(tempfile);
use JSON;
use Carp qw(croak carp);
use strict;
use warnings;

BEGIN {
  $REST::Neo4p::Agent::VERSION = '0.126';
}

our $AUTOLOAD;
our $JOB_CHUNK = 1024;
our $JSON = JSON->new()->allow_nonref(1);

sub new {
  my $class = shift;
  my $self = $class->SUPER::new(@_);
  $self->default_header( 'Accept' => 'application/json' );
  $self->default_header( 'Content-Type' => 'application/json' );
  $self->default_header( 'X-Stream' => 'true' );
  $self->protocols_allowed( ['http','https'] );
  bless $self, $class;
}

sub server {
  my $self = shift;
  $self->{__server} = shift if @_;
  return $self->{__server};
}

sub batch_mode {
  my $self = shift;
  $self->{__batch_mode} = shift if @_;
  return $self->{__batch_mode};
}

sub batch_length{ 
  my $self = shift;
  REST::Neo4p::LocalException->throw("Agent not in batch mode") unless $self->batch_mode;
  $self->{__batch_length}
}

sub connect {
  my $self = shift;
  my ($server) = @_;
  $self->{__server} = $server if defined $server;
  unless ($self->server) {
    REST::Neo4p::Exception->throw(message => 'Server not set');
   }
  my $resp = $self->get($self->server);
  unless ($resp->is_success) {
    REST::Neo4p::CommException->throw( code => $resp->code,
				       message => $resp->message );
  }
  my $json =  $JSON->decode($resp->content);
  # add the discovered URLs to the object hash, keyed by 
  # underscore + <function_name>:
  foreach (keys %{$json}) {
    next if /^extensions$/;
    # strip any trailing slash
    $json->{$_} =~ s|/+$||;
    $self->{_actions}{$_} = $json->{$_};
  }
  $resp = $self->get($self->{_actions}{data});
  unless ($resp->is_success) {
    REST::Neo4p::CommException->throw( code => $resp->code,
				       message => $resp->message." (connect phase 2)" );
  }
  $json = $JSON->decode($resp->content);
  foreach (keys %{$json}) {
    next if /^extensions$/;
    $self->{_actions}{$_} = $json->{$_};
  }
  # fix for incomplete discovery (relationship endpoint)
  unless ($json->{relationship}) {
    $self->{_actions}{relationship} = $self->{_actions}{node};
    $self->{_actions}{relationship} =~ s/node/relationship/;
  }

  return 1;
}

# _add_to_batch_queue
# takes a request and converts to a Neo4j REST batch-friendly
# hash
# VERY internal and experimental
# $url : rest endpoint that would be called ordinarily
# $rq : [get|delete|post|put]
# $content : hashref of rq content (post and put)
# $headers : hashref of additional headers
sub _add_to_batch_queue {
  my $self = shift;
  my ($url, $rq, $content, $headers) = @_;
  my $data = $self->data;
  $url =~ s|$data||; # get suffix
  my $id = ++($self->{__batch_length}||=$self->{__batch_length});
  my $job = { 
      method => uc $rq,
      to => $url,
      id => $id
     };
  $job->{body} = $content if defined $content;
  push @{$self->{__batch_queue}}, $job;
  return "{$id}"; # Neo4j batch reference for this job
}

sub execute_batch {
  my $self = shift;
  my ($chunk_size) = @_;
  unless ($self->batch_mode) {
    REST::Neo4p::LocalException->throw("Agent not in batch mode; can't execute batch");
  }
  return unless ($self->batch_length);
  my ($tfh, $tfname) = tempfile;
  $self->batch_mode(0);
  my @chunk;
  if ($chunk_size) {
    @chunk = splice @{$self->{__batch_queue}}, 0, $chunk_size;
    $self->{__batch_length} -= @chunk;
  }
  else {
    @chunk = @{$self->{__batch_queue}};
    undef $self->{__batch_queue};
    $self->{__batch_length} = 0;
  }
  $self->post_batch([],\@chunk, {':content_file' => $tfname});
  $self->batch_mode(1);
  return $tfname;
}

sub execute_batch_chunk { shift->execute_batch($JOB_CHUNK) }

# contains a reference to the returned content, as decoded by JSON
sub decoded_content { shift->{_decoded_content} }
# contains the url representation of the node returned in the Location:
# header
sub location { shift->{_location} }

sub available_actions { keys %{shift->{_actions}} }

sub no_stream {
  my $self = shift;
  my $default_headers = $self->default_headers;
  $default_headers->remove_header('X-Stream');
  $self->default_headers($default_headers);
}

sub stream {
  my $self = shift;
  my $default_headers = $self->default_headers;
  unless ($default_headers->header('X-Stream')) {
    $default_headers->header('X-Stream' => 'true');
  }
  $self->default_headers($default_headers);
}

# autoload getters for discovered neo4j rest urls

sub AUTOLOAD {
  my $self = shift;
  my $method = $AUTOLOAD;
  $method =~ s/.*:://;
  my ($rq, $action) = $method =~ /^(get_|post_|put_|delete_)*(.*)$/;
  unless (grep /^$action$/,keys %{$self->{_actions}}) {
    REST::Neo4p::LocalException->throw( __PACKAGE__." does not define method '$method'" );
  }
  return $self->{_actions}{$action} unless $rq;
  $rq =~ s/_$//;
  # reset
  $self->{_errmsg} = $self->{_location} = $self->{_decoded_content} = undef;
  for ($rq) {
    /get|delete/ && do {
      my @url_components = @_;
      my %rest_params = ();
      # look for a hashref as final arg containing field => value pairs
      if (@url_components && ref $url_components[-1] && (ref $url_components[-1] eq 'HASH')) {
	%rest_params = %{ pop @url_components };
      }
      my $url = join('/',$self->{_actions}{$action},@url_components);
      if ($self->batch_mode) {
	$url = ($url_components[0] =~ /{[0-9]+}/) ? $url_components[0] : $url; # index batch object kludge
	@_ = ($self, 
	      $url,
	      $rq);
	goto &_add_to_batch_queue; # short circuit to _add_to_batch_queue
      }
      my $resp = $self->$rq($url,%rest_params);
      eval { 
	$self->{_decoded_content} = $resp->content ? $JSON->decode($resp->content) : {};
      };
      unless ($resp->is_success) {
	if ( $self->{_decoded_content} ) {
	  my $xclass = ($resp->code == 404) ? 'REST::Neo4p::NotFoundException' : 'REST::Neo4p::Neo4jException';
	  $xclass->throw( 
	    code => $resp->code,
	    neo4j_message => $self->{_decoded_content}->{message},
	    neo4j_exception => $self->{_decoded_content}->{exception},
	    neo4j_stacktrace =>  $self->{_decoded_content}->{stacktrace}
	      );
	}
	else {
	  my $xclass = ($resp->code == 404) ? 'REST::Neo4p::NotFoundException' : 'REST::Neo4p::CommException';
	  $xclass->throw( 
	    code => $resp->code,
	    message => $resp->message
	   );
	}
      }
      $self->{_location} = $resp->header('Location');
      last;
    };
    /post|put/ && do {
      my ($url_components, $content, $addl_headers) = @_;
      unless (!$addl_headers || (ref $addl_headers eq 'HASH')) {
	REST::Neo4p::LocalException->throw('Arg 3 must be a hashref of additional headers');
      }
      my $url = join('/',$self->{_actions}{$action},@$url_components);
      if ($self->batch_mode) {
	$url = ($url_components->[0] =~ /{[0-9]+}/) ? join('/',@$url_components) : $url; # index batch object kludge
	@_ = ($self, 
	      $url,
	      $rq, $content, $addl_headers);
	goto &_add_to_batch_queue;
      }
      $content = $JSON->encode($content) if $content;
      my $resp  = $self->$rq($url, 'Content-Type' => 'application/json', Content=> $content, %$addl_headers);
      $self->{_decoded_content} = $resp->content ? $JSON->decode($resp->content) : {};
      unless ($resp->is_success) {
	if ( $self->{_decoded_content} ) {
	  my %error_fields = (
	    code => $resp->code,
	    neo4j_message => $self->{_decoded_content}->{message},
	    neo4j_exception => $self->{_decoded_content}->{exception},
	    neo4j_stacktrace =>  $self->{_decoded_content}->{stacktrace}
	   );
	  my $xclass = 'REST::Neo4p::Neo4jException';
	  if ($resp->code == 404) {
	    $xclass = 'REST::Neo4p::NotFoundException';
	  }
	  if ( $error_fields{neo4j_exception} =~ /^Syntax/ ) {
	    $xclass = 'REST::Neo4p::QuerySyntaxException';
	  }
	  $xclass->throw(%error_fields);
	}
	else {
	  my $xclass = ($resp->code == 404) ? 'REST::Neo4p::NotFoundException' : 'REST::Neo4p::CommException';
	  $xclass->throw( 
	    code => $resp->code,
	    message => $resp->message
	   );
	}
      }
      $self->{_location} = $resp->header('Location');
      last;
    };
    do { # fallthru
      croak "I shouldn't be here";
    };
  }
  return $self->{_decoded_content};
}

sub DESTROY {}

=head1 NAME

REST::Neo4p::Agent - LWP client interacting with Neo4j

=head1 SYNOPSIS

 $agent = REST::Neo4p::Agent->new();
 $agent->server('http://127.0.0.1:7474');
 unless ($agent->connect) {
  print STDERR "Didn't find the server\n";
 }

See examples under L</METHODS> below.

=head1 DESCRIPTION

The agent's job is to encapsulate and connect to the REST service URLs
of a running neo4j server. It also stores the discovered URLs for
various actions.  and provides those URLs as getters from the agent
object. The getter names are the keys in the JSON objects returned by
the server. See
L<http://docs.neo4j.org/chunked/milestone/rest-api.html> for more
details.

API and HTTP errors are distinguished and thrown by
L<Exception::Class|Exception::Class> subclasses. See
L<REST::Neo4p::Exceptions>.

C<REST::Neo4p::Agent> is a subclass of L<LWP::UserAgent|LWP::UserAgent>
and inherits its capabilities.

According to the Neo4j recommendation, the agent requests streamed
responses by default (i.e.,

 X-Stream:true

is a default header. This can be removed by calling

 $agent->no_stream

and added back with 

 $agent->stream

For batch API experimental features, see L</Batch Mode>.

=head1 METHODS

=over

=item new()

 $agent = REST::Neo4p::Agent->new();
 $agent = REST::Neo4p::Agent->new("http://127.0.0.1:7474");

Returns a new agent. Accepts optional server address arg.

=item server()

 $agent->server("http://127.0.0.1:7474");

Sets the server address and port.

=item data()

 $neo4j_data_url = $agent->data();

Returns the base of the Neo4j server API.

=item admin()

 $neo4j_admin_url = $agent->admin();

Returns the Neo4j server admin url.

=item node()

=item reference_node()

=item node_index()

=item relationship_index()

=item extensions_info

=item relationship_types()

=item batch()

=item cypher()

 $relationship_type_url = $agent->relationship_types;

These methods get the REST URL for the named API actions. Other named
actions may also be available for a given server; these are
auto-loaded from self-discovery responses provided by Neo4j. Use
C<available_actions()> to identify them.

You will probably prefer using the L</get_{action}()>,
L</put_{action}()>, L</post_{action}()>, and L</delete_{action}()>
methods to make requests directly.

=item neo4j_version()

 $version = $agent->neo4j_version;

Returns the version of the connected Neo4j server.

=item available_actions()

 @actions = $agent->available_actions();

Returns all discovered actions.

=item location()

 $agent->post_node(); # create new node
 $new_node_url = $agent->location;

Returns the value of the "location" key in the response JSON. 

=item get_{action}()

 $decoded_response = $agent->get_data(@url_components,\%rest_params)
 $types_array_ref = $agent->get_relationship_types();

Makes a GET request to the REST endpoint mapped to {action}. Arguments
are additional URL components (without slashes). If the final argument
is a hashref, it will be sent as key-value form parameters.

=item put_{action}()

 # add a property to an existing node
 $agent->put_node([13, 'properties'], { name => 'Herman' });

Makes a PUT request to the REST endpoint mapped to {action}. The first
argument, if present, must be an array B<reference> of additional URL
components. The second argument, if present, is a hashref that will be
sent in the request as (encoded) JSON content. The third argument, if 
present, is a hashref containing additional request headers.

=item post_{action}()

 # create a new node with given properties
 $agent->post_node({ name => 'Wanda' });
 # do a cypher query and save content to file
 $agent->post_cypher([], { query => 'START n=node(*) RETURN n', params=>{}},
                     { ':content_file' => $my_file_name });

Makes a POST request to the REST endpoint mapped to {action}. The first
argument, if present, must be an array B<reference> of additional URL
components. The second argument, if present, is a hashref that will be
sent in the request as (encoded) JSON content. The third argument, if 
present, is a hashref containing additional request headers.

=item delete_{action}()

  $agent->delete_node(13);
  $agent->delete_node_index('myindex');

Makes a DELETE request to the REST endpoint mapped to {action}. Arguments
are additional URL components (without slashes). If the final argument
is a hashref, it will be sent in the request as (encoded) JSON content.

=item decoded_response()

 $decoded_json = $agent->decoded_response;

Returns the response content of the last agent request, as decoded by
L<JSON|JSON>. It is generally a reference, but can be a scalar if a
bareword was returned by the server.

=item no_stream()

 $agent->no_stream;

Removes C<X-Stream: true> from the default headers.

=item stream()

 $agent->stream;

Adds C<X-Stream: true> to the default headers.

=back

=head1 Batch Mode

When the agent is in batch mode, the usual request calls are not
executed immediately, but added to a queue. The L</execute_batch()>
method sends the queued calls in the format required by the Neo4p REST
API (using the C<post_batch> method outside of batch
mode). L</execute_batch()> returns the decoded json server response in
the return format specified by the Neo4p REST batch API.

=over

=item batch_mode()

 print ($agent->batch_mode ? "I am " : "I am not ")." in batch mode\n";
 $agent->batch_mode(1);

Set/get current agent mode.

=item batch_length()

 if ($agent->batch_length() > $JOB_LIMIT) {
   print "Queue getting long; better execute\n"
 }

Returns current queue length. Throws C<REST::Neo4p::LocalException> if 
agent not in batch mode.

=item execute_batch()

 $tempfile_name = $agent->execute_batch();

 while (my $tmpf = $agent->execute_batch(50)) {
   # handle responses
 }

Processes the queued calls and returns the decoded json response from
server in a temporary file. Returns with undef if batch length is zero.
Throws C<REST::Neo4p::LocalException> if not in batch mode.

Second form takes an integer argument; this will submit the next [integer]
jobs and return the server response in the tempfile. The batch length is
updated.

You are responsible for unlinking the tempfile.

=item execute_batch_chunk()

 while (my $tmpf = $agent->execute_batch_chunk ) {
  # handle response
 }

Convenience form of
C<execute_batch($REST::Neo4p::JOB_CHUNK)>. C<$REST::Neo4p::JOB_CHUNK>
has default value of 1024.

=back

=head1 AUTHOR

    Mark A. Jensen
    CPAN ID: MAJENSEN
    majensen -at- cpan -dot- org

=head1 LICENSE

Copyright (c) 2012 Mark A. Jensen. This program is free software; you
can redistribute it and/or modify it under the same terms as Perl
itself.

=cut

1;
