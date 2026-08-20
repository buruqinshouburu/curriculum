# 课程教学计划 接口文档

> 供前端调用查看。基址（context-path）：`/flowable-demo`
> Controller：`com.doinner.csys.controller.TeachingPlanController`（`@RequestMapping("/teachingPlan")`）

---

## 接口一：课程教学计划管理列表（分页）

- **请求路径**：`GET /teachingPlan/list`
- **请求方式**：GET
- **Content-Type**：`application/x-www-form-urlencoded`（query string 传参）
- **分页**：由后端 `PageUtils.startPage()` 开启，前端按 PageHelper 约定传 `pageNum` / `pageSize`。
- **数据范围**：**只查询总库原课程**（`t_csys_course.source_id IS NULL`）。

### 1. 请求参数

| 参数名 | 类型 | 必填 | 说明 |
|---|---|---|---|
| courseName | String | 否 | 课程名称，模糊查询（`t_csys_course.name` LIKE） |
| courseCode | String | 否 | 课程编号，模糊查询（`t_csys_course.code` LIKE） |
| collegeId | Long | 否 | 开课单位 ID，精确（`t_csys_course.college_id`，与 `/course/list` 一致） |
| educationLevel | String | 否 | 适用对象。精确匹配任一被引用培养方案的 `education_level`，或总库课程自身 `education_level`（回退） |
| courseModule | String | 否 | 课程模块编码。精确匹配任一被引用课程（`c2.source_id=总库课程.id`）的 `course_Module`，或总库课程自身 `course_Module`（回退） |
| courseAttr | String | 否 | 修读要求。精确匹配任一被引用课程的 `course_attr`，或总库课程自身 `course_attr`（回退） |
| type | String | 否 | 课程类型，精确（`t_csys_course.type`）：`1`课程 / `2`实践训练课目 / `3`实验课程 / `4`实践项目 |
| version | String | 否 | 课程版本，精确（`t_csys_course.version`） |
| quoted | Integer | 否 | 是否已被培养方案调用：`1`=只查已被调用的原课程；`0` 或不传=查全部 |

> **`quoted` 语义**：传入 `1` 时，仅返回存在「调用课程实例」的原课程——即存在 `t_csys_course c2` 满足 `c2.source_id = 原课程.id`（原课程已被培养方案调用过）。传入 `0` 或不传时，不加此限制，返回全部原课程。

### 2. 返回结构

外层为分页包装 `DataTable<TeachingPlanListVo>`，业务数据在 `rows` 中：

```json
{
  "code": 200,
  "msg": "操作成功",
  "rows": [ { ...TeachingPlanListVo } ],
  "total": 123
}
```

> 外层 `code/msg/rows/total` 为框架 `DataTable` 通用结构；如与前端实际封装字段名不同，以框架为准，重点关注 `rows` 内的对象。

### 3. 返回字段 `TeachingPlanListVo`

#### 总库课程信息

