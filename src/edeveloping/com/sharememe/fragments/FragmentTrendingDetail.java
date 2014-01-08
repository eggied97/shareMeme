package edeveloping.com.sharememe.fragments;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

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
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcelable;
import android.os.StrictMode;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.DirectionalViewPager;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout.LayoutParams;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.FacebookRequestError;
import com.facebook.HttpMethod;
import com.facebook.LoggingBehavior;
import com.facebook.Request;
import com.facebook.RequestAsyncTask;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.SessionLoginBehavior;
import com.facebook.SessionState;
import com.facebook.Settings;
import com.facebook.UiLifecycleHelper;
import com.facebook.Session.OpenRequest;
import com.facebook.internal.SessionAuthorizationType;
import com.facebook.internal.SessionTracker;
import com.facebook.model.GraphUser;
import com.google.analytics.tracking.android.EasyTracker;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.ImageLoadingListener;

import edeveloping.com.sharememe.ConnectionDetector;
import edeveloping.com.sharememe.FragmentChangeActivity;
import edeveloping.com.sharememe.ImageDownloader;
import edeveloping.com.sharememe.R;
import edeveloping.com.sharememe.R.drawable;
import edeveloping.com.sharememe.R.id;
import edeveloping.com.sharememe.R.layout;
import edeveloping.com.sharememe.adapter.TestFragmentAdapterForTrending;
import edeveloping.com.sharememe.asynctaskes.postToInstagram;
import edeveloping.com.sharememe.asynctaskes.twitter;


public class FragmentTrendingDetail extends Fragment{
	
	
	private ImageView twitterIV, email, instagram, facebook;
	private TextView trendingName;
	static ArrayList<String> urlList, twitterNames;
	TextView twitterName;
	String trendingNameToBecome;
	private static DirectionalViewPager pagerForTrending;
	public static int positionOfPager=0;
	
	private AwesomePagerAdapter awesomeAdapter;
	
	private Context mContext;
	
	private ImageDownloader imageDownloader;
	
	final static String TWITTER_CONSUMER_KEY = MainFragment.TWITTER_CONSUMER_KEY;
    final static String TWITTER_CONSUMER_SECRET = MainFragment.TWITTER_CONSUMER_SECRET;
    final static String TWITPIC_APICODE = MainFragment.TWITPIC_APICODE;
 
    // Preference Constants
    static String PREFERENCE_NAME = MainFragment.PREFERENCE_NAME;
    static final String PREF_KEY_OAUTH_TOKEN = MainFragment.PREF_KEY_OAUTH_TOKEN;
    static final String PREF_KEY_OAUTH_SECRET = MainFragment.PREF_KEY_OAUTH_SECRET;
    static final String PREF_KEY_TWITTER_LOGIN = MainFragment.PREF_KEY_TWITTER_LOGIN;
 
    static final String TWITTER_CALLBACK_URL = "oauth://ShareMemeTrendingDetail";
 
    // Twitter oauth urls
    static final String URL_TWITTER_AUTH = "auth_url";
    static final String URL_TWITTER_OAUTH_VERIFIER = "oauth_verifier";
    static final String URL_TWITTER_OAUTH_TOKEN = "oauth_token";
 
 // Progress dialog
    ProgressDialog pDialog;
 
    ProgressBar spinnerr;
    // Twitter
    private static Twitter twitter;
    private static RequestToken requestToken;
     
    // Shared Preferences
    private static SharedPreferences mSharedPreferences;
     
    // Internet Connection detector
    private ConnectionDetector cd;
	
    ImageView iv;
    
    private int positionOfMemes;
    
