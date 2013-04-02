#!/usr/bin/perl



BEGIN {push @INC, "/h1/pontiusj/usr/local/lib/perl5/site_perl/5.8.5";}

sub GetRegression(){
## get regression
    my %xmap=%{$_[0]};
    my %ymap=%{$_[1]};

    my (@xValues, @yValues);
    foreach my $probe(keys %xmap){
	if(exists $ymap{$probe}){
	    push @xValues, $xmap{$probe};
	    push @yValues, $ymap{$probe};
	}
    }
    
    use Statistics::LineFit;
    my $lineFit = Statistics::LineFit->new();
    $lineFit->setData (\@xValues, \@yValues) or die "Invalid data";
    
    my ($intercept, $slope) = $lineFit->coefficients();
    defined $intercept or die "Can't fit line if x values are all equal";
    my $result = sprintf("%d\t%f\t%f\t%f",
			 (scalar @xValues), 
			 $slope, $intercept, $lineFit->rSquared());
    return $result;    
    my $meanSquaredError = $lineFit->meanSqError();
    my $durbinWatson = $lineFit->durbinWatson();
    my $sigma = $lineFit->sigma();
    my ($tStatIntercept, $tStatSlope) = $lineFit->tStatistics();
    my @predictedYs = $lineFit->predictedYs();
    my @residuals = $lineFit->residuals();
    my ($varianceIntercept, $varianceSlope) = $lineFit->varianceOfEstimates();


}


sub ParseArchiveName{
    ## name has to be basename
    my $archive=$_[0];
    my @x=split(/\./, $archive);
    my $revision=$x[-2];
    my $submission=$x[-3];
    my $level=$x[-4];
    my $platform=$x[-5];
    my $study=$x[-6];
    $study=~s/..*_//;    
    my ($center)=split(/_/, $archive);
    return ($center, $study, $platform,$level,$submission, $revision);
}

sub GetConnection{
    use DBI;
    my $dbi=DBI->connect("dbi:Oracle:host=ncidb-tcgas-p.nci.nih.gov;sid=TCGAPRD;port=1652","readonly/read1234","");
    return $dbi;
}


sub GetOracleResponse{
    use DBI;

    my $command = $_[0];
    my $dbi=GetConnection();
    my $sth = $dbi->prepare($command);
    $sth->execute( );    
    #print $command."--------\n";
    my @output;
    while ( my @row = $sth->fetchrow_array ) {
	#print join "\t",@row,"\n";
	push @output, join "\t", @row;
    }    
    return @output;
}

sub AWG_summary{

    ##################################################################
    ## Shelley's command to get all archive associated with a study
    ##################################################################

    my $study =$_[0];
    $study=uc($study);
    my $command = <<END;
    WITH bcr AS
   (SELECT DISTINCT sb.shipped_biospecimen_id,a.serial_index as batch_number
    FROM   dcccommon.shipped_biospecimen sb, dcccommon.shipped_biospecimen_file sbf, dcccommon.file_to_archive fa, dcccommon.archive_info a, dcccommon.center c, dcccommon.disease d
    WHERE  sb.shipped_biospecimen_id = sbf.shipped_biospecimen_id
    AND    sbf.file_id = fa.file_id
    AND    fa.archive_id = a.archive_id
    AND    a.center_id = c.center_id
    AND    c.center_type_code = 'BCR'
    AND a.is_latest = 1
    AND    a.disease_id = d.disease_id and d.disease_abbreviation = 'INSERT_DISEASE_CODE')   
    SELECT DISTINCT
          SUBSTR (built_barcode, 0, 12) AS patient_barcode,
          SUBSTR (built_barcode, 0, 16) AS sample_barcode,
          built_barcode AS aliquot_barcode,
          sb.uuid AS aliquot_uuid,
          dt.name AS datatype,
          p.platform_name,
          fa.file_location_url,
          at.data_level,
          a.archive_name,
          bcr.batch_number,
          a.date_added
     FROM dcccommon.shipped_biospecimen sb,
         dcccommon.disease d,
         dcccommon.center c,
         dcccommon.platform p,
         dcccommon.shipped_biospecimen_file sbf,
         dcccommon.file_to_archive fa,
         dcccommon.archive_info a,
         dcccommon.archive_type at,
         dcccommon.data_type dt,
         dcccommon.bcr
   WHERE sb.shipped_biospecimen_id = sbf.shipped_biospecimen_id
   AND   sbf.file_id = fa.file_id
   AND   fa.archive_id = a.archive_id
   AND   a.disease_id = d.disease_id
   AND   d.disease_abbreviation = 'INSERT_DISEASE_CODE'
   AND   a.archive_type_id = at.archive_type_id
   AND   a.platform_id = p.platform_id
   AND   p.base_data_type_id = dt.data_type_id
   AND   sb.SHIPPED_BIOSPECIMEN_ID = bcr.shipped_biospecimen_id(+)
   AND a.is_latest = 1        
   ORDER BY built_barcode,
         dt.name,
         p.platform_name,
         data_level
END

    $command=~s/INSERT_DISEASE_CODE/$study/g;
    #print $command."\n";
    my @lines = GetOracleResponse($command);
    return @lines;
}

