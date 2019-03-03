package casinoroyale;

import Controllers.iGameController;
import java.util.Vector;

public class Game {
    private Vector<Player> players = new Vector<Player>();
    private Table table = new Table();
    private int currentPlayer = 1;
    private int dealer = 0;
    private int round = 0;
    private int successfulBets = 0;
    private iGameController controller;
    private iServer server;

    public Vector<Player> getPlayers(){
        return this.players;
    }
    public void setServer(iServer serv){
        this.server = serv;
    }

    public void setController(iGameController controller){
        this.controller = controller;
    }

    public void addPlayer(String name){
        Player player = new Player(name);
        players.add(player);
    }

    public void removePlayer(int playerNumber){
        this.players.elementAt(playerNumber).setName("");
        this.players.elementAt(playerNumber).setMoney(0);
        this.players.elementAt(playerNumber).setIsPlaying(false);
        this.players.elementAt(playerNumber).foldPlayer();
    }
    
    public void setRound(int round){
        this.round = round;
    }
    
    public void addPlayer(Player player){
        players.add(player);
    }

    public Table getTable(){
        return this.table;
    }

    public int getSuccessfulBets(){
        return this.successfulBets;
    }

    public void incrementSuccessfulBets(){
        this.successfulBets++;
    }

    public void resetSuccessfulBets(){
        this.successfulBets = 0;        
    }

    public int getRound(){
        return this.round;
    }

    public void setTable(Table table){
        this.table = table;
    }

    public void setCurrentPlayer(int number){
        this.currentPlayer = number;
    }

    public void incrementRound(){
        this.round++;
    }

    public boolean isEndOfGame(){
        int playersStillPlaying = 0;

        for(int i = 0; i < this.players.size(); i++)
            if(this.players.elementAt(i).getMoney() > 0)
                playersStillPlaying++;

        if(playersStillPlaying == 1)
            return true;
        else
            return false;
    }

    public Player getCurrentPlayer(){
        return this.players.elementAt(this.currentPlayer);
    }

    public int getCurrentPlayerNumber(){
        return this.currentPlayer;
    }
    
    public void incrementCurrentPlayer(){
        this.currentPlayer++;
    }

    public int getDealer(){
        return this.dealer;
    }
    
    public void incrementDealer(){
        this.dealer++;
    }
    
    public void setDealer(int number){
        this.dealer = number;
    }

    public int getPlayersAllIn(){
        int counter = 0;
        for(int i = 0; i < this.players.size(); i++)
            if(this.players.elementAt(i).getAllIn() == true)
                counter++;
        return counter;
    }

    public int playersStillIn() {
        int numPlayers = 0;
        for(int i = 0; i < this.players.size(); i++)
            if(this.players.elementAt(i).getIsPlaying())
                numPlayers++;
        return numPlayers;
    }

    public void resetGame(){
        for(int i = 0; i < this.players.size(); i++){
            this.players.elementAt(i).resetPlayer();
        }
        this.table.resetTable();
        this.round = 0;
        this.successfulBets = 0;
    }
}
