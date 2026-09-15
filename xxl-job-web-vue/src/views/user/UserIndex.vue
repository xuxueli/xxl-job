<template>
  <div class="page-container">
    <!-- 搜索栏 -->
    <el-card shadow="never" class="search-card">
      <el-row :gutter="16" align="middle">
        <el-col :span="4">
          <el-select v-model="queryParams.role" placeholder="角色" clearable @change="handleSearch">
            <el-option label="全部" :value="-1" />
            <el-option label="管理员" :value="1" />
            <el-option label="普通用户" :value="0" />
          </el-select>
        </el-col>
        <el-col :span="4">
          <el-input v-model="queryParams.username" placeholder="用户名" clearable @keyup.enter.native="handleSearch" />
        </el-col>
        <el-col :span="8">
          <el-button type="primary" @click="handleSearch">搜索</el-button>
          <el-button type="success" @click="handleAdd">新增用户</el-button>
        </el-col>
      </el-row>
    </el-card>

    <!-- 用户列表 -->
    <el-card shadow="never" class="table-card">
      <el-table
        v-loading="loading"
        :data="tableData"
        border
        stripe
        style="width: 100%"
        size="default"
      >
        <el-table-column prop="username" label="用户名" min-width="150" align="center" />
        <el-table-column label="密码" width="120" align="center">
          <template v-slot>
            *********
          </template>
        </el-table-column>
        <el-table-column label="角色" width="120" align="center">
          <template v-slot="{ row }">
            <el-tag :type="row.role === 1 ? 'warning' : 'info'" size="small">
              {{ row.role === 1 ? '管理员' : '普通用户' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="权限" min-width="200" align="center">
          <template v-slot="{ row }">
            <span v-if="row.role === 1" style="color: #909399">全部</span>
            <span v-else-if="row.permission" style="font-size: 12px">
              <el-tag
                v-for="id in row.permission.split(',')"
                :key="id"
                size="small"
                style="margin-right: 4px; margin-bottom: 2px"
              >
                {{ getGroupTitle(parseInt(id)) }}
              </el-tag>
            </span>
            <span v-else style="color: #c0c4cc">-</span>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="140" align="center">
          <template v-slot="{ row }">
            <div class="table-actions">
              <el-button type="text" size="small" @click="handleEdit(row)">编辑</el-button>
              <el-popconfirm title="确定删除该用户？" @confirm="handleDelete(row)">
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
      :title="isEdit ? '编辑用户' : '新增用户'"
      width="560px"
      :close-on-click-modal="false"
      destroy-on-close
      @closed="handleFormClosed"
    >
      <el-form ref="formRef" :model="form" :rules="rules" label-width="100px">
        <el-form-item label="用户名" prop="username">
          <el-input
            v-model="form.username"
            placeholder="请输入用户名"
            :readonly="isEdit"
            maxlength="20"
          />
          <div style="color: #909399; font-size: 12px; margin-top: 2px">
            以小写字母开头，仅包含小写字母和数字
          </div>
        </el-form-item>
        <el-form-item label="密码" :prop="isEdit ? '' : 'password'">
          <el-input
            v-model="form.password"
            :placeholder="isEdit ? '不填则不变更密码' : '请输入密码'"
            maxlength="20"
            show-password
          />
        </el-form-item>
        <el-form-item label="角色" prop="role">
          <el-radio-group v-model="form.role" @change="handleRoleChange">
            <el-radio :label="0">普通用户</el-radio>
            <el-radio :label="1">管理员</el-radio>
          </el-radio-group>
        </el-form-item>
        <!-- 权限：管理员不需要，普通用户选择执行器 -->
        <el-form-item v-if="form.role !== 1" label="权限">
          <el-checkbox-group v-model="form.permission">
            <el-checkbox
              v-for="group in jobGroupList"
              :key="group.id"
              :label="String(group.id)"
            >
              {{ group.title }}({{ group.appname }})
            </el-checkbox>
          </el-checkbox-group>
        </el-form-item>
      </el-form>
      <template v-slot:footer>
        <el-button @click="formDialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="submitting" @click="handleSubmit">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script>
import { pageList, addUser, updateUser, removeUser } from '@/api/user'
import { findAll } from '@/api/jobgroup'

export default {
  name: 'UserIndex',

  data: function () {
    var validateUsername = function (rule, value, callback) {
      if (!value) {
        callback(new Error('请输入用户名'))
        return
      }
      if (value.length < 4 || value.length > 20) {
        callback(new Error('用户名长度限制为4~20'))
        return
      }
      if (!/^[a-z][a-z0-9]*$/.test(value)) {
        callback(new Error('以小写字母开头，仅包含小写字母和数字'))
        return
      }
      callback()
    }

    var validatePassword = function (rule, value, callback) {
      // 编辑模式下密码可为空
      if (this.isEdit && !value) {
        callback()
        return
      }
      if (!this.isEdit && !value) {
        callback(new Error('请输入密码'))
        return
      }
      if (value && (value.length < 4 || value.length > 20)) {
        callback(new Error('密码长度限制为4~20'))
        return
      }
      callback()
    }

    return {
      loading: false,
      tableData: [],
      total: 0,
      jobGroupList: [],

      queryParams: {
        start: 1,
        length: 10,
        role: -1,
        username: ''
      },

      formDialogVisible: false,
      isEdit: false,
      submitting: false,
      editId: null,
      form: {
        username: '',
        password: '',
        role: 0,
        permission: []
      },
      rules: {
        username: [
          { required: true, validator: validateUsername, trigger: 'blur' }
        ],
        password: [
          { validator: validatePassword, trigger: 'blur' }
        ],
        role: [
          { required: true, message: '请选择角色', trigger: 'change' }
        ]
      }
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
        var data = res.data.data || res.data.content || []
        self.jobGroupList = data
      }).catch(function (e) {
        console.error('获取执行器列表失败', e)
      })
    },

    getGroupTitle: function (groupId) {
      var group = this.jobGroupList.find(function (g) { return g.id === groupId })
      return group ? group.title : groupId
    },

    fetchData: function () {
      var self = this
      this.loading = true

      var startOffset = (this.queryParams.start - 1) * this.queryParams.length
      if (startOffset < 0) startOffset = 0

      var params = {
        start: startOffset,
        length: this.queryParams.length,
        role: this.queryParams.role != null ? this.queryParams.role : -1,
        username: this.queryParams.username || ''
      }

      pageList(params).then(function (res) {
        self.loading = false
        // API 返回 { recordsTotal, recordsFiltered, data }
        self.tableData = res.data.data || []
        self.total = res.data.recordsTotal || 0
      }).catch(function (e) {
        self.loading = false
        self.$message.error('获取用户列表失败：' + (e.message || '网络异常'))
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

    handleRoleChange: function () {
      this.form.permission = []
    },

    handleAdd: function () {
      this.isEdit = false
      this.editId = null
      this.form = {
        username: '',
        password: '',
        role: 0,
        permission: []
      }
      this.formDialogVisible = true
    },

    handleEdit: function (row) {
      this.isEdit = true
      this.editId = row.id
      this.form.username = row.username || ''
      this.form.password = ''
      this.form.role = row.role != null ? row.role : 0
      this.form.permission = row.permission ? row.permission.split(',').filter(function (v) { return v !== '' }) : []
      this.formDialogVisible = true
    },

    handleSubmit: function () {
      var self = this
      this.$refs.formRef.validate(function (valid) {
        if (!valid) return

        self.submitting = true
        var data = {
          username: self.form.username,
          password: self.form.password,
          role: self.form.role,
          permission: self.form.role === 1 ? '' : self.form.permission.join(',')
        }

        var requestFn = self.isEdit ? updateUser : addUser
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
      removeUser(row.id).then(function (res) {
        if (res.data.code === 200) {
          self.$message.success('删除成功')
          self.fetchData()
        } else {
          self.$message.error(res.data.msg || '删除失败')
        }
      }).catch(function (e) {
        self.$message.error('删除失败：' + (e.message || '网络异常'))
      })
    }
  }
}
</script>
