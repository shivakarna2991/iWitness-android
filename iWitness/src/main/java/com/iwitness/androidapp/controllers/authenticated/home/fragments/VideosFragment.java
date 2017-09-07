package com.iwitness.androidapp.controllers.authenticated.home.fragments;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.iwitness.androidapp.AppDelegate;
import com.iwitness.androidapp.Configuration;
import com.iwitness.androidapp.R;
import com.iwitness.androidapp.controllers.adapters.VideoListAdapter;
import com.iwitness.androidapp.controllers.authenticated.StreamVideoController;
import com.iwitness.androidapp.controllers.common.listeners.UpdateMenuPosListener;
import com.iwitness.androidapp.libraries.utils.RequestUtils;
import com.iwitness.androidapp.libraries.widgets.VideoListView;
import com.iwitness.androidapp.model.UserPreferences;
import com.iwitness.androidapp.network.ForegroundTask;
import com.iwitness.androidapp.network.TaskDelegate;
import com.perpcast.lib.FwiFoundation;
import com.perpcast.lib.foundation.FwiJson;
import com.perpcast.lib.services.request.FwiRequest;
import com.perpcast.lib.utils.FwiIdUtils;

import org.apache.http.HttpStatus;

import java.util.ArrayList;
import java.util.UUID;

public class VideosFragment extends SwiftLeftDeleteFragment
        implements View.OnClickListener,
        Animation.AnimationListener,
        TaskDelegate,
        VideoListView.EndlessListener {

    private int currentPage = 1;
    private boolean gettingEvents = false;
    private VideoListView lsVideos = null;
    private ProgressBar progressBar = null;
    private UUID loadVideosTaskId, deleteVideoTaskId;
    private boolean isShownPopup = false;

    private VideoListAdapter videoListAdapter = null;
    private TextView emptyView = null;
    private int deletedPos;
    private ForegroundTask task;
    UpdateMenuPosListener listener;
    String fromScreen;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_videos, container, false);
    }
    public void setOnEventListener(UpdateMenuPosListener listener) {
        listener = listener;
    }
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        setHasOptionsMenu(true);

        try {
            listener = (UpdateMenuPosListener) getActivity();
            listener.updateMenuPos(4);
        }
        catch (Exception e){

        }
        videoListAdapter = new VideoListAdapter(getActivity(), this, new ArrayList<FwiJson>());
        emptyView = (TextView) getView().findViewById(R.id.tvEmptyVideoList);
        progressBar = (ProgressBar) getView().findViewById(R.id.progress);

        // Initialize video list
        lsVideos = (VideoListView) getView().findViewById(R.id.lsVideoList);
        lsVideos.setLoadingView(R.layout.view_loading);
        lsVideos.setListener(this);
        lsVideos.setAdapter(videoListAdapter);

        if(AppDelegate.isNetworkAvailable(getActivity())) {
            progressBar.setVisibility(View.VISIBLE);
            lsVideos.requestData();
        }
        else{
            progressBar.setVisibility(View.GONE);
            showAlert("Warning","Could not load events at the moment.");
        }

        super.onActivityCreated(savedInstanceState);
    }

    // TaskDelegate's members
    @Override
    public void taskDidFinish(UUID taskId, int statusCode, Object response) {
        if(statusCode == -1){

        }else {
            gettingEvents = false;
            progressBar.setVisibility(View.GONE); //only show loading for 1st time
            lsVideos.removeLoadingView();
            FwiJson jsonRes = (FwiJson) response;

            if (taskId == loadVideosTaskId) {
                handleLoadVideosComplete(statusCode, response, jsonRes);
            } else if (taskId == deleteVideoTaskId) {
                handleDeleteVideoComplete(statusCode, response, jsonRes);
            }
        }
    }

    private void handleDeleteVideoComplete(int statusCode, Object response, FwiJson jsonRes) {
        if (statusCode == HttpStatus.SC_OK || statusCode == HttpStatus.SC_CREATED || statusCode == HttpStatus.SC_NO_CONTENT) {
            videoListAdapter.removeAt(deletedPos);
            videoListAdapter.notifyDataSetChanged();
        }
    }

    private void handleLoadVideosComplete(int statusCode, Object response, FwiJson jsonRes) {
        boolean isSuccess = false;
        if (response != null) {
            if (statusCode == HttpStatus.SC_OK || statusCode == HttpStatus.SC_CREATED) {
                FwiJson events = jsonRes.jsonWithPath("_embedded/event");
                lsVideos.addNewData(events);
                isSuccess = true;
            } else if (statusCode == HttpStatus.SC_CONFLICT) {
                String detail = jsonRes.jsonWithPath("detail").getString();
                if (detail.toLowerCase().contains("invalid page provided")) {
                    isSuccess = true;
                }
            }
        }

        if (isSuccess) {
            currentPage += 1;
        }

        if (videoListAdapter.getCount() <= 0) {
            //lsVideos.setEmptyView(emptyView);
        }
    }

    @Override
    public void onDestroy() {
        if (task != null) {
            task.cancel(true);
        }
        super.onDestroy();
    }

    @Override
    public void loadData() {
        if(AppDelegate.isNetworkAvailable(getActivity())){
            if (!gettingEvents) {
                gettingEvents = true;
                int visibleThreshold = 5;
                reverseSwipe();
                FwiRequest request = RequestUtils.generateHttpRequest(
                        FwiFoundation.FwiHttpMethod.kGet,
                        Configuration.kService_User_Event_Paging,
                        Configuration.kHostname, UserPreferences.sharedInstance().currentProfileId(),
                        Integer.toString(currentPage),
                        Integer.toString(visibleThreshold)
                );

                loadVideosTaskId = FwiIdUtils.generateUUID();
                task = new ForegroundTask(getActivity(), false, request, loadVideosTaskId);
                task.run(this);
            }
        }
       else{
            showAlert("Warning","Could not load events at the moment.");
        }
    }

    @Override
    public void onClick(final View view) {
        switch (view.getId()) {
            case R.id.tvMap: {
                break;
            }
            case R.id.btnDelete: { //delete video
                if (!isShownPopup) {
                    isShownPopup = true;
                    final VideosFragment weakThis = this;
                    AlertDialog dialog = new AlertDialog.Builder(getActivity())
                            .setTitle(getActivity().getString(R.string.confirm_delete_event_title))
                            .setMessage(getActivity().getString(R.string.confirm_delete_event_message))
                            .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int whichButton) {
                                    isShownPopup = false;
                                    deletedPos = (Integer) view.getTag();
                                    FwiJson item = videoListAdapter.getItem(deletedPos);

                                    FwiRequest request = RequestUtils.generateHttpRequest(
                                            FwiFoundation.FwiHttpMethod.kDelete,
                                            Configuration.kService_Event_Item,
                                            Configuration.kHostname,
                                            item.jsonWithPath("id").getString()
                                    );

                                    deleteVideoTaskId = FwiIdUtils.generateUUID();
                                    task = new ForegroundTask(getActivity(), request, deleteVideoTaskId);
                                    task.run(weakThis);
                                    dialog.dismiss();
                                }
                            })
                            .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int whichButton) {
                                    isShownPopup = false;
                                    dialog.dismiss();
                                }
                            })
                            .setOnKeyListener(new DialogInterface.OnKeyListener() {
                                @Override
                                public boolean onKey(DialogInterface dialogInterface, int keyCode, KeyEvent keyEvent) {
                                    //Handle the back button
                                    if (keyCode == KeyEvent.KEYCODE_BACK) {
                                        isShownPopup = false;
                                    }
                                    return false;
                                }
                            })
                            .create();

                    dialog.show();

                }
                break;
            }
        }
    }
    /*@Override
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
        switch (item.getItemId()) {
            case android.R.id.home: {
                getActivity().onBackPressed();
                return false;
            }
            default: {
                return super.onOptionsItemSelected(item);
            }
        }
    }*/

    @Override
    public ListView getListView() {
        return lsVideos;
    }

    @Override
    public void onSwipe(boolean isRight, int position, View item) {
        if (!isRight && item != null) {
            final Button btnDelete = (Button) item.findViewById(R.id.btnDelete);
            if (btnDelete != null) {
                Animation animation = AnimationUtils.loadAnimation(getActivity(), R.anim.right_to_left);
                btnDelete.startAnimation(animation);
                btnDelete.setVisibility(View.GONE);
            }
        }
    }

    @Override
    public void reverseSwipe(int position, View item) {
        if (item != null) {
            Button btnDelete = (Button) item.findViewById(R.id.btnDelete);
            if (btnDelete != null) {
                Animation animation = AnimationUtils.loadAnimation(getActivity(), R.anim.left_to_right);
                btnDelete.startAnimation(animation);
                btnDelete.setVisibility(View.GONE);

            }
        }
    }

    @Override
    public void onItemClickListener(ListAdapter adapter, int position) {
        FwiJson videoInfo = videoListAdapter.getItem(position);
        Intent intent = new Intent(getActivity(), StreamVideoController.class);
        intent.putExtra("videoInfo", videoInfo);
        startActivity(intent);
        getActivity().overridePendingTransition(R.anim.enter_slide_to_left, R.anim.exit_slide_to_left);
    }

    @Override
    public void onAnimationStart(Animation animation) {

    }

    @Override
    public void onAnimationEnd(Animation animation) {

    }

    @Override
    public void onAnimationRepeat(Animation animation) {

    }

    public void passData(Activity activity, String record) {
        fromScreen = record;

    }
    public void showAlert(String title,String Message)
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity())
                .setTitle(title)
                .setMessage(Message)
                .setPositiveButton(android.R.string.ok, new Dialog.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        // Mark this version as read.
                        dialogInterface.dismiss();
                    }
                });

        builder.create().show();
    }

}
