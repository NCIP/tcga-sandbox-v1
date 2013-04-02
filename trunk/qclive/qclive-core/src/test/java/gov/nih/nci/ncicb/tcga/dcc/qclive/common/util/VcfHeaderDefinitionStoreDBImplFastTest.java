package gov.nih.nci.ncicb.tcga.dcc.qclive.common.util;

import gov.nih.nci.ncicb.tcga.dcc.qclive.bean.VcfFileHeader;
import gov.nih.nci.ncicb.tcga.dcc.qclive.dao.VcfHeaderDefinitionQueries;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JMock;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

/**
 * Test for VcfHeaderDefinitionStoreDBImpl
 *
 * @author waltonj
 *         Last updated by: $Author$
 * @version $Rev$
 */
@RunWith(JMock.class)
public class VcfHeaderDefinitionStoreDBImplFastTest {
    private final Mockery context = new JUnit4Mockery();
    private VcfHeaderDefinitionQueries mockVcfHeaderDefinitionQueries;

    private VcfHeaderDefinitionStoreDBImpl vcfHeaderDefinitionStore;

    @Before
    public void setup() {
        mockVcfHeaderDefinitionQueries = context.mock(VcfHeaderDefinitionQueries.class);
        vcfHeaderDefinitionStore = new VcfHeaderDefinitionStoreDBImpl();
        vcfHeaderDefinitionStore.setVcfHeaderDefinitionQueries(mockVcfHeaderDefinitionQueries);
    }

    @Test
    public void testGetDefinition() {
        final VcfFileHeader header = new VcfFileHeader("A");
        context.checking(new Expectations() {{
            one(mockVcfHeaderDefinitionQueries).getHeaderDefinition("A", "B");
            will(returnValue(header));
        }});

        assertEquals(header, vcfHeaderDefinitionStore.getHeaderDefinition("A", "B"));
    }

    @Test
    public void testGetDefinitionNull() {
        context.checking(new Expectations() {{
            one(mockVcfHeaderDefinitionQueries).getHeaderDefinition("C", "D");
            will(returnValue(null));
        }});

        assertNull(vcfHeaderDefinitionStore.getHeaderDefinition("C", "D"));
    }


}
