<template>
  <el-dialog
    :visible.sync="visible"
    :title="isEdit ? '编辑任务' : '新增任务'"
    width="800px"
    :close-on-click-modal="false"
    destroy-on-close
    @open="handleOpen"
    @closed="handleClosed"
  >
    <el-form
      ref="formRef"
      :model="form"
      :rules="rules"
      label-width="120px"
    >
      <!-- ==================== 基础信息 ==================== -->
      <el-divider content-position="left">基础信息</el-divider>

      <el-row :gutter="20">
        <el-col :span="12">
          <el-form-item label="执行器" prop="jobGroup">
            <el-select v-model="form.jobGroup" placeholder="请选择执行器" style="width: 100%">
              <el-option
                v-for="group in jobGroupList"
                :key="group.id"
                :label="group.title"
                :value="group.id"
              />
            </el-select>
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="任务名称" prop="jobDesc">
            <el-input v-model="form.jobDesc" placeholder="请输入任务名称" maxlength="50" />
          </el-form-item>
        </el-col>
      </el-row>

      <el-row :gutter="20">
        <el-col :span="12">
          <el-form-item label="负责人" prop="author">
            <el-input v-model="form.author" placeholder="请输入负责人" maxlength="50" />
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="告警邮箱">
            <el-input v-model="form.alarmEmail" placeholder="请输入告警邮箱，多个用逗号分隔" maxlength="100" />
          </el-form-item>
        </el-col>
      </el-row>

      <!-- ==================== 调度配置 ==================== -->
      <el-divider content-position="left">调度配置</el-divider>

      <el-form-item label="调度类型" prop="scheduleType">
        <el-select
          v-model="form.scheduleType"
          placeholder="请选择调度类型"
          style="width: 220px"
          @change="handleScheduleTypeChange"
        >
          <el-option label="无（一次性执行）" value="NONE" />
          <el-option label="CRON" value="CRON" />
          <el-option label="固定速度（秒）" value="FIX_RATE" />
        </el-select>
      </el-form-item>

      <el-form-item v-if="form.scheduleType === 'CRON'" label="Cron" prop="cronConf">
        <el-input
          v-model="form.cronConf"
          placeholder="请输入Cron表达式，如 0 0 12 * * ?"
          style="width: 320px"
        />
        <div style="margin-top: 4px; color: #909399; font-size: 12px">
          示例：0 0/5 * * * ? （每5分钟） | 0 0 2 * * ? （每天凌晨2点） | 0 0 9 * * MON-FRI （工作日早9点）
        </div>
      </el-form-item>

      <el-form-item v-if="form.scheduleType === 'FIX_RATE'" label="固定速度" prop="fixRateConf">
        <el-input
          v-model="form.fixRateConf"
          placeholder="请输入秒数"
          style="width: 200px"
          @input="form.fixRateConf = form.fixRateConf.replace(/\D/g, '')"
        />
      </el-form-item>

      <!-- ==================== 任务配置 ==================== -->
      <el-divider content-position="left">任务配置</el-divider>

      <el-row :gutter="20">
        <el-col :span="12">
          <el-form-item label="运行模式" prop="glueType">
            <el-select v-model="form.glueType" style="width: 100%" :disabled="isEdit" @change="handleGlueTypeChange">
              <el-option label="BEAN" value="BEAN" />
              <el-option label="GLUE(Groovy)" value="GLUE_GROOVY" />
              <el-option label="GLUE(Shell)" value="GLUE_SHELL" />
              <el-option label="GLUE(Python)" value="GLUE_PYTHON" />
              <el-option label="GLUE(PHP)" value="GLUE_PHP" />
              <el-option label="GLUE(NodeJS)" value="GLUE_NODEJS" />
              <el-option label="GLUE(PowerShell)" value="GLUE_POWERSHELL" />
            </el-select>
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="JobHandler">
            <el-input
              v-model="form.executorHandler"
              placeholder="请输入JobHandler"
              :disabled="form.glueType !== 'BEAN'"
            />
          </el-form-item>
        </el-col>
      </el-row>

      <el-form-item label="执行参数">
        <el-input
          v-model="form.executorParam"
          type="textarea"
          :rows="3"
          placeholder="请输入执行参数"
          maxlength="512"
        />
      </el-form-item>

      <!-- ==================== 备注（自定义） ==================== -->
      <el-divider content-position="left">备注</el-divider>

      <el-form-item label="备注">
        <el-input
          v-model="form.remark"
          type="textarea"
          :rows="3"
          placeholder="请输入备注信息，用于补充描述该任务内容"
          maxlength="500"
          show-word-limit
        />
      </el-form-item>

      <!-- ==================== 高级配置 ==================== -->
      <el-divider content-position="left">
        <el-button type="text" @click="showAdvanced = !showAdvanced">
          高级配置
          <i :class="showAdvanced ? 'el-icon-arrow-up' : 'el-icon-arrow-down'" style="margin-left: 4px"></i>
        </el-button>
      </el-divider>

      <template v-if="showAdvanced">
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="路由策略">
              <el-select v-model="form.executorRouteStrategy" style="width: 100%">
                <el-option label="第一个" value="FIRST" />
                <el-option label="最后一个" value="LAST" />
                <el-option label="轮询" value="ROUND" />
                <el-option label="随机" value="RANDOM" />
                <el-option label="一致性HASH" value="CONSISTENT_HASH" />
                <el-option label="最不经常使用" value="LEAST_FREQUENTLY_USED" />
                <el-option label="最近最久未使用" value="LEAST_RECENTLY_USED" />
                <el-option label="故障转移" value="FAILOVER" />
                <el-option label="忙碌转移" value="BUSYOVER" />
                <el-option label="分片广播" value="SHARDING_BROADCAST" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="子任务ID">
              <el-input v-model="form.childJobId" placeholder="多个用逗号分隔" />
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="调度过期策略">
              <el-select v-model="form.misfireStrategy" style="width: 100%">
                <el-option label="忽略" value="DO_NOTHING" />
                <el-option label="立即执行一次" value="FIRE_ONCE_NOW" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="阻塞处理策略">
              <el-select v-model="form.executorBlockStrategy" style="width: 100%">
                <el-option label="单机串行" value="SERIAL_EXECUTION" />
                <el-option label="丢弃后续调度" value="DISCARD_LATER" />
                <el-option label="覆盖之前调度" value="COVER_EARLY" />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="任务超时时间(秒)">
              <el-input
                v-model="form.executorTimeout"
                placeholder="大于0时生效"
                @input="form.executorTimeout = form.executorTimeout.replace(/\D/g, '')"
              />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="失败重试次数">
              <el-input
                v-model="form.executorFailRetryCount"
                placeholder="大于0时生效"
                @input="form.executorFailRetryCount = form.executorFailRetryCount.replace(/\D/g, '')"
              />
            </el-form-item>
          </el-col>
        </el-row>
      </template>
    </el-form>

    <template v-slot:footer>
      <el-button @click="visible = false">取消</el-button>
      <el-button type="primary" :loading="submitting" @click="handleSubmit">保存</el-button>
    </template>
  </el-dialog>
