package Controllers;

import casinoroyale.Game;
import casinoroyale.Player;
import java.util.Vector;

public class CountDownPokerController extends iGameController{

    private Game pokerGame;

    public void setGame(Game game) {
        this.pokerGame = game;
    }

    public void dealCards() {
        int dealToPlayer;
        int randomCard;

        reset();
        for (int i = 0; i < this.pokerGame.getPlayers().size() * 5; i++) {
            randomCard = (int) (Math.random() * 52);
            while (pokerGame.getTable().deckContainsCard(randomCard) == false) {
                randomCard = (int) (Math.random() * 52);
            }
            dealToPlayer = i % this.pokerGame.getPlayers().size();
            if (this.pokerGame.getPlayers().elementAt(dealToPlayer).getIsPlaying()) {
                this.pokerGame.getPlayers().elementAt(dealToPlayer).addCard(randomCard);
                this.pokerGame.getTable().removeCard(randomCard);
            } else {
                this.pokerGame.getPlayers().elementAt(dealToPlayer).addCard(53);
            }
        }
    }

    public boolean bet(int amount) {
        if ((amount <= this.pokerGame.getCurrentPlayer().getMoney() //check if they have enough money
                && amount >= (this.pokerGame.getTable().getMaxBet() - this.pokerGame.getCurrentPlayer().getCurrentBet())) //check if they are matching the current bet
                || amount == this.pokerGame.getCurrentPlayer().getMoney()) {    //check if they are all in

            if (amount == this.pokerGame.getCurrentPlayer().getMoney()) {    //check if they are all in
                this.pokerGame.getCurrentPlayer().setAllIn(true);
            }

            if (amount > this.pokerGame.getTable().getMaxBet()) {   //check if they raised the bet
                this.pokerGame.getTable().setMaxBet(amount);
                this.pokerGame.resetSuccessfulBets();
            }

            this.pokerGame.getCurrentPlayer().setCurrentBet(amount);
            this.pokerGame.getCurrentPlayer().decreaseMoney(amount);    //subtract bet from player's money
            this.pokerGame.getTable().addMoneyToPot(amount);    //add money to pot
            this.pokerGame.incrementSuccessfulBets();
            this.checkForEndOfRound();
            return true;
        } else {
            return false;
        }
    }

    public boolean check() {
        if (this.pokerGame.getTable().getMaxBet() == 0) { //if the current bet is 0, they can check
            this.pokerGame.incrementSuccessfulBets();
            this.checkForEndOfRound();
            return true;
        } else {
            return false;
        }
    }

    public void fold() {
        this.pokerGame.getCurrentPlayer().foldPlayer();
        this.checkForEndOfRound();
    }

    public void tradeIn(Vector<Integer> burnCards) {
        //remove trade-in cards from hand and get new cards
        int randomCard;
        for (int i = 0; i < burnCards.size(); i++) {
            if (!burnCards.elementAt(i).equals(56)) {
                this.pokerGame.getCurrentPlayer().removeCard(burnCards.elementAt(i));
                randomCard = (int) (Math.random() * 52);
                while (pokerGame.getTable().deckContainsCard(randomCard) == false) {
                    randomCard = (int) (Math.random() * 52);
                }
                this.pokerGame.getCurrentPlayer().addCard(randomCard);
                this.pokerGame.getTable().removeCard(randomCard);
            }
        }
        this.pokerGame.incrementSuccessfulBets();
        this.checkForEndOfRound();
    }

    public void reset() {
        this.pokerGame.resetGame();
    }

    public void endGame() {
        this.server.endGame();
    }

    private void nextPlayer() {
        do {
            if ((this.pokerGame.getCurrentPlayerNumber() + 1) < this.pokerGame.getPlayers().size()) //if the next player in line is less than the number of players,
            {
                this.pokerGame.setCurrentPlayer(this.pokerGame.getCurrentPlayerNumber() + 1);      //add 1 to current player
            } else {
                this.pokerGame.setCurrentPlayer(0);
            }

        } while (!this.pokerGame.getCurrentPlayer().getIsPlaying()); //keep doing this while the current player isnt playing

        if (this.pokerGame.getCurrentPlayer().getAllIn() && this.pokerGame.getRound() != 1 &&
                this.pokerGame.getRound() != 3 && this.pokerGame.getRound() != 5) {
            this.pokerGame.incrementSuccessfulBets();
            this.checkForEndOfRound();
        }
    }

