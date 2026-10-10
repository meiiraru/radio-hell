package radio_hell.world;

import cinnamon.model.GeometryHelper;
import cinnamon.model.Vertex;
import cinnamon.render.MatrixStack;
import cinnamon.render.batch.VertexConsumer;
import cinnamon.utils.Resource;
import org.joml.Math;

public class Bullet extends Entity {

    public final int color;
    public final boolean fromPlayer;

    public Bullet(float x, float y, int color, boolean fromPlayer) {
        super(x, y, 16, 16, new Resource("radio_hell", fromPlayer ? "textures/bullet.png" : "textures/enemy_bullet.png"));
        this.color = 0xFF000000 | color;
        this.fromPlayer = fromPlayer;
        this.speed = 32f;
    }

    @Override
    public void tick() {
        super.tick();

        if (x < -w || x > 300 + w || y < -h || y > 450 + h)
            removed = true;
    }

    @Override
    public void render(MatrixStack matrices, float delta) {
        float x = Math.lerp(ox, this.x, delta) - this.w / 2f;
        float y = Math.lerp(oy, this.y, delta) - this.h / 2f;

        Vertex[] vertices = GeometryHelper.quad(matrices, x, y, w, h);
        for (Vertex vertex : vertices)
            vertex.color(color);

        VertexConsumer.MAIN.consume(vertices, texture);
    }
}