| 字段 | 类型 | 说明 |
|---|---|---|
| courseId | Long | 总库课程 ID（`t_csys_course.id`） |
| courseName | String | 课程名称 |
| courseCode | String | 课程编号 |
| type | String | 课程类型：`1`课程 / `2`实践训练课目 / `3`实验课程 / `4`实践项目 |
| version | String | 课程版本（`t_csys_course.version`） |
| programLevel | String | 项目层级（`t_csys_course.program_Level`） |
| collegeId | Long | 开课单位 ID（`t_csys_course.college_id`，与 `/course/list` 一致） |
| collegeName | String | 开课单位名称（后端按 collegeId 内存补全，来自 sys_dept） |
| educationLevel | String | 适用对象。取自被引用培养方案 `t_csys_training_scheme.education_level` 去重拼接；无引用时回退 `t_csys_course.education_level`。多值顿号分隔 |
| courseModule | String | 课程模块编码。取自被引用课程(`c2`,`c2.source_id=总库课程.id`）`course_Module` 去重拼接；无引用时回退 `t_csys_course.course_Module`。多值顿号分隔 |
| courseModuleName | String | 课程模块名称（后端按顿号拆分编码逐个翻译远程字典后重新拼接） |
| courseModuleChildren | String | 课程模块子项编码（仍取自总库课程 `course_Module_Children`，单值） |
| courseModuleChildrenName | String | 课程模块子项名称（后端内存补全，来自远程字典） |
| courseAttr | String | 修读要求/修读性质。取自被引用课程 `c2.course_attr` 去重拼接；无引用时回退 `t_csys_course.course_attr`。多值顿号分隔 |
| majorId | Long | 适用专业 ID。有被引用培养方案时此字段为回退单值(`t_csys_course.major_Id`)，真实适用专业看 `majorName` |
| majorName | String | 适用专业名称。取自被引用培养方案 `major_id -> t_csys_std_major.name` 去重拼接；无引用时回退总库课程 `major_Id -> name`。多值顿号分隔 |
| subMajorId | Long | 专业方向 ID（`t_csys_course.sub_Major_Id`） |
| subMajorName | String | 专业方向名称（后端内存补全，来自 t_csys_std_major） |
| hours | Double | 总学时 |
| credit | Double | 学分 |

#### 教学计划信息（LEFT JOIN，未建教学计划时为 null）

| 字段 | 类型 | 说明 |
|---|---|---|
| teachingPlanId | Long | 教学计划 ID（`t_csys_teaching_plan.id`），无则为 null |
| planType | Integer | 教学计划类型，字典值与课程类型相同（`1`课程 `2`实践训练课目 `3`实验课程 `4`实践项目）。取自 `t_csys_teaching_plan.plan_type`，由前端保存时传入。同一课程可存在多个不同 `planType` 的教学计划，但同一 `planType` 只能一条 |
| planVersion | String | 教学计划版本 |
| currentFlag | Integer | 是否当前版本：1是 0否 |
| enabledTerm | String | 启用时间 |
| status | Integer | 状态：0草稿 1审核中 2通过 3退回 9停用 |
| fileId | String | 教学计划文件 ID |
| fileName | String | 教学计划文件名称 |
| sourceCredit | BigDecimal | 总库课程学分快照 |

### 4. 请求示例

```
GET /flowable-demo/teachingPlan/list?pageNum=1&pageSize=10&type=1&quoted=1&courseName=数学
```

### 5. 返回示例

```json
{
  "code": 200,
  "msg": "操作成功",
  "rows": [
    {
      "courseId": 1001,
      "courseName": "高等数学",
      "courseCode": "MATH001",
      "type": "1",
      "version": "v1.0",
      "programLevel": "本科",
      "collegeId": 10,
      "collegeName": "基础教学学院",
      "educationLevel": "本科",
      "courseModule": "MOD01",
      "courseModuleName": "公共基础",
      "courseModuleChildren": "MOD0101",
      "courseModuleChildrenName": "数学类",
      "courseAttr": "必修",
      "majorId": 200,
      "majorName": "数学与应用数学",
      "subMajorId": null,
      "subMajorName": null,
      "hours": 64.0,
      "credit": 4.0,
      "teachingPlanId": 55,
      "planType": 1,
      "planVersion": "v1.0",
      "currentFlag": 1,
      "enabledTerm": "2024-2025-1",
      "status": 2,
      "fileId": "file-xxx",
      "fileName": "高等数学教学计划.docx",
      "sourceCredit": 4.0
    }
  ],
  "total": 1
}
```

---

## 接口二：查询引用课程的专业类

- **请求路径**：`GET /teachingPlan/quoteMajor/{courseId}`
- **请求方式**：GET
- **用途**：给定源课程 ID，查询「引用了该课程的专业类」（按专业类去重），用于展示该课程被哪些专业类引用。

### 1. 路径参数

| 参数名 | 类型 | 必填 | 说明 |
|---|---|---|---|
| courseId | Long | 是 | 源课程 ID（总库原课程 `t_csys_course.id`） |

### 2. 返回结构

外层为 `DataSet<List<CourseQuoteMajorVo>>`：

```json
{
  "code": 200,
  "msg": "操作成功",
  "data": [ { ...CourseQuoteMajorVo } ]
}
```

> 外层为框架 `DataSet` 通用结构；如字段名不同，以框架为准，重点关注 `data` 内数组。

### 3. 返回字段 `CourseQuoteMajorVo`

| 字段 | 类型 | 说明 |
|---|---|---|
| categoryName | String | 学科门类（门类名，来自 `t_csys_training_scheme_category`） |
| majorName | String | 专业类（专业类名，来自 `t_csys_std_major`） |
| majorId | Long | 专业类 ID（`t_csys_training_scheme.major_id`） |

### 4. 取数逻辑

逻辑参照「课程被选用情况」接口（`GET /course/chooseStatus/{sourceCourseId}`），同源取数：

```
源课程(t_csys_course, id=courseId)
  → 调用课程实例 c2 (c2.source_id = courseId)
  → 排课表 t_csys_training_scheme_course_schedule (course_id = c2.id)
  → 培养方案 t_csys_training_scheme (id = scheme_id)
```

从培养方案维度取 `major_id`（专业类）/ `category_id`（门类），分别关联名称表，**按 `major_id` 去重**后返回。

- 门类名：`ts.category_id` → `t_csys_training_scheme_category.name`
- 专业类名：`ts.major_id` → `t_csys_std_major.name`

> 注：`/course/chooseStatus` 原始 SQL 中把专业类名误命名为 `category_name`，本接口已修正——门类与专业类分别取自对应字段，命名与实体语义一致。

### 5. 请求示例

```
GET /flowable-demo/teachingPlan/quoteMajor/1001
```

### 6. 返回示例

```json
{
  "code": 200,
  "msg": "操作成功",
  "data": [
    {
      "categoryName": "理学",
      "majorName": "数学与应用数学",
      "majorId": 200
    },
    {
      "categoryName": "工学",
      "majorName": "计算机科学与技术",
      "majorId": 305
    }
  ]
}
```

---

## 接口三：查看教学计划详情

- **请求路径**：`GET /teachingPlan/detail`
- **请求方式**：GET
- **入参**：`courseId`（总库课程id，必填）、`teachingPlanId`（教学计划id，可空）
- **取值**：
  - `teachingPlanId` 为空 → 课程分支，字段取自总库课程 `t_csys_course`
  - `teachingPlanId` 非空 → 计划分支，基础信息取自 `t_csys_teaching_plan`，学时学分取 `plan.source_*` 快照（回退 course），5 个展示字段同样走 c2/ts 聚合
- **5 个展示字段**（适用对象 / 适用专业 / 修读性质 / 课程模块 / 开课学期）：均取自被引用课程 `c2` + 被引用培养方案 `ts` 聚合（多值顿号分隔），无引用时回退总库课程自身值。`t_csys_teaching_plan` 表不再承载这 5 个字段（context 表相应列也不再被读取）。
- **type 字段**：课程类型（`t_csys_course.type`），字典值 1课程 2实践训练课目 3实验课程 4实践项目。与教学计划类型 `planType` 共用同一套字典值，但 `planType` 取自教学计划表（前端保存时传入），`type` 取自课程表。
- **majorName 字段**：适用专业名称（聚合，多值顿号分隔）。

---

## 接口四：保存教学计划

- **请求路径**：`POST /teachingPlan/save`
- **请求方式**：POST
- **Content-Type**：`application/json`
- **入参**：`TeachingPlanSaveVo`，含 `plan`（教学计划主表 `TeachingPlan`）与 `context`（调用课程上下文 `TeachingPlanContext`，可为空）。

### 1. 请求体

```json
{
  "plan": {
    "id": null,
    "sourceCourseId": 1001,
    "planType": 1,
    "version": "v1.0",
    "currentFlag": 1,
    "enabledTerm": "2026年春季学期",
    "status": 0,
    "scoreRule": "..."
  },
  "context": {
    "planId": null,
    "sourceCourseId": 1001,
    "quoteCourseId": 1002,
    "schemeId": 50,
    "educationLevel": "...",
    "majorId": 200,
    "courseModule": "...",
    "term": 3,
    "courseAttr": "..."
  }
}
```

### 2. 关键字段说明

| 字段 | 类型 | 说明 |
|---|---|---|
| plan.id | Long | 为空=新增，非空=修改 |
| plan.sourceCourseId | Long | 总库课程 id（必填） |
| plan.planType | Integer | **教学计划类型，前端传入**，字典值同课程类型（1课程 2实践训练课目 3实验课程 4实践项目）。后端保留前端值不覆盖 |
| plan.version / currentFlag / enabledTerm / status / scoreRule | - | 见 `TeachingPlan` 实体 |
| context | TeachingPlanContext | 调用课程上下文，目标/达成设计 tab 用；详情的 5 个展示字段不再读 context（走 c2/ts 聚合），context 仅用于目标/毕业要求绑定的上下文区分 |

### 3. 唯一性约束

DB 层唯一约束 `uk_tp_source_course_plan_type (source_course_id, plan_type, sysflag)`：

- 同一课程（`source_course_id`）下，同一 `planType` 的有效记录（`sysflag=0`）**只能有一条**。
- 不同 `planType` 可以并存 -- 即单一课程可同时存在「课程」「实验课程」「实践训练课目」「实践项目」等多种类型的教学计划。
- 违反约束时保存接口会抛 `DuplicateKeyException`（HTTP 500），前端应先判断同类型是否已存在再决定新增/修改。

### 4. 返回

`DataSet<Long>`，data 为教学计划 id。

```json
{ "code": 200, "msg": "操作成功", "data": 55 }
```

---

## 附：字段与数据库列对照

| VO 字段 | 数据库列 | 所属表 |
|---|---|---|
| courseId | id | t_csys_course |
| courseName | name | t_csys_course |
| courseCode | code | t_csys_course |
| type | type | t_csys_course |
| version | version | t_csys_course |
| programLevel | program_Level | t_csys_course |
| collegeId | college_id | t_csys_course |
| educationLevel | education_level | ts(被引用培养方案)聚合,回退 t_csys_course |
| courseModule | course_Module | c2(被引用课程)聚合,回退 t_csys_course |
| courseModuleChildren | course_Module_Children | t_csys_course |
| courseAttr | course_attr | c2(被引用课程)聚合,回退 t_csys_course |
| majorId | major_Id | t_csys_course(回退单值) |
| subMajorId | sub_Major_Id | t_csys_course |
| hours | hours | t_csys_course |
| credit | credit | t_csys_course |
| teachingPlanId | id | t_csys_teaching_plan |
| planType | plan_type | t_csys_teaching_plan（前端传入，字典值同课程类型） |
| planVersion | version | t_csys_teaching_plan |
| currentFlag | current_flag | t_csys_teaching_plan |
| enabledTerm | enabled_term | t_csys_teaching_plan |
| status | status | t_csys_teaching_plan |
| fileId | file_id | t_csys_teaching_plan |
| fileName | file_name | t_csys_teaching_plan |
| sourceCredit | source_credit | t_csys_teaching_plan |
| categoryName | name (via category_id) | t_csys_training_scheme_category |
| majorName | name (via major_id) | ts.major_id->t_csys_std_major 聚合,回退 course.major_Id |
