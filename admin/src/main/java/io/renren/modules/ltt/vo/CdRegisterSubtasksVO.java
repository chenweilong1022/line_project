package io.renren.modules.ltt.vo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.renren.common.utils.EnumUtil;
import io.renren.modules.ltt.enums.RegistrationStatus;
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
 * @date 2023-12-02 00:31:50
 */
@Data
@TableName("cd_register_subtasks")
@ApiModel("")
@Accessors(chain = true)
public class CdRegisterSubtasksVO implements Serializable {
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
	private Integer taskId;
	/**
	 *
	 */
	@ApiModelProperty(required=false,value="")
	private Integer numberRegistrations;
	/**
	 *
	 */
	@ApiModelProperty(required=false,value="")
	private Integer numberSuccesses;
	/**
	 *
	 */
	@ApiModelProperty(required=false,value="")
	private Integer numberFailures;
	/**
	 *
	 */
	@ApiModelProperty(required=false,value="")
	private Integer registrationStatus;
	/**
	 *
	 */
	@ApiModelProperty(required=false,value="")
	private Integer deleteFlag;
	/**
	 *
	 */
	@ApiModelProperty(required=false,value="")
	private Date createTime;

	public String getRegistrationStatusStr() {
		return EnumUtil.queryValueByKey(registrationStatus, RegistrationStatus.values());
	}
}
