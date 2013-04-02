#-*-perl-*-
use lib '../lib';
use Test::More qw(no_plan);
use File::Spec;
use JSON;
use strict;
use warnings;
use TCGA::BCFactory;
use TCGA::Barcode;

my $loc = -d 'samples' ? 'samples' : File::Spec->catdir('t','samples');
my $NETWORK_TESTS_ENABLED = 1;
use_ok('TCGA::Barcode::ws');
use_ok('TCGA::ThrottledUserAgent');

my $ua = TCGA::ThrottledUserAgent->new(wait => 50, agent=>"TCGA::Barcodes/0.1" );
isa_ok $ua, 'TCGA::ThrottledUserAgent';
isa_ok $ua, 'LWP::UserAgent';
is $ua->wait, 50, 'wait us set';
like $ua->agent, qr/TCGA::Barcodes/, 'agent also set';

my $fac = TCGA::BCFactory->new('ws');
isa_ok $fac, 'TCGA::BCFactory';
open my $f, File::Spec->catfile($loc,'json.txt') or die $!;

# my $bc = TCGA::Barcode->new('1add38c2-df6f-47c9-9373-0bec0e40baf8', $fac);
my $bc = $fac->get('1add38c2-df6f-47c9-9373-0bec0e40baf8');
isa_ok $bc, 'TCGA::Barcode';
isa_ok $bc, 'TCGA::Barcode::ws';

local $/ = undef;
my $j = <$f>;
my $j_hash = decode_json($j);

# test the parser
ok $bc->_parse_json($j_hash);
is $bc->uuid, '1add38c2-df6f-47c9-9373-0bec0e40baf8', 'json parse uuid correct';
is $bc->barcode, 'TCGA-AA-3851', 'json parse barcode correct';
is $bc->item, 'participant', 'json parse item correct';
is $bc->bcr, 'IGC', 'json parse bcr correct';
is $bc->tss, 'AA', 'json parse tss correct';
is $bc->disease, 'COAD', 'json parse disease correct';
is $bc->batch, 41, 'json parse batch correct';
is $bc->plate, 'NA', 'json parse plate correct (NA)';
is $bc->center, 'NA', 'json parse center correct (NA)';

# test the ws
my $num_network_tests = 8;
SKIP : {
  skip $num_network_tests, "Network tests disabled" unless $NETWORK_TESTS_ENABLED;
  ok $bc = $fac->get('c75b5564-102d-45f2-808f-d7c4313a0752'), 'create an aliquot bc object';
  is $bc->barcode, 'TCGA-AA-3851-01A-01T-1021-13', 'auto-query on barcode() method';
  is $bc->center, 'BCGSC', 'center correct';
  is $bc->plate, 1021, 'plate correct';
  is $bc->tss, 'AA', 'tss correct';
  is $bc->disease, 'COAD', 'disease correct';
  is $bc->tissue_type, '01', 'tissue type correct';
  is $bc->analyte_type, 'T', 'analyte type correct';
  is $bc->vial, 'A', 'vial correct';
  is $bc->case, 3851, 'case correct';

}
1;


