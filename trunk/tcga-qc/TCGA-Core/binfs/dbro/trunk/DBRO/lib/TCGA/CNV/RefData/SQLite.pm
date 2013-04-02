package TCGA::CNV::RefData::SQLite;
use TCGA::CNV::Config;
use TCGA::CNV::Segment;
use base 'TCGA::CNV::RefData';
use File::Spec;
use DBI;
use Carp qw(croak);
use strict;
use warnings;

# $TABLES hash may not work with the SQLite implementation
# specify foreign key constraint?

# tables in sqlite3 schema
# chroms : chrom_id chrom first_bin last_bin
# bins : bin_id chrom binstart binstop binmid
# genes_to_bins : genes_to_bins_id gene_id bin_id
# genes : gene_id gene gene_order chrom_id start stop 
# exons : exon_id gene_id exon_order start stop


my $TABLES = {}; # link up to the superclass?  my $NUM_GENES = 0;

sub new {
  my $class = shift;
  my ($db_f, $bins_f, $bins_id, $genes_f, $genes_id, $cnvs_f) = @_;
  $TCGA::CNV::RefData::TABLES = $TABLES;
  my $obj = $class->SUPER::new(@_);
  my $dbh = $obj->{_dbh} = DBI->connect("dbi:SQLite:dbname="._catfile_ref($db_f));
  $dbh->{RaiseError} = 1;
  my $sth = $dbh->prepare("select binstart, binstop from bins limit 1");
  $sth->execute;
  my $a = $sth->fetch;
  $obj->{_binsize} = $a->[1] - $a->[0] + 1;

  $obj->{_sth_bin2gene} = $dbh->prepare("select gene_id from genes_to_bins where bin = ?");
  $obj->{_sth_chrbin_limits} = $dbh->prepare("select first_bin, last_bin from chroms where chrom = ?");
# (?1,?2,?3) is ($chr,$seg_start, $seg_stop)
  my $get_genes_s = <<GETGENES;
select g.gene_id,g.start,g.stop,min(g.stop,?3)-max(g.start,?2),g.gene,g.spliceVar from
 genes g join
 ( select gb.gene_id from 
   genes_to_bins gb join
   ( select bin from bins 
      where chrom = ?1 and 
      (( ?2 between binstart and binstop ) or 
       ( ?3  between binstart and binstop )) ) bb
   where bb.bin = gb.bin ) gg
 where g.gene_id = gg.gene_id and
 ( ( g.start between ?2 and ?3 ) or
   ( g.stop  between ?2 and ?3 ) )
GETGENES
  my $get_genes_slow_s = <<GETGENESSLOW;
select g.gene_id,g.start,g.stop,min(g.stop,?3)-max(g.start,?2),g.gene,g.spliceVar from 
 genes g 
 where chrom=?1 and
 (( g.start between ?2 and ?3) or
  (g.stop between ?2 and ?3) )
GETGENESSLOW
  $obj->{_sth_getgenes} = $dbh->prepare($get_genes_s);
  $obj->{_sth_getgenes_slow} = $dbh->prepare($get_genes_slow_s);
  $obj->{_sth_ccnv} = $dbh->prepare("select * from ccnv_u where gene_id = ?");
  $obj->{_sth_genes} = $dbh->prepare("select * from genes where gene_id = ?");
  $obj->{_sth_first_variants} = $dbh->prepare("select min(gene_id) from genes group by gene");
  return $obj;
}
# override the bins, genes, combined_cnvs methods

sub bins { my $self = shift; 1; }
sub genes { my $self = shift; my ($do_exon) = @_; 1; }
sub combined_cnvs { my $self = shift; 1; }

