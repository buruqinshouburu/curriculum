package com.doinner.csys.domain.vo;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * 团队成员保存：members 接受第三方 {email,userName,unitName} 数组。
 * 也可传 teamMemberIds（逗号分隔帐号）与 teamMemberNames（顺序对应姓名）。
 * 原 userIds 字段仍接受帐号数组/串；这里的 ID 是帐号，不是系统数字主键。
 * 姓名仅用于展示，不参与鉴权；空数组/空串用于清空可见团队成员。
 */
public class CourseTeamMemberSaveVo {

    /** 统一认证帐号：数组或英文逗号拼接字符串；空值表示清空（新增接口下为必填） */
    private Object userIds;

    /** userIds 的别名，与课程负责人 authorIds 同形状（英文逗号拼接），便于前端复用同一段赋值代码 */
    private String teamMemberIds;
    /** 与帐号串顺序对应的姓名串。 */
    private String teamMemberNames;
    /** 推荐直接传第三方人员数组，成员的 email 是帐号、userName 是姓名。 */
    private List<CoursePersonVo> members;

    /**
     * 解析为待校验的ID字符串列表；返回 null 表示请求未提交人员字段（区别于空列表=清空）。
     */
    public List<String> resolvedUserIds() {
        Object source = userIds != null ? userIds : teamMemberIds;
        if (source == null) {
            return null;
        }
        List<String> values = new ArrayList<>();
        if (source instanceof Collection<?>) {
            for (Object item : (Collection<?>) source) {
                if (item != null) {
                    values.add(String.valueOf(item).trim());
                }
            }
            return values;
        }
        String text = String.valueOf(source).trim();
        if (text.isEmpty()) {
            return values;
        }
        for (String item : text.split(",", -1)) {
            values.add(item.trim());
        }
        return values;
    }

    public Object getUserIds() {
        return userIds;
    }

    public void setUserIds(Object userIds) {
        this.userIds = userIds;
    }

    public String getTeamMemberIds() {
        return teamMemberIds;
    }

    public void setTeamMemberIds(String teamMemberIds) {
        this.teamMemberIds = teamMemberIds;
    }

    public String getTeamMemberNames() { return teamMemberNames; }
    public void setTeamMemberNames(String names) { this.teamMemberNames = names; }
    public List<CoursePersonVo> getMembers() { return members; }
    public void setMembers(List<CoursePersonVo> members) { this.members = members; }

    public List<CoursePersonVo> resolvedMembers() {
        if (members != null) return members;
        List<String> accounts = resolvedUserIds();
        if (accounts == null) return null;
        String[] names = teamMemberNames == null ? new String[0] : teamMemberNames.split(",", -1);
        if (teamMemberNames != null && names.length != accounts.size() && !accounts.isEmpty())
            throw new IllegalArgumentException("teamMemberIds 与 teamMemberNames 数量必须一致");
        List<CoursePersonVo> result = new ArrayList<>();
        for (int i = 0; i < accounts.size(); i++)
            result.add(new CoursePersonVo(accounts.get(i), i < names.length ? names[i] : null));
        return result;
    }
}
