package com.baleksan.index.document;

/**
 * @author <a href="mailto:baleksan@yammer-inc.com" boris/>
 */
public interface BinaryInputConverter {
    String convert(BinaryDocument document) throws BinaryFormatConverterException;
}
