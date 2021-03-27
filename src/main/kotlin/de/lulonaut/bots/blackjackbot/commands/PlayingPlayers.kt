package de.lulonaut.bots.blackjackbot.commands

import net.dv8tion.jda.api.entities.User

object PlayingPlayers {
    var playingPlayers = mutableListOf<User>()
}