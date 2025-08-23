package com.ozdece;

import com.ozdece.github.repository.GithubRepository;

import javax.swing.*;
import java.awt.*;

public class FrmRepository extends JFrame {

    private static final int FRAME_WIDTH = 600;
    private static final int FRAME_HEIGHT = 600;

    private static final String TITLE = "Gheasy";

    private final JList<GithubRepository> lstGithubRepository = new JList<>();

    private final JButton btnRemoveRepoFromList = new JButton("Remove Repository From List");
    private final JButton btnBrowseRepo = new JButton("Browse");
    private final JButton btnLoginGh = new JButton("Login gh via Web");

    private final Dimension screenDimension = Toolkit.getDefaultToolkit().getScreenSize();

    public FrmRepository() {
        super(TITLE);
        setupFrame();
    }

    private void setupFrame() {
        final int xPos = (screenDimension.width - FRAME_WIDTH) / 2;
        final int yPos = (screenDimension.height - FRAME_HEIGHT) / 2;

        setBounds(xPos, yPos, FRAME_WIDTH, FRAME_HEIGHT);

        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        setLayout(new BorderLayout());
        this.add(setupCentralPanel(), BorderLayout.CENTER);
    }

    private JComponent setupCentralPanel() {
        final JPanel centralPanel = new JPanel();
        final GroupLayout groupLayout = new GroupLayout(centralPanel);

        final JScrollPane spLstGithubRepository = new JScrollPane(lstGithubRepository);

        final JLabel lblGheasy = new JLabel("Gheasy, Easy gh UI tool");
        lblGheasy.setFont(lblGheasy.getFont().deriveFont(24f).deriveFont(Font.BOLD));

        final JLabel lblVersion = new JLabel("Version: ");
        // TODO: Read the version from a config file
        final JLabel lblVersionNumber = new JLabel("0.1.0");

        final Font versionFont = lblVersion.getFont().deriveFont(14f);

        lblVersionNumber.setFont(versionFont);
        lblVersion.setFont(versionFont);

        final JPanel tbBottomBar = new JPanel();

        final JLabel lblLoggedInUser = new JLabel("No user logged in");
        lblLoggedInUser.setFont(lblLoggedInUser.getFont().deriveFont(18f));

        tbBottomBar.setLayout(new FlowLayout(FlowLayout.CENTER));

        tbBottomBar.add(lblLoggedInUser);
        tbBottomBar.add(btnLoginGh);

        final JLabel lblRepositories = new JLabel("Repositories");
        lblRepositories.setFont(lblRepositories.getFont().deriveFont(Font.BOLD));

        final JPanel pnlActionButtons = new JPanel();
        pnlActionButtons.setLayout(new FlowLayout(FlowLayout.RIGHT));

        btnRemoveRepoFromList.setEnabled(false);

        pnlActionButtons.add(btnBrowseRepo);
        pnlActionButtons.add(btnRemoveRepoFromList);

        groupLayout.setHorizontalGroup(
                groupLayout.createParallelGroup()
                        .addGroup(
                                groupLayout.createSequentialGroup()
                                        .addGap(40)
                                        .addComponent(lblGheasy)
                        )
                        .addGroup(
                                groupLayout.createSequentialGroup()
                                        .addGap(40)
                                        .addComponent(lblVersion)
                                        .addComponent(lblVersionNumber)
                        )
                        .addGroup(
                                groupLayout.createSequentialGroup()
                                        .addGap(50)
                                        .addComponent(lblRepositories)
                        )
                        .addGroup(
                                groupLayout.createSequentialGroup()
                                        .addGap(40)
                                        .addComponent(spLstGithubRepository)
                                        .addGap(40)
                        )
                        .addGroup(
                                groupLayout
                                        .createSequentialGroup()
                                        .addGap(40)
                                        .addComponent(pnlActionButtons)
                                        .addGap(40)

                        )
                        .addComponent(tbBottomBar, 600, 600, Integer.MAX_VALUE)
        );

        groupLayout.setVerticalGroup(
                groupLayout.createSequentialGroup()
                        .addGap(20)
                        .addComponent(lblGheasy)
                        .addGroup(
                                groupLayout.createParallelGroup(GroupLayout.Alignment.CENTER)
                                        .addComponent(lblVersion)
                                        .addComponent(lblVersionNumber)
                        )
                        .addGap(20)
                        .addComponent(lblRepositories)
                        .addGap(10)
                        .addComponent(spLstGithubRepository)
                        .addGap(10)
                        .addComponent(pnlActionButtons, 30, 30, 30)
                        .addGap(10)
                        .addComponent(tbBottomBar, 30, 30, 30)
                        .addGap(5)
        );

        centralPanel.setLayout(groupLayout);
        return centralPanel;
    }


}
