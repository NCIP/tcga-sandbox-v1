-- $Id: retrieveGenericXMLbyFileID.sql 17921 2012-11-29 23:03:12Z snyderee $
-- $Revision: 17921 $
-- $Date: 2012-11-29 18:03:12 -0500 (Thu, 29 Nov 2012) $
------------------------------------------------------------------------------------------
select file_id, x.GENERIC_XML.getclobval() from GENERIC_XML x where file_id  = FILE_ID
