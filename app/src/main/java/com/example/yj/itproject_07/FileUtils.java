/*
 ********************************************************************************
 * Copyright (c) 2012 Samsung Electronics, Inc.
 * All rights reserved.
 *
 * This software is a confidential and proprietary information of Samsung
 * Electronics, Inc. ("Confidential Information"). You shall not disclose such
 * Confidential Information and shall use it only in accordance with the terms
 * of the license agreement you entered into with Samsung Electronics.
 ********************************************************************************
 */
package com.example.yj.itproject_07;


import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

/**
 * File tools class.
 * 
 */
public final class FileUtils {

	
	
	/**
	 * The best quality <b>100</b>.
	 */
	private static final int BEST_QUALITY = 100;

	/**
	 * Hide constructor.
	 */
	private FileUtils() {

	}

	/**
	 * Saves bitmap to temporary file.
	 * 
	 * @param bitmap
	 *            bitmap which will be save
	 * @throws IOException
	 */
	public static void saveBitmap(Bitmap bitmap) {

		final String extStorageDirectory = Environment.getExternalStorageDirectory().toString();

		final File file = new File(extStorageDirectory, "TEMP.jpg");
		OutputStream outStream = null;
		try {
			outStream = new FileOutputStream(file);
			bitmap.compress(Bitmap.CompressFormat.JPEG, BEST_QUALITY, outStream);
			outStream.flush();
		} catch (final IOException e) {
			throw new RuntimeException(e);
		} finally {
			if (outStream != null) {
				try {
					outStream.close();
				} catch (final IOException e) {
					throw new RuntimeException(e);
				}
			}
		}
	}

	/**
	 * Loads bitmap from temporary file.
	 * 
	 * @return bitmap from temporary file
	 */
	static Bitmap result=null;
	public static Bitmap loadBitmap() {
		Log.d("Key","loadBitmap1");
		final String extStorageDirectory = Environment.getExternalStorageDirectory().toString();
		final File file = new File(extStorageDirectory, "TEMP.jpg");
		Log.d("Key","loadBitmap2");
		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inSampleSize = 2;
		result = BitmapFactory.decodeFile(file.getAbsolutePath(),options);
		Log.d("Key","loadBitmap3");
		return result;
		
		
			
		
	}
	/**
	 * Returns URI to temporary file.
	 * 
	 * @return URI to temporary file
	 */
	public static Uri getTempUri() {
		final String extStorageDirectory = Environment.getExternalStorageDirectory().toString();
		final File file = new File(extStorageDirectory, "TEMP.jpg");
		return Uri.fromFile(file);
	}
}
