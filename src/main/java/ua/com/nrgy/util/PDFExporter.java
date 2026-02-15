package ua.com.nrgy.util;

import com.lowagie.text.*;
import com.lowagie.text.pdf.*;
import ua.com.nrgy.model.Tag;
import java.io.FileOutputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class PDFExporter {

    /**
     * @param adatok A mentendő tagok listája
     * @param mentesHelye A fájl útvonala
     * @param oszlopok A választott oszlopok nevei
     * @param isLandscape Tájolás (true = fekvő, false = álló)
     * @param betumeret A szöveg mérete (pt)
     */
    public static void exportTagok(List<Tag> adatok, String mentesHelye, List<String> oszlopok, boolean isLandscape, int betumeret) {
        // 1. Dokumentum létrehozása a választott tájolással
        Document document = new Document(isLandscape ? PageSize.A4.rotate() : PageSize.A4);

        try {
            PdfWriter.getInstance(document, new FileOutputStream(mentesHelye));
            document.open();

            // 2. Betűtípusok beállítása (Magyar ékezetek támogatása)
            BaseFont bf = BaseFont.createFont("c:/windows/fonts/arial.ttf", BaseFont.IDENTITY_H, BaseFont.EMBEDDED);
            Font titleFont = new Font(bf, betumeret + 6, Font.BOLD);
            Font headFont = new Font(bf, betumeret, Font.BOLD);
            Font normFont = new Font(bf, betumeret, Font.NORMAL);

            // 3. Cím és dátum hozzáadása
            Paragraph title = new Paragraph("Gyülekezeti Tagok Listája", titleFont);
            title.setAlignment(Element.ALIGN_CENTER);
            document.add(title);

            String most = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy.MM.dd HH:mm"));
            Paragraph timestamp = new Paragraph("Készült: " + most, new Font(bf, 8, Font.ITALIC));
            timestamp.setAlignment(Element.ALIGN_RIGHT);
            document.add(timestamp);
            document.add(Chunk.NEWLINE);

            // 4. Táblázat létrehozása a választott oszlopszámmal
            PdfPTable table = new PdfPTable(oszlopok.size());
            table.setWidthPercentage(100);
            table.setSpacingBefore(10f);

            // 5. Fejlécek legenerálása
            for (String fejlec : oszlopok) {
                PdfPCell cell = new PdfPCell(new Paragraph(fejlec, headFont));
                cell.setBackgroundColor(java.awt.Color.LIGHT_GRAY);
                cell.setPadding(5);
                cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                table.addCell(cell);
            }

            // 6. Adatok feltöltése soronként
            for (Tag tag : adatok) {
                for (String oszlop : oszlopok) {
                    String ertek = switch (oszlop) {
                        case "Név" -> tag.getNev();
                        case "Nem" -> tag.getNem();
                        case "Kor" -> String.valueOf(tag.getEletkor());
                        case "Szül. Idő" -> tag.getSzul_ido();
                        case "Szül. Hely" -> tag.getSzul_hely();
                        case "Utca" -> tag.getUtcaNeve();
                        case "Hsz" -> tag.getHazszam();
                        case "Telefon" -> tag.getTelefonszam();
                        case "EFJ" -> tag.isEfj_befizetes() ? "Igen" : "Nem";
                        case "Presbiter" -> tag.getPresbiterNeve();
                        case "Megjegyzés" -> tag.getMegjegyzes();
                        default -> "";
                    };

                    PdfPCell dataCell = new PdfPCell(new Phrase(ertek != null ? ertek : "", normFont));
                    dataCell.setPadding(4);
                    dataCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                    table.addCell(dataCell);
                }
            }

            document.add(table);
            document.close();
            System.out.println("PDF sikeresen elmentve: " + mentesHelye);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}