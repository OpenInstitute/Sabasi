package com.ujuziweb.mydynamicform.camera;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;

public class CameraActivity extends Activity {
        public static final int  MEDIA_TYPE_IMAGE                    = 1;
        public static final int  MEDIA_TYPE_VIDEO                    = 2;
        
        private static final int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 100;
        private Uri              fileUri;
        
        /*
         * This is the id that will be used on the application to give the name
         * of the file.
         */
        private static String    idHash;
        
        @Override
        public void onCreate(Bundle savedInstanceState) {
                super.onCreate(savedInstanceState);
                
                idHash = getIntent().getExtras().getString("idHash");
                
                // create Intent to take a picture and return control to the calling application
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                
                fileUri = getOutputMediaFileUri(MEDIA_TYPE_IMAGE); // create a file to save the image
                intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri); // set the image file name   
                
                // start the image capture Intent
                startActivityForResult(intent, CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);
        }
        
        @Override
        protected void onActivityResult(
                                        int requestCode,
                                        int resultCode,
                                        Intent data) {
                FormCamera widget = FormCamera.getInstance();
                if (widget != null) {
                        widget.addPhoto(fileUri.getPath());
                }
                
                super.onActivityResult(requestCode, resultCode, data);
                finish();
        }
        
        /** Create a file Uri for saving an image or video */
        private static Uri getOutputMediaFileUri(int type) {
                return Uri.fromFile(getOutputMediaFile(type));
        }
        
        /** Create a File for saving an image or video */
        private static File getOutputMediaFile(int type) {
                // To be safe, you should check that the SDCard is mounted
                // using Environment.getExternalStorageState() before doing this.
                
                File mediaStorageDir = new File(getExternalSdCardPath() + "/inova/" + "/dados" + "/fotos/");
                // This location works best if you want the created images to be shared
                // between applications and persist after your app has been uninstalled.
                
                // Create the storage directory if it does not exist
                if (!mediaStorageDir.exists()) {
                        if (!mediaStorageDir.mkdirs()) {
                                Log.d("MyCameraApp", "failed to create directory");
                                return null;
                        }
                }
                
                String fileName;
                String dateString = new SimpleDateFormat("yyyyMMdd_HHmmssSSS").format(new Date());
                
                if (idHash != null) {
                        fileName = idHash + "_" + dateString;
                }
                else {
                        fileName = dateString;
                }
                
                File mediaFile;
                if (type == MEDIA_TYPE_IMAGE) {
                        mediaFile = new File(mediaStorageDir.getPath() + File.separator + fileName + ".jpg");
                }
                else if (type == MEDIA_TYPE_VIDEO) {
                        mediaFile = new File(mediaStorageDir.getPath() + File.separator + fileName + ".mp4");
                }
                else {
                        return null;
                }
                
                return mediaFile;
        }
        
        /**
         * ================================
         * 
         * External SD Card Path Functions
         * 
         * ================================
         */
        public static String getExternalSdCardPath() {
                File sdCardFile = null;
                String path = null;
                
                sdCardFile = getSecondaryStorage();
                path = sdCardFile.getAbsolutePath();
                
                return path;
        }
        
        /**
         * 
         * There are a lot of different kind of android versions and trademarks,
         * so, the implementation of each version are different.
         * 
         * This function verifies in a list of possibilities the path of the
         * external sd card.
         * 
         * @return sdCardFile the external sd card {@link File}
         */
        private static File getSdcardByPossiblePaths() {
                File sdCardFile = null;
                String path = null;
                
                List<String> sdCardPossiblePath = Arrays.asList("external_sd", "ext_sd", "external", "extSdCard");
                
                for (String sdPath : sdCardPossiblePath) {
                        File file = new File("/mnt/", sdPath);
                        
                        if (testFileIsWritable(file)) {
                                path = file.getAbsolutePath();
                        }
                }
                
                if (path != null) {
                        sdCardFile = new File(path);
                }
                else {
                        sdCardFile = new File(Environment.getExternalStorageDirectory().getAbsolutePath());
                }
                
                return sdCardFile;
        }
        
        /**
         * Test whether a file is writable or not.
         * 
         * @param {@link File}
         * @return {@link Boolean} isWritable
         * 
         */
        public static boolean testFileIsWritable(File file) {
                boolean isWritable = false;
                
                if (file.isDirectory() && file.canWrite()) {
                        String path = file.getAbsolutePath();
                        
                        String timeStamp = new SimpleDateFormat("ddMMyyyy_HHmmss").format(new Date());
                        File testWritable = new File(path, "test_" + timeStamp);
                        
                        if (testWritable.mkdirs()) {
                                testWritable.delete();
                                isWritable = true;
                        }
                }
                
                return isWritable;
        }
        
        /**
         * Gets the external sd card path from a system variable, if the result
         * was null, this function call the function getSdcardByPossiblePaths to
         * verify the path manually.
         * 
         * @return externalSdCard the external sd card {@link File}
         */
        private static File getSecondaryStorage() {
                String value = System.getenv("SECONDARY_STORAGE");
                File externalSdCard = null;
                
                if (!TextUtils.isEmpty(value)) {
                        String[] paths = value.split(":");
                        
                        for (String path : paths) {
                                File file = new File(path);
                                
                                if (testFileIsWritable(file)) {
                                        externalSdCard = file;
                                }
                        }
                }
                
                if (externalSdCard == null) {
                        externalSdCard = getSdcardByPossiblePaths();
                }
                
                return externalSdCard;
        }
        
}