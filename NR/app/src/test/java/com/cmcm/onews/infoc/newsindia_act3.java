package com.cmcm.onews.infoc;


import com.cmcm.onews.util.ConflictCommons;

/**
 * CREATE FROM: newsindia_act3:117 uptime2:int
 */
public class newsindia_act3 extends act {

    private static String sSuperName = ConflictCommons.isCNVersion() ? "newscn_act3" : "newsindia_act3";

    public newsindia_act3() {
        super(sSuperName);
    }

    @Override
    public void reset() {

    }


}

