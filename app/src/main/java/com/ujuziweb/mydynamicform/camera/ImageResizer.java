package com.ujuziweb.mydynamicform.camera;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;

import android.graphics.Bitmap;

public class ImageResizer {
        
        private Bitmap resizeBitmap(Bitmap originalBitmap, int width, int height) {
                Bitmap scaledBitmap = Bitmap.createScaledBitmap(originalBitmap, width, height, false);
                return scaledBitmap;
        }
        
        private void saveBitmap(Bitmap compressedBitmap, String outputPath) throws FileNotFoundException {
                File file = new File(outputPath);
                FileOutputStream os = new FileOutputStream(file);
                compressedBitmap.compress(Bitmap.CompressFormat.JPEG, 50, os);
        }
}