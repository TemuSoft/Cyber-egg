package com.CyberEgg.CE0808;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class FileAccess {
    String file_extension = ".jpg";
    boolean file_selected;
    String fileName;
    private final AppCompatActivity activity;
    private Uri cameraImageUri;
    private File photoFile;
    private ActivityResultLauncher<Intent> imagePickerLauncher;
    private ActivityResultLauncher<Uri> takePictureLauncher;

    public FileAccess(AppCompatActivity activity) {
        this.activity = activity;
    }

    public void showImagePickerDialog(String file_name) {
        String[] options = {"Choose from Gallery", "Take Photo", "Rest to default"};
        new AlertDialog.Builder(activity).setTitle("Select Option")
                .setItems(options, (dialog, which) -> {
                    if (which == 0) {
                        pickImageFromGallery();
                    } else if (which == 1) {
                        requestCameraPermission();
                    } else {
                        SharedPreferences sharedPreferences;
                        SharedPreferences.Editor editor;
                        sharedPreferences = activity.getSharedPreferences("o0ur90dggam", activity.MODE_PRIVATE);
                        editor = sharedPreferences.edit();

                        delete_profile(file_name);
                        editor.remove(file_name);
                        editor.apply();

                        Intent intent = new Intent(activity, activity.getClass());
                        activity.startActivity(intent);
                        activity.overridePendingTransition(0, 0);
                        activity.finish();
                    }
                }).show();
    }

    private void pickImageFromGallery() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (hasPermission(Manifest.permission.READ_MEDIA_IMAGES)) {
                openGallery();
            } else {
                requestPermission(Manifest.permission.READ_MEDIA_IMAGES);
            }
        } else {
            if (hasPermission(Manifest.permission.READ_EXTERNAL_STORAGE)) {
                openGallery();
            } else {
                requestPermission(Manifest.permission.READ_EXTERNAL_STORAGE);
            }
        }
    }

    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        imagePickerLauncher.launch(intent);
    }

    private void requestCameraPermission() {
        if (hasPermission(Manifest.permission.CAMERA)) {
            takePhotoFromCamera();
        } else {
            requestPermission(Manifest.permission.CAMERA);
        }
    }

    private boolean hasPermission(String permission) {
        return ContextCompat.checkSelfPermission(activity, permission) == PackageManager.PERMISSION_GRANTED;
    }

    private void requestPermission(String permission) {
        ActivityCompat.requestPermissions(activity, new String[]{permission}, 101);
    }

    private void takePhotoFromCamera() {
        try {
            photoFile = createImageFile();
            cameraImageUri = FileProvider.getUriForFile(activity,
                    activity.getPackageName() + ".provider", photoFile);
            takePictureLauncher.launch(cameraImageUri);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void registerLaunchers(AppCompatActivity activity, ImageView imageView) {
        imagePickerLauncher = activity.registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(), result -> {
                    if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                        Uri selectedImageUri = result.getData().getData();

                        if (!isValidImage(selectedImageUri)) return;

                        String mimeType = activity.getContentResolver().getType(selectedImageUri);
                        file_extension = mimeType.equals("image/png") ? ".png" : ".jpg";

                        try {
                            Bitmap bitmap = MediaStore.Images.Media.getBitmap(activity.getContentResolver(), selectedImageUri);
                            bitmap = rotateBitmapIfRequired(bitmap, selectedImageUri);
                            imageView.setImageBitmap(bitmap);
                            saveImage(bitmap);
                            file_selected = true;
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                });

        takePictureLauncher = activity.registerForActivityResult(
                new ActivityResultContracts.TakePicture(), result -> {
                    if (result) {
                        if (!isValidCapturedImage(photoFile)) return;
                        file_extension = ".jpg";

                        try {
                            Bitmap bitmap = BitmapFactory.decodeStream(activity.getContentResolver().openInputStream(cameraImageUri));
                            bitmap = rotateBitmapIfRequired(bitmap, cameraImageUri);
                            imageView.setImageBitmap(bitmap);
                            saveImage(bitmap);
                            file_selected = true;
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                });
    }

    private File createImageFile() throws IOException {
        File storageDir = activity.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        return File.createTempFile(fileName, file_extension, storageDir);
    }

    private void saveImage(Bitmap bitmap) {
        File directory = new File(activity.getFilesDir(), "saved_images");
        if (!directory.exists()) {
            directory.mkdir();
        }

        File file = new File(directory, fileName + file_extension);

        if (file.exists())
            delete_profile(fileName);

        try (FileOutputStream fos = new FileOutputStream(file)) {
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Bitmap loadImageFromInternalStorage(String file_name) {
        set_file_extension();
        File directory = new File(activity.getFilesDir(), "saved_images");
        if (!directory.exists()) {
            directory.mkdir();
        }

        File file = new File(directory, file_name + file_extension);
        String path = file.getAbsolutePath();
        Bitmap bitmap = null;
        try {
            file = new File(path);
            if (file.exists()) {
                bitmap = BitmapFactory.decodeFile(file.getAbsolutePath());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return bitmap;
    }

    public void delete_profile(String file_name) {
        File directory = new File(activity.getFilesDir(), "saved_images");
        File file = new File(directory, file_name + file_extension);
        if (file.exists())
            file.delete();
    }

    private Bitmap rotateBitmapIfRequired(Bitmap bitmap, Uri imageUri) throws IOException {
        InputStream input = activity.getContentResolver().openInputStream(imageUri);
        ExifInterface exif = new ExifInterface(input);
        int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
        input.close();

        Matrix matrix = new Matrix();
        switch (orientation) {
            case ExifInterface.ORIENTATION_ROTATE_90:
                matrix.postRotate(90);
                break;
            case ExifInterface.ORIENTATION_ROTATE_180:
                matrix.postRotate(180);
                break;
            case ExifInterface.ORIENTATION_ROTATE_270:
                matrix.postRotate(270);
                break;
            default:
                return bitmap;
        }

        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
    }

    private boolean isValidImage(Uri imageUri) {
        String mimeType = activity.getContentResolver().getType(imageUri);
        if (mimeType == null) return false;

        if (!(mimeType.equals("image/jpeg") || mimeType.equals("image/png"))) {
            Toast.makeText(activity, "Only JPG and PNG formats are supported", Toast.LENGTH_SHORT).show();
            return false;
        }

        Cursor returnCursor = activity.getContentResolver().query(imageUri, null, null, null, null);
        int sizeIndex = returnCursor.getColumnIndex(OpenableColumns.SIZE);
        returnCursor.moveToFirst();
        long fileSize = returnCursor.getLong(sizeIndex);
        returnCursor.close();

        if (fileSize > 10 * 1024 * 1024) { // 10 MB in bytes
            Toast.makeText(activity, "Image must be less than 1MB", Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }

    private boolean isValidCapturedImage(File file) {
        String name = file.getName().toLowerCase();
        if (!(name.endsWith(".jpg") || name.endsWith(".jpeg") || name.endsWith(".png"))) {
            Toast.makeText(activity, "Only JPG and PNG formats are supported", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (file.length() > 10 * 1024 * 1024) {
            Toast.makeText(activity, "Image must be less than 1MB", Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }

    public void set_file_extension() {
        File directory = new File(activity.getFilesDir(), "saved_images");

        File jpgFile = new File(directory, fileName + ".jpg");
        File pngFile = new File(directory, fileName + ".png");
        file_extension = ".jpg";

         if (pngFile.exists()) {
            file_extension = ".png";
        }
    }

}
