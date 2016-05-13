package com.kulart05gmail.tennisscore;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.share.ShareApi;
import com.facebook.share.model.SharePhoto;
import com.facebook.share.model.SharePhotoContent;
import com.kulart05gmail.tennisscore.facebook.FaceBookShare;

import java.util.Arrays;
import java.util.List;

public class InfoActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = InfoActivity.class.getSimpleName();
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

        ivFacebook.setOnClickListener(this);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_facebook_share:
                Intent facebook = new Intent(this, FaceBookShare.class);
                startActivity(facebook);
                break;
            case R.id.iv_vk_share:
                break;
            case R.id.iv_google_share:
                break;
            case R.id.iv_twitter_share:
                break;

            default:
                break;

        }
    }
}
