package com.rtb.blocks.api;

public class SimulationId {
    public static final SimulationId BASE = new SimulationId("Base", null);
    private final String tag;
    private final SimulationId parent;

    public SimulationId(String tag, SimulationId parent) {
        this.tag = tag;
        this.parent = parent;
    }
}
