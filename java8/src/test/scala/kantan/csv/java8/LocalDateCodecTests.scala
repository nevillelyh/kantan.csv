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

package kantan.csv.java8

import java.time.LocalDate
import kantan.codecs.laws.discipline.SerializableTests
import kantan.csv._
import kantan.csv.java8.arbitrary._
import kantan.csv.laws.discipline.{CellCodecTests, RowCodecTests}
import org.scalatest.FunSuite
import org.scalatest.prop.GeneratorDrivenPropertyChecks
import org.typelevel.discipline.scalatest.Discipline

class LocalDateCodecTests extends FunSuite with GeneratorDrivenPropertyChecks with Discipline {
  checkAll("CellEncoder[LocalDate]", SerializableTests[CellEncoder[LocalDate]].serializable)
  checkAll("CellDecoder[LocalDate]", SerializableTests[CellDecoder[LocalDate]].serializable)

  checkAll("RowEncoder[LocalDate]", SerializableTests[RowEncoder[LocalDate]].serializable)
  checkAll("RowDecoder[LocalDate]", SerializableTests[RowDecoder[LocalDate]].serializable)

  checkAll("CellCodec[LocalDate]", CellCodecTests[LocalDate].codec[String, Float])
  checkAll("RowCodec[LocalDate]", RowCodecTests[LocalDate].codec[String, Float])
}
