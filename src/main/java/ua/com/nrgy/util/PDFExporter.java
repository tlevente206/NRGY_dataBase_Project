package ua.com.nrgy.util;

import com.lowagie.text.*;
import com.lowagie.text.pdf.*;
import ua.com.nrgy.model.Tag;
import java.io.FileOutputStream;
import java.util.List;

public class PDFExporter {
    public static void exportTagok(List<Tag> adatok, String mentesHelye, List<String> oszlopok, boolean landscape, int fontSize, String marginStr, boolean pNums) {
        float m = marginStr.equals("Keskeny") ? 15f : (marginStr.equals("Széles") ? 60f : 36f);
        Document doc = new Document(landscape ? PageSize.A4.rotate() : PageSize.A4, m, m, m, m);
        try {
            PdfWriter.getInstance(doc, new FileOutputStream(mentesHelye));
            doc.open();
            BaseFont bf = BaseFont.createFont("C:\\Windows\\Fonts\\arial.ttf", BaseFont.IDENTITY_H, BaseFont.EMBEDDED);
            Font hF = new Font(bf, fontSize, Font.BOLD);
            Font nF = new Font(bf, fontSize, Font.NORMAL);

            PdfPTable table = new PdfPTable(oszlopok.size());
            table.setWidthPercentage(100);

            for (String h : oszlopok) {
                PdfPCell c = new PdfPCell(new Phrase(h, hF));
                c.setBackgroundColor(java.awt.Color.LIGHT_GRAY);
                table.addCell(c);
            }

            for (Tag t : adatok) {
                for (String o : oszlopok) {
                    table.addCell(new Phrase(getValue(t, o), nF));
                }
            }
            doc.add(table);
            doc.close();
        } catch (Exception e) { e.printStackTrace(); }
    }

    private static String getValue(Tag t, String col) {
        return switch(col) {
            case "Név" -> t.getNev();
            case "Kor" -> String.valueOf(t.getEletkor());
            case "EFJ" -> t.isEfj_befizetes() ? "Igen" : "Nem";
            case "Telefon" -> t.getTelefonszam();
            case "Utca" -> t.getUtcaNeve();
            case "Hsz" -> t.getHazszam();
            case "Nem" -> t.getNem();
            case "Szül. Idő" -> t.getSzul_ido();
            case "Szül. Hely" -> t.getSzul_hely();
            case "Presbiter" -> t.getPresbiterNeve();
            case "Megjegyzés" -> t.getMegjegyzes();
            default -> "";
        };
    }
}