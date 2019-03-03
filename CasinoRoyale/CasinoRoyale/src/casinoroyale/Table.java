package casinoroyale;

import java.util.Vector;

public class Table {
    private Vector<Integer> deck = new Vector<Integer>();
    private Vector<Integer> pot = new Vector<Integer>();
    private Vector<Integer> river = new Vector<Integer>();
    private int betMinimum = 0;
    private int maxBet = 0;

    public Table(){
        this.initializeDeck();
        this.pot.add(0);
    }

    public boolean deckContainsCard(int cardNumber){
        if(deck.contains(cardNumber))
            return true;
        return false;
    }

    public void addCardToRiver(int cardNumber){
        this.river.add(cardNumber);
    }

    public Vector<Integer> getRiverCards(){
        return this.river;
    }

    public void removeCard(int cardNumber){
        deck.remove((Object)cardNumber);
    }

    public int getTableMinimum(){
        return this.betMinimum;
    }
    public void resetDeck(){
        this.deck.removeAllElements();
        this.initializeDeck();
        this.pot.removeAllElements();
        this.pot.add(0);
    }

    private void initializeDeck(){
        for(int i = 0; i < 52; i++)
            deck.add(i);
    }

    public void addMoneyToPot(int amount){
        this.pot.set(0, this.pot.elementAt(0) + amount);
    }

    public int getPot(){
        return this.pot.elementAt(0);
    }

    public int getMaxBet(){
        return this.maxBet;
    }

    public void setMaxBet(int amount){
        this.maxBet = amount;
    }

    public void resetTable(){
        this.betMinimum += 0;
        this.resetDeck();
        this.pot.set(0, 0);
        this.maxBet = 0;
        this.river.removeAllElements();
    }
}
