package org.hudsonci.plugincentral;

import java.io.*;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.hudsonci.plugincentral.l10n.L10nSubmission;
import org.hudsonci.plugincentral.model.HpiProcessor;
import org.hudsonci.plugincentral.model.Plugin;
import org.hudsonci.plugincentral.model.UpdateCenter;
import org.hudsonci.plugincentral.model.UpdateSite;
import org.hudsonci.plugincentral.security.PluginCentralSecurity;
import org.hudsonci.plugincentral.stats.UsageStats;
import org.kohsuke.stapler.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Hudson Plugin Central
 *
 * @author Winston Prakash
 */
public class PluginCentral {

    private final UpdateCenter updateCenter;
    private final File tempPluginsDir;
    private final HpiProcessor hpiProcessor;
    private final UpdateSite updateSite;
    private final String securityIniPath;
    
    private final L10nSubmission l10nSubmission;
    
    private final UsageStats usageStats;
    
    private final Logger logger = LoggerFactory.getLogger(PluginCentral.class);

    public PluginCentral() throws IOException {
        String updateSiteJsonPath = System.getProperty("update-site-json", "/Users/wjprakash/Hudson/PluginCentral-test/update-site.json");
        String updateSiteJson = FileUtils.readFileToString(new File(updateSiteJsonPath));

        updateSite = Utils.parseUpdateSite(updateSiteJson);


        String updateCenterJson = FileUtils.readFileToString(new File(updateSite.getUpdateCenterJsonLocalPath()));
        if (updateCenterJson.startsWith("updateCenter.post(")) {
            updateCenterJson = updateCenterJson.substring("updateCenter.post(".length());
        }

        if (updateCenterJson.endsWith(");")) {
            updateCenterJson = updateCenterJson.substring(0, updateCenterJson.lastIndexOf(");"));
        }
        updateCenter = Utils.parseUpdateCenter(updateCenterJson);
        tempPluginsDir = new File(updateSite.getPluginsTempDownloadLocalPathRoot());
        tempPluginsDir.mkdirs();
        hpiProcessor = new HpiProcessor(updateSite.getPluginsDownloadRootUrl(), updateSite.getPluginsDownloadLocalPathRoot());
        securityIniPath = "file:" + updateSite.getShiroSecurityIniLocalPath();
      
        l10nSubmission = new L10nSubmission(updateSite.getL10nStorePath());
        
        usageStats = new UsageStats(updateSite.getUsageStatsStorePath(), updateSite.getUsageStatsPrivateKey());
    }

    public Set<String> getPluginNames() {
        Set<String> sortedNames = new TreeSet(String.CASE_INSENSITIVE_ORDER);
        sortedNames.addAll(updateCenter.getPlugins().keySet());
        return sortedNames;
    }

    public Map<String, Plugin> getPlugins() {
        return updateCenter.getPlugins();
    }

    public PluginCentralSecurity getSecurity() {
        PluginCentralSecurity pluginCentralSecurity;
        HttpSession session = Stapler.getCurrentRequest().getSession(true);
        if (session != null) {
            pluginCentralSecurity = (PluginCentralSecurity) session.getAttribute("plugin-central-security");
            if (pluginCentralSecurity == null) {
                pluginCentralSecurity = new PluginCentralSecurity(securityIniPath);
                session.setAttribute("plugin-central-security", pluginCentralSecurity);
            }
        } else {
            //Session independent
            pluginCentralSecurity = new PluginCentralSecurity(securityIniPath);
        }
        return pluginCentralSecurity;
    }
    
    public L10nSubmission getL10nSubmission() {
        return l10nSubmission;
    }
    
    public UsageStats getUsageStats() {
        return usageStats;
    }

    public HttpResponse doLogin(@QueryParameter("j_username") String username, @QueryParameter("j_password") String password) {
        try {
            getSecurity().login(username, password);
            System.out.println("User " + username + " successfully logged in.");
            return HttpResponses.ok();
        } catch (Exception exc) {
            logger.error("Login failed for user " + username + ". " + exc.getLocalizedMessage());
            return new ErrorHttpResponse("Failed to login " + username + ". " + exc.getLocalizedMessage());
        }
    }

    public HttpResponse doLogout() throws IOException {
        try {
            getSecurity().logout();
            return HttpResponses.ok();
        } catch (Exception exc) {
            return HttpResponses.error(HttpServletResponse.SC_BAD_REQUEST, "Failed to logout. " + exc.getLocalizedMessage());
        }
    }

    public HttpResponse doDeletePlugin(@QueryParameter("name") String name) throws IOException {
        if (!getSecurity().isPermitted(PluginCentralSecurity.PLUGIN_DELETE)) {
            return new ErrorHttpResponse("Not authorized to delete plugin." + name);
        }
        if (updateCenter.deletePlugin(name)) {
            persistJson();
            return HttpResponses.ok();
        } else {
            return new ErrorHttpResponse("Failed to delete plugin " + name);
        }
    }

