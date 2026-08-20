# 教学计划考核与评价整页保存接口

更新时间：2026-08-20

服务前缀：`/csys/teachingPlan`。

## 1. 保存接口

```http
POST /csys/teachingPlan/assessment/save
Content-Type: application/json
```

统一返回：

```json
{
  "code": 200,
  "msg": "操作成功"
}
```

请求顶层字段：

| 字段 | 必填 | 类型 | 说明 |
| --- | --- | --- | --- |
| `planId` | 是 | Long | 教学计划 ID。每条明细中的 `planId` 会被顶层值覆盖。 |
| `scoreRule` | 否 | String | 项目计分规则，直接保存到教学计划主表。实践项目页面使用；传 `null`、空串或不传均清空。 |
| `assessments` | 否 | Array | 当前页面全部考核评价行。传 `[]`、`null` 或不传表示清空该计划全部考核评价。 |

保存为整表重建：后端先校验全部行，校验通过后逻辑删除旧行，再批量写入本次数组。明细 `id` 不参与更新，前端无需区分新增、修改、删除；`sort` 不传时后端按数组顺序从 `1` 开始补齐。

## 2. 四类教学计划字段口径

同一张表 `t_csys_teaching_plan_assessment` 承载四类页面，但各类页面字段不能混用。字典选择项必须提交字典 `dict_value`，不是中文 `dict_label`。

| 教学计划 | `assessmentCategory` | 手动录入字段 | 字典字段（提交 value） | 不使用字段 |
| --- | ---: | --- | --- | --- |
| 课程教学计划 | `1` 终结性、`2` 过程性 | `weight` | `assessmentItem`、`method`、`mechanism`、`standard` | `outcomeType`、`assessedContent`、`scoreSystem` |
| 实验教学计划 | `3` 实验项目 | `assessmentItem`（实验项目名称）、`weight` | `method`、`mechanism`、`standard` | `outcomeType`、`assessedContent`、`scoreSystem` |
| 实践训练课目 | `4` 训练课目 | `assessmentItem`（考核项目）、`weight` | `method`、`mechanism`、`standard` | `outcomeType`、`assessedContent`、`scoreSystem` |
| 实践项目 | `5` 成果评价 | `assessmentItem`（成果形式）、`assessedContent`、`weight`、`standard` | `outcomeType` | `method`、`mechanism`、`scoreSystem` |

字典字段对应关系：

| API 字段 | 页面含义 | 字典 type |
| --- | --- | --- |
| `assessmentItem` | 仅课程教学计划：考核项目 | `sys_assessment_item` |
| `method` | 考试/考核方式 | `sys_assessment_method` |
| `mechanism` | 评定机制 | `sys_assessment_mechanism` |
| `standard` | 评定标准/评价标准 | `sys_evaluation_standard` |
| `outcomeType` | 仅实践项目：成果类型 | `sys_plan_outcome_type` |

`standard` 支持多个字典值时，按英文逗号拼成字符串，例如 `"1,2,3,4,5"`；不要传数组。

`scoreSystem` 是历史兼容字段，新的四类页面都不要传；评定机制统一使用 `mechanism`。实践项目成果评价即使传入 `method`、`mechanism`、`scoreSystem`，后端也会置空。

## 3. 请求示例

### 3.1 课程教学计划：终结性、过程性考核

课程页固定使用 `1`、`2` 两种类别；同一类别可有多行。

```json
{
  "planId": 6001,
  "assessments": [
    {
      "assessmentCategory": 1,
      "assessmentItem": "1",
      "method": "1",
      "mechanism": "1",
      "standard": "1,2,3,4,5",
      "weight": 0.6
    },
    {
      "assessmentCategory": 2,
      "assessmentItem": "2",
      "method": "2",
      "mechanism": "1",
      "standard": "1,2,3,4,5",
      "weight": 0.4
    }
  ]
}
```

以上 `assessmentItem`、`method`、`mechanism`、`standard` 都是字典 value，不是“期末考试”“闭卷考试”等展示文字。

### 3.2 实验教学计划：实验项目考核

`assessmentItem` 是手工录入的实验项目名称，不能使用 `sys_assessment_item` 字典。

```json
{
  "planId": 6003,
  "assessments": [
    {
      "assessmentCategory": 3,
      "assessmentItem": "链表综合实验",
      "method": "2",
      "mechanism": "1",
      "standard": "1,2,3,4,5",
      "weight": 1.0
    }
  ]
}
```

### 3.3 实践训练课目：训练课目考核

`assessmentItem` 是手工录入的考核项目，不能使用 `sys_assessment_item` 字典。

```json
{
  "planId": 6002,
  "assessments": [
    {
      "assessmentCategory": 4,
      "assessmentItem": "单个军人队列动作考核",
      "method": "2",
      "mechanism": "1",
      "standard": "6,7",
      "weight": 1.0
    }
  ]
}
```

### 3.4 实践项目：成果与评价

成果类型提交 `sys_plan_outcome_type` 的字典 value；其余页面列均手工录入。项目计分规则只放顶层 `scoreRule`。

```json
{
  "planId": 6004,
  "scoreRule": "项目总成绩=个人成果×60%+团队成果×40%",
  "assessments": [
    {
      "assessmentCategory": 5,
      "outcomeType": 1,
      "assessmentItem": "个人答辩",
      "assessedContent": "个人完成质量",
      "weight": 0.6,
      "standard": "能够独立说明本人承担工作及实现过程"
    },
    {
      "assessmentCategory": 5,
      "outcomeType": 2,
      "assessmentItem": "团队演示",
      "assessedContent": "团队协作质量",
      "weight": 0.4,
      "standard": "完整展示项目成果并回答问题"
    }
  ]
}
```

## 4. 回显接口

```http
GET /csys/teachingPlan/assessment/list?planId={planId}
```

- 列表原样返回数据库中保存的字典 value，前端按本页对应字典转成 label 展示。
- 实践项目成果评价额外返回 `outcomeTypeName`，可直接展示成果类型。
- 列表有行时，每行都带同一个 `scoreRule`；列表为空时，从已有详情接口 `GET /csys/teachingPlan/detail?courseId={courseId}&teachingPlanId={planId}` 的 `data.scoreRule` 回显项目计分规则。

## 5. 当前校验与注意事项

1. 后端已校验所有类型的 `weight` 必须在 `0`～`1` 之间。
2. 实践项目成果评价已校验 `outcomeType` 必填且存在于 `sys_plan_outcome_type`，`assessmentItem` 必填；同时会清空不适用的 `method`、`mechanism`、`scoreSystem`。
3. 当前后端尚未按教学计划类型强制校验 `assessmentCategory`，也未校验课程/实验/训练课目的其他字典字段是否为合法 value；前端必须严格按第 2 节传参。
4. 现有 Word 生成、导入对非成果评价统一按 `sys_assessment_item` 转换 `assessmentItem`，会与“实验项目名称、实践训练课目考核项目手工录入”的新口径冲突；该问题需要在 Word 转换逻辑中按类别 `3`、`4` 跳过 `assessmentItem` 字典转换后再处理。

