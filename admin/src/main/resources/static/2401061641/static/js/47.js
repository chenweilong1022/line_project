webpackJsonp([47],{gfcZ:function(e,t,a){"use strict";Object.defineProperty(t,"__esModule",{value:!0});var l={render:function(){var e=this,t=e.$createElement,a=e._self._c||t;return a("el-dialog",{attrs:{width:"70%",title:(e.dataForm.id,"拉群详情"),"close-on-click-modal":!1,visible:e.visible},on:{"update:visible":function(t){e.visible=t}}},[a("div",{staticClass:"mod-config"},[a("el-form",{attrs:{inline:!0,model:e.dataForm},nativeOn:{keyup:function(t){if(!("button"in t)&&e._k(t.keyCode,"enter",13,t.key))return null;e.getDataList()}}},[a("el-form-item",[a("el-input",{attrs:{placeholder:"参数名",clearable:""},model:{value:e.dataForm.key,callback:function(t){e.$set(e.dataForm,"key",t)},expression:"dataForm.key"}})],1),e._v(" "),a("el-form-item",[a("el-select",{attrs:{placeholder:"拉群状态",clearable:""},model:{value:e.subtaskStatus,callback:function(t){e.subtaskStatus=t},expression:"subtaskStatus"}},e._l(e.workOptions,function(e){return a("el-option",{key:e.value,attrs:{label:e.label,value:e.value}})}))],1),e._v(" "),a("el-form-item",[a("el-button",{on:{click:function(t){e.getDataList()}}},[e._v("查询")])],1)],1),e._v(" "),a("el-table",{directives:[{name:"loading",rawName:"v-loading",value:e.dataListLoading,expression:"dataListLoading"}],staticStyle:{width:"100%"},attrs:{data:e.dataList,border:""},on:{"selection-change":e.selectionChangeHandle}},[a("el-table-column",{attrs:{type:"selection","header-align":"center",align:"center",width:"50"}}),e._v(" "),a("el-table-column",{attrs:{prop:"proxy","header-align":"center",align:"center",label:"代理"}}),e._v(" "),a("el-table-column",{attrs:{prop:"phone","header-align":"center",align:"center",label:"手机号"}}),e._v(" "),a("el-table-column",{attrs:{prop:"mid","header-align":"center",align:"center",label:"mid"}}),e._v(" "),a("el-table-column",{attrs:{prop:"searchStatusStr","header-align":"center",align:"center",label:"搜索状态"}}),e._v(" "),a("el-table-column",{attrs:{prop:"subtaskStatusStr","header-align":"center",align:"center",label:"拉群状态"}}),e._v(" "),a("el-table-column",{attrs:{prop:"errMsg","header-align":"center",align:"center",label:"失败原因"}}),e._v(" "),a("el-table-column",{attrs:{prop:"executionTime","header-align":"center",align:"center",label:"添加时间"}})],1),e._v(" "),a("el-pagination",{attrs:{"current-page":e.pageIndex,"page-sizes":[10,20,50,100],"page-size":e.pageSize,total:e.totalPage,layout:"total, sizes, prev, pager, next, jumper"},on:{"size-change":e.sizeChangeHandle,"current-change":e.currentChangeHandle}})],1)])},staticRenderFns:[]},n=a("46Yf")({data:function(){return{subtaskStatus:null,workOptions:[{value:1,label:"发起拉群"},{value:2,label:"子任务进行中"},{value:4,label:"子任务完成"},{value:5,label:"子任务失败"}],dataList:[],pageIndex:1,pageSize:10,totalPage:0,dataListLoading:!1,dataListSelections:[],addOrUpdateVisible:!1,visible:!1,dataForm:{id:0,groupName:"",proxy:"",intervals:"",roomId:"",chatRoomUrl:"",roomTicketId:"",uploadGroupNumber:"",currentExecutionsNumber:"",successfullyAttractGroupsNumber:"",groupStatus:"",txtUrl:"",deleteFlag:"",createTime:""}}},methods:{getDataList:function(e){var t=this;this.dataListLoading=!0,this.$http({url:this.$http.adornUrl("/ltt/cdgroupsubtasks/list"),method:"get",params:this.$http.adornParams({page:this.pageIndex,limit:this.pageSize,groupTasksId:this.id,subtaskStatus:this.subtaskStatus,key:this.dataForm.key})}).then(function(e){var a=e.data;a&&0===a.code?(t.dataList=a.page.list,t.totalPage=a.page.totalCount):(t.dataList=[],t.totalPage=0),t.dataListLoading=!1})},handleAvatarSuccess:function(e,t){this.dataForm.txtUrl=e.data},sizeChangeHandle:function(e){this.pageSize=e,this.pageIndex=1,this.getDataList()},currentChangeHandle:function(e){this.pageIndex=e,this.getDataList()},selectionChangeHandle:function(e){this.dataListSelections=e},init:function(e){this.dataForm.id=e||0,this.visible=!0,this.getDataList()}}},l,!1,null,null,null);t.default=n.exports}});