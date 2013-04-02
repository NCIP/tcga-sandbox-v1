#!/bin/sh
#Take a single field and produce counts

while getopts d:f: opt; do
	case $opt in
	d)
		DATA=$OPTARG
		;;
	f)
		FIELD=$OPTARG
		;;
	esac
done

sqlite3 $DATA "SELECT $FIELD, COUNT(*) from maf GROUP BY $FIELD"