/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.killthejob.manager;

import com.killthejob.controls.ParticleControl;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import java.util.Random;


/**
 *
 * @author Juan
 */
public class ParticleManager {

    private Node guiNode;
    private Spatial standardParticle, glowParticle;
    private Node particleNode;
    private Random rand;

    public ParticleManager(Node guiNode, Spatial standardParticle, Spatial glowParticle) {
        this.guiNode = guiNode;
        this.standardParticle = standardParticle;
        this.glowParticle = glowParticle;

        particleNode = new Node("particles");
        guiNode.attachChild(particleNode);

        rand = new Random();
    }

    public ColorRGBA hsvToColor(float h, float s, float v) {
        if (h == 0 && s == 0) {
            return new ColorRGBA(v, v, v, 1);
        }

        float c = s * v;
        float x = c * (1 - Math.abs(h % 2 - 1));
        float m = v - c;

        if (h < 1) {
            return new ColorRGBA(c + m, x + m, m, 1);
        } else if (h < 2) {
            return new ColorRGBA(x + m, c + m, m, 1);
        } else if (h < 3) {
            return new ColorRGBA(m, c + m, x + m, 1);
        } else if (h < 4) {
            return new ColorRGBA(m, x + m, c + m, 1);
        } else if (h < 5) {
            return new ColorRGBA(x + m, m, c + m, 1);
        } else {
            return new ColorRGBA(c + m, m, x + m, 1);
        }
    }

    public void enemyExplosion(Vector3f position) {
        // init colors
        float hue1 = rand.nextFloat() * 6;
        float hue2 = (rand.nextFloat() * 2) % 6f;
        ColorRGBA color1 = hsvToColor(hue1, 0.5f, 1f);
        ColorRGBA color2 = hsvToColor(hue2, 0.5f, 1f);

        // create 120 particles
        for (int i = 0; i < 120; i++) {
            Vector3f velocity = getRandomVelocity(250);

            Spatial particle = standardParticle.clone();
            particle.setLocalTranslation(position);
            ColorRGBA color = new ColorRGBA();
            color.interpolate(color1, color2, rand.nextFloat() * 0.5f);
            particle.addControl(new ParticleControl(velocity, 3100, color));
            particleNode.attachChild(particle);

        }
    }

    private Vector3f getRandomVelocity(float max) {
        // generate Vector3f with random direction
        Vector3f velocity = new Vector3f(
                rand.nextFloat() - 0.5f,
                rand.nextFloat() - 0.5f,
                0).normalizeLocal();

        // apply semi-random particle speed
        float random = rand.nextFloat() * 5 + 1;
        float particleSpeed = max * (1f - 0.6f / random);
        velocity.multLocal(particleSpeed);
        return velocity;
    }
    /* make to slow the game in android
    public void bulletExplosion(Vector3f position) {
        for (int i = 0; i < 30; i++) {
            Vector3f velocity = getRandomVelocity(175);

            Spatial particle = standardParticle.clone();
            particle.setLocalTranslation(position);
            ColorRGBA color = new ColorRGBA(0.676f, 0.844f, 0.898f, 1);
            particle.addControl(new ParticleControl(velocity, 1000, color));
            particleNode.attachChild(particle);
        }
    }
    */
    public void playerExplosion(Vector3f position) {
    ColorRGBA color1 = ColorRGBA.Blue;
    ColorRGBA color2 = ColorRGBA.Black;
 
    for (int i=0; i<1200; i++) {
        Vector3f velocity = getRandomVelocity(1000);
 
        Spatial particle = standardParticle.clone();
        particle.setLocalTranslation(position);
        ColorRGBA color = new ColorRGBA();
        color.interpolate(color1, color2, rand.nextFloat());
        particle.addControl(new ParticleControl(velocity, 2800, color));
        particleNode.attachChild(particle);
    }
}
}