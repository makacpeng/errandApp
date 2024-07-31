<template>
	<view  class="layout">
		<view style="margin-bottom: 20rpx;">
			<!-- 首页轮播图 -->
			<swiper class="banner" circular autoplay :interval="5000" :duration="500" indicator-dots style="height: 320rpx;" 
			  indicator-color="rgba(255, 255, 255, 0.6)" indicator-active-color="#006eff">
			  <swiper-item v-for="item in imgs" :key="item" >
			    <image :src="item" alt="" style="width: 100%; height: 350rpx;" />
			  </swiper-item>
			</swiper>
		</view>
		
		<!-- 消息通知 -->
		<view class="notice" >
			<uni-notice-bar v-if="content" show-icon single :text="content" />
		</view>
		
		<!-- 功能栏 -->
		<view class="func">
			<view class="big" @click="goPreOrder('代拿快递')">
				<image src="../../static/imgs/快递.png" ></image>
				<view>代拿快递</view>
			</view>
			<view class="small1" style="border-bottom-left-radius: 0;" @click="goPreOrder('代取餐品')">
				<image src="../../static/imgs/取餐.png" ></image>
				<view>代取餐品</view>
			</view>
			<view class="small2" style="border-top-left-radius: 0;" @click="goPreOrder('代买零食')">
				<image src="../../static/imgs/零食.png"></image>
				<view>代买零食</view>
			</view>
		</view>
		
		<!-- 鲜花配送和更多 -->
		<view class="box1" @click="goPreOrder('代送鲜花')">
			<view class="flower">
				<image src="../../static/imgs/花.png"></image>
				<view style="flex: 1;">代送鲜花</view>
			</view>
			<view class="more">
				<image src="../../static/imgs/花.png"></image>
				<view style="flex: 1;">敬请期待</view>
			</view>
		</view>
		
		
		<view class="box2" style="color: #006eff; font-weight: bold; margin-bottom: 10rpx;">跑腿订单</view>
		<view>
			<view v-for="item in orderList" :key="item.id" class="box2" style="margin-bottom: 10rpx;" @click="goDetail(item.id)">
				<view style="display: flex; align-items: center; margin-bottom: 20rpx;">
					<view style="flex: 1;">
						<uni-tag text="餐品" size="small" type="success" v-if="item.type === '代取餐品'"></uni-tag>
						<uni-tag text="快递" size="small" type="primary" v-if="item.type === '代拿快递'"></uni-tag> 
						<uni-tag text="零食" size="small" type="warning" v-if="item.type === '代买零食'"></uni-tag> 
						<uni-tag text="鲜花" size="small" type="error" v-if="item.type === '代送鲜花'"></uni-tag> 
						<text style="margin-left: 10rpx;">{{ item.name }}</text>
					</view>
					<view style="flex: 1; text-align: right;">
						<text style="color: #888;">跑腿费</text>
						<text style="color: red; font-size: 34rpx;">￥{{ item.price }}</text>
					</view>
				</view>
				
				<view style="display: flex; align-items: center;">
					<view style="flex: 1;">
						<text style="margin-right: 10rpx;">已下单{{ item.range }}分钟</text>
						<text style="color: orange;">待接单</text>
					</view>
					<view style="flex: 1; text-align: right;">
						<uni-tag text="接单" type="primary" size="small" @click.native.stop="accept(item)"></uni-tag>
					</view>
				</view>
			</view>
		</view>
	</view>
</template>

