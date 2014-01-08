package edeveloping.com.sharememe.fragments;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.apache.http.util.ByteArrayBuffer;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.User;
import twitter4j.auth.AccessToken;
import twitter4j.auth.OAuthAuthorization;
import twitter4j.auth.RequestToken;
import twitter4j.conf.Configuration;
import twitter4j.conf.ConfigurationBuilder;
import twitter4j.media.ImageUpload;
import twitter4j.media.ImageUploadFactory;
import twitter4j.media.MediaProvider;
import android.R.integer;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.Signature;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v4.view.DirectionalViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.Base64;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockFragment;
import com.facebook.FacebookRequestError;
import com.facebook.HttpMethod;
import com.facebook.Request;
import com.facebook.RequestAsyncTask;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.SessionLoginBehavior;
import com.facebook.Session.Builder;
import com.facebook.Session.NewPermissionsRequest;
import com.facebook.Session.OpenRequest;
import com.facebook.Session.StatusCallback;
import com.facebook.SessionState;
import com.facebook.UiLifecycleHelper;
import com.facebook.android.AsyncFacebookRunner;
import com.facebook.android.Facebook;
import com.facebook.*;
import com.facebook.internal.SessionAuthorizationType;
import com.facebook.internal.SessionTracker;
import com.facebook.model.*;
import com.facebook.widget.LoginButton;
import com.facebook.widget.LoginButton.OnErrorListener;
import com.google.ads.AdRequest;
import com.google.ads.AdView;
import com.google.analytics.tracking.android.EasyTracker;
import com.nostra13.universalimageloader.core.ImageLoader;

import edeveloping.com.sharememe.ConnectionDetector;
import edeveloping.com.sharememe.FragmentChangeActivity;
import edeveloping.com.sharememe.R;
import edeveloping.com.sharememe.R.id;
import edeveloping.com.sharememe.R.layout;
import edeveloping.com.sharememe.adapter.TestFragmentAdapter;
import edeveloping.com.sharememe.asynctaskes.postToInstagram;
import edeveloping.com.sharememe.asynctaskes.twitter;


public class MainFragment extends SherlockFragment{
	
	public static int height,width;
	ActionBar ab;
	ListView gridview;
	Boolean scrolling;
	LinearLayout llForListView;
	ImageView twitterIV, email, instagram, facebook;
	ArrayList<String> urlList;
	public static DirectionalViewPager pager;
	public static int positionOfMemes=0;
	
	private Context mContext;
	
	private static final List<String> PERMISSIONS = Arrays.asList("publish_actions");
	private static final String PENDING_PUBLISH_KEY = "pendingPublishReauthorization";
	private boolean pendingPublishReauthorization = false;
	
	final static String TWITTER_CONSUMER_KEY = "FDnWl50xoUodHtgR3gvD2Q";
    final static String TWITTER_CONSUMER_SECRET = "jqXV6jHuMqTL5nhdJS91AE260xBW7Hd4RhH0AgFnM";
    final static String TWITPIC_APICODE = "a8160fb5039a42816d7c525a86a70c69";
 
    // Preference Constants
    static String PREFERENCE_NAME = "twitter_oauth";
    static final String PREF_KEY_OAUTH_TOKEN = "oauth_token";
    static final String PREF_KEY_OAUTH_SECRET = "oauth_token_secret";
    static final String PREF_KEY_TWITTER_LOGIN = "isTwitterLogedIn";
 
    static final String TWITTER_CALLBACK_URL = "oauth://ShareMeme";
 
    // Twitter oauth urls
    static final String URL_TWITTER_AUTH = "auth_url";
    static final String URL_TWITTER_OAUTH_VERIFIER = "oauth_verifier";
    static final String URL_TWITTER_OAUTH_TOKEN = "oauth_token";
 
 // Progress dialog
    ProgressDialog pDialog;
 
    // Twitter
    private static Twitter twt;
    private static RequestToken requestToken;
     
    // Shared Preferences
    private static SharedPreferences mSharedPreferences;
     
    // Internet Connection detector
    private ConnectionDetector cd;

    private SessionTracker sessionTracker;
    
    private SessionAuthorizationType authorizationType = null;
    private SessionLoginBehavior loginBehavior = SessionLoginBehavior.SSO_WITH_FALLBACK;
    
    LayoutInflater mInflater;
    
    View mCustomView;
    
    TextView mTitleTextView;
    
