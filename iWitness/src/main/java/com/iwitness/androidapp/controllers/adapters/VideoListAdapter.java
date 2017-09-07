//  Project name: Workspace
//  File name   : VideoListAdapter.java
//
//  Author      : Phuc
//  Created date: 7/1/14
//  Version     : 1.00
//  --------------------------------------------------------------
//  Copyright (C) 2014 WebOnyx. All rights reserved.
//  --------------------------------------------------------------

package com.iwitness.androidapp.controllers.adapters;


import android.content.Context;
import android.graphics.Bitmap;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.iwitness.androidapp.R;
import com.iwitness.androidapp.libraries.utils.FontUtils;
import com.iwitness.androidapp.model.UserPreferences;
import com.perpcast.lib.foundation.FwiJson;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;


public class VideoListAdapter extends ArrayAdapter<FwiJson> {

    private View.OnClickListener listener;
    private ArrayList<FwiJson> videos;
    private String TAG ="VideoListAdapter";
    private static String result = null;
    Context context;
    // Class's constructors
    public VideoListAdapter(Context context, View.OnClickListener listener, ArrayList<FwiJson> videos) {
        super(context, R.layout.cell_video, videos);
        this.listener = listener;
        this.videos = videos;
        this.context = context;
    }


    // Class's public methods
    @Override
    public View getView(int position, View view, ViewGroup parent) {
        View convertView;
        if (view == null) {
            LayoutInflater inflater = (LayoutInflater) super.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.cell_video, parent, false);
        } else {
            convertView = view;
        }

        final FwiJson videoInfo = super.getItem(position);

        ImageView imvPhoto = (ImageView) convertView.findViewById(R.id.imvPhoto);
        TextView lblCreated  = (TextView) convertView.findViewById(R.id.lblCreated);
        TextView tv_duration  = (TextView) convertView.findViewById(R.id.tv_duration);
        TextView tv_TIME  = (TextView) convertView.findViewById(R.id.tv_TIME);
        final TextView tv_location  = (TextView) convertView.findViewById(R.id.tv_location);


        TextView tvArrowRight = (TextView) convertView.findViewById(R.id.tvArrowRight);
        TextView tvMap = (TextView) convertView.findViewById(R.id.tvMap);


        tvArrowRight.setTypeface(FontUtils.getFontAwesome());
        tvArrowRight.setText("\uf105");
//      Log.d("videoInfo..", "TEST..." + videoInfo.toString());

        //update image
        final String photoUrl = videoInfo.jsonWithPath("imageUrl").getString();
        Picasso.with(imvPhoto.getContext().getApplicationContext())
                .load(photoUrl)
                .memoryPolicy(MemoryPolicy.NO_CACHE, MemoryPolicy.NO_STORE)
                .placeholder(R.drawable.no_video)
                .config(Bitmap.Config.RGB_565)
                .into(imvPhoto);

        String duration = videoInfo.jsonWithPath("duration").getString();
        String[] arrayDuration = duration.split(":");
        String part1 = arrayDuration[1];
        String part2 = arrayDuration[2];
        tv_duration.setText(part1 +":" +part2);
        double initialLat = 0,initialLong = 0;
        if(videoInfo.jsonWithPath("initialLat").toString()!=null) {
                    initialLat = Double.parseDouble(videoInfo.jsonWithPath("initialLat").toString());
                }
                if(videoInfo.jsonWithPath("initialLong").toString()!=null) {
                    initialLong = Double.parseDouble(videoInfo.jsonWithPath("initialLong").toString());
                }
//       Log.d("values..",initialLat +" :" +initialLong);
        new GetAddressTask(context,initialLat,initialLong,tv_location).execute();

        // Define created day
        UserPreferences userPreferences = UserPreferences.sharedInstance();

        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone(userPreferences.getTimeZone()));
        calendar.setTimeInMillis(videoInfo.jsonWithPath("created").getInteger().longValue() * 1000);

        SimpleDateFormat format = new SimpleDateFormat("MM/dd/yyyy - hh:mm a");
        String dateTime = format.format(calendar.getTime());
        String[] DateTimeArray = dateTime.split("-");
        String Date = DateTimeArray[0];
        String Time = DateTimeArray[1];
        lblCreated.setText(Date);
        tv_TIME.setText(Time);

        Button btnDelete = (Button) convertView.findViewById(R.id.btnDelete);
        if (btnDelete != null) {
            btnDelete.setTag(position);
            btnDelete.setOnClickListener(this.listener);
            btnDelete.setVisibility(View.GONE);
        }

        return convertView;
    }

    public void addAll(FwiJson events) {
        videos.addAll(events.toArray());
        this.notifyDataSetChanged();
    }

    public void reset() {
        videos.clear();
        this.notifyDataSetChanged();
    }

    public void removeAt(int pos) {
        videos.remove(pos);
        this.notifyDataSetChanged();
    }

    public class GetAddressTask extends AsyncTask<Location, Void, String> {
        private Context mContext;
        private Double mLatitude;
        private Double mLongtitude;
        private TextView tv_location;
        public GetAddressTask (Context context,Double lat,Double longitude,TextView tv_loca) {
            super();
            mContext = context;
            mLatitude = lat;
            mLongtitude = longitude;
            tv_location = tv_loca;
        }

        @Override
        protected String doInBackground (android.location.Location... params) {
            Geocoder geocoder =
                    new Geocoder(mContext, Locale.getDefault());
            List<Address> addresses = null;
            try {

                addresses = geocoder.getFromLocation(mLatitude, mLongtitude, 1);
            } catch (IOException exception) {
                Log.e("ComplaintLocation",
                        "IO Exception in getFromLocation()", exception);

                return ("IO Exception trying to get address");
            } catch (IllegalArgumentException exception) {
                String errorString = "Illegal arguments " +
                        Double.toString(mLatitude) + " , " +
                        Double.toString(mLongtitude) + " passed to address service";
                Log.e("LocationSampleActivity", errorString, exception);

                return errorString;
            }

            if (addresses != null && addresses.size() > 0) {
                Address address = addresses.get(0);
//              Log.d("address.", "TEST.." +address.toString());
                if (address.getMaxAddressLineIndex() > 0) {
                    return String.format(
                            "%s,%s",
                            address.getThoroughfare(), // 2
                            address.getLocality()); // 5
                } else {
                    return String.format(
                            "%s",
                            address.getLocality()); // 3
                }
            } else return "No address found";
        }

        // Format address string after lookup
        @Override
        protected void onPostExecute (String address) {
            tv_location.setText(" "+address.toString());
    }
  }
}