<script>
	export default {
		data() {
			return {
				imgs: [
				  require('../../static/imgs/banner1.png'),
				  require('../../static/imgs/banner2.png'),
				],
				content: '',
				noticeList: [],
				inter: null,
				orderList: [],
				user: uni.getStorageSync('xm-user')
			}
		},
		onShow() {
			this.load()
			this.loadNotice()
		},
		onHide() {
			clearInterval(this.inter)
			this.inter = null
		},
		methods: {
			accept(orders) {
				if (!this.user.isRider) {  // 判断是否是骑手
					uni.showToast({
						icon: 'none',
						title: '只有认证骑手才可以接单'
					})
					return
				}
				this.$request.put('/orders/accept', orders).then(res => {
					if (res.code === '200') {
						uni.showToast({
							icon: 'success',
							title: '操作成功'
						})
						this.load()
					} else {
						uni.showToast({
							icon: 'none',
							title: res.msg
						})
					}
				})
			},
			goDetail(orderId) {
				uni.navigateTo({
					url: '/pages/detail/detail?orderId=' + orderId
				})
			},
			goPreOrder(type) {
				let orderStore = uni.getStorageSync('orderStore') || {}  // 先获取缓存的数据
				orderStore.type = type   // 设置订单的类型
				uni.setStorageSync('orderStore', orderStore)
				uni.navigateTo({
					url: '/pages/preOrder/preOrder'
				})
			},
			load() {
				this.$request.get('/orders/selectAll', {
					status: '待接单'
				}).then(res => {
					this.orderList = res.data || []
				})
			},
			loadNotice() {
				this.$request.get('/notice/selectAll').then(res => {
					this.noticeList = res.data || []
					
					let i = 0
					this.content = this.noticeList.length ? this.noticeList[i].content : ''
					
					// 切换展示公告内容
					if (this.noticeList.length > 1) {
						this.inter = setInterval(() => {
							i++
							if (i === this.noticeList.length) {
								i = 0
							}
							this.content = this.noticeList[i].content
						}, 5000)
					}
				
				})
			}
			
		}
	}
</script>

<style lang="scss">
	
	
	.layout{
		.banner{
			width: 700rpx;
			margin: 0 auto;
			margin-top: 30rpx;
			border-radius: 15rpx;
			overflow: hidden;
		}
		.notice{
			
			width: 700rpx;
			height: 90rpx;
			margin: 0 auto;
			margin-bottom: 20rpx;
			border-radius: 45rpx;	
			
			overflow: hidden;
			
		}
		.func{
			background-color: rgba(255,255,255,0);
			width: 700rpx;
			height: 380rpx;
			display: grid;
			grid-template-columns:repeat(2,1fr);
			gap: 0;
			padding:0;
			margin: 0 auto;
			text-align: center;
			
			.big{
				z-index: 1;
				grid-column: 1/1;
				grid-row: 1/3;
				background-color: #fff;
				border-radius: 15rpx;
				position: relative;
				display: flex;
				flex-flow: column;
				justify-content: center;
				align-items: center;
				font-size: 35rpx;
				image{
					height: 200rpx;
					width: 200rpx;
					margin-bottom: 15rpx;
				}
				
				
			}
			.big::after{
				z-index: 2;
				content: "";
				position: absolute;
				// background-color: skyblue;
				height: 355rpx;
				width: 0;
				border: 1rpx dashed #f8cbc7;
				right: 0;
				top: 15rpx;
			}
			
			.small1,.small2{
				position: relative;
				background: #fff;
				border-radius: 15rpx;
				display: flex;
				flex-flow: column;
				justify-content: center;
				align-items: center;
				font-size: 25rpx;
				
				image{
					height: 80rpx;
					width: 80rpx;
					margin-bottom: 10rpx;
				}

			}
			
			.small1::after{
				content: "";
				position: absolute;
				width: 325rpx;
				height: 0;
				border: 1rpx dashed #f8cbc7;
				bottom: 0;
				left: 0rpx;
			}
			
		}
		
		.box1{
			
			background: transparent;
			width: 700rpx;
			height: 150rpx;
			margin: 0 auto;
			margin-top: 30rpx;
			margin-bottom: 30rpx;
			display: grid;
			grid-template-columns: repeat(2,1fr);
			
			.flower,.more{
				position: relative;
				display: flex;
				align-items: center;
				padding-left: 50rpx;
				border-radius: 15rpx;
				background-color: #fff;
				image{
					width: 120rpx;
					height: 120rpx;
				}
			}
			.flower::after{
				content: "";
				position: absolute;
				height: 120rpx;
				width: 0;
				border: 1rpx dashed #f8cbc7;
				right: 0;
				
			}
		}
		
		.box2{
			box-sizing: border-box;
			width: 700rpx;
			margin: 0 auto;
			background-color: #fff;
			padding: 20rpx;
			border-radius: 10rpx;
			// box-shadow: 0 4rpx 20rpx rgba(0, 0, 0, .1);
		}
	}
	
</style>