package com.vincent.vpedometer.pojo;

import org.cocos2d.actions.instant.CCCallFunc;
import org.cocos2d.actions.interval.CCMoveTo;
import org.cocos2d.actions.interval.CCSequence;
import org.cocos2d.nodes.CCDirector;

/**
 * Created by Administrator on 2018/1/29.
 * the remote skill
 */

public class RemoteSkill extends Skill {


    public RemoteSkill() {
        super("fight/bullet.png");
        setScale(2);
        this.isRemoteSkill = true;
    }

    public RemoteSkill(String filepath) {
        super(filepath);
    }

    /**
     * move the image from where to where
     */
    @Override
    public void move() {
        System.out.println("remote skill release");
        //how fast the skill move
        float t = (CCDirector.sharedDirector().winSize().width - getPosition().x)
                / speed;
        System.out.println("time :" + t);
        System.out.println("end point:" + CCDirector.sharedDirector().winSize().width + "----" + getPosition().y);
        //the skill move to right end of the screen
        CCMoveTo move = CCMoveTo.action(t, ccp(CCDirector.sharedDirector().winSize().width,
                getPosition().y));
        //call the destroy method to destory skill
        CCSequence sequence = CCSequence.actions(move, CCCallFunc.action(this, "destroy"));
        runAction(sequence);

    }

    @Override
    public void baseAction() {

    }
}
