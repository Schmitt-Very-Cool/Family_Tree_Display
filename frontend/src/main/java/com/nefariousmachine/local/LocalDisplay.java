package com.nefariousmachine.local;

import com.nefariousmachine.data.FamilyTree;
import com.nefariousmachine.data.FamilyTreeFileManager;
import com.nefariousmachine.data.Person;
import com.nefariousmachine.data.Relationship;
import com.nefariousmachine.generate.TreeImageGenerator;

import javax.swing.*;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;

/**
 * Class to handle running the program locally (as opposed to running it as a web product). Uses javax.swing for
 * the actual GUI components. Calls methods in the classes of package display to generate the tree image.
 */
public class LocalDisplay extends JFrame {
    private final String[] CALENDAR_LIST = {"CE", "BCE"};
    private final TreeImageGenerator treeImageGenerator = new TreeImageGenerator();

    // --------------------------------------------------------------
    //                          GUI components
    // --------------------------------------------------------------

    // Center Panel
    private final TreePanel treePanel = new TreePanel();

    // Menu Bar
    private final JMenuBar menuBar = new JMenuBar();
    private final JMenu fileMenu = new JMenu("File");
    private final JMenuItem newButton = new JMenuItem("New");
    private final JMenuItem saveButton = new JMenuItem("Save");
    private final JMenuItem saveAsButton = new JMenuItem("Save As");
    private final JMenuItem loadButton = new JMenuItem("Load");
    private final JMenu editMenu = new JMenu("Edit");
    private final JMenuItem addPerson = new JMenuItem("Add Person");
    private final JMenuItem editPersonButton = new JMenuItem("Edit Person");
    private final JMenuItem removePersonButton = new JMenuItem("Remove Person(s)");
    private final JMenuItem addParentsButton = new JMenuItem("Add Parents");
    private final JMenuItem setParentsButton = new JMenuItem("Set Parents");
    private final JMenuItem clearParentsButton = new JMenuItem("Clear Parents");
    private final JMenuItem addPartnerButton = new JMenuItem("Add Partner");
    private final JMenuItem addRelationshipButton = new JMenuItem("Add Relationship");
    private final JMenuItem removeRelationshipButton = new JMenuItem("Remove Relationship");

    // Add Person Popup
    private final JFrame addPersonPopup = new JFrame("Add Person");
    private final JLabel addPersonNameLabel = new JLabel("Name");
    private final JLabel addPersonTitleLabel = new JLabel("Title");
    private final JLabel addPersonRegionLabel = new JLabel("Region");
    private final JLabel addPersonHouseLabel = new JLabel("House");
    private final JLabel addPersonBirthYearLabel = new JLabel("Birth Year");
    private final JLabel addPersonDeathYearLabel = new JLabel("Death Year");
    private final JTextField addPersonName = new JTextField(20);
    private final JTextField addPersonTitle = new JTextField(20);
    private final JTextField addPersonRegion = new JTextField(20);
    private final JTextField addPersonHouse = new JTextField(20);
    private final JTextField addPersonBirthYear = new JTextField(4);
    private final JTextField addPersonDeathYear = new JTextField(4);
    private final JComboBox<String> addPersonBirthYearCalendar = new JComboBox<>(CALENDAR_LIST);
    private final JComboBox<String> addPersonDeathYearCalendar = new JComboBox<>(CALENDAR_LIST);
    private final JCheckBox addPersonIsMonarch = new JCheckBox("Is Monarch");
    private final JCheckBox addPersonIsUnknown = new JCheckBox("Is Unknown");
    private final JButton addPersonSubmit = new JButton("Add");

    //Edit Person Popup
    private final JFrame editPersonPopup = new JFrame("Edit Person");
    private final JLabel editPersonNameLabel = new JLabel("Name");
    private final JLabel editPersonTitleLabel = new JLabel("Title");
    private final JLabel editPersonRegionLabel = new JLabel("Region");
    private final JLabel editPersonHouseLabel = new JLabel("House");
    private final JLabel editPersonBirthYearLabel = new JLabel("Birth Year");
    private final JLabel editPersonDeathYearLabel = new JLabel("Death Year");
    private final JTextField editPersonName = new JTextField(20);
    private final JTextField editPersonTitle = new JTextField(20);
    private final JTextField editPersonRegion = new JTextField(20);
    private final JTextField editPersonHouse = new JTextField(20);
    private final JTextField editPersonBirthYear = new JTextField(4);
    private final JTextField editPersonDeathYear = new JTextField(4);
    private final JComboBox<String> editPersonBirthYearCalendar = new JComboBox<>(CALENDAR_LIST);
    private final JComboBox<String> editPersonDeathYearCalendar = new JComboBox<>(CALENDAR_LIST);
    private final JCheckBox editPersonIsMonarch = new JCheckBox("Is Monarch");
    private final JCheckBox editPersonIsUnknown = new JCheckBox("Is Unknown");
    private final JButton editPersonSubmit = new JButton("Confirm Edits");
    private int editPersonId = -1;

