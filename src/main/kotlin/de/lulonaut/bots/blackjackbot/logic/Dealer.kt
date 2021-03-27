package de.lulonaut.bots.blackjackbot.logic

class Dealer(private val player: Player, private val deck: Deck) {
    var hand: MutableList<Card> = mutableListOf()
    var cardValue: Int = 0

    init {
    }

    fun hit() {
        val card = deck.getCard()
        cardValue += card.value
        hand.add(card)
    }


}