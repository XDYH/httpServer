package org.yh.response;

import java.io.*;
import java.util.Arrays;

public class StaticFileHandle {
    public static void sendStaticFile(Response response, String fileName) {
        File file = new File(fileName);
        if (file.exists()) {
            try {
                InputStream fileInputStream = new FileInputStream(file);
                int available = fileInputStream.available();
                byte[] buff = new byte[available];
                fileInputStream.read(buff, 0, available);

                response.setBody(buff);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
