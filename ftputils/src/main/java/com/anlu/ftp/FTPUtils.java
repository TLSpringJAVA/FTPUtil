package com.anlu.ftp;

import java.io.*;
import java.net.SocketException;
import java.util.ArrayList;

import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPReply;
import org.apache.commons.net.ftp.FTPFile;

import net.coobird.thumbnailator.Thumbnails;

/**
 * FTP工具类
 * @author 繁华
 */
public class FTPUtils {
	private static FTPUtils instance = null;
    private static FTPClient ftpClient = null;
    private String cache_dir = "e:/cache/";//用于压缩图片
	public ArrayList<String> arFiles = new ArrayList<>();//用于列出某目录的所有文件
    private String server = "192.168.2.236";
    private int port = 21;
    private String userName = "anlutest1";
    private String userPassword = "123";
	
	public void setServer(String server) {
        this.server = server;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public void setUserPassword(String userPassword) {
        this.userPassword = userPassword;
    }
    
    public static  void main(String[] args){
    	FTPUtils utils = FTPUtils.getInstance();
    	boolean isOpen=utils.open();
    	if(isOpen){
    		//utils.upload("ftpFile/data", "changeLog.txt", "D://changeLog.txt");
        	boolean result= utils.upload("changeLog.txt", "/test2", "D://changeLog.txt");
        	System.out.println("上传结果:"+result);
			
			utils.downDirFiles("/exam/","D:/1","dcm","jpg");
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
    * 连接FTP服务器
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
			System.out.println(server+":"+port+"连接成功");
		} catch (SocketException e) {
			stat = false;
			e.printStackTrace();
			System.out.println(server+":"+port+"连接失败");
		} catch (IOException e) {
			stat = false;
			e.printStackTrace();
			System.out.println(server+":"+port+"连接失败");
		}
    	return stat;
   }
   
   /**
    * 打开FTP服务器
    * @return
    */
   public boolean open(){
	   if(!connect()){
		   return false;
	   }
	   
	   boolean stat = false;
	   try {
		   stat = ftpClient.login(userName, userPassword);
		   // 检测连接是否成功
           int reply = ftpClient.getReplyCode();
           if (!FTPReply.isPositiveCompletion(reply)) {
        	   System.out.println("FPT服务器登录失败");
               close();
               stat = false;
           }else{
        	   System.out.println("FPT服务器登录成功");
           }
	   } catch (IOException e) {
		   stat = false;
	   }
	   return stat;
   }
   
   
   /**
    * 关闭FTP服务器
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
     * 下载单文件
     * @param pathName ftp完整文件名
     * @param localPath 下载到本地目标目录
     * @throws FileNotFoundException
     */
    public void downFile(String pathName,String localPath) throws IOException {
        File localFile = new File(localPath+pathName);
        File parent = localFile.getParentFile(); // 获取父文件

        if( !parent.exists() ) parent.mkdirs(); //创建所有父文件夹
            localFile.createNewFile();
            OutputStream os = new FileOutputStream(localFile);
            ftpClient.retrieveFile(new String(pathName.getBytes(),"ISO-8859-1"),os);
            System.out.println("文件："+localPath+pathName+" 下载成功");
            os.close();

    }
	
	/**
     * 下载指定目录下所有文件
     * @param dirName 开头结尾为“/”的完整ftp路径
     * @param localPath 下载到本地目标路径
     * @param ext 文件扩展名
     */
    public void downDirFiles(String dirName,String localPath,String... ext) throws IOException {
        this.arFiles.clear();
        List(dirName,ext);
        for(String pathName : arFiles){
            downFile(pathName,localPath);
        }
    }
   
   /**
    * 上传文件到FTP服务器
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
	       input.close();  //关闭输入流
	   } catch (IOException e) {  
	       
	   }
	   return stat;
   }
   
   /**
    * 上传文件到FTP服务器
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
	       input.close();  //关闭输入流
	   } catch (IOException e) {  
	       e.printStackTrace();
	   }
	   return stat;
   }
   
   /**
    * 上传文件
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
	       input.close();  //关闭输入流
	   } catch (IOException e) {  
	       
	   }
	   return stat;
   }
   
   /**
    * 上传图片自动压缩处理
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
	       input2.close();//关闭输入流
	       imagePath.delete();
		   stat = true;
	   } catch (IOException e) {  
		   e.printStackTrace();
		   stat = false;
	   }
	   return stat;
   }
   
   
   /**
    * 循环切换目录
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
    * 创建目录
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
    * 创建多个层级目录
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
    * 删除文件夹
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
    * 删除文件
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
    * 移动文件或文件夹
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
   
    /**
     * 递归遍历目录下面指定的文件名
     *
     * @param pathName 需要遍历的目录，必须以"/"开始和结束，调用前执行下 arFiles.clear()
     * @param ext      文件的扩展名
     * @throws IOException
     */
    public void List(String pathName, String... ext) throws IOException {
        if (pathName.startsWith("/") && pathName.endsWith("/")) {
            //更换目录到当前目录
            this.ftpClient.changeWorkingDirectory(pathName);
            FTPFile[] files = this.ftpClient.listFiles();
            for (FTPFile file : files) {
                if (file.isFile()) {
                    for(int i=0;i < ext.length;i++){
                        if (file.getName().endsWith(ext[i])) {
                            arFiles.add(pathName + file.getName());
                        }
                    }
                } else if (file.isDirectory()) {
                    if (!".".equals(file.getName()) && !"..".equals(file.getName())) {
                        List(pathName + file.getName() + "/", ext);
                    }
                }
            }
        }
    }
   
}