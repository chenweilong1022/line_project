<template>
  <div class="mod-config">
    <el-form :inline="true" :model="dataForm" @keyup.enter.native="getDataList()">
      <el-form-item>
        <el-input v-model="dataForm.phone" placeholder="手机号" clearable></el-input>
      </el-form-item>

      <el-form-item>
        <el-select v-model="registerStatus" placeholder="注册状态" clearable>
          <el-option
            v-for="item in workOptions"
            :key="item.value"
            :label="item.label"
            :value="item.value">
          </el-option>
        </el-select>
      </el-form-item>

      <el-form-item>
        <el-select v-model="countryCode" placeholder="国家" clearable>
          <el-option
            v-for="item in countryCodeOptions"
            :key="item.label"
            :label="item.label"
            :value="item.label">
          </el-option>
        </el-select>
      </el-form-item>

      <el-form-item>
        <el-select v-model="accountExistStatus" placeholder="首次" clearable>
          <el-option
            v-for="item in accountExistStatusOptions"
            :key="item.value"
            :label="item.label"
            :value="item.value">
          </el-option>
        </el-select>
      </el-form-item>

      <el-form-item>
        <el-select v-model="exportStatus" placeholder="导出" clearable>
          <el-option
            v-for="item in exportStatusOptions"
            :key="item.value"
            :label="item.label"
            :value="item.value">
          </el-option>
        </el-select>
      </el-form-item>

      <el-form-item>
        <el-button @click="getDataList()">查询</el-button>
<!--        <el-button v-if="isAuth('ltt:cdlineregister:save')" type="primary" @click="addOrUpdateHandle()">新增</el-button>-->
        <el-button v-if="isAuth('ltt:cdlineregister:delete')" type="danger" @click="issueLiffViewHandle()" :disabled="dataListSelections.length <= 0">批量检测</el-button>
        <el-button v-if="isAuth('ltt:cdlineregister:delete')" type="danger" @click="importTokenHandle()" :disabled="dataListSelections.length <= 0">导出token</el-button>
        <el-button type="danger" @click="clearProxyHandle()">清空proxy</el-button>
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
        prop="appVersion"
        header-align="center"
        align="center"
        label="app版本">
      </el-table-column>
      <el-table-column
        prop="countryCode"
        header-align="center"
        align="center"
        label="国家代码">
      </el-table-column>
      <el-table-column
        prop="phone"
        header-align="center"
        align="center"
        label="手机号">
      </el-table-column>
      <el-table-column
        prop="accountExistStatusStr"
        header-align="center"
        align="center"
        label="首次">
      </el-table-column>
      <el-table-column
        prop="proxy"
        header-align="center"
        align="center"
        label="代理">
      </el-table-column>
      <el-table-column
        prop="smsCode"
        header-align="center"
        align="center"
        label="验证码">
      </el-table-column>
      <el-table-column
        prop="registerStatusStr"
        header-align="center"
        align="center"
        label="注册状态">
      </el-table-column>
      <el-table-column
        prop="token"
        header-align="center"
        align="center"
        label="失败原因">
      </el-table-column>
      <el-table-column
        prop="exportStatusStr"
        header-align="center"
        align="center"
        label="导出">
      </el-table-column>
      <el-table-column
        prop="createTime"
        header-align="center"
        align="center"
        label="时间">
      </el-table-column>
<!--      <el-table-column-->
<!--        fixed="right"-->
<!--        header-align="center"-->
<!--        align="center"-->
<!--        width="150"-->
<!--        label="操作">-->
<!--        <template slot-scope="scope">-->
<!--          <el-button type="text" size="small" @click="addOrUpdateHandle(scope.row.id)">修改</el-button>-->
<!--          <el-button type="text" size="small" @click="deleteHandle(scope.row.id)">删除</el-button>-->
<!--        </template>-->
<!--      </el-table-column>-->
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
  </div>
</template>

<script>
  import AddOrUpdate from './cdlineregister-add-or-update'
  export default {
    data () {
      return {
        registerStatus: 4,
        accountExistStatus: null,
        countryCode: null,
        exportStatus: null,
        countryCodeOptions: [
          {
            label: 'th'
          },
          {
            label: 'jp'
          }
        ],
        accountExistStatusOptions: [
          {
            value: 1,
            label: '首次卡'
          },
          {
            value: 2,
            label: '二次卡'
          }
        ],
        exportStatusOptions: [
          {
            value: 1,
            label: '未导出'
          },
          {
            value: 2,
            label: '已导出'
          }
        ],
        workOptions: [
          {
            value: 1,
            label: '发起注册'
          },
          {
            value: 2,
            label: '等待验证码'
          },
          {
            value: 3,
            label: '提交验证码'
          },
          {
            value: 4,
            label: '注册成功'
          },
          {
            value: 5,
            label: '注册出现问题'
          },
          {
            value: 7,
            label: '已经拉过群了'
          },
          {
            value: 8,
            label: '账号被二次接码'
          }
        ],
        dataForm: {
          phone: ''
        },
        dataList: [],
        pageIndex: 1,
        pageSize: 10,
        totalPage: 0,
        dataListLoading: false,
        dataListSelections: [],
        addOrUpdateVisible: false
      }
    },
    components: {
      AddOrUpdate
    },
    activated () {
      this.getDataList()
    },
    methods: {
      // 获取数据列表
      getDataList () {
        this.dataListLoading = true
        this.$http({
          url: this.$http.adornUrl('/ltt/cdlineregister/list'),
          method: 'get',
          params: this.$http.adornParams({
            'page': this.pageIndex,
            'limit': this.pageSize,
            'registerStatus': this.registerStatus,
            'phone': this.dataForm.phone,
            'countryCode': this.countryCode,
            'accountExistStatus': this.accountExistStatus,
            'exportStatus': this.exportStatus,
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
            url: this.$http.adornUrl('/ltt/cdlineregister/delete'),
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
      },
      // 删除
      issueLiffViewHandle (id) {
        var ids = id ? [id] : this.dataListSelections.map(item => {
          return item.id
        })
        this.$confirm(`确定对[id=${ids.join(',')}]进行[${id ? '检测状态' : '批量检测状态'}]操作?`, '提示', {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'warning'
        }).then(() => {
          this.$http({
            url: this.$http.adornUrl('/ltt/cdlineregister/issueLiffView'),
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
      },
      clearProxyHandle () {
        this.$http({
          url: this.$http.adornUrl('/ltt/cdlineregister/clearProxy'),
          method: 'post'
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
      },
      // 删除
      importTokenHandle (id) {
        var ids = id ? [id] : this.dataListSelections.map(item => {
          return item.id
        })
        this.$confirm(`确定对[id=${ids.join(',')}]进行[${id ? '导出token' : '批量导出token'}]操作?`, '提示', {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'warning'
        }).then(() => {
          var idStr = ids.join(",");
          console.log(idStr)
          window.open(this.$http.adornUrl(`/ltt/cdlineregister/importToken?token=${this.$cookie.get('token')}&ids=${idStr}`));
        })
      }
    }
  }
</script>
