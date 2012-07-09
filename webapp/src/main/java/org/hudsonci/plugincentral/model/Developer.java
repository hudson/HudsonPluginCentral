package org.hudsonci.plugincentral.model;

import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonWriteNullProperties;

/**
 * Model representing Developer of a Plugin
 *
 * @author Winston Prakash
 */
@JsonWriteNullProperties(false)
public class Developer {

    private String developerId;
    private String email;
    private String name;

    public Developer(String name, String developerId, String email) {
        this.name = name;
        this.developerId = developerId;
        this.email = email;
    }

    public Developer() {
        
    }

    public String getDeveloperId() {
        return developerId;
    }

    public void setDeveloperId(String developerId) {
        this.developerId = developerId;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
    
    @JsonIgnore
    @Override
    public String toString(){
        return "[name: " + name + "id: " + developerId + "email: " + email + "]";
    }
}
