package org.victornieto.gestionbiblioteca.service;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.victornieto.gestionbiblioteca.dto.*;
import org.victornieto.gestionbiblioteca.model.UsuarioModel;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

public class PdfService {

    private static final float MARGIN = 40;
    private static final float ROW_HEIGHT = 20;
    private static final float CELL_MARGIN = 5;

    public void generateTitlesUnitsBooks(List<LibroInventarioDTO> list, Boolean areTitles, String[] headers, float[] colWidths, UsuarioModel user, File file) throws IOException {
        PDDocument document = new PDDocument();
        PDPage page = createPage(true);
        document.addPage(page);

        PDPageContentStream content = new PDPageContentStream(document, page);

        float yStart = page.getMediaBox().getHeight() - MARGIN;

        createHeader(
                content,
                areTitles ? "Reporte de títulos de libros" : "Reporte de unidades de libros disponibles",
                String.valueOf(LocalDate.now()),
                String.valueOf(LocalTime.now().truncatedTo(ChronoUnit.SECONDS)),
                user,
                String.valueOf(list.size()),
                yStart
        );

        yStart -= 50;

        drawRow(content, yStart, colWidths, headers, true);

        yStart -= ROW_HEIGHT;

        for(LibroInventarioDTO libro: list) {
            //salto de pagina
            if (yStart<MARGIN) {
                content.close();
                page = createPage(true);
                document.addPage(page);
                content = new PDPageContentStream(document, page);
                yStart = page.getMediaBox().getHeight() - MARGIN;

                drawRow(content, yStart, colWidths, headers, true); // rehacer encabezado
                yStart -= ROW_HEIGHT;
            }

            String[] row = {
                    String.valueOf(libro.getId_libro()),
                    libro.getTitulo(),
                    libro.getAutor(),
                    libro.getCategoria(),
                    libro.getEditorial(),
                    libro.getEdicion(),
                    String.valueOf(libro.getAnio_publicacion()),
                    String.valueOf(libro.getPaginas()),
                    String.valueOf(libro.getUnidades())
            };

            drawRow(content, yStart, colWidths, row, false);
            yStart -= ROW_HEIGHT;
        }

        content.close();
        document.save(file);
        document.close();
    }

    public void generatePrestamos(List<PrestamoListDTO> list, String[] headers, float[] colWidths, UsuarioModel user, File file) throws IOException {
        PDDocument document = new PDDocument();
        PDPage page = createPage(true);
        document.addPage(page);

        PDPageContentStream content = new PDPageContentStream(document, page);

        float yStart = page.getMediaBox().getHeight() - MARGIN;

        createHeader(
                content,
                "Reporte de préstamos activos",
                String.valueOf(LocalDate.now()),
                String.valueOf(LocalTime.now().truncatedTo(ChronoUnit.SECONDS)),
                user,
                String.valueOf(list.size()),
                yStart
        );

        yStart -= 50;

        drawRow(content, yStart, colWidths, headers, true);

        yStart -= ROW_HEIGHT;

        for(PrestamoListDTO prestamo: list) {
            //salto de pagina
            if (yStart<MARGIN) {
                content.close();
                page = createPage(true);
                document.addPage(page);
                content = new PDPageContentStream(document, page);
                yStart = page.getMediaBox().getHeight() - MARGIN;

                drawRow(content, yStart, colWidths, headers, true); // rehacer encabezado
                yStart -= ROW_HEIGHT;
            }

            String[] row = {
                    String.valueOf(prestamo.idPrestamo()),
                    prestamo.getFechaInicio().toString(),
                    prestamo.getFechaEntrega().toString(),
                    prestamo.idEjemplar().toString(),
                    prestamo.getTitulo(),
                    prestamo.getAutor(),
                    prestamo.getCliente(),
                    prestamo.getUsuario()
            };

            drawRow(content, yStart, colWidths, row, false);
            yStart -= ROW_HEIGHT;
        }

        content.close();
        document.save(file);
        document.close();
    }

