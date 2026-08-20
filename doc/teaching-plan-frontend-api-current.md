# 教学计划前端接口文档（当前版）

更新时间：2026-08-20

本文只说明当前前端需要同步的教学计划接口。统一服务前缀为 `/csys/teachingPlan`。

## 1. 训练目的列表

### 请求

```http
GET /csys/teachingPlan/trainingPurpose/list?planId={planId}&schemeId={schemeId}
```

参数：

| 参数 | 必填 | 类型 | 说明 |
| --- | --- | --- | --- |
| `planId` | 是 | Long | 实践训练课目教学计划 ID |
| `schemeId` | 否 | Long | 培养方案 ID；通识通用课目可不传，非通识课目传值时按方案过滤，不传时返回该计划全部方案的数据 |

### 返回示例

```json
{
  "code": 200,
  "data": [
    {
      "id": 62111,
      "planId": 6002,
      "schemeId": null,
      "purpose": "掌握单个军人队列动作、班队列组织等基本军事素养",
      "sort": 1,
      "graduationRequirements": "掌握计算机科学与技术的基础理论与专业知识、具有良好的职业道德和团队协作意识"
    }
  ]
}
```

新增返回字段：

| 字段 | 类型 | 说明 |
| --- | --- | --- |
| `graduationRequirements` | String | 当前训练目的已绑定的支撑毕业要求名称，按绑定顺序使用 `、` 拼接；没有绑定时返回空字符串 `""` |

前端展示时直接使用 `graduationRequirements`，不需要再逐条调用绑定列表接口来拼接名称。编辑某条训练目的的绑定时，仍使用现有的 `trainingPurposeRef/list` 和 `trainingPurposeRef/save` 接口。

## 2. 实践项目支撑候选树

实践项目第二部分有两个选择区域，调用同一个接口，通过 `type` 区分。前端不再传 `projectPlanId`、`schemeId`、课程 ID 数组或训练课目 ID 数组。

后端根据实践项目课程的 `before_course_id` 和 `after_course_id` 自动反查全部支撑课程、支撑训练课目，并返回全部培养方案的数据。

### 请求

```http
GET /csys/teachingPlan/support/candidateTree?courseId={courseId}&type={type}
```

参数：

| 参数 | 必填 | 类型 | 说明 |
| --- | --- | --- | --- |
| `courseId` | 是 | Long | 实践项目的总库源课程 ID，不是教学计划 ID |
| `type` | 是 | Integer | `1`：课程目标/训练目的；`2`：知识体系/训练内容 |

页面初始化时建议并行请求两次：

```javascript
const [goalTree, contentTree] = await Promise.all([
  request('/csys/teachingPlan/support/candidateTree', {
    params: { courseId, type: 1 }
  }),
  request('/csys/teachingPlan/support/candidateTree', {
    params: { courseId, type: 2 }
  })
])
```

### 固定树结构

```text
支撑课程或支撑训练课目
└── 培养方案名称；无培养方案时显示“通识通用”
    └── type=1：课程目标或训练目的
        type=2：知识体系或训练内容
```

### `type=1` 返回示例

```json
{
  "code": 200,
  "data": [
    {
      "key": "course:7003",
      "id": 7003,
      "name": "程序设计基础",
      "nodeType": "course",
      "refType": 1,
      "courseId": 7003,
      "schemeId": null,
      "selectable": false,
      "children": [
        {
          "key": "course:7003:scheme:7601",
          "id": 7601,
          "name": "计算机科学与技术2026级培养方案",
          "nodeType": "scheme",
          "refType": 1,
          "courseId": 7003,
          "schemeId": 7601,
          "schemeVersion": "2026",
          "selectable": false,
          "children": [
            {
              "key": "objective:60011",
              "id": 60011,
              "name": "掌握结构化程序设计的基本方法与三种基本结构",
              "nodeType": "objective",
              "refType": 1,
              "courseId": 7003,
              "schemeId": 7601,
              "typeName": "知识目标",
              "selectable": true,
              "children": []
            }
          ]
        }
      ]
    },
    {
      "key": "trainingSubject:8002",
      "id": 8002,
      "name": "军事基础训练",
      "nodeType": "trainingSubject",
      "refType": 2,
      "courseId": 8002,
      "selectable": false,
      "children": [
        {
          "key": "trainingSubject:8002:scheme:general",
          "id": null,
          "name": "通识通用",
          "nodeType": "scheme",
          "refType": 2,
          "courseId": 8002,
          "schemeId": null,
          "selectable": false,
          "children": [
            {
              "key": "purpose:62111",
              "id": 62111,
              "name": "掌握单个军人队列动作、班队列组织等基本军事素养",
              "nodeType": "purpose",
              "refType": 2,
              "courseId": 8002,
              "schemeId": null,
              "selectable": true,
              "children": []
            }
          ]
        }
      ]
    }
  ]
}
```

