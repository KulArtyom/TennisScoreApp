package com.kulart05gmail.tennisscore.logic;

import android.content.Context;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Handler;
import android.os.Message;
import android.widget.Toast;

import com.kulart05gmail.tennisscore.R;


public class Game {
    // ===========================================================
    // Constants
    // ===========================================================
    public static final String TAG = Game.class.getSimpleName();

    public static final int DELAY = 1500;

    public static final int GAME_TYPE_3 = 0;
    public static final int GAME_TYPE_5 = 1;

    private static final int POS_POINTS = 0;
    private static final int POS_GAMES = 1;
    private static final int POS_SETS = 2;

    private static final int WIN_SCORE_NO = 0;
    private static final int WIN_SCORE_FIFTEEN = 15;
    private static final int WIN_SCORE_THIRTY = 30;
    private static final int WIN_SCORE_FORTY = 40;

    public static final int GAME_STATUS_DEFAULT = 0;
    public static final int GAME_STATUS_DEUCE = 1;
    public static final int GAME_STATUS_TIE_BREAK = 2;

    // ===========================================================
    // Fields
    // ===========================================================
    private final int[] mPreviousGameScorePlayerOne = {0, 0, 0};
    private final int[] mPreviousGameScorePlayerTwo = {0, 0, 0};

    private OnScoreUpdateListener mListener;

    private int mGameType;

    private Toast mWinToastPlayerOne;
    private Toast mWinToastPlayerTwo;

    private boolean mWin;

    private int mPrevVolume;
    private boolean mIsMuted;

    private SoundPool mSoundPool;
    private int mSoundId15;
    private int mSoundId30;
    private int mSoundId40;
    private int mSoundIdAddIn;
    private int mSoundIdAddOut;
    private int mSoundIdAll;
    private int mSoundIdDeuce;
    private int mSoundIdGame;
    private int mSoundIdLove;
    private int mSoundIdMatch;
    private int mSoundIdSet;

    private Context mContext;

    private GameStatus mCurrentGameStatus;
    private GameStatus mPreviousGameStatus;

    private AudioManager mAudioManager;

    // ===========================================================
    // Constructors
    // ===========================================================
    public Game(OnScoreUpdateListener listener, Context context) {
        mListener = listener;
        mGameType = GAME_TYPE_3;
        mContext = context;

        mSoundPool = new SoundPool(1, AudioManager.STREAM_MUSIC, 0);

        mSoundId15 = mSoundPool.load(mContext, R.raw.sound15, 1);
        mSoundId30 = mSoundPool.load(mContext, R.raw.sound30, 1);
        mSoundId40 = mSoundPool.load(mContext, R.raw.sound40, 1);
        mSoundIdAddIn = mSoundPool.load(mContext, R.raw.add_in, 1);
        mSoundIdAddOut = mSoundPool.load(mContext, R.raw.add_out, 1);
        mSoundIdAll = mSoundPool.load(mContext, R.raw.all, 1);
        mSoundIdDeuce = mSoundPool.load(mContext, R.raw.deuce, 1);
        mSoundIdGame = mSoundPool.load(mContext, R.raw.game, 1);
        mSoundIdLove = mSoundPool.load(mContext, R.raw.love, 1);
        mSoundIdMatch = mSoundPool.load(mContext, R.raw.match, 1);
        mSoundIdSet = mSoundPool.load(mContext, R.raw.set, 1);

        mAudioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);

        mCurrentGameStatus = new GameStatus();

