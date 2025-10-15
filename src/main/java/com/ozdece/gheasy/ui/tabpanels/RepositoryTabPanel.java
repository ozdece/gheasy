package com.ozdece.gheasy.ui.tabpanels;

import com.ozdece.gheasy.github.repository.model.Repository;

public interface RepositoryTabPanel {
    Repository getRepository();
    TabPanelType panelType();
    void updatePanel();
}
