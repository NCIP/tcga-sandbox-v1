-- # tables in sqlite3 schema
-- # chroms : chrom_id chrom first_bin last_bin
-- # bins : bin_id chrom binstart binstop binmid
-- # genes_to_bins : genes_to_bins_id gene_id bin_id
-- # genes : gene_id gene gene_order start stop 
-- # exons : exon_id gene_id exon_order start stop

create table chroms (
       chrom_id integer primary key,
       chrom unique,
       first_bin integer,
       last_bin integer
);
create table bins (
       bin_id integer primary key,
--       chrom_id integer references chroms (chrom_id),
       chrom references chroms (chrom),
       bin integer unique,
       binstart integer,
       binstop integer,
       binmid integer
);
create table genes_to_bins (
       genes_to_bins_id integer primary key,
       gene_id integer references genes (gene_id),
       bin integer references bins (bin)
);
create table genes (
       gene_id integer primary key,
       gene,
       spliceVar integer,
       gene_order integer,
--       chrom_id integer references chroms (chrom_id),
       chrom references chroms (chrom),
       start integer,
       stop integer,
       length integer,
       constraint gene_splice_var_uniq unique (gene, spliceVar) on conflict replace
);
create index gene_index on genes (gene asc);
create index spliceVar_index on genes (spliceVar asc);
create table exons (
       exon_id integer primary key,
       gene_id integer references genes (gene_id),
       exon_order integer,
       start integer,
       stop integer,
       length integer
       );
create table ccnv (
       ccnv_id integer primary key,
       gene_id integer references genes (gene_id),
       chrom references chroms (chrom),
       start integer
)
-- ccnv_u contains the unique gene_ids that are represented in 
-- the dgv: 
-- create table ccnv_u as select distinct(gene_id) gene_id from ccnv;;
CREATE TABLE ccnv_u(gene_id INT);
CREATE INDEX ccnv_u_gene_id_idx on ccnv_u (gene_id);

