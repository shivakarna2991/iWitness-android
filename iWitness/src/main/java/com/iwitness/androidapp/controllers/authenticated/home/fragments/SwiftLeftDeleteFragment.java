package com.iwitness.androidapp.controllers.authenticated.home.fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.*;
import android.widget.ListAdapter;
import android.widget.ListView;

public abstract class SwiftLeftDeleteFragment extends Fragment {

    private int REL_SWIPE_MIN_DISTANCE;
    private int REL_SWIPE_MAX_OFF_PATH;
    private int REL_SWIPE_THRESHOLD_VELOCITY;
    private int swipedPos;

    /**
     * @return ListView
     */
    public abstract ListView getListView();

    /**
     * @param isRight  Swiping direction
     * @param position which item position is swiped
     */
    public abstract void onSwipe(boolean isRight, int position, View item);

    /**
     * @param position which item position is swiped
     * @param item
     */
    public abstract void reverseSwipe(int position, View item);

    /**
     * For single tap/Click
     *
     * @param adapter
     * @param position
     */
    public abstract void onItemClickListener(ListAdapter adapter, int position);

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        DisplayMetrics dm = getResources().getDisplayMetrics();
        REL_SWIPE_MIN_DISTANCE = (int) (100.0f * dm.densityDpi / 160.0f + 0.5);
        REL_SWIPE_MAX_OFF_PATH = (int) (250.0f * dm.densityDpi / 160.0f + 0.5);
        REL_SWIPE_THRESHOLD_VELOCITY = (int) (100.0f * dm.densityDpi / 160.0f + 0.5);
    }

    @Override
    public void onResume() {
        super.onResume();
        ListView list = getListView();
        if (list == null) {
            return;
        }

        @SuppressWarnings("deprecation")
        final GestureDetector gestureDetector = new GestureDetector(
                new MyGestureDetector());

        View.OnTouchListener gestureListener = new View.OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {
                return event != null && gestureDetector.onTouchEvent(event);
            }
        };
        list.setOnTouchListener(gestureListener);

    }

    public void reverseSwipe() {
        ListView list = getListView();
        if (list != null && list.getCount() > swipedPos) {
            reverseSwipe(swipedPos, list.getChildAt(swipedPos));
        }
    }

    private void myOnItemClick(int position) {
        if (position < 0)
            return;
        ListView list = getListView();
        onItemClickListener(list.getAdapter(), position);
    }

    class MyGestureDetector extends GestureDetector.SimpleOnGestureListener {

        private int temp_position = -1;

        // Detect a single-click and call my own handler.
        @Override
        public boolean onSingleTapUp(MotionEvent e) {
            ListView list = getListView();
            int pos = list.pointToPosition((int) e.getX(), (int) e.getY());
            myOnItemClick(pos);
            return true;
        }

        @Override
        public boolean onDown(MotionEvent e) {
            ListView list = getListView();
            temp_position = list
                    .pointToPosition((int) e.getX(), (int) e.getY());
            return super.onDown(e);
        }

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
                               float velocityY) {
            ListView list = getListView();
            if (list != null && e1 != null && e2 != null) {
                if (Math.abs(e1.getY() - e2.getY()) > REL_SWIPE_MAX_OFF_PATH)
                    return false;

                int pos = list
                        .pointToPosition((int) e1.getX(), (int) e2.getY());
                int firstVisiblePos =  list.getFirstVisiblePosition();
                boolean isRight = pos >= 0 && temp_position == pos;

                if (e1.getX() - e2.getX() > REL_SWIPE_MIN_DISTANCE
                        && Math.abs(velocityX) > REL_SWIPE_THRESHOLD_VELOCITY) {
                    if (list.getCount() > swipedPos && swipedPos != pos) {
                        reverseSwipe(swipedPos, list.getChildAt(swipedPos - firstVisiblePos));
                    }
                    swipedPos = pos;
                    onSwipe(!isRight, pos, list.getChildAt(pos - firstVisiblePos));
                } else if (e2.getX() - e1.getX() > REL_SWIPE_MIN_DISTANCE
                        && Math.abs(velocityX) > REL_SWIPE_THRESHOLD_VELOCITY) {
                    if (list.getCount() > swipedPos && swipedPos != pos) {
                        reverseSwipe(swipedPos, list.getChildAt(swipedPos - firstVisiblePos));
                    }
                    swipedPos = pos;
                    onSwipe(isRight, pos, list.getChildAt(pos - firstVisiblePos));
                }
            }
            return false;
        }

    }

}