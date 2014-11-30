package com.killthejob;

import com.killthejob.controls.RandomControl;
import com.killthejob.controls.ChercheurControl;
import com.killthejob.controls.BulletControl;
import com.jme3.app.SimpleApplication;
import com.jme3.input.MouseInput;
import com.jme3.input.controls.AnalogListener;
import com.jme3.input.controls.MouseButtonTrigger;
import com.jme3.material.Material;
import com.jme3.material.RenderState.BlendMode;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.renderer.RenderManager;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.texture.Texture2D;
import com.jme3.ui.Picture;
import com.killthejob.manager.ParticleManager;
import java.util.Random;

/**
 * test
 *
 * @author Juan Casas
 */
public class KillTheJob extends SimpleApplication implements AnalogListener {

    private Spatial player;
    private long bulletCooldown;
    private Node bulletNode;
    private long enemyCreationCooldown;
    private float enemyCreationChance = 80;
    private Node enemyNode;
    private ScreenInformation hud;
    private boolean gameOver = false;

    public static void main(String[] args) {
        KillTheJob app = new KillTheJob();
        app.start();
        /**
         * Load a model. Uses model and texture from jme3-test-data library!
         */
    }
    private ParticleManager particleManager;

    @Override
    public void simpleInitApp() {
        cam.setParallelProjection(true);
        cam.setLocation(new Vector3f(0, 0, 0.5f));
        getFlyByCamera().setEnabled(false);


        setDisplayStatView(false);
        setDisplayFps(false);

        particleManager = new ParticleManager(guiNode, getSpatial("Laser"), getSpatial("Glow"));

        player = getSpatial("tank");
        player.setUserData("alive", true);
        player.move(settings.getWidth() / 2, (Float) player.getUserData("radius"), 0);
        guiNode.attachChild(player);


        inputManager.addMapping("mousePick", new MouseButtonTrigger(MouseInput.BUTTON_LEFT));
        inputManager.addListener(this, "mousePick");
        bulletNode = new Node("bullets");
        enemyNode = new Node("enemies");
        guiNode.attachChild(enemyNode);
        guiNode.attachChild(bulletNode);


        hud = new ScreenInformation(assetManager, guiNode, settings.getWidth(), settings.getHeight());
        hud.reset();
        
        viewPort.setBackgroundColor(new ColorRGBA(0.89f, 0.89f, 0.89f, 1f));


    }

    @Override
    public void simpleUpdate(float tpf) {
        hud.update();
        if ((Boolean) player.getUserData("alive")) {
            createEnemies();
            handleCollisions();
        } else if (System.currentTimeMillis() - (Long) player.getUserData("dieTime") > 4000f && !gameOver) {
            // spawn player
            player.setLocalTranslation(settings.getWidth() / 2, (Float) player.getUserData("radius"), 0);
            guiNode.attachChild(player);
            player.setUserData("alive", true);
        }


    }

    private void handleCollisions() {
        for (int i = 0; i < enemyNode.getQuantity(); i++) {
            if ((Boolean) enemyNode.getChild(i).getUserData("active")) {
                if (checkCollision(player, enemyNode.getChild(i))) {
                    if (!hud.removeLife()) {
                        hud.endGame();
                        gameOver = true;

                    }
                    killPlayer();


                }
            }
        }
        int i = 0;
        while (i < enemyNode.getQuantity()) {
            int j = 0;
            while (j < bulletNode.getQuantity()) {
                if (checkCollision(enemyNode.getChild(i), bulletNode.getChild(j))) {
                    if (enemyNode.getChild(i).getName().equals("trackerFinal")) {
                        hud.addPoints(2);
                    } else if (enemyNode.getChild(i).getName().equals("enemyRandom")) {
                        hud.addPoints(1);
                    }

                    particleManager.enemyExplosion(enemyNode.getChild(i).getLocalTranslation());
                    enemyNode.detachChildAt(i);
                    bulletNode.detachChildAt(j);

                    break;
                }
                j++;
            }
            i++;
        }



    }

    private boolean checkCollision(Spatial a, Spatial b) {
        float distance = a.getLocalTranslation().distance(b.getLocalTranslation());
        float maxDistance = (Float) a.getUserData("radius") + (Float) b.getUserData("radius");
        return distance <= maxDistance;
    }

