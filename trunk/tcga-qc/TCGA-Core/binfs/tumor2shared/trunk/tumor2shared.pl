# $Id: tumor2shared.pl 9663 2011-02-03 21:13:03Z jensenma $
# DCCT-399
# for an input clinical archive directory
# - add shared clinical elts namespace schema 
# - convert selected common elements from <disease>_clinical to 
#   shared_clinical_elements namespace in each xml
# update manifest, including previously references biospecimen xml
# if present.
# creates new archive directory with incremented revision number

# write to convert an expanded archive:

use strict;
use warnings;
use File::Spec;
use File::Copy;
use XML::Twig;
use Time::gmtime;
use Digest::MD5;

my $COMMON_LIST_FILE = "convert_elts.txt";
my $SHARED_NS = "http://tcga.nci/bcr/xml/clinical/shared/2.3";
my $SHARED_XSD = "./TCGA_BCR.Shared_Clinical_Elements.xsd";
my $SHARED_PFX = "shared";
my @TUMORS_OK = qw(GBM OV);

my ($arkdir) = @ARGV;

die "usage: perl tumor2shared.pl [archive_directory]" unless (-d $arkdir);

my @dirs = File::Spec->splitdir($arkdir);
my $arkname = pop @dirs;

my ($DOMAIN, $TUMOR, $BATCH, $REV, $goob)  = $arkname =~ /(.*)_([A-Z]+)\..*\.(\d+)\.(\d+)\.(\d+)$/;
die "tumor type must be one of [gbm, ov]" unless grep /^$TUMOR/,@TUMORS_OK;

my $TUMOR_NS = "http://tcga.nci/bcr/xml/clinical/".lc($TUMOR);
my $TUMOR_OLD_XSD = "TCGA_BCR.".uc($TUMOR)."_Clinical.xsd";
my $TUMOR_PATCHED_XSD = "TCGA_BCR.".uc($TUMOR)."_Clinical_patched.xsd";

my @common_elts;
open my $listfile, $COMMON_LIST_FILE or die $!;
while (<$listfile>) { chomp; push @common_elts, $_; }
my $twig = XML::Twig->new( 
    pretty_print => 'indented',
    twig_handlers => {
	_all_ => \&convert
    }
    );

# create new archive directory
my $new_arkname = $arkname;
my $new_arkdir = $arkdir;
my $INCREV = $REV+1;
$new_arkname =~ s/\.$REV\./.$INCREV./;
$new_arkdir =~ s/\.$REV\./.$INCREV./;

if (! -d $new_arkdir) {
    mkdir($new_arkdir) or die $!;
}

opendir my $ark, $arkdir or die $!;
my @arkfiles = reverse grep { !/^\./ && !/^$TUMOR_OLD_XSD$/ && !/^MANIFEST.txt$/ } readdir($ark);


# transfer unchanged files
print STDERR "copy common files\n";
foreach my $file ( grep { !/\.xml/ } @arkfiles ) {
    copy( File::Spec->catfile($arkdir, $file),
	  File::Spec->catfile($new_arkdir, $file) ) or die $!;
}

print STDERR "comment in README_XSD.txt\n";
open my $rdmf, ">>", File::Spec->catfile($new_arkdir,'README_XSD.txt') or die $!;
print $rdmf "\nSPECIAL NOTE (".gmctime()."):\n".
    "$TUMOR_OLD_XSD atched to allow the use of $SHARED_XSD in data XML\n".
    "Direct questions to TCGA-DCC-BINF-L\@LIST.NIH.GOV.\n".
    "-- TCGA DCC\n";

# add patched xsd
print STDERR "copy patched schema\n";
copy( $TUMOR_PATCHED_XSD, 
      File::Spec->catfile($new_arkdir,$TUMOR_PATCHED_XSD) ) or die $!;

# add shared schema
print STDERR "copy shared elements schema\n";
copy( $SHARED_XSD,
      File::Spec->catfile($new_arkdir,$SHARED_XSD) ) or die $!;

# convert xml

foreach my $file ( grep { /\.xml/ } @arkfiles ) {
    print STDERR "convert $file\n";
    $twig->parsefile(File::Spec->catfile($arkdir,$file)) or die $!;
    $twig->root->set_att("xmlns:$SHARED_PFX",$SHARED_NS);
    $twig->root->set_att('xsi:schemaLocation', "$TUMOR_NS $TUMOR_PATCHED_XSD");
    $twig->print_to_file(File::Spec->catfile($new_arkdir,$file)) or die $!;
}

# create manifest

# get biospecimen xml lines already in manifest
my @biorecs = ();
open my $manf, File::Spec->catfile($arkdir,'MANIFEST.txt') or die $!;
while (<$manf>) { chomp; push @biorecs, $_; }
@biorecs = grep { /biospecimen/ } @biorecs;

# get md5s for new files
my $md5fac = Digest::MD5->new();
my %new_md5;

opendir my $new_ark, $new_arkdir or die $!;
my @newfiles = reverse grep { !/^\./ } readdir($new_ark) or die $!;
print STDERR "calc md5s\n";

for (@newfiles) {
    open my $f, File::Spec->catfile($new_arkdir,$_) or die $!;
    if ($^O =~ /Win32/) { binmode $f, ':raw';}
    $md5fac->reset;
    $new_md5{$_} = $md5fac->addfile($f)->hexdigest;
}
for (@biorecs) {
    my @a = split / /;
    $new_md5{$a[1]} = $a[0];
    push @newfiles, $a[1];
}

print STDERR "write new manifest\n";
# write new manifest
open $manf, ">", File::Spec->catfile($new_arkdir,'MANIFEST.txt');
for (@newfiles) {
    printf $manf "%s %s\n", $new_md5{$_}, $_;
}



1;


sub convert {
    return unless $_->namespace eq $TUMOR_NS;
    my $local_name = $_->local_name;
    if (grep /$local_name/,@common_elts) {
	$_->set_name(join(':',$SHARED_PFX,$local_name));
    }
    
}
