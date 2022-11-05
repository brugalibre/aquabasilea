<template>
  <div id="app">
    <nav class="navbar navbar-expand navbar-dark bg-info">
      <a href="/" class="navbar-brand"> Migros-Kursbucher Verwaltung</a>
      <div class="navbar-nav mr-auto" >
        <li class="nav-item">
          <router-link to="/manage" class="nav-link">
            <font-awesome-icon icon="fa-solid fa-square-parking"/>
            Migros-Kurs verwalten
          </router-link>
        </li>
      </div>

      <div v-if="!currentUser" class="navbar-nav ml-auto">
        <li class="nav-item">
          <router-link to="/register" class="nav-link">
            <font-awesome-icon icon="user-plus"/>
            Sign Up
          </router-link>
        </li>
        <li class="nav-item">
          <router-link to="/login" class="nav-link">
            <font-awesome-icon icon="sign-in-alt"/>
            Login
          </router-link>
        </li>
      </div>
      <div v-if="currentUser" class="navbar-nav ml-auto">
        <li class="nav-item">
          <router-link to="/profile" class="nav-link">
            <font-awesome-icon icon="user"/>
            {{ currentUser.username }}
          </router-link>
        </li>
        <li class="nav-item">
          <a class="nav-link" @click.prevent="logOut">
            <font-awesome-icon icon="sign-out-alt"/>
            LogOut
          </a>
        </li>
      </div>
    </nav>

    <div class="container">
      <router-view/>
    </div>

  </div>
</template>

<script>
export default {
  computed: {
    currentUser() {
      return this.$store.state.auth.user;
    }
  },
  methods: {
    logOut() {
      this.$store.dispatch('auth/logout');
      this.$router.push('/login');
    }
  }
};
</script>
<style>
* {
  font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
}

.centered-flex {
  display: flex;
  flex-wrap: wrap;
  justify-content: center;
}

.content-left-side {
  max-width: 410px;
  display: flex;
  flex-direction: column;
  height: auto;
}

.course-state-overview {
  flex-grow: 2;
}

.tile {
  padding: 10px;
  margin: 10px;
  box-shadow: inset 0 3px 6px rgba(0, 0, 0, 0.16), 0 4px 6px rgba(0, 0, 0, 0.45);
  border-radius: 10px;
}

button {
  border-collapse: collapse;
  background-color: #0095c9;
  border-color: lightskyblue;
  color: white;
}

button:disabled {
  border-collapse: collapse;
  background-color: lightslategray;
  border-color: darkgray;
}

button:disabled {
  background-color: #9F9F9F;
}

table, th, td {
  color: black;
  table-layout: auto;
  border-collapse: collapse;
  text-align: left;
  padding: 3px;
  white-space: nowrap;
}

/**
Somehow the @coreui/coreui/dist/css/coreui.css styles override the table styles here - and somehow
I couldn't import the coreui-style scoped. So thats why we use !important here
*/

tr {
  border-bottom: #0095c9 thin solid !important;
}

tr:last-child {
  border-bottom: transparent !important;
}

th {
  color: white !important;
  padding: 0.5vw 1.5vh !important;
  background-color: #0095c9 !important;
}

th:first-child {
  border-top-left-radius: 7px !important;
}

th:last-child {
  border-top-right-radius: 7px !important;
}

h1, h2, h3, h4, label {
  word-wrap: anywhere;
}

h1, h2, h3 {
  text-align: center;
}

h5 {
  padding-top: 10px;
  word-wrap: anywhere;
}

button {
  white-space: normal;
  word-wrap: break-word;
}

.grid-container {
  display: grid;
  row-gap: 10px;
}

.grid-container-40-60 {
  display: grid;
  grid-template-columns: 40% 60%;
  column-gap: 10px;
  row-gap: 10px;
  padding-right: 10px;
}

.grid-container-60-40 {
  display: grid;
  grid-template-columns: 60% 40%;
  column-gap: 10px;
  row-gap: 10px;
  padding-right: 10px;
}

.error-details {
  border-radius: 10px;
}

</style>
