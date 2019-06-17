package org.processmining.janus.plugins;

import minerful.concept.ProcessModel;
import minerful.io.encdec.declaremap.DeclareMapEncoderDecoder;
import org.deckfour.xes.model.XLog;
import org.processmining.contexts.uitopia.UIPluginContext;
import org.processmining.contexts.uitopia.annotations.UITopiaVariant;
import org.processmining.framework.plugin.annotations.Plugin;
import org.processmining.framework.plugin.annotations.PluginVariant;
import org.processmining.janus.algorithms.JanusModelCheckerAlgorithm;
import org.processmining.janus.help.JanusModelCheckerHelp;
import org.processmining.plugins.declareminer.visualizing.DeclareMap;

@Plugin(name = "Janus Declare Model Checker", parameterLabels = { "Event Log", "Declare Model", "Compliance Results" },
		returnLabels = { "Compliance Results" }, returnTypes = { Object.class }, help = JanusModelCheckerHelp.TEXT)

public class JanusModelChecker extends JanusModelCheckerAlgorithm {

	/**
	 * The plug-in variant using UI where the paramenter I/O respect the ProM data flow
	 *
	 * @param context The context to run in.
	 * @return The output.
	 */
	@UITopiaVariant(affiliation = "Vienna University of Economics and Business", author = "Alessio Cecconi", email = "alessio.cecconi@wu.ac.at")
	@PluginVariant(variantLabel = "Janus Model Checker with dialog", requiredParameterLabels = { 0, 1 })
	public Object parametricFlowUI(UIPluginContext context, XLog inputLog, DeclareMap model) {
		// undetermined progress bar
		context.getProgress().setIndeterminate(true);
		// Create default inputs.
		//		JanusModelCheckerParameters params = new JanusModelCheckerParameters();

		// Get a dialog for this parameters.
		//		JanusModelCheckerDialog dialog = new JanusModelCheckerDialog(context, params);
		// Show the dialog. User can now change the parameters.
		//		TaskListener.InteractionResult result = context.showWizard("Input Parameters", true, true, dialog);
		// User has close the dialog.
		//		if (result == TaskListener.InteractionResult.FINISHED) {
		DeclareMapEncoderDecoder decoder = new DeclareMapEncoderDecoder(model.getModel());
		return runConnections(context, inputLog, decoder.createMinerFulProcessModel());
		//		}
		// Dialog got canceled.
		//		return null;
	}

	private Object runConnections(UIPluginContext context, XLog inputLog, ProcessModel model) {
		return insiderJarParametric(context, inputLog, model);
	}
}
