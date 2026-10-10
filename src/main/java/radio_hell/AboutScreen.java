package radio_hell;

import cinnamon.gui.ParentedScreen;
import cinnamon.gui.Screen;
import cinnamon.gui.widgets.types.Label;
import cinnamon.render.MatrixStack;
import cinnamon.text.Text;
import cinnamon.world.gui.Hud;

public class AboutScreen extends ParentedScreen {

    public AboutScreen(Screen parentScreen) {
        super(parentScreen);
    }

    @Override
    public void init() {
        super.init();
        this.mainContainer.setSkin(Hud.SKIN);

        Label label = new Label(16, 16, Text.empty());
        label.setMaxWidth(width - 32);
        addWidget(label);

        label.setText(Text.of(
                """
                        Wave matching bullet hell game!
                        Survive!
                        
                        Controls:
                        
                        PLAYER 1:
                        WASD - move
                        J - RED attack
                        K - GREEN attack
                        L - BLUE attack
                        
                        PLAYER 2:
                        ↑←↓→ - move
                        NUMPAD 4 - RED attack
                        NUMPAD 5 - GREEN attack
                        NUMPAD 6 - BLUE attack"""
        ));
    }

    @Override
    protected void renderBackground(MatrixStack matrices, float delta, int color1, int color2, float size) {
        renderSolidBackground(0xFF202020);
    }
}
