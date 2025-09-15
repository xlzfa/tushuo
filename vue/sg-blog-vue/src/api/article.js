import request from '@/utils/request'


export function addArticle(data) {
  return request({
    url: '/article/add',
    method: 'post',
    data: data
  })
}

// 删除文章
export function delArticle(id) {
  return request({
    url: '/article/del/' + id,
    method: 'delete'
  })
}

// 查询文章列表
export function articleList(query) {
    return request({
        url: '/article/articleList',
        method: 'get',
        headers: {
          isToken: false
        },
        params: query
    })
}

export function searchArticle(query) {
  return request({
    url: '/article/searchArticle',
    method: 'get',
    headers: {
      isToken: false
    },
    params: query
  })
}

//查询最热文章
export function hotArticleList() {
    return request({
        url: '/article/hotArticleList',
        headers: {
          isToken: false
        },
        method: 'get'
    })
}

//获取文章详情
export function getArticle(articleId) {
    return request({
        url: '/article/' + articleId,
        headers: {
          isToken: false
        },
        method: 'get'
    })
}

export function updateViewCount(articleId) {
    return request({
        url: '/article/updateViewCount/' + articleId,
        headers: {
          isToken: false
        },
        method: 'put'
    })

}
