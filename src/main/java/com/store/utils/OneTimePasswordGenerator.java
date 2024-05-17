package com.store.utils;

import com.eatthepath.otp.TimeBasedOneTimePasswordGenerator;

import javax.crypto.KeyGenerator;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.time.Instant;

public class OneTimePasswordGenerator {
    public static int generateOTP() throws InvalidKeyException {
        final TimeBasedOneTimePasswordGenerator totp = new TimeBasedOneTimePasswordGenerator();
        final Key secretKey;
        {
            try {
                final KeyGenerator keyGenerator = KeyGenerator.getInstance(totp.getAlgorithm());
                keyGenerator.init(160);

                secretKey = keyGenerator.generateKey();
            } catch (NoSuchAlgorithmException e) {
                throw new RuntimeException(e);
            }
        }
        return totp.generateOneTimePassword(secretKey, Instant.now());
    }
}