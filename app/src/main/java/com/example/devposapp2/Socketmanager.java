package com.example.devposapp2;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.Formatter;

import android.content.Context;


public class Socketmanager
{
    public static final  boolean MESSAGE_CONNECTED=true;
    public static final  boolean MESSAGE_CONNECTED_ERROR=false;
    public static final  boolean MESSAGE_WRITE_SUCCESS=true;
    public static final  boolean MESSAGE_WRITE_ERROR=false;
    private  Socket mMyWifiSocket=null;
    private BufferedReader BufReader= null;
    private OutputStream PriOut = null;
    private boolean iState=false;

    public  String mstrIp="192.168.1.100";
    public  int mPort=9100;

    int TimeOut=1300;
    public boolean getIstate () {
        return iState;
    }
    public void threadconnect()
    {
        new ConnectThread();
    }

    public void threadconnectwrite(byte[] str, int size, int align )
    {
        new WriteThread(str,size,align);
    }

    public boolean connect()
    {

        close();
        try
        {
            mMyWifiSocket = new Socket();
            mMyWifiSocket.connect(new InetSocketAddress(mstrIp,mPort),TimeOut);

            PriOut= mMyWifiSocket.getOutputStream();
            return true;
        } catch (IOException e)
        {
            e.printStackTrace();
            SetState(MESSAGE_CONNECTED_ERROR);
            return false;
        }
    }


    public boolean write(byte[] out, int size, int align)
    {
        byte[] cc = new byte[]{0x1B,0x21,0x03};  // 0- normal size text
        //byte[] cc1 = new byte[]{0x1B,0x21,0x00};  // 0- normal size text
        byte[] bb = new byte[]{0x1B,0x21,0x08};  // 1- only bold text
        byte[] bb2 = new byte[]{0x1B,0x21,0x20}; // 2- bold with medium text
        byte[] bb3 = new byte[]{0x1B,0x21,0x10}; // 3- bold with large text
       final byte[] ESC_ALIGN_LEFT = new byte[] { 0x1b, 'a', 0x00 };
       final byte[] ESC_ALIGN_RIGHT = new byte[] { 0x1b, 'a', 0x02 };
       final byte[] ESC_ALIGN_CENTER = new byte[] { 0x1b, 'a', 0x01 };
        final byte[] ESC_CANCEL_BOLD = new byte[] { 0x1B, 0x45, 0 };
        byte[] FEED_PAPER_AND_CUT = {0x1D, 0x56, 66, 0x00};
        if(PriOut!=null)
        {
            try
            {

                switch (size){
                    case 0:
                        PriOut.write(cc);
                        break;
                    case 1:
                        PriOut.write(bb);
                        break;
                    case 2:
                        PriOut.write(bb2);
                        break;
                    case 3:
                        PriOut.write(bb3);
                        break;
                }

                switch (align){
                    case 0:
                        //left align
                        PriOut.write(ESC_ALIGN_LEFT);
                        break;
                    case 1:
                        //center align
                        PriOut.write(ESC_ALIGN_CENTER);
                        break;
                    case 2:
                        //right align
                        PriOut.write(ESC_ALIGN_RIGHT);
                        break;
                    case 3:
                        //right align
                        PriOut.write(FEED_PAPER_AND_CUT);
                        break;
                }
                PriOut.write(out);

                PriOut.flush();
                return true;
            } catch (IOException e)
            {
                e.printStackTrace();
                return false;
            }
        }
        else
        {
            return false;
        }
    }

    public boolean close()
    {
        if(mMyWifiSocket!=null)
        {
            try
            {
                mMyWifiSocket.close();
                mMyWifiSocket=null;
            }
            catch (IOException e1)
            {
                e1.printStackTrace();
            }
        }
        if(BufReader!=null)
        {
            try
            {
                BufReader.close();
                BufReader=null;
            } catch (IOException e2) {
                e2.printStackTrace();
            }
        }
        if(PriOut!=null)
        {
            try
            {
                PriOut.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            PriOut=null;
        }
        return true;
    }

    public boolean ConnectAndWrite(byte[] out,int size, int align)
    {
        if(connect())
        {
            write(out,size,align);
            close();
            SetState(MESSAGE_WRITE_SUCCESS);
            return true;
        }
        else
        {
            SetState(MESSAGE_CONNECTED_ERROR);
            return false;
        }
    }


    public Socketmanager(Context context)
    {
    }
    public Socketmanager()
    {
    }
    public void SetState(Boolean state)
    {
        iState=state;
    }

    private class ConnectThread extends Thread
    {
        public ConnectThread()
        {
            start();
        }
        public void run()
        {
            if(connect())
            {
                SetState(MESSAGE_CONNECTED);
            }
            close();
        }
    }
    private class WriteThread extends Thread
    {  byte[] out;
       int sizeF;
       int alignF;
        public WriteThread(byte[] str,int size ,int align)
        {
            out=str;
            sizeF=size;
            alignF=align;

            start();
        }
        public void run()
        {
            if(ConnectAndWrite(out,sizeF,alignF))
            {
                SetState(MESSAGE_WRITE_SUCCESS);
            }
        }
    }
}
