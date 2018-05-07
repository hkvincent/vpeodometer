package com.vincent.vpedometer.pojo;

import com.vincent.vpedometer.ui.enginne.GameEnginee;
import com.vincent.vpedometer.ui.layer.MapLayer;
import com.vincent.vpedometer.utils.GameUtils;

import org.cocos2d.actions.CCScheduler;
import org.cocos2d.actions.base.CCAction;
import org.cocos2d.actions.base.CCRepeatForever;
import org.cocos2d.actions.instant.CCCallFunc;
import org.cocos2d.actions.interval.CCAnimate;
import org.cocos2d.actions.interval.CCDelayTime;
import org.cocos2d.actions.interval.CCJumpBy;
import org.cocos2d.actions.interval.CCMoveTo;
import org.cocos2d.actions.interval.CCRotateBy;
import org.cocos2d.actions.interval.CCSequence;
import org.cocos2d.actions.interval.CCSpawn;
import org.cocos2d.nodes.CCLabel;
import org.cocos2d.sound.SoundEngine;
import org.cocos2d.types.CGPoint;
import org.cocos2d.types.util.CGPointUtil;

/**
 * Created by Administrator on 2018/1/28.
 * the fighter class to handle fighting logic
 */

public class TheFighter extends Fighter {

    private TheFighter mAttackedFither;//the target our figther to attack
    private boolean isMyFighter;//set the flag to tell game engine which one is my fighter
    public static boolean win;
    private CCLabel mLabel;

    public boolean isMyFighter() {
        return isMyFighter;
    }

    public void setMyFighter(boolean myFighter) {
        isMyFighter = myFighter;
    }


    public TheFighter(CGPoint startPoint, CGPoint endPoint) {
        super("zombies/walk/z_1_01.png");
        this.startPoint = startPoint;//start area
        this.endPoint = endPoint;//end area
        //determine the fighter need to flip or not
        if (startPoint.x < endPoint.x) {
            this.setFlipX(true);
            isMyFighter = true;


        }
        //the total life for progress bar
        theFullLife = this.life;
        this.setPosition(startPoint);
        move();
    }

    /**
     * the move image to be base action of the fighter
     */
    @Override
    public void baseAction() {
        CCAction animate = GameUtils.animate(
                "zombies/walk/z_1_%02d.png", 7, true);
        this.runAction(animate);
    }

    //move to end point
    @Override
    public void move() {
        CCMoveTo move = CCMoveTo
                .action(CGPointUtil.distance(getPosition(), endPoint) / speed,
                        endPoint);

        CCSequence s = CCSequence.actions(move,
                CCCallFunc.action(this, "destroy"));
        baseAction();
        this.runAction(s);
    }

    /**
     * the pre attack logic
     *
     * @param element the attacked fighter
     */
    @Override
    public void attack(BaseElement element) {
        if (isMyFighter()) {
            mLabel = (CCLabel) this.getParent().getParent().getChildByTag(
                    MapLayer.TAG_TOTAL_MONEY);
            mLabel.setString(String.valueOf(this.life));
        }
        if (element instanceof TheFighter) {
            mAttackedFither = (TheFighter) element;
            attackAnimation();
            //every second to do the attack, in the other hand fighter life will be reduced
            CCScheduler myScheduler = CCScheduler.sharedScheduler();
            myScheduler.schedule("doAttack", this, 1f, false);

        }

    }

    /**
     * the attack image
     */
    public void attackAnimation() {
        this.stopAllActions();
        CCAction animate = GameUtils.animate("zombies/attack/z_1_attack_%02d.png", 10,
                true);
        this.runAction(animate);
    }


    /**
     * the attack logic
     *
     * @param f
     */
    public void doAttack(float f) {
        if (mAttackedFither != null) {
            //each attack will do how many damage
            mAttackedFither.attacked(attack);
            System.out.println("isMyFighter:" + mAttackedFither.isMyFighter() + "mAttackedFither's life:" + mAttackedFither.getLife());
            //when the attacked fighter life less than 1
            if (mAttackedFither.getLife() <= 0) {
                //unscheulde all attack logic
                CCScheduler.sharedScheduler().unschedule("doAttack", this);
                CCScheduler.sharedScheduler().unschedule("attack", GameEnginee.getInstance());
                //and set I am win
                if (isMyFighter())
                    win = true;

                mAttackedFither = null;
                //stop all action of my fighter
                this.stopAllActions();
                CCDelayTime delay = CCDelayTime.action(5);
                //winner dancing
                CCSequence actions = CCSequence.actions(CCCallFunc.action(this, "winJump"), delay,
                        CCCallFunc.action(GameEnginee.getInstance(), "gameOver"));
                isAttacking = false;
                this.runAction(actions);
            }
        }
    }

    boolean isDying;
    int hasBeenDropLife;
    int theFullLife;

