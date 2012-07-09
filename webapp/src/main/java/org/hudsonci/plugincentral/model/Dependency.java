package org.hudsonci.plugincentral.model;

import org.codehaus.jackson.annotate.JsonIgnore;

/**
 * Model representing Plugin Dependency
 *
 * @author Winston Prakash
 */
public class Dependency {

    private String name;
    private boolean optional = true;
    private String version;

    public Dependency(String name, boolean optional, String version) {
        this.name = name;
        this.optional = optional;
        this.version = version;
    }

    public Dependency() {
        
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isOptional() {
        return optional;
    }

    public void setOptional(boolean optional) {
        this.optional = optional;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }
    
    @JsonIgnore
    @Override
    public String toString(){
        return "[name: " + name + "version: " + version + "optionl: " + optional + "]";
    }
}
