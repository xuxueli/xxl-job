<template>
  <div class="page-container">
    <!-- 搜索栏 -->
    <el-card shadow="never" class="search-card">
      <el-form :model="queryParams" :inline="true" size="default">
        <el-form-item label="任务名称">
          <el-input v-model="queryParams.jobDesc" placeholder="请输入任务名称" clearable style="width: 150px" />
        </el-form-item>
        <el-form-item label="执行周期">
          <el-select v-model="queryParams.cycleType" placeholder="全部" clearable style="width: 130px">
            <el-option label="周期性任务" value="periodic" />
            <el-option label="一次性任务" value="once" />
          </el-select>
        </el-form-item>
        <el-form-item label="启用状态">
          <el-select v-model="queryParams.triggerStatus" placeholder="全部" clearable style="width: 110px">
            <el-option label="运行中" :value="1" />
            <el-option label="已停止" :value="0" />
          </el-select>
        </el-form-item>
        <el-form-item label="创建人">
          <el-input v-model="queryParams.author" placeholder="请输入创建人" clearable style="width: 160px" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">查询</el-button>
          <el-button @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <!-- 任务列表 -->
    <el-card shadow="never" class="table-card">
      <el-table
        ref="tableRef"
        v-loading="loading"
        :data="pagedTableData"
        border
        stripe
        style="width: 100%"
        size="default"
      >
        <el-table-column prop="id" label="序号" width="70" align="center" />
        <el-table-column prop="jobDesc" label="任务名称" min-width="150" align="center" show-overflow-tooltip />
        <el-table-column label="执行周期" width="180" align="center">
          <template v-slot="{ row }">
            <el-tag v-if="row.scheduleType === 'NONE'" type="info" size="small">一次性任务</el-tag>
            <el-tag v-else type="primary" size="small">周期性任务</el-tag>
            <span style="margin-left: 4px; font-size: 12px; color: #606266">
              {{ formatScheduleConf(row) }}
            </span>
          </template>
        </el-table-column>
        <el-table-column prop="author" label="创建人" width="100" align="center" />
        <el-table-column label="状态" width="90" align="center">
          <template v-slot="{ row }">
            <el-tag :type="row.triggerStatus === 1 ? 'success' : 'info'" size="small">
              {{ row.triggerStatus === 1 ? '运行中' : '已停止' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="上次执行" width="160" align="center">
          <template v-slot="{ row }">
            <span v-if="row.triggerLastTime && row.triggerLastTime > 0">
              {{ formatTime(row.triggerLastTime) }}
            </span>
            <span v-else style="color: #c0c4cc">-</span>
          </template>
        </el-table-column>
        <el-table-column label="下次执行" width="160" align="center">
          <template v-slot="{ row }">
            <span v-if="row.triggerNextTime && row.triggerNextTime > 0">
              {{ formatTime(row.triggerNextTime) }}
            </span>
            <span v-else style="color: #c0c4cc">-</span>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="200" align="center">
          <template v-slot="{ row }">
            <div class="table-actions">
              <el-button type="text" size="small" @click="handleEdit(row)">修改</el-button>
              <el-button
                type="text"
                :style="{ color: row.triggerStatus === 1 ? '#e6a23c' : '#67c23a' }"
                size="small"
                @click="handleToggleStatus(row)"
              >
                {{ row.triggerStatus === 1 ? '禁用' : '启用' }}
              </el-button>
              <el-button type="text" size="small" @click="handleTrigger(row)">执行一次</el-button>
            </div>
          </template>
        </el-table-column>
      </el-table>

      <div style="display: flex; justify-content: space-between; align-items: center; margin-top: 16px">
        <span style="color: #909399; font-size: 13px">
          共 {{ filteredTableData.length }} 条记录
        </span>
        <el-pagination
          :current-page.sync="queryParams.start"
          :page-size.sync="queryParams.length"
          :page-sizes="[10, 20, 50, 100]"
          :total="filteredTableData.length"
          layout="total, sizes, prev, pager, next, jumper"
          background
          @size-change="handlePageChange"
          @current-change="handlePageChange"
        />
      </div>
    </el-card>

    <!-- 编辑对话框 -->
    <JobFormDialog
      ref="formDialogRef"
      v-model="formDialogVisible"
      :is-edit="true"
      :job-group-list="jobGroupList"
      :edit-data="currentEditData"
      @success="fetchData"
      @refresh="fetchJobGroupList"
    />

    <!-- 手动执行对话框 -->
    <el-dialog :visible.sync="triggerDialogVisible" title="手动执行" width="500px">
      <el-form label-width="100px">
        <el-form-item label="执行参数">
          <el-input
            v-model="triggerForm.executorParam"
            type="textarea"
            :rows="3"
            placeholder="请输入执行参数"
          />
        </el-form-item>
        <el-form-item label="机器地址">
          <el-input
            v-model="triggerForm.addressList"
            type="textarea"
            :rows="3"
            placeholder="请输入机器地址，为空则自动获取"
          />
        </el-form-item>
      </el-form>
      <template v-slot:footer>
        <el-button @click="triggerDialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="triggerSubmitting" @click="submitTrigger">执行</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script>
import { pageList, stopJob, startJob, triggerJob } from '@/api/jobinfo'
import { findAll } from '@/api/jobgroup'
import JobFormDialog from './components/JobFormDialog.vue'

export default {
  name: 'JobQueryList',

  components: {
    JobFormDialog
  },

  data: function () {
    return {
      loading: false,
      allTableData: [],
      jobGroupList: [],

      queryParams: {
        start: 1,
        length: 10,
        jobDesc: '',
        cycleType: '',
        triggerStatus: '',
        author: ''
      },

      // 编辑对话框
      formDialogVisible: false,
      currentEditData: {},

      // 手动执行
      triggerDialogVisible: false,
      triggerSubmitting: false,
      triggerForm: {
        executorParam: '',
        addressList: ''
      },
      triggerJobId: null
    }
  },

  computed: {
    filteredTableData: function () {
      var self = this
      return this.allTableData.filter(function (row) {
        // 执行周期过滤
        if (self.queryParams.cycleType) {
          if (self.queryParams.cycleType === 'periodic' && row.scheduleType === 'NONE') return false
          if (self.queryParams.cycleType === 'once' && row.scheduleType !== 'NONE') return false
        }

        return true
      })
    },

    pagedTableData: function () {
      var start = (this.queryParams.start - 1) * this.queryParams.length
      var end = start + this.queryParams.length
      return this.filteredTableData.slice(start, end)
    }
  },

  mounted: function () {
    this.fetchJobGroupList()
    this.fetchData()
  },

  methods: {
    fetchJobGroupList: function () {
      var self = this
      findAll().then(function (res) {
        // /jobgroup/pageList 返回 { recordsTotal, recordsFiltered, data }
        var data = res.data.data || res.data.content || []
        self.jobGroupList = data
      }).catch(function (e) {
        console.error('获取执行器列表失败', e)
      })
    },

    fetchData: function () {
      var self = this
      this.loading = true

      // jobDesc/triggerStatus/author 传给后端筛选，其余前端自己筛
      var params = {
        start: 0,
        length: 10000,
        jobGroup: -1,
        triggerStatus: this.queryParams.triggerStatus !== ''
          ? this.queryParams.triggerStatus
          : -1,
        jobDesc: this.queryParams.jobDesc || '',
        executorHandler: '',
        author: this.queryParams.author || ''
      }

      pageList(params).then(function (res) {
        self.loading = false
        if (res.data.code === 200) {
          self.allTableData = res.data.content || []
        } else if (res.data.data) {
          self.allTableData = res.data.data || []
        }
        // 重置到第一页
        self.queryParams.start = 1
      }).catch(function (e) {
        self.loading = false
        self.$message.error('获取任务列表失败：' + (e.message || '网络异常'))
      })
    },

    handleSearch: function () {
      this.fetchData()
    },

    handleReset: function () {
      this.queryParams.jobDesc = ''
      this.queryParams.cycleType = ''
      this.queryParams.triggerStatus = ''
      this.queryParams.author = ''
      this.fetchData()
    },

    handlePageChange: function () {
      // 无需额外处理，computed 属性自动响应 current-page / page-size 变化
    },

    handleEdit: function (row) {
      var self = this
      this.currentEditData = Object.assign({}, row)
      this.formDialogVisible = true
      this.$nextTick(function () {
        if (self.$refs.formDialogRef) {
          self.$refs.formDialogRef.fillEditData(row)
        }
      })
    },

    handleToggleStatus: function (row) {
      var self = this
      var isRunning = row.triggerStatus === 1
      var actionName = isRunning ? '禁用' : '启用'
      var requestFn = isRunning ? stopJob : startJob

      requestFn(row.id).then(function (res) {
        if (res.data.code === 200) {
          self.$message.success(actionName + '成功')
          self.fetchData()
        } else {
          self.$message.error(res.data.msg || (actionName + '失败'))
        }
      }).catch(function (e) {
        self.$message.error(actionName + '失败：' + (e.message || '网络异常'))
      })
    },

    handleTrigger: function (row) {
      this.triggerJobId = row.id
      this.triggerForm.executorParam = row.executorParam || ''
      this.triggerForm.addressList = ''
      this.triggerDialogVisible = true
    },

    submitTrigger: function () {
      var self = this
      this.triggerSubmitting = true
      triggerJob(this.triggerJobId, this.triggerForm.executorParam, this.triggerForm.addressList).then(function (res) {
        self.triggerSubmitting = false
        if (res.data.code === 200) {
          self.$message.success('执行成功')
          self.triggerDialogVisible = false
          self.fetchData()
        } else {
          self.$message.error(res.data.msg || '执行失败')
        }
      }).catch(function (e) {
        self.triggerSubmitting = false
        self.$message.error('执行失败：' + (e.message || '网络异常'))
      })
    },

    formatScheduleConf: function (row) {
      if (row.scheduleType === 'NONE') {
        var extra = {}
        try { extra = JSON.parse(row.executorParam || '{}') } catch (e) {}
        return extra.onceExecuteTime || '指定时间'
      }
      if (row.scheduleType === 'CRON') {
        var dayMatch = row.scheduleConf && row.scheduleConf.match(/^0 0 0 \*\/(\d+) \* \?$/)
        if (dayMatch) return '每' + dayMatch[1] + '日'
        return row.scheduleConf || ''
      }
      if (row.scheduleType === 'FIX_RATE') {
        var secs = parseInt(row.scheduleConf)
        if (!secs) return ''
        if (secs % 3600 === 0) return '每' + (secs / 3600) + '小时'
        if (secs % 60 === 0) return '每' + (secs / 60) + '分钟'
        return '每' + secs + '秒'
      }
      return row.scheduleConf || ''
    },

    formatTime: function (timestamp) {
      if (!timestamp) return '-'
      var date = new Date(timestamp)
      var y = date.getFullYear()
      var m = ('0' + (date.getMonth() + 1)).slice(-2)
      var d = ('0' + date.getDate()).slice(-2)
      var h = ('0' + date.getHours()).slice(-2)
      var min = ('0' + date.getMinutes()).slice(-2)
      var s = ('0' + date.getSeconds()).slice(-2)
      return y + '-' + m + '-' + d + ' ' + h + ':' + min + ':' + s
    }
  }
}
</script>
