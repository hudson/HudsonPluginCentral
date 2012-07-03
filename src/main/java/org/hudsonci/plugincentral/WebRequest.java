
package org.hudsonci.plugincentral;

//import com.sun.tools.javah.Util;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.ConnectException;
import java.net.HttpURLConnection;
import java.net.SocketTimeoutException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.UnknownHostException;
import java.nio.charset.Charset;

/**
 * Some common web requests done using HTTP.
 * 
 * @author Winston Prakash
 */
public class WebRequest {

    public static final String MIME_APPLICATION_XML = "application/xml; charset=UTF-8";
    private static final Charset REQUEST_ENCODING = Charset.forName("UTF-8");
    
    private final static int TIME_OUT_RETRY_COUNT = 10;
    
    protected HttpURLConnection urlConn;

    /**
     * Create a web request for the specified URI with caching and redirects disabled.
     *
     * @param uriStr The connection destination.
     * @throws URISyntaxException For bad URIs.
     * @throws IOException For failures creating the connection or setting connection options.
     */
    public WebRequest(String uriStr) throws URISyntaxException, IOException {
        createConnection(new URI(uriStr));
    }

    protected void createConnection(URI uri) throws IOException {
        urlConn = (HttpURLConnection) uri.toURL().openConnection();
        urlConn.setConnectTimeout(2000);
        urlConn.setUseCaches(true);
        urlConn.setAllowUserInteraction(false);
        urlConn.setInstanceFollowRedirects(true);
    }

    public HttpURLConnection getConnection() throws IOException{
        return urlConn;
    }
    
    void connect() throws IOException {
        boolean connected = false;
        int count = 0;
        while (!connected) {
            try {
                urlConn.connect();
                connected = true;
            } catch (SocketTimeoutException exc) {
                // Util.log("Connection timed out. try " + count);
                if (++count > TIME_OUT_RETRY_COUNT) {
                    throw new IOException(
                            "Connection timed out after " + TIME_OUT_RETRY_COUNT + " tries");
                }
                connected = false;
            } catch (UnknownHostException exc) {
                throw new IOException(
                        "Could not connect to Server. Check your internet connection and try again later.",
                        exc);
            }catch (ConnectException exc) {
                throw new IOException(
                        "Could not connect to Server. Check your internet connection and try again later.",
                        exc);
            }
        }
    }


    public void setContentType(String contentType) {
        urlConn.setRequestProperty("Content-Type", contentType);
    }

    public WebResponse delete() throws IOException {
        return request("DELETE", null);
    }

    public WebResponse get() throws IOException {
        return request("GET", null);
    }

    public WebResponse head() throws IOException {
        return request("HEAD", null);
    }

    public WebResponse post(String source) throws IOException {
        setContentType(MIME_APPLICATION_XML);
        return request("POST", source);
    }

    public WebResponse put(String source) throws IOException {
        setContentType(MIME_APPLICATION_XML);
        return request("PUT", source);
    }

    private WebResponse request(String method, String source) throws IOException {
         
        urlConn.setRequestMethod(method);
        urlConn.setDoInput(true);
        connect();
        if (source != null) {
            urlConn.setRequestProperty("Content-Length", Integer.toString(source.length()));

            urlConn.setDoOutput(true);

            urlConn.connect();

            OutputStream os = urlConn.getOutputStream();
            os.write(source.getBytes(REQUEST_ENCODING.name()));
            os.flush();
        } else {
            try{
                urlConn.connect();
            }catch (Exception exc){
                new IOException(exc.getLocalizedMessage());
            }
        }

        WebResponse response = getResponse(urlConn, !("DELETE".equalsIgnoreCase(method) || "HEAD".equalsIgnoreCase(method)));
        urlConn.disconnect();

        return response;
    }

    private WebResponse getResponse(HttpURLConnection urlConn, boolean responseExpected) throws IOException {
        WebResponse webResponse = null;
        try {
            int responseCode = urlConn.getResponseCode();
            webResponse = new WebResponse(responseCode, urlConn.getResponseMessage());
            InputStream is = null;
            if (responseExpected) {
                if (responseCode < HttpURLConnection.HTTP_MULT_CHOICE) {
                    is = urlConn.getInputStream();
                } else {
                    is = urlConn.getErrorStream();
                }
                StringBuilder stringBuilder = new StringBuilder();

                BufferedReader in = new BufferedReader(new InputStreamReader(is));
                String str;
                while ((str = in.readLine()) != null) {
                    stringBuilder.append(str).append('\n');
                }
                in.close();

                is.close();
                webResponse.setResponse(stringBuilder.toString());
                webResponse.setResponseMessage(urlConn.getResponseMessage());
            }
        } catch (Exception exc) {
            throw new IOException(exc.getLocalizedMessage());
        }
        

        return webResponse;
    }
}
