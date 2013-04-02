#!/usr/bin/perl


use strict;
use Config::IniFiles;
use File::Basename;
use File::Compare;


require $ENV{HOME}."/lib/tools_dcc.pl";

my $DIR1=$ARGV[0];
my $DIR_OUT=$ARGV[1];

### ini file
my $file_ini = $ENV{HOME}."/lib/CronOutput.ini";
my %cfg;
tie %cfg, 'Config::IniFiles', ( -file => $file_ini );


my %ticketablefiles;
foreach my $t(keys %cfg){
    if($cfg{$t}{"ticketlist"}==1){
	$ticketablefiles{$t}=1;
    }
}



## load archives already have a ticket
my %dir2ticket;
my $f=$ENV{FILE_ARCHIVE2TICKET};
open(FIN, $f) || die "Could not open file $f\n";
while(my $line=<FIN>){
    chomp $line;
    my ($a,$b, $c)=split(/\t/, $line);
    ## don't process empty lines
    next unless($b);
    ## remove empty spaces
    $b=~s/ //g;
    $dir2ticket{$b}{$a}=1;
}
close(FIN);

## process output and generate list of needed tickets
open(FOUT, "> $DIR_OUT/pending.tickets.txt");
foreach my $file(<$DIR1/*>){

    my $b=basename$file;
    if($ticketablefiles{$b}==1){	 
	print "Opening $file\n";
	open(FIN, $file);
      LINE: while(my $line=<FIN>){
	  foreach my $archive(keys %dir2ticket){
	      ## if the archive is already a ticket, get next line
	      if(index($line, $archive) >= 0){
		  next LINE;
	      }		  
	  }
	  print FOUT $b."\t".$line;
      }
	close(FIN);
	
    }else{
	print "TOADDTO INI FILE $b\n";
    }
}
close(FOUT);
