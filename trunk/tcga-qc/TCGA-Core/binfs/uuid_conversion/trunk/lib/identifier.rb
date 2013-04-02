module Identifier

	# Reads shipped item mappings between barcode and uuid from the database
	# and returns this mapping in a hash
	def self.db_mapping(username, password, db_path)
		# Build the query to get all archives for the given disease studies
		query = "SELECT bh.barcode,u.uuid FROM uuid u, barcode_history bh, uuid_item_type i WHERE i.item_type in ('Aliquot', 'Shipped Portion') AND u.latest_barcode_id = bh.barcode_id AND bh.item_type_id = i.item_type_id"

		# Connect to the database
		require 'oci8'
		conn = OCI8.new(username, password, db_path)

		# Execute query and put results in hash
		id_hash = Hash.new
		conn.exec(query) {|barcode, uuid|
			# Ensure that no barcode or UUID pre-exists in table
			abort("Duplicate barcode #{barcode} exists") if id_hash[barcode]
			abort("Duplicate UUID #{uuid} exists") if id_hash[uuid]
			id_hash[barcode] = uuid# Add values to table
		}
		conn.logoff
		return id_hash
	end
	
	# Reads shipped item mappings between barcode and uuid from a file
	# and returns this mapping in a hash
	def self.file_mapping(file)
		id_hash = Hash.new
		File.new(file).each{|line|
			line = line.chomp.split("\t")
			# Ensure that no barcode or UUID pre-exists in table
			abort("Duplicate barcode #{line[0]} exists") if id_hash[line[0]]
			abort("Duplicate UUID #{line[1]} exists") if id_hash[line[1]]
			id_hash[line[0]] = line[1] # Add values to table
		}
		return id_hash
	end
	
	# Determines the type of ID being passed in
	# 		1) aliquot barcode
	#		2) shipped portion barcode
	# 		3) UUID
	#		4) faulty barcode/UUID
	#		5) non-TCGA reference
	def self.id_type(identifier)
		case identifier
			when /TCGA\-\w{2}\-\w{4}\-\d{2}[a-zA-Z]\-\d{2}[a-zA-Z]\-\w{4}\-\d{2}/
				# Aliquot barcode
				return :aliquot
			when /TCGA\-\w{2}\-\w{4}\-\d{2}[a-zA-Z]\-\d{2}\-\w{4}\-\d{2}/
				# Shipped portion barcode
				return :shipped_portion
			when /[a-fA-F0-9]{8}-[a-fA-F0-9]{4}-[a-fA-F0-9]{4}-[a-fA-F0-9]{4}-[a-fA-F0-9]{12}/
				# UUID
				return :uuid
			when /(^TCGA\-\d{2}\-|^[a-fA-F0-9]{8}\-|[a-fA-F0-9]{4,8}\-[a-fA-F0-9]{4,8}\-)/
				# Faulty barcode/UUID
				return :bad_id
			else
				# Non-TCGA reference
				return :reference
		end
	end
	
	# Determines if the given barcode is a tumor barcode
	# Returns 'true' for tumor
	def self.is_tumor_barcode?(barcode)
		return barcode.match(/TCGA\-\w{2}\-\w{4}\-0[1-9][a-zA-Z]/) ? true : false
	end
	
	# Determines if the given barcode is a normal barcode
	# Returns 'true' for normal
	def self.is_normal_barcode?(barcode)
		return barcode.match(/TCGA\-\w{2}\-\w{4}\-1[0-4][a-zA-Z]/) ? true : false
	end

end