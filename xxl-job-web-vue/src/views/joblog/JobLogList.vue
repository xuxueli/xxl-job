<template>
  <div class="page-container">
    <!-- 搜索栏 -->
    <el-card shadow="never" class="search-card">
      <el-form :model="queryParams" :inline="true" size="default">
        <el-form-item label="执行器">
          <el-select
            v-model="queryParams.jobGroup"
            placeholder="请选择执行器"
            clearable
            style="width: 160px"
            @change="handleJobGroupChange"
          >
            <el-option label="全部" :value="-1" />
            <el-option
              v-for="group in jobGroupList"
              :key="group.id"
              :label="group.title"
              :value="group.id"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="任务名称">
          <el-select
            v-model="queryParams.jobId"
            placeholder="请选择任务"
            clearable
            filterable
            style="width: 200px"
          >
            <el-option label="全部" :value="0" />
            <el-option
              v-for="job in jobListByGroup"
              :key="job.id"
              :label="job.jobDesc"
              :value="job.id"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="执行状态">
          <el-select v-model="queryParams.logStatus" placeholder="全部" style="width: 120px">
            <el-option label="全部" :value="-1" />
            <el-option label="成功" :value="1" />
            <el-option label="失败" :value="2" />
            <el-option label="运行中" :value="3" />
          </el-select>
        </el-form-item>
        <el-form-item label="触发时间">
          <el-date-picker
            v-model="queryParams.filterTime"
            type="datetimerange"
            range-separator="至"
            start-placeholder="开始时间"
            end-placeholder="结束时间"
            value-format="yyyy-MM-dd HH:mm:ss"
            :default-time="['00:00:00', '23:59:59']"
            style="width: 380px"
          />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">查询</el-button>
          <el-button @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <!-- 日志列表 -->
    <el-card shadow="never" class="table-card">
      <el-table
        ref="tableRef"
        v-loading="loading"
        :data="tableData"
        border
        stripe
        style="width: 100%"
        size="default"
      >
        <el-table-column label="任务ID" width="100" align="center">
          <template v-slot="{ row }">
            <el-tooltip placement="top" effect="dark">
              <div slot="content">
                <div>执行器地址：{{ row.executorAddress || '-' }}</div>
                <div v-if="row.executorHandler">JobHandler：{{ row.executorHandler }}</div>
                <div>任务参数：{{ row.executorParam || '-' }}</div>
              </div>
              <span class="log-job-link">{{ row.jobId }}</span>
            </el-tooltip>
          </template>
        </el-table-column>
        <el-table-column label="触发时间" width="170" align="center">
          <template v-slot="{ row }">
            {{ formatDateTime(row.triggerTime) }}
          </template>
        </el-table-column>
        <el-table-column label="触发结果" width="90" align="center">
          <template v-slot="{ row }">
            <el-tag v-if="row.triggerCode === 200" type="success" size="small">成功</el-tag>
            <el-tag v-else-if="row.triggerCode === 500" type="danger" size="small">失败</el-tag>
            <span v-else-if="row.triggerCode === 0" style="color: #909399">-</span>
            <span v-else>{{ row.triggerCode }}</span>
          </template>
        </el-table-column>
        <el-table-column label="触发信息" width="80" align="center">
          <template v-slot="{ row }">
            <el-button
              v-if="row.triggerMsg"
              type="text"
              size="small"
              @click="showDetail('触发信息', row.triggerMsg)"
            >查看</el-button>
            <span v-else style="color: #c0c4cc">-</span>
          </template>
        </el-table-column>
        <el-table-column label="处理时间" width="200" align="center">
          <template v-slot="{ row }">
            {{ formatDateTime(row.handleTime) }}
          </template>
        </el-table-column>
        <el-table-column label="处理结果" width="200" align="center">
          <template v-slot="{ row }">
            <el-tag v-if="row.handleCode === 200" type="success" size="small">成功</el-tag>
            <el-tag v-else-if="row.handleCode === 500" type="danger" size="small">失败</el-tag>
            <el-tag v-else-if="row.handleCode === 502" type="warning" size="small">超时</el-tag>
            <span v-else-if="row.handleCode === 0" style="color: #909399">-</span>
            <span v-else>{{ row.handleCode }}</span>
          </template>
        </el-table-column>
        <el-table-column label="处理信息" width="200" align="center">
          <template v-slot="{ row }">
            <el-button
              v-if="row.handleMsg"
              type="text"
              size="small"
              @click="showDetail('处理信息', row.handleMsg)"
            >查看</el-button>
            <span v-else style="color: #c0c4cc">-</span>
          </template>
        </el-table-column>
        <el-table-column label="执行耗时" width="100" align="center">
          <template v-slot="{ row }">
            <span v-if="row.triggerTime && row.handleTime">
              {{ calcDuration(row.triggerTime, row.handleTime) }}
            </span>
            <span v-else style="color: #c0c4cc">-</span>
          </template>
        </el-table-column>
        <el-table-column label="操作" min-width="120" align="center">
          <template v-slot="{ row }">
            <div class="table-actions">
              <el-button
                v-if="row.triggerCode === 200 || row.handleCode !== 0"
                type="text"
                size="small"
                @click="handleViewLog(row)"
              >执行日志</el-button>
              <el-button
                v-if="row.triggerCode === 200 && row.handleCode === 0"
                type="text"
                size="small"
                style="color: #f56c6c"
                @click="handleKill(row)"
              >终止任务</el-button>
            </div>
          </template>
        </el-table-column>
      </el-table>

      <div style="display: flex; justify-content: space-between; align-items: center; margin-top: 16px">
        <el-button size="small" @click="handleClearLog">清理日志</el-button>
        <el-pagination
          :current-page.sync="queryParams.start"
          :page-size="queryParams.length"
          :page-sizes="[10, 20, 50, 100]"
          :total="total"
          layout="total, sizes, prev, pager, next, jumper"
          background
          @size-change="handlePageChange"
          @current-change="handlePageChange"
        />
      </div>
    </el-card>

    <!-- 信息查看对话框 -->
    <el-dialog :visible.sync="detailDialogVisible" :title="detailTitle" width="600px">
      <div class="detail-content" v-html="detailContent"></div>
      <template v-slot:footer>
        <el-button @click="detailDialogVisible = false">关闭</el-button>
      </template>
    </el-dialog>

    <!-- 执行日志对话框 -->
    <el-dialog
      :visible.sync="logDetailDialogVisible"
      title="执行日志"
      width="800px"
      @opened="handleLogDetailOpened"
    >
      <div class="log-detail-body" ref="logDetailBody">
        <pre v-if="logDetailText">{{ logDetailText }}</pre>
        <div v-else style="text-align: center; padding: 40px; color: #909399">
          <i class="el-icon-loading" style="font-size: 24px"></i>
          <p>加载中...</p>
        </div>
      </div>
      <template v-slot:footer>
        <el-button @click="loadMoreLog" :disabled="logDetailEnd || logDetailLoading">
          {{ logDetailEnd ? '已加载全部' : '加载更多' }}
        </el-button>
        <el-button @click="logDetailDialogVisible = false">关闭</el-button>
      </template>
    </el-dialog>

    <!-- 清理日志对话框 -->
    <el-dialog :visible.sync="clearLogDialogVisible" title="清理日志" width="500px">
      <el-form label-width="120px">
        <el-form-item label="执行器">
          <el-input :value="clearLogForm.jobGroupText" disabled />
        </el-form-item>
        <el-form-item label="任务">
          <el-input :value="clearLogForm.jobIdText" disabled />
        </el-form-item>
        <el-form-item label="清理类型">
          <el-select v-model="clearLogForm.type" style="width: 100%">
            <el-option label="清理一个月之前日志数据" :value="1" />
            <el-option label="清理三个月之前日志数据" :value="2" />
            <el-option label="清理六个月之前日志数据" :value="3" />
            <el-option label="清理一年之前日志数据" :value="4" />
            <el-option label="清理一千条以前日志数据" :value="5" />
            <el-option label="清理一万条以前日志数据" :value="6" />
            <el-option label="清理三万条以前日志数据" :value="7" />
            <el-option label="清理十万条以前日志数据" :value="8" />
            <el-option label="清理所有日志数据" :value="9" />
          </el-select>
        </el-form-item>
      </el-form>
      <template v-slot:footer>
        <el-button @click="clearLogDialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="clearLogSubmitting" @click="submitClearLog">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script>
