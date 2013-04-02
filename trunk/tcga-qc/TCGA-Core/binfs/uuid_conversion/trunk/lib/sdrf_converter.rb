module SdrfConverter
	
	COMMENT_COL = 'Comment [TCGA Barcode]' # Column header for barcodes
	TEMP_FILE = 'temp' # Temporary name of converted file
	NULL_VALUE = '->' # Null value in an SDRF
	EXTRACT_COL = 'Extract Name' # Column header for UUIDs (Aliquot SDRFs)
	SAMPLE_COL = 'Sample Name' # Column header for UUIDs (Shipped Portion SDRFs)
	ASSAY_COL = 'Assay Name' # Column header for assays that must contain UUIDs (RNASeq Aliquot SDRFs)
	ASSAY_PLATFORMS = ['miRNASeq','RNASeq'] # Platforms that require 'Assay Name' column to include 'Extract Name' values
	attr_reader :primary_col, :primary_obj, :primary_obj_str
	
	# Converts an aliquot SDRF, using 'Extract Name' as the primary column (for UUID)
	# See self.convert( ) for return values and parameter definitions
	def self.aliquot_convert(filename, mapping, archive, step_num, path, logger)
		@primary_col = EXTRACT_COL # 'Extract Name' is the primary identifier column
		@primary_obj = :aliquot # Object represented by the primary identifier
		@primary_obj_str = "aliquot" # String representation of primary object type
		return SdrfConverter.convert(filename, mapping, archive, step_num, path, logger)
	end
	
	# Converts a shipped portion SDRF, using 'Sample Name' as the primary column (for UUID)
	# See self.convert( ) for return values and parameter definitions
	def self.protein_convert(filename, mapping, archive, step_num, path, logger)
		@primary_col = SAMPLE_COL # 'Sample Name' is the primary identifier column
		@primary_obj = :shipped_portion # Object represented by the primary identifier
		@primary_obj_str = "shipped portion" # String representation of primary object type
		return SdrfConverter.convert(filename, mapping, archive, step_num, path, logger)
	end
	
	# Converts an SDRF to be UUID compatible
	# Paramters:
	#		filename - Name of file to be converted
	#		mapping - Hash of barcode-to-uuid mappings
	#		archive - Name of archive to which the file belongs
	#		step_num - Step number of main process that calls this method
	#		path - Path to archive
	#		logger - Writer object so that errors/warnings can be added to file
	# Returns 'false' if an error occured during conversion. Examples of errors include:
	#		- Barcodes and UUIDs provided did not match
	#		- Barcodes or UUIDs provided are not correct
	#		- No conversion was necessary
	def self.convert(filename, mapping, archive, step_num, path, logger)
		has_error = false # is there an error somewhere in this file?
		comment_col_exist = false # does the file come with a 'Comment [TCGA Barcode]' column?
		infile = File.new(filename, 'r') # original file
		outfile = File.new(TEMP_FILE, 'w') # converted file
		comment_label = nil # Bad comment label if it exists, but lets use it to complete validation
		is_rnaseq = ASSAY_PLATFORMS.any? {|platform| archive.include?platform} # are we handling an RNASeq or miRNASeq archive?
		
		# HEADER
		line = infile.gets.chomp # Read in header
		header = line.split("\t") # Split header into columns
		# Check if there is a mistyped 'Comment [TCGA Barcode]' column
		comment_label = line.match(/Comment\s+\[TCGA\s+Barcode\]/i)
		if comment_label
			comment_label = comment_label[0] # Extract comment label
			if comment_label!=COMMENT_COL
				logger.write_error(archive, "(Line 1 of #{filename}) The TCGA barcode column must be '#{COMMENT_COL}' rather than '#{comment_label}'", step_num,path)
				has_error = true
			end
		end
		# Get indices of the primary and 'Comment [TCGA Barcode]' columns
		comment_idx = header.index(comment_label)
		primary_idx = header.index(@primary_col)
		
		if !primary_idx
			# ---Primary identifier column does not exist----
			logger.write_error(archive, "(Line 1 of #{filename}) '#{@primary_col}' column does not exist", step_num,path)
			return false # Do not complete conversion
		elsif comment_idx
			# ---'Comment [TCGA Barcode]' column already exists---
			comment_col_exist = true
			# Error check: Barcode column must follow the UUID column
			if comment_idx != primary_idx + 1
				logger.write_error(archive, "(Line 1 of #{filename}) '#{COMMENT COL}' column does not follow '#{@primary_col}' column", step_num,path)
				has_error = true # Allow for quality check to continue
			end
		else
			# ---'Comment [TCGA Barcode]' column does not exist---
			# Insert the comment column after the primary column
			comment_idx = primary_idx + 1
			header.insert(comment_idx, COMMENT_COL)
			# Write headers to new SDRF
			outfile.puts header.join("\t")
		end
		
		# CONTENT - Populate SDRF with UUIDs under the primary column and barcodes under the 'Comment [TCGA Barcode]' column
		line_num = 1
		if comment_col_exist
			# --- 'Comment [TCGA Barcode]' column already exists ---
			puts "\tConverted file detected. Performing quality checks..."
			while (line = infile.gets)
				line_num+=1
				next if line.match(/^$/)
				line = line.chomp.split("\t")
				# Assume UUIDs are in the primary column and barcodes are in the comment column
				uuid = line[primary_idx].downcase
				barcode = line[comment_idx]
				# Error check - Are UUID and barode formatted correctly?
				if ![@primary_obj,:reference].include? Identifier.id_type(barcode)
					logger.write_error(archive, "(Line #{line_num} of #{filename}) #{@primary_obj_str.capitalize} barcode (instead of '#{barcode}') should be in '#{COMMENT_COL}'", step_num,path)
					has_error = true
				else
					case Identifier.id_type(uuid)
						when :bad_id
							# Bad UUID detected
							logger.write_error(archive, "(Line #{line_num} of #{filename}) '#{uuid}' is not a valid UUID", step_num,path)
							has_error = true
						when :reference
							# Non-TCGA reference ID detected. Make sure it's not the  null value
							if uuid.chomp == NULL_VALUE
								logger.write_error(archive, "(Line #{line_num} of #{filename}) '#{@primary_col}' cannot be null ('#{NULL_VALUE}')", step_num,path)
								has_error = true
							else
								# Good reference detected - output warning
								logger.write_warning(archive, "(Line #{line_num} of #{filename}) Non-TCGA identifier '#{uuid}' detected", step_num,path)
							end
							# Check that the associated barcode value contains the null value
							if barcode != NULL_VALUE
								logger.write_error(archive, "(Line #{line_num} of #{filename}) '#{COMMENT_COL}' should be '#{NULL_VALUE}' when a non-TCGA identifier is used in '#{@primary_col}'", step_num,path)
								has_error = true
							end
						when :uuid
							# UUID detected
							# Look up the barcode. Does the resulting UUID match the given UUID?
							if mapping[barcode] != uuid
								logger.write_error(archive, "(Line #{line_num} of #{filename}) UUID (#{uuid}) and barcode (#{barcode}) do not map to the same biospecimen", step_num,path)
								has_error = true
							end
						else
							# Barcode detected
							logger.write_error(archive, "(Line #{line_num} of #{filename}) '#{@primary_col}' contains a barcode; UUID expected", step_num,path)
							has_error = true
					end # switch
				end # ![@primary_obj,:reference].include? Identifier.id_type(barcode)
			end # while loop
		else
			# --- Barcode comment column does not exist in original file ---
			puts "\tUnconverted file detected. Converting file..."
			if is_rnaseq
				# There is more than one column with the 'Assay Name' header
				assay_idx = (0...header.length).find_all{|i| header[i] == ASSAY_COL}
			end
			
			while (line = infile.gets)
				line_num+=1
				next if line.match(/^$/)
				line = line.chomp.split("\t")
				id = line[primary_idx]
				# Does primary column contain a uuid or barcode?
				case Identifier.id_type(id)
					when @primary_obj
						# Aliquot/Shipped portion barcode detected
						if mapping.key?(id)
							if !has_error
								uuid = mapping[id]
								# Replace barcodes in 'Assay Name' columns (for RNASeq platforms only)
								if is_rnaseq
									assay_idx.each{|i| line[i] = line[i].tr(id, uuid)}
								end
								# Find corresponding UUID and write to file
								line.insert(primary_idx,uuid)
								outfile.puts line.join("\t")
							end
						else
							# Unrecognized TCGA barcode
							logger.write_error(archive, "(Line #{line_num} of #{filename}) '#{id}' is not a recognized #{@primary_obj_str} barcode", step_num, path)
							has_error = true
						end
					when :uuid
						# UUID detected
						id.downcase! # make it lowercase
						if mapping.value?(id)
							# Find corresponding barcode
							barcode = mapping.key(id)
							# Make sure UUID corresponds to the right type of barcode
							if Identifier.id_type(barcode)!=@primary_obj
								logger.write_error(archive, "(Line #{line_num} of #{filename}) '#{id}' is not a TCGA #{@primary_obj_str} UUID", step_num, path)
								has_error = true
							else
								# Good match - write to file
								if !has_error
									line.insert(comment_idx,barcode)
									outfile.puts line.join("\t")
								end
							end
						else
							# Unrecognized UUID
							logger.write_error(archive, "(Line #{line_num} of #{filename}) '#{id}' is not a recognized #{@primary_obj_str} UUID", step_num, path)
							has_error = true
						end
					when :reference
						# Non-TCGA reference detected
						if id.chomp == NULL_VALUE
							# Make sure reference is not null
							logger.write_error(archive, "(Line #{line_num} of #{filename}) '#{@primary_col}' cannot be null ('#{NULL_VALUE}')", step_num,path)
							has_error = true
						else
							# Write reference to file under primary column with '->' as null value for barcode
							if !has_error
								line.insert(comment_idx, NULL_VALUE)
								outfile.puts line.join("\t")
							end
							# Log warning
							logger.write_warning(archive, "(Line #{line_num} of #{filename}) Non-TCGA identifier '#{id}' detected", step_num,path)
						end
					else
						# Bad ID detected
						logger.write_error(archive, "(Line #{line_num} of #{filename}) '#{id}' is not a UUID or #{@primary_obj_str} barcode", step_num, path)
						has_error = true
				end
			end # while loop
		end # if comment_col_exist
		outfile.close
		infile.close
		
		# Return values based on conversion success
		return false if has_error # Error found
		# No conversion needed
		if comment_col_exist
			logger.write_error(archive, "No conversion needed for #{archive}/#{filename}", step_num,path)
			return false
		end
		# Conversion completed
		File.delete(filename) # Delete original
		File.rename(TEMP_FILE, filename) # Replace with converted file
		return true 
	end # self.convert( )
end # module