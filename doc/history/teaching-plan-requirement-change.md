# 课程教学计划模块 — 需求修改说明

| 项 | 内容 |
|----|------|
| 文档类型 | 需求修改 / 缺陷修复说明 |
| 关联模块 | `TeachingPlanController` / `TeachingPlanService` / `TeachingPlanModuleService` / `CourseTeachingPlanGenerator` |
| 基线路由 | `/teachingPlan/**` |
| 编写日期 | 2026-07-22 |
| 状态 | 待开发 |

---

## 1. 背景

对 `com.doinner.csys.controller.TeachingPlanController` 及其 Service、Word 生成链路做代码审查后，确认业务已从 **context / major 维度** 收敛到 **培养方案 `schemeId` 维度**（编辑页 tab 为 `scheme/list`），但导出与部分接口/注释仍停留在旧语义。

本需求汇总审查问题，明确改造范围与验收标准。

**约束（已确认）：**

- 教学计划 Word **生成接口保持 `GET`**，不改为 `POST`。
- 路径仍为：`GET /teachingPlan/createWord/{courseId}`（可增可选查询参数，见 §4）。

---

## 2. 审查问题清单（全部）

说明：优先级用于排期；**P0 本期必须处理，P1/P2 按排期纳入本需求或后续迭代**。

### 2.1 P0 — 本期必做

#### P0-1 导出 Word 仅取第一个培养方案（核心缺陷）

| 项 | 说明 |
|----|------|
| 现象 | `generateTeachingPlanWord` 中 `listSchemes(courseId)` 后 `schemes.get(0)` 取**第一条** `schemeId`，整份文档目标/达成设计/毕业要求只装一个方案数据 |
| 影响 | 源课被多个培养方案引用时，导出内容与编辑页多 tab 不一致，非当前/其他方案数据丢失 |
| 期望 | **多个 `schemeId` → 多个独立表格**（见 §3、§4） |
| 涉及 | `TeachingPlanServiceImpl`、`CourseTeachingPlanModel`、`CourseTeachingPlanGenerator`、Controller 可选参数 |

#### P0-2 删除教学计划不级联子表

| 项 | 说明 |
|----|------|
| 现象 | `DELETE /teachingPlan/{planId}` 仅逻辑删除主表 `t_csys_teaching_plan`（`sysflag=2`），不处理 teacher/section/objective/ref/content/targetDesign/practice/assessment/textbook/condition/processStep/ref 等子表 |
| 影响 | 孤儿数据；按 `planId` 查询子模块仍可能返回数据；再导出可能读到脏关联 |
| 期望 | 删除计划时，对所有挂在该 `planId` 下的子表做**逻辑删除**（与主表一致用 `sysflag`）；审核中/已通过仍不可删（现状保留） |
| 涉及 | `TeachingPlanServiceImpl.deleteTeachingPlan`、各 Mapper `deleteByPlanId`（DDL/Mapper 已有部分能力可复用） |

#### P0-3 删除教学目标不级联支撑关系

| 项 | 说明 |
|----|------|
| 现象 | `DELETE /objective/{id}` 只删目标；`saveWithRefs` 更新时会 `deleteByObjectiveId`，单独删除不会 |
| 影响 | `objective_ref` 孤儿行 |
| 期望 | 删除目标时同步逻辑删除该目标下全部 `TeachingPlanObjectiveRef` |
| 涉及 | `TeachingPlanModuleServiceImpl.deleteObjective` |

### 2.2 P1 — 强烈建议本期或紧随迭代

#### P1-1 注释 / 接口语义与实现漂移

| 位置 | 问题 |
|------|------|
| `POST /save` 注释 | 仍写「含调用课程上下文」，实现已不写 context 表 |
| `GET /detail` 注释 | 仍提「调用课程上下文」 |
| `courseGraduation` 区块注释 | 仍写「可选 context」，实际参数为 `schemeId` |
| Service 注释 vs 代码 | `saveObjectiveWithRefs` 写 `schemeId` 必填，代码未校验 |

**期望：** 注释与 Swagger 描述统一为 **scheme 维度**；废弃 context 表述。

#### P1-2 写接口缺少必填与归属校验

| 问题 | 说明 |
|------|------|
| 主流程部分有校验 | `save` 校验 plan / sourceCourseId / planType |
| 子模块 CRUD 基本透传 | `planId`/`schemeId`/`id` 为空或非法仍可能下沉 DB |
| 目标保存 | `schemeId` 未强制校验 |
| 越权风险 | 任意 `planId` 可写子资源（若无网关数据权限） |

**期望（最低集）：**

