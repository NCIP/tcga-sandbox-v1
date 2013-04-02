#!/usr/bin/perl -w

my @Dis=qw(blca brca cesc cntl coad dlbc esca gbm hnsc kirc kirp laml lcll lgg lihc lihn lnnh luad lusc ov paad prad read sarc skcm stad thca ucec);
foreach my $dis (@Dis)
{
    my $url='http://tcga-data.nci.nih.gov/tcgafiles/ftp_auth/distro_ftpusers/anonymous/tumor/'."$dis".'/bcr/minbiotab/clin/clinical_patient_public_'."$dis".'.txt';
    system "wget $url";
}

my $OutFile='Gender_Biotab.txt';
open (Out, ">$OutFile") or die "can not write outptu file";
foreach my $dis(@Dis)
{
    my $InFile='clinical_patient_public_'."$dis".'.txt';
    if(-e $InFile)
    {
          open (In, "$InFile") or die "No patient data availabel\n";
 
        my $header=<In>;
        my @GenderPos=split("\t", $header);
        my $genderPos='';
        for(my $i=0; $i<@GenderPos; $i++)
        {
            if($GenderPos[$i] =~/gender/i)
            {
                $genderPos=$i;
            }
        }
        print "$dis\t$genderPos\n";

        while(<In>)
        {
            chomp;
            my @row=split("\t", $_);
            if($genderPos eq '')
            {
                print Out "$dis\t$row[0]\tNULL\n";
            }
            else
            {
                print Out "$dis\t$row[0]\t$row[$genderPos]\n";
            }
        }
        close In; 
     system "rm $InFile";
    }
}

close Out;

