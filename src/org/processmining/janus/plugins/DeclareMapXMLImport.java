package org.processmining.janus.plugins;

import minerful.io.encdec.declaremap.DeclareMapEncoderDecoder;
import org.apache.commons.io.FileUtils;
import org.processmining.contexts.uitopia.annotations.UIImportPlugin;
import org.processmining.framework.abstractplugins.AbstractImportPlugin;
import org.processmining.framework.plugin.PluginContext;
import org.processmining.framework.plugin.annotations.Plugin;
import org.processmining.plugins.declareminer.visualizing.DeclareMap;

import javax.swing.filechooser.FileNameExtensionFilter;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

@Plugin(name = "DeclareMap model (Import from Disk)", parameterLabels = { "Filename" }, returnLabels = {
		"Declare Process Model" }, returnTypes = { DeclareMap.class })
@UIImportPlugin(description = "declare model as XML", extensions = { "xml" })
public class DeclareMapXMLImport extends AbstractImportPlugin {
	//Maybe this plugin is redundant

	protected FileNameExtensionFilter getFileFilter() {
		return new FileNameExtensionFilter("XML files", "xml");
	}

	protected Object importFromStream(PluginContext context, InputStream input, String filename, long fileSizeInBytes)
			throws Exception {
//		TODO create a function to import directly from InputStream
		File temp= new File("temp");
		FileUtils.copyInputStreamToFile(input, temp);
		DeclareMap inputModel = new DeclareMapEncoderDecoder(temp.getAbsolutePath()).createDeclareMap();
		temp.delete();
		return inputModel;
	}
}