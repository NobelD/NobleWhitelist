package me.nobeld.noblewhitelist.model.base;

import java.util.logging.Logger;

public interface BaseVersioning {
    String name();
    String version();
    Logger logger();
}
