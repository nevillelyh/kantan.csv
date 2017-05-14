/*
 * Copyright 2017 Nicolas Rinaudo
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package kantan.csv.ops

import java.io.StringWriter
import kantan.csv.{CsvWriter, _}
import kantan.csv.engine.WriterEngine

/** Provides useful syntax for collections.
  *
  * {{{
  * List(List(1, 2, 3), List(4, 5, 6)).asCsv(rfc)
  * }}}
  */
final class CsvRowsOps[A: RowEncoder](val as: TraversableOnce[A]) {
  @deprecated("use asCsv(CsvConfiguration) instead", "0.1.18")
  def asCsv(sep: Char, header: String*)(implicit e: WriterEngine): String =
    asCsv(rfc.withColumnSeparator(sep).withHeader(header:_*))

  /** Turns the collection into a CSV string.
    *
    * @param conf CSV writing behaviour.
    */
  def asCsv(conf: CsvConfiguration)(implicit e: WriterEngine): String = {
    val out = new StringWriter()
    CsvWriter(out, conf).write(as).close()
    out.toString
  }
}

trait ToCsvRowsOps {
  implicit def toCsvRowsOps[A: RowEncoder](as: TraversableOnce[A]): CsvRowsOps[A] = new CsvRowsOps(as)
}

object csvRows extends ToCsvRowsOps
