package com.mock.data.database

import com.mock.Environment
import com.mock.config
import com.mock.data.database.entity.NoteEntity
import com.mock.data.database.entity.UserEntity
import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import org.jetbrains.exposed.sql.transactions.transaction

class DatabaseFactory {

    fun init() {
        var url = config[Environment.DB_URL]!!
        var driver = config[Environment.DB_DRIVER]!!

        val connectionPool = createHikari(
            url = url,
            driver = driver,
        )
        Database.connect(connectionPool)
        transaction {
            SchemaUtils.create(UserEntity)
            SchemaUtils.create(NoteEntity)
        }
    }

    private fun createHikari(url: String, driver: String) = HikariDataSource(HikariConfig().apply {
        driverClassName = driver
        jdbcUrl = url
        maximumPoolSize = 15
        isAutoCommit = false
        transactionIsolation = "TRANSACTION_REPEATABLE_READ"
        validate()
    })
}

suspend fun <T> query(
    block: () -> T): T =
    newSuspendedTransaction(Dispatchers.IO) { block() }