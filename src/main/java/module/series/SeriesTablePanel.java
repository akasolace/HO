package module.series;

import core.gui.comp.entry.ColorLabelEntry;
import core.gui.comp.entry.DoubleLabelEntries;
import core.gui.comp.icon.DrawIcon;
import core.gui.comp.panel.ImagePanel;
import core.gui.comp.renderer.HODefaultTableCellRenderer;
import core.gui.model.VAPTableModel;
import core.gui.theme.HOColorName;
import core.gui.theme.ThemeManager;
import core.model.HOVerwaltung;
import core.model.series.LigaTabellenEintrag;
import core.util.HOLogger;
import core.util.Helper;
import core.util.StringUtils;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.event.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.util.Vector;

/**
 * A panel with a league table
 */
class SeriesTablePanel extends ImagePanel {

	public static final EmptyBorder EMPTY_BORDER = new EmptyBorder(5, 5, 5, 5);
	private Color TITLE_BACKGROUND = ThemeManager.getColor(HOColorName.LEAGUE_TITLE_BG);
	private Color TABLE_FOREGROUND = ThemeManager.getColor(HOColorName.LEAGUE_FG);

	private Color TABLE_EVEN_ROW = ThemeManager.getColor(HOColorName.TABLE_LEAGUE_EVEN);
	private Color TABLE_ODD_ROW = ThemeManager.getColor(HOColorName.TABLE_LEAGUE_ODD);

	private final String[] COLUMNNAMES = {
			"",
			"",
			HOVerwaltung.instance().getLanguageString("Punkte_kurz"),
			HOVerwaltung.instance().getLanguageString("Spiele_kurz"),
			HOVerwaltung.instance().getLanguageString("SerieAuswaertsSieg"),
			HOVerwaltung.instance().getLanguageString("SerieAuswaertsUnendschieden"),
			HOVerwaltung.instance().getLanguageString("SerieAuswaertsNiederlage"),
			HOVerwaltung.instance().getLanguageString("Tore"),
			HOVerwaltung.instance().getLanguageString("Differenz_kurz"),
			HOVerwaltung.instance().getLanguageString("Serie.Last9"),
	};
	private JTable seriesTable = new JTable();
	private Object[][] tableValues;
	private final Model model;

	/**
	 * Creates a new LigaTabelle object.
	 */
	SeriesTablePanel(Model model) {
		this.model = model;
		initComponents();

		initTable();

		initSeriesTable();
	}

	final String getSelectedTeam() {
		String team = null;

		if (seriesTable.getSelectedRow() > -1) {
			team = ((ColorLabelEntry) tableValues[seriesTable.getSelectedRow()][1]).getText();
		}

		return team;
	}

	void addListSelectionListener(ListSelectionListener listener) {
		this.seriesTable.getSelectionModel().addListSelectionListener(listener);
	}

	@Override
	public final void addKeyListener(KeyListener listener) {
		seriesTable.addKeyListener(listener);
	}

	// --Listener an Tabelle binden!------------------
	@Override
	public final void addMouseListener(MouseListener listener) {
		seriesTable.addMouseListener(listener);
	}

	// -------Refresh---------------------------------
	public final void changeSaison() {
		populateSerieTable();
	}

	private Color getColor4Row(int row) {
		return (row%2 == 0) ? TABLE_EVEN_ROW : TABLE_ODD_ROW;
	}

	private void setTableColumnWidth() {
		final TableColumnModel columnModel = seriesTable.getColumnModel();

		// Place
		columnModel.getColumn(0).setPreferredWidth(Helper.calcCellWidth(45));

		// Club
		columnModel.getColumn(1).setPreferredWidth(Helper.calcCellWidth(200));

		// Points
		columnModel.getColumn(2).setPreferredWidth(Helper.calcCellWidth(30));

		// # Played matches
		columnModel.getColumn(3).setPreferredWidth(Helper.calcCellWidth(25));

		// # Victories
		columnModel.getColumn(4).setPreferredWidth(Helper.calcCellWidth(25));

		// # Draws
		columnModel.getColumn(5).setPreferredWidth(Helper.calcCellWidth(25));

		// Verloren
		columnModel.getColumn(6).setPreferredWidth(Helper.calcCellWidth(25));

		// # Losses
		columnModel.getColumn(7).setPreferredWidth(Helper.calcCellWidth(50));

		// Goals Difference
		columnModel.getColumn(8).setPreferredWidth(Helper.calcCellWidth(50));

		// Serie
		columnModel.getColumn(9).setPreferredWidth(Helper.calcCellWidth(140));
	}

