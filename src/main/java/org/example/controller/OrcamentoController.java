package org.example.controller;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.example.model.Item;

import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.List;

public class OrcamentoController {

    public double calcularTotal(List<org.example.model.Item> itens) {
        return itens.stream().mapToDouble(org.example.model.Item::getPrice).sum();
    }

    public void gerarPlanilha(List<Item> itens, String nomeArquivo, double valorMaximo) throws IOException {
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Orçamento");

        // ==== Estilos ====
        DataFormat format = workbook.createDataFormat();

        Font boldFont = workbook.createFont();
        boldFont.setBold(true);

        CellStyle headerStyle = workbook.createCellStyle();
        headerStyle.setFont(boldFont);
        headerStyle.setFillForegroundColor(IndexedColors.LIGHT_BLUE.getIndex());
        headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        headerStyle.setAlignment(HorizontalAlignment.CENTER);
        headerStyle.setBorderBottom(BorderStyle.THIN);
        headerStyle.setBorderTop(BorderStyle.THIN);
        headerStyle.setBorderLeft(BorderStyle.THIN);
        headerStyle.setBorderRight(BorderStyle.THIN);

        CellStyle currencyStyle = workbook.createCellStyle();
        currencyStyle.setDataFormat(format.getFormat("R$ #,##0.00"));
        currencyStyle.setBorderBottom(BorderStyle.THIN);
        currencyStyle.setBorderTop(BorderStyle.THIN);
        currencyStyle.setBorderLeft(BorderStyle.THIN);
        currencyStyle.setBorderRight(BorderStyle.THIN);

        CellStyle totalStyle = workbook.createCellStyle();
        totalStyle.cloneStyleFrom(currencyStyle);
        totalStyle.setFillForegroundColor(IndexedColors.LIGHT_YELLOW.getIndex());
        totalStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);

        CellStyle liquidoStyle = workbook.createCellStyle();
        liquidoStyle.cloneStyleFrom(currencyStyle);
        liquidoStyle.setFillForegroundColor(IndexedColors.LIGHT_ORANGE.getIndex());
        liquidoStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);

        CellStyle zebraStyle = workbook.createCellStyle();
        zebraStyle.cloneStyleFrom(currencyStyle);
        zebraStyle.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
        zebraStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);

        // ==== Título ====
        Row titulo = sheet.createRow(0);
        Cell tituloCell = titulo.createCell(0);
        tituloCell.setCellValue("Orçamento: " + nomeArquivo);
        tituloCell.setCellStyle(headerStyle);
        sheet.addMergedRegion(new org.apache.poi.ss.util.CellRangeAddress(0, 0, 0, 1));

        Row valorMaxRow = sheet.createRow(1);
        Cell valorMaxLabel = valorMaxRow.createCell(0);
        valorMaxLabel.setCellValue("Valor Máximo Disponível:");
        valorMaxLabel.setCellStyle(headerStyle);

        Cell valorMaxCell = valorMaxRow.createCell(1);
        valorMaxCell.setCellValue(valorMaximo);
        valorMaxCell.setCellStyle(currencyStyle);

        // ==== Cabeçalho da Tabela ====
        Row header = sheet.createRow(3);
        Cell itemHeader = header.createCell(0);
        itemHeader.setCellValue("Item");
        itemHeader.setCellStyle(headerStyle);

        Cell precoHeader = header.createCell(1);
        precoHeader.setCellValue("Preço");
        precoHeader.setCellStyle(headerStyle);

        // ==== Dados ====
        int rowNum = 4;
        for (int i = 0; i < itens.size(); i++) {
            Item item = itens.get(i);
            Row row = sheet.createRow(rowNum++);
            Cell cellItem = row.createCell(0);
            cellItem.setCellValue(item.getName());
            cellItem.setCellStyle(i % 2 == 0 ? currencyStyle : zebraStyle);

            Cell cellPreco = row.createCell(1);
            cellPreco.setCellValue(item.getPrice());
            cellPreco.setCellStyle(i % 2 == 0 ? currencyStyle : zebraStyle);
        }

        // ==== Total ====
        double total = calcularTotal(itens);
        Row totalRow = sheet.createRow(rowNum++);
        Cell totalLabel = totalRow.createCell(0);
        totalLabel.setCellValue("Total");
        totalLabel.setCellStyle(headerStyle);

        Cell totalCell = totalRow.createCell(1);
        totalCell.setCellValue(total);
        totalCell.setCellStyle(totalStyle);

        // ==== Líquido ====
        Row liquidoRow = sheet.createRow(rowNum);
        Cell liquidoLabel = liquidoRow.createCell(0);
        liquidoLabel.setCellValue("Líquido");
        liquidoLabel.setCellStyle(headerStyle);

        Cell liquidoCell = liquidoRow.createCell(1);
        liquidoCell.setCellValue(valorMaximo - total);
        liquidoCell.setCellStyle(liquidoStyle);

        // ==== Autoajuste de colunas ====
        sheet.autoSizeColumn(0);
        sheet.autoSizeColumn(1);

        // ==== Salvar ====
        String caminho = Paths.get("C:\\Users\\Fabys\\Documents\\Orçamentos", nomeArquivo + ".xlsx").toString();
        try (FileOutputStream fileOut = new FileOutputStream(caminho)) {
            workbook.write(fileOut);
        }
        workbook.close();
    }

}
