package edeveloping.com.sharememe.asynctaskes;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

import org.apache.http.util.ByteArrayBuffer;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

public  class postToInstagram extends AsyncTask<String, String, String>{
	
	ProgressDialog pDialog;
	Context context;
	private Activity activity;
	
    public postToInstagram(Context context, Activity activity) {
        this.context = context; 
        this.activity = activity;
    }
	
	protected void onPreExecute(){       
			pDialog = new ProgressDialog(context);
	        pDialog.setMessage("Posting to Instagram...");
	        pDialog.setIndeterminate(false);
	        pDialog.setCancelable(false);
	        pDialog.show();
    }
	
	@Override
	protected String doInBackground(String... params) {		
		String DownloadUrl = params[0];
		String fileName = "forInstagram.png";
		
		try {
               File root = android.os.Environment.getExternalStorageDirectory();               

               File dir = new File (root.getAbsolutePath() + "/ShareMeme/Instagram");
               if(dir.exists()==false) {
                    dir.mkdirs();
               }

               URL url = new URL(DownloadUrl);
               File file = new File(dir, fileName);
               
               if (file.exists()){
            	   file.delete();
            	   file = new File(dir, fileName);
               }

               long startTime = System.currentTimeMillis();
               Log.d("DownloadManager", "download begining");
               Log.d("DownloadManager", "download url:" + url);
               Log.d("DownloadManager", "downloaded file name:" + fileName);

              
               URLConnection ucon = url.openConnection();
               
               InputStream is = ucon.getInputStream();
               BufferedInputStream bis = new BufferedInputStream(is);
              
               ByteArrayBuffer baf = new ByteArrayBuffer(5000);
               int current = 0;
               while ((current = bis.read()) != -1) {
                  baf.append((byte) current);
               }
               
               FileOutputStream fos = new FileOutputStream(file);
               fos.write(baf.toByteArray());
               fos.flush();
               fos.close();
               Log.d("DownloadManager", "download ready in" + ((System.currentTimeMillis() - startTime) / 1000) + " sec");               
       } catch (IOException e) {
           Log.d("DownloadManager", "Error: " + e);
       }
		return android.os.Environment.getExternalStorageDirectory().getAbsolutePath() + "/ShareMeme/Instagram/" + fileName;		
	}
	
	@Override
	protected void onPostExecute(final String lol) {
	
		pDialog.dismiss();
		
		activity.runOnUiThread(new Runnable() {
			@Override
			public void run() {
		            	
				String ShareText = " ~ Posted via ShareMeme app";
		            	
				Intent shareIntent = new Intent(android.content.Intent.ACTION_SEND);
				shareIntent.setType("image/*"); 
				shareIntent.putExtra(Intent.EXTRA_STREAM, Uri.parse("file://"+lol));
				shareIntent.putExtra(Intent.EXTRA_TEXT, "" + ShareText);
				shareIntent.putExtra(Intent.EXTRA_SUBJECT, "" + ShareText);
				shareIntent.putExtra(Intent.EXTRA_TITLE, ShareText);
		            	
				shareIntent.setPackage("com.instagram.android");
				
				context.startActivity(shareIntent);
			}
		});
	}
}
