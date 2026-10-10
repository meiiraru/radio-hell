package radio_hell;

import cinnamon.events.Await;
import cinnamon.math.Maths;
import cinnamon.utils.ColorUtils;
import org.joml.Math;
import radio_hell.world.Enemy;

public class EnemySpawns {

    public static int getRandomColor() {
        float r = Math.random() < 0.5f ? 1f : 0f;
        float g = Math.random() < 0.5f ? 1f : 0f;
        float b = Math.random() < 0.5f ? 1f : 0f;
        if (r == 0f && g == 0f && b == 0f) {
            int color = Maths.range(0, 3);
            if (color == 0) r = 1f;
            if (color == 1) g = 1f;
            if (color == 2) b = 1f;
        }
        return ColorUtils.rgbToInt(r, g, b);
    }

    public static int chooseRandom() {
        return switch (Maths.range(0, 12)) {
            case 0 -> columns();
            case 1 -> zigzag();
            case 2 -> crossfire();
            case 3 -> circleFormation();
            case 4 -> vFormation();
            case 5 -> spiral();
            case 6 -> randomRain();
            case 7 -> gridBlock();
            case 8 -> starburst();
            case 9 -> pincerAttack();
            case 10 -> sineWave();
            case 11 -> diamondFormation();
            default -> 0;
        };
    }

    public static int columns() {
        System.out.println("columns");

        //left column
        int color = getRandomColor();
        for (int i = 0; i < 5; i++) {
            Enemy e = new Enemy(32, -32, color);
            e.dy = 1;
            //every 15 ticks, spawn an enemy
            new Await(15 * i, () -> GameWorld.INSTANCE.addEntity(e));
        }

        //middle column
        color = getRandomColor();
        for (int i = 0; i < 5; i++) {
            Enemy e = new Enemy(300 / 2f, -32, color);
            e.dy = 1;
            //20 ticks additional delay for middle column
            new Await(15 * i + 20, () -> GameWorld.INSTANCE.addEntity(e));
        }

        //right column
        color = getRandomColor();
        for (int i = 0; i < 5; i++) {
            Enemy e = new Enemy(300 - 32, -32, color);
            e.dy = 1;
            //40 ticks additional delay for right column
            new Await(15 * i + 40, () -> GameWorld.INSTANCE.addEntity(e));
        }

        //return wave duration
        return 20 * 7;
    }

    public static int zigzag() {
        System.out.println("zigzag");

        for (int i = 0; i < 3; i++) {
            new Await(20 * i, () -> {
                Enemy e1 = new Enemy(300 / 2f, -32, getRandomColor());
                e1.dx = 1;
                e1.dy = 1;
                e1.speed = 6;

                Enemy e2 = new Enemy(300 / 2f + 96, -32, getRandomColor());
                e2.dx = 1;
                e2.dy = 1;
                e2.speed = 6;

                GameWorld.INSTANCE.addEntity(e1);
                GameWorld.INSTANCE.addEntity(e2);

                for (int j = 0; j < 5; j++) {
                    //every 15 ticks, swap the direction of the enemies
                    new Await(15 * j, () -> {
                        e1.dx *= -1;
                        e2.dx *= -1;
                    });
                }
            });
        }

        return 20 * 5;
    }

    public static int crossfire() {
        System.out.println("crossfire");

        for (int i = 0; i < 6; i++) {
            //left side
            new Await(20 * i, () -> {
                Enemy eLeft = new Enemy(-32, 40, getRandomColor());
                eLeft.dx = 1.5f; //move right
                eLeft.dy = 0.5f;
                eLeft.speed = 5;
                GameWorld.INSTANCE.addEntity(eLeft);
            });

            //right side
            new Await(20 * i + 10, () -> {
                Enemy eRight = new Enemy(300 + 32, 70, getRandomColor());
                eRight.dx = -1.5f; //mode left
                eRight.dy = 0.5f;
                eRight.speed = 5;
                GameWorld.INSTANCE.addEntity(eRight);
            });
        }

        return 20 * 7;
    }

    public static int circleFormation() {
        System.out.println("circle formation");

        int centerX = 300 / 2;
        int centerY = -64;
        int radius = 80;
        int enemies = 8;
        int color = getRandomColor();

        for (int i = 0; i < enemies; i++) {
            float angle = (Math.PI_TIMES_2_f / enemies) * i;
            float x = centerX + Math.cos(angle) * radius;
            float y = centerY + Math.sin(angle) * radius;

            Enemy e = new Enemy(x, y, color);
            e.dy = 1;
            GameWorld.INSTANCE.addEntity(e);
        }
        return 20 * 3;
    }

