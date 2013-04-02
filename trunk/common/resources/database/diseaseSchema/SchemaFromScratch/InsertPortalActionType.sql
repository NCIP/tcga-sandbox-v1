SET DEFINE OFF;
Insert into PORTAL_ACTION_TYPE
   (PORTAL_ACTION_TYPE_ID, NAME, DESCR, PORTAL_ACTION_TYPE_PARENT)
 Values
   (1, 'data access matrix requested', 'user requested the Data Access Matrix page for the given disease type', NULL);
Insert into PORTAL_ACTION_TYPE
   (PORTAL_ACTION_TYPE_ID, NAME, DESCR, PORTAL_ACTION_TYPE_PARENT)
 Values
   (2, 'data access matrix reset', 'user reset the data access matrix using the reset button', NULL);
Insert into PORTAL_ACTION_TYPE
   (PORTAL_ACTION_TYPE_ID, NAME, DESCR, PORTAL_ACTION_TYPE_PARENT)
 Values
   (3, 'header selected', 'user selected the header with the given name', NULL);
Insert into PORTAL_ACTION_TYPE
   (PORTAL_ACTION_TYPE_ID, NAME, DESCR, PORTAL_ACTION_TYPE_PARENT)
 Values
   (4, 'header intersected', 'user selected to intersect the header with the given name', NULL);
Insert into PORTAL_ACTION_TYPE
   (PORTAL_ACTION_TYPE_ID, NAME, DESCR, PORTAL_ACTION_TYPE_PARENT)
 Values
   (5, 'header deselected', 'user deselected the header with the given name', NULL);
Insert into PORTAL_ACTION_TYPE
   (PORTAL_ACTION_TYPE_ID, NAME, DESCR, PORTAL_ACTION_TYPE_PARENT)
 Values
   (6, 'header category selected', 'user selected a header of the given category', 3);
Insert into PORTAL_ACTION_TYPE
   (PORTAL_ACTION_TYPE_ID, NAME, DESCR, PORTAL_ACTION_TYPE_PARENT)
 Values
   (7, 'header category intersected', 'user selected to intersect a header of the given category', 4);
Insert into PORTAL_ACTION_TYPE
   (PORTAL_ACTION_TYPE_ID, NAME, DESCR, PORTAL_ACTION_TYPE_PARENT)
 Values
   (8, 'header category deselected', 'user deselected a header of the given category', 5);
Insert into PORTAL_ACTION_TYPE
   (PORTAL_ACTION_TYPE_ID, NAME, DESCR, PORTAL_ACTION_TYPE_PARENT)
 Values
   (9, 'header type selected', 'user selected a header of the given type', 3);
Insert into PORTAL_ACTION_TYPE
   (PORTAL_ACTION_TYPE_ID, NAME, DESCR, PORTAL_ACTION_TYPE_PARENT)
 Values
   (10, 'header type intersected', 'user selected to intersect a header of the given type', 4);
Insert into PORTAL_ACTION_TYPE
   (PORTAL_ACTION_TYPE_ID, NAME, DESCR, PORTAL_ACTION_TYPE_PARENT)
 Values
   (11, 'header type deselected', 'user deselected a header of the given type', 5);
Insert into PORTAL_ACTION_TYPE
   (PORTAL_ACTION_TYPE_ID, NAME, DESCR, PORTAL_ACTION_TYPE_PARENT)
 Values
   (12, 'filter applied', 'user applied a filter to the data access matrix', NULL);
Insert into PORTAL_ACTION_TYPE
   (PORTAL_ACTION_TYPE_ID, NAME, DESCR, PORTAL_ACTION_TYPE_PARENT)
 Values
   (13, 'availability filter applied', 'user applied an availability filter to the data access matrix with the given value', 12);
Insert into PORTAL_ACTION_TYPE
   (PORTAL_ACTION_TYPE_ID, NAME, DESCR, PORTAL_ACTION_TYPE_PARENT)
 Values
   (14, 'batch filter applied', 'user applied a batch filter to the data access matrix with the given value', 12);
Insert into PORTAL_ACTION_TYPE
   (PORTAL_ACTION_TYPE_ID, NAME, DESCR, PORTAL_ACTION_TYPE_PARENT)
 Values
   (15, 'center filter applied', 'user applied a center filter to the data access matrix with the given value', 12);
