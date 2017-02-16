package com.ujuziweb.mydynamicform.camera;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.ujuziweb.mydynamicform.R;
import com.ujuziweb.mydynamicform.R.string;
import com.ujuziweb.mydynamicform.FormWidget;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.widget.Button;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;

public class FormCamera extends FormWidget implements Serializable {
        private Button               cameraButton;
        protected int                _priority;
        
        private static FormCamera    instance;
        private Context              context;
        
        List<String>                 photos = new ArrayList<String>();
        String                       idHash;
        
        private HorizontalScrollView horizontalScrollView;
        private LinearLayout         photosPreview;
        
        public FormCamera(Context context, String propertyName) {
                super(context, "");
                
                this.context = context;
                instance = this;
                
                cameraButton = new Button(context);
                cameraButton.setText(propertyName);
                
                Drawable icon = context.getResources().getDrawable(R.drawable.ic_action_camera);
                cameraButton.setCompoundDrawablesWithIntrinsicBounds(null, null, null, icon);
                
                setOnCameraButtonClickListener();
                
                _layout.addView(cameraButton);
                
                initializePhotosPreviewLayout();
        }
        
        @Override
        public String getPropertyName() {
                return "photos";
        }
        
        public void initializePhotosPreviewLayout() {
                photosPreview = new LinearLayout(context);
                photosPreview.setOrientation(LinearLayout.HORIZONTAL);
                
                horizontalScrollView = new HorizontalScrollView(context);
                horizontalScrollView.addView(photosPreview);
                
                _layout.addView(horizontalScrollView);
        }
        
        public void setOnCameraButtonClickListener() {
                cameraButton.setOnClickListener(new OnClickListener() {
                        @Override
                        public void onClick(View v) {
                                Intent intent = new Intent(context, CameraActivity.class);
                                intent.putExtra("idHash", idHash);
                                context.startActivity(intent);
                        }
                });
        }
        
        /**
         * Callback that will be called when the picture was taken, add the
         * photo to array and updates the UI of the preview photos
         * 
         * @param path
         */
        public synchronized void addPhoto(String photo) {
                List<String> newList = new ArrayList<String>();
                newList.addAll(photos);
                newList.add(photo);
                
                setPhotos(newList);
                updatePhotos();
        }
        
        public synchronized void updatePhotos() {
                photosPreview.removeAllViews();
                
                for (String photoPath : photos) {
                        File file = new File(photoPath);
                        boolean exists = file.exists();
                        
                        if (exists) {
                                ImageView imageView = new ImageView(context);
                                imageView.setPadding(10, 10, 10, 10);
                                
                                Bitmap bitmap = decodeSampledBitmapFromResource(photoPath, 200, 200);
                                imageView.setImageBitmap(bitmap);
                                
                                addEventToImageView(imageView, photoPath);
                                
                                photosPreview.addView(imageView);
                        }
                }
                
                verifyPhotos();
        }
        
        public synchronized void verifyPhotos() {
                List<String> newList = new ArrayList<String>();
                Log.e("Count das photos: ", "" + newList.size());
                
                for (String photo : photos) {
                        boolean exists = new File(photo).exists();
                        
                        if (exists) {
                                newList.add(photo);
                        }
                }
                Log.e("Count das photos 2: ", "" + newList.size());
                
                setPhotos(newList);
        }
        
        public ImageView createImageView(String photoPath) {
                ImageView imageView = new ImageView(context);
                imageView.setPadding(10, 10, 10, 10);
                
                Bitmap bitmap = decodeSampledBitmapFromResource(photoPath, 200, 200);
                imageView.setImageBitmap(bitmap);
                
                addEventToImageView(imageView, photoPath);
                
                return imageView;
        }
        
        public void addEventToImageView(
                                        ImageView imageView,
                                        final String photoPath) {
                
                imageView.setOnClickListener(new OnClickListener() {
                        @Override
                        public void onClick(View v) {
                                Uri uri = Uri.fromFile(new File(photoPath));
                                
                                Intent intent = new Intent();
                                intent.setDataAndType(uri, "image/*");
                                intent.setAction(Intent.ACTION_VIEW);
                                context.startActivity(intent);
                        }
                });
                
                imageView.setOnLongClickListener(new OnLongClickListener() {
                        @Override
                        public boolean onLongClick(View v) {
                                showRemovePictureMessage(photoPath);
                                return true;
                        }
                });
        }
        
        private void showRemovePictureMessage(String photoPath) {
                final File photoFile = new File(photoPath);
                
                if (photoFile.exists()) {
                        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
                        alertDialogBuilder.setTitle(string.lbl_caution);
                        alertDialogBuilder.setMessage(string.lbl_remove_photo).setCancelable(false).setPositiveButton(string.lbl_yes, new DialogInterface.OnClickListener() {
                                public void onClick(
                                                    DialogInterface dialog,
                                                    int id) {
                                        photoFile.delete();
                                        updatePhotos();
                                        dialog.cancel();
                                }
                        }).setNegativeButton(string.lbl_no, new DialogInterface.OnClickListener() {
                                public void onClick(
                                                    DialogInterface dialog,
                                                    int id) {
                                        dialog.cancel();
                                }
                        });
                        
                        AlertDialog alertDialog = alertDialogBuilder.create();
                        alertDialog.show();
                        
                }
        }
        
        public static FormCamera getInstance() {
                return instance;
        }
        
        public static Bitmap decodeSampledBitmapFromResource(
                                                             String path,
                                                             int reqWidth,
                                                             int reqHeight) {
                
                // First decode with inJustDecodeBounds=true to check dimensions
                final BitmapFactory.Options options = new BitmapFactory.Options();
                options.inJustDecodeBounds = true;
                BitmapFactory.decodeFile(path, options);
                
                // Calculate inSampleSize
                options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);
                
                // Decode bitmap with inSampleSize set
                options.inJustDecodeBounds = false;
                return BitmapFactory.decodeFile(path, options);
        }
        
        public static int calculateInSampleSize(
                                                BitmapFactory.Options options,
                                                int reqWidth,
                                                int reqHeight) {
                // Raw height and width of image
                final int height = options.outHeight;
                final int width = options.outWidth;
                int inSampleSize = 1;
                
                if (height > reqHeight || width > reqWidth) {
                        
                        final int halfHeight = height / 2;
                        final int halfWidth = width / 2;
                        
                        // Calculate the largest inSampleSize value that is a power of 2 and keeps both
                        // height and width larger than the requested height and width.
                        while ((halfHeight / inSampleSize) > reqHeight && (halfWidth / inSampleSize) > reqWidth) {
                                inSampleSize *= 2;
                        }
                }
                
                return inSampleSize;
        }
        
        public String getIdHash() {
                return idHash;
        }
        
        public void setIdHash(String idHash) {
                this.idHash = idHash;
        }
        
        public List<String> getPhotos() {
                return photos;
        }
        
        public synchronized void setPhotos(List<String> photos) {
                this.photos = photos;
        }
}
