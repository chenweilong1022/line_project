<template>
  <el-dialog
    width="70%"
    :title="!dataForm.id ? '注册详情' : '注册详情'"
    :close-on-click-modal="false"
    :visible.sync="visible">
    <div class="mod-config">
      <el-form :inline="true" :model="dataForm" @keyup.enter.native="getDataList()">

        <el-form-item>
          <el-input v-model="dataForm.key" placeholder="参数名" clearable></el-input>
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
          prop="appVersion"
          header-align="center"
          align="center"
          label="app版本号">
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
          prop="proxy"
          header-align="center"
          align="center"
          label="注册代理">
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
          prop="errMsg"
          header-align="center"
          align="center"
          label="失败原因">
        </el-table-column>
        <el-table-column
          prop="createTime"
          header-align="center"
          align="center"
          label="时间">
        </el-table-column>
<!--        <el-table-column-->
<!--          fixed="right"-->
<!--          header-align="center"-->
<!--          align="center"-->
<!--          width="150"-->
<!--          label="操作">-->
<!--          <template slot-scope="scope">-->
<!--            <el-button type="text" size="small" @click="addOrUpdateHandle(scope.row.id)">修改</el-button>-->
<!--            <el-button type="text" size="small" @click="deleteHandle(scope.row.id)">删除</el-button>-->
<!--          </template>-->
<!--        </el-table-column>-->
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
  </el-dialog>
</template>

<script>
 import CdlineRegisterList from './cdlineregister'
 import AddOrUpdate from "./cdlineregister-add-or-update.vue";
  export default {
    data () {
      return {
        registerStatus: null,
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
          totalAmount: '',
          numberThreads: '',
          numberRegistered: '',
          numberSuccesses: '',
          numberFailures: '',
          registrationStatus: '',
          deleteFlag: '',
          createTime: ''
        },
        dataRule: {
          totalAmount: [
            { required: true, message: '不能为空', trigger: 'blur' }
          ],
          numberThreads: [
            { required: true, message: '不能为空', trigger: 'blur' }
          ],
          numberRegistered: [
            { required: true, message: '不能为空', trigger: 'blur' }
          ],
          numberSuccesses: [
            { required: true, message: '不能为空', trigger: 'blur' }
          ],
          numberFailures: [
            { required: true, message: '不能为空', trigger: 'blur' }
          ],
          registrationStatus: [
            { required: true, message: '不能为空', trigger: 'blur' }
          ],
          deleteFlag: [
            { required: true, message: '不能为空', trigger: 'blur' }
          ],
          createTime: [
            { required: true, message: '不能为空', trigger: 'blur' }
          ]
        }
      }
    },
    components: {
      AddOrUpdate,
      CdlineRegisterList
    },
    methods: {
      // 获取数据列表
      getDataList () {
        this.dataListLoading = true
        this.$http({
          url: this.$http.adornUrl('/ltt/cdlineregister/listByTaskId'),
          method: 'get',
          params: this.$http.adornParams({
            'page': this.pageIndex,
            'limit': this.pageSize,
            'registerStatus': this.registerStatus,
            'tasksId': this.dataForm.id
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
      init (id) {
        this.dataForm.id = id || 0
        this.visible = true
        this.getDataList();
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
      // 表单提交
      dataFormSubmit () {
        this.$refs['dataForm'].validate((valid) => {
          if (valid) {
            this.$http({
              url: this.$http.adornUrl(`/ltt/cdregistertask/${!this.dataForm.id ? 'save' : 'update'}`),
              method: 'post',
              data: this.$http.adornData({
                'id': this.dataForm.id || undefined,
                'totalAmount': this.dataForm.totalAmount,
                'numberThreads': this.dataForm.numberThreads,
                'numberRegistered': this.dataForm.numberRegistered,
                'numberSuccesses': this.dataForm.numberSuccesses,
                'numberFailures': this.dataForm.numberFailures,
                'registrationStatus': this.dataForm.registrationStatus,
                'deleteFlag': this.dataForm.deleteFlag,
                'createTime': this.dataForm.createTime
              })
            }).then(({data}) => {
              if (data && data.code === 0) {
                this.$message({
                  message: '操作成功',
                  type: 'success',
                  duration: 1500,
                  onClose: () => {
                    this.visible = false
                    this.$emit('refreshDataList')
                  }
                })
              } else {
                this.$message.error(data.msg)
              }
            })
          }
        })
      }
    }
  }
</script>
