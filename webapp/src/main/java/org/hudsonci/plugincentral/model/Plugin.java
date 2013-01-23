package org.hudsonci.plugincentral.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.codehaus.jackson.annotate.JsonAnySetter;
import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.map.annotate.JsonSerialize;

/**
 * Model representing Plugin info
 *
 * @author Winston Prakash
 */
@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
public class Plugin {

    protected static final String DEFAULT_CORE_VERSION = "1.395";
    private List<String> labels = new ArrayList<String>();
    private String excerpt;
    private List<Dependency> dependencies = new ArrayList<Dependency>();
    private List<Developer> developers = new ArrayList<Developer>();
    private String buildDate;
    private String name;
    private String groupId;
    private String previousTimestamp;
    private String releaseTimestamp;
    private String requiredCore;
    private String scm;
    private String title;
    private String url;
    private String version;
    private String wiki;
    private String previousVersion;
    private String type = "others";

    public void set(Plugin newPlugin) {
        if (CollectionUtils.isNotEmpty(newPlugin.getLabels())) {
            labels = newPlugin.getLabels();
        }
        if (StringUtils.isNotBlank(newPlugin.getExcerpt())) {
            excerpt = newPlugin.getExcerpt();
        }
        if (StringUtils.isNotBlank(newPlugin.getGroupId())) {
            groupId = newPlugin.getGroupId();
        }
        if (CollectionUtils.isNotEmpty(newPlugin.getDependencies())) {
            dependencies = newPlugin.getDependencies();
        }
        if (CollectionUtils.isNotEmpty(newPlugin.getDevelopers())) {
            developers = newPlugin.getDevelopers();
        }
        if (StringUtils.isNotBlank(newPlugin.getBuildDate())) {
            buildDate = newPlugin.getBuildDate();
        }
        if (StringUtils.isNotBlank(newPlugin.getPreviousTimestamp())) {
            previousTimestamp = newPlugin.getPreviousTimestamp();
        }
        if (StringUtils.isNotBlank(newPlugin.getReleaseTimestamp())) {
            releaseTimestamp = newPlugin.getReleaseTimestamp();
        }
        if (StringUtils.isNotBlank(newPlugin.getRequiredCore())) {
            requiredCore = newPlugin.getRequiredCore();
        } else {
            if (requiredCore == null) {
                requiredCore = DEFAULT_CORE_VERSION;
            }
        }
        if (StringUtils.isNotBlank(newPlugin.getScm())) {
            scm = newPlugin.getScm();
        }
        if (StringUtils.isNotBlank(newPlugin.getTitle())) {
            title = newPlugin.getTitle();
        }
        if (StringUtils.isNotBlank(newPlugin.getUrl())) {
            url = newPlugin.getUrl();
        }
        if (StringUtils.isNotBlank(newPlugin.getVersion())) {
            version = newPlugin.getVersion();
        }
        if (StringUtils.isNotBlank(newPlugin.getWiki())) {
            wiki = newPlugin.getWiki();
        }
        if (StringUtils.isNotBlank(newPlugin.getPreviousVersion())) {
            previousVersion = newPlugin.getPreviousVersion();
        }
    }

    @JsonAnySetter
    void addEntry(String key, Object value) {
    }

    public List<Dependency> getDependencies() {
        return dependencies;
    }

    @JsonIgnore
    public String getDependenciesAsString() {
        StringBuilder deps = new StringBuilder();

        for (Dependency dep : dependencies) {
            String depName = dep.getName();
            if ((depName == null) || "".equals(depName.trim())){
                continue;
            }
            deps.append("name:");
            deps.append(dep.getName());
            deps.append(";");
            deps.append("version:");
            deps.append(dep.getVersion());
            deps.append(";");
            deps.append("optional:");
            deps.append(dep.isOptional());
            deps.append(",");
        }
        return fixAndGetString(deps);
    }

    @JsonIgnore
    public void setDependenciesAsString(String dependenciesStr) {
        String[] dependencyArray = dependenciesStr.split(",");
        dependencies.clear();
        for (String dep : dependencyArray) {
            Dependency dependency = new Dependency();
            String[] depProps = dep.split(";");
            for (String prop : depProps) {
                String[] keyValue = prop.split(":");
                if (keyValue.length > 1) {
                    if (keyValue[0].trim().equalsIgnoreCase("name")) {
                        dependency.setName(keyValue[1].trim());
                    } else if (keyValue[0].trim().equalsIgnoreCase("version")) {
                        dependency.setVersion(keyValue[1].trim());
                    } else if (keyValue[0].trim().equalsIgnoreCase("optional")) {
                        dependency.setOptional(Boolean.parseBoolean(keyValue[1].trim()));
                    }
                }
            }
            dependencies.add(dependency);
        }
    }

