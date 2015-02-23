package com.adviser.schluckspecht;

import com.google.api.services.drive.model.File;

import java.util.*;
import java.util.concurrent.BlockingQueue;

/**
 * Created by menabe on 23.02.15.
 */
public class RetrieveRoot implements DriveTask {

    private static void fetch(File file, BlockingQueue<DriveTask> q) {
        if (file.getMimeType().equals("application/vnd.google-apps.folder")) {
            q.add(new RetrieveFolder(file));
        } else {
            q.add(new RetrieveFile(file));
        }
    }
     @Override
    public void process(LocalStore lc, DriveConnection dc, BlockingQueue<DriveTask> q)  {
        try {
            final Map<String, File> localFiles = lc.retrieve(".");
            final Map<String, File> driveFiles = dc.retrieveRoot();

            for (File df : driveFiles.values()) {
                final File lFile = localFiles.get(df.getTitle());
                if (lFile != null) {
                    localFiles.remove(df.getTitle()); // rest of localFiles will deleted
                    if (!df.getId().equals(lFile.getId()) ||
                        !df.getEtag().equals(lFile.getEtag()) ||
                        !df.getMd5Checksum().equals(lFile.getMd5Checksum())) {
                        // changed
                        fetch(df, q);
                    }
                } else {
                    fetch(df, q);
                }
            }
            for (File toDel : localFiles.values()) {
                System.out.println("Remove local files");
                //localFiles.delete(toDel.getTitle());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
