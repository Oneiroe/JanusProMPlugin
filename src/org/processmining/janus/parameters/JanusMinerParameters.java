package org.processmining.janus.parameters;

import org.deckfour.xes.model.XLog;
import org.deckfour.xes.model.impl.XLogImpl;
import org.processmining.basicutils.parameters.impl.PluginParametersImpl;

import java.nio.file.Path;

public class JanusMinerParameters extends PluginParametersImpl {

	private Double support;
	private Double confidence;

	/**
	 * Constructor without parameters: Variable are only initialized.
	 */
	public JanusMinerParameters() {
		super();
		setSupport(0.0);
		setConfidence(0.0);
	}

	/**
	 * Constructor with direct parameters assignation
	 *
	 * @param support
	 * @param confidence
	 */
	public JanusMinerParameters(Double support, Double confidence) {
		super();
		setSupport(support);
		setConfidence(confidence);
	}

	/**
	 * Constructor using another Parameters object
	 *
	 * @param parameters
	 */
	public JanusMinerParameters(JanusMinerParameters parameters) {
		super(parameters);
		setSupport(parameters.getSupport());
		setConfidence(parameters.getConfidence());
	}

	public Double getSupport() {
		return support;
	}

	public void setSupport(Double support) {
		this.support = support;
	}

	public Double getConfidence() {
		return confidence;
	}

	public void setConfidence(Double confidence) {
		this.confidence = confidence;
	}

	public boolean equals(Object object) {
		if (object instanceof JanusMinerParameters) {
			JanusMinerParameters parameters = (JanusMinerParameters) object;
			return super.equals(parameters) &&
					getSupport() == parameters.getSupport() &&
					getConfidence() == parameters.getConfidence();
		}
		return false;
	}

	public String toString() {
		return "(" + "," + getSupport() + "," + getConfidence() + "," + ")";
	}

}
