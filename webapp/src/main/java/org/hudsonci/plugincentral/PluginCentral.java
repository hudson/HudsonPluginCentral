package org.hudsonci.plugincentral;

import java.io.*;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.codehaus.jackson.map.ObjectMapper;
import org.hudsonci.plugincentral.model.HpiProcessor;
import org.hudsonci.plugincentral.model.Plugin;
import org.hudsonci.plugincentral.model.UpdateSite;
import org.hudsonci.plugincentral.model.UpdateCenter;
import org.hudsonci.plugincentral.security.PluginCentralSecurity;
import org.kohsuke.stapler.*;

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

    public PluginCentral() throws IOException {
        String updateSiteJsonPath = System.getProperty("update-site-json", "/var/tmp/hudson/update-site.json");
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
        securityIniPath = "file:" + updateSite.getShiroSecurityIniLocalPath();;
    }

    public Set<String> getPluginNames() {
        return updateCenter.getPlugins().keySet();
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

    public HttpResponse doLogin(@QueryParameter("j_username") String username, @QueryParameter("j_password") String password) {
        try {
            getSecurity().login(username, password);
            return HttpResponses.ok();
        } catch (Exception exc) {
            return HttpResponses.error(HttpServletResponse.SC_BAD_REQUEST, "Failed to login. " + exc.getLocalizedMessage());
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
            return HttpResponses.error(HttpServletResponse.SC_UNAUTHORIZED, "Not authorized to delete plugin." + name);
        }
        if (updateCenter.deletePlugin(name)) {
            persistJson();
            return HttpResponses.ok();
        } else {
            return HttpResponses.error(HttpServletResponse.SC_BAD_REQUEST, "Failed to delete plugin " + name);
        }
    }

    public HttpResponse doUpdatePlugin(
            @QueryParameter("name") String name,
            @QueryParameter("title") String title,
            @QueryParameter("excerpt") String excerpt,
            @QueryParameter("url") String url,
            @QueryParameter("version") String version,
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
            return HttpResponses.error(HttpServletResponse.SC_UNAUTHORIZED, "Not authorized to update plugin." + name);
        }
        try {
            Plugin plugin = updateCenter.findPlugin(name);
            plugin.setName(name);
            plugin.setExcerpt(excerpt);
            plugin.setTitle(title);
            plugin.setUrl(url);
            plugin.setVersion(version);
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
            return HttpResponses.error(HttpServletResponse.SC_BAD_REQUEST, ex);
        }
        return HttpResponses.ok();
    }

    public HttpResponse doUploadPlugin(StaplerRequest request) throws IOException, ServletException {
        if (!getSecurity().isPermitted(PluginCentralSecurity.PLUGIN_UPLOAD)) {
            return HttpResponses.error(HttpServletResponse.SC_UNAUTHORIZED, "Not authorized to upload plugin.");
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
                    updateCenter.add(newPlugin);
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