sub ScreenFileType{
    my $file=$_[0];


    ###############################################################################################
    ## helps to categorize files by reducing their name to a string representing their filetype
    ## returns filename template and integer
    ## integer = 0 for filetypes that are columns
    ## integer = 1 for filetypes that are text or binary, pdf etc.
    ###############################################################################################

    use File::Basename;
    my $basename=basename $file;
    my @formatlistwithheaders=ArchiveFileNameSuffixWithHeaders();    
    if((scalar @formatlistwithheaders) == 0 ){
	print "LIB FAIL reading ArchiveFileNameTemplates\n";
	die;
    }
    my @formatlistwithoutheaders=ArchiveFileNameSuffixWithoutHeaders();    
    foreach my $suffix(@formatlistwithheaders){
	if($basename =~m/$suffix$/){
	    return ($suffix, 1);	    
	}
    }
    foreach my $suffix(@formatlistwithoutheaders){
	if($basename=~m/$suffix$/){
	    return ($suffix, 0);	    
	}
    }
    
    my @a=split(/\./, $basename);    
    my $tag;
    if(($a[2] eq "HuEx-1_0-st-v2") ||
       ($a[2] eq "HumanMethylation27") ||
       ($a[2] eq "HumanMethylation450")||
       ($a[2] eq "IlluminaDNAMethylation_OMA001_CPI") ||
       ($a[2] eq "IlluminaDNAMethylation_OMA002_CPI") ||
       ($a[2] eq "IlluminaDNAMethylation_OMA003_CPI")){
	$tag=$a[2].".\[revision\].".$a[4];
	return ($tag, 1);	
    } elsif(($a[2] eq "Human1MDuo") ||
	    ($a[2] eq "HumanHap550")){
	$tag=$a[2].".\[revision\].".$a[-2].".".$a[-1];
	return ($tag, 1);	   
    } elsif(($a[2] eq "MDA_RPPA_Core" ) &&
	    ($a[4] =~m /Level_/      )){
	return ("$a[2].$a[3].$a[4]", 1);    
    }   


    ## if file has no descernible type, return full filename and 1 ie assume is tsv
    return ($file, 1);
}


sub ArchiveFileNameSuffixWithoutHeaders{
    my @a_info = (
		  
		  "README_DCC.txt",
		  "README.txt",
		  "README",
		  "DESCRIPTION.txt",
		  "DESCRIPTION",
		  "MANIFEST.txt",
		  "MANIFEST",
		  "CHANGES_DCC.txt",
		  "CHANGES_DCC",
		  "CHANGES.txt",
		  "DISCLAIMER.txt",
		  "DISCLAIMER",
		  ".pdf",
		  ".svs",
		  ".xml",
		  ".CEL",
		  ".maf",
		  ".tif",
		  ".fsa",
		  ".idat",
		  ".vcf",
		  ".wig.txt",
		  ".wig.bz2",
		  ".wig",
		  "rename.sh",
		  ".gz");
    return @a_info;
}

