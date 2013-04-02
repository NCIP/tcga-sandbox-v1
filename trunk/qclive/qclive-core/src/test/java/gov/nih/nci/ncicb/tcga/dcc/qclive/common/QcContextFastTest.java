package gov.nih.nci.ncicb.tcga.dcc.qclive.common;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import gov.nih.nci.ncicb.tcga.dcc.common.bean.Archive;
import gov.nih.nci.ncicb.tcga.dcc.common.bean.Center;
import gov.nih.nci.ncicb.tcga.dcc.qclive.bean.Experiment;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

/**
 * Test class for QcContext.
 *
 * @author Jessica Chen
 *         Last updated by: $Author$
 * @version $Rev$
 */
public class QcContextFastTest {
    private QcContext context;

    @Before
    public void setup() {
        context = new QcContext();
    }

    /**
     * Test that getCenterEmail returns null by default.
     */
    @Test
    public void testGetCenterEmailNoContext() {
        assertNull(context.getCenterEmail());
    }

    /**
     * Test that getCenterEmail returns archive's center email.
     */
    @Test
    public void testGetCenterEmailFromArchive() {
        Archive archive = new Archive();
        Center center = new Center();
        List<String> emailList = new ArrayList<String>();
        emailList.add("email");
        center.setEmailList(emailList);
        archive.setTheCenter(center);
        context.setArchive(archive);

        assertEquals("email", context.getCenterEmail());
    }

    /**
     * Test that getCenterEmail returns experiment archive's center's email
     */
    @Test
    public void testGetCenterEmailFromExperiment() {
        Experiment experiment = new Experiment();
        Archive archive = new Archive();
        Center center = new Center();
        List<String> emailList = new ArrayList<String>();
        emailList.add("email");
        center.setEmailList(emailList);
        archive.setTheCenter(center);
        experiment.addArchive(archive);

        context.setExperiment(experiment);

        assertEquals("email", context.getCenterEmail());
    }

    @Test
    public void getErrorsWithoutArchiveName(){
        final String errStr1 = "Error1";
        final String errStr2 = "Error2";

        context.addError(errStr1);
        context.addError(errStr2);

        assertEquals(2, context.getErrorCount());
        assertEquals(errStr1,context.getErrors().get(0));
        assertEquals(errStr2,context.getErrors().get(1));
    }

    @Test
    public void getErrorsWithArchiveName(){
        final String archive1Error1 = "archive1_error1";
        final String archive1Error2 = "archive1_error2";
        final String archive2Error1 = "archive2_error1";
        final String archive2Error2 = "archive2_error2";

        final Archive archive1 = new Archive("archive1.tar.gz");
        final Archive archive2 = new Archive("archive2.tar.gz");

        context.setItemInProgress(archive1);
        context.addError(archive1Error1);
        context.addError(archive1Error2);
        context.setItemInProgress(archive2);
        context.addError(archive2Error1);
        context.addError(archive2Error2);
        context.setItemInProgress(null);
        context.addError("some error");

        assertEquals(5,context.getErrorCount());
        assertEquals(2,context.getErrorsByArchiveName(archive1).size());
        assertEquals(2,context.getErrorsByArchiveName(archive2).size());
        assertEquals(archive1Error1,context.getErrorsByArchiveName(archive1).get(0));
        assertEquals(archive1Error2,context.getErrorsByArchiveName(archive1).get(1));
        assertEquals(archive2Error1,context.getErrorsByArchiveName(archive2).get(0));
        assertEquals(archive2Error2,context.getErrorsByArchiveName(archive2).get(1));

    }

