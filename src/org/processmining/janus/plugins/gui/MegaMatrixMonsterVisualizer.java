package org.processmining.janus.plugins.gui;

import com.fluxicon.slickerbox.components.RoundedPanel;
import com.fluxicon.slickerbox.components.SlickerTabbedPane;
import com.fluxicon.slickerbox.ui.SlickerScrollBarUI;
import minerful.logparser.LogTraceParser;
import minerful.reactive.automaton.SeparatedAutomatonOfflineRunner;
import minerful.reactive.checking.Measures;
import minerful.reactive.checking.MegaMatrixMonster;
import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;
import org.processmining.contexts.uitopia.UIPluginContext;
import org.processmining.contexts.uitopia.annotations.Visualizer;
import org.processmining.framework.plugin.annotations.Plugin;
import org.processmining.framework.plugin.annotations.PluginVariant;

import javax.swing.*;
import java.awt.*;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

/**
 * This Plugin for the visualization of Janus results is forked from
 * <p>
 * DeclareAnlyzer
 * https://svn.win.tue.nl/repos/prom/Packages/DeclareAnalyzer/
 * by Andrea Burattin and Fabrizio Maggi
 */
@Plugin(name = "Janus compliance checking results visualized", returnLabels = {
		"Janus compliance checking results visualized" },
		returnTypes = { JComponent.class }, parameterLabels = {
		"Janus compliance checking results" }, userAccessible = true)
@Visualizer(name = "Janus Compliance Checker Results Visualizer")
public class MegaMatrixMonsterVisualizer extends SlickerTabbedPane {

	private MegaMatrixMonster matrix;
	private DescriptiveStatistics[][] aggregatedMatrix;
	private java.util.List<SeparatedAutomatonOfflineRunner> automata;
	private List<MegaMatrixMonsterBytesRowVisualizer> tracesByteVisualizerList;

	@PluginVariant(requiredParameterLabels = { 0 })
	public JComponent runUI(UIPluginContext context, MegaMatrixMonster matrix) {
		this.matrix = matrix;
		this.aggregatedMatrix = matrix.getConstraintLogMeasures();
		this.automata = (List) matrix.getAutomata();

		populateConstraints();
		initComponents();
		return this;
	}

	private void populateConstraints() {
		this.tracesByteVisualizerList = new LinkedList<MegaMatrixMonsterBytesRowVisualizer>();

		for (int i = 0; i < matrix.getMatrix().length; i++) {
			MegaMatrixMonsterBytesRowVisualizer visualizer = new MegaMatrixMonsterBytesRowVisualizer(i, matrix);
			tracesByteVisualizerList.add(visualizer);
		}
	}

	public MegaMatrixMonsterVisualizer() {
		super("Janus result analyzer", GUIUtils.panelBackground, Color.lightGray, Color.gray);
		setBackground(Color.black);
		setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
		setOpaque(true);
	}

	private void initComponents() {
		/* add everything to the gui */
		addTab("Aggregated Measures", prepareAggregatedMeasures());
		//		addTab("Trace/Constraints Details", null);
		addTab("MegaMatrixMonster view", prepareMegaMatrixMonsterBigPicture());
	}

