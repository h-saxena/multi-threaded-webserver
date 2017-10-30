/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.gsw.hems.worker;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;

/**
 *
 * @author saxenah
 */
public class HttpRequestPartA implements Runnable {

    protected Socket clientSocket = null;
    protected String serverText = null;

    public HttpRequestPartA(Socket clientSocket, String serverText) {
        this.clientSocket = clientSocket;
        this.serverText = serverText;
    }

    public void run() {
        try {
            InputStream input = clientSocket.getInputStream();
            OutputStream output = clientSocket.getOutputStream();

            BufferedReader inFromServer = new BufferedReader(new InputStreamReader(input));
            String line = inFromServer.readLine();

            System.out.println("----- Reading the incoming socket data ----");
            while (!line.isEmpty()) {
                System.out.println(line);
                line = inFromServer.readLine();
            }
            System.out.println("----- Done ----");

            long time = System.currentTimeMillis();
            output.write(("HTTP/1.1 200 OK"
                    + "\n\n Welcome to Custom Web Server: " + this.serverText + " - "  + time)
                    .getBytes());
            output.close();
            input.close();
            System.out.println("Request processed: " + time);
        } catch (IOException e) {
            //report exception somewhere.
            e.printStackTrace();
        }
    }

}
