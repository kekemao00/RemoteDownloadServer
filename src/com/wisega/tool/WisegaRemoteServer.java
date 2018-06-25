
package com.wisega.tool;

import java.io.File;
import java.io.FileReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.wisega.tool.RemoteClient.ICallBack;
import java.io.BufferedReader;

public class WisegaRemoteServer extends Thread {
	private ServerSocket mServerSocket;
	private Map<String, List<ImgBean>> mImgMap = new HashMap<>();
    private Map<String,String> mConfigMap = new HashMap<>();
	@Override
	public void run() {

		try {
			List<ImgBean> beans = null;

			FileReader reader = new FileReader("./config.ini");
			BufferedReader bufferedReader = new BufferedReader(reader);
			String line,key = null;
			while ((line = bufferedReader.readLine())!=null) {
				
				if(line.equals("..."))
				{
					break;
				}
				if (line.startsWith("=")) {
					String[] sp = line.substring(1).split("`", -1);
					mConfigMap.put(sp[0],sp[1]);
				} else if (line.isEmpty()) {
					
					//证明发现了下一个mode,必须先把上一个先存
					if(beans!=null)
					{
						List<ImgBean> temp = new ArrayList<>();
						temp.addAll(beans);
						mImgMap.put(key, temp);
					}
					
					key = bufferedReader.readLine();
					beans = new ArrayList<>();
					continue;
				} else if (beans!=null) {
		
					String[] imgvalImgStrs = line.split("`", -1);
					ImgBean  bean = new ImgBean();
					bean.mName = key;
					bean.mode = imgvalImgStrs[0];
					bean.version = imgvalImgStrs[1];
					bean.imageA = imgvalImgStrs[2];
					bean.imageB = imgvalImgStrs[3];
					beans.add(bean);
				} 

			}
			bufferedReader.close();
		} catch (Exception e) {
			e.printStackTrace();
		}

		try {

			mServerSocket = new ServerSocket(Integer.parseInt(mConfigMap.get("remote_port")));
			Tool.log("2018-05-16  06:55 WisegaRemoteServer open sucess!  "+mConfigMap.get("remote_port"));
			while (true) {
				Socket socket = mServerSocket.accept();
				RemoteClient remoteClient = new RemoteClient(socket);
				remoteClient.setICallBack(new ICallBack() {

					@Override
					public void disconnect(String appMark) {
						Tool.log("disconnect ");
					}

					@Override
					public void connect(String name,String mode) {
						Object obj = mImgMap.get(name);
						if(obj==null)
						{
							remoteClient.close();
							return;
						}
						List<ImgBean> lists = (List<ImgBean>) obj;
						ImgBean tempBean = new ImgBean();
						tempBean.mode = mode;
						int index = lists.indexOf(tempBean);
						if(index>=0)
						{
							remoteClient.setBean(lists.get(index));
						}
						
					}
				});
				remoteClient.start();
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

}
