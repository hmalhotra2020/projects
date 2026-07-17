<template>
  <v-chart class="chart" :option="option" autoresize="true" v-if="ready" />
</template>

<script setup>
import { onMounted } from 'vue'
import { use } from "echarts/core";
import { CanvasRenderer } from "echarts/renderers";
import { CandlestickChart } from "echarts/charts";
import {
  TitleComponent,
  TooltipComponent,
  LegendComponent,
  GridComponent
} from "echarts/components";
import VChart, { THEME_KEY } from "vue-echarts";
import { ref, provide, watch } from "vue";
import { useDate } from 'vuetify'

use([
  CanvasRenderer,
  CandlestickChart,
  TitleComponent,
  TooltipComponent,
  LegendComponent,
  GridComponent
]);

provide(THEME_KEY, "light");

const props = defineProps(['symbol', 'historyForSymbol'])
const dt = useDate();
onMounted(() => {
  //console.log(`the component is now mounted.`)

})

const ready = ref(false)

var option = ref({
  title: {
    text: "Stock History"
  },
  xAxis: {
    data: []
  },
  yAxis: {
    minInterval: 1,
    scale: true,

  },
  series:
    {
      type: 'candlestick',
      data: []
    }

});


watch(() => props.historyForSymbol, (newValue, oldValue) => {
  //  console.log('newValue: ', newValue)
  var seriesData = []
  var xAxisData = [];

  for(var i=0; i<newValue.data.length; i++) {
    var item = newValue.data[i];
    seriesData.push([item.open, item.close, item.low, item.high])
    //var newDate = dt.date(item.date);
    xAxisData.push(dt.format(item.date, "dayOfMonth"))
  }
  option.value.series.data = seriesData
  option.value.xAxis.data = xAxisData
  //console.log('props.historyForSymbol: ', option.value.series.data)
  ready.value = true
})
</script>

<style scoped>
.chart {
  height: 400px;
}
</style>
