package org.hudsonci.plugincentral.security;

import static org.junit.Assert.assertEquals;
import org.junit.Test;

/**
 * Test class to test Plugin Central Security
 *
 * @author Winston Prakash <winston.prakash@oracle.com>
 */
public class PluginCentralSecurityTest {

    /**
     * Test of login method, of class PluginCentralSecurity.
     */
    @Test
    public void testLogin() {
        System.out.println("login");
        String userName = "sysadmin";
        String password = "admin007";
        PluginCentralSecurity instance = new PluginCentralSecurity();
        instance.login(userName, password);
    }

    /**
     * Test of isAuthenticated method, of class PluginCentralSecurity.
     */
    @Test
    public void testIsAuthenticated() {
        System.out.println("isAuthenticated");
        String userName = "sysadmin";
        String password = "admin007";
        PluginCentralSecurity instance = new PluginCentralSecurity();
        instance.login(userName, password);
        boolean expResult = true;
        boolean result = instance.isAuthenticated();
        assertEquals(expResult, result);
    }

    /**
     * Test of logout method, of class PluginCentralSecurity.
     */
    @Test
    public void testLogout() {
        System.out.println("logout");
        String userName = "sysadmin";
        String password = "admin007";
        PluginCentralSecurity instance = new PluginCentralSecurity();
        instance.login(userName, password);
        instance.logout();
    }

    /**
     * Test of hasRole method, of class PluginCentralSecurity.
     */
    @Test
    public void testHasRole() {
        System.out.println("hasRole");
        String roleName = "";
        String userName = "sysadmin";
        String password = "admin007";
        PluginCentralSecurity instance = new PluginCentralSecurity();
        instance.login(userName, password);
        boolean expResult = true;
        boolean result = instance.hasRole(PluginCentralSecurity.SYSADMIN);
        assertEquals(expResult, result);

    }

    /**
     * Test of isPermitted method, of class PluginCentralSecurity.
     */
    @Test
    public void testIsPermitted() {
        System.out.println("isPermitted");
        String userName = "hudson";
        String password = "hudson123";
        String permission = PluginCentralSecurity.PLUGIN_UPDATE;
        PluginCentralSecurity instance = new PluginCentralSecurity();
        instance.login(userName, password);
        boolean expResult = true;
        boolean result = instance.isPermitted(permission);
        assertEquals(expResult, result);
    }
}
