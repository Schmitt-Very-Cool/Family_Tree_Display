package com.nefariousmachine.local;

import com.nefariousmachine.data.FamilyTree;
import com.nefariousmachine.data.Person;
import com.nefariousmachine.generate.TreeImageGenerator;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Class to handle running the program locally (as opposed to running it as a web product). Uses javax.swing for
 * the actual GUI components. Calls methods in the classes of package display to generate the tree image.
 */
public class LocalDisplay extends JFrame {
    private String[] CALENDAR_LIST = {"CE", "BCE"};

    // --------------------------------------------------------------
    //                          GUI components
    // --------------------------------------------------------------

    // Center Panel
    private TreePanel treePanel = new TreePanel();

    // Menu Bar
    private JMenuBar menuBar = new JMenuBar();
    private JMenu addMenu = new JMenu("Add");
    private JMenuItem addPerson = new JMenuItem("Person");

    // Selection Options Bar
    private JPanel selectionOptionsBar = new JPanel();
    private JButton editPersonButton = new JButton("Edit Person");
    private JButton addParentsButton = new JButton("Add Parents");
    private JButton setParentsButton = new JButton("Set Parents");
    private JButton addRelationshipButton = new JButton("Add Relationship");

    // Add Person Popup
    private JFrame addPersonPopup = new JFrame("Add Person");
    private JLabel addPersonNameLabel = new JLabel("Name");
    private JLabel addPersonTitleLabel = new JLabel("Title");
    private JLabel addPersonRegionLabel = new JLabel("Region");
    private JLabel addPersonHouseLabel = new JLabel("House");
    private JLabel addPersonBirthYearLabel = new JLabel("Birth Year");
    private JLabel addPersonDeathYearLabel = new JLabel("Death Year");
    private JTextField addPersonName = new JTextField(20);
    private JTextField addPersonTitle = new JTextField(20);
    private JTextField addPersonRegion = new JTextField(20);
    private JTextField addPersonHouse = new JTextField(20);
    private JTextField addPersonBirthYear = new JTextField(4);
    private JTextField addPersonDeathYear = new JTextField(4);
    private JComboBox<String> addPersonBirthYearCalendar = new JComboBox<>(CALENDAR_LIST);
    private JComboBox<String> addPersonDeathYearCalendar = new JComboBox<>(CALENDAR_LIST);
    private JCheckBox addPersonIsMonarch = new JCheckBox("Is Monarch");
    private JCheckBox addPersonIsSaint = new JCheckBox("Is Saint");
    private JButton addPersonSubmit = new JButton("Add");

    // --------------------------------------------------------------

    private FamilyTree familyTree;

    public LocalDisplay(FamilyTree familyTree){
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setTitle("Family Tree Maker");
        setLayout(new BorderLayout());

        addEventHandlers();
        setupPopups();

        selectionOptionsBar.setPreferredSize(new Dimension(treePanel.getPreferredSize().width, 100));
        selectionOptionsBar.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.weightx = 1;
        gbc.gridx = 1;
        selectionOptionsBar.add(editPersonButton, gbc);
        gbc.gridx++;
        selectionOptionsBar.add(addParentsButton, gbc);
        gbc.gridx++;
        selectionOptionsBar.add(setParentsButton, gbc);
        gbc.gridx++;
        selectionOptionsBar.add(addRelationshipButton, gbc);

        addMenu.add(addPerson);
        menuBar.add(addMenu);
        add(menuBar, "North");

        this.familyTree = familyTree;
        drawTreePanel();
        add(treePanel, "Center");
        pack();
        setVisible(true);
    }

