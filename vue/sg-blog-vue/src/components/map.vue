<template>
    <div class="container">
      <!-- 左侧地图 -->
      <div class="left">
        <baidu-map
          class="map"
          :center="center"
          :zoom="zoom"
          @ready="handler"
          ak="aRXvRzFwqrV1hWsACRVEOjOarq12iBxD"
        >
          <bm-search-box :keyword="keyword" :auto-viewport="true" />
          <bm-marker
            v-for="(place, index) in places"
            :key="index"
            :position="{ lng: place.lng, lat: place.lat }"
            :title="place.name"
          />
        </baidu-map>
      </div>
  
      <!-- 右侧功能区 -->
      <div class="right">
        <div class="search-bar">
          <input v-model="keyword" placeholder="请输入地点名称" />
          <button @click="searchPlace">添加地点</button>
          <button @click="exportPDF">导出PDF</button>
        </div>
  
        <div class="place-list">
          <h3>📍 已添加地点</h3>
          <div class="place-card" v-for="(place, index) in places" :key="index">
            <div class="place-info">
              <strong>{{ place.name }}</strong>
              <textarea
                v-model="place.note"
                placeholder="添加说明..."
                class="note-input"
                rows="3"
              />
            </div>
            <button @click="reLocate(place)">定位</button>
          </div>
        </div>
      </div>
    </div>
  </template>
  
  <script>
  import jsPDF from "jspdf";
  import "jspdf-autotable";
  
  export default {
    data() {
      return {
        center: { lng: 116.404, lat: 39.915 },
        zoom: 14,
        keyword: "",
        map: null,
        localSearch: null,
        places: [],
      };
    },
    methods: {
      handler({ BMap, map }) {
        this.map = map;
        this.localSearch = new BMap.LocalSearch(map, {
          onSearchComplete: this.onSearchComplete,
        });
      },
      searchPlace() {
        if (!this.keyword) return;
        this.localSearch.search(this.keyword);
      },
      onSearchComplete(results) {
        if (
          this.localSearch.getStatus() === window.BMAP_STATUS_SUCCESS &&
          results.getCurrentNumPois() > 0
        ) {
          const poi = results.getPoi(0);
          const place = {
            name: poi.title,
            lng: poi.point.lng,
            lat: poi.point.lat,
            note: "",
          };
          this.places.push(place);
          this.center = { lng: poi.point.lng, lat: poi.point.lat };
          this.zoom = 17;
        } else {
          alert("未找到该地点！");
        }
      },
      reLocate(place) {
        this.center = { lng: place.lng, lat: place.lat };
        this.zoom = 17;
      },
  
      async exportPDF() {
        const doc = new jsPDF();
  
        try {
          // 1. 直接引用字体文件
          const fontUrl = require('@/assets/fonts/simhei.ttf'); // 通过 require 引入字体文件
          const response = await fetch(fontUrl);
          const fontData = await response.arrayBuffer();
  
          // 2. 将字体数据转为 base64
          const uint8Array = new Uint8Array(fontData);
          let binary = "";
          for (let i = 0; i < uint8Array.length; i++) {
            binary += String.fromCharCode(uint8Array[i]);
          }
          const base64Font = btoa(binary);
  
          // 3. 添加字体到 VFS，并注册
          doc.addFileToVFS("simhei.ttf", base64Font);
          doc.addFont("simhei.ttf", "simhei", "normal");
          doc.setFont("simhei");
  
          // 4. 写内容
          doc.setFontSize(16);
          doc.text("📍 已添加地点", 10, 20);
  
          const headers = ["地点名称", "描述"];
          const data = this.places.map((p) => [p.name, p.note]);
  
          doc.autoTable({
            head: [headers],
            body: data,
            startY: 30,
            margin: { horizontal: 10 },
            styles: {
              font: "simhei",  // 确保这里设置了字体
            },
            columnStyles: {
                0: { cellWidth: 50 },  // 地点名称列，宽度设为50
                1: { cellWidth: 150 }, // 描述列，宽度设为150
            },
          });
  
          doc.save("places.pdf");
        } catch (err) {
          console.error("字体加载失败", err);
          alert("导出失败，请检查字体路径或网络！");
        }
      },
    },
  };
  </script>
  
  <style scoped>
  .container {
    height: 710px;
    display: flex;
    background: #fff;
    padding: 20px;
    gap: 20px;
    border-radius: 8px;
    width: 100%;
    margin: 0 auto;
  }
  .left {
    flex: 1;
  }
  .right {
    flex: 1;
    display: flex;
    flex-direction: column;
    gap: 20px;
    overflow-y: auto;
    padding-right: 10px;
    box-sizing: border-box;
  }
  .map {
    width: 100%;
    height: 100%;
    border-radius: 8px;
    overflow: hidden;
  }
  .search-bar {
    display: flex;
    gap: 10px;
  }
  .search-bar input {
    flex: 1;
    padding: 8px;
    border: 1px solid #ccc;
    border-radius: 4px;
  }
  .search-bar button {
    padding: 8px 16px;
    background-color: #409eff;
    color: white;
    border: none;
    border-radius: 4px;
    cursor: pointer;
  }
  .place-list h3 {
    margin: 0;
    font-size: 18px;
    color: #333;
  }
  .place-card {
    background: white;
    padding: 10px;
    border-radius: 6px;
    display: flex;
    justify-content: space-between;
    align-items: flex-start;
    margin-top: 10px;
    box-shadow: 0 1px 3px rgba(0, 0, 0, 0.1);
    gap: 10px;
  }
  .place-info {
    flex: 1;
    display: flex;
    flex-direction: column;
    gap: 5px;
  }
  .note-input {
    width: 90%;
    padding: 6px 8px;
    border: 1px solid #ddd;
    border-radius: 4px;
    font-size: 14px;
    resize: vertical;
  }
  .place-card button {
    padding: 6px 12px;
    background: #67c23a;
    color: white;
    border: none;
    border-radius: 4px;
    cursor: pointer;
    height: fit-content;
  }
  </style>
  