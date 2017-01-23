package com.wian.poc.services;

import fr.opensagres.xdocreport.converter.ConverterRegistry;
import fr.opensagres.xdocreport.converter.ConverterTypeTo;
import fr.opensagres.xdocreport.converter.IConverter;
import fr.opensagres.xdocreport.converter.Options;
import fr.opensagres.xdocreport.core.document.DocumentKind;
import fr.opensagres.xdocreport.core.logging.LogUtils;
import fr.opensagres.xdocreport.core.registry.AbstractRegistry;
import fr.opensagres.xdocreport.document.IXDocReport;
import fr.opensagres.xdocreport.document.registry.XDocReportRegistry;
import fr.opensagres.xdocreport.template.IContext;
import fr.opensagres.xdocreport.template.TemplateEngineKind;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import fr.opensagres.xdocreport.core.XDocReportException;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.*;
import java.util.logging.Level;
import java.util.logging.Logger;

@RestController
public class WianSeEinde {

    @PostMapping("/fillTemplateAndConvert")

    public @ResponseBody ResponseEntity<byte[]> hello(@RequestParam(name = "inputFile",required = true) MultipartFile inputFile,
                                                      @RequestParam(name = "images",required = true) MultipartFile images[])
            throws FileNotFoundException, XDocReportException, IOException{

        ByteArrayOutputStream out = new ByteArrayOutputStream();
                //new FileOutputStream(outFile);
        IXDocReport report = XDocReportRegistry.getRegistry().loadReport(inputFile.getInputStream(), TemplateEngineKind.Velocity);


        IContext context = report.createContext();
        context.put("ProductName", "Finnic");

        for (MultipartFile image : images) {
            String name = image.getOriginalFilename().substring(0,image.getOriginalFilename().length()-4);
            Logger.getGlobal().log(Level.WARNING, "Got image "+name);
            context.put(name, image.getBytes());
        }

        Options options = Options.getTo(ConverterTypeTo.PDF);
        report.convert(context, options, out);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType("application/pdf"));
        String filename = "output.pdf";
        headers.setContentDispositionFormData(filename, filename);
        headers.setCacheControl("must-revalidate, post-check=0, pre-check=0");
        ResponseEntity<byte[]> response = new ResponseEntity<byte[]>(out.toByteArray(), headers, HttpStatus.OK);
        return response;
    }

    @PostMapping("/convertToPDF")
    public @ResponseBody ResponseEntity<byte[]> convertToPDF(@RequestParam(name = "inputFile",required = true) MultipartFile file,
                                                      RedirectAttributes redirectAttributes)
            throws FileNotFoundException, XDocReportException, IOException{

        Options options = Options.getFrom(DocumentKind.ODT).to(ConverterTypeTo.PDF);

        IConverter converter = ConverterRegistry.getRegistry().getConverter(options);
        if (converter == null){
            throw new XDocReportException("Converter not found.");
        }

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        IXDocReport report = XDocReportRegistry.getRegistry().loadReport(file.getInputStream(), TemplateEngineKind.Velocity);

        converter.convert(file.getInputStream(), out, options);


        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType("application/pdf"));
        String filename = "output.pdf";
        headers.setContentDispositionFormData(filename, filename);
        headers.setCacheControl("must-revalidate, post-check=0, pre-check=0");
        ResponseEntity<byte[]> response = new ResponseEntity<byte[]>(out.toByteArray(), headers, HttpStatus.OK);
        return response;
    }
}
