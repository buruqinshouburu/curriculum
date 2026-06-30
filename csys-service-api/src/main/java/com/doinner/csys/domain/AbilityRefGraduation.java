package com.doinner.csys.domain;

public class AbilityRefGraduation {

    // 能力素质id t_csys_std_ability
    private Long abilityId;

    // 毕业标准id t_csys_std_graduation
    private Long graduationId;


    public Long getAbilityId() {
        return abilityId;
    }

    public void setAbilityId(Long abilityId) {
        this.abilityId = abilityId;
    }

    public Long getGraduationId() {
        return graduationId;
    }

    public void setGraduationId(Long graduationId) {
        this.graduationId = graduationId;
    }
}
