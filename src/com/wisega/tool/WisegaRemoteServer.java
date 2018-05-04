package com.wisega.tool;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.wisega.tool.RemoteClient.ICallBack;


public class WisegaRemoteServer extends Thread{
    private Map<String,String> mConfigMap = new HashMap<>();
    private ServerSocket mServerSocket;
    private List<RemoteClient> mClient = new ArrayList<>();
    private Map<String,String> mOTAFiles = new HashMap<>();
	@Override
	public void run() {
		
		try {
			FileReader reader = new FileReader("./ini.txt");
			BufferedReader bufferedReader = new BufferedReader(reader);
			String line;
			while((line=bufferedReader.readLine())!=null)
			{
				String[] sp = line.split("`",-1);
				mConfigMap.put(sp[0], sp[1]);
			}
			bufferedReader.close();
			
			List<File> files = Tool.getFiles("./APPUSER");
			for(File file:files)
			{
				String name = file.getName();
				String[] sp = name.split("_",-1);
				mOTAFiles.put(sp[0], sp[1].substring(1));
			}
				
			
			
		}catch(Exception e)
		{
			e.printStackTrace();
		}
		
		try {
			
			mServerSocket = new ServerSocket(Integer.parseInt(mConfigMap.get("remote_port")));
			Tool.log("WisegaRemoteServer open sucess!");
			while(true) {
				
				Socket socket = mServerSocket.accept();
				RemoteClient remoteClient = new RemoteClient(socket,mOTAFiles);
				remoteClient.setICallBack(new ICallBack() {
					
					@Override
					public void disconnect(String appMark) {
						mClient.remove(remoteClient);
						Tool.log("disconnect "+appMark+" .Total [ "+mClient.size()+" ]");
					}
					@Override
					public void connect(String appMark) {
						
						mClient.add(remoteClient);
						Tool.log(appMark+" connect.Total [ "+mClient.size()+" ]");
					}
				});
				remoteClient.start();
			}
			
			
			
			
		}catch(Exception e)
		{
			e.printStackTrace();
		}
		
		
	}
}
