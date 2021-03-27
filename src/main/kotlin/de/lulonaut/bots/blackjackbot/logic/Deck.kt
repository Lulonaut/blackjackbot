package de.lulonaut.bots.blackjackbot.logic

class Deck {

    var deck: MutableList<Card> = mutableListOf()

    init {
        //initialize the deck and shuffle it
        initDeck()
        shuffleDeck()
    }

    private fun initDeck() {
        //loop through the suits and add all values
        for (suit in Suits.values()) {
            //ace
            deck.add(Card(CardTypes.ACE, suit, 11))
            //king
            deck.add(Card(CardTypes.KING, suit, 10))
            //queen
            deck.add(Card(CardTypes.QUEEN, suit, 10))
            //jack
            deck.add(Card(CardTypes.JACK, suit, 10))
            //numbers
            for (i in 2..10) {
                deck.add(Card(CardTypes.NUMBER, suit, i))
            }
        }
    }

    fun getCard(): Card {
        //choose a random card and remove it from the deck
        val index = (0 until deck.size).random()
        val card = deck[index]
        deck.removeAt(index)
        return card
    }

    private fun shuffleDeck() {
        deck.shuffle()
    }
}