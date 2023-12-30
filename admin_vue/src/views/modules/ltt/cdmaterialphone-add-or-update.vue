<template>
  <el-dialog
    :title="!dataForm.id ? '新增' : '修改'"
    :close-on-click-modal="false"
    :visible.sync="visible">
    <el-form :model="dataForm" :rules="dataRule" ref="dataForm" @keyup.enter.native="dataFormSubmit()" label-width="80px">
    <el-form-item label="" prop="lineRegisterId">
      <el-input v-model="dataForm.lineRegisterId" placeholder=""></el-input>
    </el-form-item>
    <el-form-item label="" prop="materialId">
      <el-input v-model="dataForm.materialId" placeholder=""></el-input>
    </el-form-item>
    <el-form-item label="" prop="luid">
      <el-input v-model="dataForm.luid" placeholder=""></el-input>
    </el-form-item>
    <el-form-item label="" prop="contactType">
      <el-input v-model="dataForm.contactType" placeholder=""></el-input>
    </el-form-item>
    <el-form-item label="" prop="contactKey">
      <el-input v-model="dataForm.contactKey" placeholder=""></el-input>
    </el-form-item>
    <el-form-item label="" prop="mid">
      <el-input v-model="dataForm.mid" placeholder=""></el-input>
    </el-form-item>
    <el-form-item label="" prop="createdTime">
      <el-input v-model="dataForm.createdTime" placeholder=""></el-input>
    </el-form-item>
    <el-form-item label="" prop="type">
      <el-input v-model="dataForm.type" placeholder=""></el-input>
    </el-form-item>
    <el-form-item label="" prop="status">
      <el-input v-model="dataForm.status" placeholder=""></el-input>
    </el-form-item>
    <el-form-item label="" prop="relation">
      <el-input v-model="dataForm.relation" placeholder=""></el-input>
    </el-form-item>
    <el-form-item label="" prop="displayName">
      <el-input v-model="dataForm.displayName" placeholder=""></el-input>
    </el-form-item>
    <el-form-item label="" prop="phoneticName">
      <el-input v-model="dataForm.phoneticName" placeholder=""></el-input>
    </el-form-item>
    <el-form-item label="" prop="pictureStatus">
      <el-input v-model="dataForm.pictureStatus" placeholder=""></el-input>
    </el-form-item>
    <el-form-item label="" prop="thumbnailUrl">
      <el-input v-model="dataForm.thumbnailUrl" placeholder=""></el-input>
    </el-form-item>
    <el-form-item label="" prop="statusMessage">
      <el-input v-model="dataForm.statusMessage" placeholder=""></el-input>
    </el-form-item>
    <el-form-item label="" prop="displayNameOverridden">
      <el-input v-model="dataForm.displayNameOverridden" placeholder=""></el-input>
    </el-form-item>
    <el-form-item label="" prop="favoriteTime">
      <el-input v-model="dataForm.favoriteTime" placeholder=""></el-input>
    </el-form-item>
    <el-form-item label="" prop="capableVoiceCall">
      <el-input v-model="dataForm.capableVoiceCall" placeholder=""></el-input>
    </el-form-item>
    <el-form-item label="" prop="capableVideoCall">
      <el-input v-model="dataForm.capableVideoCall" placeholder=""></el-input>
    </el-form-item>
    <el-form-item label="" prop="capableMyhome">
      <el-input v-model="dataForm.capableMyhome" placeholder=""></el-input>
    </el-form-item>
    <el-form-item label="" prop="capableBuddy">
      <el-input v-model="dataForm.capableBuddy" placeholder=""></el-input>
    </el-form-item>
    <el-form-item label="" prop="attributes">
      <el-input v-model="dataForm.attributes" placeholder=""></el-input>
    </el-form-item>
    <el-form-item label="" prop="settings">
      <el-input v-model="dataForm.settings" placeholder=""></el-input>
    </el-form-item>
    <el-form-item label="" prop="picturePath">
      <el-input v-model="dataForm.picturePath" placeholder=""></el-input>
    </el-form-item>
    <el-form-item label="" prop="recommendpArams">
      <el-input v-model="dataForm.recommendpArams" placeholder=""></el-input>
    </el-form-item>
    <el-form-item label="" prop="friendRequestStatus">
      <el-input v-model="dataForm.friendRequestStatus" placeholder=""></el-input>
    </el-form-item>
    <el-form-item label="" prop="musicProfile">
      <el-input v-model="dataForm.musicProfile" placeholder=""></el-input>
    </el-form-item>
    <el-form-item label="" prop="videoProfile">
      <el-input v-model="dataForm.videoProfile" placeholder=""></el-input>
    </el-form-item>
    <el-form-item label="" prop="materialType">
      <el-input v-model="dataForm.materialType" placeholder=""></el-input>
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
          lineRegisterId: '',
          materialId: '',
          luid: '',
          contactType: '',
          contactKey: '',
          mid: '',
          createdTime: '',
          type: '',
          status: '',
          relation: '',
          displayName: '',
          phoneticName: '',
          pictureStatus: '',
          thumbnailUrl: '',
          statusMessage: '',
          displayNameOverridden: '',
          favoriteTime: '',
          capableVoiceCall: '',
          capableVideoCall: '',
          capableMyhome: '',
          capableBuddy: '',
          attributes: '',
          settings: '',
          picturePath: '',
          recommendpArams: '',
          friendRequestStatus: '',
          musicProfile: '',
          videoProfile: '',
          materialType: '',
          deleteFlag: '',
          createTime: '',
          errMsg: ''
        },
        dataRule: {
          lineRegisterId: [
            { required: true, message: '不能为空', trigger: 'blur' }
          ],
          materialId: [
            { required: true, message: '不能为空', trigger: 'blur' }
          ],
          luid: [
            { required: true, message: '不能为空', trigger: 'blur' }
          ],
          contactType: [
            { required: true, message: '不能为空', trigger: 'blur' }
          ],
          contactKey: [
            { required: true, message: '不能为空', trigger: 'blur' }
          ],
          mid: [
            { required: true, message: '不能为空', trigger: 'blur' }
          ],
          createdTime: [
            { required: true, message: '不能为空', trigger: 'blur' }
          ],
          type: [
            { required: true, message: '不能为空', trigger: 'blur' }
          ],
          status: [
            { required: true, message: '不能为空', trigger: 'blur' }
          ],
          relation: [
            { required: true, message: '不能为空', trigger: 'blur' }
          ],
          displayName: [
            { required: true, message: '不能为空', trigger: 'blur' }
          ],
          phoneticName: [
            { required: true, message: '不能为空', trigger: 'blur' }
          ],
          pictureStatus: [
            { required: true, message: '不能为空', trigger: 'blur' }
          ],
          thumbnailUrl: [
            { required: true, message: '不能为空', trigger: 'blur' }
          ],
          statusMessage: [
            { required: true, message: '不能为空', trigger: 'blur' }
          ],
          displayNameOverridden: [
            { required: true, message: '不能为空', trigger: 'blur' }
          ],
          favoriteTime: [
            { required: true, message: '不能为空', trigger: 'blur' }
          ],
          capableVoiceCall: [
            { required: true, message: '不能为空', trigger: 'blur' }
          ],
          capableVideoCall: [
            { required: true, message: '不能为空', trigger: 'blur' }
          ],
          capableMyhome: [
            { required: true, message: '不能为空', trigger: 'blur' }
          ],
          capableBuddy: [
            { required: true, message: '不能为空', trigger: 'blur' }
          ],
          attributes: [
            { required: true, message: '不能为空', trigger: 'blur' }
          ],
          settings: [
            { required: true, message: '不能为空', trigger: 'blur' }
          ],
          picturePath: [
            { required: true, message: '不能为空', trigger: 'blur' }
          ],
          recommendpArams: [
            { required: true, message: '不能为空', trigger: 'blur' }
          ],
          friendRequestStatus: [
            { required: true, message: '不能为空', trigger: 'blur' }
          ],
          musicProfile: [
            { required: true, message: '不能为空', trigger: 'blur' }
          ],
          videoProfile: [
            { required: true, message: '不能为空', trigger: 'blur' }
          ],
          materialType: [
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
              url: this.$http.adornUrl(`/ltt/cdmaterialphone/info/${this.dataForm.id}`),
              method: 'get',
              params: this.$http.adornParams()
            }).then(({data}) => {
              if (data && data.code === 0) {
                this.dataForm.lineRegisterId = data.cdmaterialphone.lineRegisterId
                this.dataForm.materialId = data.cdmaterialphone.materialId
                this.dataForm.luid = data.cdmaterialphone.luid
                this.dataForm.contactType = data.cdmaterialphone.contactType
                this.dataForm.contactKey = data.cdmaterialphone.contactKey
                this.dataForm.mid = data.cdmaterialphone.mid
                this.dataForm.createdTime = data.cdmaterialphone.createdTime
                this.dataForm.type = data.cdmaterialphone.type
                this.dataForm.status = data.cdmaterialphone.status
                this.dataForm.relation = data.cdmaterialphone.relation
                this.dataForm.displayName = data.cdmaterialphone.displayName
                this.dataForm.phoneticName = data.cdmaterialphone.phoneticName
                this.dataForm.pictureStatus = data.cdmaterialphone.pictureStatus
                this.dataForm.thumbnailUrl = data.cdmaterialphone.thumbnailUrl
                this.dataForm.statusMessage = data.cdmaterialphone.statusMessage
                this.dataForm.displayNameOverridden = data.cdmaterialphone.displayNameOverridden
                this.dataForm.favoriteTime = data.cdmaterialphone.favoriteTime
                this.dataForm.capableVoiceCall = data.cdmaterialphone.capableVoiceCall
                this.dataForm.capableVideoCall = data.cdmaterialphone.capableVideoCall
                this.dataForm.capableMyhome = data.cdmaterialphone.capableMyhome
                this.dataForm.capableBuddy = data.cdmaterialphone.capableBuddy
                this.dataForm.attributes = data.cdmaterialphone.attributes
                this.dataForm.settings = data.cdmaterialphone.settings
                this.dataForm.picturePath = data.cdmaterialphone.picturePath
                this.dataForm.recommendpArams = data.cdmaterialphone.recommendpArams
                this.dataForm.friendRequestStatus = data.cdmaterialphone.friendRequestStatus
                this.dataForm.musicProfile = data.cdmaterialphone.musicProfile
                this.dataForm.videoProfile = data.cdmaterialphone.videoProfile
                this.dataForm.materialType = data.cdmaterialphone.materialType
                this.dataForm.deleteFlag = data.cdmaterialphone.deleteFlag
                this.dataForm.createTime = data.cdmaterialphone.createTime
                this.dataForm.errMsg = data.cdmaterialphone.errMsg
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
              url: this.$http.adornUrl(`/ltt/cdmaterialphone/${!this.dataForm.id ? 'save' : 'update'}`),
              method: 'post',
              data: this.$http.adornData({
                'id': this.dataForm.id || undefined,
                'lineRegisterId': this.dataForm.lineRegisterId,
                'materialId': this.dataForm.materialId,
                'luid': this.dataForm.luid,
                'contactType': this.dataForm.contactType,
                'contactKey': this.dataForm.contactKey,
                'mid': this.dataForm.mid,
                'createdTime': this.dataForm.createdTime,
                'type': this.dataForm.type,
                'status': this.dataForm.status,
                'relation': this.dataForm.relation,
                'displayName': this.dataForm.displayName,
                'phoneticName': this.dataForm.phoneticName,
                'pictureStatus': this.dataForm.pictureStatus,
                'thumbnailUrl': this.dataForm.thumbnailUrl,
                'statusMessage': this.dataForm.statusMessage,
                'displayNameOverridden': this.dataForm.displayNameOverridden,
                'favoriteTime': this.dataForm.favoriteTime,
                'capableVoiceCall': this.dataForm.capableVoiceCall,
                'capableVideoCall': this.dataForm.capableVideoCall,
                'capableMyhome': this.dataForm.capableMyhome,
                'capableBuddy': this.dataForm.capableBuddy,
                'attributes': this.dataForm.attributes,
                'settings': this.dataForm.settings,
                'picturePath': this.dataForm.picturePath,
                'recommendpArams': this.dataForm.recommendpArams,
                'friendRequestStatus': this.dataForm.friendRequestStatus,
                'musicProfile': this.dataForm.musicProfile,
                'videoProfile': this.dataForm.videoProfile,
                'materialType': this.dataForm.materialType,
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
