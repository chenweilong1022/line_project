package io.renren.modules.ltt.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
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
 * @date 2023-12-13 22:33:36
 */
@Data
@TableName("cd_static_proxy")
@ApiModel("")
@Accessors(chain = true)
public class CdStaticProxyEntity implements Serializable {
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
	private String username;
	/**
	 * 
	 */
	@ApiModelProperty(required=false,value="")
	private String password;
	/**
	 * 
	 */
	@ApiModelProperty(required=false,value="")
	private String ipProxy;
	/**
	 * 
	 */
	@ApiModelProperty(required=false,value="")
	private Integer useCount;
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

}
