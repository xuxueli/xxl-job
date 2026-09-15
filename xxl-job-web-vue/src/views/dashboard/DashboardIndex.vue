<template>
  <div class="page-container">
    <!-- 统计卡片 -->
    <el-row :gutter="16" class="stats-row">
      <el-col :span="8">
        <div class="stat-card stat-card--blue">
          <div class="stat-card__icon">
            <i class="el-icon-s-flag"></i>
          </div>
          <div class="stat-card__body">
            <div class="stat-card__label">任务数量</div>
            <div class="stat-card__value">{{ stats.jobInfoCount }}</div>
            <div class="stat-card__desc">调度中心运行的任务总量</div>
          </div>
        </div>
      </el-col>
      <el-col :span="8">
        <div class="stat-card stat-card--yellow">
          <div class="stat-card__icon">
            <i class="el-icon-date"></i>
          </div>
          <div class="stat-card__body">
            <div class="stat-card__label">调度次数</div>
            <div class="stat-card__value">{{ stats.jobLogCount }}</div>
            <div class="stat-card__desc">调度中心触发的调度总次数</div>
          </div>
        </div>
      </el-col>
      <el-col :span="8">
        <div class="stat-card stat-card--green">
          <div class="stat-card__icon">
            <i class="el-icon-setting"></i>
          </div>
          <div class="stat-card__body">
            <div class="stat-card__label">执行器数量</div>
            <div class="stat-card__value">{{ stats.executorCount }}</div>
            <div class="stat-card__desc">调度中心在线的执行器机器总数</div>
          </div>
        </div>
      </el-col>
    </el-row>

    <!-- 调度报表 -->
    <el-card shadow="never" class="chart-card">
      <div slot="header" class="chart-header">
        <span>调度报表</span>
        <el-date-picker
          v-model="dateRange"
          type="datetimerange"
          start-placeholder="开始时间"
          end-placeholder="结束时间"
          value-format="yyyy-MM-dd HH:mm:ss"
          :default-time="['00:00:00', '23:59:59']"
          @change="handleDateChange"
        />
      </div>
      <el-row :gutter="16">
        <el-col :span="16">
          <div ref="lineChartRef" class="chart-box"></div>
        </el-col>
        <el-col :span="8">
          <div ref="pieChartRef" class="chart-box"></div>
        </el-col>
      </el-row>
    </el-card>
  </div>
</template>

<script>
import { chartInfo } from '@/api/dashboard'
import { pageList as jobInfoPageList } from '@/api/jobinfo'
import { pageList as jobLogPageList } from '@/api/joblog'
import { findAll } from '@/api/jobgroup'
import * as echarts from 'echarts'

