use strict;

my $file = shift @ARGV or die "You forgot the file argument? Awesome.\n";
my $subjFile = shift @ARGV or die "Second arg should be the dbGap subjects file...\n";

# read in subjects file and make a map from patient ID to disease
my %patientDiseases;
open(SUBJ, $subjFile) or die "Can't open $subjFile\n";
while (<SUBJ>) {
  s/\n//;
  s/\r//;
  my @line = split("\t");
  $patientDiseases{$line[0]} = $line[1];
}
close SUBJ;

# now go through input file and for each line, get patient (4th column), lookup disease, add disease to the line, output into _with_disease.txt
open(IN, $file);
my $outname = $file . '_with_disease.txt';
open(OUT, ">$outname") or die "Can't open $outname for writing\n";
my $header = <IN>;
$header =~ s/\n//;
$header =~ s/\r//;
print OUT "$header\tDisease\n";
while (<IN>) {
  s/\r//;
  s/\n//;
  my @line = split("\t");
  my $patient = $line[3];
  my $disease = $patientDiseases{$patient};
  if (! defined $disease) {
    print "Could not find disease for $patient!\n";
  }
  print OUT $_;
  print OUT "\t";
  print OUT $disease;
  print OUT "\n";
}
close IN;
close OUT;

