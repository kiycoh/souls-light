package io.github.soulslight.model;

public class ArcherAttack extends AbstarctAttack {
    @Override
    public  void attack(){
        System.out.println("Attacco eseguito");
    }

    @Override
    public float getRange(){
        return 10.0f;
    }
    @Override
    public  float getDamage(){
        return 7.0f;
    }

    @Override
    public  float getAttackSpeed(){
        return 1.5f;
    }
    @Override
    public  String getSoundID(){
        return "bow_sound";
    }



}
