package com.kulart05gmail.tennisscore;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;

public class InfoActivity extends AppCompatActivity implements View.OnClickListener {

    private ImageView ivVkontakte;
    private ImageView ivFacebook;
    private ImageView ivGoogle;
    private ImageView ivTwitter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info);

        ivVkontakte = (ImageView) findViewById(R.id.iv_vk_share);
        ivFacebook = (ImageView) findViewById(R.id.iv_facebook_share);
        ivGoogle = (ImageView) findViewById(R.id.iv_google_share);
        ivTwitter = (ImageView) findViewById(R.id.iv_twitter_share);
    }

    @Override
    public void onClick(View v) {

    }
}
