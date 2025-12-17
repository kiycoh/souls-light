package io.github.soulslight.model;

/**
 * Pattern: Strategy (Concrete Strategy)
 * Implements a specific attack behavior (Mage).
 */
public class MageAttack extends AbstractAttack{
    @Override
    public  void attack(){
        System.out.println("Attacco eseguito");
    }

    @Override
    public float getRange(){
        return 13.0f;
    }
    @Override
    public  float getDamage(){
        return 25.0f;
    }

    @Override
    public  float getAttackSpeed(){
        return 0.5f;
    }
    @Override
    public  String getSoundID(){
        return "stick_sound";
    }

}
