package io.github.soulslight.model.physics;

/**
 * Ciò che il dominio riconosce come partecipante a una collisione.
 *
 * <p>Box2D espone lo userData di un corpo come {@code Object}. {@link Box2DPhysicsAdapter} lo
 * traduce in {@code Collidable} una volta sola, al confine: oltre quel punto il gioco non vede più
 * né {@code Fixture} né {@code Object}, e il disaccoppiamento dal motore fisico che {@link
 * CollisionHandler} dichiara diventa reale.
 *
 * <p>I muri non lo implementano: in Box2D sono corpi statici senza userData, e il dominio li vede
 * come {@code null}.
 */
public interface Collidable {}
