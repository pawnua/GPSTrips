/**
 * Copyright 2013 Google Inc. All Rights Reserved.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software distributed under the
 * License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either
 * express or implied. See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.pawnua.android.app.gpstrips.activities;

import android.content.Intent;
import android.content.IntentSender;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.BaseColumns;
import android.util.Log;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.drive.Drive;
import com.google.android.gms.drive.DriveApi;
import com.google.android.gms.drive.DriveApi.DriveContentsResult;
import com.google.android.gms.drive.DriveContents;
import com.google.android.gms.drive.DriveFile;
import com.google.android.gms.drive.DriveFolder;
import com.google.android.gms.drive.DriveFolder.DriveFileResult;
import com.google.android.gms.drive.DriveId;
import com.google.android.gms.drive.DriveResource;
import com.google.android.gms.drive.MetadataChangeSet;
import com.google.android.gms.drive.OpenFileActivityBuilder;
import com.pawnua.android.app.gpstrips.GalleryDataManager;
import com.pawnua.android.app.gpstrips.GpxTrackWriter;
import com.pawnua.android.app.gpstrips.R;
import com.pawnua.android.app.gpstrips.model.Trip;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * An activity to illustrate how to create a file.
 */
public class DiskCreateFileActivity extends DiskBaseDemoActivity {

//    https://developers.google.com/drive/android/
//    https://github.com/seanpjanson/GDAADemo

    private static final String TAG = "CreateFileActivity";

    private static final int REQUEST_CODE_OPENER = 1;

    private ProgressBar pbProgress;
    private Trip trip;

    private DriveId mFolderDriveId;

