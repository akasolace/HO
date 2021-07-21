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
import core.model.enums.RatingsStatistics;
import core.model.series.SerieTableEntry;
import core.util.HOLogger;
import core.util.Helper;
import core.util.StringUtils;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.event.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.util.Map;
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
			Helper.getTranslation("Punkte_kurz"),
			Helper.getTranslation("Spiele_kurz"),
			Helper.getTranslation("SerieAuswaertsSieg"),
			Helper.getTranslation("SerieAuswaertsUnendschieden"),
			Helper.getTranslation("SerieAuswaertsNiederlage"),
			Helper.getTranslation("ls.match.gf"),
			Helper.getTranslation("ls.match.ga"),
			Helper.getTranslation("ls.match.gd"),
			Helper.getTranslation("Serie.Last5"),
			Helper.getTranslation("ls.module.statistics.club.power_rating_short"),
			Helper.getTranslation("ls.match.ratingtype.hatstats"),
			Helper.getTranslation("ls.match.ratingsector.defense"),
			Helper.getTranslation("ls.match.ratingsector.midfield"),
			Helper.getTranslation("ls.match.ratingsector.attack")
	};
	private JTable seriesTable = new JTable();
	private Object[][] tableValues;
	private final Model model;
	private JComboBox m_jcbStatsAggType;

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

		// # Losses
		columnModel.getColumn(6).setPreferredWidth(Helper.calcCellWidth(25));

		// Goals For
		columnModel.getColumn(7).setPreferredWidth(Helper.calcCellWidth(25));

		// Goals Against
		columnModel.getColumn(8).setPreferredWidth(Helper.calcCellWidth(25));

		// Goals Difference
		columnModel.getColumn(9).setPreferredWidth(Helper.calcCellWidth(25));

		// Serie
		columnModel.getColumn(10).setPreferredWidth(Helper.calcCellWidth(90));

		// Statistics
		columnModel.getColumn(11).setPreferredWidth(Helper.calcCellWidth(50));
		columnModel.getColumn(12).setPreferredWidth(Helper.calcCellWidth(50));
		columnModel.getColumn(13).setPreferredWidth(Helper.calcCellWidth(50));
		columnModel.getColumn(14).setPreferredWidth(Helper.calcCellWidth(50));
		columnModel.getColumn(15).setPreferredWidth(Helper.calcCellWidth(50));
	}

	private void initComponents() {

		final GridBagLayout layout = new GridBagLayout();
		final GridBagConstraints constraints = new GridBagConstraints();
		final JPanel panel = new JPanel(layout);

		// combobox
		constraints.gridx = 0;
		constraints.gridy = 0;
		constraints.insets = new Insets(4, 0, 0, 100);
		constraints.anchor = GridBagConstraints.LINE_END;
		m_jcbStatsAggType = new JComboBox(new String[]{"Max", "Avg"});
		m_jcbStatsAggType.addActionListener(e -> populateSerieTableStatsOnly());

		layout.setConstraints(m_jcbStatsAggType, constraints);
		panel.add(m_jcbStatsAggType);

		// serie Table
		constraints.gridy = 1;
		constraints.insets = new Insets(0, 4, 4, 4);
		seriesTable.setDefaultRenderer(java.lang.Object.class, new HODefaultTableCellRenderer());
		seriesTable.setRowHeight(30);
		layout.setConstraints(seriesTable, constraints);
		panel.add(seriesTable);

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
			tableValues[0][i] = new ColorLabelEntry(COLUMNNAMES[i], ColorLabelEntry.FG_STANDARD, TITLE_BACKGROUND, SwingConstants.CENTER);
		}

		for (int i = 1; i < 9; i++) {
			final Color bg_Color = getColor4Row(i);

			final ColorLabelEntry clPositionLeft = new ColorLabelEntry("", ColorLabelEntry.FG_STANDARD, bg_Color, SwingConstants.RIGHT);
			clPositionLeft.setBorder(EMPTY_BORDER);
			final ColorLabelEntry clPositionRight = new ColorLabelEntry("", ColorLabelEntry.FG_STANDARD, bg_Color, SwingConstants.RIGHT);
			clPositionRight.setBorder(EMPTY_BORDER);

			tableValues[i][0] = new DoubleLabelEntries(clPositionLeft, clPositionRight);
			tableValues[i][1] = new ColorLabelEntry("", ColorLabelEntry.FG_STANDARD, bg_Color, SwingConstants.LEFT);
			tableValues[i][2] = new ColorLabelEntry("", ColorLabelEntry.FG_STANDARD, bg_Color, SwingConstants.CENTER);
			tableValues[i][3] = new ColorLabelEntry("", ColorLabelEntry.FG_STANDARD, bg_Color, SwingConstants.CENTER);
			tableValues[i][4] = new ColorLabelEntry("", ColorLabelEntry.FG_STANDARD, bg_Color, SwingConstants.CENTER);
			tableValues[i][5] = new ColorLabelEntry("", ColorLabelEntry.FG_STANDARD, bg_Color, SwingConstants.CENTER);
			tableValues[i][6] = new ColorLabelEntry("", ColorLabelEntry.FG_STANDARD, bg_Color, SwingConstants.CENTER);
			tableValues[i][7] = new ColorLabelEntry("", ColorLabelEntry.FG_STANDARD, bg_Color, SwingConstants.CENTER);
			tableValues[i][8] = new ColorLabelEntry("", ColorLabelEntry.FG_STANDARD, bg_Color, SwingConstants.CENTER);
			tableValues[i][9] = new ColorLabelEntry("", ColorLabelEntry.FG_STANDARD, bg_Color, SwingConstants.CENTER);
			tableValues[i][10] = new ColorLabelEntry("", ColorLabelEntry.FG_STANDARD, bg_Color, SwingConstants.LEFT);
			tableValues[i][11] = new ColorLabelEntry("", ColorLabelEntry.FG_STANDARD, bg_Color, SwingConstants.CENTER);
			tableValues[i][12] = new ColorLabelEntry("", ColorLabelEntry.FG_STANDARD, bg_Color, SwingConstants.CENTER);
			tableValues[i][13] = new ColorLabelEntry("", ColorLabelEntry.FG_STANDARD, bg_Color, SwingConstants.CENTER);
			tableValues[i][14] = new ColorLabelEntry("", ColorLabelEntry.FG_STANDARD, bg_Color, SwingConstants.CENTER);
			tableValues[i][15] = new ColorLabelEntry("", ColorLabelEntry.FG_STANDARD, bg_Color, SwingConstants.CENTER);

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
				final Vector<SerieTableEntry> tabelleneintraege = this.model.getCurrentSeries()
						.getTable().getEntries();
				final int teamid = HOVerwaltung.instance().getModel().getBasics().getTeamId();
				int j;

				for (int i = 0; i < tabelleneintraege.size(); i++) {
					final SerieTableEntry entry = tabelleneintraege.get(i);

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
						((ColorLabelEntry) tableValues[j][7]).setText(entry.getGoalsFor() + "");
						((ColorLabelEntry) tableValues[j][8]).setText(entry.getGoalsAgainst() + "");
						((ColorLabelEntry) tableValues[j][9]).setSpecialNumber(entry.getGoalsDiff(), false);
						
						FormLabel formLabel = new FormLabel(entry.getSerie());
						formLabel.setBgColor(getColor4Row(j));
						tableValues[j][10] = formLabel;
						populateSerieTableStatistics(entry, j);
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


	private void populateSerieTableStatistics(SerieTableEntry entry, int iRow) {

		var leagueStatistics = model.getLeagueStatistics();
		var leagueStatisticsTeam = leagueStatistics.get(entry.getTeamId());

		if (leagueStatisticsTeam != null) {
			String statType = (String) m_jcbStatsAggType.getSelectedItem();
			((ColorLabelEntry) tableValues[iRow][11]).setText(getStatistcs(leagueStatisticsTeam, RatingsStatistics.POWER_RATINGS, statType).toString());
			((ColorLabelEntry) tableValues[iRow][12]).setText(getStatistcs(leagueStatisticsTeam, RatingsStatistics.HATSTATS_TOTAL, statType).toString());
			((ColorLabelEntry) tableValues[iRow][13]).setText(getStatistcs(leagueStatisticsTeam, RatingsStatistics.HATSTATS_DEF, statType).toString());
			((ColorLabelEntry) tableValues[iRow][14]).setText(getStatistcs(leagueStatisticsTeam, RatingsStatistics.HATSTATS_MID, statType).toString());
			((ColorLabelEntry) tableValues[iRow][15]).setText(getStatistcs(leagueStatisticsTeam, RatingsStatistics.HATSTATS_OFF, statType).toString());
		}
		else{
			((ColorLabelEntry) tableValues[iRow][11]).setText("");
			((ColorLabelEntry) tableValues[iRow][12]).setText("");
			((ColorLabelEntry) tableValues[iRow][13]).setText("");
			((ColorLabelEntry) tableValues[iRow][14]).setText("");
			((ColorLabelEntry) tableValues[iRow][15]).setText("");
		}
	}

	private Integer getStatistcs(Map<RatingsStatistics, Model.StatisticsEntry> leagueStatisticsTeam, RatingsStatistics key, String statType) {
		if ( leagueStatisticsTeam.containsKey(key)){
			var stat = leagueStatisticsTeam.get(key);
			if (statType.equalsIgnoreCase("max")){
				return stat.getMax();
			}
			return stat.getAverage();
		}
		return 0;
	}

	private void populateSerieTableStatsOnly() {
		try {
			if (this.model.getCurrentSeries() != null) {
				final Vector<SerieTableEntry> tableEntries = this.model.getCurrentSeries().getTable().getEntries();

				int j;

				for (int i = 0; i < tableEntries.size(); i++) {
					final SerieTableEntry entry = tableEntries.get(i);

					if (entry.getPoints() > -1) {
						populateSerieTableStatistics(entry, i + 1);
					}
				}

				seriesTable.setModel(new VAPTableModel(COLUMNNAMES, tableValues));
			}

			setTableColumnWidth();
		} catch (Exception e) {
			HOLogger.instance().log(getClass(), "Reinit Tabelle : " + e);
			HOLogger.instance().log(getClass(), e);
		}
	}
}
