package org.processmining.janus.plugins;

import minerful.io.ConstraintsPrinter;
import minerful.io.encdec.declaremap.DeclareMapEncoderDecoder;
import org.processmining.contexts.uitopia.annotations.UIExportPlugin;
import org.processmining.framework.plugin.PluginContext;
import org.processmining.framework.plugin.annotations.Plugin;
import org.processmining.framework.plugin.annotations.PluginVariant;
import org.processmining.plugins.declareminer.visualizing.*;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;

@Plugin(name = "DeclareMap model (Export to Disk)", returnLabels = {}, returnTypes = {}, parameterLabels = {
		"Process model", "Output File" }, userAccessible = true)
@UIExportPlugin(description = "declare model as CSV", extension = "csv")
public class DeclareMapCSVExport {

	@PluginVariant(variantLabel = "CSV export", requiredParameterLabels = { 0, 1 })
	public void exportToCSV(PluginContext context, DeclareMap model, File file) throws IOException {
		DeclareMapEncoderDecoder deMapEnDec = new DeclareMapEncoderDecoder(model.getModel());

		ConstraintsPrinter printer = new ConstraintsPrinter(deMapEnDec.createMinerFulProcessModel());
		PrintWriter outWriter = null;
		try {
			outWriter = new PrintWriter(file);
			outWriter.print(printer.printBagCsv());
			outWriter.flush();
			outWriter.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}