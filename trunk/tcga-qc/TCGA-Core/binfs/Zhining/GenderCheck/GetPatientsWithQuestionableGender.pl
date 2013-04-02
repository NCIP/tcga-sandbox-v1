#!/usr/bin/perl

use lib "c:/perlmodule/bin";
use Tk;

my $mw=MainWindow->new;
my $infile=$mw->getOpenFile( ); 
chomp $infile;
open(In, "$infile") or die "can not open $infile $! \n";

my %PatientCount; #does this patient shows in both tumor and normal samples


while (<In>)
{
    chomp;
    my @row=split "\t", $_;
    $PatientCount{$row[1]}++;
}
close In;

my %Genotype=Hash1Row(0, 'Genotype_AliquotBarcode.txt');
my $outfile ="$infile";
$outfile=~ s/\.txt//i;
$outfile .='_out.txt';
open(Out, ">$outfile") or die "can not open $outfile $! \n";
print Out "Diseases\tPatientBarcode\tBiotab_Gender\tGenomicGender\tAliquotBarcode\tGenotypeEvidence\n";
open(In, "$infile") or die "can not open $infile $! \n";
while (<In>)
{
    chomp;
    my @row=split "\t", $_;
    if($PatientCount{$row[1]} ==2 && $row[2] ne 'NULL')
    {
        print Out "$_\t$Genotype{$row[4]}\n";
    }
}
close In;
close Out;

####################
sub Hash1Row
{
    my ($keyCol, $file) =@_;

   my %Hash=();
   open (Hin, "$file") or die "Can not open the hash file $file $!\n";
   while (<Hin>)
   {
       chomp;
       my @row=split("\t", $_);
       $mykey =$row[$keyCol];
       $myval =$_;
       $Hash{$mykey} =$myval;
    }
    close Hin;
    return %Hash;
}

