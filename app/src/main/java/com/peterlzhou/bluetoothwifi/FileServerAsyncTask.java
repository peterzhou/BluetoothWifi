package com.peterlzhou.bluetoothwifi;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Iterator;

/**
 * Created by peterlzhou on 4/20/17.
 */

public class FileServerAsyncTask extends AsyncTask<Void, Void, String> {

    private final Context context;

    /**
     * @param context
     */
    public FileServerAsyncTask(Context context) {
        this.context = context;
    }

    @Override
    protected String doInBackground(Void... params) {
        try {
            System.out.println("Starting server!");
            ServerSocket serverSocket = new ServerSocket(8888);
            Socket client = serverSocket.accept();
            System.out.println("Server met client!");
            InputStream inputstream = client.getInputStream();
            JSONObject response = convertStreamToJSON(inputstream);
            WifiActivity.setCurrentJSON(response);
            System.out.println("Response is:");
            Iterator<String> iter = response.keys();
            while (iter.hasNext()) {
                String key = iter.next();
                try {
                    Object value = response.get(key);
                    System.out.println(key + ": " + value);
                } catch (JSONException e) {
                    // Something went wrong!
                    e.printStackTrace();
                }
            }
            if (response.getBoolean("ack")== true){
                System.out.println("This is a packet we need to send");
                serverSocket.close();
                return "Success";
            }
            else{
                System.out.println("This is a response");
                serverSocket.close();
                return "Response";
            }

        } catch (IOException e) {
            System.out.println("IO EXCEPTION");
            return null;
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }

    /*
     * (non-Javadoc)
     * @see android.os.AsyncTask#onPostExecute(java.lang.Object)
     */
    @Override
    protected void onPostExecute(String result) {
        //Send packet to the server
        if (result == "Success") {
            System.out.println("Sendshit");

        }
        else if (result == "Response"){
            System.out.println("Response protocol");
        }
        else if (result == "Response"){
            System.out.println("Error");
        }

    }

    /*
     * (non-Javadoc)
     * @see android.os.AsyncTask#onPreExecute()
     */
    @Override
    protected void onPreExecute() {
        System.out.println("Opening a server socket");
    }

    private JSONObject convertStreamToJSON(InputStream is) throws JSONException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        StringBuilder sb = new StringBuilder();

        String line = null;
        try {
            while ((line = reader.readLine()) != null) {
                sb.append(line).append('\n');
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        System.out.println("String is " + sb.toString());
        return new JSONObject(sb.toString());
    }

}