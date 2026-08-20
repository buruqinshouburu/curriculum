# 教学计划前端 API 调整说明（更新至 2026-08-14）

本文仅描述本次教学计划改动中需要前端同步的部分。统一前缀沿用现有 `/teachingPlan`。

## 1. 当前培养方案整表保存

课程目标和实验课程任务背景均按“当前培养方案”整表重建。

- 非公共基础课程：`schemeId` 必填，数组只允许包含该方案的数据。
- 公共基础课程：不传 `schemeId`，后端只重建 `scheme_id IS NULL` 的公共组。
- 保存一个方案不会删除同计划其他方案的数据。

### 课程目标

`POST /teachingPlan/objective/batchSave`

```json
{
  "planId": 6001,
  "schemeId": 10001,
  "objectives": [
    {
      "objective": {
        "objectiveTypeCode": "1",
        "content": "掌握课程基本知识",
        "weight": 0.4,
        "sort": 1
      },
      "refs": [{"graduationId": 90101}]
    }
  ]
}
```

### 实验课程任务背景

`POST /teachingPlan/taskBackground/batchSave`

请求规则与课程目标一致：非公共基础传当前 `schemeId`，公共基础不传。

## 2. 实践训练课目：训练内容与时间安排

“目的”不再绑定第二部分训练目的，也不传 `purposeIds`。整个输入框作为一个字符串保存到 `TeachingPlanContent.purpose`。

新增：`POST /teachingPlan/content`

修改：`PUT /teachingPlan/content`

```json
{
  "id": 62211,
  "planId": 6002,
  "title": "1",
  "content": "单个军人队列动作与班队列训练",
  "purpose": "掌握单个军人队列动作、班队列组织等基本军事素养",
  "timeArrange": "16学时",
  "sort": 1
}
```

注意：`purpose` 中的顿号、逗号均属于正文，前端不得拆分。

原 `/contentPurpose/list`、`/contentPurpose/save` 不再用于本页面。

## 3. 实验项目输入字段

项目主记录使用 `/practiceItem`，各输入框使用 `/practiceItemDetail`。新增项目时 `itemType=1`。

| 页面字段 | detailType | content |
|---|---|---|
| 实验目的与任务 | `purpose_task` | 输入字符串 |
| 训练的能力点 | `ability_point` | 输入字符串 |
| 实验原理 | `principle` | 输入字符串 |
| 实验内容及要求 | `content_requirement` | 输入字符串 |
| 实验结果及要求 | `result_requirement` | 输入字符串 |

`objectiveId` 当前不传，训练的能力点暂按普通字符串保存。

## 4. 实践项目明细

实践项目 `itemType=2`，必须保留以下四个明细输入框：

| 页面字段 | detailType |
|---|---|
| 拟解决的复杂问题 | `complex_problem` |
| 主要任务 | `main_task` |
| 总体设计 | `overall_design` |
| 成果形式及要求 | `outcome_requirement` |

## 5. 实践项目成果与评价

继续复用 `/assessment` 接口和 `t_csys_teaching_plan_assessment`，但页面字段与普通考核不同，`assessmentCategory` 固定传 `5`。

| 页面字段 | API 字段 | 规则 |
|---|---|---|
| 成果类型 | `outcomeType` | 字典 `sys_plan_outcome_type` 的 value |
| 成果形式 | `assessmentItem` | 直接输入字符串 |
| 评价的知识和能力 | `assessedContent` | 暂时直接输入字符串 |
| 权重 | `weight` | 0～1 的小数 |
| 评价准则 | `standard` | 直接输入字符串 |
| 项目计分规则 | `scoreRule` | 回写教学计划主表 |

`method`、`mechanism`、`scoreSystem` 不用于成果评价，前端不要传。
后端校验每一项 `weight` 必须是 0～1 的小数；本次暂不校验所有成果权重合计是否为 1。

新增：`POST /teachingPlan/assessment`

修改：`PUT /teachingPlan/assessment`

```json
{
  "planId": 6004,
  "assessmentCategory": 5,
  "outcomeType": 1,
  "assessmentItem": "成果答辩与文档评审",
  "assessedContent": "系统设计、编码实现与文档质量",
  "weight": 0.4,
  "standard": "能够完整说明设计方案并提交规范文档",
  "sort": 1,
  "scoreRule": "项目成绩按各项成果权重加权计算"
}
```

列表 `GET /teachingPlan/assessment/list?planId=6004` 会额外返回 `outcomeTypeName` 供展示。

### 成果类型字典

字典类型：`sys_plan_outcome_type`

| label | value |
|---|---|
| 个人成果 | 1 |
| 团队成果 | 2 |
| 过程成果 | 3 |

## 6. 实践项目第二部分：任务背景与目标

页面四块内容统一读取、统一保存，不再分别调用章节和支撑绑定保存接口：

- 拟解决的复杂问题；
- 主要任务；
- 支撑的课程目标或训练目的；
- 涉及的知识体系或训练内容。

### 整页详情

`GET /teachingPlan/practiceProject/background/detail?planId={planId}`

返回正文、选中 id 和已绑定完整对象。`supportObjectives`、`supportContents` 可直接用于名称回显；三个 id 数组用于候选树勾选。

```json
{
  "planId": 6004,
  "complexProblem": "<p>设计并实现综合管理系统</p>",
  "mainTask": "<p>完成需求分析、设计、编码和测试</p>",
  "objectiveIds": [60011],
  "purposeIds": [62111],
  "contentIds": [65011, 62211],
  "supportObjectives": [],
  "supportContents": []
}
```

### 整页保存

`POST /teachingPlan/practiceProject/background/save`

```json
{
  "planId": 6004,
  "complexProblem": "<p>设计并实现综合管理系统</p>",
  "mainTask": "<p>完成需求分析、设计、编码和测试</p>",
  "objectiveIds": [60011],
  "purposeIds": [62111],
  "contentIds": [65011, 62211]
}
```

这是整页覆盖保存：正文传 `null` 或空串均表示清空；任一 id 列表为 `null` 或空数组均表示清空该类绑定。四块数据在同一事务内提交，任一 id 无法对应时全部回滚。

以下旧接口已删除，前端不得继续调用：

- `GET /teachingPlan/support/candidates`
- `GET /teachingPlan/supportObjective/list`
- `POST /teachingPlan/supportObjective/save`
- `GET /teachingPlan/supportContent/list`
- `POST /teachingPlan/supportContent/save`
- 通用 `/section` 接口仍供其他教学计划章节使用，但本页面的“拟解决的复杂问题、主要任务”不再单独调用它。

### 支撑绑定候选树

`GET /teachingPlan/support/candidateTree?courseId={courseId}&projectPlanId={planId}`

返回培养方案分组，`sameScheme=true` 的组优先。组内分为：

- `objectives`：课程目标；
- `purposes`：训练目的；
- `knowledgePoints`：涉及的知识体系；
- `trainingContents`：训练内容。

候选单条新增返回 `schemeId`、`schemeName`，用于已选内容显示所属培养方案。保存仍传目标/目的/content 的 ID。来源数据修改后，绑定列表和 Word 生成读取最新内容。

## 7. Word 导入结果

- `issues[].severity = WARN`：单条内容无法匹配，跳过该条，其他数据继续导入。
- `issues[].severity = ERROR`：整单不落库，旧教学计划不会被清空。
- Word 类型与接口 `planType` 不一致时返回 ERROR。
