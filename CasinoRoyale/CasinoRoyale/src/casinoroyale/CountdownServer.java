/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package casinoroyale;

import Controllers.*;
import java.awt.Color;


import java.awt.Graphics;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.EOFException;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Set;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.Timer;

/**
 *
 * @author Steve
 */
public class CountdownServer extends javax.swing.JFrame implements iServer {

    public ObjectOutputStream out;
    public ObjectInputStream in;
    public ServerSocket ss;
    public Socket connection;
    public InetAddress serverAddress;
    public myServer chatServer;
    public String name = "";
    public String chatWindow;
    public JLabel jlbls[] = new JLabel[20];
    public JLabel nameLbls[] = new JLabel[5];
    public String packet = "";
    public String cardPlr2 = "-", cardPlr3 = ":", cardPlr4 = ";";
    public int newClientNumber = 1;
    public int plrOutCounter = 1;
    public int lastElement = 0;
    Vector turn = new Vector();
    Vector clientHand = new Vector();
    Vector hand = new Vector();
    Vector burnhand = new Vector();
    public String clientBet[];
    public int clientBets = 0;
    public int playerFold = 0;
    public int playerMoney = 0;
    public int playerBet = 0;
    public int theTurn = 1;
    public int theRound = 0;
    public boolean reset = true;
    public String allCards = "";
    public int numberOfPlayers = 0;
    public boolean fold = false;
    public boolean plr2Fold = false, plr3Fold = false, plr4Fold = false;
    public int roundCompare = 2;
    public String discard[];
    public String discardServer[];
    public boolean discardBool = false;
    public int limit = 0;
    public int temp[];
    Game game;
    iGameController controller;
    InetAddress inet;

     public int port = 4000;
    Vector names = new Vector();
    public int startingMoney = 0;
    public String ipAddress = "";
    WelcomeScreen welcome;

    public CountdownServer(int prt, String name, int start, String ip) {
        port = prt;
        names.add(new String(name));
        startingMoney = start;
        ipAddress = ip;


        initComponents();
        chatServer = new myServer();    //Initializes a new chatServer Thread for communication
        chatServer.start();             //Starts the server thread and will be running in the BackGround
       
        try {
            inet = InetAddress.getLocalHost();
        } catch (UnknownHostException ex) {
            Logger.getLogger(CountdownServer.class.getName()).log(Level.SEVERE, null, ex);
        }
       

        //  turn.addElement(new Integer(0));

        /* The next Block of code
         * initializes the Server / Client burnhand
         * to 56 which is simply a placeholder
         */
        burnhand.addElement(56);
        burnhand.addElement(56);
        burnhand.addElement(56);
        burnhand.addElement(56);
        burnhand.addElement(56);

        /*The next 4 x 4 Blocks of code
         * set up an array of our player
         * labels to make handling them easier
         * (INITIALIZATION OF THE LABELS)
         */

        nameLbls[0] = plr1NameLabel;
        nameLbls[1] = plr2NameLabel;
        nameLbls[2] = plr3NameLabel;
        nameLbls[3] = plr4NameLabel;

        //PLAYER 1 Initialization
        jlbls[0] = plr1C1Label;
        jlbls[1] = plr1C2Label;
        jlbls[2] = plr1C3Label;
        jlbls[3] = plr1C4Label;
        jlbls[4] = plr1C5Label;

        //PLAYER 2 Initialization
        jlbls[5] = plr2C1Label;
        jlbls[6] = plr2C2Label;
        jlbls[7] = plr2C3Label;
        jlbls[8] = plr2C4Label;
        jlbls[9] = plr2C5Label;

        //PLAYER 3 Initialization
        jlbls[10] = plr3C1Label;
        jlbls[11] = plr3C2Label;
        jlbls[12] = plr3C3Label;
        jlbls[13] = plr3C4Label;
        jlbls[14] = plr3C5Label;

        //PLAYER 4 Initialization
        jlbls[15] = plr4C1Label;
        jlbls[16] = plr4C2Label;
        jlbls[17] = plr4C3Label;
        jlbls[18] = plr4C4Label;
        jlbls[19] = plr4C5Label;

        game = new Game();  //Starts a new Game
        controller = new CountDownPokerController(); //Starts a new Poeker Controller

        game.addPlayer("Player 1");     //Creates a Player 1 Object

        controller.setServer(this);     //Controller will have some control over the server
        game.setServer(this);           //Game will also have some control over the server
        controller.setGame(game);       //Sends the current game to the controller
        game.setController(controller); //Sets the gameController to the controller

    }
    int player1In = 1, player2In = 1, player3In = 1, player4In = 1;
    boolean lock1 = false, lock2 = false, lock3 = false, lock4 = false;
    boolean lockPlr1 = false, lockPlr2 = false, lockPlr3 = false, lockPlr4 = false;
    boolean plr1Out = false, plr2Out = false, plr3Out = false, plr4Out = false;
    //Deal The Cards Function W/ Parameter of game

    public void dealTheCards(Game game) {
        //if(game.get == 1)
        // endGame();
        String image = ""; //Initializes a card image to ""
        allCards = "~";    //Delimited string to send the client EVERY PLAYER'S CARDS
        int count = 0;
        cardPlr2 = "-";    //Delimited string to send the client PLAYER TWO'S(2) CARDS
        cardPlr3 = ":";    //Delimited string to send the client PLAYER THREE'S(3) CARDS
        cardPlr4 = ";";    //Delimited string to send the client PLAYER FOUR'S(4) CARDS

        hand.clear();   //Resets the hand to zero(0) elements

        for (int i = 0; i < game.getPlayers().size(); i++) {
            if (i == 0 && lock1 == false) {
                player1In = game.getPlayers().elementAt(i).getMoney();
                if (player1In == 0 && theRound == 7) {
                    plr1Out = true;
                    plrOutCounter--;
                    lock1 = true;

                }
            } else if (i == 1 && lock2 == false) {
                player2In = game.getPlayers().elementAt(i).getMoney();
                if (player2In == 0 && theRound == 7) {
                    plr2Out = true;
                    plrOutCounter--;
                    lock2 = true;

                }
            } else if (i == 2 && lock3 == false) {
                player3In = game.getPlayers().elementAt(i).getMoney();
                if (player3In == 0 && theRound == 7) {
                    plr3Out = true;
                    plrOutCounter--;
                    lock3 = true;

                }
            } else if (i == 3 && lock4 == false) {
                player4In = game.getPlayers().elementAt(i).getMoney();
                if (player4In == 0 && theRound == 7) {
                    plr4Out = true;
                    plrOutCounter--;
                    lock4 = true;


                }
            }
        }


        for (int i = 0; i < game.getPlayers().size(); i++) { //For loop for each seperate player

            for (int j = 0; j < 5; j++) { //For loop for the current label to place card image

                image = Integer.toString(game.getPlayers().elementAt(i).getCardsInHand().elementAt(j).getCardNumber()) + ".png"; //Sets card image to card number
                int ServerHand = game.getPlayers().elementAt(i).getCardsInHand().elementAt(j).getCardNumber();
                allCards += Integer.toString(ServerHand) + "~";

               

                int num = (i * 5) + j;
              

                if (i == 0 || theRound == 7) {
                    Icon card = new ImageIcon(getClass().getResource(image));
                    jlbls[num].setIcon(card);
                } else {
                    if (plr2Fold == true && num == 5 || plr2Fold == true && num == 6 || plr2Fold == true && num == 7 || plr2Fold == true && num == 8 || plr2Fold == true && num == 9) {
                        Icon card = new ImageIcon(getClass().getResource("53.png"));
                        jlbls[num].setIcon(card);

                    } else if (plr3Fold == true && num == 10 || plr3Fold == true && num == 11 || plr3Fold == true && num == 12 || plr3Fold == true && num == 13 || plr3Fold == true && num == 14) {
                        Icon card = new ImageIcon(getClass().getResource("53.png"));
                        jlbls[num].setIcon(card);
                    } else if (plr4Fold == true && num == 15 || plr4Fold == true && num == 16 || plr4Fold == true && num == 17 || plr4Fold == true && num == 18 || plr4Fold == true && num == 19) {
                        Icon card = new ImageIcon(getClass().getResource("53.png"));
                        jlbls[num].setIcon(card);
                    } else if (plr2Fold == false || plr3Fold == false || plr4Fold == false) {
                        Icon card = new ImageIcon(getClass().getResource("b2fv.png"));
                        jlbls[num].setIcon(card);
                    }
                  
                    if (plr1Out == true) {
                        if(lockPlr1 == false){
                       chatServer.SendMessage("o0");
                       
                       lockPlr1 = true;
                            try {
                                Thread.sleep(1000);
                            } catch (InterruptedException ex) {
                                Logger.getLogger(CountdownServer.class.getName()).log(Level.SEVERE, null, ex);
                            }
                            
                        }

                       
                    } else if (plr2Out == true && num == 5 || plr2Out == true && num == 6 || plr2Out == true && num == 7 || plr2Out == true && num == 8 || plr2Out == true && num == 9) {
                        if(lockPlr2 == false){
                       chatServer.SendMessage("o1");
                       lockPlr2 = true;
                        try {
                                Thread.sleep(1000);
                            } catch (InterruptedException ex) {
                                Logger.getLogger(CountdownServer.class.getName()).log(Level.SEVERE, null, ex);
                            }
                        }
                        Icon card = new ImageIcon(getClass().getResource("53.png"));
                        jlbls[num].setIcon(card);
                        
                    } else if (plr3Out == true && num == 10 || plr3Out == true && num == 11 || plr3Out == true && num == 12 || plr3Out == true && num == 13 || plr3Out == true && num == 14) {
                        if(lockPlr3 == false){
                       chatServer.SendMessage("o2");
                       lockPlr3 = true;
                        try {
                                Thread.sleep(1000);
                            } catch (InterruptedException ex) {
                                Logger.getLogger(CountdownServer.class.getName()).log(Level.SEVERE, null, ex);
                            }
                        }
                        Icon card = new ImageIcon(getClass().getResource("53.png"));
                        jlbls[num].setIcon(card);
                       
                    } else if (plr4Out == true && num == 15 || plr4Out == true && num == 16 || plr4Out == true && num == 17 || plr4Out == true && num == 18 || plr4Out == true && num == 19) {
                        if(lockPlr4 == false){
                       chatServer.SendMessage("o3");
                       lockPlr4 = true;
                        try {
                                Thread.sleep(1000);
                            } catch (InterruptedException ex) {
                                Logger.getLogger(CountdownServer.class.getName()).log(Level.SEVERE, null, ex);
                            }
                        }
                        Icon card = new ImageIcon(getClass().getResource("53.png"));
                        jlbls[num].setIcon(card);
                        
                    }
                }

                if (i == 0) {
                    hand.addElement(new Integer(ServerHand));

                }
                if (i == 1 && plr2Out == false) {

                    cardPlr2 += image + "-";
                } else if (i == 2 && plr3Out == false) {

                    cardPlr3 += image + ":";
                } else if (i == 3 && plr4Out == false) {

                    cardPlr4 += image + ";";
                }

            }

        }
        try {
            Thread.sleep(1000);
        } catch (InterruptedException ex) {
            Logger.getLogger(CountdownServer.class.getName()).log(Level.SEVERE, null, ex);
        }
{

        }
        chatServer.SendMessage("p" + game.getPlayers().size() + "p");
        
        if (theRound == 7 || game.playersStillIn() == 1) {
            chatServer.SendMessage(allCards);
            
        }
        if(theRound !=7){
        chatServer.SendMessage(cardPlr2);
        chatServer.SendMessage(cardPlr3);
        chatServer.SendMessage(cardPlr4);
        }

      


        if (game.getPlayers().size() == 2) {
            plr3NameLabel.setText("NOT CONNECTED");
            plr4NameLabel.setText("NOT CONNECTED");
        } else if (game.getPlayers().size() == 3) {
            plr4NameLabel.setText("NOT CONNECTED");
        }

        discardBool = false;

        //turn();

    }
    //Discard function

