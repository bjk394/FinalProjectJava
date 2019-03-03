/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package casinoroyale;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.Timer;

/**
 *
 * @author Steve
 */
public class FiveCardClient extends javax.swing.JFrame {


    public clientThread chatClient;
    public WelcomeScreen startScreen;
    public readThread myRead = new readThread();
    public String serverName;
    public String userName;
    public String [] packet;
    public Vector hand = new Vector();
    public Vector burnhand = new Vector();

    public JLabel jlbls [] = new JLabel[20];
    Icon blank = new ImageIcon(getClass().getResource("53.png"));
    char numOfPlr;
    int playerNum;
    int player = 0;
    int turn = 0;
    int round = 0;
    int playerMoney = 0;
    int playerbet = 0;
    int playerfold;
    char playerout;
    boolean plr1fold = false, plr2fold = false, plr3fold = false, plr4fold = false; //checks if player has folded
    boolean plr1out = false, plr2out = false, plr3out = false, plr4out = false;

    String name = "";
    String ipAddress ="";
    int port = 0;
    int startingMoney = 0;

    //public Client(){chatClient = new clientThread();}
    public FiveCardClient(String nme, String ip, int prt) {
        chatClient = new clientThread();
        name = nme;
        ipAddress = ip;
        port = prt;   

        initComponents();
         jlbls[0] = plr1C1Label;
         jlbls[1] = plr1C2Label;
         jlbls[2] = plr1C3Label;
         jlbls[3] = plr1C4Label;
         jlbls[4] = plr1C5Label;

         jlbls[5] = plr2C1Label;
         jlbls[6] = plr2C2Label;
         jlbls[7] = plr2C3Label;
         jlbls[8] = plr2C4Label;
         jlbls[9] = plr2C5Label;

         jlbls[10] = plr3C1Label;
         jlbls[11] = plr3C2Label;
         jlbls[12] = plr3C3Label;
         jlbls[13] = plr3C4Label;
         jlbls[14] = plr3C5Label;

         jlbls[15] = plr4C1Label;
         jlbls[16] = plr4C2Label;
         jlbls[17] = plr4C3Label;
         jlbls[18] = plr4C4Label;
         jlbls[19] = plr4C5Label;

         burnhand.addElement(56);
         burnhand.addElement(56);
         burnhand.addElement(56);
         burnhand.addElement(56);
         burnhand.addElement(56);
         JOptionPane.showMessageDialog(null, name + ipAddress + port);
    }
    void PlayerOut(char out){
    // blanks out player's cards if out of the game 
        if(out == '0'){
            plr1out = true;
            plr1C1Label.setIcon(blank);
            plr1C2Label.setIcon(blank);
            plr1C3Label.setIcon(blank);
            plr1C4Label.setIcon(blank);
            plr1C5Label.setIcon(blank);
        }
        if(out == '1'){
            if(player == 2){
                chatClient.sendMessage("quit");
            JOptionPane.showMessageDialog(null, "You are out of the game", "Player Error", JOptionPane.ERROR_MESSAGE);
        try {
            Thread.sleep(1000);
        } catch (InterruptedException ex) {
            Logger.getLogger(FiveCardClient.class.getName()).log(Level.SEVERE, null, ex);
        }
         this.setVisible(false);
         startScreen = new WelcomeScreen();
         startScreen.setVisible(true);
        }
            plr2out = true;
            plr2C1Label.setIcon(blank);
            plr2C2Label.setIcon(blank);
            plr2C3Label.setIcon(blank);
            plr2C4Label.setIcon(blank);
            plr2C5Label.setIcon(blank);
        }
        if(out == '2'){
            if(player == 3){
                chatClient.sendMessage("quits");
            JOptionPane.showMessageDialog(null, "You are out of the game", "Player Error", JOptionPane.ERROR_MESSAGE);
        try {
            Thread.sleep(1000);
        } catch (InterruptedException ex) {
            Logger.getLogger(FiveCardClient.class.getName()).log(Level.SEVERE, null, ex);
        }
         this.setVisible(false);
         startScreen = new WelcomeScreen();
         startScreen.setVisible(true);
        }
            plr3out = true;
            plr3C1Label.setIcon(blank);
            plr3C2Label.setIcon(blank);
            plr3C3Label.setIcon(blank);
            plr3C4Label.setIcon(blank);
            plr3C5Label.setIcon(blank);
        }
        if(out == '3'){
            if(player == 4){
                chatClient.sendMessage("quitter");
            JOptionPane.showMessageDialog(null, "You are out of the game", "Player Error", JOptionPane.ERROR_MESSAGE);
        try {
            Thread.sleep(1000);
        } catch (InterruptedException ex) {
            Logger.getLogger(FiveCardClient.class.getName()).log(Level.SEVERE, null, ex);
        }
         this.setVisible(false);
         startScreen = new WelcomeScreen();
         startScreen.setVisible(true);
        }
            plr4out = true;
            plr4C1Label.setIcon(blank);
            plr4C2Label.setIcon(blank);
            plr4C3Label.setIcon(blank);
            plr4C4Label.setIcon(blank);
            plr4C5Label.setIcon(blank);
        }
    }
    void fold(int fold){
    // blanks out player's cards and sets bool to true so they don't reset in update cards
        if(fold == 0){
            plr1fold = true;
            plr1C1Label.setIcon(blank);
            plr1C2Label.setIcon(blank);
            plr1C3Label.setIcon(blank);
            plr1C4Label.setIcon(blank);
            plr1C5Label.setIcon(blank);
        }
        if(fold == 1){
            plr2fold = true;
            plr2C1Label.setIcon(blank);
            plr2C2Label.setIcon(blank);
            plr2C3Label.setIcon(blank);
            plr2C4Label.setIcon(blank);
            plr2C5Label.setIcon(blank);
        }
        if(fold == 2){
            plr3fold = true;
            plr3C1Label.setIcon(blank);
            plr3C2Label.setIcon(blank);
            plr3C3Label.setIcon(blank);
            plr3C4Label.setIcon(blank);
            plr3C5Label.setIcon(blank);
        }
        if(fold == 3){
            plr4fold = true;
            plr4C1Label.setIcon(blank);
            plr4C2Label.setIcon(blank);
            plr4C3Label.setIcon(blank);
            plr4C4Label.setIcon(blank);
            plr4C5Label.setIcon(blank);
        }
    }
    void updateRound(int r){
        // updates round label with the round number sent by server
        if(r == 0){ //first round, betting
            jLabel13.setText("Current Round: Betting Round");   //only allows player to bet
            discardButton.setEnabled(false);
            betButton.setEnabled(true);
            checkButton.setEnabled(true);
            //jLabel14.setText("Pot Amount: $0"); //resets pot at beginning of this round
        }
        else if(r == 1){    //second round, trade in
            jLabel13.setText("Current Round: Trade In Round");  //only allows player to discard
            betButton.setEnabled(false);
            checkButton.setEnabled(false);
            discardButton.setEnabled(true);
        }
        else if(r == 2){    //third round, betting
            jLabel13.setText("Current Round: Betting Round");//only allows player to bet
            discardButton.setEnabled(false);
            betButton.setEnabled(true);
            checkButton.setEnabled(true);
        }
        
    }
    void updateWinner(String[] pack){
    //displays all players cards and reenables all buttons
            plr1fold = false;
            plr2fold = false;
            plr3fold = false;
            plr4fold = false;
            betButton.setEnabled(true);            //to enable buttons for next round
            checkButton.setEnabled(true);
            discardButton.setEnabled(true);
            foldButton.setEnabled(true);
        for(int i=1; i < 21; i++){
            Icon card = new ImageIcon(getClass().getResource(packet[i]+".png"));

            jlbls[i-1].setIcon(card);
        }
           // jLabel14.setText("Pot Amount: $0");
    }
    void updatePlayerName(String[] names){
        plr1NameLabel.setText(names[1]);
        plr2NameLabel.setText(names[2]);
        if(playerNum > 2){
        plr3NameLabel.setText(names[3]);
        }
        if(playerNum > 3){
        plr4NameLabel.setText(names[4]);
        }
        plr1MoneyLabel.setText("$ "+names[names.length-1]);
        plr2MoneyLabel.setText("$ "+names[names.length-1]);
        plr3MoneyLabel.setText("$ "+names[names.length-1]);
        plr4MoneyLabel.setText("$ "+names[names.length-1]);
    }
    void updatePlayer(){
    //updates display with who's turn it is and blanks out players not connected
        plrNameLabel.setText(name);
        if(player != 1)
           plr1BetField.setEditable(false);
         if(player != 2)
           plr2BetField.setEditable(false);
         if(player != 3)
           plr3BetField.setEditable(false);
         if(player != 4)
           plr4BetField.setEditable(false);
        if(playerNum == 2){
            plr4NameLabel.setText("NOT CONNECTED");
            plr3NameLabel.setText("NOT CONNECTED");

            plr3C1Label.setIcon(blank);
            plr3C2Label.setIcon(blank);
            plr3C3Label.setIcon(blank);
            plr3C4Label.setIcon(blank);
            plr3C5Label.setIcon(blank);

            plr4C1Label.setIcon(blank);
            plr4C2Label.setIcon(blank);
            plr4C3Label.setIcon(blank);
            plr4C4Label.setIcon(blank);
            plr4C5Label.setIcon(blank);
        }
        else if(playerNum == 3){
            plr4NameLabel.setText("NOT CONNECTED");
            plr4C1Label.setIcon(blank);
            plr4C2Label.setIcon(blank);
            plr4C3Label.setIcon(blank);
            plr4C4Label.setIcon(blank);
            plr4C5Label.setIcon(blank);
        }

       if(turn == 0){
        plr1NameLabel.setForeground(Color.red);
        plr2NameLabel.setForeground(Color.white);
        plr3NameLabel.setForeground(Color.white);
        plr4NameLabel.setForeground(Color.white);
        }
        if(turn == 1){
        plr2NameLabel.setForeground(Color.red);
        plr1NameLabel.setForeground(Color.white);
        plr3NameLabel.setForeground(Color.white);
        plr4NameLabel.setForeground(Color.white);
        
        }
        if(turn == 2){
        plr3NameLabel.setForeground(Color.red);
        plr2NameLabel.setForeground(Color.white);
        plr1NameLabel.setForeground(Color.white);
        plr4NameLabel.setForeground(Color.white);
        }
        if(turn == 3){
            
        plr4NameLabel.setForeground(Color.red);
        plr2NameLabel.setForeground(Color.white);
        plr3NameLabel.setForeground(Color.white);
        plr1NameLabel.setForeground(Color.white);
        }
    }
    void updateBoard(String playerID, String money, String bet, String pot){
    //updates the bet, current money, and pot
        if(playerID.equals(",")){
            //System.out.println("Hell Yeah");
            plr1MoneyLabel.setText("$ "+money);
            plr1BetField.setText(bet); 
        }
        if(playerID.equals("-")){
            plr2MoneyLabel.setText("$ "+money);
            plr2BetField.setText(bet); 
        }
         if(playerID.equals(":")){
            plr3MoneyLabel.setText("$ "+money);
            plr3BetField.setText(bet);
        }
         if(playerID.equals(";")){
            plr4MoneyLabel.setText("$ "+money);
            plr4BetField.setText(bet);
        }
        jLabel14.setText("Pot Amount: $" + pot);
    }
    void updateCards(){
    //displays player cards and the backs of other players cards
        for(int i=1; i < 6; i++){
            Icon card = new ImageIcon(getClass().getResource(packet[i])); //front of the card
            Icon back = new ImageIcon(getClass().getResource("b2fv.png")); //back of the card
            if(plr1fold == false && plr1out == false){  //won't display cards if plr 1 folded
            jlbls[0].setIcon(back);
            jlbls[1].setIcon(back);
            jlbls[2].setIcon(back);
            jlbls[3].setIcon(back);
            jlbls[4].setIcon(back);
            }
                if(player == 2){
                jlbls[i+4].setIcon(card);
                 if(playerNum > 2 && plr3fold == false && plr3out == false){
                jlbls[10].setIcon(back);
                jlbls[11].setIcon(back);
                jlbls[12].setIcon(back);
                jlbls[13].setIcon(back);
                jlbls[14].setIcon(back);
                  }
                if(playerNum > 3 && plr4fold == false && plr4out == false){
                jlbls[15].setIcon(back);
                jlbls[16].setIcon(back);
                jlbls[17].setIcon(back);
                jlbls[18].setIcon(back);
                jlbls[19].setIcon(back);
                    }
                }
            else if(player == 3)
            {
                jlbls[i+9].setIcon(card);
                if(plr2fold == false && plr2out == false){ //won't display cards if plr 2 folded
                jlbls[5].setIcon(back);
                jlbls[6].setIcon(back);
                jlbls[7].setIcon(back);
                jlbls[8].setIcon(back);
                jlbls[9].setIcon(back);
                }
                if(playerNum > 3 && plr4fold == false && plr4out == false){
                jlbls[15].setIcon(back);
                jlbls[16].setIcon(back);
                jlbls[17].setIcon(back);
                jlbls[18].setIcon(back);
                jlbls[19].setIcon(back);
                }
           }
            else if(player == 4)
            {
                jlbls[i+14].setIcon(card);
                if(plr3fold == false && plr3out == false){
                jlbls[10].setIcon(back);
                jlbls[11].setIcon(back);
                jlbls[12].setIcon(back);
                jlbls[13].setIcon(back);
                jlbls[14].setIcon(back);
                }
                if(plr2fold == false && plr2out == false){
                jlbls[5].setIcon(back);
                jlbls[6].setIcon(back);
                jlbls[7].setIcon(back);
                jlbls[8].setIcon(back);
                jlbls[9].setIcon(back);
                }
           }
           else{
                if(plr1fold == false && plr1out == false)
                jlbls[i-1].setIcon(back);
               }
            //System.out.println(i);
            packet[i] = packet[i].replace(".png", ""); //puts .png so it will display corresponding picture
            hand.add(i-1, new Integer(Integer.parseInt(packet[i]))); //puts players cards into hand vector


        }
        //  display cards to console    //
//        System.out.println(packet[0]);
//        System.out.println(packet[1]);
//        System.out.println(packet[2]);
//        System.out.println(packet[3]);
//        System.out.println(packet[4]);
//        System.out.println(packet[5]);

        }
    
