<template>
  <div class="page-container">
    <!-- 搜索栏 -->
    <el-card shadow="never" class="search-card">
      <el-row :gutter="16" align="middle">
        <el-col :span="4">
          <el-select v-model="queryParams.jobGroup" placeholder="执行器" clearable @change="handleSearch">
            <el-option
              v-for="group in jobGroupList"
              :key="group.id"
              :label="group.title"
              :value="group.id"
            />
          </el-select>
        </el-col>
        <el-col :span="3">
          <el-select v-model="queryParams.triggerStatus" placeholder="状态" clearable @change="handleSearch">
            <el-option label="全部" :value="-1" />
            <el-option label="运行中" :value="1" />
            <el-option label="已停止" :value="0" />
          </el-select>
        </el-col>
        <el-col :span="4">
          <el-input v-model="queryParams.jobDesc" placeholder="任务名称" clearable @change="handleSearch" />
        </el-col>
        <el-col :span="3">
          <el-input v-model="queryParams.author" placeholder="负责人" clearable @change="handleSearch" />
        </el-col>
        <el-col :span="4">
          <el-input v-model="queryParams.executorHandler" placeholder="JobHandler" clearable @change="handleSearch" />
        </el-col>
        <el-col :span="6" style="display: flex; gap: 8px">
          <el-button type="primary" @click="handleSearch">搜索</el-button>
          <el-button type="success" @click="handleAdd">新增任务</el-button>
        </el-col>
      </el-row>
    </el-card>

    <!-- 任务列表 -->
    <el-card shadow="never" class="table-card">
      <el-table
        v-loading="loading"
        :data="tableData"
        border
        stripe
        style="width: 100%"
        size="default"
      >
        <el-table-column prop="id" label="任务ID" width="80" align="center" />
        <el-table-column label="执行器" width="120" align="center">
          <template v-slot="{ row }">
            {{ getGroupTitle(row.jobGroup) }}
          </template>
        </el-table-column>
        <el-table-column prop="jobDesc" label="任务名称" min-width="100" align="center" show-overflow-tooltip />
        <el-table-column label="执行周期" width="180" align="center">
          <template v-slot="{ row }">
            <el-tag v-if="row.scheduleType === 'NONE'" type="info" size="small">一次性任务</el-tag>
            <el-tag v-else type="primary" size="small">周期性任务</el-tag>
            <span style="margin-left: 4px; font-size: 12px; color: #606266">
              {{ formatScheduleConf(row) }}
            </span>
          </template>
        </el-table-column>
        <el-table-column label="运行模式" min-width="120" align="center">
          <template v-slot="{ row }">
            <span v-if="row.executorHandler">{{ row.glueType }}:{{ row.executorHandler }}</span>
            <span v-else>{{ row.glueType }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="author" label="负责人" width="90" align="center" />
        <el-table-column label="状态" width="90" align="center">
          <template v-slot="{ row }">
            <el-tag :type="row.triggerStatus === 1 ? 'success' : 'info'" size="small">
              {{ row.triggerStatus === 1 ? '运行中' : '已停止' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="220" align="center">
          <template v-slot="{ row }">
            <div class="table-actions">
              <el-button type="text" size="small" @click="handleTrigger(row)">执行</el-button>
              <el-button
                type="text"
                :style="{ color: row.triggerStatus === 1 ? '#e6a23c' : '#67c23a' }"
                size="small"
                @click="handleToggleStatus(row)"
              >
                {{ row.triggerStatus === 1 ? '停止' : '启动' }}
              </el-button>
              <el-button type="text" size="small" @click="handleEdit(row)">编辑</el-button>
              <el-button type="text" size="small" @click="handleCopy(row)">复制</el-button>
              <el-popconfirm title="确定删除该任务？" @confirm="handleDelete(row)">
                <el-button slot="reference" type="text" style="color: #f56c6c" size="small">删除</el-button>
              </el-popconfirm>
            </div>
          </template>
        </el-table-column>
      </el-table>

      <div style="display: flex; justify-content: flex-end; margin-top: 16px">
        <el-pagination
          :current-page="queryParams.start"
          :page-size="queryParams.length"
          :page-sizes="[10, 20, 50, 100]"
          :total="total"
          layout="total, sizes, prev, pager, next, jumper"
          background
          @size-change="handleSizeChange"
          @current-change="handlePageChange"
        />
      </div>
    </el-card>

    <!-- 新增/编辑对话框 -->
    <JobFormDialog
      ref="formDialogRef"
      v-model="formDialogVisible"
      :is-edit="isEdit"
      :job-group-list="jobGroupList"
      :edit-data="currentEditData"
      @success="handleFormSuccess"
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
import { pageList, removeJob, stopJob, startJob, triggerJob } from '@/api/jobinfo'
import { findAll } from '@/api/jobgroup'
import JobFormDialog from './components/JobFormDialog.vue'

export default {
  name: 'JobInfoIndex',

  components: {
    JobFormDialog
  },

  data: function () {
    return {
      loading: false,
      tableData: [],
      total: 0,
      jobGroupList: [],

      queryParams: {
        start: 1,
        length: 10,
        jobGroup: '',
        triggerStatus: -1,
        jobDesc: '',
        author: '',
        executorHandler: ''
      },

      // 对话框
      formDialogVisible: false,
      isEdit: false,
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

    getGroupTitle: function (groupId) {
      var group = this.jobGroupList.find(function (g) { return g.id === groupId })
      return group ? group.title : '-'
    },

    fetchData: function () {
      var self = this
      this.loading = true
      var startOffset = (this.queryParams.start - 1) * this.queryParams.length
      if (startOffset < 0) startOffset = 0

      var params = {
        start: startOffset,
        length: this.queryParams.length,
        jobGroup: this.queryParams.jobGroup || -1,
        triggerStatus: this.queryParams.triggerStatus,
        jobDesc: this.queryParams.jobDesc || '',
        executorHandler: this.queryParams.executorHandler || '',
        author: this.queryParams.author || ''
      }

      pageList(params).then(function (res) {
        self.loading = false
        if (res.data.code === 200) {
          self.tableData = res.data.content || []
          self.total = res.data.total_count || 0
        } else {
          // 兼容不同的返回格式
          if (res.data.data) {
            self.tableData = res.data.data || []
            self.total = res.data.recordsTotal || 0
          }
        }
      }).catch(function (e) {
        self.loading = false
        self.$message.error('获取任务列表失败：' + (e.message || '网络异常'))
      })
    },

    handleSearch: function () {
      this.queryParams.start = 1
      this.fetchData()
    },

    handleSizeChange: function (val) {
      this.queryParams.length = val
      this.queryParams.start = 1
      this.fetchData()
    },

    handlePageChange: function (val) {
      this.queryParams.start = val
      this.fetchData()
    },

    handleAdd: function () {
      this.isEdit = false
      this.currentEditData = {}
      this.formDialogVisible = true
    },

    handleEdit: function (row) {
      var self = this
      this.isEdit = true
      this.currentEditData = Object.assign({}, row)
      this.formDialogVisible = true
      // 等待对话框渲染后填充数据
      this.$nextTick(function () {
        if (self.$refs.formDialogRef) {
          self.$refs.formDialogRef.fillEditData(row)
        }
      })
    },

    handleCopy: function (row) {
      var self = this
      this.isEdit = false
      this.currentEditData = {}
      this.formDialogVisible = true
      this.$nextTick(function () {
        if (self.$refs.formDialogRef) {
          self.$refs.formDialogRef.fillEditData(row)
        }
      })
    },

    handleFormSuccess: function () {
      this.fetchData()
    },

    handleToggleStatus: function (row) {
      var self = this
      var isRunning = row.triggerStatus === 1
      var actionName = isRunning ? '停止' : '启动'
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

    handleDelete: function (row) {
      var self = this
      removeJob(row.id).then(function (res) {
        if (res.data.code === 200) {
          self.$message.success('删除成功')
          self.fetchData()
        } else {
          self.$message.error(res.data.msg || '删除失败')
        }
      }).catch(function (e) {
        self.$message.error('删除失败：' + (e.message || '网络异常'))
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
    }
  }
}
</script>
