package com.vincent.vpedometer.pojo;

import org.cocos2d.nodes.CCSprite;

/**
 * Created by Administrator on 2018/1/28.
 * show skill logo
 */

public class ShowSkill {

    String format = "fight/chose/choose_default%02d.png";
    private CCSprite bgSkill;
    private CCSprite showSkill;

    private int id;//标示植物

    public ShowSkill(int i) {
        this.id = i;
        bgSkill = CCSprite.sprite(String.format(format, i));
        bgSkill.setAnchorPoint(0, 0);
        float x = (i - 1) % 4 * 54 + 16;
        float y = 175 - (i - 1) / 4 * 59;
        bgSkill.setPosition(x, y);
        bgSkill.setOpacity(100);

        showSkill = CCSprite.sprite(String.format(format, i));
        showSkill.setAnchorPoint(0, 0);
        showSkill.setPosition(bgSkill.getPosition());
    }

    public CCSprite getBgSkill() {
        return bgSkill;
    }

    public CCSprite getShowSkill() {
        return showSkill;
    }

    public int getId() {
        return id;
    }

}
