package com.wisega.main;

import com.wisega.tool.WisegaRemoteServer;

public class RemoteMain {
	
	public static void main(String[] args)
	{
		
		WisegaRemoteServer wisegaRemoteServer = new WisegaRemoteServer();
		wisegaRemoteServer.start();
	}

}