- `addObjective` / `saveObjectiveWithRefs`：`planId`、`schemeId` 必填，不存在则明确错误
- 其他写接口：`planId`（或父 id）非空；更新时 `id` 非空
- （可选增强）校验 `planId` 存在且 `sysflag` 有效

#### P1-3 `/major/list` 与 scheme 维度并存

| 项 | 说明 |
|----|------|
| 现象 | 目标已按 `planId + schemeId` 建模；`major/list` 仍按 plan+major 算 status |
| 风险 | 前端若仍依赖 major tab，与 scheme tab 双轨；若已废弃则冗余易误导 |
| 期望 | 产品确认：若前端已只用 scheme tab → 文档标记废弃或下线；若仍用 → 明确 status 算法是否改为「方案维度完成度」 |

#### P1-4 未指定 `planId` 时 Word 选计划不确定

| 项 | 说明 |
|----|------|
| 现象 | 未传 `planId` 时 `selectBySourceCourseId(courseId)`，多 `planType` 时结果不确定 |
| 期望 | 文档约定：多类型时**必须传 planId**；或选「当前/最新」规则写死并在接口注释说明 |

#### P1-5 考核评价 `scoreRule` 契约别扭

| 项 | 说明 |
|----|------|
| 现象 | `scoreRule` 属主表字段，却在 assessment 列表每行回填；新增/修改 assessment 时有值则回写主表；`null` 不改、空串清空 |
| 期望 | 接口注释写清规则；或拆独立接口改主表计分规则（可后续） |

### 2.3 P2 — 体验与结构优化

| 编号 | 问题 | 建议 |
|------|------|------|
| P2-1 | `DELETE /{planId}` 路径过宽 | 可改为 `/plan/{planId}`（破坏性，需前端同步，可选） |
| P2-2 | `objective/list` 的 schemeId 可选，`objective/tree` 必填 | 保持，但在 API 文档写清用途差异 |
| P2-3 | 实践项目删除会清明细，其他父子删除不统一 | 统一级联策略文档化 |
| P2-4 | 列表页字典/部门缓存无失效策略 | 可接受；字典变更极少 |
| P2-5 | Controller 无方法级鉴权注解 | 与项目其它 Controller 对齐即可（网关鉴权则文档说明） |

### 2.4 明确不改项

| 项 | 结论 |
|----|------|
| Word 生成 HTTP 方法 | **保持 GET**，不改为 POST |
| 计划级模块拆 scheme | 教学内容、教员、章节、实践项目、考核、教材、条件等 **仍为 plan 级单表**，不按 scheme 拆 |
| 基本信息表按专业多行 | 本期不改；仍用 detail 聚合字段 |
| 数据库表结构 | 本期不改表；复用现有 `scheme_id` 字段与查询 |

---

## 3. 产品规则（导出多 scheme）

| 规则 | 约定 |
|------|------|
| 培养方案列表来源 | 与编辑页一致：`listSchemes(sourceCourseId)` |
| 多个培养方案 | **每个 scheme 各出一套独立表格**，不得合并进同一张业务表 |
| 无培养方案引用 | 章节结构保留，渲染**一张空结构表**（与现「无数据占位」一致） |
| 计划级数据（全文一份） | 教员、章节/概述、教学内容、实践项目及明细、考核评价、教材、条件保障等 |
| scheme 级数据（每方案一份表） | 课程目标 + 支撑毕业要求；目标达成设计（知识/能力/素质）；按方案汇总的课程毕业要求 |
| 可选筛选 | 查询参数 `schemeIds`：不传 = 全部 scheme；传入 = 仅导出指定 scheme（顺序=参数顺序，未命中忽略） |
| 副标题 | 每张 scheme 表前增加说明行/三级标题，建议：`培养方案：{schemeName} {schemeVersion} / {majorName}` |

### 3.1 type=1 课程教学计划章节对照

| 章节 | 是否按 scheme 多表 |
|------|-------------------|
| 一、课程基本信息 | 否 |
| 二、课程教学团队 | 否 |
| 三、课程概述 | 否 |
| **四、课程目标与支撑毕业要求** | **是** |
| 五、课程教学内容与时间安排 | 否 |
| **六（一）知识目标达成设计** | **是** |
| **六（二）能力目标达成设计** | **是** |
| **六（三）素质目标达成设计** | **是** |
| 七、实验/实践环节教学设计 | 否 |
| 八、考核评价 | 否 |
| 九、教学条件 | 否 |

### 3.2 type=2/3/4

凡使用「课程绑定毕业要求」或目标相关字段的表格，按同一 `schemeBlocks` 循环：

- type2：训练目的与支撑毕业要求等
- type3：任务背景表中「支撑的毕业要求」等
- type4：任务背景中与毕业要求/知识体系相关展示

