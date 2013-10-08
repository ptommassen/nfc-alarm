package com.pjotterware.gamify;

import java.io.FileDescriptor;
import java.io.IOException;
import java.util.concurrent.atomic.AtomicReference;

import android.app.Notification;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.AssetFileDescriptor;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.IBinder;

public class AlarmService extends Service {
	private static final int NOTIFICATION_ID = 1;

	private final IBinder _binder = new AlarmBinder();
	private AtomicReference<MediaPlayer> mediaPlayer = new AtomicReference<MediaPlayer>();

	public class AlarmBinder extends Binder {
		AlarmService getService() {
			return AlarmService.this;
		}
	}

	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();
	}

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
	}

	@Override
	public IBinder onBind(Intent arg0) {
		return _binder;
	}

	public void startAlarm() {
		if (mediaPlayer.compareAndSet(null, new MediaPlayer())) {

			
			// move the service to the foreground while the alarm is making noise
			Notification notification = new Notification.Builder(this)
					.setSmallIcon(R.drawable.notification)
					.setContentTitle("Start waking up..!").getNotification();
			startForeground(NOTIFICATION_ID, notification);
			
			MediaPlayer player = mediaPlayer.get();
			try {
				AssetFileDescriptor openFd = getAssets().openFd("trolo.mp3");
				FileDescriptor fd = openFd
						.getFileDescriptor();
				player.setDataSource(fd,openFd.getStartOffset(), openFd.getLength());
				player.setAudioStreamType(AudioManager.STREAM_ALARM);
				player.setLooping(true);
				player.prepare();
				player.start();
			} catch (IOException e) {
			}
		}
	}

	public void stopAlarm() {
		stopForeground(true);
		MediaPlayer old = mediaPlayer.getAndSet(null);
		if (old != null) {

			SharedPreferences settings = getSharedPreferences(
					AlarmSetter.PREFS_NAME, 0);
			SharedPreferences.Editor editor = settings.edit();
			editor.putBoolean(AlarmSetter.ALARM_ENABLED, false);
			// Commit the edits
			editor.commit();

			old.stop();
			old.release();
			stopSelf();
		}

	}
}
