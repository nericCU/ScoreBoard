//Splash.java - handles splash screen view (first view created).

package com.apigee.messagee;



import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
 
public class Splash extends ApigeeActivity {
   /** Called when the activity is first created. */
	
	
   @Override
   public void onCreate(Bundle savedInstanceState) {
	   
      super.onCreate(savedInstanceState);
      
      //set view to splash screen
      setContentView(R.layout.splash);
      
      //create thread to wait a bit then switch to login screen
	  new WaitSplashTask().execute();
	  
   }
   
   
    //Delay thread for splash screen
	private class WaitSplashTask extends AsyncTask<Void, Void, Void> {

		
		public WaitSplashTask() {
			super();
		}
		
		//Function 
		@Override
		protected Void doInBackground(Void... v) {
			
			//wait
			android.os.SystemClock.sleep(3000);
			
			//kill splash activity
			finish();
			
			//start login activity
		  	Intent i = new Intent();
		  	i.setClassName("com.apigee.messagee",
              "com.apigee.messagee.Login");
		  	startActivity(i);
			return null;
		}


	}
	
}