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

package kantan.csv.laws.discipline

import imp.imp
import kantan.codecs.laws.CodecValue.{IllegalValue, LegalValue}
import kantan.csv._
import kantan.csv.DecodeError._
import kantan.csv.ParseError._
import kantan.csv.laws._
import org.scalacheck._
import org.scalacheck.Arbitrary.{arbitrary => arb}
import org.scalacheck.rng.Seed

object arbitrary extends ArbitraryInstances

trait ArbitraryInstances extends kantan.codecs.laws.discipline.ArbitraryInstances {
  val csv: Gen[List[List[String]]] = arb[List[List[Cell]]].map(_.map(_.map(_.value)))

  implicit def arbTuple1[A: Arbitrary]: Arbitrary[Tuple1[A]] =
    Arbitrary(imp[Arbitrary[A]].arbitrary.map(Tuple1.apply))


  // - Errors ----------------------------------------------------------------------------------------------------------
  // -------------------------------------------------------------------------------------------------------------------
  val genOutOfBoundsError: Gen[OutOfBounds] = for(i ← Gen.posNum[Int]) yield OutOfBounds(i)
  val genTypeError: Gen[TypeError] = genException.map(TypeError.apply)
  val genDecodeError: Gen[DecodeError] = Gen.oneOf(genOutOfBoundsError, genTypeError)

  val genIOError: Gen[IOError] = genIoException.map(IOError.apply)
  val genParseError: Gen[ParseError] = Gen.oneOf(genIOError, Gen.const(ParseError.NoSuchElement))

  implicit val arbTypeError: Arbitrary[TypeError] = Arbitrary(genTypeError)
  implicit val arbIOError: Arbitrary[IOError] = Arbitrary(genIOError)
  implicit val arbDecodeError: Arbitrary[DecodeError] = Arbitrary(genDecodeError)
  implicit val arbParseError: Arbitrary[ParseError] = Arbitrary(genParseError)
  implicit val arbReadError: Arbitrary[ReadError] = Arbitrary(Gen.oneOf(genDecodeError, genParseError))
  implicit val cogenCsvDecodeError: Cogen[DecodeError] = Cogen { (seed: Seed, err: DecodeError) ⇒ err match {
    case DecodeError.OutOfBounds(i) ⇒ imp[Cogen[Int]].perturb(seed, i)
    case DecodeError.TypeError(msg) ⇒ imp[Cogen[String]].perturb(seed, msg)
  }}



  // - Codec values ----------------------------------------------------------------------------------------------------
  // -------------------------------------------------------------------------------------------------------------------
  implicit def arbLegalRow[A](implicit arb: Arbitrary[LegalCell[A]]): Arbitrary[LegalRow[A]] = Arbitrary {
    arb.arbitrary.map { c ⇒
      LegalValue(Seq(c.encoded), c.decoded)
    }
  }

  implicit def arbIllegalRow[A](implicit arb: Arbitrary[IllegalCell[A]]): Arbitrary[IllegalRow[A]] = Arbitrary {
    arb.arbitrary.map { c ⇒
      IllegalValue(Seq(c.encoded))
    }
  }



  // - Encoders and decoders -------------------------------------------------------------------------------------------
  // -------------------------------------------------------------------------------------------------------------------
  implicit def arbCellDecoder[A: Arbitrary]: Arbitrary[CellDecoder[A]] =
    Arbitrary(arb[String ⇒ DecodeResult[A]].map(CellDecoder.from))

  implicit def arbCellEncoder[A: Arbitrary: Cogen]: Arbitrary[CellEncoder[A]] =
    Arbitrary(arb[A ⇒ String].map(CellEncoder.from))

  implicit def arbRowDecoder[A: Arbitrary]: Arbitrary[RowDecoder[A]] =
    Arbitrary(arb[Seq[String] ⇒ DecodeResult[A]].map(RowDecoder.from))

  implicit def arbRowEncoder[A: Arbitrary: Cogen]: Arbitrary[RowEncoder[A]] =
    Arbitrary(arb[A ⇒ Seq[String]].map(RowEncoder.from))



  // - CsvConfiguration ------------------------------------------------------------------------------------------------
  // -------------------------------------------------------------------------------------------------------------------
  implicit val arbCsvConfiguration: Arbitrary[CsvConfiguration] = Arbitrary {
    for {
      sep    ← Gen.oneOf(',', ';', '\t')
      quote  ← Gen.oneOf('"', '#')
      header ← Arbitrary.arbitrary[Seq[String]]
    } yield CsvConfiguration(sep, quote, header)
  }
}
