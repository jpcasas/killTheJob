/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.killthejob.controls;

import com.jme3.math.Vector3f;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.control.AbstractControl;
import com.killthejob.manager.ParticleManager;
import com.killthejob.util.UtilHelper;

/**
 *
 * @author Juan
 */
public class BulletControl extends AbstractControl {

    private int screenWidth, screenHeight;
    private float speed = 1100f;
    public Vector3f direction;
    private float rotation;
    private ParticleManager particleManager;

    public BulletControl(Vector3f direction, int screenWidth, int screenHeight, ParticleManager particleManager) {
        this.particleManager = particleManager;
        this.direction = direction;
        this.screenWidth = screenWidth;
        this.screenHeight = screenHeight;
    }

    @Override
    protected void controlUpdate(float tpf) {
        //movement
        spatial.move(direction.mult(speed * tpf));

        //rotation
        float actualRotation = UtilHelper.getAngleFromVector(direction);
        if (actualRotation != rotation) {
            spatial.rotate(0, 0, actualRotation - rotation);
            rotation = actualRotation;
        }

        //check the limits of the screen
        Vector3f loc = spatial.getLocalTranslation();
        
        if (loc.x > screenWidth
                || loc.y > screenHeight
                || loc.x < 0
                || loc.y < 0) {

            spatial.removeFromParent();
        }
    }

   

    @Override
    protected void controlRender(RenderManager rm, ViewPort vp) {
    }
}