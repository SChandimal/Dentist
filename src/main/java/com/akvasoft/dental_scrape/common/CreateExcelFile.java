package com.akvasoft.dental_scrape.common;

import com.akvasoft.dental_scrape.DentalContent;
import org.apache.poi.ss.usermodel.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class CreateExcelFile {

    public static void createXlsFile(List<DentalContent> siteRowList, String sheetName, Workbook workbook) throws IOException {

        CreationHelper createHelper = workbook.getCreationHelper();
        Sheet sheet = workbook.createSheet(sheetName);
        Font headerFont = workbook.createFont();
        headerFont.setBold(true);
        headerFont.setFontHeightInPoints((short) 14);
        headerFont.setColor(IndexedColors.RED.getIndex());

        CellStyle headerCellStyle = workbook.createCellStyle();
        headerCellStyle.setFont(headerFont);

        Row headerRow = sheet.createRow(0);

        ArrayList<String> list = new ArrayList();


        list.add("S.No.");
        list.add("Name");
        list.add("Registration No");
        list.add("Dental Council");

        for (int i = 0; i < list.size(); i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(list.get(i));
            cell.setCellStyle(headerCellStyle);
        }


        // Create Cell Style for formatting Date
        CellStyle dateCellStyle = workbook.createCellStyle();
        dateCellStyle.setDataFormat(createHelper.createDataFormat().getFormat("dd/MM/yyyy"));

        // Create Other rows and cells with employees data
        int rowNum = 1;
        int max = 6;
        for (DentalContent siteRow : siteRowList) {
            Row row = sheet.createRow(rowNum++);
            int i = 0;

            row.createCell(0).setCellValue(siteRow.getNo());
            row.createCell(1).setCellValue(siteRow.getName());
            row.createCell(2).setCellValue(siteRow.getRegistration());
            row.createCell(3).setCellValue(siteRow.getCouncil());
        }

        for (int i = 0; i < list.size(); i++) {
            sheet.autoSizeColumn(i);
        }

    }

}
