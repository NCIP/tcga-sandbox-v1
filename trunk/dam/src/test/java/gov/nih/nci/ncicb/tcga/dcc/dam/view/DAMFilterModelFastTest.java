package gov.nih.nci.ncicb.tcga.dcc.dam.view;

import gov.nih.nci.ncicb.tcga.dcc.dam.bean.DataSet;
import gov.nih.nci.ncicb.tcga.dcc.dam.view.request.FilterRequest;
import gov.nih.nci.ncicb.tcga.dcc.dam.view.request.FilterRequestI;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JMock;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

import static gov.nih.nci.ncicb.tcga.dcc.dam.dao.DataAccessMatrixQueries.SHOW_COMPLETE_ROWS_ONLY;
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * @author bertondl
 *         Last updated by: $Author$
 * @version $Rev$
 */

@RunWith(JMock.class)
public class DAMFilterModelFastTest {
    private Mockery context = new JUnit4Mockery();
    private Map<String, Cell> filteredCells;
    private Map<String, Header> filteredHeaders;
    private Cell cell1, cell2, cell3, cell4;
    private Header header1, header2, header3, header4, header5, centerHeader, platformTypeHeader;
    private DAMFilterModel damFilterModel;
    private DAMModel wrappedModel;
    private FilterRequest filterRequest;

    @Before
    public void setUp() throws Exception {
        wrappedModel = context.mock(DAMModel.class);
        damFilterModel = new DAMFilterModel(wrappedModel) {

            @Override
            public void setFilterRequest(FilterRequestI filterRequest) {
                this.filterRequest = filterRequest;
            }
        };
        filterRequest = new FilterRequest();
        cell1 = makeCell("1", 11, 1, "A");
        cell2 = makeCell("2", 11, 1, "N");
        cell3 = makeCell("3", 22, 2, "A");
        cell4 = makeCell("4", 22, 2, "A");
        filteredCells = new HashMap<String, Cell>();
        filteredHeaders = new HashMap<String, Header>();
        platformTypeHeader = makeHeader(98, Header.HeaderCategory.PlatformType, "1", null, Header.HeaderType.COL_HEADER);
        centerHeader = makeHeader(99, Header.HeaderCategory.Center, "1", platformTypeHeader, Header.HeaderType.COL_HEADER);
        header1 = makeHeader(1, Header.HeaderCategory.Batch, "1", null, Header.HeaderType.ROW_HEADER);
        header2 = makeHeader(2, Header.HeaderCategory.Level, "1", centerHeader, Header.HeaderType.COL_HEADER);
        header3 = makeHeader(3, Header.HeaderCategory.Level, "2", centerHeader, Header.HeaderType.COL_HEADER);
        header4 = makeHeader(4, Header.HeaderCategory.Sample, "TCGA-01-1234-23", header1, Header.HeaderType.ROW_HEADER);
        header5 = makeHeader(5, Header.HeaderCategory.Sample, "TCGA-01-5678-78", header1, Header.HeaderType.ROW_HEADER);
        filteredCells.put("1", cell1);
        filteredCells.put("2", cell2);
        filteredCells.put("3", cell3);
        filteredCells.put("4", cell4);
        filteredHeaders.put("header1", header1);
        filteredHeaders.put("header2", header2);
        filteredHeaders.put("header3", header3);
        filteredHeaders.put("header4", header4);
        filteredHeaders.put("header5", header5);
        filterRequest.setShowCompleteRowsOnly(SHOW_COMPLETE_ROWS_ONLY);
        filterRequest.setMode(FilterRequestI.Mode.NoOp);
        damFilterModel.setFilterRequest(filterRequest);
        damFilterModel.setFilteredCells(filteredCells);
        damFilterModel.setFilteredHeaders(filteredHeaders);

    }

    @Test
    public void testProcessShowCompleteRowsOnly() throws Exception {
        context.checking(new Expectations() {{
            allowing(wrappedModel).getHeadersForCategory(Header.HeaderCategory.Level);
            will(returnValue(new LinkedList() {{
                add(header2);
                add(header3);
            }}));
            one(wrappedModel).getTotalBatches();
            will(returnValue(1));
            one(wrappedModel).getBatchHeader(0);
            will(returnValue(header1));
            one(wrappedModel).getCellsForHeader(header4);
            will(returnValue(new LinkedList() {{
                add(cell1);
                add(cell2);
            }}));
            one(wrappedModel).getCellsForHeader(header5);
            will(returnValue(new LinkedList() {{
                add(cell3);
                add(cell4);
            }}));
        }});
        Map<String, Cell> completeRowsCells = new HashMap<String, Cell>();
        Map<String, Header> completeRowsHeaders = new HashMap<String, Header>();
        damFilterModel.processShowCompleteRowsOnly(completeRowsCells, completeRowsHeaders);
        assertNotNull(completeRowsCells);
        assertEquals(2, completeRowsCells.size());
        assertTrue(completeRowsCells.containsValue(cell3));
        assertTrue(completeRowsCells.containsValue(cell4));
        assertFalse(completeRowsCells.containsValue(cell1));
        assertFalse(completeRowsCells.containsValue(cell2));
    }

    private Cell makeCell(final String id, final int row, final int col, final String availability) {
        final Cell cell = new Cell();
        final DataSet dataset = new DataSet();
        dataset.setAvailability(availability);
        cell.setId(id);
        cell.setRow(row);
        cell.setCol(col);
        cell.addDataset(dataset);
        return cell;
    }

    private Header makeHeader(final int id, final Header.HeaderCategory category, final String name,
                              final Header parent, final Header.HeaderType headerType) {
        final Header header = new Header();
        header.setId(id);
        header.setHeaderType(headerType);
        header.setCategory(category);
        header.setName(name);
        header.setParentHeader(parent);
        if (parent != null) {
            parent.getChildHeaders().add(header);
        }
        return header;
    }
}
