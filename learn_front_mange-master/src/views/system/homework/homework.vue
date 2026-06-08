<template>
  <div class="_homework">
    <div class="search-table">
      <div class="search">
        <el-row :gutter="10" style="padding:10px">
          <el-col :xs="24" :sm="12" :md="8" :lg="8" :xl="8">
            <span class="search-title">题目:</span>
            <el-input
                style="margin-top:10px"
                size="mini"
                placeholder="请输入题目"
                v-model="search.title">
            </el-input>
          </el-col>
          <el-col :xs="24" :sm="12" :md="8" :lg="8" :xl="8">
            <span class="search-title">类型:</span>
            <el-select clearable style="margin-top:10px" size="mini" v-model="search.type" placeholder="请选择">
              <el-option label="单选" value="0"></el-option>
              <el-option label="多选" value="1"></el-option>
              <el-option label="填空" value="2"></el-option>
              <el-option label="判断" value="3"></el-option>
            </el-select>
          </el-col>
          <el-col :xs="24" :sm="12" :md="8" :lg="8" :xl="8">
            <el-button style="margin-top:10px" size="mini" icon="el-icon-search" type="primary" @click="searchPage">查询</el-button>
            <el-button style="margin-top:10px" size="mini" icon="el-icon-refresh" @click="refresh">重置</el-button>
          </el-col>
        </el-row>
      </div>
      <div class="table">
        <el-row style="padding-top:10px;margin-left:10px">
          <el-button type="primary" size="mini" icon="el-icon-plus" plain @click="add">新增</el-button>
          <el-button type="success" :disabled="update.length != 1 ?true:false" size="mini" icon="el-icon-edit" plain @click="updateDataBtn">修改</el-button>
          <el-button type="danger" :disabled="update.length <= 0 ?true:false" size="mini" icon="el-icon-delete" plain @click="deleteDataBtn">删除</el-button>
        </el-row>
        <el-table
            v-loading="loading"
            :data="displayTableData"
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
          <el-table-column type="selection" width="50"></el-table-column>
          <!-- 章节名称列 - 显示接收的章节名称 -->
          <el-table-column prop="displayChapterName" label="章节名称" width="180">
            <template slot-scope="scope">
              {{ scope.row.displayChapterName || chapterName || '-' }}
            </template>
          </el-table-column>
          <el-table-column prop="title" label="题目" min-width="150" show-overflow-tooltip></el-table-column>
          <el-table-column prop="sort" label="序号" width="80"></el-table-column>
          <el-table-column prop="answer" label="答案" width="120" show-overflow-tooltip></el-table-column>
          <el-table-column prop="type" label="类型" width="80">
            <template slot-scope="scope">
              <el-tag v-if="scope.row.type == 0">单选</el-tag>
              <el-tag v-if="scope.row.type == 1" type="warning">多选</el-tag>
              <el-tag v-if="scope.row.type == 2" type="primary">填空</el-tag>
              <el-tag v-if="scope.row.type == 3" type="success">判断</el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="createTime" label="创建时间" width="170"></el-table-column>
          <el-table-column label="操作">
            <template slot-scope="scope">
              <el-button size="mini" type="success" @click="updateData(scope.row.id)">修改</el-button>
              <el-popconfirm
                  style="margin-left:5px"
                  confirm-button-text='确认'
                  cancel-button-text='取消'
                  icon="el-icon-info"
                  icon-color="red"
                  title="确认删除选中的数据？"
                  @confirm="deleteDate(scope.row.id)">
                <el-button size="mini" slot="reference" type="danger">删除</el-button>
              </el-popconfirm>
            </template>
          </el-table-column>
        </el-table>
        <el-pagination
            background
            layout="total, sizes, prev, pager, next, jumper"
            :page-sizes="[10, 20, 50, 100]"
            :page-size="search.pageSize"
            :current-page="search.pageNumber"
            @size-change="handleSizeChange"
            @current-change="handleCurrentChange"
            :total="total">
        </el-pagination>
      </div>
    </div>
    <add @addFalse="addFalse" :chapterId="chapterId" :chapterName="chapterName" :addVisible="addVisible"></add>
    <update @updateFalse="updateFalse" :chapterId="chapterId" :chapterName="chapterName" :updateId="updateId" :updateVisible="updateVisible"></update>
  </div>
</template>

<script>
import {getApeHomeworkPage, removeApeHomework} from '../../../api/api'
import add from '../../../components/system/homework/addHomework'
import update from '../../../components/system/homework/updateHomework'
export default {
  data() {
    return{
      loading: true,
      update: [],
      remove: [],
      updateId: "",
      addVisible: false,
      updateVisible: false,
      chapterId: "",
      chapterName: "",  // 接收章节名称
      search: {
        chapterId: "",
        title: "",
        type: "",
        pageNumber: 1,
        pageSize: 10
      },
      total: 0,
      tableData: []
    }
  },
  components: {
    add,
    update
  },
  computed: {
    // 处理表格数据，添加显示用的章节名称
    displayTableData() {
      return this.tableData.map(item => ({
        ...item,
        displayChapterName: this.chapterName || item.chapterName
      }))
    }
  },
  methods: {
    searchPage() {
      this.search.pageNumber = 1
      this.query()
    },
    query() {
      const params = {
        chapterId: this.chapterId,
        title: this.search.title,
        type: this.search.type,
        pageNumber: this.search.pageNumber,
        pageSize: this.search.pageSize
      }

      console.log('查询参数:', params)

      getApeHomeworkPage(params).then(res => {
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
      this.search.title = ""
      this.search.type = ""
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
      for (let i = 0; i < val.length; i++) {
        var item = val[i]
        this.update.push(item.id)
        this.remove.push(item.id)
      }
    },
    add() {
      this.addVisible = true
    },
    addFalse() {
      this.addVisible = false
      this.query()
    },
    updateFalse() {
      this.updateId = ''
      this.updateVisible = false
      this.query()
    },
    updateData(id) {
      this.updateId = id
      this.updateVisible = true
    },
    updateDataBtn() {
      this.updateData(this.update[0])
    },
    deleteDataBtn() {
      this.$confirm('确定删除选中的'+ this.remove.length +'条数据?', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        this.deleteDate(this.remove.join(","))
      }).catch(() => {});
    },
    deleteDate(ids) {
      removeApeHomework({ids:ids}).then(res => {
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
  },
  mounted() {
    // 从路由获取章节ID和章节名称
    const chapterId = this.$route.query.chapterId || this.$route.query.id
    const chapterName = this.$route.query.chapterName || ''

    if (!chapterId) {
      this.$message.error('参数错误，无法获取章节信息')
      return
    }

    this.chapterId = chapterId
    this.chapterName = chapterName ? decodeURIComponent(chapterName) : ''
    this.search.chapterId = chapterId

    console.log('章节作业页面 - 章节ID:', this.chapterId)
    console.log('章节作业页面 - 章节名称:', this.chapterName)

    this.query()
  }
}
</script>
<style scoped>
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

/* 当前章节信息样式 */
.current-chapter-info {
  background: linear-gradient(135deg, #e8f5e9 0%, #c8e6c9 100%);
  padding: 12px 20px;
  border-radius: 8px;
  margin-bottom: 20px;
  font-size: 16px;
  color: #2e7d32;
  display: flex;
  align-items: center;
  gap: 8px;
}
.current-chapter-info i {
  font-size: 18px;
}
</style>