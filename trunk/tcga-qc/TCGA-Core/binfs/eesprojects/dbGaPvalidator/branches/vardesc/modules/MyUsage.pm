#!/usr/bin/perl -w
# $id$
####################################################################################################
our	$pgm_name		= "MyUsage.pl";
our	$VERSION		= "v1.1.4 (dev)";
our	$start_date		= "Thu May 20 21:11:26 EDT 2010";
our	$rel_date		= "";
####################################################################################################
#	MyUsage.pm
#	Thu May 20 21:11:26 EDT 2010
#	v1.1.4 (dev)
####################################################################################################
#	Eric E. Snyder (c) 2010
#	Virgnia Bioinformatics Institute
#	Virginia Polytechnic Institute and State University
#	Blacksburg, VA 24061-0447
#	USA
####################################################################################################

use strict;
use Getopt::Std;					# use getopt package for parsing command-line flags and options
use EESnyder;
require Exporter;
our @ISA = qw(Exporter );
our @EXPORT = qw( Usage );

# our ( $pgm_name, $VERSION, $rel_date, $start_date );
$Getopt::Std::STANDARD_HELP_VERSION = 1;

####################################################################################################
#	&Usage()
#	Parse command-line flags and options given pointer to %usage, which contains the parameters'
#	default values, variable type, status (required or not) and descriptive info to be printed when
#	help is needed.  %usage contains:
#		%usage = (
#			'<opt>' => {									[A-Za-z0-9]
#				'type'     => "<variable type>",			(boolean, integer, float, string)
#				'usage'    => "<text usage info>",			e.g.: use -h to get 'help'
#				'required' => <status>,						[01]; 0 => false, 1 => true
#				'init'     => <initial value>,				initial value, if omitted: zero for numeric vars,
#															"" for string
#			},
#			...
#		);
#	Note: required parameters, regardless of type, should be initialized as "" (NULL string).
#
####################################################################################################
sub Usage {
	my ( $usage, $opts, $infiles ) = @_;

#	populate %$opts with default values found in %$usage and create the first argument for getopts
#	consisting of a string of characters representing the options (with ":" following options
#	that require an argument)

	my $opt_keys = "";									# init list of option letters, e.g. "abcd:efg"
	foreach my $key ( sort keys %$usage ) {			# foreach key, in order ...
		$opt_keys .= $key;								# add key character
		my $vtype = $usage->{$key}{'type'};				# get option variable type
		if( $vtype ne "boolean" ){						# if parameter takes an argument (i.e.: non-boolean)
			$opt_keys .= ":";							# add a colon
		}
		if ( $usage->{$key}{'init'} ){					# if there is an initial value ...
			unless ( $vtype eq "boolean" ){
				$opts->{$key} = $usage->{$key}{'init'};	# set %opts value to it
			}
		} else {										# otherwise
			if ( $vtype eq "string" ){					# if string variable ...
				$opts->{$key} = "";						# initialize to null string
			} else {									# if numeric or boolean ...
				$opts->{$key} = 0;						# initialize to zero
			}
		}
	}
#	prepare message containing usage/help information

	my $pname = $0;
	$pname =~ s/^.*\///;									# get program name, strip leading path info
	my $msg = "\nusage: $pname [-options] " .
				join(" ", @$infiles ) . "\n"; 				# begin assembling "usage" information

	foreach my $key ( sort keys %$usage ) {					# foreach key ...
		my $p = $usage->{$key};								# assign tmp pointer to usage hash
		if ( $p->{'type'} eq "boolean" ) {					# for boolean variables ...
			$msg .= "\t-$key\t\t$p->{'usage'} (default = \"";	# print flag and usage info +
			$msg .= &truth( $p->{'init'} );					# default value as TRUE/FALSE
			$msg .= "\")\n";
		} elsif ( $p->{'type'} eq "string" ) {				# if var is a string
			$msg .= "\t-$key <$p->{'type'}>\t$p->{'usage'}"
				. " (default = \"$p->{'init'}\")\n";    	# print default info with quotes
		} else {											# for variables that take an argument ...
			$msg .= "\t-$key <$p->{'type'}>\t$p->{'usage'}"
				. " (default = $p->{'init'})\n";    		# print flag, var type, usage info+default value
		}
	}

#	get command-line parameters, over-writing initialized values in %$opts

	if ( &getopts( $opt_keys, $opts )  == 0 ){	 			# get args from cmdline & check return val
		die "Error processing command-line args using &getopts().\n";
	}

#	deal with special case of user entering -V for version
#	information

	my $vmsg = "";
	if ( $opts->{'V'} ){
		$vmsg = "\n$pgm_name $VERSION\n" .
				"released: " . ( $rel_date ? $rel_date : $start_date ) . "\n" .
				"\nNote: in the future, please use \"--version\" for complete version information.\n\n";
	}

#	deal with user input errors

	if ( $#ARGV == -1 && !$opts->{'h'} ) {					# check for basic screw-ups
		print STDERR "\nERROR: No input file!  For instructions, run using the \'-h\' option.\n"
			if $#ARGV == -1;
		$opts->{'h'}++;
	}
	if ( $opts->{'h'} ){									# print usage information (help)
		Getopt::Std::help_mess($opt_keys);
		die $msg . $vmsg;
	}

	foreach my $key ( sort keys %$usage ) {					# foreach key ...
		next unless $usage->{$key}{'required'};				# pass thru if parameter is required
		next if $opts->{$key};								# skip if parameter's value exists, if not...
		die "You are required to provide a value for \"-$key\" on "		# print error message
			 .	"the command line.\nRun $pname with \"-h\" option for help.\n\n";
	}

#	process command-line flags: if default value is TRUE,
#	using the flag toggles the value to FALSE

	foreach my $key ( sort keys %$usage ) {
		if ( $usage->{$key}{'type'} eq "boolean" ){
			$opts->{$key} = (
				$opts->{$key}								# if new value is TRUE then
				? ($usage->{$key}{'init'}? 0 : 1 )			# supply logical opposite of initial value
				: ($usage->{$key}{'init'}? 1 : 0 )			# otherwise supply the original initial value
			);
		}
	}

#	prepare banner summarizing run-time parameters for inclusion in output report

	my $date = &get_date;
	my $banner =
		  "#" x 80
		. "\n#	$pname "
		. $Getopt::Std::VERSION
		. "\n"
		. "#\t$date\n"
		. "#" x 80
		. "\n";														# pretty print pgm name and release #
	foreach my $key ( sort keys %$usage ) {    						# run through option letters
		$banner .= "#  " . sprintf( "$key = %10s", $opts->{$key} );	# print "opt_* = <option value>"
		$banner .= "  $usage->{$key}{'usage'}\n";					# print description of option
	}
	$banner .= "#" x 80 . "\n";										# finish off banner

	return ($banner);
}
1;