# override the "read files" methods
sub _ReadBinFile {
  my $self = shift;
  my ($bins_f, $bins_id) = ($self->bins_f, $self->bins_id);
  my (%chr_binstart, %chr_binstop, @bin2chr, @bin2start,@bin2stop,@bin2mid);

  for (qw(chr_binstart chr_binstop)) {
#    eval "tie \%$_,'DB_File',_catfile_ref('$bins_f.$bins_id.$_.bdb'),O_RDWR or die \$!";
#    die "$_ : $@" if $@;

  # TODO: this should return results from appropriate SQLite query

  }
  for (qw( bin2chr bin2stop bin2start bin2mid)) {
#    eval "tie \@$_,'DB_File',_catfile_ref('$bins_f.$bins_id.$_.bdb'),O_RDWR or die \$!";
#    die "$_ : $@" if $@;
      # TODO: this should return results from appropriate SQLite query
  }

  $TABLES->{bins}{$bins_id}{chr_bins}  = { start => \%chr_binstart, stop => \%chr_binstop };
  $TABLES->{bins}{$bins_id}{bin2chr}   = \@bin2chr;
  $TABLES->{bins}{$bins_id}{bin2start} = \@bin2start;
  $TABLES->{bins}{$bins_id}{bin2stop}  = \@bin2stop;
  $TABLES->{bins}{$bins_id}{bin2mid}   = \@bin2mid;
  $TABLES->{bins}{$bins_id}{BIN_SIZE}  = $bin2stop[$chr_binstart{1}] - $bin2start[$chr_binstart{1}] + 1;
  return 1;
}

sub _ReadGeneFile {
  my $self = shift;
  my ($do_exon) = @_;
  my ($genes_id, $genes_f, $bins_id, $bins_f) = ($self->genes_id, $self->genes_f, $self->bins_id, $self->bins_f);
  my ($bins, $BIN_SIZE);
  my (%bin2gene, %gene_index, %gene2seg);
  my (@bin2gene,@gene_order, @genelist,@gene2exon_length,@exon_order);

  # tie read/write hashes
#  for (qw(bin2gene gene_index gene2seg)) {
#     eval "tie \%$_,'DB_File',_catfile_ref('$genes_f.$genes_id.$_.bdb'),O_RDWR or die \$!";
#     die "$_ : $@" if $@;
#   }

  # TODO: this should return results from appropriate SQLite query

#   # tie read/write arrays
#   for (qw(gene_order genelist)) {
#     eval "tie \@$_,'DB_File',_catfile_ref('$genes_f.$genes_id.$_.bdb'),O_RDWR or die \$!";
#     die "$_ : $@" if $@;
#   }

  # TODO: this should return results from appropriate SQLite query

  if ($do_exon) {
#     for (qw(gene2exon_length exon_order)) {
#       eval "tie \@$_,'DB_File',_catfile_ref('$genes_f.$genes_id.$_.bdb'),O_RDWR or die \$!";
#       die "$_ : $@" if $@;
  # TODO: this should return results from appropriate SQLite query
#    }
  }
  $DB::single=1;
  $TABLES->{genes}{$genes_id}{gene_order} = \@gene_order;
  $TABLES->{genes}{$genes_id}{gene_order_h} = \%gene_index;
  $TABLES->{genes}{$genes_id}{genelist} = \@genelist;
  $TABLES->{genes}{$genes_id}{bin2gene} = \%bin2gene;
  if ($do_exon) {
    $TABLES->{genes}{$genes_id}{gene2exon_length} = \@gene2exon_length;
    $TABLES->{genes}{$genes_id}{exon_order} = \@exon_order;
  }

  return 1;
}
sub _ReadCombinedCNVFile {
  my $self = shift;
  my ($inf) = $self->cnvs_f;
  return unless $self->cnvs_f;
#  tie my %ccnv, 'DB_File', _catfile_ref("$inf.bdb"), O_RDWR or die "$inf.bdb : $!";

  # TODO: this should return results from appropriate SQLite query

#  $TABLES->{cnvs}{combined} = \%ccnv;
  return 1;
}

# $bins_f - input bin coordinates file
# $db_f - database file
# createBinsSQLiteDB - loads bins table
sub createBinsSQLiteDB {
  my $self = shift;
  my ($bins_f, $bins_id) = @_;
  my $dbh = $self->{dbh};
  $dbh->{RaiseError} = 1;
  my $inf = _catfile_ref("$bins_f.dat");
  open my $infh, $inf or die "cannot open $bins_f: $!";

  my $sth_chr = $dbh->prepare("insert into chroms (chrom,first_bin,last_bin) values (?,?,?)");
  my $sth_bin = $dbh->prepare("insert into bins (chrom,bin,binstart,binstop,binmid) values (?,?,?,?,?)");
  while (<$infh>) {
    chomp;
    if (/^##/) {
      my ($dummy, $chr, $first_bin, $last_bin) = split /\t/;
      $sth_chr->execute($chr, $first_bin, $last_bin);
    } else {
      my ($bin, $chr, $start, $stop) = split /\t/;
      $sth_bin->execute($chr, $bin, $start, $stop, int( ($start + $stop) / 2 ));
    }
  }
  return;
}

