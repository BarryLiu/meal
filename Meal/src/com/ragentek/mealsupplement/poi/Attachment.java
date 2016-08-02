package com.ragentek.mealsupplement.poi;

import java.io.File;
import java.io.InputStream;

/**
 * Created by zixiao.zhang on 2016/5/10.
 */
public class Attachment {
    private String fileName;
    private String mimeType;
    private InputStream inputStream;
    private File file;
    public Attachment() {
    }

    public Attachment(String fileName, String mimeType, InputStream inputStream) {
        this.fileName = fileName;
        this.mimeType = mimeType;
        this.inputStream = inputStream;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getMimeType() {
        return mimeType;
    }

    public void setMimeType(String mimeType) {
        this.mimeType = mimeType;
    }

    public InputStream getInputStream() {
        return inputStream;
    }

    public void setInputStream(InputStream inputStream) {
        this.inputStream = inputStream;
    }

    public File getFile() {
        return file;
    }

    public void setFile(File file) {
        this.file = file;
    }
}
