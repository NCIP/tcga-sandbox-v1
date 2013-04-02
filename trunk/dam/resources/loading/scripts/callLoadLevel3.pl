# This script will call the level 3 loader if you pass it an archiveName and the database info
use strict;
use DBI;
use Cwd qw(realpath);
use Getopt::Long;
$Getopt::Long::autoabbrev = 0;

my $fullpath = realpath($0);
$fullpath =~ s/callLoadLevel3.pl//;
my $pwdFile = $fullpath."whatNeedsDbs.txt";
my $oracleDb;

# get options
&GetOptions('D=s' => \$oracleDb);
die "Required: -D db \n" unless defined $oracleDb;

my $archive = shift @ARGV or die "Archive name needed as 1st parameter\n";
my $patternFile = shift @ARGV or die "Pattern file needed as 2nd parameter\n";
die "$patternFile not found\n" unless -e $patternFile;

$archive =~ /^((.+)_(GBM|OV|LUSC|COAD|READ|LAML|KIRP|KIRC|LUAD|BRCA|UCEC|STAD)\.(.+))\.Level_3\.(\d+)\.(\d+).0$/ or die "Archive name did not have expected format (center_disease.platform.Level_3.batch.rev.0)\n";
my $baseName = $1;
my $center = $2;
my $disease = $3;
my $platform = $4;
my $batch = $5;
my $revision = $6;
my $oracleUsername = $disease."maint";
my $oraclePassword;

# get the password from the $pwdFile for $disease
open(PWDS, $pwdFile);
while(<PWDS>) {
  chomp;
  s/\r//;
  s/\n//;

  my @line = split(/\t/);
  my $diseaseName = $line[0];
  if ($diseaseName eq $disease)
  {
     $oraclePassword = $line[1];
  }
}
die "Password not in $pwdFile file for $disease\n" unless defined $oraclePassword;

# if the oracle schema name they pass is incorrect for the disease in the archive name exit with an error
$oracleUsername =~ m/$disease/i or die "Disease in archive name, $disease, wrong for oracle schema $oracleUsername\n";

my %patterns; # key is center_platform
open(PATTERN, $patternFile);
while(<PATTERN>) {
  chomp;
  s/\r//;
  s/\n//;

  my @line = split(/\t/);
  my $centerPlatform = $line[0] . '_' . $line[1];
  my $patternStr = $line[2];
  my @patternList = split(/,/, $patternStr);
  $patterns{$centerPlatform} = \@patternList;
}
close PATTERN;

# get pattern for this center/platform
my $cp = $center . '_' . $platform;
my $script = $fullpath."load_level3.pl";
print "cp is: $cp \n";
for my $pattern (@{$patterns{$cp}}) {
    print "$pattern!\n";
    my $pattName = $pattern;
    $pattName= ~s/\*//;
    my $loadCommand = "perl $script -D $oracleDb -U $oracleUsername -P $oraclePassword $archive $pattern > $archive$pattName.out 2> $archive$pattName.err";
    print "$loadCommand\n";
    system($loadCommand);
}

