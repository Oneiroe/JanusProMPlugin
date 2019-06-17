package org.processmining.janus.plugins;

import minerful.io.encdec.ProcessModelEncoderDecoder;
import minerful.io.encdec.declaremap.DeclareMapEncoderDecoder;
import org.processmining.contexts.uitopia.annotations.UIExportPlugin;
import org.processmining.framework.plugin.PluginContext;
import org.processmining.framework.plugin.annotations.Plugin;
import org.processmining.framework.plugin.annotations.PluginVariant;
import org.processmining.plugins.declareminer.visualizing.DeclareMap;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

@Plugin(name = "Janus Miner (Export to Disk)", returnLabels = {}, returnTypes = {}, parameterLabels = {
		"Process model", "Output File" }, userAccessible = true)
@UIExportPlugin(description = "declare model as Json", extension = "json")
public class JanusMinerJsonExport {

	@PluginVariant(variantLabel = "Json export", requiredParameterLabels = { 0, 1 })
	public void exportToJson(PluginContext context, DeclareMap model, File file) throws IOException {
		DeclareMapEncoderDecoder deMapEnDec = new DeclareMapEncoderDecoder(model.getModel());

		try {
			new ProcessModelEncoderDecoder().writeToJsonFile(deMapEnDec.createMinerFulProcessModel(), file);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}