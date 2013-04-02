/*
 * Software License, Version 1.0 Copyright 2009 SRA International, Inc.
 * Copyright Notice.  The software subject to this notice and license includes both human
 * readable source code form and machine readable, binary, object code form (the "caBIG
 * Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */

package gov.nih.nci.ncicb.tcga.dcc.dam.util;

import gov.nih.nci.ncicb.tcga.dcc.dam.bean.DataSet;
import gov.nih.nci.ncicb.tcga.dcc.dam.dao.DataAccessMatrixQueries;

import java.util.*;

/**
 * Utility class for filling in "gaps" in data set list for the DAM and for sorting data sets for the model.
 *
 * @author David Nassau
 *         Last updated by: $Author$
 * @version $Rev$
 */
public class DataSetSorterAndGapFiller implements DataSorterAndGapFillerI {

    /**
     *
     * @param datasets the datasets to sort
     * @param submittedSamples the set of all submitted samples
     */
    @Override
    public void sortAndFillGaps(List<DataSet> datasets, Set<String> submittedSamples) {
        fillGaps(datasets, submittedSamples);
        sort(datasets);
    }

    @Override
    public void sort(List<DataSet> datasets) {
        Collections.sort(datasets, new DataSetComparator());
    }

    @Override
    public void fillGaps(List<DataSet> datasets, Set<String> submittedSamples) {
        Map<String, List<DataSet>> cols = new HashMap<String, List<DataSet>>();
        Map<String, List<DataSet>> rows = new HashMap<String, List<DataSet>>();
        //find all col headers
        for (DataSet ds : datasets) {
            String key = ds.getPlatformTypeId() + "." + ds.getCenterId() + "." + ds.getPlatformId() + "." +
                    ds.getLevel() + "." + ds.getPlatformTypeSortOrder() + "." + ds.getPlatformAlias();
            List<DataSet> dsForCol = cols.get(key);
            if (dsForCol == null) {
                dsForCol = new ArrayList<DataSet>();
                cols.put(key, dsForCol);
            }
            dsForCol.add(ds);
        }
        //find all row headers
        for (DataSet ds : datasets) {
            String key = ds.getBatch() + "." + ds.getSample();
            List<DataSet> dsForRow = rows.get(key);
            if (dsForRow == null) {
                dsForRow = new ArrayList<DataSet>();
                rows.put(key, dsForRow);
            }
            dsForRow.add(ds);
        }
        //for each row/col combination, there should be a dataset. If not, create one
        for (String col : cols.keySet()) {
            for (String row : rows.keySet()) {
                DataSet intersection = findRowColIntersection(cols, rows, col, row);
                if (intersection == null) {
                    DataSet nullDS = new DataSet();
                    StringTokenizer st = new StringTokenizer(col, ".");
                    nullDS.setPlatformTypeId(st.nextToken());
                    nullDS.setCenterId(st.nextToken());
                    String platform = st.nextToken();
                    if (platform != null && !platform.equals("null")) {
                        nullDS.setPlatformId(platform);
                    }
                    nullDS.setLevel(st.nextToken());
                    nullDS.setPlatformTypeSortOrder(Integer.parseInt(st.nextToken()));
                    String platformAlias = st.nextToken();
                    if (platformAlias != null && !platformAlias.equals("null")) {
                        nullDS.setPlatformAlias(platformAlias);
                    }
                    st = new StringTokenizer(row, ".");
                    nullDS.setBatch(st.nextToken());
                    nullDS.setSample(st.nextToken());
                    String batch = nullDS.getBatch();
                    batch = (batch.startsWith("Batch ") ? batch.substring("Batch ".length()) : batch);
                    if (submittedSamples != null && submittedSamples.contains(nullDS.getCenterId() + "|" + batch + "|" + nullDS.getSample())) {
                        nullDS.setAvailability(DataAccessMatrixQueries.AVAILABILITY_NOTAVAILABLE);
                    } else {
                        nullDS.setAvailability(DataAccessMatrixQueries.AVAILABILITY_NOTAPPLICABLE);
                    }
                    datasets.add(nullDS);
                }
            }
        }
    }

    private DataSet findRowColIntersection(final Map<String, List<DataSet>> cols,
                                           final Map<String, List<DataSet>> rows, final String col,
                                           final String row) {
        DataSet intersection = null;
        for (DataSet ds1 : cols.get(col)) {
            for (DataSet ds2 : rows.get(row)) {
                if (ds1 == ds2) {
                    intersection = ds1;
                    break;
                }
                if (intersection != null) {
                    break;
                }
            }
        }
        return intersection;
    }

    class DataSetComparator implements Comparator<DataSet> {

        public int compare(final DataSet ds1, final DataSet ds2) {
            int ret;
            ret = compareIntValues(ds1.getPlatformTypeSortOrder(), ds2.getPlatformTypeSortOrder());
            if (ret == 0) {
                ret = compareValues(ds1.getCenterId(), ds2.getCenterId());
                if (ret == 0) {
                    ret = compareValues(ds1.getPlatformId(), ds2.getPlatformId());
                    if (ret == 0) {
                        ret = compareValues(ds1.getLevel(), ds2.getLevel());
                        if (ret == 0) {
                            ret = compareBatches(ds1, ds2);
                            if (ret == 0) {
                                //sample id comparison gets special treatment
                                ret = compareSampleBarcodes(ds1.getSample(), ds2.getSample());
                            }
                        }
                    }
                }
            }
            return ret;
        }

        private int compareValues(String val, String otherval) {
            if (val == null) {
                val = "";
            }
            if (otherval == null) {
                otherval = "";
            }
            return val.compareTo(otherval);
        }

        private int compareIntValues(int val, int otherval) {
            int ret;
            if (val > otherval) {
                ret = 1;
            } else if (val < otherval) {
                ret = -1;
            } else {
                ret = 0;
            }
            return ret;
        }

        private int compareSampleBarcodes(final String val, final String otherval) {
            //samples - compare the last 2, then the first 12 characters
            //So we group sample types
            int ret;
            if (val.length() == 15 && otherval.length() == 15) {
                final String sampleTypeCode = val.substring(13, 15);
                final String otherSampleTypeCode = otherval.substring(13, 15);
                ret = sampleTypeCode.compareTo(otherSampleTypeCode);
                if (ret == 0) {
                    final String patient = val.substring(0, 12);
                    final String ptherPatient = otherval.substring(0, 12);
                    ret = patient.compareTo(ptherPatient);
                }
            } else {
                // if strings are not expected length, then just do regular string comparison
                ret = val.compareTo(otherval);
            }

            return ret;
        }

        private int compareBatches(DataSet ds1, DataSet ds2) {
            int ret;
            int batchNo1 = ds1.getBatchNumber();
            int batchNo2 = ds2.getBatchNumber();
            if (batchNo1 > -1 && batchNo2 > -1) {
                //compare two batch numbers
                if (batchNo1 > batchNo2) {
                    ret = 1;
                } else if (batchNo1 < batchNo2) {
                    ret = -1;
                } else {
                    ret = 0;
                }
            } else if (batchNo1 > -1) {
                //put the one that's not numeric after
                ret = -1;
            } else if (batchNo2 > -1) {
                ret = 1;
            } else {
                //both not numeric, just compare strings
                ret = ds1.getBatch().compareTo(ds2.getBatch());
            }
            return ret;
        }
    }
}
