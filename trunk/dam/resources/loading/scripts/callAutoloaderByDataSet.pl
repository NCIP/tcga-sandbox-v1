# This script will call the autoloader for a whole archive or just one data set of an archive
# depending on the options used
# 	-a archive name
#       -l autoloaderLocation
#	-t pattern type
use strict;
use DBI;
use Getopt::Long;
$Getopt::Long::autoabbrev = 0;

#global vars
my $archiveName;
my $autoloaderLocation;
my $baseName;
my $center;
my $currentOutFile;
my $disease;
my $fileTypeFile;
my $isNewFormat;
my $onProdServer;
my $out;
my $patternType;
my $platform;
my $query;
my %fileTypes;
my $pattName;
my $loaderOut;
my $dbName = "tcgaprd2";
my $dbUser = "commonmaint";
my $dbPwd = "comm7983ash";
#check options and set vars
&setUp();

# production db -- read-only
my $dbh = DBI->connect("DBI:Oracle:".$dbName, $dbUser, $dbPwd, { RaiseError => 1, AutoCommit => 0 } ) || die "ERROR: Cannot connect to the dccCommon database" . $DBI::errstr;

# make sure the platform exists
my $platformRes = $dbh->selectall_arrayref("select count(*) from platform where platform_name='$platform'");
die "Platform $platform not found!\n" unless $platformRes->[0]->[0] == 1;

# key is center_platform, val is map of file patterns and file types

open(IN, $fileTypeFile) or die "Could not open $fileTypeFile for reading -- make sure it is in this directory!\n";
while(<IN>) {
  chomp;
  s/\r//;
  s/\n//;

  # center, platform, pattern, file type
  my @line = split(/\t/);
  my $centerPlatform = $line[0] . '_' . $line[1];
  my $pattern = $line[2];
  my $fileType = $line[3];
  $pattern =~ s/\*//g;
  $pattern =~ s/\./\\\./g;
  $fileTypes{$centerPlatform}->{$pattern} = $fileType;  
}

close IN;

# only get the files with pattern type requested if a pattern was requested
if (defined $patternType)
{
$query = qq~select distinct substr(a.deploy_location,1,length(a.deploy_location) - 7) as deploy_location, a.archive_id, a.archive_name, f.file_name, p.platform_name, c.domain_name
 from archive_info a, file_to_archive fa, file_info f, center c, platform p, disease d
 where  a.is_latest=1
 and a.archive_name='$archiveName'
 and a.archive_id = fa.archive_id
 and fa.file_id = f.file_id
 and f.file_name like '%$patternType%'
 and f.level_number = 2
 and a.center_id = c.center_id 
 and a.platform_id = p.platform_id
 and p.platform_name='$platform' 
 and a.disease_id = d.disease_id 
 and d.disease_abbreviation='$disease'
 order by domain_name, platform_name, archive_name, file_name 
~;
} else {
$query = qq~select distinct substr(a.deploy_location,1,length(a.deploy_location) - 7) as deploy_location, a.archive_id, a.archive_name, f.file_name, p.platform_name, c.domain_name
 from archive_info a, file_info f, file_to_archive fa, center c, platform p, disease d
 where  a.is_latest=1
 and a.archive_name='$archiveName'
 and a.archive_id = fa.archive_id
 and fa.file_id = f.file_id
 and f.level_number = 2
 and a.center_id = c.center_id
 and a.platform_id = p.platform_id
 and p.platform_name='$platform' 
 and a.disease_id = d.disease_id
 and d.disease_abbreviation='$disease'
 order by domain_name, platform_name, archive_name, file_name 
~;
}
print "arc query: $query\n";

my $mageTabQuery = qq~select distinct substr(a.deploy_location,1,length(a.deploy_location) - 7) as mage_tab_location 
 from archive_info a, archive_info data_a
 where a.disease_id=data_a.disease_id and
 a.center_id=data_a.center_id and
 a.platform_id=data_a.platform_id and
 a.archive_type_id=6 and data_a.archive_type_id != 7 and
 data_a.archive_name='$archiveName' and a.is_latest=1
~;
#print "mage query: $mageTabQuery\n";


my $baseAutoloaderCommand = "java -Xms512m -Xmx1024m -classpath $autoloaderLocation/classes/qclive:$autoloaderLocation/classes/common:$autoloaderLocation/lib/log4j-1.2.15.jar:$autoloaderLocation/lib/ojdbc5.jar:$autoloaderLocation/lib/spring.jar:$autoloaderLocation/lib/commons-logging-1.1.1.jar:$autoloaderLocation/lib/commons-logging-adapters-1.1.1.jar:$autoloaderLocation/lib/commons-logging-api-1.1.1.jar gov.nih.nci.ncicb.tcga.dcc.qclive.loader.CommandLineLoader";

my $result = $dbh->selectall_arrayref($query, { Slice => {} });

