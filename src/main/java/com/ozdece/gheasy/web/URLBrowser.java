package com.ozdece.gheasy.web;

import reactor.core.publisher.Mono;

import java.awt.*;
import java.net.URI;
import java.util.Optional;

public class URLBrowser {

    public static Mono<Void> browse(URI uri) {
        return Mono.fromCallable(() -> {
                    final Optional<Desktop> maybeDesktop = Desktop.isDesktopSupported()
                            ? Optional.of(Desktop.getDesktop())
                            : Optional.empty();

                    if (maybeDesktop.isPresent()) {
                        final Desktop desktop = maybeDesktop.get();
                        if (desktop.isSupported(Desktop.Action.BROWSE)) {
                            desktop.browse(uri);
                            return true;
                        }
                    }

                    return false;
                })
                .then();
    }

}
