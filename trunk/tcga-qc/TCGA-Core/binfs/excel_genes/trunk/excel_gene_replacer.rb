# Script for replacing "excel genes" with their expected mapping
# INPUT: ARGV[0] = path to file with excel genes; ARGV[1] = path to file with mappings (column 0 = excel gene; column 1 = correct gene symbol)
# OUTPUT:
# - modified.txt: ARGV[0] file with excel genes replaced with their mapped HUGO gene
# - summary.txt: lists the excel gene and the number of times it was replaced in the file (as two columns)

# Read in mappings
mapper = Hash.new #[key]=excel_gene [value]=HUGO_gene
infile = File.new(ARGV[1],'r')
infile.each{|line|
	mapping = line.strip.split("\t")
	mapper[mapping[0]] = mapping[1] if mapping
}

# Replace excel genes in file
infile = File.new(ARGV[0],'r')
outfile = File.new("modified.txt",'w')
gene_hash = Hash.new #[key]=excel_gene [value]=count

infile.each{|line|
	# Add to tally of replacements made for gene
	gene_extract = line.scan(/\s(\d+\-[A-Z][a-z]+)/)
	gene_extract.each{|gene|
		gene = gene[0]
		gene_hash.has_key?(gene) ? gene_hash[gene] += 1 : gene_hash[gene] = 1
		puts "#{gene}: #{gene_hash[gene]}" #Remove
		line.sub!(gene, "\t#{mapper[gene]}")
	}
	
	# Replace gene in line or return as-is
	outfile.puts line
}
outfile.close
infile.close

# Print summary of replacement counts
summary_file = File.new("summary.txt",'w')
summary_file.puts "Excel Gene\tOccurrences"
gene_hash.each{|gene,count|
	summary_file.puts "#{gene}\t#{count}"
}
summary_file.close