webpackJsonp([48],{UGKp:function(t,a,e){"use strict";Object.defineProperty(a,"__esModule",{value:!0});var r={data:function(){return{visible:!1,dataForm:{id:0,groupTasksId:"",proxy:"",phone:"",mid:"",searchStatus:"",addStatus:"",subtaskStatus:"",executionTime:"",deleteFlag:"",createTime:""},dataRule:{groupTasksId:[{required:!0,message:"不能为空",trigger:"blur"}],proxy:[{required:!0,message:"不能为空",trigger:"blur"}],phone:[{required:!0,message:"不能为空",trigger:"blur"}],mid:[{required:!0,message:"不能为空",trigger:"blur"}],searchStatus:[{required:!0,message:"不能为空",trigger:"blur"}],addStatus:[{required:!0,message:"不能为空",trigger:"blur"}],subtaskStatus:[{required:!0,message:"不能为空",trigger:"blur"}],executionTime:[{required:!0,message:"不能为空",trigger:"blur"}],deleteFlag:[{required:!0,message:"不能为空",trigger:"blur"}],createTime:[{required:!0,message:"不能为空",trigger:"blur"}]}}},methods:{init:function(t){var a=this;this.dataForm.id=t||0,this.visible=!0,this.$nextTick(function(){a.$refs.dataForm.resetFields(),a.dataForm.id&&a.$http({url:a.$http.adornUrl("/ltt/cdgroupsubtasks/info/"+a.dataForm.id),method:"get",params:a.$http.adornParams()}).then(function(t){var e=t.data;e&&0===e.code&&(a.dataForm.groupTasksId=e.cdgroupsubtasks.groupTasksId,a.dataForm.proxy=e.cdgroupsubtasks.proxy,a.dataForm.phone=e.cdgroupsubtasks.phone,a.dataForm.mid=e.cdgroupsubtasks.mid,a.dataForm.searchStatus=e.cdgroupsubtasks.searchStatus,a.dataForm.addStatus=e.cdgroupsubtasks.addStatus,a.dataForm.subtaskStatus=e.cdgroupsubtasks.subtaskStatus,a.dataForm.executionTime=e.cdgroupsubtasks.executionTime,a.dataForm.deleteFlag=e.cdgroupsubtasks.deleteFlag,a.dataForm.createTime=e.cdgroupsubtasks.createTime)})})},dataFormSubmit:function(){var t=this;this.$refs.dataForm.validate(function(a){a&&t.$http({url:t.$http.adornUrl("/ltt/cdgroupsubtasks/"+(t.dataForm.id?"update":"save")),method:"post",data:t.$http.adornData({id:t.dataForm.id||void 0,groupTasksId:t.dataForm.groupTasksId,proxy:t.dataForm.proxy,phone:t.dataForm.phone,mid:t.dataForm.mid,searchStatus:t.dataForm.searchStatus,addStatus:t.dataForm.addStatus,subtaskStatus:t.dataForm.subtaskStatus,executionTime:t.dataForm.executionTime,deleteFlag:t.dataForm.deleteFlag,createTime:t.dataForm.createTime})}).then(function(a){var e=a.data;e&&0===e.code?t.$message({message:"操作成功",type:"success",duration:1500,onClose:function(){t.visible=!1,t.$emit("refreshDataList")}}):t.$message.error(e.msg)})})}}},s={render:function(){var t=this,a=t.$createElement,e=t._self._c||a;return e("el-dialog",{attrs:{title:t.dataForm.id?"修改":"新增","close-on-click-modal":!1,visible:t.visible},on:{"update:visible":function(a){t.visible=a}}},[e("el-form",{ref:"dataForm",attrs:{model:t.dataForm,rules:t.dataRule,"label-width":"80px"},nativeOn:{keyup:function(a){if(!("button"in a)&&t._k(a.keyCode,"enter",13,a.key))return null;t.dataFormSubmit()}}},[e("el-form-item",{attrs:{label:"",prop:"groupTasksId"}},[e("el-input",{attrs:{placeholder:""},model:{value:t.dataForm.groupTasksId,callback:function(a){t.$set(t.dataForm,"groupTasksId",a)},expression:"dataForm.groupTasksId"}})],1),t._v(" "),e("el-form-item",{attrs:{label:"",prop:"proxy"}},[e("el-input",{attrs:{placeholder:""},model:{value:t.dataForm.proxy,callback:function(a){t.$set(t.dataForm,"proxy",a)},expression:"dataForm.proxy"}})],1),t._v(" "),e("el-form-item",{attrs:{label:"",prop:"phone"}},[e("el-input",{attrs:{placeholder:""},model:{value:t.dataForm.phone,callback:function(a){t.$set(t.dataForm,"phone",a)},expression:"dataForm.phone"}})],1),t._v(" "),e("el-form-item",{attrs:{label:"",prop:"mid"}},[e("el-input",{attrs:{placeholder:""},model:{value:t.dataForm.mid,callback:function(a){t.$set(t.dataForm,"mid",a)},expression:"dataForm.mid"}})],1),t._v(" "),e("el-form-item",{attrs:{label:"",prop:"searchStatus"}},[e("el-input",{attrs:{placeholder:""},model:{value:t.dataForm.searchStatus,callback:function(a){t.$set(t.dataForm,"searchStatus",a)},expression:"dataForm.searchStatus"}})],1),t._v(" "),e("el-form-item",{attrs:{label:"",prop:"addStatus"}},[e("el-input",{attrs:{placeholder:""},model:{value:t.dataForm.addStatus,callback:function(a){t.$set(t.dataForm,"addStatus",a)},expression:"dataForm.addStatus"}})],1),t._v(" "),e("el-form-item",{attrs:{label:"",prop:"subtaskStatus"}},[e("el-input",{attrs:{placeholder:""},model:{value:t.dataForm.subtaskStatus,callback:function(a){t.$set(t.dataForm,"subtaskStatus",a)},expression:"dataForm.subtaskStatus"}})],1),t._v(" "),e("el-form-item",{attrs:{label:"",prop:"executionTime"}},[e("el-input",{attrs:{placeholder:""},model:{value:t.dataForm.executionTime,callback:function(a){t.$set(t.dataForm,"executionTime",a)},expression:"dataForm.executionTime"}})],1),t._v(" "),e("el-form-item",{attrs:{label:"",prop:"deleteFlag"}},[e("el-input",{attrs:{placeholder:""},model:{value:t.dataForm.deleteFlag,callback:function(a){t.$set(t.dataForm,"deleteFlag",a)},expression:"dataForm.deleteFlag"}})],1),t._v(" "),e("el-form-item",{attrs:{label:"",prop:"createTime"}},[e("el-input",{attrs:{placeholder:""},model:{value:t.dataForm.createTime,callback:function(a){t.$set(t.dataForm,"createTime",a)},expression:"dataForm.createTime"}})],1)],1),t._v(" "),e("span",{staticClass:"dialog-footer",attrs:{slot:"footer"},slot:"footer"},[e("el-button",{on:{click:function(a){t.visible=!1}}},[t._v("取消")]),t._v(" "),e("el-button",{attrs:{type:"primary"},on:{click:function(a){t.dataFormSubmit()}}},[t._v("确定")])],1)],1)},staticRenderFns:[]},o=e("46Yf")(r,s,!1,null,null,null);a.default=o.exports}});