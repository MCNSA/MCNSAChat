package com.mcnsa.chat.utilities;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class Slapi {
	public static <T> void save(T obj, String path)
		    throws Exception
		  {
		    ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(path));
		    oos.writeObject(obj);
		    oos.flush();
		    oos.close();
		  }

		  public static <T> Object load(String path) throws Exception {
		    ObjectInputStream ois = new ObjectInputStream(new FileInputStream(path));

		    Object result = ois.readObject();
		    ois.close();
		    return result;
		  }
}
