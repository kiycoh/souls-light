package io.github.soulslight.model;

/** Pattern: Abstract Factory (Concrete Factory) Creates enemies for the Forest environment. */
public class ForestEnemyFactory implements EnemyFactory {

  private final Enemy goblinPrototype;
  private final Enemy elfArcherPrototype;

  public ForestEnemyFactory() {
    // Initialize prototypes
    goblinPrototype =
        new Enemy("Goblin", 30, new WarriorAttack()); // Reusing WarriorAttack for simplicity
    elfArcherPrototype = new Enemy("Elf Archer", 20, new ArcherAttack());
  }

  @Override
  public Enemy createMeleeEnemy() {
    return goblinPrototype.clone();
  }

  @Override
  public Enemy createRangedEnemy() {
    return elfArcherPrototype.clone();
  }
}
