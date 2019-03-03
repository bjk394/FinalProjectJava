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
public class HoldemServer extends javax.swing.JFrame implements iServer {

    public ObjectOutputStream out;
    public ObjectInputStream in;
    public ServerSocket ss;
    public Socket connection;
    public InetAddress serverAddress;
    public myServer chatServer;
    public String name = "";
    public String chatWindow;
    public JLabel jlbls[] = new JLabel[13];
    public JLabel nameLbls[] = new JLabel[5];
    public String packet = "";
    public String cardPlr2 = "-", cardPlr3 = ":", cardPlr4 = ";";
    public int newClientNumber = 1;
    public int plrOutCounter = 1;
    public int lastElement = 0;
    Vector turn = new Vector();
    Vector clientHand = new Vector();
    Vector hand = new Vector();
    //Vector burnhand = new Vector();
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
    public int temp[];
    Vector riverCards = new Vector();
    Game game;
    iGameController controller;
    InetAddress inet;

    public int port = 4000;
    Vector names = new Vector();
    public int startingMoney = 0;
    public String ipAddress = "";
    WelcomeScreen welcome;

    public HoldemServer(int prt, String name, int start, String ip) {
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
            Logger.getLogger(HoldemServer.class.getName()).log(Level.SEVERE, null, ex);
        }
        

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
          jlbls[0] = plr1C1;
          jlbls[1] = plr1C2;

        //PLAYER 2 Initialization
          jlbls[2] = plr2C1;
          jlbls[3] = plr2C2;

        //PLAYER 3 Initialization
          jlbls[4] = plr3C1;
          jlbls[5] = plr3C2;

        //PLAYER 3 Initialization
          jlbls[6] = plr4C1;
          jlbls[7] = plr4C2;

        //Table Initialization
          jlbls[8] = flopL1;
          jlbls[9] = flopL2;
          jlbls[10] = flopL3;
          jlbls[11] = turnL;
          jlbls[12] = riverL;


