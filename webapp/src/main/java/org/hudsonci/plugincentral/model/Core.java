package org.hudsonci.plugincentral.model;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;

/**
 * Model representing core info
 * @author Winston Prakash
 */
@JsonIgnoreProperties(ignoreUnknown=true)
public class Core {

    private String buildDate ;
    private String name;
    private String url;
    private String version;

    public String getBuildDate() {
        return buildDate;
    }

    public void setBuildDate(String buildDate) {
        this.buildDate = buildDate;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }
}