    public void generateClientes(List<ClienteListDTO> list, String[] headers, float[] colWidths, UsuarioModel user, File file) throws IOException {
        PDDocument document = new PDDocument();
        PDPage page = createPage(true);
        document.addPage(page);

        PDPageContentStream content = new PDPageContentStream(document, page);

        float yStart = page.getMediaBox().getHeight() - MARGIN;

        createHeader(
                content,
                "Reporte de clientes activos",
                String.valueOf(LocalDate.now()),
                String.valueOf(LocalTime.now().truncatedTo(ChronoUnit.SECONDS)),
                user,
                String.valueOf(list.size()),
                yStart
        );

        yStart -= 50;

        drawRow(content, yStart, colWidths, headers, true);

        yStart -= ROW_HEIGHT;

        for(ClienteListDTO cliente: list) {
            //salto de pagina
            if (yStart<MARGIN) {
                content.close();
                page = createPage(true);
                document.addPage(page);
                content = new PDPageContentStream(document, page);
                yStart = page.getMediaBox().getHeight() - MARGIN;

                drawRow(content, yStart, colWidths, headers, true); // rehacer encabezado
                yStart -= ROW_HEIGHT;
            }

            String[] row = {
                    cliente.getUsername(),
                    cliente.getNombre(),
                    cliente.getCorreo(),
                    cliente.getPrestamos().toString(),
                    cliente.getSanciones().toString(),
                    cliente.getFechaCreacion().toString()
            };

            drawRow(content, yStart, colWidths, row, false);
            yStart -= ROW_HEIGHT;
        }

        content.close();
        document.save(file);
        document.close();
    }

    public void generateSanciones(List<SancionesListDTO> list, String[] headers, float[] colWidths, UsuarioModel user, File file) throws IOException {
        PDDocument document = new PDDocument();
        PDPage page = createPage(true);
        document.addPage(page);

        PDPageContentStream content = new PDPageContentStream(document, page);

        float yStart = page.getMediaBox().getHeight() - MARGIN;

        createHeader(
                content,
                "Reporte de sanciones activas",
                String.valueOf(LocalDate.now()),
                String.valueOf(LocalTime.now().truncatedTo(ChronoUnit.SECONDS)),
                user,
                String.valueOf(list.size()),
                yStart
        );

        yStart -= 50;

        drawRow(content, yStart, colWidths, headers, true);

        yStart -= ROW_HEIGHT;

        for(SancionesListDTO sancion: list) {
            //salto de pagina
            if (yStart<MARGIN) {
                content.close();
                page = createPage(true);
                document.addPage(page);
                content = new PDPageContentStream(document, page);
                yStart = page.getMediaBox().getHeight() - MARGIN;

                drawRow(content, yStart, colWidths, headers, true); // rehacer encabezado
                yStart -= ROW_HEIGHT;
            }

            String[] row = {
                    sancion.getId().toString(),
                    sancion.getSancion(),
                    sancion.getDescripcion(),
                    sancion.getCliente(),
                    sancion.getIdPrestamo().toString(),
                    sancion.getLibro(),
                    sancion.getIdEjemplar().toString(),
                    sancion.getFecha().toString()
            };

            drawRow(content, yStart, colWidths, row, false);
            yStart -= ROW_HEIGHT;
        }

        content.close();
        document.save(file);
        document.close();
    }

    public void generateCategorias(List<CategoriaFormDTO> list, String[] headers, float[] colWidths, UsuarioModel user, File file) throws IOException {
        PDDocument document = new PDDocument();
        PDPage page = createPage(false);
        document.addPage(page);

        PDPageContentStream content = new PDPageContentStream(document, page);

        float yStart = page.getMediaBox().getHeight() - MARGIN;

        createHeader(
                content,
                "Reporte de categorías disponibles",
                String.valueOf(LocalDate.now()),
                String.valueOf(LocalTime.now().truncatedTo(ChronoUnit.SECONDS)),
                user,
                String.valueOf(list.size()),
                yStart
        );

        yStart -= 50;

        drawRow(content, yStart, colWidths, headers, true);

        yStart -= ROW_HEIGHT;

        int conteo = 0;

        for(CategoriaFormDTO cat: list) {
            //salto de pagina
            if (yStart<MARGIN) {
                content.close();
                page = createPage(true);
                document.addPage(page);
                content = new PDPageContentStream(document, page);
                yStart = page.getMediaBox().getHeight() - MARGIN;

                drawRow(content, yStart, colWidths, headers, true); // rehacer encabezado
                yStart -= ROW_HEIGHT;
            }

            String[] row = {
                    String.valueOf(++conteo),
                    cat.nombre()
            };

            drawRow(content, yStart, colWidths, row, false);
            yStart -= ROW_HEIGHT;
        }

        content.close();
        document.save(file);
        document.close();
    }

