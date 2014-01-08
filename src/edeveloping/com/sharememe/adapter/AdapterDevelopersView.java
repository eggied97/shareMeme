package edeveloping.com.sharememe.adapter;

import java.util.List;

import android.graphics.Color;
import android.os.Parcelable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class AdapterDevelopersView extends FragmentPagerAdapter {
 
    public AdapterDevelopersView(FragmentManager fm) {
		super(fm);
	}

	private List<Fragment> fragments;

    public AdapterDevelopersView(FragmentManager fm, List<Fragment> fragments) {
        super(fm);
        this.fragments = fragments;
    }
   
    @Override
    public Fragment getItem(int position) {
        return this.fragments.get(position);
    } 
   
    @Override
    public int getCount() {
        return this.fragments.size();
    }
}