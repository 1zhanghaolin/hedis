package com.haolin.hedis;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.Socket;

import com.haolin.hedis.Protocol.Command;

/**
 * 
 * @Description: 传输层/API层
 * @author zhanghaolin
 * @date 2019年5月8日 上午9:40:47
 */
public class Hedis {

	private final String  DEFAULT_HOST 	= "127.0.0.1";
	private final Integer DEFAULT_PORT 	= 6379;
	
	private Socket 						connection;

	private String 						host;

	private Integer 					port;
	
	private InputStream 				inputStream;
	
	private OutputStream 				outputStream;

	public Hedis() {
		this.host = DEFAULT_HOST;
		this.port = DEFAULT_PORT;

		connect();
	}
	
	public Hedis(String host, Integer port) {
		this.host = host;
		this.port = port;

		connect();
	}

	public void set(String key, String value) {
		Protocol.sendMessage(outputStream, Command.SET, SafeEncoder.castValue(key), SafeEncoder.castValue(value));
	}
	
	public String get(String key) {
		Protocol.sendMessage(outputStream, Command.GET, SafeEncoder.castValue(key));
		String replyMessage = Protocol.getReplyMessage(inputStream);
		
		String temp1 = replyMessage.substring(replyMessage.indexOf(Protocol.LINEFLAG), replyMessage.lastIndexOf(Protocol.LINEFLAG));
		String temp2 = temp1.substring(replyMessage.indexOf(Protocol.LINEFLAG));
		String temp3 = temp2.substring(replyMessage.indexOf(Protocol.LINEFLAG)).replace(Protocol.LINEFLAG, Protocol.EMPTY);
		return temp3;
	}
	
	public String incr(String key) {
		Protocol.sendMessage(outputStream, Command.INCR, SafeEncoder.castValue(key));
		String replyMessage = Protocol.getReplyMessage(inputStream);
		return replyMessage.substring(replyMessage.indexOf(Protocol.MAOHAO), replyMessage.lastIndexOf(Protocol.LINEFLAG)).replace(Protocol.MAOHAO, Protocol.EMPTY);
	}
	
	public String decr(String key) {
		Protocol.sendMessage(outputStream, Command.DECR, SafeEncoder.castValue(key));
		String replyMessage = Protocol.getReplyMessage(inputStream);
		return replyMessage.substring(replyMessage.indexOf(Protocol.MAOHAO), replyMessage.lastIndexOf(Protocol.LINEFLAG)).replace(Protocol.MAOHAO, Protocol.EMPTY);
	}
	
	public void del(String key) {
		Protocol.sendMessage(outputStream, Command.DEL, SafeEncoder.castValue(key));
	}
	
	private void connect() {
		if (!isConnected()) {
			try {
				connection = new Socket(host, port);
				inputStream = connection.getInputStream();
				outputStream = connection.getOutputStream();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	private boolean isConnected() {
		return connection != null && connection.isBound() && !connection.isClosed() && connection.isConnected()
				&& !connection.isInputShutdown() && !connection.isOutputShutdown();
	}

	public void close() {
		if (isConnected()) {
			try {
				outputStream.flush();
				connection.close();
			} catch (IOException ex) {
				ex.printStackTrace();
			} finally {
				finallyClose(connection);
			}
		}
	}

	private void finallyClose(Socket connection) {
		if (connection != null) {
			try {
				connection.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public static class SafeEncoder {
		
		public static byte[] castValue(String value) {
			if (value == null)
				value = Protocol.EMPTY;
			return value.getBytes();
		}
		
		public static String castValue(final byte[] bs) {
			try {
				return new String(bs, Protocol.CHARSET);
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
			return Protocol.EMPTY;
		}

	}
	
	// Setter Getter
	public String getHost() {
		return host;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public Integer getPort() {
		return port;
	}

	public void setPort(Integer port) {
		this.port = port;
	}

}
