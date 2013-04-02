# Takes a BAM freeze list and verifies against a CGHub telemetry report that each BAM file exists
# Fills in any specified BAM freeze list columns with CGHub values
#
# ARGUMENTS:
#	[0] Tab-delimited text file of the BAM freeze list
#	[1] Tab-delimited text file of the CGHub telemetry report
#
# OUTPUT:
#	validated_bam_list.txt
#		List with BAM freeze list columns validated and filled in
#	BAM_errors.txt - List 
# 		List of errors found in the BAM freeze list

#------------- CONFIGURATIONS START --------------#
# BAM Freeze File: CHANGE the column numbers as appropriate (Columns start from 0; nil if column doesn't exist)
# Arranged in order for printing to output file
# List all columns you want to have in the final output
@@bam_columns = Hash.new
@@bam_columns["Participant barcode"] = 0
@@bam_columns["Aliquot barcode"] = 1
@@bam_columns["Aliquot UUID"] = 6
@@bam_columns["File location"] = 2
@@bam_columns["CGHub UUID"] = 3
@@bam_columns["BAM file"] = 4
@@bam_columns["MD5"] = 5
@@bam_columns["File size"] = nil

# CGHub Telemetry File: CHANGE the column numbers as appropriate (Columns start from 0; nil if column doesn't exist)
# Only list columns that are used in validation using the SAME KEY NAMES as @@bam_columns
# Order doesn't matter
@@cghub_columns = Hash.new
@@cghub_columns["CGHub UUID"] = 0
@@cghub_columns["BAM file"] = 3
@@cghub_columns["Aliquot barcode"] = 4
@@cghub_columns["Aliquot UUID"] = 5
@@cghub_columns["Participant barcode"] = 6
@@cghub_columns["File size"] = 11
@@cghub_columns["MD5"] = 12

# Do the files contain a header row that we should ignore when processing?
bam_header = true # Does BAM freeze file
cghub_header = true # Does CGHub telemetry file

# Primary key - What can we use as the primary key in our comparisons (e.g. CGHub ID, BAM file name)?
primary_key = "BAM file"
#------------- CONFIGURATIONS END --------------#

#------------- METHODS START --------------#
# Method: Print hash to validated BAM freeze file in the format specified at the top of script
def print_valid(hash, validated_file)
	line = Array.new
	@@bam_columns.each_key{|col|
		line.push(hash[col])
	}
	validated_file.puts line.join("\t")
end
#------------- METHODS END --------------#

# Error message for incorrect use
abort_msg = "Usage: BAM_validation.rb <bam_freeze_file_path.txt> <cghub_telemetry_file_path.txt>"
abort abort_msg if (ARGV[0]==nil or ARGV[1]==nil)

# Get reference data from CGHub file and store it in a hash:
# Key = primary_key, Value = Hash composed of all values (including the primary key)
cghub_file = File.new(ARGV[1],'r') 
cghub_data = Hash.new
cghub_file.gets if cghub_header # Get rid of the header
while (line = cghub_file.gets)
	line = line.chomp.split("\t")
	h = Hash.new
	@@cghub_columns.each{|k,v|
		h[k] = line[v]
	}
	# Tie hash data to primary key
	cghub_data[line[@@cghub_columns[primary_key]]] = h
end
cghub_file.close

# Go through the BAM freeze list data and verify values against those from CGHub
bam_freeze_file = File.new(ARGV[0], 'r')
# Write to output files as we progress
errors_file = File.new('BAM_errors.txt','w')
errors_file.puts "Level\tError" # Write column headers to file
validated_file = File.new('validated_bam_list.txt','w')
validated_file.puts @@bam_columns.keys.join("\t")  # Write column headers to file

bam_freeze_file.gets if bam_header # Get rid of the header
while (line = bam_freeze_file.gets)
	line = line.chomp.split("\t")
	# build a hash out of the BAM freeze file values
	bam_hash = Hash.new
	@@bam_columns.each{|col,idx|
		bam_hash[col] = idx ? line[idx] : nil # No value for a column we want in the validated output file but does not exist in original BAM freeze file
	}
	
	# Find the corresponding information in the CGHub telemetry data using the primary key
	primary_key_value = bam_hash[primary_key]	
	cghub_value_hash = cghub_data[primary_key_value]
	
	if (!primary_key_value or primary_key_value == '' or primary_key_value.delete("/").downcase =="na" or !cghub_value_hash)
		# Warning if there is no primary key - i.e. not submitted to CGHub output line as-is with warning
		errors_file.puts "Warning\tNo validation was performed on aliquot #{bam_hash["Aliquot barcode"]} (#{bam_hash["BAM file"]}) as it has not been submitted to CGHub"
		# Print to validated BAM freeze output file
		print_valid(bam_hash, validated_file)
		next
	end

	# Comparable columns - Columns that exist between both sources so they can be compared
	comparable_columns = @@bam_columns.keys & @@cghub_columns.keys  #Intersection

	# Build a CGHub hash so we can compare it to the BAM hash
	cghub_hash = Hash.new
	comparable_columns.each{|col|
		cghub_hash[col] = cghub_value_hash[col]
	}
	
	# Compare the two hashes
	error_found = false
	bam_hash.each{|k,v|
		if (!v and !error_found)
			# BAM freeze is missing a value - fill in with CGHub value
			bam_hash[k] = cghub_hash[k]
		elsif (v and cghub_hash[k] and cghub_hash[k].downcase != v.downcase)
			# Difference between BAM file and CGHub found
			errors_file.puts "Error\t#{k} for aliquot #{bam_hash["Aliquot barcode"]} (#{bam_hash["BAM file"]}) is #{v} in the freeze list and #{cghub_hash[k]} at CGHub"
			error_found = true
		end
	}
	
	next if error_found # skip printing validated list
	print_valid(bam_hash, validated_file) # print
end
validated_file.close
errors_file.close
bam_freeze_file.close