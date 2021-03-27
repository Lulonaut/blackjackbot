package de.lulonaut.bots.blackjackbot.logic

class Player(private var deck: Deck) {
    var hand: MutableList<Card> = mutableListOf()
    var cardValue: Int = 0

    fun hit() {
        val card = deck.getCard()
        cardValue += card.value
        hand.add(card)
    }

}