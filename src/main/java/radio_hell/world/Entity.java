package radio_hell.world;

import cinnamon.model.GeometryHelper;
import cinnamon.render.MatrixStack;
import cinnamon.render.batch.VertexConsumer;
import cinnamon.utils.Resource;
import org.joml.Math;

public class Entity {

    public final int w, h;
    public final Resource texture;

    public float x, y;
    protected float ox, oy;

    public float dx, dy;
    public float speed;

    public boolean removed;

    public Entity(float x, float y, int w, int h, Resource texture) {
        this.x = this.ox = x;
        this.y = this.oy = y;
        this.w = w;
        this.h = h;
        this.texture = texture;
    }

    public void tick() {
        this.ox = this.x;
        this.oy = this.y;

        x += dx * speed;
        y += dy * speed;
    }

    public void render(MatrixStack matrices, float delta) {
        float x = Math.lerp(ox, this.x, delta) - this.w / 2f;
        float y = Math.lerp(oy, this.y, delta) - this.h / 2f;
        VertexConsumer.MAIN.consume(GeometryHelper.quad(matrices, x, y, w, h), texture);
    }

    public boolean intersects(Entity other) {
        return intersects(other, 1f, 1f);
    }

    public boolean intersects(Entity other, float wp, float hp) {
        float w1 = (this.w * wp) / 2f;
        float h1 = (this.h * hp) / 2f;
        float w2 = other.w / 2f;
        float h2 = other.h / 2f;
        return this.x - w1 < other.x + w2 && this.x + w1 > other.x - w2 &&
               this.y - h1 < other.y + h2 && this.y + h1 > other.y - h2;
    }
}
