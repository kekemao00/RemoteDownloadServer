package com.wisega.tool;

import java.io.File;
import java.io.FileInputStream;
import java.util.Arrays;

public class SendFileTask extends Thread{
   private RemoteClient mRemoteClient;
   private File mFile;
   public SendFileTask(RemoteClient remoteClient,File file)
   {
	   mRemoteClient = remoteClient;
	   mFile = file;
   }
	@Override
	public void run() {
		Tool.log(" get file ready "+mFile.getName());
		
		try {
			FileInputStream fileInputStream = new FileInputStream(mFile);
			byte[] buff = new byte[4096];
			int len = 0;
			ByteArrayList byteArrayList = new ByteArrayList();
			while((len=fileInputStream.read(buff))>0)
			{
				mRemoteClient.writeClient(Arrays.copyOf(buff, len));
				byteArrayList.add(Arrays.copyOf(buff, len));
			
			}
			fileInputStream.close();
			Tool.log(" send file ok"+mFile.getName());
			Tool.sleep(500);
			mRemoteClient.writeClient(Tool.buildBytes((byte)0xa5,(byte)0x03,new byte[]{(byte)0x03,Tool.sumCheck(byteArrayList.all2Bytes())}));
			byteArrayList.clear();
		}catch(Exception e)
		{
			e.printStackTrace();
		}
		
		
	}
}