import { pageList, getJobsByGroup, logDetailCat, logKill, clearLog } from '@/api/joblog'
import { findAll } from '@/api/jobgroup'

export default {
  name: 'JobLogList',

  data: function () {
    return {
      loading: false,
      tableData: [],
      total: 0,
      jobGroupList: [],
      jobListByGroup: [],

      queryParams: {
        start: 1,
        length: 10,
        jobGroup: -1,
        jobId: 0,
        logStatus: -1,
        filterTime: ''
      },

      // 信息查看
      detailDialogVisible: false,
      detailTitle: '',
      detailContent: '',

      // 执行日志
      logDetailDialogVisible: false,
      logDetailLoading: false,
      logDetailEnd: false,
      logDetailText: '',
      logDetailLogId: null,
      logDetailFromLine: 1,

      // 清理日志
      clearLogDialogVisible: false,
      clearLogSubmitting: false,
      clearLogForm: {
        jobGroup: -1,
        jobGroupText: '',
        jobId: 0,
        jobIdText: '',
        type: 1
      }
    }
  },

  mounted: function () {
    var self = this
    this.fetchJobGroupList().then(function () {
      self.fetchData()
    })
  },

  methods: {
    fetchJobGroupList: function () {
      var self = this
      return findAll().then(function (res) {
        if (res.data.code === 200) {
          self.jobGroupList = res.data.content || []
        }
      }).catch(function (e) {
        console.error('获取执行器列表失败', e)
      })
    },

    handleJobGroupChange: function (val) {
      var self = this
      this.queryParams.jobId = 0
      this.jobListByGroup = []

      if (!val || val === -1) {
        this.fetchData()
        return
      }

      getJobsByGroup(val).then(function (res) {
        if (res.data.code === 200) {
          self.jobListByGroup = res.data.content || []
        }
        self.fetchData()
      }).catch(function () {
        self.fetchData()
      })
    },

    fetchData: function () {
      var self = this
      this.loading = true

      var startOffset = (this.queryParams.start - 1) * this.queryParams.length
      if (startOffset < 0) startOffset = 0

      var filterTimeStr = ''
      if (this.queryParams.filterTime && this.queryParams.filterTime.length === 2) {
        filterTimeStr = this.queryParams.filterTime[0] + ' - ' + this.queryParams.filterTime[1]
      }

      var params = {
        start: startOffset,
        length: this.queryParams.length,
        jobGroup: this.queryParams.jobGroup || -1,
        jobId: this.queryParams.jobId || 0,
        logStatus: this.queryParams.logStatus,
        filterTime: filterTimeStr
      }

      pageList(params).then(function (res) {
        self.loading = false
        if (res.data.code && res.data.code !== 200) {
          self.$message.error(res.data.msg || '查询失败')
          return
        }
        // API 返回格式: { recordsTotal, recordsFiltered, data }
        self.tableData = res.data.data || []
        self.total = res.data.recordsTotal || 0
      }).catch(function (e) {
        self.loading = false
        self.$message.error('获取日志列表失败：' + (e.message || '网络异常'))
      })
    },

    handleSearch: function () {
      this.queryParams.start = 1
      this.fetchData()
    },

    handleReset: function () {
      this.queryParams.jobGroup = -1
      this.queryParams.jobId = 0
      this.queryParams.logStatus = -1
      this.queryParams.filterTime = ''
      this.queryParams.start = 1
      this.jobListByGroup = []
      this.fetchData()
    },

    handlePageChange: function () {
      this.fetchData()
    },

    showDetail: function (title, content) {
      this.detailTitle = title
      this.detailContent = content
      this.detailDialogVisible = true
    },

    handleViewLog: function (row) {
      this.logDetailLogId = row.id
      this.logDetailText = ''
      this.logDetailFromLine = 1
      this.logDetailEnd = false
      this.logDetailDialogVisible = true
    },

    handleLogDetailOpened: function () {
      if (!this.logDetailText) {
        this.loadMoreLog()
      }
    },

    loadMoreLog: function () {
      var self = this
      this.logDetailLoading = true
      logDetailCat(this.logDetailLogId, this.logDetailFromLine).then(function (res) {
        self.logDetailLoading = false
        if (res.data.code === 200 && res.data.content) {
          var result = res.data.content
          if (self.logDetailText) {
            self.logDetailText += '\n' + result.logContent
          } else {
            self.logDetailText = result.logContent || ''
          }
          self.logDetailFromLine = result.toLineNum + 1
          if (result.end) {
            self.logDetailEnd = true
          }
        } else {
          self.$message.error(res.data.msg || '获取日志详情失败')
        }
      }).catch(function (e) {
        self.logDetailLoading = false
        self.$message.error('获取日志详情失败：' + (e.message || '网络异常'))
      })
    },

    handleKill: function (row) {
      var self = this
      this.$confirm('确认终止该任务的执行？', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(function () {
        logKill(row.id).then(function (res) {
          if (res.data.code === 200) {
            self.$message.success('终止成功')
            self.fetchData()
          } else {
            self.$message.error(res.data.msg || '终止失败')
          }
        }).catch(function (e) {
          self.$message.error('终止失败：' + (e.message || '网络异常'))
        })
      }).catch(function () {})
    },

    handleClearLog: function () {
      var self = this
      // 获取当前选中的执行器和任务名称
      var jobGroupText = '全部'
      var jobIdText = '全部'

      if (this.queryParams.jobGroup && this.queryParams.jobGroup !== -1) {
        var group = this.jobGroupList.find(function (g) { return g.id === self.queryParams.jobGroup })
        jobGroupText = group ? group.title : this.queryParams.jobGroup
      }

      if (this.queryParams.jobId && this.queryParams.jobId !== 0) {
        var job = this.jobListByGroup.find(function (j) { return j.id === self.queryParams.jobId })
        jobIdText = job ? job.jobDesc : this.queryParams.jobId
      }

      this.clearLogForm.jobGroup = this.queryParams.jobGroup || -1
      this.clearLogForm.jobGroupText = jobGroupText
      this.clearLogForm.jobId = this.queryParams.jobId || 0
      this.clearLogForm.jobIdText = jobIdText
      this.clearLogForm.type = 1
      this.clearLogDialogVisible = true
    },

    submitClearLog: function () {
      var self = this
      this.clearLogSubmitting = true
      clearLog(
        this.clearLogForm.jobGroup,
        this.clearLogForm.jobId,
        this.clearLogForm.type
      ).then(function (res) {
        self.clearLogSubmitting = false
        if (res.data.code === 200) {
          self.$message.success('清理成功')
          self.clearLogDialogVisible = false
          self.fetchData()
        } else {
          self.$message.error(res.data.msg || '清理失败')
        }
      }).catch(function (e) {
        self.clearLogSubmitting = false
        self.$message.error('清理失败：' + (e.message || '网络异常'))
      })
    },

    formatDateTime: function (timestamp) {
      if (!timestamp) return '-'
      var date = new Date(timestamp)
      if (isNaN(date.getTime())) return timestamp
      var y = date.getFullYear()
      var m = ('0' + (date.getMonth() + 1)).slice(-2)
      var d = ('0' + date.getDate()).slice(-2)
      var h = ('0' + date.getHours()).slice(-2)
      var mi = ('0' + date.getMinutes()).slice(-2)
      var s = ('0' + date.getSeconds()).slice(-2)
      return y + '-' + m + '-' + d + ' ' + h + ':' + mi + ':' + s
    },

    calcDuration: function (startTime, endTime) {
      if (!startTime || !endTime) return '-'
      var start = new Date(startTime).getTime()
      var end = new Date(endTime).getTime()
      var diff = end - start
      if (diff < 0) return '-'
      if (diff < 1000) return diff + 'ms'
      if (diff < 60000) return (diff / 1000).toFixed(1) + 's'
      return (diff / 60000).toFixed(1) + 'min'
    }
  }
}
</script>

<style scoped>
.log-job-link {
  color: #409eff;
  cursor: pointer;
}

.detail-content {
  white-space: pre-wrap;
  word-break: break-all;
  max-height: 400px;
  overflow-y: auto;
  background: #f5f7fa;
  padding: 16px;
  border-radius: 4px;
  font-size: 13px;
  line-height: 1.6;
}

.log-detail-body {
  max-height: 500px;
  overflow-y: auto;
}

.log-detail-body pre {
  white-space: pre-wrap;
  word-break: break-all;
  background: #1e1e1e;
  color: #d4d4d4;
  padding: 16px;
  border-radius: 4px;
  font-size: 13px;
  line-height: 1.6;
  margin: 0;
  min-height: 100px;
}
</style>