	private void initComponents() {
		final GridBagLayout layout = new GridBagLayout();
		final GridBagConstraints constraints = new GridBagConstraints();
		constraints.fill = GridBagConstraints.NONE;
		constraints.weightx = 1.0;
		constraints.weighty = 1.0;
		constraints.gridx = 0;
		constraints.gridy = 0;
		constraints.insets = new Insets(4, 4, 4, 4);

		seriesTable.setDefaultRenderer(java.lang.Object.class, new HODefaultTableCellRenderer());
		seriesTable.setRowHeight(30);

		final JPanel panel = new ImagePanel(layout);
		layout.setConstraints(panel, constraints);
		panel.add(seriesTable);

		constraints.gridx = 1;
		constraints.gridy = 0;
		constraints.weightx = 1.0;
		constraints.anchor = GridBagConstraints.NORTH;
		layout.setConstraints(panel, constraints);

		setLayout(new BorderLayout());
		add(panel);
	}

	//  Init from the HRF-XML
	private void initSeriesTable() {

		if ((HOVerwaltung.instance().getModel().getFixtures() != null)
				&& (HOVerwaltung.instance().getModel().getFixtures().getSaison() > 0)) {

			// Fill values if a model is available
			populateSerieTable();
		}
	}

	private void initTable() {

		tableValues = new Object[9][COLUMNNAMES.length];

		// Column Headers
		for (int i = 0; i < COLUMNNAMES.length; i++) {
			tableValues[0][i] = new ColorLabelEntry(COLUMNNAMES[i], ColorLabelEntry.FG_STANDARD,
					TITLE_BACKGROUND, SwingConstants.CENTER);
		}

		for (int i = 1; i < 9; i++) {
			final Color bg_Color = getColor4Row(i);

			final ColorLabelEntry clPositionLeft = new ColorLabelEntry("",
					ColorLabelEntry.FG_STANDARD, bg_Color, SwingConstants.RIGHT);
			clPositionLeft.setBorder(EMPTY_BORDER);
			final ColorLabelEntry clPositionRight = new ColorLabelEntry("", ColorLabelEntry.FG_STANDARD, bg_Color,
					SwingConstants.RIGHT);
			clPositionRight.setBorder(EMPTY_BORDER);
			tableValues[i][0] = new DoubleLabelEntries(clPositionLeft, clPositionRight);

			tableValues[i][1] = new ColorLabelEntry("", ColorLabelEntry.FG_STANDARD, bg_Color,
					SwingConstants.LEFT);
			tableValues[i][2] = new ColorLabelEntry("", ColorLabelEntry.FG_STANDARD, bg_Color,
					SwingConstants.CENTER);
			tableValues[i][3] = new ColorLabelEntry("", ColorLabelEntry.FG_STANDARD, bg_Color,
					SwingConstants.CENTER);
			tableValues[i][4] = new ColorLabelEntry("", ColorLabelEntry.FG_STANDARD, bg_Color,
					SwingConstants.CENTER);
			tableValues[i][5] = new ColorLabelEntry("", ColorLabelEntry.FG_STANDARD, bg_Color,
					SwingConstants.CENTER);
			tableValues[i][6] = new ColorLabelEntry("", ColorLabelEntry.FG_STANDARD, bg_Color,
					SwingConstants.CENTER);
			tableValues[i][7] = new ColorLabelEntry("", ColorLabelEntry.FG_STANDARD, bg_Color,
					SwingConstants.CENTER);
			tableValues[i][8] = new ColorLabelEntry("", ColorLabelEntry.FG_STANDARD, bg_Color,
					SwingConstants.CENTER);
			tableValues[i][9] = new ColorLabelEntry("", ColorLabelEntry.FG_STANDARD, bg_Color,
					SwingConstants.LEFT);

			for (int j = 2 ; j < 9 ; j++) {
				((JLabel)tableValues[i][j]).setBorder(EMPTY_BORDER);
			}
		}

		seriesTable.setModel(new VAPTableModel(COLUMNNAMES, tableValues));
		setTableColumnWidth();
	}

