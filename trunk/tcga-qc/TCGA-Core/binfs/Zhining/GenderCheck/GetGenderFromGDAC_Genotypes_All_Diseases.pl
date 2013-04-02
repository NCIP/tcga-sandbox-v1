
#! usr/bin/perl -w


my %Disease=Hash2Col(1,0, 'Gender_Biotab.txt');
my %Gender=Hash2Col(1,2, 'Gender_Biotab.txt');

opendir(DIR, ".") or die "$!\n";
my @files = grep(/_Gender.txt$/,readdir(DIR));
closedir(DIR);

my $outfile="Gender_Biotab_Genomic_Differences.txt";
open (Out, ">$outfile") or die "can not open $outfile\n";
print Out "Diseases\tPatient\tBiotab_Gender\tGenomic_Gender\tAliquotBarcode\n";
for(my $i=0; $i<@files; $i++)
{
    open(In, "$files[$i]") or die "can not open $files[$i]\n";
    while(<In>)
    {
        chomp;
        my @row=split("\t", $_);
        my @aliquot=split('-', $row[0]);
        my $patient=join('-', @aliquot[0..2]);
        my $dis=$Disease{$patient};
        my $biotabGender=$Gender{$patient};
        if($biotabGender ne '' && $biotabGender ne $row[1])
        {
            print Out "$dis\t$patient\t$biotabGender\t$row[1]\t$row[0]\n";
        }        
    }
    close In;
}

close Out;


sub Hash2Col
{
    my ($keyCol, $valCol, $file) =@_;

   my %Hash=();
   open (Hin, "$file") or die "Can not open the hash file $file $!\n";
   while (<Hin>)
   {
        chomp;
        my @row=split("\t", $_);
       $mykey =$row[$keyCol];
       $myval =$row[$valCol];
       $Hash{$mykey} =$myval;
    }
    close Hin;
    return %Hash;
}



