package radio_hell;

import cinnamon.gui.Screen;
import cinnamon.gui.widgets.WidgetList;
import cinnamon.gui.widgets.types.Button;
import cinnamon.model.GeometryHelper;
import cinnamon.render.MatrixStack;
import cinnamon.render.batch.VertexConsumer;
import cinnamon.text.Style;
import cinnamon.text.Text;
import cinnamon.utils.Alignment;

import java.util.function.Consumer;

public class MainMenu extends Screen {

    @Override
    public void init() {
        //superclass init
        super.init();

        //create a widget list to hold our main menu buttons
        WidgetList mainMenu = new WidgetList(width / 2, height / 2, width - 20, height / 3, 4);
        mainMenu.setAlignment(Alignment.TOP_CENTER);
        addWidget(mainMenu);

        //add buttons to the main menu

        //play button
        Button play = new Btn(Text.of("Start Game").withStyle(Style.EMPTY.outlined(true)), _ -> {
            GameWorld.INSTANCE.restart();
            client.setScreen(GameWorld.INSTANCE);
        });
        mainMenu.addWidget(play);

        //exit button-
        Button exit = new Btn(Text.of("Exit").withStyle(Style.EMPTY.outlined(true).color(0xFFFF7272)), _ -> client.window.exit());
        mainMenu.addWidget(exit);
    }

    @Override
    protected void renderBackground(MatrixStack matrices, float delta, int color1, int color2, float size) {
        renderSolidBackground(0xFF202020);
    }

    private static class Btn extends Button {

        public Btn(Text message, Consumer<Button> action) {
            super(0, 0, 148, 20, message, action);
            this.setRenderBackground(false);
        }

        @Override
        public void renderWidget(MatrixStack matrices, int mouseX, int mouseY, float delta) {
            if (isHoveredOrFocused()) {
                VertexConsumer.MAIN.consume(GeometryHelper.rectangle(matrices, getX(), getY(), getX() + getWidth(), getY() + getHeight(), 0xAAFFFFFF));
            }
            super.renderWidget(matrices, mouseX, mouseY, delta);
        }
    }
}
