package io.github.soulslight.model.enemies;

/**
 * Come un nemico va disegnato: quale animazione, quale texture di ripiego se l'animazione non è
 * stata caricata, e con che dimensioni.
 *
 * <p>Prima queste informazioni vivevano nella view, dedotte con una catena di {@code instanceof}
 * divisa fra GameScreen e TextureManager: aggiungere un nemico significava ricordarsi di toccare
 * entrambi, e dimenticarsene non produceva alcun errore. Ora ogni nemico se le porta dietro.
 */
public record EnemySprite(String animKey, String fallbackTexture, float width, float height) {}
