package com.kulart05gmail.tennisscore;

import android.content.Intent;
import android.drm.DrmStore;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.NotificationCompatBase;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;


import com.kulart05gmail.tennisscore.dialogs.ConfirmActionDialog;
import com.kulart05gmail.tennisscore.dialogs.SettingsDialogFragment;
import com.kulart05gmail.tennisscore.logic.Game;


public class MainActivity extends AppCompatActivity implements View.OnClickListener,
        SettingsDialogFragment.OnAppSettingsChangedListener, Game.OnScoreUpdateListener, ConfirmActionDialog.ConfirmActionListener {
    // ===========================================================
    // Constants
    // ===========================================================
    public static final String TAG = MainActivity.class.getSimpleName();

    // ===========================================================
    // Fields
    // ===========================================================
    private Game mGame;

    private LinearLayout mLlSoundToggler;

    private TextView mTvPlayerOneName;
    private TextView mTvPlayerTwoName;

    private TextView mTvPlayerOnePoints;
    private TextView mTvPlayerOneGames;
    private TextView mTvPlayerOneSets;

    private TextView mTvPlayerTwoPoints;
    private TextView mTvPlayerTwoGames;
    private TextView mTvPlayerTwoSets;

    private ImageView mIvPlayIconPlayerOne;
    private ImageView mIvPlayIconPlayerTwo;

    private ImageView mIvSoundIcon;

    private boolean mMuted;

    private ImageView mIvSettingsIcon;
    private ImageView mIvInfoIcon;
    private Boolean exit = false;

    // ===========================================================
    // Constructors
    // ===========================================================

    // ===========================================================
    // Getter & Setter
    // ===========================================================

    // ===========================================================
    // Methods for/from SuperClass/Interfaces
    // ===========================================================
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setContentInsetsAbsolute(0, 0);
        View root = View.inflate(this, R.layout.custom_action_bar, null);

        mIvSettingsIcon = (ImageView) root.findViewById(R.id.iv_settings_icon);
        mIvSettingsIcon.setOnClickListener(this);

        mIvInfoIcon = (ImageView) root.findViewById(R.id.iv_info_icon);
        mIvInfoIcon.setOnClickListener(this);

        toolbar.addView(root);

        setSupportActionBar(toolbar);

        findViewById(R.id.btn_plus_one).setOnClickListener(this);
        findViewById(R.id.btn_plus_two).setOnClickListener(this);
        findViewById(R.id.ll_undo).setOnClickListener(this);
        findViewById(R.id.btn_start).setOnClickListener(this);
        findViewById(R.id.btn_stop).setOnClickListener(this);

        mLlSoundToggler = (LinearLayout) findViewById(R.id.ll_sound_toggler);
        mLlSoundToggler.setOnClickListener(this);

        mTvPlayerOneName = (TextView) findViewById(R.id.tv_name_one);
        mTvPlayerTwoName = (TextView) findViewById(R.id.tv_name_two);

        mTvPlayerOnePoints = (TextView) findViewById(R.id.tv_points_one);
        mTvPlayerOneGames = (TextView) findViewById(R.id.tv_game_one);
        mTvPlayerOneSets = (TextView) findViewById(R.id.tv_sets_one);

        mTvPlayerTwoPoints = (TextView) findViewById(R.id.tv_points_two);
        mTvPlayerTwoGames = (TextView) findViewById(R.id.tv_game_two);
        mTvPlayerTwoSets = (TextView) findViewById(R.id.tv_sets_two);

        mIvPlayIconPlayerOne = (ImageView) findViewById(R.id.iv_player_one_indicator);
        mIvPlayIconPlayerTwo = (ImageView) findViewById(R.id.iv_player_two_indicator);

        mIvSettingsIcon = (ImageView) findViewById(R.id.iv_settings_icon);
        mIvInfoIcon = (ImageView) findViewById(R.id.iv_info_icon);

        mIvSoundIcon = (ImageView) findViewById(R.id.iv_sound_icon);

        mGame = new Game(this, this);
        mGame.setPlayersNames(mTvPlayerOneName.getText().toString(), mTvPlayerTwoName.getText().toString());


        showSettings();


    }


    @Override
    public void onAppSettingsChanged(String playerOneName, String playerTwoName, int gameType) {
        mTvPlayerOneName.setText(playerOneName);
        mTvPlayerTwoName.setText(playerTwoName);

        mGame.setPlayersNames(mTvPlayerOneName.getText().toString(), mTvPlayerTwoName.getText().toString());
        if (mGame.canChangeGameType()) {
            mGame.setGameType(gameType);
        } else {
            Toast.makeText(this, R.string.cant_change_game_tipe, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ll_sound_toggler:
                toggleAudioMute();
                break;
            case R.id.ll_undo:
                mGame.undoLastMove();
                break;
            case R.id.btn_plus_one:
                mIvSettingsIcon.setEnabled(false);
                mIvSettingsIcon.setOnClickListener(null);
                mIvSettingsIcon.setBackgroundResource(R.drawable.ic_settings_disabled);
                mIvInfoIcon.setEnabled(false);
                mIvInfoIcon.setOnClickListener(null);
                mIvInfoIcon.setBackgroundResource(R.drawable.ic_info_disabled);
                addScorePlayerOne();
                break;
            case R.id.btn_plus_two:
                mIvSettingsIcon.setEnabled(false);
                mIvSettingsIcon.setOnClickListener(null);
                mIvSettingsIcon.setBackgroundResource(R.drawable.ic_settings_disabled);
                mIvInfoIcon.setEnabled(false);
                mIvInfoIcon.setOnClickListener(null);
                mIvInfoIcon.setBackgroundResource(R.drawable.ic_info_disabled);
                addScorePlayerTwo();
                break;
            case R.id.btn_start:
                ConfirmActionDialog dialog = ConfirmActionDialog.getInstance(
                        getString(R.string.confirm_new_match_title), this);
                dialog.show(getFragmentManager(), ConfirmActionDialog.TAG);
                break;
            case R.id.btn_stop:
                reset();
                break;
            case R.id.iv_settings_icon:
                showSettings();
                break;
            case R.id.iv_info_icon:
               showInfoActivity();
            default:
                break;
        }
    }

    @Override
    public void onScoreUpdated(Game.GameStatus status) {
        mTvPlayerOnePoints.setText(String.valueOf(status.playerOnePoints));
        mTvPlayerOneGames.setText(String.valueOf(status.playerOneGames));
        mTvPlayerOneSets.setText(String.valueOf(status.playerOneSets));

        mTvPlayerTwoPoints.setText(String.valueOf(status.playerTwoPoints));
        mTvPlayerTwoGames.setText(String.valueOf(status.playerTwoGames));
        mTvPlayerTwoSets.setText(String.valueOf(status.playerTwoSets));

        updateServing(status.playerOneIsServing);
    }

    @Override
    public void onConfirm() {
        reset();
    }

    @Override
    public void onCancel() {
        // do nothing
    }

    // ===========================================================
    // Methods
    // ===========================================================

    private void showInfoActivity(){
        Intent intent = new Intent(this, InfoActivity.class);
        startActivity(intent);
    }

    private void addScorePlayerOne() {
        mGame.addScorePlayerOne();
    }

    private void addScorePlayerTwo() {
        mGame.addScorePlayerTwo();
    }

    private void updateServing(boolean playerOneIsServing) {
        if (playerOneIsServing) {
            mIvPlayIconPlayerOne.setVisibility(View.VISIBLE);
            mIvPlayIconPlayerTwo.setVisibility(View.INVISIBLE);
        } else {
            mIvPlayIconPlayerOne.setVisibility(View.INVISIBLE);
            mIvPlayIconPlayerTwo.setVisibility(View.VISIBLE);
        }
    }

    private void reset() {
        mIvSettingsIcon.setBackgroundResource(R.drawable.ic_settings);
        mIvSettingsIcon.setEnabled(true);
        mIvSettingsIcon.setOnClickListener(this);
        mIvInfoIcon.setBackgroundResource(R.drawable.ic_info);
        mIvInfoIcon.setEnabled(true);
        mIvInfoIcon.setOnClickListener(this);
        mIvPlayIconPlayerOne.setVisibility(View.VISIBLE);
        mIvPlayIconPlayerTwo.setVisibility(View.INVISIBLE);
        mGame.reset();
    }

    private void toggleAudioMute() {
        mGame.toggleMute();
        if (mMuted) {
          // mIvSoundIcon.setBackgroundResource(R.drawable.ic_volume_on);
            mLlSoundToggler.setBackgroundResource(R.color.color_pb_progress);
        } else {
          // mIvSoundIcon.setBackgroundResource(R.drawable.ic_volume);
            mLlSoundToggler.setBackgroundResource(R.color.color_pb);
        }
        mMuted = !mMuted;
    }

    public void showSettings() {
        SettingsDialogFragment.getInstance(this, mTvPlayerOneName.getText().toString(),
                mTvPlayerTwoName.getText().toString(), mGame.getGameType())
                .show(getFragmentManager(), SettingsDialogFragment.TAG);
    }

    @Override
    public void onStart() {
        super.onStart();


    }

    @Override
    public void onStop() {
        super.onStop();


    }

    @Override
    public void onBackPressed() {
        if (exit) {
            finish(); // finish activity
        } else {
            Toast.makeText(this, "Press Back again to Exit.",
                    Toast.LENGTH_SHORT).show();
            exit = true;
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    exit = false;
                }
            }, 3 * 1000);

        }
    }

    // ===========================================================
    // Inner and Anonymous Classes
    // ===========================================================

}
