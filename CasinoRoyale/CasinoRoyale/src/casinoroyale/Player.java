package casinoroyale;

import java.util.Vector;

public class Player {

    private String name;
    private int money = 500;
    private Vector<Card> hand = new Vector<Card>();
    private int currentBet = 0;
    private boolean isPlaying = true;
    private boolean allIn = false;
    public String handRank = "";
    public int score;
////////////////////////////////////////////////////////////////////////////////

    public Player() {
    }

    public Player(String name) {
        this.name = name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean getIsPlaying() {
        return this.isPlaying;
    }

    public boolean getAllIn() {
        return this.allIn;
    }

    public void setAllIn(boolean allIn) {
        this.allIn = allIn;
    }

    public void foldPlayer() {
        this.resetHand();
        for (int i = 0; i < 5; i++) {
            this.addCard(53);
        }
        this.isPlaying = false;
    }

    public void setIsPlaying(boolean isPlaying) {
        this.isPlaying = isPlaying;
    }

    public void addMoney(int amount) {
        this.money = this.money + amount;
    }

    public String getName() {
        return this.name;
    }

    public void setMoney(int amount) {
        this.money = amount;
    }

    public int getMoney() {
        return this.money;
    }

    public Vector<Card> getCardsInHand() {
        return this.hand;
    }

    public void addCard(int cardNumber) {
        Card card = new Card(cardNumber);
        hand.add(card);
    }

    public void removeCard(int cardNumber) {
        for (int i = 0; i < this.getCardsInHand().size(); i++) {
            if (this.getCardsInHand().elementAt(i).getCardNumber() == cardNumber) {
                this.getCardsInHand().remove(this.getCardsInHand().elementAt(i));
            }
        }
    }

    public void decreaseMoney(int amount) {
        this.money = this.money - amount;
    }

    public int getCurrentBet() {
        return this.currentBet;
    }

    public void setCurrentBet(int amount) {
        this.currentBet = amount;
    }

    public void resetHand() {
        this.hand.removeAllElements();
    }

    public String getHandName() {
        return this.handRank;
    }

    public int getHandScore() {
        return this.score;
    }

    public void setHandScore(int score) {
        this.score = score;
    }

    public void resetPlayer() {
        this.resetHand();
        this.currentBet = 0;
        this.score = 0;
        this.handRank = "";
        this.allIn = false;
        if (this.money > 0) {
            this.isPlaying = true;
        } else {
            this.isPlaying = false;
        }
    }

    public void setHandName(String name) {
        this.handRank = name;
    }
}