Insert into PORTAL_ACTION_TYPE
   (PORTAL_ACTION_TYPE_ID, NAME, DESCR, PORTAL_ACTION_TYPE_PARENT)
 Values
   (16, 'level filter applied', 'user applied a level filter to the data access matrix with the given value', 12);
Insert into PORTAL_ACTION_TYPE
   (PORTAL_ACTION_TYPE_ID, NAME, DESCR, PORTAL_ACTION_TYPE_PARENT)
 Values
   (17, 'platform type filter applied', 'user applied a platform type filter to the data access matrix with the given value', 12);
Insert into PORTAL_ACTION_TYPE
   (PORTAL_ACTION_TYPE_ID, NAME, DESCR, PORTAL_ACTION_TYPE_PARENT)
 Values
   (18, 'protected status filter applied', 'user applied a protected status filter to the data access matrix with the given value', 12);
Insert into PORTAL_ACTION_TYPE
   (PORTAL_ACTION_TYPE_ID, NAME, DESCR, PORTAL_ACTION_TYPE_PARENT)
 Values
   (19, 'sample filter applied', 'user applied a sample filter to the data access matrix with the given value', 12);
Insert into PORTAL_ACTION_TYPE
   (PORTAL_ACTION_TYPE_ID, NAME, DESCR, PORTAL_ACTION_TYPE_PARENT)
 Values
   (20, 'tumor/normal filter applied', 'user applied a tumor/normal filter to the data access matrix with the given value', 12);
Insert into PORTAL_ACTION_TYPE
   (PORTAL_ACTION_TYPE_ID, NAME, DESCR, PORTAL_ACTION_TYPE_PARENT)
 Values
   (21, 'filter cleared', 'user cleared the filter from the data access matrix', NULL);
Insert into PORTAL_ACTION_TYPE
   (PORTAL_ACTION_TYPE_ID, NAME, DESCR, PORTAL_ACTION_TYPE_PARENT)
 Values
   (22, 'color scheme changed', 'user changed the data access matrix color scheme to the given name', NULL);
Insert into PORTAL_ACTION_TYPE
   (PORTAL_ACTION_TYPE_ID, NAME, DESCR, PORTAL_ACTION_TYPE_PARENT)
 Values
   (23, 'data access download page requested', 'user requested the data access download page with the given number of cells selected', NULL);
Insert into PORTAL_ACTION_TYPE
   (PORTAL_ACTION_TYPE_ID, NAME, DESCR, PORTAL_ACTION_TYPE_PARENT)
 Values
   (24, 'files selected', 'user selected to download the given number of files', NULL);
Insert into PORTAL_ACTION_TYPE
   (PORTAL_ACTION_TYPE_ID, NAME, DESCR, PORTAL_ACTION_TYPE_PARENT)
 Values
   (25, 'level 1 files selected', 'user selected to download the given number of level 1 files', 24);
Insert into PORTAL_ACTION_TYPE
   (PORTAL_ACTION_TYPE_ID, NAME, DESCR, PORTAL_ACTION_TYPE_PARENT)
 Values
   (26, 'level 2 files selected', 'user selected to download the given number of level 2 files', 24);
Insert into PORTAL_ACTION_TYPE
   (PORTAL_ACTION_TYPE_ID, NAME, DESCR, PORTAL_ACTION_TYPE_PARENT)
 Values
   (27, 'level 3 files selected', 'user selected to download the given number of level 3 files', 24);
Insert into PORTAL_ACTION_TYPE
   (PORTAL_ACTION_TYPE_ID, NAME, DESCR, PORTAL_ACTION_TYPE_PARENT)
 Values
   (28, 'clinical files selected', 'user selected to download the given number of clinical files', 24);
Insert into PORTAL_ACTION_TYPE
   (PORTAL_ACTION_TYPE_ID, NAME, DESCR, PORTAL_ACTION_TYPE_PARENT)
 Values
   (29, 'protected files selected', 'user selected to download the given number of protected files', 24);
Insert into PORTAL_ACTION_TYPE
   (PORTAL_ACTION_TYPE_ID, NAME, DESCR, PORTAL_ACTION_TYPE_PARENT)
 Values
   (30, 'public files selected', 'user selected to download the given number of public files', 24);
