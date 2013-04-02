#!/usr/bin/perl -w

use strict;
use warnings;

#$HeadURL$
#$Rev$
#$LastChangedDate$

use File::Path qw(make_path);
use File::Path;
use File::Copy;
use Archive::Tar;
use File::Basename;
use Digest::MD5;
use Cwd;

require require "/h1/pontiusj/lib/tools_dcc.pl"; 

@ARGV == 4 or die 
"usage: $0 gscbroad ticketnumber archive_name diectory_folder\n";

my ($centername,$ticketname,$archivename,$directory_folder) = @ARGV;

my $pending_archive = NameOfPendingSubmission(basename($archivename));

$pending_archive = $pending_archive.".tar.gz";

print "Pending archive name is ".$pending_archive."\n";



#STEP 4:making new directories based on submitting center and ticket number

#modify /home/wany/test in final testing.

make_path("$directory_folder/$centername/$ticketname");
my $path1 = "$directory_folder/$centername/$ticketname/DCC";
my $path2 = "$directory_folder/$centername/$ticketname/original";
my $error1;
my $error2;
my $error;

#remove content if path is already existed
if(-e $path1)
{
	rmtree $path1;
	if( -e $path1 )
	{
		 print "directory $path1 existed.\n";
  }
}

if(-e $path2)
{
	rmtree $path2;
	if( -e $path2 )
	{
	   print "directory $path2 existed.\n";
	}
}

mkdir $path1 or $error1 = $!;
unless (-d $path1) {
    die "Cannot create directory '$path1': $error1";
}

mkdir $path2 or $error2 = $!;
unless (-d $path2) {
    die "Cannot create directory '$path2': $error2";
}

#STEP 5: copy original archive into original folder and expand original archive.


my @old_archives = glob $archivename."*";
#print $old_archives[0]." ".$old_archives[1]."\n";
#my copied_to_directory = 


foreach my $old_archive (@old_archives)
{
	    my $short_file_name = basename($old_archive);
	    my $new_archive = $path2."/".$short_file_name;
	    #my $tempname = basename($archivename);
	    #$new_archive =~ s/$tempname/$pending_archive/;
	    copy($old_archive, $new_archive) or die "copy archive failed";
	    
}

chdir $path2 or die "Change directory $path2 failed";

my $oldinput = basename($archivename);
my $input = $pending_archive;

my $tar = Archive::Tar->new;

#extract tar file
$tar->read($oldinput);
$tar->extract();

$input =~ s/.tar.gz//;
$oldinput =~ s/.tar.gz//;
#print "Debug 1:".$input;

my $newpath = $path1."/".$input;
#print "Debug 2:".$newpath."\n";

my $tempname = basename($archivename);
$newpath =~ s/$tempname/$pending_archive/;
#print "new path is ".$newpath."\n";

move("$oldinput","$newpath");

#STEP 6 and 7: check for protected MAF files or VCF files.

my $short_archive_name = basename($archivename);

(my $expandarchive = $pending_archive) =~ s/.tar.gz//;
my $basearchive = $expandarchive;
$expandarchive = $path1."/".$expandarchive;
#print "Expand archive is ". $expandarchive."\n";
chdir $expandarchive or die "Change direcotry $expandarchive failed";

#exist protectd folder
my $exist_protect_folder = 0;
my $exist_public_folder = 0;


open (MANIFEST,"< MANIFEST.txt") or die("Cannot open @_:$!\n");
open (MANIFEST_PRIVATE, ">>MANIFEST_private.txt") or die ("Cannot open @_:$!\n");
open (MANIFEST_PUBLIC, ">>MANIFEST_public.txt") or die ("Cannot open @_:$!\n");