    @Test
    public void getErrorsWithProcessName(){
        final String process1 = "process1";
        final String process2 = "process2";
        final String archive1Error1 = "archive1_"+process1+"_error1";
        final String archive1Error2 = "archive1_"+process2+"_error1";
        final String archive2Error1 = "archive2_"+process1+"_error1";
        final String archive2Error2 = "archive2_"+process2+"_error2";

        final Archive archive1 = new Archive("archive1.tar.gz");
        final Archive archive2 = new Archive("archive2.tar.gz");

        context.setItemInProgress(archive1);
        context.setCurrentProcessName(process1);
        context.addError(archive1Error1);
        context.setCurrentProcessName(process2);
        context.addError(archive1Error2);
        context.setItemInProgress(archive2);
        context.setCurrentProcessName(process1);
        context.addError(archive2Error1);
        context.setCurrentProcessName(process2);
        context.addError(archive2Error2);
        context.setItemInProgress(null);
        context.setCurrentProcessName("");
        context.addError("some error");

        assertEquals(5,context.getErrorCount());
        assertEquals(2,context.getErrorsByArchiveName(archive1).size());
        assertEquals(2,context.getErrorsByArchiveName(archive2).size());
        assertEquals(1,context.getErrorsByProcessName(archive1,process1).size());
        assertEquals(1,context.getErrorsByProcessName(archive1,process2).size());
        assertEquals(1,context.getErrorsByProcessName(archive2,process1).size());
        assertEquals(1,context.getErrorsByProcessName(archive2,process2).size());

        assertEquals(archive1Error1,context.getErrorsByArchiveName(archive1).get(0));
        assertEquals(archive1Error2,context.getErrorsByArchiveName(archive1).get(1));
        assertEquals(archive2Error1,context.getErrorsByArchiveName(archive2).get(0));
        assertEquals(archive2Error2,context.getErrorsByArchiveName(archive2).get(1));
        assertEquals(archive1Error1,context.getErrorsByProcessName(archive1,process1).get(0));
        assertEquals(archive1Error2,context.getErrorsByProcessName(archive1,process2).get(0));
        assertEquals(archive2Error1,context.getErrorsByProcessName(archive2,process1).get(0));
        assertEquals(archive2Error2,context.getErrorsByProcessName(archive2,process2).get(0));

        assertEquals("some error",context.getErrors().get(4));
    }

    @Test
    public void getWarningsWithProcessName(){
        final String process1 = "process1";
        final String process2 = "process2";
        final String archive1Warning1 = "archive1_"+process1+"_warning1";
        final String archive1Warning2 = "archive1_"+process2+"_warning1";
        final String archive2Warning1 = "archive2_"+process1+"_warning1";
        final String archive2Warning2 = "archive2_"+process2+"_warning2";

        final Archive archive1 = new Archive("archive1.tar.gz");
        final Archive archive2 = new Archive("archive2.tar.gz");

        context.setItemInProgress(archive1);
        context.setCurrentProcessName(process1);
        context.addWarning(archive1Warning1);
        context.setCurrentProcessName(process2);
        context.addWarning(archive1Warning2);
        context.setItemInProgress(archive2);
        context.setCurrentProcessName(process1);
        context.addWarning(archive2Warning1);
        context.setCurrentProcessName(process2);
        context.addWarning(archive2Warning2);
        context.setItemInProgress(null);
        context.setCurrentProcessName("");
        context.addWarning("some warning");

        assertEquals(5,context.getWarningCount());
        assertEquals(2,context.getWarningsByArchiveName(archive1).size());
        assertEquals(2,context.getWarningsByArchiveName(archive2).size());
        assertEquals(1,context.getWarningsByProcessName(archive1,process1).size());
        assertEquals(1,context.getWarningsByProcessName(archive1,process2).size());
        assertEquals(1,context.getWarningsByProcessName(archive2,process1).size());
        assertEquals(1,context.getWarningsByProcessName(archive2,process2).size());

        assertEquals(archive1Warning1,context.getWarningsByArchiveName(archive1).get(0));
        assertEquals(archive1Warning2,context.getWarningsByArchiveName(archive1).get(1));
        assertEquals(archive2Warning1,context.getWarningsByArchiveName(archive2).get(0));
        assertEquals(archive2Warning2,context.getWarningsByArchiveName(archive2).get(1));
        assertEquals(archive1Warning1,context.getWarningsByProcessName(archive1,process1).get(0));
        assertEquals(archive1Warning2,context.getWarningsByProcessName(archive1,process2).get(0));
        assertEquals(archive2Warning1,context.getWarningsByProcessName(archive2,process1).get(0));
        assertEquals(archive2Warning2,context.getWarningsByProcessName(archive2,process2).get(0));

        assertEquals("some warning",context.getWarnings().get(4));
    }
}
