package com.iwitness.androidapp.controllers.common;

import android.animation.ArgbEvaluator;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.design.widget.CoordinatorLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.iwitness.androidapp.AppDelegate;
import com.iwitness.androidapp.R;
import com.iwitness.androidapp.controllers.SplashscreenController;
import com.iwitness.androidapp.controllers.adapters.SectionsPagerAdapter;
import com.iwitness.androidapp.controllers.authentication.LoginController;
import com.iwitness.androidapp.libraries.utils.FontUtils;
import com.iwitness.androidapp.libraries.utils.SharedPrefUtils;
import com.iwitness.androidapp.model.UserPreferences;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnPageChange;

public class OnBoardingScreensActivity extends AppCompatActivity {


    private SectionsPagerAdapter mSectionsPagerAdapter;

    @BindView(R.id.container)
    ViewPager mViewPager;

    @BindView(R.id.btnNext)
    Button mButtonNext;

    @BindView(R.id.btnLogin)
    Button mButtonLogin;

    int page = 0;   //  to track page position
    SharedPreferences mSharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_on_boarding_screens);

        ButterKnife.bind(this);

        init();

        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                page = position;
            }

            @Override
            public void onPageSelected(int position) {
              
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    private void init() {
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());
        mViewPager.setAdapter(mSectionsPagerAdapter);
        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        mButtonNext.setTypeface(FontUtils.getFontFabricGloberBold());
        mButtonLogin.setTypeface(FontUtils.getFontFabricGloberBold());
    }

    @OnClick(R.id.btnNext)
    void onClickOfNext() {
        page += 1;
        mViewPager.setCurrentItem(page, true);
        if (page == mSectionsPagerAdapter.getCount()) {
            onClickOfLoginButton();
        }
    }

    @OnClick(R.id.btnLogin)
    void onClickOfLoginButton() {

        Intent intent = new Intent(OnBoardingScreensActivity.this, LoginController.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        overridePendingTransition(R.anim.enter_slide_to_left, R.anim.exit_slide_to_left);

        mSharedPreferences.edit().putBoolean("isOnBoardingScreensChecked", true).apply();

        finish();
    }


    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";

        ImageView img;

        int[] bgs = new int[]{R.drawable.demo1, R.drawable.demo2, R.drawable.demo3, R.drawable.demo4};

        public PlaceholderFragment() {
        }

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static PlaceholderFragment newInstance(int sectionNumber) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

            View rootView = inflater.inflate(R.layout.fragment_pager, container, false);
            img = (ImageView) rootView.findViewById(R.id.section_img);
            img.setBackgroundResource(bgs[getArguments().getInt(ARG_SECTION_NUMBER) - 1]);

            return rootView;
        }
    }


}
