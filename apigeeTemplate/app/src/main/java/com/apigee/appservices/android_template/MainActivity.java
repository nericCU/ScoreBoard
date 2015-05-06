package com.apigee.appservices.android_template;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.ArrayList;
import java.util.List;
import android.util.Log;

import com.apigee.fasterxml.jackson.databind.JsonNode;
import com.apigee.sdk.ApigeeClient;
import com.apigee.sdk.data.client.ApigeeDataClient;
import com.apigee.sdk.data.client.callbacks.ApiResponseCallback;
import com.apigee.sdk.data.client.entities.Entity;
import com.apigee.sdk.data.client.response.ApiResponse;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;
import android.content.Context;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;



public class MainActivity extends Activity {

    //TextView tv = new TextView(this);
    //tv.setText(“Hello World!”);
    //setContentView(tv);


    String ORGNAME = "neric"; // <-- Put your username here!!!
    String APPNAME = "sandbox";

    ApigeeClient apigeeClient = new ApigeeClient(ORGNAME,APPNAME,this.getBaseContext());
    ApigeeDataClient client = apigeeClient.getDataClient();
    ApigeeDataClient dataClient = apigeeClient.getDataClient();

    //specify the entity collection to query
    String type = "scores";

    //specify a valid query string
    String query = "status = 'active' &limit=10";

/*
    //call getEntitiesAsync to initiate the asynchronous API call
    //dataClient.getEntitiesAsync(type, query, new ApiResponseCallback() {
    dataClient.getEntitiesAsync ("scores", "select *", new ApiResponseCallback() {

        //If getEntitiesAsync fails, catch the error
        @Override
        public void onException(Exception e) {
            // Error
        }

        //If getEntitiesAsync is successful, handle the response object
        @Override
        public void onResponse(ApiResponse response) {
            try {
                if (response != null) {
                    // Success
                }
            } catch (Exception e) { //The API request returned an error
                // Fail
            }
        }
    });
*/

    /*
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final TextView text = (TextView) findViewById(R.id.mainActivityText);
      */
    /* 
        1. Set your account details in the app

        - Enter your ORGNAME below� it's the username you picked when you signed up at apigee.com
        - Keep the APPNAME as 'sandbox': it's a context we automatically created for you.
          It's completely open by default, but don't worry, other apps you create are not!      */
        
 //       String ORGNAME = "neric"; // <-- Put your username here!!!
  //      String APPNAME = "sandbox";
        
 //       ApigeeClient apigeeClient = new ApigeeClient(ORGNAME,APPNAME,this.getBaseContext());
//        ApigeeDataClient client = apigeeClient.getDataClient();
        //ApigeeDataClient client = apigeeClient.getDataClient();
 /*
         2. Set some details for your first object
 
         Great, we know where your account is now!
         Let's try to create a book, save it on Apigee, and output it in the app.
         
         - Keep the type as 'book'
         - Enter the title of your favorite book below, instead of 'the old man and the sea'.    */
         
         //Map<String, Object> data = new HashMap<String, Object>();
         //data.put("type", "book");
         //data.put("title", "Guide to Android Programming");






         
     /*
         3. Now run it!
         
         You're good to go! If you're in Eclipse, just Run the code to your preferred device or emulator.
         
         - In the menus, select Run > Run, or hit the green 'play' button in your toolbar.
         - Make sure you've followed these steps to add a device to your environment: http://developer.android.com/tools/building/building-eclipse.html
         - If everything is working as expected, you will get a visual confirmation in the app!      */
         

        /*
         client.createEntityAsync(data, new ApiResponseCallback() {
             @Override
             public void onException(Exception e) { // Error - the book was not saved properly
                 text.setText("Could not create the book.\n\nDid you enter your username correctly on line 30 of src/com/apigee/appservices/android_template/MainActivity.java?");
             }
 
             @Override
             public void onResponse(ApiResponse response) { // Success - the book was created properly
                 try { 
                     if (response != null) { 
                         // The saved object is returned in the 'response' variable
                         // defined on line 68. The code below outputs it on the page!
                         String successMessage = "Success! Here is the object we stored; "
                                             +   "notice the timestamps and unique id we created for you:\n\n"
                                             +   response.getEntities().get(0).toString();
                         text.setText(successMessage);
                     }
                 } catch (Exception e) {
                     text.setText("Could not create the book.\n\nDid you enter your username correctly on line 30 of src/com/apigee/appservices/android_template/MainActivity.java?");                  
                 }
             }
         });
         */
         
     /*
         4. Congrats, you're done!
 
         - You can try adding more properties after line 46 and reloading the app!
         - You can then see the admin view of this data by logging in at https://apigee.com/usergrid
         - Or you can go explore more advanced examples in our docs: http://apigee.com/docs/usergrid         */
//    }
}


