webpackJsonp([44],{"49N+":function(e,a,r){"use strict";Object.defineProperty(a,"__esModule",{value:!0});var t={data:function(){return{fullscreenLoading:!1,visible:!1,dataForm:{id:0,remark:"",uploadNumber:"",synchronizationsNumber:"",successesNumber:"",failuresNumber:"",materialUrl:"",navyUrl:"",deleteFlag:"",createTime:""},dataRule:{remark:[{required:!0,message:"不能为空",trigger:"blur"}],uploadNumber:[{required:!0,message:"不能为空",trigger:"blur"}],synchronizationsNumber:[{required:!0,message:"不能为空",trigger:"blur"}],successesNumber:[{required:!0,message:"不能为空",trigger:"blur"}],failuresNumber:[{required:!0,message:"不能为空",trigger:"blur"}],deleteFlag:[{required:!0,message:"不能为空",trigger:"blur"}],createTime:[{required:!0,message:"不能为空",trigger:"blur"}]}}},methods:{handleAvatarSuccess:function(e,a){this.dataForm.materialUrl=e.data},handleAvatarSuccess1:function(e,a){this.dataForm.navyUrl=e.data},init:function(e){var a=this;this.dataForm.id=e||0,this.visible=!0,this.$nextTick(function(){a.$refs.dataForm.resetFields(),a.dataForm.id&&a.$http({url:a.$http.adornUrl("/ltt/cdmaterial/info/"+a.dataForm.id),method:"get",params:a.$http.adornParams()}).then(function(e){var r=e.data;r&&0===r.code&&(a.dataForm.remark=r.cdmaterial.remark,a.dataForm.uploadNumber=r.cdmaterial.uploadNumber,a.dataForm.synchronizationsNumber=r.cdmaterial.synchronizationsNumber,a.dataForm.successesNumber=r.cdmaterial.successesNumber,a.dataForm.failuresNumber=r.cdmaterial.failuresNumber,a.dataForm.materialUrl=r.cdmaterial.materialUrl,a.dataForm.navyUrl=r.cdmaterial.navyUrl,a.dataForm.deleteFlag=r.cdmaterial.deleteFlag,a.dataForm.createTime=r.cdmaterial.createTime)})})},dataFormSubmit:function(){var e=this;this.$refs.dataForm.validate(function(a){a&&(e.fullscreenLoading=!0,e.$http({url:e.$http.adornUrl("/ltt/cdmaterial/"+(e.dataForm.id?"update":"save")),method:"post",data:e.$http.adornData({id:e.dataForm.id||void 0,remark:e.dataForm.remark,uploadNumber:e.dataForm.uploadNumber,synchronizationsNumber:e.dataForm.synchronizationsNumber,successesNumber:e.dataForm.successesNumber,failuresNumber:e.dataForm.failuresNumber,materialUrl:e.dataForm.materialUrl,navyUrl:e.dataForm.navyUrl,deleteFlag:e.dataForm.deleteFlag,createTime:e.dataForm.createTime})}).then(function(a){var r=a.data;e.fullscreenLoading=!1,r&&0===r.code?e.$message({message:"操作成功",type:"success",duration:1500,onClose:function(){e.visible=!1,e.$emit("refreshDataList")}}):e.$message.error(r.msg)}))})}}},l={render:function(){var e=this,a=e.$createElement,r=e._self._c||a;return r("el-dialog",{directives:[{name:"loading",rawName:"v-loading.fullscreen.lock",value:e.fullscreenLoading,expression:"fullscreenLoading",modifiers:{fullscreen:!0,lock:!0}}],attrs:{title:e.dataForm.id?"修改":"新增","close-on-click-modal":!1,visible:e.visible},on:{"update:visible":function(a){e.visible=a}}},[r("el-form",{ref:"dataForm",attrs:{model:e.dataForm,rules:e.dataRule,"label-width":"80px"},nativeOn:{keyup:function(a){if(!("button"in a)&&e._k(a.keyCode,"enter",13,a.key))return null;e.dataFormSubmit()}}},[r("el-form-item",{attrs:{label:"备注",prop:"remark"}},[r("el-input",{attrs:{placeholder:""},model:{value:e.dataForm.remark,callback:function(a){e.$set(e.dataForm,"remark",a)},expression:"dataForm.remark"}})],1),e._v(" "),r("el-form-item",{attrs:{label:"料子"}},[r("el-upload",{staticClass:"upload-demo",attrs:{action:"http://localhost:8880/app/file/upload","on-success":e.handleAvatarSuccess}},[r("el-button",{attrs:{size:"small",type:"primary"}},[e._v("点击上传")])],1)],1),e._v(" "),r("el-form-item",{attrs:{label:"水军"}},[r("el-upload",{staticClass:"upload-demo",attrs:{action:"http://localhost:8880/app/file/upload","on-success":e.handleAvatarSuccess1}},[r("el-button",{attrs:{size:"small",type:"primary"}},[e._v("点击上传")])],1)],1)],1),e._v(" "),r("span",{staticClass:"dialog-footer",attrs:{slot:"footer"},slot:"footer"},[r("el-button",{on:{click:function(a){e.visible=!1}}},[e._v("取消")]),e._v(" "),r("el-button",{attrs:{type:"primary"},on:{click:function(a){e.dataFormSubmit()}}},[e._v("确定")])],1)],1)},staticRenderFns:[]},s=r("46Yf")(t,l,!1,null,null,null);a.default=s.exports}});