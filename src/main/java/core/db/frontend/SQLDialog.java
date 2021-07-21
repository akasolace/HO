package core.db.frontend;

import core.db.DBManager;
import core.gui.HOMainFrame;
import core.gui.theme.HOIconName;
import core.gui.theme.ThemeManager;
import core.util.BrowserLauncher;
import core.util.HOLogger;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GraphicsEnvironment;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.util.ArrayList;
import java.util.regex.Pattern;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.JTextPane;
import javax.swing.JToolBar;
import javax.swing.JTree;


public class SQLDialog extends JDialog implements ActionListener {
	
	private static final long serialVersionUID = 7727562652486277083L;
    private JTable table;
    private JTextPane txtArea;
    private JLabel lbl;
    protected JTree tree;
    private String columnNames[];
    protected ArrayList<String> statements;
    private int index;
    boolean CRState;
    private final JButton butBook;
    private final JButton butExecute;
    private final JButton butprevious;
    private final JButton butnext;
    private final JButton buthelp;
    private final JButton buttables;
    
    public SQLDialog() {
        super(HOMainFrame.instance(), "Simple SQL Editor");
        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        statements = new ArrayList<String>();
        CRState = false;
        butBook = new JButton("History");
        butExecute = new JButton(ThemeManager.getIcon(HOIconName.TOOTHEDWHEEL));
        butprevious = new JButton(" < ");
        butnext = new JButton(" > ");
        buthelp = new JButton("HSQL Website");
        buttables = new JButton(ThemeManager.getIcon(HOIconName.INFO));
        initialize();
    }

    private void initialize() {
        int width = (int)GraphicsEnvironment.getLocalGraphicsEnvironment().getMaximumWindowBounds().getWidth();
        int heigth = (int)GraphicsEnvironment.getLocalGraphicsEnvironment().getMaximumWindowBounds().getHeight();
        setLocation((width - 800) / 2, (heigth - 480) / 2);
        setSize(800, 480);
        getContentPane().setLayout(new BorderLayout());
        JToolBar toolBar = new JToolBar("Toolbar");
        addButtons(toolBar);
        getContentPane().add(toolBar, "North");
        getContentPane().add(getMiddlePanel(), "Center");
        getContentPane().add(getInfoLabel(), "South");
    }

    private JLabel getInfoLabel() {
        if(lbl == null) {
            lbl = new JLabel();
            lbl.setPreferredSize(new Dimension(0, 20));
        }
        return lbl;
    }

    private JSplitPane getMiddlePanel() {
        JSplitPane split = new JSplitPane(0);
        split.add(new JScrollPane(getTextArea()));
        split.add(new JScrollPane(getTable()));
        return split;
    }

    private void addButtons(JToolBar toolbar) {
        initializeButton(toolbar, buttables, "F1 - shows all tables");
        initializeButton(toolbar, butprevious, "F2 - previous statement");
        initializeButton(toolbar, butBook, "F3 - show all statements");
        initializeButton(toolbar, butnext, "F4 - next statement");
        initializeButton(toolbar, butExecute, "F5 - execute the statement");
        initializeButton(toolbar, buthelp, "HSQL Doc");
    }

    private void initializeButton(JToolBar toolbar, JButton button, String tooltip) {
        button.addActionListener(this);
        button.setToolTipText(tooltip);
        //button.setPreferredSize(BUTTON_SIZE);
        button.setBackground(Color.WHITE);
        toolbar.add(button);
    }

    protected void openHSQLDoc() {
        try {
			BrowserLauncher.openURL("http://hsqldb.sourceforge.net/web/hsqlDocsFrame.html");
		} catch (Exception ex) {
			HOLogger.instance().log(SQLDialog.class, ex);
		}
    }

    protected void showAllStatements() {
        Object tmp[] = statements.toArray();
        String display[] = new String[tmp.length];
        for(int i = 0; i < tmp.length; i++)
        {
            display[i] = tmp[i].toString();
            if(display[i].length() > 30)
                display[i] = display[i].substring(0, 30) + "...";
        }

        JList list = new JList(display);
        list.addMouseListener(new MouseAdapter() {

            @Override
			public void mouseClicked(MouseEvent e) {
                int i = ((JList)e.getSource()).getSelectedIndex();
                getTextArea().setText(statements.toArray()[i].toString());
            }
        });
        JScrollPane scroll = new JScrollPane(list);
        JOptionPane.showMessageDialog(null, scroll);
    }

