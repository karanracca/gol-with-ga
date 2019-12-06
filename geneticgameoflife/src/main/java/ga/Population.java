package ga;

import java.util.ArrayList;

public class Population {
    private ArrayList<DNA> pool;

    public Population() {
        this.pool = new ArrayList<DNA>();
    }

    public ArrayList<DNA> getPool() {
        return pool;
    }

    public void setPool(ArrayList<DNA> pool) {
        this.pool = pool;
    }

    public void add(DNA dna) {
        this.pool.add(dna);
    }
}
