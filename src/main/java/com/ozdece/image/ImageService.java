package com.ozdece.image;


import reactor.core.publisher.Mono;

import javax.swing.*;
import java.util.Optional;

public interface ImageService {
    Mono<Optional<ImageIcon>> saveGitHubAvatar(String avatarUrl);
}
