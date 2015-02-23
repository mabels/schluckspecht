package com.adviser.schluckspecht;

import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.store.DataStoreFactory;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.model.File;
import com.google.api.services.drive.model.FileList;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * A sample application that runs multiple requests against the Drive API. The requests this sample
 * makes are:
 * <ul>
 * <li>Does a resumable media upload</li>
 * <li>Updates the uploaded file by renaming it</li>
 * <li>Does a resumable media download</li>
 * <li>Does a direct media upload</li>
 * <li>Does a direct media download</li>
 * </ul>
 *
 * @author rmistry@google.com (Ravi Mistry)
 */
public class SchluckSpecht {

    /**
     * Be sure to specify the name of your application. If the application name is {@code null} or
     * blank, the application will log a warning. Suggested format is "MyCompany-ProductName/1.0".
     */


//    private static final String UPLOAD_FILE_PATH = "/tmp/upload";
//    private static final String DIR_FOR_DOWNLOADS = "/tmp/download";
//    private static final java.io.File UPLOAD_FILE = new java.io.File(UPLOAD_FILE_PATH);

    /**
     * Directory to store user credentials.
     */
    /**
     * Global instance of the {@link DataStoreFactory}. The best practice is to make it a single
     * globally shared instance across your application.
     */
    //private static FileDataStoreFactory dataStoreFactory;

    /**
     * Global instance of the HTTP transport.
     */


    /**
     * Global instance of the JSON factory.
     */
    private static final JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();

    /**
     * Global Drive API client.
     */
//    private static Drive drive;
//
    /**
     * Authorizes the installed application to access user's protected data.
     */

    public static void main(String[] args) {
        try {
            final LocalStore lc = new LocalStore("/tmp/drive");
            final DriveConnection dc = DriveConnection.start();
            final int processors = Runtime.getRuntime().availableProcessors();
            final BackupProcessor[] bps = new BackupProcessor[processors];
            final BlockingQueue<DriveTask> q = new LinkedBlockingQueue<>();
            for (int i = 0; i < bps.length; ++i) {
                bps[i] = new BackupProcessor();
                bps[i].start(lc, dc, q);
            }
            q.put(new RetrieveRoot());
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

    private static List<File> retrieveAllFiles(Drive service) throws IOException {
        //List<File> result = new ArrayList<File>();
        Drive.Files.List request = service.files().list();
        do {
            try {
                FileList files = request.execute();
                for(File i : files.getItems()) {
                    System.out.println(">>"+i.getId()+":"+i.getEtag()+":"+i.getMd5Checksum()+":"+i.getTitle());
                }

      //          result.addAll(files.getItems());
                request.setPageToken(files.getNextPageToken());
            } catch (IOException e) {
                System.out.println("An error occurred: " + e);
                request.setPageToken(null);
            }
        } while (request.getPageToken() != null &&
                request.getPageToken().length() > 0);

        return null;
    }

//    /**
//     * Uploads a file using either resumable or direct media upload.
//     */
//    private static File uploadFile(boolean useDirectUpload) throws IOException {
//        File fileMetadata = new File();
//        fileMetadata.setTitle(UPLOAD_FILE.getName());
//
//        FileContent mediaContent = new FileContent("image/jpeg", UPLOAD_FILE);
//
//        Drive.Files.Insert insert = drive.files().insert(fileMetadata, mediaContent);
//        MediaHttpUploader uploader = insert.getMediaHttpUploader();
//        uploader.setDirectUploadEnabled(useDirectUpload);
//        uploader.setProgressListener(new FileUploadProgressListener());
//        return insert.execute();
//    }
//
//    /**
//     * Updates the name of the uploaded file to have a "drivetest-" prefix.
//     */
//    private static File updateFileWithTestSuffix(String id) throws IOException {
//        File fileMetadata = new File();
//        fileMetadata.setTitle("drivetest-" + UPLOAD_FILE.getName());
//
//        Drive.Files.Update update = drive.files().update(id, fileMetadata);
//        return update.execute();
//    }
//
//    /**
//     * Downloads a file using either resumable or direct media download.
//     */
//    private static void downloadFile(boolean useDirectDownload, File uploadedFile)
//            throws IOException {
//        // create parent directory (if necessary)
//        java.io.File parentDir = new java.io.File(DIR_FOR_DOWNLOADS);
//        if (!parentDir.exists() && !parentDir.mkdirs()) {
//            throw new IOException("Unable to create parent directory");
//        }
//        OutputStream out = new FileOutputStream(new java.io.File(parentDir, uploadedFile.getTitle()));
//
//        MediaHttpDownloader downloader =
//                new MediaHttpDownloader(httpTransport, drive.getRequestFactory().getInitializer());
//        downloader.setDirectDownloadEnabled(useDirectDownload);
//        downloader.setProgressListener(new FileDownloadProgressListener());
//        downloader.download(new GenericUrl(uploadedFile.getDownloadUrl()), out);
//    }
}
