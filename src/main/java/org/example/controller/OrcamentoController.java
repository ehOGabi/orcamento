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

    public void gerarPlanilha(List<org.example.model.Item> itens, String nomeArquivo, double valorMaximo) throws IOException {
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Orçamento");

        // Estilos
        CellStyle headerStyle = workbook.createCellStyle();
        Font boldFont = workbook.createFont();
        boldFont.setBold(true);
        headerStyle.setFont(boldFont);

        CellStyle currencyStyle = workbook.createCellStyle();
        currencyStyle.setDataFormat(workbook.createDataFormat().getFormat("R$ #,##0.00"));

        Row header = sheet.createRow(0);
        header.createCell(0).setCellValue("Item");
        header.createCell(1).setCellValue("Preço");
        header.getCell(0).setCellStyle(headerStyle);
        header.getCell(1).setCellStyle(headerStyle);

        int rowNum = 1;
        for (org.example.model.Item item : itens) {
            Row row = sheet.createRow(rowNum++);
            row.createCell(0).setCellValue(item.getName());
            Cell cell = row.createCell(1);
            cell.setCellValue(item.getPrice());
            cell.setCellStyle(currencyStyle);
        }

        double total = calcularTotal(itens);

        // Total
        Row totalRow = sheet.createRow(rowNum++);
        totalRow.createCell(0).setCellValue("Total");
        Cell totalCell = totalRow.createCell(1);
        totalCell.setCellValue(total);
        totalCell.setCellStyle(currencyStyle);

        // Líquido
        Row liquidoRow = sheet.createRow(rowNum);
        liquidoRow.createCell(0).setCellValue("Líquido");
        Cell liquidoCell = liquidoRow.createCell(1);
        liquidoCell.setCellValue(valorMaximo - total);
        liquidoCell.setCellStyle(currencyStyle);

        sheet.autoSizeColumn(0);
        sheet.autoSizeColumn(1);

        String caminho = "C:\\Users\\gabri\\OneDrive\\Documentos\\Orçamentos\\" + nomeArquivo + ".xlsx";
        FileOutputStream fileOut = new FileOutputStream(caminho);
        workbook.write(fileOut);
        fileOut.close();
        workbook.close();
    }
}
