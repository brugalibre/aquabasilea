<template>
  <div class="col-md-12">
    <div class="tile card card-container">
      <img
          id="profile-img"
          src="//ssl.gstatic.com/accounts/ui/avatar_2x.png"
          class="profile-img-card"
      />
      <div @submit="handleLogin" :validation-schema="schema">
        <div class="form-group">
          <label for="username">Benutzername</label>
          <CFormInput v-model="username" id="username" name="username" type="text" class="form-control"/>
          <CAlert name="username" class="error-feedback"/>
        </div>
        <div class="form-group">
          <label for="password">Passwort</label>

          <!-- XXX hier war es ursprünglich ein Field typ! aber der ist immer protected-->
          <CFormInput v-model="password" id="password" name="password" type="password" class="form-control" onsubmit="this.handleLogin()"/>
          <CAlert name="password" class="error-feedback"/>
        </div>

        <div class="form-group">
          <CButton color="info" class="login-button" :disabled="loading" v-on:click="this.handleLogin()">
            <span
                v-show="loading"
                class="spinner-border spinner-border-sm"
            ></span>
            <span>Login</span>
          </CButton>
        </div>

        <div
            v-if="message"
            class="alert"
            :class="'alert-danger'"
        >
          {{ message }}
        </div>
        <div>
          <label>
            Noch nicht registriert?
            Hier gehts zur
          </label>
          <router-link :to=registerPath>
            Registrierung
          </router-link>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
// import {ErrorMessage, Form} from "vee-validate";
import * as yup from "yup";
import LoggingService from "@/services/log/logging.service";
import RouterConstants from "@/router-constants";

export default {
  name: "Login",
  components: {
    // Form,
    // ErrorMessage,
  },
  data() {
    const schema = yup.object().shape({
      username: yup.string().required("Username is required!"),
      password: yup.string().required("Password is required!"),
    });

    return {
      registerPath: RouterConstants.REGISTER_PATH,
      courseOverviewPath: RouterConstants.COURSE_OVERVIEW_PATH,
      loading: false,
      message: '',
      username: '',
      password: '',
      schema,
    };
  },
  computed: {
    loggedIn() {
      return this.$store.state.auth.status.loggedIn;
    },
  },
  created() {
    if (this.loggedIn) {
      this.$router.push(this.courseOverviewPath);
    }
  },
  methods: {
    handleLogin() {
      this.loading = true;
      const user = {
        username: this.username,
        password: this.password
      };
      this.$store.dispatch("auth/login", user)
          .then(response => {
            if (response) {
              this.$router.push(this.courseOverviewPath);
            }
          })
          .catch(error => this.message = LoggingService.extractErrorText(error))
          .finally(() => this.loading = false);
    },
  },
};
</script>

<style scoped>

.login-button {
  margin-bottom: 35px;
  width: 100%;
}

label {
  display: block;
  margin-top: 10px;
}

.card-container.card {
  max-width: 350px !important;
  padding: 40px 40px;
}

.card {
  background-color: #f7f7f7;
  padding: 20px 25px 30px;
  margin: 0 auto 25px;
  margin-top: 50px;
  -moz-border-radius: 2px;
  -webkit-border-radius: 2px;
  border-radius: 2px;
  -moz-box-shadow: 0px 2px 2px rgba(0, 0, 0, 0.3);
  -webkit-box-shadow: 0px 2px 2px rgba(0, 0, 0, 0.3);
  box-shadow: 0px 2px 2px rgba(0, 0, 0, 0.3);
}

.profile-img-card {
  width: 96px;
  height: 96px;
  margin: 0 auto 10px;
  display: block;
  -moz-border-radius: 50%;
  -webkit-border-radius: 50%;
  border-radius: 50%;
}

.error-feedback {
  color: red;
}
</style>