export default {
  name: 'DashboardIndex',

  data: function () {
    return {
      stats: {
        jobInfoCount: 0,
        jobLogCount: 0,
        executorCount: 0
      },

      dateRange: [],
      lineChart: null,
      pieChart: null
    }
  },

  mounted: function () {
    this.fetchStats()
    this.initCharts()
    // 默认加载最近一周数据
    this.loadChartData(this.getDefaultStart(), this.getDefaultEnd())
  },

  beforeDestroy: function () {
    if (this.lineChart) {
      this.lineChart.dispose()
    }
    if (this.pieChart) {
      this.pieChart.dispose()
    }
  },

  methods: {
    getDefaultStart: function () {
      var d = new Date()
      d.setDate(d.getDate() - 7)
      d.setHours(0, 0, 0, 0)
      return this.formatDate(d)
    },

    getDefaultEnd: function () {
      var d = new Date()
      d.setHours(23, 59, 59, 0)
      return this.formatDate(d)
    },

    formatDate: function (date) {
      var y = date.getFullYear()
      var m = ('0' + (date.getMonth() + 1)).slice(-2)
      var d = ('0' + date.getDate()).slice(-2)
      var h = ('0' + date.getHours()).slice(-2)
      var min = ('0' + date.getMinutes()).slice(-2)
      var s = ('0' + date.getSeconds()).slice(-2)
      return y + '-' + m + '-' + d + ' ' + h + ':' + min + ':' + s
    },

    fetchStats: function () {
      var self = this

      // 任务总数
      jobInfoPageList({ start: 0, length: 1, jobGroup: -1, triggerStatus: -1, jobDesc: '', executorHandler: '', author: '' }).then(function (res) {
        if (res.data.code === 200) {
          self.stats.jobInfoCount = res.data.total_count || res.data.recordsTotal || 0
        }
      }).catch(function () {})

      // 调度总数
      jobLogPageList({ start: 0, length: 1, jobGroup: -1, jobId: 0, logStatus: -1, filterTime: '' }).then(function (res) {
        self.stats.jobLogCount = res.data.recordsTotal || 0
      }).catch(function () {})

      // 执行器机器数
      findAll().then(function (res) {
        var groups = res.data.data || res.data.content || []
        var addressSet = {}
        groups.forEach(function (group) {
          if (group.registryList && group.registryList.length > 0) {
            group.registryList.forEach(function (addr) {
              addressSet[addr] = true
            })
          }
        })
        self.stats.executorCount = Object.keys(addressSet).length
      }).catch(function () {})
    },

    initCharts: function () {
      this.lineChart = echarts.init(this.$refs.lineChartRef)
      this.pieChart = echarts.init(this.$refs.pieChartRef)
    },

    handleDateChange: function () {
      if (this.dateRange && this.dateRange.length === 2) {
        this.loadChartData(this.dateRange[0], this.dateRange[1])
      }
    },

    loadChartData: function (startDate, endDate) {
      var self = this
      chartInfo(startDate, endDate).then(function (res) {
        if (res.data.code === 200) {
          var data = res.data.content
          self.renderLineChart(data)
          self.renderPieChart(data)
        } else {
          self.$message.error(res.data.msg || '加载报表数据失败')
        }
      }).catch(function (e) {
        self.$message.error('加载报表数据失败：' + (e.message || '网络异常'))
      })
    },

    renderLineChart: function (data) {
      if (!this.lineChart) return
      this.lineChart.setOption({
        title: {
          text: '调度日期报表',
          left: 'center',
          textStyle: { fontSize: 14 }
        },
        tooltip: {
          trigger: 'axis',
          axisPointer: { type: 'cross' }
        },
        legend: {
          data: ['成功', '失败', '运行中'],
          top: 28
        },
        grid: {
          left: '3%',
          right: '4%',
          bottom: '3%',
          top: '60px',
          containLabel: true
        },
        xAxis: {
          type: 'category',
          boundaryGap: false,
          data: data.triggerDayList || []
        },
        yAxis: {
          type: 'value'
        },
        series: [
          {
            name: '成功',
            type: 'line',
            stack: 'Total',
            areaStyle: {},
            data: data.triggerDayCountSucList || []
          },
          {
            name: '失败',
            type: 'line',
            stack: 'Total',
            label: { show: true, position: 'top' },
            areaStyle: {},
            data: data.triggerDayCountFailList || []
          },
          {
            name: '运行中',
            type: 'line',
            stack: 'Total',
            areaStyle: {},
            data: data.triggerDayCountRunningList || []
          }
        ],
        color: ['#00A65A', '#c23632', '#F39C12']
      })
    },

    renderPieChart: function (data) {
      if (!this.pieChart) return
      this.pieChart.setOption({
        title: {
          text: '调度成功率',
          left: 'center',
          textStyle: { fontSize: 14 }
        },
        tooltip: {
          trigger: 'item',
          formatter: '{b}: {c} ({d}%)'
        },
        legend: {
          orient: 'vertical',
          left: 'left',
          top: 30,
          data: ['成功', '失败', '运行中']
        },
        series: [
          {
            type: 'pie',
            radius: '55%',
            center: ['50%', '55%'],
            data: [
              { name: '成功', value: data.triggerCountSucTotal || 0 },
              { name: '失败', value: data.triggerCountFailTotal || 0 },
              { name: '运行中', value: data.triggerCountRunningTotal || 0 }
            ],
            emphasis: {
              itemStyle: {
                shadowBlur: 10,
                shadowOffsetX: 0,
                shadowColor: 'rgba(0, 0, 0, 0.5)'
              }
            }
          }
        ],
        color: ['#00A65A', '#c23632', '#F39C12']
      })
    }
  }
}
</script>

<style scoped>
.stats-row {
  margin-bottom: 16px;
}

.stat-card {
  display: flex;
  align-items: center;
  padding: 16px 20px;
  border-radius: 4px;
  color: #fff;
  height: 100px;
}

.stat-card--blue {
  background: linear-gradient(135deg, #00c0ef, #00a8d3);
}

.stat-card--yellow {
  background: linear-gradient(135deg, #f39c12, #e08e0b);
}

.stat-card--green {
  background: linear-gradient(135deg, #00a65a, #008d4c);
}

.stat-card__icon {
  font-size: 40px;
  margin-right: 16px;
  opacity: 0.5;
}

.stat-card__body {
  flex: 1;
}

.stat-card__label {
  font-size: 13px;
  opacity: 0.9;
}

.stat-card__value {
  font-size: 28px;
  font-weight: 700;
  line-height: 1.2;
}

.stat-card__desc {
  font-size: 11px;
  opacity: 0.75;
  margin-top: 2px;
}

.chart-card {
  margin-bottom: 16px;
}

.chart-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.chart-box {
  height: 350px;
}
</style>
