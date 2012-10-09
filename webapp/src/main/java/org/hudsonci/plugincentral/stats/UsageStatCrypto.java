/*
 * The MIT License
 * 
 * Copyright (c) 2004-2010, Sun Microsystems, Inc., Kohsuke Kawaguchi
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package org.hudsonci.plugincentral.stats;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.FilterInputStream;
import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.security.GeneralSecurityException;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;
import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.CipherOutputStream;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import org.apache.shiro.codec.Base64;
import org.hudsonci.plugincentral.Utils;

public class UsageStatCrypto {

    private final String keyImage;
    //private String encrypted = "E14acG+xG93okDbwXBcO84WrLaFwzxLdCkZ3FLNPv5bjgGt5bJD0GN4qWv8iHCEzyREHU6ys8dfavlu+wUwGwd1Nrcw2xtbC7vRuxR4XMFaaY1yvxmvUY+sS+DosxujoD4VLXyV4OQ2O+JRydmW8rznJvEvPDlXoRnBMEuN24VezfT4SgDvCOJwLN9VCPjOkjJ+xMjA6EJEvb9UwpfUwLA0XiHTxQRNFduZPcAE9oju8KVFoRqiWPYzKod3T9MZYhPH249m5PuBjebdmCmfhECpvskG6N0G0VlArffADwOB5RHmLtBV0UVpYDDOl3E3sDSnICayL2a4iaXqXE2+H0Ba7dUhFrYV8XLX80F3a6FTkY5CJwtnDSpyGDJWYJV9LFD5PtO8lUwt/D9kdOsIbdFbl/Q3/UvvdbvWEMa+60wIo8fCps7HAkh6kvTUoQm7XWfGk5xLloGq9ua82DCotwLGkvrxWrKrgfa6QDysinI7ckbJ0OpQPBqENHOKm3LQDpv12CMt+Pl3A4wBItnz/iQ4cDf+FPG1GVK8MYkXFny18s9wL0JS5gU8XrP9n1IUamC7e/Yi/hf0lYdmPr8v7G2xFxn3JDp7LJ5+FTSiFAbY=";
    /**
     * Lazily computed {@link PublicKey} representation of {@link #keyImage}.
     */
    private volatile transient PublicKey key;
    /**
     * Public key to encrypt the usage statistics
     */
    private static final String DEFAULT_PUBLIC_KEY = "30819f300d06092a864886f70d010101050003818d00308189028181008e68beffebd5a213d4b47b29d611221c8cd145a865290ceac6769395cdaf98a784c11f4880548d119b3faffb79b51d06c7c783ee2d897b34c3e27010a06e9798d5b4effa4cafb74a90bf8e48099f859ce040d766eeba7d9f0d02c653d6b6a7f317e5734c03befcc3f87342257fe8e4b2f31aeefba5a60356fdedcf62169561150203010001";
    private String privateKey = "30820276020100300d06092a864886f70d0101010500048202603082025c020100028181008e68beffebd5a213d4b47b29d611221c8cd145a865290ceac6769395cdaf98a784c11f4880548d119b3faffb79b51d06c7c783ee2d897b34c3e27010a06e9798d5b4effa4cafb74a90bf8e48099f859ce040d766eeba7d9f0d02c653d6b6a7f317e5734c03befcc3f87342257fe8e4b2f31aeefba5a60356fdedcf62169561150203010001028180609edee022714acb2ba8dc5b913b04aadc3bec880942a5d9f0976920dd72031fbd698e411198cc31a2e4be10e00daac8fbe8f9313342310ea6ccce7de96bbf7c56327634cc1a577c6967d1d77dc332296575ba68a2c8b5eedc8a089e7731322014d634d1f6405dd2ff0d701c9f365b13349254d793a600c3fc6f11982f132d8d024100c88ee98a6c624999785b6038291ca996e174177cbe3b64c604042399ce074b75fd095df5be5690f42f0557cb4ceec3f912edfae1f2d4d89d97a5aadf41db1917024100b5c6bea148abb0a23df1f27a4078735cf64a2c8c435c7c4ee9f51cb2e2680b1701284433648c251d800553a2ec87c91432e651f38647dd10455687b0f5ed9ab302403a186bbe1537436a995a9ebca5ec0f940d2abbc2feb7e5b11668bc87490f03e4e1af5cf05a7a68f2d2cc116a6a969f5dff05c0aec2d0b50eb166e3e0580b47710241008881f826a6ac48b98e4b640ceadd89c506302dc427d20c8836aa1c233a6367f09eadde89b9b88526e23d1dd1fc4efc726ec7084419535e7019a221c482eb2c8102404f21b4811abe5586d9aeca08ae3a72a9369c6b12482d3132920215fdd8f91ae98203f7fc037caf2f896b838d8cdf67d59dfc8b333758e8c6ffa0f105edc0bb9d";

    public UsageStatCrypto(String privateKey) {
        this.keyImage = DEFAULT_PUBLIC_KEY;
        this.privateKey = privateKey;
    }

    private Cipher getCipher() {
        try {
            if (key == null) {
                KeyFactory keyFactory = KeyFactory.getInstance("RSA");
                key = keyFactory.generatePublic(new X509EncodedKeySpec(Utils.fromHexString(keyImage)));
            }

            Cipher cipher = Cipher.getInstance("RSA");
            cipher.init(Cipher.ENCRYPT_MODE, key);
            return cipher;
        } catch (GeneralSecurityException e) {
            throw new Error(e); // impossible
        }
    }

    public String getEncryptedData(String data) throws IOException {
        try {


            ByteArrayOutputStream baos = new ByteArrayOutputStream();

            // json -> UTF-8 encode -> gzip -> encrypt -> base64 -> string
            OutputStreamWriter w = new OutputStreamWriter(new GZIPOutputStream(new CombinedCipherOutputStream(baos, getCipher(), "AES")), "UTF-8");
            w.write(data);
            w.close();

            return new String(Base64.encode(baos.toByteArray()));
        } catch (GeneralSecurityException e) {
            throw new Error(e); // impossible
        }
    }

    /**
     * Gets the encrypted usage stat data to be sent to the Hudson server.
     */
    public String getDecryptedData(String encrypted) throws IOException {

        try {

            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            PrivateKey priv = keyFactory.generatePrivate(new PKCS8EncodedKeySpec(Utils.fromHexString(privateKey)));

            Cipher cipher = Cipher.getInstance("RSA");
            cipher.init(Cipher.DECRYPT_MODE, priv);

            byte[] cipherText = Base64.decode(encrypted);
            InputStreamReader isr = new InputStreamReader(new GZIPInputStream(
                    new CombinedCipherInputStream(new ByteArrayInputStream(cipherText), cipher, "AES", 1024)), "UTF-8");

            StringBuilder stringBuilder = new StringBuilder();

            char[] str = new char[2048];


            while (isr.read(str) > 0) {
                stringBuilder.append(str);
            }

            return stringBuilder.toString();

        } catch (GeneralSecurityException e) {
            throw new Error(e); // impossible
        }
    }

    /**
     * Assymetric cipher is slow and in case of Sun RSA implementation it can
     * only encyrypt the first block.
     *
     * So first create a symmetric key, then place this key in the beginning of
     * the stream by encrypting it with the assymetric cipher. The rest of the
     * stream will be encrypted by a symmetric cipher.
     */
    public static final class CombinedCipherOutputStream extends FilterOutputStream {

        public CombinedCipherOutputStream(OutputStream out, Cipher asym, String algorithm) throws IOException, GeneralSecurityException {
            super(out);

            // create a new symmetric cipher key used for this stream
            SecretKey symKey = KeyGenerator.getInstance(algorithm).generateKey();

            // place the symmetric key by encrypting it with asymmetric cipher
            out.write(asym.doFinal(symKey.getEncoded()));

            // the rest of the data will be encrypted by this symmetric cipher
            Cipher sym = Cipher.getInstance(algorithm);
            sym.init(Cipher.ENCRYPT_MODE, symKey);
            super.out = new CipherOutputStream(out, sym);
        }
    }

    /**
     * The opposite of the {@link CombinedCipherOutputStream}.
     */
    public static final class CombinedCipherInputStream extends FilterInputStream {

        /**
         * @param keyLength Block size of the asymmetric cipher, in bits. I
         * thought I can get it from {@code asym.getBlockSize()} but that
         * doesn't work with Sun's implementation.
         */
        public CombinedCipherInputStream(InputStream in, Cipher asym, String algorithm, int keyLength) throws IOException, GeneralSecurityException {
            super(in);

            // first read the symmetric key cipher
            byte[] symKeyBytes = new byte[keyLength / 8];
            new DataInputStream(in).readFully(symKeyBytes);
            SecretKey symKey = new SecretKeySpec(asym.doFinal(symKeyBytes), algorithm);

            // the rest of the data will be decrypted by this symmetric cipher
            Cipher sym = Cipher.getInstance(algorithm);
            sym.init(Cipher.DECRYPT_MODE, symKey);
            super.in = new CipherInputStream(in, sym);
        }
    }
    public static void main(String[] args) throws IOException {
        String privateKey = "30820276020100300d06092a864886f70d0101010500048202603082025c020100028181008e68beffebd5a213d4b47b29d611221c8cd145a865290ceac6769395cdaf98a784c11f4880548d119b3faffb79b51d06c7c783ee2d897b34c3e27010a06e9798d5b4effa4cafb74a90bf8e48099f859ce040d766eeba7d9f0d02c653d6b6a7f317e5734c03befcc3f87342257fe8e4b2f31aeefba5a60356fdedcf62169561150203010001028180609edee022714acb2ba8dc5b913b04aadc3bec880942a5d9f0976920dd72031fbd698e411198cc31a2e4be10e00daac8fbe8f9313342310ea6ccce7de96bbf7c56327634cc1a577c6967d1d77dc332296575ba68a2c8b5eedc8a089e7731322014d634d1f6405dd2ff0d701c9f365b13349254d793a600c3fc6f11982f132d8d024100c88ee98a6c624999785b6038291ca996e174177cbe3b64c604042399ce074b75fd095df5be5690f42f0557cb4ceec3f912edfae1f2d4d89d97a5aadf41db1917024100b5c6bea148abb0a23df1f27a4078735cf64a2c8c435c7c4ee9f51cb2e2680b1701284433648c251d800553a2ec87c91432e651f38647dd10455687b0f5ed9ab302403a186bbe1537436a995a9ebca5ec0f940d2abbc2feb7e5b11668bc87490f03e4e1af5cf05a7a68f2d2cc116a6a969f5dff05c0aec2d0b50eb166e3e0580b47710241008881f826a6ac48b98e4b640ceadd89c506302dc427d20c8836aa1c233a6367f09eadde89b9b88526e23d1dd1fc4efc726ec7084419535e7019a221c482eb2c8102404f21b4811abe5586d9aeca08ae3a72a9369c6b12482d3132920215fdd8f91ae98203f7fc037caf2f896b838d8cdf67d59dfc8b333758e8c6ffa0f105edc0bb9d";
        UsageStatCrypto usageStatistics = new UsageStatCrypto(privateKey);
        //String encrypted = "ZZSKZNcZGCVGhs0VaF2h8FdeBnu4wMT+lHPEGhLlwqdQXwFQlQyPG2t1YftOg98vsECCr5vLL/HOgzjm/+yHXabj2onsD4IVIIJLMZnC2WBMb/c2LBGBDQUYNVlOs2eBP5VWW9n0Noa5CuUyFKhfWJy5IPz0L2yLkDZgXC+yIApESs38kPzJonpxXvGptPmJolmtPCh6nfzccwu2fsJdrmemfFGRGN7NvemWAjUkKB/V6nhKKtankSbxMaAfcmQuvesWvMU16YvyM/zqmMZPuK1nVeD32iwvk6PJEi41gWC3r5s34AFT3AvrHNA4fq7pPD1T6inNGIHc8kf2UAwo90x45lZvpSE94dR6t5qnQt1Cu7JB4kAUk+k9qSUY0eaNhO700Y8g6ZQ2gJi7Yhg9+ZC0b12RnIN94fERU+Poml7MaofQ+A8sxScoQQEbpjDLPseb295svcn7W0hFz7Rgj/3Jpp0dQbuzasBppxxzZhaT7WTV8NpXdJtW/wQ86vidi8zgnFYOJhL/wqgMwZqMmzTEF0BXqEDIaCvdZxSz4yH9BiqGNlRtxsKpkpVVJWtNhzcZJfJbvqxA+4kTNGrlt/881Gwwa358W0La7Zo2accuoBHwgXRUN9sZQpgfNxTU";
        String encrypted = "ZFu9VIS8FKaJqPozG63pYOpZKzCPwRnCTlEJX28JsaZfT5VoIH0dazF4EDK5GPu1xwRtml+O9LhLdhIuGZMqZ8bMgXriyVjWgCY2YUX7+K1HUQD0u/8drKpSeoQ/gWN8CZZY9sN0/9U5hpOjPzd2oUEQ9Yz2TYtdXVe3L7NU2p5VpuvA/sIZg9ru/NKufY+XpC4n70HYECTEHdaBTsgyTaivMuQ3IrG5Lr8D666fKnLErV3TU7G/hYkzK4RAyu89BLfeiXEmCoTaMumG0MJc4LicAFNbKpzacBBRJyB9C1GJjMncGyAFmYSwyTtuKMpE6/tJk/xUczH5rQcOllzkAwfhN/5LqmOCafW3EV05kSOeoiqV20p50vnXep9lsB+LTHUeVxmjO8iEDqinHoB87GElUw3cYfys9uH5eONE3wWzh6QwdSZpYpHYWuGsEcWVtf7NUdL7+/mG/k17nyN7kdJVaaRL/c5amxZDCgWnoI6JDVpcTcLwuzAxADupOaCP/rHtYWKvL5W3ENEHl8IURhZdUHxkOen41kC+zWRZ9f5712ZRBVn4Goo6XurQJTBRe2Xf0OcoL64cM/mnqZow7GCe0xY9CAhY3Ej+OskMGrLUgA+AML/XdZKl1rslEre/QArFEB53Fpf4lL8fjnwlkpR/4vSpLhsYL0uOwHAdLXLRAiAOE2CW30eeN7kn3CUV+1HLzgI0g5PrjImjhDZv9A==";
        
        //System.out.println("original: " + data);
        //String encrypted = usageStatistics.getEncryptedData(data);
        //System.out.println("Encrypted: " + encrypted);
        String decrypted = usageStatistics.getDecryptedData(encrypted);
        System.out.println("decrypted: " + decrypted);
    }
}
