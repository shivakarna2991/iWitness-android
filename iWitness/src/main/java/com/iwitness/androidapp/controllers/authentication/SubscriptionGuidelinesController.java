package com.iwitness.androidapp.controllers.authentication;


import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.iwitness.androidapp.R;

public class SubscriptionGuidelinesController extends AppCompatActivity {
    TextView toolBarTitle;
    TextView tv_guidelines;
    TextView tv_privacypolicy,tv_termsofuse;
    String privacyPolicy ="https://www.iwitness.com/content-terms-of-service/";
    String termsOfuse ="https://www.iwitness.com/content-terms-of-use/";
    @Override
    public void onCreate(Bundle savedInstance) {
        super.onCreate(savedInstance);
        setContentView(R.layout.subscription_user_guidelines);

        tv_guidelines =(TextView)findViewById(R.id.tv_guidelines);
        tv_termsofuse =(TextView)findViewById(R.id.tv_termsofuse);
        tv_privacypolicy =(TextView)findViewById(R.id.tv_privacypolicy);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolBarTitle  =((TextView)toolbar.findViewById(R.id.toolbar_title));

        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        toolBarTitle.setText("Subscription");

        tv_privacypolicy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Uri uri = Uri.parse(privacyPolicy);
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(intent);
            }
        });

        tv_termsofuse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Uri uri = Uri.parse(termsOfuse);
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(intent);
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        this.finish();
        overridePendingTransition(R.anim.enter_slide_to_right, R.anim.exit_slide_to_right);

    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home: {
                this.finish();
                overridePendingTransition(R.anim.enter_slide_to_right, R.anim.exit_slide_to_right);
                return false;
            }
            default: {
                return super.onOptionsItemSelected(item);
            }
        }
    }

}