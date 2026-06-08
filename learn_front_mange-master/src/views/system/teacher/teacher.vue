<template>
  <div class="user">
    <div class="search-table">
      <div class="search">
        <!-- 调整 gutter 为 5 减小间距 -->
        <el-row :gutter="5" style="padding:10px">
          <!-- 教师名称 - 宽度调小 -->
          <el-col :xs="24" :sm="12" :md="6" :lg="5" :xl="5">
            <span class="search-title">教师名称:</span>
            <el-input
                style="margin-top:10px; width: 160px"
                size="mini"
                placeholder="请输入教师名称"
                v-model="search.userName">
            </el-input>
          </el-col>
          <!-- 学校 - 宽度调小 -->
          <el-col :xs="24" :sm="12" :md="6" :lg="5" :xl="5">
            <span class="search-title">学校:</span>
            <el-input
                style="margin-top:10px; width: 160px"
                size="mini"
                placeholder="请输入学校名称"
                v-model="search.school">
            </el-input>
          </el-col>
          <!-- 状态搜索 -->
          <el-col :xs="24" :sm="12" :md="6" :lg="5" :xl="5">
            <span class="search-title">状态:</span>
            <el-select clearable style="margin-top:10px; width: 160px" size="mini" v-model="search.status" placeholder="请选择状态">
              <el-option label="正常" value="0"></el-option>
              <el-option label="禁用" value="1"></el-option>
            </el-select>
          </el-col>
          <!-- 按钮 - 宽度固定 -->
          <el-col :xs="24" :sm="12" :md="6" :lg="4" :xl="4">
            <el-button style="margin-top:10px" size="mini" icon="el-icon-search" type="primary" @click="searchPage">查询</el-button>
            <el-button style="margin-top:10px" size="mini" icon="el-icon-refresh" @click="refresh">重置</el-button>
          </el-col>
        </el-row>
      </div>
      <div class="table">
        <el-row style="padding-top:10px;margin-left:10px">
          <el-button type="primary" size="mini" icon="el-icon-plus" plain @click="addUser()">新增</el-button>
          <el-button type="success" :disabled="update.length != 1 ?true:false" size="mini" icon="el-icon-edit" plain @click="updateUserBtn">修改</el-button>
        </el-row>
        <el-table
            :data="tableData"
            :header-cell-style="{
              'color': '#4A2B90',
              'background-color': '#ECE9F4',
            }"
            :row-style="{
              'color': '#888897',
              'font-size': '15px',
              'font-family':'黑体',
              'white-space': 'nowrap'
            }"
            @selection-change="handleSelectionChange"
            stripe
            style="width: 100%">
          <el-table-column
              type="selection"
              width="55">
          </el-table-column>
          <el-table-column
              label="教师名称"
          >
            <template slot-scope="scope">
              <div style="display:flex;align-items: center">
                <img style="width:35px;height:35px;border-radius:50%" :src="$store.state.configure.HOST + scope.row.avatar">
                <div style="margin-left:10px">{{scope.row.userName}}</div>
              </div>
            </template>
          </el-table-column>
          <el-table-column
              prop="loginAccount"
              label="登陆账号"
          >
          </el-table-column>
          <el-table-column
              prop="country"
              label="国家"
          >
          </el-table-column>
          <el-table-column
              prop="school"
              label="学校"
          >
          </el-table-column>
          <el-table-column
              prop="agree"
              label="职称"
          >
          </el-table-column>
          <el-table-column
              prop="tel"
              label="手机号"
          >
          </el-table-column>
          <el-table-column
              prop="email"
              label="邮箱"
          >
          </el-table-column>
          <!-- 状态列 -->
          <el-table-column
              prop="status"
              label="状态"
              width="80"
          >
            <template slot-scope="scope">
              <el-tag v-if="scope.row.status == 0" type="success" size="small">正常</el-tag>
              <el-tag v-if="scope.row.status == 1" type="danger" size="small">禁用</el-tag>
