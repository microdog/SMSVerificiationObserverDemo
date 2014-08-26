package com.example.smsobserver;

import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.LinearLayout;
import android.widget.TextView;

public class MainActivity extends ActionBarActivity {

	private LinearLayout mLinearLayout;

	/**
	 * SMS observation members
	 */
	private Handler mHandler;
	private SMSResultReceiver mResultReceiver;
	private SMSVerficationObserver mSmsObserver;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		mLinearLayout = (LinearLayout) findViewById(R.id.linearLayout);

		// Init members
		mHandler = new Handler();
		mResultReceiver = new SMSResultReceiver(mHandler);
		mSmsObserver = new SMSVerficationObserver(mHandler, this,
				mResultReceiver);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	protected void onPause() {
		// Unregister SMS content observer
		getContentResolver().unregisterContentObserver(mSmsObserver);

		super.onPause();
	}

	@Override
	protected void onResume() {
		super.onResume();

		// Register SMS content observer
		getContentResolver().registerContentObserver(
				Uri.parse("content://sms/"), true, mSmsObserver);
	}

	/**
	 * SMS result receiver
	 * 
	 * @author microdog
	 * 
	 */
	private class SMSResultReceiver extends ResultReceiver {

		public SMSResultReceiver(Handler handler) {
			super(handler);
		}

		@Override
		protected void onReceiveResult(int resultCode, Bundle resultData) {
			TextView textView = new TextView(MainActivity.this);
			textView.setText("Code: " + resultData.getString("code"));
			mLinearLayout.addView(textView);
		}

	}
}
