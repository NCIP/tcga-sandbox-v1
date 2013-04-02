
package TCGA::Level4::Config;
use base 'Exporter';
our @EXPORT = qw(%queries @DTYPE @DISEASE %headers);

# key is the "datatype" name as given in the <datatype>_value tables
# in the Level 3 portion of the disease schemas
#
# these need to be directed to particular disease schemas with
# $stmt = sprintf($queries{$datatype},$disease_abbrev);
our %headers = (
methylation => [qw(barcode uuid participant_code sample_type_code center_id
		   platform_id platform_name methylation_value_id
		   entrez_gene symbol chromosome chr_position
		   beta_value)],
cna  => [qw(barcode uuid participant_code sample_type_code center_id
	    platform_id platform_name cna_value_id
	    chromosome chr_start chr_stop 
	    num_mark seg_mean)],
expgene  => [qw(barcode uuid participant_code sample_type_code center_id
	    platform_id platform_name entrez_gene_symbol
	    expression_value)]
		);

  our %queries = (
methylation => <<METH
select s.built_barcode, s.uuid, s.participant_code,
       s.sample_type_code, t.center_id, t.platform_id,
       t.platform_name, t.methylation_value_id,
       t.entrez_gene_symbol,
       t.chromosome, t.chr_position,
       t.beta_value
from  (
       select b.built_barcode, b.uuid, b.participant_code,
              sbe.element_value AS sample_type_code, hybr.hybridization_ref_id
       from
         tcga%s.shipped_biospecimen b,
         tcga%s.shipped_biospecimen_element sbe,
         tcga%s.hybridization_ref hybr
       where
         b.uuid = hybr.uuid and
         b.is_viewable = 1 and  
         b.shipped_biospecimen_id = sbe.shipped_biospecimen_id and
         sbe.element_type_id = 1         
      ) s,
      (
       select plat.platform_name, dset.center_id, val.hybridization_ref_id,
              val.beta_value, val.chromosome, val.chr_position,
	      val.entrez_gene_symbol, val.methylation_value_id,
	      plat.platform_id
       from
         tcga%s.methylation_value val,
         tcga%s.data_set dset,
         tcga%s.platform plat,
         tcga%s.archive_info
       where
         val.data_set_id = dset.data_set_id and
         dset.load_complete = 1 and
         dset.platform_id = plat.platform_id and
         dset.arcive_id = a.archive_id and
         a.is_latest = 1
      ) t
where
       s.hybridization_ref_id = t.hybridization_ref_id
METH
,
cna => <<CNA
select s.built_barcode, s.uuid, s.participant_code,
       s.sample_type_code, t.center_id, t.platform_id,
       t.platform_name, t.cna_value_id,
       t.chromosome, t.chr_start, t.chr_stop,
       t.num_mark, t.seg_mean
from  (
       select b.built_barcode, b.uuid, b.participant_code,
              sbe.element_value AS sample_type_code, hybr.hybridization_ref_id
       from
         tcga%s.shipped_biospecimen b,
         tcga%s.shipped_biospecimen_element sbe,
         tcga%s.hybridization_ref hybr
       where
         b.uuid = hybr.uuid and
         b.is_viewable = 1 and  
         b.shipped_biospecimen_id = sbe.shipped_biospecimen_id and
         sbe.element_type_id = 1         
      ) s,
      (
       select plat.platform_name, dset.center_id, val.hybridization_ref_id,
              val.num_mark, val.seg_mean, val.chr_start, val.chr_stop,
	      val.chromosome, val.cna_value_id,
	      plat.platform_id
       from
         tcga%s.cna_value val,
         tcga%s.data_set dset,
         tcga%s.platform plat,
         tcga%s.archive_info a
       where
         val.data_set_id = dset.data_set_id and
         dset.load_complete = 1 and
         dset.platform_id = plat.platform_id and
         dset.archive_id = a.archive_id and
         a.is_latest = 1
      ) t
where
       s.hybridization_ref_id = t.hybridization_ref_id
CNA
,
expgene => <<EXPR
select s.built_barcode, s.uuid, s.participant_code,
       s.sample_type_code, t.center_id, t.platform_id,
       t.platform_name, t.entrez_gene_symbol,
       t.expression_value
from  (
       select b.built_barcode, b.uuid, b.participant_code,
              sbe.element_value AS sample_type_code, hybr.hybridization_ref_id
       from
         tcga%s.shipped_biospecimen b,
         tcga%s.shipped_biospecimen_element sbe,
         tcga%s.hybridization_ref hybr
       where
         b.uuid = hybr.uuid and
         b.is_viewable = 1 and  
         b.shipped_biospecimen_id = sbe.shipped_biospecimen_id and
         sbe.element_type_id = 1      
      ) s,
      (
       select plat.platform_name, dset.center_id, val.hybridization_ref_id,
              val.expression_value, val.entrez_gene_symbol,
              val.expgene_value_id,
              plat.platform_id
       from
         tcga%s.expgene_value val,
         tcga%s.data_set dset,
         tcga%s.platform plat,
         tcga%s.archive_info a
       where
         val.data_set_id = dset.data_set_id and
         dset.load_complete = 1 and
         dset.platform_id = plat.platform_id and
         dset.archive_id = a.archive_id and
         a.is_latest = 1
      ) t
where
       s.hybridization_ref_id = t.hybridization_ref_id
EXPR
);

@DTYPE = qw(methylation cna expgene);
@DISEASE = qw(
BLCA
BRCA
CESC
COAD
DLBC
ESCA
GBM
HNSC
KICH
KIRC
KIRP
LAML
LCLL
LGG
LIHC
LUAD
LUSC
OV
PAAD
PRAD
READ
SARC
SKCM
STAD
THCA
UCEC
	      );

1;
