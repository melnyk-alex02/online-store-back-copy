package com.store.utils;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class EmailTemplateReader {
    private final static Logger logger = LoggerFactory.getLogger(EmailTemplateReader.class);

    public static String readTemplateText(String filePath) {
        try {
            String emailTemplatePath = "src/main/resources/emailTemplates/";
            return new String(Files.readAllBytes(Paths.get(emailTemplatePath + filePath)));
        } catch (IOException e) {
            System.out.println("Error reading template file: " + filePath);
            logger.error("Error reading template file: {} - {}", filePath, e.getMessage());
            return "";
        }
    }

    public static String addCodeToTemplate(String template, int code) {
        return template.replace("[code]", Integer.toString(code));
    }
}