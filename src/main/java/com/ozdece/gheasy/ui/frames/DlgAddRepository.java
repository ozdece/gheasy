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

    private final JLabel lblLoadingRepositories = new JLabel("Loading Repositories...");

    private final JButton btnSave = new JButton("Add Repository");
    private final JButton btnClose = new JButton("Close");

    public DlgAddRepository(JFrame parent, RepositoryService repositoryService) {
        super(parent, true);
        setLayout(new BorderLayout());
        setBounds(350, 200, 600, 600);
        setTitle("Gheasy | Add Repository");

        this.repositoryService = repositoryService;

        this.add(buildCentralPanel(), BorderLayout.CENTER);
    }

    private JComponent buildCentralPanel() {
        final JPanel centralPanel = new JPanel();
        final GroupLayout groupLayout = new GroupLayout(centralPanel);

        final JLabel lblAccessibleOwners = new JLabel("Repository Owner: ");
        final JLabel lblSearchRepository = new JLabel("Search Repository: ");

        final JScrollPane spLstOwnerRepositories = new JScrollPane(lstOwnerRepositories);

        final JComponent bottomBar = buildBottomBar();

        lblLoadingRepositories.setVisible(false);

        groupLayout.setHorizontalGroup(
                groupLayout.createParallelGroup()
                        .addGroup(
                                groupLayout.createSequentialGroup()
                                        .addGap(20)
                                        .addComponent(lblAccessibleOwners)
                                        .addComponent(cmbAccessibleOwners, 250, 250, Integer.MAX_VALUE)
                                        .addGap(20)
                        )
                        .addGroup(
                                groupLayout.createSequentialGroup()
                                        .addGap(20)
                                        .addComponent(lblSearchRepository)
                                        .addComponent(txtSearchRepository, 250, 250, Integer.MAX_VALUE)
                                        .addGap(25)
                                        .addComponent(lblLoadingRepositories)
                                        .addGap(20)
                        )
                        .addGroup(
                                groupLayout.createSequentialGroup()
                                        .addGap(20)
                                        .addComponent(spLstOwnerRepositories)
                                        .addGap(20)
                        )
                        .addComponent(bottomBar)
        );

        groupLayout.setVerticalGroup(
                groupLayout.createSequentialGroup()
                        .addGap(10)
                        .addGroup(
                                groupLayout.createParallelGroup(GroupLayout.Alignment.CENTER)
                                        .addComponent(lblAccessibleOwners)
                                        .addComponent(cmbAccessibleOwners, 24, 24, 24)
                        )
                        .addGap(10)
                        .addGroup(
                                groupLayout.createParallelGroup(GroupLayout.Alignment.CENTER)
                                        .addComponent(lblSearchRepository)
                                        .addComponent(txtSearchRepository, 24, 24, 24)
                                        .addComponent(lblLoadingRepositories)
                        )
                        .addGap(10)
                        .addComponent(spLstOwnerRepositories)
                        .addComponent(bottomBar, 30, 30, 30)
                        .addGap(3)
        );

        centralPanel.setLayout(groupLayout);
        return centralPanel;
    }

    private JComponent buildBottomBar() {
        final JPanel bottomPanel = new JPanel();

        bottomPanel.setLayout(new FlowLayout(FlowLayout.RIGHT));

        bottomPanel.add(btnSave);
        bottomPanel.add(btnClose);

        return bottomPanel;
    }
}
