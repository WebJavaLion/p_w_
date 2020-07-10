<template>
    <v-app>
        <v-app-bar clipped-left app>
            <v-app-bar-nav-icon v-if="enoughSize" v-on:click="show = !show"></v-app-bar-nav-icon>
            <v-toolbar-title>PW</v-toolbar-title>
            <v-spacer></v-spacer>
            <span v-if="getUserInfo !== null">{{getUserInfo.email}}</span>
            <v-btn icon href="/api/logout">
                <v-icon>
                    {{logout}}
                </v-icon>
            </v-btn>
        </v-app-bar>
        <v-hover v-slot:default="{hover}">
            <v-navigation-drawer app
                                 clipped
                                 :permanent="!enoughSize"
                                 width="200"
                                 v-model="show"
                                 expand-on-hover
                                 dark>
                <v-list nav dense class="pt-3">
                    <v-list-item-group>
                        <v-list-item @click="$router.push('/api/')">
                            <v-list-item-icon class="pa-0 mr-0">
                                <v-icon left>{{ groupsIcon }}</v-icon>
                            </v-list-item-icon>
                            <v-list-item-content>
                                groups
                            </v-list-item-content>
                        </v-list-item>
                        <v-list-item @click="$router.push('/api/log')">
                            <v-list-item-icon class="pa-0 mr-0">
                                <v-icon left>{{ logout }}</v-icon>
                            </v-list-item-icon>
                            <v-list-item-content>
                                value
                            </v-list-item-content>
                        </v-list-item>
                    </v-list-item-group>
                    <v-btn v-if="hover" block small>quit</v-btn>
                </v-list>
            </v-navigation-drawer>
        </v-hover>
        <v-main>
            <v-container>
                <router-view></router-view>
            </v-container>
        </v-main>
    </v-app>

</template>

<script>
    import {mapGetters} from 'vuex'
    import WordList from "../components/WordList.vue";
    import WordGroupList from "../components/WordGroupList.vue";
    import {mdiExitToApp} from '@mdi/js'
    import {mdiFormatListBulleted} from '@mdi/js'

    export default {
        components: {WordGroupList, WordList},
        data() {
            return {
                logout: mdiExitToApp,
                groupsIcon: mdiFormatListBulleted,
                s: false,
                show: true
            }
        },
        computed: {
            ...mapGetters(['getUserInfo', "getWordGroups"]),
            enoughSize() {
                return this.$vuetify.breakpoint.xs || this.$vuetify.breakpoint.sm
            }
        },
        methods: {
            onClick() {
                console.log(this.getUserInfo)
            },
            isMOver() {
                this.s = !this.s
            }
        },
        beforeMount() {
            if (userData === null) {
                this.$router.replace("/api/log").catch(err => {
                })
            }
        }
    }
</script>

<style>

</style>