my %hashfile;
while (<MANIFEST> ){
    chomp;
    my ($uuid, $file) = split('\s+');
    $hashfile{$uuid} = $file;
    
   # print $file."\t".$ext."\n";
    if ($file =~ /\.vcf$/) {
    	if( $exist_protect_folder == 0 )
    	{
    		my $temppath = $path1."/protected";
    		mkdir $temppath or $error = $!;
    		unless (-d $temppath) {
           die "Cannot create directory '$temppath': $error";
        }
        $exist_protect_folder = 1;
    	}
    	
    	copy($file, $path1."/protected/") or die "copy archive failed";
    	print MANIFEST_PRIVATE $uuid."  ".$file."\n";
    	
    	#print "copy vcf file $file\n";
    }
    elsif ($file =~ /\.protected\.maf$/){
    	if( $exist_protect_folder == 0 )
    	{
    		my $temppath = $path1."/protected";
    		mkdir $temppath or $error = $!;
    		unless (-d $temppath) {
           die "Cannot create directory '$temppath': $error";
        }
        $exist_protect_folder = 1;
    	}
    		copy($file, $path1."/protected/") or die "copy archive failed";
        print MANIFEST_PRIVATE $uuid."  ".$file."\n";
    		#print "$file is protected.maf file\n";
    }
    elsif ($file =~ /\.somatic\.maf$/) {
    	my $return_code = check_somatic_maf($file);
    	print "Return code is ".$return_code." (0 means no issues. 1 means somatic maf file is not following our standard)\n";
      if( $return_code == 1 )
    	{
    		die "Return to submitter. No further steps.\n";
    		
    	}
    	if( $exist_public_folder == 0 )
    	{
    		my $temppath = $path1."/public";
    		mkdir $temppath or $error = $!;
    		unless (-d $temppath) {
           die "Cannot create directory '$temppath': $error";
        }
        $exist_public_folder = 1;
    	}
      copy($file, $path1."/public/") or die "copy archive failed";
      print MANIFEST_PUBLIC $uuid."  ".$file."\n";
      
    } 
      
}	

foreach my $key ( keys %hashfile )
{
	if($hashfile{$key} ne "MANIFEST.txt" and $hashfile{$key} !~ /\.somatic\.maf$/ and $hashfile{$key} !~ /\.protected\.maf$/ and $hashfile{$key} !~ /\.vcf$/ )
	{
		if( $exist_public_folder == 1 )
		{
			copy($hashfile{$key}, $path1."/public/") or die "copy archive failed";
    	 	print MANIFEST_PUBLIC $key."  ".$hashfile{$key}."\n";
		}
		if( $exist_protect_folder == 1 )
		{
			copy($hashfile{$key}, $path1."/protected/") or die "copy archive failed";
    	print MANIFEST_PRIVATE $key."  ".$hashfile{$key}."\n";
		}
	}
}

#making deploy folder

my $path3= $path1."/deploy";
my $error3;
mkdir $path3 or $error3 = $!;
unless (-d $path3) {
    die "Cannot create directory '$path3': $error3";
}

#split MANIFEST file and zip file and put it into deploy folder

chdir $path1 or die "Change directory $path1 failed";

if( $exist_public_folder == 1) 
{
	
	$newpath = $path1 ."/public/MANIFEST.txt";
	my $oldpath = $expandarchive."/MANIFEST_public.txt";
	#print "old path is ".$oldpath."\n";
	move("$oldpath", "$newpath");

  my $expandarchivetemp = $expandarchive.".temp";
  move("$expandarchive","$expandarchivetemp");

  print "Processing public archives\n";

my $basearchivename = $basearchive.".tar.gz";
system("mv public ".$basearchive);

my @old_archives = glob "$basearchive/*";

system("tar -czf ".join(" ",$basearchivename,@old_archives));
	
	#print "Expand archive is ".$basearchive1."\n";
	my $temppath=$path3."/".$basearchive.".tar.gz";
	#print "temp path is ".$temppath."\n";
	move("$basearchive.tar.gz","$temppath");
	
	 system("mv ".$basearchive." public");
	 move("$expandarchivetemp","$expandarchive");
}

chdir $path1 or die "Change directory $path1 failed";

