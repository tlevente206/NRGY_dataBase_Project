package ua.com.nrgy.util;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import ua.com.nrgy.model.Tag;
import java.io.FileOutputStream;
import java.util.List;

public class ExcelExporter {
    public static void exportTagok(List<Tag> adatok, String mentesHelye, List<String> oszlopok, boolean highlight, boolean filter, String sheetName) {
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet(sheetName != null && !sheetName.isEmpty() ? sheetName : "Tagok");

            // --- STÍLUSOK ---
            CellStyle headerStyle = workbook.createCellStyle();
            Font hFont = workbook.createFont(); hFont.setBold(true);
            headerStyle.setFont(hFont);
            headerStyle.setFillForegroundColor(IndexedColors.LIGHT_CORNFLOWER_BLUE.getIndex());
            headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            headerStyle.setBorderBottom(BorderStyle.MEDIUM);

            CellStyle redStyle = workbook.createCellStyle();
            redStyle.setFillForegroundColor(IndexedColors.ROSE.getIndex());
            redStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);

            // --- FEJLÉC ---
            Row hRow = sheet.createRow(0);
            for (int i = 0; i < oszlopok.size(); i++) {
                Cell c = hRow.createCell(i);
                c.setCellValue(oszlopok.get(i));
                c.setCellStyle(headerStyle);
            }

            // --- ADATOK ---
            for (int i = 0; i < adatok.size(); i++) {
                Row r = sheet.createRow(i + 1);
                Tag t = adatok.get(i);
                for (int j = 0; j < oszlopok.size(); j++) {
                    Cell c = r.createCell(j);
                    String colName = oszlopok.get(j);
                    String val = getValue(t, colName);
                    c.setCellValue(val);

                    // EFJ PIROS KIEMELÉS (Ha kérték és "Nem")
                    if (highlight && colName.equals("EFJ") && "Nem".equals(val)) {
                        c.setCellStyle(redStyle);
                    }
                }
            }

            // --- UTÓMUNKÁLATOK ---
            for (int i = 0; i < oszlopok.size(); i++) {
                sheet.autoSizeColumn(i); // Automatikus szélesség minden oszlopra
            }
            if (filter) {
                sheet.setAutoFilter(new org.apache.poi.ss.util.CellRangeAddress(0, adatok.size(), 0, oszlopok.size() - 1));
            }

            try (FileOutputStream out = new FileOutputStream(mentesHelye)) {
                workbook.write(out);
            }
        } catch (Exception e) { e.printStackTrace(); }
    }

    private static String getValue(Tag t, String col) {
        if (t == null) return "";
        return switch(col) {
            case "Név" -> t.getNev() != null ? t.getNev() : "";
            case "Nem" -> t.getNem() != null ? t.getNem() : "";
            case "Kor" -> String.valueOf(t.getEletkor());
            case "Szül. Idő" -> t.getSzul_ido() != null ? t.getSzul_ido() : "";
            case "Szül. Hely" -> t.getSzul_hely() != null ? t.getSzul_hely() : "";
            case "Utca" -> t.getUtcaNeve() != null ? t.getUtcaNeve() : "";
            case "Hsz" -> t.getHazszam() != null ? t.getHazszam() : "";
            case "Telefon" -> t.getTelefonszam() != null ? t.getTelefonszam() : "";
            case "EFJ" -> t.isEfj_befizetes() ? "Igen" : "Nem";
            case "Presbiter" -> t.getPresbiterNeve() != null ? t.getPresbiterNeve() : "";
            case "Megjegyzés" -> t.getMegjegyzes() != null ? t.getMegjegyzes() : "";
            default -> "";
        };
    }
}