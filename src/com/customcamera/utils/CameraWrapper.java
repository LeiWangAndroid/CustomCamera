package com.customcamera.utils;

import android.hardware.Camera.Parameters;

public class CameraWrapper {

	private static boolean zoomAvaible = false;

	static {
		zoomAvaible = android.os.Build.VERSION.SDK_INT > 7;
	}

	/* calling here forces class initialization */
	public static void checkAvailable() {
	}

	public static boolean isZoomSupported(Parameters params) {
		if (zoomAvaible) {
			//return params.isZoomSupported();
		} else {
			return false;
		}
		return zoomAvaible;
	}

	public static int getMaxZoom(Parameters params) {
		if (zoomAvaible) {
			//return params.getMaxZoom();
		} else {
			return -1;
		}
		return 0;
	}

	public static void setZoom(Parameters params, int zoomValue) {
		if (zoomAvaible) {
			//params.setZoom(zoomValue);
		}
	}

}
