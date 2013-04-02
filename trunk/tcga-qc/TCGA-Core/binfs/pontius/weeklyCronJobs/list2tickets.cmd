FILE=$1

if [ -z $FILE ] 
then
echo "Provide directory to be considered ie output.Fri_Mar__9/latest.dateinconsistencies.txt"
exit
fi

cat $FILE  | egrep FILE > remaining.tickets.txt

for ticket in  \
unc.edu_BRCA.AgilentG4502A_07_3.Level_2.8.0.0 \
unc.edu_BRCA.AgilentG4502A_07_3.Level_1.8.0.0 \
unc.edu_LGG.AgilentG4502A_07_3.Level_1.1.0.0 \
unc.edu_LGG.AgilentG4502A_07_3.Level_2.1.0.0 \
unc.edu_LUSC.IlluminaHiSeq_RNASeq.Level_3.1.2.0 \
jhu-usc.edu.UCEC.HumanMethylation27.Level_1.3.0.0 \
jhu-usc.edu.UCEC.HumanMethylation27.Level_2.3.0.0 \
jhu-usc.edu.BRCA.HumanMethylation27.Level_1.3.0.0 \
jhu-usc.edu.BRCA.HumanMethylation27.Level_2.3.0.0 \
jhu-usc.edu.BRCA.HumanMethylation27.Level_1.4.0.0 \
jhu-usc.edu.BRCA.HumanMethylation27.Level_2.4.0.0 \
jhu-usc.edu.LGG.HumanMethylation450.Level_1.1.0.0 \
jhu-usc.edu.LGG.HumanMethylation450.Level_2.1.0.0 \
jhu-usc.edu.GBM.HumanMethylation450.Level_1.1.0.0 \
jhu-usc.edu.GBM.HumanMethylation450.Level_2.1.0.0 \
jhu-usc.edu.GBM.HumanMethylation450.Level_1.2.0.0 \
jhu-usc.edu.GBM.HumanMethylation450.Level_2.2.0.0 \
nationwidechildrens.org_UCEC.tissue_images.Level_1.156.0.0 \
intgen.org_READ.tissue_images.Level_1.139.0.0
do
cat $FILE | egrep $ticket > tickets/$ticket.txt
sort remaining.tickets.txt | egrep -v $ticket > junk
mv junk remaining.tickets.txt
done

for confirmedok in broad.mit.edu_KIRC.Genome_Wide_SNP_6.Level_2.105 \
jhu-usc.edu_UCEC.HumanMethylation450.Level_2.10.0.0
do
sort remaining.tickets.txt | egrep -v $confirmedok > junk
mv junk remaining.tickets.txt
done
