# -*-perl-*-
#$Id: Entry.t 9620 2011-01-31 03:27:04Z jensenma $
use strict;
use warnings;
use Test::More tests => 43;
use Test::Exception;
use Test::Warn;
use IO::String;

use lib '../lib';

use_ok('DataDict::Dictionary::Entry');
my $e;
ok ($e = DataDict::Dictionary::Entry->new(), 'object created');
isa_ok($e,'DataDict::Dictionary::Entry');

my ($s,$t);
my $f = IO::String->new($s);
my $g = IO::String->new($t);
$e->print($f);
$e->twig_elt->print($g);
is( $s, $t, 'delegate print');

isa_ok( $e->twig_elt('caDSRInfo'), 'XML::Twig::Elt');
ok( !$e->twig_elt('narb') );

ok( $e->id(3), 'set id attribute');
is( $e->id, 3, 'get id attribute');

ok( $e->public_id(64171), 'set public_id attribute' );
is( $e->public_id, 64171, 'get public_id attribute' );
is( $e->cde, 64171, 'check cde also set');

ok( $e->caDSRvalueDomainHref('http://tcga.cancer.gov'), 'set caDSRvalueDomainHref');
is( $e->caDSRvalueDomainHref, 'http://tcga.cancer.gov', 'get caDSRvalueDomainHref');
is( $e->twig_elt('caDSRvalueDomainHref')->att('xlink:href'), 'http://tcga.cancer.gov', 'caDSRvalueDomainHref in correct location' );

ok( $e->CDEBrowserLink('http://tcga.cancer.gov'), 'set CDEBrowserLink');
is( $e->CDEBrowserLink, 'http://tcga.cancer.gov', 'get CDEBrowserLink');
is( $e->twig_elt('caDSRInfo')->att('xlink:href'), 'http://tcga.cancer.gov', 'CDEBrowserLink in correct location' );

is( $e->twig_elt->att('cde'), 64171, 'cde in correct location');
is( $e->twig_elt('caDSRInfo')->att('public_id'), 64171, 'public_id in correct location');

my $x = { xml_elt_ns => 'a', xml_elt_name => 'b', xml_tier_level => 'c',
	  xsd_current_ver => 'goob', xsd_intro_ver => 'e' };
my $y = { xml_elt_ns => '1', xml_elt_name => '2', xml_tier_level => '3',
	  xsd_current_ver => 'goob', xsd_intro_ver => '5' };

ok $e->TCGAxmlElts($x), 'add xml elt info (1)';
ok $e->TCGAxmlElts($y), 'add xml elt info (2)';
ok my @z = $e->TCGAxmlElts, 'get xml elt info';
is $z[0]->{xml_elt_ns}, 'a', 'info check (1)';
is $z[0]->{xsd_intro_ver}, 'e', 'info check(2)';
is $z[1]->{xml_elt_name}, 2, 'info check (3)';
is $z[1]->{xml_tier_level}, 3, 'info check (4)';
is $z[0]->{xsd_current_ver}, $z[1]->{xsd_current_ver}, 'info check (5)';

ok $e->studies('boog');
ok $e->studies('goob');
is_deeply( [$e->studies], ['boog','goob'], 'studies elt works');

ok $e->tags('boog');
ok $e->tags('goob');
is_deeply( [$e->tags], ['boog','goob'], 'tags elt works');



warning_like { $e->name('boog') } qr/Entry name is defined by a convention/;
is $e->name, 'B', "naming convention works";

my $pretwig = '<dictEntry id="3"><caDSRInfo public_id="64171" xlink:href="http://tcga.cancer.gov"><caDSRlongName/><caDSRdefinition/><caDSRlatestVersion/><caDSRvalueDomainHref xlink:href="http://tcga.cancer.gov" xlink:type="simple"/></caDSRInfo><TCGAInfo><CRFInfo/><CRFquestionText/><TCGAadditionalExplanation/><CRFentryAlternatives/></TCGAInfo></dictEntry>';

$pretwig = XML::Twig->new()->parse($pretwig)->root;

ok ( !scalar $pretwig->get_xpath(".//caDSRshortName"), 'caDSRshortName not there');
ok ( !scalar $pretwig->get_xpath(".//CRFdataEltLabel"), 'CRFdataEltLabel not there');
ok ( !scalar $pretwig->get_xpath(".//CRFadditionalInstructions"), 'CRFadditionalInstructions not there');

ok my $pretwig_e = DataDict::Dictionary::Entry->new($pretwig), 'create entry from twig arg, missing caDSRshortName, CRFdataEltLabel, CRFadditionalInstructions';
ok ( scalar $pretwig_e->twig_elt->get_xpath(".//caDSRshortName"), 'caDSRshortName there now');
ok ( scalar $pretwig_e->twig_elt->get_xpath(".//CRFdataEltLabel"), 'CRFdataEltLabel there now');
ok ( scalar $pretwig_e->twig_elt->get_xpath(".//CRFadditionalInstructions"), 'CRFadditionalInstructions there now');

dies_ok { $e->narb } 'unknown method fails';
1;
