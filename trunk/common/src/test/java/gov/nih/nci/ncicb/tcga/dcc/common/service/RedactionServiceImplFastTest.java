/*
 * Software License, Version 1.0 Copyright 2011 SRA International, Inc.
 * Copyright Notice.  The software subject to this notice and license includes both human
 * readable source code form and machine readable, binary, object code form (the "caBIG
 * Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */

package gov.nih.nci.ncicb.tcga.dcc.common.service;

import gov.nih.nci.ncicb.tcga.dcc.common.dao.DBUnitTestCase;
import gov.nih.nci.ncicb.tcga.dcc.common.dao.RedactionQueries;
import gov.nih.nci.ncicb.tcga.dcc.common.dao.UUIDHierarchyQueries;
import gov.nih.nci.ncicb.tcga.dcc.common.dao.annotations.AnnotationQueries;
import gov.nih.nci.ncicb.tcga.dcc.common.util.CommonBarcodeAndUUIDValidator;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JMock;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;

import java.util.LinkedList;
import java.util.List;

/**
 * Test class for RedactionServiceImpl
 *
 * @author Shelley Alonso Last updated by: $Shelley Alonso$
 * @version $Rev$
 */

@RunWith (JMock.class)
public class RedactionServiceImplFastTest extends DBUnitTestCase {
    private Mockery context = new JUnit4Mockery();
    private RedactionServiceImpl redactionService;
    private RedactionQueries mockRedactionQueries;
    private CommonBarcodeAndUUIDValidator mockBarcodeUUIDValidator;
    private UUIDHierarchyQueries mockUUIDHierarchyQueries;
    private AnnotationQueries mockAnnotationQueries;
    private List<String> childUuidList;

    @Before
    public void setup() {

        mockRedactionQueries = context.mock(RedactionQueries.class);
        mockBarcodeUUIDValidator = context.mock(CommonBarcodeAndUUIDValidator.class);
        mockUUIDHierarchyQueries = context.mock(UUIDHierarchyQueries.class);
        mockAnnotationQueries = context.mock(AnnotationQueries.class);

        redactionService = new RedactionServiceImpl();
        redactionService.setRedactionQueriesCommon(mockRedactionQueries);
        redactionService.setRedactionQueriesDisease(mockRedactionQueries);
        redactionService.setBarcodeAndUUIDValidator(mockBarcodeUUIDValidator);
        redactionService.setUuidHierarchyQueries(mockUUIDHierarchyQueries);
        redactionService.setAnnotationQueries(mockAnnotationQueries);
        
        childUuidList = new LinkedList<String>() {{
            add("uuid1");
            add("uuid2");
        }};
    }

