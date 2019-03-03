package Controllers;

import casinoroyale.Game;
import casinoroyale.Player;
import casinoroyale.iServer;
import java.util.Vector;

public abstract class iGameController {

    protected iServer server;
    protected Game pokerGame;
    protected Vector<Integer> cards = new Vector<Integer>();
    protected Vector<Integer> cardNumbers = new Vector<Integer>();
    protected Vector<Integer> cardCount = new Vector<Integer>();
    protected Vector<Integer> cardsToRank = new Vector<Integer>();

    public void setServer(iServer server) {
        this.server = server;
    }

    public abstract void dealCards();

    public void setGame(Game game) {
        pokerGame = game;  //set the game
    }

    public abstract boolean bet(int amount);

    public abstract boolean check();

    public abstract void fold();

    public abstract void tradeIn(Vector<Integer> burnCards);

    public abstract Player getWinner();
}