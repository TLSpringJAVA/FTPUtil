package com.anlu.ftp;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPReply;

/**
 * ftp 工具类 apache 支持jdk6,不支持 jdk7
 * 
 * @author dell
 *
 */
public class FtpUtil {
	// 超时时间
	private static final int FTP_TIME_OUT = 5000000;
	private static FTPClient ftp;

	/**
	 * 连接ftp 服务器，并且登录
	 * 
	 * @return
	 */
	public static boolean connect() {
		boolean flag = false;
		ftp = new FTPClient();
		try {
			String serverIp = "192.168.2.236";
			int port = 21;
			String user = "anlutest";
			String pwd = "123";

			// ftp.setDefaultTimeout(FTP_TIME_OUT);
			// ftp.setDataTimeout(FTP_TIME_OUT);
			ftp.connect(serverIp, port);
			flag = ftp.login(user, pwd);

			// 传送文件类型
			ftp.setFileType(FTP.BINARY_FILE_TYPE);
		} catch (SocketException e) {
			// e.printStackTrace();
			StringBuffer sbLog = new StringBuffer(60);
			sbLog.append("文件上传连接出现SocketException异常=");
			sbLog.append(e.getMessage());

			return flag;
		} catch (Exception e) {
			e.printStackTrace();
			StringBuffer sbLog = new StringBuffer(60);
			sbLog.append("文件上传连接出现Exception异常=");
			sbLog.append(e.getMessage());

			return flag;
		}

		int reply = 0;
		try {
			reply = ftp.getReplyCode();
			System.out.println("code=" + reply);
			if (!FTPReply.isPositiveCompletion(reply)) {
				ftp.disconnect();

				StringBuffer sbLog = new StringBuffer(60);
				sbLog.append("FTP server refused connection.");
				sbLog.append(";退出服务");

			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		return flag;
	}

	/**
	 * 上传文件
	 * 
	 * @param path
	 * @param fileName
	 * @return
	 */
	public static boolean ftp(final String path, final String fileName) {
		InputStream local = null;
		boolean result = false;

		try {
			local = new FileInputStream(path + File.separator + fileName);
			String serverPath = "";
			result = ftp.storeFile(serverPath + fileName, local);

		} catch (FileNotFoundException e) {
			StringBuffer sbLog = new StringBuffer(60);
			sbLog.append("没有发现文件异常=");
			sbLog.append(e.getMessage());

		} catch (IOException e) {
			StringBuffer sbLog = new StringBuffer(60);
			sbLog.append("没有发现文件异常=");
			sbLog.append(e.getMessage());

		} finally {
			if (null != local) {
				try {
					local.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}

			close();
		}

		return result;
	}

	// 关闭ftp

	public static void close() {
		if (ftp != null && ftp.isConnected()) {
			try {
				ftp.logout();
				ftp.disconnect();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

	}

	public static void main(String[] args) {

		// boolean result = FtpUtil.connect();
		// if(result){
		// String
		// path=FtpResource.getInstane().getStrKey(FtpResource.CLIENT_FILE_PATH);
		// System.out.println(FtpUtil.ftp(path,"noinfo.txt"));
		// }

		// OrderBO bo = new OrderBO();
		// 查询未进行结算的订单
		List list = new ArrayList();
		list.add("==================ddd");

		// 文件名称
		String fileName = "changeLog.txt";

		boolean result = true;

		// 上传对账文件
		if (result) {
			boolean ftpResult = FtpUtil.connect();
			if (ftpResult) {
				boolean ftpfile = FtpUtil.ftp("D:/", fileName);
				if (ftpfile) {
					StringBuffer log = new StringBuffer(60);
					log.append("上文件成功，名称：");
					log.append(fileName);
				} else {
					StringBuffer log = new StringBuffer(60);
					log.append("上文件成功，名称：");
					log.append(fileName);
				}
			}

		} else {
			StringBuffer log = new StringBuffer(60);
			log.append("生成对账文件时失败，对账文件名称：");
			log.append(fileName);
		}

	}

}
