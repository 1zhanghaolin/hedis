package com.haolin.hedis;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import com.haolin.hedis.Hedis.SafeEncoder;

/**
 * 
 * @Description: 协议层
 * @author zhanghaolin
 * @date 2019年5月8日 上午9:41:26
 */
public class Protocol {

	public static final String CHARSET  			= "UTF-8";
	
	public static final String HEAD					= "*";
	public static final String DOLLARS 				= "$";
	public static final String MAOHAO 				= ":";
	public static final String LINEFLAG 			= "\r\n";
	public static final String EMPTY				= "";

	public enum Command {
		SET, GET, INCR, DECR, HSET, DEL
	}

	public static void sendMessage(OutputStream outputStream, Command commond, byte[]... bs) {
		
		String msg = buildSendMessage(commond, bs);
		try {
			outputStream.write(SafeEncoder.castValue(msg));
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	
	public static String getReplyMessage(InputStream inputStream) {
		
		byte[] reply = new byte[1024];
		try {
			inputStream.read(reply);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return new String(reply);
	}
	
	public static String buildSendMessage(Command commond, byte[]... bs) {
		
		StringBuilder msg = new StringBuilder();
		msg.append(HEAD).append(bs.length + 1).append(LINEFLAG);
		msg.append(DOLLARS).append(commond.toString().length()).append(LINEFLAG);
		msg.append(commond).append(LINEFLAG);
		for (int i = 0; i < bs.length; i++) {
			msg.append(DOLLARS).append(bs[i].length).append(LINEFLAG);
			msg.append(SafeEncoder.castValue(bs[i])).append(LINEFLAG);
		}
		return msg.toString();
		
	}

}