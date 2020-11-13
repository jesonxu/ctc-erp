/**
 * flow_msg 流程信息
 * user_name 用户名称
 * dept_name 部门名称
 * role_name 用户角色名称（多个以逗号分隔）
 */
function flow_threshold_judge(flow_msg, user_name, dept_name, role_name) {
    // 实际账单金额（第三个） 和 basedata中的 平台账单金额 相同
    var result = false;
    if (!is_blank(flow_msg)) {
        try {
            var flow_msg_obj = JSON.parse(flow_msg);
            var actual_bill_info = flow_msg_obj["实际账单金额"];
            if (!is_blank(actual_bill_info)) {
                var bill_info_arr = actual_bill_info.split(",");
                if (bill_info_arr.length >= 3) {
                    // 实际账单金额数据
                    var actual_bill_money = bill_info_arr[2];
                    if (!is_blank(actual_bill_money)) {
                        actual_bill_money = parseFloat(actual_bill_money);
                        // 平台基础数据信息
                        var base_data_str = flow_msg_obj.baseData;
                        if (!is_blank(base_data_str)) {
                            var base_data_obj = JSON.parse(base_data_str);
                            // 平台账单信息
                            var platform_bill_info = base_data_obj["平台账单金额"];
                            if (!is_blank(platform_bill_info)) {
                                platform_bill_info = parseFloat(platform_bill_info);
                                // 金额相差不能大于 0.1
                                result = (Math.abs(actual_bill_money - platform_bill_info) < 0.1);
                            }
                        }
                    }
                }
            }
        } catch (error) {
            console.log("处理异常");
        }
    }
    return result;
}

function is_blank(obj) {
    return (obj == null || obj === "" || obj === undefined || obj === "undefined");
}
