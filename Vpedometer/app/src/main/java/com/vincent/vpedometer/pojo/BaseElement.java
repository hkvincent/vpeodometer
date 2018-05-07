package com.vincent.vpedometer.pojo;

import org.cocos2d.nodes.CCSprite;

/**
 * Created by Administrator on 2018/1/28.
 */

public abstract class BaseElement extends CCSprite {

    public interface DieListener {
        void die();
    }

    private DieListener dieListener; // dying listener

    public void setDieListener(DieListener dieListener) {
        this.dieListener = dieListener;
    }

    public BaseElement(String filepath) {
        super(filepath);
    }

    /**
     * the base action of sprite
     */
    public abstract void baseAction();

    public void destroy() {
        if (dieListener != null) {
            dieListener.die();
        }
        removeSelf();
    }
}
