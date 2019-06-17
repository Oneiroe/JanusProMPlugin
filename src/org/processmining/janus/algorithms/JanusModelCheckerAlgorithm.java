package org.processmining.janus.algorithms;

import minerful.JanusModelCheckLauncher;
import minerful.JanusModelCheckStarter;
import minerful.checking.params.CheckingCmdParameters;
import minerful.concept.ProcessModel;
import minerful.io.params.InputModelParameters;
import minerful.io.params.OutputModelParameters;
import minerful.logparser.LogEventClassifier;
import minerful.logparser.LogParser;
import minerful.logparser.XesLogParser;
import minerful.params.InputLogCmdParameters;
import minerful.params.SystemCmdParameters;
import minerful.params.ViewCmdParameters;
import minerful.postprocessing.params.PostProcessingCmdParameters;
import minerful.reactive.checking.MegaMatrixMonster;
import org.apache.commons.cli.Options;
import org.deckfour.xes.model.XLog;
import org.processmining.framework.plugin.PluginContext;

import static minerful.MinerFulMinerLauncher.fromInputParamToXesLogClassificationType;

public class JanusModelCheckerAlgorithm {
	/**
	 * Launch Janus model checker importing its Jar and using its functions
	 *
	 * @return
	 */
	public MegaMatrixMonster insiderJarParametric(PluginContext context, XLog inputLog, ProcessModel model) {
		JanusModelCheckStarter checkStarter = new JanusModelCheckStarter();

		String[] args = new String[] {
				//				"java",
				//				"-jar",
				//				"/home/alessio/Data/Phd/my_code/Janus ProM Plugin/ProM/src-Plugins/Janus/lib/MINERful.jar",
				//				"minerful.MinerFulMinerStarter",
				//				"-iLF", params.getInputLog().toString(),
//				"-s", params.getSupport().toString(),
//				" -c ", params.getConfidence().toString(),
				//				"-CSV", params.getOutputFilePath().toString() + ".csv",
				//				"-condec", params.getOutputFilePath().toString() + ".xml"
		};

		Options cmdLineOptions = checkStarter.setupOptions();

		SystemCmdParameters systemParams =
				new SystemCmdParameters(
						cmdLineOptions,
						args);
		OutputModelParameters outParams =
				new OutputModelParameters(
						cmdLineOptions,
						args);
		PostProcessingCmdParameters preProcParams =
				new PostProcessingCmdParameters(
						cmdLineOptions,
						args);
		CheckingCmdParameters chkParams =
				new CheckingCmdParameters(
						cmdLineOptions,
						args);
		InputLogCmdParameters inputLogParams =
				new InputLogCmdParameters(
						cmdLineOptions,
						args);
		InputModelParameters inpuModlParams =
				new InputModelParameters(
						cmdLineOptions,
						args);
		ViewCmdParameters viewParams =
				new ViewCmdParameters(
						cmdLineOptions,
						args);

		LogEventClassifier.ClassificationType classiType = fromInputParamToXesLogClassificationType(inputLogParams.eventClassification);
		LogParser logParser = new XesLogParser(inputLog, classiType);

		JanusModelCheckLauncher miFuCheLa = new JanusModelCheckLauncher(model, logParser, chkParams);
		MegaMatrixMonster evaluation = miFuCheLa.checkModel();

		return evaluation;
	}
}
