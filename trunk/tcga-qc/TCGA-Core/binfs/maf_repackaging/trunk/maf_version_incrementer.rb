# Cycles through the directories in this directory. Locates MAF files and changes the
# version to the given one

old_version = "2.2"
new_version = "2.3"
temp_file = "tempfile"
manifest_file = "MANIFEST.txt"

Dir.glob("*"){|dir| 
	if File.directory? dir
		# Enter directory
		Dir.chdir(dir)
		Dir.foreach("."){|file|
			# Pick out MAF files
			if File.extname(file) == ".maf"
				filename = file # Store filename
				File.rename(file, temp_file) # Rename original file to some temporary name
				# Modify file
				infile = File.new(temp_file, 'r')
				newfile = File.new(file, 'w')
				newfile.puts infile.gets.sub(old_version, new_version) # Change MAF version
				# Copy rest of file
				while (line = infile.gets)
					newfile.puts line
				end
				newfile.close
				infile.close
				File.delete(temp_file) # Delete the temp file
			end
		}
		# Remove old manifest file
		File.delete(manifest_file)
		# Create new manifest file
		system("md5sum * > #{manifest_file}")
		# Exit directory
		Dir.chdir("..")
	end
}