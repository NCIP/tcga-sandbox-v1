# ARGV[0] = File with list of archive paths
# ARGV[1] = File format extension of interest
WORK_DIR = "/h1/chual/"
input = File.new(ARGV[0], 'r')
input.each{|archive|
	maf_files = archive.sub(/\.tar\.gz/, "/*.#{ARGV[1]}")
	# Extract version from MAf
	version = (`head -n1 #{maf_files}`).delete "#"
	# Make version directory if it doesn't exist
	FileUtils.mkdir version if !File.directory?("#{version}")
	# Copy archive to appropriate directory
	FileUtils.cp(archive, "#{WORK_DIR}#{version}/")
}
input.close