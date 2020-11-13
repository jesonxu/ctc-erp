package com.dahantc.erp.util;

import com.dahantc.erp.commom.Constants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * 配置文件读取/更新(适用于数据量不大 10M以内)
 *
 * @author 8520
 */
public class AccountConfigUtil {

    private static Logger logger = LoggerFactory.getLogger(AccountConfigUtil.class);

    /**
     * 是否在读（在读的时候，不允许直接替换文件名称）
     */
    private static boolean reading = false;
    /**
     * 读取超时时间（默认超时时间 30秒）
     */
    private static long timeOut = 30000L;
    /**
     * 文件路径
     */
    private String filePath = Constants.RESOURCE + File.separator + "accountConfig/noInterest/accountInfo.txt";
    /**
     * 临时文件目录
     */
    private String tempFilePath = Constants.RESOURCE + File.separator + "accountConfig/noInterest/TempAccountInfo.txt";

    /**
     * 分隔符
     */
    public static String separator = "###";

    private AccountConfigUtil() {

    }

    private static volatile AccountConfigUtil instance = null;

    public static synchronized AccountConfigUtil getInstance() {
        if (instance == null) {
            synchronized (AccountConfigUtil.class) {
                if (instance == null) {
                    instance = new AccountConfigUtil();
                }
            }
        }
        return instance;
    }

    /**
     * 根据条件 读取全部
     *
     * @param condition 条件
     * @return 行数据
     */
    public List<String[]> readAll(String... condition) {
        reading = true;
        List<String[]> result = new ArrayList<>();
        FileInputStream fileInputStream = null;
        InputStreamReader isr = null;
        BufferedReader bf = null;
        try {
            File file = this.checkFile(filePath);
            if (this.readCheckAccess()) {
                fileInputStream = new FileInputStream(file);
                isr = new InputStreamReader(fileInputStream, StandardCharsets.UTF_8);
                bf = new BufferedReader(isr);
                String content;
                while ((content = bf.readLine()) != null) {
                    if (this.checkByParam(content, condition)) {
                        result.add(content.split(separator));
                    }
                }
            }
        } catch (Exception e) {
            logger.error("读取文件异常", e);
        } finally {
            try {
                if (bf != null) {
                    bf.close();
                }
                if (isr != null) {
                    isr.close();
                }
                if (fileInputStream != null) {
                    fileInputStream.close();
                }
            } catch (Exception e) {
                logger.error("关闭文件读取异常", e);
            }
        }
        reading = false;
        return result;
    }

    /**
     * 根据条件 读取全部
     *
     * @param condition 条件
     * @return 行数据
     */
    public List<String> readAllOnlyAccount(String... condition) {
        List<String> result = new ArrayList<>();
        try {
            List<String[]> accountInfos = this.readAll(condition);
            if (accountInfos != null && !accountInfos.isEmpty()) {
                return accountInfos.stream().map(row -> row[0]).collect(Collectors.toList());
            }
        } catch (Exception e) {
            logger.error("读取文件异常", e);
        }
        return result;
    }

    /**
     * 根据条件 读取全部（读取的是上传的文件）
     *
     * @param file 要读取的文件
     * @return 行数据
     */
    public List<String[]> readTxt(File file) {
        List<String[]> result = new ArrayList<>();
        FileInputStream fileInputStream = null;
        InputStreamReader isr = null;
        BufferedReader bf = null;
        try {
            if (file.exists() && file.canRead()) {
                fileInputStream = new FileInputStream(file);
                isr = new InputStreamReader(fileInputStream, StandardCharsets.UTF_8);
                bf = new BufferedReader(isr);
                String content;
                while ((content = bf.readLine()) != null) {
                    result.add(content.split(separator));
                }
            }
        } catch (Exception e) {
            logger.error("读取文件异常", e);
        } finally {
            try {
                if (bf != null) {
                    bf.close();
                }
                if (isr != null) {
                    isr.close();
                }
                if (fileInputStream != null) {
                    fileInputStream.close();
                }
            } catch (Exception e) {
                logger.error("关闭文件读取异常", e);
            }
        }
        return result;
    }

    /**
     * 校验查询内容
     *
     * @param content 读取内容
     * @param params  参数
     * @return 校验结果
     */
    private boolean checkByParam(String content, String... params) {
        if (StringUtil.isBlank(content.split(separator)[0])) {
            return false;
        }
        if (params.length > 0) {
            for (String param : params) {
                if (StringUtil.isNotBlank(param) && content.contains(param)) {
                    return false;
                }
            }
        }
        return true;
    }


