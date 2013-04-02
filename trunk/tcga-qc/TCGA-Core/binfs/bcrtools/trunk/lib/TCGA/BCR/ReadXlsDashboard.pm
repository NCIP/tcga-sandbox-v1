package TCGA::BCR::ReadXlsDashboard;
use Spreadsheet::ParseExcel;
use Spreadsheet::XLSX;
use Set::Scalar;
use TCGA::BCR::DashboardConfig;
use strict;
use warnings;

# silence non-zip file errors (xlsx parsing)
Archive::Zip::setErrorHandler( sub { } );

sub new {
  my $class = shift;
  my $obj = bless {}, $class;
  if (@_) {
    $obj->parse(shift());
  }
  $obj;
}

sub parse {
  my $self = shift;
  my ($file) = @_;
  unless (-e $file) {
    $self->{_error} = $!;
    return;
  }
  eval {
    my $p = Spreadsheet::ParseExcel->new();
    $self->{_book} = $p->parse($file);
  };
  if ($@) {
    $self->{_error} = $@;
    return;
  }
  return 1 if $self->{_book};
  eval {
    no warnings;
    $self->{_book} = Spreadsheet::XLSX->new($file);
  };
  if ($@) {
    $self->{_error} = $@;
    return;
  }
  return 1 if $self->{_book};
}

sub get_table {
  my $self = shift;
  my ($table) = @_;
  $self->_get_table($table) unless $self->{_data}{$table};
  return $self->{_data}{$table};
}

sub get_headers {
  my $self = shift;
  my ($table) = @_;
  $self->_get_table($table) unless $self->{_data}{$table};
  return keys %{$self->{_data}{$table}} if ($self->{_data}{$table});
}

sub get_json_headers {
  my $self = shift;
  my ($table) = @_;
  return values %{$json_table_info{$table}->{hdr_table}};
}

sub get_valid_table_names {
  my $self = shift;
  return keys %json_table_info;
}
    

sub _get_table {
  my $self = shift;
  my ($table) = @_;
  my ($row0, $col0) = @{$self->_find_table($table)};
  return unless defined $row0 && defined $col0;

  my $info = $json_table_info{$table};
  my $sheet = $self->{_sheets}{$table};

  my @hdrs;
  # get headers
  for ( my $c = $col0;  my $cell = $sheet->get_cell($row0,$c) ; $c++) {
    my $value = $cell->value;
    $value =~ s/\s+/ /g;
    push @hdrs, $value;
  }
  # normalize hdrs to json names
  my @keys = keys %{$info->{hdr_table}};
  for (@hdrs) {
    my $key;
    foreach my $matcher (@keys) {
      if ( $_ =~ qr/$matcher/ ) {
	$key = $matcher;
	last;
      }
    }
    $_ = $key ? $info->{hdr_table}->{$key} : $_;
  }

  # check that all required json headers were converted
  {
    my $s = Set::Scalar->new(values %{$info->{hdr_table}});
    my $t = Set::Scalar->new(@hdrs);
    my @missing = ($s - $t)->members;
    my $m = $s-$t;
    my $o = Set::Scalar->new(@OPTIONAL);
    $self->{_error} = 'Cannot map the following json elements: '.join(' ',@missing) if @missing;
    return unless ($s <= $t) || ($m->is_subset($o));
  }
  my $data = {};
  # read table
 ROW:
  for ( my $r = $row0+1; 1 ; $r++) {
    last ROW unless $sheet->get_cell($r,$col0);
    # another stop condition ("Total" row reached)
    last ROW if $sheet->get_cell($r,$col0)->value =~ /total/i;
    # another stop condition (blank reached)
    last ROW if $sheet->get_cell($r,$col0)->unformatted() =~ /^$/;
    my @row_hdrs = @hdrs;
    COLUMN :
	for ( my $c = $col0; 1 ; $c++) {
	  my $cell = $sheet->get_cell($r, $c);
	  my $hdr = shift @row_hdrs;
	  last COLUMN unless $hdr;
	  my $value;
	  if (defined $cell) {
	      $value = $cell->value;
	      # strip leading and trailing space
	      $value =~ s/^\s+//;
	      $value =~ s/\s+//;
	      $value = 0 if $value eq '';
	    }
	  push @{$data->{$hdr}}, $value;
	}
      }

  $self->{_data}{$table} = $data;
  $self->{_error} = undef;
  return 1;
}

