
package io.renren.modules.ltt.dto;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.renren.common.base.dto.PageParam;
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
@TableName("cd_get_phone")
@ApiModel("")
@Accessors(chain = true)
public class LineRegisterDTO implements Serializable {

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
	private String txtToken;

}
