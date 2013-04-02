package TCGA::CNV::SegIO;
use strict;
use warnings;

# maps the headers to the required info from the records
# (barcode, chromosome, start, stop, number_of_markers, segment_mean, sample_type)
# note - sample_type_code is only directly available in DCC query format
# an attempt to assign sample type by parsing the barcode is made for the other 
# types
my %TYPE_HDR_MAP = (
    'Broad' => [qw(ID chrom loc.start loc.end num.mark seg.mean sample_type_code)],
    'WashU' => ["Sample ID", "chromosome", "start", "end", "# markers", "mean", "sample_type_code"],
    # following is hard-coded, but should be synced with
    # @headers in TCGA::Level4::Config
    'DCC' => [qw(barcode chromosome chr_start chr_stop num_mark seg_mean sample_type_code)]
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
  my ($fn) = map { /\.gz$/ ? "gzip -dc $_ |" : $_  } $self->{_filename};
  open my $f, $fn or die $!;
  $self->{_fh} = $f;
  if (!defined $self->{_hdrs}) {
    $self->{_hdr_line} = <$f>;
    chomp $self->{_hdr_line};
    $self->{_hdrs} = [split /\t/, $self->{_hdr_line}];
    $self->_guess_type;
  }
  return $self;
}

sub next_seg {
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
    /ID.chrom.loc\.start.loc\.end/ && do {
      $self->{_type} = 'Broad';
      last;
    };
    /chromosome.start.end.cytoband/ && do {
      $self->{_type} = 'WashU';
      last;
    };
    /barcode.uuid.participant_code/ && do {
      $self->{_type} = 'DCC';
      last;
    };
    do { # default
      die "File ".$self->filename." not a recognized seg file type";
    };
  }
  return;
}

=head1 NAME

TCGA::CNV::SegIO - Provide handles to various kinds of files having CBS segment info

=head1 SYNOPSIS

my $segio = TCGA::CNV::SegIO('segfile.txt');
while ( my ($barcode,$chromosome,$start,$stop,$num_markers,$segment_mean) =
        $segio->next_seg ) {
  # operate
}

=head1 DESCRIPTION

Guesses Broad seg files, WashU seg files, and level 3 cna query dumps
from L<TCGA::Level4> scripts.

Beware of Broad seg files: the ID or "barcode" is a Broad-specific one. The SDRF needs to
be used to get the DCC barcode.

Best downstream results will be obtained with DCC Level 4 (L<TCGA::Level4>) query dumps.
=cut

1;