sub _find_table {
  my $self = shift;
  my ($table) = @_;
  return unless grep /$table/, keys %json_table_info;
  return $self->{_topleft}{$table} if $self->{_topleft}{$table};
  my $info = $json_table_info{$table};
  my $sheet_name = $self->_match_sheet($info->{worksheet});
  return unless $sheet_name;
  my $sheet = $self->book->worksheet($sheet_name);
  return unless $sheet;
  # assume table top left corner is in the first column
  # assume table name is in the first column and precedes the table
  my ($row_min, $row_max) = $sheet->row_range;
  my ($col_min, $col_max) = $sheet->col_range;
  my ($row_cursor,$col_cursor);
  for ($row_cursor=$row_min; $row_cursor<=$row_max;$row_cursor++) {
    my $cell = $sheet->get_cell($row_cursor,$col_min);
    next unless defined $cell;
    my $value = $cell->value;
    $value =~ s/\s+/ /g; # normalize whitespace to spaces
    last if ($value eq $info->{title});
  }
  return unless $row_cursor < $row_max; # not found
  for ($row_cursor++; $row_cursor<=$row_max;$row_cursor++) {
    my $cell = $sheet->get_cell($row_cursor,$col_min);
    next unless defined $cell;
    my $value = $cell->value;
    $value =~ s/\s+/ /g; # normalize whitespace to spaces
    last if ($value =~ qr/$info->{first_hdr}/);
  }
  return unless $row_cursor < $row_max; # not found
  $self->{_error} = undef;
  $self->{_sheets}{$table} = $sheet;
  return $self->{_topleft}{$table}=[$row_cursor, $col_min];
}

sub book { shift->{_book} }
sub error { shift->{_error} }

sub get_data {
  my $self = shift;
  my ($table) = @_;
  return unless $table;
  return $self->{_data}{$table};
}

sub get_sheet { 
  my $self = shift;
  my ($table) = @_;
  return unless $table;
  return $self->{_sheets}{$table};
}
sub _match_sheet {
  my $self = shift;
  my ($matcher) = @_;

  foreach my $sheet ($self->book->worksheets) {
    my $sheet_name = $sheet->get_name;
    return $sheet_name if $sheet_name =~ qr/$matcher/;
  }
  return;
}

=head1 NAME

TCGA::BCR::ReadXlsDashboard - Class to interpret BCR Excel Dashboards

=head1 SYNOPSIS

 $dash = TCGA::BCR::ReadXlsDashboard->new('dashboard.xlsx');
 $disease_summary = $dash->get_table('case_summary_by_disease');
 @tumors = @{$disease_summary->{'tumor_abbrev'}};
 @shipped = @{$disease_summary->{'shipped'}};
 @shipped_table_by_tumor{@tumors} = @shipped;
 printf "BRCA shipped: %d\n", $shipped_table_by_tumor{BRCA};

 @valid_table_names = $dash->get_valid_table_names;
 $workbook = $dash->book;
 $sheet = $dash->get_sheet('case_summary_by_disease');
 @json_tags = $dash->get_json_headers('case_summary_by_disease');

=head1 DESCRIPTION

L<TCGA::BCR::ReadXlsDashboard> provides a class to obtain selected
tables of BCR Dashboard information by reading a BCR's submitted Excel
file (either .xls or .xlsx), searching through the worksheets for the
table, and reading the table into a Perl reference (returned by
C<get_table>).

The table headings are normalized to the DCC JSON format names by
lookup tables contained in L<TCGA::BCR::DashboardConfig>.

=head1 AUTHOR

Mark A. Jensen (mark -dot- jensen -at- nih -dot- gov

=head1 COPYRIGHT

(c) 2012 SRA International, Inc.
Distributed under the terms of the caBIG v1.0 license

=cut

1;
