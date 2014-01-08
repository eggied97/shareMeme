package edeveloping.com.sharememe.asynctaskes;

import java.io.File;

import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.auth.AccessToken;
import twitter4j.auth.OAuthAuthorization;
import twitter4j.conf.Configuration;
import twitter4j.conf.ConfigurationBuilder;
import twitter4j.media.ImageUpload;
import twitter4j.media.ImageUploadFactory;
import twitter4j.media.MediaProvider;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.google.analytics.tracking.android.EasyTracker;

import edeveloping.com.sharememe.FileCache;

public class twitter extends AsyncTask<String, String, String>{
	
	final static String TWITTER_CONSUMER_KEY = "FDnWl50xoUodHtgR3gvD2Q";
    final static String TWITTER_CONSUMER_SECRET = "jqXV6jHuMqTL5nhdJS91AE260xBW7Hd4RhH0AgFnM";
    final static String TWITPIC_APICODE = "a8160fb5039a42816d7c525a86a70c69"; 

    static String PREFERENCE_NAME = "twitter_oauth";
    static final String PREF_KEY_OAUTH_TOKEN = "oauth_token";
    static final String PREF_KEY_OAUTH_SECRET = "oauth_token_secret";
    static final String PREF_KEY_TWITTER_LOGIN = "isTwitterLogedIn";
	
	ProgressDialog pDialog;
	Context mContext;
	File fileToDownload;
	private Activity activity;
	private SharedPreferences mSharedPreferences;
	
	public twitter (Activity ac, Context cntxt, SharedPreferences sp, File f){
		this.activity = ac;
		this.mContext = cntxt;
		this.mSharedPreferences = sp;
		this.fileToDownload = f;
	}
	
	protected void onPreExecute(){       
			pDialog = new ProgressDialog(mContext);
	        pDialog.setMessage("Updating to twitter...");
	        pDialog.setIndeterminate(false);
	        pDialog.setCancelable(false);
	        pDialog.show();
    }
	
	@Override
	protected String doInBackground(String... params) {		
		String url = params[0];
		
        String access_token = mSharedPreferences.getString(PREF_KEY_OAUTH_TOKEN, "");
      
        String access_token_secret = mSharedPreferences.getString(PREF_KEY_OAUTH_SECRET, "");
        
        Configuration conf = new ConfigurationBuilder()
            .setOAuthConsumerKey(TWITTER_CONSUMER_KEY)
            .setOAuthConsumerSecret(TWITTER_CONSUMER_SECRET)
            .setOAuthAccessToken(access_token)
            .setOAuthAccessTokenSecret(access_token_secret)
            .setMediaProviderAPIKey(TWITPIC_APICODE)
            .build();

		OAuthAuthorization auth = new OAuthAuthorization (conf);
		
		AccessToken accessToken = new AccessToken(access_token, access_token_secret);
		
		ImageUploadFactory upload = new ImageUploadFactory(conf);
		ImageUpload upload2 = upload.getInstance(MediaProvider.TWITPIC, auth);		
		
		try {
			String urlToPhoto = upload2.upload(fileToDownload);
			Log.d("ShareMemeDebug", "Image uploaded, Twitpic url is " + urlToPhoto);  
			
			String status = "This is a test : " + urlToPhoto;
			
		    ConfigurationBuilder builder = new ConfigurationBuilder();
		    builder.setOAuthConsumerKey(TWITTER_CONSUMER_KEY);
		    builder.setOAuthConsumerSecret(TWITTER_CONSUMER_SECRET);
		    
		    Twitter twitter = new TwitterFactory(builder.build()).getInstance(accessToken);
		    
		    twitter4j.Status response = twitter.updateStatus("http://www.sharememeapp.com ; "+urlToPhoto);
		             
		    Log.d("Status", "> " + response.getText());
		       
		} catch (TwitterException e1) {			
			EasyTracker.getTracker().sendException(e1.getMessage(), e1, false);
			e1.printStackTrace();
		}
		 return "lkalala";		
	}
	
	@Override
	protected void onPostExecute(String lol) {	
		pDialog.dismiss();	
		activity.runOnUiThread(new Runnable() {
			@Override
			public void run() {
				Toast.makeText(activity.getApplicationContext(),"Status tweeted successfully", Toast.LENGTH_SHORT).show();
		 
			}
		});
	}
}
