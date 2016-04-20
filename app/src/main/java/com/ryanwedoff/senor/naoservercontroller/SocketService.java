/**
 * This Android Service is the network backend that connects to a running socket chat. It send and receives messages to the server.
 */
package com.ryanwedoff.senor.naoservercontroller;

import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Binder;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.util.Log;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;

public class SocketService extends Service {

    public final static String SERVER_RESPONSE = "com.ryanwedoff.senor.naoservercontroller.SocketService.MESSAGE";
    public final static String SERVER_CONNECTION = "com.ryanwedoff.senor.naoservercontroller.SocketService.CONNECTION";
    final static String ACTION = "ACTION";
    public static boolean isServiceRunning = false;
    private final IBinder myBinder = new LocalBinder();
    private PrintWriter out;
    private Socket socket;
    private boolean mRun = false;
    private String outMess;
    public SocketService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        System.out.println("This is the onBind Methond");
        return myBinder;
    }

    @Override
    public void onCreate(){
        super.onCreate();
        System.out.println("I am in onCreate in the service");
    }

    /**
     * Used for sending messages to the socket connection
     */
    public void sendMessage(String message){
        outMess = message;
        if(out!=null && !out.checkError()){
            System.out.println("in sendMessage "+ message);
            out.println(message);
            out.flush();
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);
        System.out.println("I am in on start");
        //  Toast.makeText(this,"Service created ...", Toast.LENGTH_LONG).show();
        Runnable connect = new connectSocket();
        new Thread(connect).start();
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        try {
            isServiceRunning = false;
            socket.close();
            Log.i("Socket has been closed", "Closed");
        } catch (Exception e) {
            e.printStackTrace();
        }
        mRun = false;
        if (out != null)
            out.flush();
        if (out != null) {
            out.close();
        }
        socket = null;
    }

    public class LocalBinder extends Binder {
        public SocketService getService() {
            System.out.println("I am a local binder");
            return SocketService.this;
        }
    }

    /**
     * This class is for receiving messages from the socket connection
     */
   private class ReceiveMessage  {
        BufferedReader in;
        String incomingMessage;
        void exe() {
            incomingMessage = null;
            try {
                in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            } catch (IOException e) {
                e.printStackTrace();
            }
            mRun = true;
            while (mRun) {
                try {
                    incomingMessage = in.readLine();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                if (incomingMessage != null) {
                    if (!outMess.contains(incomingMessage) && !incomingMessage.equals("")) {
                        Intent intent = new Intent();
                        intent.setAction(ACTION);
                        intent.putExtra(SERVER_RESPONSE, incomingMessage);
                        sendBroadcast(intent);
                        Log.i("Response", incomingMessage);
                        incomingMessage = null;
                    }
                }
            }
            mRun = false;
        }
    }

    private class connectSocket implements Runnable {
        @Override
        public void run() {
            try {
                //Accesses settings for the ip address
                SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
                String ipAddress = preferences.getString(getString(R.string.pref_ipAddress_key), getString(R.string.pref_ipAddress_default));
                InetAddress serverAddr = InetAddress.getByName(ipAddress);
                String serverPort = preferences.getString(getString(R.string.pref_port_key),getString(R.string.pref_port_default));
                int serverPortInt = Integer.parseInt(serverPort);
                Log.i("TCP Client", "C: Connecting...");
                //create a socket to make the connection with the server
                socket = new Socket(serverAddr, serverPortInt);
                //send the message to the server
                out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())), true);
                Log.i("TCP Client", "C: Sent.");
                Log.i("TCP Client", "C: Done.");
                isServiceRunning = true;
                if(socket.getRemoteSocketAddress() != null){
                    ReceiveMessage receiveMessage = new ReceiveMessage();
                    receiveMessage.exe();
                }

            } catch (Exception e) {
                Intent intent = new Intent();
                intent.setAction(ACTION);
                intent.putExtra(SERVER_CONNECTION, "Cannot connect to Server");
                sendBroadcast(intent);
                Log.e("TCP", "C: Error", e);
            }
        }
    }


}