<!--              <el-tag v-if="scope.row.status == null || scope.row.status == ''" type="success" size="small">正常</el-tag>-->
            </template>
          </el-table-column>
          <el-table-column
              label="操作"
          >
            <template slot-scope="scope">
              <el-dropdown trigger="hover" @command="handleCommand">
                  <span class="el-dropdown-link">
                    操作<i class="el-icon-arrow-down el-icon--right"></i>
                  </span>
                <el-dropdown-menu slot="dropdown">
                  <el-dropdown-item icon="el-icon-edit" :command="scope.row.id + '#edit'">修改</el-dropdown-item>
                  <!-- 根据状态显示启用/禁用按钮 -->
                  <el-dropdown-item v-if="scope.row.status == 0 && scope.row.loginAccount != 'admin'" icon="el-icon-circle-close" :command="scope.row.id + '#disable#' + scope.row.userName">禁用</el-dropdown-item>
                  <el-dropdown-item v-if="scope.row.status == 1 && scope.row.loginAccount != 'admin'" icon="el-icon-check" :command="scope.row.id + '#enable#' + scope.row.userName">启用</el-dropdown-item>
                  <el-dropdown-item icon="el-icon-check" v-if="scope.row.loginAccount != 'admin'" :command="scope.row.id + '#pass#' + scope.row.userName">重置密码</el-dropdown-item>
                  <el-dropdown-item icon="el-icon-remove" v-if="scope.row.loginAccount != 'admin'" :command="scope.row.id + '#remove'">删除</el-dropdown-item>
                </el-dropdown-menu>
              </el-dropdown>
            </template>
          </el-table-column>
        </el-table>
        <el-pagination
            background
            layout="total, prev, pager, next"
            :page-sizes="[20, 30, 50, 100]"
            :page-size="search.pageSize"
            :current-page="search.pageNumber"
            @size-change="handleSizeChange"
            @current-change="handleCurrentChange"
            :total="total">
        </el-pagination>
      </div>
    </div>
    <add-user @addUserFalse="addUserFalse" :addUserVisible = "addUserVisible"></add-user>
    <update-user @updateUserFalse="updateUserFalse" :updateId = "updateId" :updateUserVisible = "updateUserVisible"></update-user>
    <el-dialog
        title="重置密码"
        :visible.sync="passwordDialogVisible"
        width="30%"
        :before-close="handlePasswordClose">
      <span>请输入{{userName}}的新密码：</span>
      <el-input style="margin-top:10px" show-password v-model="newPassword" size="mini" autocomplete="off"></el-input>
      <span slot="footer" class="dialog-footer">
          <el-button size="mini" @click="handlePasswordClose">取 消</el-button>
          <el-button size="mini" type="primary" @click="passwordSubmit">确 定</el-button>
        </span>
    </el-dialog>
    <!-- 状态变更确认对话框 -->
    <el-dialog
        :title="statusDialogTitle"
        :visible.sync="statusDialogVisible"
        width="30%"
        :before-close="handleStatusClose">
      <span>确定要将用户【{{userName}}】{{statusDialogTitle}}吗？</span>
      <span slot="footer" class="dialog-footer">
          <el-button size="mini" @click="handleStatusClose">取 消</el-button>
          <el-button size="mini" type="primary" @click="statusSubmit">确 定</el-button>
        </span>
    </el-dialog>
  </div>
</template>

