/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.gsw.hems;

import edu.gsw.hems.server.CustomWebServer;

/**
 *
 * @author saxenah
 */
public class ServerBootup {

    public static void main(String[] args) {

        CustomWebServer server = new CustomWebServer(4040);
        new Thread(server).start();

    }

}
