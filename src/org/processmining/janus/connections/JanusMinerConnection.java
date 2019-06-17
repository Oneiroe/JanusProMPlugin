package org.processmining.janus.connections;

import org.deckfour.xes.model.XLog;
import org.processmining.framework.connections.impl.AbstractConnection;
import org.processmining.janus.models.YourOutput;
import org.processmining.janus.parameters.*;

public class JanusMinerConnection extends AbstractConnection {

	/**
	 * Label for first input.
	 */
	public final static String FIRSTINPUT = "Input Log";

	/**
	 * Label for output.
	 */
	public final static String OUTPUT = "Declarative Model";

	/**
	 * Private copy of parameters.
	 */
	private JanusMinerParameters parameters;

	/**
	 * Create a connection.
	 * @param input1 First input.
	 * @param output Output.
	 * @param parameters Parameters.
	 */
	public JanusMinerConnection(XLog input1, YourOutput output, JanusMinerParameters parameters) {
		super("Your Connection");
		put(FIRSTINPUT, input1);
		put(OUTPUT, output);
		this.parameters = new JanusMinerParameters(parameters);
	}

	/**
	 * 
	 * @return The parameters stored in the connection.
	 */
	public JanusMinerParameters getParameters() {
		return parameters;
	}
}
