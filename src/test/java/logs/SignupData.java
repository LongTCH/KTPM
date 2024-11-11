package logs;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;

import java.io.IOException;
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SignupData extends TestData{
    private String firstName;
    private String lastName;
    private String country;
    private String phone;
    private String email;
    private String password;
    @Override
    public void writeDataRow(Row row, XSSFSheet sheet) throws IOException {
        CellStyle globalStyle = row.getRowStyle();
        Cell cell;
        cell = row.createCell(0);
        cell.setCellValue(getFirstName());
        cell.setCellStyle(globalStyle);

        cell = row.createCell(1);
        cell.setCellValue(getLastName());
        cell.setCellStyle(globalStyle);

        cell = row.createCell(2);
        cell.setCellValue(getCountry());
        cell.setCellStyle(globalStyle);

        cell = row.createCell(3);
        cell.setCellValue(getPhone());
        cell.setCellStyle(globalStyle);

        cell = row.createCell(4);
        cell.setCellValue(getEmail());
        cell.setCellStyle(globalStyle);

        cell = row.createCell(5);
        cell.setCellValue(getPassword());
        cell.setCellStyle(globalStyle);

        writeTestData(6, row, sheet);
    }
}