    public void discard() {
        if (theTurn != 0) {
            for (int i = 1; i < discard.length; i++) {
                clientHand.addElement(new Integer(discard[i]));
            }
        } else {
            for (int i = 0; i < discardServer.length; i++) {
                clientHand.addElement(new Integer(discardServer[i]));
            }
        }
        //controller.tradeIn((Integer) turn.elementAt(0),clientHand);
        controller.tradeIn(clientHand);
     
        turn();
        dealTheCards(game);

        //discardBool = true;
        clientHand.removeAllElements();
    }

    public void turn() {

        theTurn = game.getCurrentPlayerNumber();
        theRound = game.getRound();
        try {
            Thread.sleep(500);
        } catch (InterruptedException ex) {
            Logger.getLogger(CountdownServer.class.getName()).log(Level.SEVERE, null, ex);
        }
       
        chatServer.SendMessage("." + Integer.toString((Integer) theTurn) + ".");

        //theTurn = (Integer) turn.elementAt(0);
        if (theTurn == 0) {

            plr1NameLabel.setForeground(Color.RED);
            plr2NameLabel.setForeground(Color.WHITE);
            plr3NameLabel.setForeground(Color.WHITE);
            plr4NameLabel.setForeground(Color.WHITE);
        } else if (theTurn == 1) {
            plr2NameLabel.setForeground(Color.RED);
            plr1NameLabel.setForeground(Color.WHITE);
            plr3NameLabel.setForeground(Color.WHITE);
            plr4NameLabel.setForeground(Color.WHITE);
        } else if (theTurn == 2) {
            plr3NameLabel.setForeground(Color.RED);
            plr1NameLabel.setForeground(Color.WHITE);
            plr2NameLabel.setForeground(Color.WHITE);
            plr4NameLabel.setForeground(Color.WHITE);
        } else if (theTurn == 3) {
            plr4NameLabel.setForeground(Color.RED);
            plr1NameLabel.setForeground(Color.WHITE);
            plr2NameLabel.setForeground(Color.WHITE);
            plr3NameLabel.setForeground(Color.WHITE);
        }


        
        if (theRound == 0) {
            roundLabel.setText("Current Round: Betting Round");
            betButton.setEnabled(true);
            foldButton.setEnabled(true);
            checkButton.setEnabled(true);
            discardButton.setEnabled(false);
            if (reset == true) {
                potLabel.setText("Pot Amount: $0");
                reset = false;
            }

        } else if (theRound == 1) {
            roundLabel.setText("Current Round: Trade In Round (Up To 3 Cards)");
            //roundCompare = 1;
            limit = 3;
            betButton.setEnabled(false);
            foldButton.setEnabled(true);
            checkButton.setEnabled(false);
            discardButton.setEnabled(true);
        } else if (theRound == 2) {
            roundLabel.setText("current Round: Betting Round");
            betButton.setEnabled(true);
            foldButton.setEnabled(true);
            checkButton.setEnabled(true);
            discardButton.setEnabled(false);
        }else if (theRound == 3) {
            roundLabel.setText("Current Round: Trade In Round (Up To 2 Cards)");
            //roundCompare = 1;
            limit = 2;
            betButton.setEnabled(false);
            foldButton.setEnabled(true);
            checkButton.setEnabled(false);
            discardButton.setEnabled(true);
        }else if (theRound == 4) {
            roundLabel.setText("current Round: Betting Round");
            betButton.setEnabled(true);
            foldButton.setEnabled(true);
            checkButton.setEnabled(true);
            discardButton.setEnabled(false);
        }else if (theRound == 5) {
            roundLabel.setText("Current Round: Trade In Round (Up To 1 Card)");
            //roundCompare = 1;
            limit = 1;
            betButton.setEnabled(false);
            foldButton.setEnabled(true);
            checkButton.setEnabled(false);
            discardButton.setEnabled(true);
        }else if (theRound == 6) {
            roundLabel.setText("current Round: Betting Round");
            betButton.setEnabled(true);
            foldButton.setEnabled(true);
            checkButton.setEnabled(true);
            discardButton.setEnabled(false);
        }
        try {
            Thread.sleep(400);
        } catch (InterruptedException ex) {
            Logger.getLogger(CountdownServer.class.getName()).log(Level.SEVERE, null, ex);
        }
        chatServer.SendMessage("r" + theRound + "r");

    }

    public void determineBet() {
        String output = "";
        int pot = 0;

        if (theTurn == 0) {
            output = ",";
        } else if (theTurn == 1) {
            output = "-";
        } else if (theTurn == 2) {
            output = ":";
        } else if (theTurn == 3) {
            output = ";";
        }
        if (controller.bet(clientBets) == true) {
            //chatServer.SendMessage("$" + output + "true");
            pot = game.getTable().getPot();
            try {
                Thread.sleep(200);
            } catch (InterruptedException ex) {
                Logger.getLogger(CountdownServer.class.getName()).log(Level.SEVERE, null, ex);
            }
           

            chatServer.SendMessage("%" + output + Integer.toString(game.getPlayers().elementAt(theTurn).getMoney()) + output + game.getPlayers().elementAt(theTurn).getCurrentBet() + output + pot);
           

            if (game.getPlayers().size() <= 4) {
                plr1MoneyLabel.setText("$ " + Integer.toString(game.getPlayers().elementAt(0).getMoney()));
                plr1BetField.setText(Integer.toString(game.getPlayers().elementAt(0).getCurrentBet()));
                plr2MoneyLabel.setText("$ " + Integer.toString(game.getPlayers().elementAt(1).getMoney()));
                plr2BetField.setText(Integer.toString(game.getPlayers().elementAt(1).getCurrentBet()));
            }
            if (game.getPlayers().size() >= 3) {
                plr3MoneyLabel.setText("$ " + Integer.toString(game.getPlayers().elementAt(2).getMoney()));
                plr3BetField.setText(Integer.toString(game.getPlayers().elementAt(2).getCurrentBet()));
            }
            if (game.getPlayers().size() == 4) {
                plr4MoneyLabel.setText("$ " + Integer.toString(game.getPlayers().elementAt(3).getMoney()));
                plr4BetField.setText(Integer.toString(game.getPlayers().elementAt(3).getCurrentBet()));

            }
            outputAll();
            potLabel.setText("Pot Amount: $" + pot);
            turn();
        } else {
            if (theTurn == 0) {
                JOptionPane.showMessageDialog(null, "Did not bet enough money or cannot check at this time!", "Player Error", JOptionPane.ERROR_MESSAGE);
            } else {
                chatServer.SendMessage("$" + output + "false");
            }
        }


    }

