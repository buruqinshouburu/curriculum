package com.doinner.csys.service.impl;

import com.alibaba.nacos.shaded.org.checkerframework.checker.units.qual.K;
import com.doinner.csys.dao.StandardCultivationTargetMapper;
import com.doinner.csys.dao.StandardGraduationMapper;
import com.doinner.csys.domain.StandardCultivationTarget;
import com.doinner.csys.domain.StandardGraduation;
import com.doinner.csys.service.StandardService;
import com.hankcs.hanlp.HanLP;
import com.hankcs.hanlp.mining.word.WordInfo;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Component
public class WordCloudHandler {

    public static Map<String, AtomicInteger> standardCultivationTargetData;

    public static Map<String, Map<String, AtomicInteger>> standardGraduationData;

    public static List<String> standardCultivationTargetKeywords = new ArrayList<>();

    public static Map<String, List<String>> standardGraduationKeywords = new HashMap<>();

    public static final String KNOWLEDGE = "知识";

    public static final String POWER = "能力";

    public static final String QUALITY = "素质";

    public static final int PHRASE_LIMIT = 10;

    static{
//        standardCultivationTargetKeywords.add("系统整合思维能力");
//        standardCultivationTargetKeywords.add("推理和解决问题能力");
//        standardCultivationTargetKeywords.add("创新实践能力");
//        standardCultivationTargetKeywords.add("语言文字表达能力");
//        standardCultivationTargetKeywords.add("沟通协作能力");
//        standardCultivationTargetKeywords.add("科学批判能力");
//        standardCultivationTargetKeywords.add("知识迁移能力");
//        standardCultivationTargetKeywords.add("领导管理能力");
//        standardCultivationTargetKeywords.add("高素质新型军事人才");
//        standardCultivationTargetKeywords.add("联合作战指挥人才");
//        standardCultivationTargetKeywords.add("新型作战力量人才");
//        standardCultivationTargetKeywords.add("高层次科技创新人才");
//        standardCultivationTargetKeywords.add("高水平战略管理人才");
//
//        List<String> knowledgeKeyWords = new ArrayList<>();
//        knowledgeKeyWords.add("政治理论");
//        knowledgeKeyWords.add("军事基础");
//        knowledgeKeyWords.add("自然科学");
//        knowledgeKeyWords.add("专业领域");
//        knowledgeKeyWords.add("人文社科");
//        knowledgeKeyWords.add("发展动态");
//        standardGraduationKeywords.put(KNOWLEDGE, knowledgeKeyWords);
//
//        List<String> powerWords = new ArrayList<>();
//        powerWords.add("问题分析");
//        powerWords.add("解决方案");
//        powerWords.add("研究");
//        powerWords.add("使用现代工具");
//        powerWords.add("工程与社会");
//        powerWords.add("环境与可持续发展");
//        powerWords.add("个人与团队");
//        powerWords.add("项目管理");
//        powerWords.add("沟通");
//        standardGraduationKeywords.put(POWER, powerWords);
//
//        List<String> qualityWords = new ArrayList<>();
//        qualityWords.add("军人职业素养和规范");
//        qualityWords.add("军人作风");
//        qualityWords.add("战斗精神");
//        qualityWords.add("人文社会科学素养");
//        qualityWords.add("社会责任感");
//        qualityWords.add("军人职业道德");
//        qualityWords.add("自主学习");
//        qualityWords.add("终身学习");
//        qualityWords.add("创新意识");
//        qualityWords.add("国际视野");
//        standardGraduationKeywords.put(QUALITY, qualityWords);
    }

    @Resource
    private StandardCultivationTargetMapper standardCultivationTargetMapper;

    @Resource
    private StandardGraduationMapper standardGraduationMapper;

