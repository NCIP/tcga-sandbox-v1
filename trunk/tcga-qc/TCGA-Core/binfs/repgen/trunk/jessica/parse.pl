use strict;

my $file = shift @ARGV;
open (IN, "$file") or die 'DED';
open (OUT, ">$file.clean.txt") or die 'OH NO';
open (OTHER, ">$file.other.txt");
print OUT "Received Date\tPublished Date\tAliquot Barcode\tPatient Barcode\tSample Barcode\tGSC\n";
while (<IN>) {
    chomp;
    my @line = split(/\t/);
    # received, published, barcode
    my $received = $line[1];
    if ($received =~ /^(\d\d\d\d\-\d\d\-\d\d)T/) {
        $received = $1;
    }
    my $published = $line[0];
    if ($published =~ /^(\d\d\d\d\-\d\d\-\d\d)T/) {
       $published = $1;
    }
    my $barcode = $line[2];
    $barcode =~ /^((TCGA-\d\d-\d\d\d\d)-\d\d).*(\d\d)$/;
    my $patient = $1;
    my $sample = $2;
    my $centerId = $3;
    my $gsc;
    if ($centerId eq '10') {
      $gsc = 'Baylor';
    } elsif ($centerId eq '08') {
       $gsc = 'Broad';
    } elsif ($centerId eq '09') {
       $gsc = 'WashU';
     } 
    if (defined $gsc) {
      print OUT "$received\t$published\t$barcode\t$sample\t$patient\t$gsc\n";
    } else {
      print OTHER "$received\t$published\t$barcode\t$sample\t$patient\t$centerId\n";
    }
}
close IN;
close OUT;



