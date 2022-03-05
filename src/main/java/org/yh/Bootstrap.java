package org.yh;

import org.yh.endpoint.BIOEndpoint;
import org.yh.util.PropertyUtil;
import lombok.extern.log4j.Log4j;

@Log4j
public class Bootstrap {
    public static void run() {
        String port = PropertyUtil.getProperty("server.port");


        BIOEndpoint bioEndpoint = new BIOEndpoint(Integer.parseInt(port));

    }

}
