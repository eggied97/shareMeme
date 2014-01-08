package edeveloping.com.sharememe.fragments;

import edeveloping.com.sharememe.FragmentChangeActivity;
import edeveloping.com.sharememe.R;
import edeveloping.com.sharememe.R.layout;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class FragmentTellYourFriends extends Fragment{
	
	 @Override
	    public View onCreateView(LayoutInflater inflater, ViewGroup container,
	                             Bundle savedInstanceState) {
		 return inflater.inflate(R.layout.fragment_tell_your_friends, container, false);
	    }
		

	    @Override
		public void onActivityCreated(Bundle savedInstanceState) {
		    super.onActivityCreated(savedInstanceState);
		    
		    FragmentChangeActivity.bigText.setText("Tell your friends");
		    
	    }

}
