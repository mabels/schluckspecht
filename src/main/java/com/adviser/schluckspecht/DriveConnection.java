package com.adviser.schluckspecht;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.googleapis.media.MediaHttpDownloader;
import com.google.api.client.googleapis.media.MediaHttpDownloaderProgressListener;
import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.DriveScopes;
import com.google.api.services.drive.model.File;
import com.google.api.services.drive.model.FileList;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.*;

/**
 * Created by menabe on 23.02.15.
 */
public class DriveConnection {

    private static final String APPLICATION_NAME = "COM.ADVISER.SCHLUCKSPECHT/0.0.1";

    private static final java.io.File DATA_STORE_DIR =
            new java.io.File(System.getProperty("user.home"), ".store/schluckspect");

    private static final JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();

    private final HttpTransport httpTransport;
    private final FileDataStoreFactory dataStoreFactory;
    private final Credential credential;
    private final Drive drive;

    public DriveConnection(HttpTransport httpTransport, FileDataStoreFactory dataStoreFactory, Credential credential, Drive drive) {
        this.httpTransport = httpTransport;
        this.dataStoreFactory = dataStoreFactory;
        this.credential = credential;
        this.drive = drive;
    }

    private static Credential authorize(InputStream csStream, HttpTransport httpTransport, FileDataStoreFactory dataStoreFactory)
            throws Exception {
        final GoogleClientSecrets clientSecrets = GoogleClientSecrets.load(JSON_FACTORY,
                new InputStreamReader(csStream));
        GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(
                httpTransport, JSON_FACTORY, clientSecrets,
                Collections.singleton(DriveScopes.DRIVE)).setDataStoreFactory(dataStoreFactory)
                .build();
        return new AuthorizationCodeInstalledApp(flow, new LocalServerReceiver()).authorize("user");
    }

    public static DriveConnection start(InputStream csStream) throws Exception {
        final HttpTransport httpTransport = GoogleNetHttpTransport.newTrustedTransport();
        final FileDataStoreFactory dataStoreFactory = new FileDataStoreFactory(DATA_STORE_DIR);
        final Credential credential = authorize(csStream, httpTransport, dataStoreFactory);
        final Drive drive = new Drive.Builder(httpTransport, JSON_FACTORY, credential)
                .setApplicationName(APPLICATION_NAME).build();
        return new DriveConnection(httpTransport, dataStoreFactory, credential, drive);
    }

    public void download(File file, OutputStream out, MediaHttpDownloaderProgressListener mhdpl) throws IOException {
        Drive.Files.Get get = drive.files().get(file.getId());
        MediaHttpDownloader downloader = get.getMediaHttpDownloader();
        downloader.setDirectDownloadEnabled(true);
        if (mhdpl != null) {
            downloader.setProgressListener(mhdpl);
        }
        downloader.download(new GenericUrl(file.getDownloadUrl()), out);
    }

    public  Map<String, File> retrieveRoot() throws IOException {
        File root = new File();
        root.setId("root");
        root.setTitle("/");
        return retrieve(root);
    }
    public Map<String, File> retrieve(File parent) throws IOException {
        Map<String, File> result = new HashMap<>();
        Drive.Files.List request = drive.files().list().setQ("'"+parent.getId()+"' in parents");
        do {
            try {
                FileList files = request.execute();
                for (File file : files.getItems()) {
                    result.put(file.getTitle(), file);
                }
                request.setPageToken(files.getNextPageToken());
            } catch (IOException e) {
                System.out.println("An error occurred: " + e);
                request.setPageToken(null);
            }
        } while (request.getPageToken() != null &&
                request.getPageToken().length() > 0);
        return result;
    }

}
