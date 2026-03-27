根据com.example.cscy.entity.scheme.model.TrainingPlanModel生成对应培养方案 培养方案有培养目标，毕业要求，修业时间与学时学分，教学训练体系与安排四个一级标题,model类中，培养目标，修业时间与学时学分是固定的，修业时间与学时学分中的表格参照[TableCreateGenerator.java](../src/main/java/com/example/cscy/generator/TableCreateGenerator.java)中表格生成的方式方式生成毕业要求下是多级结构，level=3时是具体内容 level=1，2时是毕业要求下的两层子标题；教学训练体系下是课程信息，以表格的方式表示，表格内容严格按照[TableCreateGenerator.java](../src/main/java/com/example/cscy/generator/TableCreateGenerator.java)中生成的表头来确定，格式为 模式名，课程名，修读方式（B代表必修课程、X代表限选课程、R代表任选课程；S代表考试，C代表考查），小计，讲授，实践，"","","✔","","","✔","",""（根据semesterSchedule与springAutumn判断是否要打勾），根据要求生成对应的generator类，并给出test测试方法测试类，参考[TableCreateGenerator.java](../src/main/java/com/example/cscy/generator/TableCreateGenerator.java)类时可将要使用的通用方法重写到[WordUtils.java](../src/main/java/com/example/cscy/utils/WordUtils.java)中



Map<String, List<TrainingSchemeCourseModel>> couseMap = courses.stream().collect(Collectors.groupingBy(course -> course.getMajorName()));  如何在分组的同时根据TrainingSchemeCourseModel.getModeChildrenNameSort()字段做排序 使map中的顺序固定




❯ 在TrainingPlanGenerator类中 针对需求做更新 生成generateCourseTable 时 原来的课程模块要改为占两列 在录入数据时 要根据modeChildrenNameSort与modeFourLevelSort对数据进行排序 modeFourLevelSort可能是不存在的 录入数据时要判断        eFourLevelName是courseModeChildrenName的子模式 并且要同时进行行合并               courseModeChildrenName合并courseModeChildrenName下所有课程所占用的行       modeFou代码就行修改 并修改测试类中数据                                 seModeChildrenName各占课程模式的一列 modeFourLevelName是courseModeChildrenName的子模式 并且要同时进行行合并      模式 并且要同时进行行合并               courseModeChildrenName合并courseModeChildrenName下所有课程所占用的行  