    // todo 定时先去掉
    //@Scheduled(fixedDelay = 30000)
    public void test(){
        for (String s : HanLP.Config.CustomDictionaryPath) {
            System.out.println(s);
        }
        Map<String, AtomicInteger> _standardCultivationTargetData = new ConcurrentHashMap<>();
        StandardCultivationTarget standardCultivationTarget = new StandardCultivationTarget();
        standardCultivationTarget.setLevel(3);
        List<StandardCultivationTarget> standardCultivationTargetList = standardCultivationTargetMapper.selectStandardCultivationTargetList(standardCultivationTarget);
        standardCultivationTargetList.parallelStream().forEach(_standardCultivationTarget -> {
            List<String> phraseList = HanLP.extractKeyword(_standardCultivationTarget.getName(), PHRASE_LIMIT);
            phraseList.parallelStream().forEach(phrase -> {
                if(_standardCultivationTargetData.containsKey(phrase)){
                    _standardCultivationTargetData.get(phrase).incrementAndGet();
                }else{
                    _standardCultivationTargetData.put(phrase, new AtomicInteger(1));
                }
            });
        });
//        standardCultivationTargetData = standardCultivationTargetData.entrySet().parallelStream()
//                .sorted(Comparator.comparingInt(entry -> ((Map.Entry<String, AtomicInteger>) entry).getValue().get())
//                        .reversed()).limit(RESULT_LIMIT).collect(Collectors.toMap(entry -> entry.getKey(), entry -> entry.getValue()));
        WordCloudHandler.standardCultivationTargetData = _standardCultivationTargetData;

        Map<String, Map<String, AtomicInteger>> _standardGraduationData = new HashMap<>();
        _standardGraduationData.put(KNOWLEDGE, new ConcurrentHashMap<>());
        _standardGraduationData.put(POWER, new ConcurrentHashMap<>());
        _standardGraduationData.put(QUALITY, new ConcurrentHashMap<>());

        StandardGraduation standardGraduation = new StandardGraduation();
        standardGraduation.setSysflag(0);
        List<StandardGraduation> standardGraduationList = standardGraduationMapper.selectStandardGraduationList(standardGraduation);
        Map<Long, String> standardGraduationIdNameMap = standardGraduationList.parallelStream().filter(_standardGraduation -> _standardGraduation.getLevel() == 2)
                .collect(Collectors.toMap(StandardGraduation::getId, StandardGraduation::getName));
        standardGraduationList.parallelStream().filter(_standardGraduation -> _standardGraduation.getLevel() == 3).forEach(_standardGraduation -> {
            String parentName = standardGraduationIdNameMap.get(_standardGraduation.getParentId());
            if(StringUtils.isBlank(parentName)){
                return;
            }
            List<String> phraseList = HanLP.extractKeyword(_standardGraduation.getName(), PHRASE_LIMIT);
            phraseList.parallelStream().forEach(phrase -> {
                if(!_standardGraduationData.containsKey(parentName)){
                    return;
                }
                if(_standardGraduationData.get(parentName).containsKey(phrase)){
                    _standardGraduationData.get(parentName).get(phrase).incrementAndGet();
                }else{
                    _standardGraduationData.get(parentName).put(phrase, new AtomicInteger(1));
                }
            });
        });
        standardGraduationData = _standardGraduationData;
//        standardGraduationData.keySet().parallelStream().forEach(mapKey -> {
//            Map<String, AtomicInteger> childMap = standardGraduationData.get(mapKey);
//            standardGraduationData.put(mapKey, childMap.entrySet().parallelStream()
//                    .sorted(Comparator.comparingInt(entry -> ((Map.Entry<String, AtomicInteger>) entry).getValue().get())
//                            .reversed()).limit(RESULT_LIMIT).collect(Collectors.toMap(entry -> entry.getKey(), entry -> entry.getValue())));
//        });
//
//        standardGraduationData.entrySet().forEach(value -> {
//            System.out.println("------------------------" + value.getKey() + "-----------------------");
//            value.getValue().entrySet().forEach(entry -> {
//                System.out.println(entry.getKey() + "\t\t" + entry.getValue());
//
//            });
//        });
    }

//    @Scheduled(fixedDelay = 600000)
//    public void random(){
//        standardCultivationTargetData = standardCultivationTargetKeywords.parallelStream()
//                .collect(Collectors.toMap(keyword -> keyword, keyword -> 20 + (int) (Math.random() * 100)));
//        standardGraduationData = standardGraduationKeywords.keySet().parallelStream().collect(Collectors.toMap(key -> key, key -> {
//            List<String> keywords = standardGraduationKeywords.get(key);
//            return keywords.parallelStream()
//                    .collect(Collectors.toMap(keyword -> keyword, keyword -> 20 + (int) (Math.random() * 100)));
//        }));
//    }

    public static void main(String[] args) {
        List<String> phraseList = HanLP.extractKeyword("具备良好的科学素养和文化修养", PHRASE_LIMIT);
        System.out.println(1);

    }
}