    //Add Parents Popup
    private final JFrame addParentsPopup = new JFrame("Add Parents");
    private final JPanel addParentsParentOnePanel = new JPanel();
    private final JPanel addParentsParentTwoPanel = new JPanel();
    private final JLabel addParentsParentOneLabel = new JLabel("Parent 1");
    private final JLabel addParentsParentTwoLabel = new JLabel("Parent 2");
    private final JCheckBox addParentsParentOneUnknown = new JCheckBox("Unknown Parent");
    private final JLabel addParentsParentOneNameLabel = new JLabel("Name");
    private final JLabel addParentsParentOneTitleLabel = new JLabel("Title");
    private final JLabel addParentsParentOneRegionLabel = new JLabel("Region");
    private final JLabel addParentsParentOneHouseLabel = new JLabel("House");
    private final JLabel addParentsParentOneBirthYearLabel = new JLabel("Birth Year");
    private final JLabel addParentsParentOneDeathYearLabel = new JLabel("Death Year");
    private final JTextField addParentsParentOneName = new JTextField(20);
    private final JTextField addParentsParentOneTitle = new JTextField(20);
    private final JTextField addParentsParentOneRegion = new JTextField(20);
    private final JTextField addParentsParentOneHouse = new JTextField(20);
    private final JTextField addParentsParentOneBirthYear = new JTextField(4);
    private final JTextField addParentsParentOneDeathYear = new JTextField(4);
    private final JComboBox<String> addParentsParentOneBirthYearCalendar = new JComboBox<>(CALENDAR_LIST);
    private final JComboBox<String> addParentsParentOneDeathYearCalendar = new JComboBox<>(CALENDAR_LIST);
    private final JCheckBox addParentsParentOneIsMonarch = new JCheckBox("Is Monarch");
    private final JCheckBox addParentsParentTwoUnknown = new JCheckBox("Unknown Parent");
    private final JLabel addParentsParentTwoNameLabel = new JLabel("Name");
    private final JLabel addParentsParentTwoTitleLabel = new JLabel("Title");
    private final JLabel addParentsParentTwoRegionLabel = new JLabel("Region");
    private final JLabel addParentsParentTwoHouseLabel = new JLabel("House");
    private final JLabel addParentsParentTwoBirthYearLabel = new JLabel("Birth Year");
    private final JLabel addParentsParentTwoDeathYearLabel = new JLabel("Death Year");
    private final JTextField addParentsParentTwoName = new JTextField(20);
    private final JTextField addParentsParentTwoTitle = new JTextField(20);
    private final JTextField addParentsParentTwoRegion = new JTextField(20);
    private final JTextField addParentsParentTwoHouse = new JTextField(20);
    private final JTextField addParentsParentTwoBirthYear = new JTextField(4);
    private final JTextField addParentsParentTwoDeathYear = new JTextField(4);
    private final JComboBox<String> addParentsParentTwoBirthYearCalendar = new JComboBox<>(CALENDAR_LIST);
    private final JComboBox<String> addParentsParentTwoDeathYearCalendar = new JComboBox<>(CALENDAR_LIST);
    private final JCheckBox addParentsParentTwoIsMonarch = new JCheckBox("Is Monarch");
    private final JCheckBox addParentsIsMarriage = new JCheckBox("Is Marriage");
    private final JButton addParentsSubmit = new JButton("Add Parents");
    private int[] addParentIds;

    //Set Parents Popup
    private final JFrame setParentsPopup = new JFrame("Set Parents");
    private final JLabel setParentsParentOneLabel = new JLabel("Parent One");
    private final JComboBox<String> setParentsParentOneSelect = new JComboBox<>();
    private final JLabel setParentsParentTwoLabel = new JLabel("Parent Two");
    private final JComboBox<String> setParentsParentTwoSelect = new JComboBox<>();
    private final JButton setParentsSubmit = new JButton("Set Parents");
    private Person[] setParentsSelectedPersons;
    private ArrayList<Person> setParentsParentOneOptions;
    private ArrayList<Person> setParentsParentTwoOptions;

    //Add Relationship Popup
    private final JFrame addRelationshipPopup = new JFrame("Add Relationship");
    private final JCheckBox addRelationshipIsMarriage = new JCheckBox("Is Marriage");
    private final JButton addRelationshipSubmit = new JButton("Add Relationship");
    private Person[] chosenRelationshipPeople;

    // --------------------------------------------------------------

    private FamilyTree familyTree;

    public LocalDisplay(FamilyTree familyTree){
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setTitle("Family Tree Maker");
        setLayout(new BorderLayout());

        addToolTips();
        addEventHandlers();
        setupPopups();

        fileMenu.add(newButton);
        fileMenu.add(saveButton);
        fileMenu.add(saveAsButton);
        fileMenu.add(loadButton);
        menuBar.add(fileMenu);
        editMenu.add(addPerson);
        editMenu.add(editPersonButton);
        editMenu.add(removePersonButton);
        editMenu.add(addParentsButton);
        editMenu.add(setParentsButton);
        editMenu.add(clearParentsButton);
        editMenu.add(addRelationshipButton);
        editMenu.add(removeRelationshipButton);
        menuBar.add(editMenu);
        add(menuBar, "North");

        updateSelectionOptions();

        this.familyTree = familyTree;
        add(treePanel, "Center");
        pack();
    }