    private void checkForEndOfRound() {
        if (this.pokerGame.getSuccessfulBets() == this.pokerGame.playersStillIn()
                || this.pokerGame.playersStillIn() == 1) {  //if everybody matched the bet, increment the round
            this.pokerGame.incrementRound();
            this.pokerGame.resetSuccessfulBets();
            this.pokerGame.getTable().setMaxBet(0);
            for (int i = 0; i < this.pokerGame.getPlayers().size(); i++) {
                this.pokerGame.getPlayers().elementAt(i).setCurrentBet(0);
            }

            if (this.pokerGame.getPlayersAllIn() == this.pokerGame.playersStillIn()) {
                if(this.pokerGame.getRound() == 2)
                    this.pokerGame.setRound(3);
                else if(this.pokerGame.getRound() == 4)
                    this.pokerGame.setRound(5);
                else if(this.pokerGame.getRound() == 6)
                    this.pokerGame.setRound(7);
            }

            if (this.pokerGame.getRound() == 7 || this.pokerGame.playersStillIn() == 1) { //if its the 4th round or there is only 1 player left
                this.getWinner().addMoney(this.pokerGame.getTable().getPot());    //add money to winner's wallet
                this.server.gameOver();
                if (this.pokerGame.isEndOfGame()) {
                    this.server.endGame();
                }
                this.changeDealer();
            }
            this.getNextPlayer();
        } else {
            this.nextPlayer();
        }
    }

    private void changeDealer() {
        int oldDealer = this.pokerGame.getDealer();
        this.pokerGame.incrementDealer();
        int newDealer = -1;
        while (newDealer == -1 && this.pokerGame.getDealer() < this.pokerGame.getPlayers().size()) {
            if (this.pokerGame.getPlayers().elementAt(this.pokerGame.getDealer()).getIsPlaying()) {
                newDealer = this.pokerGame.getDealer();
            }
            this.pokerGame.incrementDealer();
        }
        if (newDealer == -1) {
            this.pokerGame.setDealer(0);
            while (newDealer == -1 && this.pokerGame.getDealer() < oldDealer) {
                if (this.pokerGame.getPlayers().elementAt(this.pokerGame.getDealer()).getIsPlaying()) {
                    newDealer = this.pokerGame.getDealer();
                }
                this.pokerGame.incrementDealer();
            }

        }
        this.pokerGame.setDealer(newDealer);
        this.getNextPlayer();
    }

    private void getNextPlayer() {
        this.pokerGame.setCurrentPlayer(this.pokerGame.getDealer());
        int newPlayer = -1;
        this.pokerGame.incrementCurrentPlayer();
        while (newPlayer == -1 && this.pokerGame.getCurrentPlayerNumber() < this.pokerGame.getPlayers().size()) {
            if (this.pokerGame.getPlayers().elementAt(this.pokerGame.getCurrentPlayerNumber()).getIsPlaying()) {
                if (this.pokerGame.getPlayers().elementAt(this.pokerGame.getCurrentPlayerNumber()).getAllIn()
                        && this.pokerGame.getRound() != 1 &&
                this.pokerGame.getRound() != 3 && this.pokerGame.getRound() != 5) {
                    this.pokerGame.incrementSuccessfulBets();
                } else {
                    newPlayer = this.pokerGame.getCurrentPlayerNumber();
                }
            }
            this.pokerGame.incrementCurrentPlayer();
        }
        if (newPlayer == -1) {
            this.pokerGame.setCurrentPlayer(0);
            while (newPlayer == -1 && this.pokerGame.getCurrentPlayerNumber() < this.pokerGame.getDealer()) {
                if (this.pokerGame.getPlayers().elementAt(this.pokerGame.getCurrentPlayerNumber()).getIsPlaying()) {
                    if (this.pokerGame.getPlayers().elementAt(this.pokerGame.getCurrentPlayerNumber()).getAllIn()
                            && this.pokerGame.getRound() != 1 &&
                this.pokerGame.getRound() != 3 && this.pokerGame.getRound() != 5) {
                        this.pokerGame.incrementSuccessfulBets();
                    } else {
                        newPlayer = this.pokerGame.getCurrentPlayerNumber();
                    }
                }
                this.pokerGame.incrementCurrentPlayer();
            }

        }
        if (newPlayer == -1) {
            this.pokerGame.setCurrentPlayer(this.pokerGame.getDealer());
        } else {
            this.pokerGame.setCurrentPlayer(newPlayer);
        }
    }

