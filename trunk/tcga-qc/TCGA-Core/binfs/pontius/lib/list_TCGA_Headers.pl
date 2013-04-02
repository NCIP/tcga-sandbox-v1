#!/usr/bin/perl


########################################
##
## looks up platform build assemblies and headers
##
########################################

use strict;
use File::Find;
use File::Basename;

require $ENV{HOME}."/lib/tools_dcc.pl";

my $DEBUG=0;
my $DEBUG_INDEX=0;
my $DIR_OUT=$ARGV[0];

###########################################
## load ini file
use Config::IniFiles;
my $file_ini = $ENV{HOME}."/lib/TCGAHeaders.ini";
my %cfg;
tie %cfg, 'Config::IniFiles', ( -file => $file_ini );
###########################################


###########################################
## get live archives
my %latestArchives = Archive2LatestLocation();
###########################################

my %headers;
foreach my $archive(keys %latestArchives){
    my @tars=(keys %{$latestArchives{$archive}});
    my $tar=$tars[0];
    my $dir=$tar;
    $dir=~s/.tar.gz//;
    my @a=split(/\//, $dir);    
    my ($study, $centerType, $center, $platform, $datatype)=@a[6..10];
    my $level="NONE";
    next unless(($dir =~ m/Level_2/) ||
		($dir =~ m/Level_3/));

    if($dir=~m/Level_2/){
	$level="Level_2";
    }elsif($dir=~m/Level_3/){
	$level="Level_3";
    }
    ## now get headers

    $DEBUG_INDEX++;
    foreach my $file(<$dir/*>){
	
	my ( $ft, $header_use ) = ScreenFileType(basename $file);

	#print "$header_use\n$ft\n$file\n\n";
	## group by center, platform and level
	next if($header_use  < 1);

	#if($DEBUG){print $file."\n";}
	open(FIN, $file) || die "Could not open $file\n";
	my $line = <FIN>;
	my @a_lines = split(/[\r\n]/, $line);
	my $line1=$a_lines[0];
	my $line2;
	if(exists $a_lines[1]){
	    $line2=$a_lines[1];
	}else{
	    $line2=<FIN>;
	    chomp $line2;
	}
	close(FIN);
	my $filetype=join("|", $platform, $level, $ft, $center, $study);
	my $h=CleanHeader($line1, $line2);	
	$headers{$filetype}{$h}++;
	
    }
    next unless($DEBUG);
    if($DEBUG_INDEX%10==0){
	&SummarizeHeaders();	
    }        
}

&SummarizeHeaders();



sub SummarizeHeaders(){

    open(FOUT,  "> $DIR_OUT/headers.summary.txt");

    my %info_out;
    foreach my $key(keys %headers){
	foreach my $header(keys %{$headers{$key}}){
	    if($DEBUG){
		print join( "\t", $header, $headers{$key}{$header}, "\n");
	    }
	    print FOUT join( "\t", 
			     $key, $header, 				 
			     $headers{$key}{$header}, 			     
			     "\n");
	    my @cols=split(/\t/, $header);
	    my ($platform, $level,$filetype,$center, $studies) = split(/\|/, $key);

	    foreach my $column(@cols){
		next if($column eq "Header2:");
		my $definition="?";
		my $lc=lc($column);
		$lc=~s/\"//g;
		if($cfg{$lc}{"$center.$filetype"}){
		    $definition = $cfg{$lc}{$center.$filetype};
		}elsif($cfg{$lc}{$center}){
		    $definition = $cfg{$lc}{$center};
		}elsif($cfg{$lc}{all}){
		    $definition = $cfg{$lc}{all};
		}
		$info_out{$center."\t".$platform."\t".$level}{$filetype}{$column}=$definition;	

	    }
	}
    }    
    close(FOUT);

    open(FOUT2, "> $DIR_OUT/header.definitions.txt");
    open(FOUT3, "> $DIR_OUT/todo.header.definitions.txt");
    foreach my $center(keys %info_out){
	foreach my $ft(keys %{$info_out{$center}}){
	    foreach my $column(keys %{$info_out{$center}{$ft}}){
		print FOUT2 join "\t", $center, $ft, $column, $info_out{$center}{$ft}{$column},"\n";
		if($info_out{$center}{$ft}{$column} =~m/\?/){
		    print FOUT3 join "\t", $center, $ft, $column, $info_out{$center}{$ft}{$column},"\n";
		}
	    }
	}
    }    
    close(FOUT2);
    close(FOUT3);

}






sub CleanHeader{
    my $h1_in=$_[0];
    my $h2_in=$_[1];

    $h1_in=~s/\r//;
    chomp $h1_in;
    $h2_in=~s/\r//;
    chomp $h2_in;

    my @a_1=split(/\t/, $h1_in);
    my @a_2=split(/\t/, $h2_in);
    unless(length($a_2[-1]) > 0){
	pop @a_2;
    }
    my $h1_out = $h1_in;

    ## truncate Sample REF and Hybridization REF lines
    if($a_1[0]=~m/ REF/){       
	if($a_1[0]=~m/^Hybridization REF/){       
	    $h1_out = $a_1[0];

	}elsif($a_1[0]=~m/Sample REF/){
	    $h1_out = $a_1[0];
	    if($a_1[1] =~ m/Order/){
		$h1_out.="\t".$a_1[1];
	    }
	}

	## for .MDA_RPPA_Core.SuperCurve.Level_2
	if(($a_1[0] =~ m/Sample REF/) &&
	   ($a_1[1] =~ m/Order/)){
	    return $h1_out;
	}

	my $h2_out = join("\t", @a_2);        	    

	##
	## cut off final columns if they are identical to previous and append with 'etc'
	##	
	while($a_2[-1] eq $a_2[-2]){
	    pop @a_2;
	}
	my $h2_out = join("\t","Header2:", @a_2);        	           
	return ($h2_out);
    }
    return ($h1_out);
}





