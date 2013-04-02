##############################################################################################################
# This script will read in a file of annotations and insert them into annotation, annotation item 
# and annotation_note tables in the dccCommon schema in the database specified by the -d option and
# from the file specified by the -f option
#
#  usage: perl AddBulkAnnotations.pl -d dbname -f filename
#
#  Written by $Author: alonsos
#
#  Modification History
#
#  Last update by: $Author: alonsos
#
##############################################################################################################
use strict;
use DBI;
use Cwd qw(realpath);
use Getopt::Long;
$Getopt::Long::autoabbrev = 0;

my $fullpath = realpath($0);
$fullpath =~ s/AddBulkAnnotations.pl//;

my $dbuser;
my $dbpwd;
my $db;
my $filename;
my $barcode;
my $annot_note;
my $catId;
my $itemId;
my $annotId;
my $diseaseId;
my $curFlag = 1;
my $enteredBy="DCC";
my $redact = "Redaction";
my $dbh;
my %STH = ();

# get options
setUp();

# get reference codes
my %itemTypes = ();
getItemTypes();
unless (%itemTypes)
{ 
	die "No Annotation Item Types found.";
}

my %itemCats = ();
getAnnotationCategories();
unless (%itemCats)
{ 
	die "No Annotation Categories found.";
}

my %diseases = ();
getDiseases();
unless (%diseases)
{
   die "No Diseases found.";
}
open(INPUT, $filename);
while(<INPUT>) {
  chomp;

  my @line = split(/\t/);
  $barcode = $line[0];
  my $disease = $line[1];
  my $annot_cat = $line[3];
  my $itemType = $line[2];
  $annot_note = $line[4];
  #print "barcode: $barcode, disease $disease,  annot_cat: $annot_cat, annot_item: $itemType, annot_note: $annot_note\n";
  $itemId = $itemTypes{$itemType};
  die "Cannot find item_type_id for $itemType \n" unless defined $itemId;
  $catId = $itemCats{$annot_cat};
  die "could not find annotation_category_id for $annot_cat \n" unless defined $catId;
  $diseaseId = $diseases{$disease};
  die "could not find disease_id for $disease \n" unless defined $diseaseId;
  $annotId = &loadAnnotation();
  die "annotation record not loaded for barcode $barcode \n" unless defined $annotId;
  &loadAnnotationItem;
  &loadAnnotationNote;
  if ($annot_cat =~ m/Redaction/) {
     #print "category: $annot_cat \n";
     &handleRedaction();
  }
}
close INPUT;

# commit and disconnect
$dbh->commit();
$dbh->disconnect();
print "Done.\n";

sub loadAnnotation() {
  my $new_id;
  # have to bind the params seperately because one is an inout param and calling execute with the
  # params inline (ie, execute(param,param,param) ) it automatically binds them all as input params
  
  $STH{loadAnnot}->bind_param(1,$catId);
  $STH{loadAnnot}->bind_param(2,$enteredBy);
  $STH{loadAnnot}->bind_param(3,$curFlag);
  $STH{loadAnnot}->bind_param_inout(4,\$new_id,38);
  $STH{loadAnnot}->execute() || die $DBI::errstr;
  return $new_id;
}

sub loadAnnotationItem() {
  $STH{loadAnnotItem}->execute($annotId, $itemId, $barcode, $diseaseId) || die $DBI::errstr;
}

sub loadAnnotationNote() {
  $STH{loadAnnotNote}->execute($annotId, $annot_note, $enteredBy) || die $DBI::errstr;
}

sub handleRedaction() {
  print "updating for redaction: $barcode \n";
  my $updated = $STH{setRedactedZero}->execute($barcode,$barcode) || die $DBI::errstr;
  print "updated rows: $updated \n";
}

sub getItemTypes() {
  my $selectItemTypes = "select item_type_id,type_display_name from annotation_item_type order by item_type_id";
  my $itemTypeId;
  my $typeName;

  my $selectItemTypes_sth = $dbh->prepare($selectItemTypes);
  $selectItemTypes_sth->execute();
	
  $selectItemTypes_sth->bind_columns( undef, \$itemTypeId, \$typeName );

  while ( $selectItemTypes_sth->fetch() ) {
    $itemTypes{$typeName} = $itemTypeId;
  }
}

sub getAnnotationCategories() {
  my $selectAnnotCategories = "select annotation_category_id, category_display_name from annotation_category";
  my $catId;
  my $catName;
  
  my $selectCategories_sth = $dbh->prepare($selectAnnotCategories);
  $selectCategories_sth->execute();

  $selectCategories_sth->bind_columns( undef, \$catId, \$catName);

  while ($selectCategories_sth->fetch() ) {
     $itemCats{$catName} = $catId;
  }
}

sub getDiseases() {
  my $selectDiseases = "select disease_id, disease_abbreviation from disease";
  my $id;
  my $abbrev;
  
  my $selectDiseases_sth = $dbh->prepare($selectDiseases);
  $selectDiseases_sth->execute();

  $selectDiseases_sth->bind_columns( undef, \$id, \$abbrev);

  while ($selectDiseases_sth->fetch() ) {
     $diseases{$abbrev} = $id;
  }
}

sub setUp() {

        my $help = 0;
	# get options - "d" is the only required option,
        &GetOptions('d=s' => \$db, 'f=s' => \$filename, 'help|?' => \$help);

        # see if the user wants syntax help
        if ( $help ) {
           print " Options:\n  Required: -d database -f filename \n   Usage:\n perl AddBulkAnnotations.pl -d database \n";
           exit;
        }
        die "Required: -d database -f filename \n" unless defined $db and defined $filename;

	if($db eq "tcgadev") {
	  $dbuser = "dcccommondev";
	  $dbpwd = "dcc58920dev";
	} elsif ($db eq "tcgaqa") {
	  $dbuser = "dcccommon";
	  $dbpwd = "dcc8294qa";
	} else {
	  $dbuser = "commonmaint";
	  if ($db eq "tcgastg") {
	    $dbpwd = "comm234hg";
	  } else {
	    $dbpwd = "comm7983ash";
	  }
	} 
	# database connection and insert statement setup
	$dbh = DBI->connect("DBI:Oracle:".$db, $dbuser, $dbpwd, { RaiseError => 1, AutoCommit => 0 } ) || die "ERROR: Cannot connect to the database" . $DBI::errstr;
	$STH{loadAnnot} = $dbh->prepare("insert into annotation(annotation_id,annotation_category_id,entered_by,entered_date,curated) values(annotation_seq.nextval, ?, ?, sysdate, ?) returning annotation_id into ?");
	$STH{loadAnnotItem} = $dbh->prepare("insert into annotation_item(annotation_item_id,annotation_id,item_type_id,annotation_item,disease_id) values(annotation_item_seq.nextval, ?, ?, ?, ?)");
	$STH{loadAnnotNote} = $dbh->prepare("insert into annotation_note(annotation_note_id,annotation_id,note,entered_by,entered_date) values(annotation_note_seq.nextval, ?, ?, ?, sysdate)");
	$STH{setRedactedZero} = $dbh->prepare("update biospecimen_breakdown_all set is_viewable = 0 where sample = ? or specific_patient = ?");
}

