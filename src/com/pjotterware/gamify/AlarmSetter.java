package com.pjotterware.gamify;

import java.util.Calendar;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.TimePicker;
import android.widget.Toast;
import android.widget.ToggleButton;

public class AlarmSetter extends Activity
{

  public static final String PREFS_NAME    = "MyPrefsFile";
  public static final String ALARM_ENABLED = "ALARM_ENABLED";
  public static final String ALARM_HOUR    = "ALARM_HOUR";
  public static final String ALARM_MINUTE  = "ALARM_MIN";
  public static final String SNOOZE_MINUTE = "SNOOZE_MIN";

  int                        alarmHour;
  int                        alarmMin;
  boolean                    alarmEnabled;
  ToggleButton               enableCheckBox;
  TimePicker                 alarmTimePicker;

  /** Called when the activity is first created. */
  @Override
  public void onCreate(Bundle savedInstanceState)
  {
    super.onCreate(savedInstanceState);

    // set Layout
    setContentView(R.layout.alarm_setter);

    // Assign UI components to local variables
    enableCheckBox = (ToggleButton) findViewById(R.id.toggleButton1);
    alarmTimePicker = (TimePicker) findViewById(R.id.timePicker1);
    enableCheckBox.setOnClickListener(new AlarmEnableListener());

    // Fetch Saved settings (Alarm Hour, Minute, Enable) and assign values
    // accordingly
    SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
    alarmEnabled = settings.getBoolean(ALARM_ENABLED, false);
    alarmHour = settings.getInt(ALARM_HOUR, 12);
    alarmMin = settings.getInt(ALARM_MINUTE, 0);

    // Set the Time Picker's Hour, Minute, AM/PM
    alarmTimePicker.setIs24HourView(true);
    alarmTimePicker.setCurrentHour(alarmHour);
    alarmTimePicker.setCurrentMinute(alarmMin);

    // Sets the property, doesn't cause OnClick Event to fire
    enableCheckBox.setChecked(alarmEnabled);
  }

  /** Called to set/cancel an alarm. */
  public void setAlarm(boolean alarmEnabled, int alarmHour, int alarmMin)
  {
    if (alarmEnabled)
    {
      turnAlarmOn(alarmHour, alarmMin);
    }
    else
    {
      turnAlarmoff();
    }
  }

  private void turnAlarmOn(int alarmHour, int alarmMin)
  {
    // Build Intent/Pending Intent for setting the alarm
    Intent alarmIntent = new Intent(AlarmSetter.this, AlarmReceiver.class);
    AlarmManager almMgr = (AlarmManager) getSystemService(ALARM_SERVICE);
    PendingIntent sender = PendingIntent.getBroadcast(AlarmSetter.this, 0, alarmIntent, 0);

    // Build Calendar object with Alarm Time and use it to set the alarm
    Calendar calendar = Calendar.getInstance();
    int curHour = calendar.get(Calendar.HOUR_OF_DAY);
    int curMin = calendar.get(Calendar.MINUTE);

    calendar.set(Calendar.HOUR_OF_DAY, alarmHour);
    calendar.set(Calendar.MINUTE, alarmMin);
    calendar.set(Calendar.SECOND, 0);
    if (alarmHour < curHour || (alarmHour == curHour && alarmMin <= curMin))
    {
      calendar.add(Calendar.HOUR_OF_DAY, 24);

    }
    almMgr.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), sender); // Build
    // the
    // Strings
    // for
    // displaying
    // the
    // alarm
    // time
    // through
    // Toast
    int calendarHour = calendar.get(Calendar.HOUR_OF_DAY);
    int calendarMin = calendar.get(Calendar.MINUTE);
    String calendarHourStr = Integer.toString(calendarHour);
    String calendarMinStr = Integer.toString(calendarMin);
    if (calendarMin < 10)
    {
      calendarMinStr = "0" + calendarMinStr;
    }
    Toast.makeText(this, "Alarm Set For " + calendarHourStr + ":" + calendarMinStr, Toast.LENGTH_LONG).show(); // Save
    // settings
    // Enabled,
    // Alarm
    // Hour,
    // Alarm
    // Minute
    SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
    SharedPreferences.Editor editor = settings.edit();
    editor.putBoolean(ALARM_ENABLED, true);
    editor.putInt(ALARM_HOUR, alarmHour);
    editor.putInt(ALARM_MINUTE, alarmMin);
    // Commit the edits
    editor.commit();
  }

  private void turnAlarmoff()
  {
    // Build Intent/Pending Intent for canceling the alarm
    Intent alarmIntent = new Intent(AlarmSetter.this, AlarmReceiver.class);
    AlarmManager almMgr = (AlarmManager) getSystemService(ALARM_SERVICE);
    PendingIntent sender = PendingIntent.getBroadcast(AlarmSetter.this, 0, alarmIntent, 0);
    almMgr.cancel(sender);
    // Display Alarm Disabled Message
    Toast.makeText(AlarmSetter.this, "Alarm Disabled", Toast.LENGTH_LONG).show();
    // Save setting Enabled
    SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
    SharedPreferences.Editor editor = settings.edit();
    editor.putBoolean(ALARM_ENABLED, false);
    // Commit the edits
    editor.commit();
  }

  /**
   * Class for implementing the Enable's Check Box // Click Event Listener
   */
  public class AlarmEnableListener implements ToggleButton.OnClickListener
  {
    @Override
    public void onClick(View v)
    { // Read State of UI components and call
      // SetAlarm routine
      alarmEnabled = enableCheckBox.isChecked();
      alarmHour = alarmTimePicker.getCurrentHour();
      alarmMin = alarmTimePicker.getCurrentMinute();
      setAlarm(alarmEnabled, alarmHour, alarmMin);
    }
  }

}