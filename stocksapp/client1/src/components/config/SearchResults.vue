<template>
  <v-card>
    <v-card-title>Results</v-card-title>
    <v-card-text>
      <v-list lines="one" v-if="symbols">
        <v-list-item v-for="symbol in symbols" :key="symbol.symbol">
          <v-row no-gutters="true">
            <v-col> {{ symbol.symbol }} </v-col>
            <v-col> {{ symbol.name }} </v-col>
            <v-col> {{ symbol.stock_exchange.name }} </v-col>
            <v-col> {{ symbol.stock_exchange.acronym }} </v-col>
            <v-col> {{ symbol.stock_exchange.city }} </v-col>
            <v-col> {{ symbol.stock_exchange.country }} </v-col>
            <v-col>
              <v-btn color="primary" @click="symbolClicked(symbol.symbol, $event)">ADD</v-btn>
            </v-col>
          </v-row>
        </v-list-item>
      </v-list>
    </v-card-text>
  </v-card>

</template>
<script>

export default {
  name: 'SearchResults',
  props: {
    symbols: []
  },
  methods: {
    symbolClicked(symbol) {
      this.$emit('symbolClicked', symbol);
    }
  }
}
</script>

<style scoped>
a {
  text-decoration: none;
}

.w3-row {
  font-weight: bold;
}
</style>
