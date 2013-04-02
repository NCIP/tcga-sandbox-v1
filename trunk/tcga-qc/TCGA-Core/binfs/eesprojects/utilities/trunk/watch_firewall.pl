#! /usr/bin/perl -w
####################################################################################################
our	$pgm_name		= "watch_firewall.pl";
our	$VERSION		= "v1.0.0 (dev)";
our	$start_date		= "Wed Aug 24 21:45:45 EDT 2011";
our	$rel_date		= "";
####################################################################################################
#	Eric E. Snyder (c) 2011
#	SRA International, Inc.
#	2115 East Jefferson St.
#	Rockville, MD 20852-4902
#	USA
####################################################################################################
=head1 NAME

watch_firewall.pl

=head1 SYNOPSIS

watch_firewall.pl /var/log/firewall

=head1 USAGE

I<watch_firewall.pl> ....

=cut
####################################################################################################
#	Testbed:	
#	Cmdline:	
####################################################################################################
#	History:
#	v1.0.0:
####################################################################################################
my @var = qw( time type  src   dst   src_port dst_port ); 
my @fmt = qw( %-18s %-22s %-16s %-16s %6d      %6d      );
foreach my $file ( @ARGV ){
	my $filestream = "$file";
	if ( $file =~ m/\.bz2$/ ){
		$filestream = "/tmp/$file.$$";
		if ( -f $filestream ){
			unlink $filestream;
			print "removed preexisting $filestream.\n";
		}
		`bzcat $file > $filestream`;
		if ( -f $filestream ){
			print "filestream = $filestream\n";
		}
	}

	open( FILE, "$filestream" ) or die "Cannot open file: \"$file\" for reading.\n";
	while( <FILE> ){
		chomp;
		my %dat = ();
		if( /^([A-z]{3} \d+ [\d:]{8}) .+\[\d+.\d+\] (\S+) .+SRC=(\S+) DST=(\S+) .*SPT=(\d+) DPT=(\d+)/ ){
			for( my $i = 0; $i < @var; $i++ ){
				my $key = $var[$i];
				$dat{$key} = ${$i+1};
				printf( "$fmt[$i]\t", ${$i+1} );
			}
			print "\n";
		}
	}
	close( FILE );
}
