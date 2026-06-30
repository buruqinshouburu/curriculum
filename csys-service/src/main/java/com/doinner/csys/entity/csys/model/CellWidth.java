package com.doinner.csys.entity.csys.model;


public class CellWidth {
    private  double singleCellWidth;
    private  int totalCols;

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
}
