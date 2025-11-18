package com.buildingenergy.substation_manager.report.service;

import com.buildingenergy.substation_manager.meter.model.MeterHistory;
import com.buildingenergy.substation_manager.reading.model.ReadingHistory;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;

@Service
public class ExcelExportService {

    public void exportReadingHistory(List<ReadingHistory> historyList, HttpServletResponse response) throws IOException {
        String month = historyList.get(0).getSavedAt().getMonth().toString();

        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        String fileName = "company_readings_" + month + ".xlsx";
        response.setHeader("Content-Disposition", "attachment; filename=" + fileName);

        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Readings");
            int rowIndex = 0;

            CellStyle cellStyle = workbook.createCellStyle();
            Font headerFont = workbook.createFont();

            headerFont.setBold(true);
            headerFont.setColor(IndexedColors.BLACK.getIndex());
            cellStyle.setFont(headerFont);

            Row header = sheet.createRow(rowIndex++);
            String[] columns = {"Company", "Office", "Old kWh M1", "New kWh M1", "Diff kWh M1", "Old kWh M2", "New kWh M2", "Diff kWh M2", "Total kWh", "Total Cost", "Saved On"};

            CellStyle dateCellStyle = createHeader(workbook, cellStyle, header, columns);

            for (ReadingHistory r : historyList) {
                Row row = sheet.createRow(rowIndex++);
                row.createCell(0).setCellValue(r.getCompanyNameSnapshot());
                row.createCell(1).setCellValue(r.getOffice());
                row.createCell(2).setCellValue(r.getOldReadingM1().stripTrailingZeros().toPlainString());
                row.createCell(3).setCellValue(r.getNewReadingM1().stripTrailingZeros().toPlainString());
                row.createCell(4).setCellValue(r.getDifferenceM1().stripTrailingZeros().toPlainString());
                row.createCell(5).setCellValue(r.getOldReadingM2().stripTrailingZeros().toPlainString());
                row.createCell(6).setCellValue(r.getNewReadingM2().stripTrailingZeros().toPlainString());
                row.createCell(7).setCellValue(r.getDifferenceM2().stripTrailingZeros().toPlainString());
                row.createCell(8).setCellValue(r.getTotalConsumption().stripTrailingZeros().toPlainString());
                row.createCell(9).setCellValue(r.getTotalCost().toString());
                row.createCell(10).setCellValue(r.getSavedAt().toLocalDate());
                row.getCell(10).setCellStyle(dateCellStyle);
            }

            for (int i = 0; i < columns.length; i++) {
                sheet.autoSizeColumn(i);
            }

            workbook.write(response.getOutputStream());
        }
    }

    public void exportMeterHistory(List<MeterHistory> historyList, HttpServletResponse response) throws IOException {
        String month = historyList.get(0).getSavedAt().getMonth().toString();

        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        String fileName = "meter_readings_" + month + ".xlsx";
        response.setHeader("Content-Disposition", "attachment; filename=" + fileName);

        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Meter Readings");

            int rowIndex = 0;

            CellStyle cellStyle = workbook.createCellStyle();
            Font headerFont = workbook.createFont();

            headerFont.setBold(true);
            headerFont.setColor(IndexedColors.BLACK.getIndex());
            cellStyle.setFont(headerFont);

            Row header = sheet.createRow(rowIndex++);

            String[] columns = {"Meter", "Outside Body", "Room", "Description", "% Energy", "Old kWh", "New kWh", "Difference kWh", "Total Cost", "Saved On"};

            CellStyle dateCellStyle = createHeader(workbook, cellStyle, header, columns);

            for (MeterHistory r : historyList) {
                Row row = sheet.createRow(rowIndex++);
                row.createCell(0).setCellValue(r.getMeterName());
                row.createCell(1).setCellValue(r.getOutsideBody());
                row.createCell(2).setCellValue(r.getRoom());
                row.createCell(3).setCellValue(r.getDescription());
                row.createCell(4).setCellValue(r.getEnergyPercentage().stripTrailingZeros().toPlainString());
                row.createCell(5).setCellValue(r.getOldReadings().stripTrailingZeros().toPlainString());
                row.createCell(6).setCellValue(r.getNewReadings().stripTrailingZeros().toPlainString());
                row.createCell(7).setCellValue(r.getDifferenceReadings().stripTrailingZeros().toPlainString());
                row.createCell(8).setCellValue(r.getTotalCost().toString());
                row.createCell(9).setCellValue(r.getSavedAt().toLocalDate());
                row.getCell(9).setCellStyle(dateCellStyle);
            }

            for (int i = 0; i < columns.length; i++) {
                sheet.autoSizeColumn(i);
            }

            workbook.write(response.getOutputStream());
        }
    }

    private CellStyle createHeader(Workbook workbook, CellStyle cellStyle, Row header, String[] columns) {
        for (int i = 0; i < columns.length; i++) {
            Cell cell = header.createCell(i);
            cell.setCellValue(columns[i]);
            cell.setCellStyle(cellStyle);
        }

        CreationHelper creationHelper = workbook.getCreationHelper();
        CellStyle dateCellStyle = workbook.createCellStyle();
        dateCellStyle.setDataFormat(creationHelper.createDataFormat().getFormat("dd-MM-yyyy"));

        return dateCellStyle;
    }

}
