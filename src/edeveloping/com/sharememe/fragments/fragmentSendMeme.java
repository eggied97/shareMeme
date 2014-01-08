package edeveloping.com.sharememe.fragments;

import edeveloping.com.sharememe.FragmentChangeActivity;
import edeveloping.com.sharememe.R;
import edeveloping.com.sharememe.R.id;
import edeveloping.com.sharememe.R.layout;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

public class fragmentSendMeme extends Fragment{
	
	ImageView okButton;
	
	 @Override
	    public View onCreateView(LayoutInflater inflater, ViewGroup container,
	                             Bundle savedInstanceState) {
		 return inflater.inflate(R.layout.fragment_send_meme, container, false);
	    }
		

	    @Override
		public void onActivityCreated(Bundle savedInstanceState) {
		    super.onActivityCreated(savedInstanceState);
		    
		    FragmentChangeActivity.bigText.setText("Send Meme");
		    
		    okButton  = (ImageView) getActivity().findViewById(R.id.ivSendMemeOkButton);
		    
		    okButton.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					
					Intent i = new Intent(Intent.ACTION_SEND);
					i.setType("message/rfc822");
					i.putExtra(Intent.EXTRA_EMAIL  , new String[]{"sharememedroid@gmail.com"});
					i.putExtra(Intent.EXTRA_TEXT, "Your twitter name : \n\n @   \n\n Please, paste/send your meme with this mail.");
					i.putExtra(Intent.EXTRA_SUBJECT, "ShareMeme SendMeme");

					try {
					    startActivity(Intent.createChooser(i, "Select your email client..."));
					} catch (android.content.ActivityNotFoundException ex) {
					    Toast.makeText(getActivity(), "There are no email clients installed.", Toast.LENGTH_SHORT).show();
					}
					
				}
			});
	    }
}
