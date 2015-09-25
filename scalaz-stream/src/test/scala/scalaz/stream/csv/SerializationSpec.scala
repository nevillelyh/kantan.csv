package scalaz.stream.csv

import java.io.{PrintWriter, StringWriter}

import com.nrinaudo.csv.scalacheck._
import org.scalatest.FunSuite
import org.scalatest.prop.GeneratorDrivenPropertyChecks

import scala.io.Source
import scalaz.concurrent.Task
import scalaz.stream.Process._
import scalaz.stream.{Cause, Process}

class SerializationSpec extends FunSuite with GeneratorDrivenPropertyChecks {
  def read(raw: String): List[List[String]] = scalaz.stream.csv.rowsR[List[String]](Source.fromString(raw), ',').runLog.run.toList
  def write(data: List[List[String]]): String = {
    val sw = new StringWriter()
    val iterator = data.iterator

    Process.repeatEval(Task.delay { if(iterator.hasNext) iterator.next() else throw Cause.Terminated(Cause.End) })
      .to(scalaz.stream.csv.rowsW[List[String]](new PrintWriter(sw), ',')).run.run

    sw.toString
  }

  test("Serialized CSV data should be parsed correctly") {
    forAll(csv) { ss: List[List[String]] => assert(read(write(ss)) == ss) }
  }
}
