package org.hudsonci.plugincentral.model;

import org.codehaus.jackson.annotate.JsonProperty;

/**
 * Update Site information
 *
 * @author Winston Prakash <winston.prakash@oracle.com>
 */
public class UpdateSite {

    private String name;

    private String updateSiteRoot;
    private String updateSiteLocalPath;
    private String updateCenterJsonLocalPath;
    private String pluginsTempDownloadLocalPathRoot;
    private String pluginsDownloadLocalPathRoot;
    private String pluginsDownloadRootUrl;
    private String shiroSecurityIniLocalPath;
    private String l10nStorePath;
    private String usageStatsStorePath;
    private String usageStatsPrivateKey;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @JsonProperty("update-site-root")
    public void setUpdateSiteRoot(String updateSiteRoot) {
        this.updateSiteRoot = updateSiteRoot;
    }

    public String getUpdateSiteRoot() {
        if (!updateSiteRoot.endsWith("/")) {
            updateSiteRoot = updateSiteRoot + "/";
        }
        return updateSiteRoot;
    }

    public String getShiroSecurityIniLocalPath() {
        return getUpdateSiteRoot() + shiroSecurityIniLocalPath;
    }

    @JsonProperty("shiro-security-ini-local-path")
    public void setShiroSecurityIniLocalPath(String shiroSecurityIniLocalPath) {
        this.shiroSecurityIniLocalPath = shiroSecurityIniLocalPath;
    }

    public String getPluginsDownloadLocalPathRoot() {
        return getUpdateSiteRoot() + pluginsDownloadLocalPathRoot;
    }

    @JsonProperty("plugins-download-local-path-root")
    public void setPluginsDownloadLocalPathRoot(String pluginsDownloadLocalPathRoot) {
        this.pluginsDownloadLocalPathRoot = pluginsDownloadLocalPathRoot;
    }

    public String getPluginsDownloadRootUrl() {
        return pluginsDownloadRootUrl;
    }

    @JsonProperty("plugins-download-root-url")
    public void setPluginsDownloadRootUrl(String pluginsDownloadRootUrl) {
        this.pluginsDownloadRootUrl = pluginsDownloadRootUrl;
    }

    public String getPluginsTempDownloadLocalPathRoot() {
        return getUpdateSiteRoot() + pluginsTempDownloadLocalPathRoot;
    }

    @JsonProperty("plugins-temp-download-local-path-root")
    public void setPluginsTempDownloadLocalPathRoot(String pluginsTempDownloadLocalPathRoot) {
        this.pluginsTempDownloadLocalPathRoot = pluginsTempDownloadLocalPathRoot;
    }

    public String getUpdateCenterJsonLocalPath() {
        return getUpdateSiteRoot() + updateCenterJsonLocalPath;
    }

    @JsonProperty("update-center-json-local-path")
    public void setUpdateCenterJsonLocalPath(String updateCenterJsonLocalPath) {
        this.updateCenterJsonLocalPath = updateCenterJsonLocalPath;
    }

    public String getUpdateSiteLocalPath() {
        return getUpdateSiteRoot() + updateSiteLocalPath;
    }

    @JsonProperty("update-site-local-path")
    public void setUpdateSiteLocalPath(String updateSiteLocalPath) {
        this.updateSiteLocalPath = updateSiteLocalPath;
    }

    public String getL10nStorePath() {
        return getUpdateSiteRoot() + l10nStorePath;
    }

    @JsonProperty("l10n-store-path")
    public void setL10nStorePath(String l10nStorePath) {
        this.l10nStorePath = l10nStorePath;
    }

    public String getUsageStatsStorePath() {
        return getUpdateSiteRoot() + usageStatsStorePath;
    }

    @JsonProperty("usage-stats-store-path")
    public void setUsageStatsStorePath(String usageStatsStorePath) {
        this.usageStatsStorePath = usageStatsStorePath;
    }

    public String getUsageStatsPrivateKey() {
        return usageStatsPrivateKey;
    }

    @JsonProperty("usage-stats-private-key")
    public void setUsageStatsPrivateKey(String usageStatsPrivateKey) {
        this.usageStatsPrivateKey = usageStatsPrivateKey;
    }
}
