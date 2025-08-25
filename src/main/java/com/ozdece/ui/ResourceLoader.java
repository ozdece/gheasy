package com.ozdece.ui;

import java.awt.*;
import java.util.Optional;

import static io.vavr.API.Try;

public class ResourceLoader {

    public static Optional<Image> loadImage(String imageResourcePath) {
        return Try(() -> Toolkit.getDefaultToolkit()
                .getImage(ResourceLoader.class.getClassLoader().getResource(imageResourcePath)))
                .toJavaOptional();
    }
}
