<template>
  <v-app>
    <v-main>
        <v-app-bar color="grey" class="text-white font-weight-bold">
          <v-btn variant="" color="" class="mr-md-4 text-white font-weight-bold" to="/">HOME</v-btn>
          <v-btn variant="" color="" class="mr-md-4 text-white font-weight-bold" to="/config">CONFIG</v-btn>
          <v-btn variant="" color="" class="mr-md-4 text-white font-weight-bold" to="/dashboard">DASHBOARD</v-btn>
          <v-spacer></v-spacer>
        </v-app-bar>
        <router-view />
    </v-main>
  </v-app>
</template>

<script setup>
//
</script>
