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

import gov.nih.nci.ncicb.tcga.dcc.clide.clientmanager.ClideClientManager;
import org.apache.log4j.Logger;

import java.util.Formatter;

/**
 * Measures and prints the composite current, max and min throughput every 3 seconds.
 *
 * @author Dominique Berton
 *         Last updated by: $Author$
 * @version $Rev$
 */

public class CompositeThroughputMonitor extends Thread {

    private ThroughputAware aware = null;

    /**
     * Maximum observed download speed
     */
    private double maxMiBs = 0.0;

    /**
     * Minimum observed download speed
     */
    private double minMiBs = 1000.0;  // a currently unreachable speed.  Max is 150.0 locally

    private long oldCounter = 0L;

    private final Logger logger = Logger.getLogger(ClideClientManager.class.getName());

    private volatile boolean stopMonitoring = false;

    public void stopMonitoring() {
        stopMonitoring = true;
    }

    public CompositeThroughputMonitor(final ThroughputAware aware) {
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
            if (newCounter==0L){
                oldCounter = newCounter;
            }
            if (newCounter > oldCounter) {
                double mibs = (newCounter - oldCounter) * 3000.0 / (endTime - startTime) /
                        ClideConstants.DOUBLE_HTTP_CHUNK_BUF_SIZE;
                maxMiBs = Math.max(mibs, maxMiBs);
                minMiBs = Math.min(mibs, minMiBs);
                final Formatter formatter = new Formatter();
                logger.info(formatter.format("Total speed: %4.3f MiB/s  (max: %4.3f, min: %4.3f) ",
                        mibs, maxMiBs, minMiBs));
                oldCounter = newCounter;
                startTime = endTime;
            } else {
                oldCounter = 0L;
            }
        }
        logger.info("Shutting down composite monitor");
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
                logger.warn("Composite ThroughputMonitor interrupted.", e);
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
}
