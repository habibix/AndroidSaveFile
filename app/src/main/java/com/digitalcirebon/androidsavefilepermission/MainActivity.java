package com.digitalcirebon.androidsavefilepermission;

import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    EditText inputUrl;
    Button downloadButton;
    Button canceldownload;
    private long downloadID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        inputUrl = (EditText) findViewById(R.id.inputUrl);
        downloadButton = (Button) findViewById(R.id.buttonDownload);
        canceldownload = (Button) findViewById(R.id.cancelDownload);

        IntentFilter filter = new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE);
        registerReceiver(new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                long boardcastDownload = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID,-1);
                if(boardcastDownload == downloadID){
                    if(getDownloadStatus() == DownloadManager.STATUS_SUCCESSFUL){
                        Toast.makeText(MainActivity.this, "DOWNLOAD COMPELETE", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(MainActivity.this, "DOWNLOAD NOT COMPELETE", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        }, filter);

        downloadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startDownload(inputUrl.getText().toString());
            }
        });

        canceldownload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cancelDownload();
            }
        });

    }

    private int getDownloadStatus() {
        DownloadManager.Query query = new DownloadManager.Query();
        query.setFilterById(downloadID);

        DownloadManager downloadManager = (DownloadManager) getSystemService(DOWNLOAD_SERVICE);
        Cursor cursor = downloadManager.query(query);

        if(cursor.moveToFirst()){
            int columnIndex = cursor.getColumnIndex(DownloadManager.COLUMN_STATUS);
            int status = cursor.getInt(columnIndex);
            return status;
        }

        return DownloadManager.ERROR_UNKNOWN;
    }

    public void startDownload(String url){
        Uri uri = Uri.parse(url);
        DownloadManager.Request request = new DownloadManager.Request(uri);
        request.setTitle("Image Download");
        request.setDescription("Download image");
        request.setDestinationInExternalFilesDir(this, Environment.DIRECTORY_DOWNLOADS, "largeimagge.jpg");

        DownloadManager downloadManager = (DownloadManager) getSystemService(DOWNLOAD_SERVICE);
        downloadID = downloadManager.enqueue(request);
    }

    public void cancelDownload(){
        DownloadManager downloadManager = (DownloadManager) getSystemService(DOWNLOAD_SERVICE);
        downloadManager.remove(downloadID);
    }
}