	/**
	 * Tab with :
	 * - the list of constraints and their overall measures
	 *
	 * @return page containing the overall measures
	 */
	private JPanel prepareAggregatedMeasures() {
		GridBagConstraints c;

		/* overall details */
		/* ================================================================== */
		RoundedPanel overallDetails = new RoundedPanel(15, 5, 3);
		overallDetails.setLayout(new BorderLayout());
		overallDetails.setBackground(GUIUtils.panelBackground);
		overallDetails.add(GUIUtils.prepareTitle("Constraints aggregated measures"), BorderLayout.NORTH);

		JPanel detailsContainer = new JPanel();
		detailsContainer.setOpaque(false);
		detailsContainer.setLayout(new GridBagLayout());

		JScrollPane detailsScrollerContainer = new JScrollPane(detailsContainer);
		detailsScrollerContainer.setOpaque(false);
		detailsScrollerContainer.getViewport().setOpaque(false);
		detailsScrollerContainer.getVerticalScrollBar()
				.setUI(new SlickerScrollBarUI(detailsScrollerContainer.getVerticalScrollBar(), GUIUtils.panelBackground,
						GUIUtils.panelTextColor, GUIUtils.panelTextColor.brighter(), 4, 11));
		detailsScrollerContainer.getHorizontalScrollBar()
				.setUI(new SlickerScrollBarUI(detailsScrollerContainer.getHorizontalScrollBar(),
						GUIUtils.panelBackground, GUIUtils.panelTextColor, GUIUtils.panelTextColor.brighter(), 4, 11));
		detailsScrollerContainer.setBorder(BorderFactory.createEmptyBorder());
		overallDetails.add(detailsScrollerContainer, BorderLayout.CENTER);

		/* header */
		c = new GridBagConstraints();
		c.insets = new Insets(5, 10, 5, 10);
		c.anchor = GridBagConstraints.SOUTHWEST;
		c.weightx = 1;
		GUIUtils.addToGridBagLayout(0, 0, detailsContainer, GUIUtils.prepareTitleLabel("Constraint"), c);
		c.weightx = 0;
		c.insets = new Insets(5, 10, 5, 10);
		c.anchor = GridBagConstraints.SOUTHEAST;
		GUIUtils.addToGridBagLayout(1, 0, detailsContainer, GUIUtils.prepareVericalTitleLabel("Quality-Measure"), c);
		GUIUtils.addToGridBagLayout(2, 0, detailsContainer, GUIUtils.prepareVericalTitleLabel("Duck-Tape"), c);
		GUIUtils.addToGridBagLayout(3, 0, detailsContainer, GUIUtils.prepareVericalTitleLabel("Mean"), c);
		GUIUtils.addToGridBagLayout(4, 0, detailsContainer, GUIUtils.prepareVericalTitleLabel("Geometric-Mean"), c);
		GUIUtils.addToGridBagLayout(5, 0, detailsContainer, GUIUtils.prepareVericalTitleLabel("Variance"), c);
		GUIUtils.addToGridBagLayout(6, 0, detailsContainer, GUIUtils.prepareVericalTitleLabel("Population-variance"),
				c);
		GUIUtils.addToGridBagLayout(7, 0, detailsContainer, GUIUtils.prepareVericalTitleLabel("Standard-Deviation"), c);
		GUIUtils.addToGridBagLayout(8, 0, detailsContainer, GUIUtils.prepareVericalTitleLabel("Percentile-75th"), c);
		GUIUtils.addToGridBagLayout(9, 0, detailsContainer, GUIUtils.prepareVericalTitleLabel("Max"), c);
		GUIUtils.addToGridBagLayout(10, 0, detailsContainer, GUIUtils.prepareVericalTitleLabel("Min"), c);

		/* details */
		String qualityMeasure = "";
		Double duckTape = 0.0;
		Double mean = 0.0;
		Double geometricMean = 0.0;
		Double variance = 0.0;
		Double populationVariance = 0.0;
		Double standardDeviation = 0.0;
		Double percentile75 = 0.0;
		Double max = 0.0;
		Double min = 0.0;

		int i = 0;
		c = new GridBagConstraints();

		//		for(String constraint : result.getConstraints()) {
		for (int constraintIndex = 0; constraintIndex < aggregatedMatrix.length; constraintIndex++) {
			Iterator<LogTraceParser> it = matrix.getLog().traceIterator();
			LogTraceParser tr = it.next();
			String constraintName = automata.get(constraintIndex)
					.toStringDecoded(tr.getLogParser().getTaskCharArchive().getTranslationMapById());
			DescriptiveStatistics[] constraintLogMeasure = aggregatedMatrix[constraintIndex];

			for (int measureIndex = 0; measureIndex < matrix.getMeasureNames().length; measureIndex++) {
				qualityMeasure = matrix.getMeasureName(measureIndex);
				duckTape = Measures.getLogDuckTapeMeasures(constraintIndex, measureIndex, matrix.getMatrix());
				mean = constraintLogMeasure[measureIndex].getMean();
				geometricMean = constraintLogMeasure[measureIndex].getGeometricMean();
				variance = constraintLogMeasure[measureIndex].getVariance();
				populationVariance = constraintLogMeasure[measureIndex].getPopulationVariance();
				standardDeviation = constraintLogMeasure[measureIndex].getStandardDeviation();
				percentile75 = constraintLogMeasure[measureIndex].getPercentile(75);
				max = constraintLogMeasure[measureIndex].getMax();
				min = constraintLogMeasure[measureIndex].getMin();

				c.insets = new Insets(5, 10, 5, 10);
				c.anchor = GridBagConstraints.WEST;
				c.gridwidth = 1;
				GUIUtils.addToGridBagLayout(0, i + 1, detailsContainer, GUIUtils.prepareLabel(constraintName), c);

				c.anchor = GridBagConstraints.EAST;
				GUIUtils.addToGridBagLayout(1, i + 1, detailsContainer,
						GUIUtils.prepareLabel(qualityMeasure), c);
				GUIUtils.addToGridBagLayout(2, i + 1, detailsContainer,
						GUIUtils.prepareLabel(duckTape, GUIUtils.df2), c);
				GUIUtils.addToGridBagLayout(3, i + 1, detailsContainer,
						GUIUtils.prepareLabel(mean, GUIUtils.df2), c);
				GUIUtils.addToGridBagLayout(4, i + 1, detailsContainer,
						GUIUtils.prepareLabel(geometricMean, GUIUtils.df2), c);
				GUIUtils.addToGridBagLayout(5, i + 1, detailsContainer,
						GUIUtils.prepareLabel(variance, GUIUtils.df2), c);
				GUIUtils.addToGridBagLayout(6, i + 1, detailsContainer,
						GUIUtils.prepareLabel(populationVariance, GUIUtils.df2),
						c);
				GUIUtils.addToGridBagLayout(7, i + 1, detailsContainer,
						GUIUtils.prepareLabel(standardDeviation, GUIUtils.df2), c);
				GUIUtils.addToGridBagLayout(8, i + 1, detailsContainer,
						GUIUtils.prepareLabel(percentile75, GUIUtils.df2), c);
				GUIUtils.addToGridBagLayout(9, i + 1, detailsContainer,
						GUIUtils.prepareLabel(max, GUIUtils.df2), c);
				GUIUtils.addToGridBagLayout(10, i + 1, detailsContainer,
						GUIUtils.prepareLabel(min, GUIUtils.df2), c);

				JPanel line = new JPanel();
				line.setBackground(GUIUtils.panelBackground.darker());
				line.setPreferredSize(new Dimension(10, 1));
				c.gridwidth = 11;
				c.fill = GridBagConstraints.HORIZONTAL;
				c.weightx = 1;
				GUIUtils.addToGridBagLayout(0, i + 2, detailsContainer, line, c);

				i += 2;
			}
		}

		return overallDetails;
	}

