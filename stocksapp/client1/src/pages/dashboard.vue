<template>
  <div class="">
    <v-layout>
      <v-app-bar color="black" density="compact" class="font-weight-bold">
        <div class="ma-md-4 pa-lg-n8">Stocks with Prices for</div>
        <div class="secondary"> &nbsp; [{{ registeredSymbols.toString() }}] </div>
      </v-app-bar>
      <v-main>
        <v-container class="left-0 w-100" style="">
          <div class="" v-for="data in realtimeDataForSymbols" v-bind:key="data.symbol">
            <v-row>
              <v-col>
                <v-card class="pa-lg-4 fill-height" color="grey-lighten-5">
                  <v-row class="text-center">
                    <v-col class="pt-lg-8">
                      <v-btn v-if="stocksInfo.get(data.symbol)" class="" color="blue-grey-darken-1" flat elevation="8">
                        {{
                          stocksInfo.get(data.symbol).name }} </v-btn>
                      <br /><br />
                    </v-col>
                  </v-row>
                  <v-row class="text-body-2 text-center" style="" v-for="(value, key)  in data" v-bind:key="key"
                    no-gutters>
                    <v-col class="text-right text-primary font-weight-bold" style="">{{ key }} :</v-col>
                    <v-col class="text-left text-secondary font-weight-bold" style="">{{ data[key] }}</v-col>
                  </v-row>
                </v-card>

              </v-col>
              <v-col style="">
                <div class="">
                  <candlechart :symbol="data.symbol" :historyForSymbol="historicalData[data.symbol]"></candlechart>
                </div>
              </v-col>
            </v-row>
          </div>
        </v-container>
      </v-main>
    </v-layout>

  </div>
</template>

<script>
import axios from "axios";
import candlechart from "@/components/charts/candlechart.vue";

export default {
  name: "DashBoard",
  props: {},
  components: {
    candlechart,
  },
  created() {

  },
  data() {
    return {
      registeredSymbols: [],
      stocksInfo: new Map(),
    };
  },
  asyncComputed: {
    realtimeDataForSymbols: async function () {
      var result = await axios.post("http://localhost:3000/api/stocks/price", { symbols: this.registeredSymbols });
      return result.data;
    },
    historicalData: async function () {
      var historyObj = {};
      for (let symbol of this.registeredSymbols) {
        var response = await axios.get("http://localhost:3000/api/stocks?symbol=" + symbol);
        historyObj[symbol] = response.data;
      }
      return historyObj;
    }
  },
  mounted() {
    this.loadSymbols();
  },
  watch: {
    registeredSymbols: function (oldVal, newVal) {
      //console.log("Value changed to : ", oldVal, newVal);
      this.loadStocksInfo();
    }

  },
  methods: {
    loadSymbols: function () {
      axios.get("http://localhost:3000/api/stocks/selected")
        .then(res => {
          this.registeredSymbols = res.data;
          //console.log('registeredSymbols: ', this.registeredSymbols)
          return res.data;
        });
    },
    loadStocksInfo: async function () {
      const stockInfo = new Map();
      for (let symbol of this.registeredSymbols) {
        var response = await axios.get("http://localhost:3000/api/search?symbol=" + symbol);
        //this.stocksInfo.set(symbol) = response.data.data[0];
        stockInfo.set(symbol, response.data.data[0]);
      }
      this.stocksInfo = stockInfo
    }
  }

}
</script>

<style scoped></style>
