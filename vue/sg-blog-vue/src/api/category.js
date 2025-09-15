import request from '@/utils/request'

// 查询分类列表
export function getCategoryList() {
    return request({
        url: '/category/getCategoryList',
        headers: {
          isToken: false
        },
        method: 'get'
    })
}

export function getHotCategory() {
  return request({
      url: '/category/getHotCategory',
      headers: {
        isToken: false
      },
      method: 'get'
  })
}

// 查询分类列表
export function listAllCategory() {
  return request({
    url: '/category/listAllCategory',
    headers: {
      isToken: false
    },
    method: 'get'
  })
}

// 查询分类列表
export function listAllTag() {
  return request({
    url: '/tag/listAllTag',
    headers: {
      isToken: false
    },
    method: 'get'
  })
}
