package org.broadinstitute.sting.queue.library.ipf.vcf

import collection.JavaConversions._
import org.broadinstitute.sting.queue.function.InProcessFunction
import org.broadinstitute.sting.commandline._
import org.broadinstitute.sting.utils.text.XReadLines
import java.io.{PrintStream, PrintWriter, File}


class VCFExtractSites( vcf: File, output: File) extends InProcessFunction {
  @Input(doc="VCF file from which to extract sites") var inVCF: File = vcf
  @Output(doc="Sites VCF file to write to") var outVCF: File = output
  @Argument(doc="Keep non-PASS sites") var keepFilters: Boolean = false
  @Argument(doc="Keep info field") var keepInfo : Boolean = true
  @Argument(doc="Keep qual field") var keepQual : Boolean = true

  def lineMap( line: String ) : String = {
    if ( line.startsWith("##") ) { return line }
    var spline = line.split("\t",9)
    if ( spline(0).startsWith("#")) { return spline.slice(0,8).reduceLeft( _+"\t"+_) }

    if ( spline(6) == "PASS" || keepFilters ) {
      var buf = new StringBuffer(spline.slice(0,5).reduceLeft(_ + "\t" + _ ))
      if ( keepQual ) {
        buf.append("\t%s".format(spline(5)))
      } else {
        buf.append("\t.")
      }

      buf.append("\tPASS")

      if ( keepInfo ) {
        buf.append("\t%s".format(spline(7)))
      } else {
        buf.append("\t.")
      }

      return buf.toString
    }

    return ""
  }

  def lineMapDebug( line: String ) : String = {
    System.out.printf("Input: %s%n ",line)
    val o = lineMap(line)
    System.out.printf("Output: %s%n",o)

    return o
  }

  def debugFilter ( line : String ) : Boolean = {
    System.out.printf("Filter In: %s%n",line)
    if ( line != "" ) {
      System.out.printf("Not filtered %n")
      return true
    } else {
      System.out.printf("Filtered%n")
      return false
    }
  }

  def debugPrint(line: String, k : PrintWriter) : Unit = {
    System.out.printf("Into print: %s%n",line)
    k.println(line)
  }

  def run {
    var w: PrintWriter = new PrintWriter( new PrintStream(outVCF) )
    asScalaIterator[String](new XReadLines(inVCF)).map(lineMap).filter( u => u != "" ).foreach( u => w.println(u) )
    w.close
  }

}
