package com.komamitsu.ocrsample;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.DisplayMetrics;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Toast;

import com.googlecode.tesseract.android.TessBaseAPI;

public class MainActivity extends Activity {
  private static final int REQUEST_GALLERY = 0;
  private static final int REQUEST_CAMERA = 1;

  private static final String TAG = MainActivity.class.getSimpleName();

  private TessBaseAPI baseAPI;
  private Uri imageUri;

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);

    baseAPI = new TessBaseAPI();
    File externalStorageDirectory = Environment.getExternalStorageDirectory();

    File appDir = new File(externalStorageDirectory, "ocrsample");
    if (!appDir.isDirectory())
      appDir.mkdir();

    final File baseDir = new File(appDir, "tessdata");
    if (!baseDir.isDirectory())
      baseDir.mkdir();

    baseAPI.init(appDir.getPath(), "eng");
    findViewById(R.id.choose_from_gallery).setOnClickListener(new OnClickListener() {

      @Override
      public void onClick(View v) {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, REQUEST_GALLERY);
      }
    });

    findViewById(R.id.take_a_photo).setOnClickListener(new OnClickListener() {

      @Override
      public void onClick(View v) {
        String filename = System.currentTimeMillis() + ".jpg";

        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.TITLE, filename);
        values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg");
        imageUri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);

        Intent intent = new Intent();
        intent.setAction(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
        startActivityForResult(intent, REQUEST_CAMERA);
      }
    });
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    getMenuInflater().inflate(R.menu.activity_main, menu);
    return true;
  }

  private void inspectFromBitmap(Bitmap bitmap) {
    baseAPI.setPageSegMode(TessBaseAPI.AVS_MOST_ACCURATE);
    baseAPI.setPageSegMode(TessBaseAPI.PSM_SINGLE_LINE);
    baseAPI.setImage(bitmap);
    String text = baseAPI.getUTF8Text();
    Toast.makeText(this, text, Toast.LENGTH_LONG).show();
    bitmap.recycle();
  }

  private void inspect(Uri uri) {
    InputStream is = null;
    try {
      is = getContentResolver().openInputStream(uri);
      Options options = new BitmapFactory.Options();
      options.inPreferredConfig = Bitmap.Config.ARGB_8888;
      options.inSampleSize = 2;
      options.inScreenDensity = DisplayMetrics.DENSITY_LOW;
      Bitmap bitmap = BitmapFactory.decodeStream(is, null, options);
      inspectFromBitmap(bitmap);
    } catch (FileNotFoundException e) {
      e.printStackTrace();
    } finally {
      if (is != null) {
        try {
          is.close();
        } catch (IOException e) {
        }
      }
    }
  }

  @Override
  protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    switch (requestCode) {
    case REQUEST_GALLERY:
      if (resultCode == RESULT_OK) {
        inspect(data.getData());
      }
      break;
    case REQUEST_CAMERA:
      if (resultCode == RESULT_OK) {
        if (imageUri != null) {
          inspect(imageUri);
        }
      }
      break;
    default:
      super.onActivityResult(requestCode, resultCode, data);
      break;
    }
  }
}
