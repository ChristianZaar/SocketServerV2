package com.example.happening;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class SocketConnection implements Runnable {

    Socket socket;
    ObjectInputStream oIS = null;
    ObjectOutputStream oOS = null;
    DbAccess dA;

    public SocketConnection(Socket socket, DbAccess dA) {
        this.socket = socket;
        this.dA = dA;
    }

    @Override
    public void run() {

        try {

            oIS = new ObjectInputStream(socket.getInputStream());
            oOS = new ObjectOutputStream(socket.getOutputStream());
            Object o = oIS.readObject();
            if(o instanceof String){
                //Run request
                runCommand((String)o);
            }

        }
        catch (IOException e){
            e.printStackTrace();
        }
        catch (ClassNotFoundException ce){
            ce.printStackTrace();
        }
        catch (Exception ex){
            ex.printStackTrace();
        }
        finally {

            try{
                // Close socket & streams
                if(socket != null )
                    socket.close();

                if(oIS != null)
                    oIS.close();

                if(oOS != null)
                    oOS.close();
            }
            catch (IOException e){
                e.printStackTrace();
            }

            dA.closeConnection();
        }


    }

    private void runCommand(String cmd) throws IOException,ClassNotFoundException{

        switch(cmd){
            case "addEventToDb":
                addEvent();
                break;
        }
    }

    private void addEvent()throws IOException, ClassNotFoundException{

            Happening happening = (Happening)oIS.readObject();
            dA.addHappening(happening);
    }

}
