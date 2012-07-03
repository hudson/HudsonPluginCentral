
package org.hudsonci.plugincentral.model;

import java.util.ArrayList;
import java.util.List;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.map.annotate.JsonSerialize;

/**
 * Model representing Update Center signature 
 * @author Winston Prakash
 */
@JsonIgnoreProperties(ignoreUnknown=true)
public class Signature {
    
    private List<String> certificates = new ArrayList<String>();
    private String digest;
    private String signature;

    public List<String> getCertificates() {
        return certificates;
    }

    public void setCertificates(List<String> certificates) {
        this.certificates = certificates;
    }

    public String getDigest() {
        return digest;
    }

    public void setDigest(String digest) {
        this.digest = digest;
    }

    public String getSignature() {
        return signature;
    }

    public void setSignature(String signature) {
        this.signature = signature;
    }
}
