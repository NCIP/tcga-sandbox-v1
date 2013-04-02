select archive_name,deploy_location
from archive_info
where date_added >= to_Date('02/06/2012','MM/DD/YYYY');