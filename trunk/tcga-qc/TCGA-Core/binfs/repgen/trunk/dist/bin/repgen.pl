#!/usr/bin/perl 

eval 'exec /usr/bin/perl  -S $0 ${1+"$@"}'
    if 0; # not running under some shell
# $Id: repgen.pl 7628 2010-07-26 16:45:32Z jensenma $
# todo
# figure out where the xml file comes in; it does, doesn't it?

=head1 NAME

repgen.pl - script for automating production of DCC reports from parsable text files

=head1 SYNOPSIS

[perl] repgen.pl [configModuleName] [options] > [outfile] 

  Options:
    [configMdulename] : kind of report desired (config module)
    --[type]-file : input file, one parm invocation per file of each [type],
                     (repgen.pl [module] --avail-filetypes to see list)
    --nolookup : do not connect to the database for lookups
                 ( default is --lookup )
    --dsn [dsnspec] : database source name in colon-delimited format
    --user [username] : db user
    --password [password] : db user password
    --sort-by [field-name] : sort the final report by specified field 
                             (acsending order)
    --avail-fields  : print the field names available for sorting upon
    --avail-filetypes : print the list of filetypes expected by the 
                        specified [module]
    --uniquify-forward [field-name] : create a set of records containing unique
                                      entries for [field-name], overwriting as
                                      the input is processed
    --uniquify-backward [field-name]: create a set of records containing unique
                                      entries for [field-name], recording only
                                      the first unique entry as the input is
                                      processed
      (to uniquify over multiple fields, enter the fields joined by '+', as in
       --uniquify-forward AliquotBC+RecDate )
    --help : get this help
    --version : get the script version

=head1 DESCRIPTION

This is a highly generalized version of Jessica's collection of parsing
scripts for telemetry files. It's intended to do everything at once
and spit out a report suitable for framing (or opening as a tab-delimited
file in Excel).

The script will parse files which contain one record per line of
text. More work will be2 necessary for parsing XML.

The script is a harness for what can be multiple configuration perl
modules, which describe:

=over

* input file report types
* desired lines from those input files
* desired fields to be parsed from each input file line
* direct database lookups for certain fields to more usefully annotate
records

=back

The script hits the DB explicitly for doing various lookups.  You can
inhibit DB connection by providing C<--nolookup>. In this case, all db
lookup fields will return the value 'N/A'. If you do not have the
L<DBD::Pg> driver, or if you cannot connect to the DB, lookups will be
disabled (with a warning, if C<--verbose> is active), but report generation
will continue.

=head1 NOTES

Note that the configuration info is exported by a module of the form
C<RepGen::[reporttype]>. It provides a very flexible way to configure
sample file line parsing and database lookups.  See 
L<RepGen::Configure> for documentation. 

This script uses L<DBD::Pg>, and will break upon the full deprecation
of the postgresql DB.

=head1 TODOS

=over

* Ways to generalize the DB lookup to include both multiple variable
conditions per query and receipt of multiple fields per record returned
are fairly straightforward and can be included if necessary.

=back

=head1 AUTHOR - Mark Jensen/TCGA DCC

Email: jensenma@mail.nih.gov

=cut

use strict;
use warnings;
use Getopt::Long;
use IO::Seekable;
use File::Temp qw(tempfile);
use DBI;
use lib '.';
use lib '../lib';
use RepGen::Configure qw( $VERSION );
# following will be imported from the selected config module:

our (%filters, %fields, @output_fields, @db_fields, %config, 
     %na_text,
     $default_user, $default_pwd,$default_dsn, 
     $TOO_MANY);

#idea is to stack the reduced NCBI_SRA_Metadata_TCGA data on top of 
# the reduces exchange data and make it pretty

# check if the postgres driver is available; if not, then things are skipped 
# downstream
my $DB_OK = eval "require DBD::Pg; 1";

my @report_lines; # to contain the actual lines of the final report
my (%records, %uniq);

# config
Getopt::Long::Configure( qw( auto_help auto_version ) );

# options
my ( $sample_files, $exchange_file, $lookup,
     $sort_field, $dsn, $user, $pwd, $show_fields, $show_filetypes,
     $uniquify_forward, $uniquify_backward,
     $verbose );

# figure out the filetype options here, then GetOptions
# the first parameter is always the report module; get this outside of GetOpt...

my $module;

if (!defined $ARGV[0]) {
    $ARGV[0] = '--help';
}
elsif ($ARGV[0] !~ /^-/) {
    $module = shift @ARGV;
}    
elsif ($ARGV[0] =~ /^--?(help|version)/) {
    1;
}
else {
    $ARGV[0] = '--help';
}

