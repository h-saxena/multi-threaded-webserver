/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.gsw.hems.server;

import edu.gsw.hems.worker.HttpRequest;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 *
 * @author saxenah
 */
public class CustomWebServer  implements Runnable {

    protected int          serverPort   = 8080;
    protected ServerSocket serverSocket = null;
    protected boolean      isStopped    = false;

    public CustomWebServer(int port){
        this.serverPort = port;
    }

    public void run(){
        openServerSocket();
        while(! this.isStopped){
            Socket clientSocket = null;
            try {
                clientSocket = this.serverSocket.accept();
            } catch (IOException e) {
                throw new RuntimeException(
                    "Error accepting client connection", e);
            }
            new Thread(
                new HttpRequest(
                    clientSocket, "Multithreaded Server")
            ).start();
        }
        System.out.println("Server Stopped.") ;
    }

    public synchronized void stop(){
        this.isStopped = true;
        try {
            this.serverSocket.close();
        } catch (IOException e) {
            throw new RuntimeException("Error closing server", e);
        }
    }

    private void openServerSocket() {
        try {
            this.serverSocket = new ServerSocket(this.serverPort);
        } catch (IOException e) {
            throw new RuntimeException("Cannot open port at " + this.serverPort, e);
        }
    }
    
}
