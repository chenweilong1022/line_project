webpackJsonp([2,42,43,44],{"49N+":function(e,t,a){"use strict";Object.defineProperty(t,"__esModule",{value:!0});var r={data:function(){return{fullscreenLoading:!1,visible:!1,dataForm:{id:0,remark:"",uploadNumber:"",synchronizationsNumber:"",successesNumber:"",failuresNumber:"",materialUrl:"",navyUrl:"",deleteFlag:"",createTime:""},dataRule:{remark:[{required:!0,message:"不能为空",trigger:"blur"}],uploadNumber:[{required:!0,message:"不能为空",trigger:"blur"}],synchronizationsNumber:[{required:!0,message:"不能为空",trigger:"blur"}],successesNumber:[{required:!0,message:"不能为空",trigger:"blur"}],failuresNumber:[{required:!0,message:"不能为空",trigger:"blur"}],deleteFlag:[{required:!0,message:"不能为空",trigger:"blur"}],createTime:[{required:!0,message:"不能为空",trigger:"blur"}]}}},methods:{handleAvatarSuccess:function(e,t){this.dataForm.materialUrl=e.data},handleAvatarSuccess1:function(e,t){this.dataForm.navyUrl=e.data},init:function(e){var t=this;this.dataForm.id=e||0,this.visible=!0,this.$nextTick(function(){t.$refs.dataForm.resetFields(),t.dataForm.id&&t.$http({url:t.$http.adornUrl("/ltt/cdmaterial/info/"+t.dataForm.id),method:"get",params:t.$http.adornParams()}).then(function(e){var a=e.data;a&&0===a.code&&(t.dataForm.remark=a.cdmaterial.remark,t.dataForm.uploadNumber=a.cdmaterial.uploadNumber,t.dataForm.synchronizationsNumber=a.cdmaterial.synchronizationsNumber,t.dataForm.successesNumber=a.cdmaterial.successesNumber,t.dataForm.failuresNumber=a.cdmaterial.failuresNumber,t.dataForm.materialUrl=a.cdmaterial.materialUrl,t.dataForm.navyUrl=a.cdmaterial.navyUrl,t.dataForm.deleteFlag=a.cdmaterial.deleteFlag,t.dataForm.createTime=a.cdmaterial.createTime)})})},dataFormSubmit:function(){var e=this;this.$refs.dataForm.validate(function(t){t&&(e.fullscreenLoading=!0,e.$http({url:e.$http.adornUrl("/ltt/cdmaterial/"+(e.dataForm.id?"update":"save")),method:"post",data:e.$http.adornData({id:e.dataForm.id||void 0,remark:e.dataForm.remark,uploadNumber:e.dataForm.uploadNumber,synchronizationsNumber:e.dataForm.synchronizationsNumber,successesNumber:e.dataForm.successesNumber,failuresNumber:e.dataForm.failuresNumber,materialUrl:e.dataForm.materialUrl,navyUrl:e.dataForm.navyUrl,deleteFlag:e.dataForm.deleteFlag,createTime:e.dataForm.createTime})}).then(function(t){var a=t.data;e.fullscreenLoading=!1,a&&0===a.code?e.$message({message:"操作成功",type:"success",duration:1500,onClose:function(){e.visible=!1,e.$emit("refreshDataList")}}):e.$message.error(a.msg)}))})}}},i={render:function(){var e=this,t=e.$createElement,a=e._self._c||t;return a("el-dialog",{directives:[{name:"loading",rawName:"v-loading.fullscreen.lock",value:e.fullscreenLoading,expression:"fullscreenLoading",modifiers:{fullscreen:!0,lock:!0}}],attrs:{title:e.dataForm.id?"修改":"新增","close-on-click-modal":!1,visible:e.visible},on:{"update:visible":function(t){e.visible=t}}},[a("el-form",{ref:"dataForm",attrs:{model:e.dataForm,rules:e.dataRule,"label-width":"80px"},nativeOn:{keyup:function(t){if(!("button"in t)&&e._k(t.keyCode,"enter",13,t.key))return null;e.dataFormSubmit()}}},[a("el-form-item",{attrs:{label:"备注",prop:"remark"}},[a("el-input",{attrs:{placeholder:""},model:{value:e.dataForm.remark,callback:function(t){e.$set(e.dataForm,"remark",t)},expression:"dataForm.remark"}})],1),e._v(" "),a("el-form-item",{attrs:{label:"料子"}},[a("el-upload",{staticClass:"upload-demo",attrs:{action:"http://localhost:8880/app/file/upload","on-success":e.handleAvatarSuccess}},[a("el-button",{attrs:{size:"small",type:"primary"}},[e._v("点击上传")])],1)],1),e._v(" "),a("el-form-item",{attrs:{label:"水军"}},[a("el-upload",{staticClass:"upload-demo",attrs:{action:"http://localhost:8880/app/file/upload","on-success":e.handleAvatarSuccess1}},[a("el-button",{attrs:{size:"small",type:"primary"}},[e._v("点击上传")])],1)],1)],1),e._v(" "),a("span",{staticClass:"dialog-footer",attrs:{slot:"footer"},slot:"footer"},[a("el-button",{on:{click:function(t){e.visible=!1}}},[e._v("取消")]),e._v(" "),a("el-button",{attrs:{type:"primary"},on:{click:function(t){e.dataFormSubmit()}}},[e._v("确定")])],1)],1)},staticRenderFns:[]},l=a("46Yf")(r,i,!1,null,null,null);t.default=l.exports},F3lq:function(e,t,a){"use strict";Object.defineProperty(t,"__esModule",{value:!0});var r=a("49N+"),i=a("qTfJ"),l=a("PcL9"),n={data:function(){return{dataForm:{key:""},dataList:[],pageIndex:1,pageSize:10,totalPage:0,dataListLoading:!1,dataListSelections:[],addOrUpdateVisible:!1,importZipVisible:!1,autoGroupVisible:!1}},components:{AddOrUpdate:r.default,AutoGroup:i.default,ImportZip:l.default},activated:function(){this.getDataList()},methods:{getDataList:function(){var e=this;this.dataListLoading=!0,this.$http({url:this.$http.adornUrl("/ltt/cdmaterial/list"),method:"get",params:this.$http.adornParams({page:this.pageIndex,limit:this.pageSize,key:this.dataForm.key})}).then(function(t){var a=t.data;a&&0===a.code?(e.dataList=a.page.list,e.totalPage=a.page.totalCount):(e.dataList=[],e.totalPage=0),e.dataListLoading=!1})},sizeChangeHandle:function(e){this.pageSize=e,this.pageIndex=1,this.getDataList()},currentChangeHandle:function(e){this.pageIndex=e,this.getDataList()},selectionChangeHandle:function(e){this.dataListSelections=e},addOrUpdateHandle:function(e){var t=this;this.addOrUpdateVisible=!0,this.$nextTick(function(){t.$refs.addOrUpdate.init(e)})},autoGroupHandle:function(e){var t=this;this.autoGroupVisible=!0,this.$nextTick(function(){t.$refs.autoGroup.init(e)})},importZipHandle:function(e){var t=this;this.importZipVisible=!0,this.$nextTick(function(){t.$refs.importZip.init(e)})},deleteHandle:function(e){var t=this,a=e?[e]:this.dataListSelections.map(function(e){return e.id});this.$confirm("确定对[id="+a.join(",")+"]进行["+(e?"删除":"批量删除")+"]操作?","提示",{confirmButtonText:"确定",cancelButtonText:"取消",type:"warning"}).then(function(){t.$http({url:t.$http.adornUrl("/ltt/cdmaterial/delete"),method:"post",data:t.$http.adornData(a,!1)}).then(function(e){var a=e.data;a&&0===a.code?t.$message({message:"操作成功",type:"success",duration:1500,onClose:function(){t.getDataList()}}):t.$message.error(a.msg)})})}}},o={render:function(){var e=this,t=e.$createElement,a=e._self._c||t;return a("div",{staticClass:"mod-config"},[a("el-form",{attrs:{inline:!0,model:e.dataForm},nativeOn:{keyup:function(t){if(!("button"in t)&&e._k(t.keyCode,"enter",13,t.key))return null;e.getDataList()}}},[a("el-form-item",[a("el-input",{attrs:{placeholder:"参数名",clearable:""},model:{value:e.dataForm.key,callback:function(t){e.$set(e.dataForm,"key",t)},expression:"dataForm.key"}})],1),e._v(" "),a("el-form-item",[a("el-button",{on:{click:function(t){e.getDataList()}}},[e._v("查询")]),e._v(" "),e.isAuth("ltt:cdmaterial:save")?a("el-button",{attrs:{type:"primary"},on:{click:function(t){e.addOrUpdateHandle()}}},[e._v("新增")]):e._e(),e._v(" "),e.isAuth("ltt:cdmaterial:delete")?a("el-button",{attrs:{type:"danger",disabled:e.dataListSelections.length<=0},on:{click:function(t){e.deleteHandle()}}},[e._v("批量删除")]):e._e()],1)],1),e._v(" "),a("el-table",{directives:[{name:"loading",rawName:"v-loading",value:e.dataListLoading,expression:"dataListLoading"}],staticStyle:{width:"100%"},attrs:{data:e.dataList,border:""},on:{"selection-change":e.selectionChangeHandle}},[a("el-table-column",{attrs:{type:"selection","header-align":"center",align:"center",width:"50"}}),e._v(" "),a("el-table-column",{attrs:{prop:"remark","header-align":"center",align:"center",label:"标题"}}),e._v(" "),a("el-table-column",{attrs:{prop:"uploadNumber","header-align":"center",align:"center",label:"上传数量"}}),e._v(" "),a("el-table-column",{attrs:{prop:"materialNumber","header-align":"center",align:"center",label:"料子数量"}}),e._v(" "),a("el-table-column",{attrs:{prop:"navyNumber","header-align":"center",align:"center",label:"水军数量"}}),e._v(" "),a("el-table-column",{attrs:{prop:"synchronizationsNumber","header-align":"center",align:"center",label:"同步数量"}}),e._v(" "),a("el-table-column",{attrs:{prop:"successesNumber","header-align":"center",align:"center",label:"成功数量"}}),e._v(" "),a("el-table-column",{attrs:{prop:"failuresNumber","header-align":"center",align:"center",label:"失败数量"}}),e._v(" "),a("el-table-column",{attrs:{prop:"createTime","header-align":"center",align:"center",label:"时间"}}),e._v(" "),a("el-table-column",{attrs:{fixed:"right","header-align":"center",align:"center",width:"150",label:"操作"},scopedSlots:e._u([{key:"default",fn:function(t){return[a("el-button",{attrs:{type:"text",size:"small"},on:{click:function(a){e.autoGroupHandle(t.row.id)}}},[e._v("自动分配")]),e._v(" "),a("el-button",{attrs:{type:"text",size:"small"},on:{click:function(a){e.importZipHandle(t.row.id)}}},[e._v("导出报表")])]}}])})],1),e._v(" "),a("el-pagination",{attrs:{"current-page":e.pageIndex,"page-sizes":[10,20,50,100],"page-size":e.pageSize,total:e.totalPage,layout:"total, sizes, prev, pager, next, jumper"},on:{"size-change":e.sizeChangeHandle,"current-change":e.currentChangeHandle}}),e._v(" "),e.addOrUpdateVisible?a("add-or-update",{ref:"addOrUpdate",on:{refreshDataList:e.getDataList}}):e._e(),e._v(" "),e.autoGroupVisible?a("auto-group",{ref:"autoGroup",on:{refreshDataList:e.getDataList}}):e._e(),e._v(" "),e.importZipVisible?a("import-zip",{ref:"importZip",on:{refreshDataList:e.getDataList}}):e._e()],1)},staticRenderFns:[]},s=a("46Yf")(n,o,!1,null,null,null);t.default=s.exports},PcL9:function(e,t,a){"use strict";Object.defineProperty(t,"__esModule",{value:!0});var r={data:function(){return{countryCodeOptions:[{label:"th"},{label:"jp"}],fullscreenLoading:!1,countryCode:null,visible:!1,dataForm:{id:0,remark:"",uploadNumber:"",synchronizationsNumber:"",accountGroupRestrictions:"",numberSingleGroups:"",successesNumber:"",failuresNumber:"",materialUrl:"",navyUrl:"",deleteFlag:"",createTime:""},dataRule:{remark:[{required:!0,message:"不能为空",trigger:"blur"}],uploadNumber:[{required:!0,message:"不能为空",trigger:"blur"}],synchronizationsNumber:[{required:!0,message:"不能为空",trigger:"blur"}],successesNumber:[{required:!0,message:"不能为空",trigger:"blur"}],failuresNumber:[{required:!0,message:"不能为空",trigger:"blur"}],deleteFlag:[{required:!0,message:"不能为空",trigger:"blur"}],createTime:[{required:!0,message:"不能为空",trigger:"blur"}]}}},methods:{handleAvatarSuccess:function(e,t){this.dataForm.materialUrl=e.data},handleAvatarSuccess1:function(e,t){this.dataForm.navyUrl=e.data},init:function(e){this.dataForm.id=e||0,this.visible=!0},dataFormSubmit:function(){window.open(this.$http.adornUrl("/ltt/cdmaterial/importZip?token="+this.$cookie.get("token")+"&id="+this.dataForm.id))}}},i={render:function(){var e=this,t=e.$createElement,a=e._self._c||t;return a("el-dialog",{directives:[{name:"loading",rawName:"v-loading.fullscreen.lock",value:e.fullscreenLoading,expression:"fullscreenLoading",modifiers:{fullscreen:!0,lock:!0}}],attrs:{title:(e.dataForm.id,"自动分群"),"close-on-click-modal":!1,visible:e.visible},on:{"update:visible":function(t){e.visible=t}}},[a("el-form",{ref:"dataForm",attrs:{model:e.dataForm,rules:e.dataRule,"label-width":"200px"},nativeOn:{keyup:function(t){if(!("button"in t)&&e._k(t.keyCode,"enter",13,t.key))return null;e.dataFormSubmit()}}},[a("el-form-item",{attrs:{label:"拉群限制",prop:"accountGroupRestrictions"}},[a("el-input",{attrs:{placeholder:""},model:{value:e.dataForm.accountGroupRestrictions,callback:function(t){e.$set(e.dataForm,"accountGroupRestrictions",t)},expression:"dataForm.accountGroupRestrictions"}})],1),e._v(" "),a("el-form-item",{attrs:{label:"单个群需要多少人",prop:"numberSingleGroups"}},[a("el-input",{attrs:{placeholder:""},model:{value:e.dataForm.numberSingleGroups,callback:function(t){e.$set(e.dataForm,"numberSingleGroups",t)},expression:"dataForm.numberSingleGroups"}})],1),e._v(" "),a("el-form-item",{attrs:{label:"拉群账号的国家"}},[a("el-select",{attrs:{placeholder:"国家",clearable:""},model:{value:e.countryCode,callback:function(t){e.countryCode=t},expression:"countryCode"}},e._l(e.countryCodeOptions,function(e){return a("el-option",{key:e.label,attrs:{label:e.label,value:e.label}})}))],1)],1),e._v(" "),a("span",{staticClass:"dialog-footer",attrs:{slot:"footer"},slot:"footer"},[a("el-button",{on:{click:function(t){e.visible=!1}}},[e._v("取消")]),e._v(" "),a("el-button",{attrs:{type:"primary"},on:{click:function(t){e.dataFormSubmit()}}},[e._v("确定")])],1)],1)},staticRenderFns:[]},l=a("46Yf")(r,i,!1,null,null,null);t.default=l.exports},qTfJ:function(e,t,a){"use strict";Object.defineProperty(t,"__esModule",{value:!0});var r={render:function(){var e=this,t=e.$createElement,a=e._self._c||t;return a("el-dialog",{directives:[{name:"loading",rawName:"v-loading.fullscreen.lock",value:e.fullscreenLoading,expression:"fullscreenLoading",modifiers:{fullscreen:!0,lock:!0}}],attrs:{title:(e.dataForm.id,"自动分群"),"close-on-click-modal":!1,visible:e.visible},on:{"update:visible":function(t){e.visible=t}}},[a("el-form",{ref:"dataForm",attrs:{model:e.dataForm,rules:e.dataRule,"label-width":"200px"},nativeOn:{keyup:function(t){if(!("button"in t)&&e._k(t.keyCode,"enter",13,t.key))return null;e.dataFormSubmit()}}},[a("el-form-item",{attrs:{label:"拉群限制",prop:"accountGroupRestrictions"}},[a("el-input",{attrs:{placeholder:""},model:{value:e.dataForm.accountGroupRestrictions,callback:function(t){e.$set(e.dataForm,"accountGroupRestrictions",t)},expression:"dataForm.accountGroupRestrictions"}})],1),e._v(" "),a("el-form-item",{attrs:{label:"单个群需要多少人",prop:"numberSingleGroups"}},[a("el-input",{attrs:{placeholder:""},model:{value:e.dataForm.numberSingleGroups,callback:function(t){e.$set(e.dataForm,"numberSingleGroups",t)},expression:"dataForm.numberSingleGroups"}})],1),e._v(" "),a("el-form-item",{attrs:{label:"拉群账号的国家"}},[a("el-select",{attrs:{placeholder:"国家",clearable:""},model:{value:e.countryCode,callback:function(t){e.countryCode=t},expression:"countryCode"}},e._l(e.countryCodeOptions,function(e){return a("el-option",{key:e.label,attrs:{label:e.label,value:e.label}})}))],1)],1),e._v(" "),a("span",{staticClass:"dialog-footer",attrs:{slot:"footer"},slot:"footer"},[a("el-button",{on:{click:function(t){e.visible=!1}}},[e._v("取消")]),e._v(" "),a("el-button",{attrs:{type:"primary"},on:{click:function(t){e.dataFormSubmit()}}},[e._v("确定")])],1)],1)},staticRenderFns:[]},i=a("46Yf")({data:function(){return{countryCodeOptions:[{label:"th"},{label:"jp"}],fullscreenLoading:!1,countryCode:null,visible:!1,dataForm:{id:0,remark:"",uploadNumber:"",synchronizationsNumber:"",accountGroupRestrictions:"",numberSingleGroups:"",successesNumber:"",failuresNumber:"",materialUrl:"",navyUrl:"",deleteFlag:"",createTime:""},dataRule:{remark:[{required:!0,message:"不能为空",trigger:"blur"}],uploadNumber:[{required:!0,message:"不能为空",trigger:"blur"}],synchronizationsNumber:[{required:!0,message:"不能为空",trigger:"blur"}],successesNumber:[{required:!0,message:"不能为空",trigger:"blur"}],failuresNumber:[{required:!0,message:"不能为空",trigger:"blur"}],deleteFlag:[{required:!0,message:"不能为空",trigger:"blur"}],createTime:[{required:!0,message:"不能为空",trigger:"blur"}]}}},methods:{handleAvatarSuccess:function(e,t){this.dataForm.materialUrl=e.data},handleAvatarSuccess1:function(e,t){this.dataForm.navyUrl=e.data},init:function(e){this.dataForm.id=e||0,this.visible=!0},dataFormSubmit:function(){var e=this;this.$refs.dataForm.validate(function(t){t&&(e.fullscreenLoading=!0,e.$http({url:e.$http.adornUrl("/ltt/cdmaterial/autoAssignGroups"),method:"post",data:e.$http.adornData({id:e.dataForm.id,accountGroupRestrictions:e.dataForm.accountGroupRestrictions,numberSingleGroups:e.dataForm.numberSingleGroups,countryCode:e.countryCode})}).then(function(t){var a=t.data;e.fullscreenLoading=!1,a&&0===a.code?e.$message({message:"操作成功",type:"success",duration:1500,onClose:function(){e.visible=!1,e.$emit("refreshDataList")}}):e.$message.error(a.msg)}))})}}},r,!1,null,null,null);t.default=i.exports}});