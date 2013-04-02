/*
 * Copyright 2009 Red Hat, Inc.
 *
 * Red Hat licenses this file to you under the Apache License, version 2.0
 * (the "License"); you may not use this file except in compliance with the
 * License.  You may obtain a copy of the License at:
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the
 * License for the specific language governing permissions and limitations
 * under the License.
 */
package gov.nih.nci.ncicb.tcga.dcc.clide.common;

import gov.nih.nci.ncicb.tcga.dcc.clide.client.ClientResponseHandler;
import org.apache.log4j.Logger;

import java.util.Formatter;

/**
 * Measures and prints the current, max and min throughput every 3 seconds.
 *
 * @author The Netty Project (netty-dev@lists.jboss.org)
 * @author Trustin Lee (tlee@redhat.com)
 * @version $Rev: 1711 $, $Date: 2009-09-04 12:01:58 +0900 (금, 04 9 2009) $
 */
public class ThroughputMonitor extends Thread {

    private ThroughputAware aware = null;

    /** Maximum observed download speed */
    private double maxMiBs = 0.0;

    /** Minimum observed download speed */
    private double minMiBs = 1000.0;  // a currently unreachable speed.  Max is 150.0 locally

    private long oldCounter = 0L;

    private final Logger logger = Logger.getLogger(
            ClientResponseHandler.class.getName());

    private boolean stopMonitoring = false;

    private long expectedBytes;

    public void stopMonitoring() {
        stopMonitoring = true;
    }

    public ThroughputMonitor(final ThroughputAware aware) {
        this.aware = aware;
    }

    @Override
    public void run() {
        oldCounter = getTransferredBytes();
        long startTime = System.currentTimeMillis();
        while (true) {
            if (closedWhileSleeping()) {
                break;
            }
            long endTime = System.currentTimeMillis();
            long newCounter = getTransferredBytes();
            if (newCounter > oldCounter) {
                long totalExpected = getExpectedBytes();
                if (totalExpected != 0) {
                    double percent = newCounter * 100.0 / totalExpected;
                    double mibs = (newCounter - oldCounter) * 3000.0 / (endTime - startTime) /
                            ClideConstants.DOUBLE_HTTP_CHUNK_BUF_SIZE;
                    maxMiBs = Math.max(mibs, maxMiBs);
                    minMiBs = Math.min(mibs, minMiBs);
                    final Formatter formatter = new Formatter();
                    logger.info(formatter.format("%4.3f MiB/s  (max: %4.3f, min: %4.3f)  %2.1f%% of file",
                            mibs, maxMiBs, minMiBs, percent));
                    oldCounter = newCounter;
                    startTime = endTime;
                }
            }
        }
        logger.info("Shutting down monitor");
    }

    /**
     * Take many little naps that add up to 3 seconds.  If we are done mid 3 seconds pop out
     *
     * @return stopMonitoring
     */
    private boolean closedWhileSleeping() {
        for (int i = 0; i < 6; ++i) {
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                logger.warn("ThroughputMonitor interrupted.", e);
            }
            if (stopMonitoring) {
                break;
            }
        }
        return stopMonitoring;

    }

    public long getTransferredBytes() {
        return aware.getTransferredBytes();
    }

    private long getExpectedBytes() {
        return expectedBytes;
    }

    public void setExpectedBytes(long bytes) {
        expectedBytes = bytes;
        oldCounter = 0;
    }
}
