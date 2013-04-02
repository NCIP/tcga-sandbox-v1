#-*-perl-*-
#$Id: 006_query.t 17665 2012-09-12 04:01:50Z jensenma $
use Test::More qw(no_plan);
use Test::Exception;
use Module::Build;
use lib '../lib';
use strict;
use warnings;
no warnings qw(once);

use_ok('REST::Neo4p');

my $build;
eval {
  $build = Module::Build->current;
};

my $TEST_SERVER = $build ? $build->notes('test_server') : 'http://127.0.0.1:7474';
my $num_live_tests = 1;
my $not_connected;

eval {
  REST::Neo4p->connect($TEST_SERVER);
};
if ( my $e = REST::Neo4p::CommException->caught() ) {
  $not_connected = 1;
  diag "Test server unavailable : ".$e->message;
}

ok my $q = REST::Neo4p::Query->new('START n=node({node_id}) RETURN n',
				   { node_id => 1 }), 'create query object';
isa_ok $q, 'REST::Neo4p::Query';
$q->{RaiseError} = 1;
is $q->query, 'START n=node({node_id}) RETURN n','query accessor';
is_deeply $q->params, { node_id => 1}, 'params accessor';


SKIP : {
  skip 'no local connection to neo4j', $num_live_tests if $not_connected;

  ok my $n1 = REST::Neo4p::Node->new({name => 'Fred', role => 'husband'}), 'Fred';
  ok my $n2 = REST::Neo4p::Node->new({name => 'Wilma', role => 'wife'}), 'Wilma';
  ok my $n3 = REST::Neo4p::Node->new({name => 'Pebbles', role => 'daughter'}), 'Pebbles';
  ok my $n4 = REST::Neo4p::Node->new({name => 'Betty', role=>'neighbor'}), 'Betty';
  ok my $n5 = REST::Neo4p::Node->new({name => 'BamBam', role=>'son'}), 'BamBam';

  ok my $r1 = $n1->relate_to($n2, 'married_to');
  ok my $r2 = $n2->relate_to($n1, 'married_to');
  ok my $r3 = $n3->relate_to($n1, 'child_of');
  ok my $r4 = $n4->relate_to($n2, 'pal_of');
  ok my $r5 = $n5->relate_to($n4, 'child_of');
  ok my $r6 = $n1->relate_to($n3, 'parent_of');
  ok my $r7 = $n2->relate_to($n3, 'parent_of');
  ok my $r8 = $n4->relate_to($n5, 'parent_of');

  ok my $q = REST::Neo4p::Query->new("START n=node($$n1) MATCH (n)-->(x) RETURN x.name, x"), 'new node query';
 $q->{RaiseError} = 1;
  $DB::single =1;
  ok $q->execute, 'execute query';
  while (my $row = $q->fetch) {
    like $row->[0], qr/Wilma|Pebbles/, 'got name';
    isa_ok($row->[1], 'REST::Neo4p::Node');
  }
  
  ok $q = REST::Neo4p::Query->new("START n=node($$n4) MATCH (n)-[r]-(x) WHERE type(r) = 'pal_of' RETURN r, x.name"), 'new relationship query';
  is $q->execute, 1, 'execute and return 1 row';
  while (my $row = $q->fetchrow_arrayref) {
    isa_ok($row->[0], 'REST::Neo4p::Relationship');
    is $row->[1], 'Wilma', "Wilma is Betty's pal";
  }

  ok $q = REST::Neo4p::Query->new("START n=node($$n5), m=node($$n3) MATCH path = (n)-[:child_of]->()-[:pal_of]->()-[:parent_of]->(m)  RETURN path");
  is $q->execute, 1, 'execute and return 1 path';
  
  while (my $row = $q->fetch) {
      my $path = $row->[0];
      isa_ok $path, 'REST::Neo4p::Path';
      is scalar $path->nodes, 4, 'got all nodes';
      cmp_ok scalar $path->relationships,'>=', 3, 'got all relationships';
  }

  CLEANUP : {
      ok $_ && $_->remove, 'reln removed' for ($r1, $r2, $r3, $r4, $r5,$r6,$r7,$r8);
      ok $_ && $_->remove, 'node removed' for ($n1, $n2, $n3, $n4, $n5);
  }
  
}
