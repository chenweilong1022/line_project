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
 * @date 2023-12-02 19:02:28
 */
@Data
@TableName("cd_group_tasks")
@ApiModel("")
@Accessors(chain = true)
public class CdGroupTasksEntity implements Serializable {
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
	private String groupName;
	/**
	 *
	 */
	@ApiModelProperty(required=false,value="")
	private String proxy;
	/**
	 *
	 */
	@ApiModelProperty(required=false,value="")
	private Integer intervals;
	/**
	 *
	 */
	@ApiModelProperty(required=false,value="")
	private String roomId;
	/**
	 *
	 */
	@ApiModelProperty(required=false,value="")
	private String taskId;
	/**
	 *
	 */
	@ApiModelProperty(required=false,value="")
	private String chatRoomUrl;
	/**
	 *
	 */
	@ApiModelProperty(required=false,value="")
	private String roomTicketId;
	/**
	 *
	 */
	@ApiModelProperty(required=false,value="")
	private Integer uploadGroupNumber;
	/**
	 *
	 */
	@ApiModelProperty(required=false,value="")
	private Integer currentExecutionsNumber;
	/**
	 *
	 */
	@ApiModelProperty(required=false,value="")
	private Integer successfullyAttractGroupsNumber;
	/**
	 *
	 */
	@ApiModelProperty(required=false,value="")
	private Integer groupStatus;
	/**
	 *
	 */
	@ApiModelProperty(required=false,value="")
	private Integer deleteFlag;
	/**
	 *
	 */
	@ApiModelProperty(required=false,value="")
	private Integer lineRegisterId;
	/**
	 *
	 */
	@ApiModelProperty(required=false,value="")
	private Integer addType;
	/**
	 *
	 */
	@ApiModelProperty(required=false,value="")
	private Integer materialId;
	/**
	 *
	 */
	@ApiModelProperty(required=false,value="")
	private Integer materialPhoneType;
	/**
	 *
	 */
	@ApiModelProperty(required=false,value="")
	private Date createTime;

}