    private UiLifecycleHelper uiHelper;
    Session session;
    private SessionTracker sessionTracker;
    private SessionAuthorizationType authorizationType = null;
    private SessionLoginBehavior loginBehavior = SessionLoginBehavior.SSO_WITH_FALLBACK;
	private static final List<String> PERMISSIONS = Arrays.asList("publish_actions");
	private static final String PENDING_PUBLISH_KEY = "pendingPublishReauthorization";
	private boolean pendingPublishReauthorization = false;
 
    
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {        
	    
		if (savedInstanceState != null) {
		    pendingPublishReauthorization = 
		        savedInstanceState.getBoolean(PENDING_PUBLISH_KEY, false);		    
		}
		
		session = Session.getActiveSession();
		
		Bundle bundle = this.getArguments();
		int LOL = bundle.getInt("trendingName", 0);
		
		View v = inflater.inflate(R.layout.fragment_trending_detail, container, false);		
		
		twitterName = (TextView) v.findViewById(R.id.textViewTwitterUsername);
		twitterIV = (ImageView) v.findViewById(R.id.iv_twitterTrending);
		facebook = (ImageView) v.findViewById(R.id.iv_facebookTrending);
		instagram = (ImageView) v.findViewById(R.id.iv_instagramTrending);
		email = (ImageView) v.findViewById(R.id.iv_emailTrending);
		
        return v;
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
	    
	    mSharedPreferences = getActivity().getApplicationContext().getSharedPreferences("MyPref", 0);
	    trendingNameToBecome = mSharedPreferences.getString("TrendingDetailName", "Random");
	    
	    if (trendingNameToBecome.equalsIgnoreCase("Shoutout")){
			Log.d("SHareMEME", "we are removing all the social media");
			
			facebook.setVisibility(View.GONE);
			instagram.setVisibility(View.GONE);
			email.setVisibility(View.GONE);
			twitterName.setVisibility(View.VISIBLE);
			twitterName.setText("");
		}else{
			facebook.setVisibility(View.VISIBLE);
			instagram.setVisibility(View.VISIBLE);
			email.setVisibility(View.VISIBLE);
			twitterName.setVisibility(View.GONE);
			twitterName.setText("");
		}
	    
	    Log.d("SHAREMEME DEBUG TRENDING DETAILS", trendingNameToBecome + " is de trending naam");
	    
	    if (urlList == null){ urlList = new ArrayList<String>(); }	    
	    
	    twitterNames  = new ArrayList<String>();
	    
		pagerForTrending = (DirectionalViewPager) getActivity().findViewById(R.id.pagerTrendingDetail);
		
		trendingName = (TextView) getActivity().findViewById(R.id.tv_TrendingName);
		
		Typeface tf = Typeface.createFromAsset(getActivity().getAssets(),"fonts/Roboto-Regular.ttf");
			 
		trendingName.setTypeface(tf);
		trendingName.setText(trendingNameToBecome);
			
		if (Build.VERSION.SDK_INT > 9) {
			StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
			StrictMode.setThreadPolicy(policy);
		}
			
		new getTrendingDetails().execute();
			
		
		
		twitterIV.setOnClickListener(new OnClickListener() {					
			@Override
			public void onClick(View v) {					
				int posALALALA = pagerForTrending.getCurrentItem();
				EasyTracker.getTracker().sendEvent("Button Press", "shareTwitter", "meme url : "+urlList.get(positionOfMemes), (long) 1);
				loginToTwitter(positionOfMemes, trendingNameToBecome);						
			}
		});		       
			       
		facebook.setOnClickListener(new OnClickListener() {			   		
			@Override
			public void onClick(View v) {
				int posALALALA = pagerForTrending.getCurrentItem();
				EasyTracker.getTracker().sendEvent("Button Press", "shareFacebook", "meme url : "+urlList.get(positionOfMemes), (long) 1);
				Toast.makeText(getActivity().getApplicationContext(), "facebook clicked", Toast.LENGTH_SHORT).show();
						
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
						request.setPermissions(PERMISSIONS); // Note that you cannot set email AND publish_actions in the same request
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
					request.setPermissions(PERMISSIONS); // Note that you cannot set email AND publish_actions in the same request
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
				int posALALALA = pagerForTrending.getCurrentItem();				
				Log.i("ShareMEME", positionOfPager+"");
				
				EasyTracker.getTracker().sendEvent("Button Press", "shareInstagram", "meme url : "+urlList.get(positionOfMemes), (long) 1);
					
				postToInstagram pti = new postToInstagram(getActivity(), getActivity());
				pti.execute(urlList.get(positionOfMemes));
			}
		});
			       
		email.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
			
				int posALALALA = pagerForTrending.getCurrentItem();
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
	
	
	@Override
	public void onResume(){
		super.onResume();
		
	       if (!isTwitterLoggedInAlready()) {
	           Uri uri = getActivity().getIntent().getData();
	           if (uri != null && uri.toString().startsWith(TWITTER_CALLBACK_URL)) {
	               
	               String verifier = uri.getQueryParameter(URL_TWITTER_OAUTH_VERIFIER);
	    
	               try {
	                  
	                   AccessToken accessToken = twitter.getOAuthAccessToken(requestToken, verifier);
	    
	                   Editor e = mSharedPreferences.edit();
	    
	                   e.putString(PREF_KEY_OAUTH_TOKEN, accessToken.getToken());
	                   e.putString(PREF_KEY_OAUTH_SECRET,
	                           accessToken.getTokenSecret());
	                   
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
	
	 public void publishStory(String Name, String Caption, String Description, String link, String picture) {
	 	   
	    	session = Session.getActiveSession();
	    	
	    	if (session != null){
	    		List<String> permissions = session.getPermissions();
	            
	            if (!isSubsetOf(PERMISSIONS, permissions)) {
	                pendingPublishReauthorization = true;
	                Session.NewPermissionsRequest newPermissionsRequest = new Session
	                        .NewPermissionsRequest(this, PERMISSIONS);
	                session.requestNewPublishPermissions(newPermissionsRequest);	               
	            }
	    		 
	    		 Bundle postParams = new Bundle();
	    	     postParams.putString("name", Name);
	    	     postParams.putString("caption", Caption);
	    	     postParams.putString("description", Description);
	    	     postParams.putString("link", link);
	    	     postParams.putString("picture", picture);

	    	     Request.Callback callback= new Request.Callback() { public void onCompleted(Response response) {} };	    	        
	    	     Request request = new Request(session, "me/feed", postParams, HttpMethod.POST, callback);

				  RequestAsyncTask task = new RequestAsyncTask(request);
				  task.execute();
	    	}else{
	    		Log.d("ShareMEMEFACEBOOK", "Session is null");	    		
	    	}
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
	    
	    public void loginToTwitter(int PosOfMemes, String type) {
	       
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
	                e.printStackTrace();
	            }
	        } else {
	        	
	        	String auth_tokenString =  mSharedPreferences.getString(PREF_KEY_OAUTH_TOKEN, "");
	        	String auth_secretString = mSharedPreferences.getString(PREF_KEY_OAUTH_SECRET, "");
	        	
	        	if (auth_secretString != "" && auth_tokenString != ""){
	        	
	        	Log.d("SHAREMEME", type.trim());
	        		
	        	if (type.trim().equalsIgnoreCase("Shoutout/la")){
	        		
	        		 AccessToken accessToken = new AccessToken(auth_tokenString, auth_secretString);
		         		
	    			 ConfigurationBuilder builder = new ConfigurationBuilder();
	    			    builder.setOAuthConsumerKey(TWITTER_CONSUMER_KEY);
	    			    builder.setOAuthConsumerSecret(TWITTER_CONSUMER_SECRET);
	        		
	        		Twitter twitterforfriendship = new TwitterFactory(builder.build()).getInstance(accessToken);
	   			 
					try {
						twitterforfriendship.createFriendship(twitterNames.get(PosOfMemes));
						long userID = accessToken.getUserId();					
						
						try	{
						    getActivity().getPackageManager().getPackageInfo("com.twitter.android", 0);
						    
						    Intent intent = new Intent(Intent.ACTION_VIEW);
						    intent.setClassName("com.twitter.android", "com.twitter.android.ProfileActivity");
						    
						    intent.putExtra("user_id", userID+"L");
						    
							startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://twitter.com/"+twitterNames.get(PosOfMemes))));
						}catch (NameNotFoundException e){
							startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://twitter.com/"+twitterNames.get(PosOfMemes))));
						}					
					} catch (TwitterException e1) {						
						e1.printStackTrace();
						Toast.makeText(getActivity(), "We had a little error, try again in a few minutes", Toast.LENGTH_SHORT).show();
					}	        		
	        	}else{	        		
		        	Log.d("ShareMeme", "Twitter clicked, and going :D");
		        	String urlOfTheMeme = urlList.get(PosOfMemes);
		        	
		        	File root = android.os.Environment.getExternalStorageDirectory(); 
		        	File dir = new File (root.getAbsolutePath() + "/ShareMeme/Cache");
		    		
		        	String Filename = String.valueOf(urlOfTheMeme.hashCode());
		        	File imageToPost = new File(dir, Filename);
	        	}	        	
	   }else{
   		Log.d("ShareMeme", "Token and shit not here");
	   }
	}      
}   	

		private boolean isTwitterLoggedInAlready() {	       
	        return mSharedPreferences.getBoolean(PREF_KEY_TWITTER_LOGIN, false);
	    }       
		
	    
	   	class getTrendingDetails extends AsyncTask<String, Void, Boolean> {
	   		
	        boolean state;
	        String errorMessage;

	        ProgressDialog dialog;

	        protected void onPreExecute(){}

	        protected Boolean doInBackground(String... stringsForLogin) {
	            
	            state = false;

	            edeveloping.nl.sharememe.utils.UserFunctions uf = new edeveloping.nl.sharememe.utils.UserFunctions();

	            Log.d("Sharememe_Debug_trending_detail", "TrendingDetailName = "+trendingNameToBecome);
	            
	            
	            trendingNameToBecome = trendingNameToBecome+"/la";
	            
	            String[] separated = trendingNameToBecome.split("/");
	            
	            JSONObject json = uf.getTrendsDetail(separated[0], 50);
	            
	            urlList.clear();
	            
	            try {
	            	JSONArray jArray = json.getJSONArray("memes");
	                        
	            	int size = jArray.length();
	                        
	            	for (int i = 0; i < size; i++){
	            		try{
	            			JSONObject o = jArray.getJSONObject(i);
	                        		
	            			urlList.add(o.getString("big_pic"));
	            			twitterNames.add(o.getString("username"));
	                        	
	            		}catch(JSONException ex1){ EasyTracker.getTracker().sendException(ex1.getMessage(), ex1, false); }
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
	        		if (pagerForTrending.getAdapter() != null){
	        			Log.d("ShareMEME_DEBUG", "There is an adapter allready");
	        			
	        			pagerForTrending = (DirectionalViewPager) getActivity().findViewById(R.id.pagerTrendingDetail);
	        		}
	        		
	        		mContext = getActivity();
	        		
	        		pagerForTrending.setOnPageChangeListener(new OnPageChangeListener() {
	    				
	    				@Override
	    				public void onPageSelected(int arg0) {	    					
	    					Log.d("ShareMemeTrending", ""+arg0);
	    					positionOfMemes = arg0;
	    					
	    					if (trendingNameToBecome.equalsIgnoreCase("Shoutout/la")){
	    						twitterName.setText("@ " +twitterNames.get(arg0));
	    					}
	    					
	    				}
	    				
	    				@Override
	    				public void onPageScrolled(int arg0, float arg1, int arg2) {}
	    				
	    				@Override
	    				public void onPageScrollStateChanged(int arg0) {}
	    			});
	        		
	        		pagerForTrending.setAdapter(new TestFragmentAdapterForTrending(getActivity().getSupportFragmentManager(), urlList));
	        		pagerForTrending.setOffscreenPageLimit(urlList.size()-1);	        		
	        	}	        	
	        }
	    }
		
		
	    private class AwesomePagerAdapter extends PagerAdapter{
            @Override
            public int getCount() {
                
            	if (urlList.size() == 0){
            		return 0;
            	}else{  
            		return urlList.size();
            	}            
            }
            @Override
            public Object instantiateItem(ViewGroup collection, int position) {
                    Log.d("sharememe", "item trending_detail. inititatted, pos : "+position);
                    
                    Resources res = getResources();
                    Drawable draw = res.getDrawable(R.drawable.ic_launcher);                    
                    
                    LinearLayout layout = new LinearLayout(getActivity());
            		layout.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
            		layout.setGravity(Gravity.CENTER);
                    
            		iv = new ImageView(FragmentChangeActivity.mContext);
                    
                    LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
                    iv.setLayoutParams(params);
                    
                    spinnerr = new ProgressBar(getActivity());
            		LayoutParams paramsForSpinner = new LayoutParams(75, 75);
            		paramsForSpinner.gravity = Gravity.CENTER;
            		spinnerr.setLayoutParams(paramsForSpinner);            		
            		
            		spinnerr.setVisibility(View.VISIBLE);
            		
            		layout.addView(spinnerr);
            		layout.addView(iv);
                    
                
                    imageDownloader = ImageDownloader.getInstance(FragmentChangeActivity.mContext);
                    
                    imageDownloader.displayImageWithCallback(iv,  FragmentTrendingDetail.urlList.get(position), new ImageLoadingListener() {
            			@Override
            			public void onLoadingCancelled(String arg0, View arg1) {}

            			@Override
            			public void onLoadingComplete(String arg0, View arg1, Bitmap arg2) {            				
            				spinnerr.setVisibility(View.GONE);
            				iv.setVisibility(View.VISIBLE);
            				iv.setImageBitmap(arg2);
            			}

            			@Override
            			public void onLoadingFailed(String arg0, View arg1, FailReason arg2) {
            				spinnerr.setVisibility(View.GONE);
            				Toast.makeText(getActivity(), "An error occurd ; "+arg2.toString(), Toast.LENGTH_SHORT).show();
            				
            			}

            			@Override
            			public void onLoadingStarted(String arg0, View arg1) {
            				spinnerr.setVisibility(View.VISIBLE);
            			}
                     });
                    
                   collection.addView(layout,0);
                                     
                   return layout;
            }
            
            @Override
            public void destroyItem(ViewGroup collection, int position, Object view) {
                    collection.removeView((LinearLayout) view);
            }

            @Override
            public boolean isViewFromObject(View view, Object object) {
                    return (view==object);
            }
            
            @Override
            public void finishUpdate(ViewGroup arg0) {}
            

            @Override
            public void restoreState(Parcelable arg0, ClassLoader arg1) {}

            @Override
            public Parcelable saveState() {
                    return null;
            }

            @Override
            public void startUpdate(ViewGroup arg0) {}    
	    }
}	