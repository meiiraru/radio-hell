package radio_hell.world;

import cinnamon.model.GeometryHelper;
import cinnamon.render.MatrixStack;
import cinnamon.render.batch.VertexConsumer;
import cinnamon.utils.Resource;
import org.joml.Math;
import radio_hell.GameWorld;

import javax.swing.*;

public class Enemy extends Entity {

    public final int color;

    public int hp = 100;

    public Enemy(float x, float y, int color) {
        super(x, y, 32, 32, new Resource("radio_hell", "textures/enemy.png"));
        this.color = 0xFF000000 | color;
        this.speed = 8;
    }

    @Override
    public void tick() {
        super.tick();

        if (Math.random() < 0.01f)
            shoot();

        if (y > 450 + h || x < -1000 || x > 1000 || y < -1000) {
            removed = true;
            return;
        }

        for (Entity entity : GameWorld.INSTANCE.entities) {
            if (entity instanceof Bullet bullet && !bullet.removed) {
                if (bullet.color == color && bullet.fromPlayer && intersects(bullet)) {
                    hp -= 10;
                    GameWorld.INSTANCE.score(1);
                    bullet.removed = true;
                    if (hp <= 0) {
                        removed = true;
                        GameWorld.INSTANCE.score(100);
                        return;
                    }
                }
            }
        }
    }

    @Override
    public void render(MatrixStack matrices, float delta) {
        super.render(matrices, delta);

        float x = Math.lerp(ox, this.x, delta);
        float y = Math.lerp(oy, this.y, delta);
        VertexConsumer.MAIN.consume(
                GeometryHelper.circle(matrices, x, y + w / 2f + 8, 8, 16, 0xFF000000 | color)
        );
    }

    private void shoot() {
        Player player = GameWorld.INSTANCE.player;
        if (GameWorld.INSTANCE.hasP2) {
            //find closest player
            Player player2 = GameWorld.INSTANCE.player2;
            float dist1 = (player.x - x) * (player.x - x) + (player.y - y) * (player.y - y);
            float dist2 = (player2.x - x) * (player2.x - x) + (player2.y - y) * (player2.y - y);
            if (dist2 < dist1)
                player = player2;
        }

        float dx = player.x - x;
        float dy = player.y - y;
        float len = Math.sqrt(dx * dx + dy * dy);
        dx /= len;
        dy /= len;

        Bullet b = new Bullet(x, y + w / 2f + 8, 0xFFFFFFFF, false);
        b.speed = 8f;
        b.dx = dx;
        b.dy = dy;
        GameWorld.INSTANCE.addEntity(b);
    }
}
