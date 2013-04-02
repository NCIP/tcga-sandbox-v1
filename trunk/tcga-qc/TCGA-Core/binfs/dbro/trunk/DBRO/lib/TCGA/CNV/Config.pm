package TCGA::CNV::Config;
use strict;
use warnings;

use base 'Exporter';
BEGIN {
  our @EXPORT = qw(
		    $MIN_OVERLAP 
		    $MIN_SEG 
		    $AMP_CAP_L2R
		    $AMP_MIN_L2R
		    $DEL_CAP_L2R
		    $DEL_MAX_L2R
		    $REFDATA_DIR
		    $NORMAL_TYPE_MATCHER
		    $TUMOR_TYPE_MATCHER
		    %CHR_ORDER
		 );

}

our $MIN_OVERLAP = $ENV{MIN_OVERLAP} || 1;
our $MIN_SEG = $ENV{MIN_SEG} || 200;
our $AMP_CAP_L2R = $ENV{AMP_CAP_L2R} || 2.0000;
our $AMP_MIN_L2R = $ENV{AMP_MIN_L2R} || 1.0000;
our $DEL_CAP_L2R = $ENV{DEL_CAP_L24} || -2.0000;
our $DEL_MAX_L2R = $ENV{DEL_MAX_L2R} || 0.9000;

# Comment in Carl's snp6.csh script:
## do NOT take -11- (normal tissue) samples
## too many look unreliable
# but for some diseases (PAAD), only 11 normals are available.
# Type 10 is blood-derived normal
our $NORMAL_TYPE_MATCHER = qr/^1[012]$/;
our $TUMOR_TYPE_MATCHER = qr/^[024]?[0-9]$/;

# location of level 0 (dgv, bins, gene name) data directory
our $REFDATA_DIR = $ENV{REFDATA_DIR} || "./refdata";
our @chr_order = (1..22,'X','Y');
our %CHR_ORDER;

@CHR_ORDER{@chr_order} = (1..@chr_order);

=head1 NAME

TCGA::CNV::Config - Set run parameters, other values for SNP6 slicer/dicer

=head1 SYNOPSIS

use TCGA::CNV::Config;

=head1 DESCRIPTION

You can set an environment variable with the same name as the config
file variable to override the defaults in the config file.

MIN_OVERLAP
MIN_SEG
REFDATA_DIR

=cut

1;