    private void killPlayer() {
        player.removeFromParent();
        player.setUserData("alive", false);
        player.setUserData("dieTime", System.currentTimeMillis());
        particleManager.playerExplosion(player.getLocalTranslation());
        enemyNode.detachAllChildren();

    }

    private void createEnemies() {
        if (System.currentTimeMillis() - enemyCreationCooldown >= 17) {
            enemyCreationCooldown = System.currentTimeMillis();
            if (enemyNode != null) {
                if (enemyNode.getQuantity() < 50) {
                    if (new Random().nextInt((int) enemyCreationChance) == 0) {
                        createTracker();
                    }
                    if (new Random().nextInt((int) enemyCreationChance) == 0) {
                        createRandom();
                    }
                }
            }
            if (enemyCreationChance >= 1.1f) {
                enemyCreationChance -= 0.005f;
            }
        }
    }

    private void createTracker() {
        Spatial seeker = getSpatial("trackerFinal");
        seeker.setLocalTranslation(getSpawnPosition());
        seeker.addControl(new ChercheurControl(player));
        seeker.setUserData("active", false);
        enemyNode.attachChild(seeker);
    }

    private void createRandom() {
        Spatial wanderer = getSpatial("enemyRandom");
        wanderer.setLocalTranslation(getSpawnPosition());
        wanderer.addControl(new RandomControl(settings.getWidth(), settings.getHeight()));
        wanderer.setUserData("active", false);
        enemyNode.attachChild(wanderer);
    }

    private Vector3f getSpawnPosition() {
        Vector3f pos;
        do {
            pos = new Vector3f(new Random().nextInt(settings.getWidth()), new Random().nextInt(settings.getHeight()), 0);
        } while (pos.distanceSquared(player.getLocalTranslation()) < 8000);
        return pos;
    }

    @Override
    public void simpleRender(RenderManager rm) {
    }

    private Spatial getSpatial(String name) {
        Node node = new Node(name);
//        load picture
        Picture pic = new Picture(name);
        Texture2D tex = (Texture2D) assetManager.loadTexture("Textures/" + name + ".png");
        pic.setTexture(assetManager, tex, true);

//        adjust picture
        float width = tex.getImage().getWidth();
        float height = tex.getImage().getHeight();
        pic.setWidth(width);
        pic.setHeight(height);
        pic.move(-width / 2f, -height / 2f, 0);

//        add a material to the picture
        Material picMat = new Material(assetManager, "Common/MatDefs/Gui/Gui.j3md");
        picMat.getAdditionalRenderState().setBlendMode(BlendMode.AlphaAdditive);
        node.setMaterial(picMat);

//        set the radius of the spatial
//        (using width only as a simple approximation)
        node.setUserData("radius", width / 2);

//        attach the picture to the node and return it
        node.attachChild(pic);

        return node;
    }

    private Vector3f getAimDirection() {
        Vector2f mouse = inputManager.getCursorPosition();
        Vector3f playerPos = player.getLocalTranslation();
        Vector3f dif = new Vector3f(mouse.x - playerPos.x, mouse.y - playerPos.y, 0);
        return dif.normalizeLocal();
    }

    public void onAnalog(String name, float value, float tpf) {
        if ((Boolean) player.getUserData("alive")) {
            if (name.equals("mousePick")) {
                //shoot Bullet
                if (System.currentTimeMillis() - bulletCooldown > 83f) {
                    bulletCooldown = System.currentTimeMillis();

                    Vector3f aim = getAimDirection();
                    Vector3f offset = new Vector3f(aim.y / 3, -aim.x / 3, 0);

                    Spatial bullet = getSpatial("bulletau");
                    Vector3f finalOffset = aim.add(offset).mult(30);
                    Vector3f trans = player.getLocalTranslation().add(finalOffset);
                    bullet.setLocalTranslation(trans);
                    bullet.addControl(new BulletControl(aim, settings.getWidth(), settings.getHeight(), particleManager));
                    bulletNode.attachChild(bullet);

                    Spatial bullet2 = getSpatial("bulletau");
                    finalOffset = aim.add(offset.negate()).mult(30);
                    trans = player.getLocalTranslation().add(finalOffset);
                    bullet2.setLocalTranslation(trans);
                    bullet2.addControl(new BulletControl(aim, settings.getWidth(), settings.getHeight(), particleManager));
                    bulletNode.attachChild(bullet2);
                }
            }
        }
    }
}
