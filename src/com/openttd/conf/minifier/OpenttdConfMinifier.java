package com.openttd.conf.minifier;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.Map;

/**
 * @author eric_renard
 */
public class OpenttdConfMinifier {
    
    private String [] DATA_CATEGORIES = {
	"[server_bind_addresses]",
	"[servers]",
	"[bans]",
	"[newgrf]",
	"[newgrf-static]",
	"[ai_players]",
	"[game_scripts]"
    };

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws IOException {
	OpenttdConf custom = null;
	if(args.length > 0) {
	    custom = new OpenttdConf(new File(args[0]));
	}
	OpenttdConf base = null;
	if(args.length > 1) {
	    base = new OpenttdConf(new File(args[1]));
	} else {
	    base = new OpenttdConf(ClassLoader.getSystemResourceAsStream("openttd.1.3.0.cfg"));
	}
	if(custom != null) {
	    OpenttdConfMinifier minifier = new OpenttdConfMinifier(base, custom);
	} else {
	    System.err.println("Usage : java -jar openttd-conf-minifier.jar custom_conf [base_conf]");
	    System.err.println("custom_conf\tyour server configuration file");
	    System.err.println("base_conf\tOptional: base configuration file");
	    System.err.println("");
	    System.err.println("By default, the base_conf is openttd.cfg from openttd 1.3.0");
	    System.err.println("The output file will be prefixed with : 'mini_'");
	    System.err.println("");
	    System.err.println("Example:\tjava -jar openttd-conf-minifier.jar my_server.cfg");
	    System.err.println("will produce:\tmini_my_server.cfg");
	    System.exit(-1);
	}
    }

    private OpenttdConfMinifier(OpenttdConf base, OpenttdConf custom) throws IOException {
	String outputFilename = "mini_" + custom.getFileName();
	try (BufferedWriter writer = new BufferedWriter(new FileWriter(new File(outputFilename)))) {
	    Collection<String> customCategories = custom.getCategories();
	    boolean newCategory = false;
	    Collection<String> dataCategories = Arrays.asList(DATA_CATEGORIES);
	    for(String category : customCategories) {
		if(!category.equals(OpenttdConf.GLOBAL_CATEGORY)) {
		    newCategory = true;
		}
		boolean checkObsoleteLines = !dataCategories.contains(category);
		Map<String, String> customProperties = custom.getPropertiesByCategory(category);
		Map<String, String> baseProperties = base.getPropertiesByCategory(category);
		System.out.println("Processing " + category);
		for(Map.Entry<String, String> property : customProperties.entrySet()) {
		    String propertyName = property.getKey();
		    String propertyValue = property.getValue();
		    if(baseProperties == null
		    || !baseProperties.containsKey(propertyName)
		    || !baseProperties.get(propertyName).equals(propertyValue)) {
			if(newCategory) {
			    writer.newLine();
			    writer.write(category);
			    writer.newLine();
			    newCategory = false;
			}
			if(checkObsoleteLines && baseProperties != null && !baseProperties.containsKey(propertyName)) {
			    System.out.println("Obsolete line: " + propertyName + " = " + propertyValue);
			    writer.write("#");
			}
			writer.write(propertyName + " = " + propertyValue);
			writer.newLine();
		    }
		}
		writer.flush();
	    }
	    System.out.println("Done !");
	    System.out.println("Minified configuration writen to "+outputFilename);
	} catch (FileNotFoundException fnfe) {
	    System.err.println(fnfe.getLocalizedMessage());
	} catch (IOException ioe) {
	    System.err.println(ioe.getLocalizedMessage());
	    throw ioe;
	}
    }
}
