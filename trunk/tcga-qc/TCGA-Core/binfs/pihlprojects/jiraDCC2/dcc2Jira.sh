#!/bin/sh
export PERL5LIB=$PERL5LIB:/h1/jensenma/lib/perl5:/h1/jensenma/lib64/perl5:/h1/jensenma/lib/perl5/site_perl:/h1/jensenma/lib64/perl5/site_perl
DATE=$(date +"%m-%d-%Y")
perl /h1/pihltd/pihlprojects/jiraDCC2/ticketDumper.pl
