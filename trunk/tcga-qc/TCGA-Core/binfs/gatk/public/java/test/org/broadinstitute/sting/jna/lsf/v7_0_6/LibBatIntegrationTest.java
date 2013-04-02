/*
 * Copyright (c) 2010, The Broad Institute
 *
 * Permission is hereby granted, free of charge, to any person
 * obtaining a copy of this software and associated documentation
 * files (the "Software"), to deal in the Software without
 * restriction, including without limitation the rights to use,
 * copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the
 * Software is furnished to do so, subject to the following
 * conditions:
 *
 * The above copyright notice and this permission notice shall be
 * included in all copies or substantial portions of the Software.
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES
 * OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT
 * HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY,
 * WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING
 * FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR
 * OTHER DEALINGS IN THE SOFTWARE.
 */

package org.broadinstitute.sting.jna.lsf.v7_0_6;

import com.sun.jna.*;
import com.sun.jna.ptr.IntByReference;
import org.apache.commons.io.FileUtils;
import org.broadinstitute.sting.utils.Utils;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.broadinstitute.sting.BaseTest;
import org.broadinstitute.sting.jna.lsf.v7_0_6.LibBat.*;

import javax.jws.soap.SOAPBinding;
import java.io.File;

/**
 * Really unit tests, but these test will only run on systems with LSF setup.
 */
public class LibBatIntegrationTest extends BaseTest {
    @BeforeClass
    public void initLibBat() {
        Assert.assertFalse(LibBat.lsb_init("LibBatIntegrationTest") < 0, LibBat.lsb_sperror("lsb_init() failed"));
    }

    @Test
    public void testClusterName() {
        String clusterName = LibLsf.ls_getclustername();
        System.out.println("Cluster name: " + clusterName);
        Assert.assertNotNull(clusterName);
    }

    @Test
    public void testReadConfEnv() {
        LibLsf.config_param[] unitsParam = (LibLsf.config_param[]) new LibLsf.config_param().toArray(4);

        unitsParam[0].paramName = "LSF_UNIT_FOR_LIMITS";
        unitsParam[1].paramName = "LSF_CONFDIR";
        unitsParam[2].paramName = "MADE_UP_PARAMETER";

        Structure.autoWrite(unitsParam);

        if (LibLsf.ls_readconfenv(unitsParam[0], null) != 0) {
            Assert.fail(LibLsf.ls_sysmsg());
        }

        Structure.autoRead(unitsParam);

        System.out.println("LSF_UNIT_FOR_LIMITS: " + unitsParam[0].paramValue);
        Assert.assertNotNull(unitsParam[1].paramValue);
        Assert.assertNull(unitsParam[2].paramValue);
        Assert.assertNull(unitsParam[3].paramName);
        Assert.assertNull(unitsParam[3].paramValue);
    }

    @Test
    public void testReadQueueLimits() {
        String queue = "hour";
        StringArray queues = new StringArray(new String[] {queue});
        IntByReference numQueues = new IntByReference(1);
        queueInfoEnt queueInfo = LibBat.lsb_queueinfo(queues, numQueues, null, null, 0);

        Assert.assertEquals(numQueues.getValue(), 1);
        Assert.assertNotNull(queueInfo);
        Assert.assertEquals(queueInfo.queue, queue);

        int runLimit = queueInfo.rLimits[LibLsf.LSF_RLIMIT_RUN];
        Assert.assertTrue(runLimit > 0, "LSF run limit is not greater than zero: " + runLimit);
    }

    @Test
    public void testSubmitEcho() throws InterruptedException {
        String queue = "hour";
        File outFile = createNetworkTempFile("LibBatIntegrationTest-", ".out");

        submit req = new submit();

        for (int i = 0; i < LibLsf.LSF_RLIM_NLIMITS; i++)
            req.rLimits[i] = LibLsf.DEFAULT_RLIMIT;

        req.projectName = "LibBatIntegrationTest";
        req.options |= LibBat.SUB_PROJECT_NAME;

        req.queue = queue;
        req.options |= LibBat.SUB_QUEUE;

        req.outFile = outFile.getPath();
        req.options |= LibBat.SUB_OUT_FILE;

        req.userPriority = 100;
        req.options2 |= LibBat.SUB2_JOB_PRIORITY;

        req.command = "echo \"Hello world.\"";

        submitReply reply = new submitReply();
        long jobId = LibBat.lsb_submit(req, reply);

        Assert.assertFalse(jobId < 0, LibBat.lsb_sperror("Error dispatching"));

        System.out.println("Waiting for job to run: " + jobId);
        int jobStatus = LibBat.JOB_STAT_PEND;
        while (Utils.isFlagSet(jobStatus, LibBat.JOB_STAT_PEND) || Utils.isFlagSet(jobStatus, LibBat.JOB_STAT_RUN)) {
            Thread.sleep(30 * 1000L);

            int numJobs = LibBat.lsb_openjobinfo(jobId, null, null, null, null, LibBat.ALL_JOB);
            try {
                Assert.assertEquals(numJobs, 1);
    
                IntByReference more = new IntByReference();

                jobInfoEnt jobInfo = LibBat.lsb_readjobinfo(more);
                Assert.assertNotNull(jobInfo, "Job info is null");
                Assert.assertEquals(more.getValue(), 0, "More job info results than expected");

                jobStatus = jobInfo.status;
            } finally {
                LibBat.lsb_closejobinfo();
            }
        }
        Assert.assertTrue(Utils.isFlagSet(jobStatus, LibBat.JOB_STAT_DONE), String.format("Unexpected job status: 0x%02x", jobStatus));

        Assert.assertTrue(FileUtils.waitFor(outFile, 120), "File not found: " + outFile.getAbsolutePath());
        Assert.assertTrue(outFile.delete(), "Unable to delete " + outFile.getAbsolutePath());
        Assert.assertEquals(reply.queue, req.queue, "LSF reply queue does not match requested queue.");
        System.out.println("Validating that we reached the end of the test without exit.");
    }
}
