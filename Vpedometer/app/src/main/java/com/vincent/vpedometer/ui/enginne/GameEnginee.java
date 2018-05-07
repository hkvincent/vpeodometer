package com.vincent.vpedometer.ui.enginne;

import android.view.MotionEvent;

import com.vincent.vpedometer.pojo.BaseElement;
import com.vincent.vpedometer.pojo.RemoteSkill;
import com.vincent.vpedometer.pojo.ShowSkill;
import com.vincent.vpedometer.pojo.Skill;
import com.vincent.vpedometer.pojo.TheFighter;
import com.vincent.vpedometer.ui.fragment.GameListFrament;
import com.vincent.vpedometer.ui.layer.MapLayer;
import com.vincent.vpedometer.utils.GameUtils;

import org.cocos2d.actions.CCProgressTimer;
import org.cocos2d.actions.CCScheduler;
import org.cocos2d.layers.CCTMXTiledMap;
import org.cocos2d.nodes.CCSprite;
import org.cocos2d.types.CGPoint;
import org.cocos2d.types.CGRect;

import java.util.ArrayList;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Created by Administrator on 2018/1/28.
 * game engine to handle fighting logic
 */

public class GameEnginee {
    private static GameEnginee mInstance = new GameEnginee(); // game engine
    private CCTMXTiledMap map;// the map object
    public static boolean isStart;
    public ArrayList<TheFighter> fighterList = new ArrayList<>();//save the fighter to list
    CopyOnWriteArrayList<ShowSkill> mSelectedShowSkill;//the skill user has selected
    private ArrayList<CGPoint> mFighterPoint;//where the fighter will show
    private Skill currentSkill;//the releasing skill

    /**
     * singleton factory
     */
    private GameEnginee() {

    }

    public static GameEnginee getInstance() {
        return mInstance;
    }


    /**
     * when game over we need to reset the game engine
     */
    public void gameOver() {
        mInstance = new GameEnginee();
        isStart = false;
        CCScheduler.sharedScheduler().unschedule("skillAttack", this);
        CCScheduler.sharedScheduler().unschedule("attack", this);
        if (TheFighter.win) {
            MapLayer.gameActivity.gameOver(true);
        } else {
            MapLayer.gameActivity.gameOver(false);
        }


    }


    /**
     * when game start we fix the selected skill,and reload the fighter to set other animation
     *
     * @param map
     * @param selectedShowSkill
     */
    public void gameStart(CCTMXTiledMap map, CopyOnWriteArrayList<ShowSkill> selectedShowSkill) {
        isStart = true;
        this.map = map;
        this.mSelectedShowSkill = selectedShowSkill;
        mFighterPoint = GameUtils.loadPoint(map, "fighter");
        loadTheFihter();
        progress();

    }

