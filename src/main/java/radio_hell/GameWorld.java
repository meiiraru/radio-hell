package radio_hell;

import cinnamon.gui.Screen;
import cinnamon.gui.widgets.Container;
import cinnamon.gui.widgets.ContainerGrid;
import cinnamon.gui.widgets.Widget;
import cinnamon.gui.widgets.types.Button;
import cinnamon.gui.widgets.types.Label;
import cinnamon.model.GeometryHelper;
import cinnamon.render.MatrixStack;
import cinnamon.render.batch.VertexConsumer;
import cinnamon.text.Style;
import cinnamon.text.Text;
import cinnamon.utils.Alignment;
import cinnamon.utils.Colors;
import cinnamon.world.gui.Hud;
import org.lwjgl.glfw.GLFW;
import radio_hell.world.Entity;
import radio_hell.world.Player;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class GameWorld extends Screen {

    public static final GameWorld INSTANCE = new GameWorld();

    public final List<Entity> entities = new ArrayList<>();
    public final Player player = new Player(1);
    public final Player player2 = new Player(2);

    public boolean hasP2 = false;
    public boolean gameOver = false;
    public int lastWaveTime = 0;

    public int score = 0;
    public int time = 0;

    private Container gameOverContainer;
    private Label scoreLabel, timeLabel;

    private GameWorld() {}

    @Override
    public void init() {
        super.init();

        gameOverContainer = new Container(0, 0);
        gameOverContainer.addWidget(new Widget(0, 0, width, height) {
            @Override
            public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
                VertexConsumer.MAIN.consume(GeometryHelper.rectangle(matrices, getX(), getY(), getX() + getWidth(), getY() + getHeight(), 0xAA000000));
            }
        });

        Label over = new Label(width / 2, height / 4, Text.of("Game Over").withStyle(Style.EMPTY.outlined(true).color(Colors.RED)), Alignment.TOP_CENTER);
        over.setTextScale(3f);
        gameOverContainer.addWidget(over);

        scoreLabel = new Label(width / 2, height / 2, Text.empty().withStyle(Style.EMPTY.outlined(true)), Alignment.CENTER);
        scoreLabel.setTextScale(2f);
        gameOverContainer.addWidget(scoreLabel);

        timeLabel = new Label(width / 2, scoreLabel.getY() + scoreLabel.getHeight() + 16, Text.empty().withStyle(Style.EMPTY.outlined(true)), Alignment.TOP_CENTER);
        timeLabel.setTextScale(1.5f);
        gameOverContainer.addWidget(timeLabel);

        ContainerGrid actions = new ContainerGrid(width / 2, height - height / 4, 4, 2);
        actions.setAlignment(Alignment.TOP_CENTER);
        actions.setSkin(Hud.SKIN);
        gameOverContainer.addWidget(actions);

        Button restart = new Button(0, 0, 100, 20, Text.of("Restart"), _ -> restart());
        actions.addWidget(restart);

        Button exit = new Button(0, 0, 100, 20, Text.of("Exit"), _ -> client.disconnect());
        actions.addWidget(exit);

        if (gameOver)
            toggleGameOver();
    }

    @Override
    public void tick() {
        super.tick();

        for (Iterator<Entity> iterator = entities.iterator(); iterator.hasNext(); ) {
            Entity entity = iterator.next();
            if (entity.removed)
                iterator.remove();
            else
                entity.tick();
        }

        if (--lastWaveTime <= 0)
            lastWaveTime = EnemySpawns.chooseRandom();

        if (!gameOver)
            time++;
    }

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        for (Entity entity : entities)
            entity.render(matrices, delta);
        super.render(matrices, mouseX, mouseY, delta);
    }

    @Override
    protected void renderChildren(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        //debug
        if (Main.debug) {
            Text.of("Entities: " + entities.size()).withStyle(Style.EMPTY.outlined(true))
                    .append("\n")
                    .append("Wave in: " + (lastWaveTime / 20) + "s")
                    .render(VertexConsumer.MAIN, matrices, 4, 4);

            if (gameOver) {
                super.renderChildren(matrices, mouseX, mouseY, delta);
                return;
            }
        }

        //score
        matrices.pushMatrix();
        matrices.translate(width / 2f, 4, 0);
        matrices.scale(1.5f, 1.5f, 1f);
        Text.of(score).withStyle(Style.EMPTY.outlined(true)).render(VertexConsumer.MAIN, matrices, 0, 0, Alignment.TOP_CENTER);
        matrices.popMatrix();

        //time
        Text.of("%02d .".formatted((time / 20) / 60)).withStyle(Style.EMPTY.outlined(true))
                .append("%02d .".formatted((time / 20) % 60))
                .append(Text.of(String.format("%#.2f", (time % 20 / 20f)).substring(2)).withStyle(Style.EMPTY.color(Colors.LIGHT_GRAY)))
                .render(VertexConsumer.MAIN, matrices, width - 4, 4, Alignment.TOP_RIGHT);

        //lives

        //p1
        Text.of("\u2764".repeat(Math.max(player.lives, 0))).withStyle(Style.EMPTY.outlined(true).color(Colors.RED))
                .render(VertexConsumer.MAIN, matrices, 4, height - 4, Alignment.BOTTOM_LEFT);

        //p2
        if (hasP2) {
            Text.of("\u2764".repeat(Math.max(player2.lives, 0))).withStyle(Style.EMPTY.outlined(true).color(Colors.RED))
                    .render(VertexConsumer.MAIN, matrices, width - 4, height - 4, Alignment.BOTTOM_RIGHT);
        } else if (!gameOver) {
            Text.of("P2 Press ATTACK").withStyle(Style.EMPTY.outlined(true).color(time % 40 < 20 ? Colors.YELLOW : Colors.LIGHT_GRAY))
                    .render(VertexConsumer.MAIN, matrices, width - 4, height - 4, Alignment.BOTTOM_RIGHT);
        }

        super.renderChildren(matrices, mouseX, mouseY, delta);
    }

    @Override
    protected void renderBackground(MatrixStack matrices, float delta, int color1, int color2, float size) {
        renderSolidBackground(0xFF202020);
    }

    public void addEntity(Entity entity) {
        client.queueTick(() -> entities.add(entity));
    }

    @Override
    public boolean keyPress(int key, int scancode, int action, int mods) {
        if (!gameOver && !hasP2 && action == GLFW.GLFW_PRESS && (key == GLFW.GLFW_KEY_KP_4 || key == GLFW.GLFW_KEY_KP_5 || key == GLFW.GLFW_KEY_KP_6)) {
            hasP2 = true;
            entities.add(player2);
        }

        return super.keyPress(key, scancode, action, mods);
    }

    public void score(int amount) {
        score += amount;
    }

    public void die(Player player) {
        Player other = player == this.player ? this.player2 : this.player;
        player.lives--;
        player.power = 0;

        if (player.lives < 0) {
            if (hasP2 && other.lives > 0) {
                other.lives--;
                return;
            }

            player.removed = true;

            if ((!hasP2 || other.lives < 0) && !gameOver) {
                gameOver = true;
                System.out.println("Game Over! Score: " + score);
                toggleGameOver();
            }
        }
    }

    private void toggleGameOver() {
        scoreLabel.setText(Text.of("Score: " + score).withStyle(Style.EMPTY.outlined(true)));
        timeLabel.setText(Text.of("Time:\n").withStyle(Style.EMPTY.outlined(true))
                .append(Text.of("%02d.".formatted((time / 20) / 60)))
                .append("%02d.".formatted((time / 20) % 60))
                .append(Text.of(String.format("%#.2f", (time % 20 / 20f)).substring(2)))
        );
        addWidget(gameOverContainer);
    }

    public void restart() {
        entities.clear();
        entities.add(player);

        player.reset();
        player2.reset();

        score = 0;
        time = 0;
        lastWaveTime = 0;
        hasP2 = false;
        gameOver = false;

        if (gameOverContainer != null)
            removeWidget(gameOverContainer);
    }
}