    public Player getWinner() {
        Vector<Integer> tiedPlayers = new Vector<Integer>();
        int highScore = 0;
        int playerLocation = 0;
        for (int i = 0; i < this.pokerGame.getPlayers().size(); i++) {    //go through all players and check if they are playing or they are all in
            if ((this.getHandScore(this.pokerGame.getPlayers().elementAt(i)) > highScore)
                    && (this.pokerGame.getPlayers().elementAt(i).getAllIn()
                    || this.pokerGame.getPlayers().elementAt(i).getIsPlaying())) {
                tiedPlayers.removeAllElements();
                tiedPlayers.add(i);
                highScore = this.pokerGame.getPlayers().elementAt(i).getHandScore();    //set the current high score
                playerLocation = i;
            } else if ((this.getHandScore(this.pokerGame.getPlayers().elementAt(i)) == highScore)
                    && (this.pokerGame.getPlayers().elementAt(i).getAllIn()
                    || this.pokerGame.getPlayers().elementAt(i).getIsPlaying())) {
                tiedPlayers.add(i);
            }
        }
        if (tiedPlayers.size() > 1) {
            highScore = 0;
            playerLocation = 0;
            for(int i = 0; i < tiedPlayers.size(); i++){
                if(this.rankHighCard(this.pokerGame.getPlayers().elementAt(i)) > highScore){
                    highScore = this.rankHighCard(this.pokerGame.getPlayers().elementAt(i));
                    playerLocation = i;
                }
            }

        }
        return this.pokerGame.getPlayers().elementAt(playerLocation);       //return the winner
    }

    private int getHandScore(Player playerToScore) {
        if (playerToScore.getCardsInHand().elementAt(0).getCardNumber() == 53) {
            playerToScore.setHandName("Not Playing");
            playerToScore.setHandScore(0);
            return 0;
        }
        this.cardNumbers.removeAllElements();
        this.cards.removeAllElements();
        this.cardCount.removeAllElements();
        this.cardsToRank.removeAllElements();

        for (int i = 0; i < playerToScore.getCardsInHand().size(); i++) {
            this.cards.add(playerToScore.getCardsInHand().elementAt(i).getCardNumber());    //add players cards to cards
        }
        for (int i = 0; i < this.pokerGame.getTable().getRiverCards().size(); i++) {
            this.cards.add(this.pokerGame.getTable().getRiverCards().elementAt(i)); //add river cards to cards
        }
        for (int i = 0; i < cards.size(); i++) {
            this.cardNumbers.add(cards.elementAt(i) % 13);  //get all card numbers
        }
        for (int i = 0; i < 13; i++) {
            this.cardCount.add(0);
        }

        for (int i = 0; i < this.cards.size(); i++) {
            this.cardCount.set(this.cards.elementAt(i) % 13, this.cardCount.elementAt(this.cards.elementAt(i) % 13) + 1);
        }


        if (checkForStraightFlush(playerToScore) > 0) {
            return playerToScore.getHandScore();
        } else if (checkForFourOfAKind(playerToScore) > 0) {
            return playerToScore.getHandScore();
        } else if (checkForFullHouse(playerToScore) > 0) {
            return playerToScore.getHandScore();
        } else if (checkForFlush(playerToScore) > 0) {
            return playerToScore.getHandScore();
        } else if (checkForStraight(playerToScore) > 0) {
            return playerToScore.getHandScore();
        } else if (checkForThreeOfAKind(playerToScore) > 0) {
            return playerToScore.getHandScore();
        } else if (checkForTwoPair(playerToScore) > 0) {
            return playerToScore.getHandScore();
        } else if (checkForPair(playerToScore) > 0) {
            return playerToScore.getHandScore();
        } else {
            return rankHighCard(playerToScore);
        }
    }

