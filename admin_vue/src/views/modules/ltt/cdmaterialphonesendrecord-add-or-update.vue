<template>
  <el-dialog
    :title="!dataForm.id ? '新增' : '修改'"
    :close-on-click-modal="false"
    :visible.sync="visible">
    <el-form :model="dataForm" :rules="dataRule" ref="dataForm" @keyup.enter.native="dataFormSubmit()" label-width="80px">
    <el-form-item label="" prop="groupTaskId">
      <el-input v-model="dataForm.groupTaskId" placeholder=""></el-input>
    </el-form-item>
    <el-form-item label="" prop="lineRegisterId">
      <el-input v-model="dataForm.lineRegisterId" placeholder=""></el-input>
    </el-form-item>
    <el-form-item label="" prop="materialId">
      <el-input v-model="dataForm.materialId" placeholder=""></el-input>
    </el-form-item>
    <el-form-item label="" prop="contactKey">
      <el-input v-model="dataForm.contactKey" placeholder=""></el-input>
    </el-form-item>
    <el-form-item label="" prop="mid">
      <el-input v-model="dataForm.mid" placeholder=""></el-input>
    </el-form-item>
    <el-form-item label="" prop="sendText">
      <el-input v-model="dataForm.sendText" placeholder=""></el-input>
    </el-form-item>
    <el-form-item label="" prop="sendType">
      <el-input v-model="dataForm.sendType" placeholder=""></el-input>
    </el-form-item>
    <el-form-item label="" prop="sendStatus">
      <el-input v-model="dataForm.sendStatus" placeholder=""></el-input>
    </el-form-item>
    <el-form-item label="" prop="deleteFlag">
      <el-input v-model="dataForm.deleteFlag" placeholder=""></el-input>
    </el-form-item>
    <el-form-item label="" prop="createTime">
      <el-input v-model="dataForm.createTime" placeholder=""></el-input>
    </el-form-item>
    <el-form-item label="" prop="errMsg">
      <el-input v-model="dataForm.errMsg" placeholder=""></el-input>
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
          groupTaskId: '',
          lineRegisterId: '',
          materialId: '',
          contactKey: '',
          mid: '',
          sendText: '',
          sendType: '',
          sendStatus: '',
          deleteFlag: '',
          createTime: '',
          errMsg: ''
        },
        dataRule: {
          groupTaskId: [
            { required: true, message: '不能为空', trigger: 'blur' }
          ],
          lineRegisterId: [
            { required: true, message: '不能为空', trigger: 'blur' }
          ],
          materialId: [
            { required: true, message: '不能为空', trigger: 'blur' }
          ],
          contactKey: [
            { required: true, message: '不能为空', trigger: 'blur' }
          ],
          mid: [
            { required: true, message: '不能为空', trigger: 'blur' }
          ],
          sendText: [
            { required: true, message: '不能为空', trigger: 'blur' }
          ],
          sendType: [
            { required: true, message: '不能为空', trigger: 'blur' }
          ],
          sendStatus: [
            { required: true, message: '不能为空', trigger: 'blur' }
          ],
          deleteFlag: [
            { required: true, message: '不能为空', trigger: 'blur' }
          ],
          createTime: [
            { required: true, message: '不能为空', trigger: 'blur' }
          ],
          errMsg: [
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
              url: this.$http.adornUrl(`/ltt/cdmaterialphonesendrecord/info/${this.dataForm.id}`),
              method: 'get',
              params: this.$http.adornParams()
            }).then(({data}) => {
              if (data && data.code === 0) {
                this.dataForm.groupTaskId = data.cdmaterialphonesendrecord.groupTaskId
                this.dataForm.lineRegisterId = data.cdmaterialphonesendrecord.lineRegisterId
                this.dataForm.materialId = data.cdmaterialphonesendrecord.materialId
                this.dataForm.contactKey = data.cdmaterialphonesendrecord.contactKey
                this.dataForm.mid = data.cdmaterialphonesendrecord.mid
                this.dataForm.sendText = data.cdmaterialphonesendrecord.sendText
                this.dataForm.sendType = data.cdmaterialphonesendrecord.sendType
                this.dataForm.sendStatus = data.cdmaterialphonesendrecord.sendStatus
                this.dataForm.deleteFlag = data.cdmaterialphonesendrecord.deleteFlag
                this.dataForm.createTime = data.cdmaterialphonesendrecord.createTime
                this.dataForm.errMsg = data.cdmaterialphonesendrecord.errMsg
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
              url: this.$http.adornUrl(`/ltt/cdmaterialphonesendrecord/${!this.dataForm.id ? 'save' : 'update'}`),
              method: 'post',
              data: this.$http.adornData({
                'id': this.dataForm.id || undefined,
                'groupTaskId': this.dataForm.groupTaskId,
                'lineRegisterId': this.dataForm.lineRegisterId,
                'materialId': this.dataForm.materialId,
                'contactKey': this.dataForm.contactKey,
                'mid': this.dataForm.mid,
                'sendText': this.dataForm.sendText,
                'sendType': this.dataForm.sendType,
                'sendStatus': this.dataForm.sendStatus,
                'deleteFlag': this.dataForm.deleteFlag,
                'createTime': this.dataForm.createTime,
                'errMsg': this.dataForm.errMsg
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
