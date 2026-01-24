package io.github.soulslight.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import io.github.soulslight.model.entities.Player.PlayerClass;
import io.github.soulslight.utils.GdxTestExtension;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

/**
 * TDD Unit tests for PlayerClass metadata used in ClassSelectionScreen.
 */
@ExtendWith(GdxTestExtension.class)
public class ClassSelectionTest {

    @Nested
    @DisplayName("PlayerClass Metadata Tests")
    class PlayerClassMetadataTests {

        @Test
        @DisplayName("Warrior should have high HP, low Will, and Shield Bash ability")
        void testWarriorStats() {
            PlayerClass warrior = PlayerClass.WARRIOR;
            assertEquals(600, warrior.getBaseHP(), "Warrior should have high HP");
            assertEquals(30, warrior.getBaseWill(), "Warrior should have low Will");
            assertEquals("Shield Bash", warrior.getSpecialAbility());
        }

        @Test
        @DisplayName("Mage should have low HP, high Will, and Arcane Blast ability")
        void testMageStats() {
            PlayerClass mage = PlayerClass.MAGE;
            assertEquals(350, mage.getBaseHP(), "Mage should have low HP");
            assertEquals(100, mage.getBaseWill(), "Mage should have high Will");
            assertEquals("Arcane Blast", mage.getSpecialAbility());
        }

        @Test
        @DisplayName("Thief should have medium HP, medium Will, and Shadow Step ability")
        void testThiefStats() {
            PlayerClass thief = PlayerClass.THIEF;
            assertEquals(400, thief.getBaseHP(), "Thief should have medium HP");
            assertEquals(60, thief.getBaseWill(), "Thief should have medium Will");
            assertEquals("Shadow Step", thief.getSpecialAbility());
        }

        @Test
        @DisplayName("Archer should have medium-high HP, medium Will, and Rain of Arrows ability")
        void testArcherStats() {
            PlayerClass archer = PlayerClass.ARCHER;
            assertEquals(450, archer.getBaseHP(), "Archer should have medium-high HP");
            assertEquals(50, archer.getBaseWill(), "Archer should have medium Will");
            assertEquals("Rain of Arrows", archer.getSpecialAbility());
        }

        @Test
        @DisplayName("All classes should have non-null special abilities")
        void testAllClassesHaveSpecialAbilities() {
            for (PlayerClass playerClass : PlayerClass.values()) {
                assertNotNull(playerClass.getSpecialAbility(), playerClass.name() + " should have a special ability");
            }
        }

        @Test
        @DisplayName("All classes should have positive HP")
        void testAllClassesHavePositiveHP() {
            for (PlayerClass playerClass : PlayerClass.values()) {
                assert playerClass.getBaseHP() > 0 : playerClass.name() + " should have positive HP";
            }
        }

        @Test
        @DisplayName("All classes should have non-negative Will")
        void testAllClassesHaveNonNegativeWill() {
            for (PlayerClass playerClass : PlayerClass.values()) {
                assert playerClass.getBaseWill() >= 0 : playerClass.name() + " should have non-negative Will";
            }
        }
    }
}
