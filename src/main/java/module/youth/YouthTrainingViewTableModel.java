package module.youth;

import core.gui.comp.entry.ColorLabelEntry;
import core.gui.comp.entry.IHOTableEntry;
import core.gui.comp.table.HOTableModel;
import core.model.HOVerwaltung;
import javax.swing.*;
import javax.swing.event.TableModelEvent;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.stream.Collectors;


public class YouthTrainingViewTableModel extends HOTableModel {

    private List<YouthTraining> youthTrainings;

    public YouthTrainingViewTableModel(int id) {
        super(id,"YouthTrainingView");
        columns =  initColumns();
    }

    private YouthTrainingColumn[] initColumns() {
        return new YouthTrainingColumn[]{
                // TODO include match type icon in first column
                new YouthTrainingColumn(0, "ls.training.date", 0) {
                    @Override
                    public IHOTableEntry getTableEntry(YouthTraining youthTraining) {
                        return new ColorLabelEntry(new SimpleDateFormat("yyyy-MM-dd hh:mm").format(youthTraining.getMatchDate()), ColorLabelEntry.FG_STANDARD, ColorLabelEntry.BG_STANDARD, SwingConstants.LEFT);
                    }
                },
                new YouthTrainingColumn(1, "ls.training.hometeam") {
                    @Override
                    public IHOTableEntry getTableEntry(YouthTraining youthTraining) {
                        return new ColorLabelEntry(youthTraining.getHomeTeamName(), ColorLabelEntry.FG_STANDARD, ColorLabelEntry.BG_STANDARD, SwingConstants.LEFT);
                    }
                },
                new YouthTrainingColumn(2, "ls.training.guestteam") {
                    @Override
                    public IHOTableEntry getTableEntry(YouthTraining youthTraining) {
                        return new ColorLabelEntry(youthTraining.getGuestTeamName(), ColorLabelEntry.FG_STANDARD, ColorLabelEntry.BG_STANDARD, SwingConstants.LEFT);
                    }
                },
                new YouthTrainingColumn(3, "ls.training.primary"){
                    @Override
                    public IHOTableEntry getTableEntry(YouthTraining youthTraining){
                        return new YouthTrainingTableEntry(youthTraining.getTraining(YouthTraining.Priority.Primary));
                    }
                    @Override
                    public boolean isEditable(){return true;}
                },
                new YouthTrainingColumn(4, "ls.training.secondary"){
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
            case 3 -> t.setTraining(YouthTraining.Priority.Primary, trainingType);
            case 4 -> t.setTraining(YouthTraining.Priority.Secondary, trainingType);
        }
    }
}