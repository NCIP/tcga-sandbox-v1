package gov.nih.nci.ncicb.tcga.dcc.common.bean;

import org.junit.Before;
import org.junit.Test;

import java.text.ParseException;
import java.util.List;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNull;

/**
 * Test for ShippedBiospecimen bean
 *
 * @author chenjw
 *         Last updated by: $Author$
 * @version $Rev$
 */
public class ShippedBiospecimenTest {
    private ShippedBiospecimen shippedBiospecimen;

    @Before
    public void setup() {
        shippedBiospecimen = new ShippedBiospecimen();
    }

    @Test
    public void testSetUuid() {
        ShippedBiospecimen shippedBiospecimen = new ShippedBiospecimen();
        shippedBiospecimen.setUuid(null);
        assertNull(shippedBiospecimen.getUuid());

        shippedBiospecimen.setUuid("abc");
        assertEquals("abc", shippedBiospecimen.getUuid());

        shippedBiospecimen.setUuid("ABC");
        assertEquals("abc", shippedBiospecimen.getUuid());


    }

    @Test
    public void testParseShippedPortion() throws ParseException {
        ShippedBiospecimen portion = ShippedBiospecimen.parseShippedPortionBarcode("TCGA-11-2222-33A-44-5555-66");
        assertEquals("TCGA", portion.getProjectCode());
        assertEquals("11", portion.getTssCode());
        assertEquals("2222", portion.getParticipantCode());
        assertEquals("33", portion.getSampleTypeCode());
        assertEquals("A", portion.getSampleSequence());
        assertEquals("44", portion.getPortionSequence());
        assertNull(portion.getAnalyteTypeCode());
        assertEquals("5555", portion.getPlateId());
        assertEquals("66", portion.getBcrCenterId());
    }

    @Test (expected = ParseException.class)
    public void testParseBadShippedPortion() throws ParseException {
        ShippedBiospecimen.parseShippedPortionBarcode("squirrel");
    }

    @Test
    public void testParseShippedPortionAlphaPlate() throws ParseException {
        ShippedBiospecimen shippedPortion = ShippedBiospecimen.parseShippedPortionBarcode("TCGA-11-2222-33A-44-BCDE-66");
        assertEquals("BCDE", shippedPortion.getPlateId());
    }


    @Test
    public void testGetShippedBiospecimenElements() {
        // make sure empty and not null if none
        assertEquals(0, shippedBiospecimen.getShippedBiospecimenElements().size());

        // add one that should add an element, should not have nulls of other elements
        shippedBiospecimen.setAnalyteTypeCode("ABC");
        List<ShippedBiospecimenElement> elements = shippedBiospecimen.getShippedBiospecimenElements();
        assertEquals(1, elements.size());
        assertEquals("ABC", elements.get(0).getElementValue());
        assertEquals(ShippedBiospecimenElement.SHIPPED_ELEMENT_TYPE_ANALYTE_TYPE_CODE, elements.get(0).getElementName());
    }

    @Test
    public void testSetAnalyteTypeCode() {
        // no NPE if not set...
        assertNull(shippedBiospecimen.getAnalyteTypeCode());

        // when set, can get value out
        shippedBiospecimen.setAnalyteTypeCode("hi");
        assertEquals("hi", shippedBiospecimen.getAnalyteTypeCode());
    }

    @Test
    public void testSetSampleTypeCode() {
        assertNull(shippedBiospecimen.getSampleTypeCode());

        shippedBiospecimen.setSampleTypeCode("123");
        assertEquals("123", shippedBiospecimen.getSampleTypeCode());
    }

    @Test
    public void testSetSampleSequence() {
        assertNull(shippedBiospecimen.getSampleSequence());

        shippedBiospecimen.setSampleSequence("xyz");
        assertEquals("xyz", shippedBiospecimen.getSampleSequence());
    }

    @Test
    public void testSetPortionSequence() {
        assertNull(shippedBiospecimen.getPortionSequence());

        shippedBiospecimen.setPortionSequence("cat");
        assertEquals("cat", shippedBiospecimen.getPortionSequence());
    }

    @Test
    public void testSetPlateId() {
        assertNull(shippedBiospecimen.getPlateId());

        shippedBiospecimen.setPlateId("blue");
        assertEquals("blue", shippedBiospecimen.getPlateId());
    }

    @Test
    public void testSetShippedBiospecimenId() {
        shippedBiospecimen.setSampleSequence("squirrel");
        shippedBiospecimen.setPortionSequence("platypus");
        shippedBiospecimen.setAnalyteTypeCode("emu");
        shippedBiospecimen.setPlateId("flounder");

        shippedBiospecimen.setShippedBiospecimenId(1000L);

        List<ShippedBiospecimenElement> elements = shippedBiospecimen.getShippedBiospecimenElements();
        for (final ShippedBiospecimenElement element : elements) {
            assertEquals(1000L, (long) element.getShippedBiospecimenId());
        }

    }
}