sub ArchiveFileNameSuffixWithHeaders{
    ## files in data archives that have headers
    my @a_tmp =(
		"txt_lmean.out.logratio.gene.tcga_level3.data.txt",
		"trimmed.annotated.exon.quantification.txt",
		"no_outlier.copynumber.data.txt",
		"after_5NN.copynumber.data.txt", 
		"tangent.copynumber.data.txt", 
		"byallele.copynumber.data.txt",
		"isoform.quantification.txt",
		"spljxn.quantification.txt",
		"gene.tcga_level3.data.txt",
		"mirna.quantification.txt",
		"raw.copynumber.data.txt",
		"gene.quantification.txt",
		"exon.quantification.txt",
		"Delta_B_Allele_Freq.txt",
		"_lowess_normalized.tsv",
		"alleleSpecificCN.dat",
		"nocnv_hg18.seg.txt",
		"nocnv_hg19.seg.txt",
		"ismpolish.data.txt",
		"Unpaired_LogR.txt",
		"birdseed.data.txt",
		"B_Allele_Freq.txt",
		"intensities.dat",
		"Paired_LogR.txt",
		"Normal_LogR.txt",
		"level2.data.txt",
		"level3.data.txt",
		"segmented.dat",
		"segnormal.txt",
		"Genotypes.txt",
		"genotype.dat",
		"hg18.seg.txt",
		"hg19.seg.txt",
		"pairedcn.dat",
		"seg.data.txt",
		"_CBS_out.txt", 
		"_Segment.tsv",
		"_GCN_V3.mat",
		"FIRMA.txt",
		"MAS5.txt",
		"gene.txt",
		"loh.txt",
		"tsv");
    return @a_tmp;
}

sub Platform2AssemblyDCCCuration{

    ##
    ## curated list of platforms and their associated genome assemblies
    ##
    my %info = (
                #/tcgafiles/ftp_auth/distro_ftpusers/anonymous/other/integration/results/AgilentG4502A_07_01 = 36.1
		agilentg4502a_07_1=>"NA(gene)",
		agilentg4502a_07_2=>"NA(gene)",
		agilentg4502a_07_3=>"NA(gene)",
		bio=>"NA(bio)",
		
		#/tcgafiles/ftp_auth/distro_ftpusers/anonymous/other/integration/adfs/tcga zmore mskcc.org_CGH-1x1M_G4447A.ADF.txt.zip
		"cgh-1x1m_g4447a"=>   "36.1",
		diagnostic_images=>   "NA(images)",
		genome_wide_snp_6=>   "36",
		"hg-cgh-244a"=>       "36",
		"hg-cgh-415k_g4124a"=>"36",
		"hg-u133_plus_2"=> "NA(ProbeID)",
		"h-mirna_8x15k"=>  "NA(miRNAID)",
		"h-mirna_8x15kv2"=>"NA(miRNAID)",
		
		#/tcgafiles/ftp_auth/distro_ftpusers/anonymous/other/integration/results/ht_hg-u133a = 36.1, but no chromosome coordinates in level3 archives
		"ht_hg-u133a"=>    "NA(ProbeID)",
		"huex-1_0-st-v2"=> "NA(ProbeID)",

		#/tcgafiles/ftp_auth/distro_ftpusers/anonymous/other/integration/adfs/tcga zmore hudsonalpha.org_TCGA_Illumina1MDuo.adf.zip
		## confirmed using blast to be consistent with build 36
		human1mduo=>          "36",

		## SAMPLE SNPS MAP TO 36.ncbi.REFERENCE /tcgafiles/ftp_auth/distro_ftpusers/anonymous/other/integration/adfs/tcga zmore hudsonalpha.org_TCGA_Illumina_HumanHap550K.adf.zip 
		humanhap550=>"36",
		humanmethylation27=> "hg18",
		humanmethylation450=>"hg18",
		illuminadnamethylation_oma002_cpi=>"36.1",
		illuminadnamethylation_oma003_cpi=>"36.1",
		#illuminaga_dnaseq=>"36or37.see.maf.vcf",
		illuminaga_mirnaseq=>"GRCh37-lite",
		illuminaga_rnaseq=>  "hg19(GRCh37)",
		## sdrf file
		illuminahiseq_dnaseqc=> "hg18(GRCh36)",
		illuminahiseq_mirnaseq=>"GRCh37-lite",
		illuminahiseq_rnaseq=>  "hg19(GRCh37)",
		mda_rppa_core=>         "NA(protein)",
		microsat_i=>            "NA(usat)",
		pathology_reports=>     "NA(reports)",
		
		##solid_dnaseq depends on center
		## but all seem to be 18/36
		## broad hg18
		## ucsc  hg36.1
		## hgsc   36
		solid_dnaseq=>  "hg36.1",		
		tissue_images=> "NA(images)"
		);		
    
    return %info;
}

