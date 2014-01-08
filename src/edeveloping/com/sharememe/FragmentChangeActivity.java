package edeveloping.com.sharememe;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import twitter4j.*;
import twitter4j.auth.AccessToken;
import twitter4j.auth.OAuthAuthorization;
import twitter4j.auth.RequestToken;
import twitter4j.conf.Configuration;
import twitter4j.conf.ConfigurationBuilder;
import twitter4j.media.ImageUpload;
import twitter4j.media.ImageUploadFactory;
import twitter4j.media.MediaProvider;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;

import com.facebook.LoggingBehavior;
import com.facebook.Request;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.Session.OpenRequest;
import com.facebook.SessionState;
import com.facebook.Settings;
import com.facebook.UiLifecycleHelper;
import com.facebook.android.Facebook;
import com.facebook.model.GraphUser;
import com.google.ads.*;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.DirectionalViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.actionbarsherlock.app.*; 
import com.actionbarsherlock.view.MenuInflater;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu.OnOpenListener;

import edeveloping.com.sharememe.FileCache;
import edeveloping.com.sharememe.R;
import edeveloping.com.sharememe.fragments.MainFragment;
import edeveloping.com.sharememe.fragments.MenuFragment;
import edeveloping.com.sharememe.fragments.fragmentTrending;

import com.google.analytics.tracking.android.EasyTracker;
import com.google.analytics.tracking.android.GoogleAnalytics;
import com.google.analytics.tracking.android.Tracker;


import com.jeremyfeinstein.slidingmenu.lib.app.*;

public class FragmentChangeActivity extends SherlockFragmentActivity {

    private Fragment mContent;
    ActionBar mActionBar;
    SlidingMenu menu;
    public static Context mContext;
    static Context mContextApp;
    static MainFragment MF;
    static int iii = 0;
    static String TrendingNAAMJA;
    static Bundle CONTENTBUNDLE;
    private Tracker mGaTracker;
    private GoogleAnalytics mGaInstance;
    public static TextView bigText;
    String shownTag;
    
    private UiLifecycleHelper uiHelper;

   
     
