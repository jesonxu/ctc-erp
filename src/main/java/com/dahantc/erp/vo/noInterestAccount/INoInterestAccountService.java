package com.dahantc.erp.vo.noInterestAccount;

import com.dahantc.erp.controller.BaseResponse;

import java.io.File;
import java.util.List;

public interface INoInterestAccountService {

    /**
     * 导入添加
     *
     * @param files 文件
     * @return 结果
     */
    BaseResponse<Boolean> importAdd(List<File> files);

    /**
     * 添加账号
     * @param accounts 账号信息
     * @return 操作结果
     */
    BaseResponse<Boolean> add(List<String> accounts);

    /**
     * 导入删除
     *
     * @param files 文件
     * @return 结果
     */
    BaseResponse<Boolean> importDelete(List<File> files);


    /**
     * 删除账号
     * @param accounts 账号信息
     * @return 操作结果
     */
    BaseResponse<Boolean> delete(List<String> accounts);
}