无 scheme 时同样保留空表结构。

### 3.3 文档示意（type=1 第四节）

```text
四、课程目标与支撑毕业要求
  培养方案：2024版计算机类培养方案 / 计算机类
  [目标类型 | 目标内容 | 支撑毕业要求]
  培养方案：2025版软件工程培养方案 / 软件工程类
  [目标类型 | 目标内容 | 支撑毕业要求]
```

第六节三类设计表同理：每个（一）（二）（三）下按 scheme 连续出多张表。

---

## 4. 接口变更说明

### 4.1 生成 Word（保持 GET）

**方法/路径：不变**

```http
GET /teachingPlan/createWord/{courseId}
```

**查询参数：**

| 参数 | 必填 | 说明 |
|------|------|------|
| `courseId` | 是（路径） | 总库课程 ID |
| `planId` | 否 | 指定教学计划；多 `planType` 时建议必传 |
| `schemeIds` | 否 | 培养方案 ID 列表。不传=全部引用方案；传则过滤。多值：`schemeIds=1&schemeIds=2` 或框架支持的 list 绑定 |

**响应：** 不变，仍为 `DataSet<FileInfo>`（生成并回写文件信息）。

**行为变更：**

| 变更前 | 变更后 |
|--------|--------|
| 仅使用 `listSchemes` 的**第一条** scheme 填目标/达成设计等 | 使用全部（或过滤后）scheme，**每方案独立表格** |
| 毕业要求可能用全局 `listCourseGraduation` | 每 block 使用 `listCourseGraduationByScheme(courseId, schemeId)` |

**不变更：** HTTP 方法仍为 **GET**。

### 4.2 删除教学计划

```http
DELETE /teachingPlan/{planId}
```

- 行为增强：主表 + 子表逻辑删除
- 门禁不变：`status ∈ {1审核中, 2通过}` 不可删

### 4.3 删除教学目标

```http
DELETE /teachingPlan/objective/{id}
```

- 行为增强：级联逻辑删除 `objective_ref`

### 4.4 其它接口

本期可不改路径与方法；需修正注释/Swagger 与校验的见 §2.2。  
`POST /save`、`GET /detail`、`GET /courseGraduation/{courseId}` 等**契约保持**，仅文档与校验对齐 scheme 语义。

---

## 5. 技术方案摘要（实现指引）

### 5.1 模型

新增生成专用块（建议路径）：

`csys-service/.../entity/csys/model/CourseTeachingPlanSchemeBlock.java`

字段建议：

- 元数据：`schemeId`、`schemeName`、`schemeVersion`、`educationLevel`、`majorId`、`majorName`、`displayTitle`
- 数据：`objectives`、`objectiveRefMap`、`targetDesigns`、`courseGraduations`

`CourseTeachingPlanModel` 增加：

```text
List<CourseTeachingPlanSchemeBlock> schemeBlocks
```

生成器只读 `schemeBlocks`；废弃单份 `objectives` / `targetDesigns` / `courseGraduations` 作为导出主路径（可删或 Deprecated）。

### 5.2 Service

1. 去掉 `schemes.get(0)`。
2. `filterSchemes(listSchemes, schemeIds)`。
3. `buildModel` 对每个 scheme 组装一个 `SchemeBlock`（目标、ref、三类达成设计、按 scheme 毕业要求）。
4. 计划级模块加载逻辑保持不变。

### 5.3 Generator

1. 增加 `forEachSchemeTable`：有 blocks 则副标题 + 循环画表；无 blocks 则一张空表。
2. `objectiveTable` / `targetDesignTable` / `abilityQualityDesignTable` 改为吃 block 内 list。
3. type2/3/4 中依赖毕业要求的表同样循环 blocks。
4. 表格创建流程保持：`createTable` → `initTableGrid` → 宽度设置（与现有 `WordUtil` 一致）。

### 5.4 删除级联

- `deleteTeachingPlan`：调用各子表 `deleteByPlanId`（无则补 Mapper）。
- `deleteObjective`：调用 `deleteByObjectiveId`。

### 5.5 涉及文件清单

| 文件 | 动作 |
|------|------|
| `CourseTeachingPlanSchemeBlock.java` | 新增 |
| `CourseTeachingPlanModel.java` | 修改 |
| `TeachingPlanService.java` | 修改签名（增加 schemeIds） |
| `TeachingPlanServiceImpl.java` | 导出取数 + 删除级联 |
| `TeachingPlanModuleServiceImpl.java` | 删目标级联 ref；可选校验 |
| `CourseTeachingPlanGenerator.java` | 多表渲染 |
| `TeachingPlanController.java` | createWord 增加 schemeIds；注释修正 |
| 各 `*Mapper.xml` | 补齐 `deleteByPlanId`（如缺） |

