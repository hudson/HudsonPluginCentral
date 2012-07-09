
package org.hudsonci.plugincentral;

import java.io.IOException;
import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

/**
 * Web application lifecycle listener.
 *
 * @author winstonp
 */
public class CustomServletListener implements ServletContextListener {

    private static final String APP = "app";

    @Override
    public void contextInitialized(ServletContextEvent event) {
        final ServletContext servletContext = event.getServletContext();
        
        try {
            PluginCentral pluginCentral = new PluginCentral();
            servletContext.setAttribute(APP, pluginCentral);
        } catch (IOException ex) {
            ex.printStackTrace();
        } 
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
    }
}