sub VCFdir2Assembly{
    my $dir=$_[0];    
    my %assemblies;
    ## process any vcf files
  VCF: foreach my $file(<$dir/*.vcf>){	
      open(FIN, $file);
      while(my $line=<FIN>){
	  $line=~s/\r//;
	  chomp $line;
	  if($line=~m/\#\#reference/){
	      $line=~s/..*ID=//;
	      $line=~s/\,..*//;
	      $line=~s/\s..*//;
	      $line=~s/\"//g;
	      $assemblies{$line}++;
	      next VCF;
	  }
      }	
  }	
    return %assemblies;
}

sub TSVdir2Assembly{  
    my $dir=$_[0];    
    my %assemblies;
    foreach my $file(<$dir/*tsv>){	
	open(FIN, $file);
	my $header = <FIN>;
	chomp $header;
	my $line=<FIN>;
	chomp $line;
	my @a=split(/\t/, $header);
	my @b=split(/\t/, $line);
	for(my $i=0; $i< scalar @a; $i++){
	    if($a[$i]=~/NCBI_Build/){
		$assemblies{$b[$i]}++
	    }
	}
    }	
    return %assemblies;
}

sub SDRFdir2Assembly{   
    my $dir=$_[0];    
    my %assemblies;
    foreach my $file(<$dir/*sdrf*>){	
	open(FIN, $file);
	my $header = <FIN>;
	chomp $header;
	my $line=<FIN>;
	chomp $line;
	my @a=split(/\t/, $header);
	my @b=split(/\t/, $line);
	for(my $i=0; $i< scalar @a; $i++){
	    if($a[$i]=~/eference/){
		$assemblies{$b[$i]}++;
	    }
	}
    }	
    return %assemblies;
}

sub MAFdir2Assembly{
    my $dir=$_[0];
    my %assemblies;
  MAF: foreach my $file(<$dir/*.maf>){
      open(FIN, $file);
      my $line1=<FIN>;
      $line1=~s/\r//;
      chomp $line1;
      while($line1=~m/\#/){
	  $line1=<FIN>;
	  $line1=~s/\r//;
	  chomp $line1;
      }	  
      my $line2=<FIN>;
      $line2=~s/\r//;
      chomp $line2;
      my @a=split(/\t/, $line1);
      my @b=split(/\t/, $line2);
      for(my $i=0; $i< scalar @a; $i++){
	  if(($a[$i] eq "NCBI_Build") ||
	     ($a[$i] eq "NCBI_BUILD")){
	      $assemblies{$b[$i]}++;
	      next MAF;
	  }
      }
  }
    return %assemblies;
}


sub LatestDir2Assembly{

    
    my %is_latest = Archive2LatestLocation();
    foreach my $archive(keys %is_latest){
	my @tars=(keys %{$is_latest{$archive}});
	my $dir=$tar[0];
	$dir=~s/.tar.gz//;
	
	if($dir =~ m/mage-tab/){
	    my $subdir=dirname $dir;
	    my %a = SDRFdir2Assembly($dir);
	    foreach my $b(keys %a){
		$b=~s/ //g;
		$dir2assembly{$subdir}{$b}{SDRF}=1;
	    }
	} else{
	    
	    #print "Loading MAF\n";
	    my %a = MAFdir2Assembly($dir);
	    foreach my $b(keys %a){
		$b=~s/ //g;
		$dir2assembly{$dir}{$b}{MAF}=1;
	    }
	    #print "Loading VCF\n";
	    %a = VCFdir2Assembly($dir);
	    foreach my $b(keys %a){
		$b=~s/ //g;
		$dir2assembly{$dir}{$b}{VCF}=1;
	    }
	    #print "Loading TSV\n";
	    %a = TSVdir2Assembly($dir);
	    foreach my $b(keys %a){
		$b=~s/ //g;
		$dir2assembly{$dir}{$b}{TSV}=1;
	    }
	}	
    }
    return %dir2assembly;
}


sub SamplePortionvsSlideBarcode{
    ## test consistency between sample portion and slidebarcode
    my $sampleportion=$_[0];
    my $slidebarcode=$_[1];

    my @a_sampleportion = split(/\-/, $sampleportion);
    my @a_slidebarcode = split(/\-/, $slidebarcode);
    ## check TCGA-XX-1234, ie tss, participant, sample
    unless(($a_sampleportion[0] eq "TCGA") &&
	   ($a_sampleportion[0] eq  $a_slidebarcode[0]) &&
	   ($a_sampleportion[1] eq  $a_slidebarcode[1])){
	return "TSS";
    }

    unless($a_sampleportion[2] eq  $a_slidebarcode[2]){
	return "PARTICIPANT";
    }

    unless($a_sampleportion[3] eq  $a_slidebarcode[3]){
	return "SAMPLE";
    }

    ## now compare portion (_[4])
    unless($a_sampleportion[4] eq  $a_slidebarcode[4]){

	##parse portion and slide designation
	my $n1=substr($a_slidebarcode[4], 0,1);
	my $n2=substr($a_slidebarcode[4], 1,1);
	my $a1=substr($a_slidebarcode[5], 0,1);

	my $m1=substr($a_sampleportion[4], 0,1);
	my $m2=substr($a_sampleportion[4], 1,1);




	## for cases when portion is not a subportion of n2
	unless(($n1 eq "0") &&
	       ($n2 eq $m1)){
	    unless(
		    ## adjust for case of bottom slide being top of next portion
		   (($a1 eq "B") && ($n1 eq "0") && (($n2+1) == $m1))  ||		   
		   (($a1 eq "B") && ($n1 eq "0") && ($m1 eq "0") && (($n2+1) == $m2))  ||		   
		   ## adjust for case of top slide being bottom of previous portion
		   (($a1 eq "T") && ($n1 eq "0") && (($n2-1) == $m1)) ||
		   (($a1 eq "T") && ($n1 eq "0") && ($m1 eq "0") && (($n2-1) == $m2))
		   ){

		return "PORTION";	
	    }		
	}	
    }
    return "NONE";
}


sub ExtractDirFileFromSdrf{

    my ($sdrf, $file_template)=@_;
    my %info;
    #open(FIN, "cat $sdrf | tr -s '\r' '\n' |") || die;
    open(FIN, $sdrf) || die;
    ############################
    ## get column numbers
    my $line=<FIN>;
    chomp $line;
    my @cols=split(/\t/, $line);

    ############################
    ## load info hash
    while($line=<FIN>){
	chomp $line;
	my @a=split(/\t/, $line);

	my $extract;
	my $file;
	my $dir;
	for(my $i=0; $i< scalar @cols; $i++){
	    if($cols[$i] eq "Extract Name"){
		$extract=$a[$i];
	    } elsif(($a[$i] =~ m/$file_template/) &&
	       ($cols[$i] eq "Derived Array Data Matrix File")){
		$file=$a[$i];
	    }elsif(($file) &&
		   !($dir) &&
		   ($cols[$i] eq "Comment [TCGA Archive Name]")){
		$dir = $a[$i];
	    }
	}
	$info{$extract}{$file}=$dir;
    }
    return %info;
}


sub Sdrf2FileList{

    ## Syntax. First argument is sdrf file
    ## second argument is hash of other arguments
    use File::Basename;

    ## get all files in the sdrf file    
    my $sdrf=$_[0];
    my %module_args = %{$_[1]};
    my $dir= dirname $sdrf;
    chomp $dir;

    $dir= dirname $dir;
    chomp $dir;

    my %info;
    ##
    ## allow for both kinds of end of line
    ##
    open(FIN, "cat $sdrf | tr -s '\r' '\n' |") || die;
    my $line=<FIN>;
    #print $line;
    chomp $line;
    my @cols=split(/\t/, $line);

    ## some sdrf files have duplicate lines
    ## tally lines in hash %repeatlines to allow skip over of repeatlines
    my %repeatlines;

  SDRFLINE:    while($line=<FIN>){
      next SDRFLINE unless($line=~m/Level_/);	
      
      
      ## verify that line contains at least one of the stringmatches
      if(exists $module_args{stringmatch}){
	  my $c=0;
	TERM: foreach my $term(keys %{$module_args{stringmatch}}){
	    next TERM if(index($line, $term) < 0);
	    $c=1;
	    last;
	}	  
	  if($c==0){
	      next SDRFLINE;
	  }
      }

      #print "--------\n";
      ## skip over duplicate lines
      $repeatlines{$line}++;
      next unless($repeatlines{$line}==1);

      #print $line;
      chomp $line;
      my @a=split(/\t/, $line);
      my ($archive, $datafile);
      for(my $i=0; $i< scalar @cols; $i++){

	  ## note, archive file NAME is before Archive Name
	  if(
	     ($cols[$i] =~ m/Data File/) ||
	     ($cols[$i] =~ m/Data Matrix File/) ||
	     ($cols[$i] =~ m/Derived Data File/) ||
	     ($cols[$i] =~ m/Derived Array Data File/) ||
	     ($cols[$i] =~ m/Derived Array Data Matrix File/) 
	     ){
	      $datafile=$a[$i];
	      #print "$datafile in sdrf\n";
	  }
	  if($cols[$i] eq "Comment [TCGA Archive Name]"){
	      $archive=$a[$i];
	  }
	  
	  ## test
	  if($cols[$i] eq "Comment [TCGA Archive Name]"){
	      
	      next if($archive  eq "->");
	      next if($datafile eq "->");
	      next unless($datafile);
	      
	      my $b=$archive;		
	      ## test this file in this $archive
	      my $file=$b."/".$datafile;		
	      $info{$file}++;
	  }	    
      }
  }
    close(FIN);
    return (keys %info);
}

sub SearchLive{
    
    my @match = @_;
    unless(@match){
	print "NO TERMS SENT TO SearchLive\n";
	return;
    }
    
    my @filelist;
    my %latest =  Archive2LatestLocation();
    foreach my $archive(keys %latest){
      TAR: foreach my $tar(keys %{$latest{$archive}}){
	  foreach my $m(@match){
	      if(index($tar, $m) >= 0 ){
		  push @filelist, $tar;
		  next TAR;
	      }
	  }
      }
    }
    return @filelist;
}


sub FindFiles{
    ##
    ## call function with array of terms
    ## function will return array of all files in the tumor directories that include all those terms in its name
    ##
    my @terms = @{$_[0]};
    use File::Find;

    my @dirs=("/tcgafiles/ftp_auth/distro_ftpusers/anonymous/tumor",
	      "/tcgafiles/ftp_auth/distro_ftpusers/tcga4yeo/tumor");
    
    my @files;
    foreach my $dir ( @dirs){
	find(\&find_d, $dir);
    }
    return @files;

    ############################################
    ## return list of files that match strings    
    ############################################
  
    sub find_d {
	my $file = $File::Find::name;
	$file =~ s/\\/\//g;  
	foreach my $t(@terms){
	    return if (index($file, $t) < 0);
	}
	push @files, $file;	
    }

}


sub File2BarcodefromSdrf{

    ## returns hash of filename to its related barcode (barcode, ie first column of sdrf file)
    my $file=$_[0];
    my %info;
    open(FIN_sdrf, $file);
    my $line=<FIN_sdrf>;
    chomp $line;
    my @headers=split(/\t/, $line);    
    while($line=<FIN_sdrf>){
	chomp $line;
	my @a=split(/\t/, $line);
	for(my $i=0; $i< scalar @headers; $i++){
	    if($headers[$i] =~ m/File/){
		$info{$a[$i]}=$a[0];
	    }
	}
    }
    close(FIN_sdrf);
    return %info;
}


sub Sdrf2Columns{
    my $file=$_[0];
    open(FIN_sdrf, $file);
    my $line=<FIN_sdrf>;
    chomp $line;
    my @headers=split(/\t/, $line);
    while($line=<FIN_sdrf>){
	chomp $line;
	my @a=split(/\t/, $line);
	for(my $i=0; $i< scalar @headers; $i++){
	    print join("\t", $headers[$i], $a[$i],"\n");
	}
    }
    close(FIN_sdrf);
}


sub GetCancers{

    my @list;
    use DBI;
    my $dbi=GetConnection();
    my $cmd = "SELECT disease_abbreviation from DCCCOMMON.disease ";
    my $sth = $dbi->prepare($cmd);
    $sth->execute( );
    while ( my @row = $sth->fetchrow_array ) {
	my $abbr =  lc($row[0]);
	push @list, $abbr;
    }

    return @list;
}



sub Archive2LatestLocation{


    
    my %info;
    my $cmd = "SELECT archive_name, deploy_location, secondary_deploy_location from dcccommon.archive_info WHERE is_latest = 1 AND deploy_status LIKE 'Available' ";    


    my @lines = GetOracleResponse($cmd);
    foreach my $line (@lines){
	my ($archive, $primary, $secondary) = split(/\t/, $line);
	$info{$archive}{$primary}=1;	
	if($secondary){
	    $info{$archive}{$secondary}=1;		
	}

    }
    return %info;    
    
}

sub GetLatest{

    ##
    ## NOTE THIS IS DEPRECATED. NOT ALL mage-tab in tcga4yeo are in secondary_deploy_location
    ## INSTEAD USE ARCHIVE2LATESTLOCATION
    my %info;
    my $cmd = "SELECT deploy_location, secondary_deploy_location from dcccommon.archive_info WHERE is_latest = 1 AND deploy_status LIKE 'Available' ";	
    my @lines = GetOracleResponse($cmd);
    foreach my $line (@lines){
	my ($tar, $secondary) = split(/\t/, $line);
	$info{$tar}=1;	
	if($secondary){
	    $info{$secondary}=1;		
	}

    }
    return %info;    
}

sub GetArchiveInfo{

    my @tags = @{$_[0]};
    my $cmd = "SELECT deploy_location, secondary_deploy_location, ";
    $cmd.= join ",", @tags;
    $cmd.=" from dcccommon.archive_info  ";	
    my @lines = GetOracleResponse($cmd);
    return @lines;
}

sub GetAllArchives{
    my %info;
    my $cmd = "SELECT deploy_location, secondary_deploy_location from dcccommon.archive_info  ";	
    my @lines = GetOracleResponse($cmd);
    foreach my $line (@lines){
	my ($tar, $secondary) = split(/\t/, $line);
	$info{$tar}=1;	
	if($secondary){
	    $info{$secondary}=1;		
	}
    }
    return %info;    
}




sub Tar2BatchRevision{
    my $tar = $_[0];
    my @a=split(/\./, $tar);
    my $batch=$a[-5];
    my $revision=$a[-4];
    my $n=scalar @a;
    $n-=6;
    my $base=join(".", @a[0..$n]);
    
    return ($base, $batch, $revision);
}


1;
