package com.adviser.schluckspecht;

import com.google.api.services.drive.model.File;

import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

/**
 * Created by menabe on 23.02.15.
 */
public class LocalStore {
    final java.io.File baseDir;

    public LocalStore(String baseDir) throws IOException {
        this.baseDir = new java.io.File(baseDir);
        this.baseDir.mkdirs();
    }
    public Map<String, File> retrieve(String path) {
        Map<String, File> ret = new HashMap<>();
        for(java.io.File fname : (new java.io.File(baseDir, path)).listFiles()) {
            File my = new File();
            my.setTitle(fname.getPath());
            ret.put(my.getTitle(), my);
        }
        return ret;
    }
}
