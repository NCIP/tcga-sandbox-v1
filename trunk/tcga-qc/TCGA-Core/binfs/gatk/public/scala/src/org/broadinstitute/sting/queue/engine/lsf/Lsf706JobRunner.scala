/*
 * Copyright (c) 2011, The Broad Institute
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

package org.broadinstitute.sting.queue.engine.lsf

import org.broadinstitute.sting.queue.function.CommandLineFunction
import org.broadinstitute.sting.queue.util._
import org.broadinstitute.sting.queue.QException
import org.broadinstitute.sting.jna.lsf.v7_0_6.{LibLsf, LibBat}
import org.broadinstitute.sting.utils.Utils
import org.broadinstitute.sting.jna.clibrary.LibC
import org.broadinstitute.sting.jna.lsf.v7_0_6.LibBat.{submitReply, submit}
import com.sun.jna.ptr.IntByReference
import com.sun.jna.{StringArray, NativeLong}
import org.broadinstitute.sting.queue.engine.{RunnerStatus, CommandLineJobRunner}

/**
 * Runs jobs on an LSF compute cluster.
 */
class Lsf706JobRunner(val function: CommandLineFunction) extends CommandLineJobRunner with Logging {

  // Run the static initializer for Lsf706JobRunner
  Lsf706JobRunner

  /** Job Id of the currently executing job. */
  private var jobId = -1L

  /** Last known status */
  private var lastStatus: RunnerStatus.Value = _

  /** The last time the status was updated */
  protected var lastStatusUpdate: Long = _

  /**
   * Dispatches the function on the LSF cluster.
   * @param function Command to run.
   */
  def start() {
    Lsf706JobRunner.lsfLibLock.synchronized {
      val request = new submit
      for (i <- 0 until LibLsf.LSF_RLIM_NLIMITS)
        request.rLimits(i) = LibLsf.DEFAULT_RLIMIT;

      request.jobName = function.description.take(LibBat.MAX_JOB_NAME_LEN)
      request.options |= LibBat.SUB_JOB_NAME

      // Set the output file for stdout
      request.outFile = function.jobOutputFile.getPath
      request.options |= LibBat.SUB_OUT_FILE

      // Set the current working directory
      request.cwd = function.commandDirectory.getPath
      request.options3 |= LibBat.SUB3_CWD

      // If the error file is set specify the separate output for stderr
      if (function.jobErrorFile != null) {
        request.errFile = function.jobErrorFile.getPath
        request.options |= LibBat.SUB_ERR_FILE
      }

      // If the job queue is set specify the job queue
      if (function.jobQueue != null) {
        request.queue = function.jobQueue
        request.options |= LibBat.SUB_QUEUE
      }

      // If the memory limit is set (GB) specify the memory limit
      if (function.memoryLimit.isDefined) {
        request.resReq = "rusage[mem=" + function.memoryLimit.get + "]"
        request.options |= LibBat.SUB_RES_REQ
      }

      // If the priority is set (user specified Int) specify the priority
      if (function.jobPriority.isDefined) {
        request.userPriority = function.jobPriority.get
        request.options2 |= LibBat.SUB2_JOB_PRIORITY
      }

      // Broad specific requirement, our esub requires there be a project
      // else it will spit out a warning to stdout. see $LSF_SERVERDIR/esub
      request.projectName = if (function.jobProject != null) function.jobProject else "Queue"
      request.options |= LibBat.SUB_PROJECT_NAME

      // LSF specific: get the max runtime for the jobQueue and pass it for this job
      request.rLimits(LibLsf.LSF_RLIMIT_RUN) = Lsf706JobRunner.getRlimitRun(function.jobQueue)

      // Run the command as sh <jobScript>
      request.command = "sh " + jobScript

      // Allow advanced users to update the request via QFunction.updateJobRun()
      updateJobRun(request)

      updateStatus(RunnerStatus.RUNNING)
      Retry.attempt(() => {
        val reply = new submitReply
        jobId = LibBat.lsb_submit(request, reply)
        if (jobId < 0)
          throw new QException(LibBat.lsb_sperror("Unable to submit job"))
      }, 1, 5, 10)
      logger.info("Submitted LSF job id: " + jobId)
    }
  }

  def status = this.lastStatus

  private def updateStatus(updatedStatus: RunnerStatus.Value) {
    this.lastStatus = updatedStatus
    this.lastStatusUpdate = System.currentTimeMillis
  }
}

object Lsf706JobRunner extends Logging {
  private val lsfLibLock = new Object
  private val SIGTERM = 15

  /** Number of seconds for a non-normal exit status before we give up on expecting LSF to retry the function. */
  private val retryExpiredSeconds = 5 * 60

  /** Amount of time a job can go without status before giving up. */
  private val unknownStatusMaxSeconds = 5 * 60

  initLsf()

  /** The name of the default queue. */
  private var defaultQueue: String = _

  /** The run limits for each queue. */
  private var queueRlimitRun = Map.empty[String,Int]

  /**
   * Initialize the Lsf library.
   */
  private def initLsf() {
    lsfLibLock.synchronized {
      if (LibBat.lsb_init("Queue") < 0)
        throw new QException(LibBat.lsb_sperror("lsb_init() failed"))
    }
  }

