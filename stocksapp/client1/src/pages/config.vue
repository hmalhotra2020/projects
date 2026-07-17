<template>
  <v-container>
    <v-row class="">
      <v-col>
        <SearchBar @searchResults="showResults"></SearchBar>
      </v-col>
      <v-col>
        <SymbolsList v-bind:symbols="mySymbolsList"></SymbolsList>
      </v-col>
    </v-row>
    <v-row class="">
      <v-col>
        <SearchResults v-bind:symbols="searchResults" @symbolClicked="addSymbols"></SearchResults>
      </v-col>
    </v-row>
  </v-container>
</template>

<script>
import SearchBar from '../components/config/SearchBar.vue'
import SearchResults from '../components/config/SearchResults.vue'
import SymbolsList from '../components/config/SymbolsList.vue'
import axios from 'axios';

export default {
  name: 'ConfigPage',
  props: {
    msg: String
  },
  components: {
    SearchBar,
    SearchResults,
    SymbolsList
  },
  data() {
    return {
      searchResults: [],
      mySymbolsList: []
    }
  },
  mounted() {
    axios.get(
      'http://localhost:3000/api/config')
      .then((response) => {
        this.mySymbolsList = response.data;
      }, error => {
        console.log(error);
      })
  },
  methods: {
    showResults(results) {
      this.searchResults = results;
    },
    addSymbols(symbol) {
      axios.post(
        'http://localhost:3000/api/config', {
        symbol: symbol
      }).then((response) => {

        this.mySymbolsList = response.data;
      }, error => {
        console.log(error);
      })
    }
  }
}
</script>

<style scoped></style>
