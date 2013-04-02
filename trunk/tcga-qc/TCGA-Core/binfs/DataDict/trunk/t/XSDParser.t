# -*-perl-*-
use strict;
use warnings;
use Test::More qw(no_plan);
use Test::Exception;

use lib '..';

use_ok('DataDict::XSDParser');
my $p;
ok ($p = DataDict::XSDParser->new(), 'object created');
isa_ok($p,'DataDict::XSDParser');

ok ( !$p->is_parsed, 'not yet parsed' );
dies_ok {$p->parse_schema_tag } 'cannot call internal method(1)';
dies_ok { $p->parse_element } 'cannot call internal method(2)';
dies_ok { $p->parse_xsd('narb.xsd') } 'bad file croaks';

$p->parse_xsd('t/samples/TCGA_BCR.Biospecimen.xsd');
is($p->num_elts + scalar($p->orphans), 115, 'number of elts parsed correct');
is($p->topic, 'biospecimen', 'topic correct');
is($p->xml_elt_ns, "http://tcga.nci/bcr/xml/biospecimen", 'namespace correct');
is($p->xsd_current_ver, "2.3.0", 'xsd version correct');
my $elt;
while ( $elt = $p->next_elt ) {
    last if $elt->xml_elt_name eq 'spectrophotometer_method';
}
is($elt->public_id, 3008378, 'cde for spectrophotometer_method');
is($elt->cde, 3008378, 'cde alias');
is($elt->xsd_intro_ver,2.3, 'intro version for spectrophotometer_method');
is($elt->xml_tier_level,2,'tier level for spectrophotometer_method');
ok $p->reset_elts;
$elt = $p->next_elt;
is($elt->xml_elt_name,'sample_type','reset elts works');
1;