    public void playerFolds(int plr) {
        
       
        //turn.removeElementAt(0);
        //dealTheCards(game);
        if (game.playersStillIn() != 1) {

            
            chatServer.SendMessage("f" + plr);
        }


        if (plr == 0) {
            for (int i = 0; i < 5; i++) {
                Icon card = new ImageIcon(getClass().getResource(""));
                jlbls[i].setIcon(card);

            }
        }
        if (plr == 1) {
            for (int i = 5; i < 10; i++) {
                Icon card = new ImageIcon(getClass().getResource(""));
                jlbls[i].setIcon(card);
                plr2Fold = true;
            }
        } else if (plr == 2) {
            for (int i = 10; i < 15; i++) {
                Icon card = new ImageIcon(getClass().getResource(""));
                jlbls[i].setIcon(card);
                plr3Fold = true;
            }
        } else if (plr == 3) {
            for (int i = 15; i < 20; i++) {
                Icon card = new ImageIcon(getClass().getResource(""));
                jlbls[i].setIcon(card);
                plr4Fold = true;
            }
        }
        try {
            Thread.sleep(500);
        } catch (InterruptedException ex) {
            Logger.getLogger(CountdownServer.class.getName()).log(Level.SEVERE, null, ex);
        }
        controller.fold();


        if (game.playersStillIn() == 1) {
           // gameOver();
            //theRound = 3;
            //outputAll();        
        } else {
            turn();
        }



    }

