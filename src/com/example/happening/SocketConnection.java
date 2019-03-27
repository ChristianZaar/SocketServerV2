package com.example.happening;

import com.example.happening.DbStuff.Cmd;
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
            if(o instanceof Cmd){
                //Run request
                runCommand((Cmd)o);
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
    private void runCommand(Cmd cmd) throws IOException,ClassNotFoundException{

        switch(cmd){

            case ADD_HAPPENING_TO_DB://Adds Happening to database
                addHappening();
                break;

            case GET_HAPPENINGS://Gets all happenings
                getHappenings();
                break;

            case ADD_ATTEND:
                addAttend();
                break;

            case DELETE_ATTEND:
                deleteAttend();
                break;

            case ADD_COMMENT:
                addComment();
                break;

            case GET_COMMENTS:
                getComments();
                break;

            case GET_ATTENDERS:
                getAttenders();
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

    /**
     * Get Comments from from db
     * @throws IOException
     * @throws ClassNotFoundException
     */
    private void getComments()throws IOException, ClassNotFoundException{
        oOS.writeObject(dA.getComments((Happening)oIS.readObject()));
    }

    /**
     * Get attenders from db
     * @throws IOException
     * @throws ClassNotFoundException
     */
    private void getAttenders()throws IOException, ClassNotFoundException{
        oOS.writeObject(dA.getAttenders((Happening)oIS.readObject()));
    }

}
