#!/bin/bash
#$Id: rmbtab.sh 11884 2011-06-27 01:19:19Z jensenma $
TMPDIR=/tmp
if [ -z "$*" ]
then
    echo "usage: rmbtab [pt_or_sample_id]  ..."
    echo " pt_or_sample_id is a patient or sample barcode/uuid "
    echo "purpose: remove records from biotab files mentioning the input "
    echo " ids, and rebuild the *.tar.gz file"
    echo "Run the script within a .../biotab/clin directory."
    exit $(true)
fi

PWD=$(pwd)
if [ $PWD = ${PWD/biotab\/clin/} ]
then
    echo "Run this script from the biotab/clin directory. Aborting..."
    exit $(false)
fi

RMLIST=$@
RMLIST=${RMLIST// /\\|}
ls -1 *.txt | { while read f ; do
	sed -nie "/$RMLIST/d;p" $f
    done
}

if [ -e *.tar.gz ] 
then 
    f=$(ls -1 *.tar.gz)
    mv $f ${f}e
    tar -czf $f *.txt
fi

WCLE=$(cat *.txte | wc -l)
WCL=$(cat *.txt | wc -l)
echo before: $WCLE
echo after: $WCL


