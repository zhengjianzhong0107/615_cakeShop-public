package com.util;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Objects;
import java.util.Random;

import org.springframework.web.context.ContextLoader;
import org.springframework.web.multipart.MultipartFile;

/**
 * 上传工具类
 * spring mvn支持
 */
public class UploadUtil {
	

	/**
	 * 图片上传
	 * @return 返回相对路径
	 * @param photo 图片文件
	 * @param photoFileName 文件名
	 * @param savePath 文件保存路径(相对于web根目录)
	 * @return
	 * @throws Exception 
	 */
	public static String fileUpload(MultipartFile file) throws Exception{
		// 判断是否有上传文件
		if (Objects.isNull(file) || file.isEmpty() || Objects.isNull(file.getOriginalFilename())) {
			return null;
		}
		String savePath = "picture"; // 保存文件的相对目录
		String fileName = file.getOriginalFilename();
		// 文件存储路径
		String path = ContextLoader.getCurrentWebApplicationContext().getServletContext().getRealPath("/")+savePath;			
		// 获取当前文件类型
		String type = fileName.substring(fileName.lastIndexOf(".")+1, fileName.length());
		// 获取当前系统时间字符串 + 三位随机数  + 后缀名
		String newFileName = new SimpleDateFormat("yyMMddssSSS").format(new Date()) + new Random().nextInt(899)+100 + "." + type;
		// 按指定路径重命名构建文件
		File savefile = new File(path,newFileName);
		// 若保存文件的文件夹不存在则创建
		if(!savefile.getParentFile().exists()){
			savefile.getParentFile().mkdirs();
		}
		System.out.println("上传文件绝对路径: "+savefile.getPath());
		file.transferTo(savefile);
		return savePath+"/"+newFileName;
	}

}
