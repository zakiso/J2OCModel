package com.github.lazydeer.ocmodelgenerator;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by dzq on 2016/12/8.
 */

public class OCTranslator {

    //OC模型的前缀
    static String classPrefix = "GD";
    //公司名称 会显示到文件头部的Copyright信息中
    static String corporateName = "Shanghai HEADING Information Engineering Co., Ltd.";
    //显示到文件头部到元信息中
    static String schemeName = "TestProject";

    //model文件夹的路径  路径从项目根目录开始到 模型包的位置，可以有多个路径
    static String[] modelDirectories = new String[]{
            "app/src/main/java/com/github/lazydeer/ocmodelgenerator/models"
    };


    /**
     * 获取路径下的所有文件
     *
     * @param file           该file路径下的所有文件
     * @param resultFileName 结果
     * @return
     */
    private List<String> getAllFile(File file, List<String> resultFileName) {
        File[] files = file.listFiles();
        if (files == null) return resultFileName;

        for (File f : files) {
            if (f.isDirectory()) {
                resultFileName.add(f.getPath());
                getAllFile(f, resultFileName);
            } else
                resultFileName.add(f.getPath());
        }
        return resultFileName;
    }


    /**
     * 解析文件路径下的信息
     *
     * @param filesPath
     */
    private void parseFile(List<String> filesPath) {
        for (String path : filesPath) {
            if (path.endsWith("java")) {
                try {
                    String className = getClassName(path);
                    Class c = Class.forName(className);
                    if (c.isEnum()) {
                        continue;
                    }
                    parseClass(c);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * 解析class中的字段
     *
     * @param cls
     */
    private void parseClass(Class cls) {
        Field[] fields = cls.getDeclaredFields();
        //除了基本类型的其他类型先记录起来
        List<String> otherTypeList = new ArrayList<>();
        Map<String, String> fieldTypeMap = new HashMap<>();
        String parentType = "NSObject";
        String superClassName = cls.getSuperclass().getName();
        if (!superClassName.equals("java.lang.Object")) {
            int start = superClassName.lastIndexOf(".") + 1;
            parentType = classPrefix + superClassName.substring(start, superClassName.length());
        }
        for (Field field : fields) {
            String filedName = field.getName();
            Class type = field.getType();
            saveTypeToMap(type, fieldTypeMap, filedName, otherTypeList);
        }
        writeToFile(fieldTypeMap, otherTypeList, cls, parentType);
    }

    /**
     * 解析type的信息并存储在map中，方便后面写入到oc文件
     *
     * @param type
     * @param container
     * @param fieldName
     * @return
     */
    private void saveTypeToMap(Class type, Map<String, String> container,
                               String fieldName, List<String> otherTypeList) {
        String ocType;
        //判断java类的类型，并且对应到oc的类型
        if (type == int.class || type == Integer.class) {
            ocType = "NSInteger";
        } else if (type == long.class || type == Long.class) {
            ocType = "CGFloat";
        } else if (type == boolean.class || type == Boolean.class) {
            ocType = "BOOL";
        } else if (type == double.class || type == Double.class) {
            ocType = "CGFloat";
        } else if (type == float.class || type == Float.class || type == BigDecimal.class) {
            ocType = "CGFloat";
        } else if (type == String.class || type == Date.class || type.isEnum()) {
            ocType = "NSString";
        } else if (type == List.class) {
            ocType = "NSArray";
        } else if (type == Object.class) {
            ocType = "NSDictionary";
        } else {
            //如果不是基础类型则为其他的自定义类型
            ocType = type.getName();
            int index = ocType.lastIndexOf(".");
            String typeName = classPrefix + ocType.substring(index + 1, ocType.length());
            ocType = typeName;
            if (!otherTypeList.contains(typeName)) {
                otherTypeList.add(typeName);
            }
        }

        if (fieldName.equals("id")) {
            container.put("Id", ocType);
        } else if (fieldName.equals("Category")) {
            container.put("HDCategory", ocType);
        } else if (fieldName.equals("Description")) {
            container.put("HDDescription", ocType);
        } else {
            container.put(fieldName, ocType);
        }
    }


    /**
     * 把内容写入到文件
     *
     * @param fieldTypeMap
     * @param otherTypeList
     * @param cls
     * @param parentType
     */
    private void writeToFile(Map<String, String> fieldTypeMap, List<String> otherTypeList,
                             Class cls, String parentType) {

        int start = cls.getName().lastIndexOf(".") + 1;
        String fileName = cls.getName().substring(start, cls.getName().length());
        fileName = classPrefix + fileName;
        String path = createFile(cls, fileName);

        String hFileName = fileName + ".h";
        File hFile = new File(path + "/" + hFileName);
        writeHeaderComment(hFileName, hFile);
        writeHContent(hFile, fieldTypeMap, otherTypeList, parentType, fileName);


        String mFileName = fileName + ".m";
        File mFile = new File(path + "/" + mFileName);
        writeHeaderComment(mFileName, mFile);
        writeMContent(mFile, fileName, otherTypeList);
    }

    /**
     * 写入m文件的内容
     *
     * @param file
     * @param className
     * @param otherTypeList
     */
    private void writeMContent(File file, String className, List<String> otherTypeList) {
        //导入foundation
        String base = "#import \"" + className + ".h\"";


        String h1 = "@implementation " + className;
        String h2 = "@end";
        FileWriter fw = null;
        try {
            fw = new FileWriter(file, true);
            fw.write("\r\n");
            fw.write("\r\n");
            fw.write(base + "\r\n");
            //写入 所有的@class
            if (otherTypeList != null && otherTypeList.size() > 0) {
                for (String otherType : otherTypeList) {
                    String s = "#import \"" + otherType + ".h\"";
                    fw.write(s + "\r\n");
                }
            }

            fw.write("\r\n");
            fw.write(h1 + "\r\n");
            fw.write("\r\n");
            fw.write("\r\n");
            fw.write(h2 + "\r\n");
            fw.close();
        } catch (IOException e1) {
            e1.printStackTrace();
            System.out.println("写入失败");
        }
    }

    /**
     * 写入h文件的内容
     *
     * @param file          h文件
     * @param fieldTypeMap  hashMap  key是字段的名称value是字段的类型
     * @param otherTypeList list  里面装的是除了基本类型之外的类，在h文件中会@里面的类，在m文件中会import
     * @param parentType    父类
     * @param className
     */
    private void writeHContent(File file, Map<String, String> fieldTypeMap, List<String> otherTypeList,
                               String parentType, String className) {
        FileWriter fw = null;
        try {

            fw = new FileWriter(file, true);

            //导入foundation
            fw.write("\r\n");
            fw.write("\r\n");
            String base = "#import <Foundation/Foundation.h>";
            fw.write(base + "\r\n");
            fw.write("\r\n");

            if (!parentType.equals("NSObject")) {
                String imp = "#import \"" + parentType + ".h\"";
                fw.write(imp + "\r\n");
                fw.write("\r\n");
            }

            //写入 所有的@class
            if (otherTypeList != null && otherTypeList.size() > 0) {
                for (String otherType : otherTypeList) {
                    String s = "@class " + otherType + ";";
                    fw.write(s + "\r\n");
                }
            }
            fw.write("\r\n");
            fw.write("\r\n");
            String name = "@interface " + className + " : " + parentType;
            fw.write(name + "\r\n");

            String cgType = "NSInteger,CGFloat,BOOL,CGFloat";
            for (String s : fieldTypeMap.keySet()) {
                String fieldName = s;
                String type = fieldTypeMap.get(s);
                String content = "@property (nonatomic, ";
                //如果类似是cgType中的某一种 限定符号就使用assign，可以根据自己的需求修改
                if (cgType.contains(type)) {
                    content += "assign)" + type + " " + fieldName + ";";
                } else if (type.equals("NSString")) {
                    content += "copy) " + type + " * " + fieldName + ";";
                } else {
                    content += "strong) " + type + " * " + fieldName + ";";
                }
                fw.write("\r\n");
                fw.write(content + "\r\n");
            }

            fw.write("\r\n");
            fw.write("\r\n");
            fw.write("@end" + "\r\n");
            fw.close();
        } catch (IOException e1) {
            e1.printStackTrace();
            System.out.println("写入失败");
        }


    }

    /**
     * 写入文件头部的元信息
     *
     * @param fileName
     * @param file
     */
    private void writeHeaderComment(String fileName, File file) {
        String h1 = "//";
        String h2 = "//  " + fileName;
        String h3 = "//  " + schemeName;
        String h4 = "//";
        SimpleDateFormat dateFormat = new SimpleDateFormat("YY/MM/dd");
        String date = dateFormat.format(new Date());
        String h5 = "//  Created by Model Generate on " + date;
        String h6 = "//  Copyright © 2016年 " + corporateName + " All rights reserved.";
        String h7 = "//";

        FileWriter fw = null;
        try {
            fw = new FileWriter(file, true);
            fw.write(h1 + "\r\n");
            fw.write(h2 + "\r\n");
            fw.write(h3 + "\r\n");
            fw.write(h4 + "\r\n");
            fw.write(h5 + "\r\n");
            fw.write(h6 + "\r\n");
            fw.write(h7 + "\r\n");
            fw.close();
        } catch (IOException e1) {
            e1.printStackTrace();
            System.out.println("写入失败");
        }

    }

    /**
     * 创建文件
     *
     * @param cls      class对象
     * @param fileName 文件名称 文件名和类名必须一致
     * @return
     */
    private String createFile(Class cls, String fileName) {
        File directory = new File("");//设定为当前文件夹
        String currentPath = directory.getAbsolutePath();
        String buildDirectory = currentPath + "/OCModelOutput";

        String[] paths = cls.getPackage().getName().split("\\.");
        String targetFileDirectoryPath = buildDirectory;
        for (String s : paths) {
            targetFileDirectoryPath += "/" + s;
        }
        File targetDirectory = new File(targetFileDirectoryPath);
        if (!targetDirectory.exists()) {
            targetDirectory.mkdirs();
        }

        File mFile = new File(targetFileDirectoryPath + "/" + fileName + ".m");
        File hFile = new File(targetFileDirectoryPath + "/" + fileName + ".h");
        if (mFile.exists()) {
            mFile.delete();
        } else {
            try {
                mFile.createNewFile();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        if (hFile.exists()) {
            hFile.delete();
        } else {
            try {
                hFile.createNewFile();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return targetFileDirectoryPath;
    }

    /**
     * 获取文件的包名字和文件的名称组成类名
     *
     * @param path 文件路径
     * @return
     */
    private String getClassName(String path) {
        File f = new File(path);
        String pattern = "(?<=package\\s).+?(?=;)";
        Pattern r = Pattern.compile(pattern);
        try {
            Scanner linReader = new Scanner(f);
            while (linReader.hasNext()) {
                String line = linReader.nextLine();
                Matcher m = r.matcher(line);
                if (m.find()) {
                    linReader.close();
                    return m.group(0) + "." + f.getName().replace(".java", "");
                }
            }
            linReader.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


    public static void main(String[] args) {
        File f = new File("");
        System.out.print(f.getAbsolutePath());
        for (String s : modelDirectories) {
            OCTranslator translate = new OCTranslator();
            File file = new File(f.getAbsolutePath() + "/" + s);
            List<String> paths = new ArrayList<>();
            translate.getAllFile(file, paths);
            translate.parseFile(paths);
        }
    }
}
