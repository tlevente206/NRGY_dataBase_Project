package ua.com.nrgy.util;

import com.lowagie.text.*;
import com.lowagie.text.pdf.*;
import ua.com.nrgy.model.Tag;
import java.io.FileOutputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class PDFExporter {
    public static void exportTagok(List<Tag> adatok, String mentesHelye, List<String> oszlopok, boolean landscape, int fontSize, String marginStr, boolean pNums) {
        float m = marginStr.equals("Keskeny") ? 15f : (marginStr.equals("Széles") ? 60f : 36f);
        Document doc = new Document(landscape ? PageSize.A4.rotate() : PageSize.A4, m, m, m, m);

        try {
            PdfWriter writer = PdfWriter.getInstance(doc, new FileOutputStream(mentesHelye));

            // Oldalszámozás eseménykezelő
            if (pNums) {
                writer.setPageEvent(new PdfPageEventHelper() {
                    @Override
                    public void onEndPage(PdfWriter writer, Document document) {
                        try {
                            BaseFont bf = BaseFont.createFont("C:\\Windows\\Fonts\\arial.ttf", BaseFont.IDENTITY_H, BaseFont.EMBEDDED);
                            PdfContentByte cb = writer.getDirectContent();
                            cb.beginText();
                            cb.setFontAndSize(bf, 9);
                            cb.showTextAligned(PdfContentByte.ALIGN_CENTER,
                                    writer.getPageNumber() + ". oldal",
                                    (document.right() - document.left()) / 2 + document.leftMargin(),
                                    document.bottom() - 20, 0);
                            cb.endText();
                        } catch (Exception e) { e.printStackTrace(); }
                    }
                });
            }

            doc.open();
            BaseFont bf = BaseFont.createFont("C:\\Windows\\Fonts\\arial.ttf", BaseFont.IDENTITY_H, BaseFont.EMBEDDED);
            Font titleFont = new Font(bf, fontSize + 6, Font.BOLD);
            Font headFont = new Font(bf, fontSize, Font.BOLD);
            Font normFont = new Font(bf, fontSize, Font.NORMAL);

            // 1. Dátum (jobbra fent)
            Paragraph datePara = new Paragraph("Készült: " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy.MM.dd HH:mm")), new Font(bf, 8));
            datePara.setAlignment(Element.ALIGN_RIGHT);
            doc.add(datePara);

            // 2. Cím (középen)
            Paragraph title = new Paragraph("Gyülekezeti Nyilvántartás", titleFont);
            title.setAlignment(Element.ALIGN_CENTER);
            doc.add(title);
            doc.add(Chunk.NEWLINE);

            // 3. Táblázat
            PdfPTable table = new PdfPTable(oszlopok.size());
            table.setWidthPercentage(100);

            // Fejlécek
            for (String h : oszlopok) {
                PdfPCell c = new PdfPCell(new Phrase(h, headFont));
                c.setBackgroundColor(java.awt.Color.LIGHT_GRAY);
                c.setPadding(5);
                table.addCell(c);
            }

            // Adatok
            for (Tag t : adatok) {
                for (String o : oszlopok) {
                    PdfPCell cell = new PdfPCell(new Phrase(getValue(t, o), normFont));
                    cell.setPadding(3);
                    table.addCell(cell);
                }
            }

            doc.add(table);
            doc.close();
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