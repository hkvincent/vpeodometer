package com.vincent.vpedometer.ui.layer;

import android.view.MotionEvent;

import com.vincent.vpedometer.R;
import com.vincent.vpedometer.pojo.ShowFighter;
import com.vincent.vpedometer.pojo.ShowSkill;
import com.vincent.vpedometer.ui.activity.GameActivity;
import com.vincent.vpedometer.ui.enginne.GameEnginee;
import com.vincent.vpedometer.utils.GameUtils;

import org.cocos2d.actions.instant.CCCallFunc;
import org.cocos2d.actions.interval.CCAnimate;
import org.cocos2d.actions.interval.CCDelayTime;
import org.cocos2d.actions.interval.CCMoveBy;
import org.cocos2d.actions.interval.CCMoveTo;
import org.cocos2d.actions.interval.CCSequence;
import org.cocos2d.layers.CCTMXTiledMap;
import org.cocos2d.nodes.CCDirector;
import org.cocos2d.nodes.CCLabel;
import org.cocos2d.nodes.CCSprite;
import org.cocos2d.sound.SoundEngine;
import org.cocos2d.types.CGPoint;
import org.cocos2d.types.CGRect;

import java.util.ArrayList;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Created by Administrator on 2018/1/24.
 * map layer to handle prepare game start
 */

public class MapLayer extends BaseLayer {

    public static final int TAG_SELECTED_BOX = 1;//
    public static final int TAG_TOTAL_MONEY = 2;//magic point

    private CCTMXTiledMap map;
    private ArrayList<CGPoint> fighetPoint;//fighter area
    private int index;
    private int speed = 80;
    private ArrayList<CCSprite> playerList;
    private CCSprite mSelectedBox;
    private CCSprite mChooseBox;
    private CopyOnWriteArrayList<ShowSkill> mShowSkills;
    private CopyOnWriteArrayList<ShowSkill> mSelectedShowSkill = new CopyOnWriteArrayList<ShowSkill>();
    public static GameActivity gameActivity;//store game activity

    public MapLayer() {
        loadMap();
        loadPlayer();
    }

    public MapLayer(GameActivity game) {
        gameActivity = game;
        loadMap();
        loadPlayer();
        SoundEngine.sharedEngine().playSound(CCDirector.theApp, R.raw.day, true);
    }


    //render the map
    private void loadMap() {
        //map file
        map = CCTMXTiledMap.tiledMap("map/fullmap.tmx");
        //set map which part is center
        map.setAnchorPoint(0.5f, 0.5f);
        //set map display where on the screen
        map.setPosition(ccp(map.getContentSize().width / 2,
                map.getContentSize().height / 2));
        this.addChild(map);
        //get fighter area
        fighetPoint = GameUtils.loadPoint(map, "fighter");
        moveMap();

    }

    /**
     * load figther status in prepare step
     */
    private void loadPlayer() {
        playerList = new ArrayList<CCSprite>();
        boolean flag = true;
        //get all fighter area
        for (CGPoint point : fighetPoint) {
            ShowFighter fighter = null;
            //determine fighter is reverse or not
            if (flag) {
                flag = false;
                fighter = new ShowFighter(true);
            } else {
                fighter = new ShowFighter(false);
            }

            fighter.setPosition(point);
            map.addChild(fighter);
            playerList.add(fighter);
        }

    }

    /**
     * move the map to right
     */
    private void moveMap() {
        float offset = winSize.width - map.getContentSizeRef().width;
        //delay one second
        CCDelayTime delay = CCDelayTime.action(1);
        //move to right position in 2 seconds
        CCMoveBy move = CCMoveBy.action(2, ccp(offset, 0));
        //create the sequence animation, at the end to call show skill box method
        CCSequence s = CCSequence.actions(delay, move, CCCallFunc.action(this, "showSkillBox"));
        map.runAction(s);

    }