### `type=2` 叶子节点

`type=2` 的前两层结构与上面完全相同，只有第三层类型不同：

```json
[
  {
    "key": "knowledgeSystem:65011:scheme:7601",
    "id": 65011,
    "name": "第一章 程序设计基础",
    "nodeType": "knowledgeSystem",
    "refType": 1,
    "courseId": 7003,
    "schemeId": 7601,
    "selectable": true,
    "children": []
  },
  {
    "key": "trainingContent:62211:scheme:general",
    "id": 62211,
    "name": "战斗体技能提升模块",
    "nodeType": "trainingContent",
    "refType": 2,
    "courseId": 8002,
    "schemeId": null,
    "selectable": true,
    "children": []
  }
]
```

节点字段：

| 字段 | 类型 | 说明 |
| --- | --- | --- |
| `key` | String | 树节点唯一键，前端树组件应使用它作为 `node-key`/`key` |
| `id` | Long | 根节点为课程 ID，第二层为方案 ID，第三层为实际绑定 ID；通识通用方案节点为 null |
| `name` | String | 节点展示文本 |
| `nodeType` | String | `course`、`trainingSubject`、`scheme`、`objective`、`purpose`、`knowledgeSystem`、`trainingContent` |
| `refType` | Integer | `1` 支撑课程，`2` 支撑训练课目 |
| `courseId` | Long | 当前分支的支撑课程或训练课目 ID |
| `schemeId` | Long | 数据所属培养方案 ID；通识通用为 null |
| `schemeVersion` | String | 培养方案版本，仅第二层方案节点有值 |
| `typeName` | String | 课程目标类型名称，仅 `objective` 节点有值 |
| `selectable` | Boolean | 只有第三层叶子节点为 true |
| `children` | Array | 子节点；叶子节点为空数组 |

### 前端选择与保存

1. 树组件只允许选择 `selectable=true` 的第三层节点。
2. `type=1` 请求中：
   - `nodeType=objective` 的叶子 `id` 放入 `objectiveIds`；
   - `nodeType=purpose` 的叶子 `id` 放入 `purposeIds`。
3. `type=2` 请求中，`knowledgeSystem` 和 `trainingContent` 的叶子 `id` 都放入 `contentIds`。
4. 同一内容可能显示在多个培养方案节点下，提交 `contentIds` 前按 `id` 去重。
5. 最终仍使用实践项目整页保存接口：

```http
POST /csys/teachingPlan/practiceProject/background/save
Content-Type: application/json
```

```json
{
  "planId": 6004,
  "complexProblem": "<p>拟解决的复杂问题</p>",
  "mainTask": "<p>主要任务</p>",
  "objectiveIds": [60011],
  "purposeIds": [62111],
  "contentIds": [65011, 62211]
}
```

注意：候选树查询传的是总库实践项目 `courseId`；整页保存传的是实践项目教学计划 `planId`，二者不能混用。

## 3. 考核与评价整页保存

请使用独立的当前说明：[教学计划考核与评价整页保存接口](teaching-plan-assessment-save-api.md)。

该文档按课程、实验教学、实践训练课目、实践项目分别说明字段、字典编码和请求示例；新页面统一使用：

```http
POST /csys/teachingPlan/assessment/save
```

## 4. 兼容性说明

- 旧的 `candidateTree?courseId=...&projectPlanId=...` 调用方式废止。
- 最终版本不传 `schemeId`，也不做“相同培养方案优先”排序。
- 无培养方案的数据统一放在名称为“通识通用”、`schemeId=null` 的第二层节点下。
- `type` 非 1、2，或 `courseId` 不是实践项目课程时，接口返回参数错误。
- 本次无数据库表结构变更，无需执行 DDL。
