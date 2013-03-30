package org.hudsonci.plugincentral.model;

import java.util.Map;
import java.util.TreeMap;

/**
 * Update Site collection
 *
 * @author Winston Prakash <winston.prakash@oracle.com>
 */
public class UpdateSiteList {

    private Map<String, UpdateSite> updateSites = new TreeMap<String, UpdateSite>(String.CASE_INSENSITIVE_ORDER);

    public Map<String, UpdateSite> getUpdateSites() {
        return updateSites;
    }

    public void setUpdateSites(Map<String, UpdateSite> updateSites) {
        this.updateSites = updateSites;
    }

}
