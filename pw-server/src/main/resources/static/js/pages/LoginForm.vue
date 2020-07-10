<template>
    <v-container>
        <v-row dense justify="center">
            <v-col cols="12" sm="12" md="4">
                <v-card>
                    <v-row dense justify="center" class="light-green lighten-3">
                        <v-col cols="12" class="pa-0">
                            <v-card-title class="blue lighten-2">Login Form</v-card-title>
                            <v-card-text>
                                <v-form>
                                    <v-text-field label="Email" v-model="username"></v-text-field>
                                    <v-text-field label="Password" v-model="password"></v-text-field>
                                    <v-btn text color="green" v-on:click="submit">login</v-btn>
                                    <v-btn text color="red" v-on:click="clearForm">reset</v-btn>
                                </v-form>
                            </v-card-text>
                        </v-col>
                    </v-row>
                </v-card>
            </v-col>
        </v-row>
    </v-container>
</template>

<script>
    import Vue from "vue";

    export default {
        name: "LoginForm",
        data() {
            return {
                username: '',
                password: ''
            }
        },
        methods: {
            submit() {
                Vue.http.post('/api/login',
                    {username: this.username, password: this.password})
                    .then(
                        res => {
                            console.log(res);
                            window.location.href = '/api/'
                        },
                        err=> console.log(err)
                    )
            },
            clearForm() {
                this.username = '';
                this.password = ''
            }
        }
    }
</script>

<style scoped>

</style>