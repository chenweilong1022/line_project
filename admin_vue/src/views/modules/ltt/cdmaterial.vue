<template>
  <div class="mod-config">
    <el-form :inline="true" :model="dataForm" @keyup.enter.native="getDataList()">
      <el-form-item>
        <el-input v-model="dataForm.key" placeholder="参数名" clearable></el-input>
      </el-form-item>
      <el-form-item>
        <el-button @click="getDataList()">查询</el-button>
        <el-button v-if="isAuth('ltt:cdmaterial:save')" type="primary" @click="addOrUpdateHandle()">新增</el-button>
        <el-button v-if="isAuth('ltt:cdmaterial:delete')" type="danger" @click="deleteHandle()" :disabled="dataListSelections.length <= 0">批量删除</el-button>
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
        prop="remark"
        header-align="center"
        align="center"
        label="标题">
      </el-table-column>
      <el-table-column
        prop="uploadNumber"
        header-align="center"
        align="center"
        label="上传数量">
      </el-table-column>
      <el-table-column
        prop="materialNumber"
        header-align="center"
        align="center"
        label="料子数量">
      </el-table-column>
      <el-table-column
        prop="navyNumber"
        header-align="center"
        align="center"
        label="水军数量">
      </el-table-column>


      <el-table-column
        prop="synchronizationsNumber"
        header-align="center"
        align="center"
        label="同步数量">
      </el-table-column>
      <el-table-column
        prop="successesNumber"
        header-align="center"
        align="center"
        label="成功数量">
      </el-table-column>
      <el-table-column
        prop="failuresNumber"
        header-align="center"
        align="center"
        label="失败数量">
      </el-table-column>
      <el-table-column
        prop="createTime"
        header-align="center"
        align="center"
        label="时间">
      </el-table-column>
      <el-table-column
        fixed="right"
        header-align="center"
        align="center"
        width="150"
        label="操作">
        <template slot-scope="scope">
          <el-button type="text" size="small" @click="autoGroupHandle(scope.row.id)">自动分配</el-button>
          <el-button type="text" size="small" @click="importZipHandle(scope.row.id)">导出报表</el-button>
        </template>
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
    <!-- 弹窗, 新增 / 修改 -->
    <add-or-update v-if="addOrUpdateVisible" ref="addOrUpdate" @refreshDataList="getDataList"></add-or-update>
    <auto-group v-if="autoGroupVisible" ref="autoGroup" @refreshDataList="getDataList"></auto-group>
    <import-zip v-if="importZipVisible" ref="importZip" @refreshDataList="getDataList" ></import-zip>
  </div>
</template>

<script>
  import AddOrUpdate from './cdmaterial-add-or-update'
  import AutoGroup from './cdmaterial-auto-group'
  import ImportZip from './cdmaterial-import-zip'
  export default {
    data () {
      return {
        dataForm: {
          key: ''
        },
        dataList: [],
        pageIndex: 1,
        pageSize: 10,
        totalPage: 0,
        dataListLoading: false,
        dataListSelections: [],
        addOrUpdateVisible: false,
        importZipVisible: false,
        autoGroupVisible: false
      }
    },
    components: {
      AddOrUpdate,
      AutoGroup,
      ImportZip
    },
    activated () {
      this.getDataList()
    },
    methods: {
      // 获取数据列表
      getDataList () {
        this.dataListLoading = true
        this.$http({
          url: this.$http.adornUrl('/ltt/cdmaterial/list'),
          method: 'get',
          params: this.$http.adornParams({
            'page': this.pageIndex,
            'limit': this.pageSize,
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
      // 新增 / 修改
      addOrUpdateHandle (id) {
        this.addOrUpdateVisible = true
        this.$nextTick(() => {
          this.$refs.addOrUpdate.init(id)
        })
      },
      // 新增 / 修改
      autoGroupHandle (id) {
        this.autoGroupVisible = true
        this.$nextTick(() => {
          this.$refs.autoGroup.init(id)
        })
      },
      // 新增 / 修改
      importZipHandle (id) {
        this.importZipVisible = true
        this.$nextTick(() => {
          this.$refs.importZip.init(id)
        })
      },
      // 删除
      deleteHandle (id) {
        var ids = id ? [id] : this.dataListSelections.map(item => {
          return item.id
        })
        this.$confirm(`确定对[id=${ids.join(',')}]进行[${id ? '删除' : '批量删除'}]操作?`, '提示', {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'warning'
        }).then(() => {
          this.$http({
            url: this.$http.adornUrl('/ltt/cdmaterial/delete'),
            method: 'post',
            data: this.$http.adornData(ids, false)
          }).then(({data}) => {
            if (data && data.code === 0) {
              this.$message({
                message: '操作成功',
                type: 'success',
                duration: 1500,
                onClose: () => {
                  this.getDataList()
                }
              })
            } else {
              this.$message.error(data.msg)
            }
          })
        })
      }
    }
  }
</script>
