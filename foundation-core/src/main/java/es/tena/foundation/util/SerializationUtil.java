package es.tena.foundation.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;

/**
 * 
 * @author Francisco Tena<francisco.tena@gmail.com>
 */
public class SerializationUtil {

	public static Serializable getSerializableFromBase64WithUrlDecode(String code) {
		try {
			return getSerializableFromBase64(URLDecoder.decode(code,"UTF-8"));
		} catch (UnsupportedEncodingException e) {
			throw new RuntimeException("Impossible to serialize " + code, e);
		}
	}

	public static Serializable getSerializableFromBase64(String b64){
		try {
			byte[] bytes = Base64.decode(b64);
			ByteArrayInputStream bis = new ByteArrayInputStream(bytes);
                        Serializable obj;
                    try (ObjectInputStream ois = new ObjectInputStream(bis)) {
                        obj = (Serializable)ois.readObject();
                    }
			return obj;
		} catch (IOException | ClassNotFoundException e) {
			throw new RuntimeException("Impossible to serialize " + b64, e);
		}
	}
	
	public static String getBase64WithUrlEncodeFromSerializable(Serializable obj) {
		try {
			return URLEncoder.encode(getBase64FromSerializable(obj),"UTF-8");
		} catch (UnsupportedEncodingException e) {
			throw new RuntimeException("Impossible to serialize " + obj,e);
		}
	}
	
	public static String getBase64FromSerializable(Serializable obj) {
		try {
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			ObjectOutputStream oos = new ObjectOutputStream(bos);
			oos.writeObject(obj);
			oos.close();
			byte[] bytes = bos.toByteArray();
			return Base64.encodeBytes(bytes);
		} catch (IOException e) {
			throw new RuntimeException("Impossible to serialize " + obj,e);
		}
	}

}
