<template>
  <v-card>
    <v-card-title>Search</v-card-title>
    <v-card-text>
        <v-text-field label="Search Symbols" v-model="msg"></v-text-field>
        <v-btn color="primary" @click="searchSymbols">Go</v-btn>
    </v-card-text>
  </v-card>
</template>

<script>
import axios from "axios";

export default {
  name: "SearchBar",
  props: {},
  data() {
    return {
      msg: "FB"
    };
  },
  computed: {
    symbols: function() {
      return [];
    }
  },
  methods: {
    searchSymbols: async function() {
      var response = await axios.get("http://localhost:3000/api/search?symbol=" + this.msg);
      this.$emit('searchResults', response.data.data);
    }
  }
};
</script>

<style scoped>
</style>
