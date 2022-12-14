<template>
  <div id="app">
    <nav class="navbar navbar-expand navbar-dark bg-info nav-bar-container">

      <div v-if="this.hasCurrentUserRole('USER')" class="aquabasilea-course-overview-router-link">
        <router-link to="/" class="nav-link">
          <!--          <span class="aquabasilea-icon"/>-->
          <h3 class="aquabasilea-course-overview-router-link-title">Deine Migros-Kurse</h3>
        </router-link>
      </div>
      <div v-else></div>
      <div v-if="!currentUser" class="navbar-nav ml-auto">
        <li class="nav-item icon-with-text">
          <span class="register-icon"/>
          <router-link to="/register" class="nav-link">
            <label class="router-link-label">Register</label>
          </router-link>
        </li>
        <li class="nav-item icon-with-text nav-bar-end-element">
          <span class="login-icon"/>
          <router-link to="/login" class="nav-link">
            <label class="router-link-label">Login</label>
          </router-link>
        </li>
      </div>
      <div v-if="currentUser" class="navbar-nav ml-auto">
        <li v-if="this.hasCurrentUserRole('ADMIN')" class="nav-item icon-with-text nav-bar-end-element">
          <span class="admin-icon"/>
          <router-link to="/admin" class="nav-link">
            <label class="router-link-label">Admin</label>
          </router-link>
        </li>
        <li class="nav-item icon-with-text">
          <span class="user-icon"/>
          <router-link to="/profile" class="nav-link">
            <label class="router-link-label">Profil verwalten</label>
          </router-link>
        </li>
        <li class="nav-item icon-with-text nav-bar-end-element">
          <span class="logout-icon"/>
          <a class="nav-link" @click.prevent="logOut">
            <label class="router-link-label">Logout</label>
          </a>
        </li>
      </div>
    </nav>
    <div class="aquabasilea-container container">
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
    },
    hasCurrentUserRole(role) {
      return this.currentUser?.roles
          .find(currentUserRole => currentUserRole === role);
    }
  }
};
</script>
<style>
* {
  font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
}

.nav-bar-end-element {
  margin: 0px 25px 0px 50px;
}

.nav-bar-container {
  display: flex;
  justify-content: space-between !important;
  max-height: 60px;
}

.router-link-label {
  white-space: nowrap;
}

.aquabasilea-container {
  min-width: 100%;
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

.login-icon {
  background: url("./assets/login.svg") transparent no-repeat right;
}

.admin-icon {
  background: url("./assets/admin.svg") transparent no-repeat right;
}

.register-icon {
  background: url("./assets/register.svg") no-repeat left;
}

.logout-icon {
  background: url("./assets/logout.svg") transparent no-repeat right;
}

.user-icon {
  background: url("./assets/user.svg") no-repeat left;
  background-size: 50% 50%;
}

.aquabasilea-icon {
  background: url("./assets/aqualogo.jpg") transparent no-repeat left;
}

.icon-with-text {
  display: grid;
  grid-template-columns: 25% 75%;
  grid-template-rows: auto;
  width: 100%;
}

.aquabasilea-course-overview-router-link {
  margin-left: 1vw;
  max-height: 50px;
  min-width: 50px;
}

.aquabasilea-course-overview-router-link-title {
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;

  width: 100%;
  text-align: left;
  float: left;
  color: white;
}

</style>
