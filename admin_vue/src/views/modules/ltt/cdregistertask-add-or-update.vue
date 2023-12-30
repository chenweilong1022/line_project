<template>
  <el-dialog
    :title="!dataForm.id ? '新增' : '修改'"
    :close-on-click-modal="false"
    :visible.sync="visible">
    <el-form :model="dataForm" :rules="dataRule" ref="dataForm" @keyup.enter.native="dataFormSubmit()" label-width="80px">
    <el-form-item label="注册数量" prop="totalAmount">
      <el-input v-model="dataForm.totalAmount" placeholder="注册数量"></el-input>
    </el-form-item>
    <el-form-item label="线程数" prop="numberThreads">
      <el-input v-model="dataForm.numberThreads" placeholder="线程数"></el-input>
    </el-form-item>

      <el-form-item label="火狐狸国家配置" prop="firefoxCountry"  v-if="type === 2">
        <el-input v-model="dataFormConfig.firefoxCountry" placeholder="火狐狸国家配置"></el-input>
      </el-form-item>

      <el-form-item label="LINE国家配置" prop="firefoxCountry"  v-if="type === 2">
        <el-input v-model="dataFormConfig.firefoxCountry1" placeholder="LINE国家配置"></el-input>
      </el-form-item>
      <el-form-item label="静态代理使用次数" prop="proxyUseCount"  v-if="type === 2">
        <el-input v-model="dataFormConfig.proxyUseCount" placeholder="静态代理使用次数"></el-input>
      </el-form-item>

      <el-form-item label="代理">
        <el-select v-model="proxy" placeholder="代理" clearable>
          <el-option
            v-for="item in proxyOptions"
            :key="item.value"
            :label="item.label"
            :value="item.value">
          </el-option>
        </el-select>
      </el-form-item>

    </el-form>
    <span slot="footer" class="dialog-footer">
      <el-button @click="visible = false">取消</el-button>
      <el-button type="primary" @click="configDataFormSubmit()">确定</el-button>
    </span>
  </el-dialog>
</template>

<script>
  export default {
    data () {
      return {
        proxyOptions: [
          {
            value: 1,
            label: 'lunaproxy'
          },
          {
            value: 2,
            label: 'ip2world'
          },
          {
            value: 3,
            label: '静态代理'
          }
        ],
        type: 2,
        proxy: '',
        visible: false,
        dataFormConfig: {
          id: 0,
          paramKey: '',
          paramValue: '',
          firefoxBaseUrl: '',
          firefoxToken: '',
          firefoxIid: '',
          firefoxCountry: '',
          firefoxCountry1: '',
          proxyUseCount: '',
          lineBaseHttp: '',
          lineAb: '',
          lineAppVersion: '',
          lineTxtToken: ''
        },
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
    methods: {
      // 表单提交
      configDataFormSubmit () {
        this.$refs['dataForm'].validate((valid) => {
          if (valid) {
            this.$http({
              url: this.$http.adornUrl(`/sys/config/update`),
              method: 'post',
              data: this.$http.adornData({
                'id': this.dataFormConfig.id || undefined,
                'paramKey': this.dataFormConfig.paramKey,
                'paramValue': this.dataFormConfig.paramValue,
                'lineBaseHttp': this.dataFormConfig.lineBaseHttp,
                'lineAb': this.dataFormConfig.lineAb,
                'lineAppVersion': this.dataFormConfig.lineAppVersion,
                'lineTxtToken': this.dataFormConfig.lineTxtToken,

                'firefoxBaseUrl': this.dataFormConfig.firefoxBaseUrl,
                'firefoxToken': this.dataFormConfig.firefoxToken,
                'firefoxIid': this.dataFormConfig.firefoxIid,
                'firefoxCountry': this.dataFormConfig.firefoxCountry,
                'firefoxCountry1': this.dataFormConfig.firefoxCountry1,
                'proxyUseCount': this.dataFormConfig.proxyUseCount,
                'proxy': this.proxy,
                'type': 2,
                'remark': this.dataFormConfig.remark
              })
            }).then(({data}) => {
              if (data && data.code === 0) {
                this.dataFormSubmit();
              } else {
                this.$message.error(data.msg)
              }
            })
          }
        })
      },
      initConfig () {
          this.$http({
            url: this.$http.adornUrl(`/sys/config/infoDefault`),
            method: 'get',
            params: this.$http.adornParams()
          }).then(({data}) => {
            if (data && data.code === 0) {
              this.dataFormConfig.id = data.config.id
              this.dataFormConfig.paramKey = data.config.paramKey
              this.dataFormConfig.paramValue = data.config.paramValue
              this.dataFormConfig.remark = data.config.remark
              this.dataFormConfig.lineBaseHttp = data.config.lineBaseHttp
              this.dataFormConfig.lineAb = data.config.lineAb
              this.dataFormConfig.lineAppVersion = data.config.lineAppVersion
              this.dataFormConfig.lineTxtToken = data.config.lineTxtToken
              this.dataFormConfig.firefoxBaseUrl = data.config.firefoxBaseUrl
              this.dataFormConfig.firefoxToken = data.config.firefoxToken
              this.dataFormConfig.firefoxIid = data.config.firefoxIid
              this.dataFormConfig.firefoxCountry = data.config.firefoxCountry
              this.dataFormConfig.firefoxCountry1 = data.config.firefoxCountry1
              this.dataFormConfig.proxyUseCount = data.config.proxyUseCount
              this.proxy = data.config.proxy
            }
          })
      },
      init (id) {
        this.dataForm.id = id || 0
        this.visible = true
        this.initConfig();
        this.$nextTick(() => {
          this.$refs['dataForm'].resetFields()
          if (this.dataForm.id) {
            this.$http({
              url: this.$http.adornUrl(`/ltt/cdregistertask/info/${this.dataForm.id}`),
              method: 'get',
              params: this.$http.adornParams()
            }).then(({data}) => {
              if (data && data.code === 0) {
                this.dataForm.totalAmount = data.cdregistertask.totalAmount
                this.dataForm.numberThreads = data.cdregistertask.numberThreads
                this.dataForm.numberRegistered = data.cdregistertask.numberRegistered
                this.dataForm.numberSuccesses = data.cdregistertask.numberSuccesses
                this.dataForm.numberFailures = data.cdregistertask.numberFailures
                this.dataForm.registrationStatus = data.cdregistertask.registrationStatus
                this.dataForm.deleteFlag = data.cdregistertask.deleteFlag
                this.dataForm.createTime = data.cdregistertask.createTime
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
