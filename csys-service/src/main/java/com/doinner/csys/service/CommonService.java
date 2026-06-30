package com.doinner.csys.service;

import com.doinner.csys.domain.StandardMajor;
import org.springframework.data.domain.Page;

import java.io.InputStream;
import java.util.List;

public interface CommonService {

    String analysisTeachPlan(String id,Long courseId);

    String analysisTeachPlanPractice(String id,Long courseId);

    String analysisTeachPlanAll(String id,Long courseId);

    List<StandardMajor> selectStandardMajorListByCollegeId(Long collegeId);

    List<StandardMajor> selectStandardMajorListByParentId(Long parentId);

    Page<StandardMajor> treeSubMajorList(StandardMajor standardMajor);

    StandardMajor addStandardMajor(StandardMajor standardMajor);

    StandardMajor updateStandardMajor(StandardMajor standardMajor);

    void deleteStandardMajor(Long id);

    String uploadFile(InputStream inputStream, String fileName, String categoryId);

    List getSubMajorList();
}
