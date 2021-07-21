package module.series;

import core.db.DBManager;
import core.gui.HOMainFrame;
import core.gui.RefreshManager;
import core.gui.comp.renderer.HODefaultTableCellRenderer;
import core.gui.theme.HOColorName;
import core.gui.theme.ImageUtilities;
import core.model.HOVerwaltung;
import core.model.enums.MatchType;
import core.model.series.Paarung;
import core.net.OnlineWorker;
import core.util.HOLogger;
import core.util.Helper;
import core.util.StringUtils;
import org.jetbrains.annotations.Nullable;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.text.DateFormat;
import java.util.Date;
import java.util.List;

import static core.gui.theme.ThemeManager.getColor;


/**
 * Display a game day.
 */
final class MatchDayPanel extends JPanel implements ActionListener {

    private static final long serialVersionUID = 6884532906036202996L;
    static final int NAECHSTER_SPIELTAG = -2;
    static final int LETZTER_SPIELTAG = -1;

    private final JButton[] buttons = new JButton[4];
    private final JLabel[] homeTeams = new JLabel[4];
    private final JLabel[] visitorTeams = new JLabel[4];
    private final JLabel[] results = new JLabel[4];
    private int iMatchRound;
    private static final Color foreground = getColor(HOColorName.LABEL_FG);
    private final Model model;

    protected MatchDayPanel(Model model, int _iMatchRound) {
        this.model = model;
        iMatchRound = _iMatchRound;
        initComponents();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        // Show the match
        int[] matchDatas;

        try {
            // Match ID, HomeTeam ID, GuestTeam ID, Home Goals, Guest Goals
            matchDatas = Helper.generateIntArray(e.getActionCommand());
         }
        catch (Exception ex) {
            HOLogger.instance().log(getClass(),  "MatchdayPanel: Action event could not be parsed:: "
                            + e.getActionCommand() + " / " + ex);
            return;
        }

        // --Match zeigen ggf runterladen--
        if (matchDatas[0] > 0) {
            // Spiel nicht vorhanden, dann erst runterladen!
            if (!DBManager.instance().isMatchInDB(matchDatas[0], MatchType.LEAGUE)) {

                OnlineWorker.downloadMatchData(matchDatas[0], MatchType.LEAGUE, // not
                        // tournament
                        false);
                fillLabels();
                RefreshManager.instance().doReInit();

            } else {
                // Match zeigen
                HOMainFrame.instance().showMatch(matchDatas[0]);
            }
        }
    }

    protected void changeSeason() {
        fillLabels();
    }

    private void setConstraintsValues(GridBagConstraints constraints, int fillValue,
                                      double weightxValue, int gridxValue, int gridyValue, int gridWithValue) {
        constraints.fill = fillValue;
        constraints.weightx = weightxValue;
        constraints.gridx = gridxValue;
        constraints.gridy = gridyValue;
        constraints.gridwidth = gridWithValue;
    }