</template>

<script>
import { addJob, updateJob } from '@/api/jobinfo'

function defaultForm() {
  return {
    // 基础信息
    jobGroup: '',
    jobDesc: '',
    author: '',
    alarmEmail: '',

    // 调度配置
    scheduleType: 'CRON',
    cronConf: '',
    fixRateConf: '',

    // 任务配置
    glueType: 'BEAN',
    executorHandler: '',
    executorParam: '',

    // 备注
    remark: '',

    // 高级配置
    executorRouteStrategy: 'FIRST',
    childJobId: '',
    misfireStrategy: 'DO_NOTHING',
    executorBlockStrategy: 'SERIAL_EXECUTION',
    executorTimeout: '',
    executorFailRetryCount: ''
  }
}

export default {
  name: 'JobFormDialog',

  props: {
    value: { type: Boolean, default: false },
    isEdit: { type: Boolean, default: false },
    jobGroupList: { type: Array, default: function () { return [] } },
    editData: { type: Object, default: function () { return {} } }
  },

  data: function () {
    return {
      visible: false,
      submitting: false,
      showAdvanced: false,
      form: defaultForm(),
      rules: {
        jobGroup: [{ required: true, message: '请选择执行器', trigger: 'change' }],
        jobDesc: [{ required: true, message: '请输入任务名称', trigger: 'blur' }],
        author: [{ required: true, message: '请输入负责人', trigger: 'blur' }],
        scheduleType: [{ required: true, message: '请选择调度类型', trigger: 'change' }],
        cronConf: [{ required: true, message: '请输入Cron表达式', trigger: 'blur' }],
        fixRateConf: [{ required: true, message: '请输入秒数', trigger: 'blur' }],
        glueType: [{ required: true, message: '请选择运行模式', trigger: 'change' }]
      }
    }
  },

  watch: {
    value: function (val) {
      this.visible = val
    },
    visible: function (val) {
      this.$emit('input', val)
    }
  },

  methods: {
    handleOpen: function () {
      // 对话框打开时更新执行器列表
      this.$emit('refresh')
    },

    handleScheduleTypeChange: function () {
      this.form.cronConf = ''
      this.form.fixRateConf = ''
    },

    handleGlueTypeChange: function () {
      if (this.form.glueType !== 'BEAN') {
        this.form.executorHandler = ''
      }
    },

    buildSubmitData: function () {
      // 调度配置
      var scheduleType = this.form.scheduleType
      var scheduleConf = ''
      if (scheduleType === 'CRON') {
        scheduleConf = this.form.cronConf
      } else if (scheduleType === 'FIX_RATE') {
        scheduleConf = this.form.fixRateConf
      }
      // NONE 时 scheduleConf 为空

      // 构建 executorParam：合并备注
      var extraParams = {}
      if (this.form.remark) {
        extraParams.remark = this.form.remark
      }

      var executorParam = this.form.executorParam
      if (Object.keys(extraParams).length > 0) {
        executorParam = JSON.stringify(extraParams)
      }

      return {
        jobGroup: this.form.jobGroup,
        jobDesc: this.form.jobDesc,
        author: this.form.author,
        alarmEmail: this.form.alarmEmail,
        scheduleType: scheduleType,
        scheduleConf: scheduleConf,
        glueType: this.form.glueType,
        executorHandler: this.form.executorHandler,
        executorParam: executorParam,
        executorRouteStrategy: this.form.executorRouteStrategy,
        executorBlockStrategy: this.form.executorBlockStrategy,
        misfireStrategy: this.form.misfireStrategy,
        childJobId: this.form.childJobId,
        executorTimeout: this.form.executorTimeout ? parseInt(this.form.executorTimeout) : 0,
        executorFailRetryCount: this.form.executorFailRetryCount ? parseInt(this.form.executorFailRetryCount) : 0
      }
    },

    handleSubmit: function () {
      var self = this
      this.$refs.formRef.validate(function (valid) {
        if (!valid) return

        // 调度类型额外校验
        if (self.form.scheduleType === 'CRON' && !self.form.cronConf) {
          self.$message.warning('请输入Cron表达式')
          return
        }
        if (self.form.scheduleType === 'FIX_RATE' && !self.form.fixRateConf) {
          self.$message.warning('请输入固定速度（秒数）')
          return
        }

        self.submitting = true
        var data = self.buildSubmitData()

        var requestFn = self.isEdit ? updateJob : addJob
        if (self.isEdit) {
          data.id = self.editData.id
        }

        requestFn(data).then(function (res) {
          self.submitting = false
          if (res.data.code === 200) {
            self.$message.success(self.isEdit ? '更新成功' : '新增成功')
            self.visible = false
            self.$emit('success')
          } else {
            self.$message.error(res.data.msg || (self.isEdit ? '更新失败' : '新增失败'))
          }
        }).catch(function (e) {
          self.submitting = false
          self.$message.error('操作失败：' + (e.message || '网络异常'))
        })
      })
    },

    handleClosed: function () {
      if (this.$refs.formRef) {
        this.$refs.formRef.resetFields()
      }
      this.form = defaultForm()
      this.showAdvanced = false
    },

    fillEditData: function (data) {
      // 基础信息
      this.form.jobGroup = data.jobGroup || ''
      this.form.jobDesc = data.jobDesc || ''
      this.form.author = data.author || ''
      this.form.alarmEmail = data.alarmEmail || ''

      // 调度配置
      this.form.scheduleType = data.scheduleType || 'CRON'
      if (data.scheduleType === 'CRON') {
        this.form.cronConf = data.scheduleConf || ''
        this.form.fixRateConf = ''
      } else if (data.scheduleType === 'FIX_RATE') {
        this.form.fixRateConf = data.scheduleConf || ''
        this.form.cronConf = ''
      } else {
        this.form.cronConf = ''
        this.form.fixRateConf = ''
      }

      // 任务配置
      this.form.glueType = data.glueType || 'BEAN'
      this.form.executorHandler = data.executorHandler || ''
      this.form.executorParam = data.executorParam || ''

      // 解析 executorParam 中的备注
      var extraParams = {}
      try { extraParams = JSON.parse(data.executorParam || '{}') } catch (e) {}
      this.form.remark = extraParams.remark || ''

      // 高级配置
      this.form.executorRouteStrategy = data.executorRouteStrategy || 'FIRST'
      this.form.childJobId = data.childJobId || ''
      this.form.misfireStrategy = data.misfireStrategy || 'DO_NOTHING'
      this.form.executorBlockStrategy = data.executorBlockStrategy || 'SERIAL_EXECUTION'
      this.form.executorTimeout = data.executorTimeout != null ? String(data.executorTimeout) : ''
      this.form.executorFailRetryCount = data.executorFailRetryCount != null ? String(data.executorFailRetryCount) : ''
    }
  }
}
</script>
