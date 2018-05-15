package com.wisega.tool;

import java.util.ArrayList;


public class ByteArrayList extends ArrayList<byte[]> {
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public ByteArrayList() {
    }

    public ByteArrayList(byte[] bytes) {
        this.add(bytes);
    }

    public ByteArrayList(byte b) {
        this.add(b);
    }

    public void add(byte b) {
        this.add(new byte[]{b});
    }

    //������list�����Ԫ��������ϳ�byte����
    public byte[] all2Bytes() {
        ArrayList<Byte> list = new ArrayList<Byte>();
        for (int i = 0; i < this.size(); i++) {
            for (int i1 = 0; i1 < this.get(i).length; i1++) {
                list.add(this.get(i)[i1]);
            }
        }
        byte[] rBytes = new byte[list.size()];
        for (int i = 0; i < rBytes.length; i++) {
            rBytes[i] = list.get(i);
        }
        return rBytes;
    }


}
