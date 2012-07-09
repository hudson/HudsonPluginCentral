
package org.hudsonci.plugincentral.model;

import org.codehaus.jackson.annotate.JsonProperty;

/**
 * Update Site information
 * @author Winston Prakash <winston.prakash@oracle.com>
 */
public class UpdateSite {
    private String updateSiteLocalPath;
    private String updateCenterJsonLocalPath;
    private String pluginsTempDownloadLocalPathRoot;
    private String pluginsDownloadLocalPathRoot;
    private String pluginsDownloadRootUrl;
    private String shiroSecurityIniLocalPath;

    @JsonProperty("shiro-security-ini-local-path")
    public String getShiroSecurityIniLocalPath() {
        return shiroSecurityIniLocalPath;
    }

    @JsonProperty("shiro-security-ini-local-path")
    public void setShiroSecurityIniLocalPath(String shiroSecurityIniLocalPath) {
        this.shiroSecurityIniLocalPath = shiroSecurityIniLocalPath;
    }
   
    @JsonProperty("plugins-download-local-path-root")
    public String getPluginsDownloadLocalPathRoot() {
        return pluginsDownloadLocalPathRoot;
    }

    @JsonProperty("plugins-download-local-path-root")
    public void setPluginsDownloadLocalPathRoot(String pluginsDownloadLocalPathRoot) {
        this.pluginsDownloadLocalPathRoot = pluginsDownloadLocalPathRoot;
    }

    @JsonProperty("plugins-download-root-url")
    public String getPluginsDownloadRootUrl() {
        return pluginsDownloadRootUrl;
    }

    @JsonProperty("plugins-download-root-url")
    public void setPluginsDownloadRootUrl(String pluginsDownloadRootUrl) {
        this.pluginsDownloadRootUrl = pluginsDownloadRootUrl;
    }

    @JsonProperty("plugins-temp-download-local-path-root")
    public String getPluginsTempDownloadLocalPathRoot() {
        return pluginsTempDownloadLocalPathRoot;
    }

    @JsonProperty("plugins-temp-download-local-path-root")
    public void setPluginsTempDownloadLocalPathRoot(String pluginsTempDownloadLocalPathRoot) {
        this.pluginsTempDownloadLocalPathRoot = pluginsTempDownloadLocalPathRoot;
    }

    @JsonProperty("update-center-json-local-path")
    public String getUpdateCenterJsonLocalPath() {
        return updateCenterJsonLocalPath;
    }

    @JsonProperty("update-center-json-local-path")
    public void setUpdateCenterJsonLocalPath(String updateCenterJsonLocalPath) {
        this.updateCenterJsonLocalPath = updateCenterJsonLocalPath;
    }

    @JsonProperty("update-site-local-path")
    public String getUpdateSiteLocalPath() {
        return updateSiteLocalPath;
    }

    @JsonProperty("update-site-local-path")
    public void setUpdateSiteLocalPath(String updateSiteLocalPath) {
        this.updateSiteLocalPath = updateSiteLocalPath;
    }
}