    private void setMatchButton(JButton button, @Nullable Paarung paarung) {
        long gameFinishTime;
        long nowTime = (new Date()).getTime();

        boolean gameFinished; // Hat das Spiel schon stattgefunden
        if (paarung != null) {
            gameFinishTime = paarung.getDatum().getTime();
            gameFinishTime = gameFinishTime + 3 * 60 * 60 * 1000L; //assuming 3 hours to make sure the game is finished
            // if paarung was not updated regularly, it could happen that hatStattgefunden would fail
            gameFinished = paarung.isGameOver() || gameFinishTime < nowTime;
        }
        else {
            gameFinished = false;
        }

        if ( gameFinished ) {
            //Match already in the database
            if (DBManager.instance().isMatchInDB(paarung.getMatchId(), MatchType.LEAGUE)) {
                button.setToolTipText(HOVerwaltung.instance().getLanguageString("tt_Ligatabelle_SpielAnzeigen"));
                button.setEnabled(true);
                button.setIcon(ImageUtilities.getRightArrowIcon(getColor(HOColorName.SHOW_MATCH), 14, 14));
            }
            // Match not yet in the database
            else {
                button.setToolTipText(HOVerwaltung.instance().getLanguageString(
                        "tt_Ligatabelle_SpielDownloaden"));
                button.setEnabled(true);
                button.setIcon(ImageUtilities.getDownloadIcon(getColor(HOColorName.DOWNLOAD_MATCH), 14, 14));
            }
        }
        // Match has not taken place yet
        else {
            button.setToolTipText(HOVerwaltung.instance().getLanguageString(
                    "tt_Ligatabelle_SpielNochnichtgespielt"));
            button.setEnabled(false);
            button.setIcon(ImageUtilities.getUnavailableIcon(getColor(HOColorName.DOWNLOAD_MATCH), 14, 14));
        }
        button.setBackground(getColor(HOColorName.LEAGUE_PANEL_BG));
    }

    private void fillLabels() {
        int spieltag = iMatchRound;
        final int myTeamID = HOVerwaltung.instance().getModel().getBasics().getTeamId();
        final String myTeamName = HOVerwaltung.instance().getModel().getBasics().getTeamName();


        if (this.model.getCurrentSeries() == null) {
            for (JButton button : buttons) setMatchButton(button, null);
            return;
        }

        // Letzte Spieltag
        if (spieltag == LETZTER_SPIELTAG) {
            spieltag = HOVerwaltung.instance().getModel().getBasics().getSpieltag() - 1;

            if (spieltag <= 0) {
                spieltag = 1;
            }
        }
        // Nächste Spieltag
        else if (spieltag == NAECHSTER_SPIELTAG) {
            spieltag = HOVerwaltung.instance().getModel().getBasics().getSpieltag();

            if (spieltag > 14) {
                spieltag = 14;
            }

        }

       List<Paarung> paarungen = this.model.getCurrentSeries().getPaarungenBySpieltag(spieltag);

        String bordertext = HOVerwaltung.instance().getLanguageString("Spieltag") + " " + spieltag;

        if (paarungen != null && paarungen.size() > 0) {
            try {
                bordertext += ("  ( "
                        + DateFormat.getDateTimeInstance().format(
                        ((Paarung) paarungen.get(0)).getDatum()) + " )");
            } catch (Exception e) {
                bordertext += ("  ( " + ((Paarung) paarungen.get(0)).getStringDate() + " )");
            }
        }

        setBorder(BorderFactory.createTitledBorder(bordertext));

        // Update panel when pairings are found
        if ((paarungen != null) && (paarungen.size() == 4)) {
            resetAllLabelsFormat();

            // Erste Paarung------------------------------------------

            for (int i = 0; i < buttons.length; i++) {
                Paarung paarung = paarungen.get(i);
                buttons[i].setActionCommand(paarung.getMatchId() + "," + paarung.getHeimId() + ","
                        + paarung.getGastId() + "," + paarung.getToreHeim() + ","
                        + paarung.getToreGast());
                setMatchButton(buttons[i], paarung);
                fillRow(homeTeams[i], visitorTeams[i], results[i], paarung, myTeamID, myTeamName);
            }
        }
        // Keine Paarungen
        else {
            for (JButton button : buttons) setMatchButton(button, null);
        }
    }

