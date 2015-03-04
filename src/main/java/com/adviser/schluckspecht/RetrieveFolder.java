package com.adviser.schluckspecht;

import com.google.api.services.drive.model.File;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.BlockingQueue;

/**
 * Created by menabe on 23.02.15.
 */
public class RetrieveFolder implements DriveTask {

    private final static Logger LOG = LoggerFactory.getLogger(RetrieveFolder.class);

    private final File file;
    private final SchluckSpecht schluckSpecht;
    public RetrieveFolder(File file, SchluckSpecht ssp) {
        this.schluckSpecht = ssp;
        this.file = file;
    }

    @Override
    public String getName() {
        return file.getTitle();
    }

    @Override
    public void process() throws IOException {
        LOG.debug("RetrieveFolder:" + file.getTitle());
        RetrieveFolder.mergeWithLocal(schluckSpecht, schluckSpecht.lc.retrieveRoot(), schluckSpecht.dc.retrieve(file));
    }

    private static void fetch(File file, SchluckSpecht schluckSpecht) {
        if (file.getMimeType().equals("application/vnd.google-apps.folder")) {
            schluckSpecht.q.add(new RetrieveFolder(file, schluckSpecht));
        } else {
            schluckSpecht.q.add(new RetrieveFile(file, schluckSpecht));
        }
    }

    public static void mergeWithLocal(SchluckSpecht schluckSpecht, Map<String, File> localFiles, Map<String, File> driveFiles) throws IOException {
        for (File df : driveFiles.values()) {
            final File lFile = localFiles.get(df.getTitle());
            if (lFile != null) {
                if (!df.getId().equals(lFile.getId()) ||
                        !df.getEtag().equals(lFile.getEtag()) ||
                        !df.getMd5Checksum().equals(lFile.getMd5Checksum())) {
                    // changed
                    fetch(df, schluckSpecht);
                } else {
                    schluckSpecht.lc.link(lFile);
                }
            } else {
                fetch(df, schluckSpecht);
            }
        }
    }
}
