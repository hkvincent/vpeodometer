package com.vincent.vpedometer.pojo;

import com.vincent.vpedometer.utils.GameUtils;

import org.cocos2d.actions.base.CCAction;
import org.cocos2d.nodes.CCSprite;

/**
 * Created by Administrator on 2018/1/25.
 * show pre fighting status of fighter
 */

public class ShowFighter extends CCSprite {

    public ShowFighter(boolean filpx) {
        super("zombies/shake/z_1_01.png");
        setScale(1);
        setAnchorPoint(0.5f, 0);
        if (filpx) {
            setFlipX(true);
        }
        shake();

    }

    private void shake() {
        CCAction animate = GameUtils.animate(
                "zombies/shake/z_1_%02d.png", 2, true);
        runAction(animate);
    }
}
