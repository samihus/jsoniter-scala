package com.github.plokhotnyuk.jsoniter_scala.benchmark

import org.openjdk.jmh.annotations.Benchmark
import scala.collection.immutable.ArraySeq

class ArraySeqOfBooleansReading extends ArrayOfBooleansBenchmark {
  @Benchmark
  def avSystemGenCodec(): ArraySeq[Boolean] = {
    import com.avsystem.commons.serialization.json._
    import java.nio.charset.StandardCharsets.UTF_8

    JsonStringInput.read[ArraySeq[Boolean]](new String(jsonBytes, UTF_8))
  }

  @Benchmark
  def borer(): ArraySeq[Boolean] = {
    import io.bullet.borer.Json

    Json.decode(jsonBytes).to[ArraySeq[Boolean]].value
  }

  @Benchmark
  def circe(): ArraySeq[Boolean] = {
    import io.circe.parser._
    import java.nio.charset.StandardCharsets.UTF_8

    decode[ArraySeq[Boolean]](new String(jsonBytes, UTF_8)).fold(throw _, identity)
  }

  @Benchmark
  def circeJawn(): ArraySeq[Boolean] = {
    import io.circe.jawn._

    decodeByteArray[ArraySeq[Boolean]](jsonBytes).fold(throw _, identity)
  }

  @Benchmark
  def circeJsoniter(): ArraySeq[Boolean] = {
    import com.github.plokhotnyuk.jsoniter_scala.benchmark.CirceJsoniterCodecs._
    import com.github.plokhotnyuk.jsoniter_scala.core._
    import io.circe.Decoder

    Decoder[ArraySeq[Boolean]].decodeJson(readFromArray(jsonBytes)).fold(throw _, identity)
  }
/* FIXME: DSL-JSON doesn't support parsing of ArraySeq
  @Benchmark
  def dslJsonScala(): ArraySeq[Boolean] = {
    import com.github.plokhotnyuk.jsoniter_scala.benchmark.DslPlatformJson._

    dslJsonDecode[ArraySeq[Boolean]](jsonBytes)
  }
*/
  @Benchmark
  def jacksonScala(): ArraySeq[Boolean] = {
    import com.github.plokhotnyuk.jsoniter_scala.benchmark.JacksonSerDesers._

    jacksonMapper.readValue[ArraySeq[Boolean]](jsonBytes)
  }
  @Benchmark
  def jsoniterScala(): ArraySeq[Boolean] = {
    import com.github.plokhotnyuk.jsoniter_scala.benchmark.JsoniterScalaCodecs._
    import com.github.plokhotnyuk.jsoniter_scala.core._

    readFromArray[ArraySeq[Boolean]](jsonBytes)
  }

  @Benchmark
  def playJson(): ArraySeq[Boolean] = {
    import play.api.libs.json.Json

    Json.parse(jsonBytes).as[ArraySeq[Boolean]]
  }

  @Benchmark
  def playJsonJsoniter(): ArraySeq[Boolean] = {
    import com.evolutiongaming.jsonitertool.PlayJsonJsoniter._
    import com.github.plokhotnyuk.jsoniter_scala.core._

    readFromArray(jsonBytes).as[ArraySeq[Boolean]]
  }

  @Benchmark
  def smithy4sJson(): ArraySeq[Boolean] = {
    import com.github.plokhotnyuk.jsoniter_scala.benchmark.Smithy4sJCodecs._
    import com.github.plokhotnyuk.jsoniter_scala.core._

    readFromArray[ArraySeq[Boolean]](jsonBytes)
  }

  @Benchmark
  def sprayJson(): ArraySeq[Boolean] = {
    import com.github.plokhotnyuk.jsoniter_scala.benchmark.SprayFormats._
    import spray.json._

    JsonParser(jsonBytes).convertTo[ArraySeq[Boolean]]
  }

  @Benchmark
  def uPickle(): ArraySeq[Boolean] = {
    import upickle.default._

    read[ArraySeq[Boolean]](jsonBytes)
  }

  @Benchmark
  def weePickle(): ArraySeq[Boolean] = {
    import com.rallyhealth.weejson.v1.jackson.FromJson
    import com.rallyhealth.weepickle.v1.WeePickle.ToScala

    FromJson(jsonBytes).transform(ToScala[ArraySeq[Boolean]])
  }

  @Benchmark
  def zioJson(): ArraySeq[Boolean] = {
    import zio.json.DecoderOps
    import java.nio.charset.StandardCharsets.UTF_8
    import com.github.plokhotnyuk.jsoniter_scala.benchmark.ZioJSONEncoderDecoders._

    new String(jsonBytes, UTF_8).fromJson[ArraySeq[Boolean]].fold(sys.error, identity)
  }
}