    public void startServer() {
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        backGroundPanel = new javax.swing.JPanel();
        player2OuterPanel = new javax.swing.JPanel();
        plr2NameLabel = new javax.swing.JLabel();
        player2CardPanel = new javax.swing.JPanel();
        plr2C1Label = new javax.swing.JLabel();
        plr2C2Label = new javax.swing.JLabel();
        plr2C3Label = new javax.swing.JLabel();
        plr2C4Label = new javax.swing.JLabel();
        plr2C5Label = new javax.swing.JLabel();
        jLabel1 = new javax.swing.JLabel();
        plr2MoneyLabel = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        plr2BetField = new javax.swing.JTextField();
        jLabel11 = new javax.swing.JLabel();
        player1OuterPanel = new javax.swing.JPanel();
        plr1NameLabel = new javax.swing.JLabel();
        player1CardPanel = new javax.swing.JPanel();
        plr1C1Label = new javax.swing.JLabel();
        plr1C2Label = new javax.swing.JLabel();
        plr1C3Label = new javax.swing.JLabel();
        plr1C4Label = new javax.swing.JLabel();
        plr1C5Label = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        plr1MoneyLabel = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        plr1BetField = new javax.swing.JTextField();
        jLabel10 = new javax.swing.JLabel();
        player3OuterPanel = new javax.swing.JPanel();
        plr3NameLabel = new javax.swing.JLabel();
        player3CardPanel = new javax.swing.JPanel();
        plr3C1Label = new javax.swing.JLabel();
        plr3C2Label = new javax.swing.JLabel();
        plr3C3Label = new javax.swing.JLabel();
        plr3C4Label = new javax.swing.JLabel();
        plr3C5Label = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        plr3MoneyLabel = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        plr3BetField = new javax.swing.JTextField();
        jLabel12 = new javax.swing.JLabel();
        player4OuterPanel = new javax.swing.JPanel();
        plr4NameLabel = new javax.swing.JLabel();
        player4CardPanel = new javax.swing.JPanel();
        plr4C1Label = new javax.swing.JLabel();
        plr4C2Label = new javax.swing.JLabel();
        plr4C3Label = new javax.swing.JLabel();
        plr4C4Label = new javax.swing.JLabel();
        plr4C5Label = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        plr4MoneyLabel = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        plr4BetField = new javax.swing.JTextField();
        jLabel13 = new javax.swing.JLabel();
        blackJackToolbar = new javax.swing.JToolBar();
        plrNameLabel = new javax.swing.JLabel();
        betButton = new javax.swing.JButton();
        foldButton = new javax.swing.JButton();
        checkButton = new javax.swing.JButton();
        discardButton = new javax.swing.JButton();
        jSeparator1 = new javax.swing.JToolBar.Separator();
        jButton1 = new javax.swing.JButton();
        jPanel1 = new javax.swing.JPanel();
        roundLabel = new javax.swing.JLabel();
        potLabel = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);
        setTitle("Countdown Server");
        setResizable(false);
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                formWindowClosing(evt);
            }
        });
        addComponentListener(new java.awt.event.ComponentAdapter() {
            public void componentShown(java.awt.event.ComponentEvent evt) {
                formComponentShown(evt);
            }
        });
        addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                formFocusGained(evt);
            }
        });

        backGroundPanel.setBackground(new java.awt.Color(0, 51, 0));
        backGroundPanel.setPreferredSize(new java.awt.Dimension(1026, 678));
        backGroundPanel.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                backGroundPanelFocusGained(evt);
            }
        });
        backGroundPanel.setLayout(new java.awt.GridLayout(2, 2));

        player2OuterPanel.setBackground(new java.awt.Color(0, 51, 0));
        player2OuterPanel.setLayout(new java.awt.BorderLayout());

        plr2NameLabel.setBackground(new java.awt.Color(255, 255, 255));
        plr2NameLabel.setFont(new java.awt.Font("Rockwell", 0, 30));
        plr2NameLabel.setForeground(new java.awt.Color(255, 255, 255));
        plr2NameLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        plr2NameLabel.setText("PLAYER #2");
        player2OuterPanel.add(plr2NameLabel, java.awt.BorderLayout.PAGE_START);

        player2CardPanel.setBackground(new java.awt.Color(0, 51, 0));

        plr2C1Label.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(255, 255, 255), 1, true));

        plr2C2Label.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(255, 255, 255), 1, true));

        plr2C3Label.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(255, 255, 255), 1, true));

        plr2C4Label.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(255, 255, 255), 1, true));

        plr2C5Label.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(255, 255, 255), 1, true));

        jLabel1.setFont(new java.awt.Font("Tahoma", 0, 14));
        jLabel1.setForeground(new java.awt.Color(255, 255, 255));
        jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel1.setText("Current Funds:");

        plr2MoneyLabel.setFont(new java.awt.Font("Rockwell", 0, 24));
        plr2MoneyLabel.setForeground(new java.awt.Color(255, 255, 255));
        plr2MoneyLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        plr2MoneyLabel.setText("0");

        jLabel3.setFont(new java.awt.Font("Tahoma", 0, 14));
        jLabel3.setForeground(new java.awt.Color(255, 255, 255));
        jLabel3.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel3.setText("Bet Amount:");

        plr2BetField.setEditable(false);

        jLabel11.setFont(new java.awt.Font("Tahoma", 0, 14));
        jLabel11.setForeground(new java.awt.Color(255, 255, 255));
        jLabel11.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel11.setText("$");

        javax.swing.GroupLayout player2CardPanelLayout = new javax.swing.GroupLayout(player2CardPanel);
        player2CardPanel.setLayout(player2CardPanelLayout);
        player2CardPanelLayout.setHorizontalGroup(
            player2CardPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(player2CardPanelLayout.createSequentialGroup()
                .addGroup(player2CardPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(player2CardPanelLayout.createSequentialGroup()
                        .addGap(29, 29, 29)
                        .addComponent(plr2MoneyLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 153, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(player2CardPanelLayout.createSequentialGroup()
                        .addGap(39, 39, 39)
                        .addGroup(player2CardPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addGroup(player2CardPanelLayout.createSequentialGroup()
                                .addComponent(plr2C1Label, javax.swing.GroupLayout.PREFERRED_SIZE, 78, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(plr2C2Label, javax.swing.GroupLayout.PREFERRED_SIZE, 78, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(plr2C3Label, javax.swing.GroupLayout.PREFERRED_SIZE, 78, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(plr2C4Label, javax.swing.GroupLayout.PREFERRED_SIZE, 78, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(plr2C5Label, javax.swing.GroupLayout.PREFERRED_SIZE, 78, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(4, 4, 4))
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, player2CardPanelLayout.createSequentialGroup()
                                .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 124, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addGroup(player2CardPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(player2CardPanelLayout.createSequentialGroup()
                                        .addComponent(jLabel11)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(plr2BetField, javax.swing.GroupLayout.PREFERRED_SIZE, 105, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 124, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGap(28, 28, 28)))))
                .addContainerGap(56, Short.MAX_VALUE))
        );
        player2CardPanelLayout.setVerticalGroup(
            player2CardPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(player2CardPanelLayout.createSequentialGroup()
                .addGap(44, 44, 44)
                .addGroup(player2CardPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(plr2C5Label, javax.swing.GroupLayout.PREFERRED_SIZE, 118, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(plr2C4Label, javax.swing.GroupLayout.PREFERRED_SIZE, 118, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(plr2C3Label, javax.swing.GroupLayout.PREFERRED_SIZE, 118, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(plr2C2Label, javax.swing.GroupLayout.PREFERRED_SIZE, 118, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(plr2C1Label, javax.swing.GroupLayout.PREFERRED_SIZE, 118, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(56, 56, 56)
                .addGroup(player2CardPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(jLabel3))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(player2CardPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(plr2MoneyLabel)
                    .addGroup(player2CardPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(plr2BetField, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel11)))
                .addContainerGap(78, Short.MAX_VALUE))
        );

        player2OuterPanel.add(player2CardPanel, java.awt.BorderLayout.LINE_START);

        backGroundPanel.add(player2OuterPanel);

        player1OuterPanel.setBackground(new java.awt.Color(0, 51, 0));
        player1OuterPanel.setLayout(new java.awt.BorderLayout());

        plr1NameLabel.setFont(new java.awt.Font("Rockwell", 0, 30));
        plr1NameLabel.setForeground(new java.awt.Color(255, 255, 255));
        plr1NameLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        plr1NameLabel.setText("PLAYER #1");
        player1OuterPanel.add(plr1NameLabel, java.awt.BorderLayout.PAGE_START);

        player1CardPanel.setBackground(new java.awt.Color(0, 51, 0));

        plr1C1Label.setForeground(new java.awt.Color(255, 255, 255));
        plr1C1Label.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(255, 255, 255), 1, true));
        plr1C1Label.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                plr1C1LabelMouseClicked(evt);
            }
        });

        plr1C2Label.setForeground(new java.awt.Color(255, 255, 255));
        plr1C2Label.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(255, 255, 255), 1, true));
        plr1C2Label.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                plr1C2LabelMouseClicked(evt);
            }
        });

        plr1C3Label.setForeground(new java.awt.Color(255, 255, 255));
        plr1C3Label.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(255, 255, 255), 1, true));
        plr1C3Label.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                plr1C3LabelMouseClicked(evt);
            }
        });

        plr1C4Label.setForeground(new java.awt.Color(255, 255, 255));
        plr1C4Label.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(255, 255, 255), 1, true));
        plr1C4Label.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                plr1C4LabelMouseClicked(evt);
            }
        });

        plr1C5Label.setForeground(new java.awt.Color(255, 255, 255));
        plr1C5Label.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(255, 255, 255), 1, true));
        plr1C5Label.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                plr1C5LabelMouseClicked(evt);
            }
        });

        jLabel2.setFont(new java.awt.Font("Tahoma", 0, 14));
        jLabel2.setForeground(new java.awt.Color(255, 255, 255));
        jLabel2.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel2.setText("Current Funds:");

        plr1MoneyLabel.setFont(new java.awt.Font("Rockwell", 0, 24));
        plr1MoneyLabel.setForeground(new java.awt.Color(255, 255, 255));
        plr1MoneyLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        plr1MoneyLabel.setText("0");

        jLabel4.setFont(new java.awt.Font("Tahoma", 0, 14));
        jLabel4.setForeground(new java.awt.Color(255, 255, 255));
        jLabel4.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel4.setText("Bet Amount:");

        jLabel10.setFont(new java.awt.Font("Tahoma", 0, 14));
        jLabel10.setForeground(new java.awt.Color(255, 255, 255));
        jLabel10.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel10.setText("$");

        javax.swing.GroupLayout player1CardPanelLayout = new javax.swing.GroupLayout(player1CardPanel);
        player1CardPanel.setLayout(player1CardPanelLayout);
        player1CardPanelLayout.setHorizontalGroup(
            player1CardPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(player1CardPanelLayout.createSequentialGroup()
                .addGroup(player1CardPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(player1CardPanelLayout.createSequentialGroup()
                        .addGap(29, 29, 29)
                        .addComponent(plr1MoneyLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 153, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(player1CardPanelLayout.createSequentialGroup()
                        .addGap(39, 39, 39)
                        .addGroup(player1CardPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addGroup(player1CardPanelLayout.createSequentialGroup()
                                .addComponent(plr1C1Label, javax.swing.GroupLayout.PREFERRED_SIZE, 78, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(plr1C2Label, javax.swing.GroupLayout.PREFERRED_SIZE, 78, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(plr1C3Label, javax.swing.GroupLayout.PREFERRED_SIZE, 78, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(plr1C4Label, javax.swing.GroupLayout.PREFERRED_SIZE, 78, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(plr1C5Label, javax.swing.GroupLayout.PREFERRED_SIZE, 78, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(4, 4, 4))
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, player1CardPanelLayout.createSequentialGroup()
                                .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 124, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addGroup(player1CardPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(player1CardPanelLayout.createSequentialGroup()
                                        .addComponent(jLabel10)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(plr1BetField, javax.swing.GroupLayout.PREFERRED_SIZE, 105, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 124, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGap(28, 28, 28)))))
                .addContainerGap(56, Short.MAX_VALUE))
        );
        player1CardPanelLayout.setVerticalGroup(
            player1CardPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(player1CardPanelLayout.createSequentialGroup()
                .addGap(44, 44, 44)
                .addGroup(player1CardPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(plr1C5Label, javax.swing.GroupLayout.PREFERRED_SIZE, 118, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(plr1C4Label, javax.swing.GroupLayout.PREFERRED_SIZE, 118, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(plr1C3Label, javax.swing.GroupLayout.PREFERRED_SIZE, 118, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(plr1C2Label, javax.swing.GroupLayout.PREFERRED_SIZE, 118, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(plr1C1Label, javax.swing.GroupLayout.PREFERRED_SIZE, 118, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(56, 56, 56)
                .addGroup(player1CardPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(jLabel4))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(player1CardPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(plr1MoneyLabel)
                    .addGroup(player1CardPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(plr1BetField, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel10)))
                .addContainerGap(78, Short.MAX_VALUE))
        );

        player1OuterPanel.add(player1CardPanel, java.awt.BorderLayout.LINE_START);

        backGroundPanel.add(player1OuterPanel);

        player3OuterPanel.setBackground(new java.awt.Color(0, 51, 0));
        player3OuterPanel.setLayout(new java.awt.BorderLayout());

        plr3NameLabel.setFont(new java.awt.Font("Rockwell", 0, 30));
        plr3NameLabel.setForeground(new java.awt.Color(255, 255, 255));
        plr3NameLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        plr3NameLabel.setText("PLAYER #3");
        player3OuterPanel.add(plr3NameLabel, java.awt.BorderLayout.PAGE_START);

        player3CardPanel.setBackground(new java.awt.Color(0, 51, 0));

        plr3C1Label.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(255, 255, 255), 1, true));

        plr3C2Label.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(255, 255, 255), 1, true));

        plr3C3Label.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(255, 255, 255), 1, true));

        plr3C4Label.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(255, 255, 255), 1, true));

        plr3C5Label.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(255, 255, 255), 1, true));

        jLabel5.setFont(new java.awt.Font("Tahoma", 0, 14));
        jLabel5.setForeground(new java.awt.Color(255, 255, 255));
        jLabel5.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel5.setText("Current Funds:");

        plr3MoneyLabel.setFont(new java.awt.Font("Rockwell", 0, 24));
        plr3MoneyLabel.setForeground(new java.awt.Color(255, 255, 255));
        plr3MoneyLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        plr3MoneyLabel.setText("0");

        jLabel6.setFont(new java.awt.Font("Tahoma", 0, 14));
        jLabel6.setForeground(new java.awt.Color(255, 255, 255));
        jLabel6.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel6.setText("Bet Amount:");

        plr3BetField.setEditable(false);

        jLabel12.setFont(new java.awt.Font("Tahoma", 0, 14));
        jLabel12.setForeground(new java.awt.Color(255, 255, 255));
        jLabel12.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel12.setText("$");

        javax.swing.GroupLayout player3CardPanelLayout = new javax.swing.GroupLayout(player3CardPanel);
        player3CardPanel.setLayout(player3CardPanelLayout);
        player3CardPanelLayout.setHorizontalGroup(
            player3CardPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(player3CardPanelLayout.createSequentialGroup()
                .addGroup(player3CardPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(player3CardPanelLayout.createSequentialGroup()
                        .addGap(29, 29, 29)
                        .addComponent(plr3MoneyLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 153, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(player3CardPanelLayout.createSequentialGroup()
                        .addGap(39, 39, 39)
                        .addGroup(player3CardPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addGroup(player3CardPanelLayout.createSequentialGroup()
                                .addComponent(plr3C1Label, javax.swing.GroupLayout.PREFERRED_SIZE, 78, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(plr3C2Label, javax.swing.GroupLayout.PREFERRED_SIZE, 78, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(plr3C3Label, javax.swing.GroupLayout.PREFERRED_SIZE, 78, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(plr3C4Label, javax.swing.GroupLayout.PREFERRED_SIZE, 78, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(plr3C5Label, javax.swing.GroupLayout.PREFERRED_SIZE, 78, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(4, 4, 4))
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, player3CardPanelLayout.createSequentialGroup()
                                .addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, 124, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addGroup(player3CardPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(player3CardPanelLayout.createSequentialGroup()
                                        .addComponent(jLabel12)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(plr3BetField, javax.swing.GroupLayout.PREFERRED_SIZE, 105, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addComponent(jLabel6, javax.swing.GroupLayout.PREFERRED_SIZE, 124, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGap(28, 28, 28)))))
                .addContainerGap(56, Short.MAX_VALUE))
        );
        player3CardPanelLayout.setVerticalGroup(
            player3CardPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(player3CardPanelLayout.createSequentialGroup()
                .addGap(44, 44, 44)
                .addGroup(player3CardPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(plr3C5Label, javax.swing.GroupLayout.PREFERRED_SIZE, 118, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(plr3C4Label, javax.swing.GroupLayout.PREFERRED_SIZE, 118, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(plr3C3Label, javax.swing.GroupLayout.PREFERRED_SIZE, 118, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(plr3C2Label, javax.swing.GroupLayout.PREFERRED_SIZE, 118, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(plr3C1Label, javax.swing.GroupLayout.PREFERRED_SIZE, 118, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(56, 56, 56)
                .addGroup(player3CardPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel5)
                    .addComponent(jLabel6))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(player3CardPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(plr3MoneyLabel)
                    .addGroup(player3CardPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(plr3BetField, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel12)))
                .addContainerGap(78, Short.MAX_VALUE))
        );

        player3OuterPanel.add(player3CardPanel, java.awt.BorderLayout.LINE_START);

        backGroundPanel.add(player3OuterPanel);

        player4OuterPanel.setBackground(new java.awt.Color(0, 51, 0));
        player4OuterPanel.setLayout(new java.awt.BorderLayout());

        plr4NameLabel.setFont(new java.awt.Font("Rockwell", 0, 30));
        plr4NameLabel.setForeground(new java.awt.Color(255, 255, 255));
        plr4NameLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        plr4NameLabel.setText("PLAYER #4");
        player4OuterPanel.add(plr4NameLabel, java.awt.BorderLayout.PAGE_START);

        player4CardPanel.setBackground(new java.awt.Color(0, 51, 0));

        plr4C1Label.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(255, 255, 255), 1, true));

        plr4C2Label.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(255, 255, 255), 1, true));

        plr4C3Label.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(255, 255, 255), 1, true));

        plr4C4Label.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(255, 255, 255), 1, true));

        plr4C5Label.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(255, 255, 255), 1, true));

        jLabel7.setFont(new java.awt.Font("Tahoma", 0, 14));
        jLabel7.setForeground(new java.awt.Color(255, 255, 255));
        jLabel7.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel7.setText("Current Funds:");

        plr4MoneyLabel.setFont(new java.awt.Font("Rockwell", 0, 24));
        plr4MoneyLabel.setForeground(new java.awt.Color(255, 255, 255));
        plr4MoneyLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        plr4MoneyLabel.setText("0");

        jLabel8.setFont(new java.awt.Font("Tahoma", 0, 14));
        jLabel8.setForeground(new java.awt.Color(255, 255, 255));
        jLabel8.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel8.setText("Bet Amount:");

        plr4BetField.setEditable(false);

        jLabel13.setFont(new java.awt.Font("Tahoma", 0, 14));
        jLabel13.setForeground(new java.awt.Color(255, 255, 255));
        jLabel13.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel13.setText("$");

        javax.swing.GroupLayout player4CardPanelLayout = new javax.swing.GroupLayout(player4CardPanel);
        player4CardPanel.setLayout(player4CardPanelLayout);
        player4CardPanelLayout.setHorizontalGroup(
            player4CardPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(player4CardPanelLayout.createSequentialGroup()
                .addGroup(player4CardPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(player4CardPanelLayout.createSequentialGroup()
                        .addGap(29, 29, 29)
                        .addComponent(plr4MoneyLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 153, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(player4CardPanelLayout.createSequentialGroup()
                        .addGap(39, 39, 39)
                        .addGroup(player4CardPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addGroup(player4CardPanelLayout.createSequentialGroup()
                                .addComponent(plr4C1Label, javax.swing.GroupLayout.PREFERRED_SIZE, 78, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(plr4C2Label, javax.swing.GroupLayout.PREFERRED_SIZE, 78, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(plr4C3Label, javax.swing.GroupLayout.PREFERRED_SIZE, 78, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(plr4C4Label, javax.swing.GroupLayout.PREFERRED_SIZE, 78, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(plr4C5Label, javax.swing.GroupLayout.PREFERRED_SIZE, 78, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(4, 4, 4))
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, player4CardPanelLayout.createSequentialGroup()
                                .addComponent(jLabel7, javax.swing.GroupLayout.PREFERRED_SIZE, 124, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addGroup(player4CardPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(player4CardPanelLayout.createSequentialGroup()
                                        .addComponent(jLabel13)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(plr4BetField, javax.swing.GroupLayout.PREFERRED_SIZE, 105, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addComponent(jLabel8, javax.swing.GroupLayout.PREFERRED_SIZE, 124, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGap(28, 28, 28)))))
                .addContainerGap(56, Short.MAX_VALUE))
        );
        player4CardPanelLayout.setVerticalGroup(
            player4CardPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(player4CardPanelLayout.createSequentialGroup()
                .addGap(44, 44, 44)
                .addGroup(player4CardPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(plr4C5Label, javax.swing.GroupLayout.PREFERRED_SIZE, 118, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(plr4C4Label, javax.swing.GroupLayout.PREFERRED_SIZE, 118, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(plr4C3Label, javax.swing.GroupLayout.PREFERRED_SIZE, 118, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(plr4C2Label, javax.swing.GroupLayout.PREFERRED_SIZE, 118, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(plr4C1Label, javax.swing.GroupLayout.PREFERRED_SIZE, 118, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(56, 56, 56)
                .addGroup(player4CardPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel7)
                    .addComponent(jLabel8))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(player4CardPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(plr4MoneyLabel)
                    .addGroup(player4CardPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(plr4BetField, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel13)))
                .addContainerGap(78, Short.MAX_VALUE))
        );

        player4OuterPanel.add(player4CardPanel, java.awt.BorderLayout.LINE_START);

        backGroundPanel.add(player4OuterPanel);

        getContentPane().add(backGroundPanel, java.awt.BorderLayout.CENTER);

        blackJackToolbar.setRollover(true);

        plrNameLabel.setText("PLAYER NAME  ");
        blackJackToolbar.add(plrNameLabel);

        betButton.setText("Bet");
        betButton.setFocusable(false);
        betButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        betButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        betButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                betButtonActionPerformed(evt);
            }
        });
        blackJackToolbar.add(betButton);

        foldButton.setText("Fold");
        foldButton.setFocusable(false);
        foldButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        foldButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        foldButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                foldButtonActionPerformed(evt);
            }
        });
        blackJackToolbar.add(foldButton);

        checkButton.setText("Check");
        checkButton.setFocusable(false);
        checkButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        checkButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        checkButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                checkButtonActionPerformed(evt);
            }
        });
        blackJackToolbar.add(checkButton);

        discardButton.setText("Discard");
        discardButton.setFocusable(false);
        discardButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        discardButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        discardButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                discardButtonActionPerformed(evt);
            }
        });
        blackJackToolbar.add(discardButton);
        blackJackToolbar.add(jSeparator1);

        jButton1.setText("Start Game");
        jButton1.setFocusable(false);
        jButton1.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jButton1.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });
        blackJackToolbar.add(jButton1);

        jPanel1.setMaximumSize(new java.awt.Dimension(900, 30));

        roundLabel.setText("Current Round:");

        potLabel.setText("Pot Amount: $0");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(316, 316, 316)
                .addComponent(roundLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 387, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(potLabel)
                .addContainerGap(40, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                .addComponent(roundLabel, javax.swing.GroupLayout.DEFAULT_SIZE, 25, Short.MAX_VALUE)
                .addComponent(potLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        blackJackToolbar.add(jPanel1);

        getContentPane().add(blackJackToolbar, java.awt.BorderLayout.SOUTH);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void backGroundPanelFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_backGroundPanelFocusGained
    }//GEN-LAST:event_backGroundPanelFocusGained

    private void formFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_formFocusGained
    }//GEN-LAST:event_formFocusGained

    private void formComponentShown(java.awt.event.ComponentEvent evt) {//GEN-FIRST:event_formComponentShown
    }//GEN-LAST:event_formComponentShown
    int counter1 = 0;
    int flipcount1 = 0;
    private void plr1C1LabelMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_plr1C1LabelMouseClicked
        if (theTurn == 0) {
            ++counter1;

            if (theTurn != 0) {
                JOptionPane.showMessageDialog(null, "It is not your turn!", "Player Error", JOptionPane.ERROR_MESSAGE);
            } else if (counter1 == 2) {
                --flipcount1;
                for (int i = 0; i < 5; i++) {
                    if (burnhand.elementAt(i) == hand.elementAt(0)) {
                        burnhand.removeElementAt(i);
                        String temp;
                        temp = (Integer) hand.elementAt(0) + ".png";
                        Icon main = new ImageIcon(getClass().getResource(temp));
                        plr1C1Label.setIcon(main);
                        break;
                    }
                }
                counter1 = 0;
            } else {
                ++flipcount1;
                Icon back = new ImageIcon(getClass().getResource("b2fv.png"));
                plr1C1Label.setIcon(back);
                burnhand.add(0, hand.elementAt(0));
            }
        }
    }//GEN-LAST:event_plr1C1LabelMouseClicked

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        turn();
        controller.dealCards();
        dealTheCards(game); //Deal Cards Test.
        jButton1.setEnabled(false);

        try {
            Thread.sleep(500);
        } catch (InterruptedException ex) {
            Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
        }

        String allNames = ">";
        for(int i = 0; i<names.size(); i++){
            nameLbls[i].setText(names.elementAt(i).toString());
            allNames += names.elementAt(i) + ">";
            game.getPlayers().elementAt(i).setMoney(startingMoney);
            game.getPlayers().elementAt(i).setName(names.elementAt(i).toString());
            plr1MoneyLabel.setText( "$ " + startingMoney);
            plr2MoneyLabel.setText( "$ " + startingMoney);
            plr3MoneyLabel.setText( "$ " + startingMoney);
            plr4MoneyLabel.setText( "$ " + startingMoney);

        }

        chatServer.SendMessage(allNames + startingMoney);
        plrNameLabel.setText(names.elementAt(0).toString());



    }//GEN-LAST:event_jButton1ActionPerformed
    int counter2 = 0;
    private void plr1C2LabelMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_plr1C2LabelMouseClicked
        if (theTurn == 0) {
            ++counter2;

            if (theTurn != 0) {
                JOptionPane.showMessageDialog(null, "It is not your turn!", "Player Error", JOptionPane.ERROR_MESSAGE);
            } else if (counter2 == 2) {
                --flipcount1;
                for (int i = 0; i < 5; i++) {
                    if (burnhand.elementAt(i) == hand.elementAt(1)) {
                        burnhand.removeElementAt(i);
                        String temp;
                        temp = (Integer) hand.elementAt(1) + ".png";
                        Icon main = new ImageIcon(getClass().getResource(temp));
                        plr1C2Label.setIcon(main);
                        break;
                    }
                }
                counter2 = 0;
            } else {
                ++flipcount1;
                Icon back = new ImageIcon(getClass().getResource("b2fv.png"));
                plr1C2Label.setIcon(back);
                burnhand.add(1, hand.elementAt(1));
            }
        }
    }//GEN-LAST:event_plr1C2LabelMouseClicked
    int counter3 = 0;
    private void plr1C3LabelMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_plr1C3LabelMouseClicked
        if (theTurn == 0) {
            ++counter3;

            if (theTurn != 0) {
                JOptionPane.showMessageDialog(null, "It is not your turn!", "Player Error", JOptionPane.ERROR_MESSAGE);
            } else if (counter3 == 2) {
                --flipcount1;
                for (int i = 0; i < 5; i++) {
                    if (burnhand.elementAt(i) == hand.elementAt(2)) {
                        burnhand.removeElementAt(i);
                        String temp;
                        temp = (Integer) hand.elementAt(2) + ".png";
                        Icon main = new ImageIcon(getClass().getResource(temp));
                        plr1C3Label.setIcon(main);
                        break;
                    }
                }
                counter3 = 0;
            } else {
                ++flipcount1;
                Icon back = new ImageIcon(getClass().getResource("b2fv.png"));
                plr1C3Label.setIcon(back);
                burnhand.add(2, hand.elementAt(2));
            }
        }
    }//GEN-LAST:event_plr1C3LabelMouseClicked
    int counter4 = 0;
    private void plr1C4LabelMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_plr1C4LabelMouseClicked
        if (theTurn == 0) {
            ++counter4;

            if (theTurn != 0) {
                JOptionPane.showMessageDialog(null, "It is not your turn!", "Player Error", JOptionPane.ERROR_MESSAGE);
            } else if (counter4 == 2) {
                --flipcount1;
                for (int i = 0; i < 5; i++) {
                    if (burnhand.elementAt(i) == hand.elementAt(3)) {
                        burnhand.removeElementAt(i);
                        String temp;
                        temp = (Integer) hand.elementAt(3) + ".png";
                        Icon main = new ImageIcon(getClass().getResource(temp));
                        plr1C4Label.setIcon(main);
                        break;
                    }
                }
                counter4 = 0;
            } else {
                ++flipcount1;
                Icon back = new ImageIcon(getClass().getResource("b2fv.png"));
                plr1C4Label.setIcon(back);
                burnhand.add(3, hand.elementAt(3));
            }
        }
    }//GEN-LAST:event_plr1C4LabelMouseClicked
    int counter5 = 0;
    private void plr1C5LabelMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_plr1C5LabelMouseClicked
        if (theTurn == 0) {
            ++counter5;

            if (theTurn != 0) {
                JOptionPane.showMessageDialog(null, "It is not your turn!", "Player Error", JOptionPane.ERROR_MESSAGE);
            } else if (counter5 == 2) {
                --flipcount1;
                for (int i = 0; i < 5; i++) {
                    if (burnhand.elementAt(i) == hand.elementAt(4)) {
                        burnhand.removeElementAt(i);
                        String temp;
                        temp = (Integer) hand.elementAt(4) + ".png";
                        Icon main = new ImageIcon(getClass().getResource(temp));
                        plr1C5Label.setIcon(main);
                        break;
                    }
                }
                counter5 = 0;
            } else {
                ++flipcount1;
                Icon back = new ImageIcon(getClass().getResource("b2fv.png"));
                plr1C5Label.setIcon(back);
                burnhand.add(4, hand.elementAt(4));
            }
        }
    }//GEN-LAST:event_plr1C5LabelMouseClicked
    int discardCounter = 0;
    private void discardButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_discardButtonActionPerformed
        ++discardCounter;
        if (theTurn != 0) {
            JOptionPane.showMessageDialog(null, "It is not your turn!", "Player Error", JOptionPane.ERROR_MESSAGE);
            
        } else if (flipcount1 > limit) {
           
            JOptionPane.showMessageDialog(null, "Can only discard up to " + limit + " cards!", "Player Error", JOptionPane.ERROR_MESSAGE);
            
        
        } else {
            flipcount1 = 0;
            discardCounter = 0;
            discardServer = new String[burnhand.size()];
            for (int i = 0; i < burnhand.size(); i++) {
                discardServer[i] = burnhand.elementAt(i).toString();



            }
            discardBool = true;
            if (discardBool == true) {
                discard();
                burnhand.clear();
                burnhand.addElement(56);
                burnhand.addElement(56);
                burnhand.addElement(56);
                burnhand.addElement(56);
                burnhand.addElement(56);
                counter1 = 0;
                counter2 = 0;
                counter3 = 0;
                counter4 = 0;
                counter5 = 0;

            }
        }

    }//GEN-LAST:event_discardButtonActionPerformed

    private void betButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_betButtonActionPerformed
        if (theTurn != 0) {
            JOptionPane.showMessageDialog(null, "It is not your turn!", "Player Error", JOptionPane.ERROR_MESSAGE);
        } else {

            int n = JOptionPane.showConfirmDialog(null, "Are you sure you want to bet this amount: $" + plr1BetField.getText());
            if (n == 0) {
                clientBets = Integer.parseInt(plr1BetField.getText().toString());
                determineBet();

            }
        }



    }//GEN-LAST:event_betButtonActionPerformed

    private void foldButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_foldButtonActionPerformed
        if (theTurn != 0) {
            JOptionPane.showMessageDialog(null, "It is not your turn!", "Player Error", JOptionPane.ERROR_MESSAGE);
        } else {
            int n = JOptionPane.showConfirmDialog(null, "Are you sure you want to fold?");
            if (n == 0) {
                betButton.setEnabled(false);
                foldButton.setEnabled(false);
                checkButton.setEnabled(false);
                discardButton.setEnabled(false);
                playerFolds(0);


                //turn();

            }
        }


    }//GEN-LAST:event_foldButtonActionPerformed

    private void checkButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_checkButtonActionPerformed
        if (theTurn != 0) {
            JOptionPane.showMessageDialog(null, "It is not your turn!", "Player Error", JOptionPane.ERROR_MESSAGE);
        } else {

            int n = JOptionPane.showConfirmDialog(null, "Are you sure you want to check?");
            if (n == 0) {
                clientBets = 0;
                determineBet();
            }
        }
    }//GEN-LAST:event_checkButtonActionPerformed

    private void formWindowClosing(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosing
         JOptionPane.showMessageDialog(null, "You Are The Server and Cannot Disconnect Until The Game Is Over", "ERROR", JOptionPane.ERROR_MESSAGE);
    }//GEN-LAST:event_formWindowClosing

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /*
         * Set the Nimbus look and feel
         */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /*
         * If Nimbus (introduced in Java SE 6) is not available, stay with the
         * default look and feel. For details see
         * http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(CountdownServer.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(CountdownServer.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(CountdownServer.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(CountdownServer.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /*
         * Create and display the form
         */
        java.awt.EventQueue.invokeLater(new Runnable() {

            public void run() {
                //new CountdownServer().setVisible(true);


            }
        });
    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel backGroundPanel;
    private javax.swing.JButton betButton;
    private javax.swing.JToolBar blackJackToolbar;
    private javax.swing.JButton checkButton;
    private javax.swing.JButton discardButton;
    private javax.swing.JButton foldButton;
    private javax.swing.JButton jButton1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JToolBar.Separator jSeparator1;
    private javax.swing.JPanel player1CardPanel;
    private javax.swing.JPanel player1OuterPanel;
    private javax.swing.JPanel player2CardPanel;
    private javax.swing.JPanel player2OuterPanel;
    private javax.swing.JPanel player3CardPanel;
    private javax.swing.JPanel player3OuterPanel;
    private javax.swing.JPanel player4CardPanel;
    private javax.swing.JPanel player4OuterPanel;
    private javax.swing.JTextField plr1BetField;
    private javax.swing.JLabel plr1C1Label;
    private javax.swing.JLabel plr1C2Label;
    private javax.swing.JLabel plr1C3Label;
    private javax.swing.JLabel plr1C4Label;
    private javax.swing.JLabel plr1C5Label;
    private javax.swing.JLabel plr1MoneyLabel;
    private javax.swing.JLabel plr1NameLabel;
    private javax.swing.JTextField plr2BetField;
    private javax.swing.JLabel plr2C1Label;
    private javax.swing.JLabel plr2C2Label;
    private javax.swing.JLabel plr2C3Label;
    private javax.swing.JLabel plr2C4Label;
    private javax.swing.JLabel plr2C5Label;
    private javax.swing.JLabel plr2MoneyLabel;
    private javax.swing.JLabel plr2NameLabel;
    private javax.swing.JTextField plr3BetField;
    private javax.swing.JLabel plr3C1Label;
    private javax.swing.JLabel plr3C2Label;
    private javax.swing.JLabel plr3C3Label;
    private javax.swing.JLabel plr3C4Label;
    private javax.swing.JLabel plr3C5Label;
    private javax.swing.JLabel plr3MoneyLabel;
    private javax.swing.JLabel plr3NameLabel;
    private javax.swing.JTextField plr4BetField;
    private javax.swing.JLabel plr4C1Label;
    private javax.swing.JLabel plr4C2Label;
    private javax.swing.JLabel plr4C3Label;
    private javax.swing.JLabel plr4C4Label;
    private javax.swing.JLabel plr4C5Label;
    private javax.swing.JLabel plr4MoneyLabel;
    private javax.swing.JLabel plr4NameLabel;
    private javax.swing.JLabel plrNameLabel;
    private javax.swing.JLabel potLabel;
    private javax.swing.JLabel roundLabel;
    // End of variables declaration//GEN-END:variables

    public void gameOver() {
        plr2Fold = false;
        plr3Fold = false;
        plr4Fold = false;
        // System.out.println("WINNER: " + game.getWinner().getHandName());
        String output = "";
        output = "Winner:" + controller.getWinner().getName() + ": " + controller.getWinner().getHandName() + "\n";

        if (game.getPlayers().size() == 2) {
            output += "\n\n" + game.getPlayers().elementAt(0).getName() + ": " + game.getPlayers().elementAt(0).getHandName();
            output += "\n\n" + game.getPlayers().elementAt(1).getName() + ": " + game.getPlayers().elementAt(1).getHandName();
        } else if (game.getPlayers().size() == 3) {
            output += "\n\n" + game.getPlayers().elementAt(0).getName() + ": " + game.getPlayers().elementAt(0).getHandName();
            output += "\n\n" + game.getPlayers().elementAt(1).getName() + ": " + game.getPlayers().elementAt(1).getHandName();
            output += "\n\n" + game.getPlayers().elementAt(2).getName() + ": " + game.getPlayers().elementAt(2).getHandName();
        } else if (game.getPlayers().size() == 4) {
            output += "\n\n" + game.getPlayers().elementAt(0).getName() + ": " + game.getPlayers().elementAt(0).getHandName();
            output += "\n\n" + game.getPlayers().elementAt(1).getName() + ": " + game.getPlayers().elementAt(1).getHandName();
            output += "\n\n" + game.getPlayers().elementAt(2).getName() + ": " + game.getPlayers().elementAt(2).getHandName();
            output += "\n\n" + game.getPlayers().elementAt(3).getName() + ": " + game.getPlayers().elementAt(3).getHandName();
        }

        theRound = 7;
        dealTheCards(game);
        // controller.dealCards();
        // dealTheCards(game);


        try {
            Thread.sleep(2000);
        } catch (InterruptedException ex) {
            Logger.getLogger(CountdownServer.class.getName()).log(Level.SEVERE, null, ex);
        }
        chatServer.SendMessage("@" + output);
        final JOptionPane pane = new JOptionPane(output);
        pane.setMessageType(JOptionPane.INFORMATION_MESSAGE);
        pane.setOptions(new Object[]{});

        final JDialog dialog = pane.createDialog(null, "Winner");



        final Timer timer = new Timer(5500, new ActionListener() {

            public void actionPerformed(final ActionEvent e) {
                dialog.setVisible(false);
            }
        });
        timer.setRepeats(false);
        timer.start();
        dialog.setVisible(true);
        dialog.dispose();
        try {
            Thread.sleep(1000);
        } catch (InterruptedException ex) {
            Logger.getLogger(CountdownServer.class.getName()).log(Level.SEVERE, null, ex);
        }
        if (theRound == 7 && game.playersStillIn() == 1 || theRound == 7 && game.playersStillIn() == 2 || theRound == 7 && game.playersStillIn() == 3 || theRound == 7 && game.playersStillIn() == 4) {
            outputAll();
            reset = true;

        }





         //turn();
    }

    public void populateData() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void outputAll() {
        if (theRound == 7) {
            plr2Fold = false;
            plr3Fold = false;
            plr4Fold = false;
            betButton.setEnabled(true);
            foldButton.setEnabled(true);
            checkButton.setEnabled(true);
            discardButton.setEnabled(false);
            if (game.getPlayers().size() <= 4) {
                plr1MoneyLabel.setText("$" + Integer.toString(game.getPlayers().elementAt(0).getMoney()));
                plr1BetField.setText(Integer.toString(game.getPlayers().elementAt(0).getCurrentBet()));
                plr2MoneyLabel.setText("$" + Integer.toString(game.getPlayers().elementAt(1).getMoney()));
                plr2BetField.setText(Integer.toString(game.getPlayers().elementAt(1).getCurrentBet()));
                chatServer.SendMessage("%" + "," + Integer.toString(game.getPlayers().elementAt(0).getMoney()) + "," + game.getPlayers().elementAt(0).getCurrentBet() + "," + 0);
               
                try {
                    Thread.sleep(500);
                } catch (InterruptedException ex) {
                    Logger.getLogger(CountdownServer.class.getName()).log(Level.SEVERE, null, ex);
                }
                chatServer.SendMessage("%" + "-" + Integer.toString(game.getPlayers().elementAt(1).getMoney()) + "-" + game.getPlayers().elementAt(1).getCurrentBet() + "-" + 0);
                
            }
            if (game.getPlayers().size() >= 3) {
                try {
                    Thread.sleep(500);
                } catch (InterruptedException ex) {
                    Logger.getLogger(CountdownServer.class.getName()).log(Level.SEVERE, null, ex);
                }
                plr3MoneyLabel.setText("$" + Integer.toString(game.getPlayers().elementAt(2).getMoney()));
                plr3BetField.setText(Integer.toString(game.getPlayers().elementAt(2).getCurrentBet()));
                chatServer.SendMessage("%" + ":" + Integer.toString(game.getPlayers().elementAt(2).getMoney()) + ":" + game.getPlayers().elementAt(2).getCurrentBet() + ":" + 0);
               
            }
            if (game.getPlayers().size() == 4) {
                try {
                    Thread.sleep(500);
                } catch (InterruptedException ex) {
                    Logger.getLogger(CountdownServer.class.getName()).log(Level.SEVERE, null, ex);
                }
                plr4MoneyLabel.setText("$" + Integer.toString(game.getPlayers().elementAt(3).getMoney()));
                plr4BetField.setText(Integer.toString(game.getPlayers().elementAt(3).getCurrentBet()));
                chatServer.SendMessage("%" + ";" + Integer.toString(game.getPlayers().elementAt(3).getMoney()) + ";" + game.getPlayers().elementAt(3).getCurrentBet() + ";" + 0);
               
            }
            potLabel.setText("Pot Amount: $" + 0);
            theRound = 0;


            //while(numberOfPlayers+1 != game.getPlayers().size());
            //   JOptionPane.showMessageDialog(null, "Awaiting Response From Client(s)...Please Wait...");



            potLabel.setText("Pot Amount: $0");
           
            numberOfPlayers = 0;
            plr2Fold = false;
            plr3Fold = false;
            plr4Fold = false;

            if (plrOutCounter != 1) {
                controller.dealCards();
                dealTheCards(game);
            }else{
                endGame();
            }




        }
    }

    public void endGame() {
        String output = "";
        output = "The winner of the game is: " + controller.getWinner().getName();
        chatServer.SendMessage("<"+output);
        try {
            Thread.sleep(1000);
        } catch (InterruptedException ex) {
            Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        JOptionPane.showMessageDialog(null, output);
        welcome = new WelcomeScreen();
        this.setVisible(false);
        welcome.setVisible(true);

    }

//    public void nextRound(int round) {
//        theRound = round;
//        System.out.println("ROUND: " +theRound);
//       // chatServer.SendMessage("r" + theRound);
//        if(theRound == 0){
//            roundLabel.setText("Current Round: Betting Round");
//        }else if(theRound == 1){
//            roundLabel.setText("Current Round: Trade In Round");
//        }else if(theRound == 2){
//            roundLabel.setText("current Round: Betting Round");
//        }
//    }
    // End of variables declaration
    public class myServer extends Thread {

        public LinkedList clients;
        public ByteBuffer read;
        public ByteBuffer write;
        public ServerSocketChannel ss;
        public Selector readerSelector;
        public CharsetDecoder asciiDecoder;

        public myServer() {
            clients = new LinkedList();
            read = ByteBuffer.allocateDirect(300);
            write = ByteBuffer.allocateDirect(300);
            asciiDecoder = Charset.forName("US-ASCII").newDecoder();


        }

        public void InitServer() {
            try {
                ss = ServerSocketChannel.open();
                ss.configureBlocking(false);
                serverAddress = InetAddress.getLocalHost();
                ss.socket().bind(new InetSocketAddress(serverAddress, port));
                readerSelector = Selector.open();
                //chatWindow.setText(serverAddress.getHostName() + "<Server> Started.\n");
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(null, "Error: " + ex.toString());
            }
        }

        public void run() {
            InitServer();

            while (true) {
                acceptNewConnection();
                readMessage();
                try {
                    Thread.sleep(100);
                } catch (InterruptedException ex) {
                    JOptionPane.showMessageDialog(null, "Error: " + ex.toString());
                }
            }
        }

        public void acceptNewConnection() {
            SocketChannel newClient;

            try {
                while ((newClient = ss.accept()) != null) {
                    chatServer.addClient(newClient);
                    newClientNumber++;
                    plrOutCounter++;
                    game.addPlayer("Player" + newClientNumber);



                    SendMessage(Integer.toString(newClientNumber));

                    turn.addElement(new Integer(newClientNumber - 1));



                    //sendBroadcastMessage(newClient, "2");
                    //sendMessage(newClient, serverAddress.getHostName() + "<server> You are connected... type 'quit' to exit");
                }
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(null, "Error:" + ex.toString());
            }

        }

        public void addClient(SocketChannel newClient) {
            clients.add(newClient);
            try {
                newClient.configureBlocking(false);
                newClient.register(readerSelector, SelectionKey.OP_READ, new StringBuffer());
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(null, "Error" + ex.toString());
            }
        }

        public void sendMessage(SocketChannel client, String msg) {
            prepareBuffer(msg);
            channelWrite(client);
        }

        public void SendMessage(String msg) {

            if (clients.size() > 0) {
                for (int i = 0; i < clients.size(); i++) {
                    SocketChannel client = (SocketChannel) clients.get(i);
                    sendMessage(client, msg);
                }
            }
        }

        public void prepareBuffer(String msg) {
            write.clear();

            write.put(msg.getBytes());

            write.putChar('\n');
            write.flip();


        }

        public void channelWrite(SocketChannel client) {
            long num = 0;
            long length = write.remaining();
            while (num != length) {
                try {
                    num += client.write(write);
                    Thread.sleep(5);
                } catch (IOException ex) {
                    JOptionPane.showMessageDialog(null, "Error: " + ex.toString());
                } catch (InterruptedException ex) {
                }
            }
            write.rewind();

        }

        public void sendBroadcastMessage(SocketChannel client, String msg) {
            prepareBuffer(msg);
            Iterator i = clients.iterator();
            while (i.hasNext()) {
                SocketChannel channel = (SocketChannel) i.next();
                if (channel != client) {
                    channelWrite(channel);
                }
            }
        }

        public void readMessage() {
            try {
                readerSelector.selectNow();
                Set readkeys = readerSelector.selectedKeys();
                Iterator iter = readkeys.iterator();
                while (iter.hasNext()) {
                    SelectionKey key = (SelectionKey) iter.next();
                    iter.remove();

                    SocketChannel client = (SocketChannel) key.channel();
                    read.clear();

                    long num = client.read(read);

                    if (num == -1) {
                        client.close();
                        clients.remove(client);
                        sendBroadcastMessage(client, "logout: " + client.socket().getInetAddress());

                    } else {
                        //StringBuffer str = (StringBuffer) key.attachment();
                        read.flip();
                        String data = asciiDecoder.decode(read).toString();
                        read.clear();

                        // str.append(data);

                        String line = data;

                        if ((line.indexOf("\n") != -1) || (line.indexOf("\r") != -1)) {
                            line = line.trim();

                            if (line.charAt(0) == '-') {
                                discard = line.split("-");
                                //System.out.println(discard[1] + " " + discard[2] + " " + discard[3]);
                                discardBool = true;
                            } else if (line.charAt(0) == ':') {
                                discard = line.split(":");
                                discardBool = true;
                            } else if (line.charAt(0) == (';')) {
                                discard = line.split(";");
                                discardBool = true;
                            }


                            //System.out.println(discardBool);
                            if (discardBool == true) {
                                
                                discard();
                            }

                            //Betting
                            if (line.charAt(0) == '$' && line.charAt(1) == '-') {
                                clientBet = line.split("-");
                                clientBets = Integer.parseInt(clientBet[1]);
                                determineBet();
                            } else if (line.charAt(0) == '$' && line.charAt(1) == ':') {
                                clientBet = line.split(":");
                                clientBets = Integer.parseInt(clientBet[1]);
                                determineBet();
                            } else if (line.charAt(0) == '$' && line.charAt(1) == ';') {
                                clientBet = line.split(";");
                                clientBets = Integer.parseInt(clientBet[1]);
                                determineBet();
                            }

                            if (line.charAt(0) == '^') {
                                playerFold = line.charAt(1) - 48;
                                playerFolds(playerFold);
                            }
                            if (line.charAt(0) == '0') {
                                numberOfPlayers++;
                            }

                            if(line.charAt(0) == '>'){
                                String nameTemp[];
                                nameTemp = line.split(">");
                                names.add(new String(nameTemp[1]));
                               
                            }

                            if (line.equals("quit")) {
                                client.close();
                                clients.remove(client);
                                game.removePlayer(1);
                                chatServer.SendMessage("o1");
                                plr2Out = true;

                            }
                            if(line.equals("quits")){
                                client.close();
                                clients.remove(client);
                                game.removePlayer(2);
                                chatServer.SendMessage("o2");
                                plr3Out = true;
                            }
                            if(line.equals("quitter")){
                                client.close();
                                clients.remove(client);
                                game.removePlayer(3);
                                chatServer.SendMessage("o3");
                                plr3Out = true;
                            }


                        }
                    }
                }
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(null, "Error: " + ex.toString());
            }
        }
    }//End Thread Class
}// End Server.java Class