  /**
   * Bulk updates job statuses.
   * @param runners Runners to update.
   */
  def updateStatus(runners: Set[Lsf706JobRunner]) {
    var updatedRunners = Set.empty[Lsf706JobRunner]

    Lsf706JobRunner.lsfLibLock.synchronized {
      val result = LibBat.lsb_openjobinfo(0L, null, null, null, null, LibBat.ALL_JOB)
      if (result < 0) {
        logger.error(LibBat.lsb_sperror("Unable to check LSF job info"))
      } else {
        try {
          val more = new IntByReference(result)
          while (more.getValue > 0) {
            val jobInfo = LibBat.lsb_readjobinfo(more)
            if (jobInfo == null) {
              logger.error(LibBat.lsb_sperror("Unable to read LSF job info"))
              more.setValue(0)
            } else {
              runners.find(runner => runner.jobId == jobInfo.jobId) match {
                case Some(runner) =>
                  updateRunnerStatus(runner, jobInfo)
                  updatedRunners += runner
                case None => /* not our job */
              }
            }
          }
        } finally {
          LibBat.lsb_closejobinfo()
        }
      }
    }

    for (runner <- runners.diff(updatedRunners)) {
      checkUnknownStatus(runner)
    }
  }

  /**
   * Tries to stop any running jobs.
   * @param runners Runners to stop.
   */
  def tryStop(runners: Set[Lsf706JobRunner]) {
    lsfLibLock.synchronized {
      // lsb_killbulkjobs does not seem to forward SIGTERM,
      // only SIGKILL, so send the Ctrl-C (SIGTERM) one by one.
      for (runner <- runners.filterNot(_.jobId < 0)) {
        try {
          if (LibBat.lsb_signaljob(runner.jobId, SIGTERM) < 0)
            logger.error(LibBat.lsb_sperror("Unable to kill job " + runner.jobId))
        } catch {
          case e =>
            logger.error("Unable to kill job " + runner.jobId, e)
        }
      }
    }
  }


  /**
   * Returns the run limit in seconds for the queue.
   * If the queue name is null returns the length of the default queue.
   * @param queue Name of the queue or null for the default queue.
   * @return the run limit in seconds for the queue.
   */
  private def getRlimitRun(queue: String) = {
    lsfLibLock.synchronized {
      if (queue == null) {
        if (defaultQueue != null) {
          queueRlimitRun(defaultQueue)
        } else {
          // Get the info on the default queue.
          val numQueues = new IntByReference(1)
          val queueInfo = LibBat.lsb_queueinfo(null, numQueues, null, null, 0)
          if (queueInfo == null)
            throw new QException(LibBat.lsb_sperror("Unable to get LSF queue info for the default queue"))
          defaultQueue = queueInfo.queue
          val limit = queueInfo.rLimits(LibLsf.LSF_RLIMIT_RUN)
          queueRlimitRun += defaultQueue -> limit
          limit
        }
      } else {
        queueRlimitRun.get(queue) match {
          case Some(limit) => limit
          case None =>
          // Cache miss.  Go get the run limits from LSF.
            val queues = new StringArray(Array[String](queue))
            val numQueues = new IntByReference(1)
            val queueInfo = LibBat.lsb_queueinfo(queues, numQueues, null, null, 0)
            if (queueInfo == null)
              throw new QException(LibBat.lsb_sperror("Unable to get LSF queue info for queue: " + queue))
            val limit = queueInfo.rLimits(LibLsf.LSF_RLIMIT_RUN)
            queueRlimitRun += queue -> limit
            limit
        }
      }
    }
  }

  private def updateRunnerStatus(runner: Lsf706JobRunner, jobInfo: LibBat.jobInfoEnt) {
    val jobStatus = jobInfo.status
    val exitStatus = jobInfo.exitStatus
    val exitInfo = jobInfo.exitInfo
    val endTime = jobInfo.endTime

    logger.debug("Job Id %s status / exitStatus / exitInfo: 0x%02x / 0x%02x / 0x%02x".format(runner.jobId, jobStatus, exitStatus, exitInfo))

    runner.updateStatus(
      if (Utils.isFlagSet(jobStatus, LibBat.JOB_STAT_DONE)) {
        // Done successfully.
        RunnerStatus.DONE
      } else if (Utils.isFlagSet(jobStatus, LibBat.JOB_STAT_EXIT) && !willRetry(exitInfo, endTime)) {
        // Exited function that (probably) won't be retried.
        RunnerStatus.FAILED
      } else {
        // Note that we still saw the job in the system.
        RunnerStatus.RUNNING
      }
    )
  }

  private def checkUnknownStatus(runner: Lsf706JobRunner) {
    // TODO: Need a second pass through either of the two archive logs using lsb_geteventrecbyline() for disappeared jobs.
    // Can also tell if we wake up and the last time we saw status was greater than lsb_parameterinfo().cleanPeriod
    // LSB_SHAREDIR/cluster_name/logdir/lsb.acct (man bacct)
    // LSB_SHAREDIR/cluster_name/logdir/lsb.events (man bhist)
    logger.debug("Job Id %s status / exitStatus / exitInfo: ??? / ??? / ???".format(runner.jobId))
    val unknownStatusSeconds = (System.currentTimeMillis - runner.lastStatusUpdate)
    if (unknownStatusSeconds > (unknownStatusMaxSeconds * 1000L)) {
      // Unknown status has been returned for a while now.
      runner.updateStatus(RunnerStatus.FAILED)
      logger.error("Unable to read LSF status for %d minutes: job id %d: %s".format(unknownStatusSeconds/60, runner.jobId, runner.function.description))
    }
  }

  /**
   * Returns true if LSF is expected to retry running the function.
   * @param exitInfo The reason the job exited.
   * @param endTime THe time the job exited.
   * @return true if LSF is expected to retry running the function.
   */
  private def willRetry(exitInfo: Int, endTime: NativeLong) = {
    exitInfo match {
      case LibBat.EXIT_NORMAL => false
      case _ => {
        val seconds = LibC.difftime(LibC.time(null), endTime)
        (seconds <= retryExpiredSeconds)
      }
    }
  }
}
