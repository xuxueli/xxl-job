<template>
  <div class="page-container">
    <!-- 搜索栏 -->
    <el-card shadow="never" class="search-card">
      <el-row :gutter="16" align="middle">
        <el-col :span="5">
          <el-input v-model="queryParams.appname" placeholder="AppName" clearable @keyup.enter.native="handleSearch" />
        </el-col>
        <el-col :span="5">
          <el-input v-model="queryParams.title" placeholder="执行器名称" clearable @keyup.enter.native="handleSearch" />
        </el-col>
        <el-col :span="10">
          <el-button type="primary" @click="handleSearch">搜索</el-button>
          <el-button type="success" @click="handleAdd">新增执行器</el-button>
        </el-col>
      </el-row>
    </el-card>

    <!-- 执行器列表 -->
    <el-card shadow="never" class="table-card">
      <el-table
        v-loading="loading"
        :data="tableData"
        border
        stripe
        style="width: 100%"
        size="default"
      >
        <el-table-column prop="appname" label="AppName" min-width="200" align="center" />
        <el-table-column prop="title" label="执行器名称" min-width="180" align="center" />
        <el-table-column label="注册方式" width="120" align="center">
          <template v-slot="{ row }">
            <el-tag :type="row.addressType === 0 ? 'success' : 'warning'" size="small">
              {{ row.addressType === 0 ? '自动注册' : '手动录入' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="OnLine 机器地址" width="160" align="center">
          <template v-slot="{ row }">
            <el-button
              v-if="row.registryList && row.registryList.length > 0"
              type="text"
              size="small"
              @click="handleShowRegistry(row)"
            >
              查看 ({{ row.registryList.length }})
            </el-button>
            <span v-else style="color: #c0c4cc">-</span>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="140" align="center">
          <template v-slot="{ row }">
            <div class="table-actions">
              <el-button type="text" size="small" @click="handleEdit(row)">编辑</el-button>
              <el-popconfirm title="确定删除该执行器？" @confirm="handleDelete(row)">
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
    <el-dialog
      :visible.sync="formDialogVisible"
      :title="isEdit ? '编辑执行器' : '新增执行器'"
      width="560px"
      :close-on-click-modal="false"
      destroy-on-close
      @closed="handleFormClosed"
    >
      <el-form ref="formRef" :model="form" :rules="rules" label-width="100px">
        <el-form-item label="AppName" prop="appname">
          <el-input v-model="form.appname" placeholder="请输入AppName" maxlength="64" />
          <div style="color: #909399; font-size: 12px; margin-top: 2px">
            以小写字母开头，仅包含小写字母、数字和中划线
          </div>
        </el-form-item>
        <el-form-item label="执行器名称" prop="title">
          <el-input v-model="form.title" placeholder="请输入执行器名称" maxlength="12" />
        </el-form-item>
        <el-form-item label="注册方式" prop="addressType">
          <el-radio-group v-model="form.addressType" @change="handleAddressTypeChange">
            <el-radio :label="0">自动注册</el-radio>
            <el-radio :label="1">手动录入</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="机器地址">
          <el-input
            v-model="form.addressList"
            type="textarea"
            :rows="5"
            :placeholder="form.addressType === 0 ? '自动注册时无需填写' : '请输入机器地址，多个用逗号分隔'"
            :readonly="form.addressType === 0"
            :style="{ backgroundColor: form.addressType === 0 ? '#f5f7fa' : '#fff' }"
          />
        </el-form-item>
      </el-form>
      <template v-slot:footer>
        <el-button @click="formDialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="submitting" @click="handleSubmit">保存</el-button>
      </template>
    </el-dialog>

    <!-- 注册地址查看对话框 -->
    <el-dialog
      :visible.sync="registryDialogVisible"
      title="OnLine 机器地址"
      width="500px"
    >
      <el-table :data="registryTableData" border size="default">
        <el-table-column label="序号" type="index" width="60" align="center" />
        <el-table-column prop="address" label="机器地址" align="center" />
      </el-table>
      <template v-slot:footer>
        <el-button @click="registryDialogVisible = false">关闭</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script>
import { pageList, save, update, remove, loadById } from '@/api/jobgroup'

export default {
  name: 'JobGroupIndex',

  data: function () {
    var validateAppname = function (rule, value, callback) {
      if (!value) {
        callback(new Error('请输入AppName'))
        return
      }
      if (value.length < 4 || value.length > 64) {
        callback(new Error('AppName长度限制为4~64'))
        return
      }
      if (!/^[a-z][a-zA-Z0-9-]*$/.test(value)) {
        callback(new Error('以小写字母开头，仅包含小写字母、数字和中划线'))
        return
      }
      callback()
    }

    return {
      loading: false,
      tableData: [],
      total: 0,

      queryParams: {
        start: 1,
        length: 10,
        appname: '',
        title: ''
      },

      // 表单对话框
      formDialogVisible: false,
      isEdit: false,
      submitting: false,
      editId: null,
      form: {
        appname: '',
        title: '',
        addressType: 0,
        addressList: ''
      },
      rules: {
        appname: [
          { required: true, validator: validateAppname, trigger: 'blur' }
        ],
        title: [
          { required: true, message: '请输入执行器名称', trigger: 'blur' },
          { min: 4, max: 12, message: '名称长度限制为4~12', trigger: 'blur' }
        ],
        addressType: [
          { required: true, message: '请选择注册方式', trigger: 'change' }
        ]
      },

      // 注册地址查看
      registryDialogVisible: false,
      registryTableData: []
    }
  },

  mounted: function () {
    this.fetchData()
  },

  methods: {
    fetchData: function () {
      var self = this
      this.loading = true

      var startOffset = (this.queryParams.start - 1) * this.queryParams.length
      if (startOffset < 0) startOffset = 0

      var params = {
        start: startOffset,
        length: this.queryParams.length,
        appname: this.queryParams.appname || '',
        title: this.queryParams.title || ''
      }

      pageList(params).then(function (res) {
        self.loading = false
        // API 返回 { recordsTotal, recordsFiltered, data }
        self.tableData = res.data.data || []
        self.total = res.data.recordsTotal || 0
      }).catch(function (e) {
        self.loading = false
        self.$message.error('获取执行器列表失败：' + (e.message || '网络异常'))
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
      this.editId = null
      this.form = {
        appname: '',
        title: '',
        addressType: 0,
        addressList: ''
      }
      this.formDialogVisible = true
    },

    handleEdit: function (row) {
      var self = this
      this.isEdit = true
      this.editId = row.id
      // 需要获取最新的地址列表（自动注册模式），所以 re-load
      loadById(row.id).then(function (res) {
        // loadById 返回 ReturnT<XxlJobGroup>: { code, content }
        var data = (res.data.code === 200 ? res.data.content : null) || row
        self.form.appname = data.appname || ''
        self.form.title = data.title || ''
        self.form.addressType = data.addressType != null ? data.addressType : 0
        self.form.addressList = data.addressList || ''
        self.formDialogVisible = true
      }).catch(function () {
        // fallback
        self.form.appname = row.appname || ''
        self.form.title = row.title || ''
        self.form.addressType = row.addressType != null ? row.addressType : 0
        self.form.addressList = row.addressList || ''
        self.formDialogVisible = true
      })
    },

    handleAddressTypeChange: function (val) {
      if (val === 0) {
        this.form.addressList = ''
      }
    },

    handleSubmit: function () {
      var self = this
      this.$refs.formRef.validate(function (valid) {
        if (!valid) return

        self.submitting = true
        var data = {
          appname: self.form.appname,
          title: self.form.title,
          addressType: self.form.addressType,
          addressList: self.form.addressList
        }

        var requestFn = self.isEdit ? update : save
        if (self.isEdit) {
          data.id = self.editId
        }

        requestFn(data).then(function (res) {
          self.submitting = false
          if (res.data.code === 200) {
            self.$message.success(self.isEdit ? '更新成功' : '新增成功')
            self.formDialogVisible = false
            self.fetchData()
          } else {
            self.$message.error(res.data.msg || (self.isEdit ? '更新失败' : '新增失败'))
          }
        }).catch(function (e) {
          self.submitting = false
          self.$message.error('操作失败：' + (e.message || '网络异常'))
        })
      })
    },

    handleFormClosed: function () {
      if (this.$refs.formRef) {
        this.$refs.formRef.resetFields()
      }
    },

    handleDelete: function (row) {
      var self = this
      remove(row.id).then(function (res) {
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

    handleShowRegistry: function (row) {
      this.registryTableData = (row.registryList || []).map(function (addr) {
        return { address: addr }
      })
      this.registryDialogVisible = true
    }
  }
}
</script>
