package io.github.soulslight.model;

/**
 * Pattern: Abstract Factory (Concrete Factory)
 * Creates enemies for the Dungeon environment.
 */
public class DungeonEnemyFactory implements EnemyFactory {

    private final Enemy skeletonPrototype;
    private final Enemy darkMagePrototype;

    public DungeonEnemyFactory() {
        skeletonPrototype = new Enemy("Skeleton", 40, new WarriorAttack());
        darkMagePrototype = new Enemy("Dark Mage", 25, new MageAttack());
    }

    @Override
    public Enemy createMeleeEnemy() {
        return skeletonPrototype.clone();
    }

    @Override
    public Enemy createRangedEnemy() {
        return darkMagePrototype.clone();
    }
}

