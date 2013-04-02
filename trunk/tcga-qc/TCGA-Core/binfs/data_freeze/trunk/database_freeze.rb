# Takes a standard Data Freeze List and creates a tab-delimited list of archives (along with corresponding archive IDs)
# PROVIDE THE DATABASE ACCOUNT PASSWORD BEFORE RUNNING SCRIPT
#
# ARGUMENTS:
#	[0] Path to Data Freeze List
#	[1] Comma-delimited list of associated disease study abbreviations WITH NO SPACES (e.g. COAD,READ)
#
# OUTPUT:
#	data_freeze_archives.txt
#		List of archives, with the archive ID in the first column, the archive name in the second 
#		column and a flag in the third column:
#			- If flag is 'MANY', the archive maps to more than one ID so only the proper one needs to be kept
#			- If flag is 'UNAVAILABLE', the archive is no longer available for download
#			- If flag is 'UNRECOGNIZED', there has never been any record of this archive
#			- If flag is 'MULTI-VERSION', this is a warning that more than one revision of this archive exists in the data freeze

#------------- CONFIGURATIONS START --------------#

header = true # Header exists for Data Freeze List?
archive_column = 8 # Column index in Data Freeze List that lists archive names (starts from 0)
username = 'commonread' # Production database username
password = 'PASSWORD' # Production database password
db_path = '//ncidb-tcgas-p.nci.nih.gov:1652/tcgaprd.nci.nih.gov' # Production database path

#------------- CONFIGURATIONS END --------------#

# Error message for incorrect use
abort_msg = "Usage: ruby database_freeze.rb <filtered_freeze_list_path.txt> <comma-delimited_list_of_diseases>"
abort abort_msg if (ARGV.size > 2 or ARGV[0]==nil or ARGV[1]==nil)

# Get list of disease study abbreviations and ensure they are uppercased and enclosed in single quotes
studies = ARGV[1].chomp.split(',').map{|abbr| "'" + abbr.upcase + "'"}

# Store the list of archive names into a hash of empty values (so they can be populated later)
archive_file = File.new(ARGV[0],'r')
archive_hash = Hash.new
archive_file.gets if header # Skip header
while (line = archive_file.gets)
	archive_hash[line.chomp.split("\t")[archive_column]] = Hash.new #{key = archive_name, value={key = archive_id, value = true if available, else false}}
end
archive_file.close

# Build the query to get all archives for the given disease studies
query = "SELECT archive_id, archive_name, deploy_status FROM DCCCOMMON.ARCHIVE_INFO ai, DCCCOMMON.DISEASE d WHERE d.disease_id = ai.disease_id AND d.disease_abbreviation IN (" + studies.join(',') +")"

# Connect to the database
require 'oci8'
conn = OCI8.new(username, password, db_path)

# Execute query and put results in hash
conn.exec(query) {|idx, archive, availability|
	archive_hash[archive][idx] = (availability == 'Available') if archive_hash.include?(archive)
}
conn.logoff

# Create output file
out_file = File.new('data_freeze_archives.txt','w')
archive_names = archive_hash.keys # List of archive names

archive_hash.each{|archive, idx_hash|
	if idx_hash.empty?
		# Unrecognized archive
		line = "\t#{archive}\tUNRECOGNIZED"
	elsif idx_hash.size > 1
		# Archive maps to more than one ID
		line = "#{idx_hash.keys.join(',')}\t#{archive}\tMANY"
	else
		# Archive maps to one ID
		line = "#{idx_hash.keys[0]}\t#{archive}#{"\tUNAVAILABLE" if !idx_hash.values[0]}"
	end
	
	# Check if there is more than one revision of the archive in the freeze
	archive_base_name = archive[/(.+\.)\d+\.\d+/,1]
	has_multi_version = archive_names.find_all{|x| x.include?(archive_base_name)}.size > 1
	out_file.puts !has_multi_version ? line : "#{line}#{line.count("\t") > 1 ? "/" : "\t"}MULTI-VERSION"
}
out_file.close
