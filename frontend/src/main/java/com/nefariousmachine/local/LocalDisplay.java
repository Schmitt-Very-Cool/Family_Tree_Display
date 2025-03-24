package com.nefariousmachine.local;

import com.nefariousmachine.data.FamilyTree;
import com.nefariousmachine.data.Person;
import com.nefariousmachine.generate.TreeImageGenerator;
import com.sun.source.tree.Tree;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

/**
 * Class to handle running the program locally (as opposed to running it as a web product). Uses javax.swing for
 * the actual GUI components. Calls methods in the classes of package display to generate the tree image.
 */
public class LocalDisplay extends JFrame {
    private String[] CALENDAR_LIST = {"CE", "BCE"};

    private boolean optionsEnabled = false;

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

    //Edit Person Popup
    private JFrame editPersonPopup = new JFrame("Edit Person");
    private JLabel editPersonNameLabel = new JLabel("Name");
    private JLabel editPersonTitleLabel = new JLabel("Title");
    private JLabel editPersonRegionLabel = new JLabel("Region");
    private JLabel editPersonHouseLabel = new JLabel("House");
    private JLabel editPersonBirthYearLabel = new JLabel("Birth Year");
    private JLabel editPersonDeathYearLabel = new JLabel("Death Year");
    private JTextField editPersonName = new JTextField(20);
    private JTextField editPersonTitle = new JTextField(20);
    private JTextField editPersonRegion = new JTextField(20);
    private JTextField editPersonHouse = new JTextField(20);
    private JTextField editPersonBirthYear = new JTextField(4);
    private JTextField editPersonDeathYear = new JTextField(4);
    private JComboBox<String> editPersonBirthYearCalendar = new JComboBox<>(CALENDAR_LIST);
    private JComboBox<String> editPersonDeathYearCalendar = new JComboBox<>(CALENDAR_LIST);
    private JCheckBox editPersonIsMonarch = new JCheckBox("Is Monarch");
    private JCheckBox editPersonIsSaint = new JCheckBox("Is Saint");
    private JButton editPersonSubmit = new JButton("Confirm Edits");
    private int editPersonId = -1;

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
        add(treePanel, "Center");
        pack();
        setVisible(true);
    }

    private void addEventHandlers() {
        treePanel.addMouseListener(new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent e) {
                treePanel.onClick(e);
                updateSelectionOptionsBar();
            }

            @Override public void mousePressed(MouseEvent e) {}
            @Override public void mouseReleased(MouseEvent e) {}
            @Override public void mouseEntered(MouseEvent e) {}
            @Override public void mouseExited(MouseEvent e) {}
        });

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

        editPersonButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                editPersonAction();
            }
        });

        editPersonSubmit.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                editPerson();
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
        editPersonPopup.add(editPersonIsSaint, gbc);
        gbc.gridy++; gbc.insets = new Insets(0, 10, 10, 10);
        editPersonPopup.add(editPersonSubmit, gbc);
        editPersonPopup.setSize(new Dimension(400, 400));
        editPersonPopup.pack();
    }

    private void enableSelectionOptionsBar() {
        if(optionsEnabled) {
            return;
        }
        add(selectionOptionsBar, "North");
        optionsEnabled = true;
        revalidate();
        repaint();
    }

    private void disableSelectionOptionsBar() {
        if(!optionsEnabled) {
            return;
        }
        remove(selectionOptionsBar);
        optionsEnabled = false;
        revalidate();
        repaint();
    }

    private void updateSelectionOptionsBar() {
        var selected = TreeImageGenerator.getSelected();
        boolean noParents = true;
        for(Person p : selected) {
            if(p.getParents() != null) {
                noParents = false;
            }
        }
        if(selected.length == 1) {
            editPersonButton.setEnabled(true);
        } else {
            editPersonButton.setEnabled(false);
        }
        if(noParents) {
            addParentsButton.setEnabled(true);
        } else {
            addParentsButton.setEnabled(false);
        }
        if(selected.length == 2) {
            addRelationshipButton.setEnabled(true);
        } else {
            addRelationshipButton.setEnabled(false);
        }
        if(selected.length == 0) {
            disableSelectionOptionsBar();
            return;
        } else {
            enableSelectionOptionsBar();
        }
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

        clearAddPersonPopup();
    }

    private void editPersonAction() {
        prepEditPersonInfo();
        editPersonPopup.setVisible(true);
    }

    private void editPerson() {
        Person personToEdit = TreeImageGenerator.getFamilyTree().getMemberById(editPersonId);
        personToEdit.setName(editPersonName.getText());
        personToEdit.setTitle(editPersonTitle.getText());
        personToEdit.setRegion(editPersonRegion.getText());
        personToEdit.setHouse(editPersonHouse.getText());
        personToEdit.setBirthYear(editPersonBirthYear.getText() + " " + editPersonBirthYearCalendar.getSelectedItem());
        personToEdit.setDeathYear(editPersonDeathYear.getText() + " " + editPersonDeathYearCalendar.getSelectedItem());
        personToEdit.setMonarch(editPersonIsMonarch.isSelected());
        personToEdit.setSaint(editPersonIsSaint.isSelected());

        TreeImageGenerator.generate();
        treePanel.setTreeImage(TreeImageGenerator.getFamilyTreeImage());
        editPersonPopup.setVisible(false);
    }

    private void prepEditPersonInfo() {
        Person[] personArray = TreeImageGenerator.getSelected();
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
        editPersonIsSaint.setSelected(person.getIsSaint());
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
}