        game = new Game();  //Starts a new Game
        controller = new HoldemController(); //Starts a new Poeker Controller

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
    public void turnOver(){
        String image;
        int length = 0;
        if(theRound == 1){
            length = 3;
        }else if(theRound == 2){
            length = 4;
        }else if(theRound == 3 || theRound == 4){
            length = 5;
        }
        for(int i =0; i<length; i++){
        image = Integer.toString((Integer)riverCards.elementAt(i)) + ".png"; //Sets card image to card number
        Icon card = new ImageIcon(getClass().getResource(image));
        jlbls[i+8].setIcon(card);
        }
    }
    public void dealTheCards(Game game) {
        //if(game.get == 1)
        // endGame();
        turnOver();
        String image = ""; //Initializes a card image to ""
        allCards = "~";    //Delimited string to send the client EVERY PLAYER'S CARDS
        int count = 0;
        cardPlr2 = "-";    //Delimited string to send the client PLAYER TWO'S(2) CARDS
        cardPlr3 = ":";    //Delimited string to send the client PLAYER THREE'S(3) CARDS
        cardPlr4 = ";";    //Delimited string to send the client PLAYER FOUR'S(4) CARDS
        String cardMiddle = "!";

        //hand.clear();   //Resets the hand to zero(0) elements

        for (int i = 0; i < game.getPlayers().size(); i++) {
            if (i == 0 && lock1 == false) {
                player1In = game.getPlayers().elementAt(i).getMoney();
                if (player1In == 0 && theRound == 4) {
                    plr1Out = true;
                    plrOutCounter--;
                    lock1 = true;

                }
            } else if (i == 1 && lock2 == false) {
                player2In = game.getPlayers().elementAt(i).getMoney();
                if (player2In == 0 && theRound == 4) {
                    plr2Out = true;
                    plrOutCounter--;
                    lock2 = true;

                }
            } else if (i == 2 && lock3 == false) {
                player3In = game.getPlayers().elementAt(i).getMoney();
                if (player3In == 0 && theRound == 4) {
                    plr3Out = true;
                    plrOutCounter--;
                    lock3 = true;

                }
            } else if (i == 3 && lock4 == false) {
                player4In = game.getPlayers().elementAt(i).getMoney();
                if (player4In == 0 && theRound == 4) {
                    plr4Out = true;
                    plrOutCounter--;
                    lock4 = true;


                }
            }
        }


        for (int i = 0; i < game.getPlayers().size(); i++) { //For loop for each seperate player

            for (int j = 0; j < 2; j++) { //For loop for the current label to place card image

                image = Integer.toString(game.getPlayers().elementAt(i).getCardsInHand().elementAt(j).getCardNumber()) + ".png"; //Sets card image to card number
                int ServerHand = game.getPlayers().elementAt(i).getCardsInHand().elementAt(j).getCardNumber();
                allCards += Integer.toString(ServerHand) + "~";

                

                int num = (i * 2) + j;
               

                if (i == 0 || theRound == 4) {
                    Icon card = new ImageIcon(getClass().getResource(image));
                    jlbls[num].setIcon(card);
                } else {
                    if (plr2Fold == true && num == 2 || plr2Fold == true && num == 3) {
                        Icon card = new ImageIcon(getClass().getResource("53.png"));
                        jlbls[num].setIcon(card);

                    } else if (plr3Fold == true && num == 4 || plr3Fold == true && num == 5) {
                        Icon card = new ImageIcon(getClass().getResource("53.png"));
                        jlbls[num].setIcon(card);
                    } else if (plr4Fold == true && num == 6 || plr4Fold == true && num == 7) {
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
                                Logger.getLogger(HoldemServer.class.getName()).log(Level.SEVERE, null, ex);
                            }
                            
                        }

                       
                    }  if (plr2Out == true && num == 2 || plr2Out == true && num == 3) {
                        if(lockPlr2 == false){
                       chatServer.SendMessage("o1");
                       lockPlr2 = true;
                        try {
                                Thread.sleep(1000);
                            } catch (InterruptedException ex) {
                                Logger.getLogger(HoldemServer.class.getName()).log(Level.SEVERE, null, ex);
                            }
                        }
                        Icon card = new ImageIcon(getClass().getResource("53.png"));
                        jlbls[num].setIcon(card);
                        
                    }  if (plr3Out == true && num == 4 || plr3Out == true && num == 5) {
                        if(lockPlr3 == false){
                       chatServer.SendMessage("o2");
                       lockPlr3 = true;
                        try {
                                Thread.sleep(1000);
                            } catch (InterruptedException ex) {
                                Logger.getLogger(HoldemServer.class.getName()).log(Level.SEVERE, null, ex);
                            }
                        }
                        Icon card = new ImageIcon(getClass().getResource("53.png"));
                        jlbls[num].setIcon(card);
                       
                    }  if (plr4Out == true && num == 6 || plr4Out == true && num == 7 ) {
                        if(lockPlr4 == false){
                       chatServer.SendMessage("o3");
                       lockPlr4 = true;
                        try {
                                Thread.sleep(1000);
                            } catch (InterruptedException ex) {
                                Logger.getLogger(HoldemServer.class.getName()).log(Level.SEVERE, null, ex);
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
            Logger.getLogger(HoldemServer.class.getName()).log(Level.SEVERE, null, ex);
        }
{

        }
        chatServer.SendMessage("p" + game.getPlayers().size() + "p");
       
        if (theRound == 4 || game.playersStillIn() == 1) {
            chatServer.SendMessage(allCards);
           
        }
        for(int i =0; i<game.getTable().getRiverCards().size(); i++){
            cardMiddle += game.getTable().getRiverCards().elementAt(i) + "!";
           
        }
       
        riverCards = game.getTable().getRiverCards();
        
        if(theRound !=3){
        chatServer.SendMessage(cardMiddle);
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

       

        //turn();

    }

    public void turn() {

        theTurn = game.getCurrentPlayerNumber();
        theRound = game.getRound();
        try {
            Thread.sleep(500);
        } catch (InterruptedException ex) {
            Logger.getLogger(HoldemServer.class.getName()).log(Level.SEVERE, null, ex);
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
            plr3NameLabel.setForeground(Color.WHITE);
            plr4NameLabel.setForeground(Color.WHITE);
            plr1NameLabel.setForeground(Color.WHITE);
        } else if (theTurn == 2) {
            plr3NameLabel.setForeground(Color.RED);
            plr4NameLabel.setForeground(Color.WHITE);
            plr1NameLabel.setForeground(Color.WHITE);
            plr2NameLabel.setForeground(Color.WHITE);
        } else if (theTurn == 3) {
            plr4NameLabel.setForeground(Color.RED);
            plr1NameLabel.setForeground(Color.WHITE);
            plr2NameLabel.setForeground(Color.WHITE);
            plr3NameLabel.setForeground(Color.WHITE);
        }


        
        if (theRound == 0) {
            roundLabel.setText("Current Round: Preliminary Betting Round");
            betButton.setEnabled(true);
            foldButton.setEnabled(true);
            checkButton.setEnabled(true);
            String image = "53.png"; //Sets card image to card number
            Icon card = new ImageIcon(getClass().getResource(image));
            jlbls[8].setIcon(card);
            jlbls[9].setIcon(card);
            jlbls[10].setIcon(card);
            jlbls[11].setIcon(card);
            jlbls[12].setIcon(card);

            if (reset == true) {
                potLabel.setText("Pot Amount: $0");
                reset = false;
            }

        } else if (theRound == 1) {
            roundLabel.setText("Current Round: Betting Round");
            //roundCompare = 1;

            turnOver();
        } else if (theRound == 2) {
            roundLabel.setText("current Round: Betting Round");
          
            turnOver();
        }else if (theRound == 3) {
            roundLabel.setText("current Round: Betting Round");          
            turnOver();
        }
        try {
            Thread.sleep(400);
        } catch (InterruptedException ex) {
            Logger.getLogger(HoldemServer.class.getName()).log(Level.SEVERE, null, ex);
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
                Logger.getLogger(HoldemServer.class.getName()).log(Level.SEVERE, null, ex);
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
            for (int i = 0; i < 2; i++) {
                Icon card = new ImageIcon(getClass().getResource(""));
                jlbls[i].setIcon(card);

            }
        }
        if (plr == 1) {
            for (int i = 2; i < 4; i++) {
                Icon card = new ImageIcon(getClass().getResource(""));
                jlbls[i].setIcon(card);
                plr2Fold = true;
            }
        } else if (plr == 2) {
            for (int i = 4; i < 6; i++) {
                Icon card = new ImageIcon(getClass().getResource(""));
                jlbls[i].setIcon(card);
                plr3Fold = true;
            }
        } else if (plr == 3) {
            for (int i = 6; i < 8; i++) {
                Icon card = new ImageIcon(getClass().getResource(""));
                jlbls[i].setIcon(card);
                plr4Fold = true;
            }
        }
        try {
            Thread.sleep(500);
        } catch (InterruptedException ex) {
            Logger.getLogger(HoldemServer.class.getName()).log(Level.SEVERE, null, ex);
        }

        controller.fold();
        if (game.playersStillIn() == 1) {
            gameOver();
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
        player2CardPanel = new javax.swing.JPanel();
        plr4NameLabel = new javax.swing.JLabel();
        plr4C1 = new javax.swing.JLabel();
        plr4C2 = new javax.swing.JLabel();
        jLabel1 = new javax.swing.JLabel();
        plr4MoneyLabel = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        plr4BetField = new javax.swing.JTextField();
        jLabel11 = new javax.swing.JLabel();
        riverL = new javax.swing.JLabel();
        player2CardPanel1 = new javax.swing.JPanel();
        plr2NameLabel = new javax.swing.JLabel();
        plr2C1 = new javax.swing.JLabel();
        plr2C2 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        plr2MoneyLabel = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        plr2BetField = new javax.swing.JTextField();
        jLabel14 = new javax.swing.JLabel();
        player2CardPanel2 = new javax.swing.JPanel();
        plr1NameLabel = new javax.swing.JLabel();
        plr1C1 = new javax.swing.JLabel();
        plr1C2 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        plr1MoneyLabel = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        plr1BetField = new javax.swing.JTextField();
        jLabel12 = new javax.swing.JLabel();
        player2CardPanel3 = new javax.swing.JPanel();
        plr3NameLabel = new javax.swing.JLabel();
        plr3C1 = new javax.swing.JLabel();
        plr3C2 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        plr3MoneyLabel = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        plr3BetField = new javax.swing.JTextField();
        jLabel13 = new javax.swing.JLabel();
        flopL1 = new javax.swing.JLabel();
        flopL2 = new javax.swing.JLabel();
        flopL3 = new javax.swing.JLabel();
        turnL = new javax.swing.JLabel();
        blackJackToolbar = new javax.swing.JToolBar();
        plrNameLabel = new javax.swing.JLabel();
        betButton = new javax.swing.JButton();
        foldButton = new javax.swing.JButton();
        checkButton = new javax.swing.JButton();
        jSeparator1 = new javax.swing.JToolBar.Separator();
        jButton1 = new javax.swing.JButton();
        jPanel1 = new javax.swing.JPanel();
        roundLabel = new javax.swing.JLabel();
        potLabel = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);
        setTitle("Hold 'EM Server");
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
        backGroundPanel.setPreferredSize(new java.awt.Dimension(1026, 778));
        backGroundPanel.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                backGroundPanelFocusGained(evt);
            }
        });
        backGroundPanel.setLayout(null);

        player2CardPanel.setBackground(new java.awt.Color(0, 51, 0));

        plr4NameLabel.setBackground(new java.awt.Color(255, 255, 255));
        plr4NameLabel.setFont(new java.awt.Font("Rockwell", 0, 30));
        plr4NameLabel.setForeground(new java.awt.Color(255, 255, 255));
        plr4NameLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        plr4NameLabel.setText("PLAYER #4");

        plr4C1.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(255, 255, 255), 1, true));

