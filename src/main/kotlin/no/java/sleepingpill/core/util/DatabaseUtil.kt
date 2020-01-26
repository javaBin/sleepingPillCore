package no.java.sleepingpill.core.util

import no.java.sleepingpill.core.database.Postgres
import java.sql.Connection
import java.sql.PreparedStatement

object DatabaseUtil {
    fun <T> withStatement(sql:String,doWithStatement: (PreparedStatement) -> T):T {
        return Postgres.openConnection().use {conn ->
            conn.prepareStatement(sql).use(doWithStatement)
        }
    }
}