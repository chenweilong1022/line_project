package io.renren.modules.ltt.vo;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.renren.common.utils.EnumUtil;
import io.renren.modules.ltt.enums.AccountExistStatus;
import io.renren.modules.ltt.enums.ExportStatus;
import io.renren.modules.ltt.enums.RegisterStatus;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;
import java.io.Serializable;
import java.util.Date;

/**
 *
 *
 * @author chenweilong
 * @email chenweilong@qq.com
 * @date 2023-11-30 22:33:44
 */
@Data
@TableName("cd_line_register")
@ApiModel("")
@Accessors(chain = true)
public class CdLineRegisterVO implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 *
	 */
	@TableId(type = IdType.AUTO)
	@ApiModelProperty(required=false,value="")
	private Integer id;
	/**
	 *
	 */
	@ApiModelProperty(required=false,value="")
	private String ab;
	/**
	 *
	 */
	@ApiModelProperty(required=false,value="")
	private String appVersion;
	/**
	 *
	 */
	@ApiModelProperty(required=false,value="")
	private String countryCode;
	/**
	 *
	 */
	@ApiModelProperty(required=false,value="")
	private String phone;
	/**
	 *
	 */
	@ApiModelProperty(required=false,value="")
	private String proxy;
	/**
	 *
	 */
	@ApiModelProperty(required=false,value="")
	private Integer proxyStatus;
	/**
	 *
	 */
	@ApiModelProperty(required=false,value="")
	private String txtToken;
	/**
	 *
	 */
	@ApiModelProperty(required=false,value="")
	private String taskId;
	/**
	 *
	 */
	@ApiModelProperty(required=false,value="")
	private String smsCode;
	/**
	 *
	 */
	@ApiModelProperty(required=false,value="")
	private String token;
	/**
	 *
	 */
	@ApiModelProperty(required=false,value="")
	private Integer registerStatus;
	/**
	 *
	 */
	@ApiModelProperty(required=false,value="")
	private Integer deleteFlag;
	/**
	 *
	 */
	@ApiModelProperty(required=false,value="")
	private Integer getPhoneId;
	/**
	 *
	 */
	@ApiModelProperty(required=false,value="")
	private Integer subtasksId;
	/**
	 *
	 */
	@ApiModelProperty(required=false,value="")
	private Integer groupTaskId;
	/**
	 *
	 */
	@ApiModelProperty(required=false,value="")
	private Integer openStatus;
	/**
	 *
	 */
	@ApiModelProperty(required=false,value="")
	private Integer accountStatus;
	/**
	 * 拉了几次群了
	 */
	@ApiModelProperty(required=false,value="")
	private Integer groupCount;
	/**
	 * 账号注册状态 1 首次 2 多次
	 */
	@ApiModelProperty(required=false,value="")
	private Integer accountExistStatus;
	/**
	 *
	 */
	@ApiModelProperty(required=false,value="")
	private String pkey;
	/**
	 * 账号注册状态 1 未导出 2 已导出
	 */
	@ApiModelProperty(required=false,value="")
	private Integer exportStatus;
	/**
	 *
	 */
	@ApiModelProperty(required=false,value="")
	private Date createTime;
	/**
	 *
	 */
	@ApiModelProperty(required=false,value="")
	private String errMsg;

	public String getAccountExistStatusStr() {
		return EnumUtil.queryValueByKey(accountExistStatus,AccountExistStatus.values());
	}

	public String getExportStatusStr() {
		return EnumUtil.queryValueByKey(exportStatus, ExportStatus.values());
	}

	public String getToken() {
		if (StrUtil.isEmpty(this.token)) {
			return token;
		}
		if (this.token.contains("deviceTotalDiskSpaceGB")) {
			return "";
		}
		return token;
	}

	public String getRegisterStatusStr() {
		return EnumUtil.queryValueByKey(registerStatus, RegisterStatus.values());
	}
}
