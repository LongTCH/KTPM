package utils;

import logs.LoginData;
import logs.TestData;
import org.apache.poi.xssf.usermodel.*;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.util.IOUtils;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;

import java.io.*;
import java.util.Set;

public class ExcelUtils {
    public static String DATA_SRC = "src/test/resources/data/";
    public static String IMAGE_SRC = "src/test/resources/images/";

    public static XSSFWorkbook getWorkbook(String filePath) throws IOException {
        File file = new File(filePath);
        if (!file.exists()) {
            throw new FileNotFoundException("File not found: " + filePath);
        }
        FileInputStream fis = new FileInputStream(file);
        XSSFWorkbook workbook = new XSSFWorkbook(fis);
        fis.close();
        return workbook;
    }

    public static XSSFSheet getSheet(XSSFWorkbook workbook, String sheetName) {
        XSSFSheet sheet = workbook.getSheet(sheetName);
        if (sheet == null) {
            throw new IllegalArgumentException("Sheet not found: " + sheetName);
        }
        return sheet;
    }

    public static CellStyle getRowStyle(XSSFWorkbook workbook) {
        CellStyle style = workbook.createCellStyle();
        style.setAlignment(HorizontalAlignment.CENTER);
        style.setVerticalAlignment(VerticalAlignment.CENTER);
        style.setWrapText(true);

        return style;
    }

    public static String getCellValue(XSSFSheet sheet, int row, int column) {
        String returnValue = "";
        XSSFCell cell = sheet.getRow(row).getCell(column);
        try {
            if (cell.getCellType() == CellType.STRING) {
                returnValue = cell.getStringCellValue();
            } else if (cell.getCellType() == CellType.NUMERIC) {
                returnValue = String.format("%.0f", cell.getNumericCellValue());
            } else {
                returnValue = "";
            }
        } catch (Exception e) {
            returnValue = "";
        }
        return returnValue;
    }

    public static void takeScreenShot(WebDriver driver, String outputSrc) throws IOException {
        File screenshot = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
        File destFile = new File(outputSrc);
        org.apache.commons.io.FileUtils.copyFile(screenshot, destFile);
    }

    public static Object[][] readSheetData(XSSFSheet sheet) {
        int rows = sheet.getPhysicalNumberOfRows();
        int cols = sheet.getRow(0).getLastCellNum();
        Object[][] data = new Object[rows - 1][cols];
        // -1 để chừa hàng cho title

        for (int row = 1; row < rows; row++) {
            for (int col = 0; col < cols; col++) {
                data[row - 1][col] = getCellValue(sheet, row, col);
            }
        }
        return data;
    }

    public static void writeImage(String imagePath, Row row, Cell cell, XSSFSheet sheet) throws IOException {
        InputStream is = new FileInputStream(imagePath);
        byte[] bytes = IOUtils.toByteArray(is);
        int pictureId = sheet.getWorkbook().addPicture(bytes, Workbook.PICTURE_TYPE_PNG);
        is.close();

        // Adding picture to the sheet
        XSSFDrawing drawing = sheet.createDrawingPatriarch();
        ClientAnchor anchor = new XSSFClientAnchor();
        anchor.setCol1(cell.getColumnIndex());
        anchor.setRow1(row.getRowNum());
        anchor.setCol2(cell.getColumnIndex() + 1);
        anchor.setRow2(row.getRowNum() + 1);

        drawing.createPicture(anchor, pictureId);
    }

    // Exports the modified workbook to a specified file path
    public static void export(XSSFWorkbook workbook, String filePath) throws IOException {
        FileOutputStream out = new FileOutputStream(filePath);
        workbook.write(out);
        out.close();
    }

    public static <T extends TestData> void writeLog(String src, String sheetName, Set<T> logs) throws IOException{
        XSSFWorkbook workbook = ExcelUtils.getWorkbook(src);
        XSSFSheet sheet = ExcelUtils.getSheet(workbook, sheetName);
        int firstRow = 0, lastRow = sheet.getPhysicalNumberOfRows();
        if (lastRow < firstRow) {
            lastRow = firstRow;
        }
        CellStyle rowStyle = ExcelUtils.getRowStyle(workbook);
        for (T log : logs) {
            Row row = sheet.createRow(lastRow++);
            row.setHeightInPoints(60);
            row.setRowStyle(rowStyle);
            log.writeDataRow(row, sheet);
        }

        export(workbook, src);
    }
}
