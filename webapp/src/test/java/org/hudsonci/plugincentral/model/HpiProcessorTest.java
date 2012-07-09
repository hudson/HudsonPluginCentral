/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.hudsonci.plugincentral.model;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.text.ParseException;
import org.junit.Test;

/**
 * Test the HPI parser
 * @author Winston Prakash
 */
public class HpiProcessorTest {
    
   
    public static final String DOWNLOAD_SITE_URL = "http://hudson-ci.org/downloads/plugins";
    public static final String PLUGIN_DOWNLOAD_PATH = "/Users/winstonp/Downloads/hudson-plugins";

    /**
     * Test of process method, of class HpiProcessor.
     */
    @Test
    public void testProcess() {
        try {
            URL hpiFileUrl = HpiProcessorTest.class.getResource("/analysis-core.hpi"); 
            String FileName = hpiFileUrl.getPath().replaceFirst("file:", "");
            File hpiFile = new File (FileName);
            HpiProcessor hpiProcessor = new HpiProcessor(DOWNLOAD_SITE_URL, PLUGIN_DOWNLOAD_PATH);
            Plugin plugin = hpiProcessor.process(hpiFile);
            System.out.println("Name: " + plugin.getName());
            System.out.println("Title: " + plugin.getTitle());
            System.out.println("Excerpt: " + plugin.getExcerpt());
            System.out.println("Version: " + plugin.getVersion());
            System.out.println("RequiredCore: " + plugin.getRequiredCore());
            System.out.println("BuildDate: " + plugin.getBuildDate());
            System.out.println("Labels: " + plugin.getLabelsAsString());
            System.out.println("Developers: " + plugin.getDevelopersAsString());
            System.out.println("Dependencies: " + plugin.getDependenciesAsString());
            System.out.println("Scm: " + plugin.getScm());
            System.out.println("Url: " + plugin.getUrl());
            System.out.println("Wiki: " + plugin.getWiki());
            System.out.println("ReleaseTimeStamp: " + plugin.getReleaseTimestamp());
        } catch (IOException ex) {
            ex.printStackTrace();
        } catch (ParseException ex) {
            ex.printStackTrace();
        }
    }

     
}
