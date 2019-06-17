package org.processmining.janus.plugins;

import minerful.io.ConstraintsPrinter;
import minerful.io.encdec.declaremap.DeclareMapEncoderDecoder;
import org.processmining.contexts.uitopia.annotations.UIExportPlugin;
import org.processmining.framework.plugin.PluginContext;
import org.processmining.framework.plugin.annotations.Plugin;
import org.processmining.framework.plugin.annotations.PluginVariant;
import org.processmining.plugins.declareminer.visualizing.DeclareMap;

import java.io.File;
import java.io.IOException;

@Plugin(name = "Janus Miner (Export to Disk)", returnLabels = {}, returnTypes = {}, parameterLabels = {
		"Process model", "Output File" }, userAccessible = true)
@UIExportPlugin(description = "declare model as XML", extension = "xml")
public class JanusMinerXMLExport {

	@PluginVariant(variantLabel = "XML export", requiredParameterLabels = { 0, 1 })
	public void exportToXML(PluginContext context, DeclareMap model, File file) throws IOException {
		DeclareMapEncoderDecoder deMapEnDec = new DeclareMapEncoderDecoder(model.getModel());

		ConstraintsPrinter printer = new ConstraintsPrinter(deMapEnDec.createMinerFulProcessModel());
		try {
			printer.saveAsConDecModel(file);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}