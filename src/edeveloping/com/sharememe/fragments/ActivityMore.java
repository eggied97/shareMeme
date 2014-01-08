package edeveloping.com.sharememe.fragments;

import java.util.List;
import java.util.Vector;

import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.User;
import twitter4j.auth.AccessToken;
import twitter4j.auth.OAuthAuthorization;
import twitter4j.auth.RequestToken;
import twitter4j.conf.Configuration;
import twitter4j.conf.ConfigurationBuilder;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.google.analytics.tracking.android.EasyTracker;

import edeveloping.com.sharememe.FragmentChangeActivity;
import edeveloping.com.sharememe.R;
import edeveloping.com.sharememe.WebDialog;
import edeveloping.com.sharememe.R.id;
import edeveloping.com.sharememe.R.layout;



public class ActivityMore extends Fragment{
	
	ActionBar mActionBar;
	static String TWITTER_CONSUMER_KEY = "FDnWl50xoUodHtgR3gvD2Q";
    static String TWITTER_CONSUMER_SECRET = "jqXV6jHuMqTL5nhdJS91AE260xBW7Hd4RhH0AgFnM";
    static String TWITPIC_APICODE = "a8160fb5039a42816d7c525a86a70c69";
  
    static String PREFERENCE_NAME = "twitter_oauth";
    static final String PREF_KEY_OAUTH_TOKEN = "oauth_token";
    static final String PREF_KEY_OAUTH_SECRET = "oauth_token_secret";
    static final String PREF_KEY_TWITTER_LOGIN = "isTwitterLogedIn";
 
    static final String TWITTER_CALLBACK_URL = "oauth://ShareMemeMoreScreen";
 
    // Twitter oauth urls
    static final String URL_TWITTER_AUTH = "auth_url";
    static final String URL_TWITTER_OAUTH_VERIFIER = "oauth_verifier";
    static final String URL_TWITTER_OAUTH_TOKEN = "oauth_token";
 
 // Progress dialog
    ProgressDialog pDialog;
 
    // Twitter
    private static Twitter twitter;
    private static RequestToken requestToken;
     
    // Shared Preferences
    private static SharedPreferences mSharedPreferences;
		
