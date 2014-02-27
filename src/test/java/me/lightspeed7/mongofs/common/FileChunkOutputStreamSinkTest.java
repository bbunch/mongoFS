package me.lightspeed7.mongofs.common;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.IOException;

import me.lightspeed7.mongofs.MongoTestConfig;
import me.lightspeed7.mongofs.gridfs.GridFS;
import me.lightspeed7.mongofs.gridfs.GridFSInputFile;
import me.lightspeed7.mongofs.gridfs.GridFSInputFileAdapter;
import me.lightspeed7.mongofs.writing.FileChunksOutputStreamSink;

import org.bson.types.ObjectId;
import org.junit.Before;
import org.junit.Test;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;
import com.mongodb.MongoClient;

public class FileChunkOutputStreamSinkTest {

    private static final String DB_NAME = "MongoFS-FileChunkOutputStreamSink";

    private DB database;
    private ObjectId id;

    private MongoClient mongoClient;

    private GridFS gridFS;

    private String bucketName;

    // private DBCollection filesCollection;

    private DBCollection chunksCollection;

    @Before
    public void before() {

        mongoClient = MongoTestConfig.constructMongoClient();

        database = mongoClient.getDB(DB_NAME);
        gridFS = new GridFS(database);
        bucketName = "buffer";
        // filesCollection = database.getCollection(bucketName + ".files");
        chunksCollection = database.getCollection(bucketName + ".chunks");

        id = new ObjectId();
    }

    @Test
    public void testFullBufferWrite()
            throws IOException {

        GridFSInputFile file = gridFS.createFile("foo");
        GridFSInputFileAdapter adapter = new GridFSInputFileAdapter(file);

        try (FileChunksOutputStreamSink sink = new FileChunksOutputStreamSink(chunksCollection, file.getId(), adapter)) {
            byte[] array = "This is a test".getBytes();
            sink.write(array, 0, array.length);
        }

        // assert
        DBObject found = chunksCollection.findOne(new BasicDBObject("files_id", file.getId()));

        assertNotNull(found.get("files_id"));
        assertEquals(file.getId(), found.get("files_id"));

        assertNotNull(found.get("data"));
        byte[] bytes = (byte[]) found.get("data");
        assertEquals(14, bytes.length);
        assertEquals("This is a test", new String(bytes, "UTF-8"));

    }

    @Test
    public void testPartialBufferWrite()
            throws IOException {

        GridFSInputFile file = gridFS.createFile("bar");
        GridFSInputFileAdapter adapter = new GridFSInputFileAdapter(file);

        try (FileChunksOutputStreamSink sink = new FileChunksOutputStreamSink(chunksCollection, file.getId(), adapter)) {
            byte[] array = "This is a test".getBytes();
            sink.write(array, 10, 4);
        }

        // assert
        DBObject found = chunksCollection.findOne(new BasicDBObject("files_id", file.getId()));

        assertNotNull(found.get("files_id"));
        assertEquals(file.getId(), found.get("files_id"));

        assertNotNull(found.get("data"));
        byte[] bytes = (byte[]) found.get("data");
        assertEquals(4, bytes.length);

        assertEquals("test", new String(bytes, "UTF-8"));
    }
}