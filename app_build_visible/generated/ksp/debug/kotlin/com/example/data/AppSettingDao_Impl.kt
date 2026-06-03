package com.example.`data`

import androidx.room.EntityInsertAdapter
import androidx.room.RoomDatabase
import androidx.room.util.getColumnIndexOrThrow
import androidx.room.util.performSuspending
import androidx.sqlite.SQLiteStatement
import javax.`annotation`.processing.Generated
import kotlin.Int
import kotlin.String
import kotlin.Suppress
import kotlin.Unit
import kotlin.collections.List
import kotlin.reflect.KClass

@Generated(value = ["androidx.room.RoomProcessor"])
@Suppress(names = ["UNCHECKED_CAST", "DEPRECATION", "REDUNDANT_PROJECTION", "REMOVAL"])
public class AppSettingDao_Impl(
  __db: RoomDatabase,
) : AppSettingDao {
  private val __db: RoomDatabase

  private val __insertAdapterOfAppSetting: EntityInsertAdapter<AppSetting>
  init {
    this.__db = __db
    this.__insertAdapterOfAppSetting = object : EntityInsertAdapter<AppSetting>() {
      protected override fun createQuery(): String =
          "INSERT OR REPLACE INTO `app_settings` (`key`,`value`) VALUES (?,?)"

      protected override fun bind(statement: SQLiteStatement, entity: AppSetting) {
        statement.bindText(1, entity.key)
        statement.bindText(2, entity.value)
      }
    }
  }

  public override suspend fun saveSetting(setting: AppSetting): Unit = performSuspending(__db,
      false, true) { _connection ->
    __insertAdapterOfAppSetting.insert(_connection, setting)
  }

  public override suspend fun getSetting(key: String): AppSetting? {
    val _sql: String = "SELECT * FROM app_settings WHERE `key` = ? LIMIT 1"
    return performSuspending(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, key)
        val _columnIndexOfKey: Int = getColumnIndexOrThrow(_stmt, "key")
        val _columnIndexOfValue: Int = getColumnIndexOrThrow(_stmt, "value")
        val _result: AppSetting?
        if (_stmt.step()) {
          val _tmpKey: String
          _tmpKey = _stmt.getText(_columnIndexOfKey)
          val _tmpValue: String
          _tmpValue = _stmt.getText(_columnIndexOfValue)
          _result = AppSetting(_tmpKey,_tmpValue)
        } else {
          _result = null
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public companion object {
    public fun getRequiredConverters(): List<KClass<*>> = emptyList()
  }
}