        plr4C2.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(255, 255, 255), 1, true));

        jLabel1.setFont(new java.awt.Font("Tahoma", 0, 14));
        jLabel1.setForeground(new java.awt.Color(255, 255, 255));
        jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel1.setText("Current Funds:");

        plr4MoneyLabel.setFont(new java.awt.Font("Rockwell", 0, 24));
        plr4MoneyLabel.setForeground(new java.awt.Color(255, 255, 255));
        plr4MoneyLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        plr4MoneyLabel.setText("0");

        jLabel3.setFont(new java.awt.Font("Tahoma", 0, 14));
        jLabel3.setForeground(new java.awt.Color(255, 255, 255));
        jLabel3.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel3.setText("Bet Amount:");

        plr4BetField.setEditable(false);

        jLabel11.setFont(new java.awt.Font("Tahoma", 0, 14));
        jLabel11.setForeground(new java.awt.Color(255, 255, 255));
        jLabel11.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel11.setText("$");

        javax.swing.GroupLayout player2CardPanelLayout = new javax.swing.GroupLayout(player2CardPanel);
        player2CardPanel.setLayout(player2CardPanelLayout);
        player2CardPanelLayout.setHorizontalGroup(
            player2CardPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(player2CardPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(player2CardPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, player2CardPanelLayout.createSequentialGroup()
                        .addComponent(plr4NameLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 378, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(57, 57, 57))
                    .addGroup(player2CardPanelLayout.createSequentialGroup()
                        .addGroup(player2CardPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(player2CardPanelLayout.createSequentialGroup()
                                .addGroup(player2CardPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(plr4MoneyLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 153, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addGroup(player2CardPanelLayout.createSequentialGroup()
                                        .addGap(10, 10, 10)
                                        .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 124, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 74, Short.MAX_VALUE)
                                .addGroup(player2CardPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(player2CardPanelLayout.createSequentialGroup()
                                        .addComponent(jLabel11)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(plr4BetField, javax.swing.GroupLayout.PREFERRED_SIZE, 105, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 124, javax.swing.GroupLayout.PREFERRED_SIZE)))
                            .addGroup(player2CardPanelLayout.createSequentialGroup()
                                .addGap(109, 109, 109)
                                .addComponent(plr4C1, javax.swing.GroupLayout.PREFERRED_SIZE, 78, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(plr4C2, javax.swing.GroupLayout.PREFERRED_SIZE, 78, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 80, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGap(84, 84, 84))))
        );
        player2CardPanelLayout.setVerticalGroup(
            player2CardPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(player2CardPanelLayout.createSequentialGroup()
                .addComponent(plr4NameLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 47, Short.MAX_VALUE)
                .addGroup(player2CardPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(plr4C2, javax.swing.GroupLayout.PREFERRED_SIZE, 118, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(plr4C1, javax.swing.GroupLayout.PREFERRED_SIZE, 118, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(46, 46, 46)
                .addGroup(player2CardPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(player2CardPanelLayout.createSequentialGroup()
                        .addComponent(jLabel1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(plr4MoneyLabel))
                    .addGroup(player2CardPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(jLabel3)
                        .addGroup(player2CardPanelLayout.createSequentialGroup()
                            .addGap(28, 28, 28)
                            .addGroup(player2CardPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(plr4BetField, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(jLabel11)))))
                .addContainerGap())
        );

        backGroundPanel.add(player2CardPanel);
        player2CardPanel.setBounds(730, 450, 445, 310);

        riverL.setForeground(new java.awt.Color(255, 255, 255));
        riverL.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(255, 255, 255), 1, true));
        riverL.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                riverLMouseClicked(evt);
            }
        });
        backGroundPanel.add(riverL);
        riverL.setBounds(730, 320, 78, 118);

        player2CardPanel1.setBackground(new java.awt.Color(0, 51, 0));

        plr2NameLabel.setBackground(new java.awt.Color(255, 255, 255));
        plr2NameLabel.setFont(new java.awt.Font("Rockwell", 0, 30));
        plr2NameLabel.setForeground(new java.awt.Color(255, 255, 255));
        plr2NameLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        plr2NameLabel.setText("PLAYER #2");

        plr2C1.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(255, 255, 255), 1, true));

        plr2C2.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(255, 255, 255), 1, true));

        jLabel2.setFont(new java.awt.Font("Tahoma", 0, 14));
        jLabel2.setForeground(new java.awt.Color(255, 255, 255));
        jLabel2.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel2.setText("Current Funds:");

        plr2MoneyLabel.setFont(new java.awt.Font("Rockwell", 0, 24));
        plr2MoneyLabel.setForeground(new java.awt.Color(255, 255, 255));
        plr2MoneyLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        plr2MoneyLabel.setText("0");

        jLabel4.setFont(new java.awt.Font("Tahoma", 0, 14));
        jLabel4.setForeground(new java.awt.Color(255, 255, 255));
        jLabel4.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel4.setText("Bet Amount:");

        plr2BetField.setEditable(false);

        jLabel14.setFont(new java.awt.Font("Tahoma", 0, 14));
        jLabel14.setForeground(new java.awt.Color(255, 255, 255));
        jLabel14.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel14.setText("$");

        javax.swing.GroupLayout player2CardPanel1Layout = new javax.swing.GroupLayout(player2CardPanel1);
        player2CardPanel1.setLayout(player2CardPanel1Layout);
        player2CardPanel1Layout.setHorizontalGroup(
            player2CardPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(player2CardPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(player2CardPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, player2CardPanel1Layout.createSequentialGroup()
                        .addComponent(plr2NameLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 378, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(57, 57, 57))
                    .addGroup(player2CardPanel1Layout.createSequentialGroup()
                        .addGroup(player2CardPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(player2CardPanel1Layout.createSequentialGroup()
                                .addGroup(player2CardPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(plr2MoneyLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 153, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addGroup(player2CardPanel1Layout.createSequentialGroup()
                                        .addGap(10, 10, 10)
                                        .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 124, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 74, Short.MAX_VALUE)
                                .addGroup(player2CardPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(player2CardPanel1Layout.createSequentialGroup()
                                        .addComponent(jLabel14)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(plr2BetField, javax.swing.GroupLayout.PREFERRED_SIZE, 105, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 124, javax.swing.GroupLayout.PREFERRED_SIZE)))
                            .addGroup(player2CardPanel1Layout.createSequentialGroup()
                                .addGap(109, 109, 109)
                                .addComponent(plr2C1, javax.swing.GroupLayout.PREFERRED_SIZE, 78, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(plr2C2, javax.swing.GroupLayout.PREFERRED_SIZE, 78, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 80, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGap(84, 84, 84))))
        );
        player2CardPanel1Layout.setVerticalGroup(
            player2CardPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(player2CardPanel1Layout.createSequentialGroup()
                .addComponent(plr2NameLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 47, Short.MAX_VALUE)
                .addGroup(player2CardPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(plr2C2, javax.swing.GroupLayout.PREFERRED_SIZE, 118, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(plr2C1, javax.swing.GroupLayout.PREFERRED_SIZE, 118, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(46, 46, 46)
                .addGroup(player2CardPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(player2CardPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel2)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(plr2MoneyLabel))
                    .addGroup(player2CardPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(jLabel4)
                        .addGroup(player2CardPanel1Layout.createSequentialGroup()
                            .addGap(28, 28, 28)
                            .addGroup(player2CardPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(plr2BetField, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(jLabel14)))))
                .addContainerGap())
        );

        backGroundPanel.add(player2CardPanel1);
        player2CardPanel1.setBounds(0, 0, 445, 310);

        player2CardPanel2.setBackground(new java.awt.Color(0, 51, 0));

        plr1NameLabel.setBackground(new java.awt.Color(255, 255, 255));
        plr1NameLabel.setFont(new java.awt.Font("Rockwell", 0, 30));
        plr1NameLabel.setForeground(new java.awt.Color(255, 255, 255));
        plr1NameLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        plr1NameLabel.setText("PLAYER #1");

        plr1C1.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(255, 255, 255), 1, true));

        plr1C2.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(255, 255, 255), 1, true));

        jLabel5.setFont(new java.awt.Font("Tahoma", 0, 14));
        jLabel5.setForeground(new java.awt.Color(255, 255, 255));
        jLabel5.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel5.setText("Current Funds:");

        plr1MoneyLabel.setFont(new java.awt.Font("Rockwell", 0, 24));
        plr1MoneyLabel.setForeground(new java.awt.Color(255, 255, 255));
        plr1MoneyLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        plr1MoneyLabel.setText("0");

        jLabel6.setFont(new java.awt.Font("Tahoma", 0, 14));
        jLabel6.setForeground(new java.awt.Color(255, 255, 255));
        jLabel6.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel6.setText("Bet Amount:");

        jLabel12.setFont(new java.awt.Font("Tahoma", 0, 14));
        jLabel12.setForeground(new java.awt.Color(255, 255, 255));
        jLabel12.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel12.setText("$");

        javax.swing.GroupLayout player2CardPanel2Layout = new javax.swing.GroupLayout(player2CardPanel2);
        player2CardPanel2.setLayout(player2CardPanel2Layout);
        player2CardPanel2Layout.setHorizontalGroup(
            player2CardPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(player2CardPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(player2CardPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, player2CardPanel2Layout.createSequentialGroup()
                        .addComponent(plr1NameLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 378, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(57, 57, 57))
                    .addGroup(player2CardPanel2Layout.createSequentialGroup()
                        .addGroup(player2CardPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(player2CardPanel2Layout.createSequentialGroup()
                                .addGroup(player2CardPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(plr1MoneyLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 153, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addGroup(player2CardPanel2Layout.createSequentialGroup()
                                        .addGap(10, 10, 10)
                                        .addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, 124, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 74, Short.MAX_VALUE)
                                .addGroup(player2CardPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(player2CardPanel2Layout.createSequentialGroup()
                                        .addComponent(jLabel12)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(plr1BetField, javax.swing.GroupLayout.PREFERRED_SIZE, 105, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addComponent(jLabel6, javax.swing.GroupLayout.PREFERRED_SIZE, 124, javax.swing.GroupLayout.PREFERRED_SIZE)))
                            .addGroup(player2CardPanel2Layout.createSequentialGroup()
                                .addGap(109, 109, 109)
                                .addComponent(plr1C1, javax.swing.GroupLayout.PREFERRED_SIZE, 78, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(plr1C2, javax.swing.GroupLayout.PREFERRED_SIZE, 78, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 80, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGap(84, 84, 84))))
        );
        player2CardPanel2Layout.setVerticalGroup(
            player2CardPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(player2CardPanel2Layout.createSequentialGroup()
                .addComponent(plr1NameLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 47, Short.MAX_VALUE)
                .addGroup(player2CardPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(plr1C2, javax.swing.GroupLayout.PREFERRED_SIZE, 118, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(plr1C1, javax.swing.GroupLayout.PREFERRED_SIZE, 118, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(46, 46, 46)
                .addGroup(player2CardPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(player2CardPanel2Layout.createSequentialGroup()
                        .addComponent(jLabel5)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(plr1MoneyLabel))
                    .addGroup(player2CardPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(jLabel6)
                        .addGroup(player2CardPanel2Layout.createSequentialGroup()
                            .addGap(28, 28, 28)
                            .addGroup(player2CardPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(plr1BetField, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(jLabel12)))))
                .addContainerGap())
        );

        backGroundPanel.add(player2CardPanel2);
        player2CardPanel2.setBounds(730, 0, 445, 310);

        player2CardPanel3.setBackground(new java.awt.Color(0, 51, 0));

        plr3NameLabel.setBackground(new java.awt.Color(255, 255, 255));
        plr3NameLabel.setFont(new java.awt.Font("Rockwell", 0, 30));
        plr3NameLabel.setForeground(new java.awt.Color(255, 255, 255));
        plr3NameLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        plr3NameLabel.setText("PLAYER #3");

        plr3C1.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(255, 255, 255), 1, true));

        plr3C2.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(255, 255, 255), 1, true));

        jLabel7.setFont(new java.awt.Font("Tahoma", 0, 14));
        jLabel7.setForeground(new java.awt.Color(255, 255, 255));
        jLabel7.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel7.setText("Current Funds:");

        plr3MoneyLabel.setFont(new java.awt.Font("Rockwell", 0, 24));
        plr3MoneyLabel.setForeground(new java.awt.Color(255, 255, 255));
        plr3MoneyLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        plr3MoneyLabel.setText("0");

        jLabel8.setFont(new java.awt.Font("Tahoma", 0, 14));
        jLabel8.setForeground(new java.awt.Color(255, 255, 255));
        jLabel8.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel8.setText("Bet Amount:");

        plr3BetField.setEditable(false);

        jLabel13.setFont(new java.awt.Font("Tahoma", 0, 14));
        jLabel13.setForeground(new java.awt.Color(255, 255, 255));
        jLabel13.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel13.setText("$");

        javax.swing.GroupLayout player2CardPanel3Layout = new javax.swing.GroupLayout(player2CardPanel3);
        player2CardPanel3.setLayout(player2CardPanel3Layout);
        player2CardPanel3Layout.setHorizontalGroup(
            player2CardPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(player2CardPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(player2CardPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, player2CardPanel3Layout.createSequentialGroup()
                        .addComponent(plr3NameLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 378, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(57, 57, 57))
                    .addGroup(player2CardPanel3Layout.createSequentialGroup()
                        .addGroup(player2CardPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(player2CardPanel3Layout.createSequentialGroup()
                                .addGroup(player2CardPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(plr3MoneyLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 153, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addGroup(player2CardPanel3Layout.createSequentialGroup()
                                        .addGap(10, 10, 10)
                                        .addComponent(jLabel7, javax.swing.GroupLayout.PREFERRED_SIZE, 124, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 74, Short.MAX_VALUE)
                                .addGroup(player2CardPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(player2CardPanel3Layout.createSequentialGroup()
                                        .addComponent(jLabel13)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(plr3BetField, javax.swing.GroupLayout.PREFERRED_SIZE, 105, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addComponent(jLabel8, javax.swing.GroupLayout.PREFERRED_SIZE, 124, javax.swing.GroupLayout.PREFERRED_SIZE)))
                            .addGroup(player2CardPanel3Layout.createSequentialGroup()
                                .addGap(109, 109, 109)
                                .addComponent(plr3C1, javax.swing.GroupLayout.PREFERRED_SIZE, 78, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(plr3C2, javax.swing.GroupLayout.PREFERRED_SIZE, 78, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 80, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGap(84, 84, 84))))
        );
        player2CardPanel3Layout.setVerticalGroup(
            player2CardPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(player2CardPanel3Layout.createSequentialGroup()
                .addComponent(plr3NameLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 47, Short.MAX_VALUE)
                .addGroup(player2CardPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(plr3C2, javax.swing.GroupLayout.PREFERRED_SIZE, 118, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(plr3C1, javax.swing.GroupLayout.PREFERRED_SIZE, 118, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(46, 46, 46)
                .addGroup(player2CardPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(player2CardPanel3Layout.createSequentialGroup()
                        .addComponent(jLabel7)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(plr3MoneyLabel))
                    .addGroup(player2CardPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(jLabel8)
                        .addGroup(player2CardPanel3Layout.createSequentialGroup()
                            .addGap(28, 28, 28)
                            .addGroup(player2CardPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(plr3BetField, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(jLabel13)))))
                .addContainerGap())
        );

        backGroundPanel.add(player2CardPanel3);
        player2CardPanel3.setBounds(0, 450, 445, 310);

        flopL1.setForeground(new java.awt.Color(255, 255, 255));
        flopL1.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(255, 255, 255), 1, true));
        flopL1.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                flopL1MouseClicked(evt);
            }
        });
        backGroundPanel.add(flopL1);
        flopL1.setBounds(370, 320, 78, 118);

        flopL2.setForeground(new java.awt.Color(255, 255, 255));
        flopL2.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(255, 255, 255), 1, true));
        flopL2.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                flopL2MouseClicked(evt);
            }
        });
        backGroundPanel.add(flopL2);
        flopL2.setBounds(460, 320, 78, 118);

        flopL3.setForeground(new java.awt.Color(255, 255, 255));
        flopL3.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(255, 255, 255), 1, true));
        flopL3.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                flopL3MouseClicked(evt);
            }
        });
        backGroundPanel.add(flopL3);
        flopL3.setBounds(550, 320, 78, 118);

        turnL.setForeground(new java.awt.Color(255, 255, 255));
        turnL.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(255, 255, 255), 1, true));
        turnL.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                turnLMouseClicked(evt);
            }
        });
        backGroundPanel.add(turnL);
        turnL.setBounds(640, 320, 78, 118);

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
                .addContainerGap(104, Short.MAX_VALUE))
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
    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        turn();
        controller.dealCards();
        dealTheCards(game); //Deal Cards Test.
        jButton1.setEnabled(false);
        try {
            Thread.sleep(500);
        } catch (InterruptedException ex) {
            Logger.getLogger(HoldemServer.class.getName()).log(Level.SEVERE, null, ex);
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

    private void riverLMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_riverLMouseClicked
        // TODO add your handling code here:
    }//GEN-LAST:event_riverLMouseClicked

    private void flopL1MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_flopL1MouseClicked
        // TODO add your handling code here:
    }//GEN-LAST:event_flopL1MouseClicked

    private void flopL2MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_flopL2MouseClicked
        // TODO add your handling code here:
    }//GEN-LAST:event_flopL2MouseClicked

    private void flopL3MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_flopL3MouseClicked
        // TODO add your handling code here:
    }//GEN-LAST:event_flopL3MouseClicked

    private void turnLMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_turnLMouseClicked
        // TODO add your handling code here:
    }//GEN-LAST:event_turnLMouseClicked

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
            java.util.logging.Logger.getLogger(HoldemServer.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(HoldemServer.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(HoldemServer.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(HoldemServer.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /*
         * Create and display the form
         */
        java.awt.EventQueue.invokeLater(new Runnable() {

            public void run() {
                //new HoldemServer().setVisible(true);


            }
        });
    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel backGroundPanel;
    private javax.swing.JButton betButton;
    private javax.swing.JToolBar blackJackToolbar;
    private javax.swing.JButton checkButton;
    private javax.swing.JLabel flopL1;
    private javax.swing.JLabel flopL2;
    private javax.swing.JLabel flopL3;
    private javax.swing.JButton foldButton;
    private javax.swing.JButton jButton1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JToolBar.Separator jSeparator1;
    private javax.swing.JPanel player2CardPanel;
    private javax.swing.JPanel player2CardPanel1;
    private javax.swing.JPanel player2CardPanel2;
    private javax.swing.JPanel player2CardPanel3;
    private javax.swing.JTextField plr1BetField;
    private javax.swing.JLabel plr1C1;
    private javax.swing.JLabel plr1C2;
    private javax.swing.JLabel plr1MoneyLabel;
    private javax.swing.JLabel plr1NameLabel;
    private javax.swing.JTextField plr2BetField;
    private javax.swing.JLabel plr2C1;
    private javax.swing.JLabel plr2C2;
    private javax.swing.JLabel plr2MoneyLabel;
    private javax.swing.JLabel plr2NameLabel;
    private javax.swing.JTextField plr3BetField;
    private javax.swing.JLabel plr3C1;
    private javax.swing.JLabel plr3C2;
    private javax.swing.JLabel plr3MoneyLabel;
    private javax.swing.JLabel plr3NameLabel;
    private javax.swing.JTextField plr4BetField;
    private javax.swing.JLabel plr4C1;
    private javax.swing.JLabel plr4C2;
    private javax.swing.JLabel plr4MoneyLabel;
    private javax.swing.JLabel plr4NameLabel;
    private javax.swing.JLabel plrNameLabel;
    private javax.swing.JLabel potLabel;
    private javax.swing.JLabel riverL;
    private javax.swing.JLabel roundLabel;
    private javax.swing.JLabel turnL;
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

        theRound = 4;
        dealTheCards(game);
        // controller.dealCards();
        // dealTheCards(game);


        try {
            Thread.sleep(2000);
        } catch (InterruptedException ex) {
            Logger.getLogger(HoldemServer.class.getName()).log(Level.SEVERE, null, ex);
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
            Logger.getLogger(HoldemServer.class.getName()).log(Level.SEVERE, null, ex);
        }
        if (theRound == 4 && game.playersStillIn() == 1 || theRound == 4 && game.playersStillIn() == 2 || theRound == 4 && game.playersStillIn() == 3 || theRound == 4 && game.playersStillIn() == 4) {
            outputAll();
            reset = true;

        }





        // turn();
    }

    public void populateData() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void outputAll() {
        if (theRound == 4) {
            plr2Fold = false;
            plr3Fold = false;
            plr4Fold = false;
            betButton.setEnabled(true);
            foldButton.setEnabled(true);
            checkButton.setEnabled(true);            
            if (game.getPlayers().size() <= 4) {
                plr1MoneyLabel.setText("$" + Integer.toString(game.getPlayers().elementAt(0).getMoney()));
                plr1BetField.setText(Integer.toString(game.getPlayers().elementAt(0).getCurrentBet()));
                plr2MoneyLabel.setText("$" + Integer.toString(game.getPlayers().elementAt(1).getMoney()));
                plr2BetField.setText(Integer.toString(game.getPlayers().elementAt(1).getCurrentBet()));
                chatServer.SendMessage("%" + "," + Integer.toString(game.getPlayers().elementAt(0).getMoney()) + "," + game.getPlayers().elementAt(0).getCurrentBet() + "," + 0);
               
                try {
                    Thread.sleep(500);
                } catch (InterruptedException ex) {
                    Logger.getLogger(HoldemServer.class.getName()).log(Level.SEVERE, null, ex);
                }
                chatServer.SendMessage("%" + "-" + Integer.toString(game.getPlayers().elementAt(1).getMoney()) + "-" + game.getPlayers().elementAt(1).getCurrentBet() + "-" + 0);
                
            }
            if (game.getPlayers().size() >= 3) {
                try {
                    Thread.sleep(500);
                } catch (InterruptedException ex) {
                    Logger.getLogger(HoldemServer.class.getName()).log(Level.SEVERE, null, ex);
                }
                plr3MoneyLabel.setText("$" + Integer.toString(game.getPlayers().elementAt(2).getMoney()));
                plr3BetField.setText(Integer.toString(game.getPlayers().elementAt(2).getCurrentBet()));
                chatServer.SendMessage("%" + ":" + Integer.toString(game.getPlayers().elementAt(2).getMoney()) + ":" + game.getPlayers().elementAt(2).getCurrentBet() + ":" + 0);
               
            }
            if (game.getPlayers().size() == 4) {
                try {
                    Thread.sleep(500);
                } catch (InterruptedException ex) {
                    Logger.getLogger(HoldemServer.class.getName()).log(Level.SEVERE, null, ex);
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
        try {
            Thread.sleep(1000);
        } catch (InterruptedException ex) {
            Logger.getLogger(HoldemServer.class.getName()).log(Level.SEVERE, null, ex);
        }
        jlbls[8].setIcon(null);
            jlbls[9].setIcon(null);
            jlbls[10].setIcon(null);
            jlbls[11].setIcon(null);
            jlbls[12].setIcon(null);
        output = "The winner of the game is: " + controller.getWinner().getName();
        chatServer.SendMessage("<" + output);
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
//


                        }
                    }
                }
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(null, "Error: " + ex.toString());
            }
        }
    }//End Thread Class
}// End Server.java Class

