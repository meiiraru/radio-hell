package radio_hell;

import cinnamon.Cinnamon;
import cinnamon.Client;
import cinnamon.events.Events;

public class Main {

    public static final boolean debug = false;

    public static void main(String... args) {
        //setup cinnamon flags
        Cinnamon.TITLE = "Radio Hell";
        Cinnamon.NAMESPACE = "radio_hell";
        Cinnamon.WIDTH = 600;
        Cinnamon.HEIGHT = 900;

        Events.CLIENT_INIT.register("window_props", () -> {
            Client.getInstance().window.setResizable(false);
            Client.getInstance().window.updateSize(600, 900, 2f, true);
        });

        Events.WINDOW_RESIZE.register("window_resize", (_, _) -> Client.getInstance().window.updateSize(600, 900, 2f, true));

        //custom main menu
        Client.mainScreen = MainMenu::new;

        //start the engine
        new Cinnamon(args).run();
    }
}
