<template>
  <el-dialog
    :title="!dataForm.id ? '自动分群' : '自动分群'"
    v-loading.fullscreen.lock="fullscreenLoading"
    :close-on-click-modal="false"
    :visible.sync="visible">
    <el-form :model="dataForm" :rules="dataRule" ref="dataForm" @keyup.enter.native="dataFormSubmit()" label-width="200px">

    <el-form-item label="限制人数" prop="accountGroupRestrictions">
      <el-input v-model="dataForm.accountGroupRestrictions" placeholder=""></el-input>
    </el-form-item>

    <el-form-item label="群名称" prop="groupName">
      <el-input v-model="dataForm.groupName" placeholder=""></el-input>
    </el-form-item>

    <el-form-item label="单个群需要多少人" prop="numberSingleGroups">
      <el-input v-model="dataForm.numberSingleGroups" placeholder=""></el-input>
    </el-form-item>

      <el-form-item  label="账号国家">
        <el-select v-model="countryCode" placeholder="国家" clearable>
          <el-option
            v-for="item in countryCodeOptions"
            :key="item.label"
            :label="item.label"
            :value="item.label">
          </el-option>
        </el-select>
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
        countryCodeOptions: [
          {
            label: 'th'
          },
          {
            label: 'jp'
          }
        ],
        fullscreenLoading: false,
        countryCode: null,
        visible: false,
        dataForm: {
          id: 0,
          ids: [],
          remark: '',
          uploadNumber: '',
          synchronizationsNumber: '',
          accountGroupRestrictions: '',
          groupName: '',
          numberSingleGroups: '',
          successesNumber: '',
          failuresNumber: '',
          materialUrl: '',
          navyUrl: '',
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
      handleAvatarSuccess1 (res, file) {
        this.dataForm.navyUrl = res.data
      },
      init (ids) {
        this.dataForm.ids = ids
        this.visible = true
      },
      // 表单提交
      dataFormSubmit () {
        this.$refs['dataForm'].validate((valid) => {
          if (valid) {
            this.fullscreenLoading = true;
            this.$http({
              url: this.$http.adornUrl(`/ltt/cdmaterial/autoAssignGroups`),
              method: 'post',
              data: this.$http.adornData({
                'id': this.dataForm.id,
                'ids': this.dataForm.ids,
                'accountGroupRestrictions': this.dataForm.accountGroupRestrictions,
                'numberSingleGroups': this.dataForm.numberSingleGroups,
                'groupName': this.dataForm.groupName,
                'countryCode': this.countryCode
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
