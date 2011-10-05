package elw.dp.ui;

import javax.swing.*;
import java.awt.*;

public class DataPathForm {
    protected JTextPane problemTextPane;
    protected JTabbedPane strTabbedPane;
    protected JTextArea sourceTextArea;
    protected JComboBox testComboBox;
    protected JTabbedPane pclTabbedPane;
    protected JTextPane logTextPane;
    protected JButton sourceAssembleButton;
    protected JButton testRunButton;
    protected JButton testBatchButton;
    protected JTextArea testMemTextArea;
    protected JTextArea testRegsTextArea;
    protected JButton testAddCustomButton;
    protected JTextArea clipTextArea;
    protected JButton runStepButton;
    protected JButton runRunButton;
    protected JButton runResetButton;
    protected JTable runRegsTable;
    protected JTable runMemTable;
    protected JTable runInstructionsTable;
    protected JLabel sourceFeedbackLabel;
    protected JLabel runStatusLabel;
    protected JPanel rootPanel;
    protected JLabel testStatusLabel;
    protected JButton testStepButton;
    protected JButton sourceVerifyButton;
    protected JButton sourceSubmitButton;

    public JPanel getRootPanel() {
        return rootPanel;
    }

    public JButton getSourceVerifyButton() {
        return sourceVerifyButton;
    }

    public JButton getSourceSubmitButton() {
        return sourceSubmitButton;
    }

    public JButton getTestStepButton() {
        return testStepButton;
    }

    public JLabel getTestStatusLabel() {
        return testStatusLabel;
    }

    public JTextArea getClipTextArea() {
        return clipTextArea;
    }

    public JTextPane getLogTextPane() {
        return logTextPane;
    }

    public JTextPane getProblemTextPane() {
        return problemTextPane;
    }

    public JTable getRunInstructionsTable() {
        return runInstructionsTable;
    }

    public JTable getRunMemTable() {
        return runMemTable;
    }

    public JTable getRunRegsTable() {
        return runRegsTable;
    }

    public JButton getRunResetButton() {
        return runResetButton;
    }

    public JButton getRunRunButton() {
        return runRunButton;
    }

    public JLabel getRunStatusLabel() {
        return runStatusLabel;
    }

    public JButton getRunStepButton() {
        return runStepButton;
    }

    public JButton getSourceAssembleButton() {
        return sourceAssembleButton;
    }

    public JLabel getSourceFeedbackLabel() {
        return sourceFeedbackLabel;
    }

    public JTextArea getSourceTextArea() {
        return sourceTextArea;
    }

    public JTabbedPane getStrTabbedPane() {
        return strTabbedPane;
    }

    public JTabbedPane getPclTabbedPane() {
        return pclTabbedPane;
    }

    public JButton getTestAddCustomButton() {
        return testAddCustomButton;
    }

    public JComboBox getTestComboBox() {
        return testComboBox;
    }

    public JTextArea getTestMemTextArea() {
        return testMemTextArea;
    }

    public JTextArea getTestRegsTextArea() {
        return testRegsTextArea;
    }

    public JButton getTestRunButton() {
        return testRunButton;
    }

    public JButton getTestBatchButton() {
        return testBatchButton;
    }

    {
// GUI initializer generated by IntelliJ IDEA GUI Designer
// >>> IMPORTANT!! <<<
// DO NOT EDIT OR ADD ANY CODE HERE!
        $$$setupUI$$$();
    }

