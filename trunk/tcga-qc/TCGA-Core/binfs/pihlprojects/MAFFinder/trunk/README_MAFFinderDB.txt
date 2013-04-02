This README covers operation of MAFFInderDB and an explanation of the SQLite database it uses

User Configurable Variables:
Database name:  Default is "maffiles.db".  This is the database the script uses to keep track of known MAF files.
Credentials file:  This is an XML file that stores usernames and passwords.  It should be stored in a read-only location only.
	<info>
		<tcgauser>1.0 Oracle DCCCommon user</tcgauser>
		<tcgapass>1.0 Oracle DCC common password</tcgapass>
		<nciuser>NCI username</nciuser>
		<ncipass>NCI Password</ncipass>
	</info>
Log file:  Location for the log file.  Defaults to "~/.maf/maf_log.txt"

SQLLite Database:
Database Name: User configurable.  Default is "maffiles.db"
Table name: maf_files
Field:  Description
filesystem (TEXT PRIMARY KEY):  TCGA filesystem location of the file (a URL in the case of maf files found as attachements on the wiki)
filename (TEXT):  Name of the file, isolated from the filesystem location
tumor_count (INTEGER): The number of unique tumor samples in the MAF files
normal_count (INTEGER): The number of unique normal samples in the MAF files
archive_name (TEXT):  The archive name the MAF file is part of
status (TEXT):  The status of the MAF file (Available, Obsolete, Unknown)
deploy_date (TEXT): The date the MAF file was deployed to the file system
md5 (TEXT):  MD5 value, if known.  Obtained from database, not calculated
url (TEXT):  URL of the MAF file.  Derived from filesystem value.
disease (TEXT):  Disease Abbreviation from 1.0 database
disease_name (TEXT): Full disease name
latest (INTEGER): Derived from is_latest field in the production database.  Values of 3 and 4 are files from filesystem only or jamboree only respectively.  Values of 5 are from the wiki
piistatus (TEXT):  Protected status of the file.  Values are Public or Protected
source (TEXT): The source of the MAF file info.  Values are "database", "filesystem", "wiki" or "jamboree"