package org.hudsonci.plugincentral.stats;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import org.apache.commons.io.FileUtils;
import org.kohsuke.stapler.StaplerRequest;
import org.kohsuke.stapler.StaplerResponse;

/**
 * The Usage Stats submission from Hudson users are stored via this utility.
 *
 * @author Winston Prakash
 */
public class UsageStats {

    private String statsStorePath;
    private String privateKey;
    private long maxFileLength = 10 * 1024 * 1024; // 10 MB

    public UsageStats(String statsStorePath, String privateKey) {
        this.statsStorePath = statsStorePath;
        this.privateKey = privateKey;
    }

    public void doSubmit(StaplerRequest req, StaplerResponse rsp) throws IOException {
        System.out.println("Accepting Usage Stats submission");

        handleSubmission(req.getQueryString());

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
    private synchronized void handleSubmission(String statsEncrypted) throws IOException {
        UsageStatCrypto usageStatCrypto = new UsageStatCrypto(privateKey);
        String statsDecrypted = usageStatCrypto.getDecryptedData(statsEncrypted) + "\n";
        
        File statsStore = new File(statsStorePath);
        statsStore.mkdirs();
        
        File statsFile = new File(statsStore, "usage-stats");

        if (!statsFile.exists()) {
            statsFile.createNewFile();
        }

        
        if (statsFile.length() < maxFileLength){
            FileUtils.writeStringToFile(statsFile, statsDecrypted, true);
        }else{
            DateFormat dateFormatter = new SimpleDateFormat("yyyyMMdd");
            File statsFileStore = new File(statsStore, "usage-stats-" + dateFormatter.format(new Date()));
            FileUtils.copyFile(statsFile, statsFileStore, true);
            FileUtils.writeStringToFile(statsFile, statsDecrypted);
        }
    }
}
