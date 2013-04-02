module VcfConverter
	SAMPLE_HEADER = '##SAMPLE' # Declaration line for SAMPLE
	INDIVIDUAL_HEADER = '##INDIVIDUAL' # Declaration line for INDIVIDUAL
	SAMPLE_NAME_ATTR = 'SampleName' # SAMPLE attribute for sample name (old format)
	SAMPLE_UUID_ATTR = 'SampleUUID' # SAMPLE attribute for sample UUID
	SAMPLE_BARCODE_ATTR = 'SampleTCGABarcode' # SAMPLE attribute for sample UUID
	TEMP_FILE = 'temp' # Temporary name of converted file
	
	# Converts a VCF to be UUID compatible
	# Paramters:
	#		filename - Name of file to be converted
	#		mapping - Hash of barcode-to-uuid mappings
	#		archive - Name of archive to which the file belongs
	#		step_num - Step number of main process that calls this method
	#		path - Path to archive
	#		logger - Writer object so that errors/warnings can be added to file
	# Returns 'false' if an error occured during conversion, else true
	def self.convert(filename, mapping, archive, step_num, path, logger)
		infile = File.new(filename, 'r') # original file
		outfile = File.new(TEMP_FILE, 'w') # converted file
		has_error = false # Does the file contain errors?
		has_converted = false # Does the file contain converted ##SAMPLE lines?
		has_non_converted = false # Does the file contain non-converted ##SAMPLE lines?
		
		# HEADER
		aliquot_barcode = nil
		patient_barcode = nil
		line_num = 0
		while (line = infile.gets and line.match(/^##/))
			line_num += 1
			next if line.match(/^$/)
			if line =~ /^#{SAMPLE_HEADER}/
				# Determine if a file conversion was attempted by the data submission center
				if line.match(/#{SAMPLE_UUID_ATTR}|#{SAMPLE_BARCODE_ATTR}/i)
					# ==== Conversion attempt detected - Do quality checks ==== #
					puts "\tConverted file detected. Performing quality checks..."
					has_converted = true
					if has_non_converted
						# File contains both converted and non-converted ##SAMPLE lines
						logger.write_error(archive, "(Line #{line_num} of #{filename}) File contains non-converted and converted #{SAMPLE_HEADER} lines", step_num,path)
						has_error = true
					end
					# Check if required fields are existent and properly cased. Extract ID if it exists
					if line.include?(SAMPLE_UUID_ATTR)
						# Find UUID
						uuid = line.match(/#{SAMPLE_UUID_ATTR}\s?=\s?\W?([a-fA-F0-9]{8}-[a-fA-F0-9]{4}-[a-fA-F0-9]{4}-[a-fA-F0-9]{4}-[a-fA-F0-9]{12})/)
						if uuid
							uuid = uuid[1].downcase # Extract UUID
						else
							# Could not extract UUID
							logger.write_error(archive, "(Line #{line_num} of #{filename}) Incorrect UUID format for '#{SAMPLE_UUID_ATTR}'", step_num,path)
							has_error = true
						end
					else
						# No UUID field specified
						logger.write_error(archive, "(Line #{line_num} of #{filename}) UUID field must appear in #{SAMPLE_HEADER} line as '#{SAMPLE_UUID_ATTR}'", step_num,path)
						has_error = true
					end
					
					if line.include?(SAMPLE_BARCODE_ATTR)
						# Find aliquot barcode
						aliquot_barcode = line.match(/#{SAMPLE_BARCODE_ATTR}\s?=\s?\W?(TCGA[\-0-9A-Z]+)/)
						if aliquot_barcode
							aliquot_barcode = aliquot_barcode[1] # Extract barcode
							# Check that this is indeed an aliquot barcode
							if Identifier.id_type(aliquot_barcode) != :aliquot
								logger.write_error(archive, "(Line #{line_num} of #{filename}) Barcode provided for '#{SAMPLE_BARCODE_ATTR}' (#{aliquot_barcode}) is not an aliquot barcode", step_num,path)
								aliquot_barcode = nil # Bad aliquot barcode - assume this does not exist
								has_error = true
							end
						else
							# Could not extract barcode
							logger.write_error(archive, "(Line #{line_num} of #{filename}) Incorrect barcode format for '#{SAMPLE_BARCODE_ATTR}'", step_num,path)
							has_error = true
						end
					else
						# No barcode field specified
						logger.write_error(archive, "(Line #{line_num} of #{filename}) Barcode field must appear in #{SAMPLE_HEADER} line as '#{SAMPLE_BARCODE_ATTR}'", step_num,path)
						has_error = true
					end
					
					# Check mapping between barcode and UUID are correct
					if aliquot_barcode and uuid
						mapped_uuid = mapping[aliquot_barcode] # Find UUID for aliquot barcode
						if !mapped_uuid
							logger.write_error(archive, "(Line #{line_num} of #{filename}) '#{aliquot_barcode}' is not a recognized TCGA barcode", step_num,path)
							has_error = true
						elsif mapped_uuid != uuid
							logger.write_error(archive, "(Line #{line_num} of #{filename}) UUID (#{uuid}) and barcode (#{aliquot_barcode}) do not map to the same biospecimen", step_num,path)
							has_error = true
						end
					end
					
				else
					# ==== Convert File ==== #
					puts "\tUnconverted file detected. Converting file..."
					has_non_converted = true
					if has_converted
						# File contains both converted and non-converted ##SAMPLE lines
						logger.write_error(archive, "(Line #{line_num} of #{filename}) File contains non-converted and converted #{SAMPLE_HEADER} lines", step_num,path)
						has_error = true
					end
					# Extract barcode
					aliquot_barcode = line.match(/#{SAMPLE_NAME_ATTR}\s?=\s?\W?(TCGA[\-0-9A-Z]+)/)
					if aliquot_barcode
						aliquot_barcode = aliquot_barcode[1] # Extract barcode
						# Check that this is indeed an aliquot barcode
						if Identifier.id_type(aliquot_barcode) != :aliquot
							logger.write_error(archive, "(Line #{line_num} of #{filename}) Barcode provided for '#{SAMPLE_BARCODE_ATTR}' (#{aliquot_barcode}) is not an aliquot barcode", step_num,path)
							aliquot_barcode = nil # Bad aliquot barcode - assume this does not exist
							has_error = true
						else
							# Find corresponding UUID
							uuid = mapping[aliquot_barcode]
							# Is this a valid TCGA aliquot barcode/UUID?
							if !uuid
								logger.write_error(archive, "(Line #{line_num} of #{filename}) '#{aliquot_barcode}' is not a recognized TCGA barcode", step_num,path)
								has_error = true
							else
								# Convert the ##SAMPLE line to include 'SampleTCGABarcode' in place of 'SampleName' and the addition of 'SampleUUID'
								line.gsub!(/#{SAMPLE_NAME_ATTR}\s?=\s?\W?TCGA[\-0-9A-Z]+/,"#{SAMPLE_UUID_ATTR}=\"#{uuid}\",#{SAMPLE_BARCODE_ATTR}=\"#{aliquot_barcode}\"") if !has_error
							end
						end # Identifier.id_type()
					else
						logger.write_error(archive, "(Line #{line_num} of #{filename}) '#{SAMPLE_NAME_ATTR}' barcode missing from #{SAMPLE_HEADER}", step_num,path)
						has_error = true
					end # if aliquot_barcode
				end # File conversion detection
				
				# Error check - Do the patient and aliquot barcodes correspond?
				if patient_barcode and aliquot_barcode and !aliquot_barcode.include?(patient_barcode)
					logger.write_error(archive, "(Line #{line_num} of #{filename}) #{INDIVIDUAL_HEADER} barcode (#{patient_barcode}) and #{SAMPLE_HEADER} barcode (#{aliquot_barcode}) do not correspond", step_num,path)
					has_error = true
				end
			elsif line =~ /^#{INDIVIDUAL_HEADER}/
				# Extract patient barcode
				patient_barcode = line.match(/TCGA[\-0-9A-Z]+/)
				patient_barcode = patient_barcode[0] if patient_barcode
				if patient_barcode and aliquot_barcode and !aliquot_barcode.include?(patient_barcode)
					logger.write_error(archive, "(Line #{line_num} of #{filename}) #{INDIVIDUAL_HEADER} barcode (#{patient_barcode}) and #{SAMPLE_HEADER} barcode (#{aliquot_barcode}) do not correspond", step_num,path)
					has_error = true
				end
			end # if line =~
			outfile.puts line if !has_error # Write to file
		end # while loop
		
		# CONTENT
		if !has_error and !has_converted
			# Nothing to convert in body of VCF - transfer rest of content to file
			while (line = infile.gets)
				outfile.puts line
			end # while loop
			infile.close
			outfile.close
			File.delete(filename) # Delete original file
			File.rename(TEMP_FILE, filename) # Replace with converted file
			return true # Successful conversion
		else
			infile.close
			outfile.close
			return false if has_error # Unsuccessful conversion
			# No conversion needed
			if has_converted
				logger.write_error(archive, "No conversion needed for #{archive}/#{filename}", step_num,path)
				return false
			end
		end # if !has_error and !has_converted
		
	end # self.convert( )

end # module