##############################################################################################################
# This script will run a sql script in multiple schemas in an environment specified by -e option
#  usage: perl RunSqlScript.pl -e environment -s schemaType -f filename
#
#  Created by Shelley Alonso 04/13/2011
#
#  Modification History
#
#  
#
##############################################################################################################
use strict;
use DBI;
use Cwd qw(realpath);
use Getopt::Long;
$Getopt::Long::autoabbrev = 0;

my $fullpath = realpath($0);
$fullpath =~ s/RunSqlScript.pl//;
my $env;
my $schemaType;
my $fileName;
my $validSchemaType = "common disease both";
my $validEnv = "test dev qa stage prod";
my $credFile;
my $db;

#get options and set global vars
setUp();

#loop thru database credentials and run the script for each of the appropriate schemas
runScript();

exit;

sub runScript() {
    my $command; 
    my $type;
    my $user;
    my $pass;
    my $result;
    print "opening file $credFile \n";
    open(INPUT, $credFile);
    while(<INPUT>) {
      chomp;
    
      ($type, $user, $pass) = split(/\t/);
      if ($type eq $schemaType || $schemaType eq "both") {
      	$command = "sqlplus " . $user . "/" . $pass ."@" . $db . " <" . $fileName . " >" . $fileName . "." . $user . ".out";
      	print "command: $command \n";
        $result = `$command`;
      }
   }
   close INPUT;

}

sub setUp() {

        my $help = 0;
	# get options - "e s and f" are required options
        &GetOptions('e=s' => \$env, 's=s' => \$schemaType, 'f=s' => \$fileName, 'help|?' => \$help);

        # see if the user wants syntax help
        if ( $help ) {
           print " Options:\n  Required: -e environment -s schemaType -f filename \n   Usage:\n RunSqlScript.pl -e environment -s schemaType -f filename\n";
           print " Valid environment: test dev qa stage or prod \n Valid schemaType: common disease both \n filename: the name of the sql script to execute \n";
           exit;
        }
        die "Required: -e environment -s schemaType -f filename \n Valid environment: test dev qa stage prod \n Valid schemaType: common disease both \n filename: the name of the sql script to execute \n" unless $env && $schemaType && $fileName;

	# validate options passed
	die "Environment must be one of $validEnv \n" unless $validEnv =~ /$env/i;
	die "Schema type must be one of $validSchemaType \n" unless $validSchemaType =~ /$schemaType/i;
	$credFile = $env . "Db.txt";
	if ($env eq 'test') {
	   $db = "tcgadb";
	} elsif ($env eq 'prod') {
	   $db = "tcgaprd2";
	} elsif ($env eq 'stage') {
	   $db = "tcgastg";
	} else {
	   $db = "tcga" . $env;
	}
}


