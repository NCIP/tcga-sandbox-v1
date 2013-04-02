infile = File.new(ARGV[0],'r')
outfile = File.new('outfile.txt','w')
outfile.puts "UUID\tBARCODE\tELEMENT_TYPE\tDISEASE_STUDY\tPARTICIPANT_CODE\tSAMPLE_CODE\tVIAL\tCENTER_CODE\tCENTER_TYPE\tPLATE_ID\tBCR\tRECEIVING_CENTER\tBATCH_NUMBER\tSHIP_DATE\tTSS_CODE\tPLATFORMS\tDATA_TYPES"
infile.each{|line|
	next if !line.include?(",")
	array = line.split("\t")
	platforms = array[-2].split(",")
	data_types = array[-1].split(",")
	outfile.puts line if platforms.size != data_types.size
}

outfile.close
infile.close