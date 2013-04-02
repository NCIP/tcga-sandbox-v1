#-*-perl-*-
#$Id: 099_error.t 17684 2012-09-23 01:12:42Z jensenma $
use Test::More qw(no_plan);
use Test::Exception;
use Module::Build;
use lib '../lib';
use REST::Neo4p;
use strict;
use warnings;
no warnings qw(once);

my $build;
eval {
  $build = Module::Build->current;
};

my $TEST_SERVER = $build ? $build->notes('test_server') : 'http://127.0.0.1:7474';
my $num_live_tests = 10;

throws_ok { REST::Neo4p->get_indexes } 'REST::Neo4p::CommException', 'not connected ok';
like $@->message, qr/not connected/i, 'not connected ok (2)';

throws_ok { REST::Neo4p::Entity->new() } 'REST::Neo4p::NotSuppException', 'attempt to instantiate Entity ok';

throws_ok { REST::Neo4p->connect('http://127.0.0.1:9999') } 'REST::Neo4p::CommException', 'bad address ok';

throws_ok { REST::Neo4p->get_indexes() } 'REST::Neo4p::LocalException', 'No type arg on get_indexes ok';

my $not_connected;
eval {
  REST::Neo4p->connect($TEST_SERVER);
};
if ( my $e = REST::Neo4p::CommException->caught() ) {
  $not_connected = 1;
  diag "Test server unavailable : ".$e->message;
}

SKIP : {
    skip 'no connection to neo4j',$num_live_tests if $not_connected;
    my $n1 = REST::Neo4p::Node->new();
    throws_ok { $n1->set_property('boog') } 'REST::Neo4p::LocalException', 'bad set_property arg ok';
    my $agent = $REST::Neo4p::AGENT;
    throws_ok { $agent->get_frelb } 'REST::Neo4p::LocalException', 'bad action ok';
    throws_ok { $agent->get_data('frelb') } 'REST::Neo4p::NotFoundException', 'bad url ok';
    is $@->code, 404, '404 ok';
    my $q = REST::Neo4p::Query->new("Start n=frleb RETUN q");
    $q->{RaiseError} = 1;
    throws_ok { $q->execute } 'REST::Neo4p::QuerySyntaxException', 'bad query syntax ok';
    $q->{RaiseError} = 0;
    lives_ok { $q->execute } 'no throw with RaiseError cleared';
    is $q->err, 400, 'but err code captured in err()';
    isa_ok $q->{_error},'REST::Neo4p::QuerySyntaxException';
    ok my $i = REST::Neo4p::Index->new('node','zzyxx'), 'create index';
    throws_ok { $i->get_property('foo') } 'REST::Neo4p::NotSuppException', 'not supported ok';
    throws_ok { $i->set_property(foo => 'bar') } 'REST::Neo4p::NotSuppException', 'not supported ok (2)';
    throws_ok { $i->get_properties } 'REST::Neo4p::NotSuppException', 'not supported ok (3)';
    throws_ok { REST::Neo4p->get_node_by_id(-1) } 'REST::Neo4p::NotFoundException', 'get bad node id ok';
    throws_ok { REST::Neo4p->get_relationship_by_id(-1) } 'REST::Neo4p::NotFoundException', 'get bad relationship id ok';
    ok $n1->remove, 'remove node';
    ok $i->remove, 'remove index';
}