    /**
     * the attacked logic
     *
     * @param attack
     */
    @Override
    public void attacked(int attack) {
        //the damage is enemy attack power min our defend
        int damage = attack - defened;
        this.life -= damage;
        hasBeenDropLife += damage;
        //set the progress bar to show enemy life
        if (!isMyFighter()) {
            System.out.println("hasBeenDropLife" + hasBeenDropLife);
            int theProgress = (int) (((float) hasBeenDropLife / (float) theFullLife) * 100);
            System.out.println("theProgress" + theProgress);
            GameEnginee.getInstance().progressTimer.setPercentage(theProgress);
        } else {
            //set the label to tell user our fighter life
            mLabel.setString(String.valueOf(this.life));
        }
        //when our fighter life is less than 1
        if (this.life <= 0) {
            this.stopAllActions();
            //show the dying image to user
            if (!isAttacking()) {
                CCAnimate animate1 = (CCAnimate) GameUtils.animate(
                        "zombies/head/z_1_head_%02d.png", 6,
                        false);
                CCAnimate animate2 = (CCAnimate) GameUtils.animate(
                        "zombies/die/z_1_die_%02d.png", 6,
                        false);
                CCSequence sequence = CCSequence.actions(animate1, animate2,
                        CCCallFunc.action(this, "died"));
                this.runAction(sequence);
            } else {
                //if the dying during the attacking, then show another set image to user
                CCAnimate animate = (CCAnimate) GameUtils
                        .animate(
                                "zombies/attack_losthead/z_1_attack_losthead_%02d.png",
                                8, false);
                CCAction die = GameUtils.animate(
                        "zombies/die/z_1_die_%02d.png", 6,
                        false);
                CCSequence sequence = CCSequence.actions((CCAnimate) animate,
                        (CCAnimate) die, CCCallFunc.action(this, "died"));
                this.runAction(sequence);
            }

        }
    }

    /**
     * the winner dancing
     */
    @Override
    public void winJump() {
        setAnchorPoint(0.5f, 0.5f);
        CCJumpBy jump = CCJumpBy.action(1.5f, ccp(10, 100), 30, 3);
        CCRotateBy rotate = CCRotateBy.action(1, 360);
        CCSpawn spawn = CCSpawn.actions(jump, rotate);
        CCSequence s = CCSequence.actions(spawn, spawn.reverse());
        CCRepeatForever repeat = CCRepeatForever.action(s);
        runAction(repeat);
        SoundEngine engine = SoundEngine.sharedEngine();
        //engine.playSound(CCDirector.theApp, R.raw.psy, true);
    }

    /**
     * the skill releas logic for attacking enemy
     *
     * @param skill which type skill
     */
    public void releaseSkill(Skill skill) {
        //set the skill position is ahead the fighter
        skill.setPosition(ccp(getPosition().x + 30, getPosition().y + 35));
        //show the skill on the map
        this.getParent().addChild(skill);
        if (skill.isRemoteSkill) {
            System.out.println("release skill" + this + "====" + skill);
            //stop fighter action when releasing skill
            this.stopAllActions();
            //skill start attack
            skill.move();
            //determine which set animation fighter need to run
            if (mAttackedFither != null) {
                float crossPoint = getPosition().x - mAttackedFither.getPosition().x;
                if (Math.abs(crossPoint) < 60) {
                    attackAnimation();
                } else {
                    move();
                }
            } else
                move();
        } else {
            //not remote skill
            this.stopAllActions();
            skill.baseAction();
            if (mAttackedFither != null) {
                float crossPoint = getPosition().x - mAttackedFither.getPosition().x;
                if (Math.abs(crossPoint) < 60) {
                    attackAnimation();
                } else {
                    move();
                }
            } else
                move();
        }

    }

    public void died() {
        destroy();
        isDying = false;
    }


    public int getLife() {
        return life;
    }

    public int getAttack() {
        return attack;
    }

    public int getSpeed() {
        return speed;
    }

    public int getMagicPoint() {
        return MagicPoint;
    }

    public int getDefened() {
        return defened;
    }

    public CGPoint getStartPoint() {
        return startPoint;
    }

    public CGPoint getEndPoint() {
        return endPoint;
    }

    public void setLife(int life) {
        this.life = life;
        theFullLife =life;
    }

    public void setAttack(int attack) {
        this.attack = attack;
    }

    public void setSpeed(int speed) {
        this.speed = speed;
    }

    public void setMagicPoint(int magicPoint) {
        MagicPoint = magicPoint;
    }

    public void setDefened(int defened) {
        this.defened = defened;
    }

    public void setStartPoint(CGPoint startPoint) {
        this.startPoint = startPoint;
    }

    public void setEndPoint(CGPoint endPoint) {
        this.endPoint = endPoint;
    }
}
