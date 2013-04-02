package TCGA::EXPR::ExprIO;
use strict;
use warnings;

# maps the headers to the required info from the records
# (barcode platform_id platform_name entrez_gene_symbol expression_value sample_type_code)
# note - sample_type_code is only directly available in DCC query format
# an attempt to assign sample type by parsing the barcode can be 
# made for other types

my %TYPE_HDR_MAP = (
#    'Broad' => [qw(ID chrom loc.start loc.end num.mark seg.mean sample_type_code)],
#    'WashU' => ["Sample ID", "chromosome", "start", "end", "# markers", "mean", "sample_type_code"],
    # following is hard-coded, but should be synced with
    # @headers in TCGA::Level4::Config
    'DCC' => [qw(barcode platform_id platform_name entrez_gene_symbol expression_value sample_type_code)]
);


sub new {
  my $class = shift;
  my $filename = shift;
  my ($headers, $header_map) = @_;
  my $self = { _filename => $filename,
	  _filetype => undef,
	  _fh => undef};
  if ($headers and ref $headers) {
    $self->{_hdrs} = $headers;
    $self->{_type} = 'custom';
    $TYPE_HDR_MAP{'custom'} = $header_map || $TYPE_HDR_MAP{'DCC'};
  }
  bless $self, $class;
  $self->_open($headers);

}

sub _open {
  my $self = shift;
  local $_;
  my ($fn) = map { /\.gz$/ ? "gzip -dc $_ |" : $_  } $self->{_filename};
  my $f;
  open ($f, $fn) or die "$fn: $!";
  $self->{_fh} = $f;
  if (!defined $self->{_hdrs}) {
    $self->{_hdr_line} = <$f>;
    chomp $self->{_hdr_line};
    $self->{_hdrs} = [split /\t/, $self->{_hdr_line}];
    $self->_guess_type;
  }
  return $self;
}

sub next_rec {
  my $self = shift;
  my $fh = $self->{_fh};
  my %h;
  my %ret;
  while (<$fh>) {
      chomp;
      if ($_) {
	  @h{$self->hdrs} = split /\t/;
	  # normalize the keys to DCC list
	  @ret{@{$TYPE_HDR_MAP{DCC}}} = @h{@{$TYPE_HDR_MAP{$self->type}}};
	  unless (defined $ret{sample_type_code}) {
	      my ($st) = $ret{barcode} =~ /TCGA-..-....-(..).-...-....-../;
	      $ret{sample_type_code} = $st;
	  }
	  last;
      }
  }
  return unless %ret;
  return @ret{@{$TYPE_HDR_MAP{DCC}}};
}

sub hdrs { return @{shift->{_hdrs} } }
sub hdr_line { return shift->{_hdr_line} }
sub filename { return shift->{_filename} }
sub type { return shift->{_type} }

sub _guess_type {
  my $self = shift;
  for ($self->hdr_line) {
#     /ID.chrom.loc\.start.loc\.end/ && do {
#       $self->{_type} = 'Broad';
#       last;
#     };
#     /chromosome.start.end.cytoband/ && do {
#       $self->{_type} = 'WashU';
#       last;
#     };
    /barcode.uuid.participant_code/ && do {
      $self->{_type} = 'DCC';
      last;
    };
    do { # default
      die "File ".$self->filename." not a recognized level 3 expression file type";
    };
  }
  return;
}

=head1 NAME

TCGA::EXPR::ExprIO - Provide handles to various kinds of files having level 3 expression data

=head1 SYNOPSIS

my $xprio = TCGA::EXPR::ExprIO('exprfile.txt');
while ( my ($barcode,$chromosome,$start,$stop,$num_markers,$segment_mean) =
        $xprio->next_rec ) {
  # operate
}

=head1 DESCRIPTION

Guesses Level 3 expr query dumps from L<TCGA::Level4> scripts.

Best downstream results will be obtained with DCC Level 4 (L<TCGA::Level4>) query dumps.
=cut

1;
