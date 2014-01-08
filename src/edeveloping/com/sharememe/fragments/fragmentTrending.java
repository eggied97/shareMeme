package edeveloping.com.sharememe.fragments;

import java.util.ArrayList;
import java.util.TreeMap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.google.ads.c;
import com.google.analytics.tracking.android.EasyTracker;

import edeveloping.com.sharememe.FragmentChangeActivity;
import edeveloping.com.sharememe.ImageDownloader;
import edeveloping.com.sharememe.R;
import edeveloping.com.sharememe.R.id;
import edeveloping.com.sharememe.R.layout;
import edeveloping.nl.sharememe.utils.ContextApplication;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class fragmentTrending extends Fragment{
	
	ArrayList<String> TrendingNames, TrendingPics;
	
	GridView trendingGrid;
	
	static String trendingnaam;
	
	private static SharedPreferences mSharedPreferences;
	
	
	 @Override
	    public View onCreateView(LayoutInflater inflater, ViewGroup container,
	                             Bundle savedInstanceState) {
	        return inflater.inflate(R.layout.fragment_trending, container, false);
	    }
		

	    @Override
		public void onActivityCreated(Bundle savedInstanceState) {
		    super.onActivityCreated(savedInstanceState);
		    
		    FragmentChangeActivity.bigText.setText("Trending");
		    
		    trendingGrid = (GridView) getActivity().findViewById(R.id.gridTrending);
		    
		    TrendingNames = new ArrayList<String>();
		    TrendingPics = new ArrayList<String>();
		    
		    mSharedPreferences = getActivity().getApplicationContext().getSharedPreferences("MyPref", 0);
		    
		    new getTrends().execute();
		    
	    }
	    
        private void switchFragment(Fragment fragment, String tag, int i, String yeey) {
            if (getActivity() == null)
                return;

            if (getActivity() instanceof FragmentChangeActivity) {
                FragmentChangeActivity fca = (FragmentChangeActivity) getActivity();
                fca.switchContent(fragment, tag, i,yeey);
            }
        } 



class getTrends extends AsyncTask<String, Void, Boolean> {
	
	boolean state;
    String errorMessage;

    ProgressDialog dialog;

    protected void onPreExecute(){}


    protected Boolean doInBackground(String... stringsForLogin) {
        


        state = false;

        edeveloping.nl.sharememe.utils.UserFunctions uf = new edeveloping.nl.sharememe.utils.UserFunctions();

        JSONObject json = uf.getTrends();
        
        
        try {
           
        	JSONArray jArray = json.getJSONArray("trends");
                    
        	int size = jArray.length();
                    
        	Log.d("SHAREMEME_DEBUG_TRENDS", ""+size);
        	
        	for (int i = 0; i < size; i++){
        		try{
        			JSONObject o = jArray.getJSONObject(i);                    		
        			TrendingNames.add(o.getString("name1"));
        			TrendingPics.add(o.getString("pic1"));             
        		}catch(JSONException ex1){
        			EasyTracker.getTracker().sendException(ex1.getMessage(), ex1, false);
        			Toast.makeText(getActivity().getApplicationContext(), "error : "+ex1.toString(), Toast.LENGTH_SHORT).show();
        		}                    	
        	}
                    
        	for (int i = 0; i < size; i++){
        		try{
        			JSONObject o = jArray.getJSONObject(i);
                    		
        			Log.d("SHAREMEME DEBUG TRENDS", o.getString("name2"));
                    		
        			TrendingNames.add(o.getString("name2"));
        			TrendingPics.add(o.getString("pic2"));          
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
    	}
    	
    	trendingGrid.setAdapter(new TrendingAdapter(getActivity(),TrendingNames, TrendingPics));
    	
    	trendingGrid.setOnItemClickListener(new OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
            	
            	Fragment newContent = null;
            	String tag="";
            	int i = 0;
            	
            	FragmentTrendingDetail ftd = (FragmentTrendingDetail) getActivity().getSupportFragmentManager().findFragmentByTag("TrendingDetails");
            	
            	Editor e = mSharedPreferences.edit();
            	e.putString("TrendingDetailName", TrendingNames.get(position));
            	e.commit();
            	
            	trendingnaam = TrendingNames.get(position);
            	 
            	 if (ftd == null){
            		 ftd = new FragmentTrendingDetail();            	
            		 i = 1;
            	 }else{ i = 2; }
            	 
            	 tag="TrendingDetails";
                 newContent = ftd;
                 Bundle bundle = new Bundle();
                
                 if (trendingnaam == "Shoutout"){
                	 bundle.putInt("trendingName", 1);
                 }else{
                	 bundle.putInt("trendingName", 0);
                 }
                 
                 ftd.setArguments(bundle);
                 
                 if (newContent != null)
                     switchFragment(newContent, tag,i, TrendingNames.get(position));
            }
    	});
    }
}

public class TrendingAdapter extends BaseAdapter {
	 private Context context;
	 private ArrayList<String> trendingNAMES, trendingPICS;
	 
	 public TrendingAdapter (Context mContext, ArrayList<String> names, ArrayList<String> pics){
		 this.trendingNAMES = names;
		 this.trendingPICS = pics;
		 this.context = mContext;
	 }
	 
	 public View getView (int pos, View convertView, ViewGroup parent){
		 LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		 
		 View ViewToImplement;
		 
		 if (convertView == null){
			
			 ViewToImplement = new View(context);
			 
			 ViewToImplement = inflater.inflate(R.layout.trending_item, null);
			 			
			 ImageView ivForPic = (ImageView) ViewToImplement.findViewById(R.id.TrendingItemImageView);
			 
			 ImageDownloader imageDownloader = ImageDownloader.getInstance(context); 				
			 imageDownloader.displayImageWithText(ivForPic, trendingPICS.get(pos),trendingNAMES.get(pos), getActivity().getApplicationContext());	
			 
		 }else{
			 ViewToImplement = (View) convertView;			 
		 }		 
		 return ViewToImplement;
	 }
	 

	@Override
	public int getCount() {
		return trendingNAMES.size();
	}

	@Override
	public Object getItem(int position) {
		return null;
	}

	@Override
	public long getItemId(int position) {
		return 0;
	}
}
}
