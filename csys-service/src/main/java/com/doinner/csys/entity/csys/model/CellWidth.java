package com.doinner.csys.entity.csys.model;


public class CellWidth {
    private  double singleCellWidth;
    private  int totalCols;

    /**
     * 课程模块列宽度倍数（默认1.0，公共基础课程教学安排表设为0.75）
     */
    private  double moduleWidthMultiplier = 1.0;
    /**
     * 课程名称列宽度倍数（默认1.0，公共基础课程教学安排表设为2.0）
     */
    private  double nameWidthMultiplier = 1.0;

    public CellWidth(int totalCols) {
        this.totalCols = totalCols;
        if(totalCols>0){
            this.singleCellWidth = (double)100/(double)this.totalCols;
        }else{
            this.singleCellWidth = 0;
        }
    }

    /**
     * 传入列的数量
     * @param colCount
     * @return
     */
    public String getCellWidth(int colCount) {
        return singleCellWidth*colCount+"%";
    }

    /**
     * 传入列的数量（支持小数，用于按倍数缩放后的宽度）
     * @param colCount
     * @return
     */
    public String getCellWidth(double colCount) {
        return singleCellWidth*colCount+"%";
    }

    public void setModuleWidthMultiplier(double moduleWidthMultiplier) {
        this.moduleWidthMultiplier = moduleWidthMultiplier;
    }

    public void setNameWidthMultiplier(double nameWidthMultiplier) {
        this.nameWidthMultiplier = nameWidthMultiplier;
    }

    /**
     * 课程模块列宽度：基准 baseUnits 列 × moduleWidthMultiplier。
     * 公共基础课程教学安排表对模块列整体缩放（如表头基准3列、数据列基准1列）。
     * @param baseUnits 基准列数
     * @return 宽度百分比字符串
     */
    public String getModuleWidth(int baseUnits) {
        return singleCellWidth*baseUnits*moduleWidthMultiplier+"%";
    }

    /**
     * 课程名称列宽度：基准1列 × nameWidthMultiplier。
     * setCourseDataRow 为各课程表共用，故通过倍数区分（公共基础表设为2.0，其余默认1.0）。
     * @return 宽度百分比字符串
     */
    public String getNameWidth() {
        return singleCellWidth*nameWidthMultiplier+"%";
    }
}
