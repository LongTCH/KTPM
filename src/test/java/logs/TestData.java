package logs;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.apache.poi.common.usermodel.HyperlinkType;
import org.apache.poi.xssf.usermodel.XSSFHyperlink;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CreationHelper;
import org.apache.poi.ss.usermodel.Row;
import utils.ExcelUtils;

import java.io.IOException;
import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public abstract class TestData {
    private String action;
    private Date logTime;
    private String testMethod;
    private String expected;
    private String actual;
    private String status;
    private String exception = null;
    private String image = null;

    public abstract void writeDataRow(Row row, XSSFSheet sheet) throws IOException;

    public void writeTestData(int startIndex, Row row, XSSFSheet sheet) throws IOException {
        CreationHelper creationHelper = sheet.getWorkbook().getCreationHelper();
        CellStyle globalStyle = row.getRowStyle();
        Cell cell;
        cell = row.createCell(startIndex);
        cell.setCellValue(getAction());
        cell.setCellStyle(globalStyle);

        cell = row.createCell(startIndex + 1);
        cell.setCellValue(getLogTime());
        CellStyle datetimeStyle = sheet.getWorkbook().createCellStyle();
        datetimeStyle.cloneStyleFrom(globalStyle);
        datetimeStyle.setDataFormat(creationHelper.createDataFormat().getFormat("yyyy-MM-dd HH:mm:ss"));
        cell.setCellStyle(datetimeStyle);

        cell = row.createCell(startIndex + 2);
        cell.setCellValue(getTestMethod());
        cell.setCellStyle(globalStyle);

        cell = row.createCell(startIndex + 3);
        cell.setCellValue(getExpected());
        cell.setCellStyle(globalStyle);

        cell = row.createCell(startIndex + 4);
        cell.setCellValue(getActual());
        cell.setCellStyle(globalStyle);

        cell = row.createCell(startIndex + 5);
        cell.setCellValue(getStatus());
        cell.setCellStyle(globalStyle);

        if (getException() != null) {
            cell = row.createCell(startIndex + 6);
            cell.setCellValue(getException());
            cell.setCellStyle(globalStyle);
        }

        if (getImage() != null) {
            cell = row.createCell(startIndex + 7);
            cell.setCellStyle(globalStyle);
            ExcelUtils.writeImage(getImage(), row, cell, sheet);

            cell = row.createCell(startIndex + 8);
            cell.setCellValue("Link screenshot");
            cell.setCellStyle(globalStyle);

            XSSFHyperlink hyperlink = (XSSFHyperlink) creationHelper.createHyperlink(HyperlinkType.URL);
            hyperlink.setAddress(getImage().replace("\\", "/"));
            cell.setHyperlink(hyperlink);
        }
    }
}
