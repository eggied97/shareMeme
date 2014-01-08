package edeveloping.com.sharememe;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.ImageLoadingListener;

import edeveloping.com.sharememe.R;
import edeveloping.com.sharememe.fragments.MainFragment;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.GetChars;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

public class TestFragmentForTrending extends Fragment {
	private static final String KEY_CONTENT = "TestFragment:Content";
	
	static int pos;
	
	int height,width;
	static ArrayList<String> urlListMemes;
	static String urlToload;
	static ImageLoader IL;
	 private ImageDownloader imageDownloader;
	 ProgressBar spinner;
	 ImageView IV;
	
	
	public static TestFragmentForTrending newInstance(String urlTooooLoad, ImageLoader imageLoader,int POSITION, ArrayList<String> al) {
		
		
		urlToload = urlTooooLoad;
		pos = POSITION;
		urlListMemes = al;
		
		Bundle bdl = new Bundle(1);
		bdl.putString("DOWNLOAD URL", urlToload);
		
		IL = imageLoader;
		
		TestFragmentForTrending fragment = new TestFragmentForTrending();
		fragment.setArguments(bdl);
		
		return fragment;
	}
	
	private String mContent = "???";
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		if ((savedInstanceState != null) && savedInstanceState.containsKey(KEY_CONTENT)) {
			mContent = savedInstanceState.getString(KEY_CONTENT);
		}
		
		IV = new ImageView(getActivity());
		LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
		IV.setLayoutParams(params);
				
		LinearLayout layout = new LinearLayout(getActivity());
		layout.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
		layout.setGravity(Gravity.CENTER);
		
		 width = container.getWidth() - 30;
		 height =  container.getHeight() - 100;
		
		IV.setMinimumWidth(width);
		IV.setMinimumHeight(height);
		
		spinner = new ProgressBar(getActivity());
		LayoutParams paramsForSpinner = new LayoutParams(75, 75);
		paramsForSpinner.gravity = Gravity.CENTER;
		spinner.setLayoutParams(paramsForSpinner);
		
		spinner.setVisibility(View.VISIBLE);
		IV.setVisibility(View.INVISIBLE);
		
		layout.addView(spinner);
		layout.addView(IV);
		
		int curPos = MainFragment.pager.getCurrentItem();		
		
		String image_url = urlListMemes.get(pos);		

		Context mContext = layout.getContext();
		
		String lalala = getArguments().getString("DOWNLOAD URL");
		
		
		imageDownloader = ImageDownloader.getInstance(FragmentChangeActivity.mContext); 
		 imageDownloader.displayImageWithCallback(IV, lalala, new ImageLoadingListener() {

			@Override
			public void onLoadingCancelled(String arg0, View arg1) {}

			@Override
			public void onLoadingComplete(String arg0, View arg1, Bitmap arg2) {				
				spinner.setVisibility(View.GONE);
				IV.setVisibility(View.VISIBLE);
				IV.setImageBitmap(arg2);
			}

			@Override
			public void onLoadingFailed(String arg0, View arg1, FailReason arg2) {
				spinner.setVisibility(View.GONE);
				Toast.makeText(arg1.getContext(), "An error occurd ; "+arg2.toString(), Toast.LENGTH_SHORT).show();
			}

			@Override
			public void onLoadingStarted(String arg0, View arg1) {				
				spinner.setVisibility(View.VISIBLE);				
			}
         });
		
		Log.d("ShareMEME_DEBUG", lalala);

		return layout;
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putString(KEY_CONTENT, mContent);
	}

}
