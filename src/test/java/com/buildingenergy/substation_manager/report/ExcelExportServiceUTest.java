package com.buildingenergy.substation_manager.report;

import com.buildingenergy.substation_manager.meter.model.MeterHistory;
import com.buildingenergy.substation_manager.reading.model.ReadingHistory;
import com.buildingenergy.substation_manager.report.service.ExcelExportService;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.DelegatingServletOutputStream;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ExcelExportServiceUTest {

    private final ExcelExportService excelExportService = new ExcelExportService();

    @Test
    void givenValidReadingHistory_whenExportReadingHistory_ThenExcelFileIsGeneratedCorrectly() throws IOException {
        ReadingHistory history = ReadingHistory.builder()
                .companyNameSnapshot("TestCompany")
                .office("Office 1")
                .oldReadingM1(BigDecimal.valueOf(10))
                .newReadingM1(BigDecimal.valueOf(15))
                .differenceM1(BigDecimal.valueOf(5))
                .oldReadingM2(BigDecimal.valueOf(20))
                .newReadingM2(BigDecimal.valueOf(28))
                .differenceM2(BigDecimal.valueOf(8))
                .totalConsumption(BigDecimal.valueOf(13))
                .totalCost(BigDecimal.valueOf(4.55))
                .userIdSnapshot(UUID.randomUUID())
                .savedAt(LocalDateTime.of(2025, 1, 15, 10, 0))
                .build();

        List<ReadingHistory> historyList = List.of(history);

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        HttpServletResponse response = mock(HttpServletResponse.class);

        when(response.getOutputStream()).thenReturn(new DelegatingServletOutputStream(outputStream));

        excelExportService.exportReadingHistory(historyList, response, 1);

        Workbook workbook = new XSSFWorkbook(new ByteArrayInputStream(outputStream.toByteArray()));
        Sheet sheet = workbook.getSheetAt(0);

        Row header = sheet.getRow(0);
        assertEquals("Company", header.getCell(0).getStringCellValue());
        assertEquals("Office", header.getCell(1).getStringCellValue());
        assertEquals("Old kWh M1", header.getCell(2).getStringCellValue());

        Row row = sheet.getRow(1);
        assertEquals("TestCompany", row.getCell(0).getStringCellValue());
        assertEquals("Office 1", row.getCell(1).getStringCellValue());
        assertEquals("10", row.getCell(2).getStringCellValue());
        assertEquals("15", row.getCell(3).getStringCellValue());
        assertEquals("5", row.getCell(4).getStringCellValue());

        LocalDate date = row.getCell(10).getLocalDateTimeCellValue().toLocalDate();
        assertEquals(LocalDate.of(2025, 1, 15), date);
    }

    @Test
    void givenValidMeterHistory_whenExportMeterHistory_thenExcelFileIsGeneratedCorrectly() throws Exception {
        MeterHistory history = MeterHistory.builder()
                .meterNameSnapshot("19")
                .outsideBody("2")
                .room("101")
                .description("Main")
                .energyPercentage(BigDecimal.valueOf(25))
                .oldReadings(BigDecimal.valueOf(100))
                .newReadings(BigDecimal.valueOf(150))
                .differenceReadings(BigDecimal.valueOf(50))
                .totalCost(BigDecimal.valueOf(12.75))
                .userIdSnapshot(UUID.randomUUID())
                .savedAt(LocalDateTime.of(2025, 3, 20, 14, 30))
                .build();

        List<MeterHistory> historyList = List.of(history);

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        HttpServletResponse response = mock(HttpServletResponse.class);

        when(response.getOutputStream()).thenReturn(new DelegatingServletOutputStream(outputStream));

        excelExportService.exportMeterHistory(historyList, response, 3);

        Workbook workbook = new XSSFWorkbook(new ByteArrayInputStream(outputStream.toByteArray()));
        Sheet sheet = workbook.getSheetAt(0);

        Row header = sheet.getRow(0);
        assertEquals("Meter", header.getCell(0).getStringCellValue());
        assertEquals("Outside Body", header.getCell(1).getStringCellValue());
        assertEquals("Room", header.getCell(2).getStringCellValue());
        assertEquals("Description", header.getCell(3).getStringCellValue());
        assertEquals("% Energy", header.getCell(4).getStringCellValue());

        Row row = sheet.getRow(1);
        assertEquals("19", row.getCell(0).getStringCellValue());
        assertEquals("2", row.getCell(1).getStringCellValue());
        assertEquals("101", row.getCell(2).getStringCellValue());
        assertEquals("Main", row.getCell(3).getStringCellValue());

        assertEquals("25", row.getCell(4).getStringCellValue());
        assertEquals("100", row.getCell(5).getStringCellValue());
        assertEquals("150", row.getCell(6).getStringCellValue());
        assertEquals("50", row.getCell(7).getStringCellValue());
        assertEquals("12.75", row.getCell(8).getStringCellValue());

        LocalDate date = row.getCell(9).getLocalDateTimeCellValue().toLocalDate();
        assertEquals(LocalDate.of(2025, 3, 20), date);
    }
}
