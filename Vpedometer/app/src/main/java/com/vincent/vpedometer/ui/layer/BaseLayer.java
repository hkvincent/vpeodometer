package com.vincent.vpedometer.ui.layer;

import org.cocos2d.layers.CCLayer;
import org.cocos2d.nodes.CCDirector;
import org.cocos2d.types.CGSize;

/**
 * Created by Administrator on 2018/1/25.
 */

public class BaseLayer extends CCLayer {
    public CGSize winSize = CCDirector.sharedDirector().winSize();

    public BaseLayer() {
    }
}
