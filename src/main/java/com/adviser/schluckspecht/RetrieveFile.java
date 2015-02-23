package com.adviser.schluckspecht;

import com.google.api.services.drive.model.File;

import java.util.concurrent.BlockingQueue;

/**
 * Created by menabe on 23.02.15.
 */
public class RetrieveFile implements DriveTask {
    private final File file;
    public RetrieveFile(File file) {
        this.file = file;
    }

    @Override
    public void process(LocalStore lc, DriveConnection dc, BlockingQueue<DriveTask> q) {
        System.out.println("RetrieveFile:"+file.getTitle());
    }
}
