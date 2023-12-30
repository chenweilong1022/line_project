<template>
  <el-dialog
    :title="!dataForm.id ? '新增' : '修改'"
    :close-on-click-modal="false"
    :visible.sync="visible">
    <el-form :model="dataForm" :rules="dataRule" ref="dataForm" @keyup.enter.native="dataFormSubmit()" label-width="80px">
    <el-form-item label="群名称" prop="groupName">
      <el-input v-model="dataForm.groupName" placeholder="群名称"></el-input>
    </el-form-item>
    <el-form-item label="群名称" prop="groupName">
      <el-upload
        class="upload-demo"
        action="http://localhost:8880/app/file/upload"
        :on-success="handleAvatarSuccess">
        <el-button size="small" type="primary">点击上传</el-button>
      </el-upload>
    </el-form-item>
    <el-form-item label="拉群间隔" prop="intervals">
      <el-input v-model="dataForm.intervals" placeholder="拉群间隔"></el-input>
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
        dataRule: {
          groupName: [
            { required: true, message: '不能为空', trigger: 'blur' }
          ],
          proxy: [
            { required: true, message: '不能为空', trigger: 'blur' }
          ],
          intervals: [
            { required: true, message: '不能为空', trigger: 'blur' }
          ],
          roomId: [
            { required: true, message: '不能为空', trigger: 'blur' }
          ],
          chatRoomUrl: [
            { required: true, message: '不能为空', trigger: 'blur' }
          ],
          roomTicketId: [
            { required: true, message: '不能为空', trigger: 'blur' }
          ],
          uploadGroupNumber: [
            { required: true, message: '不能为空', trigger: 'blur' }
          ],
          currentExecutionsNumber: [
            { required: true, message: '不能为空', trigger: 'blur' }
          ],
          successfullyAttractGroupsNumber: [
            { required: true, message: '不能为空', trigger: 'blur' }
          ],
          groupStatus: [
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
      handleAvatarSuccess (res, file) {
        this.dataForm.txtUrl = res.data
      },
      init (id) {
        this.dataForm.id = id || 0
        this.visible = true
        this.$nextTick(() => {
          this.$refs['dataForm'].resetFields()
          if (this.dataForm.id) {
            this.$http({
              url: this.$http.adornUrl(`/ltt/cdgrouptasks/info/${this.dataForm.id}`),
              method: 'get',
              params: this.$http.adornParams()
            }).then(({data}) => {
              if (data && data.code === 0) {
                this.dataForm.groupName = data.cdgrouptasks.groupName
                this.dataForm.proxy = data.cdgrouptasks.proxy
                this.dataForm.intervals = data.cdgrouptasks.intervals
                this.dataForm.roomId = data.cdgrouptasks.roomId
                this.dataForm.chatRoomUrl = data.cdgrouptasks.chatRoomUrl
                this.dataForm.roomTicketId = data.cdgrouptasks.roomTicketId
                this.dataForm.uploadGroupNumber = data.cdgrouptasks.uploadGroupNumber
                this.dataForm.currentExecutionsNumber = data.cdgrouptasks.currentExecutionsNumber
                this.dataForm.successfullyAttractGroupsNumber = data.cdgrouptasks.successfullyAttractGroupsNumber
                this.dataForm.groupStatus = data.cdgrouptasks.groupStatus
                this.dataForm.deleteFlag = data.cdgrouptasks.deleteFlag
                this.dataForm.createTime = data.cdgrouptasks.createTime
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
              url: this.$http.adornUrl(`/ltt/cdgrouptasks/${!this.dataForm.id ? 'save' : 'update'}`),
              method: 'post',
              data: this.$http.adornData({
                'id': this.dataForm.id || undefined,
                'groupName': this.dataForm.groupName,
                'proxy': this.dataForm.proxy,
                'intervals': this.dataForm.intervals,
                'roomId': this.dataForm.roomId,
                'chatRoomUrl': this.dataForm.chatRoomUrl,
                'roomTicketId': this.dataForm.roomTicketId,
                'uploadGroupNumber': this.dataForm.uploadGroupNumber,
                'currentExecutionsNumber': this.dataForm.currentExecutionsNumber,
                'successfullyAttractGroupsNumber': this.dataForm.successfullyAttractGroupsNumber,
                'groupStatus': this.dataForm.groupStatus,
                'txtUrl': this.dataForm.txtUrl,
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
