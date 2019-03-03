/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package casinoroyale;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
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
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;

/**
 *
 * @author Steve
 */
public class BackGroundServer {
    public static welcomeServer chatServer;
    public static InetAddress inet;
    public static InetAddress serverAddress;
    public static String localAddress = "";
    public static boolean startAServer = true;
    public static ServerSocket serverSocket;

    public  BackGroundServer(){
        try {
            ServerSocket serverSocket = new ServerSocket(5000);
            startAServer = true;
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "Cannot start a server on port 5000" ,"Connection Error",JOptionPane.ERROR_MESSAGE);
            startAServer = false;

        }
            if(startAServer == true){
            chatServer = new welcomeServer();    //Initializes a new chatServer Thread for communication
            chatServer.start();             //Starts the server thread and will be running in the BackGround
            JOptionPane.showMessageDialog(null, "Server Started.");
            try {
                inet = InetAddress.getLocalHost();
            } catch (UnknownHostException ex) {
                Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
            }
            startAServer = false;
        }
            
    }
     public String getLocalAddress(){
            return localAddress;
        }
    public static class welcomeServer extends Thread {

        public LinkedList clients;
        public ByteBuffer read;
        public ByteBuffer write;
        public ServerSocketChannel ss;
        public Selector readerSelector;
        public CharsetDecoder asciiDecoder;

        public welcomeServer() {
            clients = new LinkedList();
            read = ByteBuffer.allocateDirect(300);
            write = ByteBuffer.allocateDirect(300);
            asciiDecoder = Charset.forName("US-ASCII").newDecoder();


        }

        public void InitServer() {
            try {
                ss = ServerSocketChannel.open();
                ss.configureBlocking(false);
                localAddress= InetAddress.getLocalHost().getHostAddress();
                //serverAddress = InetAddress.getLocalHost();
                ss.socket().bind(new InetSocketAddress(localAddress, 5000));               
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
                    chatServer.SendMessage("~");
                    JOptionPane.showMessageDialog(null, "Connection Granted");

                    //sendBroadcastMessage(newClient, "2");
                    //sendMessage(newClient, serverAddress.getHostName() + "<server> You are connected... type 'quit' to exit");
                }
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(null, "ERROR:" + ex.toString());
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
                       

                    } else {
                        //StringBuffer str = (StringBuffer) key.attachment();
                        read.flip();
                        String data = asciiDecoder.decode(read).toString();
                        read.clear();

                        // str.append(data);

                        String line = data;

                        if ((line.indexOf("\n") != -1) || (line.indexOf("\r") != -1)) {
                            line = line.trim();
                            
                           
                            //chatServer.SendMessage("Back At You!!");
                            chatServer.SendMessage(line);
                           

//                          
//                          

                            if (line.equals("quit")) {
                                client.close();
                                clients.remove(client);                                
                                //chatWindow += ("Logout: " + client.socket().getInetAddress());
                                sendBroadcastMessage(client, "Logout: " + client.socket().getInetAddress());
                               // chatWindow += ("" + '\n');

                            } //else {
//                                chatWindow += (client.socket().getInetAddress() + ": " + line);
//                                sendBroadcastMessage(client, client.socket().getInetAddress() + ": " + line);
//                                chatWindow += ("" + '\n');
//                            }


                        }
                    }
                }
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(null, "Error: " + ex.toString());
            }
        }
    }//End Thread Class
}