	/**
	 * Tab with:
	 * - mega matrix monster in all its details
	 *
	 * @return page containing the mega matrix monster colored-bytes view
	 */
	private JPanel prepareMegaMatrixMonsterBigPicture() {

		/* title */
		JPanel titleContainer = new JPanel(new BorderLayout());
		titleContainer.setOpaque(false);
		titleContainer.add(GUIUtils.prepareTitle("Trace/constraints details"), BorderLayout.WEST);

		/* trace */
		RoundedPanel traceDetails = new RoundedPanel(15, 5, 3);
		traceDetails.setLayout(new BorderLayout());
		traceDetails.setBackground(GUIUtils.panelBackground);
		traceDetails.add(titleContainer, BorderLayout.NORTH);

		JPanel tracesContainer = new JPanel();
		tracesContainer.setOpaque(false);
		tracesContainer.setLayout(new GridBagLayout());

		JScrollPane tracesScrollerContainer = new JScrollPane(tracesContainer);
		tracesScrollerContainer.setOpaque(false);
		tracesScrollerContainer.getViewport().setOpaque(false);
		tracesScrollerContainer.getVerticalScrollBar()
				.setUI(new SlickerScrollBarUI(tracesScrollerContainer.getVerticalScrollBar(), GUIUtils.panelBackground,
						GUIUtils.panelTextColor, GUIUtils.panelTextColor.brighter(), 4, 11));
		tracesScrollerContainer.getHorizontalScrollBar()
				.setUI(new SlickerScrollBarUI(tracesScrollerContainer.getHorizontalScrollBar(),
						GUIUtils.panelBackground, GUIUtils.panelTextColor, GUIUtils.panelTextColor.brighter(), 4, 11));
		tracesScrollerContainer.setBorder(BorderFactory.createEmptyBorder());
		tracesScrollerContainer.getVerticalScrollBar().setUnitIncrement(30);
		traceDetails.add(tracesScrollerContainer, BorderLayout.CENTER);

		refreshTraces(tracesContainer, null);

		return traceDetails;
	}

	private void refreshTraces(JPanel tracesContainer, Comparator<MegaMatrixMonsterBytesRowVisualizer> comparator) {

		tracesContainer.removeAll();

		GridBagConstraints c;
		GUIUtils.addToGridBagLayout(0, 0, tracesContainer, Box.createVerticalStrut(10));
		int i = 1;

		for (MegaMatrixMonsterBytesRowVisualizer visualizer : tracesByteVisualizerList) {
			String traceName = visualizer.getTraceName();

			c = new GridBagConstraints();
			c.anchor = GridBagConstraints.WEST;
			c.fill = GridBagConstraints.BOTH;
			c.weightx = 1;

			GUIUtils.addToGridBagLayout(0, i, tracesContainer, GUIUtils.prepareTitleBordered(traceName), c);
			GUIUtils.addToGridBagLayout(0, i + 1, tracesContainer, visualizer, c);
			GUIUtils.addToGridBagLayout(0, i + 2, tracesContainer, Box.createVerticalStrut(10));

			i += 2;

		}

		GUIUtils.addToGridBagLayout(0, i + 2, tracesContainer, Box.createVerticalGlue(), 0, 1);

		tracesContainer.updateUI();
	}
}
