package com.example.`data`

import androidx.room.InvalidationTracker
import androidx.room.RoomOpenDelegate
import androidx.room.migration.AutoMigrationSpec
import androidx.room.migration.Migration
import androidx.room.util.TableInfo
import androidx.room.util.TableInfo.Companion.read
import androidx.room.util.dropFtsSyncTriggers
import androidx.sqlite.SQLiteConnection
import androidx.sqlite.execSQL
import javax.`annotation`.processing.Generated
import kotlin.Lazy
import kotlin.String
import kotlin.Suppress
import kotlin.collections.List
import kotlin.collections.Map
import kotlin.collections.MutableList
import kotlin.collections.MutableMap
import kotlin.collections.MutableSet
import kotlin.collections.Set
import kotlin.collections.mutableListOf
import kotlin.collections.mutableMapOf
import kotlin.collections.mutableSetOf
import kotlin.reflect.KClass

@Generated(value = ["androidx.room.RoomProcessor"])
@Suppress(names = ["UNCHECKED_CAST", "DEPRECATION", "REDUNDANT_PROJECTION", "REMOVAL"])
public class ShieldDatabase_Impl : ShieldDatabase() {
  private val _spamNumberDao: Lazy<SpamNumberDao> = lazy {
    SpamNumberDao_Impl(this)
  }

  private val _spamLogDao: Lazy<SpamLogDao> = lazy {
    SpamLogDao_Impl(this)
  }

  private val _blockedDomainDao: Lazy<BlockedDomainDao> = lazy {
    BlockedDomainDao_Impl(this)
  }

  private val _apkScanResultDao: Lazy<ApkScanResultDao> = lazy {
    ApkScanResultDao_Impl(this)
  }

  private val _appSettingDao: Lazy<AppSettingDao> = lazy {
    AppSettingDao_Impl(this)
  }

