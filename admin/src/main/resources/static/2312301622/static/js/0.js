webpackJsonp([0],{"5XyS":function(e,t,a){"use strict";Object.defineProperty(t,"__esModule",{value:!0});var r={data:function(){return{visible:!1,dataForm:{id:0,ab:"",appVersion:"",countryCode:"",phone:"",proxy:"",txtToken:"",taskId:"",smsCode:"",registerStatus:"",deleteFlag:"",createTime:""},dataRule:{ab:[{required:!0,message:"不能为空",trigger:"blur"}],appVersion:[{required:!0,message:"不能为空",trigger:"blur"}],countryCode:[{required:!0,message:"不能为空",trigger:"blur"}],phone:[{required:!0,message:"不能为空",trigger:"blur"}],proxy:[{required:!0,message:"不能为空",trigger:"blur"}],txtToken:[{required:!0,message:"不能为空",trigger:"blur"}],taskId:[{required:!0,message:"不能为空",trigger:"blur"}],smsCode:[{required:!0,message:"不能为空",trigger:"blur"}],registerStatus:[{required:!0,message:"不能为空",trigger:"blur"}],deleteFlag:[{required:!0,message:"不能为空",trigger:"blur"}],createTime:[{required:!0,message:"不能为空",trigger:"blur"}]}}},methods:{init:function(e){var t=this;this.dataForm.id=e||0,this.visible=!0,this.$nextTick(function(){t.$refs.dataForm.resetFields(),t.dataForm.id&&t.$http({url:t.$http.adornUrl("/ltt/cdlineregister/info/"+t.dataForm.id),method:"get",params:t.$http.adornParams()}).then(function(e){var a=e.data;a&&0===a.code&&(t.dataForm.ab=a.cdlineregister.ab,t.dataForm.appVersion=a.cdlineregister.appVersion,t.dataForm.countryCode=a.cdlineregister.countryCode,t.dataForm.phone=a.cdlineregister.phone,t.dataForm.proxy=a.cdlineregister.proxy,t.dataForm.txtToken=a.cdlineregister.txtToken,t.dataForm.taskId=a.cdlineregister.taskId,t.dataForm.smsCode=a.cdlineregister.smsCode,t.dataForm.registerStatus=a.cdlineregister.registerStatus,t.dataForm.deleteFlag=a.cdlineregister.deleteFlag,t.dataForm.createTime=a.cdlineregister.createTime)})})},dataFormSubmit:function(){var e=this;this.$refs.dataForm.validate(function(t){t&&e.$http({url:e.$http.adornUrl("/ltt/cdlineregister/"+(e.dataForm.id?"update":"save")),method:"post",data:e.$http.adornData({id:e.dataForm.id||void 0,ab:e.dataForm.ab,appVersion:e.dataForm.appVersion,countryCode:e.dataForm.countryCode,phone:e.dataForm.phone,proxy:e.dataForm.proxy,txtToken:e.dataForm.txtToken,taskId:e.dataForm.taskId,smsCode:e.dataForm.smsCode,registerStatus:e.dataForm.registerStatus,deleteFlag:e.dataForm.deleteFlag,createTime:e.dataForm.createTime})}).then(function(t){var a=t.data;a&&0===a.code?e.$message({message:"操作成功",type:"success",duration:1500,onClose:function(){e.visible=!1,e.$emit("refreshDataList")}}):e.$message.error(a.msg)})})}}},o={render:function(){var e=this,t=e.$createElement,a=e._self._c||t;return a("el-dialog",{attrs:{title:e.dataForm.id?"修改":"新增","close-on-click-modal":!1,visible:e.visible},on:{"update:visible":function(t){e.visible=t}}},[a("el-form",{ref:"dataForm",attrs:{model:e.dataForm,rules:e.dataRule,"label-width":"80px"},nativeOn:{keyup:function(t){if(!("button"in t)&&e._k(t.keyCode,"enter",13,t.key))return null;e.dataFormSubmit()}}},[a("el-form-item",{attrs:{label:"",prop:"ab"}},[a("el-input",{attrs:{placeholder:""},model:{value:e.dataForm.ab,callback:function(t){e.$set(e.dataForm,"ab",t)},expression:"dataForm.ab"}})],1),e._v(" "),a("el-form-item",{attrs:{label:"",prop:"appVersion"}},[a("el-input",{attrs:{placeholder:""},model:{value:e.dataForm.appVersion,callback:function(t){e.$set(e.dataForm,"appVersion",t)},expression:"dataForm.appVersion"}})],1),e._v(" "),a("el-form-item",{attrs:{label:"",prop:"countryCode"}},[a("el-input",{attrs:{placeholder:""},model:{value:e.dataForm.countryCode,callback:function(t){e.$set(e.dataForm,"countryCode",t)},expression:"dataForm.countryCode"}})],1),e._v(" "),a("el-form-item",{attrs:{label:"",prop:"phone"}},[a("el-input",{attrs:{placeholder:""},model:{value:e.dataForm.phone,callback:function(t){e.$set(e.dataForm,"phone",t)},expression:"dataForm.phone"}})],1),e._v(" "),a("el-form-item",{attrs:{label:"",prop:"proxy"}},[a("el-input",{attrs:{placeholder:""},model:{value:e.dataForm.proxy,callback:function(t){e.$set(e.dataForm,"proxy",t)},expression:"dataForm.proxy"}})],1),e._v(" "),a("el-form-item",{attrs:{label:"",prop:"txtToken"}},[a("el-input",{attrs:{placeholder:""},model:{value:e.dataForm.txtToken,callback:function(t){e.$set(e.dataForm,"txtToken",t)},expression:"dataForm.txtToken"}})],1),e._v(" "),a("el-form-item",{attrs:{label:"",prop:"taskId"}},[a("el-input",{attrs:{placeholder:""},model:{value:e.dataForm.taskId,callback:function(t){e.$set(e.dataForm,"taskId",t)},expression:"dataForm.taskId"}})],1),e._v(" "),a("el-form-item",{attrs:{label:"",prop:"smsCode"}},[a("el-input",{attrs:{placeholder:""},model:{value:e.dataForm.smsCode,callback:function(t){e.$set(e.dataForm,"smsCode",t)},expression:"dataForm.smsCode"}})],1),e._v(" "),a("el-form-item",{attrs:{label:"",prop:"registerStatus"}},[a("el-input",{attrs:{placeholder:""},model:{value:e.dataForm.registerStatus,callback:function(t){e.$set(e.dataForm,"registerStatus",t)},expression:"dataForm.registerStatus"}})],1),e._v(" "),a("el-form-item",{attrs:{label:"",prop:"deleteFlag"}},[a("el-input",{attrs:{placeholder:""},model:{value:e.dataForm.deleteFlag,callback:function(t){e.$set(e.dataForm,"deleteFlag",t)},expression:"dataForm.deleteFlag"}})],1),e._v(" "),a("el-form-item",{attrs:{label:"",prop:"createTime"}},[a("el-input",{attrs:{placeholder:""},model:{value:e.dataForm.createTime,callback:function(t){e.$set(e.dataForm,"createTime",t)},expression:"dataForm.createTime"}})],1)],1),e._v(" "),a("span",{staticClass:"dialog-footer",attrs:{slot:"footer"},slot:"footer"},[a("el-button",{on:{click:function(t){e.visible=!1}}},[e._v("取消")]),e._v(" "),a("el-button",{attrs:{type:"primary"},on:{click:function(t){e.dataFormSubmit()}}},[e._v("确定")])],1)],1)},staticRenderFns:[]},n=a("46Yf")(r,o,!1,null,null,null);t.default=n.exports},QF3l:function(e,t,a){"use strict";Object.defineProperty(t,"__esModule",{value:!0});var r={data:function(){return{registerStatus:null,countryCode:null,countryCodeOptions:[{label:"th"},{label:"jp"}],workOptions:[{value:1,label:"发起注册"},{value:2,label:"等待验证码"},{value:3,label:"提交验证码"},{value:4,label:"注册成功"},{value:5,label:"注册出现问题"},{value:7,label:"已经拉过群了"},{value:8,label:"账号被二次接码"}],dataForm:{phone:""},dataList:[],pageIndex:1,pageSize:10,totalPage:0,dataListLoading:!1,dataListSelections:[],addOrUpdateVisible:!1}},components:{AddOrUpdate:a("5XyS").default},activated:function(){this.getDataList()},methods:{getDataList:function(){var e=this;this.dataListLoading=!0,this.$http({url:this.$http.adornUrl("/ltt/cdlineregister/list"),method:"get",params:this.$http.adornParams({page:this.pageIndex,limit:this.pageSize,registerStatus:this.registerStatus,phone:this.dataForm.phone,countryCode:this.countryCode,key:this.dataForm.key})}).then(function(t){var a=t.data;a&&0===a.code?(e.dataList=a.page.list,e.totalPage=a.page.totalCount):(e.dataList=[],e.totalPage=0),e.dataListLoading=!1})},sizeChangeHandle:function(e){this.pageSize=e,this.pageIndex=1,this.getDataList()},currentChangeHandle:function(e){this.pageIndex=e,this.getDataList()},selectionChangeHandle:function(e){this.dataListSelections=e},addOrUpdateHandle:function(e){var t=this;this.addOrUpdateVisible=!0,this.$nextTick(function(){t.$refs.addOrUpdate.init(e)})},deleteHandle:function(e){var t=this,a=e?[e]:this.dataListSelections.map(function(e){return e.id});this.$confirm("确定对[id="+a.join(",")+"]进行["+(e?"删除":"批量删除")+"]操作?","提示",{confirmButtonText:"确定",cancelButtonText:"取消",type:"warning"}).then(function(){t.$http({url:t.$http.adornUrl("/ltt/cdlineregister/delete"),method:"post",data:t.$http.adornData(a,!1)}).then(function(e){var a=e.data;a&&0===a.code?t.$message({message:"操作成功",type:"success",duration:1500,onClose:function(){t.getDataList()}}):t.$message.error(a.msg)})})},issueLiffViewHandle:function(e){var t=this,a=e?[e]:this.dataListSelections.map(function(e){return e.id});this.$confirm("确定对[id="+a.join(",")+"]进行["+(e?"检测状态":"批量检测状态")+"]操作?","提示",{confirmButtonText:"确定",cancelButtonText:"取消",type:"warning"}).then(function(){t.$http({url:t.$http.adornUrl("/ltt/cdlineregister/issueLiffView"),method:"post",data:t.$http.adornData(a,!1)}).then(function(e){var a=e.data;a&&0===a.code?t.$message({message:"操作成功",type:"success",duration:1500,onClose:function(){t.getDataList()}}):t.$message.error(a.msg)})})},clearProxyHandle:function(){var e=this;this.$http({url:this.$http.adornUrl("/ltt/cdlineregister/clearProxy"),method:"post"}).then(function(t){var a=t.data;a&&0===a.code?e.$message({message:"操作成功",type:"success",duration:1500,onClose:function(){e.getDataList()}}):e.$message.error(a.msg)})},importTokenHandle:function(e){var t=this,a=e?[e]:this.dataListSelections.map(function(e){return e.id});this.$confirm("确定对[id="+a.join(",")+"]进行["+(e?"导出token":"批量导出token")+"]操作?","提示",{confirmButtonText:"确定",cancelButtonText:"取消",type:"warning"}).then(function(){var e=a.join(",");console.log(e),window.open(t.$http.adornUrl("/ltt/cdlineregister/importToken?token="+t.$cookie.get("token")+"&ids="+e))})}}},o={render:function(){var e=this,t=e.$createElement,a=e._self._c||t;return a("div",{staticClass:"mod-config"},[a("el-form",{attrs:{inline:!0,model:e.dataForm},nativeOn:{keyup:function(t){if(!("button"in t)&&e._k(t.keyCode,"enter",13,t.key))return null;e.getDataList()}}},[a("el-form-item",[a("el-input",{attrs:{placeholder:"手机号",clearable:""},model:{value:e.dataForm.phone,callback:function(t){e.$set(e.dataForm,"phone",t)},expression:"dataForm.phone"}})],1),e._v(" "),a("el-form-item",[a("el-select",{attrs:{placeholder:"注册状态",clearable:""},model:{value:e.registerStatus,callback:function(t){e.registerStatus=t},expression:"registerStatus"}},e._l(e.workOptions,function(e){return a("el-option",{key:e.value,attrs:{label:e.label,value:e.value}})}))],1),e._v(" "),a("el-form-item",[a("el-select",{attrs:{placeholder:"国家",clearable:""},model:{value:e.countryCode,callback:function(t){e.countryCode=t},expression:"countryCode"}},e._l(e.countryCodeOptions,function(e){return a("el-option",{key:e.label,attrs:{label:e.label,value:e.label}})}))],1),e._v(" "),a("el-form-item",[a("el-button",{on:{click:function(t){e.getDataList()}}},[e._v("查询")]),e._v(" "),e.isAuth("ltt:cdlineregister:delete")?a("el-button",{attrs:{type:"danger",disabled:e.dataListSelections.length<=0},on:{click:function(t){e.issueLiffViewHandle()}}},[e._v("批量检测")]):e._e(),e._v(" "),e.isAuth("ltt:cdlineregister:delete")?a("el-button",{attrs:{type:"danger",disabled:e.dataListSelections.length<=0},on:{click:function(t){e.importTokenHandle()}}},[e._v("导出token")]):e._e(),e._v(" "),a("el-button",{attrs:{type:"danger"},on:{click:function(t){e.clearProxyHandle()}}},[e._v("清空proxy")])],1)],1),e._v(" "),a("el-table",{directives:[{name:"loading",rawName:"v-loading",value:e.dataListLoading,expression:"dataListLoading"}],staticStyle:{width:"100%"},attrs:{data:e.dataList,border:""},on:{"selection-change":e.selectionChangeHandle}},[a("el-table-column",{attrs:{type:"selection","header-align":"center",align:"center",width:"50"}}),e._v(" "),a("el-table-column",{attrs:{prop:"appVersion","header-align":"center",align:"center",label:"app版本"}}),e._v(" "),a("el-table-column",{attrs:{prop:"countryCode","header-align":"center",align:"center",label:"国家代码"}}),e._v(" "),a("el-table-column",{attrs:{prop:"phone","header-align":"center",align:"center",label:"手机号"}}),e._v(" "),a("el-table-column",{attrs:{prop:"proxy","header-align":"center",align:"center",label:"代理"}}),e._v(" "),a("el-table-column",{attrs:{prop:"smsCode","header-align":"center",align:"center",label:"验证码"}}),e._v(" "),a("el-table-column",{attrs:{prop:"registerStatusStr","header-align":"center",align:"center",label:"注册状态"}}),e._v(" "),a("el-table-column",{attrs:{prop:"token","header-align":"center",align:"center",label:"失败原因"}}),e._v(" "),a("el-table-column",{attrs:{prop:"createTime","header-align":"center",align:"center",label:"时间"}})],1),e._v(" "),a("el-pagination",{attrs:{"current-page":e.pageIndex,"page-sizes":[10,20,50,100],"page-size":e.pageSize,total:e.totalPage,layout:"total, sizes, prev, pager, next, jumper"},on:{"size-change":e.sizeChangeHandle,"current-change":e.currentChangeHandle}}),e._v(" "),e.addOrUpdateVisible?a("add-or-update",{ref:"addOrUpdate",on:{refreshDataList:e.getDataList}}):e._e()],1)},staticRenderFns:[]},n=a("46Yf")(r,o,!1,null,null,null);t.default=n.exports},"cdA+":function(e,t,a){"use strict";Object.defineProperty(t,"__esModule",{value:!0});var r=a("0xDb"),o={data:function(){var e=this;return{visible:!1,dataForm:{password:"",newPassword:"",confirmPassword:""},dataRule:{password:[{required:!0,message:"原密码不能为空",trigger:"blur"}],newPassword:[{required:!0,message:"新密码不能为空",trigger:"blur"}],confirmPassword:[{required:!0,message:"确认密码不能为空",trigger:"blur"},{validator:function(t,a,r){e.dataForm.newPassword!==a?r(new Error("确认密码与新密码不一致")):r()},trigger:"blur"}]}}},computed:{userName:{get:function(){return this.$store.state.user.name}},mainTabs:{get:function(){return this.$store.state.common.mainTabs},set:function(e){this.$store.commit("common/updateMainTabs",e)}}},methods:{init:function(){var e=this;this.visible=!0,this.$nextTick(function(){e.$refs.dataForm.resetFields()})},dataFormSubmit:function(){var e=this;this.$refs.dataForm.validate(function(t){t&&e.$http({url:e.$http.adornUrl("/sys/user/password"),method:"post",data:e.$http.adornData({password:e.dataForm.password,newPassword:e.dataForm.newPassword})}).then(function(t){var a=t.data;a&&0===a.code?e.$message({message:"操作成功",type:"success",duration:1500,onClose:function(){e.visible=!1,e.$nextTick(function(){e.mainTabs=[],Object(r.a)(),e.$router.replace({name:"login"})})}}):e.$message.error(a.msg)})})}}},n={render:function(){var e=this,t=e.$createElement,a=e._self._c||t;return a("el-dialog",{attrs:{title:"修改密码",visible:e.visible,"append-to-body":!0},on:{"update:visible":function(t){e.visible=t}}},[a("el-form",{ref:"dataForm",attrs:{model:e.dataForm,rules:e.dataRule,"label-width":"80px"},nativeOn:{keyup:function(t){if(!("button"in t)&&e._k(t.keyCode,"enter",13,t.key))return null;e.dataFormSubmit()}}},[a("el-form-item",{attrs:{label:"账号"}},[a("span",[e._v(e._s(e.userName))])]),e._v(" "),a("el-form-item",{attrs:{label:"原密码",prop:"password"}},[a("el-input",{attrs:{type:"password"},model:{value:e.dataForm.password,callback:function(t){e.$set(e.dataForm,"password",t)},expression:"dataForm.password"}})],1),e._v(" "),a("el-form-item",{attrs:{label:"新密码",prop:"newPassword"}},[a("el-input",{attrs:{type:"password"},model:{value:e.dataForm.newPassword,callback:function(t){e.$set(e.dataForm,"newPassword",t)},expression:"dataForm.newPassword"}})],1),e._v(" "),a("el-form-item",{attrs:{label:"确认密码",prop:"confirmPassword"}},[a("el-input",{attrs:{type:"password"},model:{value:e.dataForm.confirmPassword,callback:function(t){e.$set(e.dataForm,"confirmPassword",t)},expression:"dataForm.confirmPassword"}})],1)],1),e._v(" "),a("span",{staticClass:"dialog-footer",attrs:{slot:"footer"},slot:"footer"},[a("el-button",{on:{click:function(t){e.visible=!1}}},[e._v("取消")]),e._v(" "),a("el-button",{attrs:{type:"primary"},on:{click:function(t){e.dataFormSubmit()}}},[e._v("确定")])],1)],1)},staticRenderFns:[]},s=a("46Yf")(o,n,!1,null,null,null);t.default=s.exports},oZaA:function(e,t,a){"use strict";Object.defineProperty(t,"__esModule",{value:!0});var r={name:"sub-menu",props:{menu:{type:Object,required:!0},dynamicMenuRoutes:{type:Array,required:!0}},components:{SubMenu:s},computed:{sidebarLayoutSkin:{get:function(){return this.$store.state.common.sidebarLayoutSkin}}},methods:{gotoRouteHandle:function(e){var t=this.dynamicMenuRoutes.filter(function(t){return t.meta.menuId===e.menuId});t.length>=1&&this.$router.push({name:t[0].name})}}},o={render:function(){var e=this,t=e.$createElement,a=e._self._c||t;return e.menu.list&&e.menu.list.length>=1?a("el-submenu",{attrs:{index:e.menu.menuId+"","popper-class":"site-sidebar--"+e.sidebarLayoutSkin+"-popper"}},[a("template",{attrs:{slot:"title"},slot:"title"},[a("icon-svg",{staticClass:"site-sidebar__menu-icon",attrs:{name:e.menu.icon||""}}),e._v(" "),a("span",[e._v(e._s(e.menu.name))])],1),e._v(" "),e._l(e.menu.list,function(t){return a("sub-menu",{key:t.menuId,attrs:{menu:t,dynamicMenuRoutes:e.dynamicMenuRoutes}})})],2):a("el-menu-item",{attrs:{index:e.menu.menuId+""},on:{click:function(t){e.gotoRouteHandle(e.menu)}}},[a("icon-svg",{staticClass:"site-sidebar__menu-icon",attrs:{name:e.menu.icon||""}}),e._v(" "),a("span",[e._v(e._s(e.menu.name))])],1)},staticRenderFns:[]},n=a("46Yf")(r,o,!1,null,null,null),s=t.default=n.exports}});