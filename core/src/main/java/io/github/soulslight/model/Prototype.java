package io.github.soulslight.model;

/** Pattern: Prototype Interface for game entities that can be cloned. */
public interface Prototype<T> extends Cloneable {
  T clone();
}
