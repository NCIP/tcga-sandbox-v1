#! /usr/bin/perl -w
# $Id: prettyDir.pl 17925 2012-12-06 22:51:44Z snyderee $
####################################################################################################
my $revision = '$Revision: 17925 $';
$revision =~ s/^\$\s*(.+)\s*\$$/$1/;
our $rel_date       = '$Date: 2012-12-06 17:51:44 -0500 (Thu, 06 Dec 2012) $';
unless ($rel_date =~ s/^\$Date:\s*(\d+)-(\d+)-(\d+)\s+([\d:]+)\s+([+-]\d+)\s+\((\w+),\s+\d+\s+(\w+)\s.+$/$6 $7 $3 $4 [$5] $1/ ){
    warn "Unable to parse SVN date string: \"$rel_date\".\n";
}
our $pgm_name       = "prettyDir.pl";
our $VERSION        = "v1.1.0 ($revision)";
our	$start_date		= "Thu Sep 27 10:45:59 EDT 2012";
####################################################################################################
use strict;
use warnings;
use EESnyder;
use Getopt::Std;
my $fnameExp = '\w.@#+=-';
my $ts = 4;
our( $opt_C, $opt_F, $opt_T, $opt_d, $opt_f, $opt_j, $opt_h, $opt_n, $opt_t, $opt_w );
getopts('CF:c:df:jnw:t:');
my $argv = &check_for_files( \@ARGV, 1, 1 );
my $ntabs = '';
$ntabs = "\t" x $opt_t if $opt_t;
$ts = $opt_T if $opt_T;
my $args = join( " ", @$argv );
my @ls = split( /\n/, `ls -l $args` );
my %dat = ();
for( my $i = 0; $i < @ls; $i++ ){
	if ( $ls[$i] =~ m/^\S+ \d+ (\w+) (\w+)\s+(\d+)\s+([A-Z][a-z]{2}\s+\d+\s+[\d:]+)\s+([$fnameExp]+)$/ ){
		my %h = (	'User'		=>	$1,
					'Group'		=>	$2,
					'Bytes'		=>	$3,
					'Date'		=>	$4,
					'Filename'	=>	$5,
				);
		$dat{$5} = \%h;
	}
	if ( $ls[$i] =~ m/^\S+ \d+ (\w+) (\w+)\s+(\d+)\s+([A-Z][a-z]{2}\s+\d+\s+[\d:]+)\s+([$fnameExp]+) -> ([$fnameExp]+)/ ){
		my %h = (	'User'		=>	$1,
					'Group'		=>	$2,
					'Bytes'		=>	$3,
					'Date'		=>	$4,
					'LinkName'	=>	$5,
					'Filename'	=>	$6,
				);
		$dat{$5} = \%h;
	}
}
my @wc = split( /\n/, `wc -l $args` );
foreach ( @wc ){
	next if /total/;
	if( m/^\s*(\d+)\s+([$fnameExp]+)$/){
		$dat{$2}{'Lines'} = $1;
	}
}
my @printKeys = qw( Lines Bytes Date Filename );
my %maxWidth = ();
foreach my $key ( @printKeys ){
	$maxWidth{ $key } = length( $key );
}
foreach ( keys %dat ){
	foreach my $key ( @printKeys ){
		my $w = length( $dat{$_}{$key} );
		if ( $w > $maxWidth{ $key }){
			$maxWidth{ $key } = $w;
		}
	}
}
my %tabWidth = ();
foreach my $key ( @printKeys ){
	$tabWidth{ $key }{'div'} = $maxWidth{ $key } / $ts;
	$tabWidth{ $key }{'mod'} = $maxWidth{ $key } % $ts;
}



my $output = $ntabs;
foreach my $key ( @printKeys ){
	$output .= '-' x $maxWidth{ $key } . ' ' x ( $ts - $tabWidth{ $key }{'mod'} ) ;
}
$output .= "\n$ntabs";
foreach my $key ( @printKeys ){
	$output .= $key;
	$output .= "\t" x ( 1  + ( ( $maxWidth{ $key } - length( $key ) ) / $ts ) );
}
$output .= "\n$ntabs";
foreach my $key ( @printKeys ){
	$output .= '-' x $maxWidth{ $key } . ' ' x ( $ts - $tabWidth{ $key }{'mod'} ) ;
}
$output .= "\n$ntabs";
foreach ( sort keys %dat ){
	foreach my $key ( @printKeys ){
		my $pad = 0;
		$pad = $maxWidth{ $key } - length( $dat{$_}{$key} ) unless ( $key eq 'Filename' );
		$output .= ' ' x $pad . $dat{$_}{$key} . "\t";
	}
	$output .= "\n$ntabs";
}
foreach my $key ( @printKeys ){
	$output .= '-' x $maxWidth{ $key } . ' ' x ( $ts - $tabWidth{ $key }{'mod'} ) ;
}
$output .= "\n";

print "$output";
