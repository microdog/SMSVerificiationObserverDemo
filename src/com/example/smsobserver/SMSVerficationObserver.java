package com.example.smsobserver;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.content.Context;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;
import android.util.Log;
import com.example.smsobserver.R;

/**
 * @author microdog
 * 
 */
public class SMSVerficationObserver extends ContentObserver {

	private static final String TAG = "SMSVerficationObserver";

	private Context mContext;
	private ResultReceiver mReceiver;
	private Pattern mPattern;

	public SMSVerficationObserver(Handler handler, Context context,
			ResultReceiver receiver) {
		super(handler);
		mContext = context;
		mReceiver = receiver;

		// Compile matching pattern
		Log.d(TAG, "Regexp: " + context.getString(R.string.sms_pattern));
		mPattern = Pattern.compile(context.getString(R.string.sms_pattern));
	}

	@Override
	public void onChange(boolean selfChange) {
		super.onChange(selfChange);

		// Get unread SMS from inbox sorted by _id desc
		Cursor cursor = mContext.getContentResolver().query(
				Uri.parse("content://sms/inbox"), null, "read = ?",
				new String[] { "0" }, "_id desc limit 1");

		if (cursor.moveToFirst()) {
			String body = cursor.getString(cursor.getColumnIndex("body"));
			Log.d(TAG, "SMS body: " + body);

			if (body.contains(mContext.getString(R.string.sms_signature))) {
				Log.d(TAG, "Signature detected: " + body);

				// Match verification code
				Matcher matcher = mPattern.matcher(body);
				if (matcher.find()) {
					Log.d(TAG, "Pattern matched: " + body);
					Log.d(TAG, "Code extracted: " + matcher.group(1));

					Bundle bundle = new Bundle();
					bundle.putString("code", matcher.group(1));
					mReceiver.send(0, bundle);
				}
			}
		}
		cursor.close();
	}
}
