package org.processmining.janus.plugins;

import minerful.reactive.checking.MegaMatrixMonster;
import minerful.reactive.io.JanusOutputManagementLauncher;
import org.processmining.contexts.uitopia.annotations.UIExportPlugin;
import org.processmining.framework.plugin.PluginContext;
import org.processmining.framework.plugin.annotations.Plugin;
import org.processmining.framework.plugin.annotations.PluginVariant;

import java.io.File;
import java.io.IOException;

@Plugin(name = "Mega Matrix Monster (Export to Disk)", returnLabels = {}, returnTypes = {}, parameterLabels = {
		"Mega matrix Monster", "Output File" }, userAccessible = true)
@UIExportPlugin(description = "Mega Matrix Monster as CSV", extension = "csv")
public class MegaMatrixMonsterCSVExport {

	@PluginVariant(variantLabel = "CSV export", requiredParameterLabels = { 0, 1 })
	public void exportToCSV(PluginContext context, MegaMatrixMonster matrix, File outputFile) throws IOException {
		JanusOutputManagementLauncher outManager = new JanusOutputManagementLauncher();

		//			Detailed traces results
		outManager.exportReadable3DMatrixToCSV(matrix, outputFile);
	}

}
