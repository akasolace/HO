package module.series;

import core.db.DBManager;
import core.gui.RefreshManager;
import core.gui.comp.panel.ImagePanel;
import core.gui.comp.panel.LazyImagePanel;
import core.gui.theme.HOColorName;
import core.gui.theme.HOIconName;
import core.gui.theme.ThemeManager;
import core.model.HOModel;
import core.model.HOVerwaltung;
import core.model.enums.MatchType;
import core.model.enums.RatingsStatistics;
import module.series.promotion.*;

import javax.swing.*;
import java.awt.*;
import java.util.HashMap;
import java.util.Map;


/**
 * Panel displaying the league table, as well as the series history graph.
 */
public class SeriesPanel extends LazyImagePanel {

	private JButton deleteButton;
	private JComboBox<Spielplan> seasonComboBox;
	private SeriesTablePanel seriesTable;
	private MatchDayPanel[] matchDayPanels;
	private SeriesHistoryPanel seriesHistoryPanel;
	private Model model;
	//private Map<Integer, Map<RatingsStatistics, Model.StatisticsEntry>> leagueStatistics;	// team id -> (statistic key -> value)
	private PromotionInfoPanel promotionInfoPanel;
	private LeaguePromotionHandler promotionHandler;
	private HOModel homodel;

	@Override
	protected void initialize() {
		initPromotionHandler();
	//	initLeagueStatsData();
		initComponents();
		fillSaisonCB();
		addListeners();
		registerRefreshable(true);
	}

	private void initPromotionHandler() {
		promotionHandler = new LeaguePromotionHandler();
		promotionInfoPanel = new PromotionInfoPanel(promotionHandler);
	}

	private void initLeagueStatsData() {
		/*homodel = HOVerwaltung.instance().getModel();

		int iSeason = homodel.getBasics().getSeason();
		int iMatchPlayedThisSeason = homodel.getLeague().getSpieltag();
		int iSerieID = HOVerwaltung.instance().getModel().getXtraDaten().getLeagueLevelUnitID();
		int iMatchRound;

		if (iMatchPlayedThisSeason == 0) {
			iSeason --;
			iMatchRound = 14;
		}
		else{
			iMatchRound = iMatchPlayedThisSeason;
		}
*/
		//leagueStatistics = DataDownloader.instance().fetchLeagueStatistics(iSerieID, iMatchRound, iSeason);

	}

	@Override
	protected void update() {
		fillSaisonCB();
	}

	private void delete() {
		if (seasonComboBox.getSelectedItem() != null) {
			Spielplan spielplan = (Spielplan) seasonComboBox.getSelectedItem();
			int value = JOptionPane.showConfirmDialog(this,
					HOVerwaltung.instance().getLanguageString("ls.button.delete") + " "
							+ HOVerwaltung.instance().getLanguageString("Ligatabelle") + ":\n"
							+ spielplan.toString(), HOVerwaltung.instance().getLanguageString("confirmation.title"), JOptionPane.YES_NO_OPTION);

			if (value == JOptionPane.YES_OPTION) {
				final String[] dbkey = { "Saison", "LigaID" };
				final String[] dbvalue = { spielplan.getSaison() + "", spielplan.getLigaId() + "" };

				DBManager.instance().deleteSpielplanTabelle(dbkey, dbvalue);
				DBManager.instance().deletePaarungTabelle(dbkey, dbvalue);
				this.model.setCurrentSeries(null);
				RefreshManager.instance().doReInit();
			}
		}
	}

	private void addListeners() {
		this.deleteButton.addActionListener(e -> delete());

		this.seasonComboBox.addActionListener(e -> {
			// Determine current match schedule
			if (seasonComboBox.getSelectedItem() instanceof Spielplan) {
				model.setCurrentSeries((Spielplan) seasonComboBox.getSelectedItem());
			} else {
				model.setCurrentSeries(null);
			}

			// Inform all panels
			informSaisonChange();
		});

		this.seriesTable.addListSelectionListener(e -> {
			if (!e.getValueIsAdjusting()) {
				teamSelectionChanged();
			}
		});
	}

	private void teamSelectionChanged() {
		if (this.model.getCurrentTeam() == null && seriesTable.getSelectedTeam() == null) {
			return;
		}

		if (this.model.getCurrentTeam() == null
				|| !this.model.getCurrentTeam().equals(seriesTable.getSelectedTeam())) {
			this.model.setCurrentTeam(seriesTable.getSelectedTeam());
			markierungInfo();
		}
	}

	private void fillSaisonCB() {
		// Get the match schedules as objects with the pairings
		final Spielplan[] spielplaene = DBManager.instance().getAllSpielplaene(true);
		final Spielplan markierterPlan = (Spielplan) seasonComboBox.getSelectedItem();

		// Remove all old seasons
		seasonComboBox.removeAllItems();

		// Fill new
		for (int i = 0; (spielplaene != null) && (i < spielplaene.length); i++) {
			seasonComboBox.addItem(spielplaene[i]);
		}

		//  Restore old marking
		seasonComboBox.setSelectedItem(markierterPlan);

		if ((seasonComboBox.getSelectedIndex() < 0) && (seasonComboBox.getItemCount() > 0)) {
			seasonComboBox.setSelectedIndex(0);
		}

		// Aktuellen Spielplan bestimmen
		if (seasonComboBox.getSelectedItem() instanceof Spielplan) {
			this.model.setCurrentSeries((Spielplan) seasonComboBox.getSelectedItem());
		} else {
			this.model.setCurrentSeries(null);
		}

		// Alle Panels informieren
		informSaisonChange();
	}

	private void informSaisonChange() {
		seriesTable.changeSaison();
		seriesHistoryPanel.changeSaison();
		markierungInfo();
	}