    private void fillRow(JLabel homeTeam, JLabel visitorTeam, JLabel result, Paarung paarung,
                         int myTeamID, String myTeamName) {
        homeTeam.setText(paarung.getHeimName());
        visitorTeam.setText(paarung.getGastName());
        homeTeam.setBackground(getColor(HOColorName.LEAGUE_PANEL_BG));
        visitorTeam.setBackground(getColor(HOColorName.LEAGUE_PANEL_BG));
        result.setBackground(getColor(HOColorName.LEAGUE_PANEL_BG));
        if ((paarung.getToreHeim() > -1) && (paarung.getToreGast() > -1)) {
            result.setText(StringUtils.getResultString(paarung.getToreHeim(), paarung.getToreGast(), ""));

            // HomeVictory
            if (paarung.getToreHeim() > paarung.getToreGast()) {
                homeTeam.setFont(homeTeam.getFont().deriveFont(Font.BOLD));
                homeTeam.setForeground(HODefaultTableCellRenderer.SELECTION_FG);
                if (paarung.getHeimId() == myTeamID){
                    result.setForeground(getColor(HOColorName.TABLEENTRY_IMPROVEMENT_FG));
                    result.setFont(homeTeam.getFont().deriveFont(Font.BOLD));
                }
                else if (paarung.getGastId() == myTeamID){
                    result.setForeground(getColor(HOColorName.TABLEENTRY_DECLINE_FG));
                    result.setFont(homeTeam.getFont().deriveFont(Font.BOLD));
                }


            }
            // Visitor Victory
            else if (paarung.getToreHeim() < paarung.getToreGast()) {
                visitorTeam.setFont(homeTeam.getFont().deriveFont(Font.BOLD));
                visitorTeam.setForeground(HODefaultTableCellRenderer.SELECTION_FG);
                if (paarung.getHeimId() == myTeamID){
                    result.setForeground(getColor(HOColorName.TABLEENTRY_DECLINE_FG));
                    result.setFont(homeTeam.getFont().deriveFont(Font.BOLD));
                }
                else if (paarung.getGastId() == myTeamID){
                    result.setForeground(getColor(HOColorName.TABLEENTRY_IMPROVEMENT_FG));
                    result.setFont(homeTeam.getFont().deriveFont(Font.BOLD));
                }
            }
        } else {
            result.setText(StringUtils.getResultString(-1, -1, ""));
        }

        markMyTeam(homeTeam, paarung.getHeimId(), myTeamID);
        markMyTeam(visitorTeam, paarung.getGastId(), myTeamID);
        markSelectedTeam(homeTeam, paarung.getHeimName(), myTeamName);
        markSelectedTeam(visitorTeam, paarung.getGastName(), myTeamName);
    }

    private void initButton(JButton button, GridBagConstraints constraints, GridBagLayout layout,
                            int row) {
        setConstraintsValues(constraints, GridBagConstraints.NONE, 0.0, 6, row, 1);

        button.setBackground(getColor(HOColorName.LEAGUE_PANEL_BG));
        button.addActionListener(this);
        layout.setConstraints(button, constraints);
        add(button);
    }

    private void initColon(GridBagConstraints constraints, GridBagLayout layout, int row) {
        JLabel label = new JLabel(":");
        label.setHorizontalAlignment(SwingConstants.CENTER);
        setConstraintsValues(constraints, GridBagConstraints.NONE, 0.0, 1, row, 1);
        layout.setConstraints(label, constraints);
        add(label);
    }

