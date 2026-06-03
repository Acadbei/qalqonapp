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
public class BlockedDomainDao_Impl(
  __db: RoomDatabase,
) : BlockedDomainDao {
  private val __db: RoomDatabase

  private val __insertAdapterOfBlockedDomain: EntityInsertAdapter<BlockedDomain>

  private val __deleteAdapterOfBlockedDomain: EntityDeleteOrUpdateAdapter<BlockedDomain>
  init {
    this.__db = __db
    this.__insertAdapterOfBlockedDomain = object : EntityInsertAdapter<BlockedDomain>() {
      protected override fun createQuery(): String =
          "INSERT OR REPLACE INTO `blocked_domains` (`domain`,`reason`,`dateAdded`) VALUES (?,?,?)"

      protected override fun bind(statement: SQLiteStatement, entity: BlockedDomain) {
        statement.bindText(1, entity.domain)
        statement.bindText(2, entity.reason)
        statement.bindLong(3, entity.dateAdded)
      }
    }
    this.__deleteAdapterOfBlockedDomain = object : EntityDeleteOrUpdateAdapter<BlockedDomain>() {
      protected override fun createQuery(): String =
          "DELETE FROM `blocked_domains` WHERE `domain` = ?"

      protected override fun bind(statement: SQLiteStatement, entity: BlockedDomain) {
        statement.bindText(1, entity.domain)
      }
    }
  }

  public override suspend fun insertBlockedDomains(domains: List<BlockedDomain>): Unit =
      performSuspending(__db, false, true) { _connection ->
    __insertAdapterOfBlockedDomain.insert(_connection, domains)
  }

  public override suspend fun insertBlockedDomain(domain: BlockedDomain): Unit =
      performSuspending(__db, false, true) { _connection ->
    __insertAdapterOfBlockedDomain.insert(_connection, domain)
  }

  public override suspend fun deleteBlockedDomain(domain: BlockedDomain): Unit =
      performSuspending(__db, false, true) { _connection ->
    __deleteAdapterOfBlockedDomain.handle(_connection, domain)
  }

  public override fun getAllBlockedDomainsFlow(): Flow<List<BlockedDomain>> {
    val _sql: String = "SELECT * FROM blocked_domains ORDER BY dateAdded DESC"
    return createFlow(__db, false, arrayOf("blocked_domains")) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        val _columnIndexOfDomain: Int = getColumnIndexOrThrow(_stmt, "domain")
        val _columnIndexOfReason: Int = getColumnIndexOrThrow(_stmt, "reason")
        val _columnIndexOfDateAdded: Int = getColumnIndexOrThrow(_stmt, "dateAdded")
        val _result: MutableList<BlockedDomain> = mutableListOf()
        while (_stmt.step()) {
          val _item: BlockedDomain
          val _tmpDomain: String
          _tmpDomain = _stmt.getText(_columnIndexOfDomain)
          val _tmpReason: String
          _tmpReason = _stmt.getText(_columnIndexOfReason)
          val _tmpDateAdded: Long
          _tmpDateAdded = _stmt.getLong(_columnIndexOfDateAdded)
          _item = BlockedDomain(_tmpDomain,_tmpReason,_tmpDateAdded)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun getAllBlockedDomains(): List<BlockedDomain> {
    val _sql: String = "SELECT * FROM blocked_domains"
    return performSuspending(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        val _columnIndexOfDomain: Int = getColumnIndexOrThrow(_stmt, "domain")
        val _columnIndexOfReason: Int = getColumnIndexOrThrow(_stmt, "reason")
        val _columnIndexOfDateAdded: Int = getColumnIndexOrThrow(_stmt, "dateAdded")
        val _result: MutableList<BlockedDomain> = mutableListOf()
        while (_stmt.step()) {
          val _item: BlockedDomain
          val _tmpDomain: String
          _tmpDomain = _stmt.getText(_columnIndexOfDomain)
          val _tmpReason: String
          _tmpReason = _stmt.getText(_columnIndexOfReason)
          val _tmpDateAdded: Long
          _tmpDateAdded = _stmt.getLong(_columnIndexOfDateAdded)
          _item = BlockedDomain(_tmpDomain,_tmpReason,_tmpDateAdded)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun deleteAll() {
    val _sql: String = "DELETE FROM blocked_domains"
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
