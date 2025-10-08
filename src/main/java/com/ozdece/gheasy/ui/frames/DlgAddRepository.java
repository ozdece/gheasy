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
import com.ozdece.gheasy.ui.renderers.AvailableOwnerListCellRenderer;
import io.vavr.Tuple2;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.util.Optional;

import static io.vavr.API.*;

public class DlgAddRepository extends JDialog {

    private static final Logger logger = LoggerFactory.getLogger(DlgAddRepository.class);
    private static final int OWNER_AVATAR_IMAGE_SIZE = 16;

    private final RepositoryService repositoryService;
    private final ImageService imageService;
    private final AuthService authService;

    private final JComboBox<GithubOwner> cmbAvailableOwners = new JComboBox<>();

    private final JList<Repository> lstOwnerRepositories = new JList<>();

    private final JTextField txtSearchRepository = new JTextField();

    private final JLabel lblLoadingRepositories = new JLabel("Loading Repositories...");

    private final JButton btnSave = new JButton("Add Repository");
    private final JButton btnClose = new JButton("Close");

    public DlgAddRepository(
            JFrame parent,
            RepositoryService repositoryService,
            AuthService authService,
            ImageService imageService
    ) {
        super(parent, true);
        setLayout(new BorderLayout());
        setBounds(350, 200, 600, 600);
        setTitle("Gheasy | Add Repository");

        this.repositoryService = repositoryService;
        this.authService = authService;
        this.imageService = imageService;

        this.add(buildCentralPanel(), BorderLayout.CENTER);

        loadAvailableOwners();
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

        return bottomPanel;
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
                        .saveImage(owner.avatarUrl(), OWNER_AVATAR_IMAGE_SIZE, OWNER_AVATAR_IMAGE_SIZE)
                        .map(maybeImage -> Tuple(owner, maybeImage))
                )
                .subscribeOn(Schedulers.boundedElastic())
                .collect(ImmutableMap.toImmutableMap(Tuple2::_1, Tuple2::_2));
    }

}
