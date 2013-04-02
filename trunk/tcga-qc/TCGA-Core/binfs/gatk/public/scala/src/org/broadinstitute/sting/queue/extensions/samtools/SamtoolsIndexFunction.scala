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

package org.broadinstitute.sting.queue.extensions.samtools

import java.io.File
import org.broadinstitute.sting.commandline.{Output, Input}

/**
 * Indexes a BAM file using samtools.
 */
class SamtoolsIndexFunction extends SamtoolsCommandLineFunction {
  analysisName = "samtools index"

  @Input(doc="BAM file to index")
  var bamFile: File = _

  @Output(doc="BAM file index to output", required=false)
  var bamFileIndex: File = _

  /**
   * Sets the bam file index to the bam file name + ".bai".
   */
  override def freezeFieldValues() {
    super.freezeFieldValues()
    if (bamFileIndex == null && bamFile != null)
      bamFileIndex = new File(bamFile.getPath + ".bai")
  }

  def commandLine = "%s index %s %s".format(samtools, bamFile, bamFileIndex)

  override def dotString = "Index: %s".format(bamFile.getName)
}
