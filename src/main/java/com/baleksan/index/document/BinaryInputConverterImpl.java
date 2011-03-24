package com.baleksan.index.document;

import org.apache.commons.io.IOUtils;
import org.apache.tika.exception.TikaException;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.parser.Parser;
import org.apache.tika.parser.microsoft.OfficeParser;
import org.apache.tika.parser.pdf.PDFParser;
import org.apache.tika.parser.rtf.RTFParser;
import org.apache.tika.parser.txt.TXTParser;
import org.apache.tika.sax.WriteOutContentHandler;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;

/**
 * @author <a href="mailto:baleksan@yammer-inc.com" boris/>
 */
public class BinaryInputConverterImpl implements BinaryInputConverter {
    private static final Map<DocumentTypeHelper.Type, Parser> typeParserMap;

    static {
        typeParserMap = new HashMap<DocumentTypeHelper.Type, Parser>();

        typeParserMap.put(DocumentTypeHelper.Type.PDF, new PDFParser());
        typeParserMap.put(DocumentTypeHelper.Type.MS_WORD, new OfficeParser());
        typeParserMap.put(DocumentTypeHelper.Type.PPT, new OfficeParser());
        typeParserMap.put(DocumentTypeHelper.Type.RTF, new RTFParser());
        typeParserMap.put(DocumentTypeHelper.Type.EXCEL, new OfficeParser());
        typeParserMap.put(DocumentTypeHelper.Type.PLAIN_TEXT, new TXTParser());
    }


    @Override
    public String convert(BinaryDocument document) throws BinaryFormatConverterException {
        DocumentTypeHelper.Type documentType =
                DocumentTypeHelper.determingDocumentType(document.getContentType(), document.getContent());

        if (null == documentType) {
            throw new BinaryFormatConverterException(
                    "Cannot determine the document type for mime type " + document.getContentType() + " for " +
                            document.getName());
        }

        return convert(documentType, document);
    }

    private static final long MAX_BINARY_LEN = 10 * 1000000;

    private String convert(DocumentTypeHelper.Type type, BinaryDocument document) throws BinaryFormatConverterException {
        if (document.length() > MAX_BINARY_LEN) {
            throw new BinaryFormatConverterException(
                    "Binary of length " + document.length() + " is too long for conversion");
        }
        Parser parser = typeParserMap.get(type);
        if (null == parser) {
            throw new BinaryFormatConverterException(type + " is not supported for conversion to plain text.");
        }

        try {
            Metadata metadata = new Metadata();
            StringWriter writer = new StringWriter();
            ContentHandler handler = new WriteOutContentHandler(writer);
            ParseContext parseContext = new ParseContext();

            InputStream input = document.openStream();
            try {
                parser.parse(input, handler, metadata, parseContext);
            } finally {
                // ensure that we close the stream
                IOUtils.closeQuietly(input);
            }

            return writer.toString();
        } catch (IOException e) {
            throw new BinaryFormatConverterException("IO problems in converting " + type + " document to text", e);
        } catch (TikaException e) {
            throw new BinaryFormatConverterException("Problems in converting " + type + " document to text", e);
        } catch (SAXException e) {
            throw new BinaryFormatConverterException("Problems in converting " + type + " document to text", e);
        } catch (Exception e) {
            throw new BinaryFormatConverterException("General error in converting " + type + " document to text", e);
        }
    }
}

