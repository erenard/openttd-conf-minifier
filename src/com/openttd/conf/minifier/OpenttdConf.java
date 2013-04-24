/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.openttd.conf.minifier;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

class OpenttdConf {

    private final String fileName;
    private final Map<String, SortedMap<String, String>> propertiesByCategory = new TreeMap<>();
    private final Collection<String> categoriesInOrder = new ArrayList<>();
    
    static final String GLOBAL_CATEGORY = "[global]";

    OpenttdConf(File openttdcfg) throws IOException {
	fileName = openttdcfg.getName();
	FileInputStream fis = new FileInputStream(openttdcfg);
	read(fis, true);
    }

    OpenttdConf(InputStream systemResourceAsStream) throws IOException {
	fileName = "openttd.cfg";
	read(systemResourceAsStream, false);
    }
    
    private void read(InputStream systemResourceAsStream, boolean withGlobal) throws IOException {
	try (BufferedReader reader = new BufferedReader(new InputStreamReader(systemResourceAsStream))) {
	    String line;
	    String category = GLOBAL_CATEGORY;
	    if(withGlobal) {
		categoriesInOrder.add(category);
		propertiesByCategory.put(category, new TreeMap<String, String>());
	    }
	    while ((line = reader.readLine()) != null) {
		line = line.trim();
		if(line.startsWith("[") && line.endsWith("]")) {
		    category = line;
		    categoriesInOrder.add(category);
		    propertiesByCategory.put(category, new TreeMap<String, String>());
		    continue;
		}
		int indexOfEquals = line.indexOf("=");
		if(indexOfEquals > 0) {
		    String propertyName = line.substring(0, indexOfEquals).trim();
		    String propertyValue = line.substring(indexOfEquals + 1).trim();
		    Map<String, String> properties = propertiesByCategory.get(category);
		    properties.put(propertyName, propertyValue);
		}
	    }
	} catch (FileNotFoundException fnfe) {
	    System.err.println(fnfe.getLocalizedMessage());
	} catch (IOException ioe) {
	    System.err.println(ioe.getLocalizedMessage());
	    throw ioe;
	}
    }

    public Map<String, String> getPropertiesByCategory(String category) {
        return propertiesByCategory.get(category);
    }

    public Collection<String> getCategories() {
	return categoriesInOrder;
    }

    public String getFileName() {
	return fileName;
    }
}