    /**
     * 写数据（数据是全覆盖写 只允许单个写）
     *
     * @param contents 写的内容
     * @return 结果
     */
    public synchronized boolean write(List<String> contents) {
        // 生成新的文件
        File file = null;
        try {
            file = this.checkFile(tempFilePath);
        } catch (Exception e) {
            logger.error("创建写入临时文件异常");
        }
        // 将内容写到临时文件
        boolean writeResult = this.writeContToTempFile(contents, file);
        if (writeResult) {
            // 检查条件
            if (writeCheckAccess()) {
                // 删除老文件
                if (deleteOldFile()) {
                    File formalFile = new File(filePath);
                    if (file.renameTo(formalFile)) {
                        return true;
                    }
                }
            }
        }
        // 删除文件（临时文件）
        File tempFile = new File(tempFilePath);
        if (tempFile.exists()) {
            if (tempFile.delete()) {
                logger.error("删除临时文件失败" + tempFilePath);
            }
        }
        return false;
    }

    /**
     * 原来文件删除
     *
     * @return 结果
     */
    private boolean deleteOldFile() {
        try {
            File oldFile = new File(filePath);
            if (oldFile.exists()) {
                return oldFile.delete();
            }
        } catch (Exception e) {
            logger.error("文件删除异常", e);
        }
        return false;
    }

    /**
     * 将内容 写到文件（临时文件）
     *
     * @param contents 内容
     * @param file     文件
     * @return 结果
     */
    private boolean writeContToTempFile(List<String> contents, File file) {
        if (file == null) {
            return false;
        }
        FileOutputStream fileWriter = null;
        OutputStreamWriter osw = null;
        BufferedWriter bw = null;
        try {
            fileWriter = new FileOutputStream(file);
            osw = new OutputStreamWriter(fileWriter, StandardCharsets.UTF_8);
            bw = new BufferedWriter(osw);
            for (String content : contents) {
                bw.write(content);
                bw.newLine();
                bw.flush();
            }
            return true;
        } catch (IOException e) {
            logger.error("文件写入错误");
        } finally {
            if (bw != null) {
                try {
                    bw.close();
                } catch (IOException e) {
                    logger.error("关闭BufferedWriter异常", e);
                }
            }
            if (osw != null) {
                try {
                    osw.close();
                } catch (IOException e) {
                    logger.error("关闭OutputStreamWriter异常", e);
                }
            }
            if (fileWriter != null) {
                try {
                    fileWriter.close();
                } catch (IOException e) {
                    logger.error("关闭FileOutputStream异常", e);
                }
            }
        }
        return false;
    }


    /**
     * 校验是否可以读取
     *
     * @return 判断结果
     */
    private boolean readCheckAccess() {
        File file = new File(filePath);
        long startTime = System.currentTimeMillis();
        // 一直在操作 并且 已经超时 就会返回不可用
        while (file.exists() && !file.canRead()) {
            if ((System.currentTimeMillis() - startTime) > timeOut) {
                return false;
            }
            try {
                Thread.sleep(1);
            } catch (InterruptedException e) {
                logger.error("文件读取等待异常", e);
            }
        }
        return true;
    }

    /**
     * 校验是否可以写（更名 删除老的文件）
     *
     * @return 判断结果
     */
    private boolean writeCheckAccess() {
        File file = new File(filePath);
        long startTime = System.currentTimeMillis();
        // 一直在操作 并且 已经超时 就会返回不可用(能写 并且没人读 就可以进行操作)
        while (file.exists() && (!file.canWrite() || reading)) {
            if ((System.currentTimeMillis() - startTime) > timeOut) {
                return false;
            }
            try {
                Thread.sleep(1);
            } catch (InterruptedException e) {
                logger.error("等待异常", e);
            }
            System.out.println("等待....");
        }
        return true;
    }

    /**
     * 校验文件是否存在（不存在 就创建文件）
     *
     * @return 文件
     */
    private File checkFile(String path) {
        File file = new File(path);
        if (!file.exists()) {
            try {
                if (!file.getParentFile().exists()) {
                    if (!file.getParentFile().mkdirs()) {
                        logger.error("创建文件夹失败" + file.getPath());
                        throw new RuntimeException("创建账号文件夹失败");
                    }
                }
                if (!file.createNewFile()) {
                    logger.error("创建文件失败" + file.getPath());
                    throw new RuntimeException("创建账号文件失败");
                }
            } catch (IOException e) {
                logger.error("文件创建异常", e);
                throw new RuntimeException(e);
            }
        }
        return file;
    }
}