package GUS::Community::ParseGFF;
# $Id: ParseGFF.pm 17227 2012-06-29 00:52:55Z snyderee $

# ----------------------------------------------------------
# ParseGFF.pm
#
# A package to hold data from a GFF file
#
# Created:
#
# Original by Jian Lu, Aug 22, 2006 (PATRIC@VBI)
# 
# April 23, 2007, added parseFNA to process fna data
# 
# ----------------------------------------------------------

use strict;

# ----------------------------------------------------------
# use GUS::Community::ParseGFF;
#
# my $gff = GUS::Community::ParseGFF->new();
# my $data = $gff->parseGFF3($file);
# 
#
# ----------------------------------------------------------
sub new{
   my ($class, %args) = @_;

   my $self = {};
   bless $self, $class;
   return $self;
}

####################################################
# parse .fna format data and convert it to GFF3 format
#
#
sub parseFNA {
   my($self, $file,$fnum) = @_;
   $fnum=1 if(!$fnum); 
   my $new_fnum = $self->formatNumber($fnum);
   my $vbi_num = "VBI".$new_fnum;
   my $outFile = $file;
   my $grpFile = $file;
   if($outFile =~ /\.fna$/){
      $outFile =~ s/\.fna/\.gff3/g;
      $grpFile =~ s/\.fna/\.txt/g;
   }else{
      $outFile = $outFile.".gff3";
   }
   my @ids;
   my %hash;
   my %seq;
   my $line=0;
   print "Formatting file... for $file\n";
   open(IN, $file) || die "Can't open input file!\n";
   while(<IN>){
     chomp;
     ### skip comments
     next if (/^\#/);

     ### skip blank lines
     next if (/^\s*$/);
     my $gff3;
     my $name;
     my $length;
     my $other;
     if(/^>\w+/){
       ($name,$length,$other)=split(/\s+/,$_,3);
       $name =~ s/\>//g;
       $gff3->{name}=$name;
       if($length){
          my ($wd,$size)=split(/=/,$length);
          $gff3->{size}=$size;
       }
       $line++;
       $gff3->{line}=$line;
       push(@ids, $gff3);
     }elsif(/^>>(\w+|\d+)=(.*)/){
       $hash{$1}=$2;
     }else{
       $seq{$line} .= "$_\n";
     }
   }
   close(IN);

   open(OUT,">$outFile") || die "Can't write output file: $outFile!\n";
   print OUT "##gff-version   3\n";
   print OUT "##feature-ontology      so.obo\n";
   print OUT "##attribute-ontology    gff3_attributes.obo\n";

   open(GRP,">$grpFile") || die "Can't write output file: $grpFile!\n";
   print GRP "#Accession/NCBI_Taxon_Id/Genome Name/Genome Type/Group Name\n";
   print GRP "# data from $file\n";
   my $date = `date`;
   print GRP "# $date\n";
   my %gff3;
   my $count=0;
   foreach my $id (@ids){
      my $name = $id->{name};
      my $size = $id->{size};
      my $line = $id->{line};
      my $sequence = $seq{$line};
      $sequence =~ s/\s+//g;
      $sequence =~ s/\d+//g;
      my $length = length $sequence;
      $size = $length if(!$size);
      $count++;
      my $id;
      if($hash{accession}){
         $id = $hash{accession}.".".$hash{version};
      }else{
         my $new_name = $name;
         $new_name =~ s/\./_/g;
         $id = "$vbi_num"."_".$new_name.".".$hash{version};
      }
      $hash{ID}->{$line} = $id;
      print OUT "$id\t$hash{source}\tcontig\t1\t$size\t.\t+\t.\t";
      print OUT "ID=$id;Name=$name;";
      print OUT "Dbxref=taxon:$hash{taxon};molecule_type=$hash{molecule_type};Organism_name=$hash{organism_name};topology=$hash{topology};strain=$hash{strain}";
      if($hash{translation_table}){
         print OUT ";translation_table=$hash{translation_table}";
      }
      if($hash{segment}){
         print OUT ";segment=$line"; 
      }
      if($hash{localization}){
         print OUT ";localization=$hash{localization}";
      }
      print OUT "\n";
      print GRP "$id\t$hash{taxon}\t$hash{organism_name}\tAG\t$hash{group_name}\n";
      $gff3{$name}=$id;
   }
   close(GRP);

   print OUT "##FASTA\n";
   foreach my $id (@ids){
      my $name = $id->{name};
      my $size = $id->{size};
      my $line = $id->{line};
      my $sequence = $seq{$line};
      print OUT ">$hash{ID}->{$line}\n";
      print OUT $seq{$line}; 
   }
   close(OUT);
   return $outFile;
}

sub formatNumber{
    my ($self,$num)=@_;
    my $result;
    my $totalPos=5;
    for(my $i=0; $i<$totalPos-length($num); $i++){
        $result.="0";
    }
    return $result.$num;
}


sub parseGFF3{
   my($self,$file)=@_;
   my %data;
   my %seq;
   open(GFF,"$file") ||die "can not open the file: $file!\n";
   my $seqflag=0;
   my $id;
   my $count=0;
   my $count_id=0;
   while(<GFF>){
       chomp;

       ### skip comments
       next if (/^\#/);

       ### skip blank lines
       next if (/^\s*$/);
       if (/^\>(\S+)/) {
           $seqflag=1;
           $id = $1;
           next;
       }
       if(!$seqflag){
          my ($seqID,$sourceID,$featureName,$start,$end,$score,$strand,$frame,$attribute)=split(/\t/,$_,9);
          $seqID =~ s/\s+//g;
          $seqID =~ s/\%7C/\|/g if($seqID =~ /\%7C/);
          $sourceID =~ s/\s+//g;
          $featureName =~ s/\s+//g;
          $featureName = uc $featureName if($featureName eq 'cds');
          $start =~ s/\D+//g;
          $end =~ s/\D+//g; 
          $strand =~ s/\s+//g;
          
          # features
          next if(!$featureName);
          $count++;
          my $tagID = $featureName.".".$sourceID.".".$start."-".$end."-".$count;
          $tagID =~ s/\s+//g;
          my @att=split(/;/,$attribute);
          my $ec;
          foreach my $at (@att){
             my ($tag,$value)=split(/=/,$at);
             $tag =~ s/\s+//g;
             $count_id++ if($tag eq "ID");
             if($tag eq "Dbxref"){
                my @xref = split(/,/,$value);
                foreach my $ref (@xref){
                    my ($db_id,$val)=split(/:/,$ref);
                    next if($db_id eq "EC");
                    if($featureName eq "contig"){
                       $data{$seqID}->{contig}->{$db_id}=$val; 
                    }else{
                       $data{$seqID}->{feature}->{$featureName}->{$tagID}->{$db_id}=$val;
                    }
                    $data{$seqID}->{dbxref}->{$featureName}->{$tagID}->{$db_id}=$val;
                }
                foreach my $ref (@xref){
                    my ($db_id,$val)=split(/:/,$ref);
                    if($db_id eq "EC"){
                       $ec .= $val.","; 
                    }
                }
                if($ec){
                   chop $ec;
                   $data{$seqID}->{dbxref}->{$featureName}->{$tagID}->{EC}=$ec;
                   $data{$seqID}->{feature}->{$featureName}->{$tagID}->{EC}=$ec;
                }
             }
             if($featureName eq "contig"){
                 $data{$seqID}->{contig}->{$tag}=$value;
             }else{
                 $data{$seqID}->{feature}->{$featureName}->{$tagID}->{$tag}=$value;
             }
          }
          if($featureName eq "contig"){
              $data{$seqID}->{contig}->{seq_id}=$seqID;
              $data{$seqID}->{contig}->{source_id}=$sourceID;
              $data{$seqID}->{contig}->{start}=$start;
              $data{$seqID}->{contig}->{end}=$end;
              $data{$seqID}->{contig}->{score}=$score if($score ne ".");
              $data{$seqID}->{contig}->{strand}=$strand;
              $data{$seqID}->{contig}->{frame}=$frame; 
          }else{
              $data{$seqID}->{feature}->{$featureName}->{$tagID}->{seq_id}=$seqID;
              $data{$seqID}->{feature}->{$featureName}->{$tagID}->{source_id}=$sourceID;
              $data{$seqID}->{feature}->{$featureName}->{$tagID}->{feature_name}=$featureName;
              $data{$seqID}->{feature}->{$featureName}->{$tagID}->{start}=$start;
              $data{$seqID}->{feature}->{$featureName}->{$tagID}->{end}=$end;
              $data{$seqID}->{feature}->{$featureName}->{$tagID}->{score}=$score if($score ne ".");
              $data{$seqID}->{feature}->{$featureName}->{$tagID}->{strand}=$strand;
              $data{$seqID}->{feature}->{$featureName}->{$tagID}->{frame}=$frame;
          }
        }else{
          # sequences
          $seq{$id} .=$_;
        }
   }
   close(GFF);
   foreach my $seqID (keys %data) {
      if($data{$seqID}->{contig}){
          my $id =  $data{$seqID}->{contig}->{ID}; 
          $data{$seqID}->{sequence}->{$id}=$seq{$id};
      }
      foreach my $feat (keys %{$data{$seqID}->{feature}}){
          foreach my $tagID (keys %{$data{$seqID}->{feature}->{$feat}}){
             my $id =  $data{$seqID}->{feature}->{$feat}->{$tagID}->{ID};
             $data{$seqID}->{sequence}->{$id}=$seq{$id};
          }          
      }
   }
#   print "total ID: $count_id\n";
   return \%data;
}

1;
