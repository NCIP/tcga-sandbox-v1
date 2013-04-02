SDRF=/tcgafiles/ftp_auth/distro_ftpusers/tcga4yeo/tumor/lusc/cgcc/broad.mit.edu/genome_wide_snp_6/snp/broad.mit.edu_LUSC.Genome_Wide_SNP_6.mage-tab.1.2001.0/broad.mit.edu_LUSC.Genome_Wide_SNP_6.sdrf.txt 

egrep -c Level_1.23.1002 $SDRF
egrep -c Level_1.51.1005 $SDRF
egrep -c Level_1.60.1005 $SDRF
egrep -c Level_2.51.1005 $SDRF
egrep -c Level_3.51.1005 $SDRF
