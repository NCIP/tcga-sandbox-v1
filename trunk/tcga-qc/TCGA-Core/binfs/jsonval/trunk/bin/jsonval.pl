#!/bin/perl
#$Id: jsonval.pl 17331 2012-07-11 14:49:27Z jensenma $

=head1 NAME

jsonval - Validate JSON representation of BCR dashboard data

=head1 SYNOPSIS

jsonval.pl [json file]
cat json.txt | jsonval -

=head1 USAGE

I<jsonval.pl> validates a BCR dashboard in JSON format, constructed
according to the specification at L<https://wiki.nci.nih.gov/x/jqn9AQ>.

Error messages are emitted on STDERR. Success is indicated by a zero exit value.

=cut

use strict;
use warnings;

use lib '.';
use lib '../lib';
use JSON;
use TCGA::BCR::JSONVal::Configure;
use Scalar::Util qw(looks_like_number);
use Getopt::Long;
use Pod::Usage;

local $SIG{__WARN__} = sub { die $_[0] };

# from JSONVal::Config :
our @admissible_disease_abbrevs;
our $expected_factors;
our $date_format_matcher;
our ($warn_pfx, $die_pfx, $json_die_pfx);
our (@null_ok);

my @expected_tables = sort keys %$expected_factors;

my $help;
my $FAIL=0;
my $WARN=0;
my ($json_file, $json_fh, $json_obj, $json_str, $test);

GetOptions('help|?' => \$help);
$json_file = shift @ARGV;
$help = 1 unless defined $json_file;
$help && pod2usage(2);

if ($json_file eq "-") {
  open $json_fh, "-" or die $!;
}
else {
  open $json_fh, "<", $json_file or die $!;
}
local $/ = undef;
$json_str = <$json_fh>;

$json_obj = JSON->new();
# capture JSON syntax errors
eval {
  $test = $json_obj->decode($json_str);
};
if ($@) {
  print STDERR "$json_die_pfx : $@\n";
  exit(1);
}


# check full content, names, and datatypes

my @tables = keys %$test;
for my $tbl (@tables) {
  warn_msg( "'$tbl' top-level tag not expected; ignoring..." ) unless grep /$tbl/, @expected_tables;
}

for my $table (@expected_tables) {
  unless (grep /$table/,@tables) {
    warn_msg("'$table' top-level tag is expected, but not present in input");
    next;
  }
  ($table =~ /^version$/) && do {
    my $ver = $test->{$table};
    if (ref $ver) {
      fail_msg("Tag '$table' value should be a string, not an object");
    }
    next;
  };
  ($table =~ /^timestamp$/) && do {
    my $date = $test->{$table};
    if (ref $date) {
      fail_msg("Tag '$table' value should be date string, not an object");
      next;
    }
    unless ($date =~ /$date_format_matcher/) {
      fail_msg("Tag '$table' value '$date' not in DD/MM/20YY format");
    }
    next;
  };
  # else, check subitems and types
  my $table_ary = $test->{$table};
  unless ( ref $table_ary eq 'ARRAY' ) {
    fail_msg("Tag '$table' value: an array of objects is expected, but not present in input");
    next;
  }
  unless (@$table_ary) {
    warn_msg("Tag '$table' value: No data present in array");
    next;
  }
  my $elt_index = 0;
ARRAY_ELT:
  foreach my $hash (@$table_ary) {
    $elt_index++; # count from 1
    unless ( ref $hash eq 'HASH' ) {
      fail_row_msg($table, $elt_index, "A hash object is expected, but not present in input");
      next ARRAY_ELT;
    }
    my @tags = keys %$hash;
    my @expected_tags = @{$expected_factors->{$table}};
    for my $tag (@expected_tags) {
      fail_row_msg($table,$elt_index, "Tag '$tag' is expected, but not present in input") unless (grep(/$tag/, @tags) || grep(/$tag/, @null_ok));
    }
  TAG:
    foreach my $tag ( @tags ) {
      unless ( grep /$tag/, @expected_tags ) {
	warn_row_msg($table,$elt_index,"Tag '$tag' not expected; ignoring...") unless grep /$tag/,@expected_tags;
	next TAG;
      }
      my $val = $hash->{$tag};
      for my $type ($type_map{$tag}) {
	die "DEV: no type listed for '$tag'" if (!$type);
	# value is null
	unless (defined $val) {
	  if ( !grep /$tag/, @null_ok ) {
	    warn_row_msg($table, $elt_index, "Tag '$tag' has null data");
	  }
	  next TAG;
	}
	ref($type) && do {
	  my @expected_vals = @$type;
	  if (ref $val) { # enumeration
	    fail_row_msg($table,$elt_index, "Tag '$tag' value should be one of [".join(',',@expected_vals)."], not an object");
	    next TAG;
	  }
	  # if number, check if number or date allowed
	  unless ( grep(/^$val$/, @expected_vals) ||
		   (grep(/^_int_$/, @expected_vals) && looks_like_number($val)) ||
		   (grep(/^_date_$/, @expected_vals) && ($val =~ /$date_format_matcher/))
	      ) {
	    fail_row_msg($table,$elt_index,"Tag '$tag' value should be one of [".join(',',@expected_vals)."], not '$val'");
	  }
	  next TAG;
	};
	($type eq 'string') && do {
	  if (ref $val) {
	    fail_row_msg($table,$elt_index, "Tag '$tag' value should be a string, not an object");
	    next TAG;
	  }
	  next TAG;
	};
	($type eq 'int') && do {
	  if (ref $val) {
	    fail_row_msg($table,$elt_index,"Tag '$tag' value should be an integer, not an object");
	    next TAG;
	  }
	  unless ( looks_like_number($val) ) {
	    fail_row_msg($table,$elt_index,"Tag '$tag' value is not numeric");
	  }
	  next TAG;
	};
	($type eq 'float') && do {
	  if (ref $val) {
	    fail_row_msg($table,$elt_index,"Tag '$tag' value should be a float, not an object");
	    next TAG;
	  }
	  unless ( looks_like_number($val) ) {
	    fail_row_msg($table,$elt_index,"Tag '$tag' value is not numeric");
	  }
	  next TAG;
	};
	($type eq 'date') && do {
	  if (ref $val) {
	    fail_row_msg($table,$elt_index, "Tag '$tag' value should be a date string, not an object\n");
	    next TAG;
	  }
	  unless ($val =~ /$date_format_matcher/) {
	    fail_row_msg( $table, $elt_index,"Tag '$tag' value '$val' not in DD/MM/20YY format");
	  }
	  next TAG;
	};
	# else
	die "DEV: unrecognized type '$_' in validator configuration";
      }
    }

  }
}

exit 1 if $FAIL;
exit 0;

sub fail_msg {
  my ($msg) = @_;
  printf STDERR "$die_pfx : %s\n", $msg;
  $FAIL++;
}

sub warn_msg {
  my ($msg) = @_;
  printf STDERR "$warn_pfx : %s\n", $msg;
  $WARN++;
}


sub fail_row_msg {
  my ($tbl,$row, $msg) = @_;
  printf STDERR "$die_pfx : In table '%s', row %d: %s\n", $tbl, $row, $msg;
  $FAIL++;
}

sub warn_row_msg {
  my ($tbl,$row, $msg) = @_;
  printf STDERR "$warn_pfx : In table '%s', row %d: %s\n", $tbl, $row, $msg;
  $WARN++;
}

1;
