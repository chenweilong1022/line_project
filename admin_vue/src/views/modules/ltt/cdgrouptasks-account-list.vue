<template>
  <el-dialog
    width="70%"
    :title="!dataForm.id ? '拉群详情' : '拉群详情'"
    :close-on-click-modal="false"
    :visible.sync="visible">

    <div class="mod-config">

      <el-form :inline="true" :model="dataForm" @keyup.enter.native="getDataList()">
        <el-form-item>
          <el-input v-model="dataForm.key" placeholder="参数名" clearable></el-input>
        </el-form-item>

        <el-form-item>
          <el-select v-model="subtaskStatus" placeholder="拉群状态" clearable>
            <el-option
              v-for="item in workOptions"
              :key="item.value"
              :label="item.label"
              :value="item.value">
            </el-option>
          </el-select>
        </el-form-item>

        <el-form-item>
          <el-button @click="getDataList()">查询</el-button>
        </el-form-item>

      </el-form>

      <el-table
        :data="dataList"
        border
        v-loading="dataListLoading"
        @selection-change="selectionChangeHandle"
        style="width: 100%;">
        <el-table-column
          type="selection"
          header-align="center"
          align="center"
          width="50">
        </el-table-column>
        <el-table-column
          prop="proxy"
          header-align="center"
          align="center"
          label="代理">
        </el-table-column>
        <el-table-column
          prop="phone"
          header-align="center"
          align="center"
          label="手机号">
        </el-table-column>
        <el-table-column
          prop="mid"
          header-align="center"
          align="center"
          label="mid">
        </el-table-column>
        <el-table-column
          prop="searchStatusStr"
          header-align="center"
          align="center"
          label="搜索状态">
        </el-table-column>
        <el-table-column
          prop="subtaskStatusStr"
          header-align="center"
          align="center"
          label="拉群状态">
        </el-table-column>
        <el-table-column
          prop="errMsg"
          header-align="center"
          align="center"
          label="失败原因">
        </el-table-column>
        <el-table-column
          prop="executionTime"
          header-align="center"
          align="center"
          label="添加时间">
        </el-table-column>
      </el-table>
      <el-pagination
        @size-change="sizeChangeHandle"
        @current-change="currentChangeHandle"
        :current-page="pageIndex"
        :page-sizes="[10, 20, 50, 100]"
        :page-size="pageSize"
        :total="totalPage"
        layout="total, sizes, prev, pager, next, jumper">
      </el-pagination>
    </div>
  </el-dialog>
</template>

<script>
  export default {
    data () {
      return {
        subtaskStatus: null,
        workOptions: [
          {
            value: 1,
            label: '发起拉群'
          },
          {
            value: 2,
            label: '子任务进行中'
          },
          {
            value: 4,
            label: '子任务完成'
          },
          {
            value: 5,
            label: '子任务失败'
          }
        ],
        dataList: [],
        pageIndex: 1,
        pageSize: 10,
        totalPage: 0,
        dataListLoading: false,
        dataListSelections: [],
        addOrUpdateVisible: false,
        visible: false,
        dataForm: {
          id: 0,
          groupName: '',
          proxy: '',
          intervals: '',
          roomId: '',
          chatRoomUrl: '',
          roomTicketId: '',
          uploadGroupNumber: '',
          currentExecutionsNumber: '',
          successfullyAttractGroupsNumber: '',
          groupStatus: '',
          txtUrl: '',
          deleteFlag: '',
          createTime: ''
        },
      }
    },
    methods: {
      // 获取数据列表
      getDataList (groupTasksId) {
        this.dataListLoading = true
        this.$http({
          url: this.$http.adornUrl('/ltt/cdgroupsubtasks/list'),
          method: 'get',
          params: this.$http.adornParams({
            'page': this.pageIndex,
            'limit': this.pageSize,
            'groupTasksId': this.id,
            'subtaskStatus': this.subtaskStatus,
            'key': this.dataForm.key
          })
        }).then(({data}) => {
          if (data && data.code === 0) {
            this.dataList = data.page.list
            this.totalPage = data.page.totalCount
          } else {
            this.dataList = []
            this.totalPage = 0
          }
          this.dataListLoading = false
        })
      },
      handleAvatarSuccess (res, file) {
        this.dataForm.txtUrl = res.data
      },
      // 每页数
      sizeChangeHandle (val) {
        this.pageSize = val
        this.pageIndex = 1
        this.getDataList()
      },
      // 当前页
      currentChangeHandle (val) {
        this.pageIndex = val
        this.getDataList()
      },
      // 多选
      selectionChangeHandle (val) {
        this.dataListSelections = val
      },
      init (id) {
        this.dataForm.id = id || 0
        this.visible = true
        this.getDataList();
      },
    }
  }
</script>
