package com.iwitness.androidapp.controllers.authenticated.home.fragments;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.iwitness.androidapp.AppDelegate;
import com.iwitness.androidapp.R;
import com.iwitness.androidapp.controllers.authenticated.home.NotificationListContentController;
import com.iwitness.androidapp.controllers.common.listeners.UpdateMenuPosListener;
import com.iwitness.androidapp.libraries.utils.SharedPrefUtils;
import com.iwitness.androidapp.model.NotificationsObj;
import com.iwitness.androidapp.model.UserPreferences;

import java.util.ArrayList;
import java.util.HashMap;

public class NotificationsListFragment extends Fragment implements View.OnClickListener {
    TextView tv_point1, tv_point2, tv_point3;
    ImageView iv_point2, iv_point3, iv_point1;
    RelativeLayout llBaseMid_Videos;
    ArrayList<NotificationsObj> arrayNames;
    String []  notificationNames = {
            AppDelegate.getAppContext().getResources().getString(R.string.optimize),
            AppDelegate.getAppContext().getResources().getString(R.string.tip),
            AppDelegate.getAppContext().getResources().getString(R.string.disclimer)};
    UpdateMenuPosListener listener;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_notifications, container, false);
    }
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setHasOptionsMenu(true);
        listener = (UpdateMenuPosListener) getActivity();
        listener.updateMenuPos(2);
        llBaseMid_Videos =(RelativeLayout)getView().findViewById(R.id.llBaseMid_Videos);
        tv_point1 = (TextView) getView().findViewById(R.id.tv_point1);
        tv_point2 = (TextView) getView().findViewById(R.id.tv_point2);
        tv_point3 = (TextView) getView().findViewById(R.id.tv_point3);

        iv_point1 = (ImageView) getView().findViewById(R.id.iv_point1);
        iv_point2 = (ImageView) getView().findViewById(R.id.iv_point2);
        iv_point3 = (ImageView) getView().findViewById(R.id.iv_point3);
    }

/*    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.clear();
        inflater.inflate(R.menu.home, menu);
        MenuItem item = menu.getItem(0);
        UserPreferences userPreferences = UserPreferences.sharedInstance();
        item.setTitle(String.format(getString(R.string.call_now) + " " + userPreferences.getEmergencyNumber()));
        super.onCreateOptionsMenu(menu, inflater);
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return false;
    }*/
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_point1: {
                NotificationsObj objisReadUnread = (NotificationsObj) v.getTag();
                String isReadUnread = null;
                if(objisReadUnread.readUnread.equalsIgnoreCase("false"))
                {
                    isReadUnread ="true";
                }
                Intent intent = new Intent(getActivity(), NotificationListContentController.class);
                intent.putExtra("page", tv_point1.getText().toString());
                intent.putExtra("pageNo", 1);
                intent.putExtra("isReadUnread", isReadUnread);
                intent.putExtra("NotificationsObj", objisReadUnread);
                startActivity(intent);
                getActivity().overridePendingTransition(R.anim.enter_slide_to_left, R.anim.exit_slide_to_left);
                break;
            }
            case R.id.tv_point2: {
                NotificationsObj objisReadUnread = (NotificationsObj) v.getTag();
                String isReadUnread = null;
                if(objisReadUnread.readUnread.equalsIgnoreCase("false"))
                {
                    isReadUnread ="true";
                }
                Intent intent = new Intent(getActivity(), NotificationListContentController.class);
                intent.putExtra("page", tv_point2.getText().toString());
                intent.putExtra("pageNo", 2);
                intent.putExtra("isReadUnread", isReadUnread);
                intent.putExtra("NotificationsObj", objisReadUnread);
                startActivity(intent);
                getActivity().overridePendingTransition(R.anim.enter_slide_to_left, R.anim.exit_slide_to_left);
                break;

            }
            case R.id.tv_point3: {
                NotificationsObj objisReadUnread = (NotificationsObj) v.getTag();
                String isReadUnread = null;
                if(objisReadUnread.readUnread.equalsIgnoreCase("false"))
                {
                    isReadUnread ="true";
                }
                Intent intent = new Intent(getActivity(), NotificationListContentController.class);
                intent.putExtra("page", tv_point3.getText().toString());
                intent.putExtra("pageNo", 3);
                intent.putExtra("isReadUnread", isReadUnread);
                intent.putExtra("NotificationsObj", objisReadUnread);
                startActivity(intent);
                getActivity().overridePendingTransition(R.anim.enter_slide_to_left, R.anim.exit_slide_to_left);
                break;

            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        arrayNames = new ArrayList<>();
        try{
            ArrayList<String> arrayListNames = null;
            HashMap<String,String> hashNotifications = null;
            arrayListNames = SharedPrefUtils.getVectorVales(SharedPrefUtils.IWITNESS_PREF);
            hashNotifications = SharedPrefUtils.getHashmapValues(SharedPrefUtils.IWITNESS_PREF);

            if(arrayListNames!=null && arrayListNames.size()>0)
            {
                for(int i=0;i<arrayListNames.size();i++){
                    String value = hashNotifications.get(arrayListNames.get(i));
                    if(value.equalsIgnoreCase("false"))
                    {
                        NotificationsObj obj = new NotificationsObj();
                        obj.notificationName = arrayListNames.get(i);
                        obj.imgId = 1;
                        obj.readUnread ="false";
                        arrayNames.add(obj);
                    }
                }
                for(int i=0;i<arrayListNames.size();i++){
                    String value =  hashNotifications.get(arrayListNames.get(i));
                    if(value.equalsIgnoreCase("true"))
                    {
                        NotificationsObj obj = new NotificationsObj();
                        obj.notificationName = arrayListNames.get(i);
                        obj.imgId = 0;
                        obj.readUnread ="true";
                        arrayNames.add(obj);
                    }
                }
            }
        }
        catch (Exception e)
        {
            for(int i=0;i<notificationNames.length;i++)
            {
                NotificationsObj obj = new NotificationsObj();
                obj.notificationName =  notificationNames[i];
                obj.imgId =0;
                obj.readUnread ="false";
                arrayNames.add(obj);
            }

        }

        for(int i=0;i<arrayNames.size();i++)
        {
            Log.e("arrayNames...", "value" + arrayNames.get(i));
            if(i==0) {
                tv_point1.setText(arrayNames.get(i).notificationName);

                if(arrayNames.get(i).imgId ==1)
                    iv_point1.setImageResource(R.drawable.unread_notification);
                else
                    iv_point1.setImageResource(R.drawable.read_notification);
            }
            else if(i==1){
                tv_point2.setText(arrayNames.get(i).notificationName);

                if(arrayNames.get(i).imgId ==1)
                    iv_point2.setImageResource(R.drawable.unread_notification);
                else
                    iv_point2.setImageResource(R.drawable.read_notification);

            }
            else {
                tv_point3.setText(arrayNames.get(i).notificationName);

                if(arrayNames.get(i).imgId ==1)
                    iv_point3.setImageResource(R.drawable.unread_notification);
                else
                    iv_point3.setImageResource(R.drawable.read_notification);
            }
        }

        tv_point1.setTag(arrayNames.get(0));
        tv_point1.setOnClickListener(this);

        tv_point2.setTag(arrayNames.get(1));
        tv_point2.setOnClickListener(this);

        tv_point3.setTag(arrayNames.get(2));
        tv_point3.setOnClickListener(this);

    }

    @Override
    public void onPause() {
        super.onPause();
    }

}
