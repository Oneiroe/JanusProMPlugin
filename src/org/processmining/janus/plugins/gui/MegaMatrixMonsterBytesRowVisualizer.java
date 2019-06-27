package org.processmining.janus.plugins.gui;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.awt.image.BufferedImage;
import java.lang.ref.SoftReference;
import java.util.*;

import javax.swing.JPanel;

import minerful.logparser.LogTraceParser;
import minerful.reactive.automaton.SeparatedAutomatonOfflineRunner;
import minerful.reactive.checking.MegaMatrixMonster;

public class MegaMatrixMonsterBytesRowVisualizer extends JPanel implements MouseMotionListener {

	private static final long serialVersionUID = 3287603101866616636L;
	private LogTraceParser traceParser;
	private int traceIndex;
	private MegaMatrixMonster matrix;
	private HashMap<String, Integer> constraintToPosition;

	private int constraintHeight = 20;
	private int resolutionDetailsHeight = 30;
	private int eventWidth = 8;
	private int eventHeight = -1;
	private int detailsRectangleHeight = -1;
	private int detailsVerticalMargin = -1;

	private Font defaultFont = null;
	//	private Font traceFont = null;
	private Font detailsFont = null;
	private FontMetrics defaultFontMetric = null;
	//	private FontMetrics traceFontMetric = null;
	private FontMetrics detailsFontMetric = null;
	private SoftReference<BufferedImage> buffer = null;

	private int mouseX = -1;
	private int mouseY = -1;

	private String traceName;

	/**
	 * @param
	 */
	public MegaMatrixMonsterBytesRowVisualizer(int traceIndex, MegaMatrixMonster matrix) {
		this.traceIndex = traceIndex;
		this.matrix = matrix;
		this.constraintToPosition = new HashMap<String, Integer>();

		// general information
		Iterator<LogTraceParser> logParser = matrix.getLog().traceIterator();
		for (int i = 0; i < traceIndex; i++) {
			logParser.next();
			// TODO unsafe iterator exploration
		}
		LogTraceParser traceParser = logParser.next();
		this.traceParser = traceParser;
		this.traceName = traceParser.getName();

		addMouseMotionListener(this);

		// general configuration
		int width = traceParser.length() * (eventWidth + 2);
		int height = (matrix.getAutomata().size() + 1) * constraintHeight + 50;
		int maxLetterConstraint = 0;

		// longest string
		maxLetterConstraint = Math.max(matrix.getLog().maximumTraceLength(), maxLetterConstraint);

		// Heuristics on letter width... we don't yet have the font metrics,
		// let's assume an average of 10 pixel per letter
		width += (maxLetterConstraint * 10);

		setMinimumSize(new Dimension(width, height));
		setPreferredSize(new Dimension(width, height));
		setOpaque(false);
	}