    private void addEventHandlers() {
        addPerson.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                addPersonAction();
            }
        });

        addPersonSubmit.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                addPersonToTree();
            }
        });
    }

    private void setupPopups() {
        //Add Person Popup
        GridBagConstraints gbc = new GridBagConstraints();
        addPersonPopup.setLayout(new GridBagLayout());
        gbc.gridy = 1; gbc.gridx = 1; gbc.gridwidth = 4; gbc.weightx = 0;
        gbc.anchor = GridBagConstraints.LINE_START; gbc.insets = new Insets(10, 10, 0, 10);
        addPersonPopup.add(addPersonNameLabel, gbc);
        gbc.gridy++; gbc.weighty++; gbc.insets = new Insets(0, 10, 0, 10);
        addPersonPopup.add(addPersonName, gbc);
        gbc.gridy++;
        addPersonPopup.add(addPersonTitleLabel, gbc);
        gbc.gridy++;
        addPersonPopup.add(addPersonTitle, gbc);
        gbc.gridy++;
        addPersonPopup.add(addPersonRegionLabel, gbc);
        gbc.gridy++;
        addPersonPopup.add(addPersonRegion, gbc);
        gbc.gridy++;
        addPersonPopup.add(addPersonHouseLabel, gbc);
        gbc.gridy++;
        addPersonPopup.add(addPersonHouse, gbc);
        gbc.gridy++; gbc.gridwidth = 2; gbc.insets = new Insets(0, 10, 0, 0);
        addPersonPopup.add(addPersonBirthYearLabel, gbc);
        gbc.gridx+= 2; gbc.weightx = 1; gbc.insets = new Insets(0, 10, 0, 10);
        addPersonPopup.add(addPersonDeathYearLabel, gbc);
        gbc.gridy++; gbc.gridx = 1; gbc.gridwidth = 1; gbc.weightx = 0;
        gbc.insets = new Insets(0, 10, 0, 0);
        addPersonPopup.add(addPersonBirthYear, gbc);
        gbc.gridx++; gbc.insets = new Insets(0, 0, 0, 0);
        addPersonPopup.add(addPersonBirthYearCalendar, gbc);
        gbc.gridx++; gbc.insets = new Insets(0, 10, 0, 0);
        addPersonPopup.add(addPersonDeathYear, gbc);
        gbc.gridx++; gbc.weightx = 2; gbc.insets = new Insets(0, 0, 0, 10);
        addPersonPopup.add(addPersonDeathYearCalendar, gbc);
        gbc.gridx = 1; gbc.gridy++; gbc.gridwidth = 4; gbc.weightx = 0;
        gbc.insets = new Insets(0, 10, 0, 10);
        addPersonPopup.add(addPersonIsMonarch, gbc);
        gbc.gridy++;
        addPersonPopup.add(addPersonIsSaint, gbc);
        gbc.gridy++; gbc.insets = new Insets(0, 10, 10, 10);
        addPersonPopup.add(addPersonSubmit, gbc);
        addPersonPopup.setSize(new Dimension(400, 400));
        addPersonPopup.pack();
    }

    public void enableSelectionOptionsBar(){
        
    }

    public void drawTreePanel() {

    }

    private void addPersonAction() {
        //TODO disable the submit button if there's invalid info to
        addPersonPopup.setVisible(true);
    }

    private void addPersonToTree() {
        String name = addPersonName.getText();
        String title = addPersonTitle.getText();
        String region = addPersonRegion.getText();
        String house = addPersonHouse.getText();
        String birthYearString = addPersonBirthYear.getText() + " " + addPersonBirthYearCalendar.getSelectedItem();
        String deathYearString = addPersonDeathYear.getText() + " " + addPersonDeathYearCalendar.getSelectedItem();
        boolean isMonarch = addPersonIsMonarch.isSelected();
        boolean isSaint = addPersonIsSaint.isSelected();

        Person newMember = new Person(name, title, region, house, birthYearString, deathYearString, isMonarch, isSaint, null);
        familyTree.addMember(newMember);
        TreeImageGenerator.setFamilyTree(familyTree);
        TreeImageGenerator.generate();
        treePanel.setTreeImage(TreeImageGenerator.getFamilyTreeImage());
    }
}
