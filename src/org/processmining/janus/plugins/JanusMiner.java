package org.processmining.janus.plugins;

import minerful.io.encdec.declaremap.DeclareMapEncoderDecoder;
import org.deckfour.uitopia.api.event.TaskListener.InteractionResult;
import org.processmining.contexts.uitopia.UIPluginContext;
import org.processmining.contexts.uitopia.annotations.UITopiaVariant;
import org.processmining.framework.plugin.PluginContext;
import org.processmining.framework.plugin.annotations.Plugin;
import org.processmining.framework.plugin.annotations.PluginVariant;
import org.processmining.janus.algorithms.JanusMinerAlgorithm;
import org.processmining.janus.dialogs.JanusMinerDialog;
import org.processmining.janus.help.JanusMinerHelp;
import org.processmining.janus.parameters.*;

import org.deckfour.xes.model.XLog;
import org.processmining.plugins.declareminer.visualizing.DeclareMap;

import java.nio.file.Path;

@Plugin(name = "Janus Declare Miner", parameterLabels = { "Event Log", "Support Threshold",
		"Confidence Threshold",
		"Discovered Declare Model" },
		returnLabels = { "Mined DeclareMaps Model" }, returnTypes = { DeclareMap.class }, help = JanusMinerHelp.TEXT)
public class JanusMiner extends JanusMinerAlgorithm {

	/**
	 * The plug-in variant using UI where the paramenter I/O respect the ProM data flow
	 *
	 * @param context The context to run in.
	 * @return The output.
	 */
	@UITopiaVariant(affiliation = "Vienna University of Economics and Business", author = "Alessio Cecconi", email = "alessio.cecconi@wu.ac.at")
	@PluginVariant(variantLabel = "Your plug-in name, Janus dialog", requiredParameterLabels = { 0 })
	public DeclareMap parametricFlowUI(UIPluginContext context, XLog inputLog) {
		context.getProgress().setIndeterminate(true);
		// Create default inputs.
		JanusMinerParameters params = new JanusMinerParameters();

		// Get a dialog for this parameters.
		//		YourDialog dialog = new YourDialog(context, inputLog, support, confidence, outputFilePath);
		JanusMinerDialog dialog = new JanusMinerDialog(context, params);
		// Show the dialog. User can now change the parameters.
		InteractionResult result = context.showWizard("Input Parameters", true, true, dialog);
		// User has close the dialog.
		if (result == InteractionResult.FINISHED) {
			// Apply the algorithm depending on whether a connection already exists.
			//			return runConnections(context, inputLog, support, confidence, outputFilePath);
			//			return runConnections(context, inputLog, support, confidence, outputFilePath);
			return runConnectionsParametric(context, inputLog, params);
		}
		// Dialog got canceled.
		return null;
	}

	/**
	 * Apply the algorithm by executing the jar through terminal commands
	 *
	 * @param context        The context to run in.
	 * @param inputLog       path to input log
	 * @param support        support threshold
	 * @param confidence     confidence threshold
	 * @param outputFilePath output file path.
	 * @return The output.
	 */
	private DeclareMap runConnections(PluginContext context, Path inputLog, Double support, Double confidence,
			Path outputFilePath) {
		// launch Janus jar
		System.out.println(inputLog);
		applyJar(context, inputLog, support, confidence, outputFilePath);
		// load the output file as Declare model
		DeclareMapEncoderDecoder decoder = new DeclareMapEncoderDecoder(outputFilePath.toAbsolutePath().toString());
		return decoder.createDeclareMap();
	}

	/**
	 * Apply the algorithm through Janus/Minerful API
	 *
	 * @param context  The context to run in.
	 * @param inputLog XLog input object.
	 * @param params   Algorithm parameters from user
	 * @return
	 */
	private DeclareMap runConnectionsParametric(PluginContext context, XLog inputLog, JanusMinerParameters params) {
		DeclareMap model = insiderJarParametric(context, inputLog, params);
		return model;
	}

}
