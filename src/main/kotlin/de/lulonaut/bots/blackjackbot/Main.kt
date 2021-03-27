package de.lulonaut.bots.blackjackbot

import de.lulonaut.bots.blackjackbot.commands.BlackjackCommand
import net.dv8tion.jda.api.JDA
import net.dv8tion.jda.api.JDABuilder
import java.io.IOException
import javax.security.auth.login.LoginException
import kotlin.system.exitProcess

object Main {
    private lateinit var jda: JDA

    @Throws(IOException::class, InterruptedException::class)
    @JvmStatic
    fun main(args: Array<String>) {
        try {
            //start bot with token
            jda = JDABuilder.createDefault("")
                .build()
        } catch (e: LoginException) {
            println("The Token is invalid! Please check your config.")
            exitProcess(1)
        } catch (e: Exception) {
            println("There was an error while logging in, please try again and check your config!")
            e.printStackTrace()
            exitProcess(1)
        }
        registerEvents()
        registerCommands()
        println("finished Loading.")
    }

    private fun registerEvents() {
    }

    private fun registerCommands() {
        jda.addEventListener(BlackjackCommand())
    }
}
