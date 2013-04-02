#!/bin/perl -w
####################################################################################################
#	EESnyder.pm
#	Thu Jun 23 11:39:58 EDT 2011
#	v1.0.8 (dev)
####################################################################################################
#	Eric E. Snyder (c) 2008
#	Virgnia Bioinformatics Institute
#	Virginia Polytechnic Institute and State University
#	Blacksburg, VA 24061-0447
#	USA
####################################################################################################
####################################################################################################
#	Routines in this module:
#
#	File Format Parser (Readers)
#		FASTA
#			read_fasta()						for backward compatibility (future use deprecated)
#			read_fasta_fromfp()
#			read_fasta_fromfile()
#			read_multifasta()					for backward compatibility (future use deprecated)
#			read_multifasta_file()				for backward compatibility (future use deprecated)
#			read_multifasta_fromfp()
#			read_multifasta_fromfile()
#		BLAST
#			read_blast()
#			read_blast_file()
#		GFF3
#			read_gff3_file()
#			parse_feature_attributes()
#		Genome->Chromosome Table
#			read_genome2chr()
#	Sequence Processing
#			reverse_complement()
#	k-tuple Counting
#			init_ktuple_table()
#	Sequence Formatting
#			format_fasta()
#	Retrieving Data from NCBI
#			efetch
#	General Purpose
#			read_table_in2_LoL_fromfile()
#			read_table_in2_LoL_fromfh()
#			read_2col_data2hash_fromfile()
#			read_table_in2_LoH_fromfile()
#			read_table_in2_LoH_fromtext()
#			read_table_in2_LoH_fromfh()
#			read_table_in2_LoH_fromRRfile()
#			read_table_in2_LoH_fromRRtext()
#			read_table_in2_LoH_fromRRfh()
#			read_table_in2_tiedHoH_fromfile()
#			read_table_in2_tiedHoH_fromtext()
#			read_table_in2_tiedHoH_fromfh()
#			write_file_by_name()
#			get_date()
#			wrapLongSequence()
#			id_subroutine()
#			truth()
####################################################################################################
use strict;
use English qw( -no_match_vars );  # Avoids regex performance penalty
use Tie::IxHash;
package EESnyder;

require Exporter;
our @ISA = qw(Exporter);
our %opts;
our @EXPORT = qw(	read_fasta_fromfp
					read_fasta
					read_fasta_fromfile
					read_multifasta_fromfp
					read_multifasta_fromfile
					read_multifasta
					read_multifasta_file
					read_blast_fromfp
					read_blast_fromfile
					read_blast
					read_blast_file
					run_blastall
					format_fasta
					read_gff3_fromfh
					read_gff3_fromfile
					read_gff3_file
					read_gff3
					parse_feature_attributes
					merge_overlapping_intervals
					read_table
					read_genome2chr
					read_master_names
					reverse_complement
					init_ktuple_table
					efetch
					read_table_in2_LoL_fromfile
					read_table_in2_LoL_fromfh
					read_table_in2_LoH_fromfile
					read_table_in2_LoH_fromtext
					read_table_in2_LoH_fromfh
					read_table_in2_LoH_fromRRfile
					read_table_in2_LoH_fromRRtext
					read_table_in2_LoH_fromRRfh
					read_table_in2_tiedHoH_fromfile
					read_table_in2_tiedHoH_fromtext
					read_table_in2_tiedHoH_fromfh
					write_file_by_name
					format_text
					id_subroutine
					writeMultifasta2file
					writeMultifasta2fp
					wrapLongSequence
					readFastaFromfp
					readFastaFromfile
					readMultifastaFromfp
					readMultifastaFromfp2list
					readMultifastaFromfile
					exec_pgm_ifRequired
					systemCallErrorCheck
					read_2col_data2hash_fromfile
					get_date truth );

