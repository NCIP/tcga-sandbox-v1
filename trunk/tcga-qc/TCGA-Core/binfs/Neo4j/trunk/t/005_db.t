#-*-perl-*-
#$Id: 005_db.t 17653 2012-09-05 04:54:27Z jensenma $
use Test::More tests => 34;
use Test::Exception;
use Module::Build;
use lib '../lib';
use strict;
use warnings;
no warnings qw(once);

my $build;
eval {
    $build = Module::Build->current;
};
my $TEST_SERVER = $build ? $build->notes('test_server') : 'http://127.0.0.1:7474';
my $num_live_tests = 33;

use_ok('REST::Neo4p');

my $not_connected;
eval {
  REST::Neo4p->connect($TEST_SERVER);
};
if ( my $e = REST::Neo4p::CommException->caught() ) {
  $not_connected = 1;
  diag "Test server unavailable : ".$e->message;
}
SKIP : {
  skip 'no local connection to neo4j', $num_live_tests if $not_connected;
  ok my $n1 = REST::Neo4p::Node->new(), 'node 1';
  ok my $n2 = REST::Neo4p::Node->new(), 'node 2';
  ok my $r12 = $n1->relate_to($n2, "bubba"), 'relationship 1->2';
  ok my $n3 = REST::Neo4p->get_node_by_id($$n1), 'got node by id';
  is $$n3, $$n1, 'same node';
  ok my $r = REST::Neo4p->get_relationship_by_id($$r12), 'got relationship by id';
  is $$r, $$r12, 'same relationship';
  ok my @rtypes = REST::Neo4p->get_relationship_types, 'get relationship type list';
  ok grep(/bubba/,@rtypes), 'found relationship type in type list';

  ok my $node_idx = REST::Neo4p::Index->new('node', 'node_idx'), 'new node index';
  ok my $reln_idx = REST::Neo4p::Index->new('relationship', 'reln_idx'), 'new relationship index';
  ok my @idxs = REST::Neo4p->get_indexes('node'), 'get node indexes';
  is $idxs[0]->type, 'node', 'got a node index';
  ok @idxs = REST::Neo4p->get_indexes('relationship'), 'get relationship indexes';

  ok $node_idx->add_entry($n1, 'node' => 1), 'add node entry';
  ok $node_idx->add_entry($n2, 'node' => 2), 'add node entry';
  ok $reln_idx->add_entry($r12, 'reln' => 'bubba'), 'add reln entry';

  # test finding nodes, relns, idxs from scratch (no entry in ENTITY_TABLE)
  delete $REST::Neo4p::Entity::ENTITY_TABLE->{node}{$$n1};
  delete $REST::Neo4p::Entity::ENTITY_TABLE->{relationship}{$$r12};
  delete $REST::Neo4p::Entity::ENTITY_TABLE->{index}{$$node_idx};
  ok !defined $n1->_entry, 'node 1 gone from ENTITY_TABLE';
  ok !defined $r12->_entry, 'relationship 12 gone from ENTITY_TABLE';
  ok !defined $node_idx->_entry, 'node index gone from ENTITY_TABLE';

  ok my $N = REST::Neo4p->get_node_by_id($$n1), 'restore node 1 from db';
  ok my $R = REST::Neo4p->get_relationship_by_id($$r12), 'restore relationship 12 from db';
  ok my $I = REST::Neo4p->get_index_by_name($$node_idx, 'node'), 'restore node index from db';

  is $$N, $$n1, 'got node 1 back';
  is $$R, $$r12, 'got relationship 12 back';
  is $$I, $$node_idx, 'got node index back';
  is ${($I->find_entries('node' => 1))[0]}, $$n1, 'resurrected index works';


  

  CLEANUP : {
      ok $r12->remove, 'remove relationship';
      throws_ok {REST::Neo4p->get_relationship_by_id($$r12)} 'REST::Neo4p::Exception', 'relationship is gone';
      ok $n1->remove, 'remove node';
      ok $n2->remove, 'remove node';
      ok $node_idx->remove, 'remove node index';
      ok $reln_idx->remove, 'remove relationship index';
  }
}
