package com.ozdece.gheasy.ui.frames;

import com.ozdece.gheasy.github.auth.model.GithubUser;
import com.ozdece.gheasy.github.repository.model.GithubRepository;

import javax.swing.*;

public class FrmMainDashboard extends JFrame {

    private final GithubUser user;
    private final GithubRepository repository;

    public FrmMainDashboard(GithubUser user, GithubRepository repository) {
        super(String.format("Gheasy | %s Dashboard", repository.nameWithOwner()));

        setBounds(250, 250, 1000, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        this.user = user;
        this.repository = repository;
    }

}
