package com.wisega.tool;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * ����Э�飺ͷ ���� ���� A5 ���� ���� APPӦ����Ϣ�� A5 03 01 ......... ift7 APP��ȡ����OTA��Ϣ��A5 03 02
 * APP��ȡ����OTA�ļ���A5 03 03 a/b
 */
public class RemoteClient extends Thread {
	private Socket mSocket;
	private InputStream mInputStream;
	private OutputStream mOutputStream;
	private ICallBack mICallback;
	private SendFileTask mSendFile;
	private String mName = "";
	private String mMode = "";
    private ImgBean mImgBean = new ImgBean();
	public RemoteClient(Socket socket) {
		mSocket = socket;
	}

	public String getmName() {
		return mName;
	}

	public String getmMode() {
		return mMode;
	}

	public void setICallBack(ICallBack icCallBack) {
		mICallback = icCallBack;
	}

	
    public void setBean(ImgBean bean)
    {
    	mImgBean = bean;
    }
	@Override
	public void run() {

		try {
			mInputStream = mSocket.getInputStream();
			mOutputStream = mSocket.getOutputStream();
			byte[] buff = new byte[4096];
			int len = 0;

			while ((len = mInputStream.read(buff)) > 0) {
				if (!handle(Arrays.copyOf(buff, len)))
					break;

			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (mInputStream != null) {
					mInputStream.close();
				}
				if (mOutputStream != null) {
					mOutputStream.close();
				}
				if (mSocket != null) {
					mSocket.close();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			mICallback.disconnect(mImgBean.mode);

		}

	}

	public boolean writeClient(byte[] data) {
		try {

			if (mOutputStream == null) {
				return false;
			}
			if (data.length < 100)
				Tool.log("send:" + Hex.toString(data));
			mOutputStream.write(data);
			mOutputStream.flush();
		} catch (Exception e) {
			e.printStackTrace();
		}

		return true;
	}

	private boolean handle(byte[] data) {
		Tool.log("recFromClient:" + Hex.toString(data));
		int dataLen = 0;
		if (data[0] == (byte) 0xa5) {
			if (data.length < 6 || ((dataLen = Hex.toIntB(Arrays.copyOfRange(data, 1, 5))) < data.length)) {
				Tool.log("handle data length < 6:" + dataLen);

				return false;
			}
			if (data[5] == (byte) 0x01)// �ͻ��˷�������APP���
			{
				String mAPPUser = new String(Arrays.copyOfRange(data, 6, dataLen));
				mName = mAPPUser.substring(0, mAPPUser.indexOf("~"));
				mMode = mAPPUser.substring(mAPPUser.indexOf("~") + 1, mAPPUser.length());
				mICallback.connect(mName,mMode);
				System.out.println("name=" + mName + ",mode=" + mMode);
				
				
				
			} else if (data[5] == (byte) 0x02)// �ͻ��˷�������ȡOTA�ļ�
			{

				writeClient(Tool.buildBytes((byte) 0xa5, (byte) 0x02, mImgBean.version.getBytes()));

			} else if (data[5] == (byte) 0x03)// �ͻ���Ҫ��ȡOTA�ļ���
			{

				if (data[6] == (byte) 0x01)// �ͻ��˽��ձ�־λ�Ѿ���λ
				{
					if (mSendFile != null) {
						mSendFile.start();
					}
				} else {
					String wantImgType = new String(Arrays.copyOfRange(data, 6, dataLen));
					Tool.log(mName + " wantImgType: " + wantImgType);
					List<File> otafiles = Tool.getFiles("./APPUSER/"+mImgBean.mName+"/"+mImgBean.mode);
					int len = 0;
					for(File file:otafiles)
					{
						if(file.getName().contains(wantImgType))
						{
							len = (int)file.length();
							mSendFile = new SendFileTask(this, file);
							break;
						}
					}
					
                    if(len==0)
                    {
                    	//�����Ҳ����ļ�ָ��
                    	return false;
                    }
					
					writeClient(Tool.buildBytes((byte) 0xa5, (byte) 0x03, new byte[] { (byte) 0x01 },
							Hex.fromIntB(len)));// ���пͻ���׼���ñ�־λ��Ҫ�����ļ��ˣ�ͨ�����ļ�����ռ��
				}

			}

		}
		return true;
	}
   public void close()
   {
	   if(mSocket!=null)
	   {
		   try {
			mSocket.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	   }
   }
	public interface ICallBack {
		void disconnect(String appMark);

		void connect(String name,String mode);
		
	}

}
