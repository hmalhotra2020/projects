<template>
  <v-card>
    <v-card-title>List of Added Symbols</v-card-title>
    <v-card-text>
      <div v-if="symbols">
        <div v-for="symbol in symbols" v-bind:key="symbol" class="" style="margin: 5px;">
          <v-btn color="primary">{{ symbol }}</v-btn>
        </div>
      </div>
    </v-card-text>
  </v-card>
</template>
<script>

export default {
  name: 'SymbolsList',
  props: {
    msg: String,
    symbols: Array
  }
}
</script>

<style scoped></style>
