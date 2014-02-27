package me.lightspeed7.mongofs;

import java.util.Set;
import java.util.TreeSet;

/**
 * Simple intial list for mediaTypes that we should not try to compress, since they are already compressed.
 * 
 * TODO : I want this to be auto-discovered based on runtime statistics of compression.
 * 
 * @author dbusch
 * 
 */
public class CompressionMediaTypes {

    private static Set<String> noCompressionTypes = new TreeSet<>();

    // below is a list on types that are already compressed, so don't compress again
    static {
        noCompressionTypes.add("application/x-bzip2");
        noCompressionTypes.add("application/x-gzip");
        noCompressionTypes.add("application/octet-stream");
        noCompressionTypes.add("application/x-tar");
        noCompressionTypes.add("application/zip");
    }

    public static boolean isCompressable(String mediaType) {

        return !noCompressionTypes.contains(mediaType.toString());
    }
}