    private UiLifecycleHelper uiHelper;
    
    Session session;
    
    
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
		if (savedInstanceState != null) {
		    pendingPublishReauthorization = 
		        savedInstanceState.getBoolean(PENDING_PUBLISH_KEY, false);
		}
		
		View view = inflater.inflate(R.layout.fragment_main, container, false);

		twitterIV = (ImageView) view.findViewById(R.id.iv_twitter);
		facebook = (ImageView) view.findViewById(R.id.iv_facebook);
		instagram = (ImageView) view.findViewById(R.id.iv_instagram);
		email = (ImageView) view.findViewById(R.id.iv_email);
		
		session = Session.getActiveSession();
    	
        return view;
    }
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        uiHelper = new UiLifecycleHelper(getActivity(), callback);
        uiHelper.onCreate(savedInstanceState);
    }
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
	    super.onActivityCreated(savedInstanceState);

	    FragmentChangeActivity.bigText.setText("Home");
	    
	    if (Build.VERSION.SDK_INT > 9) {
	    	StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
	    	StrictMode.setThreadPolicy(policy);
	    }		
	    
	    if (!checkInterwebs()){
	    	getActivity().finish();
	    }			
	   
	    if(TWITTER_CONSUMER_KEY.trim().length() == 0 || TWITTER_CONSUMER_SECRET.trim().length() == 0){
	         
	    	Toast.makeText(getActivity().getApplicationContext(), "There went something wrong with the twitter integration, please try again later!", Toast.LENGTH_SHORT).show();
	        return;
	    }
	    
	    mSharedPreferences = getActivity().getApplicationContext().getSharedPreferences("MyPref", 0);
	    
	    if(isFirstUse()){
	    	if (Session.getActiveSession() != null){
	    		Session.getActiveSession().closeAndClearTokenInformation();
	    	}
	    }
	        	
	    AdView adView = (AdView)getActivity().findViewById(R.id.adView);
	    adView.loadAd(new AdRequest());
		    
	    DisplayMetrics metrics = new DisplayMetrics();
	    getActivity().getWindowManager().getDefaultDisplay().getMetrics(metrics);

	    width = metrics.widthPixels;
	    height = metrics.heightPixels;
			
	    twitterIV.setOnClickListener(new OnClickListener() {
					
	    	@Override
	    	public void onClick(View v) {
	    		int posALALALA = pager.getCurrentItem();
	    		EasyTracker.getTracker().sendEvent("Button Press", "shareTwitter", "meme url : "+urlList.get(posALALALA), (long) 1);
	    		loginToTwitter(positionOfMemes);
	    	}
	    });
	    
	    if (!isTwitterLoggedInAlready()) {
	    	Uri uri = getActivity().getIntent().getData();
	    	if (uri != null && uri.toString().startsWith(TWITTER_CALLBACK_URL)) {
	    		String verifier = uri.getQueryParameter(URL_TWITTER_OAUTH_VERIFIER);			    
	    		try {			         
	    			AccessToken accessToken = twt.getOAuthAccessToken(requestToken, verifier);
	    			
	    			Editor e = mSharedPreferences.edit();
	    			
	    			e.putString(PREF_KEY_OAUTH_TOKEN, accessToken.getToken());
	    			e.putString(PREF_KEY_OAUTH_SECRET, accessToken.getTokenSecret());
			                  
	    			e.putBoolean(PREF_KEY_TWITTER_LOGIN, true);
	    			e.commit(); 
			    
	    			Log.d("Twitter OAuth Token", "> " + accessToken.getToken());
	    			
	    			long userID = accessToken.getUserId();
	    			User user = twt.showUser(userID);
	    			String username = user.getName();
			                   
	    			Log.d("ShareMeme", username + " is your username!");
			                   
	    		} catch (Exception e) {
	    			EasyTracker.getTracker().sendException(e.getMessage(), e, false);
	    			Log.e("Twitter Login Error", "> " + e.getMessage());
	    		}
	    	}
	    }
	    
	    facebook.setOnClickListener(new OnClickListener() {
			   		
	    	@Override
	    	public void onClick(View v) {
			
	    		int posALALALA = pager.getCurrentItem();
	    		EasyTracker.getTracker().sendEvent("Button Press", "shareFacebook", "meme url : "+urlList.get(posALALALA), (long) 1);
						
	    		Log.d("SHAREMEME", "facebook clicked");
						
	    		authorizationType = SessionAuthorizationType.PUBLISH;
						
	    		pDialog = new ProgressDialog(getActivity());
	    		pDialog.setMessage("Posting to facebook...");
	    		pDialog.setIndeterminate(false);
	    		pDialog.setCancelable(false);
	    		pDialog.show();
						
	    		if(Session.getActiveSession() != null){
	    			if(Session.getActiveSession().getState()==SessionState.OPENED){
	    				Log.d("facebookSHAREMEME", "1");                            	
	    				
	    				createFacebookConnection("ShareMeme", "New. Fun. Ratchet", "I laughed so hard at this one!", "http://www.sharememeapp.com", urlList.get(positionOfMemes));
	    			}else{
	    				Log.d("facebookSHAREMEME", "2");
	    				Session s = new Session(getActivity());	    				
	    				Session.OpenRequest request = new Session.OpenRequest(getActivity());
	    				request.setPermissions(PERMISSIONS);
	    				request.setLoginBehavior(SessionLoginBehavior.SSO_ONLY);
	    				
	    				request.setCallback(callback);
	    				s.openForPublish(request);
	    				
	    				createFacebookConnection("ShareMeme", "New. Fun. Ratchet", "I laughed so hard at this one!", "http://www.sharememeapp.com", urlList.get(positionOfMemes));
	    			}
	    		}else{
	    			Log.d("facebookSHAREMEME", "3");
	    			Session s = new Session(getActivity());
	    			Session.setActiveSession(s);
	    			Session.OpenRequest request = new Session.OpenRequest(getActivity());
	    			request.setLoginBehavior(SessionLoginBehavior.SSO_ONLY);
	    			request.setCallback(callback);
	    			s.openForPublish(request);
	    			
	    			createFacebookConnection("ShareMeme", "New. Fun. Ratchet", "I laughed so hard at this one!", "http://www.sharememeapp.com", urlList.get(positionOfMemes));
	    		}
							
						pDialog.dismiss();
					}
				});
			       
			       instagram.setOnClickListener(new OnClickListener() {			   		
					@Override
					public void onClick(View v) {
						int posALALALA = pager.getCurrentItem();
						
						Log.d("ShareMEME", positionOfMemes+"");
						
						EasyTracker.getTracker().sendEvent("Button Press", "shareInstagram", "meme url : "+urlList.get(posALALALA), (long) 1);					
						postToInstagram pti = new postToInstagram(getActivity(), getActivity());
						pti.execute(urlList.get(positionOfMemes));
						
					}
				});
			       
			       email.setOnClickListener(new OnClickListener() {
			   		
			    	   @Override
					public void onClick(View v) {
			    
			    		   int posALALALA = pager.getCurrentItem();
			    		   EasyTracker.getTracker().sendEvent("Button Press", "shareEmail", "meme url : "+urlList.get(posALALALA), (long) 1);
						
			    		   Intent i = new Intent(Intent.ACTION_SEND);
			    		   i.setType("message/rfc822");						
			    		   i.putExtra(Intent.EXTRA_SUBJECT, "ShareMeme share");
			    		   i.putExtra(Intent.EXTRA_TEXT   , "I laught so hard at this one! \n\n " + urlList.get(positionOfMemes) + " \n \n Download ShareMeme via this link ; ");
			    		   try {
			    			   startActivity(Intent.createChooser(i, "Send mail..."));
			    		   } catch (android.content.ActivityNotFoundException ex) {
			    			   Toast.makeText(getActivity(), "There are no email clients installed.", Toast.LENGTH_SHORT).show();
			    		   }
			    	   }
			       });
			       
			       if (urlList == null || urlList.size() < 1){
			    	   urlList = new ArrayList<String>();
			       }		
			       new getHomeFeed().execute();
	}
	
	public void publishStory(String Name, String Caption, String Description, String link, String picture) {
 	   
		session = Session.getActiveSession();

		Bundle postParams = new Bundle();
		postParams.putString("name", Name);
		postParams.putString("caption", Caption);
		postParams.putString("description", Description);
		postParams.putString("link", link);
		postParams.putString("picture", picture);
		
		final Context context = getActivity();
		Request.Callback callback = new Request.Callback() {
			public void onCompleted(Response response) {
				FacebookRequestError error = response.getError();
				if (error != null) {					
					Toast.makeText(context, error.getErrorMessage(), Toast.LENGTH_SHORT).show();
	                	} else {
	                		JSONObject graphResponse = response.getGraphObject().getInnerJSONObject();
	                		String postId = null;
	                		try {
	                			postId = graphResponse.getString("id");
	                		} catch (JSONException e) {
	                			Log.i("Facebook error", "JSON error " + e.getMessage());
	                		}
	                }
	            }
	        };

	        Request request = new Request(Session.getActiveSession(), "me/feed", postParams, HttpMethod.POST, callback);

	        RequestAsyncTask task = new RequestAsyncTask(request);
	        task.execute();
    	
	        if (session != null){  	
	        	List<String> permissions = session.getPermissions();
	        }else{
	        	Log.d("ShareMEMEFACEBOOK", "Session is null");    		
    	}
    }
    
	public void createFacebookConnection( String Namen, String Captionn, String Description, String link, String picture) {
		Session session = new Session(getActivity());
        Session.setActiveSession(session);

        Settings.addLoggingBehavior(LoggingBehavior.INCLUDE_ACCESS_TOKENS);

        Session.StatusCallback statusCallback = new Session.StatusCallback() {
            @Override
            public void call(Session session, SessionState state, Exception exception) {
                String message = "Facebook session status changed - " + session.getState() + " - Exception: " + exception;                
                Log.w("Facebook test", message);

                if (session.isOpened() || session.getPermissions().contains("publish_actions")) {
                	publishStory("ShareMeme", "New. Fun. Ratchet", "I laughed so hard at this one!", "http://www.sharememeapp.com", urlList.get(positionOfMemes));
                } else if (session.isOpened()) {
                    OpenRequest open = new OpenRequest(getActivity()).setCallback(this);
                    List<String> permission = new ArrayList<String>();
                    permission.add("publish_actions");
                    open.setPermissions(permission);
                    Log.w("Facebook test", "Open for publish");
                    session.openForPublish(open);
                }
            }
        };

        if (!session.isOpened() && !session.isClosed() && session.getState() != SessionState.OPENING) {
            session.openForRead(new Session.OpenRequest(this).setCallback(statusCallback));
        } else {
            Log.w("Facebook test", "Open active session");
            Session.openActiveSession(getActivity(), true, statusCallback);
        }
    }
    
    
    
   public boolean isFirstUse(){
	   return true;
   }
    
    public boolean isSubsetOf(Collection<String> subset, Collection<String> superset) {
        for (String string : subset) {
            if (!superset.contains(string)) {
                return false;
            }
        }
        return true;
    }
	
    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(PENDING_PUBLISH_KEY, pendingPublishReauthorization);
        uiHelper.onSaveInstanceState(outState);
    }
    
    public void onSessionStateChange(Session session, SessionState state, Exception exception) {
        if (state.isOpened()) {
            Log.i("SHAREMEMEFACEBOOK", "Logged in...");
            Request.executeMeRequestAsync(session, new Request.GraphUserCallback() {            	
            	@Override
            	public void onCompleted(GraphUser user, Response response) {}
            });
            
        } else if (state.isClosed()) {
            Log.i("SHAREMEMEFACEBOOK", "Logged out...");
        }
    }
    
    public Session.StatusCallback callback = new Session.StatusCallback() {
        @Override
        public void call(Session session, SessionState state, Exception exception) {
            onSessionStateChange(session, state, exception);
        }
    };
    
    private void loginToTwitter(int PosOfMemes) {      
        if (!isTwitterLoggedInAlready()) {
            ConfigurationBuilder builder = new ConfigurationBuilder();
            builder.setOAuthConsumerKey(TWITTER_CONSUMER_KEY);
            builder.setOAuthConsumerSecret(TWITTER_CONSUMER_SECRET);
            Configuration configuration = builder.build();
             
            TwitterFactory factory = new TwitterFactory(configuration);
            twt = factory.getInstance();
 
            try {
                requestToken = twt
                        .getOAuthRequestToken(TWITTER_CALLBACK_URL);
                this.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(requestToken.getAuthenticationURL())));
            } catch (TwitterException e) {
                e.printStackTrace();
            }
        } else {
        	String auth_tokenString =  mSharedPreferences.getString(PREF_KEY_OAUTH_TOKEN, "");
        	String auth_secretString = mSharedPreferences.getString(PREF_KEY_OAUTH_SECRET, "");
        	
        	if (auth_secretString != "" && auth_tokenString != ""){
        	
        		Log.d("ShareMeme", "Twitter clicked, and going :D");
        		String urlOfTheMeme = urlList.get(PosOfMemes);
        	
        	
        		File root = android.os.Environment.getExternalStorageDirectory(); 
        		File dir = new File (root.getAbsolutePath() + "/ShareMeme/Cache");
    		
        		String Filename = String.valueOf(urlOfTheMeme.hashCode());
        		File imageToPost = new File(dir, Filename);
        		
        		twitter TWITTER = new twitter(getActivity(), getActivity(), mSharedPreferences, imageToPost);
        		TWITTER.execute(urlOfTheMeme);        	
        	}else{
        		Log.d("ShareMeme", "Token and shit not here");
        	}
        
        }
    }
    
    @Override
    public void onStart() {
      super.onStart();    
     
		 pager =  (DirectionalViewPager)getActivity().findViewById(R.id.pager);
		 pager.setOnPageChangeListener(new OnPageChangeListener() {
			
			@Override
			public void onPageSelected(int arg0) {				
				Log.d("ShareMeme", ""+arg0);
				positionOfMemes = arg0;
				
			}
			
			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) {}
			
			@Override
			public void onPageScrollStateChanged(int arg0) {}
		});
    }
    
    @Override
    public void onStop() {
      super.onStop();
    }
    
    @Override
    public void onResume() {
        super.onResume();             
        uiHelper.onResume();
    }
    
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        uiHelper.onActivityResult(requestCode, resultCode, data);      
        Session.getActiveSession().onActivityResult(getActivity(), requestCode, resultCode, data);
    }

    @Override
    public void onPause() {
        super.onPause();
        uiHelper.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        uiHelper.onDestroy();
    }
 
    private boolean isTwitterLoggedInAlready() {       
        return mSharedPreferences.getBoolean(PREF_KEY_TWITTER_LOGIN, false);
    }       
	
	public boolean checkInterwebs (){
		 cd = new ConnectionDetector(getActivity().getApplicationContext());
		 
	        if (!cd.isConnectingToInternet()) {	            
	        	AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity().getApplicationContext());
	        	
	        	alertDialogBuilder.setTitle("No internet!");
	        	
	        	alertDialogBuilder
	        	.setMessage("We need internet in order to work, please try again if you have internet!")
	        	.setCancelable(false)
	        	.setPositiveButton("okay",new DialogInterface.OnClickListener() {
	        		public void onClick(DialogInterface dialog,int id) {}
	        	});	
	    			
	        	AlertDialog alertDialog = alertDialogBuilder.create();
	     
	        	alertDialog.show();
	    			
	        	return false;
	        }else{
	        	return true;
	        }
	}
	
		
	class getHomeFeed extends AsyncTask<String, Void, Boolean> {
		
		boolean state;
		String errorMessage;

		ProgressDialog dialog;

		protected void onPreExecute(){}


		protected Boolean doInBackground(String... stringsForLogin) {
			state = false;

			edeveloping.nl.sharememe.utils.UserFunctions uf = new edeveloping.nl.sharememe.utils.UserFunctions();

			JSONObject json = uf.getMemeStuff();
			
			try {					               
				JSONArray jArray = json.getJSONArray("memes");
	                        
				int size = jArray.length();
	                        
				for (int i = 0; i < size; i++){
					try{
						JSONObject o = jArray.getJSONObject(i);
	                        		
						if (!urlList.contains(o.getString("big_pic"))){
							urlList.add(o.getString("big_pic"));
						}
					}catch(JSONException ex1){
						EasyTracker.getTracker().sendException(ex1.getMessage(), ex1, false);
						Toast.makeText(getActivity().getApplicationContext(), "error : "+ex1.toString(), Toast.LENGTH_SHORT).show();
					}     	
				}
				
				state = true;
	                
			} catch (JSONException ex) {
				ex.printStackTrace();
				state = false;
			}
			return state;
		}

		@Override
		protected void onPostExecute(Boolean state) {
			if (state == true){
				Log.d("ShareMEME_DEBUG", "setting the pager adapter");
	        		
				if (pager.getAdapter() != null){
					Log.d("ShareMEME_DEBUG", "There is an adapter allready");
				}
	        		
				pager.setAdapter(null);
				pager.setAdapter(new TestFragmentAdapter(getActivity().getSupportFragmentManager(), urlList));
				pager.setOffscreenPageLimit(urlList.size()-1);
			}	        	
		}
	}
}
