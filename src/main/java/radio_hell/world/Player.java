package radio_hell.world;

import cinnamon.input.InputManager;
import cinnamon.model.GeometryHelper;
import cinnamon.render.MatrixStack;
import cinnamon.render.batch.VertexConsumer;
import cinnamon.utils.Resource;
import org.joml.Math;
import org.lwjgl.glfw.GLFW;
import radio_hell.GameWorld;
import radio_hell.Main;

public class Player extends Entity {

    public final int playerIndex;

    public int lives = 3;

    public int attack = 0;

    public Player(int playerIndex) {
        super(300 / 2f, 450 - 32, 32, 32, new Resource("radio_hell", "textures/player.png"));
        this.playerIndex = playerIndex;
        this.speed = 16;
    }

    @Override
    public void tick() {
        super.tick();

        float impulseX = 0;
        float impulseY = 0;
        float speed = this.speed;

        this.attack = 0;

        switch (playerIndex) {
            case 1 -> {
                if (InputManager.isKeyPressed(GLFW.GLFW_KEY_W)) impulseY = -1f;
                if (InputManager.isKeyPressed(GLFW.GLFW_KEY_S)) impulseY =  1f;
                if (InputManager.isKeyPressed(GLFW.GLFW_KEY_A)) impulseX = -1f;
                if (InputManager.isKeyPressed(GLFW.GLFW_KEY_D)) impulseX =  1f;
                if (InputManager.isKeyPressed(GLFW.GLFW_KEY_LEFT_SHIFT)) speed /= 2f;

                if (InputManager.isKeyPressed(GLFW.GLFW_KEY_J)) attack |= 0xFF0000;
                if (InputManager.isKeyPressed(GLFW.GLFW_KEY_K)) attack |= 0x00FF00;
                if (InputManager.isKeyPressed(GLFW.GLFW_KEY_L)) attack |= 0x0000FF;
            }
            case 2 -> {
                if (InputManager.isKeyPressed(GLFW.GLFW_KEY_UP))    impulseY = -1f;
                if (InputManager.isKeyPressed(GLFW.GLFW_KEY_DOWN))  impulseY =  1f;
                if (InputManager.isKeyPressed(GLFW.GLFW_KEY_LEFT))  impulseX = -1f;
                if (InputManager.isKeyPressed(GLFW.GLFW_KEY_RIGHT)) impulseX =  1f;
                if (InputManager.isKeyPressed(GLFW.GLFW_KEY_RIGHT_SHIFT)) speed /= 2f;

                if (InputManager.isKeyPressed(GLFW.GLFW_KEY_KP_4)) attack |= 0xFF0000;
                if (InputManager.isKeyPressed(GLFW.GLFW_KEY_KP_5)) attack |= 0x00FF00;
                if (InputManager.isKeyPressed(GLFW.GLFW_KEY_KP_6)) attack |= 0x0000FF;
            }
        }

        if (impulseX != 0f || impulseY != 0f)
            applyImpulse(impulseX, impulseY, speed);

        if (attack != 0) {
            attack |= 0xFF000000;

            //mix with other player bullets
            for (Entity entity : GameWorld.INSTANCE.entities) {
                if (entity instanceof Bullet bullet && bullet.fromPlayer && attack != bullet.color && intersects(bullet)) {
                    attack |= bullet.color;
                    bullet.removed = true;
                }
            }

            //shoot
            shoot();
        }

        //lose health
        for (Entity entity : GameWorld.INSTANCE.entities) {
            if (((entity instanceof Bullet bullet && !bullet.fromPlayer) || entity instanceof Enemy) && intersects(entity, 0.33f, 0.33f)) {
                GameWorld.INSTANCE.die(this);
                entity.removed = true;
            }
        }
    }

    private void applyImpulse(float impulseX, float impulseY, float speed) {
        //apply impulse
        x += impulseX * speed;
        y += impulseY * speed;

        //constrain between the screen bounds
        float w = this.w / 2f;
        float h = this.h / 2f;
        if (x < w) x = w;
        if (y < w) y = w;
        if (x > 300 - w) x = 300 - w;
        if (y > 450 - h) y = 450 - h;
    }

    private void shoot() {
        Bullet b = new Bullet(x, y - w / 2f - 8, attack, true);
        b.dy = -1;
        GameWorld.INSTANCE.addEntity(b);
    }

    @Override
    public void render(MatrixStack matrices, float delta) {
        float x = Math.lerp(ox, this.x, delta);
        float y = Math.lerp(oy, this.y, delta);

        if (attack != 0)
            VertexConsumer.MAIN.consume(GeometryHelper.circle(matrices, x, y, w / 8f, 16, 0xFF000000 | attack));

        super.render(matrices, delta);

        if (!Main.debug)
            return;

        float w = (this.w * 0.33f) / 2f;
        float h = (this.h * 0.33f) / 2f;
        VertexConsumer.MAIN.consume(GeometryHelper.rectangle(matrices, x - w, y - h, x + w, y + h, 0xFF0000FF));
    }

    public void reset() {
        this.lives = 3;
        this.attack = 0;
        this.removed = false;
        this.x = 300 / 2f;
        this.y = 450 - 32;
    }
}
