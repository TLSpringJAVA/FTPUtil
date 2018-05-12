package com.anlu.ftp;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.SocketException;

import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPReply;

import net.coobird.thumbnailator.Thumbnails;

/**
 * FTP������
 * @author ����
 */
public class FTPUtils {
	private static FTPUtils instance = null;
    private static FTPClient ftpClient = null;
    private String cache_dir = "e:/cache/";//����ѹ��ͼƬ
    private String server = "192.168.2.236";
    private int port = 21;
    private String userName = "anlutest1";
    private String userPassword = "123";
    
    public static  void main(String[] args){
    	FTPUtils utils = FTPUtils.getInstance();
    	boolean isOpen=utils.open();
    	if(isOpen){
    		//utils.upload("ftpFile/data", "changeLog.txt", "D://changeLog.txt");
        	boolean result= utils.upload("changeLog.txt", "/test2", "D://changeLog.txt");
        	System.out.println("�ϴ����:"+result);
    	}
    	
    }

    public static FTPUtils getInstance(){
	   if(instance == null){
		   instance = new FTPUtils();
	   }
	   
	   ftpClient = new FTPClient();
	   return instance;
    }
   
   /**
    * ����FTP������
    * @return
    */
   private boolean connect(){
    	boolean stat = false;
    	try {
    		if(ftpClient.isConnected()){
    			return true;
    		}
    			
			ftpClient.connect(server, port);
			stat = true;
			System.out.println(server+":"+port+"���ӳɹ�");
		} catch (SocketException e) {
			stat = false;
			e.printStackTrace();
			System.out.println(server+":"+port+"����ʧ��");
		} catch (IOException e) {
			stat = false;
			e.printStackTrace();
			System.out.println(server+":"+port+"����ʧ��");
		}
    	return stat;
   }
   
   /**
    * ��FTP������
    * @return
    */
   public boolean open(){
	   if(!connect()){
		   return false;
	   }
	   
	   boolean stat = false;
	   try {
		   stat = ftpClient.login(userName, userPassword);
		   // ��������Ƿ�ɹ�
           int reply = ftpClient.getReplyCode();
           if (!FTPReply.isPositiveCompletion(reply)) {
        	   System.out.println("FPT��������¼ʧ��");
               close();
               stat = false;
           }else{
        	   System.out.println("FPT��������¼�ɹ�");
           }
	   } catch (IOException e) {
		   stat = false;
	   }
	   return stat;
   }
   
   
   /**
    * �ر�FTP������
    */
   public void close(){
	   try {
		   if(ftpClient != null){
			   if(ftpClient.isConnected()){  
				   ftpClient.logout();
				   ftpClient.disconnect();
			   }
			   
			   ftpClient = null;
		   }
	   } catch (IOException e) {
	   }
   }
   
   
   /**
    * �ϴ��ļ���FTP������
    * @param filename
    * @param path
    * @param input
    * @return
    */
   public boolean upload(String filename,String path,InputStream input){
	   boolean stat = false;
	   try {
		   cd(path);
	       ftpClient.setBufferSize(1024);   
	       ftpClient.setControlEncoding("GBK");   
	       ftpClient.setFileType(FTPClient.BINARY_FILE_TYPE);
	       stat = ftpClient.storeFile(filename, input);           
	       input.close();  //�ر�������
	   } catch (IOException e) {  
	       
	   }
	   return stat;
   }
   
   /**
    * �ϴ��ļ���FTP������
    * @param filename
    * @param path
    * @param input
    * @return
    */
   public boolean upload(String filename,String path,String filepath){
	   boolean stat = false;
	   try {
		   cd(path);
	       ftpClient.setBufferSize(1024);   
	       ftpClient.setControlEncoding("GBK");   
	       ftpClient.setFileType(FTPClient.BINARY_FILE_TYPE);
	       FileInputStream input = new FileInputStream(new File(filepath));
	       stat = ftpClient.storeFile(filename, input);           
	       input.close();  //�ر�������
	   } catch (IOException e) {  
	       e.printStackTrace();
	   }
	   return stat;
   }
   