	@Override
	public void paint(Graphics g) {
		super.paint(g);

		int width = this.getWidth();
		int height = this.getHeight();

		// create new back buffer
		buffer = new SoftReference<BufferedImage>(new BufferedImage(width, height, BufferedImage.TRANSLUCENT));
		Graphics2D g2d = buffer.get().createGraphics();
		g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

		// setting up the "one-time font stuff"
		if (defaultFont == null) {
			defaultFont = g2d.getFont();
			defaultFont = defaultFont.deriveFont(12f);
			defaultFontMetric = g2d.getFontMetrics(defaultFont);
		}
		//		if (traceFont == null) {
		//			traceFont = defaultFont.deriveFont(Font.BOLD);
		//			traceFontMetric = g2d.getFontMetrics(traceFont);
		//		}
		if (detailsFont == null) {
			detailsFont = defaultFont.deriveFont(10f);
			detailsFontMetric = g2d.getFontMetrics(detailsFont);
		}
		if (eventHeight == -1) {
			eventHeight = defaultFontMetric.getAscent() + defaultFontMetric.getDescent();
		}
		if (detailsRectangleHeight == -1) {
			detailsRectangleHeight = detailsFontMetric.getAscent() + detailsFontMetric.getDescent();
		}
		if (detailsVerticalMargin == -1) {
			detailsVerticalMargin =
					(defaultFontMetric.getAscent() + defaultFontMetric.getDescent() - detailsRectangleHeight) / 2;
		}

		// constraints name
		int maxSpace = 0;
		for (SeparatedAutomatonOfflineRunner constraint : matrix.getAutomata()) {
			String constraintName = constraint
					.toStringDecoded(traceParser.getLogParser().getTaskCharArchive().getTranslationMapById());
			maxSpace = Math.max(maxSpace, defaultFontMetric.stringWidth(constraintName));
		}

		// header
		int headerWidth = 100;

		g2d.setColor(GUIUtils.panelTextTitleColor);
		g2d.setFont(detailsFont);

		// trace
		int k = 0;
		int extraHeight = 20;
		int constraintIndex = 0;
		for (SeparatedAutomatonOfflineRunner constraint : matrix.getAutomata()) {
			String constraintName = constraint
					.toStringDecoded(traceParser.getLogParser().getTaskCharArchive().getTranslationMapById());

			int positionY = extraHeight + defaultFontMetric.getAscent() + 6 + (k * constraintHeight);

			// trace name
			g2d.setFont(defaultFont);
			g2d.setColor(GUIUtils.panelTextColor);
			g2d.drawString(
					constraintName,
					maxSpace - defaultFontMetric.stringWidth(constraintName) + 5,
					positionY + defaultFontMetric.getAscent());
			constraintToPosition.put(constraintName, positionY);

			// the actual trace
			LinkedList<String> eventNames = new LinkedList<String>();
			traceParser.init();
			//				Trace as unencoded string
			for (int i = 0; i < traceParser.length(); i++) {
				eventNames.add(traceParser.parseSubsequent().getEvent().getTaskClass().toString());
			}
			paintTrace(g2d, maxSpace + 15, positionY, width, mouseX, mouseY, eventNames,
					matrix.getMatrix()[traceIndex][constraintIndex]);

			/* resolutions */
			//			ArrayList<List<Integer>> resolutions = ar.getResolutions();
			//			if (resolutions.size() > 0 && ar.getConflicts().size() > 0) {
			//
			//				String resolutionText = "Resolutions:";
			//				g2d.setColor(GUIUtils.panelBackground.darker());
			//				g2d.setFont(defaultFont);
			//				g2d.drawString(resolutionText,
			//						maxSpace + 5 - defaultFontMetric.stringWidth(resolutionText),
			//						positionY + constraintHeight + defaultFontMetric.getAscent());
			//
			//				Set<Integer> conflicts = ar.getConflicts();
			//				int resolutionsCounter = 0;
			//				for (List<Integer> et : resolutions) {
			//					int positionX = maxSpace + 15;
			//
			//					Set<Integer> violations = new HashSet<Integer>();
			//					Set<Integer> fulfilments = new HashSet<Integer>();
			//
			//					for (Integer c : conflicts) {
			//						if (et.contains(c)) {
			//							fulfilments.add(c);
			//						} else {
			//							violations.add(c);
			//						}
			//					}
			//
			//					// resolution trace
			//					paintTrace(g2d,
			//							positionX, positionY + constraintHeight + resolutionsCounter * (constraintHeight
			//									+ resolutionDetailsHeight),
			//							width,
			//							mouseX, mouseY, eventNames,
			//							null, violations, fulfilments, null, true);
			//
			//					// resolution local likelihood
			//					double localLikelihood = (double) fulfilments.size() / ar.getConflicts().size();
			//
			//					// resolution global likelihood
			//					double globalLikelihood = 0.0;
			//					MultiSet<Double> likelihoods = new HashMultiSet<Double>();
			//
			//					for (Integer c : conflicts) {
			//						double violationsLikelihood = 0.0;
			//						double fulfillmentsLikelihood = 0.0;
			//						for (AnalysisSingleResult constr : constraints) {
			//							if (violations.contains(c)) {
			//								if (constr.getViolations().contains(c)) {
			//									violationsLikelihood++;
			//								}
			//							} else {
			//								if (constr.getFulfilments().contains(c)) {
			//									fulfillmentsLikelihood++;
			//								}
			//							}
			//						}
			//						if (violationsLikelihood > 0) {
			//							violationsLikelihood /= constraints.size();
			//							likelihoods.add(violationsLikelihood);
			//						}
			//						if (fulfillmentsLikelihood > 0) {
			//							fulfillmentsLikelihood /= constraints.size();
			//							likelihoods.add(fulfillmentsLikelihood);
			//						}
			//					}
			//
			//					if (likelihoods.size() > 0) {
			//						for (Double l : likelihoods) {
			//							globalLikelihood += l;
			//						}
			//						globalLikelihood /= likelihoods.size();
			//					}
			//
			//					int yPositionLikelihood =
			//							positionY + (resolutionsCounter + 1) * (constraintHeight + resolutionDetailsHeight);
			//					g2d.setFont(detailsFont);
			//					g2d.setColor(GUIUtils.eventDetailsColor);
			//					g2d.drawString(GUIUtils.df2.format(localLikelihood) + "",
			//							positionX,
			//							yPositionLikelihood);
			//					g2d.drawString(GUIUtils.df2.format(globalLikelihood),
			//							positionX,
			//							yPositionLikelihood + detailsFontMetric.getAscent());
			//					g2d.drawString(GUIUtils.df2.format((localLikelihood + globalLikelihood) / 2),
			//							positionX + 130,
			//							yPositionLikelihood + detailsFontMetric.getAscent() / 2);
			//
			//					g2d.setColor(GUIUtils.panelBackground.darker());
			//					g2d.drawString("Local likelihood",
			//							positionX + 35,
			//							yPositionLikelihood);
			//					g2d.drawString("Global likelihood",
			//							positionX + 35,
			//							yPositionLikelihood + detailsFontMetric.getAscent());
			//					g2d.drawString("Avg of likelihoods",
			//							positionX + 165,
			//							yPositionLikelihood + detailsFontMetric.getAscent() / 2);
			//					g2d.drawLine(
			//							positionX + 120,
			//							yPositionLikelihood - detailsFontMetric.getAscent(),
			//							positionX + 120,
			//							yPositionLikelihood + detailsFontMetric.getAscent());
			//					g2d.drawLine(
			//							positionX + 117,
			//							yPositionLikelihood - detailsFontMetric.getAscent(),
			//							positionX + 120,
			//							yPositionLikelihood - detailsFontMetric.getAscent());
			//					g2d.drawLine(
			//							positionX + 117,
			//							yPositionLikelihood + detailsFontMetric.getAscent(),
			//							positionX + 120,
			//							yPositionLikelihood + detailsFontMetric.getAscent());
			//					g2d.drawLine(
			//							positionX + 120,
			//							yPositionLikelihood,
			//							positionX + 123,
			//							yPositionLikelihood);
			//
			//					resolutionsCounter++;
			//				}
			//				extraHeight += resolutionsCounter * (constraintHeight + resolutionDetailsHeight);
			//			}
			k++;
			constraintIndex++;
		}

		// constraint details
		constraintIndex = 0;
		for (SeparatedAutomatonOfflineRunner constraint : matrix.getAutomata()) {
			String constraintName = constraint
					.toStringDecoded(traceParser.getLogParser().getTaskCharArchive().getTranslationMapById());

			paintConstraintDetails(g2d, matrix.getMatrix()[traceIndex][constraintIndex], constraintName,
					0, constraintToPosition.get(constraintName),
					maxSpace, defaultFontMetric.getAscent() + defaultFontMetric.getDescent() + 1, mouseX, mouseY,
					height);
			constraintIndex++;
		}

		// final paint stuff
		g2d.dispose();
		Rectangle clip = g.getClipBounds();
		g.drawImage(buffer.get(), clip.x, clip.y, clip.x + clip.width, clip.y + clip.height,
				clip.x, clip.y, clip.x + clip.width, clip.y + clip.height, null);
	}