    private int checkForStraightFlush(Player playerToScore) {
        if (this.checkForFlush(playerToScore) > 0) {  //if you have a flush, check for straight
            if (this.checkForStraight(playerToScore) > 0) {
                playerToScore.setHandName("Straight Flush");
                playerToScore.setHandScore(playerToScore.score + 8000000);
                return playerToScore.getHandScore();
            }
        }
        return 0;
    }

    private int checkForFourOfAKind(Player playerToScore) {
        for (int i = this.cardCount.size() - 1; i >= 0; i--) {
            if (this.cardCount.elementAt(i) == 4) {
                playerToScore.setHandName("Four Of A Kind");
                playerToScore.setHandScore(7000000 + i);
                return playerToScore.getHandScore();
            }
        }
        return 0;
    }

    private int checkForFullHouse(Player playerToScore) {
        int firstThreeOfAKind = -1;
        int firstPair = -1;
        for (int i = this.cardCount.size() - 1; i >= 0; i--) {
            if (this.cardCount.elementAt(i) >= 2) {
                if (this.cardCount.elementAt(i) == 3 && firstThreeOfAKind == -1) {
                    firstThreeOfAKind = i;
                } else if (firstPair == -1) {
                    firstPair = i;
                }
                if (firstPair != -1 && firstThreeOfAKind != -1) {
                    playerToScore.setHandName("Full House");
                    playerToScore.setHandScore(6000000 + (firstThreeOfAKind * 12) + firstPair);
                    return playerToScore.getHandScore();
                }
            }
        }
        return 0;
    }

    private int checkForFlush(Player playerToScore) {

        int spades = 0;
        int clubs = 0;
        int diamonds = 0;
        int hearts = 0;

        for (int i = 0; i < this.cards.size(); i++) { //count number of suit cards
            if (cards.elementAt(i) <= 12) {
                spades++;
            } else if (cards.elementAt(i) <= 25) {
                clubs++;
            } else if (cards.elementAt(i) <= 38) {
                diamonds++;
            } else {
                hearts++;
            }
        }

        for (int i = 0; i < this.cards.size(); i++) { //count number of suit cards
            if (spades >= 5 && cards.elementAt(i) <= 12) {
                this.cardsToRank.add(cards.elementAt(i) % 13);

            } else if (clubs >= 5 && cards.elementAt(i) <= 25) {
                this.cardsToRank.add(cards.elementAt(i) % 13);

            } else if (diamonds >= 5 && cards.elementAt(i) <= 38) {
                this.cardsToRank.add(cards.elementAt(i) % 13);

            } else if (hearts >= 5) {
                this.cardsToRank.add(cards.elementAt(i) % 13);

            }
        }

        if (spades >= 5 || clubs >= 5 || diamonds >= 5 || hearts >= 5) {  //if you have 5 of one suit
            playerToScore.setHandName("Flush");
            playerToScore.setHandScore(5000000 + this.rankHighCard(playerToScore));
            return playerToScore.getHandScore();
        }
        return 0;
    }

