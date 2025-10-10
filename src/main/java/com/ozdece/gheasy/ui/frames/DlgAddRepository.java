package com.ozdece.gheasy.ui.frames;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.ozdece.gheasy.github.auth.AuthService;
import com.ozdece.gheasy.github.auth.model.GithubOwner;
import com.ozdece.gheasy.github.repository.RepositoryService;
import com.ozdece.gheasy.github.repository.model.Repository;
import com.ozdece.gheasy.image.ImageService;
import com.ozdece.gheasy.ui.DialogTitles;
import com.ozdece.gheasy.ui.SwingScheduler;
import com.ozdece.gheasy.ui.models.AvailableOwnerComboBoxModel;
import com.ozdece.gheasy.ui.models.RepositoryListModel;
import com.ozdece.gheasy.ui.renderers.AvailableOwnerListCellRenderer;
import com.ozdece.gheasy.ui.renderers.RepositoryListCellRenderer;
import com.typesafe.config.Config;
import io.vavr.Tuple2;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ItemEvent;
import java.io.File;
import java.util.ArrayList;
import java.util.Optional;
import java.util.function.Consumer;

import static io.vavr.API.*;

public class DlgAddRepository extends JDialog {

    private static final Logger logger = LoggerFactory.getLogger(DlgAddRepository.class);
    private static final int SEARCH_REPO_INPUT_ENTRY_DELAY_MS = 700;

    private final RepositoryService repositoryService;
    private final ImageService imageService;
    private final AuthService authService;

    private final int ownerAvatarImageSize;
    private final int minCharacterToSearchRepos;

    private final JComboBox<GithubOwner> cmbAvailableOwners = new JComboBox<>();

    private final JList<Repository> lstOwnerRepositories = new JList<>();

    private final JTextField txtSearchRepository = new JTextField();

    private final JLabel lblLoadingRepositories = new JLabel("Loading Repositories...");

    private final JButton btnSave = new JButton("Add Repository");
    private final JButton btnClose = new JButton("Close");

    private final java.util.List<Consumer<Repository>> repositoryListeners = new ArrayList<>();

    private final Timer searchRepoTimer = new Timer(SEARCH_REPO_INPUT_ENTRY_DELAY_MS, this::onSearchRepoRequested);

