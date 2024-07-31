<template>
	<view style="padding: 20rpx;">
		<view class="money">
			余额 <text>{{ account }}</text>
		</view>
		
		<view style="margin-top: 20rpx;">
			<button type="primary" style="background-color:  #fe73a0;" @click="handleCharge">充 值</button>
		</view>
	</view>
</template>

<script>
	export default {
		data() {
			return {
				account: 0,
				user: uni.getStorageSync('xm-user')
			}
		},
		onShow() {
			this.load()
		},
		methods: {
			handleCharge() {
				uni.navigateTo({
					url: '/pages/charge/charge'
				})
			},
			load() {
				this.$request.get('/user/selectById/' + this.user.id).then(res => {
					this.account = res.data.account || 0
				})
			}
		}
	}
</script>

<style lang="scss">
.money{
	background: 
		radial-gradient(90% 300px at left top, #dde5ff, transparent),
		radial-gradient(60% 300px at right top, #f9dced, transparent); 
	padding: 80rpx; 
	font-size: 40rpx; 
	text-align: center; 
	border-radius: 10rpx; 
	color: #fe73a0;
	text{
		
		margin-left: 10rpx;
	}
}
</style>