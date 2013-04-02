# Script requires 4 arguments.It expects oracle environment variables to be set in your
# environment: ORACLE_HOME, LD_LIBRARY_PATH
ARGS=4        
if [ $# -ne "$ARGS" ]
then
  echo "Usage: '$0' script_dir oracle_sid dbuser dbpwd"
  exit 
fi
# Env Parameters
 export SCRIPT_DIR=${1}
 export ORACLE_SID=${2}
 export PATH=$SCRIPT_DIR:$PATH
# get commmand line arguments
 db=${2}
 user=${3}
 pwd=${4}
#
echo $user
echo $db
echo $pwd

disease=`expr substr "$user" 5 8`
maintuser=$disease"maint"
echo $disease
echo $maintuser
# set flags
# exit on error
set -e on
set -v on

echo "Starting table changes "
sqlplus $user/$pwd@$db < ClinicalTableChanges.sql > ClinicalTableChanges.out 
echo "Completed table changes"

echo "Starting base data inserts "
sqlplus $user/$pwd@$db < InsertClinicalMetaData.sql > InsertClinicalMetaData.out 
echo "Completed base data inserts"

echo "Starting add uuid to hyb ref"
sqlplus $user/$pwd@$db << ENDOFSQL >> AlterHybRef.log

ALTER TABLE hybridization_ref ADD uuid VARCHAR2(36);
grant all on ALIQUOT to $maintuser;
grant all on ALIQUOT_ELEMENT to $maintuser;
grant all on ANALYTE to $maintuser;
grant all on ANALYTE_ELEMENT to $maintuser;
grant all on DNA to $maintuser;
grant all on DNA_ELEMENT to $maintuser;
grant all on DRUG_INTGEN to $maintuser;
grant all on DRUG_INTGEN_ELEMENT to $maintuser;
grant all on EXAMINATION to $maintuser;
grant all on EXAMINATION_ELEMENT to $maintuser;
grant all on GBMSLIDE to $maintuser;
grant all on GBM_PATHOLOGY to $maintuser;
grant all on LUNG_PATHOLOGY to $maintuser;
grant all on OVARIAN_PATHOLOGY to $maintuser;
grant all on PATIENT to $maintuser;
grant all on PATIENT_ELEMENT to $maintuser;
grant all on PORTION to $maintuser;
grant all on PORTION_ELEMENT to $maintuser;
grant all on PROTOCOL to $maintuser;
grant all on PROTOCOL_ELEMENT to $maintuser;
grant all on RADIATION to $maintuser;
grant all on RADIATION_ELEMENT to $maintuser;
grant all on RNA to $maintuser;
grant all on RNA_ELEMENT to $maintuser;
grant all on SAMPLE to $maintuser;
grant all on SAMPLE_ELEMENT to $maintuser;
grant all on SLIDE to $maintuser;
grant all on SLIDE_ELEMENT to $maintuser;
grant all on SURGERY to $maintuser;
grant all on SURGERY_ELEMENT to $maintuser;
grant all on TUMORPATHOLOGY to $maintuser;
grant all on TUMORPATHOLOGY_ELEMENT to $maintuser;
grant all on ALIQUOT_ARCHIVE to $maintuser;
grant all on ANALYTE_ARCHIVE to $maintuser;
grant all on DNA_ARCHIVE to $maintuser;
grant all on DRUG_INTGEN_ARCHIVE to $maintuser;
grant all on EXAMINATION_ARCHIVE to $maintuser;
grant all on PATIENT_ARCHIVE to $maintuser;
grant all on PORTION_ARCHIVE to $maintuser;
grant all on PROTOCOL_ARCHIVE to $maintuser;
grant all on RADIATION_ARCHIVE to $maintuser;
grant all on RNA_ARCHIVE to $maintuser;
grant all on SAMPLE_ARCHIVE to $maintuser;
grant all on SURGERY_ARCHIVE to $maintuser;
grant all on SLIDE_ARCHIVE to $maintuser;
grant all on TUMORPATHOLOGY_ARCHIVE to $maintuser;
grant all on CLINICAL_FILE_TO_TABLE to $maintuser;
grant all on CLINICAL_FILE to $maintuser;
grant all on CLINICAL_FILE_ELEMENT to $maintuser;
grant all on CLINICAL_TABLE to $maintuser;
grant all on CLINICAL_XSD_ELEMENT to $maintuser;
grant all on CLINICAL_XSD_ENUM_VALUE to $maintuser;

ENDOFSQL



exit
