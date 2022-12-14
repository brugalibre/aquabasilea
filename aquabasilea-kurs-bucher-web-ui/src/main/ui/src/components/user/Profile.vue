<template>
  <div class="container">
    <div class="jumbotron" style="width: 400px">
      <h2 style="text-align: left;">Details</h2>
      <div class="grid-container-60-40">
        <label class="attr">Benutzername</label>
        <span>{{ currentUser.username }}</span>
        <label class="attr">Mobile-Nr</label>
        <span>{{ currentUser.phoneNr }}</span>
        <label class="attr">Benutzerrollen</label>
        <span>
          <ul>
            <li v-for="role in currentUser.roles" :key="role">{{ role }}</li>
          </ul>
        </span>
      </div>
    </div>
    <div class="jumbotron" style="width: 600px; margin-top: 50px">
      <h2 style="text-align: left">Daten ändern</h2>
      <div class="grid-container-40-60">
        <label class="attr">Neue Mobile-Nr</label>
        <div style="display: flex">
          <CFormInput v-model="newPhoneNumber"></CFormInput>
          <CButton color="info" class="change-phone-nr-button" :disabled="this.isDisabled()"
                   v-on:click="this.changePhoneNr()">
              <span
                  v-show="loading"
                  class="spinner-border spinner-border-sm"
              ></span>
            Speichern
          </CButton>
        </div>
        <span v-if="message"></span>
        <div
            v-if="message"
            class="alert"
            :class="successful ? 'alert-success' : 'alert-danger'"
        >
          {{ message }}
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import LoggingService from "@/services/log/logging.service";
import UserService from "@/services/user/user.service";
import AuthService from "@/services/auth/auth.service";

export default {
  name: 'Profile',
  data() {
    return {
      loading: false,
      newPhoneNumber: '',
      successful: true,
      message: ''
    };
  },
  computed: {
    currentUser() {
      return this.$store.state.auth.user;
    }
  }, methods: {
    isDisabled() {
      return this.newPhoneNumber === this.currentUser.phoneNr
          || this.loading
          || !this.newPhoneNumber;
    },
    changePhoneNr() {
      this.loading = true;
      const changeUser = {
        username: this.currentUser.username,
        newUserPhoneNr: this.newPhoneNumber,
      }
      UserService.changePhoneNumber(changeUser)
          .then(() => {
            this.successful = true;
            this.message = 'Mobile-Nr Aktualisiert!';
            this.currentUser.phoneNr = this.newPhoneNumber;
            AuthService.setCurrentUser(this.currentUser);
          })
          .catch(error => {
            this.message = LoggingService.extractErrorText(error);
            this.successful = false;
          })
          .finally(() => this.loading = false);
    }
  }
}
</script>

<style scoped>

.change-phone-nr-button {
  margin-left: 10px;
  width: 100%;
}

.attr {
  font-weight: bold;
  word-wrap: anywhere;
}


</style>