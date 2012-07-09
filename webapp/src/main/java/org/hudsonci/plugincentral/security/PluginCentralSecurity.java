package org.hudsonci.plugincentral.security;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.config.IniSecurityManagerFactory;
import org.apache.shiro.mgt.SecurityManager;
import org.apache.shiro.session.Session;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.util.Factory;

/**
 * A Simple Security for Hudson Plugin Central (Singleton)  based on Apache Shiro.
 *
 * @author Winston Prakash
 */
public class PluginCentralSecurity {
            
    private final Subject currentUser;
    
    private String username;
    
    // Roles
    public static final String SYSADMIN = "sysadmin";
    public static final String ADMIN = "admin";
    public static final String DEVELOPER = "developer";
    
    // Permissions
    public static final String PLUGIN_UPDATE = "plugin:update";
    public static final String PLUGIN_DELETE = "plugin:delete";
    public static final String PLUGIN_UPLOAD = "plugin:upload";
    
    public PluginCentralSecurity(String iniResourcePath) {
        Factory<SecurityManager> factory = new IniSecurityManagerFactory(iniResourcePath);
        SecurityManager securityManager = factory.getInstance();
        SecurityUtils.setSecurityManager(securityManager);
        currentUser = SecurityUtils.getSubject();
    }
    
    public Session getSession(){
        return currentUser.getSession();
    }

    public void login(String username, String password) throws AuthenticationException{
        this.username = username;
        UsernamePasswordToken token = new UsernamePasswordToken(username, password);
        token.setRememberMe(true);
        currentUser.login(token);
    }
    
    public String getUsername(){
        return username;
    }

    public boolean isAuthenticated() {
        return currentUser.isAuthenticated();
    }

    public void logout() {
        currentUser.logout();
    }
    
    public boolean hasRole(String roleName){
        return currentUser.hasRole(roleName);
    }
    
    public boolean isPermitted(String permission){
        return currentUser.isPermitted(permission);
    }
}
