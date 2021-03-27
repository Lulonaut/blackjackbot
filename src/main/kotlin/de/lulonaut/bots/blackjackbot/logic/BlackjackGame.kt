package de.lulonaut.bots.blackjackbot.logic

import de.lulonaut.bots.blackjackbot.Constants
import de.lulonaut.bots.blackjackbot.commands.PlayingPlayers
import net.dv8tion.jda.api.EmbedBuilder
import net.dv8tion.jda.api.entities.Message
import net.dv8tion.jda.api.entities.MessageChannel
import net.dv8tion.jda.api.entities.User
import net.dv8tion.jda.api.events.message.MessageReceivedEvent
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent
import net.dv8tion.jda.api.hooks.ListenerAdapter
import java.awt.Color

class BlackjackGame(
    private var user: User,
    private var channel: MessageChannel,
    private var event: GuildMessageReceivedEvent
) : ListenerAdapter() {
    private lateinit var player: Player
    private lateinit var dealer: Dealer
    private var embedMessageID: Long = 0
    private val deck: Deck = Deck()
    private var inProgress = false
    private var dealerJustHit17 = false
    private var playerCompletedHand = false
    private var embedSend = false

    init {
        sendInitialMessage()
    }

    override fun onMessageReceived(event: MessageReceivedEvent) {
        //println("game:" + event.message.contentRaw)
        val msg = event.message.contentRaw
        //check if message is from user playing the game
        if (event.author.isBot || event.channel.idLong != channel.idLong || event.author.idLong != user.idLong) return

        //initial message event, ignore
        if (Constants.commandName.contains(msg)) {
            return
        }

        //player hitting
        if (event.message.contentRaw == "hit" || event.message.contentRaw == "h") {
            player.hit()

            checkIfSomeoneWon()
            if (!inProgress) return
            sendMessageOrEditEmbed(showCurrentHands().setColor(Color.yellow))
        }

        if (event.message.contentRaw == "stand" || event.message.contentRaw == "s") {
            playerCompletedHand = true
            while (dealer.cardValue < 17) {
                dealer.hit()
                dealerJustHit17 = true
            }
            checkIfSomeoneWon()
            if (!inProgress) return

            sendMessageOrEditEmbed(showCurrentHands().setColor(Color.yellow))
        }
    }

    private fun sendInitialMessage() {
        inProgress = true
        player = Player(deck)
        dealer = Dealer(player, deck)

        //dealer gets one and player gets two cards
        dealer.hit()
        player.hit()
        player.hit()

        sendMessageOrEditEmbed(showCurrentHands().setColor(Color.yellow))
    }

    private fun showCurrentHands(): EmbedBuilder {
        val eb = EmbedBuilder()
        eb.setTitle("Blackjack")
        var playerHand = ""
        for (i in player.hand) {
            playerHand += getShortCardVersion(i)
        }
        var dealerHand = ""
        for (i in dealer.hand) {
            dealerHand += getShortCardVersion(i)
        }
        eb.addField("Your Hand", playerHand + "\nValue: " + player.cardValue, true)
        eb.addField("Dealer Hand", dealerHand + "\nValue: " + dealer.cardValue, true)
        return eb
    }

    private fun getShortCardVersion(card: Card): String {
        val emoji: String = when (card.suits) {
            Suits.CLUBS -> {
                ":clubs:"
            }
            Suits.DIAMONDS -> {
                ":diamonds:"
            }
            Suits.HEARTS -> {
                ":hearts:"
            }
            Suits.SPADES -> {
                ":spades:"
            }
        }
        val value: String = when (card.type) {
            CardTypes.QUEEN -> {
                "Q"
            }
            CardTypes.KING -> {
                "K"
            }
            CardTypes.ACE -> {
                "A"
            }
            CardTypes.JACK -> {
                "J"
            }
            CardTypes.NUMBER -> {
                card.value.toString()
            }

        }
        return value + emoji

    }

    private fun checkIfSomeoneWon() {
        //push (both dealer and player have same value)
        if (player.cardValue == dealer.cardValue) {
            pushGameEnd()
            return
        }
        //player bust (over 21)
        if (player.cardValue > 21) {
            playerBustEnd()
            return
        }
        //dealer bust (over 21)
        if (dealer.cardValue > 21) {
            dealerBustEnd()
            return
        }
        //player has won normally
        if (player.cardValue > dealer.cardValue && playerCompletedHand) {
            playerWinEnd()
            return
        }
        //dealer has won normally
        if (dealer.cardValue > player.cardValue && playerCompletedHand) {
            dealerWinEnd()
            return
        }
    }

    private fun pushGameEnd() {
        val eb = showCurrentHands()
        eb.setColor(Color.black)
        eb.addField("Game over!", "Push, no one won.", false)
        end(eb)
    }

    private fun playerBustEnd() {
        val eb = showCurrentHands()
        eb.setColor(Color.red)
        eb.addField("Game over!", "Player busted, dealer won.", false)
        end(eb)
    }

    private fun dealerBustEnd() {
        val eb = showCurrentHands()
        eb.setColor(Color.green)
        eb.addField("Game over!", "Dealer busted, player won.", false)
        end(eb)
    }

    private fun dealerWinEnd() {
        val eb = showCurrentHands()
        eb.setColor(Color.red)
        eb.addField("Game over!", "Dealer got a higher endscore than the player and won.", false)
        end(eb)
    }

    private fun playerWinEnd() {
        val eb = showCurrentHands()
        eb.setColor(Color.green)
        eb.addField("Game over!", "Player got a higher endscore than the dealer and won.", false)
        end(eb)
    }

    private fun end(embedBuilder: EmbedBuilder) {
        inProgress = false
        sendMessageOrEditEmbed(embedBuilder)
        PlayingPlayers.playingPlayers.remove(user)
        event.jda.removeEventListener(this)
    }

    private fun sendMessageOrEditEmbed(embedBuilder: EmbedBuilder) {
        if (!embedSend) {
            channel.sendMessage(embedBuilder.build()).queue { message: Message ->
                embedMessageID = message.idLong
            }
            embedSend = true
        } else {
            channel.editMessageById(embedMessageID, embedBuilder.build()).queue()
        }

    }
}