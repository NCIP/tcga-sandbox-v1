#!/bin/bash
#$Id: rmtar.sh 11355 2011-05-25 02:38:49Z jensenma $
TMPDIR=/tmp
if [ -z "$*" ]
then
    echo "usage: rmtar [archive] [file] ..."
    echo " [archive] is full_archive_name.tar.gz"
    echo " [file] is the file within the archive (excluding the archive dir)"
    echo "purpose: remove files from a DCC tar.gz archive, leaving the archive valid"
    echo " with respect to the manifest and archive md5 (no effect on SDRFs)"
    exit $(true)
fi

ARCHIVE=$1
ARCHIVE_DIR=${ARCHIVE/.tar.gz/}
ARCHIVE_NAME=${ARCHIVE/*\//}
ARCHIVE_DIRNAME=${ARCHIVE_DIR/*\//}
ARCHIVE_MD5=${ARCHIVE}.md5

if [ ! -e $ARCHIVE_MD5 ]
then
    echo "Archive MD5 file not present; aborting."
    exit $(false)
fi

shift
FILELIST=$(tar -tzf $ARCHIVE)
RMLIST=$(for w in $@ ; do echo ${ARCHIVE_DIRNAME}/$w ; done)
# create revised archive, new manifest, and md5 in the TMPDIR
tar -xzf ${ARCHIVE} -C ${TMPDIR} 
pushd ${TMPDIR} > /dev/null
for f in $RMLIST ; do if [ -f $f ] ; then rm $f ; fi ; done ;
cd ${ARCHIVE_DIRNAME}
rm -f MANIFEST.txt
ls -1 | { while read f ; do md5sum $f >> MANIFEST.txt ; done ; }
cd ..
tar -czf ${ARCHIVE_NAME} ${ARCHIVE_DIRNAME}
rm -rf ${ARCHIVE_DIRNAME}
popd > /dev/null
# backup existing archive if necessary
# note this check is weak
if [ ${ARCHIVE} = ${ARCHIVE_NAME} ] 
then
    mv ${ARCHIVE} ${ARCHIVE}e
    mv ${ARCHIVE_MD5} ${ARCHIVE_MD5}e
fi
mv ${TMPDIR}/${ARCHIVE_NAME} .
md5sum ${ARCHIVE_NAME} > ${ARCHIVE_NAME}.md5
