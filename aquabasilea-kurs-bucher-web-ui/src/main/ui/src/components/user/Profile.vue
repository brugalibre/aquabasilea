<template>
  <div class="container centered-flex">
    <div v-if="currentUser">
      <div class="tile grid-container jumbotron">
        <h2>Details</h2>
        <div class="grid-container-60-40">
          <label class="attr">Benutzername</label>
          <span style="word-wrap: anywhere">{{ currentUser.username }}</span>
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
      <div class="tile grid-container jumbotron" style="margin-top: 50px">
        <h2>Daten ändern</h2>
        <div class="grid-container-40-60">
          <label class="attr">Neue Mobile-Nr</label>
          <div style="display: flex">
            <CFormInput v-model="newPhoneNumber"></CFormInput>
          </div>
          <CButton color="info" class="change-phone-nr-button" :disabled="this.isDisabled()"
                   v-on:click="this.changePhoneNr()">
              <span
                  v-show="loading"
                  class="spinner-border spinner-border-sm"
              ></span>
            Speichern
          </CButton>
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
  </div>
</template>

<script>
import LoggingService from "@/services/log/logging.service";
import MobilePhoneService from "@/services/mobilephone/mobile-phone.service";
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
  },
  methods: {
    isDisabled() {
      return this.newPhoneNumber === this.currentUser.phoneNr
          || this.loading
          || !this.newPhoneNumber;
    },
    changePhoneNr() {
      this.loading = true;
      const changeMobilePhoneRequest = {
        userId: this.currentUser.userId,
        newUserPhoneNr: this.newPhoneNumber,
      }
      MobilePhoneService.changePhoneNumber(changeMobilePhoneRequest)
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
  margin-right: 10px;
}

.attr {
  font-weight: bold;
  word-wrap: anywhere;
}

</style>