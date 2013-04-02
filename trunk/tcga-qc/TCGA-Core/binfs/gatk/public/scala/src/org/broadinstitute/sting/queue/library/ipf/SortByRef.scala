package org.broadinstitute.sting.queue.library.ipf

import collection.JavaConversions._
import org.broadinstitute.sting.queue.function.InProcessFunction
import org.broadinstitute.sting.commandline._
import org.broadinstitute.sting.utils.text.XReadLines
import java.io.{PrintStream, PrintWriter, File}
import collection.immutable.HashMap


class SortByRef( input: File, reference: File, output: File ) extends InProcessFunction {
  @Input(doc="The file to be sorted") var inFile: File = input
  @Input(doc="The reference fasta index") var fai: File = reference
  @Output(doc="The file to write the sorted file to") var outFile :  File = output
  @Argument(doc="The character or expression that separates entries") var separator : String = "\t"
  @Argument(doc="The position of the contig in the file (1-based)") var pos: Int = 1
  @Argument(doc="Comment characters (lines will be brought to file head)") var comment: List[String] = List("#")

  val COMMENT_STRING = "@#!"

  var contigMap: List[(String,PrintWriter,File)] = Nil;

  def entryToTriplet( line : String ) : (String,PrintWriter,File) = {
    val ctig : String = line.split("\t",2)(0)
    val tmpf : File = File.createTempFile("sbr",".tmp")
    val pw : PrintWriter = new PrintWriter(new PrintStream(tmpf))
    return (ctig,pw,tmpf)
  }

  def contigVal( line : String ) : PrintWriter = {

    if ( contigMap.size < 1 ) { // no contigs
      contigMap :+= entryToTriplet(COMMENT_STRING+"\t.")
      contigMap ++= ( new XReadLines(fai)).readLines.map( entryToTriplet(_)).toList
    }

    if ( comment.contains(line.charAt(0).toString) ) {
      return contigMap.find( u => u._1.equals(COMMENT_STRING)).head._2;
    }

    val matches = contigMap.find( u => u._1.equals(line.split(separator)(pos-1)))
    if ( matches.isEmpty ) {
      System.out.println("Empty match for "+line)
      return contigMap(0)._2
    } else { return matches.head._2 }
  }

  def run = {
    var w : PrintWriter = new PrintWriter(new PrintStream(outFile))
    System.out.println("Writing to temp files...")
    ( new XReadLines(inFile) ).readLines.foreach( u => contigVal(u).println(u) )
    contigMap.foreach( u => u._2.close )
    System.out.println("Concatenating...")
    contigMap.map( u => new XReadLines(u._3) ).foreach( u => asScalaIterator(u).foreach(u => w.println(u)))
    w.close()
  }
}