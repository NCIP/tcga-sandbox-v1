# Sanitizes a data archive freeze list of critical redactions (e.g. consent withdrawal) and appends corresponding annotations (from Participant to Anotations levels)
# Both input files must contain dates so that only archives with critical redactions submitted to the DCC post-redaction date are removed
#
# ARGUMENTS:
#	[0] Tab-delimited text file of the data archive freeze list
#	[1] Tab-delimited text file of the corresponding annotations
#
# OUTPUT:
#	annotated_freeze_list.txt
#		Sanitized data archive freeze list with appended annotations. SORT by identifier (barcode) for improved performance
#	removed_records.txt - List 
# 		Records removed from the freeze list due to critical redactions

#------------- CONFIGURATIONS START --------------#
# Data Freeze File: CHANGE the column numbers as appropriate (Columns start from 0)
@@freeze_columns = Hash.new
# Identifier that ties to annotations. Aliquot/shipped portions barcode will cover other identifiers (e.g. Sample barcode) formed by its substrings
@@freeze_columns["Shipped Barcode"] = 2
# Column with the dates
@@freeze_columns["Date"] = 10
# Does data archive freeze file contain a header?
freeze_header = true
# Keep as nil if this is anything but American date format (i.e. mm-dd-yyyy) - otherwise create a string to capture your format.
freeze_date_format = nil # e.g. "%m-%d-%Y" for 05-15-2010 [CASE SENSITIVE]

# Annotation File: CHANGE the column numbers as appropriate (Columns start from 0; nil if column doesn't exist)
@@annotation_columns = Hash.new
@@annotation_columns["Shipped Barcode"] = 3 # Identifier that ties to data freeze list
@@annotation_columns["Date"] = 7 # Column with the dates
@@annotation_columns["ANNOTATION_CLASSIFICATIONS"] = 4 # Annotation classification
@@annotation_columns["ANNOTATION_CATEGORIES"] = 5 # Annotation category
@@annotation_columns["ANNOTATION_NOTES"] = 6 # Annotation notes
# Does annotations file contain a header?
annotation_header = true
# Keep as nil if this is anything but American date format (i.e. mm-dd-yyyy) - otherwise create a string to capture your format.
annotation_date_format = "%m/%d/%Y" # e.g. "%m-%d-%Y" for 05-15-2010 [CASE SENSITIVE]

# List of critical redaction - i.e. any data submitted to the DCC after the annotation is in place need to be removed
# key: element level of redaction	value: array of critical annotations
critical_categories = ['Subject withdrew consent']

#------------- CONFIGURATIONS END --------------#

#------------- METHODS START --------------#
# Appends hash annotations to existing data freeze line and outputs to the appropriate file
def print(line, hash, file)
	hash.each_value{|anno_array| line.push(anno_array.uniq.join(" / "))}
	file.puts line.join("\t")
end
#------------- METHODS END --------------#

require 'date'

# Error message for incorrect use
abort_msg = "Usage: Annotations.rb <db_results_file_path.txt> <annotations_file_path.txt>"
abort abort_msg if (ARGV[0]==nil or ARGV[1]==nil)

# Read in the annotations and store them in a hash
annotations_hash = Hash.new # All annotations
annotations_file = File.new(ARGV[1],'r')
annotations_file.gets if annotation_header # Ignore headers if they exist

# Store all annotation values into a hash referenced by the identifier (including the identifier itself)
while (line = annotations_file.gets)
	line = line.chomp.split("\t")
	a_hash = Hash.new
	@@annotation_columns.each{|col,idx| a_hash[col] = line[idx]}
	# Check if an array exists for the shipped item index - add current hash to array if it's not a duplicate
	identifier = a_hash["Shipped Barcode"]
	a_array = annotations_hash[identifier]
	annotations_hash[identifier] = (a_array and !a_array.include?(a_hash)) ? a_array.push(a_hash) : [a_hash] # Add to global annotations list
end

annotations_file.close
# To speed up processing, sort hash (yes, only in Ruby)
annotations_hash = annotations_hash.sort
# Keep track of columns that have actual annotation values
annotation_value_keys = @@annotation_columns.keys - ["Shipped Barcode", "Date"]

# Go through the data freeze list - printing to output files as needed
freeze_file = File.new(ARGV[0], 'r')
annotated_file = File.new('data_freeze_list.txt','w')
removed_file = File.new('removed_records.txt','w')
# Print output file headers if given headers
if freeze_header
	header = freeze_file.gets.chomp + "\t" + annotation_value_keys.join("\t")
	annotated_file.puts header
	removed_file.puts header
end
# Keep track of last identifier as same identifiers have the same annotations and would not require reaccessing the annotations hash
last_identifier = nil
critical_date = nil # Earliest date of the critical annotations
annotations_subhash = nil # A subset of annotations only related to current identifier
range_detected = false # As annotation hash is sorted, this tells us to stop searching once we step out of the identifier ranges related to the current identifier

while (line = freeze_file.gets)
	line = line.chomp.split("\t")
	curr_identifier = line[@@freeze_columns["Shipped Barcode"]]
	curr_date = freeze_date_format ? Date.strptime(line[@@freeze_columns["Date"]],freeze_date_format) : Date.parse(line[@@freeze_columns["Date"]]) # Date of archive submission
	# Use annotations from the last round if the identifiers are the same
	if curr_identifier == last_identifier
		(critical_date and curr_date > critical_date) ? print(line, annotations_subhash, removed_file) : print(line, annotations_subhash, annotated_file)
		next
	end
	
	# Not the same identifier - reset flags
	last_identifier = curr_identifier
	critical_date = nil
	range_detected = false 
	annotations_subhash = Hash.new
	annotation_value_keys.each{|col| annotations_subhash[col] = Array.new}
	participant_barcode = curr_identifier.split("-")[0,3].join("-") # For fishing out the pertinent identifiers in the annotation hash
	
	# Cycle through the annotations hash and gather annotations, any critical dates
	annotations_hash.each{|identifier,hash_array|
		is_relevant = identifier.start_with?(participant_barcode)
		break if range_detected and !is_relevant # Stop searching if we are outside of the pertinent range
		next if !is_relevant # Haven't reached pertinent range
		range_detected = true # Within pertinent range
		
		# Check if annotation is applicable by belonging to curr_identifier's ancestor
		# Account for special case: Slide barcodes with "TS" and "BS" in the last barcode grouping
		next if !(curr_identifier.start_with?(identifier) or identifier=~/(T|B)S\w+/ and curr_identifier.start_with?(identifier.split("-")[0...-1].join("-")))
		# Cycle through and merge related annotations while taking note of any critical redactions
		hash_array.each{|anno|
			# Store critical annotation date if found. If one already exists, keep the earlier date
			if critical_categories.include?(anno["Annotation Categories"])
				anno_date = annotation_date_format ? Date.strptime(anno["Date"],annotation_date_format) : Date.parse(anno["Date"])
				critical_date = critical_date ? [critical_date, anno_date].min : anno_date
			end
			# Merge annotations with actual annotation values into subhash
			annotation_value_keys.each{|col| annotations_subhash[col].push(anno[col])}
		}
	}
	
	# Check if archive should be removed from the freeze - print accordingly
	(critical_date and curr_date >= critical_date) ? print(line, annotations_subhash, removed_file) : print(line, annotations_subhash, annotated_file)
end

removed_file.close
annotated_file.close
freeze_file.close