    public DlgAddRepository(
            JFrame parent,
            RepositoryService repositoryService,
            AuthService authService,
            ImageService imageService,
            Config config
    ) {
        super(parent, true);
        setLayout(new BorderLayout());
        setBounds(350, 200, 600, 600);
        setTitle("Gheasy | Add Repository");

        searchRepoTimer.setRepeats(false);

        this.repositoryService = repositoryService;
        this.authService = authService;
        this.imageService = imageService;
        this.ownerAvatarImageSize = config.getInt("gheasy.images.owner-avatar-image-size");
        this.minCharacterToSearchRepos = config.getInt("gheasy.repository.min-character-to-search");

        this.add(buildCentralPanel(), BorderLayout.CENTER);

        cmbAvailableOwners.addItemListener(this::onOwnerItemChanged);
        txtSearchRepository.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                restartTimer();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                restartTimer();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {}
        });

        loadAvailableOwners();
    }

    public void addRepositoryListener(Consumer<Repository> repositoryConsumer) {
        this.repositoryListeners.add(repositoryConsumer);
    }

    private JComponent buildCentralPanel() {
        final JPanel centralPanel = new JPanel();
        final GroupLayout groupLayout = new GroupLayout(centralPanel);

        final JLabel lblAvailableOwners = new JLabel("Repository Owner: ");
        final JLabel lblSearchRepository = new JLabel("Search Repository: ");

        final JScrollPane spLstOwnerRepositories = new JScrollPane(lstOwnerRepositories);

        final JComponent bottomBar = buildBottomBar();

        lblLoadingRepositories.setVisible(false);
        cmbAvailableOwners.setEnabled(false);
        txtSearchRepository.setEnabled(false);

        groupLayout.setHorizontalGroup(
                groupLayout.createParallelGroup()
                        .addGroup(
                                groupLayout.createSequentialGroup()
                                        .addGap(20)
                                        .addComponent(lblAvailableOwners)
                                        .addComponent(cmbAvailableOwners, 250, 250, Integer.MAX_VALUE)
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
                                        .addComponent(lblAvailableOwners)
                                        .addComponent(cmbAvailableOwners, 24, 24, 24)
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

        btnClose.addActionListener(e -> this.dispose());
        btnSave.addActionListener(this::onSaveButtonClicked);

        return bottomPanel;
    }

    private void onSaveButtonClicked(ActionEvent e) {
       final int selectedRepositoryIndex = lstOwnerRepositories.getSelectedIndex();

       if (selectedRepositoryIndex == -1) {
           JOptionPane.showMessageDialog(
                   null,
                   "No repository selected to add",
                   DialogTitles.OPTION_PANE_ERROR_TITLE,
                   JOptionPane.ERROR_MESSAGE
           );

           return;
       }

       final Repository selectedRepository = lstOwnerRepositories.getSelectedValue();

       repositoryService.insertBookmark(selectedRepository)
               .doOnError(err -> {
                   logger.error("An error occurred while adding a new repository \"%s\"".formatted(selectedRepository.name()), err);

                   JOptionPane.showMessageDialog(
                           null,
                           "An error occurred while adding a new repository\n" + err.getMessage(),
                           DialogTitles.OPTION_PANE_ERROR_TITLE,
                           JOptionPane.ERROR_MESSAGE
                   );
               })
               .subscribeOn(Schedulers.boundedElastic())
               .publishOn(SwingScheduler.edt())
               .subscribe(addedRepository -> {
                   logger.info("Repository {}/{} successfully bookmarked.", addedRepository.owner().name(), addedRepository.name());
                   repositoryListeners
                           .forEach(listener -> listener.accept(addedRepository));

                    this.dispose();
               });
    }

    private void loadAvailableOwners() {
       authService.getRepositoryOwners()
               .flatMap(this::withOwnerAvatars)
               .doOnError(err -> {
                   logger.error("An error occurred while retrieving repository owners.", err);

                   //TODO: Generate dialogs via a utility class
                   JOptionPane.showMessageDialog(
                           null,
                           "An error occurred while retrieving repository owners.\n%s".formatted(err.getMessage()),
                           DialogTitles.OPTION_PANE_ERROR_TITLE,
                           JOptionPane.ERROR_MESSAGE
                   );
               })
               .subscribeOn(Schedulers.boundedElastic())
               .publishOn(SwingScheduler.edt())
               .subscribe(ownerAvatarMap -> {
                    cmbAvailableOwners.setModel(new AvailableOwnerComboBoxModel(ownerAvatarMap.keySet().asList()));
                    cmbAvailableOwners.setRenderer(new AvailableOwnerListCellRenderer(ownerAvatarMap));
                    cmbAvailableOwners.setSelectedIndex(cmbAvailableOwners.getItemCount() - 1);
                    cmbAvailableOwners.setEnabled(true);
                    txtSearchRepository.setEnabled(true);
                    cmbAvailableOwners.requestFocusInWindow();
               });
    }

    private Mono<ImmutableMap<GithubOwner, Optional<File>>> withOwnerAvatars(ImmutableList<GithubOwner> owners) {
        return Flux.fromIterable(owners)
                .flatMap(owner -> imageService
                        .saveImage(owner.avatarUrl(), ownerAvatarImageSize, ownerAvatarImageSize, "%s.png".formatted(owner.name()))
                        .map(maybeImage -> Tuple(owner, maybeImage))
                )
                .subscribeOn(Schedulers.boundedElastic())
                .collect(ImmutableMap.toImmutableMap(Tuple2::_1, Tuple2::_2));
    }

    private void restartTimer() {
        if (searchRepoTimer.isRunning()) {
            searchRepoTimer.restart();
        } else {
            searchRepoTimer.start();
        }
    }


    private void onSearchRepoRequested(ActionEvent e) {
        final int selectedOwnerIndex = cmbAvailableOwners.getSelectedIndex();

        if (selectedOwnerIndex == -1) {
            JOptionPane.showMessageDialog(
                    null,
                    "No repository owner chosen",
                    DialogTitles.OPTION_PANE_ERROR_TITLE,
                    JOptionPane.ERROR_MESSAGE
            );
            return;
        }
        final String searchText = txtSearchRepository.getText().trim();

        if (searchText.length() < minCharacterToSearchRepos) {
            return;
        }

        txtSearchRepository.setEnabled(false);
        lstOwnerRepositories.setEnabled(false);
        lstOwnerRepositories.setModel(new DefaultListModel<>());

        final GithubOwner owner = cmbAvailableOwners.getItemAt(selectedOwnerIndex);

        lblLoadingRepositories.setVisible(true);

        repositoryService
                .searchRepositoriesByOwner(owner, searchText)
                .doOnError(err -> {
                    logger.error(
                            "An error occurred while retrieving search result for the query \"%s\" with owner \"%s\""
                                    .formatted(searchText, owner.name()),
                            err);
                    JOptionPane.showMessageDialog(
                            null,
                            "An error occurred while retrieving search results for the repository owner \"%s\"\n%s".formatted(owner.name(), err.getMessage()),
                            DialogTitles.OPTION_PANE_ERROR_TITLE,
                            JOptionPane.ERROR_MESSAGE);
                })
                .publishOn(SwingScheduler.edt())
                .subscribeOn(Schedulers.boundedElastic())
                .subscribe(searchResults -> {
                    final RepositoryListModel model = new RepositoryListModel(searchResults);
                    final Optional<File> maybeOwnerIcon = imageService.getImageFile("%s.png".formatted(owner.name()));

                    lstOwnerRepositories.setCellRenderer(new RepositoryListCellRenderer(maybeOwnerIcon));

                    lstOwnerRepositories.setEnabled(true);
                    lstOwnerRepositories.setModel(model);
                    lblLoadingRepositories.setVisible(false);
                    txtSearchRepository.setEnabled(true);
                });

    }

    private void onOwnerItemChanged(ItemEvent e) {
        txtSearchRepository.setText("");
        lstOwnerRepositories.setModel(new DefaultListModel<>());
        txtSearchRepository.requestFocusInWindow();
    }

}
