package org.processmining.janus.algorithms;

import minerful.JanusMinerStarter;
import minerful.JanusOfflineMinerStarter;
import minerful.MinerFulOutputManagementLauncher;
import minerful.concept.ProcessModel;
import minerful.concept.TaskCharArchive;
import minerful.io.encdec.declaremap.DeclareMapEncoderDecoder;
import minerful.io.params.OutputModelParameters;
import minerful.logparser.LogEventClassifier;
import minerful.logparser.LogParser;
import minerful.logparser.XesLogParser;
import minerful.miner.params.MinerFulCmdParameters;
import minerful.params.InputLogCmdParameters;
import minerful.params.SystemCmdParameters;
import minerful.params.ViewCmdParameters;
import minerful.postprocessing.params.PostProcessingCmdParameters;
import minerful.utils.MessagePrinter;
import org.apache.commons.cli.Options;
import org.deckfour.xes.model.XLog;
import org.processmining.framework.plugin.PluginContext;
import org.processmining.janus.parameters.JanusMinerParameters;
import org.processmining.janus.parameters.JanusMinerSelfishParameters;
import org.processmining.plugins.declareminer.importing.DeclareModelImportPlugin;
import org.processmining.plugins.declareminer.visualizing.DeclareMap;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;

import minerful.MinerFulMinerLauncher;

import static minerful.MinerFulMinerLauncher.fromInputParamToXesLogClassificationType;

public class JanusMinerAlgorithm {

	/**
	 * The method that implements your algorithm.
	 * <p>
	 * Note that this method only uses the boolean which is stored in the parameters.
	 * Nevertheless, it could have used the integer and/or the String as well.
	 *
	 * @param context    The context where to run in.
	 * @param input1     The first input.
	 * @param input2     The second input.
	 * @param parameters The parameters to use.
	 * @return The output.
	 */
	//	public YourOutput apply(PluginContext context, YourFirstInput input1, YourSecondInput input2,
	//			YourParameters parameters) {
	//		/**
	//		 * Put your algorithm here, which computes an output form the inputs provided the parameters.
	//		 */
	//		long time = -System.currentTimeMillis();
	//		parameters.displayMessage("[YourAlgorithm] Start");
	//		parameters.displayMessage("[YourAlgorithm] First input = " + input1.toString());
	//		parameters.displayMessage("[YourAlgorithm] Second input = " + input2.toString());
	//		parameters.displayMessage("[YourAlgorithm] Parameters = " + parameters.toString());
	//		YourOutput output = parameters.isYourBoolean() ? new YourOutput(input1) : new YourOutput(input2);
	//		time += System.currentTimeMillis();
	//		parameters.displayMessage("[YourAlgorithm] Output = " + output.toString());
	//		parameters.displayMessage("[YourAlgorithm] End (took " + time / 1000.0 + "  seconds).");
	//		return output;
	//	}