if ($module) {
    unless ( eval "require RepGen::$module; import RepGen::$module; 1" ) {
	die "Report config module 'RepGen::$module' is not available";
    }
}

my @filetypes = keys %fields;
my %filetype_options = map { $_ => $_."-files=s@" } @filetypes;
my %input_files;
@input_files{@filetypes} = map { [] } @filetypes; 
my @filetype_options = map { $filetype_options{$_} => $input_files{$_} } @filetypes;

$lookup = 1; # default, use DB if possible
GetOptions(
    @filetype_options,
    "dsn=s" => \$dsn,
    "user=s" => \$user,
    "password=s" => \$pwd,
    "sort-by=s" => \$sort_field,
    "lookup!" => \$lookup,
    "avail-fields" => \$show_fields,
    "avail-filetypes" => \$show_filetypes,
    "uniquify-forward=s" => \$uniquify_forward,
    "uniquify-backward=s" => \$uniquify_backward,
    "verbose" => \$verbose
    );

# help info
my @fields = map { @{$fields{$_}} } @filetypes;

if ($show_fields) {
    print STDERR join("\n", "Available sort fields are:",@output_fields);
    exit(0);
}

if ($show_filetypes) {
    die "No filetypes defined in selected module" unless @filetypes;
    print STDERR join("\n", "Input file parameters are:", map { "--$_\-files" } @filetypes),"\n";
    exit(0);
}

$sort_field ||= $fields[0];

unless ( grep /^$sort_field$/, @fields) {
    warn "specified sort field unrecognized; try --avail-fields";
}

# parm validation

$user ||= $default_user;
$pwd ||= $default_pwd;

unless (!$lookup) { 
    # expect to use the db
    $dsn ||= $default_dsn;
    die "dsn is not set, and no default defined in telem::Config" unless $dsn;
}

undef $dsn if !$lookup;

for (@filetypes) {
    for my $inpf (@{$input_files{$_}}) {
	unless ( -T $inpf ) {
	    die "$_ file '$inpf' invalid or DNE";
	}
    }
}


# taint check for completeness

