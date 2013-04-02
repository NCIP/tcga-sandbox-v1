# Runs a list of archives against soundcheck and stores validation results in a text file per archive
# Input: ARGV[0] = path to file containing a list of archive paths
# Output: Per-archive validation results (*.result) from QCLive will be deposited in the same directory as this script
SOUNDCHECK_DIR = '/home/anna/Desktop/SHARE/uuid/soundcheck' #'/h1/chual/soundcheck' # path to soundcheck

# Error message for incorrect use
abort_msg = "Usage: ruby soundcheck_automation.rb <archive_paths.txt>"
abort abort_msg if ARGV[0]==nil
abort "Can't locate file" if !File.exist?(ARGV[0])

infile = File.new(ARGV[0], 'r')

# Enter soundcheck's directory
this_dir = Dir.pwd
Dir.chdir(SOUNDCHECK_DIR)

# Call soundcheck for each archive
infile.each{|line|
	next if line.match(/^\s?$/) # skip blank lines
	path = line.chomp!
	next if !path
	archive_name = File.basename(path)
	
	# Determine center type and run soundcheck
	puts "Validating #{archive_name}..."
	case archive_name
		when /mage-tab/
			# For mage-tab, you want to validate data levels as well
			`nohup ./validate.sh #{path} -bypass -noremote -centertype CGCC > #{this_dir}/#{archive_name}.result &`
		when /(intgen|nationwide)/
			# NOT TESTED - This wouldn't work actually because nationwide sends microsatellite, which is CGCC data
			`nohup ./validate.sh #{path} -bypass -noremote -centertype BCR > #{this_dir}/#{archive_name}.result &`
		else
			`nohup ./validate.sh #{path} -bypass -noremote -centertype GSC > #{this_dir}/#{archive_name}.result &`
	end
	
}

