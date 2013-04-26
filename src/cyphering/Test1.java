package cyphering;

import java.io.*;
import java.security.NoSuchAlgorithmException;
import java.util.zip.*;

public class Test1 {
    static final int BUFFER = 2048;
    static byte data[] = new byte[BUFFER];

    static void Process(File aFile, ZipOutputStream out) {
        if (aFile.isFile()) {
            System.out.println("[FILE] " + aFile.getName());
            try {
                System.out.println("Adding: " + aFile);
                FileInputStream fi = new FileInputStream(aFile.getPath());
                BufferedInputStream origin = new BufferedInputStream(fi, BUFFER);
                String filePath = aFile.getPath();
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
        } else if (aFile.isDirectory()) {
            System.out.println("[DIR] " + aFile.getName());
            File[] listOfFiles = aFile.listFiles();
            if (listOfFiles != null) {
                for (int i = 0; i < listOfFiles.length; i++)
                    Process(listOfFiles[i], out);
            } else {
                System.out.println(" [ACCESS DENIED]");
            }
        }
    }

    public static void main1(String argv[]) {
        try {
            FileOutputStream dest = new FileOutputStream("e:/Projects/ParallelCompute/CourseGUI/CourseContentEnc.zip");
            CheckedOutputStream checksum = new CheckedOutputStream(dest, new Adler32());
            ZipOutputStream out = new ZipOutputStream(new BufferedOutputStream(checksum));
            // out.setMethod(ZipOutputStream.DEFLATED);
            // get a list of files from current directory
            String path = "e:/Projects/ParallelCompute/CourseGUI/CourseContent/";
            File f = new File(path);

            Process(f, out);

            out.close();
            System.out.println("checksum:    " + checksum.getChecksum().getValue());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public static void main2(String argv[]) {
        try {
            // NOTE: Try to store unzipped archive in the memory. There is Java out of memory exception.

            AESEncrypter encrypter = new AESEncrypter(AESEncrypter.getKeySpec());
            String path = "e:/Projects/ParallelCompute/CourseGUI/";
//            encrypter.decrypt(
//                    new FileInputStream(path + "CourseContentEnc2.zip"),
//                    new FileOutputStream(path + "CourseContentEnc3.zip"));

            FileInputStream fileInputStream = new FileInputStream(path + "CourseContentEnc2.zip");
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream(BUFFER);

            encrypter.decrypt(fileInputStream, byteArrayOutputStream);

            BufferedOutputStream dest = null;
            CheckedInputStream checksum = new CheckedInputStream(new ByteArrayInputStream(byteArrayOutputStream.toByteArray()), new Adler32());
            ZipInputStream zis = new ZipInputStream(new BufferedInputStream(checksum));
            ZipEntry entry;
            while ((entry = zis.getNextEntry()) != null) {
                System.out.println("Extracting: " + entry);
                int count;
                byte data[] = new byte[BUFFER];
                // write the files to the disk
                FileOutputStream fos = new FileOutputStream(entry.getName());
                dest = new BufferedOutputStream(fos, BUFFER);
                while ((count = zis.read(data, 0, BUFFER)) != -1) {
                    dest.write(data, 0, count);
                }
                dest.flush();
                dest.close();
            }
            zis.close();
            System.out.println("Checksum: " + checksum.getChecksum().getValue());
        } catch (IOException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
    }

    public static void main(String argv[]) {
        try {
            String path = "e:/Projects/ParallelCompute/CourseGUI/";
            AESEncrypter encrypter = new AESEncrypter(AESEncrypter.getKeySpec());
            encrypter.encrypt(new FileInputStream(path + "CourseContent.zip"), new FileOutputStream(path + "CourseContentEnc.zip"));
            encrypter.decrypt(new FileInputStream(path + "CourseContentEnc.zip"), new FileOutputStream(path + "tmp"));
        } catch (IOException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
    }
}
