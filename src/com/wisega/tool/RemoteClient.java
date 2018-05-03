package com.wisega.tool;

import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Arrays;
import java.util.List;
/**
 * ����Э�飺ͷ ���� ����
 * A5 ����   ���� 
 * APPӦ����Ϣ�� A5 03 01 .........
 * APP��ȡ����OTA��Ϣ��A5 03 02 
 * APP��ȡ����OTA�ļ���A5 03 03 a/b    
 * */
public class RemoteClient extends Thread{
	private Socket mSocket;
	private InputStream mInputStream;
	private OutputStream mOutputStream;
	private ICallBack mICallback;
	private String mAPPUser = "unknown";
	private SendFileTask mSendFile;
	public RemoteClient(Socket socket)
	{
		mSocket = socket;
	}
	public void setICallBack(ICallBack icCallBack)
	{
		mICallback = icCallBack;
	}
	
    public String getmAPPUser() {
		return mAPPUser;
	}
	@Override
    public void run() {
    	
    	try {
    		mInputStream = mSocket.getInputStream();
    		mOutputStream = mSocket.getOutputStream();
    		byte[] buff = new byte[4096];
    		int len = 0;
    	
    		while((len=mInputStream.read(buff))>0)
    		{
    			handle(Arrays.copyOf(buff,len));
    			
    		}
    	}catch(Exception e)
    	{
    		e.printStackTrace();
    	}
    	finally {
    		try {
        		if(mInputStream!=null)
        		{
        			mInputStream.close();
        		}
        		if(mOutputStream!=null)
        		{
        			mOutputStream.close();
        		}
        		if(mSocket!=null)
        		{
        			mSocket.close();
        		}
        	}catch(Exception e)
        	{
        		e.printStackTrace();
        	}
    		mICallback.disconnect(mAPPUser);
    		
    	}
    	
    }
    
    public boolean writeToClient(byte[] data)
    {
    	try {
    		
    		if(mOutputStream==null)
    		{
    			return false;
    		}
    		//if(data.length<100)
    		//Tool.log("send:"+Hex.toString(data));
    		mOutputStream.write(data);
    		mOutputStream.flush();
    	}catch(Exception e)
    	{
    		e.printStackTrace();
    	}
    	
    	return true;
    }
    private void handle(byte[] data)
    {
    	Tool.log("recFromClient:"+ Hex.toString(data));
    	int dataLen = 0;
    	if(data[0]==(byte)0xa5)
    	{
    		if(data.length<6||((dataLen = Hex.toIntB(Arrays.copyOfRange(data,1,5)))<data.length))
        	{
        		Tool.log("handle data length < 6:"+dataLen);
        		return;
        	}
    		if(data[5]==(byte)0x01)//�ͻ��˷�������APP���
    		{
    			mAPPUser = new String(Arrays.copyOfRange(data, 6, dataLen));
    			mICallback.connect(mAPPUser);
    		}
    		else if(data[5]==(byte)0x02)//�ͻ��˷�������ȡOTA�ļ�
    		{
    			List<File> files = Tool.getFiles("./APPUSER/"+mAPPUser+"/OTA");
    			StringBuilder sb = new StringBuilder();
    			for(File file:files)
    			{
    				sb.append(file.getName());
    				sb.append("`");
    			}
    			sb.delete(sb.length()-1, sb.length());
    			writeToClient(Tool.buildBytes((byte)0xa5, (byte)0x02, sb.toString().getBytes()));
    			
    		}
    		else if(data[5]==(byte)0x03)//�ͻ���Ҫ��ȡOTA�ļ���
    		{
    			
    			
    			
    			if(data[6]==(byte)0x01)//�ͻ��˽��ձ�־λ�Ѿ���λ
                {
    				if(mSendFile!=null)
    				{
    					mSendFile.start();
    				}
                
                }
    			else {
    				
    				String wantImgType = new String(Arrays.copyOfRange(data, 6, dataLen));
        			Tool.log(mAPPUser+" wantImgType: "+wantImgType);
        			String mark = "_"+wantImgType+"_";
        			List<File> files = Tool.getFiles("./APPUSER/"+mAPPUser+"/OTA");
        			long size = 0;
        			for(File file:files)
        			{
        				String name = file.getName();
        				if(!name.contains(mark))
        				{
        					mSendFile = new SendFileTask(this, file);
        					size = file.length();
        				}
        			}
    				
    				writeToClient(Tool.buildBytes((byte)0xa5,(byte)0x03,new byte[]{(byte)0x01},Hex.fromIntB((int)size)));//���пͻ���׼���ñ�־λ��Ҫ�����ļ��ˣ�ͨ�����ļ�����ռ��
    			}
    			
    			
    			
    			
    			
    		}
    		
    	}
    	
    }
    public interface ICallBack
    {
    	void disconnect(String appMark);
    	void connect(String appMark);
    }
    
}