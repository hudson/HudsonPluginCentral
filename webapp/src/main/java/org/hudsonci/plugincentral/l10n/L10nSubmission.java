package org.hudsonci.plugincentral.l10n;

import com.sun.mail.util.BASE64DecoderStream;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.StaplerRequest;
import org.kohsuke.stapler.StaplerResponse;

import javax.servlet.http.Cookie;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Date;
import java.util.Properties;
import java.util.UUID;
import java.util.zip.GZIPInputStream;

/**
 * The L10N submission from Hudson users are stored via this utility. Both the
 * submitted JSON file and the properties are stored
 *
 * @author Winston Prakash
 */
public class L10nSubmission {

    private String l10nStorePath;

    public L10nSubmission(String l10nStorePath) {
        this.l10nStorePath = l10nStorePath;
    }

    public static final class SubmissionEntry {

        public final String text, baseName, key, original;

        @DataBoundConstructor
        public SubmissionEntry(String text, String baseName, String key, String original) {
            this.text = text;
            this.baseName = baseName;
            this.key = key;
            this.original = original;
        }

        public boolean isUpdated() {
            return !original.equals(text);
        }
    }

    public void doSubmit(StaplerRequest req, StaplerResponse rsp) throws IOException {
        System.out.println("Accepting L10n submission");

        Cookie c = findIDCookie(req);
        if (c == null) {
            c = new Cookie("ID", UUID.randomUUID().toString());
            c.setPath("/");
            c.setMaxAge(60 * 60 * 24 * 365 * 10); // 10 years
            rsp.addCookie(c);
        }

        String queryString = req.getQueryString();

        handleSubmission(req, c, queryString);

        // send back the response
        rsp.setStatus(200);
        rsp.setContentType("text/javascript");
        PrintWriter out = rsp.getWriter();
        out.println("");
        out.close();
    }

    /**
     * Processes submission.
     *
     * @param req Can be null.
     * @param c Can be null.
     */
    private void handleSubmission(StaplerRequest req, Cookie c, String queryString) throws IOException {
        String id = UUID.randomUUID().toString();

        File l10nStore = new File(l10nStorePath);

        String gzippedBase64EncodedJson = IOUtils.toString(new GZIPInputStream(new BASE64DecoderStream(new ByteArrayInputStream(queryString.getBytes()))), "UTF-8");
        JSONObject json = JSONObject.fromObject(gzippedBase64EncodedJson);

        filter(json);

        l10nStore = new File(l10nStore, json.getString("locale"));
        l10nStore.mkdirs();

        FileUtils.writeStringToFile(new File(l10nStore, id + ".json"), json.toString(2), "UTF-8");

        Properties props = new Properties();
        if (req != null) {
            props.setProperty("remoteHost", req.getRemoteHost());
            props.setProperty("referer", req.getReferer());
        }
        if (c != null) {
            props.setProperty("cookie", c.getValue());
        }
        props.setProperty("timestamp", String.valueOf(System.currentTimeMillis()));
        props.setProperty("date", new Date().toString());
        props.setProperty("submitter", json.getString("submitter"));
        props.setProperty("id", json.getString("id"));
        props.setProperty("version", json.getString("version"));
        FileOutputStream os = new FileOutputStream(new File(l10nStore, id + ".properties"));
        try {
            props.store(os, null);
        } finally {
            os.close();
        }
    }

    /**
     * Filter out entries that haven't changed.
     */
    private void filter(JSONObject json) {
        Object e = json.get("entry");
        if (e instanceof JSONObject) {
            JSONArray a = new JSONArray();
            a.add(e);
            json.put("entry", a);
            e = a;
        }
        if (e instanceof JSONArray) {
            JSONArray a = (JSONArray) e;
            for (JSONObject o : new ArrayList<JSONObject>(a)) {
                if (o.getString("original").equals(o.getString("text"))) {
                    a.remove(o);
                }
            }
        }
    }

    private Cookie findIDCookie(StaplerRequest req) {
        Cookie[] cookies = req.getCookies();
        if (cookies == null) {
            return null;
        }

        for (Cookie c : cookies) {
            if (c.getName().equals("ID")) {
                return c;
            }
        }
        return null;
    }
}
