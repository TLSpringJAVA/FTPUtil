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
 * ftp ������ apache ֧��jdk6,��֧�� jdk7
 * 
 * @author dell
 *
 */
public class FtpUtil {
	// ��ʱʱ��
	private static final int FTP_TIME_OUT = 5000000;
	private static FTPClient ftp;

	/**
	 * ����ftp �����������ҵ�¼
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

			// �����ļ�����
			ftp.setFileType(FTP.BINARY_FILE_TYPE);
		} catch (SocketException e) {
			// e.printStackTrace();
			StringBuffer sbLog = new StringBuffer(60);
			sbLog.append("�ļ��ϴ����ӳ���SocketException�쳣=");
			sbLog.append(e.getMessage());

			return flag;
		} catch (Exception e) {
			e.printStackTrace();
			StringBuffer sbLog = new StringBuffer(60);
			sbLog.append("�ļ��ϴ����ӳ���Exception�쳣=");
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
				sbLog.append(";�˳�����");

			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		return flag;
	}

	/**
	 * �ϴ��ļ�
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
			sbLog.append("û�з����ļ��쳣=");
			sbLog.append(e.getMessage());

		} catch (IOException e) {
			StringBuffer sbLog = new StringBuffer(60);
			sbLog.append("û�з����ļ��쳣=");
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

	// �ر�ftp

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
		// ��ѯδ���н���Ķ���
		List list = new ArrayList();
		list.add("==================ddd");

		// �ļ�����
		String fileName = "changeLog.txt";

		boolean result = true;

		// �ϴ������ļ�
		if (result) {
			boolean ftpResult = FtpUtil.connect();
			if (ftpResult) {
				boolean ftpfile = FtpUtil.ftp("D:/", fileName);
				if (ftpfile) {
					StringBuffer log = new StringBuffer(60);
					log.append("���ļ��ɹ������ƣ�");
					log.append(fileName);
				} else {
					StringBuffer log = new StringBuffer(60);
					log.append("���ļ��ɹ������ƣ�");
					log.append(fileName);
				}
			}

		} else {
			StringBuffer log = new StringBuffer(60);
			log.append("���ɶ����ļ�ʱʧ�ܣ������ļ����ƣ�");
			log.append(fileName);
		}

	}

}
