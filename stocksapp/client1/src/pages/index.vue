<template>
  <v-row class="fill-height">
    <v-row align="center" justify="center">
      <div class="justify">
        <h2>Welcome to the POC App for Node and Vuejs</h2>
        <h4>This is the HOME component</h4>
        <h4>Please click on Config first and then Dashboard</h4>
      </div>
    </v-row>
  </v-row>
</template>

<script setup>
import { ref } from 'vue'

const drawer = ref(null)
</script>

<script>
export default {
  data: () => ({ drawer: null }),
}
</script>
