package com.doinner.csys.utils.similar;

import com.doinner.csys.utils.DuplicateCheckUtil;

public class Levenshtein implements Similar{
    @Override
    public double calcultSimilar(String s1, String s2) {
        return DuplicateCheckUtil.getDistancePercent(s1, s2);
    }
}
