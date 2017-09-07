
package com.iwitness.androidapp.controllers.adapters;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;

import com.iwitness.androidapp.R;

import java.io.IOException;
import java.io.InputStream;

public class TutorialPagerAdapter extends PagerAdapter {
    // Declare Variables
    Context _context;
    LayoutInflater _inflater;

    String[] _pages;

    public TutorialPagerAdapter(Context context) {
        this._context = context;
        _pages = new String[]{
                "page01.html",
                "page02.html",
                "page03.html",
                "page04.html",
                "page05.html",
                "page06.html",
                "page07.html",
                "page08.html",
                "page09.html",
                "page10.html",
                "page11.html"
        };
    }

    @Override
    public int getCount() {
        return _pages.length;
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {

        _inflater = (LayoutInflater) _context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View itemView = _inflater.inflate(R.layout.page_tutorial, container,
                false);

        WebView webView = (WebView) itemView.findViewById(R.id.tutorialPage);
        if (webView != null) {
            InputStream input;
            try {
                input = _context.getAssets().open(_pages[position]);
                int size = input.available();
                byte[] buffer = new byte[size];
                input.read(buffer);
                input.close();
                String text = new String(buffer);
                webView.loadData(text, "text/html", null);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        container.addView(itemView);
        return itemView;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        // Remove viewpager_item.xml from ViewPager
        container.removeView((ViewGroup) object);
    }
}
