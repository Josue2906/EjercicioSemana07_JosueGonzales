package com.example.EjercicioSemana07_JosueGonzales.controller;

import com.example.EjercicioSemana07_JosueGonzales.model.Compra;
import com.example.EjercicioSemana07_JosueGonzales.service.CompraService;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Table;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;

@Controller
@RequestMapping("/compras")
public class CompraController {

    @Autowired
    private CompraService compraService;

    @GetMapping("/nueva")
    public String mostrarFormularioCrear(Model model) {
        model.addAttribute("compra", new Compra());
        return "formulario";  // Sin subcarpeta, solo el nombre del archivo
    }

    @PostMapping
    public String guardarCompra(@ModelAttribute Compra compra) {
        compraService.guardar(compra);
        return "redirect:/compras";  // Redirige al listado después de guardar
    }

    @GetMapping("/editar/{id}")
    public String mostrarFormularioEditar(@PathVariable Long id, Model model) {
        Compra compra = compraService.buscarPorId(id)
                                       .orElseThrow(() -> new IllegalArgumentException("Compra no encontrada"));
        model.addAttribute("compra", compra);
        return "formulario";  // Sin subcarpeta, solo el nombre del archivo
    }

    @GetMapping("/eliminar/{id}")
    public String eliminarCompra(@PathVariable Long id) {
        compraService.eliminarPorId(id);
        return "redirect:/compras";  // Redirige al listado después de eliminar
    }

    @GetMapping
    public String listarCompras(Model model) {
        model.addAttribute("compras", compraService.listarTodas());
        return "lista";  // Sin subcarpeta, solo el nombre del archivo
    }
    
@GetMapping("/reporte/pdf")
    public void generarPdf(HttpServletResponse response) throws IOException {
        response.setContentType("application/pdf");
        response.setHeader("Content-Disposition", "inline; filename=compras_reporte.pdf");

        PdfWriter writer = new PdfWriter(response.getOutputStream());
        Document document = new Document(new com.itextpdf.kernel.pdf.PdfDocument(writer));

        // Actualizamos el número de columnas en la tabla para incluir los nuevos campos
    Table table = new Table(7); // Ahora con 7 columnas
    table.addCell("ID");
    table.addCell("Cliente");
    table.addCell("Fecha");
    table.addCell("Número RUC");
    table.addCell("Producto");
    table.addCell("Precio");
    table.addCell("Cantidad");

    // Obtener la lista de compras
    List<Compra> compras = compraService.listarTodas();
    for (Compra compra : compras) {
        // Llenamos la tabla con los datos correspondientes de cada compra
        table.addCell(compra.getId().toString());
        table.addCell(compra.getNombreCliente());
        table.addCell(compra.getFecha().toString());
        table.addCell(compra.getNumeroRuc());
        table.addCell(compra.getProducto()); // Añadimos producto
        table.addCell(String.valueOf(compra.getPrecio())); // Añadimos precio
        table.addCell(String.valueOf(compra.getCantidad())); // Añadimos cantidad
    }

    document.add(table);
    document.close();
    }

    @GetMapping("/reporte/excel")
    public void generarExcel(HttpServletResponse response) throws IOException {
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setHeader("Content-Disposition", "attachment; filename=compras_reporte.xlsx");

        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Compras");

        // Creamos la fila de encabezado
    Row headerRow = sheet.createRow(0);
    String[] columnHeaders = {"ID", "Cliente", "Fecha", "Número RUC", "Producto", "Precio", "Cantidad"}; // Actualizamos los encabezados
    for (int i = 0; i < columnHeaders.length; i++) {
        Cell cell = headerRow.createCell(i);
        cell.setCellValue(columnHeaders[i]);
        CellStyle style = workbook.createCellStyle();
        Font font = workbook.createFont();
        font.setBold(true);
        style.setFont(font);
        cell.setCellStyle(style);
    }

    // Obtener la lista de compras
    List<Compra> compras = compraService.listarTodas();
    int rowIndex = 1;
    for (Compra compra : compras) {
        // Crear fila para cada compra y añadir los valores de los campos
        Row row = sheet.createRow(rowIndex++);
        row.createCell(0).setCellValue(compra.getId());
        row.createCell(1).setCellValue(compra.getNombreCliente());
        row.createCell(2).setCellValue(compra.getFecha().toString());
        row.createCell(3).setCellValue(compra.getNumeroRuc());
        row.createCell(4).setCellValue(compra.getProducto()); // Añadimos producto
        row.createCell(5).setCellValue(compra.getPrecio()); // Añadimos precio
        row.createCell(6).setCellValue(compra.getCantidad()); // Añadimos cantidad
    }

    workbook.write(response.getOutputStream());
    workbook.close();
    }
}
