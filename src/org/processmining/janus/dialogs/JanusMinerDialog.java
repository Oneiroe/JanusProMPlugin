package org.processmining.janus.dialogs;

import com.fluxicon.slickerbox.components.NiceDoubleSlider;
import com.fluxicon.slickerbox.components.SlickerButton;
import info.clearthought.layout.TableLayout;
import info.clearthought.layout.TableLayoutConstants;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.Set;

import javax.swing.*;

import org.processmining.contexts.uitopia.UIPluginContext;
import org.processmining.framework.util.ui.widgets.ProMTextArea;
import org.processmining.framework.util.ui.widgets.ProMTextField;
import org.processmining.janus.parameters.JanusMinerParameters;

import com.fluxicon.slickerbox.components.NiceSlider.Orientation;
import com.fluxicon.slickerbox.factory.SlickerFactory;

public class JanusMinerDialog extends JPanel implements ActionListener {

	/**
	 *
	 */
	private static final long serialVersionUID = -60087716353524468L;
	private SlickerButton openButton;
	private SlickerButton openButtonOutput;
	private JFileChooser fc;
	private JFileChooser fcOutput;
	private ProMTextArea inputFileSelected;
	private ProMTextField outputFileSelected;
	private Double supportTresh;
	private Double confidenceTresh;

	private JanusMinerParameters paramsObject;

	/**
	 * The JPanel that allows the user to set (a subset of) the parameters.
	 */
	//	public YourDialog(UIPluginContext context, YourFirstInput input1, YourSecondInput input2, final YourParameters parameters) {
	public JanusMinerDialog(UIPluginContext context, Path inputLog, Double support, Double confidence, Path outputFilePath) {
		double size[][] = { { TableLayoutConstants.FILL }, { 30, 40, 30, 30, 30, 40 } };
		setLayout(new TableLayout(size));

		//		File chooser
		fc = new JFileChooser();
		fc.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
		//		JButton openButton = new JButton("Open input log file...");
		openButton = new SlickerButton("Open input log file...");
		openButton.addActionListener(this);
		//		int returnVal = fc.showOpenDialog(this);
		add(openButton, "0, 0");

		inputFileSelected = new ProMTextArea(false);
		inputFileSelected.setText("Input Path");
		add(inputFileSelected, "0, 1");

		//		Option selection
		Set<String> values = new HashSet<String>();/*
		values.add("Input Log Path");
		values.add("Support Threshold");
		values.add("Confidence Threshold");
		values.add("Output Model Path");

		DefaultListModel<String> listModel = new DefaultListModel<String>();
		for (String value : values) {
			listModel.addElement(value);
		}
		final ProMList<String> list = new ProMList<String>("Select option", listModel);
		list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		final String defaultValue = "Option 1";
		list.setSelection(defaultValue);
		list.addListSelectionListener(new ListSelectionListener() {
			public void valueChanged(ListSelectionEvent e) {
				List<String> selected = list.getSelectedValuesList();
				if (selected.size() == 1) {
					//					parameters.setYourString(selected.get(0));
				} else {
					*//*
		 * Nothing selected. Revert to selection of default classifier.
		 *//*
					list.setSelection(defaultValue);
					//					parameters.setYourString(defaultValue);
				}
			}
		});
		list.setPreferredSize(new Dimension(100, 100));
		add(list, "0, 1");*/

		//		Slider
		final NiceDoubleSlider doubleSilder = SlickerFactory.instance().createNiceDoubleSlider("Select number ", 0,
				1, support, Orientation.HORIZONTAL);
		doubleSilder.addChangeListener(e -> supportTresh = doubleSilder.getValue());
		add(doubleSilder, "0, 2");

		//		Slider
		final NiceDoubleSlider doubleSilderConfidence = SlickerFactory.instance()
				.createNiceDoubleSlider("Select number ", 0,
						1, support, Orientation.HORIZONTAL);
		doubleSilderConfidence.addChangeListener(e -> confidenceTresh = doubleSilderConfidence.getValue());
		add(doubleSilderConfidence, "0, 3");

		//		File chooser
		fcOutput = new JFileChooser();
		fcOutput.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
		openButtonOutput = new SlickerButton("Choose output log file...");
		openButtonOutput.addActionListener(this);
		//		int returnValOutput = fcOutput.showOpenDialog(this);
		add(openButtonOutput, "0, 4");

		outputFileSelected = new ProMTextField("Output");
		outputFileSelected.setText("output");
		add(outputFileSelected, "0, 5");

		//		Checkbox
		//		final JCheckBox checkBox = SlickerFactory.instance().createCheckBox("Select Yes/No", false);
		//		checkBox.setSelected(parameters.isYourBoolean());
		//		checkBox.addActionListener(new ActionListener() {
		//
		//			public void actionPerformed(ActionEvent e) {
		//				parameters.setYourBoolean(checkBox.isSelected());
		//			}
		//
		//		});
		//		checkBox.setOpaque(false);
		//		checkBox.setPreferredSize(new Dimension(100, 30));
		//		add(checkBox, "0, 3");
	}

	public JanusMinerDialog(UIPluginContext context, JanusMinerParameters params) {
		double size[][] = { { TableLayoutConstants.FILL }, {  30, 30 } };
		setLayout(new TableLayout(size));

		paramsObject = params;

		//		Slider Support
		final NiceDoubleSlider doubleSilder = SlickerFactory.instance().createNiceDoubleSlider("Support ", 0,
				1, 0.2, Orientation.HORIZONTAL);
		doubleSilder.addChangeListener(e -> paramsObject.setSupport(doubleSilder.getValue()));
		add(doubleSilder, "0, 0");

		//		Slider Confidence
		final NiceDoubleSlider doubleSilderConfidence = SlickerFactory.instance()
				.createNiceDoubleSlider("Confidence ", 0,
						1, 0.8, Orientation.HORIZONTAL);
		doubleSilderConfidence.addChangeListener(e -> paramsObject.setConfidence(doubleSilderConfidence.getValue()));
		add(doubleSilderConfidence, "0, 1");

	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == openButton) {
			int returnVal = fc.showOpenDialog(JanusMinerDialog.this);

			if (returnVal == JFileChooser.APPROVE_OPTION) {
				File file = fc.getSelectedFile();
				inputFileSelected.setText(file.toString());
				//This is where a real application would open the file.
				//				log.append("Opening: " + file.getName() + "." + newline);
			} else {
				//				log.append("Open command cancelled by user." + newline);
			}
			//			log.setCaretPosition(log.getDocument().getLength());
		}

		if (e.getSource() == openButtonOutput) {
			int returnVal = fc.showOpenDialog(JanusMinerDialog.this);

			if (returnVal == JFileChooser.APPROVE_OPTION) {
				File file = fc.getSelectedFile();
				outputFileSelected.setText(file.toString());
				//This is where a real application would open the file.
				//				log.append("Opening: " + file.getName() + "." + newline);
			} else {
				//				log.append("Open command cancelled by user." + newline);
			}
			//			log.setCaretPosition(log.getDocument().getLength());
		}
	}
}
