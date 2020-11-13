package edu.wm.cs.cs301.TessieBaumann.generation;

public class StubOrder implements Order {

    int skillLevel;
    Builder builder;
    boolean isPerfect;
    int seed;
    Maze mazeConfig;


    public StubOrder(int skillLevel, Builder builder, boolean isPerfect, int seed) {
        this.skillLevel = skillLevel;
        this.builder = builder;
        this.isPerfect = isPerfect;
        this.seed = seed;
    }

    @Override
    public int getSkillLevel() {
        return skillLevel;
    }

    @Override
    public Builder getBuilder() {
        return builder;
    }

    @Override
    public boolean isPerfect() {
        return isPerfect;
    }

    @Override
    public int getSeed() {
        return seed;
    }

    public Maze getMaze() {
        return mazeConfig;
    }

    @Override
    public void deliver(Maze mazeConfig) {
        this.mazeConfig = mazeConfig;

    }

    @Override
    public void updateProgress(int percentage) {
    }

}