    /**
     * there will be two fighter being cteate, one is mine, another is opposite
     */
    private void loadTheFihter() {
        final TheFighter fither1 = new TheFighter(mFighterPoint.get(1), mFighterPoint.get(0));
        //set listener to listen fighter died or not,when die we remove it from list
        fither1.setDieListener(new BaseElement.DieListener() {
            @Override
            public void die() {
                fighterList.remove(fither1);
            }
        });
        final TheFighter fither2 = new TheFighter(mFighterPoint.get(0), mFighterPoint.get(1));
        fither2.setDieListener(new BaseElement.DieListener() {

            @Override
            public void die() {
                fighterList.remove(fither2);
            }
        });
        fighterList.add(fither1);
        fighterList.add(fither2);


        //show the fighter on map
        map.addChild(fither1, 1);
        map.addChild(fither2, 1);


        //set attribute
        if (fither1.isMyFighter()) {
            fither1.setAttack(GameListFrament.myUser.getGameCharecter().getAttack());
            fither1.setDefened(GameListFrament.myUser.getGameCharecter().getDefend());
            fither1.setMagicPoint(GameListFrament.myUser.getGameCharecter().getMagicPoint());
            fither1.setLife(GameListFrament.myUser.getGameCharecter().getHealthPoint());

            fither2.setAttack(GameListFrament.selectedUser.getGameCharecter().getAttack());
            fither2.setDefened(GameListFrament.selectedUser.getGameCharecter().getDefend());
            fither2.setMagicPoint(GameListFrament.selectedUser.getGameCharecter().getMagicPoint());
            fither2.setLife(GameListFrament.selectedUser.getGameCharecter().getHealthPoint());
        }else{
            fither2.setAttack(GameListFrament.myUser.getGameCharecter().getAttack());
            fither2.setDefened(GameListFrament.myUser.getGameCharecter().getDefend());
            fither2.setMagicPoint(GameListFrament.myUser.getGameCharecter().getMagicPoint());
            fither2.setLife(GameListFrament.myUser.getGameCharecter().getHealthPoint());

            fither1.setAttack(GameListFrament.selectedUser.getGameCharecter().getAttack());
            fither1.setDefened(GameListFrament.selectedUser.getGameCharecter().getDefend());
            fither1.setMagicPoint(GameListFrament.selectedUser.getGameCharecter().getMagicPoint());
            fither1.setLife(GameListFrament.selectedUser.getGameCharecter().getHealthPoint());
        }


        System.out.println("--------------reach------------------");
        //set the timer to call attack every  0.5 second
        CCScheduler myScheduler = CCScheduler.sharedScheduler();
        myScheduler.schedule("attack", this, 0.5f, false);

    }

    //the skill has attacked to the target
    private boolean hasAttacked;

    /**
     * when user release the skill,this method will be called until the skill has been shutdown
     *
     * @param f
     */
    public void skillAttack(float f) {
        System.out.println("call skillAttack");
        //the skill is not remote skill will unschedule it
        if (!currentSkill.isRemoteSkill) {
            System.out.println("remote skill");
            CCScheduler.sharedScheduler().unschedule("skillAttack", this);
            return;
        }
        //get all the fighter
        for (TheFighter theFighter : fighterList) {
            //determine the fighter is mine or not
            if (!theFighter.isMyFighter()) {
                //get opposite X position
                int x = (int) theFighter.getPosition().x;
                int left = x - 10;
                int right = x + 30;

                System.out.println("opposite" + theFighter.getPosition().x + "---" + theFighter.getPosition().y);
                //get skill X position
                int skillXPosition = (int) currentSkill.getPosition().x;
                System.out.println("skill:" + skillXPosition + "---" + currentSkill.getPosition().y);

                //if the skill position overlap the opposite position means the skill can attack the fighter
                if (skillXPosition >= left && skillXPosition <= right && !hasAttacked) {
                    System.out.println("attack now");
                    //set this skill has been used
                    hasAttacked = true;
                    //call the fighter skill method  to reduce enemy life
                    theFighter.attacked(currentSkill.getAttack());
                    System.out.println(theFighter.isMyFighter() + "-after the fighter life" + theFighter.getLife());
                    //set skill not useful
                    currentSkill.setVisible(false);
                    currentSkill.setAttack(0);
                } else if (hasAttacked) {
                    //unschedule the skill
                    hasAttacked = false;
                    CCScheduler.sharedScheduler().unschedule("skillAttack", this);
                }
            }
        }
    }


    /**
     * all method has been invoked by cocosed schedule method will pass the float parameter to it as tag
     * this method is to check two fighter is overlap or not
     *
     * @param f
     */
    public void attack(float f) {
        if (fighterList != null && fighterList.size() > 1) {
            //get all figther
            for (TheFighter fighter : fighterList) {
                for (int i = 0; i < fighterList.size(); i++) {
                    //get opposite fighter
                    float crossPoint = fighter.getPosition().x - fighterList.get(i).getPosition().x;
                    //if fighter overlap within 60 pixes that mean they attack now.
                    if (fighter != fighterList.get(i) && Math.abs(crossPoint) < 60) {
                        //call the fighter attack method  to reduce enemy life
                        fighter.attack(fighterList.get(i));
                        fighter.setAttacking(true);
                        fighterList.get(i).attack(fighter);
                        fighterList.get(i).setAttacking(true);
                        //when two player in play,we do not need always call this attack method
                        //we un schedule it to reduce resource
                        CCScheduler.sharedScheduler().unschedule("attack", this);
                    }
                }
            }
        }
    }

