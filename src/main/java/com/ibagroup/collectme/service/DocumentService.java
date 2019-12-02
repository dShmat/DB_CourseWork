package com.ibagroup.collectme.service;

import com.ibagroup.collectme.domain.UserReports;
import com.ibagroup.collectme.service.dto.ManagerDTO;
import com.ibagroup.collectme.service.dto.PeriodDTO;
import com.ibagroup.collectme.service.dto.ReportDTO;
import org.docx4j.openpackaging.exceptions.Docx4JException;
import org.docx4j.openpackaging.packages.WordprocessingMLPackage;
import org.docx4j.openpackaging.parts.WordprocessingML.MainDocumentPart;
import org.docx4j.wml.*;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

@Service
public class DocumentService {
    private final MessageSource messageSource;

    public DocumentService(MessageSource messageSource) {
        this.messageSource = messageSource;
    }

    public File createReportsDocument(List<UserReports> userReportsList, ManagerDTO manager, PeriodDTO period) throws Docx4JException {
        WordprocessingMLPackage wordPackage = WordprocessingMLPackage.createPackage();
        formReportsMainDocumentPart(wordPackage, userReportsList, period, manager);
        String reports = messageSource.getMessage("reports.reports", null, new Locale("en"));
        File exportFile = new File(reports + " " + period.getMonth()+".docx");
        wordPackage.save(exportFile);
        return exportFile;
    }

    private MainDocumentPart formReportsMainDocumentPart(WordprocessingMLPackage wordPackage,
                                                         List<UserReports> userReportsList,
                                                         PeriodDTO period,
                                                         ManagerDTO manager) {
        MainDocumentPart mainDocumentPart = wordPackage.getMainDocumentPart();

        String activities = messageSource.getMessage("reports.Reports", null, new Locale("ru"));
        mainDocumentPart.addStyledParagraphOfText("Title", activities + " " + period.getMonth().getMonthValue()+ "/" + period.getMonth().getYear()
            + ". "+manager.getUser().getFirstName() + " " + manager.getUser().getLastName());

        userReportsList.forEach(userReportsItem ->{
            mainDocumentPart.addStyledParagraphOfText("Heading1", userReportsItem.getUser().getFirstName()
                +" " + userReportsItem.getUser().getLastName());
            List<ReportDTO> reports = userReportsItem.getReports();
            ObjectFactory objectFactory = org.docx4j.jaxb.Context.getWmlObjectFactory();
            reports.forEach(reportDTO -> {
                P headerPar = formReportsHeader(reportDTO, objectFactory);
                mainDocumentPart.getContent().add(headerPar);
                P activitiesPar = formReportsActivities(reportDTO, objectFactory);
                mainDocumentPart.getContent().add(activitiesPar);
            });
        });

        return mainDocumentPart;
    }

    private P formReportsActivities(ReportDTO reportDTO, ObjectFactory objectFactory) {
        P p = objectFactory.createP();
        R r = objectFactory.createR();
        Text t = objectFactory.createText();

        if(reportDTO.getActivities()==null) {
            String noInfo = messageSource.getMessage("reports.noInfo", null, new Locale("ru"));
            t.setValue(noInfo);
            r.getContent().add(objectFactory.createRTab());
            r.getContent().add(t);
            p.getContent().add(r);
            return p;
        } else{
            List<String> activities = Arrays.asList(reportDTO.getActivities().split("\n"));
            activities.forEach(a->{
                if(!a.trim().isEmpty()) {
                    Text t1 = objectFactory.createText();
                    t1.setValue(a);
                    r.getContent().add(objectFactory.createRTab());
                    r.getContent().add(t1);
                    r.getContent().add(objectFactory.createBr());
                }
            });
            p.getContent().add(r);
            return p;
        }
    }

    private P formReportsHeader(ReportDTO reportDTO, ObjectFactory objectFactory) {
        Text tProject = formReportsProjectHeader(reportDTO, objectFactory);
        Text tDaysAbsent = formReportsDaysAbsentHeader(reportDTO, objectFactory);

        P p = objectFactory.createP();
        R r = objectFactory.createR();
        RPr rPr = objectFactory.createRPr();
        rPr.setB( new BooleanDefaultTrue());
        r.setRPr(rPr);
        r.getContent().add(tProject);
        r.getContent().add(objectFactory.createBr());
        r.getContent().add(tDaysAbsent);
        p.getContent().add(r);
        return p;
    }

    private Text formReportsDaysAbsentHeader(ReportDTO reportDTO, ObjectFactory objectFactory) {
        String daysAbsent = messageSource.getMessage("reports.daysAbsent", null, new Locale("ru"));
        String days = messageSource.getMessage("reports.days", null, new Locale("ru"));

        Text t = objectFactory.createText();
        StringBuilder daysAbsentHeader = new StringBuilder();
        daysAbsentHeader.append(daysAbsent + "- ");
        if(reportDTO.getDaysAbsent()==null){ daysAbsentHeader.append("0 " + days);}
        else { daysAbsentHeader.append(reportDTO.getDaysAbsent()+" " + days);}
        t.setValue(daysAbsentHeader.toString());
        return t;
    }

    private Text formReportsProjectHeader(ReportDTO reportDTO, ObjectFactory objectFactory) {
        Text t = objectFactory.createText();
        StringBuilder projectHeader = new StringBuilder();
        projectHeader.append(reportDTO.getProject().getName().toUpperCase());
        if(reportDTO.getHours()==null) { projectHeader.append(" (0)");}
        else{ projectHeader.append(" ("+reportDTO.getHours()+")"); }
        t.setValue(projectHeader.toString());
        return t;
    }
}
