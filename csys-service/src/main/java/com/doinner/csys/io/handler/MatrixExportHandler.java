package com.doinner.csys.io.handler;

import com.doinner.csys.domain.vo.ExcelRelationshipVo;
import com.doinner.csys.domain.vo.MatrixVo;
import com.doinner.csys.domain.vo.TreeTableVo;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class MatrixExportHandler {

    //下面的表格名称
    private String sheetName;

    //最上面的标题
    private String totalTitle;

    //左上的标题
    private String partialTitle;

    //左边竖着的表头
    private List<TreeTableVo> vertical;

    //左边竖着的表头的标题
    private List<String> verticalTitle;

    //左边竖着的表头宽度
    private Integer verticalSize = 0;

    //右边横着的表头
    private List<TreeTableVo> horizontal;

    //右边横着的表头的标题
    private List<String> horizontalTitle;

    //右边横着的表头高度
    private Integer horizontalSize = 0;

    //打勾
    private List<ExcelRelationshipVo> relationshipList;

    private XSSFWorkbook workBook;

    private Sheet sheet;

    private XSSFCellStyle cellStyle;

    private List<CellRangeAddress> cellRangeAddresseList = new ArrayList<>();

    private Integer currentRow = 0;

    private Integer currentColumn = 0;

    private Integer maxColumnNumber = 0;

    //记录id与行号、列号映射
    private Map<String, Integer> horizontalIndexMap = new HashMap<>();
    private Map<String, Integer> verticalIndexMap = new HashMap<>();

    public MatrixExportHandler() {
    }

    public MatrixExportHandler(String sheetName, String totalTitle, String partialTitle, List<TreeTableVo> vertical, List<String> verticalTitle, List<TreeTableVo> horizontal, List<String> horizontalTitle, List<ExcelRelationshipVo> relationshipList) {
        this.sheetName = sheetName;
        this.totalTitle = totalTitle;
        this.partialTitle = partialTitle;
        this.vertical = vertical;
        this.verticalTitle = verticalTitle;
        if(ObjectUtils.isNotEmpty(verticalTitle)) {
            this.verticalSize = verticalTitle.size();
        }
        this.horizontal = horizontal;
        this.horizontalTitle = horizontalTitle;
        if(ObjectUtils.isNotEmpty(horizontalTitle)) {
            this.horizontalSize = horizontalTitle.size();
        }
        this.relationshipList = relationshipList;
    }

    public MatrixExportHandler(MatrixVo matrixVo) {
        this.totalTitle = matrixVo.getTotalTitle();
        this.partialTitle = matrixVo.getPartialTitle();
        this.vertical = matrixVo.getVertical();
        if(ObjectUtils.isNotEmpty(verticalTitle)) {
            this.verticalSize = verticalTitle.size();
        }
        this.horizontal = matrixVo.getHorizontal();
        if(ObjectUtils.isNotEmpty(horizontalTitle)) {
            this.horizontalSize = horizontalTitle.size();
        }
        this.relationshipList = matrixVo.getRelationshipVoList();
    }

    public XSSFWorkbook create(){
        init();
        writeTotalTitle();
        writePartialTitle();
        writeVertical();
        writeHorizontal();
        writeRelationship();
        mergeCells();
        return this.workBook;
    }

    private void mergeCells(){
        CellRangeAddress cellAddresses = new CellRangeAddress(0, 0, 0, maxColumnNumber);
//        if(ObjectUtils.isEmpty(this.cellRangeAddresseList)){
//            return;
//        }
//        this.sheet.addMergedRegion(cellAddresses);
        this.cellRangeAddresseList.add(cellAddresses);
        this.cellRangeAddresseList.forEach(_cellAddresses -> {
            this.sheet.addMergedRegion(_cellAddresses);
        });
    }

    private void init(){
        workBook = new XSSFWorkbook();
        sheet = workBook.createSheet(this.sheetName);
        cellStyle = workBook.createCellStyle();
        cellStyle.setWrapText(true);
        cellStyle.setVerticalAlignment(VerticalAlignment.CENTER);
    }

    private void writeTotalTitle(){
        if(StringUtils.isBlank(this.totalTitle)){
            return;
        }
        Row row = sheet.createRow(0);
        Cell cell = getCell(row, 0);
        cell.setCellValue(this.totalTitle);
    }

    private void writePartialTitle(){
        if(StringUtils.isBlank(this.partialTitle)){
            return;
        }
        Row row = sheet.createRow(1);
        Cell cell = getCell(row, 0);
        cell.setCellValue(this.partialTitle);
        int startRow = ObjectUtils.isEmpty(totalTitle)?0: 1;
        int endRow = ObjectUtils.isEmpty(verticalTitle)?horizontalSize: horizontalSize - 1;
        int endColumn = ObjectUtils.isEmpty(horizontalTitle)?verticalSize - 1: verticalSize - 2;
        CellRangeAddress cellAddresses = new CellRangeAddress(startRow, endRow, 0, endColumn);
        if(startRow!=endRow||0!=endColumn){
            cellRangeAddresseList.add(cellAddresses);
        }
//        this.sheet.addMergedRegion(cellAddresses);
    }

    private void writeVertical(){
        if(ObjectUtils.isEmpty(this.vertical)){
            return;
        }
        int startRow = horizontalSize + (StringUtils.isNotBlank(totalTitle)?1:0);
        this.currentRow = startRow;
        this.currentColumn = 0;
        //左侧如果有表头
        if(ObjectUtils.isNotEmpty(verticalTitle)){
            Row row = getRow(startRow - 1);
            for (int i = 0; i < verticalTitle.size(); i++) {
                Cell cell = getCell(row, i);
                cell.setCellValue(verticalTitle.get(i));
            }
        }
        int rowNumber = 0;
        boolean hasNext = true;
        while(hasNext){
            currentColumn = 0;
            hasNext = writeVertical(this.vertical, rowNumber++, 1);
            currentRow++;
        }

    }

    private boolean writeVertical(List<TreeTableVo> treeTableVoList, int rowNumber, int level){
        int index = 0;
        TreeTableVo _treeTableVo = treeTableVoList.get(index++);
        while(rowNumber >= _treeTableVo.getSize()){
            if(index >= treeTableVoList.size()){
                return false;
            }
            rowNumber -= _treeTableVo.getSize();
            _treeTableVo = treeTableVoList.get(index++);
        }
        TreeTableVo currentVo = _treeTableVo;
        if(rowNumber == 0) {
            Cell cell = getCell(currentRow, currentColumn);
            if(StringUtils.isNotBlank(currentVo.getName())){
                cell.setCellValue(currentVo.getName());
            }else {
                cell.setCellValue("--");
            }
            if(currentVo.getSize() > 1) {
                CellRangeAddress cellAddresses = new CellRangeAddress(currentRow, currentRow + currentVo.getSize() - 1, currentColumn, currentColumn);
                cellRangeAddresseList.add(cellAddresses);
//                this.sheet.addMergedRegion(cellAddresses);
            }
            currentColumn++;
            verticalIndexMap.put(currentVo.getId(), currentRow);
        }else{
            currentColumn++;
        }
        if(ObjectUtils.isNotEmpty(currentVo.getChildren())){
            writeVertical(currentVo.getChildren(), rowNumber, level + 1);
        }
        return true;
    }

    private void writeHorizontal(){
        if(ObjectUtils.isEmpty(this.horizontal)){
            return;
        }
        currentRow = 1;
        List<TreeTableVo> list = this.horizontal;
        while(ObjectUtils.isNotEmpty(list)){
            currentColumn = verticalSize;
            Row row = getRow(currentRow);
            list.forEach(item -> {
                Cell cell = getCell(row, currentColumn);
                if(StringUtils.isNotBlank(item.getName())){
                    cell.setCellValue(item.getName());
                }else {
                    cell.setCellValue("--");
                }
                Integer size = item.getSize();
                if(size > 1){
                    CellRangeAddress cellAddresses = new CellRangeAddress(currentRow, currentRow, currentColumn, currentColumn + size - 1);
                    cellRangeAddresseList.add(cellAddresses);
//                    this.sheet.addMergedRegion(cellAddresses);
                }
                horizontalIndexMap.put(item.getId(), currentColumn);
                currentColumn+=size;
            });
            currentRow++;
            list = list.parallelStream().filter(item -> ObjectUtils.isNotEmpty(item.getChildren())).flatMap(item -> item.getChildren().stream()).collect(Collectors.toList());
            if(currentColumn > maxColumnNumber){
                maxColumnNumber = currentColumn;
            }
        }
    }

    private void writeRelationship(){
        if(ObjectUtils.isEmpty(this.relationshipList)){
            return;
        }
        currentRow = verticalSize;
        currentColumn = horizontalSize + (StringUtils.isNotBlank(totalTitle)?1:0);
        this.relationshipList.forEach(relationship -> {
            String verticalNodeId = relationship.getVerticalNodeId();
            String horizontalNodeId = relationship.getHorizontalNodeId();
            Integer rowNumber = verticalIndexMap.get(verticalNodeId);
            Integer columnNumber = horizontalIndexMap.get(horizontalNodeId);
            if(ObjectUtils.isEmpty(rowNumber) || ObjectUtils.isEmpty(columnNumber)){
                return;
            }
            Cell cell = getCell(rowNumber, columnNumber);
            cell.setCellValue("√");
        });
    }




    private Row getRow(int rowNumber){
        Row row = sheet.getRow(rowNumber);
        if(ObjectUtils.isNotEmpty(row)){
            return row;
        }
        return sheet.createRow(rowNumber);
    }

    private Cell getCell(int rowNumber, int cellNumber){
        Row row = getRow(rowNumber);
        Cell cell = row.getCell(cellNumber);
        if(ObjectUtils.isEmpty(cell)){
            cell = row.createCell(cellNumber);
        }
        cell.setCellStyle(cellStyle);
        return cell;
    }

    private Cell getCell(Row row, int cellNumber){
        Cell cell = row.getCell(cellNumber);
        if(ObjectUtils.isEmpty(cell)){
            cell = row.createCell(cellNumber);
        }
        cell.setCellStyle(cellStyle);
        return cell;
    }

    public String getSheetName() {
        return sheetName;
    }

    public void setSheetName(String sheetName) {
        this.sheetName = sheetName;
    }

    public String getTotalTitle() {
        return totalTitle;
    }

    public void setTotalTitle(String totalTitle) {
        this.totalTitle = totalTitle;
    }

    public String getPartialTitle() {
        return partialTitle;
    }

    public void setPartialTitle(String partialTitle) {
        this.partialTitle = partialTitle;
    }

    public List<TreeTableVo> getVertical() {
        return vertical;
    }

    public void setVertical(List<TreeTableVo> vertical) {
        this.vertical = vertical;
    }

    public List<String> getVerticalTitle() {
        return verticalTitle;
    }

    public void setVerticalTitle(List<String> verticalTitle) {
        this.verticalTitle = verticalTitle;
    }

    public Integer getVerticalSize() {
        return verticalSize;
    }

    public void setVerticalSize(Integer verticalSize) {
        this.verticalSize = verticalSize;
    }

    public List<TreeTableVo> getHorizontal() {
        return horizontal;
    }

    public void setHorizontal(List<TreeTableVo> horizontal) {
        this.horizontal = horizontal;
    }

    public List<String> getHorizontalTitle() {
        return horizontalTitle;
    }

    public void setHorizontalTitle(List<String> horizontalTitle) {
        this.horizontalTitle = horizontalTitle;
    }

    public Integer getHorizontalSize() {
        return horizontalSize;
    }

    public void setHorizontalSize(Integer horizontalSize) {
        this.horizontalSize = horizontalSize;
    }

    public List<ExcelRelationshipVo> getRelationshipList() {
        return relationshipList;
    }

    public void setRelationshipList(List<ExcelRelationshipVo> relationshipList) {
        this.relationshipList = relationshipList;
    }


}
