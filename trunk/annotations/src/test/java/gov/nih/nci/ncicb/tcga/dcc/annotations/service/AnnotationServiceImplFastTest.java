package gov.nih.nci.ncicb.tcga.dcc.annotations.service;

import gov.nih.nci.ncicb.tcga.dcc.common.bean.DccAnnotation;
import gov.nih.nci.ncicb.tcga.dcc.common.bean.DccAnnotationCategory;
import gov.nih.nci.ncicb.tcga.dcc.common.bean.DccAnnotationItem;
import gov.nih.nci.ncicb.tcga.dcc.common.bean.DccAnnotationItemType;
import gov.nih.nci.ncicb.tcga.dcc.common.bean.DccAnnotationNote;
import gov.nih.nci.ncicb.tcga.dcc.common.bean.Tumor;
import gov.nih.nci.ncicb.tcga.dcc.common.dao.annotations.AnnotationQueries;
import gov.nih.nci.ncicb.tcga.dcc.common.exception.BeanException;
import gov.nih.nci.ncicb.tcga.dcc.common.security.AclSecurityUtil;
import gov.nih.nci.ncicb.tcga.dcc.common.service.annotations.AnnotationServiceImpl;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JMock;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.security.acls.domain.BasePermission;

import java.util.Date;

/**
 * Test class for AnnotationServiceImpl
 *
 * @author Rohini Raman
 *         Last updated by: $Author$
 * @version $Rev$
 */
@RunWith(JMock.class)
public class AnnotationServiceImplFastTest {
    private final Mockery context = new JUnit4Mockery();
    private AnnotationServiceImpl annotationService;
    private AnnotationQueries mockAnnotationQueries;
    private AclSecurityUtil mockAclSecurityUtil;

    @Before
    public void setUp() {
        mockAnnotationQueries = context.mock(AnnotationQueries.class);
        annotationService = new AnnotationServiceImpl();
        mockAclSecurityUtil = context.mock(AclSecurityUtil.class);
        annotationService.setAnnotationQueries(mockAnnotationQueries);
        annotationService.setAclSecurityUtil(mockAclSecurityUtil);
    }

    @Test
    public void addAnnotationWithNote() throws AnnotationQueries.AnnotationQueriesException, BeanException {

        final DccAnnotation annotation = getDccAnnotation("testnote");

        context.checking(new Expectations() {{
            one(mockAnnotationQueries).addNewAnnotation(with(getAnnotation(annotation)), with(true));
            will(returnValue(1l));
            one(mockAclSecurityUtil).addPermission(with(any(DccAnnotationNote.class)),with(BasePermission.WRITE));
        }});

        annotationService.addAnnotation(annotation.getDiseases().get(0).getTumorId(),
                annotation.getItemTypes().get(0).getItemTypeId(),
                annotation.getItems().get(0).getItem(),
                annotation.getAnnotationCategory().getCategoryId(),
                annotation.getNotes().get(0).getNoteText(),
                annotation.getCreatedBy(),
                true
                );
    }

    @Test
    public void addAnnotationWithoutNote() throws AnnotationQueries.AnnotationQueriesException, BeanException {

        final DccAnnotation annotation = getDccAnnotation("");

        context.checking(new Expectations() {{
            one(mockAnnotationQueries).addNewAnnotation(with(getAnnotation(annotation)), with(true));
            will(returnValue(1l));
        }});

        annotationService.addAnnotation(annotation.getDiseases().get(0).getTumorId(),
                annotation.getItemTypes().get(0).getItemTypeId(),
                annotation.getItems().get(0).getItem(),
                annotation.getAnnotationCategory().getCategoryId(),
                "",
                annotation.getCreatedBy(),
                true
                );
    }

    private Matcher<DccAnnotation> getAnnotation(final DccAnnotation expectedAnnotation) {
        return new org.junit.internal.matchers.TypeSafeMatcher<DccAnnotation>() {

            @Override
            public boolean matchesSafely(final DccAnnotation actualAnnotation) {
                if(!expectedAnnotation.getItems().get(0).getItemType().getItemTypeId().equals(actualAnnotation.getItems().get(0).getItemType().getItemTypeId())){
                    return false;
                }
                if(!expectedAnnotation.getItems().get(0).getDisease().getTumorId().equals(actualAnnotation.getItems().get(0).getDisease().getTumorId())){
                    return false;
                }
                if(!expectedAnnotation.getItems().get(0).getItem().equals(actualAnnotation.getItems().get(0).getItem())){
                    return false;
                }
                if(!expectedAnnotation.getItems().get(0).getItem().equals(actualAnnotation.getItems().get(0).getItem())){
                    return false;
                }
                if(!expectedAnnotation.getCreatedBy().equals(actualAnnotation.getCreatedBy())){
                    return false;
                }
                if(!(expectedAnnotation.getNotes().size() == actualAnnotation.getNotes().size())){
                    return false;
                }
                if(expectedAnnotation.getNotes().size() > 0){
                    if(!expectedAnnotation.getNotes().get(0).getNoteText().equals(actualAnnotation.getNotes().get(0).getNoteText())){
                        return false;
                    }
                }

                return true;
            }

            public void describeTo(final Description description) {
                description.appendText("Valid match");
            }
        };
    }

    private DccAnnotation getDccAnnotation(final String note){
        final DccAnnotation annotation = new DccAnnotation();
        final DccAnnotationItemType itemType = new DccAnnotationItemType();
        itemType.setItemTypeId(1l);

        final Tumor disease = new Tumor();
        disease.setTumorId(1);
        final DccAnnotationItem dccAnnotationItem = new DccAnnotationItem();

        dccAnnotationItem.setItemType(itemType);
        dccAnnotationItem.setItem("patient");
        dccAnnotationItem.setDisease(disease);
        annotation.addItem(dccAnnotationItem);

        final DccAnnotationCategory category = new DccAnnotationCategory();
        category.setCategoryId(1l);
        annotation.setAnnotationCategory(category);
        final Date now = new Date();
        annotation.setDateCreated(now);
        annotation.addNote(note, "test", now);
        annotation.setCreatedBy("test");

        return annotation;
    }

}