   /**
    * �ϴ��ļ�
    * @param filename
    * @param path
    * @param file
    * @return
    */
   public boolean upload(String filename,String path,File file){
	   boolean stat = false;
	   try {
		   cd(path);
	       ftpClient.setBufferSize(1024);   
	       ftpClient.setControlEncoding("GBK");   
	       ftpClient.setFileType(FTPClient.BINARY_FILE_TYPE);
	       FileInputStream input = new FileInputStream(file);
	       stat = ftpClient.storeFile(filename,input);           
	       input.close();  //�ر�������
	   } catch (IOException e) {  
	       
	   }
	   return stat;
   }
   
   /**
    * �ϴ�ͼƬ�Զ�ѹ������
    * @param path
    * @param filename
    * @param input
    * @param quality
    * @param maxWidth
    * @param maxHeight
    * @return
    */
   public boolean uploadImage(String path,String filename,InputStream input,float quality,int maxWidth,int maxHeight){
	   boolean stat = false;
	   try {
		   String suffex = filename.substring(filename.lastIndexOf(".")+1, filename.length());
		   System.out.println(suffex);
		   File imagePath = new File(cache_dir + filename);
		   Thumbnails.of(input).outputQuality(quality).size(maxWidth,maxHeight).toFile(imagePath);
		   input.close();
		   
		   if(!imagePath.exists()){
			   return false;
		   }
		   
		   cd(path);  
	       ftpClient.setBufferSize(1024);   
	       ftpClient.setControlEncoding("GBK");   
	       ftpClient.setFileType(FTPClient.BINARY_FILE_TYPE);
	       FileInputStream input2 = new FileInputStream(imagePath);
	       stat = ftpClient.storeFile(filename,input2);           
	       input2.close();//�ر�������
	       imagePath.delete();
		   stat = true;
	   } catch (IOException e) {  
		   e.printStackTrace();
		   stat = false;
	   }
	   return stat;
   }
   
   
   /**
    * ѭ���л�Ŀ¼
    * @param dir
    * @return
    */
   public boolean cd(String dir){
	   boolean stat = true;
	try {
		   String[] dirs = dir.split("/");
		   if(dirs.length == 0){
			   return ftpClient.changeWorkingDirectory(dir);
		   }
		   
		   stat = ftpClient.changeToParentDirectory();
			for(String dirss : dirs){
				stat = stat && ftpClient.changeWorkingDirectory(dirss);
			}
			
			stat = true;
	   } catch (IOException e) {
			stat = false;
	   }
	   return stat;
   }
   
   /***
    * ����Ŀ¼
    * @param dir
    * @return
    */
   public boolean mkdir(String dir){
	   boolean stat = false;
	   try {
		   ftpClient.changeToParentDirectory();
		   ftpClient.makeDirectory(dir);
		   stat = true;
	   } catch (IOException e) {
			stat = false;
	   }
	   return stat;
   }
   
   /***
    * ��������㼶Ŀ¼
    * @param dir dong/zzz/ddd/ewv
    * @return
    */
   public boolean mkdirs(String dir){
	   String[] dirs = dir.split("/");
	   if(dirs.length == 0){
		   return false;
	   }
	   boolean stat = false;
	   try {
		   ftpClient.changeToParentDirectory();
			for(String dirss : dirs){
				ftpClient.makeDirectory(dirss);
				ftpClient.changeWorkingDirectory(dirss);
			}
			
			ftpClient.changeToParentDirectory();
			stat = true;
	   } catch (IOException e) {
			stat = false;
	   }
	   return stat;
   }
   
   /**
    * ɾ���ļ���
    * @param pathname
    * @return
    */
   public boolean rmdir(String pathname){
	   try{
		   return ftpClient.removeDirectory(pathname);
	   }catch(Exception e){
		   return false;
	   }
   }
   
   /**
    * ɾ���ļ�
    * @param pathname
    * @return
    */
   public boolean remove(String pathname){
	   boolean stat = false;
	   try{
		   stat = ftpClient.deleteFile(pathname); 
	   }catch(Exception e){
		   stat = false;
	   }
	   return stat;
   }
   
   /**
    * �ƶ��ļ����ļ���
    * @param pathname1
    * @param pathname2
    * @return
    */
   public boolean rename(String pathname1,String pathname2){
	    try {
	    	return ftpClient.rename(pathname1, pathname2);
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
   }
   
}