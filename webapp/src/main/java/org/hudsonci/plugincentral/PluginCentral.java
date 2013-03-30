package org.hudsonci.plugincentral;

import java.io.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import org.apache.commons.io.FileUtils;
import org.hudsonci.plugincentral.model.UpdateSite;
import org.kohsuke.stapler.StaplerFallback;

/**
 * Hudson Plugin Central is a collection of update center
 *
 * @author Winston Prakash
 */
public class PluginCentral implements StaplerFallback {


    private final Map<String, UpdateCenter> updateCenters = new HashMap<String, UpdateCenter>();

    private final String defaultSite = "3.0";

    public PluginCentral() throws IOException {
        String updateSiteJsonPath = System.getProperty("update-site-json", "/Users/wjprakash/Hudson3/PluginCentral-test/update-site.json");
        String updateSiteJson = FileUtils.readFileToString(new File(updateSiteJsonPath));

        Map<String, UpdateSite> updateSites = Utils.parseUpdateSiteList(updateSiteJson).getUpdateSites();
        for (String key : updateSites.keySet()) {
            updateCenters.put(key, new UpdateCenter(updateSites.get(key)));
        }
    }

    public UpdateCenter getSite(String siteId) {
        return updateCenters.get(siteId);
    }

    @Override
    public UpdateCenter getStaplerFallback() {
        return updateCenters.get(defaultSite);
    }
    
    public Set<String> getSites(){
        return updateCenters.keySet();
    }
}