    @Test
    public void testGetItemTypeGood() throws Exception {
        context.checking(new Expectations() {{
            one(mockBarcodeUUIDValidator).validateAnyBarcodeFormat("TCGA-13-1817");
            will(returnValue(true));
            one(mockBarcodeUUIDValidator).validateUUIDFormat("810adcea-d548-42c8-ba9c-f5619597d931");
            will(returnValue(true));
            one(mockBarcodeUUIDValidator).validateAnyBarcodeFormat("810adcea-d548-42c8-ba9c-f5619597d931");
            will(returnValue(false));
        }});
        assertEquals("barcode", redactionService.getItemType("TCGA-13-1817"));
        assertEquals("uuid", redactionService.getItemType("810adcea-d548-42c8-ba9c-f5619597d931"));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGetItemTypeBad() throws Exception {
        context.checking(new Expectations() {{
            one(mockBarcodeUUIDValidator).validateAnyBarcodeFormat("squirrel");
            will(returnValue(false));
            one(mockBarcodeUUIDValidator).validateUUIDFormat("squirrel");
            will(returnValue(false));
        }});
        redactionService.getItemType("squirrel");
    }

    @Test
    public void testRedactParticipantAsBarcode() {
        context.checking(new Expectations() {{
            one(mockAnnotationQueries).getAnnotationCategoryNameForId(11L);
            will(returnValue("because I said so"));
            one(mockBarcodeUUIDValidator).validateAnyBarcodeFormat("TCGA-13-1817");
            will(returnValue(true));
            one(mockUUIDHierarchyQueries).getChildUUIDs("TCGA-13-1817", "barcode");
            will(returnValue(childUuidList));
            exactly(2).of(mockRedactionQueries).redact(with(any(SqlParameterSource[].class)), with(true));
        }});
        redactionService.redact("TCGA-13-1817", 11L);
    }

    @Test
    public void testRedactParticipantAsUuid() {
        context.checking(new Expectations() {{

            one(mockAnnotationQueries).getAnnotationCategoryNameForId(2L);
            will(returnValue("this is a category"));

            one(mockUUIDHierarchyQueries).getChildUUIDs("810adcea-d548-42c8-ba9c-f5619597d931", "uuid");
            one(mockBarcodeUUIDValidator).validateAnyBarcodeFormat("810adcea-d548-42c8-ba9c-f5619597d931");
            will(returnValue(false));
            one(mockBarcodeUUIDValidator).validateUUIDFormat("810adcea-d548-42c8-ba9c-f5619597d931");
            will(returnValue(true));
            exactly(2).of(mockRedactionQueries).redact(with(any(SqlParameterSource[].class)), with(true));
        }});

        redactionService.redact("810adcea-d548-42c8-ba9c-f5619597d931", 2L);
    }

    @Test
    public void testParticipantRedactionFromConsentWithdrawal() {
        context.checking(new Expectations() {{

            one(mockAnnotationQueries).getAnnotationCategoryNameForId(12L);
            will(returnValue("Subject withdrew consent"));

            one(mockUUIDHierarchyQueries).getChildUUIDs("810adcea-d548-42c8-ba9c-f5619597d931", "uuid");
            one(mockBarcodeUUIDValidator).validateAnyBarcodeFormat("810adcea-d548-42c8-ba9c-f5619597d931");
            will(returnValue(false));
            one(mockBarcodeUUIDValidator).validateUUIDFormat("810adcea-d548-42c8-ba9c-f5619597d931");
            will(returnValue(true));

            // false means don't update is_viewable
            exactly(2).of(mockRedactionQueries).redact(with(any(SqlParameterSource[].class)), with(false));
        }});

        redactionService.redact("810adcea-d548-42c8-ba9c-f5619597d931", 12L);
    }

    @Test
    public void testRedactSample() {
        context.checking(new Expectations() {{
            one(mockAnnotationQueries).getAnnotationCategoryNameForId(8L);
            will(returnValue("not a consent redaction"));

            one(mockUUIDHierarchyQueries).getChildUUIDs("TCGA-13-1817-01A", "barcode");
            one(mockBarcodeUUIDValidator).validateAnyBarcodeFormat("TCGA-13-1817-01A");
            will(returnValue(true));
            exactly(2).of(mockRedactionQueries).redact(with(any(SqlParameterSource[].class)), with(true));
        }});
        redactionService.redact("TCGA-13-1817-01A", 8L);
    }

    @Test
    public void testRescindParticipant() {
        context.checking(new Expectations() {{
            one(mockUUIDHierarchyQueries).getChildUUIDs("TCGA-13-1817", "barcode");
            one(mockBarcodeUUIDValidator).validateAnyBarcodeFormat("TCGA-13-1817");
            will(returnValue(true));
            exactly(2).of(mockRedactionQueries).rescind(with(any(SqlParameterSource[].class)));
        }});
        redactionService.rescind("TCGA-13-1817");

    }

    @Test
    public void testRescindParticipantAsUuid() {
        context.checking(new Expectations() {{
            one(mockUUIDHierarchyQueries).getChildUUIDs("810adcea-d548-42c8-ba9c-f5619597d931", "uuid");
            one(mockBarcodeUUIDValidator).validateAnyBarcodeFormat("810adcea-d548-42c8-ba9c-f5619597d931");
            will(returnValue(false));
            one(mockBarcodeUUIDValidator).validateUUIDFormat("810adcea-d548-42c8-ba9c-f5619597d931");
            will(returnValue(true));
            exactly(2).of(mockRedactionQueries).rescind(with(any(SqlParameterSource[].class)));
        }});
        redactionService.rescind("810adcea-d548-42c8-ba9c-f5619597d931");

    }

    @Test
    public void testRescindSample() {
        context.checking(new Expectations() {{
            one(mockUUIDHierarchyQueries).getChildUUIDs("TCGA-13-1817-01A", "barcode");
            one(mockBarcodeUUIDValidator).validateAnyBarcodeFormat("TCGA-13-1817-01A");
            will(returnValue(true));
            exactly(2).of(mockRedactionQueries).rescind(with(any(SqlParameterSource[].class)));
        }});
        redactionService.rescind("TCGA-13-1817-01A");
    }

}

