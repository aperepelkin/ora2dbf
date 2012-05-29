package ru.ailabs.text;

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.Writer;

public class ResourceStringReader {
    public String readQuery(String resourceName) throws IOException {
        return readToBuilder(getClass().getClassLoader().
                getResourceAsStream(resourceName), "UTF-8").toString();
    }

    public static StringBuilder readToBuilder(InputStream in, String encoding) throws IOException {
        StringBuilderWriter writer = new StringBuilderWriter();
        Reader reader = new InputStreamReader(in, encoding);

        try {
            copy(reader, writer);
            return writer.getBuilder();
        } finally {
            close(reader);
            close(writer);
        }
    }

    public static void copy(Reader in, Writer... out) throws IOException {
        char[] buf = new char[5000];

        int read;
        while ((read = in.read(buf)) != -1) {
            for (Writer writer : out) {
                writer.write(buf, 0, read);
            }
        }
    }
    
    public static void close(Closeable closeable) {
        if (closeable == null) return;

        try {
            closeable.close();
        } catch (Exception e) {
            //ok
        }
    }
}
