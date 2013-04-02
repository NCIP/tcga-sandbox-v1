# This script supplements and validates certain filetypes within a provided list of
# archives with UUIDs and generates new archives containing the converted data
# INPUT: 
#		ARGV[0]: Text file of absolute paths to the original archives (without .tar.gz)
#		ARGV[1]: Path to tab-delimited barcode-to-uuid mapping file. First column contains barcodes, second column contains UUIDs
# OUTPUT:
#		converted_log.txt - a list of archives that were successfully converted
#		error_log.txt - a list of errors that occurred during conversion

#------------- CONFIGURATIONS START --------------#

# OPTIONS - SET THESE BEFORE RUNNING SCRIPT!
CLEAN_DIR = true # Should the working directory be cleaned out before processing starts?
WORKING_DIR = "/h1/chual/uuid_conversion_code/converted_archives" # Archives will be built here
#~ WORKING_DIR = "/home/anna/Desktop/SHARE/uuid/workspace"
#~ WORKING_DIR = "/home/anna/Desktop/SHARE/Work/uuid/workspace"
QCLIVE_DIR = "/tcgafiles/ftp_auth/deposit_ftpusers/dcc" # Submission directory to QCLive
USE_DB = false # Use the database if true, otherwise load from file (next line). Make sure Database PASSWORD is set if true

# Database
USERNAME = 'commonread' # Production database username
PASSWORD = 'read1234common' # Production database password
DB_PATH = '//ncidb-tcgas-p.nci.nih.gov:1652/tcgaprd.nci.nih.gov' # Production database path

# Archive description files
MANIFEST_FILE = "MANIFEST.txt" # Name of manifest file
CHANGES_FILE = "UUID_CHANGES.txt" # Name of file logging UUID changes made by DCC

# Archive identifiers - What tells the archive types apart using the archive name?
MAGE_TAB_KEY = "mage-tab" 
PROTEIN_KEY = "RPPA_Core"

#------------- CONFIGURATIONS END --------------#
$LOAD_PATH.push('./lib')
require 'fileutils'
require 'identifier'
require 'writer'
require 'sdrf_converter'
require 'maf_converter.rb'
require 'vcf_converter.rb'

# Constants
ARCHIVE_EXT = ".tar.gz" # Archive extension
MD5_EXT = ".md5" # MD5 file extension
STDOUT_ERR = "**** ERROR(S) DISCOVERED! CANCELLING CONVERSION. See #{Writer::ERROR_FILENAME} for details ****" # Error message to stdout
STDOUT_NONE = "**** ARCHIVE CONTAINS NO FILES FOR CONVERSION. See #{Writer::WARNING_FILENAME} for details ****"


#------------- METHODS START --------------#
# Increment the revision number (2nd-last digit) in the archive name (without extension)
def increment_version (archive_name)
	# Separate out the base archive name and each digit for serial, revision and series
	components = archive_name.match('(^.+)\.(\d+)\.(\d+).(\d+)')
	# Reconstruct archive name with the revision number incremented
	[components[1], components[2], Integer(components[3])+1, components[4]].join(".")
end

#------------- METHODS END --------------#

# Error message for incorrect use
abort_msg = "Usage: ruby uuid_archive_converter.rb <archive_paths.txt> <id_map.txt>"
abort abort_msg if ARGV[0]==nil or ARGV.size < 2

# Error message for non-existent input file
abort "File (#{ARGV[0]}) not found" if !File.exist?(ARGV[0])
abort "File (#{ARGV[1]}) not found" if !File.exist?(ARGV[1])

# Clean working directory
if CLEAN_DIR and File.exist?(WORKING_DIR)
	puts "Cleaning workspace directory..."
	FileUtils.rm_rf(WORKING_DIR)  # Delete directory and contents
	FileUtils.mkdir(WORKING_DIR) # Recreate directory
end

# Load UUID-to-barcode mapping
puts "Building a map between barcodes and UUIDs...\n"
MAPPING_FILE = ARGV[1]
mapping = USE_DB ? Identifier.db_mapping(USERNAME, PASSWORD, DB_PATH) : Identifier.file_mapping(MAPPING_FILE)
logger = Writer.new(WORKING_DIR, MAPPING_FILE)

