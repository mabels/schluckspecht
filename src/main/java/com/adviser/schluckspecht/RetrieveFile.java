package com.adviser.schluckspecht;

import com.google.api.client.googleapis.media.MediaHttpDownloader;
import com.google.api.client.googleapis.media.MediaHttpDownloaderProgressListener;
import com.google.api.services.drive.model.File;
import org.mortbay.util.IO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.OutputStream;
import java.util.concurrent.BlockingQueue;

/**
 * Created by menabe on 23.02.15.
 */
public class RetrieveFile implements DriveTask {

    private final static Logger LOG = LoggerFactory.getLogger(RetrieveFile.class);

    private final File file;
    private final SchluckSpecht schluckSpecht;
    public RetrieveFile(File file, SchluckSpecht schluckSpecht) {
        this.schluckSpecht = schluckSpecht;
        this.file = file;
    }

    @Override
    public String getName() {
        return file.getTitle();
    }

    @Override
    public void process() throws IOException {
        LOG.debug("STRT:"+file.getTitle());
//        OutputStream os = null;
//        try {
//            os = schluckSpecht.lc.createFile(file);
//            schluckSpecht.dc.download(file, os, new MediaHttpDownloaderProgressListener() {
//
//                @Override
//                public void progressChanged(MediaHttpDownloader mediaHttpDownloader) throws IOException {
//                    if (0 < mediaHttpDownloader.getProgress() && mediaHttpDownloader.getProgress() < 100) {
//                        LOG.debug("PROG:" + file.getTitle() + ":" + (mediaHttpDownloader.getProgress() * 100) + "%");
//                    }
//                }
//            });
//        } catch (Throwable t) {
//            throw t;
//        } finally {
//            IO.close(os);
//        }
//        LOG.debug("DONE:"+file.getTitle());

    }
}