my $mtResult = $dbh->selectall_arrayref($mageTabQuery);
my $mageTabArchive = $mtResult->[0]->[0];
# Only replace the root directory name for files if not on the production server
if (! $onProdServer) {
   $mageTabArchive =~ s/tcgafiles/tcga_prod/;
}

# open output file
if (defined $patternType) {
    $currentOutFile = "$archiveName$patternType.files.txt";
    $loaderOut = "$archiveName$patternType.loader.out";
} else {
    $currentOutFile = "$archiveName.files.txt";
    $loaderOut = "$archiveName.loader.out";
}
print "Opening out file: $currentOutFile \n";
open ($out, ">$currentOutFile");

# loop thru rows retrieved by archive query and write a file of files that need
# loading for this archive; it may be all the file types for this archive or only
# one.
for my $row (@$result) {
  my $archive = $row->{DEPLOY_LOCATION};
  # Only replace the root directory name for files if not on the production server
  if (! $onProdServer) {
     $archive =~ s/tcgafiles/tcga_prod/;
  }
  my $centerPlatform = $row->{DOMAIN_NAME} . '_' . $row->{PLATFORM_NAME};
  my $fileName = $row->{FILE_NAME};

  my $fileType;
  for my $pattern (keys %{$fileTypes{$centerPlatform}}) {
    if ($pattern eq '' || $fileName =~ /$pattern/) {
      $fileType = $fileTypes{$centerPlatform}->{$pattern};
    }
  }
  if (! defined $fileType) {
    print "!!! no file type for $centerPlatform $fileName !!!\n";
  }
  
  print $out "$archive\t$mageTabArchive\t$fileType\t$fileName\n" unless ! defined $fileType;
}

if (defined $out) {
  print "Closing out file: $currentOutFile\n";
  close $out;
}

# run the last one in the loop!
if (defined $currentOutFile) {
  print "outside row loop, executing autoloader\n";
     
  my $autoloaderCommand = "$baseAutoloaderCommand $currentOutFile > $loaderOut 2>&1";
  print "Autoloader for $archiveName ($currentOutFile)...\n";
  print "autoloader command: $autoloaderCommand\n";
  system($autoloaderCommand);
  print "done!\n";
}

$dbh->disconnect();

sub setUp() {

        my $help = 0;
	# get options - "a" is the only required option,
	&GetOptions('a=s' => \$archiveName, 'l=s' => \$autoloaderLocation, 't=s' => \$patternType, 'help|?' => \$help);
        print "archive name: $archiveName, autoloaderLocation $autoloaderLocation, pattern: $patternType\n";
        # see if the user wants syntax help
        if ( $help ) {
           print " Options:\n  Required: -a archive   -l autoLoaderLocation\n  Optional: -t patternType\n    *note: use the pattern without an asterisk (ie: .Normal_LogR.txt) \n Usage:\n perl callAutoloaderByDataSet.pl -a archiveName -l autoloaderLocation -t patternType \n";
           exit;
        }
	die " Options:\n  Required: -a archive   -l autoLoaderLocation\n  Optional: -t patternType\n    *note: use the pattern without an asterisk (ie: .Normal_LogR.txt) \n Usage:\n perl callAutoloaderByDataSet.pl -a archiveName -l autoloaderLocation -t patternType \n" unless defined $archiveName and defined $autoloaderLocation;

	# determine if running on production or not. This will control if the root
	# directory for the deployment location of the files needs to be changed
	if(-d "/tcga_prod") {
	  $onProdServer = 0;
	} else {
	  $onProdServer = 1;
	}

	# make sure that the archive name is in the proper format (old or new) or exit with error
	if ($archiveName =~ /^((.+)_(GBM|OV|LUSC|COAD|READ|LAML|KIRP|KIRC|LUAD|BRCA|UCEC|STAD)\.(.+))\.Level_2\.(\d+)\.(\d+).0$/) {
	  $isNewFormat = 1;
	} elsif ($archiveName =~ /^((.+)_(GBM|OV|LUSC|COAD|READ|LAML|KIRP|KIRC|LUAD|BRCA|UCEC|STAD)\.(.+))\.(\d+)\.(\d+).0$/) {
	  $isNewFormat = 0;
	} else {
	  die "Archive name did not have expected format (center_disease.platform.type.batch.rev.0)\n";
	}
	# pull the center, disease, platorm from the archive name
	$baseName = $1;
	$center = $2;
	$disease = $3;
	$platform = $4;

	print "Center: $center, Disease: $disease, Platform: $platform\n";
	#expect file types file to be in the autoloader directory
	$fileTypeFile = "$autoloaderLocation/source_file_types.txt";

	# expect commandLineLoader properties file to be in the working directory
	die "'CommandLineLoader.properties' not found -- must have that exact name (autoloader insists)\n" unless -e "CommandLineLoader.properties";

	# make sure properties file is for this disease!
	my $grepCommand = "grep -i $disease CommandLineLoader.properties";
	my $grepResults = `$grepCommand`;
	die "CommandLineLoader.properties does not seem to point to the right schema for $disease\n" unless $grepResults =~ /oracle\.user/;

}

