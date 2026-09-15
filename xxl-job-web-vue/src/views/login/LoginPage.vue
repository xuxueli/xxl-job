<template>
  <div class="login-container">
    <div class="login-card">
      <h2 class="login-title">XXL-JOB 调度中心</h2>
      <el-form ref="loginForm" :model="form" :rules="rules" label-position="top" @keyup.enter.native="handleLogin">
        <el-form-item label="用户名" prop="userName">
          <el-input v-model="form.userName" placeholder="请输入用户名" prefix-icon="el-icon-user" />
        </el-form-item>
        <el-form-item label="密码" prop="password">
          <el-input v-model="form.password" type="password" placeholder="请输入密码" prefix-icon="el-icon-lock" show-password />
        </el-form-item>
        <el-form-item>
          <el-checkbox v-model="form.ifRemember">记住我</el-checkbox>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" :loading="loading" style="width: 100%" @click="handleLogin">
            登 录
          </el-button>
        </el-form-item>
      </el-form>
    </div>
  </div>
</template>

<script>
import { login } from '@/api/login'

export default {
  name: 'LoginPage',

  data: function () {
    return {
      loading: false,
      form: {
        userName: '',
        password: '',
        ifRemember: false
      },
      rules: {
        userName: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
        password: [{ required: true, message: '请输入密码', trigger: 'blur' }]
      }
    }
  },

  methods: {
    handleLogin: function () {
      var self = this
      this.$refs.loginForm.validate(function (valid) {
        if (!valid) return

        self.loading = true
        login(self.form.userName, self.form.password, self.form.ifRemember).then(function (res) {
          self.loading = false
          if (res.data.code === 200) {
            self.$message.success('登录成功')
            sessionStorage.setItem('xxl-job-login', 'true')
            var redirect = self.$route.query.redirect || '/'
            self.$router.replace(redirect)
          } else {
            self.$message.error(res.data.msg || '登录失败')
          }
        }).catch(function (e) {
          self.loading = false
          self.$message.error('登录失败：' + (e.message || '网络异常'))
        })
      })
    }
  }
}
</script>

<style scoped>
.login-container {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 100vh;
  padding: 24px;
  box-sizing: border-box;
  background: url('/white-bg.svg') center center / cover no-repeat #f4f7fb;
}

.login-card {
  width: 100%;
  max-width: 400px;
  /* width:100% 与 40px 内边距共存时必须用 border-box，
     否则 content-box 下卡片会比容器宽出 80px 而溢出 */
  box-sizing: border-box;
  padding: 40px;
  background: #fff;
  border-radius: 8px;
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.2);
}

.login-title {
  text-align: center;
  margin-bottom: 30px;
  font-size: 22px;
  color: #303133;
  font-weight: 600;
}
</style>
