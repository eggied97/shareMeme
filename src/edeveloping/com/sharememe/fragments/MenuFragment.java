package edeveloping.com.sharememe.fragments;


import java.util.ArrayList;

import com.actionbarsherlock.app.SherlockFragment;
import com.actionbarsherlock.internal.view.menu.ListMenuItemView;

import edeveloping.com.sharememe.FragmentChangeActivity;
import edeveloping.com.sharememe.R;
import edeveloping.com.sharememe.R.id;
import edeveloping.com.sharememe.R.layout;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.ListFragment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.LinearLayout.LayoutParams;

public class MenuFragment extends android.support.v4.app.Fragment{
	
	  private ListAdaptor adapter;
	  
	  MainFragment MainF;
	  
	  private OnItemSelectedListener listener;
	
	  @Override
	  public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		  
	    View view = inflater.inflate(R.layout.layout_menu,  container, false);
	    
	    ArrayList<String> listItems = new ArrayList<String>();
        
        listItems.add("Home");
        listItems.add("Trending");
        listItems.add("More");
        listItems.add("Send a Meme");
	    
        MainF = new MainFragment();
	    
	    ListView menuListView = (ListView) view.findViewById(R.id.listViewMenu);
	    
	    ListAdaptor adapter = new ListAdaptor(getActivity(),1, listItems);
	    menuListView.setAdapter(adapter);
	    
	    menuListView.setOnItemClickListener(new OnItemClickListener() {

            public void onItemClick(AdapterView<?> parent, View view, int position,long id) {
            	listener.OnItemSelected(position);
            }
        }); 	    
	    
	    return view;
	  }
	  
	  
	  public interface OnItemSelectedListener {
		    public void OnItemSelected(int pos);
		  }
	  
	 
	  
	  @Override
	    public void onActivityCreated(Bundle savedInstanceState) {
	        super.onActivityCreated(savedInstanceState);
	  }
	  
	  
	  private class ListAdaptor extends ArrayAdapter<String> {
	        private ArrayList<String> ListItems;

	        public ListAdaptor(Context context, int textViewResourceId, ArrayList<String> items) {
	            super(context, textViewResourceId, items);
	            this.ListItems = items;
	        }


	        @Override
	        public View getView(int position, View convertView, ViewGroup parent) {
	            View v = convertView;

	            final int pos = position;

	            if (v == null) {
	                v = ViewGroup.inflate(getContext(), R.layout.menu_row_layout, null);
	            }
	            

	            TextView text1 = (TextView) v.findViewById(R.id.text1);
	            Typeface tf = Typeface.createFromAsset(getActivity().getAssets(),
	                    "fonts/Roboto-Regular.ttf");	            
	            text1.setTypeface(tf);
	            
	            text1.setText(ListItems.get(position));
	          
	            v.setTag(pos);

	            v.setOnClickListener(new View.OnClickListener() {
	                @Override
	                public void onClick(View arg0) {
	                	 Fragment newContent = null;
	                	 String tag="";
	                	 int i = 0;
	                	 
	                     switch ((Integer)arg0.getTag()) {
	                     case 0:	                    	 
	                    	 MainFragment mf = (MainFragment) getActivity().getSupportFragmentManager().findFragmentByTag("MAIN");
	                    	 
	                    	 if (mf == null){
	                    		 mf = new MainFragment();
	                    		 i = 1;
	                    	 }else{ i = 2; }
	                    	 tag="MAIN";
	                         newContent = mf;
	                         break;
	                         
	                     case 1:	                    	
	                    	 fragmentTrending ft = (fragmentTrending) getActivity().getSupportFragmentManager().findFragmentByTag("TRENDING");
	                    	 
	                    	 if (ft == null){
	                    		 ft = new fragmentTrending();
	                    		 i = 1;
	                    	 }else{ i = 2; }
	                    	 
	                         newContent = ft;
	                         tag="TRENDING";
	                         break;
	                         
	                     case 2:
	                    	
	                    	 ActivityMore am = (ActivityMore) getActivity().getSupportFragmentManager().findFragmentByTag("MORE");
	                    	 
	                    	 if (am == null){
	                    		 am = new ActivityMore();
	                    		 i = 1;
	                    	 }else{ i = 2; }	                    	 
	                    	 
	                         newContent = am;
	                         tag="MORE";
	                         break;
	                         
	                     case 3:
	                    	 
	                    	 fragmentSendMeme fsm = (fragmentSendMeme) getActivity().getSupportFragmentManager().findFragmentByTag("SENDMEME");
	                    	 
	                    	 if (fsm == null){
	                    		 fsm = new fragmentSendMeme();
	                    		 i = 1;
	                    	 }else{ i = 2; }
	                    	 
	                         newContent = fsm;
	                         tag="SENDMEME";
	                         break;
	                         
	                     case 4:
	                    	 FragmentTellYourFriends ftyf = (FragmentTellYourFriends) getActivity().getSupportFragmentManager().findFragmentByTag("TELLFRIENDS");
	                    	 
	                    	 if (ftyf == null){
	                    		 ftyf = new FragmentTellYourFriends();
	                    		 i = 1;
	                    	 }else{ i = 2; }
	                    	 
	                         newContent = ftyf ;
	                         tag="TELLFRIENDS";
	                         break;
	                     }
	                     
	                     if (newContent != null)
	                         switchFragment(newContent, tag,i);
	                 }
	            });
	            return v;
	        }
            private void switchFragment(Fragment fragment, String tag, int i) {
                if (getActivity() == null)
                    return;

                if (getActivity() instanceof FragmentChangeActivity) {
                    FragmentChangeActivity fca = (FragmentChangeActivity) getActivity();
                    fca.switchContent(fragment, tag, i,"");
                } 
            }
	    }
}
