README for dccQuery.pl

Usage:  perl dccQuery.pl [-p|-q] (-u username -p password) '<Solr query>'

Flags:
-d: Use the Preview server
-a: Use the QA1 server
-u: NCI username for downloading protected files
-p: NCI password for downloading protected files (may need to escape shell reserved characters)

The quotes around the query are necessary since Solr uses a number of characters that are reseved in bash.

The dccQuery program will package a query, present the results, and then allow the user to download any or all of the files in the query.

For most queries, downloaded files will be stored in directories named for the archive the file belongs to.  However, if you wish to 
use a different structure, use the Solr group feature.

This query will store the files in archive directories:
center:"harvard medical school" AND disease_abbreviation:THCA AND access_level:Public

This query will store files in directories named by data type:
center:"harvard medical school" AND disease_abbreviation:THCA AND access_level:Public&group=true&group.field=data_type
