/*******************************************************************************
 *
 * Copyright (c) 2011 Oracle Corporation.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors: 
 *
 *    Winston Prakash
 *     
 *******************************************************************************/ 

package org.hudsonci.plugincentral.executablewar;

import java.io.IOException;
import java.net.URL;
import java.security.ProtectionDomain;
import java.util.Enumeration;
import java.util.jar.Manifest;
import org.mortbay.jetty.Server;
import org.mortbay.jetty.bio.SocketConnector;
import org.mortbay.jetty.webapp.WebAppContext;

/**
 * Simple class to make the war executable
 * @author Winston Prakash
 */
public class StartJetty {

    public static void main(String[] args) throws Exception {
        int httpPort = 8080;
        for (int i = 0; i < args.length; i++) {
            if (args[i].startsWith("--httpPort=")) {
                String portStr = args[i].substring("--httpPort=".length());
                httpPort = Integer.parseInt(portStr);
            }
            
            if (args[i].startsWith("--update-site=")) {
                String updateSiteJson = args[i].substring("--update-site=".length());
                System.setProperty("update-site-json", updateSiteJson); 
            }
            
            if (args[i].startsWith("--usage")) {
                printUsage();
                break;
            }
        }
         

        Server server = new Server(httpPort);
        SocketConnector connector = new SocketConnector();

        ProtectionDomain protectionDomain = StartJetty.class.getProtectionDomain();
        URL location = protectionDomain.getCodeSource().getLocation();

        WebAppContext context = new WebAppContext();

        context.setContextPath("/PluginCentral");
        context.setDescriptor(location.toExternalForm() + "/WEB-INF/web.xml");
        context.setServer(server);
        context.setWar(location.toExternalForm());

        server.addHandler(context);
        server.start();
        server.join();
    }
    
    private static void printUsage() throws IOException {
        String usageStr = "Hudson Plugin Central " + getHudsonVersion() + "\n"
                + "Usage: java -jar plugin-central.war [--option=value] [--option=value] ... \n"
                + "\n"
                + "Options:\n"
                + "   --version                        Show Hudson version and quit\n"
                + "   --httpPort=<value>               HTTP listening port. Default value is 8080\n\n"
                + "   --update-site=<filepath>         JSON file defining Updata Site parameters\n"
                ;
        
        System.out.println(usageStr);
        System.exit(0);
    }
    
    private static String getHudsonVersion() throws IOException {
        Enumeration manifests = StartJetty.class.getClassLoader().getResources("META-INF/MANIFEST.MF");
        while (manifests.hasMoreElements()) {
            URL manifestUrl = (URL) manifests.nextElement();
            Manifest manifest = new Manifest(manifestUrl.openStream());
            String hudsonVersion = manifest.getMainAttributes().getValue("Plugin-Central-Version");
            if (hudsonVersion != null) {
                return hudsonVersion;
            }
        }
        return "1.0";
    }
}