sub createGenesSQLiteDB {
  my $self = shift;
  my ($genes_f, $genes_id, $bins_f, $bins_id, $do_exon) = @_;
  my %spliceVar;
  my $genes_inf = _catfile_ref("$genes_f.dat");
  my $dbh = $self->{dbh};
  my $sth = $dbh->prepare("select binstart, binstop from bins limit 1");
  $sth->execute;
  my $a = $sth->fetch;

  my $BIN_SIZE = $a->[1] - $a->[0] + 1;

  open(my $infh, $genes_inf) or die "cannot open $genes_inf : $!";

  my $gi = 0;
  
  my $sth_gene = $dbh->prepare("insert into genes (gene,spliceVar,gene_order,chrom,start,stop) values (?,?,?,?,?,?)");
  my $sth_exon = $dbh->prepare("insert into exons (gene_id,exon_order,start,stop,length) values (?,?,?,?,?)");
  my $sth_bin2gene = $dbh->prepare("insert into genes_to_bins (gene_id,bin) values (?,?)");
  my $sth_get_geneid = $dbh->prepare("select gene_id from genes where gene = ? and spliceVar = ?");
  my $sth_get_chrombins = $dbh->prepare("select first_bin, last_bin from chroms where chrom = ?");

  while (<$infh>) {
    chomp;
    my ($gene, $chr, $start, $stop, $exons) = split /\t/;
    my ($seg,$start_bin,$stop_bin);
    # check whether it's a canonical chromosome
    # skip if not
    $sth_get_chrombins->execute($chr);
    my $fetch_a = $sth_get_chrombins->fetch;
    next unless $fetch_a; # chr is canonical (per Carl, really)
    my ($chr_binstart) = $fetch_a->[0];
    $spliceVar{$gene}++;
    $sth_gene->execute($gene, $spliceVar{$gene},$gi, $chr, $start, $stop);
    $sth_get_geneid->execute($gene, $spliceVar{$gene});
    my $geneid = $sth_get_geneid->fetch->[0];

    $start_bin = $chr_binstart  +
      int($start / $BIN_SIZE);
    $stop_bin  = $chr_binstart  +
      int($stop  / $BIN_SIZE);

    for (my $bin = $start_bin; $bin <= $stop_bin; $bin++) {
      $sth_bin2gene->execute($geneid, $bin);
    }

    if ($do_exon) {
      my $exi = 0;
      for my $exon (split(",", $exons)) {
	my ($estart, $estop) = split("-", $exon);
	$sth_exon->execute($geneid, ++$exi, $estart, $estop, $estop-$estart+1);
      }
    }
    $gi++;
  }

}
# input file from DGV Beta (the latest)
# so we use the genes table to identify the landmarks directly
# (essentially the DGV Beta db is a table of segments)
sub createCombinedCnvSQLiteDB {
  my $self = shift;
  my ($inf) = @_;
  open my $infh, _catfile_ref($inf) or die "can't open $inf: $!";
  my $dbh = $self->{_dbh};
  my $sth_ccnv = $dbh->prepare("insert into ccnv (gene_id, chrom, start) values (?,?,?)");
  while (<$infh>) {
    chomp;
    my ($varid, $chr, $start, $stop, @rest) = split /\t/;
    my @genes = $self->segment_genes($chr, $start, $stop);
    foreach (@genes) {
      $sth_ccnv->execute($_->{id},$chr,$start);
    }
#    $ccnv{"$gene,$chr,$start"} = 1;
  }
}

### API overrides

sub gene_indexes { 
  my $self = shift;
  return @{$self->{gene_indexes}} if $self->{gene_indexes};
  $self->sth('first_variants')->execute;
  while (my $a = $self->sth('first_variants')->fetch) {
    next unless $a->[0];
    push @{$self->{gene_indexes}}, $a->[0];
  }
  return @{$self->{gene_indexes}};
}

