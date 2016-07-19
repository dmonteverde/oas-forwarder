package com.despegar.oas;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Servlet implementation class Forwarder
 */
public class Forwarder
    extends HttpServlet {
    private static final long serialVersionUID = 1L;

    /**
     * Default constructor. 
     */
    public Forwarder() {
        // TODO Auto-generated constructor stub
    }

    /**
     * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        String urlToPost = request.getHeader("OAS_FWD_URL");
        URL url = new URL(urlToPost);
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setRequestMethod("POST");
        @SuppressWarnings("unchecked")
        Enumeration<String> headerNames = request.getHeaderNames();
        while (headerNames.hasMoreElements()) {
            String nextElement = headerNames.nextElement();
            if (!nextElement.toUpperCase().equals("OAS_FWD_URL")) {
                con.setRequestProperty(nextElement, request.getHeader(nextElement));
            }
        }

        con.setDoOutput(true);
        DataOutputStream os = new DataOutputStream(con.getOutputStream());
        int c = request.getInputStream().read();
        StringBuffer buf = new StringBuffer(8 * 1024);
        while (c >= 0) {
            byte b = (byte) c;
            buf.append((char) b);
            c = request.getInputStream().read();
        }
        os.writeBytes(buf.toString());
        os.flush();
        os.close();

        Map<String, List<String>> headerFields = con.getHeaderFields();
        for (String header : headerFields.keySet()) {
            for (String elem : headerFields.get(header)) {
                response.addHeader(header, elem);
            }
        }

        int read;
        byte[] buffer = new byte[1000];
        while ((read = con.getInputStream().read(buffer)) > 0) {
            response.getOutputStream().write(buffer, 0, read);
        }
        response.getOutputStream().flush();

    }

}
