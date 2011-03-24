package com.baleksan.index.document;

import java.util.HashMap;
import java.util.Map;

/**
 * @author <a href="mailto:baleksan@yammer-inc.com" boris/>
 */
public class DocumentTypeHelper {
    public static enum Type {
        PLAIN_TEXT("text/plain", "txt"),
        HTML("text/html", "html"),
        PDF("application/pdf", "pdf"),
        MS_WORD("application/msword", "doc"),
        EXCEL("application/vnd.ms-excel", "xls"),
        PPT("application/vnd.ms-powerpoint", "ppt"),
        RTF("application/rtf", "rtf");

        private static Map<String, Type> byContentType;
        private static Map<String, Type> byExtension;

        private final String mimeType;
        private final String extension;

        Type(String mimeType, String extension) {
            this.mimeType = mimeType;
            this.extension = extension;

            register();
        }

        private void register() {
            if (null == byContentType) {
                byContentType = new HashMap<String, Type>();
            }
            if (null == byExtension) {
                byExtension = new HashMap<String, Type>();
            }

            byContentType.put(getMimeType(), this);
            byExtension.put(getExtension(), this);
        }

        public String getExtension() {
            return extension;
        }

        public String getMimeType() {
            return mimeType;
        }

        public static Type byMimeType(String mimeType) {
            return byContentType.get(mimeType);
        }

        public static Type byExtension(String extension) {
            return byExtension.get(extension);
        }
    }


    public static Type determingDocumentType(String contentType, String name) {
        Type type = null;
        if (!contentType.contains(";")) {
            //if there is not semi-colon then there is only single content type
            type = Type.byMimeType(contentType);
        } else {
            String[] possibleTypes = contentType.split(";");
            for (String possibleType : possibleTypes) {
                type = Type.byMimeType(possibleType);
                if (null != type) {
                    break;
                }
            }
        }

        //look at the extension if looking a the MIME type failes
        return type == null ? determingDocumentType(name) : type;
    }

    public static Type determingDocumentType(String name) {
        Type type = null;

        int extSepIndex = name.lastIndexOf(".");
        if (-1 != extSepIndex) {
            String ext = name.substring(extSepIndex + 1);
            type = Type.byExtension(ext);
        }

        return type;
    }

}
