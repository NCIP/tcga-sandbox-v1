#$Id: Exceptions.pm 17684 2012-09-23 01:12:42Z jensenma $
package REST::Neo4p::Exceptions;
BEGIN {
  $REST::Neo4p::Exceptions::VERSION = '0.1';
}
use Exception::Class (
  'REST::Neo4p::Exception',
  'REST::Neo4p::LocalException' => {
    isa => 'REST::Neo4p::Exception',
    description => 'REST::Neo4p code-local error'
   },
  'REST::Neo4p::Neo4jException' => {
    isa => 'REST::Neo4p::Exception',
    fields => [ 'code', 'neo4j_message', 
		'neo4j_exception', 'neo4j_stacktrace' ],
    description => 'Neo4j server errors'
   },
  'REST::Neo4p::CommException' =>
    {
    isa => 'REST::Neo4p::Exception',
    fields => [ 'code' ],
    description => 'Network or HTTP errors'
   },
  'REST::Neo4p::NotFoundException' => {
    isa => 'REST::Neo4p::Neo4jException',
    fields => [ 'code', 'neo4j_message', 
		'neo4j_exception', 'neo4j_stacktrace' ],
    description => 'URL or item not found'
   },
  'REST::Neo4p::QuerySyntaxException' =>
    {
      isa => 'REST::Neo4p::Neo4jException',
      fields => [ 'code', 'neo4j_message', 
		  'neo4j_exception', 'neo4j_stacktrace' ],
      description => 'Cypher query language syntax error'
     },
  'REST::Neo4p::NotImplException' => {
    isa => 'REST::Neo4p::LocalException',
    description => 'Attempt to call an currently unimplemented method'
   },
  'REST::Neo4p::NotSuppException' => {
    isa => 'REST::Neo4p::LocalException',
    description => 'Attempt to call a non-supported inherited method'
   },
  'REST::Neo4p::ClassOnlyException' => {
    isa => 'REST::Neo4p::LocalException',
    message => 'This is a class method only',
    description => 'Attempt to call a class method from an instance'
   },
  'REST::Neo4p::QueryResponseException' => {
    isa => 'REST::Neo4p::LocalException',
    description => 'Problem parsing the response to a cypher query (prob. a bug)'
   }
   );

1;
