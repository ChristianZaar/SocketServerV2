package com.example.happening;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Scanner;
import java.time.LocalDateTime;

public class Main {

    public static void main(String[] args){

        final int PORT = 6969;
        ServerSocket serverSocket = null;
        String connectionURL = "";
        File file = new File("URL.zaar");
        try {
            Scanner sc = new Scanner(file);

            if (sc.hasNextLine())
                connectionURL = sc.nextLine();
            else
                System.exit(0);
        }
        catch(FileNotFoundException e){
            System.out.println("Put textfile with server URL in project root. Found on happening offical google drive.");
        }

        try {
            serverSocket = new ServerSocket(PORT);
        } catch (IOException e) {
            System.err.println("Error in creation of the server socket");
            System.exit(0);
        }

        while (true) {
            try {       // listen for a connection
                Socket socket = serverSocket.accept();
                System.out.println("Connection accepted");
                Thread sC = new Thread(new SocketConnection(socket, new DbAccess(connectionURL)));
                sC.start();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