    public class clientThread extends Thread {

        public LinkedList clients;
        public ByteBuffer read;
        public ByteBuffer write;
        public SocketChannel sChan;
        public Selector readSelector;
        public CharsetDecoder asciiDecoder;
               

        public clientThread() {
            clients = new LinkedList();
            read = ByteBuffer.allocateDirect(300);
            write = ByteBuffer.allocateDirect(300);
            asciiDecoder = Charset.forName("US-ASCII").newDecoder();
        }

        public void run() {
           serverName = ipAddress;
            connect(serverName);
            myRead.start();
            while (true) {
                readMessage();

                try {
                    Thread.sleep(30);
                } catch (InterruptedException ex) {
                  }
            }
        }

        public void connect(String host) {
            try {
                readSelector = Selector.open();
                InetAddress addr = InetAddress.getByName(host);
                sChan = SocketChannel.open(new InetSocketAddress(addr, port));
                sChan.configureBlocking(false);
                sChan.register(readSelector, SelectionKey.OP_READ, new StringBuffer());
            } catch (Exception ex) {
            }
        }
        public void sendMessage(String msg){
            prepareBuffer(msg);
            //chatWindow.append(userName + " says: " + msg + "\n");
            channelWrite(sChan);

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
                    num += sChan.write(write);
                    Thread.sleep(5);
                } catch (IOException ex) {
                    JOptionPane.showMessageDialog(null, "Error: " + ex.toString());
                } catch (InterruptedException ex) {
                }
            }
            write.rewind();
        }

        public void readMessage() {
            try {
                //read.clear();
                readSelector.selectNow();
                Set readyKeys = readSelector.selectedKeys();
                Iterator iterator = readyKeys.iterator();

                while (iterator.hasNext()) {
                    SelectionKey key = (SelectionKey) iterator.next();
                    iterator.remove();
                    SocketChannel channel = (SocketChannel) key.channel();

                    long nbytes = channel.read(read);

                    if (nbytes == -1) {
                        chatWindow.append("you logged out. \n");
                        channel.close();
                    } else {
                       // StringBuffer sb = (StringBuffer) key.attachment();
                        read.flip();
                        String str = asciiDecoder.decode(read).toString();
                       // sb.append(str);
                        read.clear();

                        String line = str;
                        
                        if ((line.indexOf("\n") != -1) || (line.indexOf("\r") != -1)) {

                            line = line.trim();
                       //    System.out.println("line: "+line);
                           char c = line.charAt(0); //string delimiter
                           
                       //    System.out.println("c: "+c);
                            if(c >= 48 && c <= 58 && (player != 2 && player != 3 && player != 4)){
                            player = Integer.parseInt(line);
                            chatClient.sendMessage(">"+name);
                            }
                            else if(c == '.'){        // . is the player and turn delimiter
                                String temp = Character.toString(line.charAt(1));
                                turn = Integer.parseInt(temp);
                                updatePlayer();
                            }else if(c == '>'){
                            packet = line.split(">");
                            updatePlayerName(packet);

                        }else if(c == '$'){                   // $ is the bet delimiter

                             if(player == 2){
                              String betpack[] = line.split("-");
                        
                             if(betpack[1].equals("false")){
                             JOptionPane.showMessageDialog(null, "Did not bet enough money or cannot check at this time.", "Player Error", JOptionPane.ERROR_MESSAGE);
                             }
                             }
                             if(player == 3){
                             String betpack[] = line.split(":");
                          
                            if(betpack[1].equals("false")){
                             JOptionPane.showMessageDialog(null, "Did not bet enough money or cannot check at this time.", "Player Error", JOptionPane.ERROR_MESSAGE);
                             }
                             }
                             if(player == 4){
                             String betpack[] = line.split(";");
                           
                            if(betpack[1].equals("false")){
                             JOptionPane.showMessageDialog(null, "Did not bet enough money or cannot check at this time.", "Player Error", JOptionPane.ERROR_MESSAGE);
                            }
                             }
                         }
                            else if(c == 'r')
                            {           //r is the round delimiter
                            round = line.charAt(1) - 48;
                            updateRound(round);
                        }
                            else if(c == 'f')
                            {           // f is the fold delimiter
                            playerfold = line.charAt(1) - 48;
                            fold(playerfold);
                        }
                       
                            else if(c == '%'){           // % is the update board delimiter
                            char p = line.charAt(1);
                            String pl;       //player delimiter
                            String mo;          //players current money
                            String be;          //players bet amount
                            String po;
                            if(p == ','){       // , is the player 1 delimiter
                            String betpack[] = line.split(",");
                            betpack[0]= ",";
                          
                            pl = betpack[0];
                            mo = betpack[1];
                            be = betpack[2];
                            po = betpack[3];
                            updateBoard(pl, mo, be, po);
                            }
                            if(p == '-'){
                            String betpack[] = line.split("-");
                            betpack[0]= "-";
                           
                            pl = betpack[0];
                            mo = betpack[1];
                            be = betpack[2];
                            po = betpack[3];
                            updateBoard(pl, mo, be, po);
                            }
                            else if (p == ':') {
                            String betpack[] = line.split(":");
                            betpack[0]= ":";
                            pl = betpack[0];
                            mo = betpack[1];
                            be = betpack[2];
                            po = betpack[3];
                            updateBoard(pl, mo, be, po);
                        }
                            else if(p == ';'){
                            String betpack[] = line.split(";");
                            betpack[0]= ";";
                            pl = betpack[0];
                            mo = betpack[1];
                            be = betpack[2];
                            po = betpack[3];
                            updateBoard(pl, mo, be, po);
                          }
                            }else if(c == 'o'){     // o is the player out delimiter
                                playerout = line.charAt(1);
                                PlayerOut(playerout);
                            }else if(c == 'p'){    // p is the number of players delimiter
                            numOfPlr = line.charAt(1);
                            playerNum = numOfPlr - 48;
                            updatePlayer();
                        }else if(c == '~'){  //~ is the show all cards delimiter
                            packet = line.split("~");
                            plr1fold = false;
                            updateWinner(packet);
                        }else if (c == '<') { // < is the final winner delimiter
                                String[] winMessage = line.split("<");
                                //JOptionPane.showMessageDialog(null, winMessage);
                                final JOptionPane pane = new JOptionPane(winMessage);
                                pane.setMessageType(JOptionPane.INFORMATION_MESSAGE);
                                pane.setOptions(new Object[]{});
                                final JDialog dialog = pane.createDialog("Winner");

                                final Timer timer = new Timer(5000, new ActionListener() {
                                    //timer displays dialog box for 5 seconds

                                    public void actionPerformed(final ActionEvent e) {
                                        dialog.setVisible(false);
                                    }
                                });
                                timer.setRepeats(false);
                                timer.start();
                                dialog.setVisible(true);
                                dialog.dispose();
                                PlayerOut('0');
                                PlayerOut('1');
                                PlayerOut('2');
                                PlayerOut('3');

                            }
                         else if(c == '@'){ // w is the winner delimiter
                             String[] winMessage = line.split("@");
                            //JOptionPane.showMessageDialog(null, winMessage);
                            final JOptionPane pane = new JOptionPane(winMessage);
                            pane.setMessageType(JOptionPane.INFORMATION_MESSAGE);
                            pane.setOptions(new Object[] {} );
                            final JDialog dialog = pane.createDialog("Winner");

                            final Timer timer = new Timer(5000, new ActionListener() {      //timer displays dialog box for 5 seconds
        
                            public void actionPerformed(final ActionEvent e) {
                            dialog.setVisible(false);
                            }
                            });
                                timer.setRepeats(false);
                                timer.start();
                                dialog.setVisible(true);
                                dialog.dispose();
        
                            }
                            if(player == 2){
                             packet = line.split("-");  // - is the player 2 delimiter
                             updateCards();
                             updatePlayer();
                            }
                            if(player == 3){
                                packet = line.split(":"); // : is the player 3 delimiter
                                updateCards();
                                updatePlayer();
                            }
                             if(player == 4){
                                packet = line.split(";");  // ; is the player 4 delimiter
                                updateCards();
                                updatePlayer();

                            }
                          
//                            chatWindow.append(">" + line);
//                            chatWindow.append("" + '\n');

//                            String bs[] = line.split(",");
//                           turn = Integer.parseInt(bs[0]);
                           
                           // sb.delete(0, sb.length());
                          
                        }
                        line = "";

                    }

                }
            }catch(IOException ex){
                
            }catch(Exception e){
                
            }
        }


    }//End of Client Thread
    
    public class readThread extends Thread {
        public void run(){
            chatClient.readMessage();
        }
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jScrollPane1 = new javax.swing.JScrollPane();
        chatWindow = new javax.swing.JTextArea();
        userText = new javax.swing.JTextField();
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
        jLabel12 = new javax.swing.JLabel();
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
        jLabel11 = new javax.swing.JLabel();
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
        jLabel9 = new javax.swing.JLabel();
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
        jLabel10 = new javax.swing.JLabel();
        blackJackToolbar = new javax.swing.JToolBar();
        plrNameLabel = new javax.swing.JLabel();
        betButton = new javax.swing.JButton();
        foldButton = new javax.swing.JButton();
        checkButton = new javax.swing.JButton();
        discardButton = new javax.swing.JButton();
        jSeparator1 = new javax.swing.JToolBar.Separator();
        jPanel1 = new javax.swing.JPanel();
        jLabel13 = new javax.swing.JLabel();
        jLabel14 = new javax.swing.JLabel();

        chatWindow.setColumns(20);
        chatWindow.setRows(5);
        jScrollPane1.setViewportView(chatWindow);

        setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);
        setTitle("Client");
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                formWindowClosing(evt);
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

        plr2NameLabel.setFont(new java.awt.Font("Rockwell", 0, 30));
        plr2NameLabel.setForeground(new java.awt.Color(255, 255, 255));
        plr2NameLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        plr2NameLabel.setText("PLAYER #2");
        player2OuterPanel.add(plr2NameLabel, java.awt.BorderLayout.PAGE_START);

        player2CardPanel.setBackground(new java.awt.Color(0, 51, 0));

        plr2C1Label.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(255, 255, 255), 1, true));
        plr2C1Label.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                plr2C1LabelMouseClicked(evt);
            }
        });

        plr2C2Label.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(255, 255, 255), 1, true));
        plr2C2Label.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                plr2C2LabelMouseClicked(evt);
            }
        });

        plr2C3Label.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(255, 255, 255), 1, true));
        plr2C3Label.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                plr2C3LabelMouseClicked(evt);
            }
        });

        plr2C4Label.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(255, 255, 255), 1, true));
        plr2C4Label.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                plr2C4LabelMouseClicked(evt);
            }
        });

        plr2C5Label.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(255, 255, 255), 1, true));
        plr2C5Label.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                plr2C5LabelMouseClicked(evt);
            }
        });

        jLabel1.setFont(new java.awt.Font("Tahoma", 0, 14));
        jLabel1.setForeground(new java.awt.Color(255, 255, 255));
        jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel1.setText("Current Funds:");

        plr2MoneyLabel.setFont(new java.awt.Font("Rockwell", 0, 24));
        plr2MoneyLabel.setForeground(new java.awt.Color(255, 255, 255));
        plr2MoneyLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        plr2MoneyLabel.setText("1000");

        jLabel3.setFont(new java.awt.Font("Tahoma", 0, 14));
        jLabel3.setForeground(new java.awt.Color(255, 255, 255));
        jLabel3.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel3.setText("Bet Amount:");

        plr2BetField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                plr2BetFieldActionPerformed(evt);
            }
        });

        jLabel12.setForeground(new java.awt.Color(255, 255, 255));
        jLabel12.setText("$");

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
                                        .addGap(6, 6, 6)
                                        .addComponent(jLabel12)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(plr2BetField, javax.swing.GroupLayout.PREFERRED_SIZE, 105, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 124, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGap(36, 36, 36)))))
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
                        .addComponent(jLabel12)
                        .addComponent(plr2BetField, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)))
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

        plr1C2Label.setForeground(new java.awt.Color(255, 255, 255));
        plr1C2Label.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(255, 255, 255), 1, true));

        plr1C3Label.setForeground(new java.awt.Color(255, 255, 255));
        plr1C3Label.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(255, 255, 255), 1, true));

        plr1C4Label.setForeground(new java.awt.Color(255, 255, 255));
        plr1C4Label.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(255, 255, 255), 1, true));

        plr1C5Label.setForeground(new java.awt.Color(255, 255, 255));
        plr1C5Label.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(255, 255, 255), 1, true));

        jLabel2.setFont(new java.awt.Font("Tahoma", 0, 14));
        jLabel2.setForeground(new java.awt.Color(255, 255, 255));
        jLabel2.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel2.setText("Current Funds:");

        plr1MoneyLabel.setFont(new java.awt.Font("Rockwell", 0, 24));
        plr1MoneyLabel.setForeground(new java.awt.Color(255, 255, 255));
        plr1MoneyLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        plr1MoneyLabel.setText("1000");

        jLabel4.setFont(new java.awt.Font("Tahoma", 0, 14));
        jLabel4.setForeground(new java.awt.Color(255, 255, 255));
        jLabel4.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel4.setText("Bet Amount:");

        jLabel11.setForeground(new java.awt.Color(255, 255, 255));
        jLabel11.setText("$");

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
                                        .addComponent(jLabel11)
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
                        .addComponent(jLabel11)))
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
        plr3C1Label.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                plr3C1LabelMouseClicked(evt);
            }
        });

        plr3C2Label.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(255, 255, 255), 1, true));
        plr3C2Label.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                plr3C2LabelMouseClicked(evt);
            }
        });

        plr3C3Label.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(255, 255, 255), 1, true));
        plr3C3Label.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                plr3C3LabelMouseClicked(evt);
            }
        });

        plr3C4Label.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(255, 255, 255), 1, true));
        plr3C4Label.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                plr3C4LabelMouseClicked(evt);
            }
        });

        plr3C5Label.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(255, 255, 255), 1, true));
        plr3C5Label.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                plr3C5LabelMouseClicked(evt);
            }
        });

        jLabel5.setFont(new java.awt.Font("Tahoma", 0, 14));
        jLabel5.setForeground(new java.awt.Color(255, 255, 255));
        jLabel5.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel5.setText("Current Funds:");

        plr3MoneyLabel.setFont(new java.awt.Font("Rockwell", 0, 24));
        plr3MoneyLabel.setForeground(new java.awt.Color(255, 255, 255));
        plr3MoneyLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        plr3MoneyLabel.setText("1000");

        jLabel6.setFont(new java.awt.Font("Tahoma", 0, 14));
        jLabel6.setForeground(new java.awt.Color(255, 255, 255));
        jLabel6.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel6.setText("Bet Amount:");

        plr3BetField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                plr3BetFieldActionPerformed(evt);
            }
        });

        jLabel9.setForeground(new java.awt.Color(255, 255, 255));
        jLabel9.setText("$");

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
                                        .addComponent(jLabel9)
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
                        .addComponent(jLabel9)))
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
        plr4C1Label.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                plr4C1LabelMouseClicked(evt);
            }
        });

        plr4C2Label.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(255, 255, 255), 1, true));
        plr4C2Label.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                plr4C2LabelMouseClicked(evt);
            }
        });

        plr4C3Label.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(255, 255, 255), 1, true));
        plr4C3Label.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                plr4C3LabelMouseClicked(evt);
            }
        });

        plr4C4Label.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(255, 255, 255), 1, true));
        plr4C4Label.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                plr4C4LabelMouseClicked(evt);
            }
        });

        plr4C5Label.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(255, 255, 255), 1, true));
        plr4C5Label.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                plr4C5LabelMouseClicked(evt);
            }
        });

        jLabel7.setFont(new java.awt.Font("Tahoma", 0, 14));
        jLabel7.setForeground(new java.awt.Color(255, 255, 255));
        jLabel7.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel7.setText("Current Funds:");

        plr4MoneyLabel.setFont(new java.awt.Font("Rockwell", 0, 24));
        plr4MoneyLabel.setForeground(new java.awt.Color(255, 255, 255));
        plr4MoneyLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        plr4MoneyLabel.setText("1000");

        jLabel8.setFont(new java.awt.Font("Tahoma", 0, 14));
        jLabel8.setForeground(new java.awt.Color(255, 255, 255));
        jLabel8.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel8.setText("Bet Amount:");

        jLabel10.setForeground(new java.awt.Color(255, 255, 255));
        jLabel10.setText("$");

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
                                        .addComponent(jLabel10)
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
                        .addComponent(jLabel10)))
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

        jPanel1.setMaximumSize(new java.awt.Dimension(900, 25));

        jLabel13.setText("Current Round: ");

        jLabel14.setText("Pot Amount: ");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addGap(373, 373, 373)
                .addComponent(jLabel13, javax.swing.GroupLayout.DEFAULT_SIZE, 372, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel14, javax.swing.GroupLayout.PREFERRED_SIZE, 139, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                .addComponent(jLabel13, javax.swing.GroupLayout.DEFAULT_SIZE, 25, Short.MAX_VALUE)
                .addComponent(jLabel14, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        blackJackToolbar.add(jPanel1);

        getContentPane().add(blackJackToolbar, java.awt.BorderLayout.SOUTH);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void backGroundPanelFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_backGroundPanelFocusGained

}//GEN-LAST:event_backGroundPanelFocusGained

    private void betButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_betButtonActionPerformed
        if(turn+1 != player){
            JOptionPane.showMessageDialog(null, "It is not your turn!", "Player Error", JOptionPane.ERROR_MESSAGE);
       }else{
            if(player == 2){
            int n = JOptionPane.showConfirmDialog(null, "Are you sure you want to bet this amount: $"+plr2BetField.getText());
                if(n == 0){
                    
                    if(plr2BetField.getText().isEmpty())      //if field is null send zero
                        chatClient.sendMessage("$;"+"0");
                    else{
                        playerbet = Integer.parseInt(plr2BetField.getText());
                chatClient.sendMessage("$-"+plr2BetField.getText());
              
                    }
                }
            }
            if(player == 3){
            int n = JOptionPane.showConfirmDialog(null, "Are you sure you want to bet this amount: $"+plr3BetField.getText());
                if(n == 0){
                    playerbet = Integer.parseInt(plr3BetField.getText());
                    if(plr3BetField.getText().isEmpty())       //if field is null send zero
                        chatClient.sendMessage("$;"+"0");
                    else{
                        playerbet = Integer.parseInt(plr3BetField.getText());
                chatClient.sendMessage("$-"+plr3BetField.getText());
            
                    }
                }
            }
            if(player == 4){
            int n = JOptionPane.showConfirmDialog(null, "Are you sure you want to bet this amount: $"+plr4BetField.getText());
                if(n == 0){
                    playerbet = Integer.parseInt(plr4BetField.getText());
                    if(plr4BetField.getText().isEmpty())       //if field is null send zero
                        chatClient.sendMessage("$;"+"0");
                     else{
                        playerbet = Integer.parseInt(plr4BetField.getText());
                chatClient.sendMessage("$-"+plr4BetField.getText());
        
                    }
                }
            }
        }
    }//GEN-LAST:event_betButtonActionPerformed

    private void foldButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_foldButtonActionPerformed
        if(turn+1 != player){
            JOptionPane.showMessageDialog(null, "It is not your turn!", "Player Error", JOptionPane.ERROR_MESSAGE);
        }
        else {
            int n = JOptionPane.showConfirmDialog(null, "Are you sure you want to fold? ");
            if(n == 0){
                chatClient.sendMessage("^"+(player-1));
                betButton.setEnabled(false);            //to prevent user from clicking on buttons
                checkButton.setEnabled(false);
                discardButton.setEnabled(false);
                foldButton.setEnabled(false);
            }
        }
    }//GEN-LAST:event_foldButtonActionPerformed

    private void checkButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_checkButtonActionPerformed
       if(turn+1 != player){
            JOptionPane.showMessageDialog(null, "It is not your turn!", "Player Error", JOptionPane.ERROR_MESSAGE);
        }
        else {
            if(player == 2){
            int n = JOptionPane.showConfirmDialog(null, "Are you sure you want to check? ");
                if(n == 0){
                    playerbet = 0;
                chatClient.sendMessage("$-"+playerbet);
                }
            }
            if(player == 3){
            int n = JOptionPane.showConfirmDialog(null, "Are you sure you want to check? ");
                if(n == 0){
                    playerbet = 0;
                chatClient.sendMessage("$:"+playerbet);
                }
            }
            if(player == 4){
            int n = JOptionPane.showConfirmDialog(null, "Are you sure you want to check? ");
                if(n == 0){
                    playerbet = 0;
                chatClient.sendMessage("$;"+playerbet);
                }
            }
        }


    }//GEN-LAST:event_checkButtonActionPerformed
    int discardCounter = 0; //keeps track of how many times discard is clicked per round
    private void discardButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_discardButtonActionPerformed
        
        ++discardCounter;
        if(turn+1 != player){
            JOptionPane.showMessageDialog(null, "It is not your turn!", "Player Error", JOptionPane.ERROR_MESSAGE);
            discardCounter = 0;
        }
        else if(flipcount1 > 3){
            JOptionPane.showMessageDialog(null, "Can only discard three cards!", "Player Error", JOptionPane.ERROR_MESSAGE);
            discardCounter = 0;
            }
        else if(discardCounter >= 2){
            JOptionPane.showMessageDialog(null, "Already discarded!", "Player Error", JOptionPane.ERROR_MESSAGE);
        }
         else{
            String temp = "";
            String cardPlayer2 = "-";
            String cardPlayer3 = ":";
            String cardPlayer4 = ";";
            for(int i = 0; i < burnhand.size(); i++){
                if(player == 2){
                 cardPlayer2 += (Integer)burnhand.elementAt(i)+"-";
                 temp = cardPlayer2;
                }
                if(player == 3){
                 cardPlayer3 += (Integer)burnhand.elementAt(i)+":";
                 temp = cardPlayer3;
                }
                if(player == 4){
                 cardPlayer4 += (Integer)burnhand.elementAt(i)+";";
                 temp = cardPlayer4;
                }
            }
            chatClient.sendMessage(temp);
            burnhand.clear();
            burnhand.addElement(56);
            burnhand.addElement(56);
            burnhand.addElement(56);
            burnhand.addElement(56);
            burnhand.addElement(56);
            flipcount1 = 0;
            discardCounter = 0;
        }
    }//GEN-LAST:event_discardButtonActionPerformed
