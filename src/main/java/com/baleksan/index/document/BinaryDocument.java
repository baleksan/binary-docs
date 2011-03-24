package com.baleksan.index.document;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.InputStream;

/**
 * @author <a href="mailto:baleksan@yammer-inc.com" boris/>
 */
public class BinaryDocument {
    private String contentType;
    private String content;
    private String name;
    private long length;
    
    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public void setContent(String content) {
        this.content = content;
        this.length = content.length();
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getContentType() {
        return contentType;
    }

    public String getContent() {
        return content;
    }

    public String getName() {
        return name;
    }

    public long length() {
        return length;
    }

    public InputStream openStream() {
        return new BufferedInputStream(new ByteArrayInputStream(content.getBytes()));
    }
}
