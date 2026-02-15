package ua.com.nrgy.util;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import ua.com.nrgy.model.Tag;
import java.io.FileOutputStream;
import java.util.List;

public class ExcelExporter {
    public static void exportTagok(List<Tag> adatok, String mentesHelye, List<String> oszlopok, boolean highlight, boolean filter, String lapNev) {
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet(lapNev);

            // Stílusok
            CellStyle headerStyle = workbook.createCellStyle();
            Font hFont = workbook.createFont(); hFont.setBold(true);
            headerStyle.setFont(hFont);
            headerStyle.setFillForegroundColor(IndexedColors.LIGHT_CORNFLOWER_BLUE.getIndex());
            headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);

            CellStyle redStyle = workbook.createCellStyle();
            redStyle.setFillForegroundColor(IndexedColors.ROSE.getIndex());
            redStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);

            // Fejléc
            Row hRow = sheet.createRow(0);
            for (int i = 0; i < oszlopok.size(); i++) {
                Cell c = hRow.createCell(i);
                c.setCellValue(oszlopok.get(i));
                c.setCellStyle(headerStyle);
            }

            // Adatok
            for (int i = 0; i < adatok.size(); i++) {
                Row r = sheet.createRow(i + 1);
                Tag t = adatok.get(i);
                for (int j = 0; j < oszlopok.size(); j++) {
                    Cell c = r.createCell(j);
                    String col = oszlopok.get(j);
                    String val = getValue(t, col);
                    c.setCellValue(val);
                    if (highlight && col.equals("EFJ") && "Nem".equals(val)) c.setCellStyle(redStyle);
                }
            }

            // Szélesség beállítása az adatok után
            for (int i = 0; i < oszlopok.size(); i++) sheet.autoSizeColumn(i);

            try (FileOutputStream out = new FileOutputStream(mentesHelye)) { workbook.write(out); }
        } catch (Exception e) { e.printStackTrace(); }
    }

    private static String getValue(Tag t, String col) {
        return switch(col) {
            case "Név" -> t.getNev();
            case "Nem" -> t.getNem();
            case "Kor" -> String.valueOf(t.getEletkor());
            case "EFJ" -> t.isEfj_befizetes() ? "Igen" : "Nem";
            case "Telefon" -> t.getTelefonszam();
            case "Utca" -> t.getUtcaNeve();
            case "Hsz" -> t.getHazszam();
            default -> "";
        };
    }
}