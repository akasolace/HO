package tool.updater;

import java.awt.Dimension;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import core.gui.comp.HyperLinkLabel;
import core.model.HOVerwaltung;

public class UpdaterPanel extends JPanel {

	private static final long serialVersionUID = 1L;
	private String version;
	private String releaseNote;
	private String updateLink;
	
	public UpdaterPanel(String version, String releaseNote) {
		new UpdaterPanel(version,releaseNote, "");
	}

	public UpdaterPanel(String version, String releaseNote, String updateLink) {
		this.version = version;
		this.releaseNote = releaseNote;
		this.updateLink = updateLink;
		setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));
		initLayout();
	}
	
	private void initLayout() {
		initLabelVersion();
		initHiperLink();
	    add(Box.createRigidArea(new Dimension(10,0)));
	    initReleaseNotesPanel();
	}
	
	// Create Version panel
	private void initLabelVersion() {
		JPanel panel = new JPanel();
	    JLabel label = new JLabel(version);	    
		
		panel.setLayout(new BoxLayout(panel, BoxLayout.PAGE_AXIS));
		panel.setBorder(BorderFactory.createCompoundBorder());
	    panel.add(label);

		panel.add(Box.createRigidArea(new Dimension(0,10)));
	    add(panel);
	}

	// Create hiperlink
	private void initHiperLink() {
		if(!updateLink.equals("")) {
			JPanel panel = new JPanel();
			JLabel linkLabel = new HyperLinkLabel(updateLink, updateLink);

			panel.setLayout(new BoxLayout(panel, BoxLayout.PAGE_AXIS));
			panel.setBorder(BorderFactory.createCompoundBorder());
			panel.add(linkLabel);

			panel.add(Box.createRigidArea(new Dimension(0, 10)));
			add(panel);
		}
	}
	
	// Create Release Notes panel
	private void initReleaseNotesPanel() {
		JPanel panel = new JPanel();
		JTextArea txtArea  = new JTextArea(10, 40);
		JScrollPane scrollPane = new JScrollPane(txtArea);
				
		txtArea.setText(releaseNote);
		txtArea.setCaretPosition(0);
	    panel.setLayout(new BoxLayout(panel, BoxLayout.PAGE_AXIS));
	    panel.setBorder(BorderFactory.createCompoundBorder(
	    	BorderFactory.createTitledBorder(HOVerwaltung.instance().getLanguageString("ls.update.releasenote")),
	    	BorderFactory.createEmptyBorder(10,10,10,10)));
	    panel.add(scrollPane);
	    panel.add(Box.createRigidArea(new Dimension(0,10)));
	    add(panel);
	}
}
