# $Id: Configure.pm 7628 2010-07-26 16:45:32Z jensenma $
package RepGen::Configure;

# this is POD only

=NAME

RepGen::Configure - a base class for configuring the report generator repgen.pl


=SYNOPSIS

When creating a new configuration module, do

 use base RepGen::Configure;

to inherit the globals and their standard contents.

=DESCRIPTION

Here is a list of the parser globals and how they're used.

=over

* C<%fields>

is a hashref of keys representing the filetypes to be parsed, with
values of arrayrefs of the short fields names to be extracted from each
parsed line. Extraction parameters are described within the C<%config>
hash.

* C<@db_fields>

is an array of keys representing the fields (by short field name) to
be looked up in the database via an SQL query, using parsed field data
as the lookup key. Currently, just a simple one-to-one lookup is supported.

* C<%config> 

is a configuration hash exported to the main program. 
The keys are the members of C<@fields{@filetypes}> and C<@db_fields>.
The value for each short field key is a hash ref with following elements:

 header     : the header text for the field in the final report
 token      : the index of the token in the split line to be parsed by...
 regexp     : the regular expression to use to parse the token. If this 
              is undefined, then the whole token is returned.
 match_posn : the position of the match in the regexp to be extracted
              ($1, $2, etc.)
 constant   : a string value to be always returned, in lieu of an 
              extracted string (regexp, token, match_posn are ignored if
              constant field is set)

The value for each C<@db_fields> key is a hash ref with following elements:

 header     : the header text for the field in the final report
 sql        : the DB query in SQL; the parsed field datum which is the
              lookup key should be represented with a placeholder (C<?>):
              e.g., ... WHERE patient=? AND ...
 lookup_fld : the field name (as found in @fields) whose data will act as the 
              keys for the DB lookup

* C<@output_fields> 

is a list of fields (as short field names) desired in the output file, in desired order. This can be a mixture of fields names from among the input file types as parsed.

* C<%filters>

is a hash whose keys are the filetypes, and values are subroutine
refs that take one line from the input file, and return true if that
should be parsed, and false if it should be ignored.

* C<%na_text>

is a hash whose keys are filetypes, values are hashrefs keyed by the filetype's
fields, with values containing the text that should be reported when a N/A entry
for that field is encountered.

* C<$default_dsn>, C<$default_user>, C<$default_pwd>

are the respective defaults used for accessing the database for lookups.

* $TOO_MANY

contains the number of non-matching regexp searches allowed before the
script throws up its hands and expires. When this happens, it should mean
that an incorrect input filetype was used as input.

=back 

=AUTHOR - Mark A. Jensen

 Email: jensenma -at- mail -dot- nih -dot- gov

=cut

use strict;
use warnings;
use base qw(Exporter);

our @EXPORT = qw( %config %filters %fields @db_fields
                  @output_fields
                  $default_dsn $default_user $default_pwd
                  $TOO_MANY $VERSION);

our (%fields, %filters, %config, @output_fields, @db_fields);

# set some defaults here, particularly the dsn/user/pass info
our $VERSION = 0.02;

our $TOO_MANY = 10;
# default dsn
my ($dbname,$host,$port) =
    qw( atlas cbiodb560.nci.nih.gov 5456);
# default user/pwd
our ($default_user, $default_pwd) = qw( tcgaread read298 );
our $default_dsn = "DBI:Pg:dbname=$dbname;host=$host;port=$port";

1;