<script>
import {getUserPage, removeUser, resetPassword, updateUserStatus, getApeSchoolList, getApeMajorList} from '../../../api/api'
import addUser from "../../../components/system/teacher/addTeacher"
import updateUser from "../../../components/system/teacher/updateTeacher"
export default {
  data() {
    return{
      loading: true,
      update: [],
      remove: [],
      updateId: "",
      addUserVisible: false,
      updateUserVisible: false,
      passwordDialogVisible: false,
      statusDialogVisible: false,
      statusDialogTitle: "",
      filterText: "",
      search: {
        userName: "",
        tel: "",
        status: "",
        deptId: "",
        userType: 1,
        school: "",
        major: "",
        pageNumber: 1,
        pageSize:10
      },
      userName: "",
      userId: "",
      newPassword: "",
      total: 0,
      tableData: [],
      post: [],
      role: [],
      school: [],
      major: []
    }
  },
  components: {
    addUser,
    updateUser
  },
  methods: {
    getApeSchoolList() {
      getApeSchoolList().then(res=> {
        if(res.code == 1000) {
          this.school = res.data
        } else {
          this.$notify.error({
            title: '错误',
            message: res.message
          });
        }
      })
    },
    getApeMajorList() {
      getApeMajorList().then(res => {
        if(res.code == 1000) {
          this.major = res.data
        } else {
          this.$notify.error({
            title: '错误',
            message: res.message
          });
        }
      })
    },
    searchPage() {
      this.search.pageNumber = 1
      this.query()
    },
    query() {
      getUserPage(this.search).then(res => {
        if(res.code == 1000) {
          this.tableData = res.data.records
          this.total = res.data.total
          this.loading = false
        } else {
          this.$notify.error({
            title: '错误',
            message: res.message
          });
        }
      })
    },
    refresh() {
      this.search.userName = ""
      this.search.tel = ""
      this.search.status = ""
      this.search.deptId = ""
      this.search.school = ""
      this.search.major = ""
      this.query()
    },
    handleCurrentChange(val) {
      this.search.pageNumber = val
      this.query()
    },
    handleSizeChange(val) {
      this.search.pageSize = val
      this.query()
    },
    handleSelectionChange(val) {
      this.update = []
      this.remove = []
      for (let i = 0;i < val.length;i++) {
        var item = val[i]
        this.update.push(item.id)
        this.remove.push(item.id)
      }
    },
    addUser() {
      this.addUserVisible = true
    },
    addUserFalse() {
      this.addUserVisible = false
      this.query()
    },
    updateUser(id) {
      this.updateId = id
      this.updateUserVisible = true
    },
    updateUserFalse() {
      this.updateUserVisible = false
      this.updateId = ""
      this.query()
    },
    updateUserBtn() {
      this.updateUser(this.update[0])
    },
    // 启用/禁用用户
    openStatusDialog(id, userName, action) {
      this.userId = id
      this.userName = userName
      this.statusDialogTitle = action === 'enable' ? '启用' : '禁用'
      this.statusDialogVisible = true
    },
    handleStatusClose() {
      this.userId = ""
      this.userName = ""
      this.statusDialogVisible = false
    },
    statusSubmit() {
      const status = this.statusDialogTitle === '启用' ? 0 : 1
      updateUserStatus({id: this.userId, status: status}).then(res => {
        if(res.code == 1000) {
          this.$notify.success({
            title: '成功',
            message: `${this.userName}${this.statusDialogTitle}成功`
          });
          this.handleStatusClose()
          this.query()
        } else {
          this.$notify.error({
            title: '错误',
            message: res.message
          });
        }
      })
    },
    handleCommand(command) {
      var data = command.split("#")
      if(data[1] == 'edit') {
        this.updateUser(data[0])
      } else if(data[1] == 'pass') {
        this.userName = data[2]
        this.openPassword(data[0])
      } else if(data[1] == 'disable') {
        this.openStatusDialog(data[0], data[2], 'disable')
      } else if(data[1] == 'enable') {
        this.openStatusDialog(data[0], data[2], 'enable')
      } else {
        this.$confirm('确定删除选中的数据?', '提示', {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'warning'
        }).then(() => {
          this.deleteDate(data[0])
        }).catch(() => {

        });
      }
    },
    deleteDateBtn() {
      this.$confirm('确定删除选中的'+ this.remove.length +'条数据?', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        this.deleteDate(this.remove.join(","))
      }).catch(() => {

      });
    },
    deleteDate(ids) {
      removeUser({ids:ids}).then(res => {
        if(res.code == 1000) {
          this.$message({
            type: 'success',
            message: '删除成功!'
          });
          this.pageNumber = 1
          this.query()
        } else {
          this.$notify.error({
            title: '错误',
            message: res.message
          });
        }
      })
    },
    openPassword(id) {
      this.userId = id
      this.passwordDialogVisible = true
    },
    handlePasswordClose() {
      this.userName = ""
      this.userId = ""
      this.newPassword = ""
      this.passwordDialogVisible = false
    },
    passwordSubmit() {
      if(!this.newPassword.trim()) {
        this.$message({
          message: '请输入新密码',
          type: 'warning'
        });
        return;
      }
      resetPassword({id: this.userId, newPassword: this.newPassword}).then(res => {
        if(res.code == 1000) {
          this.$notify.success({
            title: '成功',
            message: "重置成功"
          });
          this.handlePasswordClose()
        } else {
          this.$notify.error({
            title: '错误',
            message: res.message
          });
        }
      })
    }
  },
  mounted() {
    this.getApeSchoolList()
    this.getApeMajorList()
    this.query()
  },
  watch: {

  },
}
</script>

<style scoped>
.user {
  width: 100%;
  display: flex;
}
.tree {
  width: 20%;
  background: #ffffff;
  height: 83vh;
  overflow: scroll;
  border-radius: 7px;
  box-shadow: 0 2px 12px 0 rgba(0,0,0,.1)
}
.tree::-webkit-scrollbar {
  width: 0px;
  height: 0px;
}
.search-table {
  width: 100%;
}
.search {
  background: #ffffff;
  border-radius: 7px;
  box-shadow: 0 2px 12px 0 rgba(0,0,0,.1)
}
.table {
  background: #ffffff;
  border-radius: 7px;
  box-shadow: 0 2px 12px 0 rgba(0,0,0,.1);
  margin-top: 10px
}
.el-col {
  display: flex;
  flex-direction: row;
  align-items: center;
}
.search-title {
  font-family: '黑体';
  float: right;
  white-space: nowrap;
  font-size: 14px;
  margin-top:10px;
  width: 63px;
  text-align: right;
}
.el-table {
  padding: 10px;
}
.el-dialog__header {
  border-bottom: 1px solid #F4F8F9 !important;
}
.el-dialog {
  border-radius: 10px!important;
}
</style>