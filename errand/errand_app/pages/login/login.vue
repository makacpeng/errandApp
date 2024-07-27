<template>
	<view style="padding: 20rpx;">
		<view class="box" style="padding: 60rpx 30rpx;">
			<view style="font-size: 40rpx;">欢迎登录</view>
			<view style="margin: 60rpx 0;">
				<uni-forms ref="formRef" :modelValue="form" :rules="rules" validateTrigger="blur">
					<uni-forms-item name="username" required>
						<uni-easyinput prefixIcon="person" v-model="form.username" placeholder="请输入账号" />
					</uni-forms-item>
					<uni-forms-item name="password" required>
						<uni-easyinput type="password" prefixIcon="locked" v-model="form.password" placeholder="请输入密码" />
					</uni-forms-item>
					<uni-forms-item>
						<button @click="login" type="primary" style="width: 100%; height: 70rpx; line-height: 70rpx; background-color: #006eff; border-color: #006eff;">登 录</button>
					</uni-forms-item>
				</uni-forms>
			</view>
			
			<view style="text-align: right;">
				还没有账号？请 <navigator url="/pages/register/register" style="display: inline; margin-left: 5rpx; color: #006eff;">注册</navigator>
			</view>
			
		</view>
	</view>
</template>

<script>
	export default {
		data() {
			return {
				form: { role: 'USER' },
				rules: {
					// 对字段进行必填验证
					username: {
						rules:[
							// 校验不能为空
							{
								required: true,
								errorMessage: '请输入账号',
							}
						],
					},
					password: {
						rules:[
							// 校验不能为空
							{
								required: true,
								errorMessage: '请输入密码',
							}
						],
					},
				}
			}
		},
		methods: {
			login() {
				this.$refs.formRef.validate().then(res => {
					this.$request.post('/login', this.form).then(res => {
						if (res.code === '200') {  // 登录成功
							uni.setStorageSync('xm-user', res.data)
							uni.showToast({
								icon: 'success',
								title: '登录成功'
							})
							setTimeout(() => {
								uni.switchTab({
									url: '/pages/index/index'
								})
							}, 500)
						} else {
							uni.showToast({
								icon: 'none',
								title: res.msg
							})
						}
					})
				}).catch(err => {
					console.log('err', err);
				})
			}
		}
	}
</script>

<style>

</style>