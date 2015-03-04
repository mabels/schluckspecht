package com.adviser.schluckspecht;

import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.store.DataStoreFactory;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.model.File;
import com.google.api.services.drive.model.FileList;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class SchluckSpecht {

    public final LocalStore lc;
    public final DriveConnection dc;
    public final BlockingQueue<DriveTask> q;

    private SchluckSpecht(LocalStore lc, DriveConnection dc, BlockingQueue<DriveTask> q) {
        this.lc = lc;
        this.dc = dc;
        this.q = q;
    }

    public static void main(String[] args) {
        try {
            final LocalStore lc = LocalStore.newVersion("/tmp/drive");
            final DriveConnection dc = DriveConnection.start(new FileInputStream("./client_secrets.json"));
            final int processors = Runtime.getRuntime().availableProcessors();
            final BackupProcessor[] bps = new BackupProcessor[processors];
            final BlockingQueue<DriveTask> q = new LinkedBlockingQueue<>();
            for (int i = 0; i < bps.length; ++i) {
                bps[i] = new BackupProcessor();
                bps[i].start(lc, dc, q);
            }
            q.put(new RetrieveRoot(new SchluckSpecht(lc, dc, q)));
            for(BackupProcessor bp : bps) {
                bp.join();
            }
            return;
        } catch (IOException e) {
            System.err.println(e.getMessage());
        } catch (Throwable t) {
            t.printStackTrace();
        }
        System.exit(1);
    }
}
