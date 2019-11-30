package ga;

import life.Life;

public class GA {
    public static void main(String arg[]) {
        Life life;
        int gen;
        for (int i = 0; i < 5; i++) {
            life = new Life(1000, 1000);
            gen = life.startGame(null);
            System.out.println(gen);
            life.getFrame().dispose();
        }
    }


}
