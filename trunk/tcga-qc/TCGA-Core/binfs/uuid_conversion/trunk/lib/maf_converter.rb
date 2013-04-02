module MafConverter
	TUMOR_BARCODE_COL = 'Tumor_Sample_Barcode' # Column header for tumor aliquot barcodes
	TUMOR_UUID_COL = 'Tumor_Sample_UUID' # Column header for tumor aliquot UUIDs
	NORM_BARCODE_COL = 'Matched_Norm_Sample_Barcode'  # Column header for matched normal aliquot barcodes
	NORM_UUID_COL = 'Matched_Norm_Sample_UUID' # Column header for matched normal aliquot UUID
	INSERT_IDX = 32 # 33rd column in sequence of required columns
	VALID_VERSION = "2.3" # Latest MAF version compatible with QCLive
	TEMP_FILE = 'temp' # Temporary name of converted file
	
	# Converts a MAF to be UUID compatible
	# Paramters:
	#		filename - Name of file to be converted
	#		mapping - Hash of barcode-to-uuid mappings
	#		archive - Name of archive to which the file belongs
	#		step_num - Step number of main process that calls this method
	#		path - Path to archive
	#		logger - Writer object so that errors/warnings can be added to file
	# Returns 'false' if an error occured during conversion, else true
	def self.convert(filename, mapping, archive, step_num, path, logger)
		has_error = false # is there an error somewhere in this file?
		infile = File.new(filename, 'r') # original file
		outfile = File.new(TEMP_FILE, 'w') # converted file
		is_incremented = false # MAF version of file has been incremented to VALID_VERSION
		is_converted = false # File has been converted by script
		tumor_barcode_idx = nil # Index of tumor barcode column
		norm_barcode_idx = nil # Index of normal barcode column
		tumor_uuid_idx = nil # Index of tumor UUID column
		norm_uuid_idx = nil # Index of normal UUID column
		
		# Convert to valid version of MAF
		line = infile.gets
		if !line.include?(VALID_VERSION)
			logger.write_warning(archive, "(Line 1 of #{filename}) Converted to MAF version #{VALID_VERSION}", step_num,path) 
			outfile.puts "#version #{VALID_VERSION}"
			is_incremented = true # Something has been modified in the file
		else
			# Already at latest version - repeat line in new output file
			outfile.puts line
		end
		
		# HEADER
		line = infile.gets.chomp # Read in header
		header = line.split("\t") # Split header into columns
		
		# Extract possibly mistyped UUID columns (See check below)
		tumor_uuid_label = line.match(/#{TUMOR_UUID_COL}/i)
		norm_uuid_label = line.match(/#{NORM_UUID_COL}/i)
		
		# Detect if conversion was attempted by data submission center or if conversion is needed
		if tumor_uuid_label or norm_uuid_label
			# ==== Conversion attempt detected - (Includes quality checks) ==== #
			puts "\tConverted file detected. Performing quality checks..."
			is_converted = true
			# Ensure both UUID columns exist
			if !tumor_uuid_label or !norm_uuid_label
				logger.write_error(archive, "(Line 1 of #{filename}) Both UUID columns, '#{TUMOR_UUID_COL}' and '#{NORM_UUID_COL}', must exist", step_num,path) 
				return false # Stop quality checks
			else
				# ==== Do quality checks ==== #
				# Extract tumor and normal labels
				if tumor_uuid_label
					tumor_uuid_label = tumor_uuid_label[0]
					# Check if tumor header is correct (checks case only - no fuzzy matching)
					if tumor_uuid_label!=TUMOR_UUID_COL
						logger.write_error(archive, "(Line 1 of #{filename}) The tumor aliquot UUID column must be '#{TUMOR_UUID_COL}' rather than '#{tumor_uuid_label}'", step_num,path) 
						has_error = true
					end
				end
				
				if norm_uuid_label
					norm_uuid_label = norm_uuid_label[0]
					# Check if normal header is correct (checks case only - no fuzzy matching)
					if norm_uuid_label!=NORM_UUID_COL
						logger.write_error(archive, "(Line 1 of #{filename}) The normal aliquot UUID column must be '#{NORM_UUID_COL}' rather than '#{norm_uuid_label}'", step_num,path) 
						has_error = true
					end
				end
				
				# Get ID column indices
				tumor_barcode_idx = header.index(TUMOR_BARCODE_COL)
				norm_barcode_idx = header.index(NORM_BARCODE_COL)
				tumor_uuid_idx = header.index(tumor_uuid_label)
				norm_uuid_idx = header.index(norm_uuid_label)
				
				# Are the UUID columns in the right place?
				if tumor_uuid_idx!= INSERT_IDX
					logger.write_error(archive, "(Line 1 of #{filename}) Column #{INSERT_IDX+1} must be '#{TUMOR_UUID_COL}'", step_num,path) 
					has_error = true
				end
				
				if norm_uuid_idx!= INSERT_IDX+1
					logger.write_error(archive, "(Line 1 of #{filename}) Column #{INSERT_IDX+2} must be '#{NORM_UUID_COL}'", step_num,path) 
					has_error = true
				end	
			end # if !tumor_uuid_label or !norm_uuid_label
			
		else # Detect conversion attempt by submission center
			
			# ==== Convert File ==== #
			puts "\tUnconverted file detected. Converting file..."
			# HEADER
			# Get indices of the  barcode columns
			tumor_barcode_idx = header.index(TUMOR_BARCODE_COL)
			norm_barcode_idx = header.index(NORM_BARCODE_COL)
			# Error check
			if !tumor_barcode_idx or !norm_barcode_idx
				logger.write_error(archive, "(Line 2 of #{filename}) Barcode columns '#{TUMOR_BARCODE_COL}' and/or '#{NORM_BARCODE_COL}' are missing", step_num,path)
				return false
			end
			# Insert the UUID columns
			header.insert(INSERT_IDX, NORM_UUID_COL) # Normal column follows tumor, so we insert it first
			header.insert(INSERT_IDX, TUMOR_UUID_COL) # Insert tumor UUID column
			outfile.puts header.join("\t") # Write headers to file
			
		end
		
		# CONTENT - Populate MAF with UUIDs under the 'Tumor_Sample_UUID' and 'Matched_Norm_Sample_UUID' columns
		line_num = 2
		while (line = infile.gets)
			line_num += 1
			next if line.match(/^$/)
			line = line.chomp.split("\t")
			
			# Check if barcodes are valid and map to corresponding UUIDs
			tumor_barcode = line[tumor_barcode_idx]
			norm_barcode = line[norm_barcode_idx]
			tumor_uuid = is_converted ? line[tumor_uuid_idx].downcase : nil
			norm_uuid = is_converted ? line[norm_uuid_idx].downcase : nil
			
			# Is the barcode a tumor aliquot barcode?
			if Identifier.id_type(tumor_barcode) != :aliquot or !Identifier.is_tumor_barcode?(tumor_barcode)
				logger.write_error(archive, "(Line #{line_num} of #{filename}) '#{tumor_barcode}' is not a tumor aliquot barcode", step_num,path)
				has_error =true
			else
				if is_converted
					# Quality check: Check UUID mapping
					if tumor_uuid != mapping[tumor_barcode]
						logger.write_error(archive, "(Line #{line_num} of #{filename}) UUID (#{tumor_uuid}) and barcode (#{tumor_barcode}) do not map to the same biospecimen", step_num,path)
						has_error=true
					end
				else
					# Conversion: Map to UUID
					tumor_uuid = mapping[tumor_barcode]
				end # is_converted
				# Is the tumor aliquot barcode registered at the DCC?
				if !tumor_uuid
					logger.write_error(archive, "(Line #{line_num} of #{filename}) '#{tumor_barcode}' is not a recognized TCGA aliquot barcode", step_num,path)
					has_error =true
				end
			end
			
			# Is the barcode a normal aliquot barcode?
			if Identifier.id_type(norm_barcode) != :aliquot or !Identifier.is_normal_barcode?(norm_barcode)
				logger.write_error(archive, "(Line #{line_num} of #{filename}) '#{norm_barcode}' is not a normal aliquot barcode", step_num,path)
				has_error =true
			else
				if is_converted
					# Quality check: Check UUID mapping
					if norm_uuid != mapping[norm_barcode]
						logger.write_error(archive, "(Line #{line_num} of #{filename}) UUID (#{norm_uuid}) and barcode (#{norm_barcode}) do not map to the same biospecimen", step_num,path)
						has_error=true
					end
				else
					# Conversion: Map to UUID
					norm_uuid = mapping[norm_barcode]
				end # is_converted
				# Is the normal aliquot barcode registered at the DCC?
				if !norm_uuid
					logger.write_error(archive, "(Line #{line_num} of #{filename}) '#{norm_barcode}' is not a recognized TCGA aliquot barcode", step_num,path)
					has_error =true
				end
			end

			# Insert UUIDs and write to file
			if !has_error and !is_converted
				line.insert(INSERT_IDX, norm_uuid) # Normal column follows tumor, so we insert it first
				line.insert(INSERT_IDX, tumor_uuid) # Insert tumor UUID
				outfile.puts line.join("\t")
			end
		end # while loop
		
		outfile.close
		infile.close
		
		return false if has_error # Errors found, conversion failed
		# No conversion needed
		if is_converted and !is_incremented
			logger.write_error(archive, "No conversion needed for #{archive}/#{filename}", step_num,path)
			return false
		end
		
		# Conversion completed
		File.delete(filename) # Delete original file
		File.rename(TEMP_FILE, filename) # Replace with converted file
		return true # Successful conversion
	end # self.convert( )
	
end # module