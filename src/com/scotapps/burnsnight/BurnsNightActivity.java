/*
Heavily based on Google samples: APIDemos MediaPlayerDemo_Audio.java
 */

package com.scotapps.burnsnight;

import com.admob.android.ads.AdListener;
//import com.admob.android.ads.AdManager;
import com.admob.android.ads.AdView;

import android.app.Activity; //import android.content.Context;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.MediaController;

//import android.widget.TextView;
//import android.widget.MediaController.MediaPlayerControl;

//import android.media.SoundPool;
//import android.media.AudioManager;

//TODO: add bagpipe background
//TODO: move to SoundPool BUT beware of sound file size limit (~1MB??) for SoundPool.
//TODO: Use SoundPool.OnLoadCompleteListener -- BUT this requires API lvl 8.

public class BurnsNightActivity extends Activity implements AdListener {

	private static final String TAG = "BurnsNight";

	// static parameters for SoundPool
	/*
	 * private static final int MAX_STREAMS = 2; private static final int
	 * LOOP_FOREVER = -1; private static final int NO_LOOP = 0; private static
	 * final float NORMAL_RATE = 1f; private static final int PRIORITY = 0;
	 */

	private MediaPlayer mMediaPlayer; // for the poem reading
	// private MediaPlayer mBackgroundMusic;

	private MediaController mController;

	// do not show fast forward/rewind button in media controller
	private static final boolean NO_FAST_FORWARD = false;

	// Time in ms before controls hide -- 0 for never hide
	private static final int NO_HIDE_TIMEOUT = 0;

	// pause for 1.5s before showing MediaController, to avoid NPE
	private static final int WAIT_DELAY = 2500;
	
	private static final int ABOUT_DIALOG_ID = 0;

	private ImageView mBurnsPortrait;

	private AdView mAdView;

	// Interface between MediaPlayer and MediaController
	private MediaController.MediaPlayerControl mPlayerControl = new MediaController.MediaPlayerControl() {

		// Hmm. How to complete this one?
		public int getBufferPercentage() {
			return -1;
		}

		public int getCurrentPosition() {
			return mMediaPlayer.getCurrentPosition();
		}

		public int getDuration() {
			return mMediaPlayer.getDuration();
		}

		public boolean isPlaying() {
			return mMediaPlayer.isPlaying();
		}

		public void pause() {
			mMediaPlayer.pause();
		}

		public void seekTo(int pos) {
			mMediaPlayer.seekTo(pos);
		}

		public void start() {
			mMediaPlayer.start();
		}
	};

	/*
	 * private SoundPool mSoundPool; private int mBackgroundSoundId; private int
	 * mReadingSoundId;
	 */

	@Override
	public void onCreate(Bundle icicle) {
		super.onCreate(icicle);
		setContentView(R.layout.main);
		
		mBurnsPortrait = (ImageView) findViewById(R.id.ImageView01);

		//show progress alert
		//final boolean argument: whether the dialog is cancelable.
		ProgressDialog loadDialog = ProgressDialog.show(BurnsNightActivity.this, "", 
                "Loading. Please wait...", true);

		
//		AdManager.setTestDevices(new String[] { AdManager.TEST_EMULATOR });
		mAdView = (AdView) findViewById(R.id.ad);
		mAdView.requestFreshAd();

		
		// see Android SoundPool docs for "0", "1" arguments
		// short version: have no effect, use for compatibility
		/*
		 * mSoundPool = new SoundPool (MAX_STREAMS, AudioManager.STREAM_MUSIC,
		 * 0); mBackgroundSoundId = mSoundPool.load(this, R.raw.background, 1);
		 * mReadingSoundId = mSoundPool.load(this, R.raw.reading, 1);
		 */
		try {
			mMediaPlayer = MediaPlayer.create(this, R.raw.reading);
		} catch (Exception e) {
			Log.e(TAG, e.getMessage());
		}

		// Add buttons to play, pause, seek
		mController = new MediaController(this, NO_FAST_FORWARD);
		mController.setAnchorView(mBurnsPortrait);

		mController.setMediaPlayer(mPlayerControl);

		mController.setEnabled(true);

		loadDialog.dismiss();
		
		// Horrible! Delay 1.5s to allow state to 'settle'
		// If not, we get NullPointerException in mController.show()
		// see here:
		// http://groups.google.com/group/android-developers/browse_thread/thread/bd94a4a77e125a0e/f65ad7372ca8dee2
		new Handler().postDelayed(new Runnable() {

			public void run() {
				try {
					mController.show(NO_HIDE_TIMEOUT);
				} catch (Exception e) {
					Log.e(TAG, "Error: " + e.toString());
				}

			}
		}, WAIT_DELAY);

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		boolean result = super.onCreateOptionsMenu(menu);

		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.layout.menu, menu);

