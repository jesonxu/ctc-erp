package com.dahantc.erp.vo.noInterestAccount.service;

import com.dahantc.erp.controller.BaseResponse;
import com.dahantc.erp.util.AccountConfigUtil;
import com.dahantc.erp.util.ParseFile;
import com.dahantc.erp.util.StringUtil;
import com.dahantc.erp.vo.noInterestAccount.INoInterestAccountService;
import com.dahantc.erp.vo.noInterestAccount.entity.NoInterestAccountInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 不计息账号的service 实现类
 *
 * @author 8520
 */
@Service(value = "noInterestAccountService")
public class NoInterestAccountServiceImpl implements INoInterestAccountService {

    private static Logger logger = LoggerFactory.getLogger(NoInterestAccountServiceImpl.class);

    /**
     * 导入添加
     *
     * @param files 文件
     * @return 结果
     */
    @Override
    public BaseResponse<Boolean> importAdd(List<File> files) {
        long startTime = System.currentTimeMillis();
        try {
            // 读取文件内容（支持txt 和 Excel文件）
            Set<NoInterestAccountInfo> accountInfos = readFileInfo(files);
            if (accountInfos.isEmpty()) {
                return BaseResponse.error("导入文件中，未能解析出账号信息");
            }
            AccountConfigUtil accountConfigUtil = AccountConfigUtil.getInstance();
            // 平台已经有的账号
            List<String[]> existAccountInfos = accountConfigUtil.readAll();
            if (existAccountInfos != null && !existAccountInfos.isEmpty()) {
                accountInfos.addAll(existAccountInfos.stream().map(NoInterestAccountInfo::new).collect(Collectors.toSet()));
            }
            boolean result = accountConfigUtil.write(accountInfos.stream().map(NoInterestAccountInfo::toAccountInfo).collect(Collectors.toList()));
            logger.info("导入添加账号完成，耗时:" + (System.currentTimeMillis() - startTime));
            if (result) {
                return BaseResponse.success("添加成功");
            } else {
                return BaseResponse.error("添加账号，保存失败");
            }
        } catch (Exception e) {
            logger.error("文件解析异常", e);
        }
        return BaseResponse.error("添加异常");
    }


    /**
     * 读取文件里面的账号信息
     *
     * @param files 文件
     * @return 不计息账号信息
     */
    private Set<NoInterestAccountInfo> readFileInfo(List<File> files) {
        AccountConfigUtil accountConfigUtil = AccountConfigUtil.getInstance();
        Set<NoInterestAccountInfo> accountInfos = new HashSet<>();
        try {
            // 读取文件内容（支持txt 和 Excel文件）
            if (files != null && !files.isEmpty()) {
                for (File toFile : files) {
                    String fileType = toFile.getName().substring(toFile.getName().lastIndexOf("."));
                    if (StringUtil.isNotBlank(fileType)) {
                        List<String[]> fileAccountInfo = null;
                        if (".xls".equalsIgnoreCase(fileType)) {
                            fileAccountInfo = ParseFile.parseExcel2003(toFile);
                        } else if (".xlsx".equalsIgnoreCase(fileType)) {
                            fileAccountInfo = ParseFile.parseExcel2007(toFile);
                        } else if (".txt".equalsIgnoreCase(fileType)) {
                            fileAccountInfo = accountConfigUtil.readTxt(toFile);
                        }
                        if (fileAccountInfo != null && !fileAccountInfo.isEmpty()) {
                            accountInfos.addAll(fileAccountInfo.stream()
                                    .filter(row -> row.length > 0 && StringUtil.isNotBlank(row[0]))
                                    .map(NoInterestAccountInfo::new).collect(Collectors.toSet()));
                        }
                    }
                }
            }
        } catch (Exception e) {
            logger.error("文件读取异常", e);
        }
        return accountInfos;
    }