    /**
     * to show skill box method and selected skill box
     */
    public void showSkillBox() {
        setIsTouchEnabled(true);
        skillChose();
        skillChoose();
    }


    private boolean isMoving = false;//when skill is moving we can not select other skill
    private CCSprite btnStart;
    private CCSprite startLabel;//set the label
    private CCLabel label;

    /**
     * skill choose box display
     */
    private void skillChoose() {
        mChooseBox = CCSprite.sprite("fight/chose/fight_choose.png");
        mChooseBox.setAnchorPoint(0, 0);
        //mChooseBox.setScale(1.5);
        this.addChild(mChooseBox);

        //to show all skill
        mShowSkills = new CopyOnWriteArrayList<ShowSkill>();
        for (int i = 1; i <= 9; i++) {
            ShowSkill showSkill = new ShowSkill(i);
            mChooseBox.addChild(showSkill.getBgSkill());
            mChooseBox.addChild(showSkill.getShowSkill());
            mShowSkills.add(showSkill);
        }
        //and show start button
        btnStart = CCSprite.sprite("fight/chose/fight_start.png");
        //where the start button on
        btnStart.setPosition(mChooseBox.getContentSize().width / 2, 30);
        //show start button on skill box
        mChooseBox.addChild(btnStart);
        //show the magic point number text
        label = CCLabel.labelWithString(String.valueOf(0),
                "hkbd.ttf", 15);
        label.setColor(ccc3(0, 0, 0));
        label.setPosition(33, CCDirector.sharedDirector().winSize().height - 62);
        this.addChild(label, 1, TAG_TOTAL_MONEY);

    }

    /**
     * show the selected skill box
     */
    public void skillChose() {
        mSelectedBox = CCSprite.sprite("fight/chose/fight_chose.png");
        mSelectedBox.setAnchorPoint(0, 1);
        //mSelectBox.setScale(1.5);
        mSelectedBox.setPosition(0, winSize.getHeight());
        this.addChild(mSelectedBox, 0, TAG_SELECTED_BOX);

    }


    /**
     * handle user touching
     *
     * @param event
     * @return
     */
    @Override
    public boolean ccTouchesMoved(MotionEvent event) {
        //map.touchMove(event, map);
        //if game has started, the touching event will give game engine to handle it
        if (GameEnginee.isStart) {
            GameEnginee.getInstance().handleTouch(event);
            return true;
        }
        //reverse the location of android from cocos2d location
        CGPoint convertTouchToNodeSpace = convertTouchToNodeSpace(event);
        //if the touching is in skill choose box, we go in
        if (CGRect.containsPoint(mChooseBox.getBoundingBox(),
                convertTouchToNodeSpace)) {
            //if the touching is in skill choose box and is in start button,we start the game
            if (CGRect.containsPoint(btnStart.getBoundingBox(),
                    convertTouchToNodeSpace)) {
                //seleced skill can not be empty and all skill need to be static
                if (!mSelectedShowSkill.isEmpty() && !isMoving) {
                    //prepare the game
                    gamePrepare();
                }
                return true;
            }
            //for loop the skill and get each skill location of the skill select box
            for (ShowSkill showSkill : mShowSkills) {
                //if the touching is in showSkill item, we add the showSkill to selected skill box
                if (CGRect.containsPoint(showSkill.getBgSkill()
                        .getBoundingBox(), convertTouchToNodeSpace)) {
                    //the skill can not be selected over 5 items
                    if (mSelectedShowSkill.size() < 5 && !isMoving && !(mSelectedShowSkill.contains(showSkill))) {
                        isMoving = true;
                        mSelectedShowSkill.add(showSkill);
                        //set the moving animation to skill move from skill box to selected skill box
                        CCMoveTo move = CCMoveTo.action(0.5f, ccp(75 + (mSelectedShowSkill.size() - 1) * 53,
                                winSize.height - 65));
                        //when the skill on ready we call the unlock method to let user  selecting next skill
                        CCSequence s = CCSequence.actions(move,
                                CCCallFunc.action(this, "unlock"));
                        showSkill.getShowSkill().runAction(s);
                    }

                    break;
                }
            }//unselect the skill
        } else if (CGRect.containsPoint(mSelectedBox.getBoundingBox(),
                convertTouchToNodeSpace)) {
            boolean isSelect = false;
            for (ShowSkill showSkill : mSelectedShowSkill) {
                if (CGRect.containsPoint(showSkill.getShowSkill()
                        .getBoundingBox(), convertTouchToNodeSpace)) {
                    //move to skill box from selected skill  box
                    CCMoveTo move = CCMoveTo.action(0.5f, showSkill
                            .getBgSkill().getPosition());
                    showSkill.getShowSkill().runAction(move);
                    mSelectedShowSkill.remove(showSkill);
                    isSelect = true;
                    continue;
                }
                //the other showSkill go to left
                if (isSelect) {
                    CCMoveBy move = CCMoveBy.action(0.5f, ccp(-53, 0));
                    showSkill.getShowSkill().runAction(move);
                }
            }
        }

        return super.ccTouchesMoved(event);
    }


