package module.youth;

import core.model.HOVerwaltung;
import core.module.config.ModuleConfig;

import javax.swing.*;
import java.awt.*;

public class YouthPanel extends JPanel {

    private YouthPlayerView youthPlayerView;
    private YouthTrainingView youthTrainingView;

    public YouthPanel() {
        setLayout(new BorderLayout());
        youthPlayerView = new YouthPlayerView();
        var tabbedPane = new JTabbedPane();
        tabbedPane.addTab(HOVerwaltung.instance().getLanguageString("ls.youth.player"), this.youthPlayerView);
        youthTrainingView = new YouthTrainingView();
        tabbedPane.addTab(HOVerwaltung.instance().getLanguageString("ls.youth.training"), this.youthTrainingView);
        add(tabbedPane, BorderLayout.CENTER);
    }

    public void storeUserSettings() {
        youthPlayerView.storeUserSettings();
        youthTrainingView.storeUserSettings();
    }

    public void refreshYouthPlayerView() {
        youthPlayerView.refresh();
    }
}