    private void initComponents() {
        for (int i = 0; i < buttons.length; i++) {
            buttons[i] = new JButton();
            homeTeams[i] = new JLabel();
            visitorTeams[i] = new JLabel();
            results[i] = new JLabel();
        }
        final GridBagLayout layout = new GridBagLayout();
        final GridBagConstraints constraints = new GridBagConstraints();
        constraints.anchor = GridBagConstraints.CENTER;
        constraints.weighty = 0.0;
        constraints.insets = new Insets(2, 2, 2, 2);

        this.setBackground(getColor(HOColorName.LEAGUE_PANEL_BG));

        JLabel label;

        setLayout(layout);

        label = new JLabel(HOVerwaltung.instance().getLanguageString("Heim"));
        label.setFont(label.getFont().deriveFont(Font.BOLD));
        label.setHorizontalAlignment(SwingConstants.CENTER);
        setConstraintsValues(constraints, GridBagConstraints.HORIZONTAL, 1.0, 0, 0, 1);
        layout.setConstraints(label, constraints);
        add(label);

        label = new JLabel();
        label.setHorizontalAlignment(SwingConstants.CENTER);
        setConstraintsValues(constraints, GridBagConstraints.HORIZONTAL, 0.0, 1, 0, 1);
        layout.setConstraints(label, constraints);
        add(label);

        label = new JLabel(HOVerwaltung.instance().getLanguageString("Gast"));
        label.setFont(label.getFont().deriveFont(Font.BOLD));
        label.setHorizontalAlignment(SwingConstants.CENTER);
        setConstraintsValues(constraints, GridBagConstraints.HORIZONTAL, 1.0, 2, 0, 1);
        layout.setConstraints(label, constraints);
        add(label);

        label = new JLabel(HOVerwaltung.instance().getLanguageString("ls.match.result"));
        label.setFont(label.getFont().deriveFont(Font.BOLD));
        label.setHorizontalAlignment(SwingConstants.CENTER);
        setConstraintsValues(constraints, GridBagConstraints.NONE, 0.5, 3, 0, 3);
        layout.setConstraints(label, constraints);
        add(label);

        label = new JLabel("");

        // label.setFont( label.getFont().deriveFont( Font.BOLD ) );
        label.setHorizontalAlignment(SwingConstants.CENTER);
        setConstraintsValues(constraints, GridBagConstraints.NONE, 0.0, 4, 0, 1);
        layout.setConstraints(label, constraints);
        add(label);

        for (int j = 0; j < buttons.length; j++) {
            initTeam(homeTeams[j], constraints, layout, j + 1, 0);
            initColon(constraints, layout, j + 1);
            initTeam(visitorTeams[j], constraints, layout, j + 1, 2);
            initResultLabel(results[j], constraints, layout, j + 1);
            initButton(buttons[j], constraints, layout, j + 1);
        }

        fillLabels();
    }

    private void initResultLabel(JLabel label, GridBagConstraints constraints, GridBagLayout layout, int row) {
        setConstraintsValues(constraints, GridBagConstraints.NONE, 0.5, 3, row, 3);
        label.setHorizontalAlignment(SwingConstants.RIGHT);
        layout.setConstraints(label, constraints);
        add(label);
    }

    private void initTeam(JLabel label, GridBagConstraints constraints, GridBagLayout layout,
                          int row, int column) {
        label.setHorizontalAlignment(SwingConstants.CENTER);
        setConstraintsValues(constraints, GridBagConstraints.HORIZONTAL, 1.0, column, row, 1);
        layout.setConstraints(label, constraints);
        add(label);
    }

    private void markMyTeam(JLabel team, int teamID, int myTeamId) {
        if (teamID == myTeamId) {
            team.setFont(team.getFont().deriveFont(Font.BOLD));
            team.setForeground(getColor(HOColorName.HOME_TEAM_FG));
        }
    }

    private void markSelectedTeam(JLabel team, String teamName, String myTeamName) {
        if (teamName.equals(this.model.getCurrentTeam())) {
            team.setFont(team.getFont().deriveFont(Font.BOLD));
            if (teamName.equals(myTeamName)){
                team.setForeground(getColor(HOColorName.HOME_TEAM_FG));
            }
            else {
                team.setForeground(getColor(HOColorName.SELECTED_TEAM_FG));
            }
        }
    }

    private void resetAllLabelsFormat() {
        for (int i = 0; i < buttons.length; i++) {
            resetLabelFormat(homeTeams[i]);
            resetLabelFormat(visitorTeams[i]);
            resetLabelFormat(results[i]);
        }
    }

    private void resetLabelFormat(JLabel label) {
        label.setFont(label.getFont().deriveFont(Font.PLAIN));
        label.setForeground(foreground);
        label.setOpaque(true);
    }
}
