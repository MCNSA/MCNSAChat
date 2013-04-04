package com.mcnsa.chat.client.packets;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class TimeoutPacket  implements IPacket {
	public static final short id = 11;

	public String shortName = null;
	
	public TimeoutPacket() {
	}
	
	public TimeoutPacket(String shortName) {
		this.shortName = shortName;
	}

	public void write(DataOutputStream out) throws IOException {
		out.writeShort(id);
		out.writeUTF(shortName);
		out.flush();
	}

	public void read(DataInputStream in) throws IOException {
		shortName = in.readUTF();
	}
}
