<template>
  <el-dialog
    :title="!dataForm.id ? '新增' : '修改'"
    :close-on-click-modal="false"
    :visible.sync="visible">
    <el-form :model="dataForm" :rules="dataRule" ref="dataForm" @keyup.enter.native="dataFormSubmit()" label-width="80px">
    <el-form-item label="" prop="groupTasksId">
      <el-input v-model="dataForm.groupTasksId" placeholder=""></el-input>
    </el-form-item>
    <el-form-item label="" prop="proxy">
      <el-input v-model="dataForm.proxy" placeholder=""></el-input>
    </el-form-item>
    <el-form-item label="" prop="phone">
      <el-input v-model="dataForm.phone" placeholder=""></el-input>
    </el-form-item>
    <el-form-item label="" prop="mid">
      <el-input v-model="dataForm.mid" placeholder=""></el-input>
    </el-form-item>
    <el-form-item label="" prop="searchStatus">
      <el-input v-model="dataForm.searchStatus" placeholder=""></el-input>
    </el-form-item>
    <el-form-item label="" prop="addStatus">
      <el-input v-model="dataForm.addStatus" placeholder=""></el-input>
    </el-form-item>
    <el-form-item label="" prop="subtaskStatus">
      <el-input v-model="dataForm.subtaskStatus" placeholder=""></el-input>
    </el-form-item>
    <el-form-item label="" prop="executionTime">
      <el-input v-model="dataForm.executionTime" placeholder=""></el-input>
    </el-form-item>
    <el-form-item label="" prop="deleteFlag">
      <el-input v-model="dataForm.deleteFlag" placeholder=""></el-input>
    </el-form-item>
    <el-form-item label="" prop="createTime">
      <el-input v-model="dataForm.createTime" placeholder=""></el-input>
    </el-form-item>
    </el-form>
    <span slot="footer" class="dialog-footer">
      <el-button @click="visible = false">取消</el-button>
      <el-button type="primary" @click="dataFormSubmit()">确定</el-button>
    </span>
  </el-dialog>
</template>

<script>
  export default {
    data () {
      return {
        visible: false,
        dataForm: {
          id: 0,
          groupTasksId: '',
          proxy: '',
          phone: '',
          mid: '',
          searchStatus: '',
          addStatus: '',
          subtaskStatus: '',
          executionTime: '',
          deleteFlag: '',
          createTime: ''
        },
        dataRule: {
          groupTasksId: [
            { required: true, message: '不能为空', trigger: 'blur' }
          ],
          proxy: [
            { required: true, message: '不能为空', trigger: 'blur' }
          ],
          phone: [
            { required: true, message: '不能为空', trigger: 'blur' }
          ],
          mid: [
            { required: true, message: '不能为空', trigger: 'blur' }
          ],
          searchStatus: [
            { required: true, message: '不能为空', trigger: 'blur' }
          ],
          addStatus: [
            { required: true, message: '不能为空', trigger: 'blur' }
          ],
          subtaskStatus: [
            { required: true, message: '不能为空', trigger: 'blur' }
          ],
          executionTime: [
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
    methods: {
      init (id) {
        this.dataForm.id = id || 0
        this.visible = true
        this.$nextTick(() => {
          this.$refs['dataForm'].resetFields()
          if (this.dataForm.id) {
            this.$http({
              url: this.$http.adornUrl(`/ltt/cdgroupsubtasks/info/${this.dataForm.id}`),
              method: 'get',
              params: this.$http.adornParams()
            }).then(({data}) => {
              if (data && data.code === 0) {
                this.dataForm.groupTasksId = data.cdgroupsubtasks.groupTasksId
                this.dataForm.proxy = data.cdgroupsubtasks.proxy
                this.dataForm.phone = data.cdgroupsubtasks.phone
                this.dataForm.mid = data.cdgroupsubtasks.mid
                this.dataForm.searchStatus = data.cdgroupsubtasks.searchStatus
                this.dataForm.addStatus = data.cdgroupsubtasks.addStatus
                this.dataForm.subtaskStatus = data.cdgroupsubtasks.subtaskStatus
                this.dataForm.executionTime = data.cdgroupsubtasks.executionTime
                this.dataForm.deleteFlag = data.cdgroupsubtasks.deleteFlag
                this.dataForm.createTime = data.cdgroupsubtasks.createTime
              }
            })
          }
        })
      },
      // 表单提交
      dataFormSubmit () {
        this.$refs['dataForm'].validate((valid) => {
          if (valid) {
            this.$http({
              url: this.$http.adornUrl(`/ltt/cdgroupsubtasks/${!this.dataForm.id ? 'save' : 'update'}`),
              method: 'post',
              data: this.$http.adornData({
                'id': this.dataForm.id || undefined,
                'groupTasksId': this.dataForm.groupTasksId,
                'proxy': this.dataForm.proxy,
                'phone': this.dataForm.phone,
                'mid': this.dataForm.mid,
                'searchStatus': this.dataForm.searchStatus,
                'addStatus': this.dataForm.addStatus,
                'subtaskStatus': this.dataForm.subtaskStatus,
                'executionTime': this.dataForm.executionTime,
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