	/**
	 * Launch Janus miner through its Jar
	 *
	 * @return
	 */
	public void applyJar(PluginContext context, Path inputLog, Double support, Double confidence,
			Path outputFilePath) {

		System.out.println("java -jar MINERful.jar minerful.MinerFulMinerStarter -iLF " + inputLog.toString()
				+ " -s " + support.toString()
				+ " -c " + confidence.toString()
				+ " -CSV " + outputFilePath.toString() + ".csv"
				+ " -condec" + outputFilePath.toString() + ".xml");
		// Run a java app in a separate system process
		Process proc = null;
		try {
			proc = Runtime.getRuntime().exec(
					"java -jar MINERful.jar minerful.MinerFulMinerStarter -iLF " + inputLog.toString()
							+ " -s " + support.toString()
							+ " -c " + confidence.toString()
							+ " -CSV " + outputFilePath.toString() + ".csv"
							+ " -condec " + outputFilePath.toString() + ".xml");
		} catch (IOException e) {
			System.out.println("Exception in jar running");
			e.printStackTrace();
		}
		try {
			proc.waitFor();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		// Then retrieve the process output
		InputStream in = proc.getInputStream();
		InputStream err = proc.getErrorStream();
	}

	/**
	 * Launch Janus miner through its Jar
	 *
	 * @return
	 */
	public DeclareMap applyJarParametric(PluginContext context, JanusMinerSelfishParameters params) {

		System.out
				.println(
						"java -jar /home/alessio/Data/Phd/my_code/Janus ProM Plugin/ProM/src-Plugins/Janus/lib/MINERful.jar minerful.MinerFulMinerStarter -iLF "
								+ params.getInputLog().toString()
								+ " -s " + params.getSupport().toString()
								+ " -c " + params.getConfidence().toString()
								+ " -CSV " + params.getOutputFilePath().toString() + ".csv"
								+ " -condec" + params.getOutputFilePath().toString() + ".xml");

		// Run a java app in a separate system process
		Process proc = null;
		System.out.println();
		try {
			proc = Runtime.getRuntime().exec(new String[] {
					"java",
					"-jar",
					"/home/alessio/Data/Phd/my_code/Janus ProM Plugin/ProM/src-Plugins/Janus/lib/MINERful.jar",
					"minerful.MinerFulMinerStarter",
					"-iLF", params.getInputLog().toString(),
					"-s", params.getSupport().toString(),
					" -c ", params.getConfidence().toString(),
					"-CSV", params.getOutputFilePath().toString() + ".csv",
					"-condec", params.getOutputFilePath().toString() + ".xml"
			});

			//wait process ending
			proc.waitFor();

			// Then retrieve the process output (or error)
			InputStream is = proc.getInputStream();
			byte b[] = new byte[is.available()];
			is.read(b, 0, b.length);
			System.out.println(new String(b));

			InputStream err = proc.getErrorStream();
			byte be[] = new byte[err.available()];
			err.read(be, 0, be.length);
			System.out.println(new String(be));

		} catch (IOException e) {
			System.out.println("Exception in jar running");
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		// load output as DeclareMap
		//		params.getOutputFilePath().toString() + ".xml"
		DeclareMap res = null;
		try {
			res = (DeclareMap) new DeclareModelImportPlugin()
					.importFile(context, params.getOutputFilePath().toString() + ".xml");
		} catch (Exception e) {
			e.printStackTrace();
		}
		return res;
	}

	/**
	 * Launch Janus miner importing its Jar and using its functions
	 *
	 * @return
	 */
	public DeclareMap insiderJarParametric(PluginContext context, XLog inputLog, JanusMinerParameters params) {
		String[] args = new String[] {
				//				"java",
				//				"-jar",
				//				"/home/alessio/Data/Phd/my_code/Janus ProM Plugin/ProM/src-Plugins/Janus/lib/MINERful.jar",
				//				"minerful.MinerFulMinerStarter",
				//				"-iLF", params.getInputLog().toString(),
				"-s", params.getSupport().toString(),
				" -c ", params.getConfidence().toString(),
				//				"-CSV", params.getOutputFilePath().toString() + ".csv",
				//				"-condec", params.getOutputFilePath().toString() + ".xml"
		};

		// START MINERful MAIN
//		JanusMinerStarter minerMinaStarter = new JanusMinerStarter();
		JanusOfflineMinerStarter minerMinaStarter = new JanusOfflineMinerStarter(); // faster

		Options cmdLineOptions = minerMinaStarter.setupOptions();

		InputLogCmdParameters inputParams =
				new InputLogCmdParameters(
						cmdLineOptions,
						args);
		MinerFulCmdParameters minerFulParams =
				new MinerFulCmdParameters(
						cmdLineOptions,
						args);
		ViewCmdParameters viewParams =
				new ViewCmdParameters(
						cmdLineOptions,
						args);
		OutputModelParameters outParams =
				new OutputModelParameters(
						cmdLineOptions,
						args);
		SystemCmdParameters systemParams =
				new SystemCmdParameters(
						cmdLineOptions,
						args);
		PostProcessingCmdParameters postParams =
				new PostProcessingCmdParameters(
						cmdLineOptions,
						args);

		if (systemParams.help) {
			systemParams.printHelp(cmdLineOptions);
			System.exit(0);
		}

		LogEventClassifier.ClassificationType classiType = fromInputParamToXesLogClassificationType(inputParams.eventClassification);
		LogParser logParser = new XesLogParser(inputLog, classiType);

		ProcessModel minerfulProcessModel = minerMinaStarter.mine(logParser, inputParams, minerFulParams, postParams, logParser.getTaskCharArchive());

		DeclareMapEncoderDecoder encoder = new DeclareMapEncoderDecoder(minerfulProcessModel);

		return encoder.createDeclareMap();
	}

}
