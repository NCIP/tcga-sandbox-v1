# jessica's basic script
# 
# inputs: four classes of files, where do they come from?
#  accessions - these blahblahblah
#  metadata xml - this blahblah
#  exchange.tab - this blahblah
#  tcga_subj.txt - a lookup table on data about patients/study subjects
#   - 
#
# weaken the dependence on directory structure and file location
# add configuration ability; no hardcoded stuff ; look toward the db
# look toward the database and eliminate auxiliary files

for i in *.tar.gz; do 
    tar -xzf $i; 
done
# we're not going to aggregate stuff at the shell level. 
rm *.txt
touch sra_samples.txt
# this parsing can be coupled with that in the parse.pl code
for i in NCBI_SRA_Metadata_TCGA_*; do 
    grep -P "\tTCGA-" $i/SRA_Accessions* | cut -f5,6,10 | sort -u >> sra_samples.txt; 
done
# ff sorts and uniquifies
# why sort here?
sort -u sra_samples.txt > sra_samples_sorted.txt 
# pull out fields from the barcodes 
# (so this is deprecated before it's begun)
# but in future we hit the db with the uuids to get the metadata
perl parse.pl sra_samples_sorted.txt
# parseBAM uses the exchange.tab to obtain the bam file info for 
# report; it just tacks this to end of the sample report
perl parseBAM.pl exchange.tab sra_samples_sorted.txt.clean.txt
perl addDiseaseColumn.pl sra_samples_sorted.txt.clean.txt tcga_subj.txt
# put ari's tumor abbrev lookup here; is this doing what addDiseaseColumn
# is doing?

# finally, open sra_samples_sorted.txt.clean.txt in Excel, bold the headers, and sort by the first date column
# could actually output excel with the right modules! RTF or other markup 
# (html, wiki)