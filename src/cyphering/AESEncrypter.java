/**
 *
 */
package cyphering;

/**
 * @author Ashwin Kumar
 *
 */

import javax.crypto.*;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.*;
import java.security.NoSuchAlgorithmException;
import java.security.spec.AlgorithmParameterSpec;

public class AESEncrypter {
    Cipher ecipher;
    Cipher dcipher;

    public AESEncrypter(SecretKey key) {
        // Create an 8-byte initialization vector
        byte[] iv = new byte[]{0x00, 0x01, 0x02, 0x03, 0x04, 0x05, 0x06,
                0x07, 0x08, 0x09, 0x0a, 0x0b, 0x0c, 0x0d, 0x0e, 0x0f};

        AlgorithmParameterSpec paramSpec = new IvParameterSpec(iv);
        try {
            ecipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            dcipher = Cipher.getInstance("AES/CBC/PKCS5Padding");

            // CBC requires an initialization vector
            ecipher.init(Cipher.ENCRYPT_MODE, key, paramSpec);
            dcipher.init(Cipher.DECRYPT_MODE, key, paramSpec);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Buffer used to transport the bytes from one stream to another
    byte[] buf = new byte[1024];

    /**
     * To Encrypt
     *
     * @param in
     * @param out
     */
        public void encrypt(InputStream in, OutputStream out) {
        try {
            // Bytes written to out will be encrypted
            out = new CipherOutputStream(out, ecipher);

            // Read in the cleartext bytes and write to out to encrypt
            int numRead = 0;
            while ((numRead = in.read(buf)) >= 0) {
                out.write(buf, 0, numRead);
            }
            out.close();
        } catch (java.io.IOException e) {
        }
    }

    /**
     * To Decrypt
     *
     * @param in
     * @param out
     */
    public void decrypt(InputStream in, OutputStream out) {
        try {
            // Bytes read from in will be decrypted
            in = new CipherInputStream(in, dcipher);

            // Read in the decrypted bytes and write the cleartext to out
            int numRead = 0;
            while ((numRead = in.read(buf)) >= 0) {
                out.write(buf, 0, numRead);
            }
            out.close();
        } catch (java.io.IOException e) {
        }
    }

    /**
     * To Generate and Return Key
     *
     * @return
     * @throws IOException
     * @throws NoSuchAlgorithmException
     */
    public static SecretKeySpec getKeySpec() throws IOException, NoSuchAlgorithmException {
        byte[] bytes = new byte[16];
        File f = new File("aes_key");
        SecretKey key;
        SecretKeySpec spec;
        if (f.exists()) {
            new FileInputStream(f).read(bytes);
        } else {
            KeyGenerator kgen = KeyGenerator.getInstance("AES");
            kgen.init(128);
            key = kgen.generateKey();
            bytes = key.getEncoded();
            new FileOutputStream(f).write(bytes);
        }
        spec = new SecretKeySpec(bytes, "AES");
        return spec;
    }

    public static void main(String args[]) {
        try {

            /*
                * KeyGenerator kgen = KeyGenerator.getInstance("AES");
                * kgen.init(128);
                * SecretKey key = kgen.generateKey();
                */

            // Create encrypter/decrypter class
            AESEncrypter encrypter = new AESEncrypter(getKeySpec());

            //String path = "e:/Projects/ParallelCompute/CourseGUI/ScalaFX/";
            String path = "e:/Projects/ParallelCompute/CourseGUI/";

            // Encrypt
            encrypter.encrypt(new FileInputStream(path + "CourseContentEnc.zip"),
                    new FileOutputStream(path + "CourseContentEnc2.zip"));
            // Decrypt
            encrypter.decrypt(new FileInputStream(path + "CourseContentEnc2.zip"),
                    new FileOutputStream(path + "CourseContentEnc3.zip"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}