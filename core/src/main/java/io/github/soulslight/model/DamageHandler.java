package io.github.soulslight.model;

/** Pattern: Chain of Responsibility Abstract handler for processing damage. */
public abstract class DamageHandler {
  protected DamageHandler nextHandler;

  public void setNext(DamageHandler next) {
    this.nextHandler = next;
  }

  public float handleDamage(float amount) {
    float remainingDamage = process(amount);
    if (remainingDamage > 0 && nextHandler != null) {
      return nextHandler.handleDamage(remainingDamage);
    }
    return remainingDamage;
  }

  /**
   * Process the damage and return the remaining amount.
   *
   * @param amount Incoming damage.
   * @return Remaining damage to be passed to the next handler.
   */
  protected abstract float process(float amount);
}
