/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.gsw.hems.worker;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.util.StringTokenizer;

/**
 *
 * @author saxenah
 */
public class HttpRequest implements Runnable {

    final static String CRLF = "\r\n";

    protected Socket clientSocket = null;
    protected String serverText = null;

    public HttpRequest(Socket clientSocket, String serverText) {
        this.clientSocket = clientSocket;
        this.serverText = serverText;
    }

    public void run() {
        try {
            InputStream input = clientSocket.getInputStream();
            OutputStream output = clientSocket.getOutputStream();

            BufferedReader inFromServer = new BufferedReader(new InputStreamReader(input));
            String line = inFromServer.readLine();

            StringTokenizer tokens = new StringTokenizer(line);
            tokens.nextToken(); // skip over the HTTP Method name
            String fileName = tokens.nextToken();

            System.out.println("----- Reading the incoming socket data ----");
            while (!line.isEmpty()) {
                System.out.println(line);
                line = inFromServer.readLine();
            }
            System.out.println("----- Reading socket Done ----");

            if (fileName != null && !fileName.trim().equals("")) {
                // Prepend a "." so that file request is within the current directory.
                fileName = "./resources" + fileName;
            }

            String statusLine = null;
            String contentTypeLine = null;
            String entityBody = null;

            // Open the requested file.
            FileInputStream fis = null;
            try {
                //fileName = "./index1.html";
                fis = new FileInputStream(fileName);

            } catch (FileNotFoundException e) {
                System.err.println("File Not found - " + fileName);
            }

            if (fis != null) {
                statusLine = "HTTP/1.0 200 OK " + CRLF;
                contentTypeLine = "Content-type: " + contentType(fileName) + CRLF;

            } else {
                statusLine = "HTTP/1.0 404 OK " + CRLF;
                contentTypeLine = "Content-type: text/html" + CRLF;
                entityBody = "<HTML>"
                        + "<HEAD><TITLE>Not Found</TITLE></HEAD>"
                        + "<BODY>Not Found</BODY></HTML>";
            }

            DataOutputStream os = new DataOutputStream(output);
            // Send the status line.
            os.writeBytes(statusLine);
            // Send the content type line.
            os.writeBytes(contentTypeLine);

            // Send a blank line to indicate the end of the header lines.
            os.writeBytes(CRLF);

            if (fis != null) {
                try {
                    sendBytes(fis, os);
                } catch (Exception ex) {
                    //Logger.getLogger(HttpRequest.class.getName()).log(Level.SEVERE, null, ex);
                    System.err.println("Error : in Sending bytes of data");
                } finally {
                    fis.close();
                }
            }
            output.close();
            input.close();
            System.out.println("Request processed: " );
        } catch (IOException e) {
            //report exception somewhere.
            e.printStackTrace();
        }
    }

    public static String contentType(String fileName) {
        if (fileName.endsWith(".htm") || fileName.endsWith(".html")) {
            return "text/html";
        }
        else if (fileName.endsWith(".css") ) {
            return "text/css";
        }
        else if (fileName.endsWith(".gif") ) {
            return "image/gif";
        }
        else if (fileName.endsWith(".jpeg") ) {
            return "image/jpeg";
        }
        else if (fileName.endsWith(".png") ) {
            return "image/png";
        }
        else if (fileName.endsWith(".wave") ) {
            return "audio/wave";
        }
        return "application/octet-stream";
    }

    private static void sendBytes(FileInputStream fis, OutputStream os)
            throws Exception {
        // Construct a 1K buffer to hold bytes on their way to the socket.
        byte[] buffer = new byte[1024];
        int bytes = 0;
        // Copy requested file into the socket's output stream.
        while ((bytes = fis.read(buffer)) != -1) {
            os.write(buffer, 0, bytes);
        }
    }

}
