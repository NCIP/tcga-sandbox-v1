class Writer
	# Conversion logs
	CONVERTED_FILENAME = 'converted_log.txt' # File listing successfully converted archives
	ERROR_FILENAME = 'error_log.txt' # File listing archives that failed during conversion and reasons for failure
	WARNING_FILENAME = 'warning_log.txt' # File listing archives that contain possible errors
	attr_accessor :converted_log, :error_log, :warning_log
	
	# Creates a new logs in the provided working directory and populate with headers
	def initialize(working_dir, mapping_file)
		# Print mapping file path to top of logs so this can be tracked
		id_map_msg = "ID mapping file: #{mapping_file}"
		# Converted log
		@converted_log = File.new(working_dir + "/#{CONVERTED_FILENAME}",'w')
		@converted_log.puts id_map_msg
		@converted_log.puts "ORIGINAL ARCHIVE\tCONVERTED ARCHIVE"
		# Error log
		@error_log = File.new(working_dir + "/#{ERROR_FILENAME}",'w')
		@error_log.puts id_map_msg
		@error_log.puts "ARCHIVE\tERROR\tSTOPPED AT\tARCHIVE PATH"
		# Warning log
		@warning_log = File.new(working_dir + "/#{WARNING_FILENAME}",'w')
		@warning_log.puts id_map_msg
		@warning_log.puts "ARCHIVE\tWARNING\tFOUND AT\tARCHIVE PATH"		
	end

	# Writes to ERROR_LOG
	# archive = name of the (original) archive that failed conversion
	# error_msg = reason for failure
	# step_num = (int) step # at which error was created
	# archive_path = absolute path to archive
	def write_error(archive, error_msg, step_num, archive_path)
		@error_log.puts "#{archive}\t#{error_msg}\tSTEP #{step_num}\t#{archive_path}"
	end
	
	# Writes to WARNING_LOG
	# archive = name of the (original) archive that contains warning
	# warning_msg = reason for warning
	# step_num = (int) step # at which warning was created
	# archive_path = absolute path to archive
	def write_warning(archive, warning_msg, step_num, archive_path)
		@warning_log.puts "#{archive}\t#{warning_msg}\tSTEP #{step_num}\t#{archive_path}"
	end

	# Writes to CONVERTED_LOG
	# original_archive = pre-converted archive name
	# new_archive = converted archive name
	def write_converted(original_archive, new_archive)
		@converted_log.puts "#{original_archive}\t#{new_archive}"
	end
end
