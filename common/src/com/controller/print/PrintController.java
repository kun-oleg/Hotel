package com.controller.print;

import com.dataprint.PrintFilds;
import com.services.print.PrintFlatsServiceImpl;
import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.engine.export.JRPdfExporter;
import net.sf.jasperreports.engine.export.JRPdfExporterParameter;
import net.sf.jasperreports.engine.util.JRLoader;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.jasperreports.JasperReportsUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import javax.inject.Inject;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by user on 11.08.2016.
 */
@Controller
@RequestMapping(value="/report")
public class PrintController {

    @Inject
    private PrintFlatsServiceImpl printFlatsServiceImpl;

    @RequestMapping(value="/print",method= RequestMethod.GET)
    @Secured({ "ROLE_ADMIN","ROLE_USER"})
    public void getReport(HttpServletResponse response) throws JRException, IOException {
        String userName = SecurityContextHolder.getContext().getAuthentication().getName();
        InputStream jasperStream = this.getClass().getResourceAsStream("/print/report1.jasper");
        Map<String,Object> params = new HashMap<String,Object>();
        List list = printFlatsServiceImpl.getListParamFlats(userName);
        JRDataSource dataSource = new JRBeanCollectionDataSource(list);
        JasperReport jasperReport = (JasperReport) JRLoader.loadObject(jasperStream);
        JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, params, dataSource);

        response.setContentType("application/x-pdf");
        response.setHeader("Content-disposition", "inline; filename=offer.pdf");

        final OutputStream outStream = response.getOutputStream();

        JasperExportManager.exportReportToPdfStream(jasperPrint, outStream);

    }

}
