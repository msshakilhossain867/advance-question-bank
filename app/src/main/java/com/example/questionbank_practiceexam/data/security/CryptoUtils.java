package com.example.questionbank_practiceexam.data.security;


import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

import java.io.InputStream;
import java.io.OutputStream;

public class CryptoUtils {

    private static final String ALGORITHM = "AES";
    // ⚠️ এই ১৬ অক্ষরের পাসওয়ার্ডটি পিসির এনক্রিপ্টরের সাথে হুবহু মিল থাকতে হবে
    private static final String SECRET_KEY = "MyBcsSecretKey45";

    // লক করা ফাইল খুলে মোবাইলের সিস্টেমে রাইট করার মেথড
    public static void decryptDatabase(InputStream inputStream, OutputStream outputStream) throws Exception {
        SecretKeySpec keySpec = new SecretKeySpec(SECRET_KEY.getBytes(), ALGORITHM);
        Cipher cipher = Cipher.getInstance(ALGORITHM);
        cipher.init(Cipher.DECRYPT_MODE, keySpec);

        byte[] buffer = new byte[1024];
        int bytesRead;
        while ((bytesRead = inputStream.read(buffer)) != -1) {
            byte[] output = cipher.update(buffer, 0, bytesRead);
            if (output != null) {
                outputStream.write(output);
            }
        }
        byte[] outputBytes = cipher.doFinal();
        if (outputBytes != null) {
            outputStream.write(outputBytes);
        }
    }
}