###################################################################################################
#	read_fasta_fromfp()
#	my $seqHash = &read_fasta( \*FILE );
#	Given a pointer to a FASTA format file containing one sequence, function returns pointer to
# 	hash containing information about sequence:
#
#	%seqence = (	'seq'		=>	"TTTTCCACACTTATCCACAGGGCGCGGGCGGGACTCGGTTGCCCCTCTGA...",
#					'length'	=>	2122487,
#					'id'		=>	'NC_010742.1',
#					'desc'		=>	'Brucella abortus S19',
#				);
####################################################################################################
sub read_fasta_fromfp {
	my ( $fp ) = @_;

	my ( $id, $description, $header ) = ( "", "", "" );				# header-related vars
	my ( $seq ) = ( "" );											# sequence-related vars
	while( <$fp> ){
		chomp;
		next if /^#/;
		next if /^\s*$/;
		if ( /^>/ ){
			$header = $_;											# grab FASTA header
			next;
		}
		$seq .= $_;													# append line to growing sequence
	}
	if ( $header =~ /^>(\S+)\s+(.+)$/ ){							# parse FASTA header with id & desc
		$id = $1;													# seqid and ...
		$description = $2;											# description
	} elsif ( $header =~ /^>(\S+)$/ ){								# parse FASTA header with id only
		$id = $1;
	} else {
	}

	my $length = length( $seq );									# measure sequence length

	my %sequence =	(	'seq'		=>	$seq,						# package sequence variables
						'length'	=>	$length,					# into sequence hash
						'id'		=>	$id,
						'desc'		=>	$description,
						'header'	=>	$header,					# unparsed FASTA header
					);

	return( \%sequence );											# return sequence hash
}
####################################################################################################
sub read_fasta {
	return ( &read_fasta_fromfp( @_ ) );
}
####################################################################################################
sub read_fasta_fromfile {
	my ( $file ) = @_;

	open( FILE, "$file" ) or die "Cannot open FASTA file: \"$file\" for reading.\n";
	my $seqs = &read_fasta_fromfp( \*FILE );
	close( FILE );
	return( $seqs );
}
####################################################################################################
#	read_multifasta_fromfp()
#	my $seqHash = &read_fasta( \*FILE, $filter, $sflag );
#	Given a pointer to a FASTA format file containing many sequences and a regular expression filter
#	such that:
#
#		$id =~ s/$filter/$1/;
#
#	will yield the desired sequence identifier.  For example:
#
#		FASTA file contains:	"patric|NC_003317.1.329"
#		desired seqid:			"NC_003317.1"
#
#		$filter = "\w+\|(\w+\.\d)\.\d+"
#
#	The sflag, if present, indicates that the sequence should not be returned in the %seqs hash.  In
#	other words, the FASTA file was simply parsed for information about the sequences rather than
#	the sequences themselves.
#
#	The function returns pointer to	hash containing information about sequences:
#
#	%seqs = (	'NC_010742.1'	=>	{	'seq'		=>	"TTTTCCACACTTATCCACAGGGCGCGGGCGGGACTCGGTTGCCCCTCTGA...",
#										'length'	=>	2122487,
#										'id'		=>	'NC_010742.1',
#										'desc'		=>	'Brucella abortus S19',
#										'header'	=>	'NC_010742.1	Brucella abortus S19',
#									},
#				'NC_010740.1'	=>	{	...
#									},
#				...
#			);
####################################################################################################
sub read_multifasta_fromfp {
	my ( $fp, $filter, $sflag ) = @_;

	my %seqs = ();														# hash for storing sequence records
	$/ = "\n>";															# change input record separator
	while( <$fp> ){
		chomp;
		s/^>//;															# remove initial '>' (from first record)
		my ( $header, $seq ) = ( "HEADER", "SEQUENCE" );
		( $header, $seq ) = split( "\n", $_, 2 );
#		print "header = \"$header\"; seq = \"$seq\"\n";
		my ( $id, $description ) = ( "", "" );
		if ( $header =~ /^(\S+)\s+(.+)$/ ){								# parse FASTA header with id & desc
			$id = $1;													# seqid and ...
			$description = $2;											# description
		} elsif ( $header =~ /^(\S+)\s*$/ ){							# parse FASTA header with id only
			$id = $1;
		} else {
			die "Cannot parse FASTA header line:\nheader = \"$header\"\n";
		}
		if ( $filter ){
			$id =~ s/$filter/$1/;										# regex filter for seqid
		}
		unless ( $seq ) {
			warn "For header: \"$header\", no corresponding sequence found.\n";
			warn "Skipping.\n";
			next;
		}
		$seq =~ s/\s+//g;												# remove whitespace from sequence
		$seq = uc $seq;													# force to uppercase
		my $length = length( $seq );									# measure sequence length
		my %sequence =	(	'length'	=>	$length,					# package sequence variables into sequence hash
							'id'		=>	$id,						# from '>' to first whitespace; seqid
							'desc'		=>	$description,				# everything after first whitespace
							'header'	=>	$header,					# the unprocessed header
						);
		$sequence{'seq'} = $seq;										# add actual sequence if sflag not set
		$seqs{ $id } = \%sequence unless $sflag;
		print "seqs{$id}{'length'} = \"$seqs{$id}{'length'}\"\n" if $opts{'D'};
	}
	$/ = "\n";															# change input record separator
	return( \%seqs );													# return sequence hash
}
####################################################################################################
#	read_multifasta_fromfile()
#	Wrapper for read_multifasta_fromfp() which takes a FASTA file name plus additional arguments and
#	opens the file, supplying an informative message on failure.  The resulting filehandle is used
#	to call read_multifasta_fromfp() with the filter as an argument.
####################################################################################################
sub read_multifasta_fromfile {
	my $file = shift @_;

	open( FILE, "$file" ) or die "Cannot open multifasta file: \"$file\" for reading.\n";
	my $seqs = &read_multifasta_fromfp( \*FILE, @_ );
	close( FILE );
	return( $seqs );
}
####################################################################################################
#	read_multifasta()
#	Alias for read_multifasta_fromfp()
####################################################################################################
sub read_multifasta {
	return( &read_multifasta_fromfp( @_ ) );
}
####################################################################################################
#	read_multifasta_file()
#	Alias for read_multifasta_fromfile();
####################################################################################################
sub read_multifasta_file {
	return( &read_multifasta_fromfile( @_ ) );
}
####################################################################################################
#	read_blast_fromfp()
#	my $blastHash = &read_blast_fromfp( \*FILE );
#	Given a filehandle to a BLAST output file, function return a pointer to a hash of hashes
#	containing the results.
#
#	Fields:
#	(1)	pct_id 		(2)  align_len 		(3) mismatches 		(4) gap_opens
#	(5) q_start 	(6)  q_end 			(7) s_start 		(8) s_end
#	(9) e-value 	(10) bit_score
#
#						 	 Fields:   (1)  (2)  (3) (4)  (5)  (6)  (7)  (8) (9) (10)
#
#	VBI0010BM1_0748 VBI3958BC1_1378	 99.75 2771   5   2     1 2771    1 2769 0.0 5422
#	VBI0010BM1_0748 VBI6002BOv1_1439 99.28 1104   3   2     1 1104   31 1129 0.0 2115
#	VBI0010BM1_0748 VBI6002BOv1_1439 99.69  654   2   0  2118 2771 1740 2393 0.0 1281
#	VBI0010BM1_1370 VBI0010BM1_1370 100.00 1157   0   0     1 1157    1 1157 0.0 2294
#
#	%bhash	=	(	VBI0010BM1_0748	=>	{	VBI3958BC1_1378	=>	[	(	pct_id		=>	99.75,
#																		align_len	=>	2771,
#																		...
#																	),
#																],
#											VBI6002BOv1_1439 => [	(   ), (   ),
#																],
#										},
#					VBI0010BM1_0749	=>	{
#										},
#					...
#				);
#
#	To reprint BLAST results (e.g., with filters applied):
#
#		my ( $bxdat, $bfields ) = &read_blast_file( $blastx_output, [ ( 'fid\|\d+\|locus\|(\w+)$', '^.+\|(\w+\.*\d*)$' ) ] );
#		foreach my $q ( keys %$bxdat ){
#			print "query: \"$q\"\n";
#			foreach my $t ( keys %{$bxdat->{$q}} ){
#				print "target: \"$t\"\n";
#				my $i = 0;
#				foreach my $a ( @{$bxdat->{$q}{$t}} ){
#					foreach my $f ( @$bfields ){
#	#					print "bxdat->{$q}{$f}[$i] = \"$a->{$f}\"\n";
#						print "$a->{$f}\t";
#					}
#					$i++;
#					print "\n";
#				}
#			}
#		}
#
####################################################################################################
sub read_blast_fromfp {
	my ( $fp ) = @_;

 	my @blast_fields 	= qw(	query_id subject_id pct_id align_len mismatches
								gap_opens q_start q_end s_start s_end e-value
								bit_score );
	my %bdat = ();														# blast data hash
	my ( $qid, $tid ) = ( "", "" );										# current query and target ids
	while( <$fp> ){														# read data from file
		chomp;
		next if /^#/;
		next if /^\s*$/;
		my @fields = split( "\t" );										# split line into fields
		if ( $fields[0] eq $qid ){										# if still reading data from same query ...
																		# continue
		} else {														# if not ...
			$qid = $fields[0];											# reset qid
		}
		if ( $fields[1] eq $tid ){										# if still reading data from same target ...
																		# continue
		} else {														# if not
			$tid = $fields[1];											# reset tid
		}
		my %hit = ();													# init hash for alignment info
		for( my $i = 0; $i < @blast_fields; $i++ ){						# loop through list of field names
			$hit{ $blast_fields[$i] } = $fields[$i];					# and asign fields to hash by name
		}
		push( @{ $bdat{ $qid }{$tid } }, \%hit );						# then add the hit hash to array of hits
																		# asigned to data hash keyed on query
																		# and target ids
	}
	return( \%bdat, \@blast_fields );
}
####################################################################################################
sub read_blast_fromfile {
	my ( $file ) = @_;

	open( FILE, "$file" ) or die "Cannot open blast file: \"$file\" for reading.\n";
	my @returns = &read_blast_fromfp( \*FILE );
	close( FILE );
	return( @returns );
}
####################################################################################################
sub read_blast {
	return( &read_blast_fromfp( @_ ) );
}
####################################################################################################
sub read_blast_file {
	return( &read_blast_fromfile( @_ ) );
}
####################################################################################################
#	run_blastall()
#
#	Parameters:
#		$pgm		blast program name (e.g., blastx, blastn, tblastn, tblastx, blastp, megablast )
#		$infile		input sequence file
#		$db			database name
#		$outfile	file to which BLAST output is written
#		$params		BLAST command line options (other than -p, -i, -d and -o)
#
#	Execute a BLAST search using the specified program and parameters; returns command line that
#	was executed.
####################################################################################################
sub run_blastall {
	my ( $pgm, $infile, $db, $outfile, $params ) = @_;

	my @blast_pgm = qw( blastx blastn tblastn tblastx blastp megablast );	# valid blastall programs
	my $valid = 0;															# is input program valid?
	foreach my $program ( @blast_pgm ){										# foreach valid program name
		if ( $pgm eq $program ){											# check input against them
			$valid++;														# mark as valid
			last;															# stop looking
		}
	}
	unless ( $valid ){
		die "Program name: \"$pgm\" not a valid BLAST program in run_blastall().\n";
	}
	unless ( -e $infile && !-z $infile ){
		die "Infile: \"$infile\" does not exist or is of zero size in run_blastall()\n";
	}

	my $comment = "";														# comment if blast not run
	my $cmd = "blastall -p $pgm -d $db -i $infile $params -o $outfile"; 	# generate BLAST command
	if (  -e $outfile && !-z $outfile ){									# if blastx output exists & has non-zero size
		$comment = "blastall not run; using results in \"$outfile\".\n";	# message about not running
	} else {																# if it does NOT exist or it is empty...
		if( `$cmd` ){														# execute command & check return value
			die "Execution of command failed:\ncmd = \"$cmd\"\n";			# if non-zero => error
		}
	}
	return( "\n$cmd$comment\n" );											# return command line executed
}																			# or w/comment if not executed
####################################################################################################
#	Given a pointer to a FASTA file record, prepare to print it in FASTA format
####################################################################################################
sub format_fasta {
	my ( $rec, $width ) = @_;

	my $seq = $rec->{'seq'};
	$seq =~ s/\s+//g;													# remove whitespace from seq, if any; 2009-02-02
	$seq =~ s/(.{$width})/$1\n/g;
#	chomp $seq;
	$seq =~ s/\n+$//;
	my $output = ">$rec->{'id'} $rec->{'desc'}\n$seq\n";

	return( $output );
}
## begin read_gff3 related #########################################################################
####################################################################################################
#	read_gff3_fromfh()
#	Function reads an entire GFF3 file and returns pointers to resulting data structures:
#
#	%gff3	=	(	'seqhash'	=>	\%seqs,
#					'fileinfo'	=>	\%gff_info,
#				);
#
#	%seqs	=	(	NC_006932.1	=>	\%seq1,								# "contig" sequences id'd in header
#					NC_006933.1 =>	\%seq2,
#					...
#				);
#
#	%seq1	=	(	'id'		=>	NC_006932.1,
#					'start'		=>	1,
#					'end'		=>	2124241,
#					'sequence'	=>	'TTTTCCACACTTATCCACAGGGCGCGGGCGGGACTCGGTTGCCCCTCTGA...',
#					'feat_list'	=>	[	\%feat1, \%feat2, ... ],
#					'feat_type_list'	=>	{	'contig'	=>	[	\%contig1, \%contig2, ... ],
#												'gene'		=>	[	\%gene1, \%gene2, ... ],
#												'CDS'		=>	[	\%cds1, \%cds2, ... ],
#												...
#											},
## (disabled)		'feat_hash'	=>	\%hash,								# hash of features keyed on feature ID
#				);
#
#	%feat1	=	(	'seqid'		=>	'patric|NC_006932.1.306',
#					'source'	=>	PATRIC,
#					'type'		=>	CDS,
#					'start'		=>	634,
#					'end'		=>	2274,
#					'score'		=>	'.',
#					'strand'	=>	'+',
#					'phase'		=>	'.',
#					'attributes'=>	'ID=patric|cds.000002.489485;Parent=gene.000001.489484;Dbxref=...',
#					'translation'=>	'MTSARTMARTNVKAGETMAMMTGKSTATIGDNAHANRIHGSSATGNDKSL...',
#					'featid'	=>	'patric|cds.000002.489485',
#					'attrib_hash'=>	\%attribute_hash,
#				);
#
#	%gff_info	=	(	'gff-version'			=>	3,
#						'feature-ontology'		=>	so.obo,
#						'attribute-ontology'	=>	gff3_attributes.obo,
#					);
#
#	Not part of GFF3 structure returned:
#
#	%feat_hash	=	(	"patric|cds.000002.489485"	=>	"MTSARTMARTNVKAGETMAMMTGKSTATIGDNAHANRIHGSSAT....",
#						"patric|cds.000028.493713"	=>	"LFRGCADNAAPPKEKKNEPEILSSVAFAATIGFASAAYADITIG....",
#						...
#					);
#	%id2feat	=	(	"patric|cds.000002.489485"	=>	\%feat1,
#						"patric|cds.000028.493713"	=>	\%feat2,
#						...
#					);
#
#	Note:
#	For PATRIC sequences, although the sequence-region sequence is identified by its NCBI accession
#	number only, all other references are to accession number + Project and seq id.  For example:
#
#		##sequence-region   NC_006932.1 1   2124241
#							^^^^^^^^^^^ GenBank Accno only
#		patric|NC_006932.1.306  PATRIC  contig  1   2124241 .   +   .   ID=patric|NC_006932.1.306
#		^^^^^^^^^^^^^^^^^^^^^^ Accno + project + seq_id					^^^^^^^^^^^^^^^^^^^^^^^^^
#	Note: as of Tue Feb 24 16:55:29 EST 2009 this is no longer true!
####################################################################################################
sub read_gff3_fromfh {
	my ( $fh ) = @_;
#							1	  2		3	  4	   5	6	  7		 8		 9
	my @gff3_cols	= qw( seqid source type start end score strand phase attributes );

	my $gff_info = ( {} );
	my %seqs = ();
	my %tmp_seqs = ();
	my %id2feat = ();

#	read BRC GFF3 header

	while( <$fh> ){																# read gff file header
		if ( /^##sequence-region\s+(\S+)\s+(\d+)\s+(\S+)/ ){					# seq region defines chromosome (or plasmid)
			my %hash	= (	'id'	=> $1, 										# name (without project or seqid)
							'start'	=> $2, 										# start position (usually 1)
							'end'	=> $3	);									# end position (=> length)
			$tmp_seqs{ $1 } = \%hash;											# assign seq region hash to %seqs
		}
		$gff_info->{$1} = $2 if ( /^##(\w+-ontology)\s+(\S+)/ );
		$gff_info->{$1} = $2 if ( /^##(gff-version)\s+(\d+)/ );
		last if ( /^##attribute-ontology/ );
	}

#	read GFF3 feature lines

	my %tmp_feats_by_type = ();													# temporary feats keyed on type (and seqid) (before sorting)
	my %feat_types_by_seqid = ();												# hash of different feature type counts by sequence
	while( <$fh> ){																# read GFF feature lines
		last if /^##FASTA/;														# breakout when FASTA starts
		my @cols = split( /\t/ );												# split GFF line into fields
		my %feat = ();															# init hash to represent data line
		for( my $i = 0; $i <  @gff3_cols; $i++ ){								# loop through GFF column names
			$feat{ $gff3_cols[$i] }	= $cols[$i];								# assign value to hash keyed on col name
			$feat{ $gff3_cols[0] }	=~ s/^\w+\|(\w+\.\d)\.\d+/$1/;				# remove project and extra .\d+ from sequence ID
		}
		if ( exists $feat_types_by_seqid{ $feat{'type'} } ){					# counting examples of features types
			$feat_types_by_seqid{$feat{'seqid'}}{ $feat{'type'} }++;			# such as gene, CDS, source, etc.
		} else {
			$feat_types_by_seqid{$feat{'seqid'}}{ $feat{'type'} } = 1;
		}
		my $attribs = &parse_feature_attributes( $feat{'attributes'} ); 		# get hash of attributes
		$feat{ 'attrib_hash'} = $attribs;										# assign attributes to attrib_hash field
#		my $id = $feat{ 'featid' } = $attribs->{'ID'};							# assign feature ID
		my $id =  $attribs->{'ID'};												# assign feature ID
		if ( 1 ){																# was $opt_i
			$feat{'featid'} = $attribs->{'ID'};
		} else {
			my $fid = "";
			if ( $attribs->{'ID'} =~ /\.(\d+)$/ ){								# get feature ID from last number in ID
				$fid = $1;
			} else {
				die "Cannot parse fid from \"$attribs->{'ID'}\".\n";
			}
			$feat{'featid'} = "fid|$fid|locus|$attribs->{'Name'}";				# make something like: "fid|77907|locus|VBI_00010BM1_0728"
		}
		push( @{$seqs{$feat{'seqid'}}{'feat_list'}}, \%feat );					# push new feature hash onto feature array
		push( @{$tmp_feats_by_type{$feat{'seqid'}}{$feat{'type'}}}, \%feat ); 	# feat array keyed on feat type
#		$seqs{ $feat{'seqid'} }{'feat_hash'}{ $id } = \%feat;					# add feature to feature hash keyed on feat ID
		$id2feat{ $id } = \%feat;
	}

	foreach my $seqid ( keys %seqs ){
		foreach my $type ( keys %{$feat_types_by_seqid{$seqid}} ){				# Sort features (organized by type) by position
			@{ $seqs{$seqid}{'feat_type_list'}{$type} } =
				sort 	{	$a->{'start'}	<=>		$b->{'start'} ||
							$a->{'end'}		<=>		$b->{'end'}
						}
				@{$tmp_feats_by_type{$seqid}{$type}} ;
				if ( !$tmp_feats_by_type{$seqid}{$type} ){
					print "break: $seqid, $type\n";
				}
		}
	}

#	ensure long form of seq names is used throughout

	foreach my $seq ( keys %seqs ){										# make info in %seqs reflect full seq name
		foreach my $tmp_seq ( keys %tmp_seqs ){							# see Note above
			if ( $seq =~ m/$tmp_seq/ ){									# if short form finds match in long form ...
				foreach my $field ( keys %{$tmp_seqs{ $tmp_seq }} ){	# loop through fields
					$seqs{ $seq }{ $field } = $tmp_seqs{ $tmp_seq }{$field};	# and copy to %seqs
				}
			}
		}
	}

#	read FASTA sequences and assign to appropriate elements of %seqs

	my %tmp_seqdata = ();
	$/ = "\n>";															# change input record sep to suit FASTA format
	while( <$fh> ){
		chomp;
		s/^>//g;														# remove any FASTA '>' not removed as RS
		my ( $seqid, $sequence ) = split ( /\n/, $_, 2 );				# split record into ID and sequence
		$seqid =~ s/^\w+\|(\w+\.\d)\.\d+/$1/;							# remove project and extra .\d+ from sequence ID
		$sequence =~ s/\W+//g;											# remove non-sequence characters
		$tmp_seqdata{ $seqid } = uc $sequence;							# use upper case for all sequences
		if ( exists $id2feat{$seqid}{'type'} ){
			if ( $id2feat{ $seqid }{'type'} ne 'contig' ){
				$id2feat{ $seqid }{ 'translation' } = $sequence ;
		#		print "id2feat{ $seqid }{'translation'} = \"$id2feat{ $seqid }{'translation'}\"\n";
			}
		}
#		print "seqid = \"$seqid\"\n";
#		print "sequence = \"$sequence\"\n" if ( length $sequence < 1000 );
	}
	$/ = "\n";															# change input record sep back to normal

#	assign sequence to seq1, seq2, ... themselves

	foreach my $seq ( keys %seqs ){
		print "sequence: \"$seq\"\n" if $opts{'v'};
		$seqs{ $seq }{ 'sequence' } = $tmp_seqdata{ $seq };
		my $sub = substr( $tmp_seqdata{ $seq }, 0, 1000 );
		print "sequence: \"$seq\" = \"$sub\"\n" if $opts{'v'};
	}

#	assembly %gff3 structure

	my %gff3	=	(	'seqhash'	=>	\%seqs,
						'fileinfo'	=>	$gff_info,
					);
	return( \%gff3 );
}
####################################################################################################
#	read_gff3_fromfile()
#	Wrapper for read_gff3_fromfh which opens a named file before calling file reader.
####################################################################################################
sub read_gff3_fromfile {
	my ( $file ) = @_;

	open( FILE, "$file" ) or
		die "Cannot open GFF3 file: \"$file\" for reading.\n";
	my $gff3 = &read_gff3_fromfh( \*FILE );
	close( FILE );
	return ( $gff3 );
}
####################################################################################################
# the logic for the original names of these routines was really screwed up
####################################################################################################
sub read_gff3_file {
	return( &read_gff3_fromfh( @_ ) );
}
####################################################################################################
sub read_gff3 {
	return( &read_gff3_fromfile( @_ ) );
}
####################################################################################################
#	parse_feature_attributes()
#	Parse column 9 of GFF3 file, the attribute field.
#	Example:
#	ID=patric|cds.000002.489485;Parent=gene.000001.489484;Dbxref=protein_id:YP_220785.1;gene_symbol=dnaA;\
#	locus_tag=BruAb1_0001;Name=VBI0009BA1_0001;Ontology_term=GO:0003677,GO:0003688,GO:0005524,GO:0006270,\
#	GO:0006275;description=chromosomal replication initiator protein DnaA;web_id=patric|cds.000002.489485
#
#	ID				patric|cds.000002.489485
#	Parent			gene.000001.489484
#	Dbxref			protein_id:YP_220785.1
#	gene_symbol		dnaA
#	locus_tag		BruAb1_0001;
#	Name			VBI0009BA1_0001
#	Ontology_term	GO:0003677,GO:0003688,GO:0005524,GO:0006270,GO:0006275;
#	description		chromosomal replication initiator protein DnaA
#	web_id			patric|cds.000002.489485
#
#	Just do one level of parsing. Do not subdivide Ontology_term, Dbxref, etc..  It is not
#	required at this point.
####################################################################################################
sub parse_feature_attributes {
	my ( $attrib ) = @_;

	my %attribs = ();													# end result goes in attribute hash
	my @kvps = split( /;/, $attrib );									# split attribute field into key-value pairs
	foreach my $kvp ( @kvps ){											# foreach attribute
		my ( $key, $value ) = split( /=/, $kvp );						# split into attrib ID and its value
		$attribs{ $key } = $value;										# assign to hash
#		print "$key = \"$value\"\n";
	}
	return ( \%attribs );
}
## end read_gff3 related ###########################################################################

####################################################################################################
#	merge_overlapping_intervals()
#
#	Takes a LoL and an array with the field numbers of the coordinates, then merges rows that
#	overlap.
####################################################################################################
sub merge_overlapping_intervals {
	return( "not implemented yet.\n" );
}
####################################################################################################
#	read_table()
#
#	Called as:		my $table = &read_table( \*FILE, $delimit );
#
#	Reads a rectangular matrix into a list of lists, returning a pointer to it.
####################################################################################################
sub read_table {
	my ( $fh, $delimit ) = @_;

	my @table = ();
	while( <$fh> ){
		chomp;
		next if /^#/;
		next if /^\s*$/;
		my @row = split( /$delimit/ );
		push( @table, \@row );
	}
	return( \@table );
}
####################################################################################################
#	read_genome2chr()
#	Reads the relationship between genome name (long form) and chromosome accession number(s).
#	This is particularly useful when dealing with bacteria with multiple chromosomes.  Given a
#	file containing information in the following format:
#
#	# optional header
#	genome_name		chr1_accno[,chr2_accno,etc.]
#
#	In other words, each data line should consist of two whitespace-delimited columns, the first
#	containing the genome name (no spaces allowed), the second containing a comma-delimited list
#	of accession numbers sorted by chromosome number (no spaces allowed).  Blank lines are tolerated
#	as are comments which start with '#' in the first position.
#
#	The function returns a hash keyed on genome_name containing pointer to list of accession
#	numbers:
#
#	%genome2chr =	(	'Brucella_suis_1330'				=>	[ 'NC_004310.3', 'NC_004311.2' ],
#						'Brucella_abortus_bv_1_str_9-941'	=>	[ 'NC_006932.1', 'NC_006933.1' ],
#						...
#					);
####################################################################################################
sub read_genome2chr {
	my ( $file ) = @_;

	open( FILE, "$file" ) or
		die "Cannot open file: \"$file\" for reading in function &read_genome2chr().\n";

	my %genome2chrs = ();
	while( <FILE> ){
		chomp;
		next if /^#/;
		next if /^\s*$/;
		my ( $genome_name, $accnos ) = split( /\s+/ );
		my @accno_list = split( /,/, $accnos );
		$genome2chrs{ $genome_name } = \@accno_list;
	}
	close( FILE );
	return( \%genome2chrs );
}
####################################################################################################
#	read_master_names()
#	Reads the relationship between genome name and chromosome accession number(s).
#	This is essential when dealing with bacteria with multiple chromosomes.  Unlike read_genome2chr(),
#	this function reads the "standardized" format of "master_names" file.
#
#	Given a file containing information in the following format:
#
#	# optional header
#	genome_name_long	genome_name_short		chr1_accno[,chr2_accno,etc.]
#
#	In other words, each data line should consist of three whitespace-delimited columns, the first
#	containing the long form of the genome name (spaces replaced with underscores), the second
#	contains a short form limited to 10 chars, the third containing a comma-delimited list
#	of accession numbers sorted by chromosome number (no spaces allowed).  Blank lines are tolerated
#	as are comments which start with '#' in the first position.
#
#	The function takes one parameter: the name of the master_names file.
#
#	The function returns four hashes in total, two hashes keyed on genome_name (long and short forms)
#	containing pointer to list of accession numbers:
#
#	%genome_long2chr =	(	'Brucella_suis_1330'				=>	[ 'NC_004310.3', 'NC_004311.2' ],
#							'Brucella_abortus_bv_1_str_9-941'	=>	[ 'NC_006932.1', 'NC_006933.1' ],
#							...
#						);
#	%genome_short2chr =	(	'Bsui1330'							=>	[ 'NC_004310.3', 'NC_004311.2' ],
#							'Bab9941'							=>	[ 'NC_006932.1', 'NC_006933.1' ],
#							...
#						);
#
#	...and two hashes which allow one to convert between long and short name styles:
#
#	%gname_short2long =	(	'Bsui1330'							=>	'Brucella_suis_1330',
#							'Bab9941'							=>	'Brucella_abortus_bv_1_str_9-941',
#							...
#						);
#	%gname_long2short = (	'Brucella_suis_1330'				=>	'Bsui1330',
#							'Brucella_abortus_bv_1_str_9-941'	=>	'Bab9941',
#							...
#						);
####################################################################################################
sub read_master_names {
	my ( $file ) = @_;

	open( FILE, "$file" ) or
		die "Cannot open master_names file: \"$file\" for reading.\n";
	my ( %gl2chr, %gs2chr, %gn_s2l, %gn_l2s ) = ( (), (), (), () );
	while( <FILE> ){
		chomp;
		next if /^#/;
		next if /^\s*$/;
		my ( $long, $short, $chrs ) = split( /\s+/ );
		my @chrs = split( /,/, $chrs );
		$gl2chr{ $long  }	=	\@chrs;
		$gs2chr{ $short }	=	\@chrs;
		$gn_s2l{ $short }	=	$long;
		$gn_l2s{ $long  }	=	$short;
		print "$long	$short	" . join( ';', @chrs ) . "\n" if $opts{'d'};
	}
	close( FILE );
	return( \%gl2chr, \%gs2chr, \%gn_s2l, \%gn_l2s );
}
####################################################################################################
#	reverse_complement()
#	Generate the reverse complement of a DNA sequence, preserving the case of the input sequence
#	by default or forcing it to uppercase, if an additional (TRUE) argument is provided.
####################################################################################################
sub reverse_complement {
	my ( $seq, $forceUpperCase ) = @_;

	$seq = reverse( $seq );
	if ( $forceUpperCase ){
   		$seq =~  tr/acgtrymkswhbvdnxACGTRYMKSWHBVDNX\^\$/TGCAYRKMSWDVBHNXTGCAYRKMSWDVBHNX\$\^/;
	} else {
   		$seq =~  tr/acgtrymkswhbvdnxACGTRYMKSWHBVDNX\^\$/tgcayrkmswdvbhnxTGCAYRKMSWDVBHNX\$\^/;
	}
	return( $seq );
}
####################################################################################################
#	efetch()
#	Given a list of identifiers, subroutine queries NCBI and returns the desired information.
#	Parameter		Meaning				Allowed_Values
#		db			database			@db_allowed
#		id			identifier			NCBI sequence number (GI), accession, accession.version, fasta,
#										GeneID, genome ID, seqid
#		retmode		output format		xml, html, text, asn.1
#		rettype		output type			@rettype_allowed, scope is complex, see:
#										http://eutils.ncbi.nlm.nih.gov/entrez/query/static/efetchseq_help.html#rettypeparam
#		strand		DNA strand			( "+", "-" ), NCBI uses 1=plus, 2=minus (it is converted)
#		seq_start	start residue num
#		seq_stop	end residue num
#		complexity	regulates display	see http://eutils.ncbi.nlm.nih.gov/entrez/query/static/efetchseq_help.html#seqparam
#										( 0, 1, 2, 3, 4 )
####################################################################################################
sub efetch {
	my 				( $db, $id, $retmode, $rettype, $my_strand, $seq_start, $seq_stop, $complexity ) = @_;
	my @plabel	= qw(  db   id   retmode   rettype   my_strand   seq_start   seq_stop   complexity );

	my @db_allowed		= qw( gene genome nucleotide nuccore nucest nucgss protein popset snp sequences );
	my @retmode_allowed	= qw( xml html text asn.1 );
	my @rettype_allowed	= qw( native fasta gb gbc gbwithparts est gss gp gpc seqid acc chr flt rsr brief docset );
	my $tool			= "perl_efetch";
	my $email			= 'eesnyder@vbi.vt.edu';
	my $retmax			= 100000;
	my $url				= 'http://eutils.ncbi.nlm.nih.gov/entrez/eutils/efetch.fcgi?';
	my $wget_cmd 		= 'wget -q -O - ';									# web retrieval program cmdline


	$url .= "tool=$tool&email=$email&retmax=$retmax";		# add identifying information to query

	my %cparams	= 	(	'db'		=>	{	'allowed'	=>	\@db_allowed,
											'offset'	=>	0,
										},
						'retmode'	=>	{	'allowed'	=>	\@retmode_allowed,
											'offset'	=>	2,
										},
						'rettype'	=>	{	'allowed'	=>	\@rettype_allowed,
											'offset'	=>	3,
										},
					);
#	validate parameters

	foreach my $param ( keys %cparams ){
		my $match = 0;
		next unless ( $_[ $cparams{$param}{'offset'} ] ne '' );
		foreach my $allowed_value ( @{ $cparams{ $param }{'allowed'} } ){
			if ( $_[ $cparams{$param}{'offset'} ] eq $allowed_value ){
				$match++;
				last;
			}
		}
		unless ( $match ){
			my $msg = "Invalid parameter value for \"$param\"; valid values are:\n\t";
			$msg .= join( ', ', @{ $cparams{$param}{'allowed'} } ) . "\n";
			die $msg;
		}
	}

#	assemble URL

	for( my $i = 0; $i < @_; $i++ ){
		if ( $_[$i] ne '' ){
			$url .= "&$plabel[$i]=$_[$i]";
		}
	}

#	compose and execute command

	my $cmd	= $wget_cmd . "\'$url\'";
	print "cmd = \"$cmd\"\n";
	my $result = `$cmd`;
	return( $result );
}
####################################################################################################
#	read_table_in2_LoL_fromfile()
#
#	Parameters:
#		$file		filename
#		$fsep		field separator
#	Returns:
#		\@LoL
#
#	Subroutine parses a table into an array of arrays (or a List of Lists (LoL)) using the
#	&read_table_in2_LoL_fromfh() subroutine (see below).
####################################################################################################
sub read_table_in2_LoL_fromfile {
	my ( $file, $fsep ) = @_;

	open( FILE, $file ) or 												# open input file...
		die "Cannot open file: \"$file\" for reading.\n";				# or die trying
	my $LoL = &read_table_in2_LoL_fromfh( \*FILE, $fsep );				# read file into LoL
	close( FILE );														# close file
	return( $LoL );														# return LoL
}
####################################################################################################
#	read_table_in2_LoL_fromfh()
#
#	Parameters:
#		$fh			filehandle
#		$fsep		field separator
#	Returns:
#		\@LoL
#
#	Function parses a table into an array of arrays (or a List of Lists (LoL)).  If field separator
#	is not provided, it is assumed to be multiple whitespace ( /\s+ ).  When reading data, comment
#	lines starting with '#' and blank lines are ignored.
####################################################################################################
sub read_table_in2_LoL_fromfh {
	my ( $fh, $fsep ) = @_;

	$fsep = '\s+' unless $fsep;										# field separator assumed to be
																	# whitespace if not provided.
	my @LoL = ();													# init List of Lists
	while ( <$fh> ){												# loop over input file lines
		chomp;														# remove trailing newline char
		next if /^#/;												# ignore comment lines
		next if /^\s*$/;											# ignore blank lines
		my @tmp = split( /$fsep/ );									# split line by fsep into array
		push( @LoL, \@tmp );										# add pointer to array to list
	}
	return( \@LoL );												# return pointer to LoL
}
####################################################################################################
#	get_date()
#	Returns date in format resembling the UNIX "date" command.
####################################################################################################
sub get_date {
	my ( $sec, $min, $hour, $mday, $month, $year, $wday, $yday, $isdst ) = localtime( time );
	my @dow = qw( Sun Mon Tue Wed Thu Fri Sat );
	my @moy = qw( Jan Feb Mar Apr May Jun Jul Aug Sep Oct Nov Dec );
	my @tmt = qw( EST EDT );											# hardcoded Eastern time zone
	my $date = sprintf("%3s %3s %2d %02d:%02d:%02d %3s %4d",
						$dow[$wday], $moy[$month], $mday, $hour, $min, $sec, $tmt[$isdst], 1900+$year );
	return( $date );
}
####################################################################################################
sub write_file_by_name {
	my ( $fname, $ftext, $warning ) = @_;

	my $message = "Cannot open file: \"$fname\" for writing.";
	$message .= " $warning" if $warning;
	$message .= "\n";

	open( FILE, ">$fname" ) or die $message;
	print FILE $ftext;
	close( FILE );
}
####################################################################################################
#	format_text()
#	Given a string and a width in columns, fold the text into lines shorter than the indicated
#	width, respecting the integrity of individual words (i.e., only break lines between words).
####################################################################################################
sub format_text {
	my ( $string, $width ) = @_;

	$width = 80 unless $width;									# if width is omitted, use 80 as default

	my @words = split( /\s+/, $string );						# split text into words on whitespace

	my $output = "";											# grows word-by-word as while progresses
	my $line_length = 0;										# measurement of length between \n chars
	my $word;													# a single non-whitespace token
	while ( $word = shift @words ){								# while words are available, read one at a time
		$word .= " " if $word =~ m/\.$/;						# check for periods & pad with space
		my $word_length = length( $word ) + 1;					# length including a space
		if ( $line_length + $word_length > $width ){			# if adding word will exceed line length
			$output .= "\n$word ";								# add newline + word + " " to output
			$line_length = $word_length;						# reset line length to word length (+" ")
		} else {												# if under the limit
			$output .= "$word ";								# append word + " " to output
			$line_length += $word_length;						# add word length (+" ") to line length
		}
	}
	$output =~ s/ +$//;											# remove trailing space(s).
	return( $output );											# return results
}

####################################################################################################
sub id_subroutine {
	my ( $name, $parameters, $override ) = @_;

	if ( $opts{'d'} or $override ){
		print "$name( \"" . join( '", "', @{$parameters} ) . "\" )\n";
	}
}
###################################################################################################
sub truth {
	my ( $flag ) = @_;

	if ( $flag ){
		return ( "TRUE" );
	} else {
		return ( "FALSE" );
	}
}
####################################################################################################
1;



###################################################################################################
#	writeMultifasta2file( \@listOfSeqhashes, $filename, $width )
#	Given a filename and a list of SeqHashes, write out a multiFASTA file.
###################################################################################################
sub writeMultifasta2file {
	my ( $seqlist, $fname, $width ) = @_;

	open( FILE, ">$fname" ) or
		die "Cannot open \"$fname\" for writing multiFASTA file.\n";
	&writeMultifasta2fp( $seqlist, \*FILE, $width );
	close( FILE );
}
###################################################################################################
#	writeMultifasta2fp( \@listOfSeqhashes, $fp )
#	Given a filehandle and a list of SeqHashes, write out a multiFASTA file.
###################################################################################################
sub writeMultifasta2fp {
	my ( $seqlist, $fp, $width ) = @_;

	foreach my $seq ( @$seqlist ){
		print $fp ">$seq->{'header'}\n";
		print $fp &wrapLongSequence( $seq->{'seq'}, $width ) . "\n";
	}
}
###################################################################################################
#	wrapLongSequence( $seqstring, $seqwidth)
###################################################################################################
sub wrapLongSequence {
	my( $seq, $width ) = @_;

	$seq =~ s/\s+//;										# remove any whitespace
	$seq =~ s/(.{$width})/$1\n/g;							# insert \n every $width residues
	$seq =~ s/\n+$//;										# make sure seq doesn't end with \n
	return( $seq );
}
###################################################################################################
#	my $seqHash = &readFastaFromfp( \*FILE );
#	Given a pointer to a FASTA format file containing one sequence, function returns pointer to
# 	hash containing information about sequence:
#
#	%seqence = (	'seq'		=>	"TTTTCCACACTTATCCACAGGGCGCGGGCGGGACTCGGTTGCCCCTCTGA...",
#					'length'	=>	2122487,
#					'id'		=>	'NC_010742.1',
#					'desc'		=>	'Brucella abortus S19',
#				);
####################################################################################################
sub readFastaFromfp {
	my ( $fp ) = @_;

	my ( $id, $description, $header ) = ( "", "", "" );				# header-related vars
	my ( $seq ) = ( "" );											# sequence-related vars
	while( <$fp> ){
		chomp;
		next if /^#/;
		next if /^\s*$/;
		if ( /^>/ ){
			$header = $_;											# grab FASTA header
			next;
		}
		$seq .= $_;													# append line to growing sequence
	}
	if ( $header =~ /^>(\S+)\s+(.+)$/ ){							# parse FASTA header with id & desc
		$id = $1;													# seqid and ...
		$description = $2;											# description
	} elsif ( $header =~ /^>(\S+)$/ ){								# parse FASTA header with id only
		$id = $1;
	} else {
	}

	my $length = length( $seq );									# measure sequence length

	my %sequence =	(	'seq'		=>	$seq,						# package sequence variables
						'length'	=>	$length,					# into sequence hash
						'id'		=>	$id,
						'desc'		=>	$description,
					);

	return( \%sequence );											# return sequence hash
}
####################################################################################################
sub readFastaFromfile {
	my ( $file ) = @_;

	open( FILE, "$file" ) or die "Cannot open FASTA file: \"$file\" for reading.\n";
	my $seqs = &readFastaFromfp( \*FILE );
	close( FILE );
	return( $seqs );
}
####################################################################################################
#	readMultifastaFromfp()
#	my $seqHash = &read_fasta( \*FILE, $filter, $sflag );
#	Given a pointer to a FASTA format file containing many sequences and a regular expression filter
#	such that:
#
#		$id =~ s/$filter/$1/;
#
#	will yield the desired sequence identifier.  For example:
#
#		FASTA file contains:	"patric|NC_003317.1.329"
#		desired seqid:			"NC_003317.1"
#
#		$filter = "\w+\|(\w+\.\d)\.\d+"
#
#	The sflag, if present, indicates that the sequence should not be returned in the %seqs hash.  In
#	other words, the FASTA file was simply parsed for information about the sequences rather than
#	the sequences themselves.
#
#	The function returns pointer to	hash containing information about sequences:
#
#	%seqs = (	'NC_010742.1'	=>	{	'seq'		=>	"TTTTCCACACTTATCCACAGGGCGCGGGCGGGACTCGGTTGCCCCTCTGA...",
#										'length'	=>	2122487,
#										'id'		=>	'NC_010742.1',
#										'desc'		=>	'Brucella abortus S19',
#										'header'	=>	'NC_010742.1	Brucella abortus S19',
#									},
#				'NC_010740.1'	=>	{	...
#									},
#				...
#			);
####################################################################################################
sub readMultifastaFromfp {
	my ( $fp, $filter, $sflag ) = @_;

	my %seqs = ();														# hash for storing sequence records
	$/ = "\n>";															# input record separator
	while( <$fp> ){
		chomp;
		s/^>//;															# remove initial '>' (from first record)
		my ( $header, $seq ) = ( "HEADER", "SEQUENCE" );
		( $header, $seq ) = split( "\n", $_, 2 );
		print "header = \"$header\"; seq = \"$seq\"\n";
		my ( $id, $description ) = ( "", "" );
		if ( $header =~ /^(\S+)\s+(.+)$/ ){								# parse FASTA header with id & desc
			$id = $1;													# seqid and ...
			$description = $2;											# description
		} elsif ( $header =~ /^(\S+)\s*$/ ){							# parse FASTA header with id only
			$id = $1;
		} else {
			die "Cannot parse FASTA header line:\nheader = \"$header\"\n";
		}
		if ( $filter ){
			$id =~ s/$filter/$1/;										# regex filter for seqid
		}
		unless ( $seq ) {
			warn "For header: \"$header\", no corresponding sequence found.\n";
			warn "Skipping.\n";
			next;
		}
		$seq =~ s/\s+//g;												# remove whitespace from sequence
		my $length = length( $seq );									# measure sequence length
		my %sequence =	(	'length'	=>	$length,					# package sequence variables into sequence hash
							'id'		=>	$id,						# from '>' to first whitespace; seqid
							'desc'		=>	$description,				# everything after first whitespace
							'header'	=>	$header,					# the unprocessed header
						);
		$sequence{'seq'} = $seq;										# add actual sequence if sflag not set
		$seqs{ $id } = \%sequence unless $sflag;
		print "seqs{$id}{'length'} = \"$seqs{$id}{'length'}\"\n" if $opts{'D'};
	}
	$/ = "\n";
	return( \%seqs );													# return sequence hash
}
####################################################################################################
#	readMultifastaFromfile()
#	Wrapper for read_multifasta_fromfp() which takes a FASTA file name plus additional arguments and
#	opens the file, supplying an informative message on failure.  The resulting filehandle is used
#	to call read_multifasta_fromfp() with the filter as an argument.
####################################################################################################
sub readMultifastaFromfile {
	my $file = shift @_;

	open( FILE, "$file" ) or die "Cannot open multifasta file: \"$file\" for reading.\n";
	my $seqs = &readMultifastaFromfp( \*FILE, @_ );
	close( FILE );
	return( $seqs );
}
####################################################################################################
#	readMultifastaFromfp2list()
#	my $seqAray = &read_fasta( \*FILE );
#	Given a pointer to a FASTA format file containing many sequences, data is returned in an
#	array of seqHashes.
#
#	The function returns pointer to	hash containing information about sequences:
#
#	%seqs = (	{	'seq'		=>	"TTTTCCACACTTATCCACAGGGCGCGGGCGGGACTCGGTTGCCCCTCTGA...",
#					'length'	=>	2122487,
#					'id'		=>	'NC_010742.1',
#					'desc'		=>	'Brucella abortus S19',
#					'header'	=>	'NC_010742.1	Brucella abortus S19',
#									},
#				{...
#									},
#				...
#			);
####################################################################################################
sub readMultifastaFromfp2list{
	my ( $fp ) = @_;

	my @seqs = ();														# array for storing sequence records
	$/ = "\n>";															# input record separator
	while( <$fp> ){
		chomp;
		s/^>//;															# remove initial '>' (from first record)
		my ( $header, $seq ) = ( "HEADER", "SEQUENCE" );
		( $header, $seq ) = split( "\n", $_, 2 );
#		print "header = \"$header\"; seq = \"$seq\"\n";
		my ( $id, $description ) = ( "", "" );
		if ( $header =~ /^(\S+)\s+(.+)$/ ){								# parse FASTA header with id & desc
			$id = $1;													# seqid and ...
			$description = $2;											# description
		} elsif ( $header =~ /^(\S+)\s*$/ ){							# parse FASTA header with id only
			$id = $1;
		} else {
			die "Cannot parse FASTA header line:\nheader = \"$header\"\n";
		}
		unless ( $seq ) {
			warn "For header: \"$header\", no corresponding sequence found.\n";
			warn "Skipping.\n";
			next;
		}
		$seq =~ s/\s+//g;												# remove whitespace from sequence
		my $length = length( $seq );									# measure sequence length
		my %sequence =	(	'seq'		=>	$seq,						# actual sequence string
							'length'	=>	$length,					# package sequence variables into sequence hash
							'id'		=>	$id,						# from '>' to first whitespace; seqid
							'desc'		=>	$description,				# everything after first whitespace
							'header'	=>	$header,					# the unprocessed header
						);
		push( @seqs, \%sequence );
	}
	$/ = "\n";
	return( \@seqs );													# return sequence hash
}
####################################################################################################
#	exec_pgm_ifRequired( $pgm, \@outfiles, $args, $force )
#	This subroutine executes the program $pgm with arguments $args when one or more \@outfiles
#	(is/are) not present.  If all outfiles are present, execution is deemed unnecessary,
#	unless $force is TRUE.
####################################################################################################
sub	exec_pgm_ifRequired{
	my ( $pgm, $outfiles, $args, $force ) = @_;

	my $file_count = 0;
	foreach my $file ( @$outfiles ){
		$file_count++	if	(	-e $file	&&							# file exists
								-s $file	&&							# non-zero size
								-f $file								# is a plain file
							);
	}
	return( 0 ) 		if	(	( $file_count == @$outfiles )	&&		# if all outfiles are there
								( ! $force )							# and not forcing rerun
							);											# do nothing & return happy
	system( "$pgm $args" );												# otherwise run the program
	&systemCallErrorCheck( $pgm );										# and check the return stats
}
####################################################################################################
sub systemCallErrorCheck {
	my ( $pgm, $force ) = @_;
	if ($? == -1) {
		print "$pgm failed to execute: $!\n";
		die unless $force;
	} elsif ($? & 127) {
		printf(	"%s died with signal %d, %s coredump\n",
				$pgm, ($? & 127), ( ($? & 128)?'with':'without' ) );
		die unless $force;
	} else {
		printf( "%s exited with value %d\n", $pgm, $? >> 8 ) if $opts{'d'};
	}
}
####################################################################################################
#	read_2col_data2hash_fromfile( $file_name )
#	Takes fully-qualified filename for file containing two columns of data, such as a parameter
#	name followed by its value, and converts it to a hash of the right-column values keyed on the
#	left column.  This is intended to be a method for reading in simple parameter files.  The '#'
#	character is treated as a comment-delimiter.
#
#	# sample parameter file
#	para1	value1
#	para2	value2.1 value2.2
#
#	%hash = (	'para1'	=>	"value1",
#				'para2'	=>	"value2.1 value2.2",
#			);
####################################################################################################
sub read_2col_data2hash_fromfile {
	my ( $fname ) = @_;

	open( FILE, "$fname" ) or
		die "Cannot open file \"$fname\" for reading in read_2col_data2hash_fromfile().\n";
	my $line = 0;
	my %hash = ();
	while( <FILE> ){
		chomp;									# remove trailing newline char
		$line++;								# track input file line number
		next if /^#/;
		s/#.*$//;								# remove comments
		next if /^\s*$/;						# ignore blank lines
		my @tmp = split( /\s+/, $_, 2 );		# split line by whitespace into key & value
		if ( @tmp != 2 ){						# check for correct field count
			die "Insufficient number of fields in line $line of \"$fname\"; expecting 2.\n"
				. "Line = \"$_\"";
		}
		$hash{ $tmp[0] } = $tmp[1];
#		$line++;
	}
	return( \%hash );
}
####################################################################################################
#	read_table_in2_LoH_fromfile()
####################################################################################################
sub read_table_in2_LoH_fromfile {
	my( $fname, $fsep ) = @_;

	open( FILE, "$fname" ) or
		die "Cannot open file \"$fname\" for reading in read_table_in2_LoH_fromfile().\n";

	my $list = &read_table_in2_LoH_fromfh( \*FILE, $fsep, $fname );

	return( $list );
}
####################################################################################################
#	read_table_in2_LoH_fromfh()
####################################################################################################
sub read_table_in2_LoH_fromfh {
	my ( $fh, $fsep, $filename ) = @_;

	my @list = <$fh>;
	my $ptr = &read_table_in2_LoH_fromtext( \@list, $fsep, $filename );
	return( $ptr );
}
####################################################################################################
#	read_table_in2_LoH_fromtext()
####################################################################################################
sub read_table_in2_LoH_fromtext {
	my ( $linelist, $fsep, $filename ) = @_;

	my $warning;
	if( $filename ){
		$warning = "$filename: number of data columns > number of headings at row:";
	} else {
		$warning = "Number of data columns > number of headings at row:";
	}

	unless ( $fsep ) {
		$fsep = '\t';
		warn 	"No field separator specified for read_table_in2_LoH_fromtext().\n".
				"Using single <tab> character by default.\n" if $opts{'d'};
	}
	my $vline = 0;											# valid line counter
	my $tline = 0;											# total line counter
	my @list = ();
	my @headings = ();
	foreach ( @$linelist ){
		chomp;
		$tline++;											# increment total line counter
		next if /^#/;										# skip comment lines
		s/#.*$//;											# remove trailing comments
		next if /^\s*$/;									# remove blank lines
		my @tmp = split( /$fsep/, $_ );
		if ( $vline ){										# if reading after heading line
			if ( @tmp > @headings ){
				my $stmp = @tmp;
				my $sheadings = @headings;
				if ( $opts{'d'} ){
					warn( "$warning $tline.\n" );
					warn( "($stmp > $sheadings)\n" );
				}
			}
			my %hash = ();
			for( my $i = 0; $i < @headings; $i++ ){
				$hash{ $headings[$i] } = $tmp[$i];
			}
			push( @list, \%hash );
		} else {
			@headings = @tmp;								# define headings from first line
		}
		$vline++;											# increment valid line counter
	}
	warn "read_table_in2_LoH_fromtext(): No data in your table.\n" unless $vline;
	return( \@list );
}
####################################################################################################
#	read_table_in2_LoH_fromRRfile()
####################################################################################################
sub read_table_in2_LoH_fromRRfile {
	my( $fname, $fsep ) = @_;

	open( FILE, "$fname" ) or
		die "Cannot open file \"$fname\" for reading in read_table_in2_LoH_fromfile().\n";

	my $list = &read_table_in2_LoH_fromRRfh( \*FILE, $fsep, $fname );

	return( $list );
}
####################################################################################################
#	read_table_in2_LoH_fromRRfh()
####################################################################################################
sub read_table_in2_LoH_fromRRfh {
	my ( $fh, $fsep, $filename ) = @_;

	my @list = <$fh>;
	my $ptr = &read_table_in2_LoH_fromRRtext( \@list, $fsep, $filename );
	return( $ptr );
}
####################################################################################################
#	read_table_in2_LoH_fromRRtext()
#	Thu Jun 23 11:39:58 EDT 2011:  Add functionality to permit the last column to contain records
#	with multiple values, so called ragged-right tables.
####################################################################################################
sub read_table_in2_LoH_fromRRtext {
	my ( $linelist, $fsep, $filename ) = @_;

	my $warning;
	if( $filename ){
		$warning = "$filename: number of data columns > number of headings at row:";
	} else {
		$warning = "Number of data columns > number of headings at row:";
	}

	unless ( $fsep ) {
		$fsep = '\t';
		warn 	"No field separator specified for read_table_in2_LoH_fromtext().\n".
				"Using single <tab> character by default.\n" if $opts{'d'};
	}
	my $vline = 0;											# valid line counter
	my $tline = 0;											# total line counter
	my @list = ();
	my @headings = ();
	foreach ( @$linelist ){
		chomp;
		$tline++;											# increment total line counter
		next if /^#/;										# skip comment lines
		s/#.*$//;											# remove trailing comments
		next if /^\s*$/;									# remove blank lines
		my @tmp = split( /$fsep/, $_ );
		if ( $vline ){										# if reading after heading line
			if ( @tmp > @headings ){
				my $stmp = @tmp;
				my $sheadings = @headings;
				warn( "$warning $tline.\n" ) if $opts{'d'};
				warn( "($stmp > $sheadings)\n" ) if $opts{'d'};
			}
			my %hash = ();
			my $i = 0;
			for( $i = 0; $i < @headings - 1; $i++ ){
				$hash{ $headings[$i] } = $tmp[$i];
			}
			$hash{ $headings[$i] } = [ splice( @tmp, $i ) ];
			push( @list, \%hash );
		} else {
			@headings = @tmp;								# define headings from first line
		}
		$vline++;											# increment valid line counter
	}
	warn "read_table_in2_LoH_fromRRtext(): No data in your table.\n" unless $vline;
	return( \@list );
}
####################################################################################################
#	read_table_in2_tiedHoH_fromfile()
####################################################################################################
sub read_table_in2_tiedHoH_fromfile {
	my( $fname, $fsep ) = @_;

	open( FILE, "$fname" ) or
		die "Cannot open file \"$fname\" for reading in read_table_in2_tiedHoH_fromfile().\n";
	my $list = &read_table_in2_tiedHoH_fromfh( \*FILE, $fsep );
	return( $list );
}
####################################################################################################
#	read_table_in2_tiedHoH_fromfh()
####################################################################################################
sub read_table_in2_tiedHoH_fromfh {
	my ( $fh, $fsep ) = @_;

	my @list = <$fh>;
	my $ptr = &read_table_in2_tiedHoH_fromtext( \@list, $fsep );
	return( $ptr );
}
####################################################################################################
#	read_table_in2_tiedHoH_fromtext()
####################################################################################################
sub read_table_in2_tiedHoH_fromtext {
	my ( $linelist, $fsep ) = @_;

	unless ( $fsep ) {
		$fsep = '\t';
		warn 	"No field separator specified for read_table_in2_tiedHoH_fromtext().\n".
				"Using single <tab> character by default.\n" if $opts{'d'};
	}
	my $vline = 0;											# valid line counter
	my $tline = 0;											# total line counter
	my $rhash = Tie::IxHash->new;
	my @headings = ();
	foreach ( @$linelist ){
		chomp;
		$tline++;											# increment total line counter
		next if /^#/;										# skip comment lines
		s/#.*$//;											# remove trailing comments
		next if /^\s*$/;									# remove blank lines
		my @tmp = split( /$fsep/, $_ );
		if ( $vline ){										# if reading after heading line
			print "$vline $tline\n";
			if ( @tmp > @headings ){
				warn( "Number of data columns > number of headings at row: $tline.\n" );
			}
			my %chash = ();
			for( my $i = 0; $i < @headings; $i++ ){
				$chash{ $headings[$i] } = $tmp[$i];
			}
			$rhash->Push( $headings[0] => 1 );
# \%chash );
		} else {
			@headings = @tmp;								# define headings from first line
		}
		$vline++;											# increment valid line counter
	}
	warn "read_table_in2_tiedHoH_fromtext(): No data in your table.\n" unless $vline;
	return( $rhash );
}
####################################################################################################
#	\%ktt = init_ktuple_table( $k, $alphabet )
#	Given k (k-tuple length) and an alphabet (typically, ACGT), generate an initialized hash
#	containing all possible k-tuples and a pointer to the corresponding list.
####################################################################################################
sub init_ktuple_table {
	my ( $k,													# k = ktuple length, i.e. "k" itself
		$alphabet )												# alphabet of possible characters
		= @_;

	print "init_ktuple_table( \"$k\",  \"$alphabet\" )\n" if $opts{'d'};

	my @nbase = sort split( //, $alphabet );					# alphabet in sorted array form
	my $n = @nbase;												# length of character vocabulary
	my $n_ktuples = $n**$k;										# number of distinct k-tuples, given $n and $k

	my @words = ( "" ) x $n_ktuples;							# init word list as long as unique ktuple list
	my @let = ( "" ) x $n_ktuples;								# init letter list with same properties
	print "n = $n; k = $k; $n**$k = ".$n_ktuples."\n" if $opts{'d'};

	for ( my $i = 0; $i < $n_ktuples; $i++ ){					# loop up to the number of possible ktuples
		$let[$i] = $words[$i] = $nbase[$i%$n];					# make list of characters ( A C G T A C G T A C G T ... G T )
	}
	for ( my $t = 1; $t < $k; $t++ ){							# loop from 0 to ktuple length
		for ( my $i = 0; $i < $n_ktuples; $i++ ){				# loop over number of possible ktuples
			$words[$i] .= $let[$i/($n**$t)];					# append letter to end of growing word in blocks
		}														# of $n**$k at a time
	}
	print join( "\n", @words ) if $opts{'d'};
	print "count = " . @words . "\n" if $opts{'d'};

	@words = sort @words;
	my %ktt;
	foreach my $w ( @words ) {									# convert ktuple list to empty hash w/ktuple as key
		$ktt{ $w } = 0;
	}
	return ( \%ktt );											# return the hash of ktuples
}
####################################################################################################
1;
