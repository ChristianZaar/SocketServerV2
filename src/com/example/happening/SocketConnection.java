package com.example.happening;

import com.example.happening.DbStuff.GetAttendRequest;
import com.example.happening.DbStuff.GetHappeningsRequest;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.time.LocalDateTime;

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
            oOS.writeObject(dA.getRetVal());
            oOS.flush();

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

    /**
     * Run command receive on socket
     * @param cmd
     * @throws IOException
     * @throws ClassNotFoundException
     */
    private void runCommand(String cmd) throws IOException,ClassNotFoundException{

        switch(cmd){

            case "addHappeningToDb"://Adds Happening to database
                addHappening();
                break;

            case "getHappenings"://Gets all happenings
                getHappenings();
                break;

            case "addAttend":
                addAttend();
                break;

            case "deleteAttend":
                deleteAttend();
                break;

            case "addComment":
                addComment();
                break;

            case "getComments":
                getComments();
                break;

                default:
                    System.out.println("Unknown request."+ cmd);
        }
    }

    /**
     * Add Happening
     * @throws IOException
     * @throws ClassNotFoundException
     */
    private void addHappening()throws IOException, ClassNotFoundException{

        Happening happening = (Happening)oIS.readObject();
        dA.addHappening(happening);

    }

    /**Get all happenings
     * Get all hapenings
     * @return ArrayList<Happening>
     */
    private void getHappenings() throws IOException,ClassNotFoundException{
        GetHappeningsRequest getHReq = (GetHappeningsRequest)oIS.readObject();

        oOS.writeObject(dA.getHappenings(
                getHReq.getUserName(),
                LocalDateTime.parse(getHReq.getDateStart()+"T00:00"),
                LocalDateTime.parse(getHReq.getDateEnd()+"T23:59")));
        oOS.flush();
    }

    /**
     * Adds attend to db
     * @throws IOException
     * @throws ClassNotFoundException
     */
    private void addAttend() throws IOException, ClassNotFoundException{
        dA.addAttend((GetAttendRequest)oIS.readObject());
    }

    /**
     * Delete attend from db
     * @throws IOException
     * @throws ClassNotFoundException
     */
    private void deleteAttend() throws IOException, ClassNotFoundException{
        dA.deleteAttend((GetAttendRequest)oIS.readObject());
    }

    /**
     * Add comment
     * @throws IOException
     * @throws ClassNotFoundException
     */
    private void addComment()throws IOException, ClassNotFoundException{
        dA.addComment((Comment)oIS.readObject());
    }

    private void getComments()throws IOException, ClassNotFoundException{
        oOS.writeObject(dA.getComments((Happening)oIS.readObject()));
    }
}