if( $exist_protect_folder == 1 )
{
	$newpath = $path1 ."/protected/MANIFEST.txt";
	my $oldpath = $expandarchive."/MANIFEST_private.txt";
	move("$oldpath", "$newpath");

print "Processing protected archives\n";
(my $basearchive1= $basearchive) =~ s/DNASeq/DNASeq_Cont/i;
my $basearchivename = $basearchive1.".tar.gz";
system("mv protected ".$basearchive1);


my @old_archives = glob "$basearchive1/*";

system("tar -czf ".join(" ",$basearchivename,@old_archives));
	
	#print "Expand archive is ".$basearchive1."\n";
	my $temppath=$path3."/".$basearchive1.".tar.gz";
	#print "temp path is ".$temppath."\n";
	move("$basearchive1.tar.gz","$temppath");

 system("mv ".$basearchive1." protected");
}
#generate md5 file in deploy folder

chdir $path3 or die "Change directory $path3 failed";

if( $exist_public_folder == 1)
{
	
  my $md5 = Digest::MD5->new;
	
	open (FILE,$basearchive.".tar.gz") or die "Error: Could not open $basearchive";
	binmode(FILE);
	my $md5sum = $md5->addfile(*FILE)->hexdigest;
	#print "md5 open is ".$md5sum."\n";
	
	open (FILE_out,">".$basearchive.".tar.gz.md5");
	print FILE_out $md5sum."  ".$basearchive.".tar.gz\n";
}

if( $exist_protect_folder == 1 )
{
	my $md5 = Digest::MD5->new;
	
	(my $basearchive1= $basearchive) =~ s/DNASeq/DNASeq_Cont/i;
	open (FILE,$basearchive1.".tar.gz") or die "Error: Could not open $basearchive1";
	binmode(FILE);
	my $md5sum = $md5->addfile(*FILE)->hexdigest;
	#print "md5 protected is ".$md5sum."\n";
	
	open (FILE_out,">".$basearchive1.".tar.gz.md5");
	print FILE_out $md5sum."  ".$basearchive1.".tar.gz\n";
}

sub check_somatic_maf{
  
  my $return_code = 0;
  my $line;
  
  open (FILE,"<@_") or die("Cannot open @_:$!\n");
  
  $line = <FILE>; #first line
  $line = <FILE>; #header line
    
  while($line = <FILE>){
		
		if( $line =~ /^#/ )
  	{
  		next;
  	}
		
		$line = uc $line;
		chomp($line); 
		
		my @splitarray = split /\s/,$line;
		
		if( $splitarray[25] =~ m/mutation_status/i )
		{
			next;
		}
		
		if( $splitarray[8] =~ m/De_novo_Start_InFrame/i or $splitarray[8] =~ m/De_novo_Start_OutOfFrame/i ) #APPS-6553
		{
			 $return_code = 1;
  	   print "Find De_novo_Start_InFrame or De_novo_Start_OutOfFrame for $splitarray[0]\n";
  	   return $return_code;
  	}
		 
		if( $splitarray[25] !~ m/somatic/i )
		{
			$return_code = 1;;
			print "Find mutation_status: $splitarray[25] for $splitarray[0].\tReturn it to submitter\n";
			return $return_code; 
		}
		elsif ( $splitarray[24] !~ m/valid/i && $splitarray[23] !~ m/verified/i )
		{
			if( $splitarray[8] =~ m/5\'UTR/i  || $splitarray[8] =~ m/3\'UTR/i || $splitarray[8] =~ m/5\'Flank/i || $splitarray[8] =~ m/3\'Flank/ || $splitarray[8] =~ m/IGR/ || $splitarray[8] =~ m/Intron/i )
			{
				$return_code = 1;
				print "Find $splitarray[0]-Mutation_Status:$splitarray[25], Validation_status:$splitarray[24], Verification_status:$splitarray[23], Variant_Classification:$splitarray[8]. Return to submitter.\n";
				return $return_code;
				
			}
		}
	}
  return $return_code;
}
