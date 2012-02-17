package model;

import java.io.IOException;
import java.net.JarURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public class ContentAccess {
    public static void main(String[] args) {
        try {
            // Create a URL that refers to a jar file on the net
            //URL url = new URL("jar:http://hostname/my.jar!/");

            // Create a URL that refers to a jar file in the file system
            //URL url = new URL("jar:file:/c:/almanac/my.jar!/");
            //URL url = new URL("jar:file:/e:/Projects/ParallelCompute/CourseGUI/CourseContent.zip!/");

            // Get the jar file
            //JarURLConnection conn = (JarURLConnection) url.openConnection();
            //JarFile jarfile = conn.getJarFile();

            //ZipEntry manifest = jarfile.getEntry("manifest");
            //System.out.println(manifest);

            // When no entry is specified on the URL, the entry name is null
            //String entryName = conn.getEntryName();  // null


            // Create a URL that refers to an entry in the jar file
            URL url = new URL("jar:file:/e:/Projects/ParallelCompute/CourseGUI/CourseContent.zip!/manifest.xml");

            // Get the jar file
            JarURLConnection conn = (JarURLConnection) url.openConnection();
            Object content = conn.getContent();
            System.out.println(content);

            JarFile jarfile = conn.getJarFile();

            // Get the entry name; it should be the same as specified on URL
            String entryName = conn.getEntryName();

            // Get the jar entry
            JarEntry jarEntry = conn.getJarEntry();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
