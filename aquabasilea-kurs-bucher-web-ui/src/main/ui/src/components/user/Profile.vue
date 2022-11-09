<template>
  <div class="container">
    <div class="jumbotron" style="width: 300px">
      <div class="grid-container-60-40">
        <label class="attr">Benutzername</label>
        <span>{{ currentUser.username }}</span>
        <label class="attr">Benutzerrollen</label>
        <span>
          <ul>
        <li v-for="role in currentUser.roles" :key="role">{{ role }}</li>
          </ul></span>
      </div>
    </div>

    <label>Mhh.. ja zugegeben ist noch ein bisschen leer hier..
      Vielleicht kommt da später noch mehr</label>
  </div>
</template>

<script>
export default {
  name: 'Profile',
  computed: {
    currentUser() {
      return this.$store.state.auth.user;
    }
  },
  mounted() {
    if (!this.currentUser) {
      this.$router.push('/login');
    }
  }
};
</script>

<style scoped>


.attr {
  font-weight: bold;
  word-wrap: anywhere;
}


</style>