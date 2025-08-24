package com.ozdece.ui.frames;

import com.ozdece.GheasyApplication;
import com.ozdece.github.auth.model.GithubUser;
import com.ozdece.github.repository.model.GithubRepository;
import com.ozdece.github.repository.GithubRepositoryService;
import com.ozdece.image.ImageService;
import com.ozdece.ui.SwingScheduler;
import com.ozdece.ui.models.GithubRepositoryListModel;
import com.ozdece.ui.renderers.GithubRepositoryListCellRenderer;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.File;

public class FrmRepository extends JFrame {

    private static final int FRAME_WIDTH = 600;
    private static final int FRAME_HEIGHT = 600;

    private final JList<GithubRepository> lstGithubRepository = new JList<>();

    private final JLabel lblLoggedInUser = new JLabel();

    private final JButton btnRemoveRepoFromList = new JButton("Remove Repository From List");
    private final JButton btnBrowseRepo = new JButton("Browse");

    private final Dimension screenDimension = Toolkit.getDefaultToolkit().getScreenSize();

    private final GithubUser githubUser;

    private final ImageService imageService;
    private final GithubRepositoryService githubRepositoryService;

    public FrmRepository(ImageService imageService, GithubRepositoryService githubRepositoryService, GithubUser githubUser) {
        super("Gheasy | " + githubUser.username());
        this.githubUser = githubUser;
        this.githubRepositoryService = githubRepositoryService;
        this.imageService = imageService;

        setupFrame();
        //TODO: load the avatar after the frame is loaded
        updateGithubAvatar();
        loadBookmarkedRepositories();
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

        lstGithubRepository.setCellRenderer(new GithubRepositoryListCellRenderer());
        final JScrollPane spLstGithubRepository = new JScrollPane(lstGithubRepository);

        final JLabel lblGheasy = new JLabel("Gheasy, Easy gh UI tool");
        lblGheasy.setFont(lblGheasy.getFont().deriveFont(24f).deriveFont(Font.BOLD));

        final JLabel lblVersion = new JLabel("Version: ");
        final JLabel lblVersionNumber = new JLabel(GheasyApplication.VERSION);

        final Font versionFont = lblVersion.getFont().deriveFont(14f);

        lblVersionNumber.setFont(versionFont);
        lblVersion.setFont(versionFont);

        final JPanel tbBottomBar = new JPanel();

        final String loggedInUserText = githubUser.fullName()
                .map(fullName -> String.format("%s (%s)", fullName, githubUser.username()))
                .orElse(githubUser.username());


        lblLoggedInUser.setText(loggedInUserText);
        lblLoggedInUser.setFont(lblLoggedInUser.getFont().deriveFont(16f));

        tbBottomBar.setLayout(new FlowLayout(FlowLayout.CENTER));

        tbBottomBar.add(lblLoggedInUser);

        final JLabel lblRepositories = new JLabel("Repositories");
        lblRepositories.setFont(lblRepositories.getFont().deriveFont(Font.BOLD));

        final JPanel pnlActionButtons = new JPanel();
        pnlActionButtons.setLayout(new FlowLayout(FlowLayout.RIGHT));

        btnRemoveRepoFromList.setEnabled(false);

        btnBrowseRepo.addActionListener(this::onBtnBrowseRepoClicked);

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
                        .addGap(8)
        );

        centralPanel.setLayout(groupLayout);
        return centralPanel;
    }

    private void onBtnBrowseRepoClicked(ActionEvent e) {
        final JFileChooser githubFolderChooser = new JFileChooser();

        githubFolderChooser.setApproveButtonText("Choose Folder");
        githubFolderChooser.setDialogTitle("Choose a GitHub Repository Folder");
        githubFolderChooser.setAcceptAllFileFilterUsed(false);
        githubFolderChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

        final int dialogResponse = githubFolderChooser.showOpenDialog(this);

        if (dialogResponse != JFileChooser.APPROVE_OPTION) return;

        final File selectedFile = githubFolderChooser.getSelectedFile();

        githubRepositoryService.isGitHubRepo(selectedFile)
                //TODO: Define constants for JOptionPane titles
                .doOnError(err ->
                        JOptionPane.showMessageDialog(
                                null,
                                "Cannot determine the selected folder as GitHub repository folder!",
                                "Gheasy | Error",
                                JOptionPane.ERROR_MESSAGE)
                )
                .then(githubRepositoryService.get(selectedFile))
                .flatMap(githubRepositoryService::upsertBookmark)
                .subscribe(githubRepository -> {
                    System.out.println(githubRepository);
                });

    }

    private void updateGithubAvatar() {

        imageService.saveGitHubAvatar(githubUser.avatarUrl())
                //TODO: log this via logger
                .doOnError(err -> System.err.println("An error occurred while saving Github avatar\n" + err.getMessage()))
                .publishOn(SwingScheduler.edt())
                .subscribe(maybeImageIcon -> maybeImageIcon.ifPresent(lblLoggedInUser::setIcon));
    }

    private void loadBookmarkedRepositories() {
        githubRepositoryService.getBookmarkedRepositories()
                .publishOn(SwingScheduler.edt())
                .subscribe(githubRepositories -> {
                    final GithubRepositoryListModel listModel = new GithubRepositoryListModel(githubRepositories.asList());

                    lstGithubRepository.setModel(listModel);
                });
    }

}
