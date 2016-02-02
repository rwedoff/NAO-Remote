package com.ryanwedoff.senor.naoservercontroller;

import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
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

    PrintWriter out;
    Socket socket;
    InetAddress serverAddr;
    private final IBinder myBinder = new LocalBinder();


    final static String MY_ACTION = "MY_ACTION";
    public final static String EXTRA_MESSAGE = "com.ryanwedoff.senor.naoservercontroller.SocketService.MESSAGE";

    public class LocalBinder extends Binder {
        public SocketService getService(){
            System.out.println("I am a local binder");
            return SocketService.this;
        }
    }
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

    public void IsBoundable(){
        //Toast.makeText(this, "This is bindable", Toast.LENGTH_LONG).show();
    }

    public void sendMessage(String message){
        if(out!=null && !out.checkError()){
            System.out.println("in sendMessage "+ message);
            out.println(message);
            out.flush();
        }
    }

    private class ReceiveMessage extends AsyncTask<BufferedReader, Void, String> {
        BufferedReader in;
        @Override
        protected String doInBackground(BufferedReader... params) {
            String response = "";
                try {
                    try{
                        in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                    }catch (NullPointerException n){
                        Log.e("ERROR","NULL Pointer in Receive");
                    }
                    try {
                            response = in.readLine();
                    } catch (Exception e) {
                        Log.e("Error", "Error");
                        e.printStackTrace();
                        response = "ERROR: Connection error\n";
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }

            if(response!=null){
                Log.e("Response", response);
                return response;
            }
            else
                return "";
        }

        @Override
        protected void onPostExecute(String result) {
            Log.e("Post", "Post");
            if(result == null){
                result = "No response from server";
            }
            Intent intent = new Intent();
            intent.setAction(MY_ACTION);
            intent.putExtra(EXTRA_MESSAGE, result);
            sendBroadcast(intent);
        }

        @Override
        protected void onPreExecute() {}

        @Override
        protected void onProgressUpdate(Void... values) {}
    }


    @Override
    public int onStartCommand(Intent intent,int flags, int startId){
        super.onStartCommand(intent, flags, startId);
        System.out.println("I am in on start");
        //  Toast.makeText(this,"Service created ...", Toast.LENGTH_LONG).show();
        Runnable connect = new connectSocket();
        new Thread(connect).start();
        return START_STICKY;
    }

    public void recvMess(){
        new ReceiveMessage().execute();
    }


    class connectSocket implements Runnable {
        @Override
        public void run() {
            try {
                //Accesses settings for the ip address
                SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
                String ipAddress = preferences.getString(getString(R.string.pref_ipAddress_key), getString(R.string.pref_ipAddress_default));
                serverAddr = InetAddress.getByName(ipAddress);

                String serverport = preferences.getString(getString(R.string.pref_port_key),getString(R.string.pref_port_default));
                int serverPortInt = Integer.parseInt(serverport);
                Log.e("TCP Client", "C: Connecting...");
                //create a socket to make the connection with the server
                socket = new Socket(serverAddr, serverPortInt);
                try {
                    //send the message to the server
                    out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())), true);
                    Log.e("TCP Client", "C: Sent.");
                    Log.e("TCP Client", "C: Done.");
                    new ReceiveMessage().execute();
                }
                catch (Exception e) {
                    Log.e("TCP", "S: Error", e);
                }
            } catch (Exception e) {
                Intent intent = new Intent();
                intent.setAction(MY_ACTION);
                intent.putExtra(EXTRA_MESSAGE, "Cannot connect to Server");
                sendBroadcast(intent);
                Log.e("TCP", "C: Error", e);
            }
        }
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        try {
            socket.close();
            Log.e("Socket has been closed", "Closed");
        } catch (Exception e) {
            e.printStackTrace();
        }
        socket = null;
    }


}
