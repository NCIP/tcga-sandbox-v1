#$Id: Configure.pm 13448 2011-09-29 19:36:19Z snyderee $
package JSONVal::Configure;

use strict;
use warnings;

use base 'Exporter';
our @EXPORT = qw(	@admissible_disease_abbrevs	 %type_map	 @null_ok	 $expected_factors
					$date_format_matcher		 $warn_pfx	 $die_pfx	 $json_die_pfx);

our @null_ok;
our $VERSION		= "1.3";
our $warn_pfx		= "Warning (DCC)";
our $die_pfx		= "FAIL (DCC)";
our $json_die_pfx	= "FAIL (JSON)";
our @admissible_disease_abbrevs =
	qw[	BLNP	KIRP	OV
		BLP		LAML	PRAD
		BRCA	LCLL	READ
		CESC	LGG		SALD
		COAD	LIHC	SKCM
		GBM		LNNH	STAD
		HNSC	LUAD	THCA
		KIRC	LUSC	UCEC	];

our %type_map = (
		rec_date => 'date',
		tss_id => 'string',
		total_cases_rcvd => 'int',
		pending_init_screen => 'int',
		dq_init_screen => 'int',
		submitted_to_bcr => 'int',
		pending_path_qc => 'int',
		dq_path => 'int',
		qual_path => 'int',
		pending_mol_qc => 'int',
		dq_mol => 'int',
		qual_mol => 'int',
		dq_other => 'int',
		dq_genotype => 'int',
		pending_shipment => 'int',
		shipped => 'int',
		annotated => 'int',
		period => 'string',
		tumor_abbrev => [@admissible_disease_abbrevs],
		tissue_type => ['normal','tumor'],
		batch => ['multiple','_int_'], 		  # number or 'multiple' allowed
		num_cases => 'int',
		ship_date_cgcc => ['pending', '_date_'], # date or 'pending' allowed
		ship_date_gsc => ['pending', '_date_'],  # date or 'pending' allowed
		qualified_hold => 'int',				  # added 5/19/2011, EES, DCCT-534
		comment => 'string',
		qual_pass_rate => 'float',				  # added 9/28/2011, EES, DCCT-834
);

@type_map{@admissible_disease_abbrevs} = ('int') x @admissible_disease_abbrevs;

# tags for which a null value is acceptable
@null_ok = (qw( comment ship_date_cgcc ship_date_gsc batch ), @admissible_disease_abbrevs);

our $expected_factors =
  { "version" => [],
    "timestamp" => [],
    "case_by_shipment" => [qw[ rec_date tss_id total_cases_rcvd pending_init_screen dq_init_screen submitted_to_bcr pending_path_qc
                               dq_path qual_path pending_mol_qc dq_mol qual_mol dq_other dq_genotype
                               pending_shipment qualified_hold shipped annotated qual_pass_rate ]],
    "case_summary_by_tss" => [qw[       tss_id total_cases_rcvd pending_init_screen dq_init_screen submitted_to_bcr pending_path_qc
                               dq_path qual_path pending_mol_qc dq_mol qual_mol dq_other dq_genotype
                               pending_shipment qualified_hold shipped annotated qual_pass_rate ]],
    "case_summary_by_disease" => [qw[          tumor_abbrev total_cases_rcvd pending_init_screen dq_init_screen submitted_to_bcr pending_path_qc
                               dq_path qual_path pending_mol_qc dq_mol qual_mol dq_other dq_genotype
                               pending_shipment qualified_hold shipped annotated qual_pass_rate ]],
    "incoming_cases" => ["period", @admissible_disease_abbrevs],
    "shipment_schedule" => [qw[ tumor_abbrev tissue_type batch num_cases ship_date_cgcc ship_date_gsc comment ]] };

our $date_format_matcher = qr/^(?:(?:0*[1-9])|(?:1[0-2]))\/(?:(?:0*[1-9])|(?:[1-2][0-9])|(?:3[0-1]))\/20[0-9]{2}$/;

1;
