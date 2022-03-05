package org.yh;

import lombok.extern.log4j.Log4j;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Log4j
public class WebConfig {
    private static final Map<String, String> servletMapping = new HashMap<>();
    private static final Map<String, String> servlets = new HashMap<>();
    public WebConfig() {
        init();
    }
    private void init(){
        SAXReader reader  = new SAXReader();
        Document document = null;

        try {
            document = reader.read(this.getClass().getResourceAsStream("/web.xml"));
        } catch (DocumentException e) {
            log.error("无配置文件");
            System.exit(1);
        }
        Element rootElement = document.getRootElement();
        //解析servlet
        List<Element> servlets = rootElement.elements("servlet");
        for (Element servlet : servlets) {
            String servletName = servlet.elementText("servlet-name");
            String servletClass = servlet.elementText("servlet-class");
            this.servlets.put(servletName, servletClass);
        }
        //解析servlet-mapping
        List<Element> servletMappings = rootElement.elements("servlet-mapping");
        for (Element servletMapping : servletMappings) {
            String urlPattern = servletMapping.elementText("url-pattern");
            String servletName = servletMapping.elementText("servlet-name");
            this.servletMapping.put(urlPattern, servletName);
        }
    }

    public static String getServletName(String urlPattern) {
        return servletMapping.get(urlPattern);
    }
    public static String getServletClass(String servletName) {
        return servlets.get(servletName);
    }

}
