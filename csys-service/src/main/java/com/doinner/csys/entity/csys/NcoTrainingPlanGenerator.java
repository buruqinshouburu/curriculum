package com.doinner.csys.entity.csys;

/**
 * 军士(职业技术教育学员)培养方案文档生成器。
 *
 * 适用对象：培养层次 educationLevel 为 3(军士职业技术教育学员)时，
 * 由 {@code TrainingServiceImpl.createTrainingPlanGenerator} 选择本子类实例化。
 *
 * 说明：本类目前继承父类 {@link TrainingPlanGenerator}(四年制原样布局)，
 * 建表与数据填充逻辑沿用父类。若军士三年制需要不同的学期列数或表头结构，
 * 可参照 {@link FiveYearTrainingPlanGenerator} 的方式重写 {@link #termColumnCount()}
 * 及相应的表头/建表方法。
 */
public class NcoTrainingPlanGenerator extends TrainingPlanGenerator {
}