    private void addToolTips() {
        newButton.setToolTipText("New family tree");
        saveButton.setToolTipText("Save this family tree");
        saveAsButton.setToolTipText("Save this family tree to a new file");
        loadButton.setToolTipText("Load in a family tree from a file");
        addPerson.setToolTipText("Add a new Person to the Tree.");
        editPersonButton.setToolTipText("Edit details of the selected Person.");
        removePersonButton.setToolTipText("Removes selected Person or Persons from the Tree.");
        addParentsButton.setToolTipText("Create two new Persons and set all of the selected members to be their children.");
        setParentsButton.setToolTipText("Choose two of the selected Persons who are in a relationship with one another to be the parents of the remaining selected Persons.");
        clearParentsButton.setToolTipText("Removes parent links from all selected Persons. Does not remove child links.");
        addPartnerButton.setToolTipText("Add a new Person to the Tree and set them to be in a relationship with the selected Person.");
        addRelationshipButton.setToolTipText("Put the two selected Persons in a Relationship together.");
        removeRelationshipButton.setToolTipText("Removes the relationship between the two selected Persons.");
    }

    private void addEventHandlers() {
        treePanel.addMouseListener(new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent e) {
                treePanel.onClick(e, treeImageGenerator);
                updateSelectionOptions();
            }

            @Override public void mousePressed(MouseEvent e) {}
            @Override public void mouseReleased(MouseEvent e) {}
            @Override public void mouseEntered(MouseEvent e) {}
            @Override public void mouseExited(MouseEvent e) {}
        });

        saveAsButton.addActionListener(e -> saveTree());
        loadButton.addActionListener(e -> loadTree());
        addPerson.addActionListener(e -> addPersonAction());
        addPersonSubmit.addActionListener(e -> addPersonToTree());
        editPersonButton.addActionListener(e -> editPersonAction());
        editPersonSubmit.addActionListener(e -> editPerson());
        removePersonButton.addActionListener(e -> removePerson());
        addParentsButton.addActionListener(e -> addParentsAction());
        addParentsSubmit.addActionListener(e -> addParents());
        setParentsButton.addActionListener(e -> setParentsAction());
        setParentsParentOneSelect.addActionListener(e -> setParentsParentOneAction());
        setParentsParentTwoSelect.addActionListener(e -> setParentsParentTwoAction());
        setParentsSubmit.addActionListener(e -> setParents());
        clearParentsButton.addActionListener(e -> clearParents());
        addRelationshipButton.addActionListener(e -> addRelationshipAction());
        addRelationshipSubmit.addActionListener(e -> addRelationship());
        removeRelationshipButton.addActionListener(e -> removeRelationship());
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
        addPersonPopup.add(addPersonIsUnknown, gbc);
        gbc.gridy++; gbc.insets = new Insets(0, 10, 10, 10);
        addPersonPopup.add(addPersonSubmit, gbc);
        addPersonPopup.setSize(new Dimension(400, 400)); //Don't know why but all the TextAreas collapse if I don't include this
        addPersonPopup.pack();

        //Edit Person Popup
        gbc = new GridBagConstraints();
        editPersonPopup.setLayout(new GridBagLayout());
        gbc.gridy = 1; gbc.gridx = 1; gbc.gridwidth = 4; gbc.weightx = 0;
        gbc.anchor = GridBagConstraints.LINE_START; gbc.insets = new Insets(10, 10, 0, 10);
        editPersonPopup.add(editPersonNameLabel, gbc);
        gbc.gridy++; gbc.weighty++; gbc.insets = new Insets(0, 10, 0, 10);
        editPersonPopup.add(editPersonName, gbc);
        gbc.gridy++;
        editPersonPopup.add(editPersonTitleLabel, gbc);
        gbc.gridy++;
        editPersonPopup.add(editPersonTitle, gbc);
        gbc.gridy++;
        editPersonPopup.add(editPersonRegionLabel, gbc);
        gbc.gridy++;
        editPersonPopup.add(editPersonRegion, gbc);
        gbc.gridy++;
        editPersonPopup.add(editPersonHouseLabel, gbc);
        gbc.gridy++;
        editPersonPopup.add(editPersonHouse, gbc);
        gbc.gridy++; gbc.gridwidth = 2; gbc.insets = new Insets(0, 10, 0, 0);
        editPersonPopup.add(editPersonBirthYearLabel, gbc);
        gbc.gridx+= 2; gbc.weightx = 1; gbc.insets = new Insets(0, 10, 0, 10);
        editPersonPopup.add(editPersonDeathYearLabel, gbc);
        gbc.gridy++; gbc.gridx = 1; gbc.gridwidth = 1; gbc.weightx = 0;
        gbc.insets = new Insets(0, 10, 0, 0);
        editPersonPopup.add(editPersonBirthYear, gbc);
        gbc.gridx++; gbc.insets = new Insets(0, 0, 0, 0);
        editPersonPopup.add(editPersonBirthYearCalendar, gbc);
        gbc.gridx++; gbc.insets = new Insets(0, 10, 0, 0);
        editPersonPopup.add(editPersonDeathYear, gbc);
        gbc.gridx++; gbc.weightx = 2; gbc.insets = new Insets(0, 0, 0, 10);
        editPersonPopup.add(editPersonDeathYearCalendar, gbc);
        gbc.gridx = 1; gbc.gridy++; gbc.gridwidth = 4; gbc.weightx = 0;
        gbc.insets = new Insets(0, 10, 0, 10);
        editPersonPopup.add(editPersonIsMonarch, gbc);
        gbc.gridy++;
        editPersonPopup.add(editPersonIsUnknown, gbc);
        gbc.gridy++; gbc.insets = new Insets(0, 10, 10, 10);
        editPersonPopup.add(editPersonSubmit, gbc);
        editPersonPopup.setSize(new Dimension(400, 400)); //Don't know why but all the TextAreas collapse if I don't include this
        editPersonPopup.pack();

        //Add Parents Popup
        addParentsParentOnePanel.setBorder(new LineBorder(Color.BLACK));
        addParentsParentTwoPanel.setBorder(new LineBorder(Color.BLACK));
        gbc = new GridBagConstraints();
        addParentsPopup.setLayout(new GridBagLayout());
        gbc.gridy = 1; gbc.gridx = 1; gbc.weightx = 0;
        gbc.anchor = GridBagConstraints.CENTER; gbc.insets = new Insets(10, 10, 0, 10);
        addParentsPopup.add(addParentsParentOneLabel, gbc);
        gbc.gridx++;
        addParentsPopup.add(addParentsParentTwoLabel, gbc);
        addParentsParentOnePanel.setLayout(new GridBagLayout());
        gbc.gridx = 1; gbc.gridwidth = 4; gbc.anchor = GridBagConstraints.LINE_START;
        addParentsParentOnePanel.add(addParentsParentOneUnknown, gbc);
        gbc.gridy++; gbc.weighty++; gbc.insets = new Insets(0, 10, 0, 10);
        addParentsParentOnePanel.add(addParentsParentOneNameLabel, gbc);
        gbc.gridy++;
        addParentsParentOnePanel.add(addParentsParentOneName, gbc);
        gbc.gridy++;
        addParentsParentOnePanel.add(addParentsParentOneTitleLabel, gbc);
        gbc.gridy++;
        addParentsParentOnePanel.add(addParentsParentOneTitle, gbc);
        gbc.gridy++;
        addParentsParentOnePanel.add(addParentsParentOneRegionLabel, gbc);
        gbc.gridy++;
        addParentsParentOnePanel.add(addParentsParentOneRegion, gbc);
        gbc.gridy++;
        addParentsParentOnePanel.add(addParentsParentOneHouseLabel, gbc);
        gbc.gridy++;
        addParentsParentOnePanel.add(addParentsParentOneHouse, gbc);
        gbc.gridy++; gbc.gridwidth = 2; gbc.insets = new Insets(0, 10, 0, 0);
        addParentsParentOnePanel.add(addParentsParentOneBirthYearLabel, gbc);
        gbc.gridx+= 2; gbc.weightx = 1; gbc.insets = new Insets(0, 10, 0, 10);
        addParentsParentOnePanel.add(addParentsParentOneDeathYearLabel, gbc);
        gbc.gridy++; gbc.gridx = 1; gbc.gridwidth = 1; gbc.weightx = 0;
        gbc.insets = new Insets(0, 10, 0, 0);
        addParentsParentOnePanel.add(addParentsParentOneBirthYear, gbc);
        gbc.gridx++; gbc.insets = new Insets(0, 0, 0, 0);
        addParentsParentOnePanel.add(addParentsParentOneBirthYearCalendar, gbc);
        gbc.gridx++; gbc.insets = new Insets(0, 10, 0, 0);
        addParentsParentOnePanel.add(addParentsParentOneDeathYear, gbc);
        gbc.gridx++; gbc.weightx = 2; gbc.insets = new Insets(0, 0, 0, 10);
        addParentsParentOnePanel.add(addParentsParentOneDeathYearCalendar, gbc);
        gbc.gridx = 1; gbc.gridy++; gbc.gridwidth = 4; gbc.weightx = 0;
        gbc.insets = new Insets(0, 10, 0, 10);
        addParentsParentOnePanel.add(addParentsParentOneIsMonarch, gbc);
        gbc = new GridBagConstraints();
        addParentsParentTwoPanel.setLayout(new GridBagLayout());
        gbc.gridy = 1; gbc.gridx = 1; gbc.gridwidth = 4; gbc.weightx = 0;
        gbc.anchor = GridBagConstraints.LINE_START; gbc.insets = new Insets(10, 10, 0, 10);
        addParentsParentTwoPanel.add(addParentsParentTwoUnknown, gbc);
        gbc.gridy++; gbc.weighty++; gbc.insets = new Insets(0, 10, 0, 10);
        addParentsParentTwoPanel.add(addParentsParentTwoNameLabel, gbc);
        gbc.gridy++;
        addParentsParentTwoPanel.add(addParentsParentTwoName, gbc);
        gbc.gridy++;
        addParentsParentTwoPanel.add(addParentsParentTwoTitleLabel, gbc);
        gbc.gridy++;
        addParentsParentTwoPanel.add(addParentsParentTwoTitle, gbc);
        gbc.gridy++;
        addParentsParentTwoPanel.add(addParentsParentTwoRegionLabel, gbc);
        gbc.gridy++;
        addParentsParentTwoPanel.add(addParentsParentTwoRegion, gbc);
        gbc.gridy++;
        addParentsParentTwoPanel.add(addParentsParentTwoHouseLabel, gbc);
        gbc.gridy++;
        addParentsParentTwoPanel.add(addParentsParentTwoHouse, gbc);
        gbc.gridy++; gbc.gridwidth = 2; gbc.insets = new Insets(0, 10, 0, 0);
        addParentsParentTwoPanel.add(addParentsParentTwoBirthYearLabel, gbc);
        gbc.gridx+= 2; gbc.weightx = 1; gbc.insets = new Insets(0, 10, 0, 10);
        addParentsParentTwoPanel.add(addParentsParentTwoDeathYearLabel, gbc);
        gbc.gridy++; gbc.gridx = 1; gbc.gridwidth = 1; gbc.weightx = 0;
        gbc.insets = new Insets(0, 10, 0, 0);
        addParentsParentTwoPanel.add(addParentsParentTwoBirthYear, gbc);
        gbc.gridx++; gbc.insets = new Insets(0, 0, 0, 0);
        addParentsParentTwoPanel.add(addParentsParentTwoBirthYearCalendar, gbc);
        gbc.gridx++; gbc.insets = new Insets(0, 10, 0, 0);
        addParentsParentTwoPanel.add(addParentsParentTwoDeathYear, gbc);
        gbc.gridx++; gbc.weightx = 2; gbc.insets = new Insets(0, 0, 0, 10);
        addParentsParentTwoPanel.add(addParentsParentTwoDeathYearCalendar, gbc);
        gbc.gridx = 1; gbc.gridy++; gbc.gridwidth = 4; gbc.weightx = 0;
        gbc.insets = new Insets(0, 10, 0, 10);
        addParentsParentTwoPanel.add(addParentsParentTwoIsMonarch, gbc);
        gbc = new GridBagConstraints();
        gbc.gridy = 2; gbc.gridx = 1;
        addParentsPopup.add(addParentsParentOnePanel, gbc);
        gbc.gridx++;
        addParentsPopup.add(addParentsParentTwoPanel, gbc);
        gbc.gridx = 1; gbc.gridwidth = 2; gbc.gridy++;
        gbc.insets = new Insets(10, 0, 10, 0);
        addParentsPopup.add(addParentsIsMarriage, gbc);
        gbc.gridy++;
        addParentsPopup.add(addParentsSubmit, gbc);
        addParentsPopup.setSize(new Dimension(400, 400)); //Don't know why but all the TextAreas collapse if I don't include this
        addParentsPopup.pack();

        //Set Parents Popup
        setParentsParentOneSelect.setPreferredSize(new Dimension(100, 20));
        setParentsParentTwoSelect.setPreferredSize(new Dimension(100, 20));
        setParentsPopup.setLayout(new GridBagLayout());
        gbc = new GridBagConstraints();
        gbc.gridy = 1; gbc.gridx = 1; gbc.insets = new Insets(10, 10, 5, 5);
        setParentsPopup.add(setParentsParentOneLabel, gbc);
        gbc.gridx++; gbc.insets = new Insets(10, 5, 5, 10);
        setParentsPopup.add(setParentsParentTwoLabel, gbc);
        gbc.gridx = 1; gbc.gridy++; gbc.insets = new Insets(5, 10, 5, 5);
        setParentsPopup.add(setParentsParentOneSelect, gbc);
        gbc.gridx++; gbc.insets = new Insets(5, 5, 5, 10);
        setParentsPopup.add(setParentsParentTwoSelect, gbc);
        gbc.gridx = 1; gbc.gridy++; gbc.gridwidth = 2; gbc.insets = new Insets(5, 10, 10, 10);
        setParentsPopup.add(setParentsSubmit, gbc);
        setParentsPopup.setSize(new Dimension(1000, 1000)); //Don't know why but all the TextAreas collapse if I don't include this
        setParentsPopup.pack();

        //Add Relationship Popup
        addRelationshipPopup.setLayout(new GridBagLayout());
        gbc = new GridBagConstraints();
        gbc.gridy = 1;
        addRelationshipPopup.add(addRelationshipIsMarriage, gbc);
        gbc.gridy++;
        addRelationshipPopup.add(addRelationshipSubmit, gbc);
        addRelationshipPopup.setSize(new Dimension(400, 400)); //Don't know why but all the TextAreas collapse if I don't include this
        addRelationshipPopup.pack();
    }

    /**
     * Call this to update which options in the Edit menu should be enabled or disabled.
     */
    private void updateSelectionOptions() {
        var selected = treeImageGenerator.getSelected();
        boolean noParents = true;
        for(Person p : selected) {
            if (p.getParents() != null) {
                noParents = false;
                break;
            }
        }
        boolean hasRelationship = false;
        if(selected.length > 2) {
            for(int i = 0; i < selected.length; i++) {
                for(int j = i + 1; j < selected.length; j++) {
                    if(treeImageGenerator.getFamilyTree()
                            .getRelationship(selected[i].getId(), selected[j].getId()) != null) {
                        hasRelationship = true;
                        break;
                    }
                }
                if(hasRelationship) {
                    break;
                }
            }
        }
        editPersonButton.setEnabled(selected.length == 1);
        if(noParents) {
            addParentsButton.setEnabled(true);
            clearParentsButton.setEnabled(false);
        } else {
            addParentsButton.setEnabled(false);
            clearParentsButton.setEnabled(true);
        }
        setParentsButton.setEnabled(hasRelationship);
        addRelationshipButton.setEnabled(selected.length == 2);
        removeRelationshipButton.setEnabled(selected.length == 2 && treeImageGenerator.getFamilyTree()
                .getRelationship(selected[0].getId(), selected[1].getId()) != null);
    }

    private void addPersonAction() {
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
        boolean isUnknown = addPersonIsUnknown.isSelected();

        Person newMember = new Person(name, title, region, house, birthYearString, deathYearString, isMonarch, isUnknown, null);
        familyTree.addMember(newMember);
        System.out.println("Added Member " + name + " (id=" + newMember.getId() + ")");
        treeImageGenerator.setFamilyTree(familyTree);
        treeImageGenerator.generate();
        treeImageGenerator.draw();
        treePanel.setTreeImage(treeImageGenerator.getFamilyTreeImage());

        clearAddPersonPopup();
    }

    private void editPersonAction() {
        prepEditPersonInfo();
        editPersonPopup.setVisible(true);
    }

    private void editPerson() {
        Person personToEdit = treeImageGenerator.getFamilyTree().getMemberById(editPersonId);
        personToEdit.setName(editPersonName.getText());
        personToEdit.setTitle(editPersonTitle.getText());
        personToEdit.setRegion(editPersonRegion.getText());
        personToEdit.setHouse(editPersonHouse.getText());
        personToEdit.setBirthYear(editPersonBirthYear.getText() + " " + editPersonBirthYearCalendar.getSelectedItem());
        personToEdit.setDeathYear(editPersonDeathYear.getText() + " " + editPersonDeathYearCalendar.getSelectedItem());
        personToEdit.setMonarch(editPersonIsMonarch.isSelected());
        personToEdit.setUnknown(editPersonIsUnknown.isSelected());

        treeImageGenerator.draw();
        treePanel.setTreeImage(treeImageGenerator.getFamilyTreeImage());
        editPersonPopup.setVisible(false);
    }

    private void removePerson() {
        for(Person p : treeImageGenerator.getSelected()) {
            treeImageGenerator.getFamilyTree().removeMember(p);
        }
        treeImageGenerator.generate();
        treeImageGenerator.draw();
        treePanel.setTreeImage(treeImageGenerator.getFamilyTreeImage());
        treeImageGenerator.select(-1, -1);
        updateSelectionOptions();
    }

    private void addParentsAction() {
        prepAddParentsPopup();
        addParentIds = treeImageGenerator.getSelectedIds();
        addParentsPopup.setVisible(true);
    }

    private void addParents() {
        Person parent1;
        if(!addParentsParentOneUnknown.isSelected()) {
            String parent1Name = addParentsParentOneName.getText();
            String parent1Title = addParentsParentOneTitle.getText();
            String parent1Region = addParentsParentOneRegion.getText();
            String parent1House = addParentsParentOneHouse.getText();
            String parent1BirthYear = addParentsParentOneBirthYear.getText() + " "
                    + addParentsParentOneBirthYearCalendar.getSelectedItem();
            String parent1DeathYear = addParentsParentOneDeathYear.getText() + " "
                    + addParentsParentOneDeathYearCalendar.getSelectedItem();
            boolean parent1IsMonarch = addParentsParentOneIsMonarch.isSelected();
            parent1 = new Person(parent1Name, parent1Title, parent1Region, parent1House, parent1BirthYear,
                    parent1DeathYear, parent1IsMonarch, false, null);
        } else {
            parent1 = new Person("???", "", "", "", "", "",
                    false, false, null);
        }
        Person parent2;
        if(!addParentsParentTwoUnknown.isSelected()) {
            String parent2Name = addParentsParentTwoName.getText();
            String parent2Title = addParentsParentTwoTitle.getText();
            String parent2Region = addParentsParentTwoRegion.getText();
            String parent2House = addParentsParentTwoHouse.getText();
            String parent2BirthYear = addParentsParentTwoBirthYear.getText() + " "
                    + addParentsParentTwoBirthYearCalendar.getSelectedItem();
            String parent2DeathYear = addParentsParentTwoDeathYear.getText() + " "
                    + addParentsParentTwoDeathYearCalendar.getSelectedItem();
            boolean parent2IsMonarch = addParentsParentTwoIsMonarch.isSelected();
            parent2 = new Person(parent2Name, parent2Title, parent2Region, parent2House, parent2BirthYear,
                    parent2DeathYear, parent2IsMonarch, false, null);
        } else {
            parent2 = new Person("???", "", "", "", "", "",
                    false, false, null);
        }

        Relationship parentage = new Relationship(parent1, parent2, addParentsIsMarriage.isSelected());
        var selected = treeImageGenerator.getFamilyTree().getPersons(addParentIds);
        for(Person selection : selected) {
            selection.setParents(parentage);
        }

        treeImageGenerator.getFamilyTree().addMember(parent1);
        treeImageGenerator.getFamilyTree().addMember(parent2);
        treeImageGenerator.getFamilyTree().
                addRelationship(parentage);

        treeImageGenerator.generate();
        treeImageGenerator.draw();
        treePanel.setTreeImage(treeImageGenerator.getFamilyTreeImage());
        addParentsPopup.setVisible(false);
    }

    private void setParentsAction() {
        prepSetParentsPopup();
        setParentsPopup.setVisible(true);
    }

    private void setParentsParentOneAction() {
        if(setParentsParentOneSelect.getSelectedIndex() == 0) {
            try {
                setParentsParentTwoSelect.setSelectedIndex(0);
            } catch(IllegalArgumentException ignored) {

            }
            setParentsParentTwoSelect.setEnabled(false);
            setParentsSubmit.setEnabled(false);
            return;
        }
        setParentsParentTwoOptions = new ArrayList<>();
        Person parentOne = setParentsParentOneOptions.get(setParentsParentOneSelect.getSelectedIndex() - 1);
        for (Person setParentsParentOneOption : setParentsParentOneOptions) {
            if (treeImageGenerator.getFamilyTree()
                    .getRelationship(parentOne.getId(), setParentsParentOneOption.getId()) != null) {
                setParentsParentTwoOptions.add(setParentsParentOneOption);
            }
        }
        String[] parentTwoList = new String[setParentsParentTwoOptions.size() + 1];
        parentTwoList[0] = "Select Parent Two";
        for(int i = 0; i < setParentsParentTwoOptions.size(); i++) {
            parentTwoList[i + 1] = setParentsParentTwoOptions.get(i).toString();
        }
        setParentsParentTwoSelect.setModel(new DefaultComboBoxModel<>(parentTwoList));
        setParentsParentTwoSelect.setSelectedIndex(0);
        setParentsParentTwoSelect.setEnabled(true);
    }

    private void setParentsParentTwoAction() {
        if(setParentsParentTwoSelect.getSelectedIndex() == 0) {
            setParentsSubmit.setEnabled(false);
            return;
        }
        setParentsSubmit.setEnabled(true);
    }

    private void setParents() {
        Person parentOne = setParentsParentOneOptions.get(setParentsParentOneSelect.getSelectedIndex()-1);
        Person parentTwo = setParentsParentTwoOptions.get(setParentsParentTwoSelect.getSelectedIndex()-1);
        Relationship parentage = treeImageGenerator.getFamilyTree().getRelationship(parentOne.getId(), parentTwo.getId());
        for(Person p : setParentsSelectedPersons) {
            if(p == parentOne || p == parentTwo) {
                continue;
            }
            p.setParents(parentage);
        }

        treeImageGenerator.generate();
        treeImageGenerator.draw();
        treePanel.setTreeImage(treeImageGenerator.getFamilyTreeImage());
        updateSelectionOptions();

        setParentsPopup.setVisible(false);
    }

    private void clearParents() {
        Person[] selected = treeImageGenerator.getSelected();
        for(Person p : selected) {
            p.setParents(null);
        }

        treeImageGenerator.generate();
        treeImageGenerator.draw();
        treePanel.setTreeImage(treeImageGenerator.getFamilyTreeImage());
        updateSelectionOptions();
    }

    private void addRelationshipAction() {
        chosenRelationshipPeople = treeImageGenerator.getSelected();
        addRelationshipPopup.setVisible(true);
    }

    private void addRelationship() {
        Relationship relationship = new Relationship(chosenRelationshipPeople[0],
                chosenRelationshipPeople[1], addRelationshipIsMarriage.isSelected());
        treeImageGenerator.getFamilyTree().addRelationship(relationship);
        System.out.println("Added relationship " + relationship.getPeople()[0] + "+" + relationship.getPeople()[1]);

        treeImageGenerator.generate();
        treeImageGenerator.draw();
        treePanel.setTreeImage(treeImageGenerator.getFamilyTreeImage());

        addRelationshipPopup.setVisible(false);
        updateSelectionOptions();
    }

    private void removeRelationship() {
        Person[] lovers = treeImageGenerator.getSelected();
        Relationship r = treeImageGenerator.getFamilyTree().getRelationship(lovers[0].getId(), lovers[1].getId());
        treeImageGenerator.getFamilyTree().removeRelationship(r);

        treeImageGenerator.generate();
        treeImageGenerator.draw();
        treePanel.setTreeImage(treeImageGenerator.getFamilyTreeImage());

        updateSelectionOptions();
    }

    private void prepEditPersonInfo() {
        Person[] personArray = treeImageGenerator.getSelected();
        if(personArray.length != 1) {
            return;
        }
        Person person = personArray[0];
        editPersonId = person.getId();
        editPersonName.setText(person.getName());
        editPersonTitle.setText(person.getTitle());
        editPersonRegion.setText(person.getRegion());
        editPersonHouse.setText(person.getHouse());
        String birthYearFullText = person.getBirthYear();
        String birthYear = birthYearFullText.substring(0, birthYearFullText.lastIndexOf(' '));
        String birthYearCalendar = birthYearFullText.substring(birthYearFullText.lastIndexOf(' ') + 1);
        editPersonBirthYear.setText(birthYear);
        editPersonBirthYearCalendar.setSelectedItem(birthYearCalendar);
        String deathYearFullText = person.getDeathYear();
        String deathYear = deathYearFullText.substring(0, deathYearFullText.lastIndexOf(' '));
        String deathYearCalendar = deathYearFullText.substring(deathYearFullText.lastIndexOf(' ') + 1);
        editPersonDeathYear.setText(deathYear);
        editPersonDeathYearCalendar.setSelectedItem(deathYearCalendar);
        editPersonIsMonarch.setSelected(person.getIsMonarch());
        editPersonIsUnknown.setSelected(person.getIsUnknown());
    }

    private void clearAddPersonPopup() {
        addPersonName.setText("");
        addPersonTitle.setText("");
        addPersonRegion.setText("");
        addPersonHouse.setText("");
        addPersonBirthYear.setText("");
        addPersonBirthYearCalendar.setSelectedIndex(0);
        addPersonDeathYear.setText("");
        addPersonBirthYearCalendar.setSelectedIndex(0);
        addPersonPopup.setVisible(false);
    }

    private void prepAddParentsPopup() {
        addParentsParentOneUnknown.setSelected(false);
        addParentsParentOneName.setText("");
        addParentsParentOneTitle.setText("");
        addParentsParentOneRegion.setText("");
        addParentsParentOneHouse.setText("");
        addParentsParentOneBirthYear.setText("");
        addParentsParentOneBirthYearCalendar.setSelectedIndex(0);
        addParentsParentOneDeathYear.setText("");
        addParentsParentOneDeathYearCalendar.setSelectedIndex(0);
        addParentsParentOneIsMonarch.setSelected(false);

        addParentsParentTwoUnknown.setSelected(false);
        addParentsParentTwoName.setText("");
        addParentsParentTwoTitle.setText("");
        addParentsParentTwoRegion.setText("");
        addParentsParentTwoHouse.setText("");
        addParentsParentTwoBirthYear.setText("");
        addParentsParentTwoBirthYearCalendar.setSelectedIndex(0);
        addParentsParentTwoDeathYear.setText("");
        addParentsParentTwoDeathYearCalendar.setSelectedIndex(0);
        addParentsParentTwoIsMonarch.setSelected(false);
    }

    private void prepSetParentsPopup() {
        setParentsSelectedPersons = treeImageGenerator.getSelected();
        setParentsParentOneOptions = new ArrayList<>();
        for(Person p : setParentsSelectedPersons) {
            for(Person q : setParentsSelectedPersons) {
                if(treeImageGenerator.getFamilyTree().getRelationship(p.getId(), q.getId()) != null) {
                    setParentsParentOneOptions.add(p);
                    break;
                }
            }
        }
        String[] parentOneList = new String[setParentsParentOneOptions.size() + 1];
        parentOneList[0] = "Select Parent One";
        for(int i = 0; i < setParentsParentOneOptions.size(); i++) {
            parentOneList[i + 1] = setParentsParentOneOptions.get(i).toString();
        }
        /*
        Parent Two List isn't enabled until Parent One is selected, and the list of available Parent 2s is determined
        by Parent One, so we just need a placeholder item for Parent Two to sit on.
         */
        String[] parentTwoFakeList = {"Select Parent Two"};
        setParentsParentOneSelect.setModel(new DefaultComboBoxModel<>(parentOneList));
        setParentsParentOneSelect.setSelectedIndex(0);
        setParentsParentTwoSelect.setModel(new DefaultComboBoxModel<>(parentTwoFakeList));
        setParentsParentTwoSelect.setSelectedIndex(0);
        setParentsParentTwoSelect.setEnabled(false);
        setParentsSubmit.setEnabled(false);
    }

    private void saveTree() {
        FamilyTreeFileManager.saveWithDialog(familyTree, this);
    }

    private void loadTree() {
        FamilyTree loadedTree = FamilyTreeFileManager.loadWithDialog(this);
        if(loadedTree != null) {
            familyTree = loadedTree;
            treeImageGenerator.setFamilyTree(familyTree);
            treeImageGenerator.generate();
            treeImageGenerator.draw();
            treePanel.setTreeImage(treeImageGenerator.getFamilyTreeImage());
        }
    }
}
