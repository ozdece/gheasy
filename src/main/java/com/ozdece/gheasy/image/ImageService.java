package com.ozdece.gheasy.image;


import reactor.core.publisher.Mono;

import java.io.File;
import java.util.Optional;

public interface ImageService {
    Mono<Optional<File>> saveImage(String avatarUrl, int width, int height);
}
