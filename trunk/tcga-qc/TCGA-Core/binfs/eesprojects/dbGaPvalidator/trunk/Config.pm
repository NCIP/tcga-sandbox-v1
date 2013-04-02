package dbGaP::Config;
use strict;
use warnings;

BEGIN {
    require Exporter;
    our @ISA = qw(Exporter);
    our @EXPORT = qw(@DBGAP_DD_HEADERS);
}

our @DBGAP_DD_HEADERS = qw(VARNAME VARDESC TYPE VALUES);


=head1 NAME

dbGaP::Config - Common configuration values and constants for dbGaP submissions

=head1 AUTHOR

Mark A. Jensen (mark.jensen@nih.gov)

=head1 COPYRIGHT

(c) 2011 SRA International Inc.

=cut

    1;
