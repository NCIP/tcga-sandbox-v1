# -*-perl-*-
use strict;
use warnings;
use Test::More qw(tests 41);
use IPC::Run3 qw(run3);
use File::Spec;
use Module::Build;
my  ($in, $out, $err);

my ($v, $this_dir, $f);
my ($tdir, $sdir);
if (eval('Module::Build->current; 1')) {
    # in the build script; use current directory
    $this_dir = Module::Build->current->base_dir;
    $tdir = File::Spec->catdir($this_dir, "t");
    $sdir = File::Spec->catdir($this_dir, "bin");
}
else {
    # running directly
    ($v, $this_dir, $f)  = File::Spec->splitpath(__FILE__);
    $tdir = File::Spec->rel2abs($this_dir);
    $sdir = File::Spec->rel2abs(File::Spec->catdir( File::Spec->updir($this_dir), "bin" ));
}
chdir($sdir);

# test a success (=0) exit
ok( !doval('test_json_1.txt'), "good json gives success");
is( $err, '', "no messages");

# test stdin as input
ok( run3( [qw(cat ../t/samples/test_json_1.txt | perl jsonval.pl -)], \$in, \$out, \$err), "stdin works");

# die on nonexistent input file
ok( doval('I_dont_exist.txt'), "input file DNE fails with nonzero exit value");

# fail on json syntax error
$in = "\{\"this\":\"that\}";
ok( run3( [qw(perl jsonval.pl -)], \$in, \$out, \$err), "bad json ingested");
like $err, qr/FAIL \(JSON\)/, "bad json syntax caught";
undef $in;
1;
# missing expected tag at top level
# missing expected tag at row level
ok( doval('test_json_missing_tags_1.txt'), "missing tags test fails with code 1");
is( num_msgs(), 4, "4 errors expected");
like $err, qr/'case_by_shipment', row 4: Tag 'rec_date' is expected/, "missing rec tag";
like $err, qr/'case_summary_by_disease', row 1: Tag 'tumor_abbrev' is expected/, "missing tumor_abbrev tag";
like $err, qr/'shipment_schedule' top-level tag is expected/, "missing top-level shipment_schedule";
like $err, qr/'timestamp' top-level tag is expected/, "missing top-level timestamp";
fail_emitted_ok();

# unexpected tag at top level
# unexpected tag at row level
ok( !doval('test_json_extra_tags.txt'), "extra tags pass with warnings" );
is( num_msgs(), 2, "2 errors expected");
like $err, qr/'bloog' top-level tag not expected/, "warn extra tag at toplevel";
like $err, qr/'shipment_schedule', row 1: Tag 'flintstones' not expected/, "warn extra tag in shipment_schedule";
warn_emitted_ok();
unlike $err, qr/'FAIL \(DCC\)'/, "no fail emitted";

# object not simple value at top level
# simple value not object at top level
# object not simple value at row level

ok( doval('test_json_bad_values_1.txt'), "bad values test 1 fails with exit value 1");
is( num_msgs(), 3, "3 errors expected" );
like $err, qr/'case_summary_by_disease', row 2: Tag 'submitted_to_bcr' value should be an integer, not an object/, "submitted_to_bcr not a number";
like $err, qr/'incoming_cases' value: an array of objects is expected, but not present in input/, "toplevel incoming_cases not an array";
like $err, qr/'timestamp' value should be date string, not an object/, "toplevel timestamp not a date string";

# bad date formats
ok( doval('test_json_bad_dates.txt'), "bad dates test fails with code 1");
is( num_msgs(), 5, "5 errors expected" );
like $err, qr/'case_by_shipment', row 1: Tag 'rec_date' value '02\/32\/2010'/, "got bad day";
like $err, qr/'shipment_schedule', row 1: Tag 'ship_date_gsc' value should be one of/, "got bad month";
like $err, qr/'shipment_schedule', row 3: Tag 'ship_date_cgcc' value should be one of/, "got 1999";
like $err, qr/'shipment_schedule', row 4: Tag 'ship_date_gsc' value should be one of/, "got cruft";
like $err, qr/'timestamp' value '11\/12\/10'/, "got 2-digit year";
fail_emitted_ok();

# non-numeric "int" value
# unknown enumeration member
ok( doval('test_json_bad_values_2.txt'), "bad values test 2 fails with exit value 1");
is (num_msgs(), 4, "4 errors expected");
like $err, qr/'case_by_shipment', row 1: Tag 'submitted_to_bcr' value is not numeric/, "submitted_to_bcr non-numeric";
like $err, qr/'shipment_schedule', row 1: Tag 'num_cases' value is not numeric/, "num_cases non-numeric";
like $err, qr/'shipment_schedule', row 2: Tag 'tumor_abbrev' value should be one of .* not 'FUDD'/, "FUDD not a tumor abbrev";
like $err, qr/'shipment_schedule', row 5: Tag 'tissue_type' value should be one of \[normal,tumor\], not 'iffy'/, "tissue_type not 'normal' or 'tumor'";

# non-numeric "int" values assoc with qualified_hold tag
ok( doval('test_json_qualified_hold_1.txt'), "bad values for qualified_hold test 1 fails with exit value 1");
is (num_msgs(), 4, "4 errors expected");
like $err, qr/'case_summary_by_disease', row 1: Tag 'qualified_hold' value is not numeric/, "qualified_hold non-numeric";


# errors reported at correct row indices : tested by the regexps above

sub doval {
    my $file = File::Spec->catfile('..', "t", "samples", shift());
    my @cmd = ( 'perl', "jsonval.pl", $file);
    eval {
	run3 \@cmd, \$in, \$out, \$err;
	1;
    };
    return $?;
}

sub fail_emitted_ok {
  like $err, qr/FAIL \(DCC\)/, "fail emitted";
}

sub warn_emitted_ok {
  like $err, qr/Warning \(DCC\)/, "warning emitted";
}

sub num_msgs {
  my @a = split( /\n/, $err );
  return scalar @a;
}
