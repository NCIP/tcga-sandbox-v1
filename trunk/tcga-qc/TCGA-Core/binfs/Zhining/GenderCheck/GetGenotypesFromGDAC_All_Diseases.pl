
#! usr/bin/perl -w


print "Enter GDAC user name \n";
my $userName=<stdin>;
chomp $userName;

print "Enter GDAC Password\n";
my $Password=<stdin>;
chomp $Password;   
#Hash in Y probes
my %Probes=Hash1Col(0, 'Y', 'Y-probes.txt');

#loop through diseases
my @Dis=qw(blca brca cesc coadread gbm hnsc kirp lgg lihc luad lusc ov paad prad stad thca ucec);
for(my $i=0; $i<@Dis; $i++)
{
    my $dis=$Dis[$i];
    my $disUp=$dis;
    $disUp=~tr/[a-z]/[A-Z]/;
    my $file="gdac.broadinstitute.org_"."$disUp".".Merge_snp__genome_wide_snp_6__broad_mit_edu__Level_2__birdseed_genotype__birdseed.Level_3.2012012400.1.0.tar.gz";
    my $url="https://tcga-data.nci.nih.gov/tcgafiles/ftp_auth/distro_ftpusers/tcga4yeo/other/gdacs/gdacbroad/"."$dis"."/stddata/2012012400/$file";
    ProcessOneDisease($Password,$userName, $dis, $file, $url);
}


#######################################
sub ProcessOneDisease
{
    my ($pwd, $user, $Disease, $file, $url)=@_;
    DownloadBirdseed($pwd, $user, $url, $file);

    #read birdseed genotypes
    my $outfile="$Disease".'_Y_probes.txt';
    GetYprobeGenotypes($file, $outfile);

    #Predict gender
    my $GenderFile="$Disease".'_Gender.txt';
    open(In, "$outfile") or die "can not open Y probe genotype file\n";
    my @Matrix;
    my $Width=0;
    my $Length=0;
    while(<In>)
    {
        chomp;
        my @row=split("\t", $_);
        push(@Matrix, \@row);
        $Length++;
        $Width=@row;
    }
    close In;

    open(Out, ">$GenderFile") or die "can not  open gender file\n";
    for(my $i=1; $i<$Width; $i=$i+2)
    {
        my $GenotypeCount;
        for(my $j=0; $j<$Length; $j++)
        {
            $GenotypeCount += $Matrix[$j][$i];
        }

        if( ($GenotypeCount/$Length) <-0.8)
        {
            print Out "$Matrix[0][$i]\tFEMALE\n";
        }
        elsif( ($GenotypeCount/$Length) >0.5)
        {
            print Out "$Matrix[0][$i]\tMALE\n";
        }
        else
        {
            print Out "$Matrix[0][$i]\tUnknown\n";
        }
    }
    close Out;
}




######################################################
sub DownloadBirdseed
{
    my ($PWD, $USER, $URL, $FileName)=@_;
    print "start downloading ...\n";
    $PWD="\'"."$PWD"."\'";  # This is not allow in windows and not needed to escape
    system "wget --no-check-certificate --http-user=$USER --http-passwd=$PWD $URL";
    print "Expanding the compressed file...\n";
    system "tar -xzvf $FileName ";
    
}


####################################################
sub GetYprobeGenotypes
{
    my ($dir, $outfile) =@_;
    my $File=$dir;
    $dir=~s/.tar.gz//; #remove .tar.gz
    opendir(DIR, $dir) or die $!;
    my $genotypefile='';
    while (my $FileInDir = readdir(DIR))
    {
        if($FileInDir =~/birdseed/)
        {
            $genotypefile=$FileInDir;
            last;
        }
    }
    closedir(DIR);
    system "mv ./$dir/$genotypefile ./";
    open(In, "$genotypefile") or die "can not open birdseed file\n";

    open(Out, ">$outfile") or die "can not open Y probe output file \n";
    my $header=<In>;
    print Out "$header";
    my $count=0;
    while(<In>)
    {
        my @row=split("\t", $_);
        $count++;
        if ($Probes{$row[0]} eq 'Y')
        {
            print Out "$_";
        }
        if($count%10000==0)
        {
            print "processing the $count -th row\n";
        }
    }
    close In;

    #clean up
    system "rm $File $genotypefile ";
    system "rm -r -f $dir";
}

sub Hash1Col
{
    my ($keyCol, $ConstVal, $file) =@_;

    %Hash=();
    open (Hin, "$file") or die "Can not open the file $file $!\n";
    while (<Hin>)
   {
        my @row=split("\t", $_);
       $mykey =$row[$keyCol];
       chomp $mykey;
       $myval =$ConstVal;
       $Hash{$mykey} =$myval;
    }

    return %Hash;
}
