webpackJsonp([7,36,37],{C9ND:function(t,e,a){"use strict";Object.defineProperty(e,"__esModule",{value:!0});var r={data:function(){return{visible:!1,dataForm:{id:0,username:"",password:"",ipProxy:"",useCount:"",deleteFlag:"",createTime:""},dataRule:{username:[{required:!0,message:"不能为空",trigger:"blur"}],password:[{required:!0,message:"不能为空",trigger:"blur"}],ipProxy:[{required:!0,message:"不能为空",trigger:"blur"}],useCount:[{required:!0,message:"不能为空",trigger:"blur"}],deleteFlag:[{required:!0,message:"不能为空",trigger:"blur"}],createTime:[{required:!0,message:"不能为空",trigger:"blur"}]}}},methods:{handleAvatarSuccess:function(t,e){this.dataForm.txtUrl=t.data},init:function(t){var e=this;this.dataForm.id=t||0,this.visible=!0,this.$nextTick(function(){e.$refs.dataForm.resetFields(),e.dataForm.id&&e.$http({url:e.$http.adornUrl("/ltt/cdstaticproxy/info/"+e.dataForm.id),method:"get",params:e.$http.adornParams()}).then(function(t){var a=t.data;a&&0===a.code&&(e.dataForm.username=a.cdstaticproxy.username,e.dataForm.password=a.cdstaticproxy.password,e.dataForm.ipProxy=a.cdstaticproxy.ipProxy,e.dataForm.useCount=a.cdstaticproxy.useCount,e.dataForm.deleteFlag=a.cdstaticproxy.deleteFlag,e.dataForm.createTime=a.cdstaticproxy.createTime)})})},dataFormSubmit:function(){var t=this;this.$refs.dataForm.validate(function(e){e&&t.$http({url:t.$http.adornUrl("/ltt/cdstaticproxy/saveBatch"),method:"post",data:t.$http.adornData({id:t.dataForm.id||void 0,username:t.dataForm.username,password:t.dataForm.password,ipProxy:t.dataForm.ipProxy,useCount:t.dataForm.useCount,txtUrl:t.dataForm.txtUrl,deleteFlag:t.dataForm.deleteFlag,createTime:t.dataForm.createTime})}).then(function(e){var a=e.data;a&&0===a.code?t.$message({message:"操作成功",type:"success",duration:1500,onClose:function(){t.visible=!1,t.$emit("refreshDataList")}}):t.$message.error(a.msg)})})}}},i={render:function(){var t=this,e=t.$createElement,a=t._self._c||e;return a("el-dialog",{attrs:{title:(t.dataForm.id,"批量新增"),"close-on-click-modal":!1,visible:t.visible},on:{"update:visible":function(e){t.visible=e}}},[a("el-form",{ref:"dataForm",attrs:{model:t.dataForm,rules:t.dataRule,"label-width":"80px"},nativeOn:{keyup:function(e){if(!("button"in e)&&t._k(e.keyCode,"enter",13,e.key))return null;t.dataFormSubmit()}}},[a("el-form-item",{attrs:{label:"代理文件"}},[a("el-upload",{staticClass:"upload-demo",attrs:{action:"http://localhost:8880/app/file/upload","on-success":t.handleAvatarSuccess}},[a("el-button",{attrs:{size:"small",type:"primary"}},[t._v("点击上传")])],1)],1)],1),t._v(" "),a("span",{staticClass:"dialog-footer",attrs:{slot:"footer"},slot:"footer"},[a("el-button",{on:{click:function(e){t.visible=!1}}},[t._v("取消")]),t._v(" "),a("el-button",{attrs:{type:"primary"},on:{click:function(e){t.dataFormSubmit()}}},[t._v("确定")])],1)],1)},staticRenderFns:[]},o=a("46Yf")(r,i,!1,null,null,null);e.default=o.exports},tKaj:function(t,e,a){"use strict";Object.defineProperty(e,"__esModule",{value:!0});var r=a("zXrt"),i=a("C9ND"),o={data:function(){return{dataForm:{key:""},dataList:[],pageIndex:1,pageSize:10,totalPage:0,dataListLoading:!1,dataListSelections:[],addOrUpdateVisible:!1,addBatchVisible:!1}},components:{AddOrUpdate:r.default,AddBatch:i.default},activated:function(){this.getDataList()},methods:{getDataList:function(){var t=this;this.dataListLoading=!0,this.$http({url:this.$http.adornUrl("/ltt/cdstaticproxy/list"),method:"get",params:this.$http.adornParams({page:this.pageIndex,limit:this.pageSize,key:this.dataForm.key})}).then(function(e){var a=e.data;a&&0===a.code?(t.dataList=a.page.list,t.totalPage=a.page.totalCount):(t.dataList=[],t.totalPage=0),t.dataListLoading=!1})},sizeChangeHandle:function(t){this.pageSize=t,this.pageIndex=1,this.getDataList()},currentChangeHandle:function(t){this.pageIndex=t,this.getDataList()},selectionChangeHandle:function(t){this.dataListSelections=t},addOrUpdateHandle:function(t){var e=this;this.addOrUpdateVisible=!0,this.$nextTick(function(){e.$refs.addOrUpdate.init(t)})},addBatchHandler:function(t){var e=this;this.addBatchVisible=!0,this.$nextTick(function(){e.$refs.addBatch.init(t)})},deleteHandle:function(t){var e=this,a=t?[t]:this.dataListSelections.map(function(t){return t.id});this.$confirm("确定对[id="+a.join(",")+"]进行["+(t?"删除":"批量删除")+"]操作?","提示",{confirmButtonText:"确定",cancelButtonText:"取消",type:"warning"}).then(function(){e.$http({url:e.$http.adornUrl("/ltt/cdstaticproxy/delete"),method:"post",data:e.$http.adornData(a,!1)}).then(function(t){var a=t.data;a&&0===a.code?e.$message({message:"操作成功",type:"success",duration:1500,onClose:function(){e.getDataList()}}):e.$message.error(a.msg)})})}}},s={render:function(){var t=this,e=t.$createElement,a=t._self._c||e;return a("div",{staticClass:"mod-config"},[a("el-form",{attrs:{inline:!0,model:t.dataForm},nativeOn:{keyup:function(e){if(!("button"in e)&&t._k(e.keyCode,"enter",13,e.key))return null;t.getDataList()}}},[a("el-form-item",[a("el-input",{attrs:{placeholder:"参数名",clearable:""},model:{value:t.dataForm.key,callback:function(e){t.$set(t.dataForm,"key",e)},expression:"dataForm.key"}})],1),t._v(" "),a("el-form-item",[a("el-button",{on:{click:function(e){t.getDataList()}}},[t._v("查询")]),t._v(" "),t.isAuth("ltt:cdstaticproxy:save")?a("el-button",{attrs:{type:"primary"},on:{click:function(e){t.addOrUpdateHandle()}}},[t._v("新增")]):t._e(),t._v(" "),t.isAuth("ltt:cdstaticproxy:save")?a("el-button",{attrs:{type:"primary"},on:{click:function(e){t.addBatchHandler()}}},[t._v("批量新增")]):t._e(),t._v(" "),t.isAuth("ltt:cdstaticproxy:delete")?a("el-button",{attrs:{type:"danger",disabled:t.dataListSelections.length<=0},on:{click:function(e){t.deleteHandle()}}},[t._v("批量删除")]):t._e()],1)],1),t._v(" "),a("el-table",{directives:[{name:"loading",rawName:"v-loading",value:t.dataListLoading,expression:"dataListLoading"}],staticStyle:{width:"100%"},attrs:{data:t.dataList,border:""},on:{"selection-change":t.selectionChangeHandle}},[a("el-table-column",{attrs:{type:"selection","header-align":"center",align:"center",width:"50"}}),t._v(" "),a("el-table-column",{attrs:{prop:"username","header-align":"center",align:"center",label:"username"}}),t._v(" "),a("el-table-column",{attrs:{prop:"password","header-align":"center",align:"center",label:"password"}}),t._v(" "),a("el-table-column",{attrs:{prop:"ipProxy","header-align":"center",align:"center",label:"ipProxy"}}),t._v(" "),a("el-table-column",{attrs:{fixed:"right","header-align":"center",align:"center",width:"150",label:"操作"},scopedSlots:t._u([{key:"default",fn:function(e){return[a("el-button",{attrs:{type:"text",size:"small"},on:{click:function(a){t.addOrUpdateHandle(e.row.id)}}},[t._v("修改")]),t._v(" "),a("el-button",{attrs:{type:"text",size:"small"},on:{click:function(a){t.deleteHandle(e.row.id)}}},[t._v("删除")])]}}])})],1),t._v(" "),a("el-pagination",{attrs:{"current-page":t.pageIndex,"page-sizes":[10,20,50,100],"page-size":t.pageSize,total:t.totalPage,layout:"total, sizes, prev, pager, next, jumper"},on:{"size-change":t.sizeChangeHandle,"current-change":t.currentChangeHandle}}),t._v(" "),t.addOrUpdateVisible?a("add-or-update",{ref:"addOrUpdate",on:{refreshDataList:t.getDataList}}):t._e(),t._v(" "),t.addBatchVisible?a("add-batch",{ref:"addBatch",on:{refreshDataList:t.getDataList}}):t._e()],1)},staticRenderFns:[]},n=a("46Yf")(o,s,!1,null,null,null);e.default=n.exports},zXrt:function(t,e,a){"use strict";Object.defineProperty(e,"__esModule",{value:!0});var r={data:function(){return{visible:!1,dataForm:{id:0,username:"",password:"",ipProxy:"",useCount:"",deleteFlag:"",createTime:""},dataRule:{username:[{required:!0,message:"不能为空",trigger:"blur"}],password:[{required:!0,message:"不能为空",trigger:"blur"}],ipProxy:[{required:!0,message:"不能为空",trigger:"blur"}],useCount:[{required:!0,message:"不能为空",trigger:"blur"}],deleteFlag:[{required:!0,message:"不能为空",trigger:"blur"}],createTime:[{required:!0,message:"不能为空",trigger:"blur"}]}}},methods:{init:function(t){var e=this;this.dataForm.id=t||0,this.visible=!0,this.$nextTick(function(){e.$refs.dataForm.resetFields(),e.dataForm.id&&e.$http({url:e.$http.adornUrl("/ltt/cdstaticproxy/info/"+e.dataForm.id),method:"get",params:e.$http.adornParams()}).then(function(t){var a=t.data;a&&0===a.code&&(e.dataForm.username=a.cdstaticproxy.username,e.dataForm.password=a.cdstaticproxy.password,e.dataForm.ipProxy=a.cdstaticproxy.ipProxy,e.dataForm.useCount=a.cdstaticproxy.useCount,e.dataForm.deleteFlag=a.cdstaticproxy.deleteFlag,e.dataForm.createTime=a.cdstaticproxy.createTime)})})},dataFormSubmit:function(){var t=this;this.$refs.dataForm.validate(function(e){e&&t.$http({url:t.$http.adornUrl("/ltt/cdstaticproxy/"+(t.dataForm.id?"update":"save")),method:"post",data:t.$http.adornData({id:t.dataForm.id||void 0,username:t.dataForm.username,password:t.dataForm.password,ipProxy:t.dataForm.ipProxy,useCount:t.dataForm.useCount,deleteFlag:t.dataForm.deleteFlag,createTime:t.dataForm.createTime})}).then(function(e){var a=e.data;a&&0===a.code?t.$message({message:"操作成功",type:"success",duration:1500,onClose:function(){t.visible=!1,t.$emit("refreshDataList")}}):t.$message.error(a.msg)})})}}},i={render:function(){var t=this,e=t.$createElement,a=t._self._c||e;return a("el-dialog",{attrs:{title:t.dataForm.id?"修改":"新增","close-on-click-modal":!1,visible:t.visible},on:{"update:visible":function(e){t.visible=e}}},[a("el-form",{ref:"dataForm",attrs:{model:t.dataForm,rules:t.dataRule,"label-width":"80px"},nativeOn:{keyup:function(e){if(!("button"in e)&&t._k(e.keyCode,"enter",13,e.key))return null;t.dataFormSubmit()}}},[a("el-form-item",{attrs:{label:"username",prop:"username"}},[a("el-input",{attrs:{placeholder:""},model:{value:t.dataForm.username,callback:function(e){t.$set(t.dataForm,"username",e)},expression:"dataForm.username"}})],1),t._v(" "),a("el-form-item",{attrs:{label:"password",prop:"password"}},[a("el-input",{attrs:{placeholder:""},model:{value:t.dataForm.password,callback:function(e){t.$set(t.dataForm,"password",e)},expression:"dataForm.password"}})],1),t._v(" "),a("el-form-item",{attrs:{label:"ipProxy",prop:"ipProxy"}},[a("el-input",{attrs:{placeholder:""},model:{value:t.dataForm.ipProxy,callback:function(e){t.$set(t.dataForm,"ipProxy",e)},expression:"dataForm.ipProxy"}})],1)],1),t._v(" "),a("span",{staticClass:"dialog-footer",attrs:{slot:"footer"},slot:"footer"},[a("el-button",{on:{click:function(e){t.visible=!1}}},[t._v("取消")]),t._v(" "),a("el-button",{attrs:{type:"primary"},on:{click:function(e){t.dataFormSubmit()}}},[t._v("确定")])],1)],1)},staticRenderFns:[]},o=a("46Yf")(r,i,!1,null,null,null);e.default=o.exports}});