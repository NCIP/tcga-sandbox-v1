There are three files in this package that are used to create MAF files that can be used in testing

mafdb.pl - This program reads in an existing MAF file and creates a SQLite database that contains the data

Usage:  
	mafdb.pl [-m|--maf  MAF_file.maf] [-d|--database database_file]
Options:
	-m|--maf  Use this flag to indicate which maf file you want parsed into the database
	-d|--database  This is the sqlite database that will hold the contents of the maf file
Example:
	#perl mafdb.pl -m PR-TCGA-Analysis_set.aggregated.tcga.somatic.maf -d somatic.db
	
	This will read the file PR-TCGA-Analysis_set.aggregated.tcga.somatic.maf and create a database called somatic.db
	
	
maffile_creator.pl - The program uses a database created by mafdb.pl to create new maf files and can substitute values in any column

Usage: 
	maffile_creator.pl [-m|--maf MAF_file.maf] [-d|--database database_file] [-f|--field MAF file field] [-v|--value Substitute value]
		[-r|--realvalue  The actual value of the column]
		
Options:
	-m|--maf filename: This will be the output MAF file
	-d|--database database:  This is the input database used to create the MAF file (created by mafdb.pl)
	-f|--field fieldname:  This is the legitimate MAF field name where you wish to replace values
	-v|--value Value:  The value you want entered into the field identified by the -f flag
	-r|--realvalue:  Value/ALL:  If ALL, all values of the field identified by -f will be replaced with the value specified 
		by -v.  If a real MAF value is provided, only those instances where the field value matches the -r value will be replaced by 
		the -v value
		
Example
	#perl maffile_creator.pl -m Test1.maf -d somatic.db -f Validation_Status -v BAD_VALUE -r Invalid
	
	This will replace all instances of Validation_Status == Invalid with Validation_Status == BAD_VALUE and save to the file Test1.maf
	
	
maf_dbquery.sh - This is a shell script that queries a SQLite database and returns the number of times a value is used in a specified column

Usage:
	maf_dbquery.sh [-d database] [-f MAF field]
	
Options:
	-d database: This is the database to query.  Must have been created by mafdb.pl
	-f field: This is the MAF field to count
	
Example:
	#./maf_dbquery.sh -d somatic.db -f Validation_Status
	
	This will return counts of all the values found in the Validation_Status column of the somatic.db database
