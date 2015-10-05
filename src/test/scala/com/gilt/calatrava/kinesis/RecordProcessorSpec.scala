package com.gilt.calatrava.kinesis

import java.nio.ByteBuffer
import java.util.{ArrayList => JArrayList, List => JList}

import com.amazonaws.services.kinesis.clientlibrary.interfaces.IRecordProcessorCheckpointer
import com.amazonaws.services.kinesis.clientlibrary.types.ShutdownReason
import com.amazonaws.services.kinesis.model.Record
import com.gilt.calatrava.v0.models.SinkEvent
import org.mockito.Matchers._
import org.mockito.Mockito._
import org.scalatest.{BeforeAndAfterEach, WordSpec}
import org.scalatest.mock.MockitoSugar

class RecordProcessorSpec extends WordSpec with MockitoSugar with BeforeAndAfterEach {

  private val ShardId = "Shard-0001"

  val mockListener = mock[SinkEventProcessor]
  val mockCheckpointer = mock[IRecordProcessorCheckpointer]
  val processor = new RecordProcessor(mockListener)

  override def beforeEach(): Unit = {
    reset(mockListener, mockCheckpointer)
    processor.initialize(ShardId)
  }

  "RecordProcessor" must {

    "ignore records that cannot be parsed as JSON" in {
      processor.processRecords(makeRecords("Not a SinkEvent"), mockCheckpointer)
      verify(mockListener, never()).processEvent(any[SinkEvent])
    }

    "process records that can be parsed as SinkEvents" in {
      processor.processRecords(makeRecords(makeValidRecord("123"), makeValidRecord("234")), mockCheckpointer)
      verify(mockListener, times(2)).processEvent(any[SinkEvent])
      verify(mockCheckpointer, times(1)).checkpoint()
    }

    "does not checkpoint if the events cannot be processed" in {
      when(mockListener.processEvent(any[SinkEvent])).thenThrow(new RuntimeException("Boom!"))

      processor.processRecords(makeRecords(makeValidRecord("123")), mockCheckpointer)
      verify(mockCheckpointer, never()).checkpoint()
    }

    "do a checkpoint when shutdown on exit" in {
      processor.shutdown(mockCheckpointer, ShutdownReason.TERMINATE)
      verify(mockCheckpointer, times(1)).checkpoint()
    }

    "not do a checkpoint when shutdown on failure" in {
      processor.shutdown(mockCheckpointer, ShutdownReason.ZOMBIE)
      verify(mockCheckpointer, never()).checkpoint()
    }
  }

  private[this] def makeRecords(strings: String*): JList[Record] = {
    val list = new JArrayList[Record]()
    strings foreach { string =>
      val mockRecord = mock[Record]
      when(mockRecord.getData).thenReturn(ByteBuffer.wrap(string.getBytes))
      list.add(mockRecord)
    }
    list
  }

  private[this] def makeValidRecord(objectKey: String) = s"""{ "event_object_key": "$objectKey" }"""
}