        callUpdateCallback(false);
    }

    // ===========================================================
    // Getter & Setter
    // ===========================================================
    public void setGameType(int gameType) {
        mGameType = gameType;
    }

    public void setPlayersNames(String playerOneName, String playerTwoName) {
        mWinToastPlayerOne = Toast.makeText(mContext,
                playerOneName + mContext.getString(R.string.won_the_game), Toast.LENGTH_LONG);
        mWinToastPlayerTwo = Toast.makeText(mContext,
                playerTwoName + mContext.getString(R.string.won_the_game), Toast.LENGTH_LONG);
    }

    public int getGameType() {
        return mGameType;
    }

    // ===========================================================
    // Methods for/from SuperClass/Interfaces
    // ===========================================================

    // ===========================================================
    // Methods
    // ===========================================================
    public void addScorePlayerOne() {
        savePreviousGameScore();
        stopAllSounds();
        if (!mWin) {
            if (mCurrentGameStatus.statusName == GAME_STATUS_TIE_BREAK) {
                mCurrentGameStatus.tiebreakMovedUntilChangeover--;
                if (mCurrentGameStatus.tiebreakMovedUntilChangeover == 0) {
                    callUpdateCallback(true);
                    mCurrentGameStatus.tiebreakMovedUntilChangeover = 2;
                }
                mCurrentGameStatus.playerOnePoints++;
                if (mCurrentGameStatus.playerOnePoints >= 7 && mCurrentGameStatus.playerOnePoints - mCurrentGameStatus.playerTwoPoints >= 2) {
                    gameWinPlayerOne();
                }
            } else {
                switch (mCurrentGameStatus.playerOnePoints) {
                    case WIN_SCORE_NO:
                        mCurrentGameStatus.playerOnePoints = WIN_SCORE_FIFTEEN;

                        if (mCurrentGameStatus.playerTwoPoints == WIN_SCORE_FIFTEEN) {
                            playSound(mSoundId15);
                            mAllSoundHandler.sendEmptyMessageDelayed(0, DELAY);
                        } else if (mCurrentGameStatus.playerTwoPoints == WIN_SCORE_NO) {
                            if (mCurrentGameStatus.playerOneIsServing) {
                                playSound(mSoundId15);
                                mLoveSoundHandler.sendEmptyMessageDelayed(0, DELAY);
                            } else {
                                playSound(mSoundIdLove);
                                mFifteenSoundHandler.sendEmptyMessageDelayed(0, DELAY);
                            }
                        } else {
                            if (mCurrentGameStatus.playerOneIsServing) {
                                playSound(mSoundId15);
                                if (mCurrentGameStatus.playerTwoPoints == WIN_SCORE_THIRTY) {
                                    mThirtySoundHandler.sendEmptyMessageDelayed(0, DELAY);
                                } else {
                                    mFortySoundHandler.sendEmptyMessageDelayed(0, DELAY);
                                }
                            } else {
                                if (mCurrentGameStatus.playerTwoPoints == WIN_SCORE_THIRTY) {
                                    playSound(mSoundId30);
                                } else {
                                    playSound(mSoundId40);
                                }
                                mFifteenSoundHandler.sendEmptyMessageDelayed(0, DELAY);
                            }
                        }
                        break;
                    case WIN_SCORE_FIFTEEN:
                        mCurrentGameStatus.playerOnePoints = WIN_SCORE_THIRTY;

                        if (mCurrentGameStatus.playerTwoPoints == WIN_SCORE_THIRTY) {
                            playSound(mSoundId30);
                            mAllSoundHandler.sendEmptyMessageDelayed(0, DELAY);
                        } else if (mCurrentGameStatus.playerTwoPoints == WIN_SCORE_NO) {
                            if (mCurrentGameStatus.playerOneIsServing) {
                                playSound(mSoundId30);
                                mLoveSoundHandler.sendEmptyMessageDelayed(0, DELAY);
                            } else {
                                playSound(mSoundIdLove);
                                mThirtySoundHandler.sendEmptyMessageDelayed(0, DELAY);
                            }
                        } else {
                            if (mCurrentGameStatus.playerOneIsServing) {
                                playSound(mSoundId30);
                                if (mCurrentGameStatus.playerTwoPoints == WIN_SCORE_FIFTEEN) {
                                    mFifteenSoundHandler.sendEmptyMessageDelayed(0, DELAY);
                                } else {
                                    mFortySoundHandler.sendEmptyMessageDelayed(0, DELAY);
                                }
                            } else {
                                if (mCurrentGameStatus.playerTwoPoints == WIN_SCORE_FIFTEEN) {
                                    playSound(mSoundId15);
                                } else {
                                    playSound(mSoundId40);
                                }
                                mThirtySoundHandler.sendEmptyMessageDelayed(0, DELAY);
                            }
                        }
                        break;
                    case WIN_SCORE_THIRTY:
                        mCurrentGameStatus.playerOnePoints = WIN_SCORE_FORTY;
                        if (mCurrentGameStatus.playerTwoPoints == WIN_SCORE_FORTY) {
                            playSound(mSoundIdDeuce);
                        } else if (mCurrentGameStatus.playerTwoPoints == WIN_SCORE_NO) {
                            if (mCurrentGameStatus.playerOneIsServing) {
                                playSound(mSoundId40);
                                mLoveSoundHandler.sendEmptyMessageDelayed(0, DELAY);
                            } else {
                                playSound(mSoundIdLove);
                                mFortySoundHandler.sendEmptyMessageDelayed(0, DELAY);
                            }
                        } else {
                            if (mCurrentGameStatus.playerOneIsServing) {
                                playSound(mSoundId40);
                                if (mCurrentGameStatus.playerTwoPoints == WIN_SCORE_FIFTEEN) {
                                    mFifteenSoundHandler.sendEmptyMessageDelayed(0, DELAY);
                                } else {
                                    mThirtySoundHandler.sendEmptyMessageDelayed(0, DELAY);
                                }
                            } else {
                                if (mCurrentGameStatus.playerTwoPoints == WIN_SCORE_FIFTEEN) {
                                    playSound(mSoundId15);
                                } else {
                                    playSound(mSoundId30);
                                }
                                mFortySoundHandler.sendEmptyMessageDelayed(0, DELAY);
                            }
                        }
                        break;
                    case WIN_SCORE_FORTY:
                        if ((mCurrentGameStatus.playerTwoPoints < WIN_SCORE_FORTY) ||
                                (mCurrentGameStatus.playerTwoPoints == WIN_SCORE_FORTY && mCurrentGameStatus.advantagePlayerOne)) {
                            gameWinPlayerOne();
                        } else if (mCurrentGameStatus.playerTwoPoints == WIN_SCORE_FORTY) {
                            if (mCurrentGameStatus.statusName == GAME_STATUS_DEFAULT) {
                                mCurrentGameStatus.statusName = GAME_STATUS_DEUCE;
                            }
                            if (mCurrentGameStatus.advantagePlayerTwo) {
                                mCurrentGameStatus.advantagePlayerTwo = false;
                                playSound(mSoundIdDeuce);
                            } else {
                                mCurrentGameStatus.advantagePlayerOne = true;

                                if (mCurrentGameStatus.playerOneIsServing)
                                    playSound(mSoundIdAddIn);
                                else
                                    playSound(mSoundIdAddOut);
                            }
                        }
                        break;
                }
            }
            updateScore();
        }
    }

    public void addScorePlayerTwo() {
        savePreviousGameScore();
        stopAllSounds();
        if (!mWin) {
            if (mCurrentGameStatus.statusName == GAME_STATUS_TIE_BREAK) {
                mCurrentGameStatus.playerTwoPoints++;

                mCurrentGameStatus.tiebreakMovedUntilChangeover--;
                if (mCurrentGameStatus.tiebreakMovedUntilChangeover == 0) {
                    callUpdateCallback(true);
                    mCurrentGameStatus.tiebreakMovedUntilChangeover = 2;
                }

                if (mCurrentGameStatus.playerTwoPoints >= 7 && mCurrentGameStatus.playerTwoPoints - mCurrentGameStatus.playerOnePoints >= 2) {
                    gameWinPlayerTwo();
                }
            } else {
                switch (mCurrentGameStatus.playerTwoPoints) {
                    case WIN_SCORE_NO:
                        mCurrentGameStatus.playerTwoPoints = WIN_SCORE_FIFTEEN;
                        if (mCurrentGameStatus.playerOnePoints == WIN_SCORE_FIFTEEN) {
                            playSound(mSoundId15);
                            mAllSoundHandler.sendEmptyMessageDelayed(0, DELAY);
                        } else if (mCurrentGameStatus.playerOnePoints == WIN_SCORE_NO) {
                            if (!mCurrentGameStatus.playerOneIsServing) {
                                playSound(mSoundId15);
                                mLoveSoundHandler.sendEmptyMessageDelayed(0, DELAY);
                            } else {
                                playSound(mSoundIdLove);
                                mFifteenSoundHandler.sendEmptyMessageDelayed(0, DELAY);
                            }
                        } else {
                            if (!mCurrentGameStatus.playerOneIsServing) {
                                playSound(mSoundId15);
                                if (mCurrentGameStatus.playerOnePoints == WIN_SCORE_THIRTY) {
                                    mThirtySoundHandler.sendEmptyMessageDelayed(0, DELAY);
                                } else {
                                    mFortySoundHandler.sendEmptyMessageDelayed(0, DELAY);
                                }
                            } else {
                                if (mCurrentGameStatus.playerOnePoints == WIN_SCORE_THIRTY) {
                                    playSound(mSoundId30);
                                } else {
                                    playSound(mSoundId40);
                                }
                                mFifteenSoundHandler.sendEmptyMessageDelayed(0, DELAY);
                            }
                        }
                        break;
                    case WIN_SCORE_FIFTEEN:
                        mCurrentGameStatus.playerTwoPoints = WIN_SCORE_THIRTY;
                        if (mCurrentGameStatus.playerOnePoints == WIN_SCORE_THIRTY) {
                            playSound(mSoundId30);
                            mAllSoundHandler.sendEmptyMessageDelayed(0, DELAY);
                        } else if (mCurrentGameStatus.playerOnePoints == WIN_SCORE_NO) {
                            if (!mCurrentGameStatus.playerOneIsServing) {
                                playSound(mSoundId30);
                                mLoveSoundHandler.sendEmptyMessageDelayed(0, DELAY);
                            } else {
                                playSound(mSoundIdLove);
                                mThirtySoundHandler.sendEmptyMessageDelayed(0, DELAY);
                            }
                        } else {
                            if (!mCurrentGameStatus.playerOneIsServing) {
                                playSound(mSoundId30);
                                if (mCurrentGameStatus.playerOnePoints == WIN_SCORE_FIFTEEN) {
                                    mFifteenSoundHandler.sendEmptyMessageDelayed(0, DELAY);
                                } else {
                                    mFortySoundHandler.sendEmptyMessageDelayed(0, DELAY);
                                }
                            } else {
                                if (mCurrentGameStatus.playerOnePoints == WIN_SCORE_FIFTEEN) {
                                    playSound(mSoundId15);
                                } else {
                                    playSound(mSoundId40);
                                }
                                mThirtySoundHandler.sendEmptyMessageDelayed(0, DELAY);
                            }
                        }
                        break;
                    case WIN_SCORE_THIRTY:
                        mCurrentGameStatus.playerTwoPoints = WIN_SCORE_FORTY;
                        if (mCurrentGameStatus.playerOnePoints == WIN_SCORE_FORTY) {
                            playSound(mSoundIdDeuce);
                        } else if (mCurrentGameStatus.playerOnePoints == WIN_SCORE_NO) {
                            if (!mCurrentGameStatus.playerOneIsServing) {
                                playSound(mSoundId40);
                                mLoveSoundHandler.sendEmptyMessageDelayed(0, DELAY);
                            } else {
                                playSound(mSoundIdLove);
                                mFortySoundHandler.sendEmptyMessageDelayed(0, DELAY);
                            }
                        } else {
                            if (!mCurrentGameStatus.playerOneIsServing) {
                                playSound(mSoundId40);
                                if (mCurrentGameStatus.playerOnePoints == WIN_SCORE_FIFTEEN) {
                                    mFifteenSoundHandler.sendEmptyMessageDelayed(0, DELAY);
                                } else {
                                    mThirtySoundHandler.sendEmptyMessageDelayed(0, DELAY);
                                }
                            } else {
                                if (mCurrentGameStatus.playerOnePoints == WIN_SCORE_FIFTEEN) {
                                    playSound(mSoundId15);
                                } else {
                                    playSound(mSoundId30);
                                }
                                mFortySoundHandler.sendEmptyMessageDelayed(0, DELAY);
                            }
                        }
                        break;
                    case WIN_SCORE_FORTY:
                        if ((mCurrentGameStatus.playerOnePoints < WIN_SCORE_FORTY) ||
                                (mCurrentGameStatus.playerOnePoints == WIN_SCORE_FORTY && mCurrentGameStatus.advantagePlayerTwo)) {
                            gameWinPlayerTwo();
                        } else if (mCurrentGameStatus.playerOnePoints == WIN_SCORE_FORTY) {
                            if (mCurrentGameStatus.statusName == GAME_STATUS_DEFAULT) {
                                mCurrentGameStatus.statusName = GAME_STATUS_DEUCE;
                                playSound(mSoundIdDeuce);
                            }
                            if (mCurrentGameStatus.advantagePlayerOne) {
                                mCurrentGameStatus.advantagePlayerOne = false;
                                playSound(mSoundIdDeuce);
                            } else {
                                mCurrentGameStatus.advantagePlayerTwo = true;

                                if (!mCurrentGameStatus.playerOneIsServing)
                                    playSound(mSoundIdAddIn);
                                else
                                    playSound(mSoundIdAddOut);
                            }
                        }
                        break;
                }
            }
            updateScore();
        }
    }

    private void updateScore() {
        callUpdateCallback(false);
    }

    public void reset() {
        stopAllSounds();

        mCurrentGameStatus.playerOnePoints = 0;
        mCurrentGameStatus.playerOneGames = 0;
        mCurrentGameStatus.playerOneSets = 0;
        mCurrentGameStatus.playerTwoPoints = 0;
        mCurrentGameStatus.playerTwoGames = 0;
        mCurrentGameStatus.playerTwoSets = 0;

        mWin = false;
        mCurrentGameStatus.statusName = GAME_STATUS_DEFAULT;
        callUpdateCallback(false);
    }

    public void undoLastMove() {
        if (mPreviousGameStatus != null) {
            mCurrentGameStatus = mPreviousGameStatus;
            callUpdateCallback(false);
        }
    }

    private void savePreviousGameScore() {
        mPreviousGameStatus = new GameStatus(mCurrentGameStatus);
    }

    private void callUpdateCallback(boolean changeOver) {
        if (changeOver)
            mCurrentGameStatus.playerOneIsServing = !mCurrentGameStatus.playerOneIsServing;
        mListener.onScoreUpdated(mCurrentGameStatus);
    }

    private void gameWinPlayerOne() {
        playSound(mSoundIdGame);
        mCurrentGameStatus.playerOneGames++;
        mCurrentGameStatus.advantagePlayerOne = false;
        mCurrentGameStatus.advantagePlayerTwo = false;

        if (mCurrentGameStatus.statusName == GAME_STATUS_DEUCE) {
            mCurrentGameStatus.statusName = GAME_STATUS_DEFAULT;
        }

        callUpdateCallback(true);

        if (mCurrentGameStatus.playerOneGames == 6 && mCurrentGameStatus.playerTwoGames == 6) {
            mCurrentGameStatus.statusName = GAME_STATUS_TIE_BREAK;
            mCurrentGameStatus.tiebreakMovedUntilChangeover = 1;
        } else if ((mCurrentGameStatus.playerOneGames >= 6 && mCurrentGameStatus.playerOneGames - mCurrentGameStatus.playerTwoGames >= 2) ||
                (mCurrentGameStatus.statusName == GAME_STATUS_TIE_BREAK && mCurrentGameStatus.playerOnePoints - mCurrentGameStatus.playerTwoPoints >= 2)) {
            setWinPlayerOne();
        }

        mCurrentGameStatus.playerOnePoints = 0;
        mCurrentGameStatus.playerTwoPoints = 0;
    }

    private void setWinPlayerOne() {
        mCurrentGameStatus.statusName = GAME_STATUS_DEFAULT;
        playSound(mSoundIdSet);
        mCurrentGameStatus.playerOneSets++;
        mCurrentGameStatus.playerOneGames = 0;
        mCurrentGameStatus.playerTwoGames = 0;
        if ((mGameType == GAME_TYPE_3 && mCurrentGameStatus.playerOneSets == 2) ||
                (mGameType == GAME_TYPE_5 && mCurrentGameStatus.playerOneSets == 3)) {
            matchWinPlayerOne();
        }
    }

    private void matchWinPlayerOne() {
        playSound(mSoundIdMatch);
        mWinToastPlayerOne.show();
        mWin = true;
    }


    private void gameWinPlayerTwo() {
        playSound(mSoundIdGame);
        mCurrentGameStatus.playerTwoGames++;

        mCurrentGameStatus.advantagePlayerOne = false;
        mCurrentGameStatus.advantagePlayerTwo = false;

        if (mCurrentGameStatus.statusName == GAME_STATUS_DEUCE) {
            mCurrentGameStatus.statusName = GAME_STATUS_DEFAULT;
        }

        callUpdateCallback(true);

        if (mCurrentGameStatus.playerTwoGames == 6 && mCurrentGameStatus.playerOneGames == 6) {
            mCurrentGameStatus.statusName = GAME_STATUS_TIE_BREAK;
            mCurrentGameStatus.tiebreakMovedUntilChangeover = 1;
        } else if ((mCurrentGameStatus.playerTwoGames >= 6 && mCurrentGameStatus.playerTwoGames - mCurrentGameStatus.playerOneGames >= 2) ||
                (mCurrentGameStatus.statusName == GAME_STATUS_TIE_BREAK && mCurrentGameStatus.playerTwoPoints - mCurrentGameStatus.playerOnePoints >= 2)) {
            mCurrentGameStatus.playerTwoGames = 0;
            setWinPlayerTwo();
        }
        mCurrentGameStatus.playerOnePoints = 0;
        mCurrentGameStatus.playerTwoPoints = 0;
    }

    private void setWinPlayerTwo() {
        playSound(mSoundIdSet);
        mCurrentGameStatus.statusName = GAME_STATUS_DEFAULT;
        mCurrentGameStatus.playerTwoSets++;
        mCurrentGameStatus.playerOneGames = 0;
        mCurrentGameStatus.playerTwoGames = 0;
        if ((mGameType == GAME_TYPE_3 && mCurrentGameStatus.playerTwoSets == 2) ||
                (mGameType == GAME_TYPE_5 && mCurrentGameStatus.playerTwoSets == 3)) {
            matchWinPlayerTwo();
        }
    }

    private void matchWinPlayerTwo() {
        playSound(mSoundIdMatch);
        mWinToastPlayerTwo.show();
        mWin = true;
    }

    public boolean canChangeGameType() {
        return mCurrentGameStatus.playerOnePoints == 0 && mCurrentGameStatus.playerOneGames == 0 && mCurrentGameStatus.playerOneSets == 0 &&
                mCurrentGameStatus.playerTwoPoints == 0 && mCurrentGameStatus.playerTwoGames == 0 && mCurrentGameStatus.playerTwoSets == 0;
    }

    public void toggleMute() {
        if (mIsMuted) {
            mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, mPrevVolume, 0);
            mIsMuted = false;
        } else {
            mPrevVolume = mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
            mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, 0, 0);
            mIsMuted = true;
        }
    }

    private void stopAllSounds() {
        mLoveSoundHandler.removeCallbacksAndMessages(null);
        mFifteenSoundHandler.removeCallbacksAndMessages(null);
        mThirtySoundHandler.removeCallbacksAndMessages(null);
        mFortySoundHandler.removeCallbacksAndMessages(null);

        mSoundPool.stop(mSoundId15);
        mSoundPool.stop(mSoundId30);
        mSoundPool.stop(mSoundId40);
        mSoundPool.stop(mSoundIdAddIn);
        mSoundPool.stop(mSoundIdAddOut);
        mSoundPool.stop(mSoundIdAll);
        mSoundPool.stop(mSoundIdDeuce);
        mSoundPool.stop(mSoundIdGame);
        mSoundPool.stop(mSoundIdLove);
        mSoundPool.stop(mSoundIdMatch);
        mSoundPool.stop(mSoundIdSet);
    }

    private void playSound(int soundId) {
        float actualVolume = (float) mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
        float maxVolume = (float) mAudioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        float volume = actualVolume / maxVolume;
        mSoundPool.play(soundId, volume, volume, 1, 0, 1F);
    }

    // ===========================================================
    // Inner and Anonymous Classes
    // ===========================================================
    public interface OnScoreUpdateListener {
        public void onScoreUpdated(GameStatus status);
    }

    public static class GameStatus {

        public int playerOnePoints;
        public int playerOneGames;
        public int playerOneSets;

        public int playerTwoPoints;
        public int playerTwoGames;
        public int playerTwoSets;

        public boolean advantagePlayerOne;
        public boolean advantagePlayerTwo;

        public int statusName = GAME_STATUS_DEFAULT;

        public boolean playerOneIsServing = true;

        public int tiebreakMovedUntilChangeover;

        public GameStatus() {
        }

        public GameStatus(GameStatus status) {
            playerOnePoints = status.playerOnePoints;
            playerOneGames = status.playerOneGames;
            playerOneSets = status.playerOneSets;
            playerTwoPoints = status.playerTwoPoints;
            playerTwoGames = status.playerTwoGames;
            playerTwoSets = status.playerTwoSets;
            advantagePlayerOne = status.advantagePlayerOne;
            advantagePlayerTwo = status.advantagePlayerTwo;
            statusName = status.statusName;
            playerOneIsServing = status.playerOneIsServing;
            tiebreakMovedUntilChangeover = status.tiebreakMovedUntilChangeover;
        }
    }

    private Handler mLoveSoundHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            playSound(mSoundIdLove);
        }
    };

    private Handler mFifteenSoundHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            playSound(mSoundId15);
        }
    };

    private Handler mThirtySoundHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            playSound(mSoundId30);
        }
    };
    private Handler mFortySoundHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            playSound(mSoundId40);
        }
    };
    private Handler mAllSoundHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            playSound(mSoundIdAll);
        }
    };
}
