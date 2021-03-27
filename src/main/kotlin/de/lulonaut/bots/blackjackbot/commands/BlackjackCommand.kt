package de.lulonaut.bots.blackjackbot.commands

import de.lulonaut.bots.blackjackbot.Constants
import de.lulonaut.bots.blackjackbot.logic.BlackjackGame
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent
import net.dv8tion.jda.api.hooks.ListenerAdapter

class BlackjackCommand : ListenerAdapter() {
    override fun onGuildMessageReceived(event: GuildMessageReceivedEvent) {
        val msg: String = event.message.contentRaw
        if (!Constants.commandName.contains(msg)) return


        if (PlayingPlayers.playingPlayers.contains(event.author)) return
        val game = BlackjackGame(event.author, event.channel, event)
        event.jda.addEventListener(game)
        PlayingPlayers.playingPlayers.add(event.author)
        Thread {
            try {
                Thread.sleep(300000)
                PlayingPlayers.playingPlayers.remove(event.author)
                event.jda.removeEventListener(game)
            } catch (e: InterruptedException) {
                PlayingPlayers.playingPlayers.remove(event.author)
                event.jda.removeEventListener(game)
            }
        }
    }
}