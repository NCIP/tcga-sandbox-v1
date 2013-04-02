package TCGA::BCR::DashboardConfig;
use base "Exporter";
use DateTime::Format::Strptime;
use JSON;

our @EXPORT = qw(%json_table_info @OPTIONAL);
our @EXPORT_OK = qw(%dtfs $laml_kludge);

our %dtfs = (
	     iso => DateTime::Format::Strptime->new( pattern => '%F' ),
	     json => DateTime::Format::Strptime->new( pattern => '%m/%d/%Y' ),
	     alt_other => DateTime::Format::Strptime->new( pattern => '%m-%d-%y'),
	     other => DateTime::Format::Strptime->new( pattern => '%y-%m-%d' )
);


our @OPTIONAL = qw( pass_pending_geno );

our %json_table_info = 
(
 'case_summary_by_disease' => 
 {
  worksheet => 'Case Summary by Cancer',
  title => 'Table 2.1',
  first_hdr => 'Tumor Type',
  hdr_table => \%dis_hdr_to_json
 },
 'case_summary_by_tss' => 
 {
  worksheet => 'Case Summary by TSS',
  title => 'Table 3.1',
  first_hdr => '^TSS',
  hdr_table => \%tss_hdr_to_json
 },
 'case_by_shipment' => 
 {
  worksheet => 'Case .*by Shipment',
  title => 'Table 4.1',
  first_hdr => '(Date Received at BCR)|(Receive Date)',
  hdr_table => \%ship_hdr_to_json
  }
);

# keys of the hdr to json tables are regexps
our %dis_hdr_to_json =
(
'Tumor Type' => 'tumor_abbrev',
'Total Cases Rec..ved' => 'total_cases_rcvd',
'Pending BCR Initial Screening' => 'pending_init_screen',
'DQ BCR Initial Screening' => 'dq_init_screen',
'Submitted to BCR' => 'submitted_to_bcr',
'Pending Path' => 'pending_path_qc',
'^DQ Path' => 'dq_path',
'^Q Path' => 'qual_path',
'Pending Molecular' => 'pending_mol_qc',
'^DQ Molecular' => 'dq_mol',
'^Pass Pending Geno' => 'pass_pending_geno',
'^Q Molecular' => 'qual_mol',
'DQ Other' => 'dq_other',
'(Disqualify|DQ) Genotype' => 'dq_genotype',
'Qu.?lified ?(-|at BCR) ?Awaiting Shipment' => 'pending_shipment',
'Qualified (-|\/) Hold' => 'qualified_hold',
'Shipped' => 'shipped',
'BCR Q Rate' => 'qual_pass_rate',
'Notification ?[\\\/] ?Redact..n' => 'annotated');

%ship_hdr_to_json =
(
 'Date Received at BCR|Receive Date' => 'rec_date',
# 'TSS' => 'tss_id',
  'TSS Code' => 'tss_id',
# 'Tumor Type' => '',
 'Total Cases Rec..ved' => 'total_cases_rcvd',
 'Pending BCR Initial Screening' => 'pending_init_screen',
 'DQ BCR Initial Screening' => 'dq_init_screen',
 'Submitted to BCR' => 'submitted_to_bcr',
 'Pending Path' => 'pending_path_qc',
 '^DQ Path' => 'dq_path',
 '^Q Path' => 'qual_path',
 'Pending Molecular' => 'pending_mol_qc',
 '^DQ Molecular' => 'dq_mol',
 '^Q Molecular' => 'qual_mol',
 'DQ Other' => 'dq_other',
 '(Disqualify|DQ) Genotyp' => 'dq_genotype',
 'Qu.?lified ?(-|at BCR) ?Awaiting Shipment' => 'pending_shipment',
 'Qualified [-\/] Hold' => 'qualified_hold',
 'Shipped' => 'shipped',
 'BCR Q Rate' => 'qual_pass_rate',
 'Notification ?[\\\/] ?Redact..n' => 'annotated',
# 'Comments' => '',
);

%tss_hdr_to_json = 
(
# 'TSS' => 'tss_id',
 'TSS (ID|Code)' => 'tss_id',
# 'Tumor Type' => '',
 'Total Cases Rec..ved' => 'total_cases_rcvd',
 'Pending BCR Initial Screening' => 'pending_init_screen',
 'DQ BCR Initial Screening' => 'dq_init_screen',
 'Submitted to BCR' => 'submitted_to_bcr',
 'Pending Path' => 'pending_path_qc',
 '^DQ Path' => 'dq_path',
 '^Q Path' => 'qual_path',
 'Pending Molecular' => 'pending_mol_qc',
 '^DQ Molecular' => 'dq_mol',
 '^Q Molecular' => 'qual_mol',
 'DQ Other' => 'dq_other',
 '(Disqualify|DQ) Genotyp' => 'dq_genotype',
 'Qu.?lified ?(-|at BCR) ?Awaiting Shipment' => 'pending_shipment',
 'Qu.?lified [\/-] Hold' => 'qualified_hold',
 'Shipped' => 'shipped',
 'BCR Q Rate' => 'qual_pass_rate',
 'Notificati?on ?[\\\/] ?Redact..n' => 'annotated',
);

my $laml_kludge_json =<<LAML;
{"case_summary_by_disease" : [{
   "dq_mol" : 0,
   "qual_path" : 202,
   "total_cases_rcvd" : 202,
   "tumor_abbrev" : "LAML",
   "dq_other" : 0,
   "submitted_to_bcr" : 202,
   "dq_path" : 0,
   "shipped" : 202,
   "pending_init_screen" : 0,
   "pending_mol_qc" : 0,
   "dq_genotype" : 0,
   "pending_shipment" : 0,
   "dq_init_screen" : 0,
   "pending_path_qc" : 0,
   "annotated" : 0,
   "qual_mol" : 202
}]}
LAML
our $laml_kludge = decode_json $laml_kludge_json;
1;
