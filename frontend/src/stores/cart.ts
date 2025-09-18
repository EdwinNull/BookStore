import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import type { CartItem } from '@/types'

export const useCartStore = defineStore('cart', () => {
  // 状态
  const items = ref<CartItem[]>([])

  // 从localStorage加载购物车数据
  const loadCart = () => {
    const savedCart = localStorage.getItem('cart')
    if (savedCart) {
      items.value = JSON.parse(savedCart)
    }
  }

  // 保存购物车数据到localStorage
  const saveCart = () => {
    localStorage.setItem('cart', JSON.stringify(items.value))
  }

  // 计算属性
  const totalItems = computed(() => items.value.reduce((sum, item) => sum + item.quantity, 0))
  const totalPrice = computed(() => items.value.reduce((sum, item) => sum + item.price * item.quantity, 0))
  const isEmpty = computed(() => items.value.length === 0)

  // 添加商品到购物车
  const addItem = (book: any, quantity: number = 1) => {
    const existingItem = items.value.find(item => item.book_id === book.book_id)

    if (existingItem) {
      existingItem.quantity += quantity
    } else {
      items.value.push({
        book_id: book.book_id,
        title: book.title,
        price: book.price,
        quantity,
        cover_image: book.cover_image
      })
    }

    saveCart()
  }

  // 移除商品
  const removeItem = (bookId: number) => {
    const index = items.value.findIndex(item => item.book_id === bookId)
    if (index > -1) {
      items.value.splice(index, 1)
      saveCart()
    }
  }

  // 更新商品数量
  const updateQuantity = (bookId: number, quantity: number) => {
    const item = items.value.find(item => item.book_id === bookId)
    if (item) {
      if (quantity <= 0) {
        removeItem(bookId)
      } else {
        item.quantity = quantity
        saveCart()
      }
    }
  }

  // 清空购物车
  const clearCart = () => {
    items.value = []
    saveCart()
  }

  // 获取购物车商品数量
  const getItemQuantity = (bookId: number) => {
    const item = items.value.find(item => item.book_id === bookId)
    return item?.quantity || 0
  }

  // 检查商品是否在购物车中
  const isInCart = (bookId: number) => {
    return items.value.some(item => item.book_id === bookId)
  }

  // 初始化
  loadCart()

  return {
    // 状态
    items,

    // 计算属性
    totalItems,
    totalPrice,
    isEmpty,

    // 方法
    addItem,
    removeItem,
    updateQuantity,
    clearCart,
    getItemQuantity,
    isInCart
  }
})