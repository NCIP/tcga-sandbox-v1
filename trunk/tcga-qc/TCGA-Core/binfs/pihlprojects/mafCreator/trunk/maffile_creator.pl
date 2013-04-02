#!/usr/bin/perl
# create a SQLIte db of a maf file for use in generating MAF file test cases

use strict;
use warnings;

use DBI;
use Getopt::Long;
use List::Util qw(first);

###############################
#                             #
#    Program Variables        #
#                             #
###############################

my $help;
my $maffile;
my $field;
my $value;
my $dbfile;
my $realvalue = "notset";

my @field_array = ('Hugo_Symbol', 'Entrez_Gene_Id', 'Center', 'NCBI_Build', 'Chromosome', 'Start_Position', 'End_Position', 'Strand', 'Variant_Classification', 'Variant_Type', 
			'Reference_Allele', 'Tumor_Seq_Allele1', 'Tumor_Seq_Allele2', 'dbSNP_RS', 'dbSNP_Val_Status', 'Tumor_Sample_Barcode', 'Matched_Norm_Sample_Barcode',
			'Match_Norm_Seq_Allele1', 'Match_Norm_Seq_Allele2', 'Tumor_Validation_Allele1', 'Tumor_Validation_Allele2', 'Match_Norm_Validation_Allele1', 
			'Match_Norm_Validation_Allele2', 'Verification_Status', 'Validation_Status', 'Mutation_Status', 'Sequencing_Phase', 'Sequence_Source', 'Validation_Method',
			'Score', 'BAM_File', 'Sequencer', 'Tumor_Sample_UUID', 'Matched_Norm_Sample_UUID');
			
my %field_hash = ('Hugo_Symbol' => 1, 'Entrez_Gene_Id' => 1, 'Center' => 1, 'NCBI_Build' => 1, 'Chromosome' => 1, 'Start_Position' => 1, 'End_Position' => 1, 'Strand' => 1, 'Variant_Classification' => 1, 'Variant_Type' => 1, 
			'Reference_Allele' => 1, 'Tumor_Seq_Allele1' => 1, 'Tumor_Seq_Allele2' => 1, 'dbSNP_RS' => 1, 'dbSNP_Val_Status' => 1, 'Tumor_Sample_Barcode' => 1, 'Matched_Norm_Sample_Barcode' => 1,
			'Match_Norm_Seq_Allele1' => 1, 'Match_Norm_Seq_Allele2' => 1, 'Tumor_Validation_Allele1' => 1, 'Tumor_Validation_Allele2' => 1, 'Match_Norm_Validation_Allele1' => 1, 
			'Match_Norm_Validation_Allele2' => 1, 'Verification_Status' => 1, 'Validation_Status' => 1, 'Mutation_Status' => 1, 'Sequencing_Phase' => 1, 'Sequence_Source' => 1, 'Validation_Method' => 1,
			'Score' => 1, 'BAM_File' => 1, 'Sequencer' => 1, 'Tumor_Sample_UUID' => 1, 'Matched_Norm_Sample_UUID' => 1);

printUsage() if (@ARGV <8 or ! GetOptions('help|?'=>\$help, 'm|maf=s'=>\$maffile, 'd|database=s'=>\$dbfile,
	'f|field=s'=>\$field, 'v|value=s'=>\$value, 'r|realvalue=s'=> \$realvalue) or defined $help);
	
unless(-e $dbfile){
	print("Invalid database file:\t$dbfile\n");
	exit;
}

unless (exists $field_hash{$field}){
	print("Invalid database field:\t$field\n");
	exit;
}

open(OUTPUT, ">", $maffile) or die "Can't open $maffile: #!\n";
my $index = first{$field_array[$_] eq $field}0..$#field_array;

printHeader(\@field_array,\*OUTPUT);
printRows($value,$realvalue,$index,\*OUTPUT);




###################################################################
#                                                                 #
#                    Subroutines start here                       #
#                                                                 #
###################################################################

sub buildQuery{
	my $field = shift;
	my $hash = shift;
	my $query = "SELECT";
	my $queryend = " FROM maf";
	
	foreach my $dbfield (keys %$hash){
		if($field eq $dbfield){
			next;
		}
		elsif($query eq "SELECT"){
			$query = $query." $dbfield";
		}
		else{
			$query = $query.", $dbfield"; 
		}
	}
	
	$query = $query.$queryend;
	return $query;
}

sub printHeader{
	my $array = shift;
	my $fh = shift;
	my $header_string = "#version 2.4\n";
	foreach (@$array){
		if($_ eq "Hugo_Symbol"){
			$header_string = $header_string.$_;
		}
		else{
			$header_string = $header_string."\t".$_;
		}
	}
	$header_string = $header_string."\n";
	print($fh $header_string);
}

sub printRows{
	my $value = shift;
	my $realvalue = shift;
	my $index = shift;
	my $fh = shift;
	
	my $dbh = DBI->connect("dbi:SQLite:dbname=$dbfile","","", {});
	my $sql = "SELECT * FROM maf";
	my $sth =$dbh-> prepare($sql) or die "Couldn't prepare statement: " . $dbh->errstr;
	$sth -> execute();
	while (my @row = $sth->fetchrow_array()){
		if($realvalue eq "ALL"){
			splice(@row,$index,1,$value); #replaces all the values in that column
		}
		elsif($row[$index] eq $realvalue){
			splice(@row,$index,1,$value); #replaces only the specified value in the column
		}
		for(my $i=0;$i<=$#row;$i++){
			if($i == $#row){
				print($fh $row[$i]."\n");
			}
			else{
				print($fh $row[$i]."\t");
			}
		}
	}

}

sub printUsage{
	print "Unknown option: @_\n" if ( @_ );
  	print "usage: maffile_creator.pl  [--maf|-m OUTPUT MAF 2.4  File] [--database|-d Database File] [--field|-f Database Field to replace]
  		[--value|-v Value to use in Database Field] [--realvalue|-r Indivdial value to replace (ALL to replace all)][--help|-?]\n";
  exit;
}
