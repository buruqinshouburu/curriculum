package com.doinner.csys.io.service;

import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;

public interface ImportService {

    void courseImport(MultipartFile file, Long collegeId);

    void importStandardCultivationTarget(MultipartFile file, Long schemeId);

    void importStandardGraduation(MultipartFile file, Long collegeId,Long categoryId,Long majorId,String version,String graduationType,Integer type,Long schemeId,String educationLevel);

    void importStandardCultivation(MultipartFile file, Long collegeId);

    void importTrainingScheme(MultipartFile file, Long collegeId);

    void getTemplete(int type, HttpServletResponse response);

    void importTrainingSchemeRef(MultipartFile file, Long collegeId, Long themeId);

    void importAbility(MultipartFile file, Long collegeId);

    void importStandardGraduationRefCultivationTarget(MultipartFile file, Long collegeId, Long graduationId);

    void importCultivationRefGraduation(MultipartFile file, Long collegeId, Long cultivationId);
}
