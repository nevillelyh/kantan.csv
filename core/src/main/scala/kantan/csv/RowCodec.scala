package kantan.csv

import kantan.codecs.Codec

trait RowCodec[A] extends Codec[Seq[String], A, CsvError, RowDecoder, RowEncoder] with RowDecoder[A] with RowEncoder[A]

@export.exports(Subclass)
object RowCodec extends GeneratedRowCodecs {
  def apply[A](decoder: Seq[String] ⇒ CsvResult[A], encoder: A ⇒ Seq[String]): RowCodec[A] = new RowCodec[A] {
    override def encode(a: A) = encoder(a)
    override def decode(row: Seq[String]) = decoder(row)
  }
}