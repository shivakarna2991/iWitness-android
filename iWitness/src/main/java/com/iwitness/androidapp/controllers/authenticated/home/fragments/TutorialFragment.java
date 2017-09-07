package com.iwitness.androidapp.controllers.authenticated.home.fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.*;
import com.iwitness.androidapp.R;
import com.iwitness.androidapp.controllers.adapters.TutorialPagerAdapter;
import com.iwitness.androidapp.model.UserPreferences;
import com.viewpagerindicator.UnderlinePageIndicator;

public class TutorialFragment extends Fragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_tutorial, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        // Locate the ViewPager in viewpager_main.xml
        ViewPager viewPager = (ViewPager) getView().findViewById(R.id.pager);
        // Pass results to ViewPagerAdapter Class
        PagerAdapter adapter = new TutorialPagerAdapter(getActivity());
        // Binds the Adapter to the ViewPager
        viewPager.setAdapter(adapter);

        // ViewPager Indicator
        UnderlinePageIndicator mIndicator = (UnderlinePageIndicator) getView().findViewById(R.id.indicator);
        mIndicator.setFades(false);
        mIndicator.setViewPager(viewPager);

        UserPreferences userPreferences = UserPreferences.sharedInstance();
        userPreferences.setFirstRegistered(false);
        userPreferences.setFirstLogin(false);
        userPreferences.save();
    }
}