Insert into PORTAL_ACTION_TYPE
   (PORTAL_ACTION_TYPE_ID, NAME, DESCR, PORTAL_ACTION_TYPE_PARENT)
 Values
   (31, 'calculated archive size', 'user requested an archive with the given calculated size', NULL);
Insert into PORTAL_ACTION_TYPE
   (PORTAL_ACTION_TYPE_ID, NAME, DESCR, PORTAL_ACTION_TYPE_PARENT)
 Values
   (32, 'archive creation queued', 'archive creation job added to the queue', NULL);
Insert into PORTAL_ACTION_TYPE
   (PORTAL_ACTION_TYPE_ID, NAME, DESCR, PORTAL_ACTION_TYPE_PARENT)
 Values
   (33, 'archive creation started', 'archive creation job started', NULL);
Insert into PORTAL_ACTION_TYPE
   (PORTAL_ACTION_TYPE_ID, NAME, DESCR, PORTAL_ACTION_TYPE_PARENT)
 Values
   (34, 'archive creation finished', 'archive creation job finished with no errors', NULL);
Insert into PORTAL_ACTION_TYPE
   (PORTAL_ACTION_TYPE_ID, NAME, DESCR, PORTAL_ACTION_TYPE_PARENT)
 Values
   (35, 'archive creation failed', 'archive creation job failed with the given message', NULL);
Insert into PORTAL_ACTION_TYPE
   (PORTAL_ACTION_TYPE_ID, NAME, DESCR, PORTAL_ACTION_TYPE_PARENT)
 Values
   (36, 'uncompressed archive size', 'archive created of the given uncompressed size', NULL);
Insert into PORTAL_ACTION_TYPE
   (PORTAL_ACTION_TYPE_ID, NAME, DESCR, PORTAL_ACTION_TYPE_PARENT)
 Values
   (37, 'compressed archive size', 'archive created of the given compressed size', NULL);
Insert into PORTAL_ACTION_TYPE
   (PORTAL_ACTION_TYPE_ID, NAME, DESCR, PORTAL_ACTION_TYPE_PARENT)
 Values
   (38, 'total archive creation time', 'time in milliseconds it took to run the file packager, not counting queue time', NULL);
Insert into PORTAL_ACTION_TYPE
   (PORTAL_ACTION_TYPE_ID, NAME, DESCR, PORTAL_ACTION_TYPE_PARENT)
 Values
   (39, 'waiting in queue time', 'time in milliseconds the job waiting in the queue before being run', NULL);
Insert into PORTAL_ACTION_TYPE
   (PORTAL_ACTION_TYPE_ID, NAME, DESCR, PORTAL_ACTION_TYPE_PARENT)
 Values
   (40, 'file processing time', 'time in milliseconds it took to process the files before generating the archive, including generating level 2 and 3 files.', 38);
Insert into PORTAL_ACTION_TYPE
   (PORTAL_ACTION_TYPE_ID, NAME, DESCR, PORTAL_ACTION_TYPE_PARENT)
 Values
   (41, 'archive generation time', 'time in milliseconds it took to generate the actual archive tar.gzip file, not including file processing time.', 38);
Insert into PORTAL_ACTION_TYPE
   (PORTAL_ACTION_TYPE_ID, NAME, DESCR, PORTAL_ACTION_TYPE_PARENT)
 Values
   (42, 'end date filter applied', 'user applied an end date filter to the data access matrix with the given value', 12);
Insert into PORTAL_ACTION_TYPE
   (PORTAL_ACTION_TYPE_ID, NAME, DESCR, PORTAL_ACTION_TYPE_PARENT)
 Values
   (43, 'start date filter applied', 'user applied a start date filter to the data access matrix with the given value', 12);
Insert into PORTAL_ACTION_TYPE
   (PORTAL_ACTION_TYPE_ID, NAME, DESCR, PORTAL_ACTION_TYPE_PARENT)
 Values
   (45, 'platform filter applied', 'user applied a platform filter to the data access matrix with the given value', 12);
Insert into PORTAL_ACTION_TYPE
   (PORTAL_ACTION_TYPE_ID, NAME, DESCR, PORTAL_ACTION_TYPE_PARENT)
 Values
   (44, 'metadata files selected', NULL, NULL);
COMMIT;