	private void paintTrace(
			Graphics2D g2d,
			int x, int y,
			int width,
			int mouseX, int mouseY,
			List<String> trace,
			byte[] traceEvaluation) {

		int textWidth = 0;

		for (int j = 0; j < trace.size(); j++) {

			int positionX = x + (j * (eventWidth + 2));

			g2d.setColor(GUIUtils.event);
			/*
			 * 0 -> 00 -> Activator: False, Target: False
			 * 1 -> 01 -> Activator: False, Target: true
			 * 2 -> 10 -> Activator: True,  Target: False
			 * 3 -> 11 -> Activator: True,  Target: True
			 */
			switch (traceEvaluation[j]) {
				case 1:
					g2d.setColor(GUIUtils.eventStrange);
					break;
				case 2:
					g2d.setColor(GUIUtils.eventViolated);
					break;
				case 3:
					g2d.setColor(GUIUtils.eventFulfilled);
					break;
				default:
					break;
			}

			g2d.fillRoundRect(positionX, y, eventWidth, eventHeight, 4, 4);
		}

		/* details part */
		if (mouseX >= x && mouseX <= x + (trace.size() * (eventWidth + 2))) {
			if (mouseY >= y && mouseY <= y + defaultFontMetric.getAscent() + defaultFontMetric.getDescent()) {

				int j = (mouseX - x) / (eventWidth + 2);

				if (j >= 0 && j < trace.size()) {

					String is = "";

					g2d.setColor(GUIUtils.event);
					switch (traceEvaluation[j]) {
						case 2:
							if (is == "") {
								is += " is ";
							} else {
								is += ", ";
							}
							is += "violation";
							break;
						case 3:
							if (is == "") {
								is += " is ";
							} else {
								is += ", ";
							}
							is += "fulfilment";
							break;
						default:
							break;
					}

					is += " (ev. no. " + (j + 1) + ")";

					int positionX = x + (j * (eventWidth + 2));

					/* name of the event and constraint status */
					String text = "\"" + trace.get(j) + "\"" + is;
					textWidth = detailsFontMetric.stringWidth(text);

					boolean flip = false;
					if (positionX + textWidth + 11 > width) {
						flip = true;
						positionX -= (textWidth + (eventWidth + 2) * 2 + 5);
					}

					g2d.setColor(GUIUtils.eventDetailsBackground);
					g2d.fillRoundRect(positionX + eventWidth + 5, y + detailsVerticalMargin, textWidth + 6,
							detailsRectangleHeight, 5, 5);
					if (!flip) {
						g2d.fillPolygon(new int[] {
										positionX + eventWidth + 1,
										positionX + eventWidth + 5,
										positionX + eventWidth + 5 },
								new int[] {
										y + (detailsRectangleHeight / 2) + detailsVerticalMargin,
										y + (detailsRectangleHeight / 2) + detailsVerticalMargin - 3,
										y + (detailsRectangleHeight / 2) + detailsVerticalMargin + 3 }, 3);
					} else {
						g2d.fillPolygon(new int[] {
										positionX + textWidth + (eventWidth + 2) * 2 + 5,
										positionX + textWidth + (eventWidth + 2) * 2 - 1,
										positionX + textWidth + (eventWidth + 2) * 2 - 1 },
								new int[] {
										y + (detailsRectangleHeight / 2) + detailsVerticalMargin,
										y + (detailsRectangleHeight / 2) + detailsVerticalMargin - 3,
										y + (detailsRectangleHeight / 2) + detailsVerticalMargin + 3 }, 3);
					}
					g2d.setColor(GUIUtils.eventDetailsColor);
					g2d.setFont(detailsFont);
					g2d.drawString(text, positionX + eventWidth + 8,
							y + detailsVerticalMargin + detailsFontMetric.getAscent());

				}
			}
		}
	}

