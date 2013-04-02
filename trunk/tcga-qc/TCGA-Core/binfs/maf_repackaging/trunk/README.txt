Note: If you don't have ruby access on the server, add '/h1/chual/ruby/bin to path. You can permanently add this by saving the following to the end of ~/.bashrc, logging off then back on.

"PATH=/h1/chual/ruby/bin:$PATH
export PATH"
--------------------

1) Run query.sql and save output to text file.
	- Make sure text file does not contain double-quotes
	- This query gets the deploy location of all is_latest MAF archives. Modify if necessary.

2) Copy file_download.rb to the server and modify the value of WORK_DIR. WORK_DIR is where your MAF archives will be copied to.

3) Run file_download.rb:
	>ruby file_download.rb archive_paths.txt maf
	
4) Expand all the archives (in working directory):
	>for archive in *.tar.gz; do tar -zvxf $archive; done
	
5) Put maf_version_incrementer.rb into the working directory and run it
	>ruby maf_version_incrementer.rb

6) Repackage the archives (in the directory with the newly created directories)
	>for dir in *; do tar -zvcf $dir.tar.gz $dir; done

7) Create MD5 files
	>for archive in *.tar.gz; do md5sum $archive > $archive.md5; done