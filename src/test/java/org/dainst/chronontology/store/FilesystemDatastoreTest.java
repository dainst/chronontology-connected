package org.dainst.chronontology.store;

import org.apache.commons.io.FileUtils;
import org.dainst.chronontology.JsonTestUtils;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.Test;

import java.io.File;
import java.io.IOException;

import static org.testng.Assert.assertEquals;

/**
 * @author Daniel M. de Oliveira
 */
public class FilesystemDatastoreTest {

    private static final String TYPE_NAME= "typename";
    private static final String BASE_FOLDER = "src/test/resources/datastoretest/";
    private static final FilesystemDatastore store = new FilesystemDatastore(BASE_FOLDER);

    @AfterMethod
    public static void after() throws IOException {
        FileUtils.deleteDirectory(new File(BASE_FOLDER));
    }

    @Test
    public void putAndGet() throws IOException {
        store.put(TYPE_NAME,"a", JsonTestUtils.sampleDocument("a"));
        assertEquals(store.get(TYPE_NAME,"a"), JsonTestUtils.sampleDocument("a"));
    }

    @Test
    public void putAndGet2() throws IOException {
        store.put(TYPE_NAME,"a", JsonTestUtils.sampleDocument("1"));
        store.put(TYPE_NAME,"a", JsonTestUtils.sampleDocument("2"));
        assertEquals(store.get(TYPE_NAME,"a"), JsonTestUtils.sampleDocument("2"));
    }

    @Test
    public void putAndGetAndIgnoreNonParsableNumberedFiles() throws IOException {
        store.put(TYPE_NAME,"a", JsonTestUtils.sampleDocument("1"));
        new File(BASE_FOLDER+TYPE_NAME+"/a/a.json").createNewFile();
        assertEquals(store.get(TYPE_NAME,"a"), JsonTestUtils.sampleDocument("1"));
    }

    @Test
    public void ignoredFile() throws IOException {
        new File(BASE_FOLDER+TYPE_NAME+"/a/").mkdirs();
        new File(BASE_FOLDER+TYPE_NAME+"/a/a.json").createNewFile();
        assertEquals(store.get(TYPE_NAME,"a"), null);
    }

    @Test
    public void notexistingFile() throws IOException {
        new File(BASE_FOLDER+TYPE_NAME+"/a/").mkdirs();
        assertEquals(store.get(TYPE_NAME,"a"), null);
    }

    @Test
    public void notexistingDir() throws IOException {
        assertEquals(store.get(TYPE_NAME,"a"), null);
    }

    @Test
    public void getSpecificVersions() throws IOException {
        store.put(TYPE_NAME,"a", JsonTestUtils.sampleDocument("1"));
        store.put(TYPE_NAME,"a", JsonTestUtils.sampleDocument("2"));
        JsonTestUtils.jsonAssertEquals(
                store.get(TYPE_NAME,"a",1),
                JsonTestUtils.sampleDocument("1"));
        JsonTestUtils.jsonAssertEquals(
                store.get(TYPE_NAME,"a",2),
                JsonTestUtils.sampleDocument("2"));
    }

    @Test
    public void specificVersionDoesNotExist() throws IOException {
        store.put(TYPE_NAME,"a", JsonTestUtils.sampleDocument("1"));
        assertEquals(
                store.get(TYPE_NAME,"a",2),
                null);
    }
}
