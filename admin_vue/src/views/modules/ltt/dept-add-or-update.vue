<template>
  <el-dialog
    :title="!dataForm.id ? '新增' : '修改'"
    :close-on-click-modal="false"
    :visible.sync="visible">
    <el-form :model="dataForm" :rules="dataRule" ref="dataForm" @keyup.enter.native="dataFormSubmit()" label-width="80px">
    <el-form-item label="" prop="deptname">
      <el-input v-model="dataForm.deptname" placeholder=""></el-input>
    </el-form-item>
    <el-form-item label="" prop="deptresponsibility">
      <el-input v-model="dataForm.deptresponsibility" placeholder=""></el-input>
    </el-form-item>
    <el-form-item label="" prop="parentdeptid">
      <el-input v-model="dataForm.parentdeptid" placeholder=""></el-input>
    </el-form-item>
    <el-form-item label="" prop="deptman">
      <el-input v-model="dataForm.deptman" placeholder=""></el-input>
    </el-form-item>
    <el-form-item label="" prop="depttel">
      <el-input v-model="dataForm.depttel" placeholder=""></el-input>
    </el-form-item>
    <el-form-item label="" prop="remark">
      <el-input v-model="dataForm.remark" placeholder=""></el-input>
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
          deptid: 0,
          deptname: '',
          deptresponsibility: '',
          parentdeptid: '',
          deptman: '',
          depttel: '',
          remark: ''
        },
        dataRule: {
          deptname: [
            { required: true, message: '不能为空', trigger: 'blur' }
          ],
          deptresponsibility: [
            { required: true, message: '不能为空', trigger: 'blur' }
          ],
          parentdeptid: [
            { required: true, message: '不能为空', trigger: 'blur' }
          ],
          deptman: [
            { required: true, message: '不能为空', trigger: 'blur' }
          ],
          depttel: [
            { required: true, message: '不能为空', trigger: 'blur' }
          ],
          remark: [
            { required: true, message: '不能为空', trigger: 'blur' }
          ]
        }
      }
    },
    methods: {
      init (id) {
        this.dataForm.deptid = id || 0
        this.visible = true
        this.$nextTick(() => {
          this.$refs['dataForm'].resetFields()
          if (this.dataForm.deptid) {
            this.$http({
              url: this.$http.adornUrl(`/ltt/dept/info/${this.dataForm.deptid}`),
              method: 'get',
              params: this.$http.adornParams()
            }).then(({data}) => {
              if (data && data.code === 0) {
                this.dataForm.deptname = data.dept.deptname
                this.dataForm.deptresponsibility = data.dept.deptresponsibility
                this.dataForm.parentdeptid = data.dept.parentdeptid
                this.dataForm.deptman = data.dept.deptman
                this.dataForm.depttel = data.dept.depttel
                this.dataForm.remark = data.dept.remark
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
              url: this.$http.adornUrl(`/ltt/dept/${!this.dataForm.deptid ? 'save' : 'update'}`),
              method: 'post',
              data: this.$http.adornData({
                'deptid': this.dataForm.deptid || undefined,
                'deptname': this.dataForm.deptname,
                'deptresponsibility': this.dataForm.deptresponsibility,
                'parentdeptid': this.dataForm.parentdeptid,
                'deptman': this.dataForm.deptman,
                'depttel': this.dataForm.depttel,
                'remark': this.dataForm.remark
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
