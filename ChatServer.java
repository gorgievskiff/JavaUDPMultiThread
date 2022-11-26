package Lab1UDp;

import java.io.*;
import java.net.*;
import java.util.*;

class Client{
    InetAddress address;
    int port;
    String name;

    public Client(InetAddress address, int port){
        this.address = address;
        this.port = port;
    }
}

public class ChatServer extends Thread {
    public final static int PORT = 7331;
    private final static int BUFFER = 1024;

    private DatagramSocket socket;
    private ArrayList<InetAddress> clientAddresses;
    private ArrayList<Integer> clientPorts;
    private HashSet<String> existingClients;
    private Map<String,Client> clientMap = new HashMap<String,Client>();
    public ChatServer() throws IOException {
        socket = new DatagramSocket(PORT);
        clientAddresses = new ArrayList();
        clientPorts = new ArrayList();
        existingClients = new HashSet();
    }

    public void run() {
        byte[] buf = new byte[BUFFER];
        while (true) {
            try {
                String initialMessage = "First login using 'login your name'\nIf you want to see all clients just type 'clients'\nIf you want to sent a message to someone use 'sent#message#clientname";
                Arrays.fill(buf, (byte)0);
                DatagramPacket packet = new DatagramPacket(buf, buf.length);
                socket.receive(packet);

                String content = decode(buf);

                InetAddress clientAddress = packet.getAddress();
                int clientPort = packet.getPort();

                byte [] initialData = initialMessage.getBytes();
                packet = new DatagramPacket(initialData,initialData.length,clientAddress,clientPort);
                socket.send(packet);

                if(content.startsWith("login")){
                    String [] split = content.split(" ");
                    String clientName = split[1];
                    clientMap.put(clientName,new Client(clientAddress,clientPort));
                }else if(content.startsWith("clients")){
                    String clients = "";

                    for (Map.Entry<String, Client> set :
                            clientMap.entrySet()) {
                        clients+= "Name: " + set.getKey() + "  Address: " + set.getValue().address + "  Port: " + set.getValue().port + "\n";
                    }

                    byte [] data = clients.getBytes();
                    packet = new DatagramPacket(data,data.length,clientAddress,clientPort);
                    socket.send(packet);
                }else if(content.startsWith("sent")){
                    //sent message clientName
                    int currentClient = 0;
                    currentClient = packet.getPort();
                    String cName = "";

                    //System.out.println("Current client: " + currentClient);
                    //String [] split = content.split(" ");
                    StringTokenizer st = new StringTokenizer(content,"#");
                    String trash = st.nextToken();
                    String message = st.nextToken();
                    String clientName = st.nextToken();
                    //System.out.println(message + "\n");
                    //System.out.println(clientName + "\n");

                    for (Map.Entry<String, Client> set :
                            clientMap.entrySet()) {
                        System.out.println(set.getKey() + "     " + set.getValue().port);
                        if(set.getKey().equals(clientName)){
                            System.out.println("dadadad");

                            for(Map.Entry<String,Client> set1 : clientMap.entrySet()){
                                int p = set.getValue().port;
                                if(set1.getValue().port == currentClient){
                                    System.out.println("dadada");
                                    cName = set.getKey();
                                }
                            }

                            message = cName + ": " + message;
                            byte [] data = message.getBytes();
                            packet = new DatagramPacket(data,data.length,set.getValue().address,set.getValue().port);
                            socket.send(packet);

                        }
                    }
                }





//                String id = clientAddress.toString() + "," + clientPort;
//                if (!existingClients.contains(id)) {
//                    existingClients.add( id );
//                    clientPorts.add( clientPort );
//                    clientAddresses.add(clientAddress);
//                }
//
//                for (int i=0; i < clientAddresses.size(); i++) {
//                    byte [] data = new String("Address: " + clientPorts.get(i)).getBytes();
//                    InetAddress cl = clientAddresses.get(i);
//                    int cp = clientPorts.get(i);
//                    packet = new DatagramPacket(data, data.length, cl, cp);
//                    socket.send(packet);
//
//                }


                //System.out.println(id + " : " + content);
               // String [] split = content.split(" ");
                //String msg = split[0];

               // byte[] data = (id + " : " +  content).getBytes();
//                for (int i=0; i < clientAddresses.size(); i++) {
//                    InetAddress cl = clientAddresses.get(i);
//                    int cp = clientPorts.get(i);
//                    packet = new DatagramPacket(data, data.length, cl, cp);
//                    socket.send(packet);
//                }
                //data = new byte[BUFFER];
            } catch(Exception e) {
                System.err.println(e);
            }
        }
    }

    public static String decode(byte[] msg){

        int i=0;
        StringBuilder sb = new StringBuilder();
        while(msg[i]!=0){
            sb.append((char)msg[i]);
            i++;
        }
        return sb.toString();
    }

    public static void main(String args[]) throws Exception {
        ChatServer s = new ChatServer();
        s.start();
    }
}