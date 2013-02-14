package com.customcamera.utils;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;

import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Matrix;
import android.graphics.Paint;

/**
 * class contains various useful functions
 * 
 * @author qba
 * 
 */
public class CameraUtils {

	/**
	 * max picture width
	 */
	private static final int MAX_WIDTH = 400;
	/**
	 * max picture height
	 */
	private static final int MAX_HEIGHT = 300;

	/**
	 * convert int to byte array
	 * 
	 * @param value
	 *            integer value
	 * @return byte array
	 */
	public static byte[] convertIntToByte(int value) {
		byte[] temp = new byte[4];
		byte[] result = ByteBuffer.allocate(4).putInt(value).array();
		temp[0] = result[3];
		temp[1] = result[2];
		temp[2] = result[1];
		temp[3] = result[0];
		return temp;
	}

	/**
	 * convert byte array to integer
	 * 
	 * @param data
	 *            byte data
	 * @return integer value
	 */
	public static int convertByteToInt(byte[] data) {
		byte[] temp = new byte[4];
		if (data != null && data.length == 4) {
			for (int i = 0; i <= 3; i++) {
				temp[i] = data[3 - i];
			}
			return ByteBuffer.wrap(temp).getInt(0);
		}
		return 0;
	}

	/**
	 * save picture byte data to file
	 * 
	 * @param dir
	 *            directory name
	 * @param name
	 *            file name
	 * @param data
	 *            picture data
	 * @throws IOException
	 */
	public static void saveToFile(File dir, String name, byte[] data)
			throws IOException {
		File photo = new File(dir, name);
		FileOutputStream fos = new FileOutputStream(photo);
		BufferedOutputStream bos = new BufferedOutputStream(fos);
		Bitmap bm = BitmapFactory.decodeByteArray(data, 0, data.length);
		bm = convertToGrayscale(bm);
		bm.compress(CompressFormat.JPEG, 50, bos);
		bos.close();
		fos.flush();
		fos.close();
	}

	/**
	 * save bitmap to file
	 * 
	 * @param dir
	 *            directory name
	 * @param name
	 *            file name
	 * @param bitmap
	 *            picture
	 * @throws IOException
	 */
	public static void saveToFile(File dir, String name, Bitmap bitmap)
			throws IOException {
		File photo = new File(dir, name);
		FileOutputStream fos = new FileOutputStream(photo);
		BufferedOutputStream bos = new BufferedOutputStream(fos);
		bitmap.compress(CompressFormat.JPEG, 90, bos);
		bos.close();
		fos.flush();
		fos.close();
	}

	/**
	 * convert bitmap to grey scale
	 * 
	 * @param bm
	 *            bitmap we want to convert
	 * @return bitmap in greyscale
	 */
	public static Bitmap convertToGrayscale(Bitmap bm) {
		Bitmap result = Bitmap.createBitmap(bm.getWidth(), bm.getHeight(),
				Bitmap.Config.RGB_565);
		Canvas c = new Canvas(result);
		Paint paint = new Paint();
		ColorMatrix cm = new ColorMatrix();
		cm.setSaturation(0);
		ColorMatrixColorFilter f = new ColorMatrixColorFilter(cm);
		paint.setColorFilter(f);
		c.drawBitmap(bm, 0, 0, paint);
		bm.recycle();
		return result;
	}

	/**
	 * rotate bitmap
	 * 
	 * @param source
	 *            bitmap to rotate
	 * @param rotation
	 *            rotation in degrees
	 * @return rotated bitmap
	 */
	public static Bitmap rotateBitmap(Bitmap source, float rotation) {
		Matrix matrix = new Matrix();
		matrix.postRotate(rotation);
		Bitmap result = Bitmap.createBitmap(source, 0, 0, source.getWidth(),
				source.getHeight(), matrix, false);
		return result;
	}

	/**
	 * scale bitmap to fit to MAX_WIDTH x MAX_HEIGHT bounding box
	 * 
	 * @param source
	 *            bitmap we want to scale
	 * @return scaled bitmap
	 */
	public static Bitmap scaleBitmap(Bitmap source) {
		float scale = Math.min(MAX_WIDTH / (float) source.getWidth(),
				MAX_HEIGHT / (float) source.getHeight());
		Matrix matrix = new Matrix();
		matrix.postScale(scale, scale);
		Bitmap result = Bitmap.createBitmap(source, 0, 0, source.getWidth(),
				source.getHeight(), matrix, false);
		return result;
	}

	/**
	 * rotate and scale bitmap in one operation
	 * 
	 * @param source
	 *            bitmap we want to scale and rotate
	 * @param rotation
	 *            rotation in degrees
	 * @return transformed bitmap
	 */
	public static Bitmap rotateAndScaleBitmap(Bitmap source, float rotation,
			int maxWidth, int maxHeight) {
		float scale;
		if (maxHeight != 0 && maxWidth != 0) {
			scale = Math.min(maxWidth / (float) source.getWidth(), maxHeight
					/ (float) source.getHeight());
		} else {
			scale = Math.min(MAX_WIDTH / (float) source.getWidth(), MAX_HEIGHT
					/ (float) source.getHeight());
		}
		Matrix matrix = new Matrix();
		matrix.postScale(scale, scale);
		matrix.postRotate(rotation);
		Bitmap result = Bitmap.createBitmap(source, 0, 0, source.getWidth(),
				source.getHeight(), matrix, false);
		return result;
	}


	
}