    public static int vFormation() {
        System.out.println("v formation");

        float centerX = 300 / 2f;
        int color = getRandomColor();

        for (int i = 0; i < 4; i++) {
            if (i == 0) {
                Enemy tip = new Enemy(centerX, -32, color);
                tip.dy = 1;
                GameWorld.INSTANCE.addEntity(tip);
            } else {
                //left wing
                Enemy left = new Enemy(centerX - (i * 32), -32 - (i * 32), color);
                left.dy = 1;
                GameWorld.INSTANCE.addEntity(left);

                //right wing
                Enemy right = new Enemy(centerX + (i * 32), -32 - (i * 32), color);
                right.dy = 1;
                GameWorld.INSTANCE.addEntity(right);
            }
        }
        return 20 * 3;
    }

    public static int spiral() {
        System.out.println("spiral");

        for (int i = 0; i < 16; i++) {
            float angle = i * Math.PI_OVER_4_f; //45 degrees

            new Await(5 * i, () -> {
                Enemy e = new Enemy(300 / 2f, -32, getRandomColor());
                e.dx = Math.cos(angle);
                e.dy = Math.abs(Math.sin(angle)) + 0.5f; //keep moving downwards
                e.speed = 4;
                GameWorld.INSTANCE.addEntity(e);
            });
        }
        return 20 * 5;
    }

    public static int randomRain() {
        System.out.println("random rain");

        for (int i = 0; i < 15; i++) {
            new Await(10 * i, () -> {
                int randomX = Maths.range(16, 300 - 16);
                Enemy e = new Enemy(randomX, -32, getRandomColor());
                e.dy = Math.random() > 0.5 ? 1.5f : 1.0f;
                e.speed = 5;
                GameWorld.INSTANCE.addEntity(e);
            });
        }
        return 20 * 8;
    }

    public static int gridBlock() {
        System.out.println("grid block");

        float startX = 300 / 2f - 40;
        float startY = -100;
        int color = getRandomColor();

        //3 rows, 4 columns
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 4; col++) {
                Enemy e = new Enemy(startX + (col * 30), startY + (row * 30), color);
                e.dy = 0.8f;
                e.speed = 3;
                GameWorld.INSTANCE.addEntity(e);
            }
        }
        return 20 * 8;
    }

    public static int starburst() {
        System.out.println("starburst");

        //shoots 3 successive waves of bursts
        for (int wave = 0; wave < 3; wave++) {
            new Await(wave * 40, () -> {
                int color = getRandomColor();
                int numProjectiles = 7;

                for (int i = 0; i < numProjectiles; i++) {
                    float angle = (Math.PI_f / (numProjectiles - 1)) * i;
                    Enemy e = new Enemy(300 / 2f, -16, color);
                    e.dx = Math.cos(angle);
                    e.dy = Math.sin(angle);
                    e.speed = 4;
                    GameWorld.INSTANCE.addEntity(e);
                }
            });
        }
        return 20 * 6;
    }

    public static int pincerAttack() {
        System.out.println("pincer attack");

        for (int i = 0; i < 6; i++) {
            new Await(10 * i, () -> {
                //top left zooming down and right
                Enemy left = new Enemy(16, -32, getRandomColor());
                left.dx = 1.0f;
                left.dy = 1.2f;
                left.speed = 5;

                //top right zooming down and left
                Enemy right = new Enemy(300 - 16, -32, getRandomColor());
                right.dx = -1.0f;
                right.dy = 1.2f;
                right.speed = 5;

                GameWorld.INSTANCE.addEntity(left);
                GameWorld.INSTANCE.addEntity(right);
            });
        }
        return 20 * 5;
    }

    public static int sineWave() {
        System.out.println("sine wave");

        int color = getRandomColor();
        for (int i = 0; i < 15; i++) {
            float waveOffset = i * 0.6f;
            new Await(10 * i, () -> {
                float x = 300 / 2f + (Math.sin(waveOffset) * 100f);
                Enemy e = new Enemy(x, -32, color);
                e.dx = -1f;
                e.dy = 1.5f;
                e.speed = 4;
                GameWorld.INSTANCE.addEntity(e);
                for (int j = 0; j < 40; j++)
                    new Await(10 * j, () -> e.dx *= -1);
            });
        }
        return 20 * 8;
    }

    public static int diamondFormation() {
        System.out.println("diamond formation");

        float centerX = 300 / 2f;
        float startY = -128;
        int color = getRandomColor();
        int size = 2;

        for (int row = -size; row <= size; row++) {
            int cols = size - Math.abs(row);
            for (int col = -cols; col <= cols; col++) {
                Enemy e = new Enemy(centerX + (col * 32), startY + (row * 32), color);
                e.dy = 1.2f;
                e.speed = 4;
                GameWorld.INSTANCE.addEntity(e);
            }
        }
        return 20 * 5;
    }
}
