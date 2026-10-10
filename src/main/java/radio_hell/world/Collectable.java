package radio_hell.world;

import cinnamon.utils.Resource;
import radio_hell.GameWorld;

public class Collectable extends Entity {

    public Collectable(float x, float y) {
        super(x, y, 16, 16, new Resource("radio_hell", "textures/collectable.png"));
        this.speed = 8;
        this.dy = 1;
    }

    @Override
    public void tick() {
        super.tick();

        if (y > 450 + h || x < -1000 || x > 1000 || y < -1000) {
            removed = true;
            return;
        }

        for (Entity entity : GameWorld.INSTANCE.entities) {
            if (entity instanceof Player player && !player.removed && intersects(player)) {
                interact(player);
                removed = true;
                return;
            }
        }
    }

    public void interact(Player player) {
        if (player.lives >= 5) {
            player.power++;
            return;
        }

        if (Math.random() < 0.5f) {
            player.lives++;
        } else {
            player.power++;
        }
    }
}
