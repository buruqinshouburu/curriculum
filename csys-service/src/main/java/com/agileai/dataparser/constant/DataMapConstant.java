package com.agileai.dataparser.constant;

import com.doinner.csys.domain.StandardCultivation;
import com.doinner.csys.domain.StandardCultivationTarget;
import com.doinner.csys.domain.StandardGraduation;
import org.bson.types.ObjectId;

import java.util.HashMap;
import java.util.Map;

public class DataMapConstant {

    public static final Map<ObjectId, StandardCultivationTarget> sctMap = new HashMap<>();

    public static final Map<ObjectId, StandardGraduation> sgMap = new HashMap<>();

    public static final Map<ObjectId, StandardCultivation> scMap = new HashMap<>();

    public static final Map<ObjectId, Long> courseIdMap = new HashMap<>();

}
