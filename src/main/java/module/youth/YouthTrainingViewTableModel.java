package module.youth;

import core.gui.HOMainFrame;
import core.gui.comp.entry.ColorLabelEntry;
import core.gui.comp.entry.IHOTableEntry;
import core.gui.comp.table.HOTableModel;
import core.model.HOVerwaltung;
import core.module.IModule;

import javax.swing.*;
import java.util.List;
import java.util.stream.Collectors;


public class YouthTrainingViewTableModel extends HOTableModel {

    // TODO add training download when chpp api to trainings page is available

    private List<YouthTraining> youthTrainings;

    public YouthTrainingViewTableModel(int id) {
        super(id,"YouthTrainingView");
        columns =  initColumns();
    }

    private YouthTrainingColumn[] initColumns() {
        return new YouthTrainingColumn[]{
                new YouthTrainingColumn(0, "ls.youth.training.date", 0) {
                    @Override
                    public IHOTableEntry getTableEntry(YouthTraining youthTraining) {
                        return new ColorLabelEntry(time2Int(youthTraining.getMatchDate()), formatTime(youthTraining.getMatchDate()), ColorLabelEntry.FG_STANDARD, ColorLabelEntry.BG_STANDARD, SwingConstants.LEFT);
                    }
                },
                new YouthTrainingColumn(1, "ls.youth.training.hometeam") {
                    @Override
                    public IHOTableEntry getTableEntry(YouthTraining youthTraining) {
                        return new ColorLabelEntry(youthTraining.getHomeTeamName(), ColorLabelEntry.FG_STANDARD, ColorLabelEntry.BG_STANDARD, SwingConstants.LEFT);
                    }
                },
                new YouthTrainingColumn(2, "ls.youth.training.guestteam") {
                    @Override
                    public IHOTableEntry getTableEntry(YouthTraining youthTraining) {
                        return new ColorLabelEntry(youthTraining.getGuestTeamName(), ColorLabelEntry.FG_STANDARD, ColorLabelEntry.BG_STANDARD, SwingConstants.LEFT);
                    }
                },
                new YouthTrainingColumn(3, "ls.youth.training.primary"){
                    @Override
                    public IHOTableEntry getTableEntry(YouthTraining youthTraining){
                        return new YouthTrainingTableEntry(youthTraining.getTraining(YouthTraining.Priority.Primary));
                    }
                    @Override
                    public boolean isEditable(){return true;}
                },
                new YouthTrainingColumn(4, "ls.youth.training.secondary"){
                    @Override
                    public IHOTableEntry getTableEntry(YouthTraining youthTraining){
                        return new YouthTrainingTableEntry(youthTraining.getTraining(YouthTraining.Priority.Secondary));
                    }
                    @Override
                    public boolean isEditable(){return true;}
                },

                new YouthTrainingColumn(99, "ls.training.id", 0) {
                    @Override
                    public IHOTableEntry getTableEntry(YouthTraining youthTraining) {
                        return new ColorLabelEntry(youthTraining.getMatchId()+"", ColorLabelEntry.FG_STANDARD, ColorLabelEntry.BG_STANDARD, SwingConstants.LEFT);
                    }
                    @Override
                    public boolean isDisplay() {
                        return false;
                    }
                }
        };
    }

    @Override
    public boolean isCellEditable(int row, int column) {
        return columns[column].isEditable();
    }

    @Override
    protected void initData() {
        youthTrainings = HOVerwaltung.instance().getModel().getYouthTrainings()
                .stream()
                .sorted( (i1, i2) -> i2.getMatchDate().compareTo(i1.getMatchDate()))
                .collect(Collectors.toList());
        m_clData = new Object[youthTrainings.size()][columns.length];
        int rownum=0;
        for ( var youthTraining: youthTrainings ) {
            int columnnum=0;
            for (var col: columns){
                m_clData[rownum][columnnum] = ((YouthTrainingColumn)col).getTableEntry(youthTraining);
                columnnum++;
            }
            rownum++;
        }
        fireTableDataChanged();
    }

    public YouthTraining getYouthTraining(int row){
        return this.youthTrainings.get(row);
    }

    @Override
    public void setValueAt(Object value, int row, int column) {
        super.setValueAt(value, row, column);
        var t = this.getYouthTraining(row);
        var trainingType = ((YouthTrainingTableEntry)value).getTrainingType();
        switch (column) {
            case 3 -> setTraining(t, YouthTraining.Priority.Primary, trainingType);
            case 4 -> setTraining(t, YouthTraining.Priority.Secondary, trainingType);
        }
    }

    private void setTraining(YouthTraining t, YouthTraining.Priority prio, YouthTrainingType trainingType) {
        if ( t.getTraining(prio) != trainingType){

            t.setTraining(prio, trainingType);
            t.recalcSkills();
            t.store();

            var youthPanel = (YouthPanel) HOMainFrame.instance().getTabbedPane().getModulePanel(IModule.YOUTH);
            youthPanel.refreshYouthPlayerView();
        }
    }
}
