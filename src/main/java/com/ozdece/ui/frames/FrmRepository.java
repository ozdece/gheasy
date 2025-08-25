package com.ozdece.ui.frames;

import com.ozdece.GheasyApplication;
import com.ozdece.github.auth.model.GithubUser;
import com.ozdece.github.repository.model.GithubRepository;
import com.ozdece.github.repository.GithubRepositoryService;
import com.ozdece.image.ImageService;
import com.ozdece.ui.Fonts;
import com.ozdece.ui.SwingScheduler;
import com.ozdece.ui.models.GithubRepositoryListModel;
import com.ozdece.ui.renderers.GithubRepositoryListCellRenderer;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;
import java.util.ArrayList;

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
        super("Gheasy | User: " + githubUser.username());
        this.githubUser = githubUser;
        this.githubRepositoryService = githubRepositoryService;
        this.imageService = imageService;

        setupFrame();
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

        btnRemoveRepoFromList.addActionListener(this::onRemoveRepoFromListClicked);

        lstGithubRepository.addMouseListener(new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent e) {

                // If the right click button is clicked
                if (e.getButton() == MouseEvent.BUTTON3) {
                    final Point mousePoint = new Point(e.getX(), e.getY());
                    final int positionIndex = lstGithubRepository.locationToIndex(mousePoint);

                    // If we can't point any item then end the call
                    if (positionIndex == -1)
                        return;

                    lstGithubRepository.setSelectedIndex(positionIndex);
                    showBookmarkPopupMenu(lstGithubRepository, mousePoint.x, mousePoint.y);
                }

                // We don't switch to dashboard if it's not double clicked!
                if (e.getClickCount() != 2) return;

                final int selectedIndex = lstGithubRepository.getSelectedIndex();

                // If no element is selected then don't do anything
                if (selectedIndex == -1) return;

                final GithubRepository selectedRepo = lstGithubRepository.getSelectedValue();

                loadMainDashboard(selectedRepo);
            }

            @Override
            public void mousePressed(MouseEvent e) {}

            @Override
            public void mouseReleased(MouseEvent e) {}

            @Override
            public void mouseEntered(MouseEvent e) {}

            @Override
            public void mouseExited(MouseEvent e) {}
        });

        lstGithubRepository.addListSelectionListener(e -> {
            btnRemoveRepoFromList.setEnabled(true);
        });

        lstGithubRepository.setCellRenderer(new GithubRepositoryListCellRenderer());
        final JScrollPane spLstGithubRepository = new JScrollPane(lstGithubRepository);

        spLstGithubRepository.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);

        final JLabel lblGheasy = new JLabel("Gheasy, Easy gh UI tool");
        lblGheasy.setFont(Fonts.boldFontWithSize(24));

        final JLabel lblVersion = new JLabel("Version: ");
        final JLabel lblVersionNumber = new JLabel(GheasyApplication.VERSION);

        final Font versionFont = Fonts.fontWithSize(14);

        lblVersionNumber.setFont(versionFont);
        lblVersion.setFont(versionFont);

        final JPanel tbBottomBar = new JPanel();

        final String loggedInUserText = githubUser.fullName()
                .map(fullName -> String.format("%s (%s)", fullName, githubUser.username()))
                .orElse(githubUser.username());


        lblLoggedInUser.setText(loggedInUserText);
        lblLoggedInUser.setFont(Fonts.fontWithSize(16));

        tbBottomBar.setLayout(new FlowLayout(FlowLayout.CENTER));

        tbBottomBar.add(lblLoggedInUser);

        final JLabel lblRepositories = new JLabel("Repositories");
        lblRepositories.setFont(Fonts.BOLD_FONT);

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
                .then(githubRepositoryService.get(selectedFile))
                .flatMap(githubRepositoryService::upsertBookmark)
                .doOnError(err ->
                        JOptionPane.showMessageDialog(
                                null,
                                "Cannot determine the selected folder as GitHub repository folder!",
                                "Gheasy | Error",
                                JOptionPane.ERROR_MESSAGE)
                )
                .publishOn(SwingScheduler.edt())
                .subscribe(githubRepository -> {
                    ((GithubRepositoryListModel) lstGithubRepository.getModel())
                            .upsertGithubRepository(githubRepository);

                    loadMainDashboard(githubRepository);
                    this.dispose();
                });
    }

    public void updateGithubAvatar() {

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
                    final GithubRepositoryListModel listModel = new GithubRepositoryListModel(new ArrayList<>(githubRepositories.asList()));

                    lstGithubRepository.setModel(listModel);
                });
    }

    private void loadMainDashboard(GithubRepository githubRepository) {
        final FrmMainDashboard frmMainDashboard = new FrmMainDashboard(githubUser, githubRepository);

        frmMainDashboard.setVisible(true);
        this.dispose();
    }

    private void showBookmarkPopupMenu(JComponent parent, int xPos, int yPos) {
        final JPopupMenu popupMenu = new JPopupMenu();

        final JMenuItem miOpenRepository = new JMenuItem("Load Repository");
        final JMenuItem miRemoveRepository = new JMenuItem("Remove From List");

        miOpenRepository.addActionListener(e -> {
            //TODO: make sure that item index is not -1 for safety
            final GithubRepository selectedGithubRepository = lstGithubRepository.getSelectedValue();
            this.loadMainDashboard(selectedGithubRepository);
        });

        miRemoveRepository.addActionListener(this::onRemoveRepoFromListClicked);

        popupMenu.add(miOpenRepository);
        popupMenu.add(miRemoveRepository);

        popupMenu.show(parent, xPos, yPos);
    }

    private void onRemoveRepoFromListClicked(ActionEvent e) {

        final int selectedIndex = lstGithubRepository.getSelectedIndex();

        if (selectedIndex == -1) {
            JOptionPane.showMessageDialog(
                    null,
                    "Please first select a repository to remove from list.",
                    //TODO: Set up a class where you retrieve these titles
                    "Gheasy | Error",
                    JOptionPane.ERROR_MESSAGE
            );
            return;
        }

        final GithubRepository selectedGithubRepository = lstGithubRepository.getSelectedValue();
        githubRepositoryService
                .removeBookmark(selectedGithubRepository)
                .doOnError(err -> JOptionPane.showMessageDialog(
                        null,
                        "An error occurred while removing the repository from list\n" + err.getMessage(),
                        //TODO: Set up a class where you retrieve these titles
                        "Gheasy | Error",
                        JOptionPane.ERROR_MESSAGE
                ))
                .publishOn(SwingScheduler.edt())
                .subscribe(updatedBookmarks -> {
                    final GithubRepositoryListModel model = new GithubRepositoryListModel(new ArrayList<>(updatedBookmarks));
                    lstGithubRepository.setModel(model);
                });
    }

}
