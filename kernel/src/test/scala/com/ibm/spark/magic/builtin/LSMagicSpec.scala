package com.ibm.spark.magic.builtin

import java.io.OutputStream
import java.net.URL

import com.ibm.spark.interpreter.Interpreter
import com.ibm.spark.magic.dependencies.{IncludeOutputStream, IncludeInterpreter, IncludeSparkContext}
import com.ibm.spark.magic.{CellMagic, LineMagic}
import org.apache.spark.SparkContext
import org.scalatest.{Matchers, FunSpec}
import org.scalatest.mock.MockitoSugar

import org.mockito.Mockito._
import org.mockito.Matchers._

class TestLSMagic(sc: SparkContext, intp: Interpreter, os: OutputStream)
  extends LSMagic
  with IncludeSparkContext
  with IncludeInterpreter
  with IncludeOutputStream
  {
    override val sparkContext: SparkContext = sc
    override val interpreter: Interpreter = intp
    override val outputStream: OutputStream = os
  }

class LSMagicSpec extends FunSpec with Matchers with MockitoSugar {
  describe("LSMagic") {

    describe("#execute") {
      it("should call println with a magics message") {
        val lsm = spy(new TestLSMagic(
          mock[SparkContext], mock[Interpreter], mock[OutputStream])
        )
        val classList = new BuiltinLoader().loadClasses()
        lsm.execute("")
        verify(lsm).magicNames("%", classOf[LineMagic], classList)
        verify(lsm).magicNames("%%", classOf[CellMagic], classList)
      }
    }

    describe("#magicNames") {
      it("should filter classnames by interface") {
        val prefix = "%"
        val interface = classOf[LineMagic]
        val classes : List[Class[_]] = List(classOf[LSMagic], classOf[Integer])
        val lsm = new TestLSMagic(
          mock[SparkContext], mock[Interpreter], mock[OutputStream])
        lsm.magicNames(prefix, interface, classes).length should be(1)
      }
      it("should prepend prefix to each name"){
        val prefix = "%"
        val className = classOf[LSMagic].getSimpleName
        val interface = classOf[LineMagic]
        val expected = s"${prefix}${className}"
        val classes : List[Class[_]] = List(classOf[LSMagic], classOf[Integer])
        val lsm = new TestLSMagic(
          mock[SparkContext], mock[Interpreter], mock[OutputStream])
        lsm.magicNames(prefix, interface, classes) should be(List(expected))
      }
    }

  }

}
