#-*-perl-*-
#$Id: script.t 10217 2011-03-06 02:02:42Z jensenma $
use strict;
use warnings;
use lib '../lib';
use Test::More tests => 14;
use IPC::Run qw(run);

my @result;

use_ok('checkXmlBtab::Config');

@result = run_script();
like $result[2], qr/^Usage:/, "help on bare cmd";
@result = run_script( [qw(--help)] );
like $result[2], qr/^Usage:/, "help works";

@result = run_script( [qw(--verbose --exclude drugs --exclude radiations)], [qw(intgen.org_clinical_patient_all_GBM_public_data_only.txt intgen.org_clinical.TCGA-02-0021.xml)] );
is (0, $result[0], "good xml file passes (with exclusions)");
like $result[1], qr/OK/, "high sign";

@result = run_script( [qw(--noverbose --strict --exclude drugs --exclude radiations)], [qw(intgen.org_clinical_patient_all_GBM_public_data_only.txt intgen.org_clinical.TCGA-02-0021.xml)] );
is (1, $result[0], "good xml file fails strict validation");

@result = run_script( [qw(--verbose --exclude radiations)], [qw(intgen.org_clinical_patient_all_GBM_public_data_only.txt intgen.org_clinical.TCGA-19-1786.xml)] );
is ($result[0], 1, "bad xml file fails");
my @a = $result[2] =~ /(procurement)/gm;
is (3, @a, "3 xml fields with missing data");

@result = run_script( [qw(--noverbose --diff --exclude radiations)], [qw(intgen.org_clinical_patient_all_GBM_public_data_only.txt intgen.org_clinical.TCGA-19-1786.xml)] );

like $result[1], qr/TCGA-19-1786\.xml:vital_status\s\(TCGA-19-1786\)\sLIVING\sDECEASED/, "diff works";

@result = run_script( [qw(--noverbose)], [qw(intgen.org_clinical.TCGA-19-1786.xml intgen.org_clinical_patient_all_GBM_public_data_only.txt )] );
like $result[2], qr/looks like XML/, "caught biotab/xml switch on cmdline";

@result = run_script( [qw(--noverbose)], [qw(intgen.org_clinical_patient_all_GBM_public_data_only.txt intgen.org_clinical_patient_all_GBM_public_data_only.txt  )] );

like $result[2], qr/Problems with XML/, "caught bad 'xml' file";

@result = run_script( [qw(--verbose)], [qw(nationwidechildrens.org_clinical_sample_all_UCEC.txt nationwidechildrens.org_biospecimen.TCGA-BK-A0CC.xml)] );

like $result[2], qr/XML tag is empty, but procurement_status is 'Completed'/, "processed clinical sample biotab";

#@result = run_script( [qw(--verbose)], [qw(clinical_radiation_all_UCEC.txt nationwidechildrens.org_biospecimen.TCGA-BK-A0CC.xml)] );

#like $result[2], qr/XML does not contain biotab key/, "informative warning for incompatible biotab/XML pair";

@result = run_script( [qw(--verbose)], [qw(clinical_analyte_all_BRCA.txt nationwidechildrens.org_biospecimen.TCGA-A2-A0CT.xml)] );

like $result[1], qr/ OK$/, "multiple barcode key example works with sample biotab";

@result = run_script( [qw(--verbose)], [qw(clinical_drug_all_BRCA.txt nationwidechildrens.org_clinical.TCGA-A2-A0D0.xml)] );

like $result[1], qr/ OK$/, "multiple barcode key example works with patient biotab";

1;


sub run_script {
    my @opts = $_[0] && @{$_[0]};
    my @argv = $_[1] && @{$_[1]};
    my ($in, $out, $err);
    my @cmd = ( 'perl', script(), map {$_ || ()} @opts, map {test_file($_) || ()} @argv );
    run \@cmd, \$in, \$out, \$err;
    return ($? >> 8, $out, $err);
}

sub test_file { $_[0] && "t/samples/$_[0]" }
sub script { "bin/checkXmlBtab.pl" }
