package com.pjotterware.gamify;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.widget.Toast;

public class AlarmActivity extends Activity {
	public static final String STARTING = "STARTING";

	private AlarmService _alarmService;

	private ServiceConnection connection = new ServiceConnection() {

		@Override
		public void onServiceConnected(ComponentName name, IBinder service) {
			_alarmService = ((AlarmService.AlarmBinder) service).getService();

			// find out if we were started to start or stop the alarm ;)
			Intent intent = getIntent();

			// checked in at location
			if (intent.getType() != null
					&& intent.getType().equals(MimeTypes.LOCATION_MIME)) {

				_alarmService.stopAlarm();
				Toast.makeText(AlarmActivity.this, "Alarm stopped!",
						Toast.LENGTH_LONG).show();
			} else if (intent.getBooleanExtra(STARTING, false)) {
				_alarmService.startAlarm();
				Toast.makeText(AlarmActivity.this, "WAKE UP!",
						Toast.LENGTH_LONG).show();
			} else
				throw new RuntimeException(
						"Unknown why this is being started! >_O");

			// immediately finish this activity, cause it is stupid :')
			finish();

		}

		@Override
		public void onServiceDisconnected(ComponentName name) {
			_alarmService = null;
		}
	};

	protected void onCreate(android.os.Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		startService(new Intent(this, AlarmService.class));
		bindService(new Intent(this, AlarmService.class), connection,
				Context.BIND_AUTO_CREATE);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();

		unbindService(connection);
	}
}
