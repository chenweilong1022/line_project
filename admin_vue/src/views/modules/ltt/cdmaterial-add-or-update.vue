<template>
  <el-dialog
    :title="!dataForm.id ? '新增' : '修改'"
    v-loading.fullscreen.lock="fullscreenLoading"
    :close-on-click-modal="false"
    :visible.sync="visible">
    <el-form :model="dataForm" :rules="dataRule" ref="dataForm" label-width="80px">
    <el-form-item label="备注" prop="remark">
      <el-input v-model="dataForm.remark" placeholder=""></el-input>
    </el-form-item>
      <el-form-item label="number" prop="number">
        <el-input v-model="dataForm.number" placeholder=""></el-input>
      </el-form-item>
      <el-form-item  label="料子类型">
        <el-select v-model="type" placeholder="类型" clearable>
          <el-option
            v-for="item in typeOptions"
            :key="item.value"
            :label="item.label"
            :value="item.value">
          </el-option>
        </el-select>
      </el-form-item>

      <el-form-item label="料子">
        <el-upload
          class="upload-demo"
          action="http://localhost:8880/app/file/upload"
          :on-success="handleAvatarSuccess">
          <el-button size="small" type="primary">点击上传</el-button>
        </el-upload>
      </el-form-item>
      <el-form-item label="水军">
        <el-upload
          v-model:file-list="fileList"
          class="upload-demo"
          :multiple="multiple"
          action="http://localhost:8880/app/file/upload"
          :on-preview="handlePreview"
          :on-remove="handleRemove"
          :on-success="handleAvatarSuccess1">
          <el-button size="small" type="primary">点击上传</el-button>
        </el-upload>
      </el-form-item>
    </el-form>
    <span slot="footer" class="dialog-footer">
      <el-button @click="visible = false">取消</el-button>
      <el-button type="primary" @click="dataFormSubmit()">确定</el-button>
      <el-button type="primary" @click="exportSyHandler()">导出剩余</el-button>
    </span>
  </el-dialog>
</template>

<script>
  export default {
    data () {
      return {
        multiple: true,
        fileList: [],
        type: null,
        typeOptions: [
          {
            value: 1,
            label: '拉群料子'
          },
          {
            value: 2,
            label: '私信料子'
          }
        ],
        fullscreenLoading: false,
        visible: false,
        dataForm: {
          id: 0,
          remark: '',
          uploadNumber: '',
          synchronizationsNumber: '',
          successesNumber: '',
          failuresNumber: '',
          materialUrl: '',
          navyUrl: '',
          navyUrlList: [],
          deleteFlag: '',
          createTime: ''
        },
        dataRule: {
          remark: [
            { required: true, message: '不能为空', trigger: 'blur' }
          ],
          uploadNumber: [
            { required: true, message: '不能为空', trigger: 'blur' }
          ],
          synchronizationsNumber: [
            { required: true, message: '不能为空', trigger: 'blur' }
          ],
          successesNumber: [
            { required: true, message: '不能为空', trigger: 'blur' }
          ],
          failuresNumber: [
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
        this.dataForm.materialUrl = res.data
      },
      // handleAvatarSuccess1 (res, file) {
      //   this.dataForm.navyUrl = res.data
      // },
      handleAvatarSuccess1 (uploadFile, response, uploadFiles) {
        console.log(uploadFile)
        console.log(uploadFiles)
        this.fileList = uploadFiles
      },
      handleRemove (uploadFile, uploadFiles) {
        this.fileList = uploadFiles
        console.log(uploadFile)
        console.log(uploadFiles)
      },
      handlePreview (uploadFile, uploadFiles) {
        console.log(uploadFile)
        console.log(uploadFiles)
        this.fileList = uploadFiles
      },
      init (id) {
        this.dataForm.id = id || 0
        this.visible = true
        this.$nextTick(() => {
          this.$refs['dataForm'].resetFields()
          if (this.dataForm.id) {
            this.$http({
              url: this.$http.adornUrl(`/ltt/cdmaterial/info/${this.dataForm.id}`),
              method: 'get',
              params: this.$http.adornParams()
            }).then(({data}) => {
              if (data && data.code === 0) {
                this.dataForm.remark = data.cdmaterial.remark
                this.dataForm.uploadNumber = data.cdmaterial.uploadNumber
                this.dataForm.synchronizationsNumber = data.cdmaterial.synchronizationsNumber
                this.dataForm.successesNumber = data.cdmaterial.successesNumber
                this.dataForm.failuresNumber = data.cdmaterial.failuresNumber
                this.dataForm.materialUrl = data.cdmaterial.materialUrl
                this.dataForm.navyUrl = data.cdmaterial.navyUrl
                this.dataForm.deleteFlag = data.cdmaterial.deleteFlag
                this.dataForm.createTime = data.cdmaterial.createTime
              }
            })
          }
        })
      },
      exportSyHandler () {
      //   materialUrl
        window.open(this.$http.adornUrl(`/ltt/cdmaterial/exportSy?token=${this.$cookie.get('token')}&materialUrl=${this.dataForm.materialUrl}`))
      },
      // 表单提交
      dataFormSubmit () {
        this.dataForm.navyUrlList = []
        for (let i = 0; i < this.fileList.length; i++) {
          let data = this.fileList[i]
          this.dataForm.navyUrlList.push(data.response.data)
        }
        console.log(this.dataForm.navyUrlList)
        this.$refs['dataForm'].validate((valid) => {
          if (valid) {
            this.fullscreenLoading = true;
            this.$http({
              url: this.$http.adornUrl(`/ltt/cdmaterial/${!this.dataForm.id ? 'save' : 'update'}`),
              method: 'post',
              data: this.$http.adornData({
                'id': this.dataForm.id || undefined,
                'remark': this.dataForm.remark,
                'number': this.dataForm.number,
                'type': this.type,
                'uploadNumber': this.dataForm.uploadNumber,
                'synchronizationsNumber': this.dataForm.synchronizationsNumber,
                'successesNumber': this.dataForm.successesNumber,
                'failuresNumber': this.dataForm.failuresNumber,
                'materialUrl': this.dataForm.materialUrl,
                'navyUrl': this.dataForm.navyUrl,
                'navyUrlList': this.dataForm.navyUrlList,
                'deleteFlag': this.dataForm.deleteFlag,
                'createTime': this.dataForm.createTime
              })
            }).then(({data}) => {
              this.fullscreenLoading = false;
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
