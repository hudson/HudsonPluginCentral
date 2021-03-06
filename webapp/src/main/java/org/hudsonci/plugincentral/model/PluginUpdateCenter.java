package org.hudsonci.plugincentral.model;

import java.util.Map;
import java.util.TreeMap;
import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonWriteNullProperties;

/**
 * Model representing Update Center 
 * @author Winston Prakash
 */
@JsonWriteNullProperties(false)
public class PluginUpdateCenter {

    private Core core;
    private String id = "default";
    private String connectionCheckUrl  = "http://www.google.com";
    private Map<String, Plugin> plugins = new TreeMap<String, Plugin>(String.CASE_INSENSITIVE_ORDER);
    private Signature signature = new Signature();

    public String getUpdateCenterVersion() {
        return updateCenterVersion;
    }

    public void setUpdateCenterVersion(String updateCenterVersion) {
        this.updateCenterVersion = updateCenterVersion;
    }
    private String updateCenterVersion;

    public Signature getSignature() {
        return signature;
    }

    public void setSignature(Signature signature) {
        this.signature = signature;
    }

    public String getConnectionCheckUrl() {
        return connectionCheckUrl;
    }

    public void setConnectionCheckUrl(String connectionCheckUrl) {
        this.connectionCheckUrl = connectionCheckUrl;
    }

    public Core getCore() {
        return core;
    }

    public void setCore(Core core) {
        this.core = core;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Map<String, Plugin> getPlugins() {
        return plugins;
    }

    public void setPlugins(Map<String, Plugin> plugins) {
        this.plugins = plugins;
    }

    @JsonIgnore
    public Plugin findPlugin(String name) {
        return plugins.get(name);
    }

    @JsonIgnore
    public void replacePlugin(Plugin oldPlugin, Plugin newPlugin) {    
        plugins.get(oldPlugin.getName()).set(newPlugin);
    }

    @JsonIgnore
    public void add(Plugin plugin) {
        plugins.put(plugin.getName(), plugin);
    }
    
    @JsonIgnore
    public boolean deletePlugin(String name) {
        Plugin plugin = plugins.get(name);
        Plugin removedPlugin = plugins.remove(name);
        return plugin == removedPlugin;
    }
}
