<template>
  <el-dialog
    :title="!dataForm.id ? '新增' : '修改'"
    :close-on-click-modal="false"
    :visible.sync="visible">
    <el-form :model="dataForm" :rules="dataRule" ref="dataForm" @keyup.enter.native="dataFormSubmit()" label-width="80px">
    <el-form-item label="" prop="ab">
      <el-input v-model="dataForm.ab" placeholder=""></el-input>
    </el-form-item>
    <el-form-item label="" prop="appVersion">
      <el-input v-model="dataForm.appVersion" placeholder=""></el-input>
    </el-form-item>
    <el-form-item label="" prop="countryCode">
      <el-input v-model="dataForm.countryCode" placeholder=""></el-input>
    </el-form-item>
    <el-form-item label="" prop="phone">
      <el-input v-model="dataForm.phone" placeholder=""></el-input>
    </el-form-item>
    <el-form-item label="" prop="proxy">
      <el-input v-model="dataForm.proxy" placeholder=""></el-input>
    </el-form-item>
    <el-form-item label="" prop="txtToken">
      <el-input v-model="dataForm.txtToken" placeholder=""></el-input>
    </el-form-item>
    <el-form-item label="" prop="taskId">
      <el-input v-model="dataForm.taskId" placeholder=""></el-input>
    </el-form-item>
    <el-form-item label="" prop="smsCode">
      <el-input v-model="dataForm.smsCode" placeholder=""></el-input>
    </el-form-item>
    <el-form-item label="" prop="registerStatus">
      <el-input v-model="dataForm.registerStatus" placeholder=""></el-input>
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
          ab: '',
          appVersion: '',
          countryCode: '',
          phone: '',
          proxy: '',
          txtToken: '',
          taskId: '',
          smsCode: '',
          registerStatus: '',
          deleteFlag: '',
          createTime: ''
        },
        dataRule: {
          ab: [
            { required: true, message: '不能为空', trigger: 'blur' }
          ],
          appVersion: [
            { required: true, message: '不能为空', trigger: 'blur' }
          ],
          countryCode: [
            { required: true, message: '不能为空', trigger: 'blur' }
          ],
          phone: [
            { required: true, message: '不能为空', trigger: 'blur' }
          ],
          proxy: [
            { required: true, message: '不能为空', trigger: 'blur' }
          ],
          txtToken: [
            { required: true, message: '不能为空', trigger: 'blur' }
          ],
          taskId: [
            { required: true, message: '不能为空', trigger: 'blur' }
          ],
          smsCode: [
            { required: true, message: '不能为空', trigger: 'blur' }
          ],
          registerStatus: [
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
              url: this.$http.adornUrl(`/ltt/cdlineregister/info/${this.dataForm.id}`),
              method: 'get',
              params: this.$http.adornParams()
            }).then(({data}) => {
              if (data && data.code === 0) {
                this.dataForm.ab = data.cdlineregister.ab
                this.dataForm.appVersion = data.cdlineregister.appVersion
                this.dataForm.countryCode = data.cdlineregister.countryCode
                this.dataForm.phone = data.cdlineregister.phone
                this.dataForm.proxy = data.cdlineregister.proxy
                this.dataForm.txtToken = data.cdlineregister.txtToken
                this.dataForm.taskId = data.cdlineregister.taskId
                this.dataForm.smsCode = data.cdlineregister.smsCode
                this.dataForm.registerStatus = data.cdlineregister.registerStatus
                this.dataForm.deleteFlag = data.cdlineregister.deleteFlag
                this.dataForm.createTime = data.cdlineregister.createTime
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
              url: this.$http.adornUrl(`/ltt/cdlineregister/${!this.dataForm.id ? 'save' : 'update'}`),
              method: 'post',
              data: this.$http.adornData({
                'id': this.dataForm.id || undefined,
                'ab': this.dataForm.ab,
                'appVersion': this.dataForm.appVersion,
                'countryCode': this.dataForm.countryCode,
                'phone': this.dataForm.phone,
                'proxy': this.dataForm.proxy,
                'txtToken': this.dataForm.txtToken,
                'taskId': this.dataForm.taskId,
                'smsCode': this.dataForm.smsCode,
                'registerStatus': this.dataForm.registerStatus,
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
