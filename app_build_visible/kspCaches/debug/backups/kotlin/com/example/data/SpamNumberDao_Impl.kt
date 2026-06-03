package com.example.`data`

import androidx.room.EntityDeleteOrUpdateAdapter
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
public class SpamNumberDao_Impl(
  __db: RoomDatabase,
) : SpamNumberDao {
  private val __db: RoomDatabase

  private val __insertAdapterOfSpamNumber: EntityInsertAdapter<SpamNumber>

  private val __deleteAdapterOfSpamNumber: EntityDeleteOrUpdateAdapter<SpamNumber>
  init {
    this.__db = __db
    this.__insertAdapterOfSpamNumber = object : EntityInsertAdapter<SpamNumber>() {
      protected override fun createQuery(): String =
          "INSERT OR REPLACE INTO `spam_numbers` (`phoneNumber`,`label`,`dateAdded`) VALUES (?,?,?)"

      protected override fun bind(statement: SQLiteStatement, entity: SpamNumber) {
        statement.bindText(1, entity.phoneNumber)
        statement.bindText(2, entity.label)
        statement.bindLong(3, entity.dateAdded)
      }
    }
    this.__deleteAdapterOfSpamNumber = object : EntityDeleteOrUpdateAdapter<SpamNumber>() {
      protected override fun createQuery(): String =
          "DELETE FROM `spam_numbers` WHERE `phoneNumber` = ?"

      protected override fun bind(statement: SQLiteStatement, entity: SpamNumber) {
        statement.bindText(1, entity.phoneNumber)
      }
    }
  }

  public override suspend fun insertSpamNumbers(numbers: List<SpamNumber>): Unit =
      performSuspending(__db, false, true) { _connection ->
    __insertAdapterOfSpamNumber.insert(_connection, numbers)
  }

  public override suspend fun insertSpamNumber(number: SpamNumber): Unit = performSuspending(__db,
      false, true) { _connection ->
    __insertAdapterOfSpamNumber.insert(_connection, number)
  }

  public override suspend fun deleteSpamNumber(number: SpamNumber): Unit = performSuspending(__db,
      false, true) { _connection ->
    __deleteAdapterOfSpamNumber.handle(_connection, number)
  }

  public override fun getAllSpamNumbersFlow(): Flow<List<SpamNumber>> {
    val _sql: String = "SELECT * FROM spam_numbers ORDER BY dateAdded DESC"
    return createFlow(__db, false, arrayOf("spam_numbers")) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        val _columnIndexOfPhoneNumber: Int = getColumnIndexOrThrow(_stmt, "phoneNumber")
        val _columnIndexOfLabel: Int = getColumnIndexOrThrow(_stmt, "label")
        val _columnIndexOfDateAdded: Int = getColumnIndexOrThrow(_stmt, "dateAdded")
        val _result: MutableList<SpamNumber> = mutableListOf()
        while (_stmt.step()) {
          val _item: SpamNumber
          val _tmpPhoneNumber: String
          _tmpPhoneNumber = _stmt.getText(_columnIndexOfPhoneNumber)
          val _tmpLabel: String
          _tmpLabel = _stmt.getText(_columnIndexOfLabel)
          val _tmpDateAdded: Long
          _tmpDateAdded = _stmt.getLong(_columnIndexOfDateAdded)
          _item = SpamNumber(_tmpPhoneNumber,_tmpLabel,_tmpDateAdded)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun getAllSpamNumbers(): List<SpamNumber> {
    val _sql: String = "SELECT * FROM spam_numbers"
    return performSuspending(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        val _columnIndexOfPhoneNumber: Int = getColumnIndexOrThrow(_stmt, "phoneNumber")
        val _columnIndexOfLabel: Int = getColumnIndexOrThrow(_stmt, "label")
        val _columnIndexOfDateAdded: Int = getColumnIndexOrThrow(_stmt, "dateAdded")
        val _result: MutableList<SpamNumber> = mutableListOf()
        while (_stmt.step()) {
          val _item: SpamNumber
          val _tmpPhoneNumber: String
          _tmpPhoneNumber = _stmt.getText(_columnIndexOfPhoneNumber)
          val _tmpLabel: String
          _tmpLabel = _stmt.getText(_columnIndexOfLabel)
          val _tmpDateAdded: Long
          _tmpDateAdded = _stmt.getLong(_columnIndexOfDateAdded)
          _item = SpamNumber(_tmpPhoneNumber,_tmpLabel,_tmpDateAdded)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun getSpamNumber(phone: String): SpamNumber? {
    val _sql: String = "SELECT * FROM spam_numbers WHERE phoneNumber = ? LIMIT 1"
    return performSuspending(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, phone)
        val _columnIndexOfPhoneNumber: Int = getColumnIndexOrThrow(_stmt, "phoneNumber")
        val _columnIndexOfLabel: Int = getColumnIndexOrThrow(_stmt, "label")
        val _columnIndexOfDateAdded: Int = getColumnIndexOrThrow(_stmt, "dateAdded")
        val _result: SpamNumber?
        if (_stmt.step()) {
          val _tmpPhoneNumber: String
          _tmpPhoneNumber = _stmt.getText(_columnIndexOfPhoneNumber)
          val _tmpLabel: String
          _tmpLabel = _stmt.getText(_columnIndexOfLabel)
          val _tmpDateAdded: Long
          _tmpDateAdded = _stmt.getLong(_columnIndexOfDateAdded)
          _result = SpamNumber(_tmpPhoneNumber,_tmpLabel,_tmpDateAdded)
        } else {
          _result = null
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun deleteAll() {
    val _sql: String = "DELETE FROM spam_numbers"
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