    /**
     * 添加账号
     *
     * @param accounts 账号信息
     * @return 操作结果
     */
    @Override
    public BaseResponse<Boolean> add(List<String> accounts) {
        try {
            long startTime = System.currentTimeMillis();
            if (accounts == null || accounts.isEmpty()) {
                return BaseResponse.error("添加的账号不能为空");
            }
            Set<NoInterestAccountInfo> validAccount = accounts.stream()
                    .filter(StringUtil::isNotBlank)
                    .map(row -> row.split(AccountConfigUtil.separator))
                    .map(NoInterestAccountInfo::new).collect(Collectors.toSet());
            if (validAccount.isEmpty()) {
                return BaseResponse.error("请填写有效账号");
            }
            AccountConfigUtil accountConfigUtil = AccountConfigUtil.getInstance();
            // 平台已经有的账号
            List<String[]> existAccountInfos = accountConfigUtil.readAll();
            int before = validAccount.size();
            if (existAccountInfos != null && !existAccountInfos.isEmpty()) {
                Set<NoInterestAccountInfo> existAccounts = existAccountInfos.stream().map(NoInterestAccountInfo::new).collect(Collectors.toSet());
                before = before + existAccounts.size();
                validAccount.addAll(existAccounts);
                if (validAccount.size() == existAccountInfos.size()) {
                    return BaseResponse.error("添加失败，账号已经存在");
                }
            }
            int after = validAccount.size();
            boolean result = accountConfigUtil.write(validAccount.stream().map(NoInterestAccountInfo::toAccountInfo).collect(Collectors.toList()));
            logger.info("导入添加账号完成，耗时:" + (System.currentTimeMillis() - startTime));
            if (result) {
                return BaseResponse.success("添加成功，" + (after - before) + "个，已经存在");
            } else {
                return BaseResponse.error("添加账号，保存失败");
            }
        } catch (Exception e) {
            logger.error("添加账号异常", e);
        }
        return BaseResponse.error("账号添加异常");
    }

    @Override
    public BaseResponse<Boolean> importDelete(List<File> files) {
        long startTime = System.currentTimeMillis();
        try {
            // 读取文件内容（支持txt 和 Excel文件）
            Set<NoInterestAccountInfo> accountInfos = readFileInfo(files);
            if (accountInfos.isEmpty()) {
                return BaseResponse.error("删除失败，导入文件中，未能解析出账号信息");
            }
            AccountConfigUtil accountConfigUtil = AccountConfigUtil.getInstance();
            // 平台已经有的账号
            List<String[]> existAccountInfos = accountConfigUtil.readAll();
            if (existAccountInfos == null || existAccountInfos.isEmpty()) {
                // 平台本来就没有数据
                return BaseResponse.error("删除成功");
            }
            // 平台已经存在的账号
            Set<NoInterestAccountInfo> existAccounts = existAccountInfos.stream().map(NoInterestAccountInfo::new).collect(Collectors.toSet());
            int beforeCount = existAccounts.size();
            existAccounts.removeAll(accountInfos);
            if (existAccounts.size() == beforeCount) {
                // 这些数据本来就不在平台数据中
                return BaseResponse.error("删除成功");
            }
            boolean result = accountConfigUtil.write(existAccounts.stream().map(NoInterestAccountInfo::toAccountInfo).collect(Collectors.toList()));
            logger.info("导入删除账号完成，耗时:" + (System.currentTimeMillis() - startTime));
            if (result) {
                return BaseResponse.success("删除成功");
            } else {
                return BaseResponse.error("删除失败，保存失败");
            }
        } catch (Exception e) {
            logger.error("账号导入删除异常", e);
        }
        return BaseResponse.error("删除异常");
    }

    @Override
    public BaseResponse<Boolean> delete(List<String> accounts) {
        long startTime = System.currentTimeMillis();
        try {
            if (accounts == null || accounts.isEmpty()){
                return BaseResponse.error("删除账号不能为空");
            }
            // 需要删除的数据
            Set<NoInterestAccountInfo> deleteAccounts = accounts.stream()
                    .filter(StringUtil::isNotBlank)
                    .map(account-> account.split(AccountConfigUtil.separator))
                    .map(NoInterestAccountInfo::new).collect(Collectors.toSet());

            AccountConfigUtil accountConfigUtil = AccountConfigUtil.getInstance();
            // 平台已经有的账号
            List<String[]> existAccountInfos = accountConfigUtil.readAll();
            if (existAccountInfos == null || existAccountInfos.isEmpty()) {
                // 平台本来就没有数据
                return BaseResponse.error("删除成功");
            }
            // 平台已经存在的账号
            Set<NoInterestAccountInfo> existAccounts = existAccountInfos.stream().map(NoInterestAccountInfo::new).collect(Collectors.toSet());
            int beforeCount = existAccounts.size();
            existAccounts.removeAll(deleteAccounts);
            if (existAccounts.size() == beforeCount) {
                // 这些数据本来就不在平台数据中
                return BaseResponse.error("删除成功");
            }
            boolean result = accountConfigUtil.write(existAccounts.stream().map(NoInterestAccountInfo::toAccountInfo).collect(Collectors.toList()));
            logger.info("导入删除账号完成，耗时:" + (System.currentTimeMillis() - startTime));
            if (result) {
                return BaseResponse.success("删除成功");
            } else {
                return BaseResponse.error("删除失败，保存失败");
            }
        } catch (Exception e) {
            logger.error("账号导入删除异常", e);
        }
        return BaseResponse.error("删除异常");
    }
}
