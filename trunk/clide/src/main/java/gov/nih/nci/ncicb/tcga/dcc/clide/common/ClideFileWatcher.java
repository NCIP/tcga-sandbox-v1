/*
 * Software License, Version 1.0 Copyright 2010 SRA International, Inc.
 * Copyright Notice.  The software subject to this notice and license includes both human
 * readable source code form and machine readable, binary, object code form (the "caBIG
 * Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */

package gov.nih.nci.ncicb.tcga.dcc.clide.common;

import gov.nih.nci.ncicb.tcga.dcc.clide.server.ServerContext;
import org.apache.log4j.Logger;

import java.io.File;
import java.util.TimerTask;

/**
 * Very hacky unsafe and non-deterministic way of polling for file change .. until we
 * all upgrade for JDK7 eventually
 *
 * @author Dominique Berton
 *         Last updated by: $Author$
 * @version $Rev$
 */
public class ClideFileWatcher extends TimerTask {

  private final Logger logger = Logger.getLogger(getClass());
  private long timeStamp;
  private long length;
  private File file;
  private int executionNumber = 0;

  public ClideFileWatcher( File file ) {
    this.file = file;
    this.timeStamp = file.lastModified();
    this.length = file.length();
  }

  public final void run() {
      long timeStamp = file.lastModified();
      long length = file.length();
      executionNumber++;
      //At the first execution java does not capture any
     //changes in the file, we have to wait for the second
     //execution with its user-defined timer (3s to capture changes with my mac)
      if (executionNumber > 2) {
          if (this.timeStamp != timeStamp || this.length != length) {
              this.timeStamp = timeStamp;
              this.length = length;
              logger.debug("File: " + file + " has changed");
              ServerContext.fileInProgress = true;
          } else {
              ServerContext.fileInProgress = false;
              logger.debug("File: " + file + " has not changed");
              logger.debug("Closing task thread: " + this.cancel());
          }
      }
  } 
}//end of class