package org.hudsonci.plugincentral;

import java.io.StringWriter;
import java.io.Writer;
import java.net.HttpURLConnection;
import java.net.URISyntaxException;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.SerializationConfig;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Map;
import java.util.TreeMap;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import org.hudsonci.plugincentral.model.Plugin;
import org.hudsonci.plugincentral.model.PluginUpdateCenter;
import org.hudsonci.plugincentral.model.UpdateSite;
import org.hudsonci.plugincentral.model.UpdateSiteList;

/**
 * Update Center Utilities
 * @author Winston Prakash
 */
public class Utils {

    private static ObjectMapper jsonObjectMapper = new ObjectMapper();

    static {
        jsonObjectMapper.configure(SerializationConfig.Feature.INDENT_OUTPUT, true);
    }

    public static PluginUpdateCenter parseFromUrl(String updateCenterURL) throws IOException {
        try {
            WebRequest webRequest = new WebRequest(updateCenterURL);
            WebResponse webResponse = webRequest.get();
            if (webResponse.getResponseCode() < HttpURLConnection.HTTP_MULT_CHOICE) {
                String response = webResponse.getResponse();
                //System.out.println(response);

                if (response.startsWith("updateCenter.post(")) {
                    response = response.substring("updateCenter.post(".length());
                }

                if (response.endsWith(");")) {
                    response = response.substring(0, response.lastIndexOf(");"));
                }

                return parseUpdateCenter(response);
            } else {
                throw new IOException("Error fetching Json. Server returned code - " + webResponse.getResponseCode());
            }
        } catch (URISyntaxException ex) {
            throw new IOException(ex.getCause());
        }

    }

    public static PluginUpdateCenter parseUpdateCenter(String jsonString) throws IOException {
        return parse(jsonString, PluginUpdateCenter.class);
    }
    
    public static UpdateSite parseUpdateSite(String jsonString) throws IOException {
        return parse(jsonString, UpdateSite.class);
    }
    
    public static UpdateSiteList parseUpdateSiteList(String jsonString) throws IOException {
        return parse(jsonString, UpdateSiteList.class);
    }
    
    public static <T extends Object> T parse(String jsonString, Class<T> valueType) throws IOException{
        return jsonObjectMapper.readValue(jsonString, valueType);
    }

    public static String getAsString(PluginUpdateCenter updateCenter) throws IOException {
        // Sort the plugins first
        Map<String, Plugin> plugins = new TreeMap<String, Plugin>(String.CASE_INSENSITIVE_ORDER);
        for (String name : updateCenter.getPlugins().keySet()){
            plugins.put(name, updateCenter.findPlugin(name));
        }
        updateCenter.setPlugins(plugins);
        Writer writer = new StringWriter();
        jsonObjectMapper.writeValue(writer, updateCenter);
        return writer.toString();
    }

    public static boolean downloadFile(String uriStr, File target) throws IOException {
        InputStream stream = null;
        OutputStream out = null;
        try {
            HttpURLConnection connection;

            WebRequest webRequest = new WebRequest(uriStr);
            connection = webRequest.getConnection();

            //just make sure to connect with multiple connect attempt
            connection.connect();

            byte[] buff = new byte[4096];
            stream = new BufferedInputStream(connection.getInputStream());

            out = new FileOutputStream(target);
            
            int bytesRead;
            while ((bytesRead = stream.read(buff)) != -1) {
                out.write(buff, 0, bytesRead);
            }

            return false;
        } catch (URISyntaxException ex) {
            throw new IOException(ex);
        } finally {
            if (null != stream) {
                stream.close();
            }

            if (null != out) {
                out.close();
            }
        }
    }
    
    /**
     * Cuts all the leading path portion and get just the file name.
     */
    public static String getFileName(String filePath) {
        int idx = filePath.lastIndexOf('\\');
        if (idx >= 0) {
            return getFileName(filePath.substring(idx + 1));
        }
        idx = filePath.lastIndexOf('/');
        if (idx >= 0) {
            return getFileName(filePath.substring(idx + 1));
        }
        return filePath;
    }
    
    /**
     * Converts a string into 128-bit AES key.
     * @since 1.308
     */
    public static SecretKey toAes128Key(String s) {
        try {
            // turn secretKey into 256 bit hash
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            digest.reset();
            digest.update(s.getBytes("UTF-8"));

            // Due to the stupid US export restriction JDK only ships 128bit version.
            return new SecretKeySpec(digest.digest(), 0, 128 / 8, "AES");
        } catch (NoSuchAlgorithmException e) {
            throw new Error(e);
        } catch (UnsupportedEncodingException e) {
            throw new Error(e);
        }
    }

    public static String toHexString(byte[] data, int start, int len) {
        StringBuilder buf = new StringBuilder();
        for (int i = 0; i < len; i++) {
            int b = data[start + i] & 0xFF;
            if (b < 16) {
                buf.append('0');
            }
            buf.append(Integer.toHexString(b));
        }
        return buf.toString();
    }

    public static String toHexString(byte[] bytes) {
        return toHexString(bytes, 0, bytes.length);
    }

    public static byte[] fromHexString(String data) {
        byte[] r = new byte[data.length() / 2];
        for (int i = 0; i < data.length(); i += 2) {
            r[i / 2] = (byte) Integer.parseInt(data.substring(i, i + 2), 16);
        }
        return r;
    }
}