	private void populateSerieTable() {
		try {
			if (this.model.getCurrentSeries() != null) {
				final Vector<LigaTabellenEintrag> tabelleneintraege = this.model.getCurrentSeries()
						.getTabelle().getEintraege();
				final int teamid = HOVerwaltung.instance().getModel().getBasics().getTeamId();
				int j;

				for (int i = 0; i < tabelleneintraege.size(); i++) {
					final LigaTabellenEintrag entry = tabelleneintraege.get(i);

					if (entry.getPoints() > -1) {
						j = i + 1;

						// Column position  ========================================
						((DoubleLabelEntries) tableValues[j][0]).getLeft().setText("");
						((DoubleLabelEntries) tableValues[j][0]).getLeft().setFontStyle(Font.BOLD);
						if (entry.getPosition() < entry.getPreviousPosition()) {
							((DoubleLabelEntries) tableValues[j][0]).getLeft().setIcon(new DrawIcon(DrawIcon.UPWARD_DIRECTION));
						} else if (entry.getPosition() > entry.getPreviousPosition()) {
							((DoubleLabelEntries) tableValues[j][0]).getLeft().setIcon(new DrawIcon(DrawIcon.DOWNWARD_DIRECTION));
						}

						((DoubleLabelEntries) tableValues[j][0]).getRight().setText(entry.getPosition() + "");
						((DoubleLabelEntries) tableValues[j][0]).getRight().setFontStyle(Font.BOLD);


						// Column club ========================================
						((ColorLabelEntry) tableValues[j][1]).setIcon(ThemeManager.instance().getSmallClubLogo(entry.getTeamId()));
						((ColorLabelEntry) tableValues[j][1]).setText(entry.getTeamName());
						((ColorLabelEntry) tableValues[j][1]).setFontStyle(Font.BOLD);
//						((ColorLabelEntry) tableValues[j][1]).getRight().setAlignment(SwingConstants.LEFT);

						if (entry.getTeamId() == teamid) {
							((ColorLabelEntry) tableValues[j][1]).setFGColor(ThemeManager.getColor(HOColorName.HOME_TEAM_FG));
						} else {
							((ColorLabelEntry) tableValues[j][1]).setFGColor(TABLE_FOREGROUND);
						}

						// Other columns ========================================
						((ColorLabelEntry) tableValues[j][2]).setText(entry.getPoints() + "");
						((ColorLabelEntry) tableValues[j][2]).setFontStyle(Font.BOLD);
						((ColorLabelEntry) tableValues[j][3]).setText(entry.getAnzSpiele() + "");
						((ColorLabelEntry) tableValues[j][4]).setText(entry.getG_Siege() + "");
						((ColorLabelEntry) tableValues[j][5]).setText(entry.getG_Un() + "");
						((ColorLabelEntry) tableValues[j][6]).setText(entry.getG_Nied() + "");
						((ColorLabelEntry) tableValues[j][7]).setText(StringUtils.getResultString(
								entry.getToreFuer(), entry.getToreGegen(), ""));
						((ColorLabelEntry) tableValues[j][8]).setSpecialNumber(
								entry.getGesamtTorDiff(), false);

						FormLabel formLabel = new FormLabel(entry.getSerie());
						formLabel.setBgColor(getColor4Row(j));
						tableValues[j][9] = formLabel;
					}
				}

				// Model setzen
				seriesTable.setModel(new VAPTableModel(COLUMNNAMES, tableValues));
			}

			setTableColumnWidth();
		} catch (Exception e) {
			HOLogger.instance().log(getClass(), "Reinit Tabelle : " + e);
			HOLogger.instance().log(getClass(), e);
		}
	}
}
