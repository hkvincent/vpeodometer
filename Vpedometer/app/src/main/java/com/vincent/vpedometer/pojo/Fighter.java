package com.vincent.vpedometer.pojo;

import org.cocos2d.types.CGPoint;

/**
 * Created by Administrator on 2018/1/28.
 * all fighter super class
 */

public abstract class Fighter extends BaseElement {

    protected int life = 200;// HP
    protected int attack = 30;// attack
    protected int speed = 50;// MOVE SPEED
    int MagicPoint = 30;
    int defened = 20;

    protected CGPoint startPoint;
    protected CGPoint endPoint;

    @Override
    public abstract void baseAction();

    public Fighter(String filepath) {
        super(filepath);
        setAnchorPoint(0.5f, 0);
    }

    public Fighter(String filepath, int life, int attack, int speed, int magicPoint, int defened) {
        super(filepath);
        setAnchorPoint(0.5f, 0);
        this.life = life;
        this.attack = attack;
        this.speed = speed;
        this.MagicPoint = magicPoint;
    }

    protected boolean isAttacking;

    public boolean isAttacking() {
        return isAttacking;
    }

    public void setAttacking(boolean isAttacking) {
        this.isAttacking = isAttacking;
    }


    public abstract void move();


    public abstract void attack(BaseElement element);


    public abstract void attacked(int attack);

    public abstract void winJump();
}
