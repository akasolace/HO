package module.youth;

import core.gui.HOMainFrame;
import core.gui.RefreshManager;
import core.gui.Refreshable;
import core.gui.comp.renderer.HODefaultTableCellRenderer;
import core.gui.model.UserColumnController;
import core.module.IModule;

import javax.swing.*;
import javax.swing.table.TableColumnModel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

public class YouthTrainingView extends JScrollPane implements Refreshable {

    private JTable table;
    private YouthTrainingViewTableModel tableModel;

    public YouthTrainingView() {
        table = new JTable();
        this.setViewportView(table);
        initModel();
        RefreshManager.instance().registerRefreshable(this);
        table.setDefaultRenderer(Object.class, new HODefaultTableCellRenderer());
    }

    private void initModel() {
        setOpaque(false);
        if (tableModel == null) {
            tableModel = UserColumnController.instance().getYouthTrainingViewColumnModel();
            table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
            table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
            table.setRowSelectionAllowed(true);

            table.setModel(tableModel);
            TableColumnModel tableColumnModel = table.getColumnModel();
            for (int i = 0; i < tableModel.getColumnCount(); i++) {
                tableColumnModel.getColumn(i).setIdentifier(i);
            }

            for ( var c : tableModel.getColumns()){
                if ( c.isEditable()){
                    var tablecol = table.getColumn(c.getIndex());
                    if ( tablecol != null ){
                        var cb = new JComboBox<>(new YouthTrainingTableEntry.ComboBoxModel());
                        var editor = new DefaultCellEditor(cb);
                        editor.addCellEditorListener(table);
                        tablecol.setCellEditor(editor);
                    }
                }
            }

            tableModel.restoreUserSettings(table);
        }
        tableModel.initData();
    }

    @Override
    public void refresh() {
        this.tableModel.initData();
        repaint();
    }

    @Override
    public void reInit() {
        initModel();
        repaint();
    }

    public void storeUserSettings() {
        this.tableModel.storeUserSettings(table);
    }
/*
    @Override
    public void actionPerformed(ActionEvent e) {
        var youthPanel = (YouthPanel) HOMainFrame.instance().getTabbedPane().getModulePanel(IModule.YOUTH);
        youthPanel.refreshYouthPlayerView();
    }*/
}
