package com.doinner.csys.service;

import com.doinner.common.core.domain.Message;
import com.doinner.csys.domain.TeachingProgrammeInstance;
import com.doinner.csys.domain.TeachingProgrammeInstanceDto;
import com.doinner.csys.domain.TeachingProgrammeInstanceExtract;
import com.doinner.csys.domain.TeachingProgrammeOutline;
import com.doinner.file.api.domain.FileInfo;
import org.springframework.data.domain.Page;

import java.util.List;

public interface TeachingProgrammeService {

    TeachingProgrammeOutline addOutline(TeachingProgrammeOutline outline);

    TeachingProgrammeOutline updateOutline(TeachingProgrammeOutline outline);

    List<TeachingProgrammeOutline> outlineList(TeachingProgrammeOutline outline);

    void deleteOutlines(Long[] ids);

    TeachingProgrammeOutline addTemplate(TeachingProgrammeOutline outline);

    TeachingProgrammeOutline updateTemplate(TeachingProgrammeOutline outline);

    TeachingProgrammeOutline selectOutlineTree(Long id);


    TeachingProgrammeInstance addInstance(TeachingProgrammeInstance instance);

    TeachingProgrammeInstance updateInstance(TeachingProgrammeInstance instance);

    TeachingProgrammeInstance selectInstance(Long id);

    TeachingProgrammeInstance instanceAddDetails(TeachingProgrammeInstance instance);

    TeachingProgrammeInstance instanceUpdateDetails(TeachingProgrammeInstance instance);

    List<TeachingProgrammeInstance> instanceList(TeachingProgrammeInstance instance);

    void deleteInstance(Long[] ids);


    TeachingProgrammeInstance createWord(Long instanceId);

    FileInfo downloadAndPreView(String fileId);

    TeachingProgrammeInstanceExtract extractTeachingProgramme(TeachingProgrammeInstanceExtract teachingProgrammeInstanceExtract);

    TeachingProgrammeInstanceExtract addTeachingProgramme(TeachingProgrammeInstanceExtract teachingProgrammeInstanceExtract);

    TeachingProgrammeInstanceExtract updateTeachingProgramme(TeachingProgrammeInstanceExtract teachingProgrammeInstanceExtract);

    void deleteTeachingProgramme(List<Long> ids);

    Page<TeachingProgrammeInstanceExtract> selectExtractTeachingProgramme(TeachingProgrammeInstanceExtract teachingProgrammeInstanceExtract);


    Message copyInstance(TeachingProgrammeInstanceDto instance);
}
