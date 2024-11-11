package logs;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.simple.SimpleLogger;
import utils.ExcelUtils;

import java.io.IOException;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class LoginData extends TestData {
    private static final Logger logger = LoggerFactory.getLogger(LoginData.class);
    private String email;
    private String password;

    @Override
    public void writeDataRow(Row row, XSSFSheet sheet) throws IOException {
        CellStyle globalStyle = row.getRowStyle();
        Cell cell;
        cell = row.createCell(0);
        cell.setCellValue(getEmail());
        cell.setCellStyle(globalStyle);

        cell = row.createCell(1);
        cell.setCellValue(getPassword());
        cell.setCellStyle(globalStyle);

        writeTestData(2, row, sheet);
    }
}
