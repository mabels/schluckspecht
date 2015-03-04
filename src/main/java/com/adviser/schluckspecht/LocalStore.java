package com.adviser.schluckspecht;

import com.google.api.services.drive.model.File;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by menabe on 23.02.15.
 */
public class LocalStore {

    private final static Logger LOG = LoggerFactory.getLogger(LocalStore.class);

    final java.io.File baseDir;
    final java.io.File prevVersion;
    final java.io.File currentVersion;

    private static java.io.File findPrevVersion(java.io.File baseDir) {
        java.io.File found = new java.io.File("");
        for (java.io.File fname : baseDir.listFiles()) {
           if (fname.getName().length() == "yyyyMMddHHmmss".length() && fname.getName().matches("[0-9]+")) {
               if (fname.getName().compareTo(found.getName()) > 0) {
                   found = fname;
               }
           }
        }
        if (found.getName().isEmpty()) {
            return null;
        }
        return found;
    }

    private LocalStore(String baseDir) throws IOException {
        this.baseDir = new java.io.File(baseDir);
        this.baseDir.mkdirs();
        this.prevVersion = findPrevVersion(this.baseDir);
        final Calendar cal = Calendar.getInstance();
        cal.getTime();
        final SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
        this.currentVersion = new java.io.File(this.baseDir, sdf.format(cal.getTime()));
        this.currentVersion.mkdirs();
        LOG.debug("LocalStore:"+baseDir+":"+currentVersion+":"+prevVersion);
    }

    public Map<String, File> retrieve(String path) {
        Map<String, File> ret = new HashMap<>();
        if (prevVersion == null) {
            return ret;
        }
        for(java.io.File fname : prevVersion.listFiles()) {
            File my = new File();
            my.setTitle(fname.getPath());
            ret.put(my.getTitle(), my);
        }
        return ret;
    }


    public Map<String, File> retrieveRoot() {
        return retrieve(".");
    }

    public void link(File f) throws IOException {
        // link previous including meta
        LOG.debug("link"+f.getTitle());
        java.io.File prevF = new java.io.File(prevVersion, f.getTitle());
        java.io.File currF = new java.io.File(currentVersion, f.getTitle());
        Files.createSymbolicLink(prevF.toPath(), currF.toPath());
        prevF = new java.io.File(prevVersion, f.getTitle()+"-meta");
        currF = new java.io.File(currentVersion, f.getTitle()+"-meta");
        Files.createSymbolicLink(prevF.toPath(), currF.toPath());
    }

    public OutputStream createFile(File f) throws FileNotFoundException {
        return new FileOutputStream(new java.io.File(currentVersion, f.getTitle()+"-"+f.getId()));
    }

    public static LocalStore newVersion(String baseDir) throws IOException {
        return new LocalStore(baseDir);
    }
}
