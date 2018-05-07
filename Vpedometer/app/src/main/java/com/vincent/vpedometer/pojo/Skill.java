package com.vincent.vpedometer.pojo;

/**
 * Created by Administrator on 2018/1/29.
 * All skill super class
 */

public abstract class Skill extends BaseElement {
    protected int attack = 80;
    protected int speed = 120;
    public boolean isRemoteSkill;


    public Skill(String filepath) {
        super(filepath);
    }

    public abstract void move();

    public int getAttack() {
        return attack;
    }

    public void setAttack(int attack) {
        this.attack = attack;
    }

}
