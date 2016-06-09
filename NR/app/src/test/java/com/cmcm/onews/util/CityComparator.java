package com.cmcm.onews.util;

import com.cmcm.onews.model.ONewsCity;

import java.util.Comparator;

/**
 * Created by cm on 2016/4/13.
 */
public class CityComparator implements Comparator<ONewsCity> {
    @Override
    public int compare(ONewsCity lhs, ONewsCity rhs) {
        if (lhs == null) return -1;
        if (rhs == null) return 1;
        int gidSize = lhs.getGid().compareTo(rhs.getGid());
        if (gidSize == 0) {
            return lhs.getCity().compareTo(rhs.getCity());
        } else
            return gidSize;
    }
}