unless ( !defined($user) || ($user =~ /^[a-z0-9]+$/) ) {
    die "username is invalid";
}
unless ( !defined($pwd) || ($pwd =~ /^[a-z0-9~!@#$%^*]+$/) ) {
    die "password is invalid";
}

my $dbh;
if ($DB_OK) {
  if ($dsn) {
    $dbh = DBI->connect($dsn,$user,$pwd);
    $DB_OK &&= $dbh;
    if ($DB_OK) {
      $dbh->{RaiseError} = 1;
    }
    else {
      warn "Cannot establish db connection; skipping db lookups for this report" if $verbose;
    }
  }
}
else {
  warn "DBD driver not available; skipping db lookups for this report" if $verbose;
}

$DB_OK &&= $lookup;

### Step 1: 
# Filter and concatenate input files of each type into tempfiles:

my %tempfiles;
for my $ftype (@filetypes) {
    $tempfiles{$ftype} = [tempfile( "${module}XXXX", SUFFIX => ".cat", DIR => '.', UNLINK => 0)];
    my $tfh = $tempfiles{$ftype}[0];
    foreach my $fn (@{$input_files{$ftype}}) {
	open my $fh, $fn or die "Problem with sample file '$fn' : $!";
	my @lines = <$fh>;
	# take only the wanted lines using the filters sub for this filetype
	@lines = grep { $filters{$ftype} ? $filters{$ftype}->($_) : 1 } @lines;
	print $tfh @lines;
	close $fh;
    }
    seek($tfh,0,0);
}

my $errct=0;
my $line = 0;

### Step 2:
# parse the filtered input file lines, for all input files and
# filetypes, according to the details in the config module; for each
# parsed line, we build an array of hashes containing the records with
# the desired data

foreach my $ftype (@filetypes) {
    my @fields = @{$fields{$ftype}};
    my $tfh = $tempfiles{$ftype}[0];
  LINE:
    while (<$tfh>) {
	if ($errct > $TOO_MANY) {
	    die "Too many parse errors in input files; sure you specified the right ones?";
	}
	$line++;
	chomp;
	my %values;
	my @tokens = split /\t/;
	# parse line
	foreach my $fld (@fields) {
	    my $cfg = $config{"$ftype\.$fld"};
	    # constants override:
	    if ($cfg->{constant}) {
		$values{$fld} = $cfg->{constant};
		next;
	    }
	    # parse with regexp:
	    next unless defined $cfg->{token}; 
	    my $tok = $tokens[$cfg->{token}];
	    my $regexp = $cfg->{regexp};
	    my $posn = $cfg->{match_posn};
	    # this is too clever for its own good; multiple matches made
	    # at once are wasted. Will see how bad this is (probably not bad)
	    next unless !defined $regexp || defined $posn; # this probably skips cruft in the file, not sure.
	    defined $posn && $posn--; # offset correction
	    if ($regexp) {
		my @matches = $tok =~ qr/$regexp/;
		if (@matches) {
		    $values{$fld} = $matches[$posn];
		}
		else { # no match
		  # check if an N/A value, represented by '-'
		  # todo: make an $NA_regexp for the Configure.pm file
		  if ( $tok =~ /^-$/ ) { # N/A value
		    $values{$fld} = $na_text{$ftype}{$fld} || 'N/A';
		  }
		  else { # an error
		    $errct++;
		    warn "No regexp match in token '$tok' for field '$fld' at line $line" if $verbose;
		  }
		}
	    }
	    else { # take the whole token
		$values{$fld} = $tok;
	    }
	}
	# uniqification:
	if ($uniquify_forward) { # means, replace as you go
	    my @subkeys = split /\+/, $uniquify_forward;
	    my $key = (@subkeys > 1 ? join(':',@values{@subkeys}) : $values{$subkeys[0]});
	    if ($uniq{$key}) { # already recorded
		# update the record this is pointing to
		while ( my ($f,$v) = each %values ) {
		    $uniq{$key}{$f} = $v;
		}
	    }
	    else { # put on the rec list
		push @{$records{$ftype}}, $uniq{$key} = { %values }
	    }
	    1;
	}
	elsif ($uniquify_backward) { # means, skip if you've seen it already
	    my @subkeys = split /\+/, $uniquify_backward;
	    my $key = (@subkeys > 1 ? join(':',@values{@subkeys}) : $values{$subkeys[0]});
	    if (!defined $uniq{$key}) {
		my $h = { %values };
		push @{$records{$ftype}}, $h;
		$uniq{$key} = $h;
	    }
	    else { #stub
		1;
	    }
	}
	else {
	# store for now; later convert the center ID to the center name,
	# lookup the disease for the pt id, etc.
	    push @{$records{$ftype}}, { %values };
	}
    }
}

# Step 3:
# Do the database lookups according to the config module spec and add these
# looked-up fields to each stored record hash element.
# Note that we cache the lookups in a hash so thousands of DB hits are not
# necessary...

my %memo;
foreach my $fld (@db_fields) {
  my $sth;
  # polymorphism: if there is no sql statement defined, 
  # look for a lookup hash in the tbl attribute
  if ($DB_OK && $config{$fld}{sql}) {
      $sth = $dbh->prepare($config{$fld}{sql});
    }
  else {
    $sth = $config{$fld}{tbl};
  };
  foreach my $ftype (@filetypes) {
    foreach my $rec (@{$records{$ftype}}) {
      my $key = $rec->{$config{$fld}{lookup_fld}};
      next unless defined $key;
      if ( defined $memo{$fld.'.'.$key} ) { # get the cached data
	$rec->{$fld} = $memo{$fld.'.'.$key};
      }
      else { # hit the DB, if DB is present, and cache it too
	my $res;
	if ($sth and ref($sth) =~ /db/i) {
	  $sth->execute($key);
	  $res = $sth->fetch;
	}
	elsif (ref($sth) eq 'HASH') {
	  $res->[0] = $sth->{$key};
	}
	else {
	  $res->[0] = "N/A"; # db not present, skip
	}
	$memo{$key} = $rec->{$fld} = ($res ? $$res[0] || "N/A" : "N/A");
      }
    }
  }
}


### Step 4:
# Output the report as a tab-delimited text file.
# Header line is constructed based on the 'header' fields
# as specified in the config module


foreach my $ftype (@filetypes) {
  next unless $records{$ftype};
  print join("\t", map { $config{"$ftype\.$_"}{header} || $config{$_}{header} || "N/A" } @output_fields), "\n";
  foreach my $rec (sort {
    ($a->{$sort_field} || 'N/A') cmp 
      ($b->{$sort_field} || 'N/A')
    } @{$records{$ftype}}) {
    print join("\t", map { $rec->{$_} || "N/A" } @output_fields), "\n";
  }
}

END {
  # cleanup
    if ($_ && $tempfiles{$_} && $tempfiles{$_}[0]) {
	close $tempfiles{$_}[0] for @filetypes;
	unlink $tempfiles{$_}[1] for @filetypes;
    }
}
1;