    protected JTextPane getTextArea() {
        if(txtArea == null) {
            txtArea = new JTextPane();
            txtArea.setCaretPosition(0);
            txtArea.addKeyListener(new HighlightingKeyListener(txtArea));
            txtArea.addKeyListener(new KeyAdapter() {

                @Override
				public void keyPressed(KeyEvent e)
                {
                    if(112 == e.getKeyCode())
                        openTablesDialog();
                    if(113 == e.getKeyCode())
                        back();
                    if(114 == e.getKeyCode())
                        showAllStatements();
                    if(115 == e.getKeyCode())
                        forward();
                    if(116 == e.getKeyCode())
                        refresh();
                }

            });
        }
        return txtArea;
    }

    private JScrollPane getTable() {
        DummyTableModel model = new DummyTableModel(null, null);
        table = new JTable(model);
        table.setAutoResizeMode(0);
        JScrollPane scroll = new JScrollPane(table);
        scroll.setPreferredSize(new Dimension(0, 150));
        return scroll;
    }

    public void refresh() {
        String txt = getTextArea().getText().toUpperCase();
        var pattern = Pattern.compile("SELECT .* FROM ");
        var matcher = pattern.matcher(txt);
        if(matcher.find()){
            DummyTableModel model = new DummyTableModel(getValues(), columnNames);
            table.setModel(model);
        } else {
            try {
                int rows = DBManager.instance().getAdapter().executeUpdate(getTextArea().getText());
                getInfoLabel().setText(rows + " rows updated");
            }
            catch(Exception ex)
            {
                handleException(ex, "Statement wrong! ");
            }
        }
        statements.add(getTextArea().getText());
        index++;
    }

    private void handleException(Exception ex, String itxt) {
        ex.printStackTrace();
    }

    private Object[][] getValues() {
        Object values[][] = (Object[][])null;
        int rowCount = 0;
        String txt = getTextArea().getText().toUpperCase();
        int index1 = txt.indexOf("FROM");
        String select = txt.substring(0, index1 - 1);
        String sql = txt.substring(index1, getTextArea().getText().length());
        try
        {
            long start = System.currentTimeMillis();
            ResultSet rs = DBManager.instance().getAdapter().executeQuery(select + " " + sql);
            rs.last();
            rowCount = rs.getRow();
            rs.beforeFirst();
            ResultSetMetaData metaData = rs.getMetaData();
            values = new Object[rowCount][metaData.getColumnCount()];
            columnNames = new String[metaData.getColumnCount()];
            for(int i = 0; i < columnNames.length; i++)
                columnNames[i] = metaData.getColumnName(i + 1);

            for(int i = 0; rs.next(); i++) {
                for(int j = 0; j < columnNames.length; j++) {
                    String content = rs.getString(j + 1);
                    values[i][j] = content != null ? ((Object) (content)) : "(null)";
                }

            }

            if(rs != null)
                rs.close();
            long ergebnis = System.currentTimeMillis() - start;
            getInfoLabel().setText(rowCount + " rows (" + ergebnis + " ms)");
        }
        catch(Exception ex)
        {
            JOptionPane.showMessageDialog(null, "SQL Statement is wrong!");
        }
        return values;
    }

    protected void forward() {
        if(index > 0 && index < statements.size()) {
            getTextArea().setText(statements.get(index).toString());
            index++;
        }
    }

    protected void back() {
        if(index > 1) {
            getTextArea().setText(statements.get(index - 2).toString());
            index--;
        }
    }

    @Override
	public void actionPerformed(ActionEvent arg0) {
        if(arg0.getSource() == butBook)
            showAllStatements();
        if(arg0.getSource() == butExecute)
            refresh();
        if(arg0.getSource() == butprevious)
            back();
        if(arg0.getSource() == butnext)
            forward();
        if(arg0.getSource() == buthelp)
            openHSQLDoc();
        if(arg0.getSource() == buttables)
            openTablesDialog();
    }

    private void openTablesDialog() {
        TablesDialog dialog = new TablesDialog(this);
        dialog.setVisible(true);
    }




}