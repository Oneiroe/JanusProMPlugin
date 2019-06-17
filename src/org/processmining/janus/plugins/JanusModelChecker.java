package org.processmining.janus.plugins;

import org.processmining.framework.plugin.annotations.Plugin;
import org.processmining.janus.help.JanusMinerHelp;
import org.processmining.janus.help.JanusModelCheckerHelp;

@Plugin(name = "Janus Declare Model Checker", parameterLabels = { "Event Log", "Declare Model", "Compliance Results" },
		returnLabels = { "Compliance Results" }, returnTypes = { Object.class }, help = JanusModelCheckerHelp.TEXT)

public class JanusModelChecker {
}
