课程有总库课程与调用课程两个概念 根据template_type字段区分 调用的课程有源课程的Id保存在sourceId中。当前删除操作是总库课程逻辑删除，调用课程物理删除  分别通过public Message removeStorageCourse(@RequestBody List<Long> ids)  与public Message removeInvokeCourse(@RequestBody List<Long> ids)方法完成。
目前遇到问题 调用课程会被莫名其妙删除。为解决这一问题想添加一个调用课程删除的日志表，记录每次的删除动作，包括删除的课程信息，操作人，操作时间等信息。按照sql中其他的表结构格式创建日志表
并在调用课程的删除方法中添加新增删除日志的操作。项目依赖包不全，无法测试以及添加jar包。有问题问我