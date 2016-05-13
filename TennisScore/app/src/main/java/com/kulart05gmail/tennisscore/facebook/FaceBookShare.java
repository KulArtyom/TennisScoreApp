package com.kulart05gmail.tennisscore.facebook;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.share.ShareApi;
import com.facebook.share.model.ShareLinkContent;
import com.facebook.share.model.SharePhoto;
import com.facebook.share.model.SharePhotoContent;
import com.kulart05gmail.tennisscore.InfoActivity;
import com.kulart05gmail.tennisscore.R;

import java.util.Arrays;
import java.util.List;

/**
 * Created by KulArtyom on 13.05.2016.
 */
public class FaceBookShare extends AppCompatActivity {

    private static final String TAG = FaceBookShare.class.getSimpleName();

    private CallbackManager callbackManager;
    private LoginManager manager;
    private List<String> permissionNeeds;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getApplicationContext());
        callbackManager = CallbackManager.Factory.create();
        permissionNeeds = Arrays.asList("publish_actions");

        manager = LoginManager.getInstance();
        manager.logInWithPublishPermissions(this, permissionNeeds);
        manager.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                if (loginResult != null) {
                    publishImage();
                }
                finish();


            }

            @Override
            public void onCancel() {
                Toast.makeText(getApplicationContext(),"Cancel Facebook share",Toast.LENGTH_SHORT).show();
                finish();
                Log.v(TAG, "LoginActivity onCancel");

            }

            @Override
            public void onError(FacebookException error) {
                Toast.makeText(getApplicationContext(),"Error Facebook share",Toast.LENGTH_SHORT).show();
                Log.v("LoginActivity registerCallBack: Error", error.getCause().toString());
            }
        });

    }

    private void publishImage() {
        /**
         * Шаринг картинки
         * */
//        Bitmap image = BitmapFactory.decodeResource(getResources(), R.drawable.logo_app_1);
//
//        SharePhoto photo = new SharePhoto.Builder()
//                .setBitmap(image)
//                .setCaption("I'm playing tennis with Score Tennis")
//                .build();
//
//        SharePhotoContent content = new SharePhotoContent.Builder().addPhoto(photo).build();


//        contentURL — публикуемая ссылка;
//        contentTitle — заголовок материалов в ссылке;
//        imageURL — URL-адрес миниатюры, которая будет отображаться в публикации;
//        contentDescription — описание материалов (обычно 2–4 предложения).

        ShareLinkContent content = new ShareLinkContent.Builder()
                .setContentUrl(Uri.parse("https://vk.com/artyomkul"))
                .setContentTitle("Woy Woy Woy!!!!").setImageUrl(Uri.parse("http://pngimg.com/upload/tennis_PNG10404.png"))
                .setContentDescription("Real cool app")
                .build();



        ShareApi.share(content, null);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        callbackManager.onActivityResult(requestCode, resultCode, data);
    }


}
