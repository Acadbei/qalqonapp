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
public class ApkScanResultDao_Impl(
  __db: RoomDatabase,
) : ApkScanResultDao {
  private val __db: RoomDatabase

  private val __insertAdapterOfApkScanResult: EntityInsertAdapter<ApkScanResult>
  init {
    this.__db = __db
    this.__insertAdapterOfApkScanResult = object : EntityInsertAdapter<ApkScanResult>() {
      protected override fun createQuery(): String =
          "INSERT OR REPLACE INTO `apk_scan_results` (`packageName`,`appName`,`riskLevel`,`riskDetails`,`scanTimestamp`) VALUES (?,?,?,?,?)"

      protected override fun bind(statement: SQLiteStatement, entity: ApkScanResult) {
        statement.bindText(1, entity.packageName)
        statement.bindText(2, entity.appName)
        statement.bindText(3, entity.riskLevel)
        statement.bindText(4, entity.riskDetails)
        statement.bindLong(5, entity.scanTimestamp)
      }
    }
  }

  public override suspend fun insertScanResults(results: List<ApkScanResult>): Unit =
      performSuspending(__db, false, true) { _connection ->
    __insertAdapterOfApkScanResult.insert(_connection, results)
  }

  public override suspend fun insertScanResult(result: ApkScanResult): Unit =
      performSuspending(__db, false, true) { _connection ->
    __insertAdapterOfApkScanResult.insert(_connection, result)
  }

  public override fun getAllScanResultsFlow(): Flow<List<ApkScanResult>> {
    val _sql: String = "SELECT * FROM apk_scan_results ORDER BY scanTimestamp DESC"
    return createFlow(__db, false, arrayOf("apk_scan_results")) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        val _columnIndexOfPackageName: Int = getColumnIndexOrThrow(_stmt, "packageName")
        val _columnIndexOfAppName: Int = getColumnIndexOrThrow(_stmt, "appName")
        val _columnIndexOfRiskLevel: Int = getColumnIndexOrThrow(_stmt, "riskLevel")
        val _columnIndexOfRiskDetails: Int = getColumnIndexOrThrow(_stmt, "riskDetails")
        val _columnIndexOfScanTimestamp: Int = getColumnIndexOrThrow(_stmt, "scanTimestamp")
        val _result: MutableList<ApkScanResult> = mutableListOf()
        while (_stmt.step()) {
          val _item: ApkScanResult
          val _tmpPackageName: String
          _tmpPackageName = _stmt.getText(_columnIndexOfPackageName)
          val _tmpAppName: String
          _tmpAppName = _stmt.getText(_columnIndexOfAppName)
          val _tmpRiskLevel: String
          _tmpRiskLevel = _stmt.getText(_columnIndexOfRiskLevel)
          val _tmpRiskDetails: String
          _tmpRiskDetails = _stmt.getText(_columnIndexOfRiskDetails)
          val _tmpScanTimestamp: Long
          _tmpScanTimestamp = _stmt.getLong(_columnIndexOfScanTimestamp)
          _item =
              ApkScanResult(_tmpPackageName,_tmpAppName,_tmpRiskLevel,_tmpRiskDetails,_tmpScanTimestamp)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun deleteAll() {
    val _sql: String = "DELETE FROM apk_scan_results"
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
