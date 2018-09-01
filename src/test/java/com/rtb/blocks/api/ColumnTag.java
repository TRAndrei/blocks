package com.rtb.blocks.api;

public class ColumnTag {
    public static final ColumnTag ZR = new ColumnTag("zr");
    public static final ColumnTag DF = new ColumnTag("df");
    public static final ColumnTag ATM = new ColumnTag("atm");

    private final String tag;

    public ColumnTag(String tag) {
        this.tag = tag;
    }
}
