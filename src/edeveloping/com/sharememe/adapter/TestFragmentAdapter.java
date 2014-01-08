package edeveloping.com.sharememe.adapter;

import java.util.ArrayList;

import com.nostra13.universalimageloader.core.ImageLoader;

import edeveloping.com.sharememe.fragments.TestFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

public class TestFragmentAdapter extends android.support.v4.app.FragmentStatePagerAdapter {
	ArrayList<String> urlList;
	static ImageLoader imageLoader;
	
	public TestFragmentAdapter(FragmentManager fragmentManager, ArrayList<String> urls) {
		super(fragmentManager);
		urlList = new ArrayList<String>();
		urlList = urls;
	}

	@Override
	public Fragment getItem(int position) {
		String urlToLoad = urlList.get(position);
		Log.d("ShareMEME_DEBUG", "making new viewpager item at : "+position);
		return TestFragment.newInstance(urlToLoad, ImageLoader.getInstance(),position, urlList);
	}

	@Override
	public int getCount() {
		if (urlList.size() < 1){
			return 1;			
		}else{
			return urlList.size();
		}		
	}	
}