    private PagerAdapter mPagerAdapter;
    
    
    ImageView ivFeedback, ivAdvertise, ivTerms;
    TextView versionTV;
 
    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.activity_more, container, false);
    }
	

    @Override
	public void onActivityCreated(Bundle savedInstanceState) {
	    super.onActivityCreated(savedInstanceState);
	    
	    FragmentChangeActivity.bigText.setText("More");
	    
	    mSharedPreferences = getActivity().getApplicationContext().getSharedPreferences("MyPref", 0);
	    
	    this.initialisePaging();
	    
	    ImageView twitterEgbert = (ImageView) getActivity().findViewById(R.id.twitter_egbert);
	    ImageView twitterMerrick = (ImageView) getActivity().findViewById(R.id.twitter_Merrick);
	    
	    ivFeedback = (ImageView) getActivity().findViewById(R.id.ivFeedBack);
	    ivAdvertise = (ImageView) getActivity().findViewById(R.id.ivAdvertise);
	    ivTerms = (ImageView) getActivity().findViewById(R.id.IvTerms);
	    
	    versionTV = (TextView) getActivity().findViewById(R.id.textviewEditVersion);
	    
	    String version;
	    
	    try{
		    PackageInfo pInfo = getActivity().getPackageManager().getPackageInfo(getActivity().getPackageName(), 0);
		    version = pInfo.versionName;
	    }catch (Exception e){
	    	EasyTracker.getTracker().sendException(e.getMessage(), false);
	    	version = "1";
	    }
	    
	    versionTV.setText(version);
	    
	    twitterEgbert.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				EasyTracker.getTracker().sendEvent("Button Press", "TwitterFollow", "@eggied97", (long) 1);
				loginToTwitter("eggied97",25532313);
			}
		});
	    
	    twitterMerrick.setOnClickListener(new OnClickListener() {			
			@Override
			public void onClick(View v) {
				EasyTracker.getTracker().sendEvent("Button Press", "TwitterFollow", "@mlee____", (long) 1);
				loginToTwitter("mlee____",71062674);
			}
		});
	    
	    ivFeedback.setOnClickListener(new OnClickListener() {			
			@Override
			public void onClick(View v) {
				EasyTracker.getTracker().sendEvent("Button Press", "email_feedback", "awesome", (long) 1);				
				Intent i = new Intent(Intent.ACTION_SEND);
				i.setType("message/rfc822");
				i.putExtra(Intent.EXTRA_EMAIL  , new String[]{"sharememedroid@gmail.com"});
				i.putExtra(Intent.EXTRA_SUBJECT, "ShareMeme feedback");
				try {
				    startActivity(Intent.createChooser(i, "Select your email client..."));
				} catch (android.content.ActivityNotFoundException ex) {
				    Toast.makeText(getActivity(), "There are no email clients installed.", Toast.LENGTH_SHORT).show();
				}
			}
		});
	    
	    ivAdvertise.setOnClickListener(new OnClickListener() {			
			@Override
			public void onClick(View v) {
				EasyTracker.getTracker().sendEvent("Button Press", "email_advertisement", "cool", (long) 1);
				
				Intent i = new Intent(Intent.ACTION_SEND);
				i.setType("message/rfc822");
				i.putExtra(Intent.EXTRA_EMAIL  , new String[]{"sharememedroid@gmail.com"});
				i.putExtra(Intent.EXTRA_SUBJECT, "ShareMeme advertisement");

				try {
				    startActivity(Intent.createChooser(i, "Select your email client..."));
				} catch (android.content.ActivityNotFoundException ex) {
				    Toast.makeText(getActivity(), "There are no email clients installed.", Toast.LENGTH_SHORT).show();
				}
			}
		});
	    
	    ivTerms.setOnClickListener(new OnClickListener() {			
			@Override
			public void onClick(View v) {
				WebDialog wd = new WebDialog(getActivity(), "http://www.m.sharememeapp.com/terms");
				wd.show();
			}
		});
	    
	    
	    if (!isTwitterLoggedInAlready()) {
	    	Uri uri = getActivity().getIntent().getData();
	    	if (uri != null && uri.toString().startsWith(TWITTER_CALLBACK_URL)) {
	    		
	    		String verifier = uri.getQueryParameter(URL_TWITTER_OAUTH_VERIFIER);
	    
	    		try {
	    			AccessToken accessToken = twitter.getOAuthAccessToken(requestToken, verifier);
	    
	    			Editor e = mSharedPreferences.edit();
	    
	    			e.putString(PREF_KEY_OAUTH_TOKEN, accessToken.getToken());
	    			e.putString(PREF_KEY_OAUTH_SECRET, accessToken.getTokenSecret());	                  
	    			e.putBoolean(PREF_KEY_TWITTER_LOGIN, true);
	    			e.commit();
	                   
	    			Log.d("Twitter OAuth Token", "> " + accessToken.getToken());
	    
	    			long userID = accessToken.getUserId();
	    			User user = twitter.showUser(userID);
	    			String username = user.getName();
	                   
	    			Log.d("ShareMeme", username + " is your username!");
	                   
	    		} catch (Exception e) {	                   
	    			EasyTracker.getTracker().sendException(e.getMessage(), e, false);
	    			Log.e("Twitter Login Error", "> " + e.getMessage());
	    		}
	    	}
	    }
    }
    
    private void loginToTwitter(String usernameTwitter, int userID) {       
        if (!isTwitterLoggedInAlready()) {
            ConfigurationBuilder builder = new ConfigurationBuilder();
            builder.setOAuthConsumerKey(TWITTER_CONSUMER_KEY);
            builder.setOAuthConsumerSecret(TWITTER_CONSUMER_SECRET);
            Configuration configuration = builder.build();
             
            TwitterFactory factory = new TwitterFactory(configuration);
            twitter = factory.getInstance();
 
            try {
                requestToken = twitter.getOAuthRequestToken(TWITTER_CALLBACK_URL);
                this.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(requestToken.getAuthenticationURL())));
            } catch (TwitterException e) {
            	EasyTracker.getTracker().sendException(e.getMessage(), e, false);
                e.printStackTrace();
            }
        }else{        	
        	String auth_tokenString =  mSharedPreferences.getString(PREF_KEY_OAUTH_TOKEN, "");
        	String auth_secretString = mSharedPreferences.getString(PREF_KEY_OAUTH_SECRET, "");
        	
        	if (auth_secretString != "" && auth_tokenString != ""){
        	
        		Log.d("ShareMeme", "Twitter clicked, and going :D");        	
        	
        		try {
        			Configuration conf = new ConfigurationBuilder()
        			.setOAuthConsumerKey(TWITTER_CONSUMER_KEY)
        			.setOAuthConsumerSecret(TWITTER_CONSUMER_SECRET)
        			.setOAuthAccessToken(auth_tokenString)
        			.setOAuthAccessTokenSecret(auth_secretString)
        			.setMediaProviderAPIKey(TWITPIC_APICODE)
        			.build();

        			OAuthAuthorization auth = new OAuthAuthorization (conf);
        		
        			AccessToken accessToken = new AccessToken(auth_tokenString, auth_secretString);
			 
        			ConfigurationBuilder builder = new ConfigurationBuilder();
        			builder.setOAuthConsumerKey(TWITTER_CONSUMER_KEY);
        			builder.setOAuthConsumerSecret(TWITTER_CONSUMER_SECRET);
			             
			    
        			Twitter twitter1 = new TwitterFactory(builder.build()).getInstance(accessToken);
			 
        			twitter1.createFriendship(usernameTwitter);				
				
        			try{
					
				    
        				getActivity().getPackageManager().getPackageInfo("com.twitter.android", 0);
				    
        				Intent intent = new Intent(Intent.ACTION_VIEW);
        				intent.setClassName("com.twitter.android", "com.twitter.android.ProfileActivity");
				   
        				intent.putExtra("user_id", userID+"L");
				 
        				startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://twitter.com/"+usernameTwitter)));
					
        			}catch (NameNotFoundException e){
        				startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://twitter.com/"+usernameTwitter)));
        			}
				
        		} catch (TwitterException e) {				
        			e.printStackTrace();
        			EasyTracker.getTracker().sendException(e.getMessage(), e, false);
        		}
        	}else{
        		Log.d("ShareMeme", "Token and shit not here");        		
        	}        
        }
    }
 
    
    private boolean isTwitterLoggedInAlready() {       
        return mSharedPreferences.getBoolean(PREF_KEY_TWITTER_LOGIN, false);
    }       
	
    private void initialisePaging() {
    	WizardPagerAdapter adapter = new WizardPagerAdapter();
        ViewPager pager = (ViewPager)super.getActivity().findViewById(R.id.pagerWithTheDevelopers);
        pager.setAdapter(adapter);
    }
    
    @Override
    public void onStart() {
      super.onStart();  
    }
    
    @Override
    public void onStop() {
      super.onStop();
    }
    
   
    class WizardPagerAdapter extends PagerAdapter {

        public Object instantiateItem(View collection, int position) {
            int resId = 0;
            switch (position) {
            case 0:
                resId = R.id.EgbertPage;
                break;
            case 1:
                resId = R.id.MerrickPage;
                break;
            }
            return collection.findViewById(resId);
        }

        @Override
        public int getCount() {
            return 2;
        }

        @Override
        public boolean isViewFromObject(View arg0, Object arg1) {
            return arg0 == ((View) arg1);
        }
    }

}