  protected override fun createOpenDelegate(): RoomOpenDelegate {
    val _openDelegate: RoomOpenDelegate = object : RoomOpenDelegate(1,
        "449425f208037890cc60a0ee4ef8232e", "848f8b7a48fb89bf623f1476c8658eeb") {
      public override fun createAllTables(connection: SQLiteConnection) {
        connection.execSQL("CREATE TABLE IF NOT EXISTS `spam_numbers` (`phoneNumber` TEXT NOT NULL, `label` TEXT NOT NULL, `dateAdded` INTEGER NOT NULL, PRIMARY KEY(`phoneNumber`))")
        connection.execSQL("CREATE TABLE IF NOT EXISTS `spam_logs` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `sender` TEXT NOT NULL, `message` TEXT NOT NULL, `type` TEXT NOT NULL, `timestamp` INTEGER NOT NULL, `actionTaken` TEXT NOT NULL)")
        connection.execSQL("CREATE TABLE IF NOT EXISTS `blocked_domains` (`domain` TEXT NOT NULL, `reason` TEXT NOT NULL, `dateAdded` INTEGER NOT NULL, PRIMARY KEY(`domain`))")
        connection.execSQL("CREATE TABLE IF NOT EXISTS `apk_scan_results` (`packageName` TEXT NOT NULL, `appName` TEXT NOT NULL, `riskLevel` TEXT NOT NULL, `riskDetails` TEXT NOT NULL, `scanTimestamp` INTEGER NOT NULL, PRIMARY KEY(`packageName`))")
        connection.execSQL("CREATE TABLE IF NOT EXISTS `app_settings` (`key` TEXT NOT NULL, `value` TEXT NOT NULL, PRIMARY KEY(`key`))")
        connection.execSQL("CREATE TABLE IF NOT EXISTS room_master_table (id INTEGER PRIMARY KEY,identity_hash TEXT)")
        connection.execSQL("INSERT OR REPLACE INTO room_master_table (id,identity_hash) VALUES(42, '449425f208037890cc60a0ee4ef8232e')")
      }

      public override fun dropAllTables(connection: SQLiteConnection) {
        connection.execSQL("DROP TABLE IF EXISTS `spam_numbers`")
        connection.execSQL("DROP TABLE IF EXISTS `spam_logs`")
        connection.execSQL("DROP TABLE IF EXISTS `blocked_domains`")
        connection.execSQL("DROP TABLE IF EXISTS `apk_scan_results`")
        connection.execSQL("DROP TABLE IF EXISTS `app_settings`")
      }

      public override fun onCreate(connection: SQLiteConnection) {
      }

      public override fun onOpen(connection: SQLiteConnection) {
        internalInitInvalidationTracker(connection)
      }

      public override fun onPreMigrate(connection: SQLiteConnection) {
        dropFtsSyncTriggers(connection)
      }

      public override fun onPostMigrate(connection: SQLiteConnection) {
      }

      public override fun onValidateSchema(connection: SQLiteConnection):
          RoomOpenDelegate.ValidationResult {
        val _columnsSpamNumbers: MutableMap<String, TableInfo.Column> = mutableMapOf()
        _columnsSpamNumbers.put("phoneNumber", TableInfo.Column("phoneNumber", "TEXT", true, 1,
            null, TableInfo.CREATED_FROM_ENTITY))
        _columnsSpamNumbers.put("label", TableInfo.Column("label", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsSpamNumbers.put("dateAdded", TableInfo.Column("dateAdded", "INTEGER", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        val _foreignKeysSpamNumbers: MutableSet<TableInfo.ForeignKey> = mutableSetOf()
        val _indicesSpamNumbers: MutableSet<TableInfo.Index> = mutableSetOf()
        val _infoSpamNumbers: TableInfo = TableInfo("spam_numbers", _columnsSpamNumbers,
            _foreignKeysSpamNumbers, _indicesSpamNumbers)
        val _existingSpamNumbers: TableInfo = read(connection, "spam_numbers")
        if (!_infoSpamNumbers.equals(_existingSpamNumbers)) {
          return RoomOpenDelegate.ValidationResult(false, """
              |spam_numbers(com.example.data.SpamNumber).
              | Expected:
              |""".trimMargin() + _infoSpamNumbers + """
              |
              | Found:
              |""".trimMargin() + _existingSpamNumbers)
        }
        val _columnsSpamLogs: MutableMap<String, TableInfo.Column> = mutableMapOf()
        _columnsSpamLogs.put("id", TableInfo.Column("id", "INTEGER", true, 1, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsSpamLogs.put("sender", TableInfo.Column("sender", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsSpamLogs.put("message", TableInfo.Column("message", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsSpamLogs.put("type", TableInfo.Column("type", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsSpamLogs.put("timestamp", TableInfo.Column("timestamp", "INTEGER", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsSpamLogs.put("actionTaken", TableInfo.Column("actionTaken", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        val _foreignKeysSpamLogs: MutableSet<TableInfo.ForeignKey> = mutableSetOf()
        val _indicesSpamLogs: MutableSet<TableInfo.Index> = mutableSetOf()
        val _infoSpamLogs: TableInfo = TableInfo("spam_logs", _columnsSpamLogs,
            _foreignKeysSpamLogs, _indicesSpamLogs)
        val _existingSpamLogs: TableInfo = read(connection, "spam_logs")
        if (!_infoSpamLogs.equals(_existingSpamLogs)) {
          return RoomOpenDelegate.ValidationResult(false, """
              |spam_logs(com.example.data.SpamLog).
              | Expected:
              |""".trimMargin() + _infoSpamLogs + """
              |
              | Found:
              |""".trimMargin() + _existingSpamLogs)
        }
        val _columnsBlockedDomains: MutableMap<String, TableInfo.Column> = mutableMapOf()
        _columnsBlockedDomains.put("domain", TableInfo.Column("domain", "TEXT", true, 1, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsBlockedDomains.put("reason", TableInfo.Column("reason", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsBlockedDomains.put("dateAdded", TableInfo.Column("dateAdded", "INTEGER", true, 0,
            null, TableInfo.CREATED_FROM_ENTITY))
        val _foreignKeysBlockedDomains: MutableSet<TableInfo.ForeignKey> = mutableSetOf()
        val _indicesBlockedDomains: MutableSet<TableInfo.Index> = mutableSetOf()
        val _infoBlockedDomains: TableInfo = TableInfo("blocked_domains", _columnsBlockedDomains,
            _foreignKeysBlockedDomains, _indicesBlockedDomains)
        val _existingBlockedDomains: TableInfo = read(connection, "blocked_domains")
        if (!_infoBlockedDomains.equals(_existingBlockedDomains)) {
          return RoomOpenDelegate.ValidationResult(false, """
              |blocked_domains(com.example.data.BlockedDomain).
              | Expected:
              |""".trimMargin() + _infoBlockedDomains + """
              |
              | Found:
              |""".trimMargin() + _existingBlockedDomains)
        }
        val _columnsApkScanResults: MutableMap<String, TableInfo.Column> = mutableMapOf()
        _columnsApkScanResults.put("packageName", TableInfo.Column("packageName", "TEXT", true, 1,
            null, TableInfo.CREATED_FROM_ENTITY))
        _columnsApkScanResults.put("appName", TableInfo.Column("appName", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsApkScanResults.put("riskLevel", TableInfo.Column("riskLevel", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsApkScanResults.put("riskDetails", TableInfo.Column("riskDetails", "TEXT", true, 0,
            null, TableInfo.CREATED_FROM_ENTITY))
        _columnsApkScanResults.put("scanTimestamp", TableInfo.Column("scanTimestamp", "INTEGER",
            true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        val _foreignKeysApkScanResults: MutableSet<TableInfo.ForeignKey> = mutableSetOf()
        val _indicesApkScanResults: MutableSet<TableInfo.Index> = mutableSetOf()
        val _infoApkScanResults: TableInfo = TableInfo("apk_scan_results", _columnsApkScanResults,
            _foreignKeysApkScanResults, _indicesApkScanResults)
        val _existingApkScanResults: TableInfo = read(connection, "apk_scan_results")
        if (!_infoApkScanResults.equals(_existingApkScanResults)) {
          return RoomOpenDelegate.ValidationResult(false, """
              |apk_scan_results(com.example.data.ApkScanResult).
              | Expected:
              |""".trimMargin() + _infoApkScanResults + """
              |
              | Found:
              |""".trimMargin() + _existingApkScanResults)
        }
        val _columnsAppSettings: MutableMap<String, TableInfo.Column> = mutableMapOf()
        _columnsAppSettings.put("key", TableInfo.Column("key", "TEXT", true, 1, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsAppSettings.put("value", TableInfo.Column("value", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        val _foreignKeysAppSettings: MutableSet<TableInfo.ForeignKey> = mutableSetOf()
        val _indicesAppSettings: MutableSet<TableInfo.Index> = mutableSetOf()
        val _infoAppSettings: TableInfo = TableInfo("app_settings", _columnsAppSettings,
            _foreignKeysAppSettings, _indicesAppSettings)
        val _existingAppSettings: TableInfo = read(connection, "app_settings")
        if (!_infoAppSettings.equals(_existingAppSettings)) {
          return RoomOpenDelegate.ValidationResult(false, """
              |app_settings(com.example.data.AppSetting).
              | Expected:
              |""".trimMargin() + _infoAppSettings + """
              |
              | Found:
              |""".trimMargin() + _existingAppSettings)
        }
        return RoomOpenDelegate.ValidationResult(true, null)
      }
    }
    return _openDelegate
  }

  protected override fun createInvalidationTracker(): InvalidationTracker {
    val _shadowTablesMap: MutableMap<String, String> = mutableMapOf()
    val _viewTables: MutableMap<String, Set<String>> = mutableMapOf()
    return InvalidationTracker(this, _shadowTablesMap, _viewTables, "spam_numbers", "spam_logs",
        "blocked_domains", "apk_scan_results", "app_settings")
  }

  public override fun clearAllTables() {
    super.performClear(false, "spam_numbers", "spam_logs", "blocked_domains", "apk_scan_results",
        "app_settings")
  }

  protected override fun getRequiredTypeConverterClasses(): Map<KClass<*>, List<KClass<*>>> {
    val _typeConvertersMap: MutableMap<KClass<*>, List<KClass<*>>> = mutableMapOf()
    _typeConvertersMap.put(SpamNumberDao::class, SpamNumberDao_Impl.getRequiredConverters())
    _typeConvertersMap.put(SpamLogDao::class, SpamLogDao_Impl.getRequiredConverters())
    _typeConvertersMap.put(BlockedDomainDao::class, BlockedDomainDao_Impl.getRequiredConverters())
    _typeConvertersMap.put(ApkScanResultDao::class, ApkScanResultDao_Impl.getRequiredConverters())
    _typeConvertersMap.put(AppSettingDao::class, AppSettingDao_Impl.getRequiredConverters())
    return _typeConvertersMap
  }

  public override fun getRequiredAutoMigrationSpecClasses(): Set<KClass<out AutoMigrationSpec>> {
    val _autoMigrationSpecsSet: MutableSet<KClass<out AutoMigrationSpec>> = mutableSetOf()
    return _autoMigrationSpecsSet
  }

  public override
      fun createAutoMigrations(autoMigrationSpecs: Map<KClass<out AutoMigrationSpec>, AutoMigrationSpec>):
      List<Migration> {
    val _autoMigrations: MutableList<Migration> = mutableListOf()
    return _autoMigrations
  }

  public override fun spamNumberDao(): SpamNumberDao = _spamNumberDao.value

  public override fun spamLogDao(): SpamLogDao = _spamLogDao.value

  public override fun blockedDomainDao(): BlockedDomainDao = _blockedDomainDao.value

  public override fun apkScanResultDao(): ApkScanResultDao = _apkScanResultDao.value

  public override fun appSettingDao(): AppSettingDao = _appSettingDao.value
}
