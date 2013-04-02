#!/bin/bash
echo "Value is $1"
for ((i=1;i<=$1;i++))
do
  wget -q -O - http://tcga-d2-app1.nci.nih.gov:29080/data-browser/search/tcga/?q=disease_abbreviation:GBM
  #perl ./dccQuery.pl 'disease_abbreviation:GBM'
done
