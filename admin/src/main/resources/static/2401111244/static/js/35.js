webpackJsonp([35],{qny9:function(t,e,a){"use strict";Object.defineProperty(e,"__esModule",{value:!0});var r={data:function(){return{visible:!1,dataForm:{deptid:0,deptname:"",deptresponsibility:"",parentdeptid:"",deptman:"",depttel:"",remark:""},dataRule:{deptname:[{required:!0,message:"不能为空",trigger:"blur"}],deptresponsibility:[{required:!0,message:"不能为空",trigger:"blur"}],parentdeptid:[{required:!0,message:"不能为空",trigger:"blur"}],deptman:[{required:!0,message:"不能为空",trigger:"blur"}],depttel:[{required:!0,message:"不能为空",trigger:"blur"}],remark:[{required:!0,message:"不能为空",trigger:"blur"}]}}},methods:{init:function(t){var e=this;this.dataForm.deptid=t||0,this.visible=!0,this.$nextTick(function(){e.$refs.dataForm.resetFields(),e.dataForm.deptid&&e.$http({url:e.$http.adornUrl("/ltt/dept/info/"+e.dataForm.deptid),method:"get",params:e.$http.adornParams()}).then(function(t){var a=t.data;a&&0===a.code&&(e.dataForm.deptname=a.dept.deptname,e.dataForm.deptresponsibility=a.dept.deptresponsibility,e.dataForm.parentdeptid=a.dept.parentdeptid,e.dataForm.deptman=a.dept.deptman,e.dataForm.depttel=a.dept.depttel,e.dataForm.remark=a.dept.remark)})})},dataFormSubmit:function(){var t=this;this.$refs.dataForm.validate(function(e){e&&t.$http({url:t.$http.adornUrl("/ltt/dept/"+(t.dataForm.deptid?"update":"save")),method:"post",data:t.$http.adornData({deptid:t.dataForm.deptid||void 0,deptname:t.dataForm.deptname,deptresponsibility:t.dataForm.deptresponsibility,parentdeptid:t.dataForm.parentdeptid,deptman:t.dataForm.deptman,depttel:t.dataForm.depttel,remark:t.dataForm.remark})}).then(function(e){var a=e.data;a&&0===a.code?t.$message({message:"操作成功",type:"success",duration:1500,onClose:function(){t.visible=!1,t.$emit("refreshDataList")}}):t.$message.error(a.msg)})})}}},d={render:function(){var t=this,e=t.$createElement,a=t._self._c||e;return a("el-dialog",{attrs:{title:t.dataForm.id?"修改":"新增","close-on-click-modal":!1,visible:t.visible},on:{"update:visible":function(e){t.visible=e}}},[a("el-form",{ref:"dataForm",attrs:{model:t.dataForm,rules:t.dataRule,"label-width":"80px"},nativeOn:{keyup:function(e){if(!("button"in e)&&t._k(e.keyCode,"enter",13,e.key))return null;t.dataFormSubmit()}}},[a("el-form-item",{attrs:{label:"",prop:"deptname"}},[a("el-input",{attrs:{placeholder:""},model:{value:t.dataForm.deptname,callback:function(e){t.$set(t.dataForm,"deptname",e)},expression:"dataForm.deptname"}})],1),t._v(" "),a("el-form-item",{attrs:{label:"",prop:"deptresponsibility"}},[a("el-input",{attrs:{placeholder:""},model:{value:t.dataForm.deptresponsibility,callback:function(e){t.$set(t.dataForm,"deptresponsibility",e)},expression:"dataForm.deptresponsibility"}})],1),t._v(" "),a("el-form-item",{attrs:{label:"",prop:"parentdeptid"}},[a("el-input",{attrs:{placeholder:""},model:{value:t.dataForm.parentdeptid,callback:function(e){t.$set(t.dataForm,"parentdeptid",e)},expression:"dataForm.parentdeptid"}})],1),t._v(" "),a("el-form-item",{attrs:{label:"",prop:"deptman"}},[a("el-input",{attrs:{placeholder:""},model:{value:t.dataForm.deptman,callback:function(e){t.$set(t.dataForm,"deptman",e)},expression:"dataForm.deptman"}})],1),t._v(" "),a("el-form-item",{attrs:{label:"",prop:"depttel"}},[a("el-input",{attrs:{placeholder:""},model:{value:t.dataForm.depttel,callback:function(e){t.$set(t.dataForm,"depttel",e)},expression:"dataForm.depttel"}})],1),t._v(" "),a("el-form-item",{attrs:{label:"",prop:"remark"}},[a("el-input",{attrs:{placeholder:""},model:{value:t.dataForm.remark,callback:function(e){t.$set(t.dataForm,"remark",e)},expression:"dataForm.remark"}})],1)],1),t._v(" "),a("span",{staticClass:"dialog-footer",attrs:{slot:"footer"},slot:"footer"},[a("el-button",{on:{click:function(e){t.visible=!1}}},[t._v("取消")]),t._v(" "),a("el-button",{attrs:{type:"primary"},on:{click:function(e){t.dataFormSubmit()}}},[t._v("确定")])],1)],1)},staticRenderFns:[]},i=a("46Yf")(r,d,!1,null,null,null);e.default=i.exports}});