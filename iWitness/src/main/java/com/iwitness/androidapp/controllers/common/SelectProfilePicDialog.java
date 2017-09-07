package com.iwitness.androidapp.controllers.common;


import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import com.iwitness.androidapp.R;
import com.iwitness.androidapp.libraries.utils.FontUtils;
import com.iwitness.androidapp.model.UserPreferences;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by samadhanmalpure on 2017-08-03.
 */

public class SelectProfilePicDialog extends Dialog {

    private SelectProfilePicDialogDialogButtonClickListener mItemClickListener;
    private UserPreferences userPreferences;

    @BindView(R.id.tvCamera)
    TextView mTextViewCamera;

    @BindView(R.id.libraryButton)
    TextView mTextViewPhotoLibrary;

    @BindView(R.id.btnCancel)
    TextView mTextViewCancel;


    public SelectProfilePicDialog(Context context, SelectProfilePicDialogDialogButtonClickListener itemClickListener) {
        super(context);
        mItemClickListener = itemClickListener;
    }

    /**
     * This is an interface for initializing the counter to initial amount when user either click on cancel button or clicks outside the dialog. on record video screen.
     */

    public interface SelectProfilePicDialogDialogButtonClickListener {
        void getSelectedPosition(int selectedPosition);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.select_profile_pic_dialog_layout);

        ButterKnife.bind(this);

        init();

        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(this.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        this.getWindow().setAttributes(lp);

        userPreferences = UserPreferences.sharedInstance();
    }

    private void init() {
        mTextViewCamera.setTypeface(FontUtils.getFontFabricGloberBold());
        mTextViewPhotoLibrary.setTypeface(FontUtils.getFontFabricGloberBold());
        mTextViewCancel.setTypeface(FontUtils.getFontFabricGloberBold());
    }


    @OnClick(R.id.btnCancel)
    void cancelDialog() {
        this.dismiss();
    }

    @Override
    public void dismiss() {
        super.dismiss();
    }

    @OnClick(R.id.tvCamera)
    void onCameraSelection() {
        mItemClickListener.getSelectedPosition(0);
        this.dismiss();
    }

    @OnClick(R.id.libraryButton)
    void onPhotoLibrarySelection() {
        mItemClickListener.getSelectedPosition(1);
        this.dismiss();
    }
}
