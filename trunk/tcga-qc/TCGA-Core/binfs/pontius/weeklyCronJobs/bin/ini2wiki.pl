#!/usr/bin/perl


use strict;
use Config::IniFiles;
use File::Basename;
use File::Compare;


require $ENV{HOME}."/lib/tools_dcc.pl";

my $DIR1=$ARGV[0];
my $DIR2=$ARGV[1];
my $DIR_OUT=$ARGV[2];

### ini file
my $file_ini = $ENV{HOME}."/lib/CronOutput.ini";
my %cfg;
tie %cfg, 'Config::IniFiles', ( -file => $file_ini );


my %info;
## resort based on scope
foreach my $fout(keys %cfg){
    my %tmp;
    foreach my $tag("scope", "program","ticketlist","input", "fileformat"){
	if(exists $cfg{$fout}{$tag}){
	    $tmp{$tag}=$cfg{$fout}{$tag};
	} else{
	    print "NO VALUE FOR $fout $tag\n";
	    $tmp{$tag}="NA";
	}
    }
    my $scope=$tmp{"scope"};    
    my $program=$tmp{"program"};
    my $fileformat=$tmp{"fileformat"};
    $info{$scope}{$program}{$fout}=$fileformat;    
}

## sort
my @perscope = sort { $a <=> $b } (keys %info);


open(FOUT, "> wiki.Cron.txt");
print FOUT "||Scope of check|| script || outputfile || output||\n";
foreach my $scope(@perscope){
    foreach my $program(keys %{$info{$scope}}){
	foreach my $outputfile(keys %{$info{$scope}{$program}}){
	    print FOUT "|".$scope."|".$program."|".$outputfile.
		"|".
		$info{$scope}{$program}{$outputfile}.
		"|\n";
	}
    }
}
close(FOUT);