    public void generateEditoriales(List<EditorialFormDTO> list, String[] headers, float[] colWidths, UsuarioModel user, File file) throws IOException {
        PDDocument document = new PDDocument();
        PDPage page = createPage(false);
        document.addPage(page);

        PDPageContentStream content = new PDPageContentStream(document, page);

        float yStart = page.getMediaBox().getHeight() - MARGIN;

        createHeader(
                content,
                "Reporte de editoriales disponibles",
                String.valueOf(LocalDate.now()),
                String.valueOf(LocalTime.now().truncatedTo(ChronoUnit.SECONDS)),
                user,
                String.valueOf(list.size()),
                yStart
        );

        yStart -= 50;

        drawRow(content, yStart, colWidths, headers, true);

        yStart -= ROW_HEIGHT;

        int conteo = 0;

        for(EditorialFormDTO edit: list) {
            //salto de pagina
            if (yStart<MARGIN) {
                content.close();
                page = createPage(true);
                document.addPage(page);
                content = new PDPageContentStream(document, page);
                yStart = page.getMediaBox().getHeight() - MARGIN;

                drawRow(content, yStart, colWidths, headers, true); // rehacer encabezado
                yStart -= ROW_HEIGHT;
            }

            String[] row = {
                    String.valueOf(++conteo),
                    edit.nombre()
            };

            drawRow(content, yStart, colWidths, row, false);
            yStart -= ROW_HEIGHT;
        }

        content.close();
        document.save(file);
        document.close();
    }

    private void drawRow(PDPageContentStream content, float y, float[] colWidths, String[] texts, boolean header) throws IOException {
        float x = MARGIN;
        content.setFont(header ? PDType1Font.HELVETICA_BOLD : PDType1Font.HELVETICA, 9);

        for (int i=0; i<texts.length; i++) {
            //borde
            content.addRect(x, y-ROW_HEIGHT, colWidths[i], ROW_HEIGHT);
            content.stroke();

            //texto
            content.beginText();
            content.newLineAtOffset(x+CELL_MARGIN, y-15);
            content.showText(truncate(texts[i], colWidths[i]));
            content.endText();

            x += colWidths[i];
        }
    }

    private String truncate(String text, float width) {
        /**
         * Truncar el texto si excede el espacio en la casilla
         */
        int maxChars = (int) (width/6);
        return text.length() > maxChars ? text.substring(0, maxChars-3) + "..." : text;
    }

    private void createHeader(PDPageContentStream content, String title, String date, String time, UsuarioModel user, String amounRecords, float yStart) throws IOException {
        // titulo
        content.beginText();
        content.setFont(PDType1Font.HELVETICA_BOLD, 12);
        content.newLineAtOffset(MARGIN, yStart);
        content.showText(title);
        content.endText();

        yStart -= 12;

        // fecha y hora
        content.beginText();
        content.setFont(PDType1Font.HELVETICA, 10);
        content.newLineAtOffset(MARGIN, yStart);
        content.showText("Fecha y hora del reporte: " + date + ", " + time);
        content.endText();

        yStart -= 12;

        // usuario que generó el reporte
        content.beginText();
        content.setFont(PDType1Font.HELVETICA, 10);
        content.newLineAtOffset(MARGIN, yStart);
        content.showText("Reporte generado por: " + user.getNombre() + " " + user.getApellido_p() + (user.getApellido_m()!=null ? (" " + user.getApellido_m()) : "") + ", username: " + user.getUsername());
        content.endText();

        yStart -= 12;

        // numero de registros encontrados
        content.beginText();
        content.setFont(PDType1Font.HELVETICA, 10);
        content.newLineAtOffset(MARGIN, yStart);
        content.showText("Registros encontrados: " + amounRecords);
        content.endText();
    }

    private PDPage createPage(Boolean horizontal) {
        if (horizontal) {
            PDRectangle landscape = new PDRectangle(PDRectangle.A4.getHeight(), PDRectangle.A4.getWidth());
            PDPage page = new PDPage(landscape);
            page.setRotation(90);
            return page;

        } else {
            return new PDPage(PDRectangle.A4);
        }
    }
}
