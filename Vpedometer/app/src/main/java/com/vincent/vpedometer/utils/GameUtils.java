package com.vincent.vpedometer.utils;

import org.cocos2d.actions.base.CCAction;
import org.cocos2d.actions.base.CCRepeatForever;
import org.cocos2d.actions.interval.CCAnimate;
import org.cocos2d.layers.CCLayer;
import org.cocos2d.layers.CCScene;
import org.cocos2d.layers.CCTMXObjectGroup;
import org.cocos2d.layers.CCTMXTiledMap;
import org.cocos2d.nodes.CCAnimation;
import org.cocos2d.nodes.CCDirector;
import org.cocos2d.nodes.CCNode;
import org.cocos2d.nodes.CCSprite;
import org.cocos2d.nodes.CCSpriteFrame;
import org.cocos2d.transitions.CCFadeTransition;
import org.cocos2d.types.CGPoint;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * game engine tools
 *
 * @author vincent
 */
public class GameUtils {

    /**
     * animation tool
     *
     * @param format image path format
     * @param num    how many image need to be loaded
     * @param repeat repeat the animation
     * @return
     */
    public static CCAction animate(String format, int num, boolean repeat) {
        return animate(format, num, repeat, 0.2f);
    }


    /**
     * set the animation
     *
     * @param format the image where in
     * @param num    how many image need to be loaded
     * @param repeat repeat or not
     * @param t      fps
     * @return
     */
    public static CCAction animate(String format, int num, boolean repeat, float t) {
        ArrayList<CCSpriteFrame> frames = new ArrayList<CCSpriteFrame>();
        for (int i = 1; i <= num; i++) {
            frames.add(CCSprite.sprite(String.format(format, i))
                    .displayedFrame());
        }

        CCAnimation anim = CCAnimation.animation("loading", t, frames);// the t is how many time to display one image

        if (!repeat) {
            CCAnimate animate = CCAnimate.action(anim, false);// paramter 2 say animation repeat or not
            return animate;
        } else {
            CCAnimate animate = CCAnimate.action(anim);
            CCRepeatForever r = CCRepeatForever.action(animate);
            return r;
        }
    }

    /**
     * change  the game layer
     */
    public static void changeLayer(CCLayer layer) {
        CCScene scene = CCScene.node();
        scene.addChild(layer);

        // CCJumpZoomTransition transition = CCJumpZoomTransition.transition(2,
        // scene);// change

        CCFadeTransition transition = CCFadeTransition.transition(1, scene);// how to change
        CCDirector.sharedDirector().replaceScene(transition);// change sence
    }

    /**
     * get the specific map point
     */
    public static ArrayList<CGPoint> loadPoint(CCTMXTiledMap map,
                                               String groupName) {
        ArrayList<CGPoint> points = new ArrayList<CGPoint>();

        CCTMXObjectGroup objectGroupNamed = map.objectGroupNamed(groupName);
        ArrayList<HashMap<String, String>> objects = objectGroupNamed.objects;
        for (HashMap<String, String> hashMap : objects) {
            Integer x = Integer.parseInt(hashMap.get("x"));
            Integer y = Integer.parseInt(hashMap.get("y"));
            points.add(CCNode.ccp(x, y));
        }

        return points;
    }
}
