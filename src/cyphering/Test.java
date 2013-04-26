package cyphering;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public class Test {
    static final int BUFFER = 2048;

    public static void main(String argv[]) {
        try {
            BufferedOutputStream dest = null;
            ZipFile zipfile = new ZipFile("e:/Projects/ParallelCompute/CourseGUI/CourseContent.zip");

            Enumeration<? extends ZipEntry> entries = zipfile.entries();
            while (entries.hasMoreElements()) {
                ZipEntry zipEntry = entries.nextElement();
                System.out.println(zipEntry.getName());
            }

            ZipEntry entry = zipfile.getEntry("Lecture1/slide1.wav");
            System.out.println("Extracting: " + entry);
            BufferedInputStream is = new BufferedInputStream(zipfile.getInputStream(entry));
            int count;
            byte data[] = new byte[BUFFER];
            FileOutputStream fos = new FileOutputStream("slide1.wav");
            dest = new BufferedOutputStream(fos, BUFFER);
            while ((count = is.read(data, 0, BUFFER)) != -1) {
                dest.write(data, 0, count);
            }
            dest.flush();
            dest.close();
            is.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}