    private void initActionBar(Context mContext){
   	 mActionBar = getSupportActionBar();
        /*mActionBar.setDisplayShowHomeEnabled(false);
        mActionBar.setDisplayShowTitleEnabled(false);
        mInflater = LayoutInflater.from(this);
        mCustomView = mInflater.inflate(R.layout.custom_action_layout, null);
        mTitleTextView = (TextView) mCustomView.findViewById(R.id.title_text);
        mTitleTextView.setText("Home");
        mActionBar.setCustomView(mCustomView);
        mActionBar.setDisplayShowCustomEnabled(true);
        mActionBar.setBackgroundDrawable(getResources().getDrawable(R.drawable.navbar));*/
        
   	 
   	 EasyTracker.getInstance().setContext(this);
   	 
   	 mActionBar.setDisplayShowCustomEnabled(true);
        View view = getLayoutInflater().inflate(R.layout.custom_action_layout, null);
        mActionBar.setDisplayShowHomeEnabled(false);
       
        getSupportActionBar().setCustomView(view);
        
        view.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				
					menu.toggle(true);
				
			}
		});
        
        CONTENTBUNDLE = new Bundle();
   }
	
    
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        uiHelper.onActivityResult(requestCode, resultCode, data);
       // Session.getActiveSession().onActivityResult(getActivity(), requestCode, resultCode, data);
        Session.getActiveSession().onActivityResult(this, requestCode, resultCode, data);
    }
    
    public void onSessionStateChange(Session session, SessionState state, Exception exception) {
        if (state.isOpened()) {
            Log.i("SHAREMEMEFACEBOOK", "Logged in... FROM ACTIVITY");
            
            // make request to the /me API
            Request.executeMeRequestAsync(session, new Request.GraphUserCallback() {

              // callback after Graph API response with user object
              @Override
              public void onCompleted(GraphUser user, Response response) {
            	  Log.d("SHAREMEMEFACEBOOK_ACTIVITY", user.getFirstName());
              }
            });
            
        } else if (state.isClosed()) {
            Log.i("SHAREMEMEFACEBOOK", "Logged out... FROM ACTIVITY");
        }
    }
    
    public Session.StatusCallback callback = new Session.StatusCallback() {
        @Override
        public void call(Session session, SessionState state, Exception exception) {
            onSessionStateChange(session, state, exception);
        }
    };
    
    
    public FragmentChangeActivity() {
        super();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // set the Above View
        
        initActionBar(getApplicationContext());
        bigText = (TextView) findViewById(R.id.title_text);
        
       mContext = getApplicationContext();
       mContextApp = this;
       
       
       MF = new MainFragment();
       
       uiHelper = new UiLifecycleHelper(this, callback);
       uiHelper.onCreate(savedInstanceState);
       
       
       
       int i=0;
       
       mContent = MF; 
       shownTag = "MAIN";
       
       EasyTracker.getTracker().sendView("/StartPage");
        
       
        setContentView(R.layout.activity_main);
        getSupportFragmentManager().beginTransaction().add(R.id.content_frame, mContent, "MAIN").commit();
        
        
        /**
         * MENU
         * */
        
        menu = new SlidingMenu(getApplicationContext());
       
        menu.setMode(SlidingMenu.LEFT);
        menu.setTouchModeAbove(SlidingMenu.TOUCHMODE_MARGIN);
        menu.setShadowWidthRes(R.dimen.shadow_width);
        menu.setShadowDrawable(R.drawable.shadow);
        menu.setBehindOffsetRes(R.dimen.slidingmenu_offset);
        menu.setFadeDegree(0.6f);        
        menu.attachToActivity(this, SlidingMenu.SLIDING_CONTENT);
        menu.setMenu(R.layout.menu);
        getSupportFragmentManager()
        .beginTransaction()
        .replace(R.id.menuFrame, new MenuFragment())
        .commit();

    }    

    @Override
    public void onBackPressed() {
    	   if (shownTag.equalsIgnoreCase("TrendingDetails")){
           	//go back to the trending page
           	
           	 fragmentTrending ft = (fragmentTrending) getSupportFragmentManager().findFragmentByTag("TRENDING");
           	 
           	 FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
           	 
           	 fragmentTransaction.remove(getSupportFragmentManager().findFragmentByTag("TrendingDetails"));
           	 
           	 fragmentTransaction.show(ft);
           	 
           	 fragmentTransaction.commit();
           	 
           	 shownTag = "TRENDING";
           	 
           }else if(shownTag.equalsIgnoreCase("MAIN")){
        	   
        	   super.onBackPressed();

           }else{
           	 MainFragment ft = (MainFragment) getSupportFragmentManager().findFragmentByTag("MAIN");
           	 
           	 FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
           	 
           	 fragmentTransaction.remove(mContent);
           	 
           	 fragmentTransaction.show(ft);
           	 
           	 fragmentTransaction.commit();
           	 
           	 shownTag = "MAIN";
           	 
             bigText.setText("Home");
           }
    	   
    	   
    };
    

    public void switchContent(Fragment fragment, String tag, int i , String trendingname) {
    	//the fragment changer.
    	
        mContent = fragment;
        
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        
        if (shownTag != null){
        	Fragment shown = getSupportFragmentManager().findFragmentByTag(shownTag);        	
        	fragmentTransaction.hide(shown);
       }
        
        Fragment frm = getSupportFragmentManager().findFragmentByTag(tag);
        
        if (frm == null){
        	
        	Log.d("fragment change", "first use");
	        	
	        	fragmentTransaction.add(R.id.content_frame, mContent, tag).commit();
	       	 shownTag = tag;
	        }else if(tag == "TrendingDetails"){
	       	 	//replace and backtrace
	        	fragmentTransaction.addToBackStack(null);	        
	        	fragmentTransaction.replace(R.id.content_frame, mContent, tag).commit();
	        }else{	        	
	        	fragmentTransaction.show(mContent).commit();   
	           	shownTag = tag;
	        }
        
        
        
        if (tag == "MAIN"){
        	bigText.setText("Home");
        }else if (tag == "MORE"){
        	bigText.setText("More");
        }else if (tag == "TELLFRIENDS"){
        	bigText.setText("Tell your friends");        	
        }else if (tag == "SENDMEME"){
        	bigText.setText("send your meme");
        }else if (tag == "TRENDING"){
        	bigText.setText("Trending");  
        }else if(tag == "TrendingDetails"){}else{}
        
        EasyTracker.getTracker().sendView("/"+shownTag);
        menu.showContent();
    }
}