---

## 6. 实现分期建议

| 阶段 | 内容 | 对应问题 |
|------|------|----------|
| **阶段 A** | 多 scheme 取数 + type=1 第四/六节多表 + Controller `schemeIds`（**GET 不变**） | P0-1 |
| **阶段 B** | type=2/3/4 毕业要求/相关表多 scheme | P0-1 收尾 |
| **阶段 C** | 删计划级联子表；删目标级联 ref | P0-2、P0-3 |
| **阶段 D** | 注释/Swagger 清理；objective schemeId 校验；planId 约定 | P1-1、P1-2、P1-4 |
| **阶段 E（可选）** | major 接口处置、scoreRule 拆分、路径收窄 | P1-3、P1-5、P2-* |

---

## 7. 验收标准

### 7.1 导出（P0-1）

| # | 场景 | 期望 |
|---|------|------|
| 1 | 源课 0 个培养方案引用 | 第四节、第六节结构在，空表占位 |
| 2 | 1 个 scheme，有目标 | 第四节 1 个副标题 + 1 表，内容正确 |
| 3 | 2 个 scheme，A 有目标 B 无 | 第四节 2 副标题 + 2 表；B 为空结构 |
| 4 | 2 个 scheme，达成设计不同 | 六（一）（二）（三）各 2 表，数据不串 scheme |
| 5 | `schemeIds` 只传其中一个 | 仅该 scheme 的表出现 |
| 6 | 指定 `planId` | 计划级数据来自该计划；scheme 表按规则展开 |
| 7 | Word / LibreOffice 打开 | 列宽与合并正常，表格不错乱 |
| 8 | 接口方法 | 仍为 **GET** `/teachingPlan/createWord/{courseId}` |

### 7.2 删除（P0-2、P0-3）

| # | 场景 | 期望 |
|---|------|------|
| 1 | 草稿计划删除 | 主表及子表 `sysflag=2`，列表不可见 |
| 2 | 审核中/已通过 | 删除失败，明确错误提示 |
| 3 | 删除目标后查 ref 列表 | 无有效 ref |
| 4 | 仅删目标不删计划 | 其它目标与子模块不受影响 |

### 7.3 文档与校验（P1，若纳入本期）

| # | 场景 | 期望 |
|---|------|------|
| 1 | Swagger/JavaDoc | 无「context 表写入」等过时描述 |
| 2 | 保存目标缺 schemeId | 业务异常/参数错误，不落脏数据 |

---

## 8. 风险与说明

1. **scheme 数量多时文档变长**：属产品预期；可用 `schemeIds` 导出子集。
2. **达成设计类型字符串**（如 `"知识目标"`）须与库中 `design_type_code` / 前端存值一致，改造时勿擅自改匹配关键字。
3. **GET 生成有副作用**（上传文件、回写 fileId）：本期按产品要求保留 GET；调用方需避免无意义重复触发。
4. **前端**：默认不传 `schemeIds` 即导出全部 tab；若只需当前 tab，传单个 id 即可。

---

## 9. 附录：现有主接口一览（审查基线，便于对照）

| 能力 | 方法 | 路径 | 备注 |
|------|------|------|------|
| 分页列表 | GET | `/teachingPlan/list` | |
| 引用专业类 | GET | `/teachingPlan/quoteMajor/{courseId}` | |
| 详情 | GET | `/teachingPlan/detail` | |
| 保存主表 | POST | `/teachingPlan/save` | 按 sourceCourseId+planType 查重 |
| **生成 Word** | **GET** | **`/teachingPlan/createWord/{courseId}`** | **保持 GET**；本期增强 scheme |
| 删除计划 | DELETE | `/teachingPlan/{planId}` | 本期级联 |
| 培养方案 tab | GET | `/teachingPlan/scheme/list` | 导出 scheme 同源 |
| 专业列表 | GET | `/teachingPlan/major/list` | 待确认是否废弃 |
| 目标/树/支撑/毕业要求 | CRUD | `/objective/**`、`/objectiveRef/**`、`/courseGraduation/**` | 删目标级联 |
| 其它模块 | CRUD | content / targetDesign / practice / assessment / textbook / condition / processStep / ref / teacher / section | 删计划时级联 |

---

## 10. 修订记录

| 日期 | 版本 | 说明 |
|------|------|------|
| 2026-07-22 | v1.0 | 初稿：汇总 Controller 审查问题；导出多 scheme 多表方案；明确生成接口保持 GET |