	private void initComponents() {
		this.model = new Model();
		//model.setLeagueStatistics(leagueStatistics);

		setLayout(new BorderLayout());

		// ComboBox für Saisonauswahl
		final JPanel panel = new ImagePanel(new BorderLayout());

		final JPanel toolbarPanel = new ImagePanel(null);
		seasonComboBox = new JComboBox();
		seasonComboBox.setToolTipText(HOVerwaltung.instance().getLanguageString(
				"tt_Ligatabelle_Saisonauswahl"));
		seasonComboBox.setSize(200, 25);
		seasonComboBox.setLocation(10, 5);
		toolbarPanel.add(seasonComboBox);

		deleteButton = new JButton(ThemeManager.getIcon(HOIconName.REMOVE));
		deleteButton.setToolTipText(HOVerwaltung.instance().getLanguageString(
				"tt_Ligatabelle_SaisonLoeschen"));
		deleteButton.setSize(25, 25);
		deleteButton.setLocation(220, 5);
		deleteButton.setBackground(ThemeManager.getColor(HOColorName.BUTTON_BG));
		toolbarPanel.add(deleteButton);

		promotionInfoPanel.setSize(650, 30);
		promotionInfoPanel.setLocation(290, 5);

		toolbarPanel.add(promotionInfoPanel);

		toolbarPanel.setPreferredSize(new Dimension(240, 35));
		panel.add(toolbarPanel, BorderLayout.NORTH);

		JPanel leagueStatsPanel = new ImagePanel(new GridLayout(1, 2));
		leagueStatsPanel.add(initLigaTabelle());
		leagueStatsPanel.add(initTabellenverlaufStatistik());

		final JPanel tablePanel = new ImagePanel(new BorderLayout());
		tablePanel.add(leagueStatsPanel, BorderLayout.NORTH);

		final JPanel historyPanel = new ImagePanel(new BorderLayout());
		historyPanel.add(initSpielPlan(), BorderLayout.CENTER);

		tablePanel.add(historyPanel, BorderLayout.CENTER);

		panel.add(tablePanel, BorderLayout.CENTER);

		add(panel, BorderLayout.CENTER);
	}

	private Component initLigaTabelle() {
		seriesTable = new SeriesTablePanel(this.model);

		JScrollPane scrollpane = new JScrollPane(seriesTable);
		scrollpane.getViewport().setScrollMode(JViewport.BACKINGSTORE_SCROLL_MODE);
		scrollpane.setPreferredSize(new Dimension((int) seriesTable.getPreferredSize().getWidth(),
				(int) seriesTable.getPreferredSize().getHeight() + 22));

		return scrollpane;
	}

	private Component initSpielPlan() {
		JLabel label;
		matchDayPanels = new MatchDayPanel[14];
		for (int i = 0; i < matchDayPanels.length; i++) {
			matchDayPanels[i] = new MatchDayPanel(this.model, i + 1);
		}

		final GridBagLayout layout = new GridBagLayout();
		final GridBagConstraints constraints = new GridBagConstraints();
		constraints.fill = GridBagConstraints.NONE;
		constraints.weightx = 1.0;
		constraints.weighty = 1.0;
		constraints.gridy = 0;
		constraints.insets = new Insets(4, 4, 4, 4);

		final JPanel panel = new ImagePanel(layout);

		label = new JLabel();
		constraints.gridx = 0;
		constraints.gridy = 0;
		constraints.gridheight = 7;
		constraints.fill = GridBagConstraints.HORIZONTAL;
		layout.setConstraints(label, constraints);
		panel.add(label);

		for (int i = 0; i < 7; i++) {
			constraints.gridx = 1;
			constraints.gridy = i;
			constraints.gridheight = 1;
			layout.setConstraints(matchDayPanels[i], constraints);
			panel.add(matchDayPanels[i]);
		}

		label = new JLabel();
		constraints.gridx = 2;
		constraints.gridy = 0;
		constraints.gridheight = 7;
		constraints.fill = GridBagConstraints.HORIZONTAL;
		layout.setConstraints(label, constraints);
		panel.add(label);

		for (int i = 7; i < matchDayPanels.length; i++) {
			constraints.gridx = 3;
			constraints.gridy = i - 7;
			constraints.gridheight = 1;
			layout.setConstraints(matchDayPanels[i], constraints);
			panel.add(matchDayPanels[i]);
		}

		label = new JLabel();
		constraints.gridx = 4;
		constraints.gridy = 0;
		constraints.gridheight = 7;
		constraints.fill = GridBagConstraints.HORIZONTAL;
		layout.setConstraints(label, constraints);
		panel.add(label);

		final JScrollPane scrollpane = new JScrollPane(panel);
		scrollpane.getVerticalScrollBar().setBlockIncrement(100);
		scrollpane.getVerticalScrollBar().setUnitIncrement(20);
		scrollpane.getViewport().setScrollMode(JViewport.BACKINGSTORE_SCROLL_MODE);
		return scrollpane;
	}

	private Component initTabellenverlaufStatistik() {
		seriesHistoryPanel = new SeriesHistoryPanel(this.model);

		final JPanel panel = new ImagePanel();
		panel.add(seriesHistoryPanel);

		final JScrollPane scrollpane = new JScrollPane(panel);
		scrollpane.getViewport().setScrollMode(JViewport.BACKINGSTORE_SCROLL_MODE);
		scrollpane.setPreferredSize(new Dimension((int) seriesTable.getPreferredSize().getWidth(),
				(int) seriesTable.getPreferredSize().getHeight()));

		return scrollpane;
	}

	private void markierungInfo() {
		for (MatchDayPanel matchDayPanel : matchDayPanels) {
			matchDayPanel.changeSeason();
		}
	}

}
