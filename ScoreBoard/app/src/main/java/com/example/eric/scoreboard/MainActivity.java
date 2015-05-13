package com.example.eric.scoreboard;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;
import android.app.Activity;

public class MainActivity extends Activity {

    TextView scoreView;
    TextView connectionStatus;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        String url = "https://api.usergrid.com/neric/sandbox/scores/";
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        // get reference to the views
        scoreView = (TextView) findViewById(R.id.scoreView);
        connectionStatus = (TextView) findViewById(R.id.connectionStatus);

        // check if connection to server was valid
        if(isConnected()){
            connectionStatus.setBackgroundColor(0xFF33FF00);

            //Create string to display at top text field
            String displayTop = "You are connected to:\n";
            displayTop += url;
            connectionStatus.setText(displayTop);
        }
        else{
            connectionStatus.setBackgroundColor(0xFFFF0033);
            connectionStatus.setText("No connection to server.");
        }

        // get network connection asynchronously
        new HttpAsyncTask().execute(url);
    }

    public static String GET(String url){
        InputStream inputStream = null;
        String result = "";
        try {

            // create HttpClient
            HttpClient client = new DefaultHttpClient();

            // GET request to the server
            HttpResponse res = client.execute(new HttpGet(url));

            // store response to inputStream
            inputStream = res.getEntity().getContent();

            // convert inputstream to string
            if(inputStream != null)
                result = convertInputStreamToString(inputStream);
            else
                result = "Could not get input stream!";

        } catch (Exception e) {
            Log.d("InputStream", e.getLocalizedMessage());
        }

        return result;
    }

    private static String convertInputStreamToString(InputStream inputStream) throws IOException{
        BufferedReader bufferedReader = new BufferedReader( new InputStreamReader(inputStream));
        String line = "";
        String result = "";
        while((line = bufferedReader.readLine()) != null)
            result += line;

        inputStream.close();
        return result;
    }

    public boolean isConnected(){
        ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Activity.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected())
            return true;
        else
            return false;
    }

    private class HttpAsyncTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... urls) {

            return GET(urls[0]);
        }

        // Display the result from AsyncTask()
        @Override
        protected void onPostExecute(String result) {

            //Post a popup text box
            Toast.makeText(getBaseContext(), "JSON Received!", Toast.LENGTH_LONG).show();

            try {
                JSONObject json = new JSONObject(result);

                String display = "Score:               Name:";
                display +=     "\n_______________________________________\n";

                //Get the entities jsonarray
                JSONArray jsonArr = json.getJSONArray("entities");

                //Add Scores to the display string
                for(int i=0; i<jsonArr.length(); i++){

                    //Get the score
                    String scr1 = jsonArr.getJSONObject(i).getString("Score");

                    //Score is maximum a 7-digit number. Pad the left with zeros
                    for(int j=0; j< 7 - scr1.length(); j++)
                    {
                        display += "0";
                    }

                    //Add rest of information to display
                    display += scr1;
                    display += "          ";
                    display += jsonArr.getJSONObject(i).getString("name");
                    display += "\n";
                }

                //send the display string to ScoreView
                scoreView.setText(display);

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}