    private int checkForStraight(Player playerToScore) {
        if (this.cardsToRank.size() == 0) {
            for (int j = 12; j >= 4; j--) {
                if (this.cardCount.elementAt(j) >= 1 && this.cardCount.elementAt(j - 1) >= 1 && this.cardCount.elementAt(j - 2) >= 1
                        && this.cardCount.elementAt(j - 3) >= 1 && this.cardCount.elementAt(j - 4) >= 1) { //check if it has 5 cards in a row
                    playerToScore.setHandName("Straight");
                    playerToScore.setHandScore(4000000 + (j + (j - 1) + (j - 2) + (j - 3) + (j - 4)));
                    return playerToScore.getHandScore();
                }
            }
            if (this.cardCount.elementAt(12) >= 1 && this.cardCount.elementAt(0) >= 1 && this.cardCount.elementAt(1) >= 1
                    && this.cardCount.elementAt(2) >= 1 && this.cardCount.elementAt(3) >= 1) { //check if it has 5 cards in a row Ace,2,3,4,
                playerToScore.setHandName("Straight");
                playerToScore.setHandScore(4000000 + 0 + 1 + 2 + 3);
                return playerToScore.getHandScore();
            }
        } else {
            for (int i = 0; i >= this.cardsToRank.size(); i++) {
                if (this.cardsToRank.contains(this.cardsToRank.elementAt(i) + 1) && this.cardsToRank.contains(this.cardsToRank.elementAt(i) + 2)
                        && this.cardsToRank.contains(this.cardsToRank.elementAt(i) + 3) && this.cardsToRank.contains(this.cardsToRank.elementAt(i) + 4)) { //check if it has 5 cards in a row
                    playerToScore.setHandName("Straight");
                    playerToScore.setHandScore(4000000 + (i + (i + 1) + (i + 2) + (i + 3) + (i + 4)));
                    return playerToScore.getHandScore();
                }
            }
            if (this.cardsToRank.contains(12) && this.cardsToRank.contains(0)
                    && this.cardsToRank.contains(1) && this.cardsToRank.contains(2)
                    && this.cardsToRank.contains(3)) { //check if it has 5 cards in a row Ace,2,3,4,
                playerToScore.setHandName("Straight");
                playerToScore.setHandScore(4000000 + 0 + 1 + 2 + 3);
                return playerToScore.getHandScore();
            }
        }
        return 0;
    }

    private int checkForThreeOfAKind(Player playerToScore) {
        for (int i = 12; i >= 0; i--) {
            if (this.cardCount.elementAt(i) == 3) {
                playerToScore.setHandName("Three Of A Kind");
                playerToScore.setHandScore(3000000 + ((int) (800000 / Math.pow((int) 3, (int) (12 - i)))));
                return playerToScore.getHandScore();
            }
        }
        return 0;
    }

    private int checkForTwoPair(Player playerToScore) {
        int firstPair = -1;
        for (int i = 12; i >= 0; i--) {
            if (this.cardCount.elementAt(i) == 2) {
                if (firstPair == -1) {
                    firstPair = i;
                } else {
                    playerToScore.setHandScore(1);
                    playerToScore.setHandName("Two Pair");
                    playerToScore.setHandScore(2000000 + ((int) (400000 / Math.pow((int) 3, (int) (12 - firstPair)))) + ((int) (400000 / Math.pow((int) 3, (int) (12 - i)))));
                    return playerToScore.getHandScore();
                }
            }
        }
        return 0;
    }

    private int checkForPair(Player playerToScore) {
        for (int i = 12; i >= 0; i--) {
            if (this.cardCount.elementAt(i) == 2) {
                playerToScore.setHandName("One Pair");
                playerToScore.setHandScore(1000000 + ((int) (800000 / Math.pow((int) 3, (int) (12 - i)))));
                return playerToScore.getHandScore();
            }
        }
        return 0;
    }

    private int rankHighCard(Player playerToScore) {
        this.cardNumbers.removeAllElements();
        for (int i = 0; i < playerToScore.getCardsInHand().size(); i++) {
            this.cardNumbers.add(playerToScore.getCardsInHand().elementAt(i).getCardNumber() % 13);  //get all card numbers
        }
        int score = 0;
        if (this.cardsToRank.size() > 0) {
            for (int i = 0; i < this.cardsToRank.size(); i++) {
                score += (99999 / Math.pow((int) 5, (int) (12 - this.cardsToRank.elementAt(i))));
            }
        } else {
            for (int i = 0; i < this.cardNumbers.size(); i++) {
                score += (99999 / Math.pow((int) 5, (int) (12 - this.cardNumbers.elementAt(i))));
            }
        }

        if (playerToScore.getHandName() == "") {
            playerToScore.setHandName("High Card");
            playerToScore.setHandScore(score);
        }
        return score;
    }
}
