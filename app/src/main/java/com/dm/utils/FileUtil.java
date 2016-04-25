package com.dm.utils;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.io.Serializable;
import java.nio.channels.FileChannel;
import java.text.DecimalFormat;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;

import com.dm.ioc.IocContainer;
import com.dm.logger.Logger;

/**
 * 
 * @Description: 文件操作工具类
 * @author Damon.Han
 * @date 2016年1月18日 下午2:30:04
 */
public class FileUtil {
	private static final String TAG = FileUtil.class.getSimpleName();
	/** UTF-8 */
	public final static String UTF_8 = "UTF-8";
	/** GBK */
	public final static String GBK = "GBK";
	/** Unicode */
	public final static String UNICODE = "Unicode";

	/**
	 * 
	 * @Description: 获取指定的文件夹，如果此文件夹不存在则根据给定的文件夹名称在cache文件夹内新建
	 * @param name
	 *            -文件夹名称
	 * @return
	 * @return File
	 * @throw
	 */
	public static File getDir(String name) {
		File file = new File(getDir().getAbsolutePath() + "/" + name);
		if (!file.exists()) {
			file.mkdirs();
		}
		return file;
	}

	/***
	 * 获取项目独有文件夹
	 * 
	 * @return
	 */
	public static File getDir() {
		Context context = IocContainer.getIocContainer().getApplicationContext();
		File dir = null;
		if ((!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED))) {
			dir = context.getCacheDir();
		} else {
			// 使用getExternalFilesDir避免4.2.2以上外置卡不可写问题
			dir = new File(context.getExternalFilesDir(null).getAbsolutePath());
		}
		dir.mkdirs();
		return dir;
	}

	/**
	 * 获取项目缓存文件夹
	 * 
	 * @return
	 */
	public static File getCacheDir() {
		Context context = IocContainer.getIocContainer().getApplicationContext();
		File file = context.getExternalCacheDir();
		if (!file.exists()) {
			file.mkdirs();
		}
		return file;
	}

	/**
	 * 获取项目使用过程中产生的图片文件夹(cache文件夹下)
	 * 
	 * @return
	 */
	public static File getImageDir() {
		File file = new File(getDir().getAbsolutePath() + "/image");
		file.mkdirs();
		return file;
	}

	/**
	 * 递归删除文件夹
	 * 
	 * @param dirf
	 */
	public static void deleteDir(File dirf) {
		if (dirf.isDirectory()) {
			File[] childs = dirf.listFiles();
			for (int i = 0; i < childs.length; i++) {
				deleteDir(childs[i]);
			}
		}
		dirf.delete();
	}

	/**
	 * uri装换文件
	 * 
	 * @param context
	 * @param uri
	 * @return
	 */
	public static File uriToFile(Activity context, Uri uri) {
		String[] proj = {MediaStore.Images.Media.DATA};
		Cursor actualimagecursor = context.managedQuery(uri, proj, null, null, null);
		int actual_image_column_index = actualimagecursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
		actualimagecursor.moveToFirst();
		String img_path = actualimagecursor.getString(actual_image_column_index);
		File file = new File(img_path);
		return file;
	}

	/**
	 * 写入文件
	 * 
	 * @param in
	 * @param file
	 */
	public static void write(InputStream in, File file) {
		if (file.exists()) {
			file.delete();
		}
		try {
			file.createNewFile();
			FileOutputStream out = new FileOutputStream(file);
			byte[] buffer = new byte[1024];
			while (in.read(buffer) > -1) {
				out.write(buffer);
			}
			out.flush();
			in.close();
			out.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 写入文件
	 * 
	 * @param in
	 * @param file
	 * @param append
	 *            -是否追加内容
	 */
	public static void write(String in, File file, boolean append) {
		if (file.exists()) {
			file.delete();
		}
		try {
			file.createNewFile();
			FileWriter fw = new FileWriter(file, append);
			fw.write(in);
			fw.flush();
			fw.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 读文件
	 * 
	 * @param file
	 * @return 字符串
	 */
	public static String read(File file) {
		if (!file.exists()) {
			return "";
		}
		FileReader reader = null;
		BufferedReader br = null;
		try {
			reader = new FileReader(file);
			br = new BufferedReader(reader);
			StringBuffer buffer = new StringBuffer();
			String s;
			while ((s = br.readLine()) != null) {
				buffer.append(s);
			}
			return buffer.toString();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (br != null) {
				try {
					br.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			if (reader != null) {
				try {
					reader.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		return "";
	}

	/**
	 * 用encoding格式读取并返回filePathAndName文件内容
	 * 
	 * @param filePathAndName
	 *            文件路径及名称例如：D:\\xxx.txt"
	 * @param encoding
	 *            编码格式
	 * @return String
	 */
	public static String readTxt(String filePathAndName, String encoding) throws IOException {
		encoding = encoding.trim();
		StringBuffer sb = new StringBuffer("");
		String st = "";
		BufferedReader br = null;
		try {
			FileInputStream fs = new FileInputStream(filePathAndName);
			InputStreamReader isr;
			if (encoding.equals("")) {
				isr = new InputStreamReader(fs);
			} else {
				isr = new InputStreamReader(fs, encoding);
			}
			br = new BufferedReader(isr);
			try {
				String data = "";
				while ((data = br.readLine()) != null) {
					sb.append(data);
				}
			} catch (Exception e) {
				sb.append(e.toString());
			}
			st = sb.toString();
			if (st != null && st.length() > 1)
				st = st.substring(0, st.length() - 1);
		} catch (IOException es) {
			st = "";
		} finally {
			if (br != null)
				br.close();
		}
		return st;
	}

	/**
	 * 创建名称为 folderPath的文件夹
	 * 
	 * @param folderPath
	 * @return String
	 */
	public static String createFolder(String folderPath) {
		String txt = folderPath;
		try {
			java.io.File myFilePath = new java.io.File(txt);
			txt = folderPath;
			if (!myFilePath.exists()) {
				myFilePath.mkdir();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return txt;
	}

	/**
	 * 创建filePathAndName文件，并且写入fileContent文件内容
	 * 
	 * @param filePathAndName
	 *            文件路径及名称例如：D:\\xxx.txt"
	 * @param fileContent
	 *            文件内容
	 * @return
	 */
	public static void createFile(String filePathAndName, String fileContent) {
		try {
			String filePath = filePathAndName;
			filePath = filePath.toString();
			File myFilePath = new File(filePath);
			if (!myFilePath.exists()) {
				myFilePath.createNewFile();
			}
			FileWriter resultFile = new FileWriter(myFilePath);
			PrintWriter myFile = new PrintWriter(resultFile);
			String strContent = fileContent;
			myFile.println(strContent);
			myFile.close();
			resultFile.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 创建filePathAndName文件，并且用encoding字符格式写入fileContent内容
	 * 
	 * @param filePathAndName
	 *            文件路径及名称
	 * @param fileContent
	 *            文件内容
	 * @param encoding
	 *            编码格式：例如：GBK 或 UTF-8
	 * @return
	 */
	public static void createFile(String filePathAndName, String fileContent, String encoding) {
		try {
			String filePath = filePathAndName;
			filePath = filePath.toString();
			File myFilePath = new File(filePath);
			if (!myFilePath.exists()) {
				myFilePath.createNewFile();
			}
			PrintWriter myFile = new PrintWriter(myFilePath, encoding);
			String strContent = fileContent;
			myFile.println(strContent);
			myFile.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 删除文件
	 * 
	 * @param filePathAndName
	 *            文件路径及名称
	 * @return Boolean
	 */
	public static boolean delFile(String filePathAndName) {
		boolean bea = false;
		try {
			String filePath = filePathAndName;
			File myDelFile = new File(filePath);
			if (myDelFile.exists()) {
				myDelFile.delete();
				bea = true;
			} else {
				bea = false;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return bea;
	}

	/**
	 * 删除文件夹
	 * 
	 * @param folderPath
	 *            文件路径
	 * @return
	 */
	public static void delFolder(String folderPath) {
		try {
			delAllFile(folderPath);
			String filePath = folderPath;
			filePath = filePath.toString();
			File myFilePath = new File(filePath);
			myFilePath.delete();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 删除所有文件
	 * 
	 * @param path
	 *            路径
	 * @return
	 */
	public static boolean delAllFile(String path) {
		boolean bea = false;
		File file = new File(path);
		if (!file.exists()) {
			return bea;
		}
		if (!file.isDirectory()) {
			return bea;
		}
		String[] tempList = file.list();
		File temp = null;
		for (int i = 0; i < tempList.length; i++) {
			if (path.endsWith(File.separator)) {
				temp = new File(path + tempList[i]);
			} else {
				temp = new File(path + File.separator + tempList[i]);
			}
			if (temp.isFile()) {
				temp.delete();
			}
			if (temp.isDirectory()) {
				delAllFile(path + "/" + tempList[i]);
				delFolder(path + "/" + tempList[i]);
				bea = true;
			}
		}
		return bea;
	}

	/**
	 * 文件复制
	 * 
	 * @param oldPathFile
	 * @param newPathFile
	 * @return void
	 * @throws IOException
	 */
	public static void copyFile(String oldPathFile, String newPathFile) throws IOException {
		FileOutputStream fs = null;
		InputStream inStream = null;
		try {
			@SuppressWarnings("unused")
			int bytesum = 0;
			int byteread = 0;
			File oldfile = new File(oldPathFile);
			if (oldfile.exists()) {
				inStream = new FileInputStream(oldPathFile);
				fs = new FileOutputStream(newPathFile);
				byte[] buffer = new byte[1024];
				while ((byteread = inStream.read(buffer)) != -1) {
					bytesum += byteread;
					fs.write(buffer, 0, byteread);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (inStream != null)
				inStream.close();
			if (fs != null)
				fs.close();
		}
	}

	/**
	 * 文件夹复制
	 * 
	 * @param oldPath
	 * @param newPath
	 * @return
	 */
	public static void copyFolder(String oldPath, String newPath) {
		try {
			new File(newPath).mkdirs();
			File a = new File(oldPath);
			String[] file = a.list();
			File temp = null;
			for (int i = 0; i < file.length; i++) {
				if (oldPath.endsWith(File.separator)) {
					temp = new File(oldPath + file[i]);
				} else {
					temp = new File(oldPath + File.separator + file[i]);
				}
				if (temp.isFile()) {
					FileInputStream input = new FileInputStream(temp);
					FileOutputStream output = new FileOutputStream(newPath + "/" + (temp.getName()).toString());
					byte[] b = new byte[1024 * 5];
					int len;
					while ((len = input.read(b)) != -1) {
						output.write(b, 0, len);
					}
					output.flush();
					output.close();
					input.close();
				}
				if (temp.isDirectory()) {
					copyFolder(oldPath + "/" + file[i], newPath + "/" + file[i]);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 文件移动
	 * 
	 * @param oldPath
	 * @param newPath
	 * @return void
	 * @throws IOException
	 */
	public static void moveFile(String oldPath, String newPath) throws IOException {
		copyFile(oldPath, newPath);
		delFile(oldPath);
	}

	/**
	 * 文件夹移动
	 * 
	 * @param oldPath
	 * @param newPath
	 * @return
	 */
	public static void moveFolder(String oldPath, String newPath) {
		copyFolder(oldPath, newPath);
		delFolder(oldPath);
	}

	/**
	 * 重命名文件或文件夹
	 * 
	 * @param resFilePath
	 *            源文件路径
	 * @param newFileName
	 *            重命名
	 * @return 操作成功标识
	 */
	public static boolean renameFile(String resFilePath, String newFileName) {
		String newFilePath = formatPath(getParentPath(resFilePath) + "/" + newFileName);
		File resFile = new File(resFilePath);
		File newFile = new File(newFilePath);
		return resFile.renameTo(newFile);
	}

	/**
	 * 读取文件或者目录的大小
	 * 
	 * @param distFilePath
	 *            目标文件或者文件夹
	 * @return 文件或者目录的大小，如果获取失败，则返回-1
	 */
	public static long genFileSize(File distFile) {
		if (distFile == null) {
			return -1L;
		}
		if (distFile.isFile()) {
			return getSingleFileSizes(distFile);
		} else if (distFile.isDirectory()) {
			return FileUtil.getFolderFileSize(distFile);
		}
		return -1L;
	}

	/**
	 * // 取得单个文件大小
	 * 
	 * @param f
	 * @return
	 */
	public static long getSingleFileSizes(File f) {
		long s = 0;
		if (f.exists()) {
			FileInputStream fis = null;
			try {
				fis = new FileInputStream(f);
				s = fis.available();
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else {
			System.out.println("文件不存在");
		}
		return s;
	}

	// 递归
	// 取得文件夹大小
	public static long getFolderFileSize(File f) {
		long size = 0;
		File flist[] = f.listFiles();
		if (flist != null) {
			for (int i = 0; i < flist.length; i++) {
				if (flist[i].isDirectory()) {
					size = size + getFolderFileSize(flist[i]);
				} else {
					size = size + flist[i].length();
				}
			}
		}
		return size;
	}

	public static String FormetFileSize(long fileS) {// 转换文件大小
		DecimalFormat df = new DecimalFormat("#.00");
		String fileSizeString = "";
		if (fileS < 1024) {
			fileSizeString = df.format((double) fileS) + "B";
		} else if (fileS < 1048576) {
			fileSizeString = df.format((double) fileS / 1024) + "K";
		} else if (fileS < 1073741824) {
			fileSizeString = df.format((double) fileS / 1048576) + "M";
		} else {
			fileSizeString = df.format((double) fileS / 1073741824) + "G";
		}
		return fileSizeString;
	}

	/**
	 * 递归求取目录文件个数
	 * 
	 * @param f
	 * @return
	 */
	public static long getFilelistCount(File f) {
		long size = 0;
		File flist[] = f.listFiles();
		if (flist != null) {
			size = flist.length;
			for (int i = 0; i < flist.length; i++) {
				if (flist[i].isDirectory()) {
					size = size + getFilelistCount(flist[i]);
					size--;
				}
			}
		}
		return size;

	}

	/**
	 * 判断一个文件是否存在
	 * 
	 * @param filePath
	 *            文件路径
	 * @return 存在返回true，否则返回false
	 */
	public static boolean isExist(String filePath) {
		return new File(filePath).exists();
	}

	/**
	 * 为fileDir目录下fileName文件，返回写入管道对象（BufferedWriter）
	 * 
	 * @param fileDir
	 * @param fileName
	 * @return BufferedWriter
	 */
	public static BufferedWriter getWriter(String fileDir, String fileName) {
		try {
			File f1 = new File(fileDir);
			if (!f1.exists()) {
				f1.mkdirs();
			}
			f1 = new File(fileDir, fileName);
			if (!f1.exists()) {
				f1.createNewFile();
			}
			BufferedWriter bw = new BufferedWriter(new FileWriter(f1.getPath(), true));
			return bw;
		} catch (Exception e) {
			System.out.println(e.getLocalizedMessage());
			return null;
		}
	}

	/**
	 * 用encoding字符格式读取fileDir目录下fileName文件，返回读取文件管道对象（BufferedReader）
	 * 
	 * @param fileDir
	 * @param fileName
	 * @param encoding
	 * @return
	 */
	public static BufferedReader getReader(String fileDir, String fileName, String encoding) {
		try {
			File file = new File(fileDir, fileName);
			InputStreamReader read = new InputStreamReader(new FileInputStream(file), encoding);
			BufferedReader br = new BufferedReader(read);
			return br;
		} catch (FileNotFoundException ex) {
			ex.printStackTrace();
			return null;
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * 获取文件父路径
	 * 
	 * @param path
	 *            文件路径
	 * @return 文件父路径
	 */
	public static String getParentPath(String path) {
		return new File(path).getParent();
	}

	/**
	 * 获取相对路径
	 * 
	 * @param fullPath
	 *            全路径
	 * @param rootPath
	 *            根路径
	 * @return 相对根路径的相对路径
	 */
	public static String getRelativeRootPath(String fullPath, String rootPath) {
		String relativeRootPath = null;
		String _fullPath = formatPath(fullPath);
		String _rootPath = formatPath(rootPath);

		if (_fullPath.startsWith(_rootPath)) {
			relativeRootPath = fullPath.substring(_rootPath.length());
		} else {
			throw new RuntimeException("要处理的两个字符串没有包含关系，处理失败！");
		}
		if (relativeRootPath == null)
			return null;
		else
			return formatPath(relativeRootPath);
	}

	/**
	 * 格式化文件路径，将其中不规范的分隔转换为标准的分隔符,并且去掉末尾的"/"符号。
	 * 
	 * @param path文件路径
	 * @return 格式化后的文件路径
	 */
	public static String formatPath(String path) {
		String reg0 = "////＋";
		String reg = "////＋|/＋";
		String temp = path.trim().replaceAll(reg0, "/");
		temp = temp.replaceAll(reg, "/");
		if (temp.endsWith("/")) {
			temp = temp.substring(0, temp.length() - 1);
		}
		if (System.getProperty("file.separator").equals("//")) {
			temp = temp.replace("/", "//");
		}
		return temp;
	}

	/**
	 * 输入流转换成字节数组
	 * 
	 * @param in
	 *            ：输入流
	 * @return byte[] 字节
	 */
	public static byte[] readBytes(InputStream in) {
		BufferedInputStream bufin = new BufferedInputStream(in);
		int buffSize = 1024;
		ByteArrayOutputStream out = new ByteArrayOutputStream(buffSize);
		byte[] temp = new byte[buffSize];
		int size = 0;
		try {
			while ((size = bufin.read(temp)) != -1) {
				out.write(temp, 0, size);
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				bufin.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		byte[] content = out.toByteArray();
		return content;
	}

	/**
	 * MAIN
	 * 
	 * @param args
	 */
	public static void main(String args[]) {
		long startTime = System.currentTimeMillis();
		try {
			long l = 0;
			String path = "C://Windows";
			File ff = new File(path);
			if (ff.isDirectory()) { // 如果路径是文件夹的时候
				System.out.println("文件个数" + getFilelistCount(ff));
				System.out.println("目录");
				l = getFolderFileSize(ff);
				System.out.println(path + "目录的大小为：" + FormetFileSize(l));
			} else {
				l = getSingleFileSizes(ff);
				System.out.println(path + "文件的大小为：" + FormetFileSize(l));
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		long endTime = System.currentTimeMillis();
		System.out.println("总共花费时间为：" + (endTime - startTime) + "毫秒...");
	}

	/**
	 * 从Uri中获取path
	 * 
	 * @param context
	 * @param uri
	 * @return
	 */
	public static String getPath(Context context, Uri uri) {

		if ("content".equalsIgnoreCase(uri.getScheme())) {
			String[] projection = {"_data"};
			Cursor cursor = null;

			try {
				cursor = context.getContentResolver().query(uri, projection, null, null, null);
				int column_index = cursor.getColumnIndexOrThrow("_data");
				if (cursor.moveToFirst()) {
					return cursor.getString(column_index);
				}
			} catch (Exception e) {
				// Eat it
			}
		}

		else if ("file".equalsIgnoreCase(uri.getScheme())) {
			return uri.getPath();
		}

		return null;
	}

	public static void fileChannelCopy(File s, File t) {
		FileInputStream fi = null;
		FileOutputStream fo = null;
		try {
			fi = new FileInputStream(s);
			fo = new FileOutputStream(t);
			FileChannel in = fi.getChannel();// 得到对应的文件通道
			FileChannel out = fo.getChannel();// 得到对应的文件通道
			in.transferTo(0, in.size(), out);// 连接两个通道，并且从in通道读取，然后写入out通道
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (fo != null)
					fo.close();
				if (fi != null)
					fi.close();
			} catch (IOException e) {
				e.printStackTrace();
			}

		}
	}

	public static String formatFileSizeToString(long fileLen) {// 转换文件大小
		DecimalFormat df = new DecimalFormat("#.00");
		String fileSizeString = "";
		if (fileLen < 1024) {
			fileSizeString = df.format((double) fileLen) + "B";
		} else if (fileLen < 1048576) {
			fileSizeString = df.format((double) fileLen / 1024) + "K";
		} else if (fileLen < 1073741824) {
			fileSizeString = df.format((double) fileLen / 1048576) + "M";
		} else {
			fileSizeString = df.format((double) fileLen / 1073741824) + "G";
		}
		return fileSizeString;
	}

	/***
	 * 根据路径删除图片
	 */
	public static boolean deleteFile(File file) throws IOException {
		return file != null && file.delete();
	}

	/***
	 * 获取文件扩展名
	 * 
	 * @param filename
	 * @return 返回文件扩展名
	 */
	public static String getExtensionName(String filename) {
		if ((filename != null) && (filename.length() > 0)) {
			int dot = filename.lastIndexOf('.');
			if ((dot > -1) && (dot < (filename.length() - 1))) {
				return filename.substring(dot + 1);
			}
		}
		return filename;
	}

	/**
	 * 读取指定文件的输出
	 */
	public static String getFileOutputString(String path) {
		try {
			BufferedReader bufferedReader = new BufferedReader(new FileReader(path), 8192);
			StringBuilder sb = new StringBuilder();
			String line = null;
			while ((line = bufferedReader.readLine()) != null) {
				sb.append("\n").append(line);
			}
			bufferedReader.close();
			return sb.toString();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 
	 * @Description:读取asset中的文件
	 * @param file
	 * @param context
	 * @return
	 * @return String
	 * @throw
	 */
	public static String readAssetsFile(String file, Context context) {
		StringBuffer sb = new StringBuffer();
		try {
			InputStream is = context.getResources().getAssets().open(file);
			BufferedReader reader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
			String readLine = null;
			while ((readLine = reader.readLine()) != null) {
				sb.append(readLine);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

		return sb.toString();
	}

	public static String readFileToString(File file) {
		StringBuffer sb = new StringBuffer();
		try {
			InputStream is = new FileInputStream(file);
			BufferedReader reader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
			String readLine = null;
			while ((readLine = reader.readLine()) != null) {
				sb.append(readLine);
			}
			reader.close();
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}

		String content = sb.toString();
		Logger.d(TAG, String.format("read file's content = %s", content.length() >= 150 ? content.substring(0, 150) : content));
		return sb.toString();
	}

	public static byte[] readFileToBytes(File file) {
		try {
			return readStreamToBytes(new FileInputStream(file));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public static byte[] readStreamToBytes(InputStream in) throws Exception {
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		byte[] buffer = new byte[1024 * 8];
		int length = -1;
		while ((length = in.read(buffer)) != -1) {
			out.write(buffer, 0, length);
		}
		out.flush();
		byte[] result = out.toByteArray();
		in.close();
		out.close();
		return result;
	}

	public static boolean writeFile(File file, String content) {
		if (!file.getParentFile().exists()) {
			file.getParentFile().mkdirs();
		}
		FileOutputStream out = null;
		try {
			out = new FileOutputStream(file);
			out.write(content.getBytes());
			out.flush();
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		} finally {
			if (out != null)
				try {
					out.close();
				} catch (Exception e2) {
				}
		}

		return true;
	}

	public static boolean writeFile(File file, byte[] bytes) {
		if (!file.getParentFile().exists())
			file.getParentFile().mkdirs();

		FileOutputStream out = null;
		ByteArrayInputStream in = new ByteArrayInputStream(bytes);
		try {
			out = new FileOutputStream(file);

			byte[] buffer = new byte[1024 * 8];
			int length = -1;

			while ((length = in.read(buffer)) != -1) {
				out.write(buffer, 0, length);
			}
			out.flush();
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		} finally {
			if (out != null)
				try {
					out.close();
				} catch (Exception e2) {
				}
		}

		return true;
	}

	public static void copyFile(File sourceFile, File targetFile) throws Exception {
		FileInputStream in = new FileInputStream(sourceFile);
		byte[] buffer = new byte[128 * 1024];
		int len = -1;
		FileOutputStream out = new FileOutputStream(targetFile);
		while ((len = in.read(buffer)) != -1)
			out.write(buffer, 0, len);
		out.flush();
		out.close();
		in.close();
	}

	public static <T> T readObject(File file, Class<T> clazz) throws Exception {
		ObjectInputStream in = null;
		try {
			in = new ObjectInputStream(new FileInputStream(file));

			return (T) in.readObject();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (in != null)
				in.close();
		}

		return null;
	}

	public static void writeObject(File file, Serializable object) throws Exception {
		if (!file.getParentFile().exists())
			file.getParentFile().mkdirs();

		ObjectOutputStream out = null;
		try {
			out = new ObjectOutputStream(new FileOutputStream(file));

			out.writeObject(object);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			out.close();
		}
	}
}
