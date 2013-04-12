# This shell script requires 5 arguments. It expects oracle environment variables to be set in your
# environment: ORACLE_HOME, LD_LIBRARY_PATH
ARGS=6        # Script requires 6 arguments.
if [ $# -ne "$ARGS" ]
then
  echo "Usage: '$0' script_dir oracle_sid dbuser dbpwd DISEASE tier"
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
 disease=${5}
 tier=${6}
 common="dccCommon"
 echo "common schema is $common"
#
#echo "Starting table creation "
sqlplus $user/$pwd@$db < CreateDiseaseSchema.sql > CreatePortalSchema.out
#echo "Completed table creation"
# 
   echo "Starting grant creation "
   sqlplus $user/$pwd@$db < CreateGrants$disease.sql > CreateGrants$disease.out
   echo "Completed grant creation "


echo "Starting get reference data from dccCommon"
sqlplus $user/$pwd@$db << ENDOFSQL >> GetCommonRefData$disease.log

INSERT INTO $user.center_type SELECT * from $common.center_type;

-- insert center records from dccCommon center table
INSERT INTO $user.center SELECT center_id,domain_name,center_type_code,display_name,short_name,sort_order
FROM $common.center;

-- insert center_to_bcr_center records
INSERT INTO $user.center_to_bcr_center SELECT c.* from $common.center_to_bcr_center c;

-- get data_type records
INSERT INTO $user.data_type (data_type_id, name, center_type_code, ftp_display, available, sort_order)
SELECT data_type_id, name , center_type_code, ftp_display, available, sort_order 
FROM  $common.data_type;

-- get data_type_relationship records
INSERT INTO $user.data_type_relationship (group_data_type_id,data_type_id)
SELECT group_data_type_id,data_type_id)
FROM $common.data_type_relationship;

-- insert platform records from dccCommon that do not exist in platform, but only available platforms
INSERT INTO $user.platform (platform_id,  platform_name ,platform_display_name, platform_alias,sort_order, available, center_type_code,base_data_type_id)
SELECT p.platform_id,  p.platform_name ,p.platform_display_name, p.platform_alias,p.sort_order, p.available, p.center_type_code, p.base_data_type_id FROM
$common.platform p;

-- get visibility
INSERT INTO $user.visibility (visibility_id,visibility_name,identifiable)
SELECT visibility_id,visibility_name,identifiable FROM $common.visibility;

-- get data_visibility
INSERT INTO $user.Data_Visibility (data_visibility_id,Data_type_id, Level_number, Visibility_Id)
SELECT data_visibility_id,Data_type_id, Level_number, Visibility_Id FROM $common.data_visibility;

-- get data type related tables

INSERT INTO $user.data_level SELECT * from $common.data_level;

INSERT INTO $user.data_type_to_platform SELECT * FROM $common.data_type_to_platform;

-- get archive_type
INSERT INTO $user.archive_type SELECT * from $common.archive_type;

insert into anomaly_type(anomaly_type_id,anomaly,anomaly_description,value_threshold,value_threshold_type,display_name,patient_threshold,comments) values (4,'AMPLIFIED','Amplification of all or part of the gene.  Based on data from Harvard, Broad, MSKCC, and Stanford.',2.5,'GTE','AMP',.1,'');
insert into anomaly_type(anomaly_type_id,anomaly,anomaly_description,value_threshold,value_threshold_type,display_name,patient_threshold,comments) values (5,'DELETED','Deletion of all or part of the gene.  Based on data from Harvard, Broad, MSKCC, and Stanford.',1.5,'LTE','DEL',.1,'');
insert into anomaly_type(anomaly_type_id,anomaly,anomaly_description,value_threshold,value_threshold_type,display_name,patient_threshold,comments) values (1,'MUTATION','A validated, functional mutation (i.e. silent change is not counted here) exists in the gene of interest.  Data from submitted by the three GSC Centers',1,'GTE','MUTATION',.05,'');
insert into anomaly_type(anomaly_type_id,anomaly,anomaly_description,value_threshold,value_threshold_type,display_name,patient_threshold,comments) values (2,'OVEREXPRESSION, AFFYMETRIX','Overexpression of the gene is seen in Affymetrix 133A Microarray analysis.',2,'GTE','EXP+_AF',.25,'');
insert into anomaly_type(anomaly_type_id,anomaly,anomaly_description,value_threshold,value_threshold_type,display_name,patient_threshold,comments) values (3,'UNDEREXPRESSION, AFFYMETRIX','Underexpression of the gene is seen in Affymetrix 133A Microarray analysis.',.1,'LTE','EXP-_AF',.15,'');
COMMIT;

INSERT INTO shipped_item_type values (1,'Aliquot');
INSERT INTO shipped_item_type values (2,'Shipping Portion');
commit;


ENDOFSQL

#
echo "Starting big reference data "
sqlplus $user/$pwd@$db < InsertBigReferenceData.sql > InsertBigReferenceData.out 
echo "Completed insert big reference data"
#
echo "Starting disease-specific reference data "
sqlplus $user/$pwd@$db < Insert$disease.sql > Insert$disease.out 
echo "Completed insert disease-specific reference data"
