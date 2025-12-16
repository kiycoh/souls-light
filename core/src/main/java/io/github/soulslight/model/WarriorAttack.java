package io.github.soulslight.model;

public class WarriorAttack extends AbstarctAttack{

    @Override
    public  void attack(){
        System.out.println("Attacco eseguito");
    }

    @Override
    public float getRange(){
        return 1.0f;
    }
    @Override
    public  float getDamage(){
        return 20.0f;
    }

    @Override
    public  float getAttackSpeed(){
        return 1.0f;
    }
    @Override
    public  String getSoundID(){
        return "sword_swing";
    }

}
