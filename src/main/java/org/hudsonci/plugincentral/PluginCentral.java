package org.hudsonci.plugincentral;

import java.io.*;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.io.FilenameUtils;
import org.hudsonci.plugincentral.model.HpiProcessor;
import org.hudsonci.plugincentral.model.Plugin;
import org.hudsonci.plugincentral.model.UpdateSiteMetadata;
import org.hudsonci.plugincentral.security.PluginCentralSecurity;
import org.kohsuke.stapler.*;

/**
 * Hudson Plugin Central
 *
 * @author Winston Prakash
 */
public class PluginCentral {

    public static final String HUDSON_UPDATE_SITE = "/Users/winstonp/Downloads";
    public static final String HUDSON_UPDATE_SITE_JSON = HUDSON_UPDATE_SITE  + "/update-center.json";
    public static final String DOWNLOAD_SITE_URL = "http://hudson-ci.org/downloads/plugins";
    public static final String PLUGIN_DOWNLOAD_PATH = "/Users/winstonp/Downloads/hudson-plugins";
    public static final String TEMP_PLUGIN_DIR = "/Users/winstonp/Downloads/tmp-plugins";
    private UpdateSiteMetadata hudsonUpdateSiteMetadata;
    private File tempPluginsDir;
    private HpiProcessor hpiProcessor = new HpiProcessor(DOWNLOAD_SITE_URL, PLUGIN_DOWNLOAD_PATH);
    
    private PluginCentralSecurity security = PluginCentralSecurity.getInstance();

    public PluginCentral() throws IOException {
        StringBuilder StringBuilder = new StringBuilder();
        BufferedReader in = new BufferedReader(new FileReader(HUDSON_UPDATE_SITE_JSON));
        String str;
        while ((str = in.readLine()) != null) {
            StringBuilder.append(str);
            StringBuilder.append("\n");
        }
        in.close();
        String json = StringBuilder.toString();

        if (json.startsWith("updateCenter.post(")) {
            json = json.substring("updateCenter.post(".length());
        }

        if (json.endsWith(");")) {
            json = json.substring(0, json.lastIndexOf(");"));
        }
        hudsonUpdateSiteMetadata = Utils.parse(json);
        tempPluginsDir = new File(TEMP_PLUGIN_DIR);
        tempPluginsDir.mkdirs();
    }

    public Set<String> getPluginNames() {
        return hudsonUpdateSiteMetadata.getPlugins().keySet();
    }

    public Map<String, Plugin> getPlugins() {
        return hudsonUpdateSiteMetadata.getPlugins();
    }
    
    public PluginCentralSecurity getSecurity() {
        return security;
    }
    
    public HttpResponse doLogin(@QueryParameter("j_username") String username, @QueryParameter("j_password") String password) { 
        try {
            security.login(username, password);
            return HttpResponses.ok();
        } catch (Exception exc) {
            return HttpResponses.error(HttpServletResponse.SC_BAD_REQUEST, "Failed to login. " + exc.getLocalizedMessage());
        }
    } 
    
    public HttpResponse doLogout() throws IOException { 
        try {
            security.logout();
            return HttpResponses.ok();
        } catch (Exception exc) {
            return HttpResponses.error(HttpServletResponse.SC_BAD_REQUEST, "Failed to logout. " + exc.getLocalizedMessage());
        }
    } 

    public HttpResponse doDeletePlugin(@QueryParameter("name") String name) throws IOException {
        if (!PluginCentralSecurity.getInstance().isPermitted(PluginCentralSecurity.PLUGIN_DELETE)){
           return HttpResponses.error(HttpServletResponse.SC_UNAUTHORIZED, "Not authorized to delete plugin." + name);
        }
        if (hudsonUpdateSiteMetadata.deletePlugin(name)) {
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
        if (!PluginCentralSecurity.getInstance().isPermitted(PluginCentralSecurity.PLUGIN_UPDATE)){
           return HttpResponses.error(HttpServletResponse.SC_UNAUTHORIZED, "Not authorized to update plugin." + name);
        }
        try {
            Plugin plugin = hudsonUpdateSiteMetadata.findPlugin(name);
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
        if (!PluginCentralSecurity.getInstance().isPermitted(PluginCentralSecurity.PLUGIN_UPLOAD)){
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
                    hudsonUpdateSiteMetadata.add(newPlugin);
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
        String newJson = Utils.getAsString(hudsonUpdateSiteMetadata);
        newJson = "updateCenter.post(" + newJson + ");";
        //System.out.println(newJson);
        File newUpdateCenter = new File(HUDSON_UPDATE_SITE + "update-center_new.json");
        BufferedWriter out = new BufferedWriter(new FileWriter(newUpdateCenter));
        out.write(newJson);
        out.close();
        File oldUpdateCenter = new File(HUDSON_UPDATE_SITE_JSON);
        if (!oldUpdateCenter.delete()) {
            throw new IOException("Failed to delete " + HUDSON_UPDATE_SITE_JSON);
        }

        if (!newUpdateCenter.renameTo(oldUpdateCenter)) {
            throw new IOException("Failed to rename " + newUpdateCenter.getName() + " to " + oldUpdateCenter.getName());
        }
    }
}
