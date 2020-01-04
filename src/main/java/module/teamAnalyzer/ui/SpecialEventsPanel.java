package module.teamAnalyzer.ui;

import core.gui.model.BaseTableModel;
import core.model.player.IMatchRoleID;
import core.model.player.Player;
import core.specialevents.SpecialEventsPrediction;
import core.specialevents.SpecialEventsPredictionManager;
import core.util.Helper;
import module.teamAnalyzer.vo.TeamLineup;

import javax.swing.*;
import java.awt.*;
import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Vector;


public class SpecialEventsPanel extends JPanel {
    private JTable table;
    private BaseTableModel tableModel;
    private JLabel resultLabel;

    private String[] columns = {
            "Kind",
            "Player",
            "Opponent Player",
            "Involved Player",
            "Prob.",
            "Scores",
            "Opponent Scores"
    };



    public SpecialEventsPanel(){
        Vector<Object> data = new Vector<Object>();

        tableModel = new BaseTableModel(data, new Vector<String>(Arrays.asList(columns)));
        table = new JTable(tableModel);

        //table.setDefaultRenderer(Object.class, new RatingTableCellRenderer());

        setLayout(new BorderLayout());

        JScrollPane scrollPane = new JScrollPane(table);

        scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);

        JLabel start = new JLabel("Special Events");
        add(start, BorderLayout.PAGE_START);
        add(scrollPane);

        resultLabel = new JLabel( "Result: 0.00 - 0.00");
        add(resultLabel, BorderLayout.PAGE_END);

    }

    public void reload(TeamLineup teamLineup) {
        if ( teamLineup==null) return;
        SpecialEventsPredictionManager specialEventsPredictionManager = teamLineup.getSpecialEventsPrediction();
        if (specialEventsPredictionManager == null) return;

        tableModel = new BaseTableModel(new Vector<Object>(), new Vector<String>(Arrays.asList(columns)));
        table.setModel(tableModel);

        List<SpecialEventsPrediction> teamEvents = specialEventsPredictionManager.getTeamEvents();
        HashSet<IMatchRoleID> involvedPositions;
        for ( SpecialEventsPrediction se : teamEvents){
            HashSet<Player> involved = new HashSet<Player>();
            involvedPositions = se.getInvolvedPositions();
            if ( involvedPositions != null) {
                for (IMatchRoleID id : involvedPositions) {
                    involved.add(specialEventsPredictionManager.getPlayer(id));
                }
            }
            involvedPositions = se.getInvolvedOpponentPositions();
            if ( involvedPositions != null){
                for ( IMatchRoleID id: involvedPositions){
                    involved.add(specialEventsPredictionManager.getOpponentPlayer(id));
                }
            }

            tableModel.addRow(
                    getRow(
                            se.getEventTypeAsString(),
                            specialEventsPredictionManager.getPlayer(se.getResponsiblePosition()),
                            null,
                            involved,
                            se.getChanceCreationProbability(),
                            se.getChanceCreationProbability()>0?se.getGoalProbability():null,
                            se.getChanceCreationProbability()>0?null:-se.getGoalProbability()
                    )
            );
        }

        List<SpecialEventsPrediction> opponentEvents = specialEventsPredictionManager.getOpponentEvents();
        for ( SpecialEventsPrediction se : opponentEvents){
            HashSet<Player> involved = new HashSet<Player>();

            involvedPositions = se.getInvolvedPositions();
            if ( involvedPositions != null) {
                for (IMatchRoleID id : involvedPositions) {
                    involved.add(specialEventsPredictionManager.getOpponentPlayer(id));     // SE from opponent perspective
                }
            }
            involvedPositions = se.getInvolvedOpponentPositions();
            if ( involvedPositions  != null) {
                for (IMatchRoleID id : involvedPositions) {
                    involved.add(specialEventsPredictionManager.getPlayer(id));     // SE from opponent perspective
                }
            }

            tableModel.addRow(
                    getRow(
                            se.getEventTypeAsString(),
                            null,
                            specialEventsPredictionManager.getOpponentPlayer(se.getResponsiblePosition()),
                            involved,
                            se.getChanceCreationProbability(),
                            se.getChanceCreationProbability()>0?null:-se.getGoalProbability(),
                            se.getChanceCreationProbability()>0?se.getGoalProbability():null
                    )
            );
        }

        double scores = specialEventsPredictionManager.getResultScores();
        double opponentScores = specialEventsPredictionManager.getOpponentResultScores();
        this.resultLabel.setText(String.format("Result: %.2f : %.2f", scores, opponentScores));
    }

    private Vector<Object> getRow(String kind, Player player, Player opponentPlayer, HashSet<Player> involved, double probability, Double scores, Double scoresOpponent) {

        Vector<String> involvedPlayerNames = new Vector<>();
        for ( Player p : involved){
            involvedPlayerNames.add(p.getName());
        }
        Vector<Object> rowData = new Vector<Object>();

        rowData.add(kind);
        rowData.add(player!=null?player.getName():"");
        rowData.add(opponentPlayer!=null?opponentPlayer.getName():"");
        rowData.add(involvedPlayerNames);

        DecimalFormat df = new DecimalFormat("#.00");
        rowData.add(df.format(probability));
        rowData.add(scores!=null?df.format(scores):"");
        rowData.add(scoresOpponent!=null?df.format(scoresOpponent):"");

        return rowData;
    }

}
