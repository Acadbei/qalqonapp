package com.example.`data`

import androidx.room.EntityInsertAdapter
import androidx.room.RoomDatabase
import androidx.room.coroutines.createFlow
import androidx.room.util.getColumnIndexOrThrow
import androidx.room.util.performSuspending
import androidx.sqlite.SQLiteStatement
import javax.`annotation`.processing.Generated
import kotlin.Int
import kotlin.Long
import kotlin.String
import kotlin.Suppress
import kotlin.Unit
import kotlin.collections.List
import kotlin.collections.MutableList
import kotlin.collections.mutableListOf
import kotlin.reflect.KClass
import kotlinx.coroutines.flow.Flow

@Generated(value = ["androidx.room.RoomProcessor"])
@Suppress(names = ["UNCHECKED_CAST", "DEPRECATION", "REDUNDANT_PROJECTION", "REMOVAL"])
public class SpamLogDao_Impl(
  __db: RoomDatabase,
) : SpamLogDao {
  private val __db: RoomDatabase

  private val __insertAdapterOfSpamLog: EntityInsertAdapter<SpamLog>
  init {
    this.__db = __db
    this.__insertAdapterOfSpamLog = object : EntityInsertAdapter<SpamLog>() {
      protected override fun createQuery(): String =
          "INSERT OR REPLACE INTO `spam_logs` (`id`,`sender`,`message`,`type`,`timestamp`,`actionTaken`) VALUES (nullif(?, 0),?,?,?,?,?)"

      protected override fun bind(statement: SQLiteStatement, entity: SpamLog) {
        statement.bindLong(1, entity.id.toLong())
        statement.bindText(2, entity.sender)
        statement.bindText(3, entity.message)
        statement.bindText(4, entity.type)
        statement.bindLong(5, entity.timestamp)
        statement.bindText(6, entity.actionTaken)
      }
    }
  }

  public override suspend fun insertSpamLog(log: SpamLog): Unit = performSuspending(__db, false,
      true) { _connection ->
    __insertAdapterOfSpamLog.insert(_connection, log)
  }

  public override fun getAllSpamLogsFlow(): Flow<List<SpamLog>> {
    val _sql: String = "SELECT * FROM spam_logs ORDER BY timestamp DESC"
    return createFlow(__db, false, arrayOf("spam_logs")) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        val _columnIndexOfId: Int = getColumnIndexOrThrow(_stmt, "id")
        val _columnIndexOfSender: Int = getColumnIndexOrThrow(_stmt, "sender")
        val _columnIndexOfMessage: Int = getColumnIndexOrThrow(_stmt, "message")
        val _columnIndexOfType: Int = getColumnIndexOrThrow(_stmt, "type")
        val _columnIndexOfTimestamp: Int = getColumnIndexOrThrow(_stmt, "timestamp")
        val _columnIndexOfActionTaken: Int = getColumnIndexOrThrow(_stmt, "actionTaken")
        val _result: MutableList<SpamLog> = mutableListOf()
        while (_stmt.step()) {
          val _item: SpamLog
          val _tmpId: Int
          _tmpId = _stmt.getLong(_columnIndexOfId).toInt()
          val _tmpSender: String
          _tmpSender = _stmt.getText(_columnIndexOfSender)
          val _tmpMessage: String
          _tmpMessage = _stmt.getText(_columnIndexOfMessage)
          val _tmpType: String
          _tmpType = _stmt.getText(_columnIndexOfType)
          val _tmpTimestamp: Long
          _tmpTimestamp = _stmt.getLong(_columnIndexOfTimestamp)
          val _tmpActionTaken: String
          _tmpActionTaken = _stmt.getText(_columnIndexOfActionTaken)
          _item = SpamLog(_tmpId,_tmpSender,_tmpMessage,_tmpType,_tmpTimestamp,_tmpActionTaken)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override fun getRecentSpamLogsFlow(): Flow<List<SpamLog>> {
    val _sql: String = "SELECT * FROM spam_logs ORDER BY timestamp DESC LIMIT 50"
    return createFlow(__db, false, arrayOf("spam_logs")) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        val _columnIndexOfId: Int = getColumnIndexOrThrow(_stmt, "id")
        val _columnIndexOfSender: Int = getColumnIndexOrThrow(_stmt, "sender")
        val _columnIndexOfMessage: Int = getColumnIndexOrThrow(_stmt, "message")
        val _columnIndexOfType: Int = getColumnIndexOrThrow(_stmt, "type")
        val _columnIndexOfTimestamp: Int = getColumnIndexOrThrow(_stmt, "timestamp")
        val _columnIndexOfActionTaken: Int = getColumnIndexOrThrow(_stmt, "actionTaken")
        val _result: MutableList<SpamLog> = mutableListOf()
        while (_stmt.step()) {
          val _item: SpamLog
          val _tmpId: Int
          _tmpId = _stmt.getLong(_columnIndexOfId).toInt()
          val _tmpSender: String
          _tmpSender = _stmt.getText(_columnIndexOfSender)
          val _tmpMessage: String
          _tmpMessage = _stmt.getText(_columnIndexOfMessage)
          val _tmpType: String
          _tmpType = _stmt.getText(_columnIndexOfType)
          val _tmpTimestamp: Long
          _tmpTimestamp = _stmt.getLong(_columnIndexOfTimestamp)
          val _tmpActionTaken: String
          _tmpActionTaken = _stmt.getText(_columnIndexOfActionTaken)
          _item = SpamLog(_tmpId,_tmpSender,_tmpMessage,_tmpType,_tmpTimestamp,_tmpActionTaken)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun deleteAll() {
    val _sql: String = "DELETE FROM spam_logs"
    return performSuspending(__db, false, true) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        _stmt.step()
      } finally {
        _stmt.close()
      }
    }
  }

  public companion object {
    public fun getRequiredConverters(): List<KClass<*>> = emptyList()
  }
}
