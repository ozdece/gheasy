package com.ozdece.gheasy.ui.frames;

import com.ozdece.gheasy.github.auth.model.GithubOwner;
import com.ozdece.gheasy.github.repository.RepositoryService;
import com.ozdece.gheasy.github.repository.model.Repository;

import javax.swing.*;
import java.awt.*;

public class DlgAddRepository extends JDialog {

    private final RepositoryService repositoryService;

    private final JComboBox<GithubOwner> cmbAccessibleOwners = new JComboBox<>();

    private final JList<Repository> lstOwnerRepositories = new JList<>();

    private final JTextField txtSearchRepository = new JTextField();

    public DlgAddRepository(JFrame parent, RepositoryService repositoryService) {
        super(parent, true);
        setLayout(new BorderLayout());

        this.repositoryService = repositoryService;

        this.add(buildCentralPanel(), BorderLayout.CENTER);
    }

    private JComponent buildCentralPanel() {
        final JPanel centralPanel = new JPanel();
        final GroupLayout groupLayout = new GroupLayout(centralPanel);

        final JLabel lblAccessibleOwners = new JLabel("Repository Owner: ");
        final JLabel lblSearchRepository = new JLabel("Search Repository: ");

        final JScrollPane spLstOwnerRepositories = new JScrollPane(lstOwnerRepositories);

        final JButton btnSave = new JButton("Add");
        final JButton btnClose = new JButton("Close");

        groupLayout.setHorizontalGroup(
                groupLayout.createParallelGroup()
                        .addGroup(
                                groupLayout.createSequentialGroup()
                                        .addGap(5)
                                        .addComponent(lblAccessibleOwners)
                                        .addComponent(cmbAccessibleOwners)
                        )
                        .addGroup(
                                groupLayout.createSequentialGroup()
                                        .addGap(5)
                                        .addComponent(lblSearchRepository)
                                        .addComponent(txtSearchRepository)
                                        .addGap(5)
                        )
                        .addGroup(
                                groupLayout.createSequentialGroup()
                                        .addGap(5)
                                        .addComponent(spLstOwnerRepositories)
                                        .addGap(5)
                        )
                        .addGroup(
                                groupLayout.createSequentialGroup()
                                        .addComponent(btnSave)
                                        .addComponent(btnClose)
                        )
        );

        centralPanel.setLayout(groupLayout);
        return centralPanel;
    }

}