	private void paintConstraintDetails(
			Graphics2D g2d,
			byte[] traceEvaluation,
			String constraintName,
			int x, int y,
			int width, int height,
			int mouseX, int mouseY, int maxYPosition) {

		if (mouseX >= x && mouseX <= x + width) {
			if (mouseY >= y && mouseY <= y + height) {

				int panelHeight = 90;
				int panelWidth = detailsFontMetric.stringWidth(constraintName) + 20;
				if (panelWidth < 230) {
					panelWidth = 230;
				}

				int positionY = y - 5;
				int positionX = x + width + 10;
				int stringWidth = defaultFontMetric.stringWidth(constraintName);

				if (positionY + panelHeight > maxYPosition) {
					positionY = maxYPosition - panelHeight - 5;
				}

				g2d.setColor(GUIUtils.eventDetailsBackground);
				g2d.fillRoundRect(positionX, positionY, panelWidth, panelHeight, 10, 10);
				g2d.setColor(GUIUtils.panelBackground.darker());
				g2d.fillRoundRect(width - stringWidth, y, stringWidth + 7, height, 7, 7);

				g2d.setColor(GUIUtils.panelTextColor);
				g2d.drawRoundRect(width - stringWidth, y, stringWidth + 7, height, 7, 7);
				g2d.drawLine(
						positionX - 3, y + 7,
						positionX, y + 7);

				g2d.setFont(defaultFont);
				//				g2d.setColor(GUIUtils.eventDetailsColor.darker());
				g2d.setColor(GUIUtils.eventDetailsColor);
				g2d.drawString(
						constraintName,
						width - defaultFontMetric.stringWidth(constraintName) + 5,
						y + defaultFontMetric.getAscent());

				g2d.setFont(detailsFont);
				g2d.setColor(GUIUtils.eventDetailsColor);
				g2d.drawString(constraintName,
						positionX + 10,
						positionY + detailsFontMetric.getDescent() + detailsFontMetric.getAscent());
				g2d.drawLine(positionX + 10,
						positionY + detailsFontMetric.getDescent() + detailsFontMetric.getAscent() + 5,
						positionX + panelWidth - 10,
						positionY + detailsFontMetric.getDescent() + detailsFontMetric.getAscent() + 5);

				int row = 1;
				g2d.drawString("Activations:", positionX + 10,
						positionY + 5 + ++row * (detailsFontMetric.getAscent() + detailsFontMetric.getDescent() + 2));
				g2d.drawString("" + getActivations(traceEvaluation), positionX + 70,
						positionY + 5 + row * (detailsFontMetric.getAscent() + detailsFontMetric.getDescent() + 2));
				g2d.drawString("Fulfilments:", positionX + 10,
						positionY + 5 + ++row * (detailsFontMetric.getAscent() + detailsFontMetric.getDescent() + 2));
				g2d.drawString("" + getFulfilments(traceEvaluation), positionX + 70,
						positionY + 5 + row * (detailsFontMetric.getAscent() + detailsFontMetric.getDescent() + 2));
				g2d.drawString("Violations:", positionX + 10,
						positionY + 5 + ++row * (detailsFontMetric.getAscent() + detailsFontMetric.getDescent() + 2));
				g2d.drawString("" + getViolations(traceEvaluation), positionX + 70,
						positionY + 5 + row * (detailsFontMetric.getAscent() + detailsFontMetric.getDescent() + 2));

			}
		}
	}

	private int getViolations(byte[] traceEvaluation) {
		int result = 0;
		for (byte eval : traceEvaluation) {
			result += eval / 2; // activator and target are both true when the byte is 3
		}
		return result;
	}

	private int getFulfilments(byte[] traceEvaluation) {
		int result = 0;
		for (byte eval : traceEvaluation) {
			result += eval / 3; // activator and target are both true when the byte is 3
		}
		return result;
	}

	private int getActivations(byte[] traceEvaluation) {
		int result = 0;
		for (byte eval : traceEvaluation) {
			result += eval / 3; // activator and target are both true when the byte is 3
			result += eval / 2; // activator and target are both true when the byte is 3
		}
		return result;
	}

	public String getTraceName() {
		return traceName;
	}

	@Override
	public void mouseDragged(MouseEvent arg0) {

	}

	@Override
	public void mouseMoved(MouseEvent arg0) {
		mouseX = arg0.getX();
		mouseY = arg0.getY();
		repaint();
	}
}