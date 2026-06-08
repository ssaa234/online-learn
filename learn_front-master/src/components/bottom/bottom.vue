<template>
  <div class="bottom">
          <div class="bottom-one">
              <div style="width:50%">
                <div style="color:#FFC27A;font-size:25px;font-weight:bold">
                  <span style="color:#20B486; white-space: nowrap;">K12信息技术在线学习平台</span>
                </div>
                <div style="margin-top:10px;font-weight:bold">关于我们</div>
                <div style="font-weight:bold">Contact Us</div>
                <div style="color:#6D737A;margin-top:10px;min-width: 500px;">该网站是专为K12打造的信息技术编程在线学习平台，支持教师高效教学与资源分享，助力学生自主探索编程知识。</div>
              </div>
          </div>
          <div class="bottom-two">
              <div style="font-weight:bold"></div>
              <div style="font-weight:bold"></div>
              <div style="cursor: pointer;color:#6D737A;margin-top:30px" @click="toPage(0)"></div>
              <div style="cursor: pointer;color:#6D737A;margin-top:15px" @click="toPage(1)"></div>
              <div style="cursor: pointer;color:#6D737A;margin-top:15px" @click="toPage(2)"></div>
              <div style="cursor: pointer;color:#6D737A;margin-top:15px" @click="toPage(3)"></div>
              <div style="cursor: pointer;color:#6D737A;margin-top:15px" @click="toPage(4)"></div>
          </div>
          <div class="bottom-three">
              <div style="font-weight:bold"></div>
              <div style="font-weight:bold"></div>
              <div style="cursor: pointer;color:#6D737A;margin-top:30px" @click="toPage(5)"></div>
              <div style="cursor: pointer;color:#6D737A;margin-top:15px" @click="toPage(6)"></div>
              <div style="cursor: pointer;color:#6D737A;margin-top:15px" @click="toPage(7)"></div>
              <div style="cursor: pointer;color:#6D737A;margin-top:15px" @click="toPage(8)"></div>
              <div style="cursor: pointer;color:#6D737A;margin-top:15px" @click="toPage(9)"></div>
          </div>
          <div class="bottom-four">
              <div style="width:50%">
                <div style="font-size:15px;font-weight:bold">
                  联系方式
                </div>
                <div style="font-size:15px;font-weight:bold">
                  contact information
                </div>
                <div style="color:#6D737A;margin-top:10px">邮箱: 2634115448@qq.com</div>
                <div style="color:#6D737A;margin-top:10px">QQ: 2634115448</div>
                <div style="color:#6D737A;margin-top:10px">微信: 13927199224</div>
                <div style="color:#6D737A;margin-top:10px">电话: 13927199224</div>
              </div>
          </div>
      </div>
</template>

<script>
  export default {
    data() {
      return{
        
      }
    },
    methods: {
        toPage(index) {
          if (index == 0) {
            this.$router.push("/")
          } else if (index == 1) {
            this.$router.push("/task")
          } else if (index == 2) {
            this.$router.push("/teacher")
          } else if (index == 3) {
            this.$router.push("/test")
          } else if (index == 4) {
            this.$router.push("/article")
          } else if (index == 5) {
            this.$router.push("/notice")
          } else if (index == 6) {
            this.$router.push("/message")
          } else if (index == 7) {
            this.$router.push("/about")
          } else if (index == 8) {
            this.$router.push("/center")
          } else if (index == 9) {
            this.$router.push("/myWrongAssign")
          }
        }
    },
    created() {
     
    },
    mounted() {
      
    }
 }
</script>

<style  scoped>
  .bottom {
    width: 100%;
    height: 45%;
    background-image: url('../../assets/image/index/index_back.png');
    display: flex;
}
.bottom-one,.bottom-four {
    display: flex;
    flex-direction: column;
    justify-content: center;
    align-items: center;
    width: 30%;
    height: 100%;
    font-family: '黑体';
    font-size: 14px;
}
.bottom-two,.bottom-three {
    display: flex;
    flex-direction: column;
    justify-content: center;
    align-items: center;
    width: 20%;
    height: 100%;
    font-family: '黑体';
    font-size: 14px;
}
</style>