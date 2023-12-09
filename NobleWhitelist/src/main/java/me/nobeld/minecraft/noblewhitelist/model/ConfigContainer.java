package me.nobeld.minecraft.noblewhitelist.model;

public record ConfigContainer<T>(String path, T def) {
}