    //to save the select skill which has been used
    private ArrayList<ShowSkill> mHasRealseShowSkill = new ArrayList<ShowSkill>();


    /**
     * handle user touching monitor of phone
     *
     * @param event
     */
    public void handleTouch(MotionEvent event) {
        //the cocos2d engine XY coordinate is reverse, so we need to reverse it again to normal
        CGPoint convertTouchToNodeSpace = map.convertTouchToNodeSpace(event);
        //get the select box
        CCSprite selectedBox = (CCSprite) map.getParent().getChildByTag(
                MapLayer.TAG_SELECTED_BOX);
        //if user is touching in select box area
        if (CGRect.containsPoint(selectedBox.getBoundingBox(),
                convertTouchToNodeSpace)) {
            //get all selected skill
            for (ShowSkill showSkill : mSelectedShowSkill) {
                //if the selected skill position in contain user touching
                if (CGRect.containsPoint(showSkill.getShowSkill()
                        .getBoundingBox(), convertTouchToNodeSpace)) {
                    //if skill has been used, we can not give the user use it again
                    if (mHasRealseShowSkill.contains(showSkill)) {
                        return;
                    }
                    //set the skill opacity to tell user this skill has been used
                    showSkill.getShowSkill().setOpacity(100);
                    mHasRealseShowSkill.add(showSkill);

                    //determine which skill user current selects
                    switch (showSkill.getId()) {
                        case 1:
                            //remote skill has been selected to use
                            RemoteSkill remoteSkill = new RemoteSkill();
                            //get our fighter from list
                            for (TheFighter theFighter : fighterList) {
                                if (theFighter.isMyFighter()) {
                                    //set current skill
                                    currentSkill = remoteSkill;
                                    //call skillAttack method every 0.1seconds
                                    //the third parameter is cocos2d this method can not be paused
                                    CCScheduler.sharedScheduler().schedule("skillAttack", this, 0.1f, false);
                                    theFighter.releaseSkill(remoteSkill);

                                }
                            }
                            break;
                        case 2:
                            System.out.println("release showSkill two");
                            break;
                        case 3:
                            System.out.println("release showSkill two");
                            break;
                        case 4:
                            System.out.println("release showSkill three");
                            break;
                        case 5:
                            System.out.println("release showSkill three");
                            break;

                        default:
                            break;
                    }

                    break;
                }
            }
        } else {
            //set we can move the map
            map.touchMove(event, map);
        }
    }

    //the life progress of enemy
    public CCProgressTimer progressTimer;


    //set progress bar style
    private void progress() {
        progressTimer = CCProgressTimer
                .progressWithFile("fight/progress.png");

        progressTimer.setPosition(
                150, 30);
        map.getParent().addChild(progressTimer);

        progressTimer.setScale(1f);
        // 0-100
        progressTimer.setPercentage(0);
        System.out.println(progressTimer);
        // the style of progress
        progressTimer
                .setType(CCProgressTimer.kCCProgressTimerTypeHorizontalBarLR);

        // the look of progress
        CCSprite sprite = CCSprite.sprite("fight/flagmeter.png");
        sprite.setPosition(150,
                30);
        map.getParent().addChild(sprite);
        sprite.setScale(1f);


        CCSprite name = CCSprite
                .sprite("fight/FlagMeterLevelProgress.png");
        name.setPosition(150, 8);
        map.getParent().addChild(name);
        name.setScale(0.6f);
    }
}