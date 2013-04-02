# -*-perl-*-

use Test::More 'no_plan';
use strict;
use warnings;

use lib '../lib';
BEGIN{
    use_ok( 'TCGA::DBRO' );
    use_ok( 'TCGA::DBRO::Util' );
}

my @x = (0.1, .5,1,2,3,4,5,6 );
my @right_scores = qw( -1.1985622 -1.0141680 -0.7836753 -0.3226898  0.1382956  0.5992811  1.0602665 1.5212520); # as calculated in R by (x - mean(x))/sd(x)

is_deeply( [map { sprintf("%.7f",$_) } @{z_scores(\@x)}], \@right_scores );

1;
