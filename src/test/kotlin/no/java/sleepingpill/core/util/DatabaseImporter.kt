package no.java.sleepingpill.core.util

import no.java.sleepingpill.core.SparkStart
import java.io.File


object DatabaseImporter {
    @JvmStatic
    fun main(args: Array<String>) {
        if (args.size < 2) {
            println("Usega Databaseimporter <configfile> <importfile>")
            return
        }
        SparkStart.setConfigFile(args)
        val allLines = File(args[1]).readLines()
        var i = allLines.size
        println("Starting with $i")
        for (line in allLines) {
            i--
            if (i%100 == 0) {
                println(i)
            }
            handleLine(line)
        }
        println("Done")
    }

    private fun handleLine(line:String) {
        val updated = line.replace("public.","")
        DatabaseUtil.withStatement(updated) {
            it.executeUpdate()
        }
    }

}