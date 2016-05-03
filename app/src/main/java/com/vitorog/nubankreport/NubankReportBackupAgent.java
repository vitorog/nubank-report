package com.vitorog.nubankreport;

import android.app.backup.BackupAgent;
import android.app.backup.BackupDataInput;
import android.app.backup.BackupDataOutput;
import android.os.ParcelFileDescriptor;
import android.util.Log;

import java.io.IOException;

/**
 * TODO: Backup Agent
 *
 * Created by Vitor on 27/04/2016.
 */
public class NubankReportBackupAgent extends BackupAgent {
    private static final String TAG = "NubankReportBackupAgent";

    @Override
    public void onBackup(ParcelFileDescriptor oldState, BackupDataOutput data, ParcelFileDescriptor newState) throws IOException {
        Log.i(TAG, "Backup started");
    }

    @Override
    public void onRestore(BackupDataInput data, int appVersionCode, ParcelFileDescriptor newState) throws IOException {
        Log.i(TAG, "Backup restored");
    }
}