    /**
     * Method generated by IntelliJ IDEA GUI Designer
     * >>> IMPORTANT!! <<<
     * DO NOT edit this method OR call it in your code!
     *
     * @noinspection ALL
     */
    private void $$$setupUI$$$() {
        rootPanel = new JPanel();
        rootPanel.setLayout(new BorderLayout(0, 0));
        final JSplitPane splitPane1 = new JSplitPane();
        splitPane1.setDividerLocation(300);
        splitPane1.setOneTouchExpandable(true);
        splitPane1.setOrientation(0);
        splitPane1.setResizeWeight(1.0);
        rootPanel.add(splitPane1, BorderLayout.CENTER);
        strTabbedPane = new JTabbedPane();
        strTabbedPane.setFocusable(true);
        strTabbedPane.setTabPlacement(3);
        strTabbedPane.setToolTipText("Solution Source / Test selection / Stepping");
        splitPane1.setLeftComponent(strTabbedPane);
        final JPanel panel1 = new JPanel();
        panel1.setLayout(new BorderLayout(0, 0));
        strTabbedPane.addTab("Source", panel1);
        final JScrollPane scrollPane1 = new JScrollPane();
        panel1.add(scrollPane1, BorderLayout.CENTER);
        sourceTextArea = new JTextArea();
        sourceTextArea.setColumns(60);
        sourceTextArea.setFont(new Font("Courier New", sourceTextArea.getFont().getStyle(), 13));
        sourceTextArea.setRows(8);
        sourceTextArea.setToolTipText("<html>Here your MIPS solution code goes.<br/>One instruction per line.</html>");
        scrollPane1.setViewportView(sourceTextArea);
        final JPanel panel2 = new JPanel();
        panel2.setLayout(new GridBagLayout());
        panel1.add(panel2, BorderLayout.SOUTH);
        sourceAssembleButton = new JButton();
        sourceAssembleButton.setActionCommand("Compile");
        sourceAssembleButton.setFocusable(false);
        sourceAssembleButton.setLabel("Assemble");
        sourceAssembleButton.setMargin(new Insets(3, 3, 3, 3));
        sourceAssembleButton.setText("Assemble");
        GridBagConstraints gbc;
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.insets = new Insets(1, 1, 1, 1);
        panel2.add(sourceAssembleButton, gbc);
        sourceVerifyButton = new JButton();
        sourceVerifyButton.setFocusable(false);
        sourceVerifyButton.setMargin(new Insets(3, 5, 3, 3));
        sourceVerifyButton.setText("Verify");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(1, 2, 1, 1);
        panel2.add(sourceVerifyButton, gbc);
        final JPanel panel3 = new JPanel();
        panel3.setLayout(new BorderLayout(0, 0));
        gbc = new GridBagConstraints();
        gbc.gridx = 3;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.insets = new Insets(2, 4, 2, 4);
        panel2.add(panel3, gbc);
        panel3.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.black), null));
        sourceFeedbackLabel = new JLabel();
        sourceFeedbackLabel.setText("...");
        sourceFeedbackLabel.setToolTipText("Source pane status");
        panel3.add(sourceFeedbackLabel, BorderLayout.CENTER);
        sourceSubmitButton = new JButton();
        sourceSubmitButton.setMargin(new Insets(3, 3, 3, 5));
        sourceSubmitButton.setText("Submit");
        gbc = new GridBagConstraints();
        gbc.gridx = 2;
        gbc.gridy = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(1, 1, 1, 2);
        panel2.add(sourceSubmitButton, gbc);
        final JPanel panel4 = new JPanel();
        panel4.setLayout(new BorderLayout(0, 0));
        strTabbedPane.addTab("Test", panel4);
        final JPanel panel5 = new JPanel();
        panel5.setLayout(new GridBagLayout());
        panel4.add(panel5, BorderLayout.SOUTH);
        final JLabel label1 = new JLabel();
        label1.setText("Test:");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.EAST;
        gbc.insets = new Insets(0, 2, 0, 2);
        panel5.add(label1, gbc);
        testComboBox = new JComboBox();
        testComboBox.setMinimumSize(new Dimension(96, 25));
        testComboBox.setPreferredSize(new Dimension(96, 25));
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel5.add(testComboBox, gbc);
        testAddCustomButton = new JButton();
        testAddCustomButton.setFocusable(false);
        testAddCustomButton.setMargin(new Insets(1, 3, 1, 3));
        testAddCustomButton.setText("+");
        testAddCustomButton.setToolTipText("Add an editable copy of selected test");
        gbc = new GridBagConstraints();
        gbc.gridx = 2;
        gbc.gridy = 0;
        gbc.insets = new Insets(5, 2, 5, 1);
        panel5.add(testAddCustomButton, gbc);
        final JToolBar.Separator toolBar$Separator1 = new JToolBar.Separator();
        gbc = new GridBagConstraints();
        gbc.gridx = 3;
        gbc.gridy = 0;
        panel5.add(toolBar$Separator1, gbc);
        testRunButton = new JButton();
        testRunButton.setFocusable(false);
        testRunButton.setMargin(new Insets(3, 3, 3, 3));
        testRunButton.setText("Run");
        gbc = new GridBagConstraints();
        gbc.gridx = 5;
        gbc.gridy = 0;
        gbc.insets = new Insets(1, 1, 1, 1);
        panel5.add(testRunButton, gbc);
        testBatchButton = new JButton();
        testBatchButton.setFocusable(false);
        testBatchButton.setMargin(new Insets(3, 3, 3, 5));
        testBatchButton.setText("Batch");
        testBatchButton.setMnemonic('B');
        testBatchButton.setDisplayedMnemonicIndex(0);
        gbc = new GridBagConstraints();
        gbc.gridx = 6;
        gbc.gridy = 0;
        gbc.insets = new Insets(1, 1, 1, 2);
        panel5.add(testBatchButton, gbc);
        testStepButton = new JButton();
        testStepButton.setFocusable(false);
        testStepButton.setMargin(new Insets(3, 5, 3, 3));
        testStepButton.setText("Step");
        gbc = new GridBagConstraints();
        gbc.gridx = 4;
        gbc.gridy = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(1, 2, 1, 1);
        panel5.add(testStepButton, gbc);
        final JPanel panel6 = new JPanel();
        panel6.setLayout(new BorderLayout(0, 0));
        gbc = new GridBagConstraints();
        gbc.gridx = 7;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.insets = new Insets(2, 4, 2, 4);
        panel5.add(panel6, gbc);
        panel6.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.black), null));
        testStatusLabel = new JLabel();
        testStatusLabel.setText("...");
        testStatusLabel.setToolTipText("Test pane status");
        panel6.add(testStatusLabel, BorderLayout.CENTER);
        final JSplitPane splitPane2 = new JSplitPane();
        splitPane2.setResizeWeight(0.5);
        panel4.add(splitPane2, BorderLayout.CENTER);
        final JScrollPane scrollPane2 = new JScrollPane();
        splitPane2.setRightComponent(scrollPane2);
        testMemTextArea = new JTextArea();
        testMemTextArea.setColumns(20);
        testMemTextArea.setFont(new Font("Courier New", testMemTextArea.getFont().getStyle(), 13));
        testMemTextArea.setLineWrap(false);
        testMemTextArea.setRows(10);
        testMemTextArea.setText("");
        testMemTextArea.setToolTipText("<html>Memory Input/Output<br/><br/>Format:<br/>address:input:expected</html>");
        scrollPane2.setViewportView(testMemTextArea);
        final JScrollPane scrollPane3 = new JScrollPane();
        splitPane2.setLeftComponent(scrollPane3);
        testRegsTextArea = new JTextArea();
        testRegsTextArea.setColumns(20);
        testRegsTextArea.setFont(new Font("Courier New", testRegsTextArea.getFont().getStyle(), 13));
        testRegsTextArea.setRows(10);
        testRegsTextArea.setToolTipText("<html>Register Input/Output<br/><br/>Format:<br/>$reg:input:expected</html>");
        scrollPane3.setViewportView(testRegsTextArea);
        final JPanel panel7 = new JPanel();
        panel7.setLayout(new BorderLayout(0, 0));
        strTabbedPane.addTab("Run", panel7);
        final JPanel panel8 = new JPanel();
        panel8.setLayout(new GridBagLayout());
        panel7.add(panel8, BorderLayout.SOUTH);
        runStepButton = new JButton();
        runStepButton.setFocusable(false);
        runStepButton.setMargin(new Insets(3, 5, 3, 3));
        runStepButton.setText("Step");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.insets = new Insets(1, 2, 1, 1);
        panel8.add(runStepButton, gbc);
        runRunButton = new JButton();
        runRunButton.setFocusable(false);
        runRunButton.setMargin(new Insets(3, 3, 3, 5));
        runRunButton.setText("Run");
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.insets = new Insets(1, 1, 1, 2);
        panel8.add(runRunButton, gbc);
        final JToolBar.Separator toolBar$Separator2 = new JToolBar.Separator();
        gbc = new GridBagConstraints();
        gbc.gridx = 2;
        gbc.gridy = 0;
        panel8.add(toolBar$Separator2, gbc);
        runResetButton = new JButton();
        runResetButton.setFocusable(false);
        runResetButton.setMargin(new Insets(3, 5, 3, 5));
        runResetButton.setText("Reset");
        gbc = new GridBagConstraints();
        gbc.gridx = 3;
        gbc.gridy = 0;
        gbc.insets = new Insets(1, 2, 1, 2);
        panel8.add(runResetButton, gbc);
        final JPanel panel9 = new JPanel();
        panel9.setLayout(new BorderLayout(0, 0));
        gbc = new GridBagConstraints();
        gbc.gridx = 4;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.insets = new Insets(2, 4, 2, 4);
        panel8.add(panel9, gbc);
        panel9.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.black), null));
        runStatusLabel = new JLabel();
        runStatusLabel.setText("...");
        runStatusLabel.setToolTipText("Run pane status");
        panel9.add(runStatusLabel, BorderLayout.CENTER);
        final JPanel panel10 = new JPanel();
        panel10.setLayout(new BorderLayout(0, 0));
        panel7.add(panel10, BorderLayout.CENTER);
        final JSplitPane splitPane3 = new JSplitPane();
        splitPane3.setDividerLocation(120);
        splitPane3.setOrientation(0);
        panel10.add(splitPane3, BorderLayout.CENTER);
        final JScrollPane scrollPane4 = new JScrollPane();
        splitPane3.setRightComponent(scrollPane4);
        runInstructionsTable = new JTable();
        scrollPane4.setViewportView(runInstructionsTable);
        final JSplitPane splitPane4 = new JSplitPane();
        splitPane4.setDividerLocation(180);
        splitPane3.setLeftComponent(splitPane4);
        final JScrollPane scrollPane5 = new JScrollPane();
        splitPane4.setLeftComponent(scrollPane5);
        runRegsTable = new JTable();
        scrollPane5.setViewportView(runRegsTable);
        final JScrollPane scrollPane6 = new JScrollPane();
        splitPane4.setRightComponent(scrollPane6);
        runMemTable = new JTable();
        scrollPane6.setViewportView(runMemTable);
        pclTabbedPane = new JTabbedPane();
        pclTabbedPane.setToolTipText("Problem statement / Code snippets / System Log.");
        splitPane1.setRightComponent(pclTabbedPane);
        final JPanel panel11 = new JPanel();
        panel11.setLayout(new BorderLayout(0, 0));
        pclTabbedPane.addTab("Problem", panel11);
        final JScrollPane scrollPane7 = new JScrollPane();
        panel11.add(scrollPane7, BorderLayout.CENTER);
        problemTextPane = new JTextPane();
        problemTextPane.setEditable(false);
        problemTextPane.setToolTipText("Problem statement goes here...");
        problemTextPane.putClientProperty("JEditorPane.honorDisplayProperties", Boolean.TRUE);
        scrollPane7.setViewportView(problemTextPane);
        final JPanel panel12 = new JPanel();
        panel12.setLayout(new BorderLayout(0, 0));
        pclTabbedPane.addTab("Clipper", panel12);
        final JScrollPane scrollPane8 = new JScrollPane();
        panel12.add(scrollPane8, BorderLayout.CENTER);
        clipTextArea = new JTextArea();
        clipTextArea.setColumns(40);
        clipTextArea.setFont(new Font("Courier New", clipTextArea.getFont().getStyle(), 13));
        clipTextArea.setRows(10);
        clipTextArea.setToolTipText("Store your temp clips of text here");
        scrollPane8.setViewportView(clipTextArea);
        final JPanel panel13 = new JPanel();
        panel13.setLayout(new BorderLayout(0, 0));
        pclTabbedPane.addTab("Log", panel13);
        final JScrollPane scrollPane9 = new JScrollPane();
        panel13.add(scrollPane9, BorderLayout.CENTER);
        logTextPane = new JTextPane();
        logTextPane.setEditable(false);
        logTextPane.setFont(new Font("Courier New", logTextPane.getFont().getStyle(), 13));
        logTextPane.putClientProperty("JEditorPane.honorDisplayProperties", Boolean.TRUE);
        scrollPane9.setViewportView(logTextPane);
    }

    /**
     * @noinspection ALL
     */
    public JComponent $$$getRootComponent$$$() {
        return rootPanel;
    }
}
