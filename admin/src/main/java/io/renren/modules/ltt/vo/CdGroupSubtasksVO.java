package io.renren.modules.ltt.vo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.renren.common.utils.EnumUtil;
import io.renren.modules.ltt.enums.AddStatus;
import io.renren.modules.ltt.enums.GroupSubTasksStatus;
import io.renren.modules.ltt.enums.SearchStatus;
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
 * @date 2023-12-02 19:02:29
 */
@Data
@TableName("cd_group_subtasks")
@ApiModel("")
@Accessors(chain = true)
public class CdGroupSubtasksVO implements Serializable {
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
	private Integer groupTasksId;
	/**
	 *
	 */
	@ApiModelProperty(required=false,value="")
	private String proxy;
	/**
	 *
	 */
	@ApiModelProperty(required=false,value="")
	private String phone;
	/**
	 *
	 */
	@ApiModelProperty(required=false,value="")
	private String mid;
	/**
	 *
	 */
	@ApiModelProperty(required=false,value="")
	private Integer searchStatus;
	/**
	 *
	 */
	@ApiModelProperty(required=false,value="")
	private Integer addStatus;
	/**
	 *
	 */
	@ApiModelProperty(required=false,value="")
	private Integer subtaskStatus;
	/**
	 *
	 */
	@ApiModelProperty(required=false,value="")
	private Date executionTime;
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
	/**
	 *
	 */
	@ApiModelProperty(required=false,value="")
	private String errMsg;

	public String getSearchStatusStr() {
		return EnumUtil.queryValueByKey(searchStatus, SearchStatus.values());
	}

	public String getAddStatusStr() {
		return EnumUtil.queryValueByKey(addStatus, AddStatus.values());
	}

	public String getSubtaskStatusStr() {
		return EnumUtil.queryValueByKey(subtaskStatus, GroupSubTasksStatus.values());
	}
}
