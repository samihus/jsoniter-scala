package com.github.plokhotnyuk.jsoniter_scala.circe

import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import com.github.plokhotnyuk.jsoniter_scala.core._
import io.circe.Json
import io.circe.parser._

class JsoniterScalaCodecSpec extends AnyWordSpec with Matchers {
  "JsoniterScalaCodec.jsonCodec" should {
    "deserialize json" in {
      import com.github.plokhotnyuk.jsoniter_scala.circe.JsoniterScalaCodec._

      val jsonStr = """{"n":null,"s":"VVV","n1":1.0,"n2":2,"a":[null,"WWW",[],{}],"o":{"a":[]}}"""
      val json = parse(jsonStr).getOrElse(null)
      readFromString(jsonStr) shouldBe json
    }
    "not deserialize invalid json" in {
      import com.github.plokhotnyuk.jsoniter_scala.circe.JsoniterScalaCodec._

      val jsonStr = """{"n":null[]"""
      assert(intercept[JsonReaderException](readFromString(jsonStr)).getMessage.contains("expected '}' or ','"))
    }
    "not deserialize deeply nested json" in {
      import com.github.plokhotnyuk.jsoniter_scala.circe.JsoniterScalaCodec._

      val jsonStr1 = """[{"n":""" * 64 + "[]" + "}]" * 64
      val jsonStr2 = """{"n":[""" * 64 + "{}" + "]}" * 64
      assert(intercept[JsonReaderException](readFromString(jsonStr1)).getMessage.contains("depth limit exceeded"))
      assert(intercept[JsonReaderException](readFromString(jsonStr2)).getMessage.contains("depth limit exceeded"))
    }
    "allow customization for number parsing" in {
      implicit val jsonCodec: JsonValueCodec[Json] =
        com.github.plokhotnyuk.jsoniter_scala.circe.JsoniterScalaCodec
          .jsonCodec(numberParser = in => Json.fromDoubleOrNull(in.readDouble())) // compatible with JS and faster than the default one
      val jsonStr = """{"n":null,"s":"VVV","n1":1.0,"n2":2,"a":[null,"WWW",[],{}],"o":{"a":[]}}"""
      val json = parse(jsonStr).getOrElse(null)
      readFromString(jsonStr) shouldBe json
    }
    "serialize json" in {
      import com.github.plokhotnyuk.jsoniter_scala.circe.JsoniterScalaCodec._

      val jsonStr = """{"s":"VVV","n1":1.0,"n2":2,"a":[null,"WWW",[],{}],"o":{"a":[]}}"""
      val json = readFromString(jsonStr)
      writeToString(json) shouldBe jsonStr
    }
    "not serialize invalid json" in {
      import com.github.plokhotnyuk.jsoniter_scala.circe.JsoniterScalaCodec._

      val json1 = parse("\"\ud800\"").getOrElse(null)
      assert(intercept[Throwable](writeToString(json1)).getMessage.contains("illegal char sequence of surrogate pair"))
    }
    "not serialize deeply nested json" in {
      import com.github.plokhotnyuk.jsoniter_scala.circe.JsoniterScalaCodec._

      val json1 = parse("""[{"n":""" * 64 + "[]" + "}]" * 64).getOrElse(null)
      val json2 = parse("""{"n":[""" * 64 + "{}" + "]}" * 64).getOrElse(null)
      assert(intercept[Throwable](writeToString(json1)).getMessage.contains("depth limit exceeded"))
      assert(intercept[Throwable](writeToString(json2)).getMessage.contains("depth limit exceeded"))
    }
    "allow filtering for key-value serialization" in {
      implicit val jsonCodec: JsonValueCodec[Json] =
        com.github.plokhotnyuk.jsoniter_scala.circe.JsoniterScalaCodec.jsonCodec(doSerialize = _ ne Json.Null)
      val jsonStr = """{"n":null,"s":"VVV","n1":1.0,"n2":2,"a":[null,"WWW",[],{}],"o":{"a":[]}}"""
      val jsonStrExpected = """{"s":"VVV","n1":1.0,"n2":2,"a":[null,"WWW",[],{}],"o":{"a":[]}}"""
      val json = readFromString(jsonStr)
      writeToString(json) shouldBe jsonStrExpected
    }
  }
}