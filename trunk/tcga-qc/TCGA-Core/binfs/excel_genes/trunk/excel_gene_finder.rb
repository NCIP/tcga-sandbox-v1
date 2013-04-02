# Script for seeking "excel genes" in a given file
# INPUT: ARGV[0]=path to file with excel genes
# OUTPUT: File 'excel_gene_summary.txt' which lists the excel gene and the number of times it exists in the file (as two columns)

infile = File.new(ARGV[0],'r')
gene_hash = Hash.new #[key]=excel_gene [value]=count

infile.each{|line|
	gene_extract = line.scan(/\s(\d+\-[A-Z][a-z]+)/)
	gene_extract.each{|gene|
		gene = gene[0] # Pull excel gene from nested array
		# If gene exists, increment count; otherwise, add to hash
		gene_hash.has_key?(gene) ? gene_hash[gene] += 1 : gene_hash[gene] = 1
		puts "#{gene}: #{gene_hash[gene]}" #Remove
	}
}
infile.close

# Print results to file
outfile = File.new("excel_gene_summary.txt",'w')
outfile.puts "Excel Gene\tOccurrences"
gene_hash.each{|gene,count|
	outfile.puts "#{gene}\t#{count}"
}
outfile.close