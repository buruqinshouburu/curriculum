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
| teachCollegeId | Long | 否 | 开课单位 ID，精确（`t_csys_course.teach_college_id`） |
| educationLevel | String | 否 | 适用对象，精确（`t_csys_course.education_level`） |
| courseModule | String | 否 | 课程模块编码，精确（`t_csys_course.course_Module`） |
| courseAttr | String | 否 | 修读要求，精确（`t_csys_course.course_attr`） |
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
| teachCollegeId | Long | 开课单位 ID |
| teachCollegeName | String | 开课单位名称（后端内存补全，来自 sys_dept） |
| educationLevel | String | 适用对象（`t_csys_course.education_level`） |
| courseModule | String | 课程模块编码 |
| courseModuleName | String | 课程模块名称（后端内存补全，来自远程字典） |
| courseModuleChildren | String | 课程模块子项编码 |
| courseModuleChildrenName | String | 课程模块子项名称（后端内存补全，来自远程字典） |
| courseAttr | String | 修读要求 |
| majorId | Long | 适用专业 ID（`t_csys_course.major_Id`） |
| majorName | String | 适用专业名称（后端内存补全，来自 t_csys_std_major） |
| subMajorId | Long | 专业方向 ID（`t_csys_course.sub_Major_Id`） |
| subMajorName | String | 专业方向名称（后端内存补全，来自 t_csys_std_major） |
| hours | Double | 总学时 |
| credit | Double | 学分 |

#### 教学计划信息（LEFT JOIN，未建教学计划时为 null）

| 字段 | 类型 | 说明 |
|---|---|---|
| teachingPlanId | Long | 教学计划 ID（`t_csys_teaching_plan.id`），无则为 null |
| planType | Integer | 计划类型：1普通课程 2实验课程 3实践训练课目 4实践项目 |
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
      "teachCollegeId": 10,
      "teachCollegeName": "基础教学学院",
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

## 附：字段与数据库列对照

| VO 字段 | 数据库列 | 所属表 |
|---|---|---|
| courseId | id | t_csys_course |
| courseName | name | t_csys_course |
| courseCode | code | t_csys_course |
| type | type | t_csys_course |
| version | version | t_csys_course |
| programLevel | program_Level | t_csys_course |
| teachCollegeId | teach_college_id | t_csys_course |
| educationLevel | education_level | t_csys_course |
| courseModule | course_Module | t_csys_course |
| courseModuleChildren | course_Module_Children | t_csys_course |
| courseAttr | course_attr | t_csys_course |
| majorId | major_Id | t_csys_course |
| subMajorId | sub_Major_Id | t_csys_course |
| hours | hours | t_csys_course |
| credit | credit | t_csys_course |
| teachingPlanId | id | t_csys_teaching_plan |
| planType | plan_type | t_csys_teaching_plan |
| planVersion | version | t_csys_teaching_plan |
| currentFlag | current_flag | t_csys_teaching_plan |
| enabledTerm | enabled_term | t_csys_teaching_plan |
| status | status | t_csys_teaching_plan |
| fileId | file_id | t_csys_teaching_plan |
| fileName | file_name | t_csys_teaching_plan |
| sourceCredit | source_credit | t_csys_teaching_plan |
| categoryName | name (via category_id) | t_csys_training_scheme_category |
| majorName | name (via major_id) | t_csys_std_major |
