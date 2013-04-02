#-*-perl-*-
use Test::More qw(no_plan);
use File::Spec;
use JSON;
use lib '../lib';
use strict;
use warnings;

my $TESTDIR = -e 't' ? File::Spec->catdir('t','samples') : File::Spec->catdir('..','t','samples');

TODO: {
  local $TODO = 'test that json encoded numbers are not quoted strings';
  fail;
}

TODO: {
  local $TODO = 'test that date formats are correctly parsed';
  fail;
}
