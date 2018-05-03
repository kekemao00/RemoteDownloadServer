package com.wisega.tool;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Tool {
	
	public static void log(String msg)
	{
		System.out.println("    "+getCurTime()+"\n      "+msg);
	}
	public static String getCurTime() {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        return simpleDateFormat.format(new Date());
    }
	public  static  List<File> getFiles(String path)
	{
		List<File> list = new ArrayList<File>();
		File root = new File(path);
		if(!root.exists())
		{
			return list;
		}
		File[] files = root.listFiles();
		if(files==null)
		{
			return list;
		}
		
		for(File file:files)
		{
			list.add(file);
		}
		
	    return list;	
	}
	 public static  byte[] buildBytes(byte head,byte type,byte[]... data)
	    {
	        ByteArrayList buildData = new ByteArrayList();
	        for(byte[] byteEach:data)
	        {
	            buildData.add(byteEach);
	        }

	        ByteArrayList buildData2 = new ByteArrayList();
	        byte[] byteData = buildData.all2Bytes();

	        byte[] lenBytes = Hex.fromIntB(6+byteData.length);
	        buildData2.add(head);
	        buildData2.add(lenBytes);
	        buildData2.add(type);
	        buildData2.add(byteData);
	        return buildData2.all2Bytes();

	    }
	 public static void sleep(long time)
	 {
		 try {
			Thread.sleep(time);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	 }
	 public static byte sumCheck(byte[] data) {
	        byte result = 0;
	        int len = data.length;
	        for (int i = 0; i < len; i++) {
	            result = (byte) (result + data[i]);
	        }
	        return result;
	    }
}
