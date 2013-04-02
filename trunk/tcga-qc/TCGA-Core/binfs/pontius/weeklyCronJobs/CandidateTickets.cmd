FILE=$1
## go through pending tickets file and select candidates for JIRA tickets


## skip sdrf complaints of idat file
cat $FILE | egrep -v idat | \
egrep -v flagged  | \
egrep -v README   | \
egrep -v CHANGES   | \
egrep -v DESCRIPTION | \
egrep -v wig.txt.gz  | \
egrep -v NO_MAGETAB_FOR | \
egrep -v .maf 