		return result;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		switch (item.getItemId()) {
		case R.id.about:
			showDialog(ABOUT_DIALOG_ID);
		}

		//event has been 'consumed'
		return true;

	}

	// TODO: None of this member function should be needed -- kept just in case
	// for now.
	/*
	 * private void playAudio() { try { //reading.mp3 is a PD audio file of a
	 * poem reading mMediaPlayer = MediaPlayer.create(this, R.raw.reading);
	 * mMediaPlayer.start();
	 * 
	 * //CANNOT play two concurrent sounds like this... :-/ // mBackgroundMusic
	 * = MediaPlayer.create(this, R.raw.background); //
	 * mBackgroundMusic.start(); } catch (Exception e) { Log.e(TAG, "error: " +
	 * e.getMessage(), e); }
	 * 
	 * //SoundPool approach -- apparently not suitable for large sound files
	 * AudioManager mgr = (AudioManager)
	 * getSystemService(Context.AUDIO_SERVICE);
	 * 
	 * int streamVolume = mgr.getStreamVolume(AudioManager.STREAM_MUSIC);
	 * 
	 * try { mSoundPool.play(mBackgroundSoundId, streamVolume, streamVolume,
	 * PRIORITY, LOOP_FOREVER, NORMAL_RATE); mSoundPool.play(mReadingSoundId,
	 * streamVolume, streamVolume, PRIORITY, NO_LOOP, NORMAL_RATE); } catch
	 * (Exception e) { Log.e(TAG, "Error: " + e.getMessage() ); } }
	 */

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		try {
			mController.show(NO_HIDE_TIMEOUT);
		} catch (Exception e) {
			Log.e(TAG, e.toString());
		}
		return super.onTouchEvent(event);
	}


protected Dialog onCreateDialog(int id) {
		switch (id) {
		//TODO: build a nicer "about" dialog
		case ABOUT_DIALOG_ID:
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setTitle(R.string.dialog_about);
			builder.setMessage(R.string.dialog_message);
			builder.setCancelable(true);
			
			builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
		           public void onClick(DialogInterface dialog, int id) {
		                dialog.dismiss();
		           }
		       });

			return builder.create();
		}
	return null;
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();

		if (mMediaPlayer != null) {
			mMediaPlayer.release();
			mMediaPlayer = null;
		}
		/*
		 * if (mBackgroundMusic != null) { mBackgroundMusic.release();
		 * mBackgroundMusic = null; }
		 */

		// release SoundPool resources
		/*
		 * if (mSoundPool != null) { mSoundPool.release(); mSoundPool = null; }
		 */

	}

	@Override
	public void onFailedToReceiveAd(AdView arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onFailedToReceiveRefreshedAd(AdView arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onReceiveAd(AdView arg0) {
		// TODO Auto-generated method stub
		arg0.setVisibility(View.VISIBLE);
		mAdView.setVisibility(View.VISIBLE);

	}

	@Override
	public void onReceiveRefreshedAd(AdView arg0) {
		// TODO Auto-generated method stub
		arg0.setVisibility(View.VISIBLE);
		mAdView.setVisibility(View.VISIBLE);
	}
}