// each counter keeps track of how many times user clicks on the card
public int counter1 = 0;
public int counter2 = 0;
public int counter3 = 0;
public int counter4 = 0;
public int counter5 = 0;
public int flipcount1 = 0;  //keeps track of how many cards are flipped over


    private void plr2C1LabelMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_plr2C1LabelMouseClicked
       if(player == 2){
        ++counter1;
        
     
        if(turn+1 != player){
            JOptionPane.showMessageDialog(null, "It is not your turn!", "Player Error", JOptionPane.ERROR_MESSAGE);
        }
    
 else if (counter1 == 2) {  //indicates card is flipped face up so must be removed from the burn hand
             --flipcount1;
    
        for(int i = 0; i<5; i++){   //looks for specific card in burn hand
            if(burnhand.elementAt(i)==hand.elementAt(0)){
       
               burnhand.removeElementAt(i);
             String temp;
             temp = (Integer)hand.elementAt(0)+".png";
             Icon main = new ImageIcon(getClass().getResource(temp));
                plr2C1Label.setIcon(main);
                break;
            }
        }
            counter1 = 0;
         }
         else{
            ++flipcount1;
                Icon back = new ImageIcon(getClass().getResource("b2fv.png"));
                plr2C1Label.setIcon(back);
            burnhand.add(0, hand.elementAt(0));
   
        }
        }
    }//GEN-LAST:event_plr2C1LabelMouseClicked

    private void plr2C2LabelMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_plr2C2LabelMouseClicked
        if(player == 2){
        ++counter2;
        if(turn+1 != player){
            JOptionPane.showMessageDialog(null, "It is not your turn!", "Player Error", JOptionPane.ERROR_MESSAGE);
        }
   
 else if (counter2 == 2) {
             --flipcount1;
        for(int i = 0; i<5; i++){
            if(burnhand.elementAt(i)==hand.elementAt(1)){
               burnhand.removeElementAt(i);
             String temp;
             temp = (Integer)hand.elementAt(1)+".png";
             Icon main = new ImageIcon(getClass().getResource(temp));
                plr2C2Label.setIcon(main);
                break;
            }
        }
            counter2 = 0;
         }
         else{
            ++flipcount1;
                Icon back = new ImageIcon(getClass().getResource("b2fv.png"));
                plr2C2Label.setIcon(back);
            burnhand.add(1, hand.elementAt(1));
        }
        }
    }//GEN-LAST:event_plr2C2LabelMouseClicked

    private void plr2C3LabelMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_plr2C3LabelMouseClicked
       if(player == 2){
        ++counter3;
        if(turn+1 != player){
            JOptionPane.showMessageDialog(null, "It is not your turn!", "Player Error", JOptionPane.ERROR_MESSAGE);
        }
    
 else if (counter3 == 2) {
             --flipcount1;
        for(int i = 0; i<5; i++){
            if(burnhand.elementAt(i)==hand.elementAt(2)){
               burnhand.removeElementAt(i);
             String temp;
             temp = (Integer)hand.elementAt(2)+".png";
             Icon main = new ImageIcon(getClass().getResource(temp));
                plr2C3Label.setIcon(main);
                break;
            }
        }
            counter3 = 0;
         }
         else{
             ++flipcount1;
                Icon back = new ImageIcon(getClass().getResource("b2fv.png"));
                plr2C3Label.setIcon(back);
            burnhand.add(2, hand.elementAt(2));
        }
        }
    }//GEN-LAST:event_plr2C3LabelMouseClicked

    private void plr2C4LabelMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_plr2C4LabelMouseClicked
       if(player == 2){
        ++counter4;
        if(turn+1 != player){
            JOptionPane.showMessageDialog(null, "It is not your turn!", "Player Error", JOptionPane.ERROR_MESSAGE);
        }
        else if (counter4 == 2) {
             --flipcount1;
        for(int i = 0; i<5; i++){
            if(burnhand.elementAt(i)==hand.elementAt(3)){
               burnhand.removeElementAt(i);
             String temp;
             temp = (Integer)hand.elementAt(3)+".png";
             Icon main = new ImageIcon(getClass().getResource(temp));
                plr2C4Label.setIcon(main);
                break;
            }
        }
            counter4 = 0;
         }
         else{
            ++flipcount1;
                Icon back = new ImageIcon(getClass().getResource("b2fv.png"));
                plr2C4Label.setIcon(back);
            burnhand.add(3, hand.elementAt(3));
        }
        }
    }//GEN-LAST:event_plr2C4LabelMouseClicked

    private void plr2C5LabelMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_plr2C5LabelMouseClicked
       if(player == 2){
        ++counter5;
        if(turn+1 != player){
            JOptionPane.showMessageDialog(null, "It is not your turn!", "Player Error", JOptionPane.ERROR_MESSAGE);
        }
 
 else if (counter5 == 2) {
             --flipcount1;
        for(int i = 0; i<5; i++){
            if(burnhand.elementAt(i)==hand.elementAt(4)){
               burnhand.removeElementAt(i);
             String temp;
             temp = (Integer)hand.elementAt(4)+".png";
             Icon main = new ImageIcon(getClass().getResource(temp));
                plr2C5Label.setIcon(main);
                break;
            }
        }
            counter5 = 0;
         }
         else{
            ++flipcount1;
                Icon back = new ImageIcon(getClass().getResource("b2fv.png"));
                plr2C5Label.setIcon(back);
            burnhand.add(4, hand.elementAt(4));
        }
        }
    }//GEN-LAST:event_plr2C5LabelMouseClicked

    private void plr3C5LabelMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_plr3C5LabelMouseClicked
       if(player == 3){
        ++counter5;
        if(turn+1 != player){
            JOptionPane.showMessageDialog(null, "It is not your turn!", "Player Error", JOptionPane.ERROR_MESSAGE);
        }

 else if (counter5 == 2) {
             --flipcount1;
        for(int i = 0; i<5; i++){
            if(burnhand.elementAt(i)==hand.elementAt(4)){
               burnhand.removeElementAt(i);
             String temp;
             temp = (Integer)hand.elementAt(4)+".png";
             Icon main = new ImageIcon(getClass().getResource(temp));
                plr3C5Label.setIcon(main);
                break;
            }
        }
            counter5 = 0;
         }
         else{
            ++flipcount1;
                Icon back = new ImageIcon(getClass().getResource("b2fv.png"));
                plr3C5Label.setIcon(back);
            burnhand.add(4, hand.elementAt(4));
        }
        }
    }//GEN-LAST:event_plr3C5LabelMouseClicked

    private void plr3C4LabelMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_plr3C4LabelMouseClicked
        if(player == 3){
        ++counter4;
        if(turn+1 != player){
            JOptionPane.showMessageDialog(null, "It is not your turn!", "Player Error", JOptionPane.ERROR_MESSAGE);
        }

 else if (counter4 == 2) {
             --flipcount1;
        for(int i = 0; i<5; i++){
            if(burnhand.elementAt(i)==hand.elementAt(3)){
               burnhand.removeElementAt(i);
             String temp;
             temp = (Integer)hand.elementAt(3)+".png";
             Icon main = new ImageIcon(getClass().getResource(temp));
                plr3C4Label.setIcon(main);
                break;
            }
        }
            counter4 = 0;
         }
         else{
            ++flipcount1;
                Icon back = new ImageIcon(getClass().getResource("b2fv.png"));
                plr3C4Label.setIcon(back);
            burnhand.add(3, hand.elementAt(3));
        }
        }

    }//GEN-LAST:event_plr3C4LabelMouseClicked

    private void plr3C3LabelMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_plr3C3LabelMouseClicked
        if(player == 3){
        ++counter3;
        if(turn+1 != player){
            JOptionPane.showMessageDialog(null, "It is not your turn!", "Player Error", JOptionPane.ERROR_MESSAGE);
        }

 else if (counter3 == 2) {
             --flipcount1;
        for(int i = 0; i<5; i++){
            if(burnhand.elementAt(i)==hand.elementAt(2)){
               burnhand.removeElementAt(i);
             String temp;
             temp = (Integer)hand.elementAt(2)+".png";
             Icon main = new ImageIcon(getClass().getResource(temp));
                plr3C3Label.setIcon(main);
                break;
            }
        }
            counter3 = 0;
         }
         else{
             ++flipcount1;
                Icon back = new ImageIcon(getClass().getResource("b2fv.png"));
                plr3C3Label.setIcon(back);
            burnhand.add(2, hand.elementAt(2));
        }
        }
    }//GEN-LAST:event_plr3C3LabelMouseClicked

    private void plr3C2LabelMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_plr3C2LabelMouseClicked
        if(player == 3){
        ++counter2;
        if(turn+1 != player){
            JOptionPane.showMessageDialog(null, "It is not your turn!", "Player Error", JOptionPane.ERROR_MESSAGE);
        }

 else if (counter2 == 2) {
             --flipcount1;
        for(int i = 0; i<5; i++){
            if(burnhand.elementAt(i)==hand.elementAt(1)){
               burnhand.removeElementAt(i);
             String temp;
             temp = (Integer)hand.elementAt(1)+".png";
             Icon main = new ImageIcon(getClass().getResource(temp));
                plr3C2Label.setIcon(main);
                break;
            }
        }
            counter2 = 0;
         }
         else{
            ++flipcount1;
                Icon back = new ImageIcon(getClass().getResource("b2fv.png"));
                plr3C2Label.setIcon(back);
            burnhand.add(1, hand.elementAt(1));

        }
        }

    }//GEN-LAST:event_plr3C2LabelMouseClicked

    private void plr3C1LabelMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_plr3C1LabelMouseClicked
         if(player == 3){
        ++counter1;
        if(turn+1 != player){
            JOptionPane.showMessageDialog(null, "It is not your turn!", "Player Error", JOptionPane.ERROR_MESSAGE);
        }

 else if (counter1 == 2) {
             --flipcount1;
        for(int i = 0; i<5; i++){
            if(burnhand.elementAt(i)==hand.elementAt(0)){
               burnhand.removeElementAt(i);
             String temp;
             temp = (Integer)hand.elementAt(0)+".png";
             Icon main = new ImageIcon(getClass().getResource(temp));
                plr3C1Label.setIcon(main);
                break;
            }
        }
            counter1 = 0;
         }
         else{
            ++flipcount1;
                Icon back = new ImageIcon(getClass().getResource("b2fv.png"));
                plr3C1Label.setIcon(back);
            burnhand.add(0, hand.elementAt(0));
        }
        }
    }//GEN-LAST:event_plr3C1LabelMouseClicked

    private void plr4C1LabelMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_plr4C1LabelMouseClicked
         if(player == 4){
        ++counter1;
        if(turn+1 != player){
            JOptionPane.showMessageDialog(null, "It is not your turn!", "Player Error", JOptionPane.ERROR_MESSAGE);
        }

 else if (counter1 == 2) {
             --flipcount1;
        for(int i = 0; i<5; i++){
            if(burnhand.elementAt(i)==hand.elementAt(0)){
               burnhand.removeElementAt(i);
             String temp;
             temp = (Integer)hand.elementAt(0)+".png";
             Icon main = new ImageIcon(getClass().getResource(temp));
                plr4C1Label.setIcon(main);
                break;
            }
        }
            counter1 = 0;
         }
         else{
            ++flipcount1;
                Icon back = new ImageIcon(getClass().getResource("b2fv.png"));
                plr4C1Label.setIcon(back);
            burnhand.add(0, hand.elementAt(0));
        }
        }
    }//GEN-LAST:event_plr4C1LabelMouseClicked

    private void plr4C3LabelMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_plr4C3LabelMouseClicked
        if(player == 4){
        ++counter3;
        if(turn+1 != player){
            JOptionPane.showMessageDialog(null, "It is not your turn!", "Player Error", JOptionPane.ERROR_MESSAGE);
        }

 else if (counter3 == 2) {
             --flipcount1;
        for(int i = 0; i<5; i++){
            if(burnhand.elementAt(i)==hand.elementAt(2)){
               burnhand.removeElementAt(i);
             String temp;
             temp = (Integer)hand.elementAt(2)+".png";
             Icon main = new ImageIcon(getClass().getResource(temp));
                plr4C3Label.setIcon(main);
                break;
            }
        }
            counter3 = 0;
         }
         else{
             ++flipcount1;
                Icon back = new ImageIcon(getClass().getResource("b2fv.png"));
                plr4C3Label.setIcon(back);
            burnhand.add(2, hand.elementAt(2));
        }
        }
    }//GEN-LAST:event_plr4C3LabelMouseClicked

    private void plr4C2LabelMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_plr4C2LabelMouseClicked
        if(player == 4){
        ++counter2;
        if(turn+1 != player){
            JOptionPane.showMessageDialog(null, "It is not your turn!", "Player Error", JOptionPane.ERROR_MESSAGE);
        }

 else if (counter2 == 2) {
             --flipcount1;
        for(int i = 0; i<5; i++){
            if(burnhand.elementAt(i)==hand.elementAt(1)){
               burnhand.removeElementAt(i);
             String temp;
             temp = (Integer)hand.elementAt(1)+".png";
             Icon main = new ImageIcon(getClass().getResource(temp));
                plr4C2Label.setIcon(main);
                break;
            }
        }
            counter2 = 0;
         }
         else{
            ++flipcount1;
                Icon back = new ImageIcon(getClass().getResource("b2fv.png"));
                plr4C2Label.setIcon(back);
            burnhand.add(1, hand.elementAt(1));
        }
        }
    }//GEN-LAST:event_plr4C2LabelMouseClicked

    private void plr4C4LabelMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_plr4C4LabelMouseClicked
        if(player == 4){
        ++counter4;
        if(turn+1 != player){
            JOptionPane.showMessageDialog(null, "It is not your turn!", "Player Error", JOptionPane.ERROR_MESSAGE);
        }

 else if (counter4 == 2) {
             --flipcount1;
        for(int i = 0; i<5; i++){
            if(burnhand.elementAt(i)==hand.elementAt(3)){
               burnhand.removeElementAt(i);
             String temp;
             temp = (Integer)hand.elementAt(3)+".png";
             Icon main = new ImageIcon(getClass().getResource(temp));
                plr4C4Label.setIcon(main);
                break;
            }
        }
            counter4 = 0;
         }
         else{
            ++flipcount1;
                Icon back = new ImageIcon(getClass().getResource("b2fv.png"));
                plr4C4Label.setIcon(back);
            burnhand.add(3, hand.elementAt(3));
        }
        }
    }//GEN-LAST:event_plr4C4LabelMouseClicked

    private void plr4C5LabelMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_plr4C5LabelMouseClicked
        if(player == 4){
        ++counter5;

        //System.out.println("click count5: "+counter5);
        if(turn+1 != player){
            JOptionPane.showMessageDialog(null, "It is not your turn!", "Player Error", JOptionPane.ERROR_MESSAGE);
        }

 else if (counter5 == 2) {
             --flipcount1;
        for(int i = 0; i<5; i++){
            if(burnhand.elementAt(i)==hand.elementAt(4)){
               burnhand.removeElementAt(i);
             String temp;
             temp = (Integer)hand.elementAt(4)+".png";
             Icon main = new ImageIcon(getClass().getResource(temp));
                plr4C5Label.setIcon(main);
                break;
            }
        }
            counter5 = 0;
         }
         else{
            ++flipcount1;
                Icon back = new ImageIcon(getClass().getResource("b2fv.png"));
                plr4C5Label.setIcon(back);
            burnhand.add(4, hand.elementAt(4));
        }
        }
    }//GEN-LAST:event_plr4C5LabelMouseClicked

    private void plr3BetFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_plr3BetFieldActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_plr3BetFieldActionPerformed

    private void plr2BetFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_plr2BetFieldActionPerformed

    }//GEN-LAST:event_plr2BetFieldActionPerformed

    private void formWindowClosing(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosing
     JOptionPane.showMessageDialog(null, "Cannot quit when a game is running!", "Player Error", JOptionPane.ERROR_MESSAGE);
        
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
            java.util.logging.Logger.getLogger(FiveCardClient.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(FiveCardClient.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(FiveCardClient.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(FiveCardClient.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /*
         * Create and display the form
         */
        java.awt.EventQueue.invokeLater(new Runnable() {

            public void run() {
              //  new Client().setVisible(true);
            }
        });
    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel backGroundPanel;
    private javax.swing.JButton betButton;
    private javax.swing.JToolBar blackJackToolbar;
    public javax.swing.JTextArea chatWindow;
    private javax.swing.JButton checkButton;
    private javax.swing.JButton discardButton;
    private javax.swing.JButton foldButton;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
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
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
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
    public javax.swing.JTextField userText;
    // End of variables declaration//GEN-END:variables
}
