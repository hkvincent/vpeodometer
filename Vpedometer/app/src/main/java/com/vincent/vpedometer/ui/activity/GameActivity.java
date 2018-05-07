package com.vincent.vpedometer.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Window;

import com.vincent.vpedometer.ui.enginne.GameEnginee;
import com.vincent.vpedometer.ui.layer.MapLayer;

import org.cocos2d.layers.CCScene;
import org.cocos2d.nodes.CCDirector;
import org.cocos2d.opengl.CCGLSurfaceView;
import org.cocos2d.sound.SoundEngine;

/**
 * Created by Administrator on 2018/1/25.
 */

public class GameActivity extends AppCompatActivity {

    private CCDirector director;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        CCGLSurfaceView view = new CCGLSurfaceView(this);

        setContentView(view);

        director = CCDirector.sharedDirector();
        director.attachInView(view);

        director.setDisplayFPS(true);
        //director.setAnimationInterval(1 / 60f);/

        director.setDeviceOrientation(CCDirector.kCCDeviceOrientationLandscapeLeft);
        director.setScreenSize(920, 480);


        CCScene scene = CCScene.node();
        MapLayer gameLayer = new MapLayer(this);

        scene.addChild(gameLayer);


        director.runWithScene(scene);
    }

    @Override
    protected void onResume() {
        super.onResume();
        director.resume();
        SoundEngine.sharedEngine().resumeSound();
    }

    @Override
    protected void onPause() {
        super.onPause();
        director.pause();
        SoundEngine.sharedEngine().pauseSound();
    }

    @Override
    protected void onDestroy() {
        GameEnginee.getInstance().gameOver();
        super.onDestroy();
        director.end();
    }

    public void gameOver() {

        this.finish();
    }

    public void gameOver(boolean win) {
        Intent userData = new Intent();
        if (win) {

            userData.putExtra("win", true);

        } else {
            userData.putExtra("win", false);

        }
        setResult(1, userData);
        this.finish();
    }
}