    public HttpResponse doUpdatePlugin(
            @QueryParameter("name") String name,
            @QueryParameter("title") String title,
            @QueryParameter("excerpt") String excerpt,
            @QueryParameter("url") String url,
            @QueryParameter("version") String version,
            @QueryParameter("type") String type,
            @QueryParameter("wiki") String wiki,
            @QueryParameter("previousVersion") String previousVersion,
            @QueryParameter("scm") String scm,
            @QueryParameter("requiredCore") String requiredCore,
            @QueryParameter("releaseTimestamp") String releaseTimestamp,
            @QueryParameter("previousTimestamp") String previousTimestamp,
            @QueryParameter("buildDate") String buildDate,
            @QueryParameter("developers") String developers,
            @QueryParameter("dependencies") String dependencies,
            @QueryParameter("labels") String labels) {
        if (!getSecurity().isPermitted(PluginCentralSecurity.PLUGIN_UPDATE)) {
            return new ErrorHttpResponse("Not authorized to update plugin." + name);
        }
        try {
            Plugin plugin = updateCenter.findPlugin(name);
            plugin.setName(name);
            plugin.setExcerpt(excerpt);
            plugin.setTitle(title);
            plugin.setUrl(url);
            plugin.setVersion(version);
            plugin.setType(type);
            plugin.setWiki(wiki);
            plugin.setPreviousVersion(previousVersion);
            plugin.setScm(scm);
            plugin.setRequiredCore(requiredCore);
            plugin.setReleaseTimestamp(releaseTimestamp);
            plugin.setPreviousTimestamp(previousTimestamp);
            plugin.setBuildDate(buildDate);
            plugin.setDependenciesAsString(dependencies);
            plugin.setDevelopersAsString(developers);
            plugin.setLabelsAsString(labels);
            persistJson();
        } catch (Exception ex) {
            return new ErrorHttpResponse(ex.getLocalizedMessage());
        }
        return HttpResponses.ok();
    }

    public HttpResponse doUploadPlugin(StaplerRequest request) throws IOException, ServletException {
        if (!getSecurity().isPermitted(PluginCentralSecurity.PLUGIN_UPLOAD)) {
            return new ErrorHttpResponse("Not authorized to upload plugin.");
        }
        try {
            List<FileItem> items = new ServletFileUpload(new DiskFileItemFactory()).parseRequest(request);
            for (FileItem fileItem : items) {
                if (fileItem.getFieldName().equals("file")) {
                    String fileName = FilenameUtils.getName(fileItem.getName());
                    if ("".equals(fileName) || !fileName.endsWith(".hpi")) {
                        return new ErrorHttpResponse("File " + fileName + " may not be a plugin");
                    }
                    File uploadedPluginFile = new File(tempPluginsDir, fileName);
                    fileItem.write(uploadedPluginFile);
                    Plugin newPlugin = hpiProcessor.process(uploadedPluginFile);
                    Plugin plugin = updateCenter.findPlugin(newPlugin.getName());
                    if (plugin != null) {
                        plugin.setExcerpt(newPlugin.getExcerpt());
                        plugin.setTitle(newPlugin.getTitle());
                        plugin.setUrl(newPlugin.getUrl());
                        plugin.setPreviousVersion(plugin.getVersion());
                        plugin.setVersion(newPlugin.getVersion());
                        plugin.setType(newPlugin.getType());
                        plugin.setWiki(newPlugin.getWiki());
                        plugin.setScm(newPlugin.getScm());
                        plugin.setRequiredCore(newPlugin.getRequiredCore());
                        plugin.setPreviousTimestamp(plugin.getReleaseTimestamp());
                        plugin.setReleaseTimestamp(newPlugin.getReleaseTimestamp());
                        plugin.setBuildDate(newPlugin.getBuildDate());
                        plugin.setDependenciesAsString(newPlugin.getDependenciesAsString());
                        plugin.setDevelopersAsString(newPlugin.getDevelopersAsString());
                        plugin.setLabelsAsString(newPlugin.getLabelsAsString());
                        newPlugin = plugin;
                    } else {
                        updateCenter.add(newPlugin);
                    }

                    fileItem.delete();
                    persistJson();
                    return HttpResponses.plainText(newPlugin.getName() + " uploaded.");
                }
            }

        } catch (Exception exc) {
            return new ErrorHttpResponse(exc.getLocalizedMessage());
        }
        return new ErrorHttpResponse("Failed to upload plugin");
    }

    private static class ErrorHttpResponse implements HttpResponse {

        private String message;

        ErrorHttpResponse(String message) {
            this.message = message;
        }

        @Override
        public void generateResponse(StaplerRequest sr, StaplerResponse rsp, Object o) throws IOException, ServletException {
            rsp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            rsp.setContentType("text/plain;charset=UTF-8");
            PrintWriter w = new PrintWriter(rsp.getWriter());
            w.println(message);
            w.close();
        }
    }

    public void persistJson() throws IOException {
        String newJson = Utils.getAsString(updateCenter);
        newJson = "updateCenter.post(" + newJson + ");";
        //System.out.println(newJson);
        File newUpdateCenter = new File(updateSite.getUpdateCenterJsonLocalPath() + "_new");
        BufferedWriter out = new BufferedWriter(new FileWriter(newUpdateCenter));
        out.write(newJson);
        out.close();
        File oldUpdateCenter = new File(updateSite.getUpdateCenterJsonLocalPath());
        if (!oldUpdateCenter.delete()) {
            throw new IOException("Failed to delete " + updateSite.getUpdateCenterJsonLocalPath());
        }

        if (!newUpdateCenter.renameTo(oldUpdateCenter)) {
            throw new IOException("Failed to rename " + newUpdateCenter.getName() + " to " + oldUpdateCenter.getName());
        }
    }
}
