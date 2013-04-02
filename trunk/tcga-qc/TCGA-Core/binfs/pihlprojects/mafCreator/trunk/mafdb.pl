#!/usr/bin/perl
# create a SQLIte db of a maf file for use in generating MAF file test cases

use strict;
use warnings;

use DBI;
use Getopt::Long;

my $help;
my $maffile;
my $dbfile;

printUsage() if (@ARGV <4 or ! GetOptions('help|?'=>\$help, 'm|maf=s'=>\$maffile, 'd|database=s'=>\$dbfile) or defined $help);

unless(-e $dbfile){
	initDB($dbfile);
}

parseMAF($maffile,$dbfile);

###################################################################
#                                                                 #
#                    Subroutines start here                       #
#                                                                 #
###################################################################

sub parseMAF{
	my $file = shift;
	my $dbfile = shift;
	my $dbh = DBI->connect("dbi:SQLite:dbname=$dbfile","","", {});
	my $sql = qq(INSERT INTO maf(Hugo_Symbol, Entrez_Gene_Id, Center, NCBI_Build, Chromosome, Start_Position, End_Position, Strand, Variant_Classification, Variant_Type, 
	Reference_Allele, Tumor_Seq_Allele1, Tumor_Seq_Allele2, dbSNP_RS, dbSNP_Val_Status, Tumor_Sample_Barcode, Matched_Norm_Sample_Barcode,
	Match_Norm_Seq_Allele1, Match_Norm_Seq_Allele2, Tumor_Validation_Allele1, Tumor_Validation_Allele2, Match_Norm_Validation_Allele1, 
	Match_Norm_Validation_Allele2, Verification_Status, Validation_Status, Mutation_Status, Sequencing_Phase, Sequence_Source, Validation_Method,
	Score, BAM_File, Sequencer, Tumor_Sample_UUID, Matched_Norm_Sample_UUID) VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?));
	
	my $sth =$dbh-> prepare($sql) or die "Couldn't prepare statement: " . $dbh->errstr;
	

	
	open(MAF,$file) or die "Can't open $file:$!\n";
	
	while(<MAF>){
		if($_ =~m/#/){
			next;
		}
		elsif($_=~m/Hugo_Symbol/){
			next;
		}
		else{
			chomp($_);
			my ($Hugo_Symbol, $Entrez_Gene_Id, $Center, $NCBI_Build, $Chromosome, $Start_Position, $End_Position, $Strand, $Variant_Classification, $Variant_Type, 
			$Reference_Allele, $Tumor_Seq_Allele1, $Tumor_Seq_Allele2, $dbSNP_RS, $dbSNP_Val_Status, $Tumor_Sample_Barcode, $Matched_Norm_Sample_Barcode,
			$Match_Norm_Seq_Allele1, $Match_Norm_Seq_Allele2, $Tumor_Validation_Allele1, $Tumor_Validation_Allele2, $Match_Norm_Validation_Allele1, 
			$Match_Norm_Validation_Allele2, $Verification_Status, $Validation_Status, $Mutation_Status, $Sequencing_Phase, $Sequence_Source, $Validation_Method,
			$Score, $BAM_File, $Sequencer, $Tumor_Sample_UUID, $Matched_Norm_Sample_UUID) = split(/\t/,$_);
	
			$sth -> execute($Hugo_Symbol, $Entrez_Gene_Id, $Center, $NCBI_Build, $Chromosome, $Start_Position, $End_Position, $Strand, $Variant_Classification, $Variant_Type, 
			$Reference_Allele, $Tumor_Seq_Allele1, $Tumor_Seq_Allele2, $dbSNP_RS, $dbSNP_Val_Status, $Tumor_Sample_Barcode, $Matched_Norm_Sample_Barcode,
			$Match_Norm_Seq_Allele1, $Match_Norm_Seq_Allele2, $Tumor_Validation_Allele1, $Tumor_Validation_Allele2, $Match_Norm_Validation_Allele1, 
			$Match_Norm_Validation_Allele2, $Verification_Status, $Validation_Status, $Mutation_Status, $Sequencing_Phase, $Sequence_Source, $Validation_Method,
			$Score, $BAM_File, $Sequencer, $Tumor_Sample_UUID, $Matched_Norm_Sample_UUID);
		}
	}
	
	$dbh->disconnect;
}
sub initDB{
	my $dbfile = shift;
	my $dbh = DBI->connect("dbi:SQLite:dbname=$dbfile","","", {});
	my $sql=qq(CREATE TABLE maf (Hugo_Symbol TEXT,Entrez_Gene_Id INTEGER,Center TEXT,NCBI_Build TEXT,Chromosome TEXT,Start_Position INTEGER,End_Position INTEGER,
		Strand TEXT, Variant_Classification TEXT,Variant_Type TEXT, Reference_Allele TEXT, Tumor_Seq_Allele1 TEXT,Tumor_Seq_Allele2 TEXT, dbSNP_RS TEXT,
		dbSNP_Val_Status TEXT, Tumor_Sample_Barcode TEXT, Matched_Norm_Sample_Barcode TEXT, Match_Norm_Seq_Allele1 TEXT, Match_Norm_Seq_Allele2 TEXT, Tumor_Validation_Allele1 TEXT,
		Tumor_Validation_Allele2 TEXT, Match_Norm_Validation_Allele1 TEXT, Match_Norm_Validation_Allele2 TEXT, Verification_Status TEXT, Validation_Status TEXT,
		Mutation_Status TEXT, Sequencing_Phase TEXT, Sequence_Source TEXT, Validation_Method TEXT, Score TEXT, BAM_File TEXT, Sequencer TEXT, Tumor_Sample_UUID TEXT, 
		Matched_Norm_Sample_UUID TEXT));
	my $sth =$dbh-> prepare($sql) or die "Couldn't prepare statement: " . $dbh->errstr;
	$sth -> execute();
	$dbh->disconnect;
}

sub printUsage{
	print "Unknown option: @_\n" if ( @_ );
  	print "usage: program [--maf|-m MAF 2.3  File] [--database|-d Database File] [--help|-?]\n";
  exit;
}