# sub gene_indexes {
#   my $self = shift;
#   my ($gene) = @_;
#   my $genes_id = $self->genes_id;
#   my $s = $TABLES->{genes}{$genes_id}{gene_order_h}{$gene};
#   return unless $s;
#   return split(/:/,$s);
# }


sub num_genes_indexed {
  my $self = shift;
  return scalar $self->gene_indexes;
}

sub exons_as_segments { shift->not_implemented('exons_as_segments') }

# sub num_genes_indexed { scalar shift->genelist }

# sub exons_as_segments {
#   my $self = shift;
#   my ($gene_index) = @_;
#   my $genes_id = $self->genes_id;
#   my $s = $TABLES->{genes}{$genes_id}{exon_order}[$gene_index];
#   return unless $s;
#   my @segs = split /;/, $s;
#   my @ret;
#   push @ret, TCGA::CNV::Segment->new($_) for @segs;
#   return @ret;
# }

### all below refactored or ok for SQLite implementation

sub gene_as_segment {
  my $self = shift;
  my ($id) = (@_);
  $self->{_sth_genes}->execute($id);
  my $result = $self->{_sth_genes}->fetch;
  return unless defined $result;
  # note this will return only the first splice variant in the db.
  my %data;
  @data{qw( gene_id gene spliceVar gene_order chrom start stop length )} = @$result;
  return TCGA::CNV::Segment->new($data{chrom}, $data{start}, $data{stop}, $data{gene_id}, $data{gene});
}

sub is_cnv {
  my $self = shift;
  my ($gseg) = @_;
  $gseg = $gseg->Id if (ref $gseg);
  # if the gene id is present in the ccnv table, then is_cnv
  # if not, then not
  my $dbh = $self->{_dbh};
  $self->{_sth_ccnv}->execute($gseg);
  return defined $self->{_sth_ccnv}->fetch;
}

sub bin2gene {
  my $self = shift;
  my ($bin) = @_;
  my $genes_id = $self->genes_id;
  my $s =$TABLES->{genes}{$genes_id}{bin2gene}{$bin};
  return unless $s;
  return [split(/;/,$s)];
}

sub binsize {
  my $self = shift;
  return $self->{_binsize};
}

sub genes_in_bin {
  my $self = shift;
  my ($bin) = @_;
  my $result = $self->{_sth_bin2gene}->execute($bin);
  return unless $result;
  my @ret;
  while (my $a = $result->fetch) {
    push @ret, $a->[0];
  }
  return @ret;
}

sub chrbin_limits {
  my $self = shift;
  my ($chr) = @_;
  $self->{_sth_chrbin_limits}->execute($chr);
  my $result = $self->{_sth_chrbin_limits}->fetch;
  return unless $result;
  return @{$result};
}

### 

# get all genes intersected by a given chromosome segment
sub segment_genes {
  my $self = shift;
  my ($chr,$seg_start, $seg_stop) = @_;
  my $result = $self->sth('getgenes_slow')->execute( $chr, $seg_start, $seg_stop);
  return unless $result;
  my @ret;
  while (my $a = $self->sth('getgenes_slow')->fetch) {
    push @ret, {
		id => $a->[0],
		start => $a->[1],
		stop => $a->[2]
	       }; # returning (gene_id start stop)
  }
  return @ret; 

}

###

sub _catfile_ref {
  my ($fn) = @_;
  return File::Spec->catfile($REFDATA_DIR,$fn);
}

sub refdata {shift->{_refdata}};

# statement handle getter
sub sth {
  my $self = shift;
  my $suffix = shift;
  my $sth = $self->{"_sth_$suffix"};
  croak "Statment with suffix $suffix not defined" unless $sth;
  return $sth;
}

sub not_implemented {
  my $self = shift;
  die ref($self)." : ".shift()." not implemented";
}

=head1 NAME

TCGA::CNV::RefData::SQLite - SQLite relational backend for genome reference TCGA::CNV::RefData object

=head1 SYNOPSIS

=head1 DESCRIPTION

This is a subclass of TCGA::CNV::RefData that uses an SQLite
relational DB to store the gene/genome reference data. Hope it works
better than BDB and L<Tie::File>.

=cut

1;