    @JsonIgnore
    public String getDevelopersAsString() {
        StringBuilder devs = new StringBuilder();

        for (Developer dev : developers) {
            String devName = dev.getName();
            if ((devName != null) && !"".equals(devName.trim())){
                devs.append("name:");
                devs.append(devName);
                devs.append(";");
            }
            String devId = dev.getDeveloperId();
            if ((devId != null) && !"".equals(devId.trim())) {
                devs.append("id:");
                devs.append(devId);
                devs.append(";");
            }
            String devEmail = dev.getEmail();
            if ((devEmail != null) && !"".equals(devEmail.trim())) {
                devs.append("email:");
                devs.append(devEmail);
            }
            devs.append(",");
        }
        return fixAndGetString(devs);
    }

    public void setDevelopersAsString(String developersStr) {
        String[] developerArray = developersStr.split(",");
        developers.clear();
        for (String dev : developerArray) {
            Developer developer = new Developer();
            String[] devProps = dev.split(";");
            for (String prop : devProps) {
                String[] keyValue = prop.split(":");
                if (keyValue.length > 1) {
                    if (keyValue[0].trim().equalsIgnoreCase("name")) {
                        developer.setName(keyValue[1].trim());
                    } else if (keyValue[0].trim().equalsIgnoreCase("id")) {
                        developer.setDeveloperId(keyValue[1].trim());
                    } else if (keyValue[0].trim().equalsIgnoreCase("email")) {
                        developer.setEmail(keyValue[1].trim());
                    }
                }
            }
            developers.add(developer);
        }
    }

    @JsonIgnore
    public String getLabelsAsString() {
        StringBuilder lbls = new StringBuilder();

        for (String label : labels) {
            lbls.append(label);

            lbls.append(",");
        }
        return fixAndGetString(lbls);
    }

    // Remove the last ","
    private String fixAndGetString(StringBuilder strBuilder) {
        String str = strBuilder.toString();
        if ((str.length() > 0) && str.charAt(str.length() - 1) == ',') {
            str = str.substring(0, str.length() - 1);
        }
        return str;
    }

    public void setLabelsAsString(String labelsStr) {
        String[] labelArray = labelsStr.split(",");
        labels = Arrays.asList(labelArray);
    }

    public void setDependencies(List<Dependency> dependencies) {
        this.dependencies = dependencies;
    }

    public String getBuildDate() {
        return buildDate;
    }

    public void setBuildDate(String buildDate) {
        this.buildDate = buildDate;
    }

    public List<Developer> getDevelopers() {
        return developers;
    }

    public void setDevelopers(List<Developer> developers) {
        this.developers = developers;
    }

    public String getExcerpt() {
        return excerpt;
    }

    public void setExcerpt(String excerpt) {
        this.excerpt = excerpt;
    }

    public List<String> getLabels() {
        return labels;
    }

    public void setLabels(List<String> labels) {
        this.labels = labels;
    }

    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }

    public String getGroupId() {
        return groupId;
    }
    
    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public String getPreviousTimestamp() {
        return previousTimestamp;
    }

    public void setPreviousTimestamp(String previousTimestamp) {
        this.previousTimestamp = previousTimestamp;
    }

    public String getReleaseTimestamp() {
        return releaseTimestamp;
    }

    public void setReleaseTimestamp(String releaseTimestamp) {
        this.releaseTimestamp = releaseTimestamp;
    }

    public String getRequiredCore() {
        return requiredCore;
    }

    public void setRequiredCore(String requiredCore) {
        this.requiredCore = requiredCore;
    }

    public String getPreviousVersion() {
        return previousVersion;
    }

    public void setPreviousVersion(String previousVersion) {
        this.previousVersion = previousVersion;
    }

    public String getScm() {
        return scm;
    }

    public void setScm(String scm) {
        this.scm = scm;
    }

    public String getTitle() {
        if ("".equals(title)) {
            title = name;
        }
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
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

    public String getWiki() {
        return wiki;
    }

    public void setWiki(String wiki) {
        this.wiki = wiki;
    }

    @JsonIgnore
    public boolean isNewerThan(Plugin otherPlugin) {
        try {
            return new VersionNumber(getVersion()).compareTo(new VersionNumber(otherPlugin.getVersion())) > 0;
        } catch (IllegalArgumentException e) {
            // couldn't parse as the version number.
            return false;
        }
    }
    
    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
