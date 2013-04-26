/**
 *
 */
package cyphering;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.zip.Adler32;
import java.util.zip.CheckedOutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * @author Ashwin Kumar
 */

public class CompressionUtility {
    static final int BUFFER = 2048;

    public static void main(String argv[]) {
        try {
            BufferedInputStream origin;
            //FileOutputStream dest = new FileOutputStream("e:/Projects/ParallelCompute/CourseGUI/ScalaFX/myfigs.zip");
            FileOutputStream dest = new FileOutputStream("e:/Projects/ParallelCompute/CourseGUI/CourseContentEnc.zip");
            CheckedOutputStream checksum = new CheckedOutputStream(dest, new Adler32());
            ZipOutputStream out = new ZipOutputStream(new BufferedOutputStream(checksum));
            // out.setMethod(ZipOutputStream.DEFLATED);
            byte data[] = new byte[BUFFER];
            // get a list of files from current directory
            String path = "e:/Projects/ParallelCompute/CourseGUI/CourseContent/";
            File f = new File(path);
            File files[] = f.listFiles();

            for (int i = 0; i < files.length; i++) {
                try {
                    System.out.println("Adding: " + files[i]);
                    FileInputStream fi = new FileInputStream(path + files[i]);
                    origin = new BufferedInputStream(fi, BUFFER);
                    String filePath = files[i].getPath();
                    ZipEntry entry = new ZipEntry(filePath);
                    out.putNextEntry(entry);
                    int count;
                    while ((count = origin.read(data, 0, BUFFER)) != -1) {
                        out.write(data, 0, count);
                    }
                    origin.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            out.close();
            System.out.println("checksum:    " + checksum.getChecksum().getValue());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}