# Convert each archive in list
infile = File.new(ARGV[0], 'r')
infile.each{|original_archive|
	original_dir = original_archive.chomp.gsub(/(.tar|#{ARCHIVE_EXT})$/, '')# Path of the archive directory
	original_basename = File.basename(original_dir) # Name of the archive directory
	new_basename = increment_version(original_basename) # Name of the converted archive directory
	puts "\n============= #{original_basename} =============\n"
	# STEP 1 - Copy the expanded original archive to the working directory
	puts "STEP 1 - Copying #{original_basename} directory to working directory #{WORKING_DIR}..."
	if !File.exist?(original_dir)
		logger.write_error(original_basename, "Archive directory does not exist", 1, original_archive.chomp)
		puts STDOUT_ERR
		next # Do not complete conversion for this archive
	end
	FileUtils.cp_r(original_dir, WORKING_DIR)
	FileUtils.cd(WORKING_DIR + "/" + original_basename)
	
	# STEP 2 - MD5 check against MANIFEST.txt to ensure that copy was successful
	puts "STEP 2 - Checking data integrity..."
	success = system("grep -v '#{MANIFEST_FILE}' #{MANIFEST_FILE} | md5sum -c")
	# Error check
	if !success
		logger.write_error(original_basename, "Failed data integrity check (md5sum)", 2, original_archive.chomp)
		puts STDOUT_ERR
		next # Do not complete conversion for this archive
	end

	# STEP 3 - Rename the directory to the new archive name
	puts "STEP 3 - Renaming #{original_basename} to #{new_basename}..."
	FileUtils.cd("..")
	File.rename(original_basename, new_basename)
	new_dir = WORKING_DIR + "/" + new_basename # path of new directory
	# Error check
	if !File.exist?(new_dir)
		# Some reason the directory doesn't exist
		logger.write_error(original_basename, "Could not enter working archive directory: #{new_dir}", 3, original_archive.chomp)
		puts STDOUT_ERR
		next # Do not complete conversion for this archive
	end
	FileUtils.cd(new_dir) # Enter working archive directory
	
	# STEP 4 - Delete old MANIFEST.txt
	puts "STEP 4 - Deleting original #{MANIFEST_FILE}..."
	# Error check
	if !File.exist?(MANIFEST_FILE)
		# Some reason the directory doesn't exist
		logger.write_error(original_basename, "Could not delete old #{MANIFEST_FILE}", 3, original_archive.chomp)
		puts STDOUT_ERR
		next # Do not complete conversion for this archive
	end	
	File.delete(MANIFEST_FILE) # Delete original manifest
	
	# STEP 5 - Create UUID_CHANGES.txt to log files that have been modified during this conversion
	puts "STEP 5 - Creating #{CHANGES_FILE} to log modified data files..."
	change_log = File.new(CHANGES_FILE,'w')
	# Write file header
	change_log.puts "UUIDs have been added to #{original_basename} to create this archive (#{new_basename}) by the DCC.\n\nThis file was automatically generated on #{Time.now.strftime("%Y-%m-%d")}.\n\nMODIFIED FILES:\n"
	
	# STEP 6 - Convert files to be UUID-friendly
	# Determine the kind of archive we're dealing with and convert accordingly
	if new_basename.include?(MAGE_TAB_KEY)
		#-------- MAGE-TAB archive determined --------#
		# Find SDRF file(s)
		conversion_file = nil # SDRF filename
		discontinue = false # Is there a reason to discontinue processing?
		Dir.foreach("."){|file|
			if file.downcase.include?("sdrf")
				# FAIL if more than 1 SDRF file exists
				if conversion_file
					logger.write_error(original_basename, "More than one SDRF exists", 6, original_archive.chomp)
					discontinue = true
					break # Do not complete conversion for this archive
				else
					conversion_file = file
				end
			end
		}
		if discontinue
			# Error discovered
			puts STDOUT_ERR
			next
		elsif !conversion_file
			# Nothing to convert in archive
			puts STDOUT_NONE
			logger.write_warning(original_basename, "No files converted", 6, original_archive.chomp)
			next
		end
	
		#Find out if it is for aliquot or shipped portion
		if new_basename.include?(PROTEIN_KEY)
			# Shipped Portion MAGE-TAB archive determined
			puts "STEP 6 - Converting SHIPPED PORTION SDRF file: #{conversion_file}..."
			# Convert file
			success = SdrfConverter.protein_convert(conversion_file, mapping, original_basename, 6, original_archive.chomp, logger)
		else
			puts "STEP 6 - Converting ALIQUOT SDRF file: #{conversion_file}..."
			# Convert file
			success = SdrfConverter.aliquot_convert(conversion_file, mapping, original_basename, 6, original_archive.chomp, logger)
		end
		# Write converted file to UUID_CHANGES.txt
		if success
			change_log.puts conversion_file
		else
			puts STDOUT_ERR
			next
		end
	else
		#-------- MAF/VCF archive determined --------#
		# Find all MAF and VCF files
		discontinue = false # Is there a reason to discontinue processing?
		conversion_file = nil # File for conversion
		Dir.foreach("."){|file|
			if file.downcase.include?("maf")
				# WARN if file does not end in .maf
				if File.extname(file).downcase != ".maf"
					logger.write_warning(original_basename, "Possible MAF file not ending with '.maf': #{file}", 6, original_archive.chomp)
				else
					conversion_file = file
					puts "STEP 6 - Converting MAF file: #{conversion_file}..."
					# Convert file
					success = MafConverter.convert(conversion_file, mapping, original_basename, 6, original_archive.chomp, logger)
					# Write converted file to UUID_CHANGES.txt
					if success
						change_log.puts file
					else
						puts STDOUT_ERR
						discontinue = true
						next
					end # if success
				end # if File.extname(file).downcase != ".maf"
			elsif file.downcase.include?("vcf")
				# WARN if file does not end in .vcf
				if File.extname(file).downcase != ".vcf"
					logger.write_warning(original_basename, "Possible VCF file not ending with '.vcf': #{file}", 6, original_archive.chomp)
				else
					conversion_file = file
					puts "STEP 6 - Converting VCF file: #{conversion_file}..."
					# Convert file
					success = VcfConverter.convert(conversion_file, mapping, original_basename, 6, original_archive.chomp, logger)
					# Write converted file to UUID_CHANGES.txt
					if success
						change_log.puts file
					else
						puts STDOUT_ERR
						discontinue = true
						next
					end # if success
				end # if File.extname(file).downcase != ".vcf"
			end # if file.downcase.include?("maf")
		}
		next if discontinue # discontinue
		if !conversion_file
			# Nothing to convert in archive
			puts STDOUT_NONE
			logger.write_warning(original_basename, "No files converted", 6, original_archive.chomp)
			next
		end
		
	end # if new_basename.include?(MAGE_TAB_KEY)
	change_log.close # Close UUID_CHANGES.txt
	
	# STEP 7 - Create new MANIFEST.txt
	puts "STEP 7 - Creating #{MANIFEST_FILE}..."
	success = system("md5sum * > #{MANIFEST_FILE}")
	# Error check
	if !success
		logger.write_error(original_basename, "Could not create #{MANIFEST_FILE}", 7, original_archive.chomp)
		puts STDOUT_ERR
		next # Do not complete conversion for this archive
	end
	
	# STEP 8 - Create new archive
	puts "STEP 8 - Creating new archive: #{new_basename}#{ARCHIVE_EXT}..."
	FileUtils.cd("..")
	success = system("tar", "-zvcf", new_basename+ARCHIVE_EXT, new_basename)
	# Error check
	if !success
		logger.write_error(original_basename, "Could not create archive: #{new_basename+ARCHIVE_EXT}", 7, original_archive.chomp)
		puts STDOUT_ERR
		next # Do not complete conversion for this archive
	end
	
	# STEP 9 - Create archive MD5 hash file
	puts "STEP 9 - Creating archive MD5 file: #{new_basename}#{ARCHIVE_EXT}#{MD5_EXT}..."
	success = system("md5sum #{new_basename+ARCHIVE_EXT} > #{new_basename+ARCHIVE_EXT+MD5_EXT}")
	# Error check
	if !success
		logger.write_error(original_basename, "Could not create MD5 file: #{new_basename+ARCHIVE_EXT+MD5_EXT}", 7, original_archive.chomp)
		puts STDOUT_ERR
		next # Do not complete conversion for this archive
	end
	
	# STEP 10 - Log successful conversion
	puts "STEP 10 - Logging successful conversion..."
	logger.write_converted(original_basename, new_basename)
	
	# STEP 11 - Move archive to QCLive submission directory
	puts "STEP 11 - Submitting #{new_basename}#{ARCHIVE_EXT} to QCLive..."
	#FileUtils.mv(new_basename+ARCHIVE_EXT, QCLIVE_DIR) # Submit the archive
	#FileUtils.mv(new_basename+ARCHIVE_EXT+MD5_EXT, QCLIVE_DIR) # Submit the MD5 file 
}

