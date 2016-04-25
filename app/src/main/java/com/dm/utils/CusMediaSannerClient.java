package com.dm.utils;

import java.io.File;

import android.media.MediaScannerConnection;
import android.net.Uri;
import android.util.Log;

public class CusMediaSannerClient implements MediaScannerConnection.MediaScannerConnectionClient {

	public File mFile;
	public MediaScannerConnection mMediaScanConn = null;
	
	public CusMediaSannerClient(File file){
		mFile = file;
	}
	
	public void setMediaScannerConnection(MediaScannerConnection mediaScanConn){
		mMediaScanConn = mediaScanConn;
	}
	
	@Override
	public void onMediaScannerConnected() {

		mMediaScanConn.scanFile(mFile.getAbsolutePath(), null);
		
/*		if (filePath != null) {

			if (filePath.isDirectory()) {
				File[] files = filePath.listFiles();
				if (files != null) {
					for (int i = 0; i < files.length; i++) {
						if (files[i].isDirectory())
							scanfile(files[i]);
						else {
							mediaScanConn.scanFile(files[i].getAbsolutePath(),
									fileType);
						}
					}
				}
			}
		}

		filePath = null;
		fileType = null;*/

	}
	
	@Override
	public void onScanCompleted(String path, Uri uri) {
		mMediaScanConn.disconnect();
	}

}