    private int status = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_disk_create);

        pbProgress = (ProgressBar) findViewById(R.id.progressBar);

        trip = Trip.getTripByID(getIntent().getLongExtra(BaseColumns._ID, -1));
        status = 0;

    }

    @Override
    public void onConnected(Bundle connectionHint) {
        super.onConnected(connectionHint);

        if (status == 0) {
            // Select Drive Folder
            IntentSender intentSender = Drive.DriveApi
                    .newOpenFileActivityBuilder()
                    .setMimeType(new String[]{DriveFolder.MIME_TYPE})
                    .build(getGoogleApiClient());
            try {
                startIntentSenderForResult(
                        intentSender, REQUEST_CODE_OPENER, null, 0, 0, 0);
            } catch (IntentSender.SendIntentException e) {
                Log.w(TAG, "Unable to send intent", e);
            }
        }
        else if (status == 1){
            // Copy all files to selected drive folder (DriveId = mFolderDriveId)
            copyFilesToGoogleDriveTask(trip);

        }
        else{
            finish();
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQUEST_CODE_OPENER:
                if (resultCode == RESULT_OK) {
                    mFolderDriveId = (DriveId) data.getParcelableExtra(
                            OpenFileActivityBuilder.EXTRA_RESPONSE_DRIVE_ID);

                    // not connected here after folder pick
                    // connect again and copy files in OnConnected()
                    status = 1;

                    if (getGoogleApiClient() == null || !getGoogleApiClient().isConnected())
                        getGoogleApiClient().connect();

//                    // Copy all files to selected drive folder (DriveId = mFolderDriveId)
//                    copyFilesToGoogleDriveTask(trip);
                }
                else{
                    status = -1;
                }
                break;
            default:
                super.onActivityResult(requestCode, resultCode, data);
        }
    }

    private static DriveContents file2Contents(DriveContents driveContents, File file) {
        OutputStream outputStream = driveContents.getOutputStream();
        if (outputStream != null) try {
            InputStream inputStream = new FileInputStream(file);
            byte[] buf = new byte[4096];
            int c;
            while ((c = inputStream.read(buf, 0, buf.length)) > 0) {
                outputStream.write(buf, 0, c);
                outputStream.flush();
            }
        } catch (Exception e)  {

        }
        finally {
            try {
                outputStream.close();
            } catch (Exception ignore) {
            }
        }
        return driveContents;
    }
    private void copyFilesToGoogleDriveTask(final Trip trip) {
        new AsyncTask<Trip, Integer, Void>() {

            @Override
            protected Void doInBackground(Trip... params) {

                // Copy Gallery
                File[] fileImages = GalleryDataManager.getFileImages(GalleryDataManager.createImageGallery(params[0]));

                if (fileImages.length == 0) return  null;

                DriveId parentId = createFolder(trip.getName());

                int i = 1;
                int amount = fileImages.length + 1;

                for (File file: fileImages) {
                    if (createFile(parentId, file, "image/jpeg")) {
                        publishProgress(i, amount);
                    }
                    i++;
                }

                File gpxfile = GpxTrackWriter.saveTrip(params[0]);
                if (createFile(parentId, gpxfile, "text/plain")) {
//                    publishProgress(gpxfile.getName());
                    publishProgress(i, amount);
                }

                // db
                boolean test = false;
                if (test){
                    File dbFile = new File("/data/data/com.pawnua.android.app.gpstrips/databases/GPSTrips.db");
                    if (dbFile.exists()) {
                        createFile(parentId, dbFile, "text/plain");
                    }


                }

                return null;
            }

            @Override
            protected void onProgressUpdate(Integer... params) {
                super.onProgressUpdate(params);
                pbProgress.setMax(params[1]);
                pbProgress.setProgress(params[0]);
//                showMessage("Complete copying " + strings[0]);
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                // Done
                finish();
            }

        }.execute(trip);
    }

    private DriveId createFolder(String title) {
        DriveId dId = null;
        if (getGoogleApiClient() == null || !getGoogleApiClient().isConnected() ) return null;

        try {

            DriveFolder folder = Drive.DriveApi.getFolder(getGoogleApiClient(), mFolderDriveId);

            MetadataChangeSet changeSet;
            changeSet = new MetadataChangeSet.Builder().setTitle(title).setMimeType("application/vnd.google-apps.folder").build();
            DriveFolder.DriveFolderResult driveFolderResult = folder.createFolder(getGoogleApiClient(), changeSet).await();
            DriveFolder dFld = (driveFolderResult != null) && driveFolderResult.getStatus().isSuccess() ? driveFolderResult.getDriveFolder() : null;
            if (dFld != null) {
                DriveResource.MetadataResult metadataResult = dFld.getMetadata(getGoogleApiClient()).await();
                if ((metadataResult != null) && metadataResult.getStatus().isSuccess()) {
                    dId = metadataResult.getMetadata().getDriveId();
                }
            }
        } catch (Exception e) {
        }
        return dId == null ? null : dId;
    }

    private boolean createFile(DriveId parentId, File file, String mimeType) {
        // CreateFile
        if (getGoogleApiClient() == null || !getGoogleApiClient().isConnected() ) return false;

        DriveFolder folder = parentId==null ?
                Drive.DriveApi.getFolder(getGoogleApiClient(), mFolderDriveId):
                Drive.DriveApi.getFolder(getGoogleApiClient(), parentId);

        DriveContentsResult driveContentsResult = Drive.DriveApi.newDriveContents(getGoogleApiClient()).await();

        MetadataChangeSet changeSet = new MetadataChangeSet.Builder()
                .setTitle(file.getName())
                .setMimeType(mimeType)
                .build();

        DriveFileResult driveFileResult = folder.createFile(getGoogleApiClient(), changeSet, driveContentsResult.getDriveContents()).await();

        DriveFile driveFile = driveFileResult != null && driveFileResult.getStatus().isSuccess() ? driveFileResult.getDriveFile() : null;
        if (driveFile == null) return false;

        driveContentsResult = driveFile.open(getGoogleApiClient(), DriveFile.MODE_WRITE_ONLY, null).await();

        if ((driveContentsResult != null) && (driveContentsResult.getStatus().isSuccess()))
            try {
            com.google.android.gms.common.api.Status stts = file2Contents(driveContentsResult.getDriveContents(), file).commit(getGoogleApiClient(), changeSet).await();
            if ((stts != null) && stts.isSuccess()) {
                DriveResource.MetadataResult r3 = driveFile.getMetadata(getGoogleApiClient()).await();
                if (r3 != null && r3.getStatus().isSuccess()) {
//                            dId = r3.getMetadata().getDriveId();
                }
            }
        } catch (Exception e) {
                return false;
            }
//                publishProgress();
        return true;
    }


    final private ResultCallback<DriveFileResult> fileCallback = new
            ResultCallback<DriveFileResult>() {
        @Override
        public void onResult(DriveFileResult result) {
            if (!result.getStatus().isSuccess()) {
                showMessage("Error while trying to create the file");
                return;
            }
            showMessage("Created a file with content: " + result.getDriveFile().getDriveId());
        }
    };


}