    /**
     * when user click the start button this  method be invoked
     */
    private void gamePrepare() {
        setIsTouchEnabled(false);
        //no show the skill box
        mChooseBox.removeSelf();
        //move the map to original position
        moveMapBack();
        //zoom out selected skill box
        mSelectedBox.setScale(1.3);
        //reposition the magic point position
        label.setPosition(33 * 1.3f, (CCDirector.sharedDirector().winSize().height - 62 * 1.3f));
        //get all selected skill and resize it,relocate it
        for (ShowSkill showSkill : mSelectedShowSkill) {
            //resize it as same as selected skill box
            showSkill.getShowSkill().setScale(1.3);
            //relocate it one correct position
            showSkill.getShowSkill().setPosition(
                    showSkill.getShowSkill().getPosition().x * 1.3f,
                    showSkill.getShowSkill().getPosition().y * 0.95f);

            this.addChild(showSkill.getShowSkill());
        }


        //label.setPosition(22, CCDirector.sharedDirector().winSize().height - 42);
        //label.setScale(0.65f);
    }


    /**
     * the function to move back map to original position
     */
    private void moveMapBack() {
        float offset = map.getContentSizeRef().width - winSize.width;

        CCDelayTime delay = CCDelayTime.action(1);
        CCMoveBy move = CCMoveBy.action(2, ccp(offset, 0));

        CCSequence s = CCSequence.actions(delay, move, delay,
                CCCallFunc.action(this, "showLabel"));

        map.runAction(s);
    }

    /**
     * show the ready label to tell user fighting will start and cal gameBegin method
     */
    public void showLabel() {
        //startLabel = CCSprite.sprite("fight/startready_01.png");
        startLabel = CCSprite.sprite("fight/ready_01.png");
        startLabel.setPosition(winSize.width / 2, winSize.height / 2);
        this.addChild(startLabel);
       // CCAnimate animate = (CCAnimate) GameUtils.animate("fight/startready_%02d.png", 3, false, 0.5f);
        CCAnimate animate = (CCAnimate) GameUtils.animate("fight/ready_%02d.png", 3, false, 0.5f);
        CCSequence s = CCSequence.actions(animate,
                CCCallFunc.action(this, "gameBegin"));
        startLabel.runAction(s);
    }

    /**
     *
     */
    public void gameBegin() {
        startLabel.removeSelf();
        System.out.println("game start!!!");
        setIsTouchEnabled(true);
        for (CCSprite cc : playerList) {
            cc.removeSelf();
        }
        playerList.clear();
        GameEnginee.getInstance().gameStart(map, mSelectedShowSkill);
        //play the music when game begin
        SoundEngine.sharedEngine().playSound(CCDirector.theApp, R.raw.day, true);
    }


    /**
     * let user select next skill
     */
    public void unlock() {
        isMoving = false;
    }


}