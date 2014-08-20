package me.lightspeed7.mongofs.url;

import java.net.MalformedURLException;
import java.net.URL;

import me.lightspeed7.mongofs.util.CompressionMediaTypes;

import org.bson.types.ObjectId;

public final class Parser {

    private Parser() {
        // hidden
    }

    public static URL construct(final ObjectId id, final String fileName, final String mediaType, final String compressionFormat,
            final boolean compress, final boolean encrypted) throws MalformedURLException {

        String protocol = MongoFileUrl.PROTOCOL;
        if (compressionFormat != null) {
            protocol += ":" + compressionFormat;
        }
        else {
            if (compress && CompressionMediaTypes.isCompressable(mediaType)) {
                protocol += ":" + MongoFileUrl.GZIPPED;
            }
            else if (encrypted) {
                protocol += ":" + MongoFileUrl.ENCRYPTED;
            }
        }
        return construct(String.format("%s:%s?%s#%s", protocol, fileName, id.toString(), mediaType == null ? "" : mediaType.toString()));
    }

    public static URL construct(final String spec) throws MalformedURLException {

        return new URL(null, spec, new Handler());
    }

}
